package com.catpuppyapp.puppygit.fileeditor.ui.composable.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.ui.theme.Theme

@Composable
fun FieldIcon(
    isMultipleSelection: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
//    val inDarkTheme = Theme.inDarkTheme

    Box(modifier = modifier) {
        when {
            isMultipleSelection && isSelected -> {
                CheckCircleIcon(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            }
            isSelected -> {
//                这个菜单图标在行号下面，当没选中行时，应该尽量降低存在感，DarkMode下要比行号暗（但不要暗到看不见），正常模式下要比行号亮（但不要刺眼）
                MenuIcon(modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }
        }
    }
}

@Preview
@Composable
private fun FieldIcon_Preview() {
    MaterialTheme {
        Column {
            FieldIcon(isMultipleSelection = false, isSelected = false, modifier = Modifier.size(32.dp))
            FieldIcon(isMultipleSelection = false, isSelected = true, modifier = Modifier.size(32.dp))
            FieldIcon(isMultipleSelection = true, isSelected = true, modifier = Modifier.size(32.dp))
        }
    }
}


@Composable
fun FieldIcon(
    focused: Boolean,
    modifier: Modifier = Modifier
) {
    val inDarkTheme = Theme.inDarkTheme

    Box(modifier = modifier) {
        if (focused){
            // 这个菜单图标在行号下面，当没选中行时，应该尽量降低存在感，DarkMode下要比行号暗（但不要暗到看不见），正常模式下要比行号亮（但不要刺眼）
            MenuIcon(modifier = Modifier.align(Alignment.Center), color = Color.Gray)
        }
    }
}
