package com.catpuppyapp.puppygit.compose

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.navToFileChooser
import com.catpuppyapp.puppygit.screen.shared.FileChooserType
import com.catpuppyapp.puppygit.style.MyStyleKt


/**
 * App Internal File Chooser, no-depend system file chooser, also no saf support
 */
@Composable
fun InternalFileChooser(
    activityContext: Context,  //这个最好从外部传，在弹窗内获取的Context，无法转换为Activity，除非递归查找
    path:MutableState<String>,
    chooserType: FileChooserType = FileChooserType.SINGLE_DIR,  //默认选dir，如果想选文件，可传对应类型
    pathTextFieldLabel:String=stringResource(R.string.path),
    pathTextFieldPlaceHolder:String=stringResource(R.string.eg_storage_emulate_0_repos),
) {
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
                    navToFileChooser(chooserType)
                }

            ) {
                Icon(imageVector = Icons.Filled.MoreHoriz, contentDescription = stringResource(R.string.three_dots_icon_for_choose_folder))
            }
        }
    )

}
