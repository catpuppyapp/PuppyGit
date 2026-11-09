package dev.jeziellago.compose.markdowntext.plugins.syntaxhighlight

import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.parser.Parser.ParserExtension
import org.commonmark.renderer.text.TextContentRenderer
import org.commonmark.renderer.text.TextContentRenderer.TextContentRendererExtension

class SyntaxHighlightExtension private constructor() : ParserExtension,
    TextContentRendererExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(
            SyntaxHighlightDelimiterProcessor(
                openingCharacter = '=',
                closingCharacter = '=',
                minLength = 2,
            ),
        )
    }

    override fun extend(rendererBuilder: TextContentRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context ->
            SyntaxHighlightNodeRenderer(
                context
            )
        }
    }

    companion object {
        fun create(): Extension {
            return SyntaxHighlightExtension()
        }
    }
}
