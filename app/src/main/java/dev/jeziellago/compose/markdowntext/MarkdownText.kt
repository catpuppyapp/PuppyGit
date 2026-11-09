package dev.jeziellago.compose.markdowntext

import android.content.Context
import android.os.Build
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.TextViewCompat
import coil.ImageLoader
import dev.jeziellago.compose.markdowntext.plugins.image.ImagesPlugin
import io.noties.markwon.Markwon

/**
 * @param onLinkClicked handle the url and return a boolean indicated consumed, if return true, default url handler will not handle the link; if return false, default link handler will take it
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    linkColor: Color = Color.Unspecified,
    truncateOnTextOverflow: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    isTextSelectable: Boolean = false,
    autoSizeConfig: AutoSizeConfig? = null,
    @FontRes fontResource: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null,
    // this option will disable all clicks on links, inside the markdown text
    // it also enable the parent view to receive the click event
    disableLinkMovementMethod: Boolean = false,
    imageLoader: ImageLoader? = null,
    coilStore: ImagesPlugin.CoilStore? = null,
    linkifyMask: Int = Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS or Linkify.WEB_URLS,
    enableSoftBreakAddsNewLine: Boolean = true,
    syntaxHighlightColor: Color = Color.LightGray,
    syntaxHighlightTextColor: Color = Color.Unspecified,
    headingBreakColor: Color = Color.Transparent,
    enableUnderlineForLink: Boolean = true,
    importForAccessibility: Int = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO,
    beforeSetMarkdown: ((TextView, Spanned) -> Unit)? = null,
    afterSetMarkdown: ((TextView) -> Unit)? = null,
    onLinkClicked: (String) -> Boolean = {false},
    onTextLayout: ((numLines: Int) -> Unit)? = null
) {
    val defaultColor: Color = LocalContentColor.current
    val context: Context = LocalContext.current

    // this was remembered, if remember, must set dependencies params as key of remember,
    //   else will not update variable correctly, e.g. when navi to other file which parent difference to current,
    //   then the coilStore will not update, so, all relative path resource will not load correctly
    val markdownRender: Markwon = MarkdownRender.create(
        context,
        imageLoader,
        coilStore,
        linkifyMask,
        enableSoftBreakAddsNewLine,
        syntaxHighlightColor,
        syntaxHighlightTextColor,
        headingBreakColor,
        enableUnderlineForLink,
        beforeSetMarkdown,
        afterSetMarkdown,
        onLinkClicked,
        style
    )

    val androidViewModifier = if (onClick != null) {
        Modifier
            .clickable { onClick() }
            .then(modifier)
    } else {
        modifier
    }
    AndroidView(
        modifier = androidViewModifier,
        factory = { factoryContext ->

            val linkTextColor = linkColor.takeOrElse { style.color.takeOrElse { defaultColor } }

            CustomTextView(factoryContext).apply {
                viewId?.let { id = viewId }
                fontResource?.let { font -> applyFontResource(font) }
                importantForAccessibility = importForAccessibility

                setMaxLines(maxLines)
                setLinkTextColor(linkTextColor.toArgb())

                setTextIsSelectable(isTextSelectable)

                movementMethod = LinkMovementMethod.getInstance()

                if (truncateOnTextOverflow) enableTextOverflow()

                autoSizeConfig?.let { config ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            config.autoSizeMinTextSize,
                            config.autoSizeMaxTextSize,
                            config.autoSizeStepGranularity,
                            config.unit
                        )
                    }
                }
            }
        },
        update = { textView ->
            with(textView) {
                applyTextColor(style.color.takeOrElse { defaultColor }.toArgb())
                applyFontSize(style)
                applyLineHeight(style)
                applyTextDecoration(style)

                with(style) {
                    applyTextAlign(textAlign)
                    fontFamily?.let { applyFontFamily(this) }
                    fontStyle?.let { applyFontStyle(it) }
                    fontWeight?.let { applyFontWeight(it) }
                }
            }
            markdownRender.setMarkdown(textView, markdown)
            if (disableLinkMovementMethod) {
                textView.movementMethod = null
            }
            if (onTextLayout != null) {
                textView.post {
                    onTextLayout(textView.lineCount)
                }
            }
            textView.maxLines = maxLines
        }
    )
}
