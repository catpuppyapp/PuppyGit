package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun OpenAsAskReloadDialog(
    onCancel:()->Unit,
    doReload:()->Unit,
) {
    ConfirmDialogAndDisableSelection(
        title = stringResource(R.string.reload_file),
        requireShowTextCompose = true,
        textCompose = {
            CopyScrollableColumn {
                Text(stringResource(R.string.back_editor_from_external_app_ask_reload))
            }
        },
        okBtnText = stringResource(R.string.reload),
        cancelBtnText = stringResource(R.string.cancel),
        cancelTextColor = MyStyleKt.TextColor.danger(),
        onCancel = onCancel,
        onOk = doReload,
    )
}
