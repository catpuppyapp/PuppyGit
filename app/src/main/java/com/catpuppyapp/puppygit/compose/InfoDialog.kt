package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R

@Composable
fun InfoDialog(
    showTitleInfoDialog: MutableState<Boolean>,
    content: @Composable ColumnScope.()->Unit,
) {
    ConfirmDialog2(
        title = stringResource(R.string.info),
        requireShowTextCompose = true,
        textCompose = {
            MySelectionContainer {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    content()
                }
            }
        },
        onCancel = { showTitleInfoDialog.value = false },
        cancelBtnText = stringResource(R.string.close),
        showOk = false
    ) { }
}
