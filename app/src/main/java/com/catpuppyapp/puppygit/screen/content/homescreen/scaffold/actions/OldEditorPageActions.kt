package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable

@Composable
fun OldEditorPageActions(
    editorPageShowingFilePath: MutableState<String>,
//    editorPageRequireOpenFilePath: MutableState<String>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    needRefreshEditorPage: MutableState<String>,
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
//    editorPageShowSaveDoneToast: MutableState<Boolean>,
    isSaving: MutableState<Boolean>,
    isEdited: MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    showCloseDialog: MutableState<Boolean>,
    closeDialogCallback:CustomStateSaveable<(Boolean)->Unit>,
    doSave:suspend ()->Unit,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    editorPageRequest:MutableState<String>,
) {
    /*
        注意：如果以后需要同一个EditorInnerPage配合多个title，就不要在这执行操作了，把这里的action逻辑放到EditorInnerPage执行，在这只发request，类似ChangeList页面请求执行pull/push那样
     */



    //改成统一在EditorInnerPage关闭了
//    val closeFile = {
//        //关闭打开文件，随便写的，不知道好使不好使
//        loadingOn(appContext.getString(R.string.closing))
//        editorPageShowingFilePath.value=""
//        editorPageRequireOpenFilePath.value=""
//        editorPageShowingFileIsReady.value=false
//        isEdited.value=false
//        isSaving.value=false
//        loadingOff()
//        changeStateTriggerRefreshPage(needRefreshEditorPage)
//    }

//    val doSave = {
//        loadingOn(appContext.getString(R.string.saving))
//        isSaving.value = true
//        changeStateTriggerRefreshPage(needRefreshEditorPage)
//
//        FsUtils.saveFile(editorPageShowingFilePath.value, editorPageTextEditorState.value.getAllText())
//
//        Msg.requireShow(appContext.getString(R.string.file_saved))
//        isSaving.value = false
//        isEdited.value = false
//        loadingOff()
//
//        changeStateTriggerRefreshPage(needRefreshEditorPage)
//    }

    if (editorPageShowingFilePath.value.isNotBlank()) {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.close),
            icon = Icons.Filled.Close,
            iconContentDesc = stringResource(id = R.string.close),
        ) {
            showCloseDialog.value=true

//            if(isEdited.value) {
//                //显示弹窗，询问用户是否保存文件
//                showCloseDialog.value=true
//                //设置弹窗的确认和取消回调，弹窗会把用户选择的是取消还是保存传给回调。这个函数也可用cache替换，或其他方式，只要能共享内存即可
//                closeDialogCallback.value = { requireSave ->
//                    doJobThenOffLoading {
//                        if (requireSave) { //用户如果选择了保存则执行保存
//                            doSave()
//                        }
//                        //关闭文件
//                        closeFile()
//                        changeStateTriggerRefreshPage(needRefreshEditorPage)
//                    }
//                }
//            }else {  //没编辑过，直接关闭
//                closeFile()
//                changeStateTriggerRefreshPage(needRefreshEditorPage)
//            }
        }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.reload_file),
            icon = Icons.Filled.Refresh,
            iconContentDesc = stringResource(id = R.string.reload_file),
        ) {
            showReloadDialog.value = true

        }

        LongPressAbleIconBtn(
            enabled = editorPageShowingFileIsReady.value && isEdited.value && !isSaving.value,  //文件未就绪时不能保存
            tooltipText = stringResource(R.string.save),
            icon = Icons.Filled.Save,
            iconContentDesc = stringResource(id = R.string.save),
        ) {
            editorPageRequest.value = PageRequest.requireSave
//            //保存文件
//            if(isEdited.value) {
//                //离开页面时，保存文件。 ( x 20240421 已经可记住了) 无法记住上次滚动位置，还是不行！)
//                doJobThenOffLoading {
//                    doSave()
//                }
//            }
        }
        LongPressAbleIconBtn(
//            enabled = editorPageShowingFileIsReady.value,
            enabled = true,  // open as 永远启用
            tooltipText = stringResource(R.string.open_as),
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            iconContentDesc = stringResource(id = R.string.open_as),
        ) {
            editorPageRequest.value = PageRequest.requireOpenAs

        }
    }
}
