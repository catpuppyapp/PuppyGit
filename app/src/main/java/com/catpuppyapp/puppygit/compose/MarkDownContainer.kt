package com.catpuppyapp.puppygit.compose

import android.text.util.Linkify
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.markdown.MdUtil
import dev.jeziellago.compose.markdowntext.MarkdownText

// support selection as default, need not use `SelectionContainer` wrap it
@Composable
fun MarkDownContainer(
    content:String,
    modifier:Modifier = Modifier,
    basePathNoEndSlash:String = "",
    style: TextStyle = LocalTextStyle.current,
    onLinkClicked: (String) -> Boolean = {false},
) {
    val activityContext = LocalContext.current
    val inDarkTheme = Theme.inDarkTheme

    MarkdownText(
        modifier = modifier,
        markdown = content,
        linkifyMask = Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS,
        onLinkClicked = onLinkClicked,
        linkColor = MyStyleKt.ClickableText.getColor(),
        style = style,

        // enable selection and copy
        isTextSelectable = true,

        syntaxHighlightColor = if(inDarkTheme) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceDim,
        coilStore = MdUtil.getCoilStore(context = activityContext, basePathNoEndSlash = basePathNoEndSlash)
    )
}
