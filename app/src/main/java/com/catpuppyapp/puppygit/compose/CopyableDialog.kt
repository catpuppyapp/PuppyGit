package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading


@Composable
fun CopyableDialog(
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.close),
    okBtnText: String = stringResource(R.string.copy),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    loadingOn:(loadingText:String)->Unit={},
    loadingOff:()->Unit={},
    loadingText: String= stringResource(R.string.loading),
    onCancel: () -> Unit,
    onOk: suspend () -> Unit,  //加suspend是为了避免拷贝的时候卡住或抛异常，不过外部不需要开协程，本组件内开就行了，调用者只写逻辑即可
) {
    //本来是想：如果执行完操作的某个阶段需要改loadingText，可使用此状态变量作为doJob协程的参数并在需要更新loadingText时修改此状态变量的值。但实际上：在onOk里更新loadingText就行了
//    val loadingTextState = StateUtil.getRememberSaveableState(initValue = loadingText)

    CopyableDialog2(
        title = title,
        text = text,
        requireShowTitleCompose = requireShowTitleCompose,
        titleCompose = titleCompose,
        requireShowTextCompose = requireShowTextCompose,
        textCompose = textCompose,
        cancelBtnText = cancelBtnText,
        okBtnText = okBtnText,
        cancelTextColor = cancelTextColor,
        okTextColor= okTextColor,
        okBtnEnabled= okBtnEnabled,
        loadingOn= loadingOn,
        loadingOff= loadingOff,
        loadingText= loadingText,
        onCancel= onCancel,
        onOk= onOk,
        cancelCompose = {
            TextButton(
                onClick = onCancel
            ) {
                Text(
                    text = cancelBtnText,
                    color = cancelTextColor,
                )
            }
        },
        okCompose = {
            TextButton(
                enabled = okBtnEnabled,
                onClick = {
                    //执行用户传入的callback
                    doJobThenOffLoading(loadingOn, loadingOff, loadingText) {
                        onOk()
                    }
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


@Composable
fun CopyableDialog2(
    title: String="",
    text: String="",
    requireShowTitleCompose:Boolean=false,
    titleCompose:@Composable ()->Unit={},
    requireShowTextCompose:Boolean=false,
    textCompose:@Composable ()->Unit={},
    cancelBtnText: String = stringResource(R.string.close),
    okBtnText: String = stringResource(R.string.copy),
    cancelTextColor: Color = Color.Unspecified,
    okTextColor: Color = Color.Unspecified,
    okBtnEnabled: Boolean=true,
    loadingOn:(loadingText:String)->Unit={},
    loadingOff:()->Unit={},
    loadingText: String= stringResource(R.string.loading),
    cancelCompose: (@Composable ()->Unit)? = null,
    okCompose: (@Composable ()->Unit)? = null,

    //若cancelCompose为null，则此函数为 cancel按钮 和 onDismiss 回调；否则仅为onDismiss回调。（p.s. onDismiss就是弹窗显示的情况下，点击非弹窗区域触发的那个函数）
    onCancel: () -> Unit,
    onDismiss: ()->Unit = onCancel,

    onOk: suspend () -> Unit,  //加suspend是为了避免拷贝的时候卡住或抛异常，不过外部不需要开协程，本组件内开就行了，调用者只写逻辑即可
) {
    //本来是想：如果执行完操作的某个阶段需要改loadingText，可使用此状态变量作为doJob协程的参数并在需要更新loadingText时修改此状态变量的值。但实际上：在onOk里更新loadingText就行了
//    val loadingTextState = StateUtil.getRememberSaveableState(initValue = loadingText)

    AlertDialog(
        title = {
            //避免和外部的selection冲突导致长按选择文本时app崩溃
            // 例如，旧版的compose（新版不是不崩，只是我没测试过，不确定），
            // 外部组件用selection container包了整个屏幕，然后显示弹窗，然后长按弹窗内文字，就会崩溃
            MySelectionContainer {
                if(requireShowTitleCompose) {
                    titleCompose()
                }else {
                    DialogTitle(title)
                }
            }
        },
        text = {
            MySelectionContainer {
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
            if(cancelCompose == null) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(
                        text = cancelBtnText,
                        color = cancelTextColor,
                    )
                }
            }else {
                cancelCompose()
            }
        },
        confirmButton = {
            if(okCompose == null) {
                TextButton(
                    enabled = okBtnEnabled,
                    onClick = {
                        //执行用户传入的callback
                        doJobThenOffLoading(loadingOn, loadingOff, loadingText) {
                            onOk()
                        }
                    },
                ) {
                    Text(
                        text = okBtnText,
                        color = if(okBtnEnabled) okTextColor else Color.Unspecified,
                    )
                }
            }else {
                okCompose()
            }
        },

    )


}
