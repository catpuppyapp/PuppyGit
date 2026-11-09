package com.catpuppyapp.puppygit.compose

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.saf.MyOpenDocumentTree
import com.catpuppyapp.puppygit.utils.saf.SafUtil

private const val TAG = "SystemFolderChooserSaf"

/**
 * A Folder Chooser depend System File Chooser, may not work if system removed internal file picker, in that case, can input path instead
 */
@Deprecated("replace with `InternalFileChooser`")
@Composable
fun SystemFolderChooserSaf(
    activityContext: Context,
    safEnabled:MutableState<Boolean>,
    safPath:MutableState<String>,
    nonSafPath:MutableState<String>,
    path:MutableState<String>,
    pathTextFieldLabel:String=stringResource(R.string.path),
    pathTextFieldPlaceHolder:String=stringResource(R.string.eg_storage_emulate_0_repos),
    showSafSwitchButton:Boolean = false,
    chosenPathCallback:(uri: Uri?)->Unit = {uri->
        if(uri != null) {
            if(AppModel.devModeOn) {
                //example of output: uri.toString() == uri.path: false, uri.toString()=content://com.android.externalstorage.documents/tree/primary%3ARepos, uri.path=/tree/primary:Repos
                // toString更完整些，是我期望的safPath，uri.path缺乏一些信息，不行
                MyLog.d(TAG, "uri.toString() == uri.path: ${uri.toString() == uri.path}, uri.toString()=${uri.toString()}, uri.path=${uri.path}")
            }

            safPath.value = SafUtil.uriToDbSupportedFormat(uri)

            //最初是检查realPath.isNotBlank()才调用回调，但感觉检查与否意义不大，如果路径真为空，就清空也没什么
            nonSafPath.value = FsUtils.getRealPathFromUri(uri)

            path.value = if(safEnabled.value) safPath.value else nonSafPath.value

            MyLog.d(TAG, "#chooseDirLauncher: uri.toString()=${uri.toString()}, uri.path=${uri.path}, safEnabled=${safEnabled.value}, safPath=${safPath.value}, nonSafPath=${nonSafPath.value}")
        }
    }
) {


    val chooseDirLauncher = rememberLauncherForActivityResult(MyOpenDocumentTree()) { uri ->
        if(uri != null){
            //获取永久访问权限
            SafUtil.takePersistableRWPermission(activityContext.contentResolver, uri)

            //更新path
            chosenPathCallback(uri)
        }
    }


    GrantManageStoragePermissionClickableText(activityContext)

    TextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MyStyleKt.defaultHorizontalPadding),
        value = path.value,
        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
        onValueChange = {
            path.value = it
        },
        label = {
            Text(pathTextFieldLabel)
        },
        placeholder = {
            Text(pathTextFieldPlaceHolder)
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    //show folder chooser
                    chooseDirLauncher.launch(null)
                }

            ) {
                Icon(imageVector = Icons.Filled.MoreHoriz, contentDescription = stringResource(R.string.three_dots_icon_for_choose_folder))
            }
        }
    )



//        Spacer(Modifier.height(15.dp))
//        Text(stringResource(R.string.if_unable_choose_a_path_just_copy_paste_instead), fontWeight = FontWeight.Light)

    if(showSafSwitchButton) {
        Spacer(Modifier.height(15.dp))
        MyCheckBox(text = stringResource(R.string.saf_mode), value = safEnabled, onValueChange = { newSafEnabledValue ->
            path.value = if (newSafEnabledValue) {
                safPath.value
            } else {
                nonSafPath.value
            }

            safEnabled.value = newSafEnabledValue
        })
        DefaultPaddingText(stringResource(R.string.saf_mode_note))
    }
}
