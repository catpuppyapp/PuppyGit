package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R


@Composable
fun ConfirmDialog(
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.no),
    okBtnText: String = stringResource(R.string.yes),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    onCancel: () -> Unit,
    onOk: () -> Unit,
) {


    AlertDialog(
        title = {
            if(requireShowTitleCompose) {
                titleCompose()
            }else {
                DialogTitle(title)
            }
        },
        text = {
            if(requireShowTextCompose) {
                textCompose()
            }else {
                CopyScrollableColumn {
                    Row {
                        Text(text)
                    }
                }
            }
        },
        //点击弹框外区域的时候触发此方法，一般设为和OnCancel一样的行为即可
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text(
                    text = cancelBtnText,
                    color = cancelTextColor,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = okBtnEnabled,
                onClick = {
                    //执行用户传入的callback
                    onOk()
                },
            ) {
                Text(
                    text = okBtnText,
                    color = if(okBtnEnabled) okTextColor else Color.Unspecified,
                )
            }
        },

    )


}