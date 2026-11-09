package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import com.catpuppyapp.puppygit.constants.LineBreak


@Composable
fun SelectLineBreakDialog(
    current: LineBreak,
    closeDialog: () -> Unit,
    onClick: (selected: LineBreak) -> Unit,
) {
    SingleSelectDialog(
        currentItem = current,
        itemList = LineBreak.list,
        text = { it.visibleValue },
        closeDialog = closeDialog,
        onClick = onClick
    )
}
