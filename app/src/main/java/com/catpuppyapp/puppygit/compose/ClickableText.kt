package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun SingleLineClickableText(
    text:String,
    onClick:(()->Unit)?
) {
    ClickableText(
        text = text,
        maxLines = 1,
        modifier = if(onClick == null) {
            Modifier
        } else {
            MyStyleKt.ClickableText.modifierNoPadding.clickable { onClick() }
        },
    )
}


@Composable
fun MultiLineClickableText(
    text:String,
    onClick:(()->Unit)?
) {
    ClickableText(
        text = text,
        modifier = if(onClick == null) {
            Modifier
        } else {
            MyStyleKt.ClickableText.modifierNoPadding.clickable { onClick() }
        },
    )
}

/**
 * 这个主要是为了统一可点击文字的style，不过为了兼容旧代码，没onclick事件，
 * 可以手动传modifier.clickable{}，或者，如果不需要改modifier参数，可用其他重载的带onClick()的实现
 */
@Composable
fun ClickableText(
    text:String,
    maxLines:Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    fontWeight: FontWeight? = MyStyleKt.TextItem.defaultFontWeight(),
    style:TextStyle = MyStyleKt.ClickableText.getStyle(),
    color:Color = MyStyleKt.ClickableText.getColor(),
    fontSize:TextUnit = MyStyleKt.ClickableText.fontSize,
    modifier: Modifier
) {
    Text(
        text = text,
        maxLines = maxLines,
        overflow = overflow,
        fontWeight = fontWeight,
        style = style,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
    )
}

@Composable
fun ClickableText(
    text:String,
    maxLines:Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    fontWeight: FontWeight? = MyStyleKt.TextItem.defaultFontWeight(),
    style:TextStyle = MyStyleKt.ClickableText.getStyle(),
    color:Color = MyStyleKt.ClickableText.getColor(),
    fontSize:TextUnit = MyStyleKt.ClickableText.fontSize,
    onClick: (() -> Unit)?
) {
    ClickableText(
        text = text,
        maxLines = maxLines,
        overflow = overflow,
        fontWeight = fontWeight,
        style = style,
        color = color,
        fontSize = fontSize,
        modifier = if(onClick == null) {
            Modifier
        } else {
            MyStyleKt.ClickableText.modifierNoPadding.clickable { onClick() }
        },
    )
}
