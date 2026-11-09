package com.catpuppyapp.puppygit.fileeditor.ui.composable.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditorMenu(
    icon: @Composable BoxScope.() -> Unit,
    label: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { icon() }
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { label() }
    }
}

@Preview
@Composable
private fun EditorMenu_Preview() {
    EditorMenu(
        icon = { CancelIcon() },
        label = { Text(text = "Cancel") }
    )
}
