package com.catpuppyapp.puppygit.dto

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MenuIconBtnItem (
    val icon:ImageVector,
    val text:String,
    val desc:String = text, // for accessbility
    val size:Dp = defaultIconSize,
    val pressedCircleSize:Dp = defaultPressedCircleSize,
    val enabled:()->Boolean={true},
    val visible:()->Boolean={true},
    val onClick:()->Unit,
) {
    companion object {
        val defaultIconSize = 24.dp
        val defaultPressedCircleSize = 32.dp

    }
}
