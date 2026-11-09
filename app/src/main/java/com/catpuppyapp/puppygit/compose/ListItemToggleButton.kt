package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.runtime.Composable
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun ListItemToggleButton(
    checked: Boolean,
    onCheckedChange:(Boolean)->Unit,
    content:@Composable ()->Unit,
) {
    FilledTonalIconToggleButton(
        shape = MyStyleKt.ToggleButton.defaultShape(),
        colors = MyStyleKt.ToggleButton.defaultColors(),
        checked = checked,
        onCheckedChange = onCheckedChange,
    ) {
        content()
    }
}

