package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.catpuppyapp.puppygit.style.MyStyleKt

/**
 * 一般用来显示弹窗中的 CheckBox 类似的组件的 note 文本，这组件带左右padding，所以文字也需要padding下以对齐
 */
@Composable
fun PaddingText(
    text:String,
    color: Color = Color.Unspecified,
    paddingValues: PaddingValues = PaddingValues(horizontal = MyStyleKt.defaultHorizontalPadding),
    fontWeight: FontWeight? = FontWeight.Light,
) {
    Text(
        text = text,
        color = color,
        fontWeight = fontWeight,
        modifier = Modifier.padding(paddingValues)
    )
}

@Composable
fun DefaultPaddingText(
    text:String,
    color: Color = Color.Unspecified
) {
    PaddingText(text, color)
}
