package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt


@Composable
fun InLineIcon(
    icon: ImageVector,
    tooltipText: String,  //若为empty，不显示长按提示文本
    iconContentDesc: String? = tooltipText,
    iconModifier: Modifier = Modifier.size(MyStyleKt.defaultInLineIconSize),
    pressedCircleSize: Dp = MyStyleKt.defaultInLineIconsPressedCircleSize,
    enabled: Boolean = true,
    iconColor: Color = LocalContentColor.current,
    onClick: (() -> Unit)? = null
) {
    LongPressAbleIconBtn(
        tooltipText = tooltipText,
        icon = icon,
        iconContentDesc = iconContentDesc,
        iconModifier = iconModifier,
        enabled = enabled,
        iconColor = iconColor,
        pressedCircleSize = pressedCircleSize,
        onClick = onClick,
    )
}


@Composable
fun InLineCopyIcon(
    onClick: () -> Unit
) {
    InLineIcon(
        icon = Icons.Filled.ContentCopy,
        tooltipText = stringResource(R.string.copy),
    ){
        onClick()
    }
}

@Composable
fun InLineHistoryIcon(
    tooltipText: String = stringResource(R.string.commit_history),
    onClick: () -> Unit
) {
    InLineIcon(
        icon = Icons.Filled.History,
        tooltipText = tooltipText,
    ){
        onClick()
    }
}

@Composable
fun InLineFolderIcon(
    tooltipText: String = stringResource(R.string.show_in_files),
    onClick: () -> Unit
) {
    InLineIcon(
        icon = Icons.Filled.Folder,
        tooltipText = tooltipText,
    ){
        onClick()
    }
}
