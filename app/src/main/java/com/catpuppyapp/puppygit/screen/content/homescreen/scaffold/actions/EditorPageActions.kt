package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.compose.FontSizeAdjuster
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.SimpleCheckBox
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.dev.editorEnableLineSelecteModeFromMenuTestPassed
import com.catpuppyapp.puppygit.dev.editorSearchTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dto.UndoStack
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.shared.EditorPreviewNavStack
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.settings.SettingsCons
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import kotlinx.coroutines.runBlocking

@Composable
fun EditorPageActions(
    disableSoftKb: MutableState<Boolean>,

    initPreviewMode:()->Unit,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    isPreviewModeOn:Boolean,
    previewNavStack: EditorPreviewNavStack,
    previewPath: String,
    previewPathChanged: String,

    editorPageShowingFilePath: MutableState<FilePath>,
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
    editorPageSearchMode:MutableState<Boolean>,
    editorPageMergeMode:MutableState<Boolean>,
    editorPagePatchMode:MutableState<Boolean>,
    readOnlyMode:MutableState<Boolean>,
    editorSearchKeyword:String,
    isSubPageMode:Boolean,
    fontSize:MutableIntState,
    lineNumFontSize:MutableIntState,
    adjustFontSizeMode:MutableState<Boolean>,
    adjustLineNumFontSizeMode:MutableState<Boolean>,
    showLineNum:MutableState<Boolean>,
    showUndoRedo:MutableState<Boolean>,
    undoStack:UndoStack,

    editorNeedSave: () -> Boolean,
) {
    /*
        注意：如果以后需要同一个EditorInnerPage配合多个title，就不要在这执行操作了，把这里的action逻辑放到EditorInnerPage执行，在这只发request，类似ChangeList页面请求执行pull/push那样
     */

//    val haptic = LocalHapticFeedback.current
//    val scope = rememberCoroutineScope()

    val hasGoodKeyword = editorSearchKeyword.isNotEmpty()

    //这几个模式互斥，其实可以做成枚举

    if(isPreviewModeOn) {
        val currentIsNotAtHome = remember(previewPathChanged) { derivedStateOf { runBlocking { previewNavStack.currentIsRoot().not() } } }
        val canGoBack = remember(previewPathChanged) { derivedStateOf { runBlocking { previewNavStack.backStackIsNotEmpty() } } }
        val canGoForward = remember(previewPathChanged) { derivedStateOf { runBlocking { previewNavStack.aheadStackIsNotEmpty() } } }

        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.edit),
            icon = Icons.Filled.Edit,
        ) {
            requireEditorScrollToPreviewCurPos.value = true
            editorPageRequest.value = PageRequest.requireEditPreviewingFile
        }
        LongPressAbleIconBtn(
            //若没在首页则启用首页按钮
            enabled = currentIsNotAtHome.value,

            tooltipText = stringResource(R.string.home),
            icon = Icons.Filled.Home,
        ) {
            editorPageRequest.value = PageRequest.requireBackToHome
        }
        LongPressAbleIconBtn(
            enabled = canGoBack.value,
            tooltipText = stringResource(R.string.go_back),
            icon = Icons.AutoMirrored.Filled.ArrowBackIos,
        ) {
            editorPageRequest.value = PageRequest.editorPreviewPageGoBack
        }

        LongPressAbleIconBtn(
            enabled = canGoForward.value,
            tooltipText = stringResource(R.string.go_forward),
            icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
        ) {
            editorPageRequest.value = PageRequest.editorPreviewPageGoForward
        }

        LongPressAbleIconBtn(
            enabled = true,
            tooltipText = stringResource(R.string.refresh),
            icon = Icons.Filled.Refresh,
        ) {
            editorPageRequest.value = PageRequest.editor_RequireRefreshPreviewPage
        }

        return  //返回，以免显示菜单项
    }else if(editorPageSearchMode.value) {
        LongPressAbleIconBtn(
            enabled = hasGoodKeyword,
            tooltipText = stringResource(R.string.find_previous),
            icon = Icons.Filled.ArrowUpward,
            iconContentDesc = stringResource(R.string.find_previous),
        ) {
            editorPageRequest.value = PageRequest.findPrevious
        }

        LongPressAbleIconBtn(
            enabled = hasGoodKeyword,
            tooltipText = stringResource(R.string.find_next),
            icon = Icons.Filled.ArrowDownward,
            iconContentDesc = stringResource(R.string.find_next),

            onLongClick = {
                //震动反馈
//                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                //显示功能提示和所有记数“FindNext(all:40)”，其中40是全文查找到的匹配关键字数量，文案尽量紧凑，避免toast显示不全
                editorPageRequest.value = PageRequest.showFindNextAndAllCount

            }
        ) {
            editorPageRequest.value = PageRequest.findNext
        }

        return  //返回，以免显示菜单项
    }else if(adjustFontSizeMode.value) {
        FontSizeAdjuster(fontSize = fontSize, resetValue = SettingsCons.defaultFontSize)

        return
    }else if(adjustLineNumFontSizeMode.value) {
        FontSizeAdjuster(fontSize = lineNumFontSize, resetValue = SettingsCons.defaultLineNumFontSize)

        return
    }


    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }

    val closeMenu = {dropDownMenuExpandState.value = false}

    val enableMenuItem = editorPageShowingFilePath.value.isNotBlank()

    //是否显示三点菜单图标，以后可能会增加其他判断因素，所以单独弄个变量
    val showMenuIcon = enableMenuItem

    if(enableMenuItem) {
        if(showUndoRedo.value) {
            val enableUndo = remember(undoStack.undoStackIsEmpty()) { undoStack.undoStackIsEmpty().not() }
            val undoStr = stringResource(R.string.undo)
            LongPressAbleIconBtn(
                enabled = enableUndo,
                tooltipText = undoStr,
                icon = Icons.AutoMirrored.Filled.Undo,
                iconContentDesc = undoStr,
                onLongClick = {
                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    // 显示 "Undo(saved steps count)"
                    Msg.requireShow("$undoStr(${undoStack.undoStackSize()})")
                }
            ) {
                editorPageRequest.value = PageRequest.requestUndo
            }

            val enableRedo = remember(undoStack.redoStackIsEmpty()) { undoStack.redoStackIsEmpty().not() }
            val redoStr = stringResource(R.string.redo)
            LongPressAbleIconBtn(
                enabled = enableRedo,
                tooltipText = redoStr,
                icon = Icons.AutoMirrored.Filled.Redo,
                iconContentDesc = redoStr,
                onLongClick = {
                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    // 显示 "Undo(saved steps count)"
                    Msg.requireShow("$redoStr(${undoStack.redoStackSize()})")
                }
            ) {
                editorPageRequest.value = PageRequest.requestRedo
            }
        }

        if(editorPageMergeMode.value) {
            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.previous_conflict),
                icon = Icons.Filled.ArrowUpward,
                iconContentDesc = stringResource(R.string.previous_conflict),
            ) {
                editorPageRequest.value = PageRequest.previousConflict
            }

            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.next_conflict),
                icon = Icons.Filled.ArrowDownward,
                iconContentDesc = stringResource(R.string.next_conflict),

                onLongClick = {
                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    //显示功能提示和所有记数“NextConflict(all:40)”，其中40是全文查找到的所有冲突数量，文案尽量紧凑，避免toast显示不全
                    editorPageRequest.value = PageRequest.showNextConflictAndAllConflictsCount

                }
            ) {
                editorPageRequest.value = PageRequest.nextConflict
            }
        }else {
            // 若非合并模式，显示预览markdown按钮
            // 若是合并模式，可能 撤销重做 加上 合并按钮，顶栏已经有超过4个按钮，
            // 所以就不显示这个预览了，而且合并时一般不会有预览的需求
            LongPressAbleIconBtn(
                tooltipText = stringResource(R.string.preview),
                icon = Icons.Filled.RemoveRedEye,
                iconContentDesc = stringResource(R.string.preview),
            ) {
                initPreviewMode()
            }
        }
    }

    if(showMenuIcon) {
        val softKbController = LocalSoftwareKeyboardController.current

        //菜单图标
        LongPressAbleIconBtn(
            //这种需展开的菜单，禁用内部的选项即可
//        enabled = enableAction.value,

            tooltipText = stringResource(R.string.menu),
            icon = Icons.Filled.MoreVert,
            iconContentDesc = stringResource(R.string.menu),
            onClick = {
                // hide softkb by ourself to fix the menu can't auto expand after softkb auto hidden
                softKbController?.hide()

                //切换菜单展开状态
                // if menu expanded, actually, user cant click the 3dots again,
                //   because the menu will cover the whole screen, so if user can click the 3dots icon, the menu must be hidden,
                //   so set the expand always to ture when clicked 3dots icon is ok
                // 若菜单展开用户无法点开3点按钮，若用户能点菜单必然隐藏，所以这里设为true即可，如果设为切换当前状态，需确保初始值为false，否则逻辑会错
                dropDownMenuExpandState.value = true
            }
        )
    }

    //菜单列表
    Row(modifier = Modifier.padding(top = MyStyleKt.TopBar.dropDownMenuTopPaddingSize)) {
//        val enableMenuItem = enableAction.value && !changeListPageNoRepo.value  && !hasTmpStatus

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
                    editorPageRequest.value = PageRequest.requireSave

                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem && editorPageShowingFileIsReady.value,

                text = { Text(stringResource(R.string.preview)) },
                onClick = {
                    initPreviewMode()

                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem,

                text = { Text(stringResource(R.string.open_as)) },
                onClick = {
                    editorPageRequest.value = PageRequest.requireOpenAs

                    closeMenu()
                }
            )



            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.go_to_line)) },
                onClick = {
                    editorPageRequest.value = PageRequest.goToLine  //发请求，由TextEditor组件开启搜索模式
                    closeMenu()
                }
            )

            if(UserUtil.isPro() && (dev_EnableUnTestedFeature || editorSearchTestPassed)){
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.find)) },
                    onClick = {
                        editorPageRequest.value = PageRequest.requireSearch  //发请求，由TextEditor组件开启搜索模式
                        closeMenu()
                    }
                )
            }

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.syntax_highlighting)) },
                onClick = {
                    editorPageRequest.value = PageRequest.selectSyntaxHighlighting
                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.line_break)) },
                onClick = {
                    editorPageRequest.value = PageRequest.showLineBreakDialog
                    closeMenu()
                }
            )


            DropdownMenuItem(
                enabled = enableMenuItem,

                text = { Text(stringResource(R.string.encoding)) },
                onClick = {
                    editorPageRequest.value = PageRequest.showSelectEncodingDialog

                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem,

                text = { Text(stringResource(R.string.convert_encoding)) },
                onClick = {
                    editorPageRequest.value = PageRequest.convertEncoding

                    closeMenu()
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem,

                text = { Text(stringResource(R.string.file_history)) },
                onClick = {
                    editorPageRequest.value = PageRequest.requireGoToFileHistory

                    closeMenu()
                }
            )


            if(!isSubPageMode) {
                DropdownMenuItem(
                    enabled = enableMenuItem,
                    text = { Text(stringResource(R.string.show_in_files)) },
                    onClick = {
                        editorPageRequest.value = PageRequest.showInFiles

                        closeMenu()
                    }

                )

            }

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.show_undo_redo)) },
                trailingIcon = {
                    SimpleCheckBox(showUndoRedo.value)
                },
                onClick = {
                    val newValue = !showUndoRedo.value

                    // 更新页面变量
                    showUndoRedo.value = newValue

                    //更新配置文件
                    SettingsUtil.update {
                        it.editor.showUndoRedo = newValue
                    }

                    //关闭菜单
//                    closeMenu()
                }

            )


            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.show_line_num)) },
                trailingIcon = {
                    SimpleCheckBox(showLineNum.value)
                },
                onClick = {
//                    closeMenu()

                    //切换
                    val newValue = !showLineNum.value
                    showLineNum.value = newValue

                    //保存
                    SettingsUtil.update {
                        it.editor.showLineNum = newValue
                    }
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem && showLineNum.value,
                text = { Text(stringResource(R.string.line_num_size)) },
                onClick = {
                    closeMenu()

                    adjustLineNumFontSizeMode.value = true
                }

            )

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.font_size)) },
                onClick = {
                    closeMenu()

                    adjustFontSizeMode.value = true
                }
            )

            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.tab_size)) },
                onClick = {
                    closeMenu()

                    editorPageRequest.value = PageRequest.showSetTabSizeDialog
                }
            )


            val autoCloseSymbolPair = remember { mutableStateOf(SettingsUtil.isEditorAutoCloseSymbolPairEnabled()) }
            DropdownMenuItem(
                enabled = enableMenuItem,
                text = { Text(stringResource(R.string.symbol_pair)) },
                trailingIcon = {
                    SimpleCheckBox(autoCloseSymbolPair.value)
                },
                onClick = {
//                    closeMenu()

                    //切换
                    val newValue = !autoCloseSymbolPair.value
                    autoCloseSymbolPair.value = newValue

                    //保存
                    SettingsUtil.update {
                        it.editor.autoCloseSymbolPair = newValue
                    }
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

                        editorPageRequest.value = PageRequest.editorSwitchSelectMode
                    }

                )
            }


        }
    }
}
