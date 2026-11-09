package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DropDownMenuItemText(
    text1:String,
    text2:String = "",
    basePadding: PaddingValues = PaddingValues(0.dp),

    text1Scrollable: Boolean = true,
    text2Scrollable: Boolean = true,

    trailIcon: ImageVector? = null,
    trailIconWidth: Dp = 0.dp,
    trailIconOnClick: () -> Unit = {},
) {
    TwoLineSettingsItem(
        text1 = text1,
        text2 = text2,
        basePadding = basePadding,
        text1Scrollable = text1Scrollable,
        text2Scrollable = text2Scrollable,
        trailIcon = trailIcon,
        trailIconWidth = trailIconWidth,
        trailIconOnClick = trailIconOnClick
    )
}
