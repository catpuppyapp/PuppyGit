package com.catpuppyapp.puppygit.syntaxhighlight.base

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.TextStyle
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.util.RendererUtils
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.registry.IThemeSource


private const val TAG = "TextMateInit"

object TextMateUtil {
    private var inited = false

    // colorScheme is shareable
    var colorScheme: EditorColorScheme = EditorColorScheme()
        private set

    // only need init once
    fun doInit(appContext: Context) {
        if(inited) {
            return
        }

        inited = true

        try {
            setupTextmate(appContext)

            colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
                .apply {
                    // 必须调用，不然没颜色
                    applyDefault()
                }
        }catch (e: Exception) {
            inited = false
            MyLog.e(TAG, "$TAG#doInit err: ${e.stackTraceToString()}")
        }
    }

    // will call this method after app theme changed,
    //   so if we forget apply theme before analyzing,
    //   it still will use the right color theme
    fun updateTheme(inDarkTheme:Boolean) {
        PLTheme.setTheme(inDarkTheme)
    }


    /**
     * Setup Textmate. Load our grammars and themes from assets
     */
    private fun setupTextmate(appContext: Context) {
        // Add assets file provider so that files in assets can be loaded
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(
                appContext.assets // use application context
            )
        )

        loadDefaultTextMateThemes()
        loadDefaultTextMateLanguages()
    }

    /**
     * Load default textmate themes
     */
    /*suspend*/ fun loadDefaultTextMateThemes() /*= withContext(Dispatchers.IO)*/ {
        val themes = PLTheme.THEMES
        val themeRegistry = ThemeRegistry.getInstance()
        themes.forEach { name ->
            val path = "textmate/$name.json"
            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
                    ), name
                ).apply {
                    isDark = name != PLTheme.THEME_LIGHT
                }
            )
        }
    }


    /**
     * Load default languages from JSON configuration
     *
     * @see loadDefaultLanguagesWithDSL Load by Kotlin DSL
     */
    private /*suspend*/ fun loadDefaultTextMateLanguages() /*= withContext(Dispatchers.Main)*/ {
        GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
    }


    fun setReceiverThenDoAct(
        language: Language?,
        receiver: StyleReceiver,
        act: (StyleReceiver)->Unit,
    ) {
        language?.analyzeManager?.setReceiver(receiver)
        act(receiver)
    }


    fun cleanLanguage(language: Language?) {
        if (language != null) {
            val formatter = language.getFormatter()
            formatter.setReceiver(null)
            formatter.destroy()
            language.getAnalyzeManager().setReceiver(null)
            language.getAnalyzeManager().destroy()
            language.destroy()
        }
    }


    fun forEachSpanResult(rawText: String, spans:List<Span>, foreach:(start: Int, end: Int, spanStyle: SpanStyle) -> Unit) {
        var start = 0
        var spanIdx = 1

        while (spanIdx <= spans.size && start < rawText.length) {
            val curSpan = spans.get(spanIdx - 1)
            val nextSpan = spans.getOrNull(spanIdx++)
            var endExclusive = nextSpan?.column ?: rawText.length

            // invalid start index check
            // don't break, if break, maybe will lost data
            if(start < 0) { // this should never happened
                MyLog.i(TAG, "should never happened, plz check the code: invalid `start` index when apply syntax highlight styles: start=$start, endExclusive=$endExclusive, rawText.length=${rawText.length}")

                start = 0
            }

            // invalid end index check
            // fix: editor lost data
            // if the range invalid and break, then the text will
            //   become a substring which doesn't covered whole text,
            //   for users, they will see the data lost, but don't know why.
            // 修复编辑器丢失数据
            // 如果范围无效，然后直接break（之前就是这么干的），会导致text被截断到上个有效的`endExclusive`，
            //   若其值不等于rawText.length，会表现为数据丢失
            if(endExclusive > rawText.length) { // this maybe happens
                if(AppModel.devModeOn) {
                    MyLog.d(TAG, "invalid `end` index when apply syntax highlight styles: start=$start, endExclusive=$endExclusive, rawText.length=${rawText.length}")
                }

                endExclusive = rawText.length
            }

            // must is valid substring range when reached here

            // empty range, should never happen
            if(start >= endExclusive) {
                MyLog.i(TAG, "should never happened, plz check the code: empty range when apply syntax highlight styles: start=$start, endExclusive=$endExclusive, rawText.length=${rawText.length}")

                start = endExclusive

                // is empty range, so no text will append, so, just continue
                continue
            }

            val style = curSpan.style
            val foregroundColor = Color(RendererUtils.getForegroundColor(curSpan, colorScheme))

            // disable bg color for avoid conflicts with editor's merge mode bg color,
            //   (but, actually, I never have seen this bg colors, maybe most time is transparency)
//                val backgroundColor = Color(RendererUtils.getBackgroundColor(curSpan, colorScheme))

            val fontWeight = if(TextStyle.isBold(style)) FontWeight.Bold else null
            val fontStyle = if(TextStyle.isItalics(style)) FontStyle.Italic else null

            foreach(
                start,
                endExclusive,
                SpanStyle(color = foregroundColor, fontStyle = fontStyle, fontWeight = fontWeight)
            )

            start = endExclusive
        }


        // make sure no text omitted
        if(start < rawText.length) {
            foreach(
                start,
                rawText.length,
                MyStyleKt.emptySpanStyle
            )
        }
    }
}

