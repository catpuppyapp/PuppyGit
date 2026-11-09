package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.catpuppyapp.puppygit.compose.BottomBar
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.FileChangeListener
import com.catpuppyapp.puppygit.compose.FileChangeListenerState
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.LoadingTextSimple
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.OpenAsAskReloadDialog
import com.catpuppyapp.puppygit.compose.OpenAsDialog
import com.catpuppyapp.puppygit.compose.PageCenterIconButton
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.SelectEncodingDialog
import com.catpuppyapp.puppygit.compose.SelectLineBreakDialog
import com.catpuppyapp.puppygit.compose.SelectSyntaxHighlightingDialog
import com.catpuppyapp.puppygit.compose.SelectedItemDialog
import com.catpuppyapp.puppygit.compose.SelectionRow
import com.catpuppyapp.puppygit.compose.SetTabSizeDialog
import com.catpuppyapp.puppygit.compose.rememberFileChangeListenerState
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.LineNum
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dev.soraEditorComposeTestPassed
import com.catpuppyapp.puppygit.dto.FileDetail
import com.catpuppyapp.puppygit.dto.FileSimpleDto
import com.catpuppyapp.puppygit.dto.UndoStack
import com.catpuppyapp.puppygit.etc.PathType
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.ScrollEvent
import com.catpuppyapp.puppygit.fileeditor.ui.composable.FileEditor
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.editor.FileDetailList
import com.catpuppyapp.puppygit.screen.functions.getEditorStateOnChange
import com.catpuppyapp.puppygit.screen.functions.goToFileHistory
import com.catpuppyapp.puppygit.screen.shared.EditorPreviewNavStack
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.screen.shared.MainActivityLifeCycle
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.screen.shared.doActIfIsExpectLifeCycle
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.EncodingUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.doActWithLockIfFree
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.fileopenhistory.FileOpenHistoryMan
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.getFileNameFromCanonicalPath
import com.catpuppyapp.puppygit.utils.getFormattedLastModifiedTimeOfFile
import com.catpuppyapp.puppygit.utils.getHumanReadableSizeStr
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.catpuppyapp.puppygit.utils.isFileSizeOverLimit
import com.catpuppyapp.puppygit.utils.parseIntOrDefault
import com.catpuppyapp.puppygit.utils.showToast
import com.catpuppyapp.puppygit.utils.snapshot.SnapshotFileFlag
import com.catpuppyapp.puppygit.utils.snapshot.SnapshotUtil
import com.catpuppyapp.puppygit.utils.state.CustomStateListSaveable
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.withMainContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileInputStream

private const val TAG = "EditorInnerPage"

private var justForSaveFileWhenDrawerOpen = getShortUUID()

@Composable
fun EditorInnerPage(
    stateKeyTag:String,

    editorCharset: MutableState<String?>,

    lastSavedFieldsId: MutableState<String>,
    codeEditor: CustomStateSaveable<MyCodeEditor>,
    plScope: MutableState<PLScope>,

    disableSoftKb: MutableState<Boolean>,
    editorRecentListScrolled: MutableState<Boolean>,

    recentFileList: CustomStateListSaveable<FileDetail>,
    selectedRecentFileList: CustomStateListSaveable<FileDetail>,
    recentFileListSelectionMode: MutableState<Boolean>,
    recentListState: LazyStaggeredGridState,
    inRecentFilesPage: MutableState<Boolean>,

    editorFilterRecentListState: LazyStaggeredGridState,
    editorFilterRecentList: MutableList<FileDetail>,
    editorFilterRecentListOn: MutableState<Boolean>,  // filter on but may haven't a valid keyword, so actually not enabled filter
    editorEnableRecentListFilter: MutableState<Boolean>,  // indicate filter mode actually enabled or not
    editorFilterRecentListKeyword: CustomStateSaveable<TextFieldValue>,
    editorFilterRecentListLastSearchKeyword: MutableState<String>,
    editorFilterRecentListResultNeedRefresh: MutableState<String>,
    editorFilterRecentListSearching: MutableState<Boolean>,
    editorFilterRecentListSearchToken: MutableState<String>,
    editorFilterResetSearchValues: ()->Unit,
    editorRecentFilesQuitFilterMode: ()->Unit,




    loadLock:Mutex,  // 避免重复加载的锁
    ignoreFocusOnce: MutableState<Boolean>,
//    softKbVisibleWhenLeavingEditor: MutableState<Boolean>,
    previewLoading:MutableState<Boolean>,
    editorPreviewFileDto: CustomStateSaveable<FileSimpleDto>,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    requirePreviewScrollToEditorCurPos:MutableState<Boolean>,
    previewPageScrolled:MutableState<Boolean>,
    previewPath:String,
    updatePreviewPath:(String)->Unit,
    previewNavStack:CustomStateSaveable<EditorPreviewNavStack>,
    isPreviewModeOn:MutableState<Boolean>,
    mdText:MutableState<String>,
    basePath:MutableState<String>,
    quitPreviewMode:()->Unit,
    initPreviewMode:()->Unit,
    contentPadding: PaddingValues,
    currentHomeScreen: MutableIntState,
//    editorPageRequireOpenFilePath:MutableState<String>,
    editorPageShowingFilePath:MutableState<FilePath>,
    editorPageShowingFileIsReady:MutableState<Boolean>,
    editorPageTextEditorState:CustomStateSaveable<TextEditorState>,
    /**
     * 此变量严格来说并不是Editor的上个状态，而是最后一个记录到Undo/Redo stack的状态
     */
//    lastTextEditorState:CustomStateSaveable<TextEditorState>,
//    editorPageShowSaveDoneToast:MutableState<Boolean>,
    needRefreshEditorPage:MutableState<String>,
    isSaving:MutableState<Boolean>,  //这个变量只是用来判断是否可用保存按钮的
    isEdited:MutableState<Boolean>,
    showReloadDialog: MutableState<Boolean>,
    isSubPageMode:Boolean,
    showCloseDialog:MutableState<Boolean>,
    closeDialogCallback:CustomStateSaveable<(Boolean)->Unit>,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    saveOnDispose:Boolean,
    doSave: suspend ()->Unit,
    naviUp: () -> Unit,
    requestFromParent:MutableState<String>,
    editorPageShowingFileDto:CustomStateSaveable<FileSimpleDto>,

    //这个不是showingPath的上个值，这个是editor最后打开的文件，
    // 就是点 open last 时用到的那个变量。可能在关闭文件时才会更新此值（记不清了）
    lastFilePath:MutableState<String>,

    editorLastScrollEvent:CustomStateSaveable<ScrollEvent?>,
    editorListState:LazyListState,
    editorPageIsInitDone:MutableState<Boolean>,
    editorPageIsContentSnapshoted:MutableState<Boolean>,
    goToFilesPage:(path:String) -> Unit,
    drawerState: DrawerState? = null,  //只有editor作为顶级页面时用到这个变量，子页面没用，用来在drawer打开时保存文件
    goToLine:Int = LineNum.lastPosition,  //若大于0，打开文件后跳转到指定行；否则还是旧逻辑（跳转到上次退出前的第一行）。SubPageEditor会用到此变量，HomeScreen跳转来Editor的话，目前用不到，以后若有需要再传参
    editorSearchMode:MutableState<Boolean>,
    editorSearchKeyword:CustomStateSaveable<TextFieldValue>,
    readOnlyMode:MutableState<Boolean>,
    editorMergeMode:MutableState<Boolean>,
    editorPatchMode:MutableState<Boolean>,
    editorShowLineNum:MutableState<Boolean>,
    editorLineNumFontSize:MutableIntState,
    editorFontSize:MutableIntState,
    editorAdjustLineNumFontSizeMode:MutableState<Boolean>,
    editorAdjustFontSizeMode:MutableState<Boolean>,
    editorLastSavedLineNumFontSize:MutableIntState,
    editorLastSavedFontSize:MutableIntState,

    openDrawer:()->Unit,
    editorOpenFileErr:MutableState<Boolean>,
    undoStack: UndoStack,

) {
    // inner page严格来说和普通组件地位一样，所以应该用componentKey
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val scope = rememberCoroutineScope()
    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val inDarkTheme = Theme.inDarkTheme

    val exitApp = {

        AppModel.exitApp()

        Unit
    }


    //这样可在修改设置项后立刻生效
    val settings = remember(isPreviewModeOn.value) {
        val s = SettingsUtil.getSettingsSnapshot()
        previewPageScrolled.value = s.showNaviButtons
        editorRecentListScrolled.value = s.showNaviButtons
        s
    }


    val recentFilesLimit = remember(settings.editor.recentFilesLimit) { settings.editor.recentFilesLimit }

    val saveLock = remember(editorPageShowingFilePath.value.ioPath) { Cache.getSaveLockOfFile(editorPageShowingFilePath.value.ioPath) }
//    val saveLock = Mutex()  //其实这样也行，不过根据路径创建锁更严谨，跨页面也适用，比如如果首页的Editor正在保存，然后打开子页面，这时子页面必须等首页保存完成，但如果用这个和页面生命周期一样的锁，就无法实现那种效果了，但和页面生命周期一样的锁其实也够用

//    val isEdited = rememberSaveable{ mutableStateOf(false) }

    //BasicTextField用的变量
//    val editorPageShowingFileText = rememberSaveable{ mutableStateOf("") }  //BasicTextField用的文本，用来存储打开的文件的所有内容
//    val editorPageEditorFocusRequester = remember{ FocusRequester() }  //BasicTextField用的focusRequester
//    val lastFilePath = StateUtil.getRememberSaveableState(initValue = "")
    val editorPageShowingFileHasErr = rememberSaveable { mutableStateOf(false) }  //BasicTextField用的文本，用来存储打开的文件的所有内容
    val editorPageShowingFileErrMsg = rememberSaveable { mutableStateOf("") }  //BasicTextField用的文本，用来存储打开的文件的所有内容

//    val editorPageFileSavedSuccess = stringResource(R.string.file_saved)
    val unknownErrStrRes = stringResource(R.string.unknown_err)

    val curPreviewFileUsedCharset = rememberSaveable { mutableStateOf<String?>(null) }

    // lastCursorAtColumn block start
    // save last cursor at column when navigate line with keyboard Down/Up, will ignore less value than current, and will reset when open file
    val lastCursorAtColumn = rememberSaveable { mutableStateOf(0) }
    val updateLastCursorAtColumn = { newValue:Int ->
        if(newValue > lastCursorAtColumn.value) {
            lastCursorAtColumn.value = newValue
        }
    }
    val resetLastCursorAtColumn = {
        lastCursorAtColumn.value = 0
    }
    val getLastCursorAtColumnValue = {
        lastCursorAtColumn.value
    }
    // lastCursorAtColumn block end

    //在编辑器弹出键盘用的，不过后来用simple editor库了，就不需要这个了
//    val keyboardCtl = LocalSoftwareKeyboardController.current

//    val editorPageOpenedFileMap = rememberSaveable{ mutableStateOf("{}") } //{canonicalPath:fileName}

//    val editorPageShowingFileCanonicalPath = rememberSaveable{ mutableStateOf("") } //当前展示的文件的真实路径

    val editorPageSetShowingFileErrWhenLoading:(errMsg:String)->Unit = { errMsg->
        editorPageShowingFileHasErr.value=true
        editorPageShowingFileErrMsg.value=errMsg
    }
    val editorPageClearShowingFileErrWhenLoading = {
        editorPageShowingFileHasErr.value=false
        editorPageShowingFileErrMsg.value=""
    }
    val hasError = {
        editorPageShowingFileHasErr.value
    }

    val saveFontSizeAndQuitAdjust = {
        editorAdjustFontSizeMode.value = false

        if(editorLastSavedFontSize.intValue != editorFontSize.intValue) {
            editorLastSavedFontSize.intValue = editorFontSize.intValue

            SettingsUtil.update {
                it.editor.fontSize = editorFontSize.intValue
            }
        }

        Unit
    }

    val saveLineNumFontSizeAndQuitAdjust = {
        editorAdjustLineNumFontSizeMode.value = false

        if(editorLastSavedLineNumFontSize.intValue != editorLineNumFontSize.intValue) {
            editorLastSavedLineNumFontSize.intValue = editorLineNumFontSize.intValue

            SettingsUtil.update {
                it.editor.lineNumFontSize = editorLineNumFontSize.intValue
            }
        }

        Unit

    }

    val appPaused = rememberSaveable { mutableStateOf(false) }
    val fileChangeListenerState = rememberFileChangeListenerState(editorPageShowingFilePath.value.ioPath)


    editorOpenFileErr.value = remember {
        derivedStateOf {editorPageShowingFileHasErr.value && !editorPageShowingFileIsReady.value}
    }.value

    val needAndReadyDoSave:()->Boolean = { isEdited.value && !readOnlyMode.value && editorPageTextEditorState.value.fieldsId != lastSavedFieldsId.value }  //因为用了锁，只要拿到锁，肯定别人没在保存，所以无需判断isSaving，只判断文件是否编辑过即可，若没编辑过，说明之前保存成功后没动过，否则，应保存文件

    //打开主页抽屉的时候，触发保存文件。子页面永远不会触发，子页面的保存已经写到点返回箭头里了，这里不用管它
    val justForSave = remember {
        derivedStateOf {
            val drawIsOpen = drawerState?.isOpen == true
            val needRequireSave = drawIsOpen && needAndReadyDoSave()
            if (needRequireSave) {
                requestFromParent.value = PageRequest.requireSave
            }
            "justForSave: "+"uuid="+getShortUUID() + ", drawerIsOpen=" + drawIsOpen + ", isEdited=" + isEdited.value +", needRequireSave="+needRequireSave
        }
    }
    justForSaveFileWhenDrawerOpen = justForSave.value  //得获取state值，不然不会触发计算，也就不会保存
//    println(justForSaveFileWhenDrawerOpen)  //test

//    val needAndReadyDoSave = remember{derivedStateOf { isEdited.value && !isSaving.value }}
//    val needAndReadyDoSave:()->Boolean = { isEdited.value && !isSaving.value }

    // BEGIN: save functions
    val doSaveInCoroutine = {
        //离开页面时，保存文件
        doJobThenOffLoading {
            saveLock.withLock {
                if(needAndReadyDoSave()) {
                    FileChangeListenerState.ignoreOnce(fileChangeListenerState)
                    doSave()


                    MyLog.d(TAG, "#doSaveInCoroutine: file saved")
                }else{
                    MyLog.w(TAG, "#doSaveInCoroutine: will not save file, cause maybe other job already saved or saving")
                }
            }
        }
    }

    val doSaveNoCoroutine = suspend {
            //离开页面时，保存文件
        saveLock.withLock {
            if(needAndReadyDoSave()) {
                FileChangeListenerState.ignoreOnce(fileChangeListenerState)

                doSave()


                MyLog.d(TAG, "#doSaveNoCoroutine: file saved")
            }else{
                MyLog.w(TAG, "#doSaveNoCoroutine: will not save file, cause maybe other job already saved or saving")
            }
        }
    }

    //requireShowMsgToUser这个变量是为后台静默自动保存做铺垫
    val doSimpleSafeFastSaveInCoroutine = { requireShowMsgToUser:Boolean, requireBackupContent:Boolean, requireBackupFile:Boolean, contentSnapshotFlag:SnapshotFileFlag, fileSnapshotFlag:SnapshotFileFlag ->
        //离开页面时，保存文件
        doJobThenOffLoading {
            saveLock.withLock {
                if(needAndReadyDoSave()) {
                    try {
                        FileChangeListenerState.ignoreOnce(fileChangeListenerState)

                        isSaving.value=true

                        val filePath = editorPageShowingFilePath.value
                        val editorState = editorPageTextEditorState.value
//                        val fileContent = editorPageTextEditorState.value.getAllText()
//                        val fileContent = null

                        val ret = FsUtils.simpleSafeFastSave(
                            context = activityContext,
                            content = null,
                            editorState = editorState,
                            trueUseContentFalseUseEditorState = false,
                            targetFilePath = filePath,
                            requireBackupContent = requireBackupContent,
                            requireBackupFile = requireBackupFile,
                            contentSnapshotFlag = contentSnapshotFlag,
                            fileSnapshotFlag = fileSnapshotFlag
                        )

                        if(ret.success()) {
                            isEdited.value=false

                            lastSavedFieldsId.value = editorState.fieldsId

                            //更新用于判断是否重载的dto，不然每次修改内容再切到后台保存后再回来都会重载
                            editorPageShowingFileDto.value = FileSimpleDto.genByFile(editorPageShowingFilePath.value.toFuckSafFile(activityContext))


                            MyLog.d(TAG, "#doSimpleSafeFastSaveInCoroutine: file saved")
                            if(requireShowMsgToUser){
                                Msg.requireShow(activityContext.getString(R.string.file_saved))
                            }
                        }else {
                            isEdited.value=true
                            MyLog.e(TAG, "#doSimpleSafeFastSaveInCoroutine: save file err: ${ret.msg}")
                            if(requireShowMsgToUser) {
                                Msg.requireShow(ret.msg)
                            }
                        }

                    }finally {
                        isSaving.value=false
                    }
                }else{
                    MyLog.w(TAG, "#doSimpleSafeFastSaveInCoroutine: will not save file, cause maybe other job already saved or saving")
                }
            }
        }
    }
    // END: save functions



    //更新最后打开文件状态变量并保存到配置文件（注：重复打开同一文件不会重复更新）
    val saveLastOpenPath = {path:String->
        if(path.isNotBlank() && lastFilePath.value != path) {
            lastFilePath.value = path
            //更新配置文件中记录的最后打开文件
            SettingsUtil.update {
                it.editor.lastEditedFilePath = path
            }
        }
    }






    val closeFile = {
//        showCloseDialog.value=false

        isEdited.value = false
        isSaving.value=false
//        editorPageRequireOpenFilePath.value = ""
        //存上当前文件路径，要不然reOpen时还得从配置文件查，当然，app销毁后，此变量作废，依然需要从配置文件查
        saveLastOpenPath(editorPageShowingFilePath.value.ioPath)

        val emptyPath = FilePath("")
        editorPageShowingFilePath.value = emptyPath
        editorPageShowingFileDto.value.fullPath=""
        editorPageClearShowingFileErrWhenLoading()  //关闭文件清除错误，不然文件标题变了，错误还在显示
        editorPageShowingFileIsReady.value = false

        // start: reset syntax highlighting related vars
        codeEditor.value.reset(FuckSafFile(AppModel.realAppContext, emptyPath), force = true)
        // end: reset syntax highlighting related vars
        doJobThenOffLoading {
            undoStack.reset("", force = true)
        }
//        changeStateTriggerRefreshPage(needRefreshEditorPage)
    }

    if(showCloseDialog.value) {
        //若没编辑过直接关闭，否则需要弹窗确认
        if(!needAndReadyDoSave()) {
            showCloseDialog.value=false
            closeFile()
        }else {
            ConfirmDialog(
                title = stringResource(id = R.string.close),
                text = stringResource(id = R.string.will_close_file_are_u_sure),
                okTextColor = MyStyleKt.TextColor.danger(),
                onCancel = { showCloseDialog.value=false }
            ) {
                showCloseDialog.value=false
                closeFile()
            }
        }
    }

    //强制重载，不检测修改时间
    val reloadFile = r@{ force:Boolean ->

        // reload没必要退出预览模式啊，要不你就检测下如果当前是预览模式就别reload edit模式的文件，
        // 要不就无视，直接加载，但没必要退出；反之，退出预览模式时会检查是否需要重载文件
//        quitPreviewMode()

//        showReloadDialog.value=false

        //非force有概率不会重载如果判断后认为文件没修改的话（根据大小和最后修改时间，不一定总是准）
        if(force) {
            //确保重载：清空文件路径，这样和showingFilePath对比就永远不会为真，也就会百分百重载文件
            editorPageShowingFileDto.value.fullPath = ""

            // here shouldn't use reset code editor instead of release it,
            //   because it will reset PLScope to Auto,
            //   but we expect keep PLScope when reloading file
            codeEditor.value.releaseAndClearUndoStack()
        }else { // check file change if is not require force reload
            val requireOpenFilePath = editorPageShowingFileDto.value.fullPath

            try {
                val newDto = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(requireOpenFilePath)))
                val oldDto = editorPageShowingFileDto.value

                if (newDto == oldDto) {
                    MyLog.d(TAG,"EditorInnerPage#reloadFile(force=false): file may not changed, skip reload, file path is '${requireOpenFilePath}'")
                    //文件可能没改变，放弃加载
                    editorPageShowingFileIsReady.value = true
                    return@r
                }
            }catch (e: Exception) {
                MyLog.d(TAG,"EditorInnerPage#reloadFile(force=false): check file changes err, will reload file '${requireOpenFilePath}', err=${e.stackTraceToString()}")
            }
        }


        //重新加载文件，需要弹窗确认“重新加载文件将丢失未保存的修改，确定？”，加载时需要有遮罩加载动画避免加载时用户操作
        //设置当前文件为请求打开的文件，然后走打开文件流程
        isEdited.value = false
        isSaving.value = false
        editorPageShowingFileIsReady.value = false  //设置文件状态为未就绪，以显示loading界面


        changeStateTriggerRefreshPage(needRefreshEditorPage)
    }

    val forceReloadFile={
        val force = true
        reloadFile(force)
    }

    val forceReloadFilePath = { path: FilePath ->
        editorPageShowingFilePath.value = path
        forceReloadFile()
    }

    val showInFiles = { path:FilePath ->
        if(path.ioPathType == PathType.ABSOLUTE) {  // '/'开头的绝对路径
            goToFilesPage(path.ioPath)
        }else {  // saf uri，"content://"开头的玩意之类的
            // 文件管理器无法定位到非 / 开头的绝对路径，提示下路径无效即可
            Msg.requireShowLongDuration(activityContext.getString(R.string.file_path_invalid)+": "+path.ioPath)
        }
    }


    val getTheLastOpenedFilePath = {
        //如果内存中有上次关闭文件的路径，直接使用，否则从配置文件加载
        lastFilePath.value.ifBlank {
            SettingsUtil.getSettingsSnapshot().editor.lastEditedFilePath
        }
    }

    val reloadOnOkBeforeCb = remember { mutableStateOf<(()->Unit)?>(null) }
    val initReloadDialogWithCallback = { onOkBeforeCb: (()->Unit)? ->
        reloadOnOkBeforeCb.value = onOkBeforeCb
        showReloadDialog.value = true
    }

    //重新加载文件确认弹窗
    if(showReloadDialog.value) {
        //未编辑过文件，直接重载
        if(!needAndReadyDoSave()) {
            showReloadDialog.value = false  //立即关弹窗避免重入

            //检查源文件是否被外部修改过，若修改过，创建快照，然后再重载
            val newDto = FileSimpleDto.genByFile(editorPageShowingFilePath.value.toFuckSafFile(activityContext))

            if (newDto != editorPageShowingFileDto.value) {
                val fileName = editorPageShowingFileDto.value.name
                MyLog.d(TAG,"#showReloadDialog: file '${fileName}' may changed by external, will save content snapshot before reload")
//                val content = editorPageTextEditorState.value.getAllText()
//                val content = null
                val editorState = editorPageTextEditorState.value
                doJobThenOffLoading {
                    val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                        srcFileName = fileName,
                        fileContent = null,
                        editorState = editorState,
                        trueUseContentFalseUseEditorState = false,
                        flag = SnapshotFileFlag.editor_content_BeforeReloadFoundSrcFileChanged
                    )
                    if(snapRet.hasError()) {
                        MyLog.e(TAG, "#showReloadDialog: save content snapshot before reload, err: "+snapRet.msg)
                    }
                }
            }

            reloadOnOkBeforeCb.value?.invoke()
            reloadOnOkBeforeCb.value = null

            //重载文件
            forceReloadFile()
        }else {
            // 编辑过文件，弹窗询问是否确认重载
            ConfirmDialog(
                title = stringResource(R.string.reload_file),
                text = stringResource(R.string.will_reload_file_are_u_sure),
                okTextColor = MyStyleKt.TextColor.danger(),
                onCancel = {
                    showReloadDialog.value = false

                    reloadOnOkBeforeCb.value = null
                }
            ) {
                showReloadDialog.value = false

                reloadOnOkBeforeCb.value?.invoke()
                reloadOnOkBeforeCb.value = null

                forceReloadFile()
            }
        }
    }


    val showClearRecentFilesDialog = rememberSaveable { mutableStateOf(false) }
    if(showClearRecentFilesDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.clear),
            text = stringResource(R.string.clear_recent_files_confirm_text),
            okBtnText = stringResource(R.string.clear),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showClearRecentFilesDialog.value = false}
        ) {
            showClearRecentFilesDialog.value = false
            doJobThenOffLoading {
                try {
                    FileOpenHistoryMan.reset()
                    Msg.requireShow(activityContext.getString(R.string.cleared))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#ClearRecentFilesListFromEditor err: ${e.stackTraceToString()}")
                }

            }
        }
    }

    // for show save button
    val editorAsUnsaved = {
        codeEditor.value.doActWithLatestEditorStateInCoroutine("#asUnsaved") {
            it.asUnsaved()
        }
    }


    val showLineBreakDialog = rememberSaveable { mutableStateOf(false) }
    if(showLineBreakDialog.value) {
        SelectLineBreakDialog(
            current = codeEditor.value.lineBreak,
            closeDialog = { showLineBreakDialog.value = false }
        ) {
            // if line break changed, update it
            if(it != codeEditor.value.lineBreak) {
                codeEditor.value.lineBreak = it
                editorAsUnsaved()
            }
        }
    }



    val showBackFromExternalAppAskReloadDialog = rememberSaveable { mutableStateOf(false) }
    if(showBackFromExternalAppAskReloadDialog.value) {
        OpenAsAskReloadDialog(
            onCancel = { showBackFromExternalAppAskReloadDialog.value=false }
        ) {  // doReload
            //检查源文件是否被外部修改过，若修改过，创建快照，然后再重载
            val newDto = FileSimpleDto.genByFile(editorPageShowingFilePath.value.toFuckSafFile(activityContext))

            if (newDto != editorPageShowingFileDto.value) {
                val fileName = editorPageShowingFileDto.value.name
                MyLog.d(TAG,"#showBackFromExternalAppAskReloadDialog: file '${fileName}' may changed by external, will save content snapshot before reload")
//                val content = editorPageTextEditorState.value.getAllText()
//                val content = null
                val editorState = editorPageTextEditorState.value
                doJobThenOffLoading {
                    val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                        srcFileName = fileName,
                        fileContent = null,
                        editorState = editorState,
                        trueUseContentFalseUseEditorState = false,
                        flag = SnapshotFileFlag.editor_content_BeforeReloadFoundSrcFileChanged_ReloadByBackFromExternalDialog
                    )
                    if(snapRet.hasError()) {
                        MyLog.e(TAG, "#showBackFromExternalAppAskReloadDialog: save content snapshot before reload, err: "+snapRet.msg)
                    }
                }
            }

            //reload文件
            showBackFromExternalAppAskReloadDialog.value=false
            forceReloadFile()
        }
    }

    val showOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val openAsDialogFilePath = rememberSaveable { mutableStateOf("") }
    // `derivedStateOf` can auto capture state values, so, don't have to pass the `key` explicitly actually, but pass is ok as well
    val openAsDialogFileName = remember(openAsDialogFilePath.value) { derivedStateOf { getFileNameFromCanonicalPath(openAsDialogFilePath.value) } }
    val showReloadDialogForOpenAs = rememberSaveable { mutableStateOf(false) }
//    val showOpenInEditor = StateUtil.getRememberSaveableState(initValue = false)
    fun initOpenAsDialog(filePath:String, showReloadDialog:Boolean = true) {
        showReloadDialogForOpenAs.value = showReloadDialog
        openAsDialogFilePath.value = filePath
        showOpenAsDialog.value = true
    }

    if(showOpenAsDialog.value) {
        OpenAsDialog(readOnly = readOnlyForOpenAsDialog, fileName = openAsDialogFileName.value, filePath = openAsDialogFilePath.value,
            openSuccessCallback = {
                if(showReloadDialogForOpenAs.value) {
                    //x 废弃，废案，万一用户就想保留陈旧内容呢？还是询问用户吧) 如果成功请求外部打开文件，把文件就绪设为假，下次返回就会重新加载文件，避免显示陈旧内容
                    //如果请求外部打开成功，不管用户有无选择app（想实现成选择才询问是否重新加载，但无法判断）都询问是否重载文件
                    showBackFromExternalAppAskReloadDialog.value = true  // 显示询问是否重载的弹窗
                }
            }
        ) {
            //onClose
            showOpenAsDialog.value = false
        }
    }

    val checkPathThenGoToFilesPage = {
        val path = editorPageShowingFilePath.value
        if(path.isBlank()) {
            Msg.requireShow(activityContext.getString(R.string.invalid_path))
        }else {
            //如果文件不存在，显示个提示，然后跳转到file页面但不选中任何条目，否则会选中当前editor打开的文件
            val fuckSafFile = FuckSafFile(activityContext, path)
            if(!fuckSafFile.exists()) {
                Msg.requireShow(activityContext.getString(R.string.file_doesnt_exist))
            }else {  //文件存在，跳转
                //若是saf之类的路径 content://那种路径，则无法跳转哦
                //这个跳转最好用canonicalPath而不是originPath，不然当originPath为 file:// 那种路径时，跳转会失效
                goToFilesPage(fuckSafFile.canonicalPath)
            }
        }
    }

    //若想重载文件但保留stack，重载前将此变量设为真，一次性有效
    val keepPreviewNavStackOnce = rememberSaveable { mutableStateOf(false) }
    val previewLoadingOn = {
        previewLoading.value = true
    }
    val previewLoadingOff = {
        previewLoading.value = false
    }

    val previewNavBack = {
        runBlocking {
            val last = previewNavStack.value.back()
            if(last == null) {
                quitPreviewMode()
            }else {
                requestFromParent.value = if(isSubPageMode) PageRequest.requireInitPreviewFromSubEditor else PageRequest.requireInitPreview
            }
        }

        Unit
    }

    val previewNavAhead = {
        runBlocking {
            val next = previewNavStack.value.ahead()

            if(next != null){
                requestFromParent.value = if(isSubPageMode) PageRequest.requireInitPreviewFromSubEditor else PageRequest.requireInitPreview
            }
        }

        Unit
    }


    //检查类型：
    // - 若相对路径或绝对路径，检查是否存在对应文件，若不存在，吐司提示，若存在，跳转
    // - 若mailto用邮箱打开（参考关于页面的实现）
    // - 若url或其他，一律用浏览器打开
    // return true means consumed, else default link handler will take it
    val previewLinkHandler:(link:String)->Boolean = { link ->
        if(FsUtils.maybeIsRelativePath(link)) {
            val previewNavStack = previewNavStack.value
            runBlocking {
                val previewingFileFullPath = previewNavStack.previewingPath
                //当前预览文件不一定是showing file path，有可能跳转过，所以正确操作应该是取出栈中最上面的一个元素
                val linkFullPath = FsUtils.getAbsolutePathIfIsRelative(path = link, basePathNoEndSlash = FsUtils.getParentPath(previewingFileFullPath))

                val aheadFirstPath = previewNavStack.getAheadStackFirst()?.path
                if(aheadFirstPath == linkFullPath) {  //当前条目已经在ahead栈中，不需要再push。这种可能是后退过再点击和前进目标相同的链接，就会导致两个路径一样
                    previewNavAhead()
                    true
                }else { //当前条目不在栈中，push
                    val pushSuccess = previewNavStack.push(linkFullPath)
                    if(pushSuccess) {
                        previewNavAhead()
                        true
                    }else{
                        // push failed, maybe is not a valid relative path, then return false to let default link handler to take it
                        false
                    }
                }
            }
        }else {
            false
        }
    }

    val updatePreviewDto = { previewPath:String ->
        editorPreviewFileDto.value = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(previewPath)))
    }

    val refreshPreviewPageNoCoroutine = { previewPath:String, force:Boolean ->
        val needRefresh = if(force) {
            true
        }else {
            val newDto = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(previewPath)))
            val oldDto = editorPreviewFileDto.value
            //检查文件是否更新了，若判断很可能没更新，则不重载
            if (newDto == oldDto) {
                MyLog.d(TAG,"EditorInnerPage#refreshPreviewPageNoCoroutine: file may not changed, skip reload, file path is '${previewPath}'")
                false
            }else {
                true
            }
        }

        if(needRefresh) {
            val encoding = EncodingUtil.detectEncoding(newInputStream = { FileInputStream(previewPath) })
            curPreviewFileUsedCharset.value = encoding
            mdText.value = FsUtils.readFile(previewPath, EncodingUtil.resolveCharset(encoding))
            updatePreviewDto(previewPath)
        }

        //本来想顺便更新下editor state，太麻烦，那个editor的fields得处理，不能简单读下文件再赋值，而且退出preview模式已经做了重载检测，所以顶多这里载入一次，返回编辑模式再载入一次，可接受，不改了。

    }

    val refreshPreviewPage = { previewPath:String, force:Boolean ->
        doJobThenOffLoading(
            loadingOn = { previewLoadingOn() },
            loadingOff = { previewLoadingOff() }
        ) {
            refreshPreviewPageNoCoroutine(previewPath, force)
        }
    }
    //用不着这个了，在内部处理了
//    if(requestFromParent.value == PageRequest.backFromExternalAppAskReloadFile) {
//        PageRequest.clearStateThenDoAct(requestFromParent) {
//            showBackFromExternalAppAskReloadDialog.value=true
//        }
//    }

//    val codeEditorState = if(soraEditorComposeTestPassed) rememberCodeEditorState() else null


    val loadingRecentFiles = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val loadingTextForRecentFiles = rememberSaveable { mutableStateOf("") }
    val loadingOnForRecentFileList = { msg:String ->
        loadingTextForRecentFiles.value = msg
        loadingRecentFiles.value = true
    }
    val loadingOffForRecentFileList = {
        loadingRecentFiles.value = false
    }

    val needRefreshRecentFileList = rememberSaveable { mutableStateOf("") }
    val reloadRecentFileList = {
        changeStateTriggerRefreshPage(needRefreshRecentFileList)
    }

    val tabIndentSpacesCount = rememberSaveable { mutableStateOf(SettingsUtil.editorTabIndentCount()) }
    val tabSizeBuf = mutableCustomStateOf(stateKeyTag, "tabSizeBuf") { TextFieldValue("") }

    val showSetTabSizeDialog = rememberSaveable { mutableStateOf(false) }
    val initSetTabSizeDialog = {
        tabSizeBuf.value = tabIndentSpacesCount.value.toString().let { TextFieldValue(it, selection = TextRange(0, it.length)) }

        showSetTabSizeDialog.value = true
    }
    if(showSetTabSizeDialog.value) {
        val closeDialog = { showSetTabSizeDialog.value = false }
        SetTabSizeDialog(
            tabSizeBuf = tabSizeBuf,
            onCancel = closeDialog,
            onOk = ok@{ newSize ->
                val newSize = parseIntOrDefault(newSize, null)
                if(newSize == null) {
                    Msg.requireShow(activityContext.getString(R.string.invalid_number))
                    return@ok
                }

                closeDialog()

                tabIndentSpacesCount.value = newSize
                SettingsUtil.update {
                    it.editor.tabIndentSpacesCount = newSize
                }
            }
        )
    }

    val showSelectEncodingDialog = rememberSaveable { mutableStateOf(false) }
    val isConvertEncoding = rememberSaveable { mutableStateOf(false) }
    val initSelectEncodingDialog = { convert: Boolean ->
        isConvertEncoding.value = convert

        showSelectEncodingDialog.value = true
    }

    if(showSelectEncodingDialog.value) {
        SelectEncodingDialog(
            currentCharset = editorCharset.value,
            closeDialog = { showSelectEncodingDialog.value = false },
        ) { newCharset ->
            showSelectEncodingDialog.value = false

            if(newCharset != editorCharset.value) {
                if(isConvertEncoding.value) {  // convert encoding when write content to file
                    editorCharset.value = newCharset

                    editorAsUnsaved()
                }else {  // use another encoding read the file
                    initReloadDialogWithCallback {
                        editorCharset.value = newCharset
                    }
                }
            }
        }
    }

    val showSelectSyntaxHighlightDialog = rememberSaveable { mutableStateOf(false) }
    val initSelectSyntaxHighlightingDialog = {
        doJobThenOffLoading {
            // save first, for avoid change syntax highlighting cause app crash and lost data
            doSaveNoCoroutine()

            showSelectSyntaxHighlightDialog.value = true
        }
    }
    if(showSelectSyntaxHighlightDialog.value) {
        SelectSyntaxHighlightingDialog(
            plScope = plScope.value,
            closeDialog = { showSelectSyntaxHighlightDialog.value = false }
        ) {
            showSelectSyntaxHighlightDialog.value = false

            if(it != plScope.value) {
                plScope.value = it

                doJobThenOffLoading {
                    doSaveNoCoroutine()
                    forceReloadFile()
                }
            }
        }
    }


    if(requestFromParent.value == PageRequest.showLineBreakDialog) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            showLineBreakDialog.value = true
        }
    }

    if(requestFromParent.value == PageRequest.showSelectEncodingDialog) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSelectEncodingDialog(false)
        }
    }


    if(requestFromParent.value == PageRequest.convertEncoding) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSelectEncodingDialog(true)
        }
    }


    if(requestFromParent.value == PageRequest.selectSyntaxHighlighting) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSelectSyntaxHighlightingDialog()
        }
    }

    if(requestFromParent.value == PageRequest.showSetTabSizeDialog) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            initSetTabSizeDialog()
        }
    }

    if(requestFromParent.value == PageRequest.reloadRecentFileList) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            reloadRecentFileList()
        }
    }

    if(requestFromParent.value == PageRequest.reloadIfChanged) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            //这个一般是app自动检测的，非用户手动触发，所以若判断很可能文件没改变，就不必重载
            val force = false
            reloadFile(force)
        }
    }

    if(requestFromParent.value == PageRequest.requireSave) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            if(needAndReadyDoSave()){
                doSaveInCoroutine()
            }
        }
    }

    if(requestFromParent.value == PageRequest.requireClose) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            showCloseDialog.value=true
        }
    }

    if(requestFromParent.value == PageRequest.editorPreviewPageGoBack) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            previewNavBack()
        }
    }

    if(requestFromParent.value == PageRequest.editorPreviewPageGoForward) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            previewNavAhead()
        }
    }

    if(requestFromParent.value == PageRequest.editor_RequireRefreshPreviewPage) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            //这个请求一般是点击刷新按钮或者下拉刷新，都是用户手动触发的，所以强制重载
            val force = true
            refreshPreviewPage(previewPath, force)
        }
    }

    if(requestFromParent.value == PageRequest.requireInitPreview || requestFromParent.value == PageRequest.requireInitPreviewFromSubEditor) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val editorPageShowingFilePath = editorPageShowingFilePath.value.ioPath
            val previewNavStack = previewNavStack.value
            doJobThenOffLoading {
                //先保存，不然如果文件大切换预览会卡住然后崩溃导致会丢数据
                doSaveNoCoroutine()

                previewLoadingOn()

                val switchFromEditPage = isPreviewModeOn.value.not()
                var pathWillPreview = previewNavStack.getCurrent().path

                //如果是从编辑器页面切换到预览页面，定位到当前预览页面
                //判断条件为 “从编辑页面而来 且 stack当前路径和编辑器正在显示的路径不同 且 backStack或aheadStack非空”
                //判断是否需要在预览页面的导航栈“加塞”
                if(switchFromEditPage && pathWillPreview != editorPageShowingFilePath && previewNavStack.backStackOrAheadStackIsNotEmpty()) {
                    //执行类似点击链接的操作，把当前路径压入栈中
                    val pushSuccess = previewNavStack.push(editorPageShowingFilePath)
                    if(pushSuccess) {
                        previewNavStack.ahead()
                    }

                    requirePreviewScrollToEditorCurPos.value = true

                    // ahead之后，再用stack的getFirst()，取到的应该就是当前路径editorPageShowingFilePath。
                    // 若不是，就代表有bug了，这时要么使用错误的路径，但导航栈状态ok；
                    // 要么使用正确的路径editorPageShowingFilePath，但是导航栈被破坏，后果为“回退，再前进，不会恢复到editorPageShowingFilePath”。
                    // 由于后果不严重，而且用户要是不导航就不会触发，所以这里使用正确路径。
                    pathWillPreview = editorPageShowingFilePath
                }

                //开启预览模式
                //取出当前文件所在目录作为相对路径的父目录
                //从stack取出第一个元素，若没有，使用showing path
                previewNavStack.previewingPath = pathWillPreview
                updatePreviewPath(pathWillPreview)
                basePath.value = FsUtils.getParentPath(pathWillPreview)
                //如果要预览的路径和当前正在编辑的文件路径一样，直接使用内存中的数据；否则从文件读取
                mdText.value = if(pathWillPreview == editorPageShowingFilePath) {
                    curPreviewFileUsedCharset.value = editorCharset.value
                    editorPageTextEditorState.value.getAllText()
                } else {
                    val encoding = EncodingUtil.detectEncoding(newInputStream = { FileInputStream(pathWillPreview) })
                    curPreviewFileUsedCharset.value = encoding
                    FsUtils.readFile(pathWillPreview, EncodingUtil.resolveCharset(encoding))
                }

                //每次更新mdText后，都更新下dto，用来快速检测文件是否改变
                updatePreviewDto(pathWillPreview)
                //开启预览模式
                isPreviewModeOn.value = true

                previewLoadingOff()


            }
        }
    }
    if(requestFromParent.value == PageRequest.editorPreviewPageGoToTop) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val scrollState = runBlocking { previewNavStack.value.getCurrentScrollState() }
            UIHelper.scrollTo(scope, scrollState, 0)
        }
    }
    if(requestFromParent.value == PageRequest.editorPreviewPageGoToBottom) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val scrollState = runBlocking { previewNavStack.value.getCurrentScrollState() }
            UIHelper.scrollTo(scope, scrollState, Int.MAX_VALUE)
        }
    }
    if(requestFromParent.value == PageRequest.requireSaveFontSizeAndQuitAdjust) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            saveFontSizeAndQuitAdjust()
        }
    }
    if(requestFromParent.value == PageRequest.requireSaveLineNumFontSizeAndQuitAdjust) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            saveLineNumFontSizeAndQuitAdjust()
        }
    }
    if(requestFromParent.value == PageRequest.doSaveIfNeedThenSwitchReadOnly) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                //开启readonly前保存文件
                if(needAndReadyDoSave()) {
                    doSaveNoCoroutine()
                }

                readOnlyMode.value = !readOnlyMode.value
            }
        }

    }
    //这里没必要用else，以免漏判，例如我在上面可能改了request变量，如果用else，可能会漏或等下次刷新才能收到请求，但如果全用if，前面改的后面立刻就能收到
    if(requestFromParent.value == PageRequest.requireOpenAs) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            if(editorPageShowingFilePath.value.isNotBlank()) {
                doJobThenOffLoading {
                    //如果编辑过文件，先保存再请求外部打开
                    //保存
                    doSaveNoCoroutine()

                    //请求外部程序打开文件
//                    readOnlyForOpenAsDialog.value = FsUtils.isReadOnlyDir(editorPageShowingFilePath.value)

                    initOpenAsDialog(editorPageShowingFilePath.value.toFuckSafFile(activityContext).canonicalPath)
                }

            }else{
                Msg.requireShow(activityContext.getString(R.string.file_path_invalid))
            }
        }
    }

    if(requestFromParent.value == PageRequest.showInFiles) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            checkPathThenGoToFilesPage()
        }
    }

    if(requestFromParent.value == PageRequest.requireGoToFileHistory) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            goToFileHistory(editorPageShowingFilePath.value, activityContext)
        }
    }

    if(requestFromParent.value == PageRequest.requireEditPreviewingFile) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            runBlocking {
                // user expect edit current previewing file
                val previewingPath = previewNavStack.value.previewingPath  // or use page state `previewPath.value`, or `previewNavStack.getCurrent()`
                previewNavStack.value.editingPath = previewingPath

                //若正在预览和正在编辑的文件不同则重载，否则仅退出预览模式即可
                if(previewingPath != editorPageShowingFilePath.value.ioPath) {
                    keepPreviewNavStackOnce.value = true  //保留导航栈
                    forceReloadFilePath(FilePath(previewingPath))
                }

                quitPreviewMode()

            }
        }
    }

    if(requestFromParent.value == PageRequest.requireBackToHome) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            runBlocking {
                previewNavStack.value.backToHome()
                if(previewNavStack.value.backStackIsNotEmpty()) {
                    previewNavBack()
                }
            }
        }
    }


    val quitRecentListSelectionMode = {
        recentFileListSelectionMode.value = false
        selectedRecentFileList.value.clear()
    }


    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }

    val backHandlerOnBack = getBackHandler(
        inRecentFilesPage = inRecentFilesPage,
        recentFileListSelectionMode = recentFileListSelectionMode,
        quitRecentListSelectionMode = quitRecentListSelectionMode,
        editorFilterRecentListOn = editorFilterRecentListOn,
        editorRecentFilesQuitFilterMode = editorRecentFilesQuitFilterMode,

        previewNavBack = previewNavBack,
        isPreviewModeOn = isPreviewModeOn,
        quitPreviewMode = quitPreviewMode,
        activityContext = activityContext,
        textEditorState = editorPageTextEditorState,
        isSubPage = isSubPageMode,
        isEdited = isEdited,
        readOnlyMode = readOnlyMode,
//        doSaveInCoroutine,
        doSaveNoCoroutine = doSaveNoCoroutine,
        searchMode = editorSearchMode,
        needAndReadyDoSave = needAndReadyDoSave,
        naviUp = naviUp,
        adjustFontSizeMode=editorAdjustFontSizeMode,
        adjustLineNumFontSizeMode=editorAdjustLineNumFontSizeMode,
        saveFontSizeAndQuitAdjust = saveFontSizeAndQuitAdjust,
        saveLineNumFontSizeAndQuitAdjust = saveLineNumFontSizeAndQuitAdjust,
        exitApp = exitApp,
        openDrawer=openDrawer,
        requestFromParent = requestFromParent

    )

    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end


    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val detailsStr = rememberSaveable { mutableStateOf("") }
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(R.string.details),
            text = detailsStr.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsStr.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }


    if(requestFromParent.value==PageRequest.showDetails) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val editorPageShowingFilePath = editorPageShowingFilePath.value
            val currentFile = FuckSafFile(activityContext, if(isPreviewModeOn.value) FilePath(previewPath) else editorPageShowingFilePath)
            val isEditingFile = currentFile.path.ioPath == editorPageShowingFilePath.ioPath
//            val fileReadable = currentFile.canRead()  // 注：saf的canRead()不准，所以弃用此判断
            val fileReadable = true
            val fileName = currentFile.name
            val fileSize = if(fileReadable) getHumanReadableSizeStr(currentFile.length()) else 0
            //仅文件可读且当前预览或编辑的文件与当前编辑的文件相同时才显示行数和字数
            val showLinesCharsCount = isEditingFile && fileReadable
            val (charsCount, linesCount) = if(showLinesCharsCount) editorPageTextEditorState.value.getCharsAndLinesCount() else Pair(0, 0)
//            val lastModifiedTimeStr = getFormatTimeFromSec(sec=file.lastModified()/1000, offset = getSystemDefaultTimeZoneOffset())
            val lastModifiedTimeStr = if(fileReadable) getFormattedLastModifiedTimeOfFile(currentFile) else ""


            val sb = StringBuilder()
            val suffix = "\n\n"
            sb.append(activityContext.getString(R.string.file_name)+": "+fileName).append(suffix)
            sb.append(activityContext.getString(R.string.path)+": "+ currentFile.path.ioPath).append(suffix)

            if(showLinesCharsCount) {
                sb.append(activityContext.getString(R.string.chars)+": "+charsCount).append(suffix)
                sb.append(activityContext.getString(R.string.lines) +": "+linesCount).append(suffix)
            }

            if(fileReadable) {
                sb.append(activityContext.getString(R.string.file_size)+": "+fileSize).append(suffix)
                sb.append(activityContext.getString(R.string.last_modified)+": "+lastModifiedTimeStr).append(suffix)
            }

            sb.append(activityContext.getString(R.string.encoding)+": "+(if(isPreviewModeOn.value) curPreviewFileUsedCharset.value else editorCharset.value)).append(suffix)
            sb.append(activityContext.getString(R.string.line_break)+": "+codeEditor.value.lineBreak.visibleValue).append(suffix)

            detailsStr.value = sb.removeSuffix(suffix).toString()
            showDetailsDialog.value = true
        }
    }



    //如果可能ok，返回的是FuckSafFile，否则返回路径
//    val ifLastPathOkThenDoOkActElseDoNoOkAct:(okAct:(last: FuckSafFile)->Unit, noOkAct:(last:String)->Unit)->Unit = { okAct, noOkAct ->
//        var last = lastFilePath.value
//        if(last.isBlank()) {  //如果内存中有上次关闭文件的路径，直接使用，否则从配置文件加载
//            last = SettingsUtil.getSettingsSnapshot().editor.lastEditedFilePath
//        }
//
//        //如果查无上次打开文件，吐司提示 "last file not found!"；否则打开文件
//        //x 废弃，应在设置页面添加一个手动清除编辑器记录的位置信息的功能而不是一出异常就清除) 注：这里不要判断文件是否存在，留到reload时判断，在那里如果发现文件不存在将清除文件的上次编辑位置等信息
////        var fuckSafFile: FuckSafFile? = null
////        if(last.isNotBlank() && FuckSafFile(activityContext, FilePath(last)).let { fuckSafFile = it; it.exists() }) {
//
//        //这个根据我的体验，少检测少废话，直接打开，有错直接展示在editor页面体验最好，
//        // 不然吐丝提示文件未找到，我根本不知道是哪个鸟文件找不到
//        if(last.isNotBlank()) {
//            okAct(FuckSafFile(activityContext, FilePath(last)))
//        }else {
//           noOkAct(last)
//        }
//    }



    val notOpenFile = !editorPageShowingFileHasErr.value && !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isBlank()
    val loadingFile = !editorPageShowingFileHasErr.value && !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isNotBlank()
    val somethingWrong = editorPageShowingFileHasErr.value || !editorPageShowingFileIsReady.value || editorPageShowingFilePath.value.isBlank()


    //not open file (and no err)
    if (notOpenFile) {  //文件未就绪且无正在显示的文件且没错误


        val selectionMode = recentFileListSelectionMode
        val quitSelectionMode = quitRecentListSelectionMode




        //多选模式相关函数，开始
        val switchItemSelected = { item: FileDetail ->
            //如果元素不在已选择条目列表则添加
            UIHelper.selectIfNotInSelectedListElseRemove(item, selectedRecentFileList.value)
            //开启选择模式
            selectionMode.value = true
        }

        val selectItem = { item:FileDetail ->
            selectionMode.value = true
            UIHelper.selectIfNotInSelectedListElseNoop(item, selectedRecentFileList.value)
        }

        val isItemInSelected= { item:FileDetail ->
            selectedRecentFileList.value.contains(item)
        }
        // 多选模式相关函数，结束


        LaunchedEffect(needRefreshRecentFileList.value) {
            doJobThenOffLoading(loadingOnForRecentFileList, loadingOffForRecentFileList, activityContext.getString(R.string.loading)) {

                try {
                    val historyMap = FileOpenHistoryMan.getHistory().storage

                    val recentFiles = historyMap
                        // sort
                        .toSortedMap({ k1, k2 ->
                            //从自己的对象查自己的key，肯定有对象，所以后面直接双叹号断言非空
                            val v1 = historyMap.get(k1)!!
                            val v2 = historyMap.get(k2)!!
                            // lastUsedTime descend sort
                            if (v1.lastUsedTime > v2.lastUsedTime) -1 else 1
                        }).let { sortedMap ->
                            val list = mutableListOf<FileDetail>()
                            for((k, _lastEditPos) in sortedMap) {
                                if(list.size >= recentFilesLimit) {
                                    break
                                }

                                val file = FuckSafFile(activityContext, FilePath(k))
                                //若文件名为空，说明失去读写权限了，不添加到列表
                                if(file.name.isNotEmpty()) {
                                    // auto detect encoding when reading content
                                    val fileShortContent = FsUtils.readShortContent(file)

                                    //如果从外部app请求本app打开文件，然后对方app没允许获取永久uri权限，那么下次重启本app后，这个文件名有可能会变成空白，除非请求打开的路径可以解析出相应的绝对路径，那样本app就会使用绝对路径访问文件，就是 "/storage/emulate/0" 那种路径，这时文件名就不会有错了，除非用户没授权访问外部存储
                                    // Pair(fileName, FilePath对象)
                                    list.add(
                                        FileDetail(
                                            file = file,
                                            shortContent = fileShortContent,
                                        )
                                    )
                                }
                            }

                            list
                        };

                    // add sorted list to state list
                    recentFileList.value.let {
                        it.clear()
                        it.addAll(recentFiles)
                    }

                    // update selected list
                    if(recentFileListSelectionMode.value) {
                        val selectedList = selectedRecentFileList.value
                        val newSelectedList = mutableListOf<FileDetail>()
                        selectedList.forEachBetter {
                            for(f in recentFiles) {
                                if(f.file.path.ioPath == it.file.path.ioPath) {
                                    newSelectedList.add(f)
                                }
                            }
                        }

                        selectedList.clear()
                        selectedList.addAll(newSelectedList)

                        if(selectedList.isEmpty()) {
                            quitSelectionMode()
                        }
                    }

                    changeStateTriggerRefreshPage(editorFilterRecentListResultNeedRefresh)

                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "get recent files err")
                    MyLog.e(TAG, "Recent Files onClick err: ${e.stackTraceToString()}")
                }
            }
        }


        if(loadingRecentFiles.value) {
            LoadingTextSimple(loadingTextForRecentFiles.value, contentPadding)
        }else {
            if(recentFileList.value.isNotEmpty()) {

                LaunchedEffect(Unit) {
                    inRecentFilesPage.value = true
                }

                DisposableEffect(Unit) {
                    onDispose {
                        inRecentFilesPage.value = false

                        // this is possible, because when leave this code block, the recentFileList maybe empty(e.g. removed all recent files)
                        // but if switch page, this maybe false, so need check before quit
                        if(recentFileList.value.isEmpty()) {
                            quitSelectionMode()
                        }
                    }
                }

                PullToRefreshBox(
                    contentPadding = contentPadding,
                    onRefresh = { reloadRecentFileList() }
                ) {
                    FileDetailList(
                        filterListState = editorFilterRecentListState,
                        filterList = editorFilterRecentList,
                        filterOn = editorFilterRecentListOn,
                        enableFilterState = editorEnableRecentListFilter,
                        filterKeyword = editorFilterRecentListKeyword,
                        lastSearchKeyword = editorFilterRecentListLastSearchKeyword,
                        filterResultNeedRefresh = editorFilterRecentListResultNeedRefresh,
                        searching = editorFilterRecentListSearching,
                        searchToken = editorFilterRecentListSearchToken,
                        resetSearchVars = editorFilterResetSearchValues,

                        contentPadding = contentPadding,
                        state = recentListState,
                        isItemSelected = isItemInSelected,
                        list = recentFileList.value,
                        onClick = {
                            if(selectionMode.value) {
                                switchItemSelected(it)
                            }else {
                                forceReloadFilePath(it.file.path)
                            }
                        },
                        itemOnLongClick = {idx, it->
                            if(selectionMode.value) {
                                // span select
                                UIHelper.doSelectSpan(
                                    itemIdxOfItemList = idx,
                                    item = it,
                                    selectedItems = selectedRecentFileList.value,
                                    itemList = recentFileList.value,
                                    switchItemSelected = switchItemSelected,
                                    selectIfNotInSelectedListElseNoop = selectItem
                                )
                            }else {
                                switchItemSelected(it)
                            }
                        },
                    )
                }
            }else {
                // if is sub editor, after close a file, the closed file must available in the `recentFileList`, so, no chance to reach `else` block
                //仅在主页导航来的情况下才显示选择文件，否则显示了也不好使，因为显示子页面的时候，主页可能被销毁了，或者被覆盖了，改状态跳转页面不行，除非导航，但没必要导航，直接隐藏即可
                if(!isSubPageMode) {
                    PageCenterIconButton(
                        contentPadding = contentPadding,
                        icon = Icons.Filled.Folder,
                        text = stringResource(R.string.select_file),
                        onClick = {
                            currentHomeScreen.intValue = Cons.selectedItem_Files
                        }
                    )
                } else {  // should never reach here, but may be...will reach here in some weird cases, so, better keep this code block
                    PageCenterIconButton(
                        contentPadding = contentPadding,
                        icon = ImageVector.vectorResource(R.drawable.outline_reopen_window_24),
                        text = stringResource(R.string.reopen),
                        onClick = {
                            forceReloadFilePath(FilePath(getTheLastOpenedFilePath()))
                        }
                    )
                }
            }

            val showDeleteRecentFilesDialog = rememberSaveable { mutableStateOf(false) }
            val deleteFileOnDisk = rememberSaveable { mutableStateOf(false) }
            val initDeleteRecentFilesDialog = {
                deleteFileOnDisk.value = false
                showDeleteRecentFilesDialog.value = true
            }

            if(showDeleteRecentFilesDialog.value) {
                ConfirmDialog(
                    title = stringResource(R.string.delete),
                    requireShowTextCompose = true,
                    textCompose = {
                        ScrollableColumn {
                            SelectionRow {
                                Text(stringResource(R.string.will_delete_selected_items_are_u_sure), fontSize = MyStyleKt.TextSize.medium)
                            }

                            Spacer(Modifier.height(20.dp))

                            MyCheckBox(stringResource(R.string.del_files_on_disk), deleteFileOnDisk)
                        }
                    },
                    onCancel = { showDeleteRecentFilesDialog.value = false },
                    okBtnText = stringResource(R.string.delete),
                    okTextColor = if(deleteFileOnDisk.value) MyStyleKt.TextColor.danger() else Color.Unspecified,
                ) {
                    showDeleteRecentFilesDialog.value = false

//                    Msg.requireShow(activityContext.getString(R.string.deleting))

                    val deleteFileOnDisk = deleteFileOnDisk.value
                    val targetList = selectedRecentFileList.value.toList()

                    doJobThenOffLoading {
                        targetList.forEachBetter {
                            recentFileList.value.remove(it)
                            editorFilterRecentList.remove(it)

                            selectedRecentFileList.value.remove(it)
                            FileOpenHistoryMan.remove(it.file.path.ioPath)

                            if(deleteFileOnDisk) {
                                if(it.file.path.ioPathType == PathType.ABSOLUTE) {
                                    try {
                                        File(it.file.path.ioPath).delete()
                                    }catch (e: Exception) {
                                        MyLog.w(TAG, "remove file failed: ioPath=${it.file.path.ioPath}, err=${e.localizedMessage}")
                                    }
                                }
                            }
                        }

                        Msg.requireShow(activityContext.getString(R.string.deleted))
                    }
                }

            }

            val iconList = listOf(
                Icons.Filled.Delete,
                Icons.Filled.DocumentScanner,  // show in files
                Icons.AutoMirrored.Filled.OpenInNew, // open as
                Icons.Filled.SelectAll,
            )
            val iconTextList = listOf(
                stringResource(R.string.delete),
                stringResource(R.string.show_in_files),
                stringResource(R.string.open_as),
                stringResource(R.string.select_all),
            )
            val iconOnClickList = listOf(
                delete@{
                    initDeleteRecentFilesDialog()
                },

                showInFiles@{
                    selectedRecentFileList.value.firstOrNull()?.let { showInFiles(it.file.path) }
                    Unit
                },

                openAs@{
                    selectedRecentFileList.value.firstOrNull()?.let { initOpenAsDialog(it.file.path.ioPath, showReloadDialog = false) }
                    Unit
                },

                selectAll@{
                    selectedRecentFileList.value.let {
                        it.clear()
                        it.addAll(recentFileList.value)
                    }

                    Unit
                }
            )

            val getSelectedFilesCount = {
                selectedRecentFileList.value.size
            }
            val iconEnableList = listOf(
                delete@{ true },
                showInFiles@{ isSubPageMode.not() && selectedRecentFileList.value.size == 1 },
                openAs@{ selectedRecentFileList.value.size == 1 },
                selectAll@{ true },
            )



            val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
            if(showSelectedItemsShortDetailsDialog.value) {
                SelectedItemDialog(
                    selectedItems = selectedRecentFileList.value,
                    formatter = {it.file.name},
                    switchItemSelected = switchItemSelected,
                    clearAll = {selectedRecentFileList.value.clear()},
                    closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
                )
            }

            val countNumOnClickForBottomBar = {
                showSelectedItemsShortDetailsDialog.value = true
            }
            if(selectionMode.value) {
                BottomBar(
                    quitSelectionMode=quitSelectionMode,
                    iconList=iconList,
                    iconTextList=iconTextList,
                    iconDescTextList=iconTextList,
                    iconOnClickList=iconOnClickList,
                    iconEnableList=iconEnableList,
                    moreItemTextList=listOf(),
                    moreItemOnClickList=listOf(),
                    moreItemEnableList = listOf(),
                    getSelectedFilesCount = getSelectedFilesCount,
                    countNumOnClickEnabled = true,
                    countNumOnClick = countNumOnClickForBottomBar,
                    reverseMoreItemList = true
                )
            }

        }

    }

    // open file err or no file opened or loading file
    if(
        ((editorOpenFileErr.value) // open file err
//                || (notOpenFile)  // no file opened
                || (loadingFile))  // loading file
        && somethingWrong  // load file err or file not ready or file path is blank
        && !notOpenFile
    ){
        FullScreenScrollableColumn(contentPadding) {
            val fontSize = MyStyleKt.TextSize.default
            val iconModifier = MyStyleKt.Icon.modifier

            //20240429: 这里必须把判断用到的状态变量写全，不然目前的Compose的刷新机制可能有bug，当if的第一个判断条件值没变时，可能会忽略后面的判断，有待验证
//        if((""+editorPageShowingFileHasErr.value+editorPageShowingFileIsReady.value+editorPageShowingFilePath.value).isNotEmpty()) {
            //下面的判断其实可用if else，但为了确保状态改变能刷新对应分支，全用的if
            // open file err
            if (editorOpenFileErr.value) {  //如果文件未加载就绪，加载出错显示错误，否则显示Loading...
                // err text
                MySelectionContainer {
                    Row {
                        Text(
                            text = stringResource(id = R.string.open_file_failed)+"\n"+editorPageShowingFileErrMsg.value,
                            color = MyStyleKt.TextColor.error(),
                            fontSize = fontSize

                        )

                    }

                }
                Spacer(modifier = Modifier.height(15.dp))

                // actions
                Row {
                    LongPressAbleIconBtn(
                        enabled = true,
                        iconModifier = iconModifier,
                        tooltipText = stringResource(R.string.open_as),
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        iconContentDesc = stringResource(id = R.string.open_as),
                    ) {
                        //点击用外部程序打开文件
                        requestFromParent.value = PageRequest.requireOpenAs
                    }

                    LongPressAbleIconBtn(
                        enabled = true,
                        iconModifier = iconModifier,
                        tooltipText = stringResource(R.string.reload),
                        icon =  Icons.Filled.Refresh,
                        iconContentDesc = stringResource(id = R.string.reload),
                    ) {
                        forceReloadFile()
                    }

                    //只有顶级页面的editor才显示show in files
                    if(!isSubPageMode){
                        LongPressAbleIconBtn(
                            enabled = true,
                            iconModifier = iconModifier,
                            tooltipText = stringResource(R.string.show_in_files),
                            icon =  Icons.Filled.DocumentScanner,
                            iconContentDesc = stringResource(id = R.string.show_in_files),
                        ) {
                            checkPathThenGoToFilesPage()
                        }

                    }

                    LongPressAbleIconBtn(
                        enabled = true,
                        iconModifier = iconModifier,
                        tooltipText = stringResource(R.string.close),
                        icon =  Icons.Filled.Close,
                        iconContentDesc = stringResource(id = R.string.close),
                    ) {
                        closeFile()
                    }
                }

            }


            // loading file
            //没错误且文件未就绪且正在显示的文件路径不为空，那就是正在加载，显示loading
            if(loadingFile) {
                Text(stringResource(R.string.loading))
            }
//        }
        }
    }


    val isTimeShowEditor = !editorPageShowingFileHasErr.value && editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isNotBlank()
    // file loaded (load file successfully)
    if(isTimeShowEditor) {
        if(soraEditorComposeTestPassed) {
//            CodeEditor(
//                modifier = Modifier
//                    .padding(contentPadding)
//                    .fillMaxSize()
//                ,
//                state = codeEditorState!!,
//            )
        }else {
            val fileFullPath = editorPageShowingFilePath.value
            val fileEditedPos = FileOpenHistoryMan.get(fileFullPath.ioPath)

            FileEditor(
                stateKeyTag = stateKeyTag,

                plScope = plScope,

                disableSoftKb = disableSoftKb,
                updateLastCursorAtColumn = updateLastCursorAtColumn,
                getLastCursorAtColumnValue = getLastCursorAtColumnValue,

                ignoreFocusOnce = ignoreFocusOnce,
//                softKbVisibleWhenLeavingEditor = softKbVisibleWhenLeavingEditor,
                requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
                requirePreviewScrollToEditorCurPos = requirePreviewScrollToEditorCurPos,
                isSubPageMode = isSubPageMode,
                previewNavBack = previewNavBack,
                previewNavAhead = previewNavAhead,
                previewNavStack = previewNavStack,
                refreshPreviewPage = { requestFromParent.value = PageRequest.editor_RequireRefreshPreviewPage },
                previewLinkHandler = previewLinkHandler,
                previewLoading = previewLoading.value,
                mdText = mdText,
                basePath = basePath,

                isPreviewModeOn = isPreviewModeOn,
                quitPreviewMode = quitPreviewMode,
                initPreviewMode = initPreviewMode,

                openDrawer = openDrawer,
                requestFromParent = requestFromParent,
                fileFullPath = fileFullPath,
                lastEditedPos = fileEditedPos,
                textEditorState = editorPageTextEditorState,

                contentPadding = contentPadding,
                //如果外部Column进行Padding了，这里padding传0即可
//            contentPadding = PaddingValues(0.dp),

                isContentEdited = isEdited,   //谁调用onChanged，谁检查内容是否改变
                editorLastScrollEvent=editorLastScrollEvent,
                editorListState=editorListState,
                editorPageIsInitDone = editorPageIsInitDone,
                editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
                goToLine=goToLine,
                readOnlyMode=readOnlyMode.value,
                searchMode = editorSearchMode,
                searchKeyword=editorSearchKeyword.value.text,
                mergeMode=editorMergeMode.value,
                showLineNum=editorShowLineNum,
                lineNumFontSize=editorLineNumFontSize,
                fontSize=editorFontSize,
                undoStack = undoStack,
                patchMode = editorPatchMode.value,
                tabIndentSpacesCount = tabIndentSpacesCount,
            )
        }


        if(appPaused.value.not()) {
            FileChangeListener(
                state = fileChangeListenerState,
                context = activityContext,
                path = editorPageShowingFilePath.value.ioPath,
            ) {
                val printFilePath = "filePath = '${editorPageShowingFilePath.value.ioPath}'"

                if(
                // 未在Editor点击 open as，用外部程序打开，显示询问是否重载的弹窗，
                // 若显示此弹窗，用户可手动确认是否重载，所以没必要自动重载
                    showBackFromExternalAppAskReloadDialog.value.not()
                    && isPreviewModeOn.value.not() // will try reload file when quit preview mode, so, don't need reload at here
                    && editorPageShowingFilePath.value.isNotBlank()
                    && isEdited.value.not()  // if edited, should not auto reload to avoid lost user changes
                    && isSaving.value.not()  // not saving in progress
                ) {
                    MyLog.d(TAG, "file is changed by external, will reload it, $printFilePath")

                    // text editor state changed, so we should clear redo stack
                    // and push currently text editor state into undo stack
                    doJobThenOffLoading {
                        undoStack.clearRedoStackThenPushToUndoStack(editorPageTextEditorState.value, force = true)
                    }

                    // set force to false to check last saved file info to avoid nonsense reload,
                    //   but most time the file need a force reload when reached here, because we already detected changes,
                    //   anyway, set force to false is a better choice
                    // 其实这个检测，force false或true区别不大，
                    // 因为这里已经检测到修改了，
                    // 大概率会需要重载文件，
                    // 不过false也没坏处，
                    // 顶多多检测几次文件信息，
                    // 确认文件确实修改了才重载，没明显坏处
                    val force = false
                    reloadFile(force)
                }else {
                    MyLog.d(TAG, "file is changed by external, but currently app was modified file also, so will not reload it, $printFilePath")
                }
            }
        }

    }





    //按Home键把app切到后台时保存文件，chatgpt弱智玩意告诉我这个东西监听的是Activity的生命周期，日，其实他妈的是Compose的！
    // 这个东西监听的是Compose的生命周期，不是Activity的，若Compose所处的Activity的ON_PAUSE被触发，
    // 此组件的一定会触发；若此组件的被触发，Activity的不一定触发。而且，由于compose是基于Activity创建的，
    // 所以compose的on pause会先被调用，而Activity的之后才会调用，如果想要检测Activity的on_pause的话需要注意这一点。
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        appPaused.value = true

        val requireShowMsgToUser = true

        val requireBackupContent = true
        val requireBackupFile = true
        val contentSnapshotFlag = SnapshotFileFlag.editor_content_OnPause
        val fileSnapshotFlag = SnapshotFileFlag.editor_file_OnPause

        doSimpleSafeFastSaveInCoroutine(
            requireShowMsgToUser,
            requireBackupContent,
            requireBackupFile,
            contentSnapshotFlag,
            fileSnapshotFlag
        )

        //先调用保存文件再log，以免app崩溃时慢一拍导致没存上
        MyLog.d(TAG, "#Lifecycle.Event.ON_PAUSE: will save file: ${editorPageShowingFilePath.value}")

//        MyLog.d(TAG, "#Lifecycle.Event.ON_PAUSE: called")

    }

    //按Home切换到别的app再返回，检查如果当前文件已保存（已编辑为假），则重载
    //和on pause触发顺序相反，Activity的on resume事件先调用，之后才调用compose的，因为必须得先有Activity才能有compose，后者依赖前者，所以销毁的时候，先销毁后面的，恢复的时候，先恢复前面的。
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        appPaused.value = false

        // if need will re-analyze, if need not, will do nothing
        //   this check is necessary, else, after you changed theme dark/light,
        //   the syntax highlighting will not update automatically
        codeEditor.value.updatePlScopeThenAnalyze()

        //检查，如果 Activity 的on resume事件刚被触发，说明用户刚从后台把app调出来，这时调用软重载文件（会检测变化若判断很可能无变化则不重载）
        doActIfIsExpectLifeCycle(MainActivityLifeCycle.ON_RESUME) {
            //如果显示重载确认弹窗，则不自动重载；如果未显示重载确认弹窗且文件未编辑（换句话说：已成功保存），则自动重载
            if(isPreviewModeOn.value) {
                //预览模式从后台返回，无脑重载，因为有可能用户仅用预览模式，但在外部用另一个软件编辑文件，切换着查看，所以总是重载
                val force = false
                refreshPreviewPage(previewPath, force)

                MyLog.d(TAG, "#Lifecycle.Event.ON_RESUME: Preview Mode is On, will reload file: $previewPath")

            }
        }

        MyLog.d(TAG, "#Lifecycle.Event.ON_RESUME: called")

    }

    LaunchedEffect(needRefreshEditorPage.value) {
        //这里不需要loadingOn和loadingOff，靠editorPageShowingFileIsReady来判断是否加载完毕文件，历史遗留问题，这个页面的loading有点混乱
        // doJobThenOffLoading(loadingOn, loadingOff, appContext.getString(R.string.loading)){
        doJobThenOffLoading {
            try {

                doActWithLockIfFree(loadLock, "EditorInnerPage#Init#${needRefreshEditorPage.value}#${editorPageShowingFilePath.value.ioPath}") {
                    doInit(
                        editorCharset = editorCharset,
                        lastSavedFieldsId = lastSavedFieldsId,
                        codeEditor = codeEditor.value,
                        resetLastCursorAtColumn = resetLastCursorAtColumn,
                        requirePreviewScrollToEditorCurPos = requirePreviewScrollToEditorCurPos,
                        ignoreFocusOnce = ignoreFocusOnce,
                        isPreviewModeOn = isPreviewModeOn,
                        previewPath = previewPath,
                        updatePreviewPath = updatePreviewPath,
                        keepPreviewStack = keepPreviewNavStackOnce,
                        previewNavStack = previewNavStack,
                        activityContext = activityContext,
                        editorPageShowingFilePath = editorPageShowingFilePath,
                        editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                        editorPageClearShowingFileErrWhenLoading = editorPageClearShowingFileErrWhenLoading,
                        editorPageTextEditorState = editorPageTextEditorState,
                        unknownErrStrRes = unknownErrStrRes,
                        editorPageSetShowingFileErrWhenLoading = editorPageSetShowingFileErrWhenLoading,
                        pageRequest = requestFromParent,
                        editorPageShowingFileDto = editorPageShowingFileDto,
                        isSubPage = isSubPageMode,
                        editorLastScrollEvent = editorLastScrollEvent,
                        editorPageIsInitDone = editorPageIsInitDone,
                        isEdited = isEdited,
                        isSaving = isSaving,
                        isContentSnapshoted = editorPageIsContentSnapshoted,
//                        lastTextEditorState = lastTextEditorState,
                        undoStack = undoStack,
                        hasError = hasError
                    )

                }
            } catch (e:Exception) {
                Msg.requireShowLongDuration("init Editor err: ${e.localizedMessage}")
                MyLog.e(TAG, "#init Editor page err: ${e.stackTraceToString()}")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // 由于是绝对路径，所以至少有个"/"，因此用isNotBlank()判断而不是isNotEmpty()
            //打开成功或打开失败，设置下文件路径，这样在旋转屏幕后就会再次重试打开文件以恢复之前的状态
            // 由于存在 fileIsReady或openFileErr还没更新但文件路径不为空就销毁组件的情况，
            //  所以，这里不检查文件是否成功打开，直接赋值，理论上来说，只要在关闭文件的逻辑清空路径就可确保这里不会出现反直觉的用户体验，而我已在关闭文件时清空了路径，但我不确定效果如何，
            //  测试一下，若没问题就这样，否则改成先检测文件ready或err，再保存路径
            //改成在读取完后保存此路径了
//            AppModel.lastEditFile = editorPageShowingFilePath.value

            //20240327:尝试解决加载文件内容未更新的bug
            editorPageShowingFileIsReady.value = false

            if(saveOnDispose) {
                doSaveInCoroutine()
            }

            //保存最后打开文件路径
            saveLastOpenPath(editorPageShowingFilePath.value.ioPath)

            //避免离开编辑器再回来自动弹键盘(改用ignore focus once控制了，不需要这个了)
//            editorPageTextEditorState.value = editorPageTextEditorState.value.copy(focusingLineIdx = null)

        // ///////////            editorPageShowingFilePath.value = ""
        }
    }
}



private suspend fun doInit(
    editorCharset: MutableState<String?>,
    lastSavedFieldsId: MutableState<String>,
    codeEditor: MyCodeEditor,
    resetLastCursorAtColumn: ()->Unit,
    requirePreviewScrollToEditorCurPos: MutableState<Boolean>,
    ignoreFocusOnce: MutableState<Boolean>,
    isPreviewModeOn: MutableState<Boolean>,
    previewPath: String,
    updatePreviewPath: (String)->Unit,
    keepPreviewStack:MutableState<Boolean>,
    previewNavStack:CustomStateSaveable<EditorPreviewNavStack>,
    activityContext:Context,
    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    editorPageClearShowingFileErrWhenLoading: () -> Unit,
    editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
    unknownErrStrRes: String,
    editorPageSetShowingFileErrWhenLoading: (errMsg: String) -> Unit,
    pageRequest:MutableState<String>,
    editorPageShowingFileDto: CustomStateSaveable<FileSimpleDto>,
    isSubPage: Boolean,
    editorLastScrollEvent:CustomStateSaveable<ScrollEvent?>,
    editorPageIsInitDone:MutableState<Boolean>,
    isEdited:MutableState<Boolean>,
    isSaving:MutableState<Boolean>,
    isContentSnapshoted:MutableState<Boolean>,
//    lastTextEditorState: CustomStateSaveable<TextEditorState>,
    undoStack:UndoStack,
    hasError:()->Boolean,
) {

    MyLog.d(TAG, "#doInit: editorPageShowingFilePath=${editorPageShowingFilePath.value}")

    //保存后不改变needrefresh就行了，没必要传这个变量
    //保存文件时会设置这个变量，因为保存的内容本来就是最新的，不需要重新加载
//        if(pageRequest.value ==PageRequest.needNotReloadFile) {
//            PageRequest.clearStateThenDoAct(pageRequest){}
//            return@doJobThenOffLoading
//        }

    //打开文件
    //告知组件文件还未就绪（例如 未加载完毕）(20240326:尝试解决加载文件内容未更新的bug，把这行挪到了上面)
//        editorPageShowingFileIsReady.value = false  //20240429:文件未就绪应归调用者设置
    //如果存在待打开的文件，则打开，文件可能来自从文件管理器的点击

    // 加载文件 (load file)
    if (!editorPageShowingFileIsReady.value) {  //从文件管理器跳转到editor 或 打开文件后从其他页面跳转到editor
        //准备文件路径，开始
        //优先打开从文件管理器跳转来的文件，如果不是跳转来的，打开之前显示的文件



        //到这，文件路径就确定了
        val editorPageShowingFilePath = editorPageShowingFilePath.value
        val requireOpenFilePath = editorPageShowingFilePath.ioPath
        if (requireOpenFilePath.isBlank()) {
            //这没必要清undoStack
            //这时页面会显示选择文件和打开上次文件，这里无需处理
            return
        }
        //准备文件路径，结束

        //执行到这里，一定有一个非空的文件路径




        //读取文件内容
        try {

            //reset undo stack if needed
            undoStack.reset(requireOpenFilePath, force = false)


            // reset if needed
            codeEditor.reset(FuckSafFile(AppModel.realAppContext, FilePath(requireOpenFilePath)), force = false)



            val file = FuckSafFile(activityContext, editorPageShowingFilePath)

            //如果文件修改时间和大小没变，不重新加载文件
            //x 废弃 20240503 subPage为什么要百分百重载呢？再说subPage本来就是百分百重载啊，因为一关页面再开不就重载了吗？没必要在这特意区分是否subPage！) subPage百分百重载文件；
            // 注：从Files点击百分百重载，因为请求打开文件时清了dto
            //如果打开文件没出错则检查是否已修改，否则不检查，强制重新加载
            if(hasError().not()) {
                val newDto = FileSimpleDto.genByFile(FuckSafFile(activityContext, FilePath(requireOpenFilePath)))
                val oldDto = editorPageShowingFileDto.value

                if (newDto == oldDto) {
                    MyLog.d(TAG,"EditorInnerPage#loadFile: file may not changed, skip reload, file path is '${requireOpenFilePath}'")
                    //文件可能没改变，放弃加载
                    editorPageShowingFileIsReady.value = true
                    return
                }
            }


            //清除错误信息，如果打开文件时出错，会重新设置错误信息
            editorPageClearShowingFileErrWhenLoading()


            //如果文件不存在，抛异常，然后会显示错误信息给用户
            if(!file.isActuallyReadable()) {
                //如果当前显示的内容不为空，为当前显示的内容创建个快照，然后抛异常
//                val content = editorPageTextEditorState.value.getAllText()
//                val content = null
                if(editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value) {
                    MyLog.w(TAG, "#loadFile: file doesn't exist anymore, but content is not empty, will create snapshot for content")
                    val fileName = file.name
                    val editorState = editorPageTextEditorState.value
                    doJobThenOffLoading {
                        val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                            srcFileName = fileName,
                            fileContent = null,
                            editorState = editorState,
                            trueUseContentFalseUseEditorState = false,
                            flag = SnapshotFileFlag.editor_content_FileNonExists_Backup
                        )
                        if (snapRet.hasError()) {
                            MyLog.e(TAG, "#loadFile: create content snapshot for '$requireOpenFilePath' err: ${snapRet.msg}")

                            Msg.requireShowLongDuration("save content snapshot for '$fileName' err: " + snapRet.msg)
                        }else {
                            isContentSnapshoted.value=true
                        }
                    }
                }

                //抛异常
                throw RuntimeException(activityContext.getString(R.string.err_file_doesnt_exist_anymore))
            }

            //对于saf uri这个判断并不准确，所以，不判断了，直接获取io流，若target真的不是文件的话会报错
//            if (!file.isFile) {
//                throw RuntimeException(activityContext.getString(R.string.err_target_is_not_a_file))
//            }

            //检查文件大小，太大了打开会有问题，要么崩溃，要么无法保存
            //如果文件大小超出app支持的最大限制，提示错误
            val settings = SettingsUtil.getSettingsSnapshot()
            val maxSizeLimit = settings.editor.maxFileSizeLimit
            if (isFileSizeOverLimit(file.length(), limit = maxSizeLimit)) {
//                    editorPageSetShowingFileErrWhenLoading("Err: Doesn't support open file over "+Cons.editorFileSizeMaxLimitForHumanReadable)
                throw RuntimeException(activityContext.getString(R.string.err_doesnt_support_open_file_over_limit) + "(" + getHumanReadableSizeStr(maxSizeLimit) + ")")
            }


//            if(debugModeOn) {
//                println("editorPageShowingFileDto.value.fullPath: "+editorPageShowingFileDto.value.fullPath)
//            }

            //(好像已经解决了？) TODO 其实漏了一种情况，不过不是很重要而且发生的概率几乎为0，那就是：如果用户编辑了文件但没保存，
            // 然后切换窗口，在外部修改文件，再切换回来，editor发现文件被修改过，这时会自动重载。
            // 问题就在于自动重载，应该改成询问用户是否重载，或者自动重载前先创建当前未保存内容的快照。
            // 但由于目前一切换出editor就自动保存了，
            // 所以其实不会发生“内容没保存的状态下发现文件被修改过”的情况，只会发生文件保存了，
            // 然后发现文件被外部修改过的情况，而这种情况直接重载就行，因为在之前保存的时候就已经创建了内容
            // 和当时的源文件的快照，而那个内容快照就是重载前的内容(因为保存后在editor没修改过，所以当时保存的后重载前editor显示的是同一内容)。

            MyLog.d(TAG,"EditorInnerPage#loadFile: will load file '${requireOpenFilePath}'")

            //重新读取文件把滚动位置设为null以触发定位到配置文件记录的滚动位置
            //如果打开文件报错，这几个值也该是false，所以在打开文件前就设置这几个值
            isEdited.value=false
            isSaving.value=false

            editorPageIsInitDone.value=false
            editorLastScrollEvent.value=null

            // 新打开文件，重置此值，不然会聚焦失败一次
            ignoreFocusOnce.value = false
            resetLastCursorAtColumn()

            //读取文件内容
//            editorPageTextEditorState.value = TextEditorState.create(FsUtils.readFile(requireOpenFilePath))
//                editorPageTextEditorState.value = TextEditorState.create(FsUtils.readLinesFromFile(requireOpenFilePath))
            //为新打开的文件创建全新的state
            if(soraEditorComposeTestPassed) {
//                codeEditorState!!.content.value = Content(file.bufferedReader().use { it.readText() })
            }else {
                // detect charset if need
                if (editorCharset.value == null) {
                    editorCharset.value = file.detectEncoding()
                }

                val newState = TextEditorState(
                    codeEditor = codeEditor,

                    fields = TextEditorState.fuckSafFileToFields(file, editorCharset.value),
                    isContentEdited = isEdited,
                    editorPageIsContentSnapshoted = isContentSnapshoted,
                    isMultipleSelectionMode = false,
                    focusingLineIdx = null,
                    onChanged = getEditorStateOnChange(
                        editorPageTextEditorState = editorPageTextEditorState,
                        lastSavedFieldsId = lastSavedFieldsId,
                        undoStack = undoStack,
                        resetLastCursorAtColumn = resetLastCursorAtColumn,
                    ),
//                    temporaryStyles = null,
                )

                editorPageTextEditorState.value = newState
//                lastTextEditorState.value = newState

                codeEditor.lineBreak = file.detectLineBreak(editorCharset.value)

                // syntax highlightings
                codeEditor.updatePlScopeThenAnalyze()

                // init last saved fields id
                // 刚打开文件，肯定没修改，等同于当前状态已保存
                lastSavedFieldsId.value = newState.fieldsId
            }


            isContentSnapshoted.value=false
            //文件就绪
            editorPageShowingFileIsReady.value = true
//                editorPageShowingFilePath.value = requireOpenFilePath  //左值本身就是右值，无变化，无需赋值

            //更新dto，这个dto和重载有关，和视图无关，页面是否发现它修改都无所谓，所以用更新其实也可以。
//            FileSimpleDto.updateDto(editorPageShowingFileDto.value, file)
            //这的file是只读，没改过，所以直接用file即可，若改过，我不确定是否能获取到最新修改，应该能，若没把握，可重新创建个file
            editorPageShowingFileDto.value = FileSimpleDto.genByFile(file)

            //子页面不记路径到配置文件 (20240821 废弃，原因：很多时候，我用子页面打开文件，然后我期望在首页editor用open last打开那个文件，结果没记住
//            if(!isSubPage) {
            //若不想让子页面editor记住上次打开文件路径，把更新配置文件中记录最后打开文件的代码放这里即可
//            }

            //更新最后打开文件状态变量（注：重复打开同一文件不会重复更新）
            //20240823: 改成关闭文件或销毁组件时保存了，打开时没必要保存了
//            saveLastOpenPath(requireOpenFilePath)

            // update file last used time
            FileOpenHistoryMan.touch(requireOpenFilePath)


            // preview mode maybe
            val keepPreviewStackOnce = keepPreviewStack.value
            keepPreviewStack.value = false  //不管是否保持上次的stack，都只消费此值一次，用完就重置
            val keepPreviewStack = Unit //防止后面调用此变量
            //判断是否需要生成新的预览页面导航栈
            //如果当前stack不属于当前文件 且 没请求保留当前栈，重新生成
            if(previewNavStack.value.editingPath != requireOpenFilePath && keepPreviewStackOnce.not()) {
                previewNavStack.value.reset(requireOpenFilePath)
                requirePreviewScrollToEditorCurPos.value = true
            }


            //这个路径可能在旋转屏幕后丢失，恢复下
            if(previewPath.isBlank()) {
                var newPreviewPath = previewNavStack.value.getCurrent().path
                if(newPreviewPath.isBlank()) {
                    newPreviewPath = requireOpenFilePath
                }

                updatePreviewPath(newPreviewPath)
            }




        } catch (e: Exception) {
            editorPageShowingFileIsReady.value = false
            //设置错误信息
            //显示提示
            editorPageSetShowingFileErrWhenLoading(e.localizedMessage ?: unknownErrStrRes)
            //清除配置文件中记录的当前文件编辑位置信息
            //应该提供一个选项来移除保存的最后编辑位置信息而不是一出异常就移除，万一用户只是临时把文件改下名，然后又改回来呢？或者用户手动改了权限，导致无法读取文件然后又改回来了，这些情况下位置信息就没必要删，总之删除位置信息应改成手动删除，而不是一出异常就删
//            SettingsUtil.update {
//                it.editor.filesLastEditPosition.remove(requireOpenFilePath)
//            }
            //记录日志
            MyLog.e(TAG, "EditorInnerPage#loadFile(): " + e.stackTraceToString())
        }

        //如果文件加载成功，添加它到打开的文件列表
//            if (editorPageShowingFileIsReady.value) {
//                //如果当前请求打开的文件不在编辑器的已打开文件列表，则添加
//                val openedFileMap = JSONObject(editorPageOpenedFileMap.value)
//                if (!openedFileMap.has(requireOpenFilePath)) {
//                    openedFileMap.put(
//                        requireOpenFilePath,
//                        getFileNameFromCanonicalPath(requireOpenFilePath)
//                    )
//                    editorPageRequireOpenFilePath.value = ""  //清空待打开的文件列表
//                    editorPageOpenedFileMap.value = openedFileMap.toString()  //存储当前列表到字符串
//                }
//            }

    }

//        else {  //从抽屉菜单点击Editor项进入Editor
//            //读取文件列表，展示当前打开的文件

//            //update editorOpenedFileMap from db
//            //update editorCurShowFile from db
//            //update editorPageShowingFileIsReady to true
//        }

}


@Composable
private fun getBackHandler(
    previewNavBack: ()->Unit,

    inRecentFilesPage: MutableState<Boolean>,
    recentFileListSelectionMode: MutableState<Boolean>,
    quitRecentListSelectionMode: ()->Unit,
    editorFilterRecentListOn: MutableState<Boolean>,
    editorRecentFilesQuitFilterMode:()->Unit,

    isPreviewModeOn:MutableState<Boolean>,
    quitPreviewMode:()->Unit,

    activityContext: Context,
    textEditorState: CustomStateSaveable<TextEditorState>,
    isSubPage: Boolean,
    isEdited: MutableState<Boolean>,
    readOnlyMode: MutableState<Boolean>,
//    doSaveInCoroutine: () -> Unit,
    doSaveNoCoroutine:suspend ()->Unit,
    searchMode:MutableState<Boolean>,
    needAndReadyDoSave:()->Boolean,
    naviUp:()->Unit,
    adjustFontSizeMode:MutableState<Boolean>,
    adjustLineNumFontSizeMode:MutableState<Boolean>,
    saveFontSizeAndQuitAdjust:()->Unit,
    saveLineNumFontSizeAndQuitAdjust:()->Unit,
    exitApp: () -> Unit,
    openDrawer:()->Unit,
    requestFromParent: MutableState<String>

): () -> Unit {
    val backStartSec = rememberSaveable { mutableLongStateOf(0) }
    val pressBackAgainForExitText = stringResource(R.string.press_back_again_to_exit);
    val showTextAndUpdateTimeForPressBackBtn = {
        openDrawer()
        showToast(activityContext, pressBackAgainForExitText, Toast.LENGTH_SHORT)
        backStartSec.longValue = getSecFromTime() + Cons.pressBackDoubleTimesInThisSecWillExit
    }

    val backHandlerOnBack = {
        //是多选模式则退出多选，否则检查是否编辑过文件，若编辑过则保存，然后判断是否子页面，是子页面则返回上级页面，否则显示再按返回退出的提示
        if(inRecentFilesPage.value && recentFileListSelectionMode.value) {
            quitRecentListSelectionMode()
        }else if(inRecentFilesPage.value && editorFilterRecentListOn.value) {
            editorRecentFilesQuitFilterMode()
        }else if(isPreviewModeOn.value) {
            previewNavBack()
        }else if(textEditorState.value.isMultipleSelectionMode) {  //退出编辑器多选模式
            requestFromParent.value = PageRequest.editorQuitSelectionMode
        }else if(searchMode.value){
            searchMode.value = false
        }else if(adjustFontSizeMode.value){
            saveFontSizeAndQuitAdjust()
        }else if(adjustLineNumFontSizeMode.value){
            saveLineNumFontSizeAndQuitAdjust()
        }else {  //双击返回逻辑
            doJobThenOffLoading {
                if(needAndReadyDoSave()) {  //文件编辑过，先保存，再允许退出
                    doSaveNoCoroutine()
                    //20240509: 修改成保存完文件就返回，这样就可把返回键当保存功能用了
                    return@doJobThenOffLoading
                }

                //保存文件后再按返回键则执行返回逻辑
                if(isSubPage) {  //作为子页面
                    withMainContext {
                        naviUp()
                    }
                }else {  //一级页面
                    //如果在两秒内按返回键，就会退出，否则会提示再按一次可退出程序
                    if (Cons.disableDoublePressBackToExit || (backStartSec.longValue > 0 && getSecFromTime() <= backStartSec.longValue)) {  //大于0说明不是第一次执行此方法，那检测是上次获取的秒数，否则直接显示“再按一次退出app”的提示
                        exitApp()
                    } else {
                        withMainContext {
                            showTextAndUpdateTimeForPressBackBtn()
                        }
                    }
                }
            }
        }

        Unit
    }

//    if(isSubPage) {  //作为子页面
//        backHandlerOnBack = {
//            //如果编辑过，保存然后返回上级页面，否则直接返回上级页面
//            if(textEditorState.value.isMultipleSelectionMode) {  //退出编辑器多选模式
//                textEditorState.value = textEditorState.value.createCancelledState()
//            }else {
//                doJobThenOffLoading {
//                    //如果文件编辑过，保存，然后再返回上级页面
//                    if (isEdited.value) {
//                        doSaveNoCoroutine()
//                    }
//
//                    withMainContext {
//                        naviUp()
//                    }
//                }
//            }
//        }
//    }

    return backHandlerOnBack
}
