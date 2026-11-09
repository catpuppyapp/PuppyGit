package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R


/**
 * difference with `ConfirmDialog`: this support hidden onOk/onCancel Button and custom onDismiss(default do onCancel)
 * and the default confirm text is "OK", denied text is "Cancel"
 */
@Composable
fun ConfirmDialog2(
    modifier: Modifier=Modifier,
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.cancel),
    okBtnText: String = stringResource(R.string.ok),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    showOk:Boolean = true,
    showCancel:Boolean = true,
    onCancel: () -> Unit,
    onDismiss: ()->Unit = onCancel,  //点击非弹窗区域时执行的操作，若不指定则和onCancel行为一致
    onOk: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
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
        onDismissRequest = onDismiss,
        dismissButton = {
            if(showCancel) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(
                        text = cancelBtnText,
                        color = cancelTextColor,
                    )
                }
            }
        },
        confirmButton = {
            if(showOk) {
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

            }
        },

    )


}