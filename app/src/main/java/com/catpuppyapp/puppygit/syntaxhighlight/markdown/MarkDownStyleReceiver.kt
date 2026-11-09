package com.catpuppyapp.puppygit.syntaxhighlight.markdown

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.catpuppyapp.puppygit.syntaxhighlight.base.MyStyleReceiver
import com.catpuppyapp.puppygit.syntaxhighlight.base.TextMateUtil
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import io.github.rosemoe.sora.lang.styling.Styles

private const val TAG = "MarkDownStyleReceiver"

class MarkDownStyleReceiver(
    val highlighter: MarkDownSyntaxHighlighter,
) : MyStyleReceiver(TAG, highlighter.myLang?.analyzeManager) {

    override fun handleStyles(styles: Styles) {
        val spansReader = styles.spans.read()
        val lines = highlighter.getTextLines()
        val lastIndex = lines.lastIndex

        // 换行符在显示的时候可能高度会有点高，
        // 如果想更灵活控制怎么显示，
        // 可改成构建annotatedString list，
        // 具体修改方法很简单：调换 buildAnnotatedString 和 for-each lines的代码即可
        val result = buildAnnotatedString {
            lines.forEachIndexedBetter { idx, line ->
                val spans = spansReader.getSpansOnLine(idx)
                TextMateUtil.forEachSpanResult(line, spans) { start, end, style ->
                    withStyle(style) {
                        append(line.substring(start, end))
                    }
                }

                if(idx != lastIndex) {
                    append('\n')
                }
            }
        }

        highlighter.release()

        highlighter.onReceive(result)
    }

}
