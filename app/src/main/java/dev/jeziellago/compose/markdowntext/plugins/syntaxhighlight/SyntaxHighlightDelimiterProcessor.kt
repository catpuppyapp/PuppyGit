package dev.jeziellago.compose.markdowntext.plugins.syntaxhighlight

import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

class SyntaxHighlightDelimiterProcessor(
    private val openingCharacter: Char,
    private val closingCharacter: Char,
    private val minLength: Int,
) : DelimiterProcessor {

    override fun getOpeningCharacter(): Char = openingCharacter

    override fun getClosingCharacter(): Char = closingCharacter

    override fun getMinLength(): Int = minLength

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() >= minLength && closer.length() >= minLength) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            minLength
        } else {
            0
        }
    }

    override fun process(opener: Text, closer: Text, delimiterCount: Int) {
        // Wrap nodes between delimiters in SyntaxHighlight.
        val syntaxHighlight: Node = SyntaxHighlight(opener.literal)

        var tmp = opener.next
        while (tmp != null && tmp !== closer) {
            val next = tmp.next
            syntaxHighlight.appendChild(tmp)
            tmp = next
        }

        opener.insertAfter(syntaxHighlight)
    }
}
