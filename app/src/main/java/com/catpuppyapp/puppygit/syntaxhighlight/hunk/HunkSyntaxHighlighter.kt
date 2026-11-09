package com.catpuppyapp.puppygit.syntaxhighlight.hunk

import android.content.Context
import android.os.Bundle
import androidx.annotation.WorkerThread
import androidx.compose.ui.text.SpanStyle
import com.catpuppyapp.puppygit.git.PuppyHunkAndLines
import com.catpuppyapp.puppygit.msg.OneTimeToast
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.syntaxhighlight.base.TextMateUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


private const val TAG = "HunkSyntaxHighlighter"

class HunkSyntaxHighlighter(
    val hunk: PuppyHunkAndLines,
) {
    val appContext: Context = AppModel.realAppContext
    val analyzeLock = Mutex()
    var myLang: TextMateLanguage? = null


    // Don't call this on main thread
    // But this annotation is not enforced, it just a mark....
    @WorkerThread
    fun noMoreMemory(noMoreMemToaster: OneTimeToast) : Boolean {
        return hunk.diffItemSaver.syntaxDisabledOrNoMoreMem(noMoreMemToaster)
    }

    fun analyze(scope: PLScope, noMoreMemToaster: OneTimeToast) {
        if(noMoreMemory(noMoreMemToaster)) {
            return
        }


        doJobThenOffLoading job@{
            analyzeLock.withLock {
                if(noMoreMemory(noMoreMemToaster)) {
                    return@job
                }

                doAnalyzeNoLock(scope)
            }
        }
    }

    fun release() {
        cleanOldLanguage()
    }

    fun cleanOldLanguage() {
        TextMateUtil.cleanLanguage(myLang)
    }

    private fun doAnalyzeNoLock(scope: PLScope) {
        val text = hunk.linesToString()
        // even text is empty, still need create language object
//        if(text.isEmpty()) {
//            return
//        }

//        println("text: $text")

        // if no bug, should not trigger full syntax analyze a lot
        MyLog.w(TAG, "will run full syntax highlighting analyze(at $TAG, filename: ${hunk.diffItemSaver.fileName()})")


        cleanOldLanguage()

        // run new analyze
        val autoComplete = false
        val lang = TextMateLanguage.create(scope.scope, autoComplete)
        myLang = lang



        try {
            //闭包的receiver和给函数传参的是同一个实例
            TextMateUtil.setReceiverThenDoAct(lang, MyHunkStyleReceiver(this)) { receiver ->
                lang.analyzeManager.reset(ContentReference(Content(text)), Bundle(), receiver)
            }
        }catch (e: Exception) {
            // maybe will got NPE, if language changed to null by a new analyze
            MyLog.e(TAG, "#doAnalyzeNoLock() err: call `TextMateUtil.setReceiverThenDoAct()` err: fileRelativePath=${hunk.diffItemSaver.relativePathUnderRepo} err=${e.stackTraceToString()}")

        }
    }

    fun applyStyles(styles: Styles) {
        val spansReader = styles.spans.read()
        hunk.diffItemSaver.operateStylesMapWithWriteLock { styleMap ->
            hunk.lines.forEachIndexedBetter { idx, line ->
                val spans = spansReader.getSpansOnLine(idx)
                val lineStyles = mutableListOf<LineStylePart>()
                TextMateUtil.forEachSpanResult(line.getContentNoLineBreak(), spans) { start, end, style ->
                    lineStyles.add(LineStylePart(start, end, style))
                }

                styleMap.put(line.key, lineStyles)
            }
        }

        // diff页面只分析一次，无需增量分析，所以拿完对象就可以释放内存了
        release()
    }
}


data class LineStylePart(
    val start: Int,
    val end: Int,  // exclusive
    val style: SpanStyle,
)
