package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope


@Composable
fun SelectSyntaxHighlightingDialog(
    plScope: PLScope,
    closeDialog: () -> Unit,
    onClick: (selectedScope: PLScope) -> Unit,
) {
    SingleSelectDialog(
        currentItem = plScope,
        itemList = PLScope.SCOPES_NO_AUTO,
        text = { it.name },
        closeDialog = closeDialog,
        onClick = onClick
    )
}
