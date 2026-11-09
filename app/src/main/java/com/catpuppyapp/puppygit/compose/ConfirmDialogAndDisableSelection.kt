package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R


/**
 * 原型拷贝的ConfirmDialog3，添加了禁用长按选择的功能。
 *
 * 应用场景：适用于整个页面启用长按选择的场景，例如Diff页面，这时如果弹窗不禁用长按选择，就会在长按弹窗非输入框文本时导致app崩溃
 *
 * 注意：MySelectionContainer和DisableSelection可互相嵌套，所以，如果在类似Diff页面那样整个页面启用了长按选择的情况下担心弹窗崩溃，可使用此弹窗，并且可在此弹窗内容中使用 MySelectionContainer 以使弹窗内容可拷贝（这样启用的长按选择不会导致app崩溃）
 */
@Composable
fun ConfirmDialogAndDisableSelection(
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
    onCancel: () -> Unit,
    onDismiss: ()->Unit = onCancel,  //点击非弹窗区域时执行的操作，若不指定则和onCancel行为一致
    onOk: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            DisableSelection {
                if(requireShowTitleCompose) {
                    titleCompose()
                }else {
                    DialogTitle(title)
                }
            }
        },
        text = {
            DisableSelection {
                if(requireShowTextCompose) {
                    textCompose()
                }else {
                    ScrollableColumn {
                        Text(text)
                    }
                }
            }
        },
        //点击弹框外区域的时候触发此方法，一般设为和OnCancel一样的行为即可
        onDismissRequest = onDismiss,
        dismissButton = {
            DisableSelection {
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
            }
        },
        confirmButton = {
            DisableSelection {
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
            }
        },
    )
}
