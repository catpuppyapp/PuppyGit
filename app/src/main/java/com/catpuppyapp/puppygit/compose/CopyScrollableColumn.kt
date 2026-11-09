package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CopyScrollableColumn(content:@Composable ()->Unit) {
    //先禁用选择，再启用，避免外部如果被选择容器包围，弹窗再加选择容器会崩溃（compose曾经的bug，不知道是否已经修复）
    MySelectionContainer {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            content()
        }
    }
}
