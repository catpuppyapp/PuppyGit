package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.utils.UIHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun <T> SingleSelectDialog(
    currentItem: T?,
    itemList: List<T>,
    text: (T) -> String,
    selected:(T) -> Boolean = { it == currentItem },
    closeDialog: () -> Unit,
    onClick: (selectedItem: T) -> Unit,
) {

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    PlainDialogWithPadding(onClose = closeDialog) {
        LazyColumn(state = listState) {
            itemList.forEachIndexed { idx, it ->
                item {
                    SingleSelectionItem(
                        idx = idx,
                        item = it,
                        selected = selected(it),
                        minHeight = 60.dp,
                        text = { idx, it -> text(it) },
                        onClick = { idx, it ->
                            closeDialog()
                            onClick(it)
                        },
                    )
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        scope.launch {
            delay(200)

            UIHelper.scrollByPredicate(scope, itemList, listState, animation = true) { _, it ->
                selected(it)
            }
        }
    }
}
