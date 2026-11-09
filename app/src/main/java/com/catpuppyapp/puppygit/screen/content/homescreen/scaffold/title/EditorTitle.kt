package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Difference
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.ReadOnlyIcon
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SimpleCheckBox
import com.catpuppyapp.puppygit.compose.SmallIcon
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.dev.editorEnableLineSelecteModeFromMenuTestPassed
import com.catpuppyapp.puppygit.dev.editorMergeModeTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClickRequest
import com.catpuppyapp.puppygit.screen.shared.EditorPreviewNavStack
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.settings.util.EditorSettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.onOffText
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorTitle(
    disableSoftKb: MutableState<Boolean>,
    recentFileListIsEmpty: Boolean,
    recentFileListFilterModeOn: Boolean,
    recentListFilterKeyword: CustomStateSaveable<TextFieldValue>,
    getActuallyRecentFilesListState: () -> LazyStaggeredGridState,
    getActuallyRecentFilesListLastPosition: () -> MutableState<Int>,

    patchModeOn: MutableState<Boolean>,
    previewNavStack: EditorPreviewNavStack,
    previewingPath: String,
    isPreviewModeOn: Boolean,
    previewLastScrollPosition: MutableState<Int>,
    scope: CoroutineScope,


    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageRequestFromParent: MutableState<String>,
    editorSearchMode: Boolean,
    editorSearchKeyword: CustomStateSaveable<TextFieldValue>,
    editorPageMergeMode: MutableState<Boolean>,
    readOnly: MutableState<Boolean>,

    editorPageShowingFileIsReady: MutableState<Boolean>,
    isSaving: MutableState<Boolean>,
    isEdited: MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    showCloseDialog: MutableState<Boolean>,
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,

    editorNeedSave: () -> Boolean,
) {
//    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current

//    val inDarkTheme = remember { Theme.inDarkTheme }

    val softKbController = LocalSoftwareKeyboardController.current
    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val closeMenu = { dropDownMenuExpandState.value = false }
    val switchDropDownMenu = {
        softKbController?.hide()
        dropDownMenuExpandState.value = true
    }

    val enableMenuItem = editorPageShowingFilePath.value.isNotBlank()

    // file opened
    if(editorPageShowingFilePath.value.isNotBlank()) {
        val fileName =  remember(isPreviewModeOn, previewingPath, editorPageShowingFilePath.value) {
            FuckSafFile(activityContext, if(isPreviewModeOn) FilePath(previewingPath) else editorPageShowingFilePath.value).name
        }


//        val filePath = getFilePathStrBasedRepoDir(editorPageShowingFilePath.value, returnResultStartsWithSeparator = true)

        val filePathNoFileNameNoEndSlash = FsUtils.getPathWithInternalOrExternalPrefixAndRemoveFileNameAndEndSlash(
            path = if(isPreviewModeOn) previewingPath else editorPageShowingFilePath.value.ioPath,
            fileName
        )

        Column(
            //双击标题回到文件顶部；长按可跳转到指定行；点击显示路径
            modifier = Modifier
                .combinedClickable(
                    //打开文件没出错 或 预览模式则启用，预览模式不管打开出没出错，都尝试显示弹窗，不过如果文件无法打开，
//                    enabled = !editorOpenFileErr || isPreviewModeOn,

                    onDoubleClick = {
                        if (isPreviewModeOn) {
                            runBlocking {
                                defaultTitleDoubleClick(scope, previewNavStack.getCurrentScrollState(), previewLastScrollPosition)
                            }
                        } else {
                            defaultTitleDoubleClickRequest(editorPageRequestFromParent)
                        }
                    },
                    onLongClick = if (isPreviewModeOn.not()) ({
//                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

//                        // go to line
//                            editorPageRequestFromParent.value = PageRequest.goToLine

                        // switch disable/enable software keyboard
                        val newValue = disableSoftKb.value.not()
                        EditorSettingsUtil.updateDisableSoftKb(newValue, disableSoftKb)
                        // because value represent disable when it's true, so, need a `not()` call to know on/off
                        Msg.requireShow(activityContext.getString(R.string.software_keyboard) + ": ${onOffText(newValue.not())}")

                    }) else null

                ) {  //onClick
                    if(isPreviewModeOn) {
                        editorPageRequestFromParent.value = PageRequest.showDetails
                    }else {
                        switchDropDownMenu()
                    }
                }
                .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
        ) {
            if(editorSearchMode) {
                FilterTextField(
                    filterKeyWord = editorSearchKeyword,
                    // avoid mistake clicked
                    showClear = false,
                    containerModifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent opke@{ keyEvent ->

                            // return true to stop key event propaganda
                            if (keyEvent.type != KeyEventType.KeyDown) {
                                return@opke false
                            }


                            // F3
                            if(keyEvent.key == Key.F3 && !keyEvent.isShiftPressed) {
                                editorPageRequestFromParent.value = PageRequest.findNext
                                return@opke true
                            }

                            // Shift+F3
                            if(keyEvent.key == Key.F3 && keyEvent.isShiftPressed) {
                                editorPageRequestFromParent.value = PageRequest.findPrevious
                                return@opke true
                            }


                            return@opke false
                        }
                )
            }else {
                ScrollableRow {
                    if(isPreviewModeOn) {
                        SmallIcon(
                            imageVector = Icons.Filled.RemoveRedEye,
                            contentDescription = stringResource(R.string.preview),
                        )
                    }else {
                        if(editorPageMergeMode.value) {
                            SmallIcon(
                                imageVector = Icons.Filled.Merge,
                                contentDescription = stringResource(R.string.merge_mode),
                            )
                        }

                        if(patchModeOn.value) {
                            SmallIcon(
                                imageVector = Icons.Outlined.Difference,
                                contentDescription = stringResource(R.string.patch_mode),
                            )
                        }

                        if(disableSoftKb.value) {
                            SmallIcon(
                                imageVector = ImageVector.vectorResource(R.drawable.outline_keyboard_off_24),
                                contentDescription = stringResource(R.string.software_keyboard)+": ${onOffText(disableSoftKb.value.not())}",
                            )
                        }

                        if(readOnly.value) {
                            ReadOnlyIcon()
                        }
                    }



                    Text(
                        text =fileName,
                        fontSize = MyStyleKt.Title.firstLineFontSizeSmall,
                        maxLines=1,
                        overflow = TextOverflow.Ellipsis,
//                        color = if(editorPageMergeMode) MyStyleKt.ChangeListItemColor.getConflictColor(inDarkTheme) else Color.Unspecified
                    )
                }
                ScrollableRow  {
                    Text(
                        text = filePathNoFileNameNoEndSlash,
                        fontSize = MyStyleKt.Title.secondLineFontSize,
                        maxLines=1,
                        overflow = TextOverflow.Ellipsis

                    )
                }

            }

        }

        //菜单列表
        DropdownMenu(
            expanded = dropDownMenuExpandState.value,
            onDismissRequest = { closeMenu() }
        ) {

            DropdownMenuItem(
                enabled = enableMenuItem,

                text = { Text(stringResource(R.string.close)) },
                onClick = {
                    showCloseDialog.value=true

                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem,

                text = { Text(stringResource(R.string.reload_file)) },
                onClick = {
                    showReloadDialog.value = true

                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem && editorNeedSave(),

                text = { Text(stringResource(R.string.save)) },
                onClick = {
                    editorPageRequestFromParent.value = PageRequest.requireSave

                    closeMenu()
                }
            )

            if(UserUtil.isPro() && (dev_EnableUnTestedFeature || editorMergeModeTestPassed)){
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.merge_mode)) },
                    trailingIcon = {
                        SimpleCheckBox(editorPageMergeMode.value)
                    },
                    onClick = {
                        editorPageMergeMode.value = !editorPageMergeMode.value

//                        closeMenu()
                    }

                )
            }

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.patch_mode)) },
                trailingIcon = {
                    SimpleCheckBox(patchModeOn.value)
                },
                onClick = {
                    val newValue = !patchModeOn.value
                    patchModeOn.value = newValue

                    //更新配置文件
                    SettingsUtil.update {
                        it.editor.patchModeOn = newValue
                    }

//                    closeMenu()
                }

            )

            DropdownMenuItem(
                //非readOnly目录才允许开启或关闭readonly状态，否则强制启用readonly状态且不允许关闭
//                enabled = enableMenuItem && !FsUtils.isReadOnlyDir(editorPageShowingFilePath.value),
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.read_only)) },
                trailingIcon = {
                    SimpleCheckBox(readOnly.value)
                },
                onClick = {
                    //如果是从非readonly mode切换到readonly mode，则执行一次保存，然后再切换readonly mode
                    editorPageRequestFromParent.value = PageRequest.doSaveIfNeedThenSwitchReadOnly

//                    closeMenu()
                }

            )


            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.software_keyboard)) },
                trailingIcon = {
                    // checked if not disabled
                    SimpleCheckBox(disableSoftKb.value.not())
                },
                onClick = {
//                    closeMenu()

                    EditorSettingsUtil.updateDisableSoftKb(disableSoftKb.value.not(), disableSoftKb)
                }

            )

            if(proFeatureEnabled(editorEnableLineSelecteModeFromMenuTestPassed)) {
                val selectModeOn = editorPageTextEditorState.value.isMultipleSelectionMode

                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.select_mode)) },
                    trailingIcon = {
                        SimpleCheckBox(selectModeOn)
                    },
                    onClick = {
                        closeMenu()

                        editorPageRequestFromParent.value = PageRequest.editorSwitchSelectMode
                    }

                )
            }

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.details)) },
                onClick = {
                    closeMenu()

                    editorPageRequestFromParent.value = PageRequest.showDetails
                }

            )

        }



    }else {
        // file not opened
        if(recentFileListFilterModeOn) {
            FilterTextField(filterKeyWord = recentListFilterKeyword)
        }else {
            ScrollableRow(
                modifier = Modifier
                    .combinedClickable(
                        //打开文件没出错 或 预览模式则启用，预览模式不管打开出没出错，都尝试显示弹窗，不过如果文件无法打开，
                        //enabled = !editorOpenFileErr || isPreviewModeOn,

                        onDoubleClick = {
                            defaultTitleDoubleClick(scope, getActuallyRecentFilesListState(), getActuallyRecentFilesListLastPosition())
                        },
                    ) { }
                    .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
            ) {
                Text(
                    text = stringResource(if(recentFileListIsEmpty) R.string.editor else R.string.recent_files),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
