package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dev.DevFeature
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@Composable
fun FilesPageActions(
    isFileChooser:Boolean,
//    showCreateFileOrFolderDialog: MutableState<Boolean>,
    refreshPage: () -> Unit,
    filterOn: () -> Unit,
    filesPageGetFilterMode:()->Int,
    doFilter:(String)->Unit,
    requestFromParent:MutableState<String>,
    filesPageSimpleFilterOn:MutableState<Boolean>,
    filesPageSimpleFilterKeyWord:CustomStateSaveable<TextFieldValue>
) {

    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }


    if(!filesPageSimpleFilterOn.value) { //没filter的常规模式或显示filter结果模式
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.filter_files),
            icon = Icons.Filled.Search,
            iconContentDesc = stringResource(id = R.string.filter_files),
        ) {
            filesPageSimpleFilterKeyWord.value = TextFieldValue("")
            filesPageSimpleFilterOn.value = true
        }

        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.refresh),
            icon = Icons.Filled.Refresh,
            iconContentDesc = stringResource(id = R.string.refresh),
        ) {
            refreshPage()
        }
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.create),
            icon = Icons.Filled.Add,
            iconContentDesc = stringResource(id = R.string.create),
        ) {
            requestFromParent.value=PageRequest.createFileOrFolder
        }


        //菜单图标
        LongPressAbleIconBtn(
            //这种需展开的菜单，禁用内部的选项即可
//        enabled = enableAction.value,

            tooltipText = stringResource(R.string.menu),
            icon = Icons.Filled.MoreVert,
            iconContentDesc = stringResource(R.string.menu),
            onClick = {
                //切换菜单展开状态
                dropDownMenuExpandState.value = !dropDownMenuExpandState.value
            }
        )
        Row(modifier = Modifier.padding(top = MyStyleKt.TopBar.dropDownMenuTopPaddingSize)) {
            val enableMenuItem = true
            //菜单列表
            DropdownMenu(
                expanded = dropDownMenuExpandState.value,
                onDismissRequest = { dropDownMenuExpandState.value = false }
            ) {
//                DropdownMenuItem(
//                    enabled = enableMenuItem,
//                    text = { Text(stringResource(R.string.internal_storage)) },
//                    onClick = {
//                        requestFromParent.value = PageRequest.goToInternalStorage
//                        dropDownMenuExpandState.value = false
//                    }
//                )
//                DropdownMenuItem(
//                    enabled = enableMenuItem,
//                    text = { Text(stringResource(R.string.external_storage)) },
//                    onClick = {
//                        requestFromParent.value = PageRequest.goToExternalStorage
//                        dropDownMenuExpandState.value = false
//                    }
//                )
//                if(AppModel.devModeOn) {
//                    DropdownMenuItem(
//                        enabled = enableMenuItem,
//                        text = { Text(DevFeature.inner_data_storage) },
//                        onClick = {
//                            requestFromParent.value = PageRequest.goToInnerDataStorage
//                            dropDownMenuExpandState.value = false
//                        }
//                    )
//
//                    DropdownMenuItem(
//                        enabled = enableMenuItem,
//                        text = { Text(DevFeature.external_data_storage) },
//                        onClick = {
//                            requestFromParent.value = PageRequest.goToExternalDataStorage
//                            dropDownMenuExpandState.value = false
//                        }
//                    )
//                }

                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.copy_full_path)) },
                    onClick = {
                        requestFromParent.value = PageRequest.copyFullPath
                        dropDownMenuExpandState.value = false
                    }
                )

                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.copy_repo_relative_path)) },
                    onClick = {
                        requestFromParent.value = PageRequest.copyRepoRelativePath
                        dropDownMenuExpandState.value = false
                    }
                )

                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.go_to)) },
                    onClick = {
                        requestFromParent.value = PageRequest.goToPath
                        dropDownMenuExpandState.value = false
                    }
                )

                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.view_and_sort)) },
                    onClick = {
                        requestFromParent.value = PageRequest.showViewAndSortMenu
                        dropDownMenuExpandState.value = false
                    }
                )


                if(isFileChooser.not()) {
                    DropdownMenuItem(
                        enabled = enableMenuItem,
                        text = { Text(stringResource(R.string.import_str)) },
                        onClick = {
                            requestFromParent.value = PageRequest.safImport
                            dropDownMenuExpandState.value = false
                        }
                    )


                    if(AppModel.devModeOn) {
                        DropdownMenuItem(
                            enabled = enableMenuItem,
                            text = { Text(DevFeature.appendDevPrefix(stringResource(R.string.export))) },
                            onClick = {
                                requestFromParent.value = PageRequest.safExport
                                dropDownMenuExpandState.value = false
                            }
                        )

                        DropdownMenuItem(
                            enabled = enableMenuItem,
                            text = { Text(DevFeature.safDiff_text) },
                            onClick = {
                                requestFromParent.value = PageRequest.safDiff
                                dropDownMenuExpandState.value = false
                            }
                        )
                    }
                }

            }
        }
    }
}

