package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp


@Composable
fun SizeIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription:String?,
    size: Dp,
    tint:Color = LocalContentColor.current,
) {
    Icon(
        modifier = modifier.size(size),
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
    )
}
