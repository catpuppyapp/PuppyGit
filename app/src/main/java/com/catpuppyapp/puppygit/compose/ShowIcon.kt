package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun ShowIcon(
    icon: ImageVector,
    contentDescription: String?,
    iconColor: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = iconColor,
        modifier = modifier
    )
}
