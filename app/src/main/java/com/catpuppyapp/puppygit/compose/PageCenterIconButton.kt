package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun PageCenterIconButton(
    contentPadding: PaddingValues,
    onClick: ()->Unit,
    icon:ImageVector,
    text:String,
    iconDesc:String? = text.ifBlank { null },
    elseContent: @Composable () -> Unit = {},  //这参数别放最后，避免和另一个重载版本的content搞混
    condition: Boolean = true,
    attachContent: @Composable () -> Unit = {},
) {
    FullScreenScrollableColumn(contentPadding) {
        CenterIconButton(
            icon = icon,
            text = text,
            iconDesc = iconDesc,
            attachContent = attachContent,
            condition = condition,
            elseContent = elseContent,
            onClick = onClick,
        )
    }
}
