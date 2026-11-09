package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SwipeIcon(
    modifier: Modifier = Modifier.padding(horizontal = 10.dp),
    imageVector: ImageVector,
    contentDescription: String? = null,
) {
    Icon(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = contentDescription
    )
}
