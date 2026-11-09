package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * 小型浮动按钮
 * alpha:按钮和背景的透明度
 */
@Composable
fun SmallFab(modifier: Modifier = Modifier, icon:ImageVector, iconDesc:String, alpha:Float = 0.5f, onClick: () -> Unit) {
    SmallFloatingActionButton(
        //除了弹出输入法时加padding，多选模式也需要加padding，不然Fab会盖住底栏按钮
        modifier=modifier,
        //不要提升，不然会有投影，半透明背景会有块空白，不好看
        elevation= FloatingActionButtonDefaults.elevation(0.dp,0.dp,0.dp,0.dp),
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha=alpha),
        contentColor = MaterialTheme.colorScheme.secondary.copy(alpha = alpha)
    ) {
        Icon(icon, iconDesc)
    }
}
