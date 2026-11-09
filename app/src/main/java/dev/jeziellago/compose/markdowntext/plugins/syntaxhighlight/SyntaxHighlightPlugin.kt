package dev.jeziellago.compose.markdowntext.plugins.syntaxhighlight

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.core.factory.CodeSpanFactory
import org.commonmark.parser.Parser

class SyntaxHighlightPlugin : AbstractMarkwonPlugin() {

    override fun configureParser(builder: Parser.Builder) {
        builder.extensions(setOf(SyntaxHighlightExtension.create()))
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(
            SyntaxHighlight::class.java,
            CodeSpanFactory()
        )
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(
            SyntaxHighlight::class.java
        ) { visitor, syntaxHighlight ->
            val length = visitor.length()
            visitor.builder().append('\u00a0')
            visitor.visitChildren(syntaxHighlight)
            visitor.builder().append('\u00a0')
            visitor.setSpansForNodeOptional(syntaxHighlight, length)
        }
    }
}
