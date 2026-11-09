package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@Composable
fun CreateFileOrFolderDialog2(
    errMsg: MutableState<String>,
    fileName: CustomStateSaveable<TextFieldValue>,
    onCancel: () -> Unit,
    onOk: (fileName: String, isDir:Boolean) -> Boolean,
) {
    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val doCreate = { isDir:Boolean ->
        val createSuccess = onOk(fileName.value.text, isDir)
        if(createSuccess) {
            onCancel()
        }
    }

    val hasErr = {
        errMsg.value.isNotEmpty()
    }

    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.create))
        },
        text = {
            ScrollableColumn {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { event ->
                            // ctrl + enter to create folder
                            // enter to create file

                            if (event.type != KeyEventType.KeyDown) {
                                false
                            } else if(event.key == Key.Enter && event.isCtrlPressed) {
                                val isDir = true
                                doCreate(isDir)
                                true
                            } else if(event.key == Key.Enter && event.isCtrlPressed.not()) {
                                val isDir = false
                                doCreate(isDir)
                                true
                            } else {
                                false
                            }
                        }
                    ,

                    value = fileName.value,
                    singleLine = true,
                    onValueChange = {
                        //一修改就清空错误信息，然后点创建的时候会再检测，若有错误会再设置上
                        errMsg.value = ""

                        fileName.value = it
                    },
                    isError = hasErr(),
                    supportingText = {
                        if (hasErr()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = errMsg.value,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (hasErr()) {
                            Icon(imageVector= Icons.Filled.Error,
                                contentDescription=errMsg.value,
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    label = {
                        Text(stringResource(R.string.name))
                    },

                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        val isDir = false
                        doCreate(isDir)
                    }),
                )

            }
        },
        //点击弹框外区域的时候触发此方法，一般设为和OnCancel一样的行为即可
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            ScrollableRow {
                //按钮启用条件
                val maybeIsGoodFileName = fileName.value.text.isNotEmpty() && !hasErr()

                //创建文件夹（左边）
                TextButton(
                    enabled = maybeIsGoodFileName,
                    onClick = {
                        val isDir = true
                        doCreate(isDir)
                    },
                ) {
                    Text(
                        text = activityContext.getString(R.string.folder),
                    )
                }
                //创建文件（右边）
                TextButton(
                    enabled = maybeIsGoodFileName,
                    onClick = {
                        val isDir = false
                        doCreate(isDir)
                    },
                ) {
                    Text(
                        text = activityContext.getString(R.string.file),
                    )
                }


            }
        },

    )

    Focuser(focusRequester, scope)


}
