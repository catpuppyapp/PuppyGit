package dev.jeziellago.compose.markdowntext.plugins.syntaxhighlight

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

class SyntaxHighlight(val textLiteral: String) : CustomNode(), Delimited {
    override fun getOpeningDelimiter(): String {
        return DELIMITER
    }

    override fun getClosingDelimiter(): String {
        return DELIMITER
    }

    companion object {
        private const val DELIMITER = "=="
    }
}
