package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import com.catpuppyapp.puppygit.utils.EncodingUtil


@Composable
fun SelectEncodingDialog(
    currentCharset: String?,
    closeDialog: () -> Unit,
    onClick: (selectedCharset: String) -> Unit,
) {
    SingleSelectDialog(
        currentItem = currentCharset,
        itemList = EncodingUtil.supportedCharsetList,
        text = { it },
        closeDialog = closeDialog,
        onClick = onClick
    )
}
