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
fun ConfirmDialog3(
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
    customOk:(@Composable ()->Unit)? = null,
    customCancel:(@Composable ()->Unit)? = null,
    onCancel: () -> Unit = {},

    //点击非弹窗区域时执行的操作，若不指定则和onCancel行为一致，除非和onCancel行为不一样，
    //   否则即使使用 `customCancel`，也推荐设置onCancel而不是设置onDismiss
    //click outside of dialog will call this function, recommend use `onCancel` instead passing this param,
    //  if it has same behavior with `onCancel`, even use `customCancel`.
    onDismiss: ()->Unit = onCancel,
    onOk: () -> Unit = {},
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
                if(customCancel != null) {
                    customCancel()
                }else {
                    TextButton(
                        onClick = onCancel
                    ) {
                        Text(
                            text = cancelBtnText,
                            color = cancelTextColor,
                        )
                    }
                }
            }
        },
        confirmButton = {
            if(showOk) {
                if(customOk != null) {
                    customOk()
                }else {
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

            }
        },

    )


}