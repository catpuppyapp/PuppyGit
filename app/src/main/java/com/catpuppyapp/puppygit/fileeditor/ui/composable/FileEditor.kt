package com.catpuppyapp.puppygit.fileeditor.ui.composable

import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PanToolAlt
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.BottomBar
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MarkDownContainer
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.SelectedItemDialog3
import com.catpuppyapp.puppygit.compose.SwipeIcon
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dto.UndoStack
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.MyTextFieldState
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.ScrollEvent
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.TextEditor
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.lineNumOffsetForGoToEditor
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.getClipboardText
import com.catpuppyapp.puppygit.screen.shared.EditorPreviewNavStack
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.settings.FileEditedPos
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLFont
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLTheme
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.addTopPaddingIfIsFirstLine
import com.catpuppyapp.puppygit.utils.appendCutSuffix
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.paddingLineNumber
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

private const val TAG = "FileEditor"

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun FileEditor(
    stateKeyTag:String,

    plScope: State<PLScope>,

    disableSoftKb: MutableState<Boolean>,

    updateLastCursorAtColumn:(Int)->Unit,
    getLastCursorAtColumnValue:()->Int,

    ignoreFocusOnce: MutableState<Boolean>,
//    softKbVisibleWhenLeavingEditor: MutableState<Boolean>,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    requirePreviewScrollToEditorCurPos:MutableState<Boolean>,
    isSubPageMode:Boolean,
    previewNavBack:()->Unit,
    previewNavAhead:()->Unit,
    previewNavStack:CustomStateSaveable<EditorPreviewNavStack>,
    refreshPreviewPage:()->Unit,

    previewLoading:Boolean,
    mdText:MutableState<String>,
    basePath:MutableState<String>,
    previewLinkHandler:(link:String)->Boolean,

    isPreviewModeOn:MutableState<Boolean>,
    quitPreviewMode:()->Unit,
    initPreviewMode:()->Unit,

    openDrawer:()->Unit,
    requestFromParent:MutableState<String>,
    fileFullPath:FilePath,
    lastEditedPos:FileEditedPos,
    textEditorState:CustomStateSaveable<TextEditorState>,
    contentPadding:PaddingValues,
    isContentEdited:MutableState<Boolean>,
    editorLastScrollEvent:CustomStateSaveable<ScrollEvent?>,
    editorListState: LazyListState,
    editorPageIsInitDone:MutableState<Boolean>,
    editorPageIsContentSnapshoted:MutableState<Boolean>,
    goToLine:Int,
    readOnlyMode:Boolean,
    searchMode:MutableState<Boolean>,
    searchKeyword:String,
    mergeMode:Boolean,
    patchMode:Boolean,
    showLineNum:MutableState<Boolean>,
    lineNumFontSize:MutableIntState,
    fontSize:MutableIntState,
    undoStack: UndoStack,
    tabIndentSpacesCount: State<Int>,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val deviceConfiguration = AppModel.getCurActivityConfig()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    val scope = rememberCoroutineScope()

    val inDarkTheme = Theme.inDarkTheme

    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val firstLineTopPaddingInPx = with(density) { -(MyStyleKt.Padding.firstLineTopPaddingValuesInDp.toPx()) }


    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
//    val editableController by rememberTextEditorController(textEditorState.value, onChanged = { onChanged(it) }, isContentChanged, editorPageIsContentSnapshoted)

//    val bottomPadding = if (textEditorState.value.isMultipleSelectionMode) 100.dp else 0.dp  //如果使用这个padding，开启选择模式时底栏会有背景，否则没有，没有的时候就像直接浮在编辑器上
//    val bottomPadding = 0.dp

//    val contentBottomPaddingValue = with(LocalDensity.current) { WindowInsets.ime.getBottom(this).toDp() }  //获取键盘高度？用来做padding以在显示键盘时能看到最后一行内容？
//    val contentPaddingValues = PaddingValues(
//        start=contentPadding.calculateStartPadding(LayoutDirection.Ltr),
//        top=contentPadding.calculateTopPadding(),
//        end=contentPadding.calculateEndPadding(LayoutDirection.Rtl),
//        bottom = contentPadding.calculateBottomPadding()+contentBottomPaddingValue+300.dp  // make bottom higher for better view
////        bottom = contentPadding.calculateBottomPadding()+contentBottomPaddingValue  // make bottom higher for better view
//    )

//    val contentPaddingValues = PaddingValues(bottom = contentBottomPaddingValue)
    val contentPaddingValues = contentPadding

    val enableSelectMode = { index:Int ->
        //隐藏键盘
        keyboardController?.hide()

        // 非行选择模式，启动行选择模式 (multiple selection mode on)
        //注：索引传-1或其他无效索引即可在不选中任何行的情况下启动选择模式，从顶栏菜单开启选择模式默认不选中任何行，所以这里传-1
        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#createMultipleSelectionModeState") { textEditorState ->
            textEditorState.createMultipleSelectionModeState(index)
        }
    }

    //切换行选择模式
    if(requestFromParent.value == PageRequest.editorSwitchSelectMode) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            //如果已经是选择模式，退出；否则开启选择模式
            if(textEditorState.value.isMultipleSelectionMode) {  //退出选择模式
                textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#quitSelectionMode") { textEditorState ->

                    textEditorState.quitSelectionMode()
                }
            }else {  //开启选择模式
                enableSelectMode(-1)
            }

        }
    }

    val deleteSelectedLines = {
        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteSelectedLines") { textEditorState ->
            textEditorState.deleteSelectedLines()
        }
    }

    if(showDeleteDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete_lines),
            text = replaceStringResList(stringResource(R.string.will_delete_n_lines_ask), listOf(textEditorState.value.getSelectedCount().toString())) ,
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDeleteDialog.value=false }
        ) {
            //关弹窗
            showDeleteDialog.value=false
            //删除行
            deleteSelectedLines()
            //显示通知
            Msg.requireShow(activityContext.getString(R.string.deleted))
        }
    }



//    val isRtl = UIHelper.isRtlLayout()

    //处理返回和前进
    val onLeftToRight = {
        if(isPreviewModeOn.value) {
            previewNavBack()
        } else if(isSubPageMode.not()) {
            openDrawer()
        }
    }
    val onRightToLeft = {
        if(isPreviewModeOn.value) {
            previewNavAhead()
        } else {
            initPreviewMode()

            //避免切到预览再切回来后自动弹键盘 (后来设置了忽略聚焦一次，不需要用这个了)
//            textEditorState.value = textEditorState.value.copy(focusingLineIdx = null)
        }
    }

//    val dragHandleInterval = remember { 500L } //ms
//    val curTime = rememberSaveable { mutableLongStateOf(0) }



    //只需要contentPadding的上和下，不然横屏会因为底部导航栏而有偏差
    val swipeIconModifier = Modifier.padding(top = contentPadding.calculateTopPadding(), bottom = contentPadding.calculateBottomPadding()).padding(horizontal = 10.dp);

    //左边操作总是启用，但需要确定是否显示动画
    val leftToRightAct = SwipeAction(
        icon = {
            //只有满足条件才显示图标，否则若图标根据条件变化，滑动图标松手后会短暂显示变化的图标，不好
            if(isPreviewModeOn.value) {
                SwipeIcon(
                    modifier = swipeIconModifier,
                    imageVector = runBlocking {
                        if (previewNavStack.value.backStackIsEmpty()) Icons.Filled.Edit else Icons.AutoMirrored.Filled.ArrowBackIos
                    },
                    contentDescription = null
                )
            }

        },
        background = Color.Unspecified,
        //编辑模式由于有抽屉菜单，所以这里禁用动画
        enableAnimation = isPreviewModeOn.value,
        enableAct = isSubPageMode.not() || isPreviewModeOn.value,
        enableVibration = isPreviewModeOn.value,  //在编辑器滑动出现抽屉菜单时不震动
        onSwipe = { onLeftToRight() }
    )

    val supportSwipeToEnablePreview = isPreviewModeOn.value.not() && PLScope.isSupportPreview(plScope.value)
    val enableRightToLeftAct = supportSwipeToEnablePreview || (isPreviewModeOn.value && runBlocking { previewNavStack.value.aheadStackIsNotEmpty() })

    //右边操作不总是启用，但动画总是启用
    val rightToLeftAct = SwipeAction(
        icon = {
            if(enableRightToLeftAct) {
                SwipeIcon(
                    modifier = swipeIconModifier,
                    imageVector = if(isPreviewModeOn.value) Icons.AutoMirrored.Filled.ArrowForwardIos else Icons.Filled.RemoveRedEye,
                    contentDescription = null
                )
            }
        },
        background = Color.Unspecified,
        enableAnimation = supportSwipeToEnablePreview || isPreviewModeOn.value,
        enableAct = enableRightToLeftAct,
        onSwipe = { onRightToLeft() },
    )


    val fontColor = remember(inDarkTheme) { UIHelper.getFontColor(inDarkTheme) }

    SwipeableActionsBox(
        startActions = listOf(leftToRightAct),
        endActions = listOf(rightToLeftAct),
    ) {
        val curPreviewScrollState = runBlocking { previewNavStack.value.getCurrentScrollState() }




        val scrollIfIndexInvisible = { index:Int ->
            try {
                val first = editorListState.firstVisibleItemIndex
                val end = editorListState.layoutInfo.visibleItemsInfo.maxByOrNull { it.index }?.index
                //如果指定行不在可见范围内，滚动到指定行以使其可见
                if (end != null && (index < first || index > end)) {
                    //滚动到指定行
                    UIHelper.scrollToItem(scope, editorListState, index + lineNumOffsetForGoToEditor)
                }
            }catch (e: Exception) {
                MyLog.d(TAG, "#scrollIfIndexInvisible err: ${e.stackTraceToString()}")
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if(isPreviewModeOn.value) {
                        Modifier
                    }else {
                        Modifier
                            .background(PLTheme.getBackground(inDarkTheme))
                            // listen keyboard pressed for TextEditor
                            // redo and undo shortcuts supports as default by BasicTextField,
                            //  so here must use `onPreviewKeyEvent` rather than `onKeyEvent` to intercept the key events
                            // see: https://developer.android.com/develop/ui/compose/touch-input/keyboard-input/commands
                            .onPreviewKeyEvent opke@{ keyEvent ->

                                // return true to stop key event propaganda
                                if (keyEvent.type != KeyEventType.KeyDown) {
                                    return@opke false
                                }

                                val textEditorState = textEditorState.value
                                val lastScrollEvent = editorLastScrollEvent

                                // below keyboard events was handle in `MyTextField`, now moved to here
                                if(readOnlyMode.not() && textEditorState.isMultipleSelectionMode.not()) {

                                    val (focusedLineIndex, textFieldState) = textEditorState.getCurrentField()

                                    if(focusedLineIndex != null && textFieldState != null) {
                                        val textFieldValue = textFieldState.value
                                        val selection = textFieldValue.selection
                                        val index = focusedLineIndex
                                        val event = keyEvent

                                        // backspace
                                        if (backspacePressed(event, selection) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#onBackspacePressed") { textEditorState ->
                                                    try {
                                                        textEditorState.deleteNewLine(targetIndex = index)
                                                        scrollIfIndexInvisible(index - 1)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onDeleteNewLine err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onDeleteNewLine err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }

                                        // Delete key
                                        if (forwardDeletePressed(event, selection, textFieldValue) {
                                                // delete current line's end equals to delete next line's start, so plus 1
                                                val index = index + 1
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#onForwardDeletePressed") { textEditorState ->
                                                    try {
                                                        textEditorState.deleteNewLine(targetIndex = index)
                                                        scrollIfIndexInvisible(index - 1)
                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("forward delete err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onDeleteNewLine err: forward delete err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }

                                        if (goUpKeyPressed(event) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectPrevOrNextField") { textEditorState ->
                                                    try {
                                                        textEditorState.selectPrevOrNextField(
                                                            isNext = false,
                                                            updateLastCursorAtColumn,
                                                            getLastCursorAtColumnValue,
                                                        )

                                                        scrollIfIndexInvisible(index - 1)

                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onUpFocus err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onUpFocus err: "+e.stackTraceToString())
                                                    }
                                                }
                                            }) {
                                            return@opke true
                                        }


                                        if (goDownKeyPressed(event) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectPrevOrNextField") { textEditorState ->
                                                try {
                                                        textEditorState.selectPrevOrNextField(
                                                            isNext = true,
                                                            updateLastCursorAtColumn,
                                                            getLastCursorAtColumnValue,
                                                        )

                                                        scrollIfIndexInvisible(index + 1)

                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onDownFocus err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onDownFocus err: "+e.stackTraceToString())
                                                    }
                                                }
                                            }) {
                                            return@opke true
                                        }


                                        if (goLeftPressed(event, textFieldState) { lineSwitched ->
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->

                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = true,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = false,
                                                        )

                                                        scrollIfIndexInvisible(if(lineSwitched) index - 1 else index)

                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onLeftPressed err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onLeftPressed err: "+e.stackTraceToString())
                                                    }
                                                }

                                        }) {
                                            return@opke true
                                        }

                                        if (goRightPressed(event, textFieldState) { lineSwitched ->
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->

                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = false,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = false,
                                                        )

                                                        scrollIfIndexInvisible(if(lineSwitched) index + 1 else index)


                                                    } catch (e: Exception) {
                                                        Msg.requireShowLongDuration("#onRightPressed err: " + e.localizedMessage)
                                                        MyLog.e(TAG, "#onRightPressed err: " + e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }

                                        if (goHomePressed(event, textFieldState) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->

                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = true,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = true,
                                                        )

                                                        scrollIfIndexInvisible(index)

                                                    }catch (e:Exception) {
                                                        Msg.requireShowLongDuration("#onLeftPressed err: "+e.localizedMessage)
                                                        MyLog.e(TAG, "#onLeftPressed err: "+e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }

                                        if (goEndPressed(event, textFieldState) {
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#moveCursor") { textEditorState ->

                                                    try {
                                                        textEditorState.moveCursor(
                                                            trueToLeftFalseRight = false,
                                                            textFieldState = textFieldState,
                                                            targetFieldIndex = index,
                                                            headOrTail = true,
                                                        )

                                                        scrollIfIndexInvisible(index)

                                                    } catch (e: Exception) {
                                                        Msg.requireShowLongDuration("#onRightPressed err: " + e.localizedMessage)
                                                        MyLog.e(TAG, "#onRightPressed err: " + e.stackTraceToString())
                                                    }
                                                }
                                        }) {
                                            return@opke true
                                        }

                                    }

                                }



                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.S) { // save
                                    requestFromParent.value = PageRequest.requireSave
                                    return@opke true
                                }

                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.W) { // close
                                    requestFromParent.value = PageRequest.requireClose
                                    return@opke true
                                }

                                if ((keyEvent.isCtrlPressed && keyEvent.key == Key.Y)
                                    || (keyEvent.isCtrlPressed && keyEvent.isShiftPressed && keyEvent.key == Key.Z)
                                ) { // redo
                                    requestFromParent.value = PageRequest.requestRedo
                                    return@opke true
                                }

                                if (keyEvent.isShiftPressed.not() && keyEvent.isCtrlPressed && keyEvent.key == Key.Z) { // undo
                                    requestFromParent.value = PageRequest.requestUndo
                                    return@opke true
                                }

                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.F) { // search
                                    requestFromParent.value = PageRequest.requireSearch  //发请求，由TextEditor组件开启搜索模式
                                    return@opke true
                                }

                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.G) {
                                    requestFromParent.value = PageRequest.goToLine
                                    return@opke true
                                }

                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.MoveHome) { // go to top of file
                                    lastScrollEvent.value = ScrollEvent(0)

                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#goToEndOrTopOfFile") { textEditorState ->
                                        textEditorState.goToEndOrTopOfFile(goToTop = true)
                                    }
                                    return@opke true
                                }

                                if (keyEvent.isCtrlPressed && keyEvent.key == Key.MoveEnd) { // go to end of file
                                    lastScrollEvent.value = ScrollEvent(textEditorState.fields.lastIndex.coerceAtLeast(0))

                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#goToEndOrTopOfFile") { textEditorState ->

                                        textEditorState.goToEndOrTopOfFile(goToTop = false)
                                    }
                                    return@opke true
                                }

                                val isCtrlAndC = keyEvent.isCtrlPressed && keyEvent.key == Key.C
                                val isCtrlAndX = keyEvent.isCtrlPressed && keyEvent.key == Key.X
                                if(isCtrlAndC || isCtrlAndX) {
                                    if(textEditorState.isMultipleSelectionMode) {
                                        clipboardManager.setText(AnnotatedString(textEditorState.getSelectedText()))
                                        Msg.requireShow(
                                            replaceStringResList(
                                                activityContext.getString(R.string.n_lines_copied),
                                                listOf(textEditorState.getSelectedCount().toString())
                                            ).let { if(isCtrlAndX) it.appendCutSuffix() else it }
                                        )

                                        if(isCtrlAndX) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteSelectedLines") { textEditorState ->
                                                textEditorState.deleteSelectedLines()
                                            }
                                        }

                                        return@opke true
                                    }else {
                                        val (currentIndex, currentField) = textEditorState.getCurrentField()
                                        // if not collapsed, will handle by text filed default
                                        if(currentIndex != null && currentField != null && currentField.value.selection.collapsed) {
                                            clipboardManager.setText(AnnotatedString(currentField.value.text))

                                            // if press ctrl + x continuously or just simple keep pressed, the toast msg will be a terrible noise, so better disable it
//                                            Msg.requireShow(activityContext.getString(R.string.copied).let { if(isCtrlAndX) it.appendCutSuffix() else it })

//                                            if(ctrlAndC){
//                                                // full select the line (deprecated by a bug: text will selected actually,
//                                                //   but doesn't show the selected background color, so users maybe will not
//                                                //   know text is selected, and the text of this line will fully replaced if
//                                                //   users typed anything, this behavior will make users confuse)
//                                                val fullSelectedField = currentField.let {
//                                                    it.value.let { it.copy(selection = TextRange(0, it.text.length)) }
//                                                }
//                                                doJobThenOffLoading {
//                                                    textEditorState.updateField(
//                                                        currentIndex,
//                                                        fullSelectedField
//                                                    )
//                                                }
//                                            }

                                            if(isCtrlAndX) {
                                                // delete the line
                                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteLineByIndices") { textEditorState ->
                                                    textEditorState.deleteLineByIndices(listOf(currentIndex))
                                                }
                                            }

                                            return@opke true
                                        }
                                    }
                                }

                                if(textEditorState.isMultipleSelectionMode && keyEvent.isCtrlPressed && keyEvent.key == Key.A) {
                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#createSelectAllState") { textEditorState ->

                                        textEditorState.createSelectAllState()
                                    }

                                    return@opke true
                                }


                                // multi selection and ctrl+v pressed
                                if(textEditorState.isMultipleSelectionMode && textEditorState.selectedIndices.isNotEmpty() && keyEvent.isCtrlPressed && keyEvent.key == Key.V) {
                                    val clipboardText = getClipboardText(clipboardManager)
                                    if(clipboardText == null) {
                                        Msg.requireShowLongDuration(activityContext.getString(R.string.clipboard_is_empty))
                                    }else {
                                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#appendTextToLastSelectedLine") { textEditorState ->
                                            textEditorState.appendTextToLastSelectedLine(clipboardText)
                                        }
                                    }

                                    return@opke true
                                }

                                // shift + tab
                                if(keyEvent.key == Key.Tab && keyEvent.isShiftPressed) {
                                    if(textEditorState.isMultipleSelectionMode) {
                                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->

                                            textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = false) }
                                        }

                                        return@opke true
                                    }else {
                                        val (idx, f) = textEditorState.getCurrentField()
                                        if(idx != null && f != null) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#handleTabIndent") { textEditorState ->

                                                textEditorState.handleTabIndent(idx, f, tabIndentSpacesCount.value, trueTabFalseShiftTab = false)
                                            }

                                            return@opke true
                                        }

                                    }
                                }


                                // tab
                                if(keyEvent.key == Key.Tab && !keyEvent.isShiftPressed) {
                                    if(textEditorState.isMultipleSelectionMode) {
                                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->

                                            textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = true) }
                                        }

                                        return@opke true
                                    }else {
                                        val (idx, f) = textEditorState.getCurrentField()
                                        if(idx != null && f != null) {
                                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#handleTabIndent") { textEditorState ->

                                                textEditorState.handleTabIndent(idx, f, tabIndentSpacesCount.value, trueTabFalseShiftTab = true)
                                            }

                                            return@opke true
                                        }
                                    }
                                }

                                // F3
                                if(keyEvent.key == Key.F3 && !keyEvent.isShiftPressed) {
                                    requestFromParent.value = if(searchMode.value) {
                                        PageRequest.findNext
                                    }else {
                                        PageRequest.requireSearch
                                    }

                                    return@opke true
                                }

                                // Shift+F3
                                if(keyEvent.key == Key.F3 && keyEvent.isShiftPressed) {
                                    requestFromParent.value = PageRequest.findPrevious

                                    return@opke true
                                }


                                return@opke false
                            }
                    }
                )
        ) {
            if(isPreviewModeOn.value) {
                PullToRefreshBox(
                    contentPadding = contentPadding,
//                    isRefreshing = previewLoading, //有bug，得传state，不然加载后这图标会留在原地，但要改的话里面代码得改，其他很多地方也都得改，我懒得改，索性直接禁用，用其他东西loading
                    onRefresh = { refreshPreviewPage() }
                ) {
                    Column(
                        modifier = Modifier
                            .baseVerticalScrollablePageModifier(contentPadding, curPreviewScrollState)
                        ,
                    ) {
                        //内容顶部padding，为了和editor的首行top padding高度保持一致，所以调用了相同的函数，
                        // 其中0代表索引0，索引0代表firstLine，firstLine则会加top padding
                        // 预览md的内容都是一整块，没有索引，所以这个顶部padding是固定添加的
                        Spacer(Modifier.addTopPaddingIfIsFirstLine(0))

                        MarkDownContainer(
                            content = mdText.value,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            basePathNoEndSlash = basePath.value,
                            style = LocalTextStyle.current.copy(fontSize = fontSize.intValue.sp, color = fontColor, fontFamily = PLFont.editorCodeFont()),
                            onLinkClicked = { link ->
                                previewLinkHandler(link)
                            }
                        )

                        Spacer(Modifier.height(30.dp))
                    }

                }

                LaunchedEffect(Unit) {
                    try {
                        if(requirePreviewScrollToEditorCurPos.value) {
                            //一次性变量，立刻重置
                            requirePreviewScrollToEditorCurPos.value = false

                            val fontSizeInPx = UIHelper.spToPx(sp = fontSize.intValue, density = density)
                            val screenWidthInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenWidthDp, density = density)
                            val screenHeightInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenHeightDp, density = density)
                            val editorCurLineIndex = editorListState.firstVisibleItemIndex

                            //计算目标滚动位置
                            val targetPos = textEditorState.value.lineIdxToPx(lineIndex=editorCurLineIndex, fontSizeInPx=fontSizeInPx, screenWidthInPx=screenWidthInPx, screenHeightInPx=screenHeightInPx)

                            //滚动
                            UIHelper.scrollTo(scope, curPreviewScrollState, targetPos.toInt())
                        }
                    }catch (e:Exception) {
                        //并非很严重的错误，debug级别吧
                        MyLog.d(TAG, "let preview scroll to current editor position failed: ${e.stackTraceToString()}")
                    }
                }
            } else {

                // 20250618: these code not work after update jetpack compose, no way to prevent the software keyboard popup for now

                // shit code start
                //update soft keyboard visible state
                val kbVisible = rememberUpdatedState(UIHelper.isSoftkeyboardVisible())

                // old version compose will call disposable effect and ON_PAUSE lifecycle together, but now changed behavior?
                DisposableEffect(Unit) {
                    onDispose {
                        if(AppModel.devModeOn) {
                            MyLog.d(TAG, "FileEditor#DisposableEffect#onDispose: called, imeVisible=${kbVisible.value}")
                        }

                        val kbHidden = !kbVisible.value
                        ignoreFocusOnce.value = kbHidden

                        // if software keyboard disabled, no need lose focus,
                        //   else will try lose focus for avoid unexpected software keyboard popup
                        if(kbHidden && !disableSoftKb.value) {
                            textEditorState.value = textEditorState.value.copy(focusingLineIdx = null)
                        }

                        if(AppModel.devModeOn) {
                            MyLog.d(TAG, "FileEditor#DisposableEffect#onDispose: called, ignoreFocusOnce=${ignoreFocusOnce.value}")
                        }
                    }
                }

//                LaunchedEffect(Unit) {
//                    if(isOnPause.value.not()) {
//                        if(ignoreFocusOnce.value) {
//                            ignoreFocusOnce.value = false
//                            requestFromParent.value = PageRequest.hideKeyboardForAWhile
//                        }
//                    }
//                }

                // 20250802 disabled: not work after compose libs upgraded
//                LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
//                    isOnPause.value = true
//                    MyLog.d(TAG, "FileEditor#LifecycleEventEffect#ON_PAUSE: called, imeVisible=${SharedState.editor_softKeyboardIsVisible.value}")
//                    // 如果离开页面时，软键盘状态是隐藏，则切换回来后不弹出键盘
//                    SharedState.editor_softKeyboardIsVisible.value.let {
//                        softKbVisibleWhenLeavingEditor.value = it
//                        ignoreFocusOnce.value = it.not()  // if invisible, then ignore once popup soft keyboard
//                    }
//
//                    createIgnoreFocusTextEditorStateIfNeed()
//
//                    MyLog.d(TAG, "FileEditor#LifecycleEventEffect#ON_PAUSE: called, ignoreFocusOnce=${ignoreFocusOnce.value}")
//
//                }

//                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
//                    if(isOnPause.value) {
//                        ignoreFocusOnce.value = softKbVisibleWhenLeavingEditor.value.not()
//                        // if invisible, then ignore once popup soft keyboard
////                        if(ignoreFocusOnce.value) {
////                            requestFromParent.value = PageRequest.hideKeyboardForAWhile
////                        }
//                    }
//
//                    MyLog.d(TAG, "FileEditor#LifecycleEventEffect#ON_RESUME: called, softKbVisibleWhenLeavingEditor.value.not()=${softKbVisibleWhenLeavingEditor.value.not()}")
//
//                }
                // shit code end





                // Show Text Editor
                val showLineNum = showLineNum.value
//                val bottomLineWidth = remember { 1.dp }
                val changeTypeWidth = remember(showLineNum) { if(showLineNum) 5.dp else 10.dp }

                TextEditor(
                    stateKeyTag = stateKeyTag,

                    disableSoftKb = disableSoftKb,
                    updateLastCursorAtColumn = updateLastCursorAtColumn,
                    getLastCursorAtColumnValue = getLastCursorAtColumnValue,

                    ignoreFocusOnce = ignoreFocusOnce,
                    undoStack = undoStack,
                    curPreviewScrollState = curPreviewScrollState,
                    requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
                    requestFromParent = requestFromParent,
                    fileFullPath = fileFullPath,
                    lastEditedPos = lastEditedPos,
                    textEditorState = textEditorState.value,
                    contentPaddingValues = contentPaddingValues,
                    lastScrollEvent =editorLastScrollEvent,
                    listState =editorListState,
                    editorPageIsInitDone = editorPageIsInitDone,
                    goToLine=goToLine,
                    readOnlyMode=readOnlyMode,
                    searchMode = searchMode,
                    searchKeyword =searchKeyword,
                    mergeMode=mergeMode,
                    patchMode=patchMode,
                    fontSize=fontSize,
                    fontColor = fontColor,
                    scrollIfIndexInvisible = scrollIfIndexInvisible,
                ) { index, size, isSelected, currentField, focusingIdx, isMultiSelectionMode, innerTextField ->
                    //用来让行号背景填充整行高度
//                    val lineHeight = remember { mutableStateOf(0.dp) }

                    // TextLine
                    Row(
                        modifier = Modifier
//                            .onGloballyPositioned { lineHeight.value = UIHelper.pxToDp(it.size.height, density) }
                            //如果不加这个东西，下面行号那列fillMaxHeight() not work，原因不明，说实话我不知道这玩意是干嘛的
                            // IntrinsicSize.Max 这个主意来自：https://stackoverflow.com/a/76362040
                            .height(IntrinsicSize.Max)

                            .fillMaxWidth()
                            .background(
                                getBackgroundColor(
                                    isSelected = isSelected,
                                    isMultiSelectionMode = isMultiSelectionMode,
                                    currentIdx = index,
                                    focusingIdx = focusingIdx,
                                    inDarkTheme = inDarkTheme,

                                )
                            )
                            .padding(end = 5.dp)

                    ) {

                        // 行号
                        Row(
                            modifier = Modifier
                                .background(MyStyleKt.TextColor.lineNumBgColor(inDarkTheme))
                                //让行号占满整行高度
//                                .height(lineHeight.value)
                                .fillMaxHeight()
                                .combinedClickable(
                                    onLongClick = {
                                        if (textEditorState.value.isMultipleSelectionMode) {
//                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectFieldSpan") { textEditorState ->
                                                textEditorState.selectFieldSpan(targetIndex = index)
                                            }
                                        }
                                    }
                                ) {
                                    //如果是行选择模式，选中当前点击的行如果不是行选择模式；进入行选择模式
                                    if (textEditorState.value.isMultipleSelectionMode) {
                                        //选中/取消选中 当前点击的行
                                        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                            textEditorState.selectField(targetIndex = index)
                                        }

                                    } else { // 非行选择模式，启动行选择模式 (multiple selection mode on)
                                        enableSelectMode(index)
                                    }
                                }
                                .addTopPaddingIfIsFirstLine(index)
                            ,

                            //让行号从右向左对齐，如果对短行号加了空格padding并且字体等宽其实这个可选
                            horizontalArrangement = Arrangement.End,
                        ) {

                            //行号
                            if(showLineNum) {
                                val expectLength = size.toString().length

                                Text(
                                    text = paddingLineNumber((index+1).toString(), expectLength),
                                    color = MyStyleKt.TextColor.lineNumColor(inDarkTheme, textEditorState.value.focusingLineIdx == index),
                                    fontSize = lineNumFontSize.intValue.sp,
                                    fontFamily = PLFont.codeFont,  //等宽字体，和diff页面的行号保持一致

                                    //右边加点padding给修改类型指示器，左边加点padding给和屏幕边缘拉开距离
                                    modifier = Modifier.padding(start = 5.dp, end = changeTypeWidth)
                                )

                            }


                            //行修改类型指示器
                            Row(modifier = Modifier
//                                .width(if(showLineNum) changeTypeWidth-3.dp else 10.dp)
                                .width(changeTypeWidth)
                                //整行高度显示
                                .fillMaxHeight()
                                //画修改类型指示条
                                .changeTypeIndicator(
                                    changeTypeLineWidth = changeTypeWidth,
                                    changeTypeColor = currentField.getColorOfChangeType(inDarkTheme),
                                    // 如果是第一行，需要加上顶部padding值，不然会有一段空白，不好看
                                    yStartOffsetInPx = if(index == 0) firstLineTopPaddingInPx else 0f
                                )
                            ){
                                //这个东西原本应该是用来增加行号和修改类型指示器间距的，但现在好像没用了
//                                Spacer(Modifier.width(1.dp))
                            }

                        }



                        // TextField
                        innerTextField(
                            Modifier
                                .weight(0.9f, true) //这东西有什么用？
                                .align(Alignment.CenterVertically)

                                //给第一行top加点padding，不然离上面太近，难受
                                .addTopPaddingIfIsFirstLine(index)
                                //底线
//                                .bottomLine(
//                                    bottomLineWidth = bottomLineWidth,
//                                    color = UIHelper.getDividerColor(),
//                                )
                        )


                    }
                }


                // Multiple Selection Menu
                if (textEditorState.value.isMultipleSelectionMode) {
                    //涉及到状态变量的函数一律不要用remember，
                    // 不然状态变量改变后函数不会重新生成，
                    // 例如删除按钮的enable函数依赖只读，
                    // 期望打开关闭只读影响其启用状态，若remember，
                    // 则开关将无影响，否则有影响，
                    // 当然也可把所有相关的状态放到rememberd的括号里，
                    // 例如 remember(相关状态1，状态2，等) {你的函数}，
                    // 但这样写太麻烦了

                    // BottomBar params block start
                    val quitSelectionMode = {
                        textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#quitSelectionMode") { textEditorState ->

                            textEditorState.quitSelectionMode()
                        }

                        Unit
                    }
                    val iconList = listOf(
                        Icons.AutoMirrored.Filled.FormatIndentDecrease,  // shift + tab
                        Icons.AutoMirrored.Filled.FormatIndentIncrease,  // tab
                        Icons.Filled.Delete,
                        Icons.Filled.CleaningServices,  // clear line content
                        Icons.Filled.ContentCut,
                        Icons.Filled.ContentCopy,
                        Icons.Filled.ContentPaste,
                        Icons.AutoMirrored.Filled.KeyboardReturn,  // append a line
                        Icons.Filled.SelectAll
                    )
                    val iconTextList = listOf(
                        "Shift + Tab",
                        "Tab",
                        stringResource(R.string.delete),
                        stringResource(R.string.clear),
                        stringResource(R.string.cut),
                        stringResource(R.string.copy),
                        stringResource(R.string.paste),
                        stringResource(R.string.append_a_line),
                        stringResource(R.string.select_all),
                    )
                    val iconOnClickList = listOf(
                        onShiftTab@{
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->

                                textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = false) }
                            }

                            Unit
                        },
                        onTab@{
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#indentLines") { textEditorState ->

                                textEditorState.let { it.indentLines(tabIndentSpacesCount.value, it.selectedIndices, trueTabFalseShiftTab = true) }
                            }

                            Unit
                        },
                        onDelete@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onDelete
                            }

                            val selectedLinesNum = textEditorState.value.getSelectedCount();
                            if (selectedLinesNum < 1) {
                                Msg.requireShow(activityContext.getString(R.string.no_line_selected))
                                return@onDelete
                            }

                            showDeleteDialog.value = true
                        },
                        onClear@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onClear
                            }

                            //不用确认，直接清空，若后悔可用undo撤销
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#clearSelectedFields") { textEditorState ->
                                textEditorState.clearSelectedFields()
                            }

                            Unit
                        },
                        onCut@{
                            val selectedLinesNum = textEditorState.value.getSelectedCount();
                            if (selectedLinesNum < 1) {
                                Msg.requireShow(activityContext.getString(R.string.no_line_selected))
                                return@onCut
                            }

                            clipboardManager.setText(AnnotatedString(textEditorState.value.getSelectedText()))
                            Msg.requireShow(replaceStringResList(activityContext.getString(R.string.n_lines_copied), listOf(selectedLinesNum.toString())).appendCutSuffix())
                            deleteSelectedLines()
                        },
                        onCopy@{
                            val selectedLinesNum = textEditorState.value.getSelectedCount();
                            if (selectedLinesNum < 1) {
                                Msg.requireShow(activityContext.getString(R.string.no_line_selected))
                                return@onCopy
                            }

                            clipboardManager.setText(AnnotatedString(textEditorState.value.getSelectedText()))
                            Msg.requireShow(replaceStringResList(activityContext.getString(R.string.n_lines_copied), listOf(selectedLinesNum.toString())))
//                            editableController.value.createCopiedState()
                        },

                        onPaste@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onPaste
                            }

                            val clipboardText = getClipboardText(clipboardManager)
                            if(clipboardText == null) {
                                Msg.requireShowLongDuration(activityContext.getString(R.string.clipboard_is_empty))
                                return@onPaste
                            }

                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#paste") { textEditorState ->

                                textEditorState.paste(
                                    text = clipboardText,

                                    // after pasted, scroll to the last line
                                    afterReplacedAllThenDoAct = { newFields ->
                                        val scrollTarget = newFields.lastIndex

                                        doJobThenOffLoading {
                                            delay(200)
                                            editorLastScrollEvent.value = ScrollEvent(scrollTarget)
                                        }
                                    }
                                )
                            }

                            Unit
                        },

                        onAppendALine@{
                            if (readOnlyMode) {
                                Msg.requireShow(activityContext.getString(R.string.readonly_cant_edit))
                                return@onAppendALine
                            }

                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#appendTextToLastSelectedLine") { textEditorState ->

                                textEditorState.appendTextToLastSelectedLine(
                                        text = "",
                                        afterAppendThenDoAct = { targetIndex ->
                                            doJobThenOffLoading {
                                                delay(100)
                                                scrollIfIndexInvisible(targetIndex)
                                            }
                                        }
                                    )
                                }

                                Unit
                        },
                        onSelectAll@{
                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#createSelectAllState") { textEditorState ->

                                textEditorState.createSelectAllState()
                            }

                            Unit
                        }
                    )

                    val selectedLines = textEditorState.value.getSelectedCount()
                    val hasLineSelected = selectedLines > 0
                    val hasLineSelectedAndNotReadOnly = hasLineSelected && readOnlyMode.not()
                    val iconEnableList = listOf(
                        onShiftTab@{ hasLineSelectedAndNotReadOnly },  // outdent
                        onTab@{ hasLineSelectedAndNotReadOnly },  // indent
                        onDelete@{ hasLineSelectedAndNotReadOnly },  // delete
                        onClear@{ hasLineSelectedAndNotReadOnly },  // clear the line
                        onCut@{ hasLineSelectedAndNotReadOnly },  // cut
                        onCopy@{ hasLineSelected },  // copy
                        onPaste@{ hasLineSelectedAndNotReadOnly },  // paste，只要 "选中某行" 就启用，执行时再检查剪贴板是否为空
                        onAppendALine@{ hasLineSelectedAndNotReadOnly },  // append a line
                        onSelectAll@{ true },  // select all
                    )
                    // BottomBar params block end

                    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
                    if(showSelectedItemsShortDetailsDialog.value) {
                        val trailingIconSize = MyStyleKt.defaultIconSize
                        val trailIconsWidth = trailingIconSize * 2

                        val closeDialog = { showSelectedItemsShortDetailsDialog.value = false }

                        SelectedItemDialog3(
                            selectedItems = textEditorState.value.selectedIndices,
                            textPadding = PaddingValues(start = 5.dp, end = trailIconsWidth),
                            //预览的时候带行号，拷贝的时候不带
                            text = {
                                // "行号: 行内容"
                                //it是index，index+1即行号
                                Text(text = "${it+1}: ${textEditorState.value.getContentOfLineIndex(it)}", softWrap = false, overflow = TextOverflow.Ellipsis)
                            },
                            textFormatterForCopy = { textEditorState.value.getContentOfLineIndex(it) },

                            clearAll = {
                                textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#clearSelectedItemList") { textEditorState ->

                                    textEditorState.clearSelectedItemList()
                                }
                            },
                            closeDialog = closeDialog,

                            customTrailIcon = {
                                Row(
                                    modifier = Modifier.size(height = trailingIconSize, width = trailIconsWidth).align(Alignment.CenterEnd),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // go to line
                                    LongPressAbleIconBtn(
                                        tooltipText = "",
                                        icon = Icons.Filled.PanToolAlt,
                                        iconContentDesc = "go to line button: click to go to line, long click to select span",
                                        onClick = {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                                // update focusing line index index
                                                // `forceAdd` to avoid lost selected status after go to line
                                                textEditorState.selectField(targetIndex = it, forceAdd = true)

                                                // scroll if need
                                                scrollIfIndexInvisible(it)

                                                // close dialog
                                                closeDialog()
                                            }
                                        },
                                        onLongClick = {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectFieldSpan") { textEditorState ->
                                                textEditorState.selectFieldSpan(targetIndex = it)

                                                // scroll if need
                                                scrollIfIndexInvisible(it)

                                                // close dialog
                                                closeDialog()
                                            }
                                        }
                                    )


                                    // unselect
                                    IconButton(
                                        onClick = {
                                            textEditorState.value.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                                textEditorState.selectField(targetIndex = it)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = stringResource(R.string.trash_bin_icon_for_delete_item)
                                        )
                                    }
                                }

                            }
                        )
                    }

                    BottomBar(
                        quitSelectionMode=quitSelectionMode,
                        iconList=iconList,
                        iconTextList=iconTextList,
                        iconDescTextList=iconTextList,
                        iconOnClickList=iconOnClickList,
                        iconEnableList=iconEnableList,
                        moreItemTextList= listOf(),
                        moreItemOnClickList= listOf(),
                        moreItemEnableList = listOf(),
                        getSelectedFilesCount = {selectedLines},
                        countNumOnClickEnabled = true,
                        countNumOnClick = {showSelectedItemsShortDetailsDialog.value = true}
                    )
                }
            }

//        SmallFab(modifier = Modifier.align(Alignment.BottomEnd), icon = Icons.Filled.Save, iconDesc = stringResource(id = R.string.save)) {
//
//        }
        }
    }

}

@Composable
private fun getBackgroundColor(
    isSelected: Boolean,
    isMultiSelectionMode:Boolean,
    currentIdx:Int,
    focusingIdx:Int,
    inDarkTheme:Boolean
): Color {
    return if (isMultiSelectionMode && isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    }else if(isMultiSelectionMode.not() && currentIdx == focusingIdx) {
        //选中行颜色
        MyStyleKt.LastClickedItem.getEditorLastClickedLineBgColor(inDarkTheme)
    } else {
        Color.Unspecified
    }
}

@Composable
private fun Modifier.bottomLine(
    bottomLineWidth: Dp,
    color: Color
) :Modifier {
    val density = LocalDensity.current
    val bottomLineWidthPx = with(density) { bottomLineWidth.toPx() }

    return drawBehind {
        val width = size.width
        val height = size.height
        val bottomLineHeight = height - bottomLineWidthPx / 2

        // 底线
        drawLine(
            color = color,
            start = Offset(x = 0f, y = bottomLineHeight),
            end = Offset(x = width, y = bottomLineHeight),
            strokeWidth = bottomLineWidthPx
        )

    }
}


@Composable
private fun Modifier.changeTypeIndicator(
    changeTypeLineWidth:Dp,
    changeTypeColor: Color,
    yStartOffsetInPx:Float,
) :Modifier {
//    val density = LocalDensity.current
    val isRtl = UIHelper.isRtlLayout()

    return drawBehind {
        val width = size.width
        val height = size.height


        // 每行左边的修改类型指示器，显示当前行是新增的还是修改的
        val startX = if(isRtl) width else 0f

        drawLine(
            color = changeTypeColor,
            strokeWidth = changeTypeLineWidth.toPx(),  //宽度
            //起始和结束点，单位应该是px
            start = Offset(startX, yStartOffsetInPx),
            end = Offset(startX, height),
        )

    }
}


// BEGIN: text field keyboard event handler

private fun goHomePressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: () -> Unit): Boolean {
    return onPreviewHomeOrEndKeyEvent(event, textFieldState, trueHomeFalseEnd = true, invoke)
}

private fun goEndPressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: () -> Unit): Boolean {
    return onPreviewHomeOrEndKeyEvent(event, textFieldState, trueHomeFalseEnd = false, invoke)
}

private fun goLeftPressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: (lineSwitched: Boolean) -> Unit): Boolean {
    return onPreviewLeftOrRightKeyEvent(event, textFieldState, trueLeftFalseRight = true, invoke)
}

private fun goRightPressed(event: KeyEvent, textFieldState: MyTextFieldState, invoke: (lineSwitched: Boolean) -> Unit): Boolean {
    return onPreviewLeftOrRightKeyEvent(event, textFieldState, trueLeftFalseRight = false, invoke)
}

private fun goUpKeyPressed(event: KeyEvent, invoke: () -> Unit): Boolean {
    return onPreviewUpOrDownKeyEvent(event, trueUpFalseDown = true, invoke)
}

private fun goDownKeyPressed(event: KeyEvent, invoke: () -> Unit): Boolean {
    return onPreviewUpOrDownKeyEvent(event, trueUpFalseDown = false, invoke)
}


// backspace
private fun backspacePressed(
    event: KeyEvent,
    selection: TextRange,
    invoke: () -> Unit
): Boolean {
    //不是删除不响应
    if (event.key != Key.Backspace) return false

    //没删除到行开头不响应，这时由TextField负责更新数据，
    // 若删除到行开头则需要在TextFiled外部将当前TextField从列表移除，所以需要外部处理
    // equals to Zero is same as selection range collapsed and index == 0
    if (selection != TextRange.Zero) return false

    //处理事件
    invoke()
    return true
}

// Delete
private fun forwardDeletePressed(
    event: KeyEvent,
    selection: TextRange,
    field: TextFieldValue,
    invoke: () -> Unit
): Boolean {
    // 不在当前行末尾不需要处理，这时默认处理机制即可，会自动删除光标后面的字符
    if (event.key != Key.Delete || selection.collapsed.not() || selection.start != field.text.length) {
        return false
    }

    //处理事件
    invoke()
    return true
}

private fun onPreviewUpOrDownKeyEvent(
    event: KeyEvent,

    trueUpFalseDown: Boolean,
    invoke: () -> Unit,
): Boolean {
    if(event.isCtrlPressed || event.isShiftPressed || event.isAltPressed || event.isMetaPressed) return false

    val expectedKey = if (trueUpFalseDown) KEYCODE_DPAD_UP else KEYCODE_DPAD_DOWN
    if (event.nativeKeyEvent.keyCode != expectedKey) return false


    invoke()
    return true
}

private fun onPreviewLeftOrRightKeyEvent(
    event: KeyEvent,
    field: MyTextFieldState,
    trueLeftFalseRight: Boolean,
    invoke: (lineSwitched: Boolean) -> Unit,
): Boolean {
    if(event.isCtrlPressed || event.isShiftPressed || event.isAltPressed || event.isMetaPressed || field.value.selection.collapsed.not()) return false

    val expectedKey = if(trueLeftFalseRight) KEYCODE_DPAD_LEFT else KEYCODE_DPAD_RIGHT
    if (event.nativeKeyEvent.keyCode != expectedKey) return false


    invoke((trueLeftFalseRight && field.value.selection == TextRange.Zero) || (trueLeftFalseRight.not() && field.value.selection.start == field.value.text.length))

    return true
}


private fun onPreviewHomeOrEndKeyEvent(
    event: KeyEvent,
    field: MyTextFieldState,
    trueHomeFalseEnd: Boolean,
    invoke: () -> Unit,
): Boolean {
    if(event.isCtrlPressed || event.isShiftPressed || event.isAltPressed || event.isMetaPressed || field.value.selection.collapsed.not()) return false

    val expectedKey = if(trueHomeFalseEnd) Key.MoveHome else Key.MoveEnd
    if (event.key != expectedKey) return false

    invoke()
    return true
}


//@Deprecated("due to `BasicTextField` can handle \\n by it self, so no need this function")
//private fun insertNewLineAtCursor(textFieldValue: TextFieldValue):String {
//    val splitPosition = textFieldValue.selection.start  //光标位置，有可能在行末尾，这时和text.length相等，并不会越界
//    val maxOfPosition = textFieldValue.text.length
//
//    //这个情况不应该发生
//    if (splitPosition < 0 || splitPosition > maxOfPosition) {  //第2个判断没错，就是大于最大位置，不是大于等于，没写错，光标位置有可能在行末尾，这时其索引和text.length相等，所以只有大于才有问题
//        val errMsg = "splitPosition '$splitPosition' out of range '[0, $maxOfPosition]'"
//        MyLog.e(TAG, "#getNewTextOfLine: $errMsg")
//        throw RuntimeException(errMsg)
//    }
//
//    // 在光标位置插入个换行符然后返回就行了
//    return textFieldValue.text.let {
//        val sb = StringBuilder()
//        sb.append(it.substring(0, splitPosition))
//        sb.append("\n")
//
//        //这里不需要判断，string.substring() 可用的startIndex最大值即为string.length，
//        // 若是length，会返回空字符串，与期望一致
//        sb.append(it.substring(splitPosition))
//
//        sb.toString()
//    }
//
//}
// END: text field keyboard event handler
