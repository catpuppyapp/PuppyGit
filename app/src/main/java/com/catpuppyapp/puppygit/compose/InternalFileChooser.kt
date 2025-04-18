package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.navToFileChooser
import com.catpuppyapp.puppygit.screen.shared.FileChooserType


/**
 * App Internal File Chooser, no-depend system file chooser, also no saf support
 */
@Composable
fun InternalFileChooser(
    path:MutableState<String>,
    chooserType: FileChooserType = FileChooserType.SINGLE_DIR,  //默认选dir，如果想选文件，可传对应类型
    pathTextFieldLabel:String=stringResource(R.string.path),
    pathTextFieldPlaceHolder:String=stringResource(R.string.eg_storage_emulate_0_repos),
) {
    //这里不需要能滚动，应该由使用此组件的组件考虑是否能滚动
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(.8f),
                value = path.value,
                maxLines = 6,
                onValueChange = {
                    path.value = it
                },
                label = {
                    Text(pathTextFieldLabel)
                },
                placeholder = {
                    Text(pathTextFieldPlaceHolder)
                }
            )

            IconButton(
                onClick = {
                    navToFileChooser(chooserType)
                }

            ) {
                Icon(imageVector = Icons.Filled.MoreHoriz, contentDescription = stringResource(R.string.three_dots_icon_for_choose_folder))
            }

        }
    }
}
