package com.catpuppyapp.puppygit.syntaxhighlight.markdown

import android.content.Context
import android.os.Bundle
import androidx.compose.ui.text.AnnotatedString
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLTheme
import com.catpuppyapp.puppygit.syntaxhighlight.base.TextMateUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


private const val TAG = "MarkDownSyntaxHighlighter"

class MarkDownSyntaxHighlighter(
    val text: String,
    val onReceive: (AnnotatedString) -> Unit,
) {
    val appContext: Context = AppModel.realAppContext
    val analyzeLock = Mutex()
    var myLang: TextMateLanguage? = null
    val scope = PLScope.MARKDOWN

    // cache if use more than 1 times
//    private var cachedLines:List<String>? = null
//    fun getTextLines() = cachedLines ?: text.lines().let { cachedLines = it; it }

    // one time use, no need cache
    fun getTextLines() = text.lines()

    fun analyze() {
        doJobThenOffLoading {
            analyzeLock.withLock {
                doAnalyzeNoLock()
            }
        }
    }

    fun release() {
        cleanOldLanguage()
    }

    fun cleanOldLanguage() {
        TextMateUtil.cleanLanguage(myLang)
    }

    private fun doAnalyzeNoLock() {
        // if no bug, should not trigger full syntax analyze a lot
        MyLog.w(TAG, "will run full syntax highlighting analyze(at $TAG)")


        cleanOldLanguage()

        PLTheme.updateThemeByAppTheme()

        // run new analyze
        val autoComplete = false
        val lang = TextMateLanguage.create(scope.scope, autoComplete)
        myLang = lang

        try {
            //闭包的receiver和给函数传参的是同一个实例
            TextMateUtil.setReceiverThenDoAct(lang, MarkDownStyleReceiver(this)) { receiver ->
                lang.analyzeManager.reset(ContentReference(Content(text)), Bundle(), receiver)
            }
        }catch (e: Exception) {
            // maybe will got NPE, if language changed to null by a new analyze
            MyLog.e(TAG, "#doAnalyzeNoLock() err: call `TextMateUtil.setReceiverThenDoAct()` err: ${e.stackTraceToString()}")

        }
    }


}

