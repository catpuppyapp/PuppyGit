package com.catpuppyapp.puppygit.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.SmallFab
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dto.FileDetail
import com.catpuppyapp.puppygit.dto.FileSimpleDto
import com.catpuppyapp.puppygit.dto.UndoStack
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.ScrollEvent
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.editor.FileDetailListActions
import com.catpuppyapp.puppygit.screen.content.homescreen.innerpage.EditorInnerPage
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions.EditorPageActions
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title.EditorTitle
import com.catpuppyapp.puppygit.screen.functions.getInitTextEditorState
import com.catpuppyapp.puppygit.screen.shared.EditorPreviewNavStack
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.cache.NaviCache
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.generateRandomString
import com.catpuppyapp.puppygit.utils.state.mutableCustomBoxOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

//for debug
private const val TAG = "SubPageEditor"


//子页面版本editor
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPageEditor(
//    context: Context,
//    navController: NavController,
//    drawerState: DrawerState,
//    scope: CoroutineScope,
//    scrollBehavior: TopAppBarScrollBehavior,
//    currentPage: MutableIntState,
//    repoPageListState: LazyListState,
//    filePageListState: LazyListState,
//    haptic: HapticFeedback,
    goToLine:Int,  //大于0，打开文件定位到对应行，小于0，打开文件定位上次编辑行（之前的逻辑不变
    initMergeMode:Boolean,
    initReadOnly:Boolean,
    editorPageLastFilePath:MutableState<String>,
    filePathKey:String,
    naviUp:()->Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)




    val navController = AppModel.navController
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
//    val appContext = AppModel.appContext  //这个获取不了Activity!
    val activityContext = LocalContext.current  //这个能获取到
    val scope = rememberCoroutineScope()
    val inDarkTheme = Theme.inDarkTheme

//    val allRepoParentDir = AppModel.allRepoParentDir
    val settings = remember { SettingsUtil.getSettingsSnapshot() }

//    val changeListRefreshRequiredByParentPage= StateUtil.getRememberSaveableState(initValue = "")
//    val changeListRequireRefreshFromParentPage = {
//        //TODO 显示个loading遮罩啥的
//        changeStateTriggerRefreshPage(changeListRefreshRequiredByParentPage)
//    }
//    val changeListCurRepo = rememberSaveable{ mutableStateOf(RepoEntity()) }


    // canonicalPath
//    val editorPageRequireOpenFilePath = StateUtil.getRememberSaveableState(initValue = (Cache.getByType<String>(filePathKey))?:"")
//    val needRefreshFilesPage = rememberSaveable { mutableStateOf(false) }

    //这个变量有在Activity销毁时保存其值的机制，所以仅需从Cache获取一次，后续不管恢复成功还是失败都无所谓，成功就用它的值，失败就用Activity销毁时保存的值。
    // 注意：这里用getByTypeThenDel()是对的，因为此变量有特殊处理，在Activity销毁时会保存其值，其他页面state变量，如果没特殊处理，应该使用get而不是getThenDel以避免rememberSaveable发生恢复状态变量失败的bug时app报错
    // 注意：虽然rememberSaveable有bug(有时无法在旋转屏幕或其他显示配置改变后正常恢复页面的state变量值)，但就算就算此值在文件打开时恢复失败变成空字符串也无所谓，因为在Activity销毁时保存其值的变量仍会存储最后打开文件路径。
    val editorPageShowingFilePath = rememberSaveable { mutableStateOf(FilePath(NaviCache.getByType<String>(filePathKey) ?: ""))} //当前展示的文件的canonicalPath
    val editorPageShowingFileIsReady = rememberSaveable { mutableStateOf(false)} //当前展示的文件是否已经加载完毕

    val editorPageIsEdited = rememberSaveable { mutableStateOf(false)}
    val editorPageIsContentSnapshoted = rememberSaveable{mutableStateOf(false)}  //是否已对当前内容创建了快照

    //TextEditor用的变量
    val editorPageTextEditorState = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "editorPageTextEditorState",
        initValue = getInitTextEditorState()
    )
//    val editorPageLastTextEditorState = mutableCustomStateOf(
//        keyTag = stateKeyTag,
//        keyName = "editorPageLastTextEditorState",
//        initValue = getInitTextEditorState()
//    )
    val needRefreshEditorPage = rememberSaveable { mutableStateOf("")}
    val editorPageIsSaving = rememberSaveable { mutableStateOf(false)}
    val showReloadDialog = rememberSaveable { mutableStateOf(false)}
    val editorPageShowingFileDto = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageShowingFileDto",FileSimpleDto() )
    val editorPageSnapshotedFileInfo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageSnapshotedFileInfo",FileSimpleDto() )



    val editorShowUndoRedo = rememberSaveable{mutableStateOf(settings.editor.showUndoRedo)}
//    val editorUndoStack = remember(editorPageShowingFilePath.value){ derivedStateOf { UndoStack(filePath = editorPageShowingFilePath.value.ioPath) } }
//    val editorUndoStack = remember { SharedState.subEditorUndoStack }
    val editorUndoStack = mutableCustomStateOf(stateKeyTag, "editorUndoStack") { UndoStack("") }

    // null to auto detect file encoding
    val editorCharset = rememberSaveable { mutableStateOf<String?>(null) }
    val editorPlScope = rememberSaveable { mutableStateOf(PLScope.AUTO) }
    val codeEditor = mutableCustomStateOf(stateKeyTag, "codeEditor") {
        MyCodeEditor(
            editorState = editorPageTextEditorState,
            undoStack = editorUndoStack,
            plScope = editorPlScope,
            editorCharset = editorCharset,
        )
    }


    val naviUp = {
        codeEditor.value.releaseAndClearUndoStack()
        naviUp()
    }


    val editorPageLastScrollEvent = mutableCustomStateOf<ScrollEvent?>(stateKeyTag, "editorPageLastScrollEvent") { null }  //这个用remember就行，没必要在显示配置改变时还保留这个滚动状态，如果显示配置改变，直接设为null，从配置文件读取滚动位置重定位更好
    val editorPageLazyListState = rememberLazyListState()
    val editorPageIsInitDone = rememberSaveable{mutableStateOf(false)}  //这个也用remember就行，无需在配置改变时保存此状态，直接重置成false就行
    val editorPageSearchMode = rememberSaveable{mutableStateOf(false)}
    val editorPageSearchKeyword = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "editorPageSearchKeyword", TextFieldValue("") )
    val editorReadOnlyMode = rememberSaveable{mutableStateOf(initReadOnly)}

    //如果用户pro且功能测试通过，允许使用url传来的初始值，否则一律false
    val editorPageMergeMode = rememberSaveable{mutableStateOf(initMergeMode)}
    val editorPagePatchMode = rememberSaveable(settings.editor.patchModeOn) { mutableStateOf(settings.editor.patchModeOn) }

    val editorPageRequestFromParent = rememberSaveable { mutableStateOf("")}


    val requireEditorScrollToPreviewCurPos = rememberSaveable { mutableStateOf(false) }
    val requirePreviewScrollToEditorCurPos = rememberSaveable { mutableStateOf(false) }
    val editorPreviewPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val editorPreviewLastScrollPosition = rememberSaveable { mutableStateOf(0) }

    val editorIsPreviewModeOn = rememberSaveable { mutableStateOf(false) }
    val editorMdText = rememberSaveable { mutableStateOf("") }
    val editorBasePath = rememberSaveable { mutableStateOf("") }
    val (editorPreviewPath, updatePreviewPath_Internal) = rememberSaveable { mutableStateOf("") }
    val editorPreviewPathChanged = rememberSaveable { mutableStateOf("") }  //由于有可能重复入栈相同路径，例如从a->a，这时previewPath不会变化，导致其关联的检测当前页面是否是home页面的代码也不会被触发，所以，在修改previewPath的地方，同时给这个变量赋值随机数以触发页面刷新
    val updatePreviewPath = { newPath:String ->
        updatePreviewPath_Internal(newPath)
        editorPreviewPathChanged.value = generateRandomString()
    }

    val editorPreviewNavStack = mutableCustomStateOf(stateKeyTag, "editorPreviewNavStack") { EditorPreviewNavStack("") }
    val editorPagePreviewLoading = rememberSaveable { mutableStateOf(false) }

    val editorQuitPreviewModeWithoutReloadCheck = {
        editorBasePath.value = ""
        editorMdText.value = ""
        editorIsPreviewModeOn.value = false
    }

    val editorQuitPreviewMode = {
        editorQuitPreviewModeWithoutReloadCheck()

        editorPageRequestFromParent.value = PageRequest.reloadIfChanged
    }

    val editorInitPreviewMode = {
        //请求执行一次保存，不然有可能切换
        editorPageRequestFromParent.value = PageRequest.requireInitPreviewFromSubEditor
    }

    val editorPreviewFileDto = mutableCustomStateOf(stateKeyTag, "editorPreviewFileDto") { FileSimpleDto() }

    val editorDisableSoftKb = rememberSaveable { mutableStateOf(settings.editor.disableSoftwareKeyboard) }


    val editorRecentFileList = mutableCustomStateListOf(stateKeyTag, "recentFileList") { listOf<FileDetail>() }
    val editorSelectedRecentFileList = mutableCustomStateListOf(stateKeyTag, "editorSelectedRecentFileList") { listOf<FileDetail>() }
    val editorRecentFileListSelectionMode = rememberSaveable { mutableStateOf(false) }
    val editorRecentListState = rememberLazyStaggeredGridState()
    val editorInRecentFilesPage = rememberSaveable { mutableStateOf(false) }


    //初始值不用忽略，因为打开文件后默认focusing line idx为null，所以这个值是否忽略并没意义
    //这个值不能用state，不然修改state后会重组，然后又触发聚焦，就没意义了
    val ignoreFocusOnce = rememberSaveable { mutableStateOf(false) }
//    val softKbVisibleWhenLeavingEditor = rememberSaveable { mutableStateOf(false) }


    val settingsTmp = settings  //之前在这重新获取了一个，后来发现没必要，为避免改变量名，这直接赋值算了
    val editorShowLineNum = rememberSaveable{mutableStateOf(settingsTmp.editor.showLineNum)}
    val editorLineNumFontSize = rememberSaveable { mutableIntStateOf(settingsTmp.editor.lineNumFontSize)}
    val editorFontSize = rememberSaveable { mutableIntStateOf(settingsTmp.editor.fontSize)}
    val editorAdjustFontSizeMode = rememberSaveable{mutableStateOf(false)}
    val editorAdjustLineNumFontSizeMode = rememberSaveable{mutableStateOf(false)}
    val editorLastSavedLineNumFontSize = rememberSaveable { mutableIntStateOf(editorLineNumFontSize.intValue) } //用来检查，如果没变，就不执行保存，避免写入硬盘
    val editorLastSavedFontSize = rememberSaveable { mutableIntStateOf(editorFontSize.intValue)}
    val editorOpenFileErr = rememberSaveable{mutableStateOf(false)}


    val editorLoadLock = mutableCustomBoxOf(stateKeyTag, "editorLoadLock") { Mutex() }.value


    val showCloseDialog = rememberSaveable { mutableStateOf(false)}

    val closeDialogCallback = mutableCustomStateOf<(Boolean)->Unit>(
        keyTag = stateKeyTag,
        keyName = "closeDialogCallback",
        initValue = { requireSave:Boolean -> Unit}
    )

    val initLoadingText = activityContext.getString(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(initLoadingText)}
    val isLoading = rememberSaveable { mutableStateOf(false)}

    val loadingOn = {msg:String ->
        loadingText.value=msg
        isLoading.value=true
//        Msg.requireShow(msg)
//        changeStateTriggerRefreshPage(needRefreshEditorPage)
    }
    val loadingOff = {
        isLoading.value=false
        loadingText.value=initLoadingText
//        changeStateTriggerRefreshPage(needRefreshEditorPage)
    }

    val lastSavedFieldsId = rememberSaveable { mutableStateOf("") }

    val doSave:suspend ()->Unit = FsUtils.getDoSaveForEditor(
        editorPageShowingFilePath = editorPageShowingFilePath,
        editorPageLoadingOn = loadingOn,
        editorPageLoadingOff = loadingOff,
        activityContext = activityContext,
        editorPageIsSaving = editorPageIsSaving,
        needRefreshEditorPage = needRefreshEditorPage,
        editorPageTextEditorState = editorPageTextEditorState,
        pageTag = TAG,
        editorPageIsEdited = editorPageIsEdited,
        requestFromParent = editorPageRequestFromParent,
        editorPageFileDto = editorPageShowingFileDto,
        isSubPageMode = true,
        isContentSnapshoted =editorPageIsContentSnapshoted,
        snapshotedFileInfo = editorPageSnapshotedFileInfo,
        lastSavedFieldsId = lastSavedFieldsId,
    )



    val editorFilterRecentListState = rememberLazyStaggeredGridState()
    val editorFilterRecentList = mutableCustomStateListOf(stateKeyTag, "editorFilterRecentList") { listOf<FileDetail>() }
    val editorFilterRecentListOn = rememberSaveable { mutableStateOf(false) }
    val editorEnableRecentListFilter = rememberSaveable { mutableStateOf(false) }
    val editorFilterRecentListKeyword = mutableCustomStateOf(stateKeyTag, "editorFilterRecentListKeyword") { TextFieldValue("") }
    val editorFilterRecentListLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val editorFilterRecentListResultNeedRefresh = rememberSaveable { mutableStateOf("") }
    val editorFilterRecentListSearching = rememberSaveable { mutableStateOf(false) }
    val editorFilterRecentListSearchToken = rememberSaveable { mutableStateOf("") }
    val editorFilterResetSearchValues = {
        editorFilterRecentListSearching.value = false
        editorFilterRecentListSearchToken.value = ""
        editorFilterRecentListLastSearchKeyword.value = ""
    }

    val editorInitRecentFilesFilterMode = {
        editorFilterRecentListKeyword.value = TextFieldValue("")
        editorFilterRecentListOn.value = true
    }
    val editorRecentFilesQuitFilterMode = {
        editorFilterResetSearchValues()
        editorFilterRecentListOn.value = false
    }


    val editorRecentListLastScrollPosition = rememberSaveable { mutableStateOf(0) }
    val editorRecentListFilterLastScrollPosition = rememberSaveable { mutableStateOf(0) }

    val getActuallyRecentFilesList = {
        if(editorEnableRecentListFilter.value) editorFilterRecentList.value else editorRecentFileList.value
    }
    val getActuallyRecentFilesListState = {
        if(editorEnableRecentListFilter.value) editorFilterRecentListState else editorRecentListState
    }
    val getActuallyRecentFilesListLastPosition = {
        if(editorEnableRecentListFilter.value) editorRecentListFilterLastScrollPosition else editorRecentListLastScrollPosition
    }

    val editorRecentListScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }

    val editorNeedSave = { editorPageShowingFileIsReady.value && editorPageIsEdited.value && !editorPageIsSaving.value && !editorReadOnlyMode.value && lastSavedFieldsId.value != editorPageTextEditorState.value.fieldsId }


    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    //这页面不用来打开外部文件，正常来说不会出现file uri，不过虽然可以通过最近文件列表打开一个uri路径，但这个处理起来有点繁琐，算了，不管了，又不是不能用

                    EditorTitle(
                        disableSoftKb = editorDisableSoftKb,

                        recentFileListIsEmpty = editorRecentFileList.value.isEmpty(),
                        recentFileListFilterModeOn = editorFilterRecentListOn.value,
                        recentListFilterKeyword = editorFilterRecentListKeyword,
                        getActuallyRecentFilesListState = getActuallyRecentFilesListState,
                        getActuallyRecentFilesListLastPosition = getActuallyRecentFilesListLastPosition,

                        patchModeOn = editorPagePatchMode,
                        previewNavStack = editorPreviewNavStack.value,
                        previewingPath = editorPreviewPath,
                        isPreviewModeOn = editorIsPreviewModeOn.value,
                        previewLastScrollPosition = editorPreviewLastScrollPosition,
                        scope = scope,
                        editorPageShowingFilePath = editorPageShowingFilePath,
                        editorPageRequestFromParent = editorPageRequestFromParent,
                        editorSearchMode = editorPageSearchMode.value,
                        editorSearchKeyword = editorPageSearchKeyword,
                        editorPageMergeMode = editorPageMergeMode,
                        readOnly = editorReadOnlyMode,

                        editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                        isSaving = editorPageIsSaving,
                        isEdited = editorPageIsEdited,
                        showReloadDialog = showReloadDialog,
                        showCloseDialog = showCloseDialog,
                        editorPageTextEditorState = editorPageTextEditorState,
                        editorNeedSave = editorNeedSave,
                    )

                },
                navigationIcon = {
                    if(editorIsPreviewModeOn.value || editorPageSearchMode.value
                        || editorAdjustFontSizeMode.value || editorAdjustLineNumFontSizeMode.value
                        || (editorInRecentFilesPage.value && editorFilterRecentListOn.value)
                    ) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),
                        ) {
                            if(editorIsPreviewModeOn.value) {
                                editorQuitPreviewMode()
                            }else if(editorPageSearchMode.value){
                                editorPageSearchMode.value = false
                            }else if(editorAdjustFontSizeMode.value) {
                                editorPageRequestFromParent.value = PageRequest.requireSaveFontSizeAndQuitAdjust
                            }else if(editorAdjustLineNumFontSizeMode.value) {
                                editorPageRequestFromParent.value = PageRequest.requireSaveLineNumFontSizeAndQuitAdjust
                            }else if(editorInRecentFilesPage.value && editorFilterRecentListOn.value) {
                                editorRecentFilesQuitFilterMode()
                            }
                        }
                    }else {
                        val (tooltipText, icon, iconContentDesc) = if(editorNeedSave()) {
                            Triple(stringResource(R.string.save), Icons.Filled.Save, stringResource(R.string.save))
                        }else {
                            Triple(stringResource(R.string.back), Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }

                        LongPressAbleIconBtn(
                            tooltipText = tooltipText,
                            icon = icon,
                            iconContentDesc = iconContentDesc,
                        ) {
                            doJobThenOffLoading {
                                //未保存，先保存，再点击，再返回
                                // save first, then press again to navi back
                                if(editorNeedSave()) {
                                    editorPageRequestFromParent.value = PageRequest.requireSave
                                    return@doJobThenOffLoading
                                }

                                //navi back
                                withContext(Dispatchers.Main) {
                                    naviUp()
                                }

//                            changeStateTriggerRefreshPage(needRefreshEditorPage)  //都离开页面了，刷新鸡毛啊

                            }
                        }
                    }

                },
                actions = {
                    if(!editorOpenFileErr.value) {
                        val notOpenFile = !editorPageShowingFileIsReady.value && editorPageShowingFilePath.value.isBlank()

                        if(notOpenFile && editorRecentFileList.value.isNotEmpty()) {
                            FileDetailListActions(
                                request = editorPageRequestFromParent,
                                filterModeOn = editorFilterRecentListOn.value,

                                initFilterMode = editorInitRecentFilesFilterMode,
                            )
                        }else  {
                            EditorPageActions(
                                disableSoftKb = editorDisableSoftKb,
                                requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
                                initPreviewMode = editorInitPreviewMode,
                                previewNavStack = editorPreviewNavStack.value,
                                previewPath = editorPreviewPath,
                                previewPathChanged = editorPreviewPathChanged.value,
                                isPreviewModeOn = editorIsPreviewModeOn.value,
                                editorPageShowingFilePath = editorPageShowingFilePath,
                                //                        editorPageRequireOpenFilePath,
                                editorPageShowingFileIsReady = editorPageShowingFileIsReady,
                                needRefreshEditorPage = needRefreshEditorPage,
                                editorPageTextEditorState = editorPageTextEditorState,
                                //                        editorPageShowSaveDoneToast,
                                isSaving = editorPageIsSaving,
                                isEdited = editorPageIsEdited,
                                showReloadDialog = showReloadDialog,
                                showCloseDialog = showCloseDialog,
                                closeDialogCallback=closeDialogCallback,
                                //                        isLoading = isLoading,
                                doSave = doSave,
                                loadingOn = loadingOn,
                                loadingOff = loadingOff,
                                editorPageRequest = editorPageRequestFromParent,
                                editorPageSearchMode = editorPageSearchMode,
                                editorPageMergeMode=editorPageMergeMode,
                                editorPagePatchMode=editorPagePatchMode,
                                readOnlyMode = editorReadOnlyMode,
                                editorSearchKeyword = editorPageSearchKeyword.value.text,
                                isSubPageMode=true,

                                fontSize=editorFontSize,
                                lineNumFontSize=editorLineNumFontSize,
                                adjustFontSizeMode=editorAdjustFontSizeMode,
                                adjustLineNumFontSizeMode=editorAdjustLineNumFontSizeMode,
                                showLineNum = editorShowLineNum,
                                undoStack = editorUndoStack.value,
                                showUndoRedo = editorShowUndoRedo,

                                editorNeedSave = editorNeedSave,

                            )
                        }
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(editorIsPreviewModeOn.value && editorPreviewPageScrolled.value) {
                GoToTopAndGoToBottomFab(
                    scope = scope,
                    listState = runBlocking { editorPreviewNavStack.value.getCurrentScrollState() },
                    listLastPosition = editorPreviewLastScrollPosition,
                    showFab = editorPreviewPageScrolled
                )
            }else if(editorNeedSave()) {
                SmallFab(
                    modifier= MyStyleKt.Fab.getFabModifierForEditor(editorPageTextEditorState.value.isMultipleSelectionMode, UIHelper.isPortrait()),
                    icon = Icons.Filled.Save, iconDesc = stringResource(id = R.string.save)
                ) {
                    editorPageRequestFromParent.value = PageRequest.requireSave
                }
            }else if(editorInRecentFilesPage.value && editorRecentListScrolled.value) {
                GoToTopAndGoToBottomFab(
                    filterModeOn = editorEnableRecentListFilter.value,
                    scope = scope,
                    filterListState = editorFilterRecentListState,
                    listState = editorRecentListState,
                    filterListLastPosition = editorRecentListFilterLastScrollPosition,
                    listLastPosition = editorRecentListLastScrollPosition,
                    showFab = editorRecentListScrolled,
                    listSize = getActuallyRecentFilesList().size,
                )
            }
        }
    ) { contentPadding ->


//        if(isLoading.value || editorPagePreviewLoading.value) {
//            LoadingDialog(
//                // edit mode可能会设loading text，例如正在保存之类的；preview直接显示个普通的loading文案就行
//                if(isLoading.value) loadingText.value else stringResource(R.string.loading)
//            )
//        }

        EditorInnerPage(
//            stateKeyTag = Cache.combineKeys(stateKeyTag, "EditorInnerPage"),
            stateKeyTag = stateKeyTag,

            editorCharset = editorCharset,

            lastSavedFieldsId = lastSavedFieldsId,

            codeEditor = codeEditor,
            plScope = editorPlScope,

            disableSoftKb = editorDisableSoftKb,
            editorRecentListScrolled = editorRecentListScrolled,
            recentFileList = editorRecentFileList,
            selectedRecentFileList = editorSelectedRecentFileList,
            recentFileListSelectionMode = editorRecentFileListSelectionMode,
            recentListState = editorRecentListState,
            inRecentFilesPage = editorInRecentFilesPage,

            editorFilterRecentListState = editorFilterRecentListState,
            editorFilterRecentList = editorFilterRecentList.value,
            editorFilterRecentListOn = editorFilterRecentListOn,
            editorEnableRecentListFilter = editorEnableRecentListFilter,
            editorFilterRecentListKeyword = editorFilterRecentListKeyword,
            editorFilterRecentListLastSearchKeyword = editorFilterRecentListLastSearchKeyword,
            editorFilterRecentListResultNeedRefresh = editorFilterRecentListResultNeedRefresh,
            editorFilterRecentListSearching = editorFilterRecentListSearching,
            editorFilterRecentListSearchToken = editorFilterRecentListSearchToken,
            editorFilterResetSearchValues = editorFilterResetSearchValues,
            editorRecentFilesQuitFilterMode = editorRecentFilesQuitFilterMode,

            ignoreFocusOnce = ignoreFocusOnce,
//            softKbVisibleWhenLeavingEditor = softKbVisibleWhenLeavingEditor,

            previewLoading = editorPagePreviewLoading,
            editorPreviewFileDto = editorPreviewFileDto,
            requireEditorScrollToPreviewCurPos = requireEditorScrollToPreviewCurPos,
            requirePreviewScrollToEditorCurPos = requirePreviewScrollToEditorCurPos,
            previewPageScrolled = editorPreviewPageScrolled,
            previewPath = editorPreviewPath,
            updatePreviewPath = updatePreviewPath,
            previewNavStack = editorPreviewNavStack,
            isPreviewModeOn = editorIsPreviewModeOn,
            mdText = editorMdText,
            basePath = editorBasePath,
            quitPreviewMode = editorQuitPreviewMode,
            initPreviewMode = editorInitPreviewMode,

            contentPadding = contentPadding,

            //editor作为子页面时其实不需要这个变量，只是调用的组件需要，又没默认值，所以姑且创建一个
            currentHomeScreen = remember { mutableIntStateOf(Cons.selectedItem_Repos)},

//            editorPageRequireOpenFilePath=editorPageRequireOpenFilePath,
            editorPageShowingFilePath=editorPageShowingFilePath,
            editorPageShowingFileIsReady=editorPageShowingFileIsReady,
            editorPageTextEditorState=editorPageTextEditorState,
//            lastTextEditorState=editorPageLastTextEditorState,
//            editorPageShowSaveDoneToast=editorPageShowSaveDoneToast,
            needRefreshEditorPage=needRefreshEditorPage,
            isSaving = editorPageIsSaving,
            isEdited = editorPageIsEdited,
            showReloadDialog=showReloadDialog,
            isSubPageMode=true,
            showCloseDialog=showCloseDialog,
            closeDialogCallback=closeDialogCallback,
//            isLoading = isLoading,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            saveOnDispose = false,  //这时父页面（当前页面）负责保存，不需要子页面保存，所以传false
            doSave = doSave,  //doSave还是要传的，虽然销毁组件时的保存关闭了，但是返回时的保存依然要用doSave
            naviUp=naviUp,
            requestFromParent = editorPageRequestFromParent,
            editorPageShowingFileDto = editorPageShowingFileDto,
            lastFilePath = editorPageLastFilePath,
            editorLastScrollEvent = editorPageLastScrollEvent,
            editorListState = editorPageLazyListState,
            editorPageIsInitDone = editorPageIsInitDone,
            editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
            goToFilesPage = {},  //子页面不支持在Files显示文件，所以传空函数即可，应该由子页面的调用者(应该是个顶级页面)来实现在Files页面显示文件，子页面只负责编辑（暂时先这样，实际上就算想支持也不行，因为子页面无法跳转到顶级的Files页面）
            goToLine=goToLine,
            editorSearchMode = editorPageSearchMode,
            editorSearchKeyword = editorPageSearchKeyword,
            readOnlyMode = editorReadOnlyMode,
            editorMergeMode = editorPageMergeMode,
            editorPatchMode = editorPagePatchMode,

            editorShowLineNum=editorShowLineNum,
            editorLineNumFontSize=editorLineNumFontSize,
            editorFontSize=editorFontSize,

            editorAdjustLineNumFontSizeMode = editorAdjustLineNumFontSizeMode,
            editorAdjustFontSizeMode = editorAdjustFontSizeMode,

            editorLastSavedLineNumFontSize = editorLastSavedLineNumFontSize,
            editorLastSavedFontSize = editorLastSavedFontSize,
            openDrawer = {}, //非顶级页面按返回键不需要打开抽屉
            editorOpenFileErr = editorOpenFileErr,
            undoStack = editorUndoStack.value,
            loadLock = editorLoadLock

        )
    }





}



