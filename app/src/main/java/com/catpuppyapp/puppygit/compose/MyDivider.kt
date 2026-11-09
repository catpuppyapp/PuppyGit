package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp


@Composable
fun MyHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color.copy(alpha = 0.4f),
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}
