package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.catpuppyapp.puppygit.compose.ApplyPatchDialog
import com.catpuppyapp.puppygit.compose.BottomBar
import com.catpuppyapp.puppygit.compose.CardButton
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyScrollableColumn
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CreateFileOrFolderDialog2
import com.catpuppyapp.puppygit.compose.DefaultPaddingText
import com.catpuppyapp.puppygit.compose.GitIgnoreDialog
import com.catpuppyapp.puppygit.compose.GrantManageStoragePermissionClickableText
import com.catpuppyapp.puppygit.compose.LoadingTextCancellable
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.compose.MyCheckBox2
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.OpenAsDialog
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.SingleLineCardButton
import com.catpuppyapp.puppygit.compose.SingleSelection
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.DevFeature
import com.catpuppyapp.puppygit.dev.applyPatchTestPassed
import com.catpuppyapp.puppygit.dev.importReposFromFilesTestPassed
import com.catpuppyapp.puppygit.dev.initRepoFromFilesPageTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dto.FileItemDto
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.IgnoreItem
import com.catpuppyapp.puppygit.git.ImportRepoResult
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.FileListItem
import com.catpuppyapp.puppygit.screen.content.listitem.SelectedFileItemsDialog
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.goToFileHistory
import com.catpuppyapp.puppygit.screen.functions.initSearch
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.FileChooserType
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.DirViewAndSort
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.settings.enums.dirviewandsort.SortMethod
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.DirSearchUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.FsUtils.PasteResult
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.RegexUtil
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.cache.ThumbCache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.checkFileOrFolderNameAndTryCreateFile
import com.catpuppyapp.puppygit.utils.compareStringAsNumIfPossible
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJob
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.filterAndMap
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.getFileExtOrEmpty
import com.catpuppyapp.puppygit.utils.getFileNameFromCanonicalPath
import com.catpuppyapp.puppygit.utils.getFileNameOrEmpty
import com.catpuppyapp.puppygit.utils.getFilePathUnderParent
import com.catpuppyapp.puppygit.utils.getHumanReadableSizeStr
import com.catpuppyapp.puppygit.utils.getRangeForRenameFile
import com.catpuppyapp.puppygit.utils.getRequestDataByState
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.catpuppyapp.puppygit.utils.getViewAndSortForPath
import com.catpuppyapp.puppygit.utils.isPathExists
import com.catpuppyapp.puppygit.utils.mime.MimeType
import com.catpuppyapp.puppygit.utils.mime.guessFromFileName
import com.catpuppyapp.puppygit.utils.mime.intentType
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.requestStoragePermissionIfNeed
import com.catpuppyapp.puppygit.utils.saf.MyOpenDocumentTree
import com.catpuppyapp.puppygit.utils.saf.SafAndFileCmpUtil
import com.catpuppyapp.puppygit.utils.saf.SafAndFileCmpUtil.OpenInputStreamFailed
import com.catpuppyapp.puppygit.utils.saf.SafUtil
import com.catpuppyapp.puppygit.utils.showToast
import com.catpuppyapp.puppygit.utils.state.CustomStateListSaveable
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.state.mutableCustomBoxOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.storagepaths.StoragePaths
import com.catpuppyapp.puppygit.utils.storagepaths.StoragePathsMan
import com.catpuppyapp.puppygit.utils.trimLineBreak
import com.catpuppyapp.puppygit.utils.withMainContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.File
import kotlin.coroutines.cancellation.CancellationException


private const val TAG = "FilesInnerPage"

//是否在底栏显示导入按钮，没必要，不用显示，导入是针对当前文件夹的，应该在顶栏显示
//避免以后修改，保留代码，用开关变量控制是否显示
private const val showImportForBottomBar = false





@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesInnerPage(
    stateKeyTag:String,

    errScrollState: ScrollState,
    getErr: ()->String,
    setErr: (String)->Unit,
    hasErr:()->Boolean,  // 让父组件知道是否出错了，以此来决定go to top/bottom操作哪个listState

    naviUp:()->Unit,
    updateSelectedPath:(path:String) -> Unit,
    isFileChooser:Boolean,
    fileChooserType:FileChooserType,  //若 `isFileChooser` 为假则此值无效
    contentPadding: PaddingValues,
//    filePageListState: LazyListState,
    currentHomeScreen: MutableIntState,
    editorPageShowingFilePath: MutableState<FilePath>,
    editorPageShowingFileIsReady: MutableState<Boolean>,
    needRefreshFilesPage: MutableState<String>,
    currentPath: ()->String,
    updateCurrentPath:(String)->Unit,
    showCreateFileOrFolderDialog: MutableState<Boolean>,
    requireImportFile: MutableState<Boolean>,
    requireImportUriList: CustomStateListSaveable<Uri>,
    filesPageGetFilterMode:()->Int,
    filesPageFilterKeyword:CustomStateSaveable<TextFieldValue>,
    filesPageFilterModeOff:()->Unit,
    currentPathFileList:CustomStateListSaveable<FileItemDto>,
    filesPageRequestFromParent:MutableState<String>,
    requireInnerEditorOpenFile:(filePath:String, expectReadOnly:Boolean)->Unit,
    filesPageSimpleFilterOn:MutableState<Boolean>,
    filesPageSimpleFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    filesPageLastKeyword:MutableState<String>,
    filesPageSearchToken:MutableState<String>,
    filesPageSearching:MutableState<Boolean>,
    resetFilesSearchVars:()->Unit,

    filesPageScrolled:MutableState<Boolean>,
    curListState:CustomStateSaveable<LazyListState>,
    filterListState:LazyListState,

    openDrawer:()->Unit,
    isFileSelectionMode:MutableState<Boolean>,
    isPasteMode:MutableState<Boolean>,
    selectedItems:CustomStateListSaveable<FileItemDto>,
    checkOnly:MutableState<Boolean>,
    selectedRepo:CustomStateSaveable<RepoEntity>,

    goToRepoPage:(targetIdIfHave:String)->Unit,
    goToChangeListPage:(repoWillShowInChangeListPage:RepoEntity)->Unit,
    lastPathByPressBack: MutableState<String>,
    curPathFileItemDto:CustomStateSaveable<FileItemDto>,
    currentPathBreadCrumbList:CustomStateListSaveable<FileItemDto>,
    enableFilterState:MutableState<Boolean>,
    filterList:CustomStateListSaveable<FileItemDto>,
    lastPosition:MutableState<Int>,
    keepFilterResultOnce:MutableState<Boolean>,
    goToPath:(String)->Unit,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val inDarkTheme = Theme.inDarkTheme

    val allRepoParentDir = AppModel.allRepoParentDir;
//    val appContext = AppModel.appContext;
    val activityContext = LocalContext.current;
    val exitApp = AppModel.exitApp
    val haptic = LocalHapticFeedback.current

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current


    val settingsSnapshot = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "settingsSnapshot") {
        val s = SettingsUtil.getSettingsSnapshot()

        //实际上filesPageScrolled已经是是否显示navi buttons的开关了，变量名是历史遗留问题，因为最初是只有滚动屏幕才显示导航按钮
        filesPageScrolled.value = s.showNaviButtons
        s
    }

    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }


    //文件管理器三个点的菜单项
//    val menuKeyRename = "rename"
//    val menuValueRename = stringResource(R.string.rename)
//    val menuKeyInfo = "info"
//    val menuValueInfo = stringResource(R.string.info)
//    val fileMenuMap = remember{ mutableMapOf<String, String>(
//        menuKeyRename to menuValueRename,
//        menuKeyInfo to menuValueInfo,
//    ) }
//    val filesPageSelectionBarHeight = 60.dp
//    val filesPageSelectionBarBackgroundColor = MaterialTheme.colorScheme.primaryContainer


//    val selFilePathListJsonObjStr = rememberSaveable{ mutableStateOf("{}") }  //key是文件名，所以这个列表只能存储相同目录下的文件，不同目录有可能名称冲突，但由于选择模式只能在当前目录选择，所以这个缺陷可以接受。json格式:{fileName:canonicalPath}
//    val opType = remember{ mutableStateOf("") }
//    val opCodeMove = "mv"
//    val opCodeCopy = "cp"
//    val opCodeDelete = "del"
////    val selFilePathListJsonObj = JSONObject()  //想着复用同一个对象呢，不过没那个api，看下如果性能还行，就这样吧，不行的话，换别的json库

    val fileAlreadyExistStrRes = stringResource(R.string.file_already_exists)
    val successStrRes = stringResource(R.string.success)
    val errorStrRes = stringResource(R.string.error)


    val defaultLoadingText = activityContext.getString(R.string.loading)
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText) }
    val loadingOn = { msg: String ->
        loadingText.value = msg
        isLoading.value = true
    }
    val loadingOff = {
        isLoading.value = false
        loadingText.value = defaultLoadingText
    }


    // 取消任务相关变量，开始
    val requireCancelAct = rememberSaveable { mutableStateOf(false) }
    val cancellableActRunning = rememberSaveable { mutableStateOf(false) }
    val resetAct = {
        requireCancelAct.value = false
    }

    val cancelAct = {
        requireCancelAct.value = true
    }

    val startCancellableAct = {
        resetAct()
        cancellableActRunning.value = true
    }

    val stopCancellableAct = {
        cancellableActRunning.value = false
        resetAct()
    }
    //取消任务相关变量，结束

    //这个cancel变量必须确保及时更新，而我发现如果在非主线程更新可能获取到旧值，所以改成强制在主线程更新
    val loadingOnCancellable: suspend (String) -> Unit = { loadingText: String ->
        withMainContext {
            loadingOn(loadingText)
            // reset，使其可取消
            startCancellableAct()
        }
    }
    val loadingOffCancellable: suspend () -> Unit = {
        withMainContext {
            loadingOff()
            // reset，使其他可取消任务任务能正常开始执行
            stopCancellableAct()
        }
    }

//    val repoList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "repoList", initValue = listOf<RepoEntity>())


//    val currentPathBreadCrumbList = remember{ mutableStateListOf<FileItemDto>() }

    //实现了点击更新list并且避免并发修改异常，但我发现，只要每次在切换路径后重新生成一下面包屑就行了，虽然代码执行起来可能有点麻烦，效率可能差一点点，但是逻辑更简单而且，总之没必要整这么麻烦，废弃这个方案了
//    val currentPathBreadCrumbList = remember{ mutableIntStateOf(1) }  //为0时刚进页面，初始化，后续为1时读取list1修改list2，为2时读取list2修改list1，避免并发修改异常
//    val currentPathBreadCrumbList1 = remember{ mutableStateListOf<FileItemDto>() }  // key 1
//    val currentPathBreadCrumbList2 = remember{ mutableStateListOf<FileItemDto>() }  // key 2


    val containsForSelected = { srcList: List<FileItemDto>, item: FileItemDto ->
        srcList.indexOfFirst { it.equalsForSelected(item) } != -1
    }

    val filesPageQuitSelectionMode = {
        isFileSelectionMode.value = false  //关闭选择模式
        isPasteMode.value = false  //关闭粘贴模式
        selectedItems.value.clear()  //清空选中文件列表
    }

    val switchItemSelected = { item: FileItemDto ->
        isFileSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItems.value, contains = containsForSelected)
    }

    val selectItem = { item: FileItemDto ->
        isFileSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItems.value, contains = containsForSelected)
    }

    val getSelectedFilesCount = {
        selectedItems.value.size
    }

    val isItemInSelected = { f: FileItemDto ->
//        selectedItems.value.contains(f)
        selectedItems.value.indexOfFirst { it.equalsForSelected(f) } != -1
    }


    val renameFileItemDto = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "renameFileItemDto",
        initValue = FileItemDto()
    )
    val renameFileName = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "renameFileName",
        initValue = TextFieldValue("")
    )
    val renameHasErr = rememberSaveable { mutableStateOf(false) }
    val renameErrText = rememberSaveable { mutableStateOf("") }
    val showRenameDialog = rememberSaveable { mutableStateOf(false) }
    val updateRenameFileName: (TextFieldValue) -> Unit = {
        val newVal = it
        val oldVal = renameFileName.value

        //只有当值改变时，才解除输入框报错
        if (oldVal.text != newVal.text) {
            //用户一改名，就取消字段错误设置，允许点击克隆按钮，点击后再次检测，有错再设置为真
            renameHasErr.value = false
        }

        //这个变量必须每次都更新，不能只凭text是否相等来判断是否更新此变量，因为选择了哪些字符、光标在什么位置 等信息也包含在这个TextFieldValue对象里
        renameFileName.value = newVal

    }


    val showGoToPathDialog = rememberSaveable { mutableStateOf(false) }
    val pathToGo = mutableCustomStateOf(stateKeyTag, "pathToGo") { TextFieldValue("") }
    if (showGoToPathDialog.value) {
        val focusRequester = remember { FocusRequester() }

        val goToDialogOnOk = {
            showGoToPathDialog.value = false

            //取出用户输入的path 和 当前路径（用来跳转相对路径）
            val pathToGoRaw = pathToGo.value.text
            val currentPath = currentPath()

            doJobThenOffLoading {
                // remove '\n'
                val pathToGo = trimLineBreak(pathToGoRaw)
                val pathToGoRaw = Unit // to avoid mistake use

                // handle path to absolute path, btw: internal path must before external path, because internal actually starts with external, if swap order, code block of internal path will ignore ever
                val finallyPath = FsUtils.internalExternalPrefixPathToRealPath(pathToGo)

                val f = File(finallyPath)
                if (f.canRead()) {
                    goToPath(f.canonicalPath)
                } else { // can't read path: usually by path non-exists or no permission to read
//                    try relative path
                    val f = File(currentPath, pathToGo)
                    if (f.canRead()) {
                        goToPath(f.canonicalPath)
                    } else {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.cant_read_path))
                    }
                }

            }

            Unit
        }

        ConfirmDialog(
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onPreviewKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown) {
                                    false
                                } else if (event.key == Key.Enter) {
                                    goToDialogOnOk()
                                    true
                                } else {
                                    false
                                }
                            },
                        value = pathToGo.value,
//                        singleLine = true,
                        onValueChange = {
                            pathToGo.value = it
                        },
                        label = {
                            Text(stringResource(R.string.path))
                        },

                        // 把软键盘回车替换成Go
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        // 点击Go直接跳转路径
                        keyboardActions = KeyboardActions(onGo = {
                            goToDialogOnOk()
                        })
                    )
                }
            },
            okBtnEnabled = pathToGo.value.text.isNotBlank(),
            okBtnText = stringResource(id = R.string.go),
            cancelBtnText = stringResource(id = R.string.cancel),
            title = stringResource(R.string.go_to),
            onCancel = { showGoToPathDialog.value = false }
        ) {
            goToDialogOnOk()
        }

        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }


    val showApplyAsPatchDialog = rememberSaveable { mutableStateOf(false) }
    val errMsgForApplyPatch = rememberSaveable { mutableStateOf("") }
    val loadingRepoList = rememberSaveable { mutableStateOf(false) }
    val loadingRepoListJob = mutableCustomBoxOf<Job?>(stateKeyTag, "loadingRepoListJob") { null }  //如果不remember，这个值会被反复赋值，导致之前存的任务被冲掉
    val fileFullPathForApplyAsPatch = rememberSaveable { mutableStateOf("") }
    val allRepoList = mutableCustomStateListOf(stateKeyTag, "allRepoList", listOf<RepoEntity>())
    val initApplyAsPatchDialog = { item: FileItemDto ->
        //这里如果仓库特别多，查特别慢，用户会有卡住的感觉，所以先显示弹窗，
        // 然后在弹窗显示个loading...，加载完成前不能点击确定，但能点击取消，
        // 同时异步查仓库列表，查完后再在弹窗显示就行了

        //clear err msg
        errMsgForApplyPatch.value = ""

        val patchFileFullPath = item.fullPath
        //parent dir name, use to match repo name
        val parentName = item.toFile().parentFile?.name ?: ""


        loadingRepoList.value = true

        loadingRepoListJob.value = doJob job@{
            val result = runCatching {
//                delay(10_000)  // test

                //从db查仓库列表
                val repoDb = AppModel.dbContainer.repoRepository
                //这里不需要查底层仓库信息，实际上只需要仓库完整路径，如果有必要还可以优化下，改成只查name 和 fullpath，不查整个repo entity
                val listFromDb = repoDb.getReadyRepoList(requireSyncRepoInfoWithGit = false)

                delay(1)  //调度点，响应job.cancel()

                //存到状态变量
                allRepoList.value.apply {
                    clear()
                    addAll(listFromDb)
                }


                //检查当前选中的仓库是否仍有效
                if (listFromDb.isNotEmpty()) {
                    // if selectedRepo not in list(例如之前选过，然后被删除了), select first
                    val selectedRepoId = selectedRepo.value.id

                    // repo name match dir name first, it none match, check last selected repo id , if valid , use it, else use idx 0
                    val repoNameMatchedDirNameIdx = listFromDb.indexOfFirst { it.repoName == parentName }.let { if (it < 0) listFromDb.indexOfFirst { selectedRepoId == it.id } else it }
                    selectedRepo.value = listFromDb[repoNameMatchedDirNameIdx.coerceAtLeast(0)]

                    delay(1)
                }

                loadingRepoList.value = false
            }

            if (result.isFailure) {
                val errMsg = result.exceptionOrNull()?.localizedMessage ?: "load repos err"

                errMsgForApplyPatch.value = errMsg
                loadingRepoList.value = false

                MyLog.e(TAG, "loading repo list for ApplyAsPatchDialog err: $errMsg")
            }
        }

        fileFullPathForApplyAsPatch.value = patchFileFullPath
        showApplyAsPatchDialog.value = true
    }

    if (showApplyAsPatchDialog.value) {
        ApplyPatchDialog(
            errMsg = errMsgForApplyPatch.value,
            checkOnly = checkOnly,
            selectedRepo = selectedRepo,
            patchFileFullPath = fileFullPathForApplyAsPatch.value,
            repoList = allRepoList.value,
            loadingRepoList = loadingRepoList.value,
            onCancel = {
                showApplyAsPatchDialog.value = false;

                //取消加载仓库的job，因为用户可能在加载完之前就关了弹窗，若不取消，
                // 就还在加载，这时用户如果再点应用仓库，两个任务会冲突，
                // 第2个没执行完，第1个执行完，用户会看到上次加载出的仓库，
                // 然后等会仓库列表又会刷新，因为这时第2个任务执行完了。。。
                // 总之为了避免问题，这里取消下上次的job，减少出问题的概率（但理论上并不能完全避免）
                runCatching { loadingRepoListJob.value?.cancel() }
            },
            onErrCallback = { e, selectedRepoId ->
                val errMsgPrefix = "apply patch err: err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(selectedRepoId, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#ApplyPatchDialog err: $errMsgPrefix${e.stackTraceToString()}")
            },
            onFinallyCallback = {
                showApplyAsPatchDialog.value = false

                if (enableFilterState.value.not()) {
                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                }
            },
            onOkCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        )
    }


    val showOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false) }
    val openAsDialogFilePath = rememberSaveable { mutableStateOf("") }
    val showOpenInEditor = rememberSaveable { mutableStateOf(false) }
    val fileNameForOpenAsDialog = remember { derivedStateOf { getFileNameFromCanonicalPath(openAsDialogFilePath.value) } }

    val initOpenAsDialog = { fileFullPath: String, showOpenInInnerTextEditor: Boolean ->
        openAsDialogFilePath.value = fileFullPath
        showOpenInEditor.value = showOpenInInnerTextEditor

        showOpenAsDialog.value = true
    }

    if (showOpenAsDialog.value) {
        OpenAsDialog(
            readOnly = readOnlyForOpenAsDialog, fileName = fileNameForOpenAsDialog.value, filePath = openAsDialogFilePath.value, showOpenInEditor = showOpenInEditor.value,
            openInEditor = { expectReadOnly: Boolean ->
                requireInnerEditorOpenFile(openAsDialogFilePath.value, expectReadOnly)
            }
        ) {
            showOpenAsDialog.value = false
        }
    }

//    if(needRefreshFilesPage.value) {
//        initFilesPage()
//        needRefreshFilesPage.value=false
//    }
    //如果apply patch测试通过，则启用包含apply as patch的菜单，否则使用不包含此选项的菜单
    //注意：会忽略值为空的选项！可用此特性来对用户隐藏未测试特性
    val fileMenuKeyTextList = listOf(
        if (isFileChooser) "" else stringResource(R.string.open),  //用内部编辑器打开，得加个这个选项，因为如果自动判断文件该用外部程序打开，但又没对应的外部程序，用户又想用内部编辑器打开，但文件管理器又判断文件该用内部文件打开，就死局了，所以得加这个选项
        if (isFileChooser) "" else stringResource(R.string.open_as),
        stringResource(R.string.rename),
        if (isFileChooser) "" else if (proFeatureEnabled(applyPatchTestPassed)) stringResource(R.string.apply_as_patch) else "",  //应用patch，弹窗让用户选仓库，然后可对仓库应用patch
        if (isFileChooser) "" else stringResource(R.string.file_history),
        stringResource(R.string.copy_full_path),
        if (isFileChooser) "" else stringResource(R.string.copy_repo_relative_path),
        stringResource(R.string.details),
        stringResource(R.string.share),
        stringResource(R.string.delete),
    )

    //目录条目菜单没有open with
    val dirMenuKeyTextList = listOf(
        stringResource(R.string.rename),
        stringResource(R.string.copy_full_path),
        if (isFileChooser) "" else stringResource(R.string.copy_repo_relative_path),
        if (isFileChooser) "" else stringResource(R.string.import_as_repo),
        if (isFileChooser) "" else stringResource(R.string.init_repo),
        stringResource(R.string.add_storage_path),
        stringResource(R.string.details),
        stringResource(R.string.delete),
    )


    // 复制真实路径到剪贴板，显示提示copied
    val copyThenShowCopied = { text: String ->
        clipboardManager.setText(AnnotatedString(text))
        Msg.requireShow(activityContext.getString(R.string.copied))
    }

    // 复制app内路径到剪贴板，显示提示copied
//    val copyPath = {realFullPath:String ->
//        copyThenShowCopied(getFilePathStrBasedRepoDir(realFullPath, returnResultStartsWithSeparator = true))
//    }

    val copyRepoRelativePath = { realFullPath: String ->
        try {
            val repo = Libgit2Helper.findRepoByPath(realFullPath)
            if (repo == null) {
                Msg.requireShow(activityContext.getString(R.string.no_repo_found))
            } else {
                repo.use {
                    val realtivePath = Libgit2Helper.getRelativePathUnderRepo(Libgit2Helper.getRepoWorkdirNoEndsWithSlash(it), realFullPath)

                    // well, the repo already found, but the path got null, usually it happens when trying to copy a repo-relative-path from a repo folder
                    if (realtivePath == null) {
                        Msg.requireShow(activityContext.getString(R.string.path_not_under_repo))
                    } else {
                        copyThenShowCopied(realtivePath)
                    }
                }
            }
        } catch (e: Exception) {
            Msg.requireShowLongDuration(e.localizedMessage ?: "err")
            MyLog.e(TAG, "#copyRepoRelativePath err: ${e.stackTraceToString()}")
        }
    }


    // details variables block start
    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val details_ItemsSize = rememberSaveable { mutableLongStateOf(0L) }  // this is not items count, is file size. this size is a recursive count
    val details_AllCount = rememberSaveable { mutableIntStateOf(0) }  // selected items count(folder + files). note: this is not a recursive count
    val details_FilesCount = rememberSaveable { mutableIntStateOf(0) }  // files count in selected items. not recursive count
    val details_FoldersCount = rememberSaveable { mutableIntStateOf(0) }  // folders count in selected items. not recursive count
    val details_CountingItemsSize = rememberSaveable { mutableStateOf(false) }  // indicate is calculating file size or finished
    val details_itemList = mutableCustomStateListOf(stateKeyTag, "details_itemList", listOf<FileItemDto>())

    val initDetailsDialog = { list: List<FileItemDto> ->
        val list = if (list.size == 1 && list.first().isDir) {
            // count folder and files if only show details for 1 item
            try {
                var filesCount = 0
                var folderCount = 0
                val first = list.first()

                first.toFile().listFiles()!!.forEachBetter {
                    if (it.isDirectory) {
                        folderCount++
                    } else {
                        filesCount++
                    }
                }

                listOf(first.copy(folderCount = folderCount, fileCount = filesCount))
            } catch (_: Exception) {
                list
            }
        } else {
            list
        }

        details_FoldersCount.intValue = list.count { it.isDir }
        details_FilesCount.intValue = list.size - details_FoldersCount.intValue
        details_AllCount.intValue = list.size


        //count files/folders size
        doJobThenOffLoading {
            //prepare
            details_CountingItemsSize.value = true
            details_ItemsSize.longValue = 0

            //count
            list.forEachBetter {
                //ps: 因为已经在函数中追加了size，所以if(it.isDir)的代码块返回0即可
                if (it.isDir) {
                    FsUtils.calculateFolderSize(it.toFile(), details_ItemsSize)
                } else {
                    details_ItemsSize.longValue += it.sizeInBytes
                }
            }

            //done
            details_CountingItemsSize.value = false
        }



        details_itemList.value.clear()
        details_itemList.value.addAll(list)

        showDetailsDialog.value = true
    }

    // details variables block end


    // import as repo variables block start
    val showImportAsRepoDialog = rememberSaveable { mutableStateOf(false) }
    val importAsRepoList = mutableCustomStateListOf(stateKeyTag, "importAsRepoList", listOf<String>())
    val isReposParentFolderForImport = rememberSaveable { mutableStateOf(false) }
    val initImportAsRepoDialog = { fullPathList: List<String> ->
        importAsRepoList.value.clear()
        importAsRepoList.value.addAll(fullPathList)
        isReposParentFolderForImport.value = false
        showImportAsRepoDialog.value = true
    }
    // import as repo variables block end


    // init repo dialog variables block start
    val showInitRepoDialog = rememberSaveable { mutableStateOf(false) }
    val initRepoAsBare = rememberSaveable { mutableStateOf(false) }
    val initRepoList = mutableCustomStateListOf(stateKeyTag, "initRepoList", listOf<String>())
    val initInitRepoDialog = { pathList: List<String> ->
        initRepoList.value.clear()
        initRepoList.value.addAll(pathList)
        initRepoAsBare.value = false
        showInitRepoDialog.value = true
    }
    // init repo dialog variables block end


    // save succeed return true, false otherwise
    fun addStoragePath(
        newPath: String,
        storagePaths: StoragePaths = StoragePathsMan.get(),
        callSave: Boolean = true,
        showMsg: Boolean = true,
    ) : Boolean {
        try {
            if (File(newPath).isDirectory.not()) {
                if(showMsg) {
                    Msg.requireShowLongDuration("err: " + activityContext.getString(R.string.path_is_not_a_dir))
                }

                return false
            }

            // use to save path
            val currentList = storagePaths.storagePaths

            // save if not contains and not equals to app internal storage path(this is default repos storage path, can't delete or add, it always at first item of the Storage Paths in the CloneScreen)
            if (StoragePathsMan.allowAddPath(newPath) && !currentList.contains(newPath)) {
                currentList.add(newPath)

                if(callSave) {
                    StoragePathsMan.save(storagePaths)
                }
            }

            if(showMsg) {
                Msg.requireShow(activityContext.getString(R.string.success))
            }

            return true
        } catch (e: Exception) {
            if(showMsg) {
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            }

            MyLog.e(TAG, "add storage path '$newPath' at `$TAG` err: ${e.localizedMessage}")

            return false
        }
    }


    val showDelFileDialog = rememberSaveable { mutableStateOf(false) }
    val allCountForDelDialog = rememberSaveable { mutableStateOf(0) }
    val listForDeleteDialog = mutableCustomStateListOf(stateKeyTag, "listForDeleteDialog") { listOf<FileItemDto>() }
//    val fileCountForDelDialog = rememberSaveable { mutableStateOf(0)}
//    val folderCountForDelDialog = rememberSaveable { mutableStateOf(0)}
    val initDelFileDialog = { list: List<FileItemDto> ->
//        val list = selectedItems.value
//        allCountForDelDialog.value = list.size
//        folderCountForDelDialog.value = list.count { it.isDir }
//        fileCountForDelDialog.value = allCountForDelDialog.value - folderCountForDelDialog.value

        allCountForDelDialog.value = list.size
        listForDeleteDialog.value.apply {
            clear()
            addAll(list)
        }

        showDelFileDialog.value = true
    }
    if (showDelFileDialog.value) {
        ConfirmDialog2(
            title = stringResource(id = R.string.delete),
            text = replaceStringResList(stringResource(R.string.n_items_will_be_deleted), listOf("" + allCountForDelDialog.value)),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDelFileDialog.value = false }
        ) {
            //关闭弹窗
            showDelFileDialog.value = false

            //执行删除
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
                val targets = listForDeleteDialog.value.toList()

                if (targets.isEmpty()) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                    return@doJobThenOffLoading  // 结束操作
                }

                // here better don't catch exception, if got err, throw and abort delete immediately
                targets.forEachBetter {
                    val file = File(it.fullPath)
                    // 如果要删除的路径包含.git，记个警告log，但不阻止，用户非要删，我不管 （没必要警告，用户爱删就删，随便）
//                    if(file.canonicalPath.contains(".git")) {
//                        MyLog.w(TAG, "#DelFileDialog: may delete file under '.git' folder, fullPath will del is: '${file.canonicalPath}'")
//                    }

                    //不管是目录还是文件，直接梭哈
                    file.deleteRecursively()

                    // 从已选中条目列表移除条目
                    selectedItems.value.removeIf({selectedItem -> it.fullPath == selectedItem.fullPath} )
                }


                Msg.requireShow(activityContext.getString(R.string.success))

                //如果选择条目为空则退出选择模式并刷新目录
                if(selectedItems.value.isEmpty()) {
                    filesPageQuitSelectionMode()
                }

                if (enableFilterState.value) {  //filter模式刷新会重新递归查找，太重量级，直接更新下过滤结果即可
                    val filterList = filterList.value
                    targets.forEachBetter { filterList.remove(it) }
                } else {
                    // 如果当前路径依然存在，仅从条目列表移除对应删除的条目
                    // 否则刷新页面
                    if(File(currentPath()).exists()) {
                        // 不刷新页面，只从当前目录移除对应条目
                        val list = currentPathFileList.value
                        targets.forEachBetter { list.remove(it) }
                    }else {
                        // 有可能用户会把当前所在目录给删掉，
                        // 只要通过面包屑进行跳转再选中当前目录，再通过面包屑返回当前目录，再删除即可
                        // 这种情况一般不会触发，没必要严格限制，确保这里刷新页面，这样如果触发上述情况，
                        // 当前目录就会正确显示为不可读，若不刷新页面仅更新列表，就会错误显示已经不存在的条目
                        changeStateTriggerRefreshPage(needRefreshFilesPage)
                    }
                }
            }
        }
    }


    val renameFile = { item: FileItemDto ->
        renameFileItemDto.value = item  // 旧item
        renameFileName.value = TextFieldValue(item.name, selection = getRangeForRenameFile(item.name))  //旧文件名

        renameHasErr.value = false  //初始化为没错误，不然会显示上次报的错，比如“文件名已存在！”
        renameErrText.value = ""  //初始化错误信息为空，理由同上

        showRenameDialog.value = true  // 显示弹窗
    }

    val shareFiles = { files: List<FileItemDto> ->
        try {
            FsUtils.shareFiles(activityContext, files)
        }catch (e: Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "$TAG#shareFiles err: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    val fileMenuKeyActList = listOf<(FileItemDto) -> Unit>(
        open@{ item: FileItemDto ->
            val expectReadOnly = false
            requireInnerEditorOpenFile(item.fullPath, expectReadOnly)
            Unit
        },

        openAs@{ item: FileItemDto ->
            val showInnerEditor = false
            initOpenAsDialog(item.fullPath, showInnerEditor)

            Unit
        },
//        editWith@{ item:FileItemDto ->
//            val isSuccess = FsUtils.openFileAsEditMode(appContext, File(item.fullPath))
//            if(!isSuccess) {
//                Msg.requireShow(appContext.getString(R.string.open_file_edit_mode_err_maybe_try_view))
//                changeStateTriggerRefreshPage(needRefreshFilesPage)
//                //如果后面还有代码，需要在这return
//            }
//            Unit
//        },
//        viewWith@{ item:FileItemDto ->
//            val isSuccess = FsUtils.openFileAsViewMode(appContext, File(item.fullPath))
//            if(!isSuccess) {
//                Msg.requireShow(appContext.getString(R.string.open_file_with_view_mode_err))
//                changeStateTriggerRefreshPage(needRefreshFilesPage)
//                //如果后面还有代码，需要在这return
//            }
//            Unit
//        },
        renameFile,
        applyAsPatch@{ item: FileItemDto ->
            initApplyAsPatchDialog(item)
//            Unit  // for make return type is Unit, or specify type at declare statement
        },
//        copyPath@{
//            copyPath(it.fullPath)
//        },
        fileHistory@{
            goToFileHistory(it.fullPath, activityContext)
        },

        copyFullPath@{
            copyThenShowCopied(it.fullPath)
        },

        copyRepoRelativePath@{
            copyRepoRelativePath(it.fullPath)
        },

        details@{
            initDetailsDialog(listOf(it))
        },

        share@{
            shareFiles(listOf(it))
        },

        delete@{
            initDelFileDialog(listOf(it))
        }

//        export@{ item:FileItemDto ->
//            val ret = FsUtils.getAppDirUnderPublicDocument()
//            if(ret.hasError()) {
//                Msg.requireShow(appContext.getString(R.string.open_public_folder_err))
//                return@export
//            }
//
//            // 测试结果，成功！）这个如果成功，替换成批量复制，把那个函数抽出来，加两个参数，一个目标路径，一个是否移动，就行了
//            val src = File(item.fullPath)
//            val target = File(ret.data!!.canonicalPath, src.name)
//            src.copyTo(target, false)
//            Msg.requireShow(appContext.getString(R.string.success))
//        }
    )
    val dirMenuKeyActList = listOf(
        renameFile,
        copyFullPath@{
            copyThenShowCopied(it.fullPath)
        },
        copyRepoRelativePath@{
            copyRepoRelativePath(it.fullPath)
        },
        importAsRepo@{
            initImportAsRepoDialog(listOf(it.fullPath))
        },
        initRepo@{
            initInitRepoDialog(listOf(it.fullPath))
        },
        addStoragePath@{
            addStoragePath(it.fullPath)
            Unit
        },
        details@{
            initDetailsDialog(listOf(it))
        },

        delete@{
            initDelFileDialog(listOf(it))
        }
    )

    val isImportMode = rememberSaveable { mutableStateOf(false) }
    val showImportResultDialog = rememberSaveable { mutableStateOf(false) }
//    val successImportList = rememberSaveable { mutableStateListOf<String>() }
//    val failedImportList = rememberSaveable { mutableStateListOf<String>() }
    val failedImportListStr = rememberSaveable { mutableStateOf("") }
    val successImportCount = rememberSaveable { mutableIntStateOf(0) }
    val failedImportCount = rememberSaveable { mutableIntStateOf(0) }


    val getListState: (String) -> LazyListState = { path: String ->
        Cache.getFilesListStateByPath(path)
    }

    val breadCrumbListState = rememberLazyListState()

    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }

    val backHandlerOnBack = getBackHandler(
        naviUp = naviUp,
        isFileChooser = isFileChooser,
        appContext = activityContext,
        isFileSelectionMode = isFileSelectionMode,
        filesPageQuitSelectionMode = filesPageQuitSelectionMode,
        currentPath = currentPath,
        allRepoParentDir = allRepoParentDir,
        needRefreshFilesPage = needRefreshFilesPage,
        exitApp = exitApp,
        getFilterMode = filesPageGetFilterMode,
        filesPageFilterModeOff = filesPageFilterModeOff,
        filesPageSimpleFilterOn = filesPageSimpleFilterOn,
        openDrawer = openDrawer,
        goToPath = goToPath,
        resetSearchVars = resetFilesSearchVars,

        )

    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = { backHandlerOnBack() })
    //back handler block end


    //导入失败则显示这个对话框，可以复制错误信息
    if (showImportResultDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.import_has_err),
            cancelBtnText = stringResource(R.string.close),
            showOk = false,
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    ScrollableColumn {
                        Row {
                            Text(text = stringResource(R.string.import_success) + ": " + successImportCount.value)
                        }
                        Row {
                            Text(text = stringResource(R.string.import_failed) + ": " + failedImportCount.value)
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(failedImportListStr.value))
                            Msg.requireShow(activityContext.getString(R.string.copied))  //这里如果用 Msg.requierShow() 还得刷新页面才能看到信息，这个操作没必要刷新页面，不如直接用Toast，不过Toast怎么实现的？不用刷新页面吗？
                            //test x能) 测试下刷新页面是否就能看到信息且不影响弹窗（当然不会影响，因为显示弹窗的状态变量还是真啊！只要状态没变，页面还是一样）
//                        Msg.requireShow(appContext.getString(R.string.copied))  //这里如果用 Msg.requierShow() 还得刷新页面才能看到信息，不如直接用Toast
//                        changeStateTriggerRefreshPage(needRefreshFilesPage)
                            //test
                        }
                        ) {
                            Text(
                                text = stringResource(R.string.you_can_click_here_copy_err_msg),
                                style = MyStyleKt.ClickableText.getStyle(),
                                color = MyStyleKt.ClickableText.getColor(),
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Text(text = stringResource(R.string.err_msg) + ":")
                        }
                        Row {
                            Text(text = failedImportListStr.value, color = MyStyleKt.TextColor.error())
                        }

//                    Column(modifier = Modifier
//                        .heightIn(max = 300.dp)
//                        .verticalScroll(rememberScrollState())) {
//                        Text(text = failedImportListStr.value)
//                    }
                    }
                }
            },
            onCancel = { showImportResultDialog.value = false }
        ) {
            showImportResultDialog.value = false
        }
    }




    if (showRenameDialog.value) {
        val focusRequester = remember { FocusRequester() }

        val onOk = {

            try {
                val oldFileItemDto = renameFileItemDto.value // 旧文件
                val renameFileItemDto = Unit // avoid mistake using

                val newFileName = renameFileName.value.text
                val fileOrFolderNameCheckRet = checkFileOrFolderNameAndTryCreateFile(newFileName, activityContext)
                if (fileOrFolderNameCheckRet.hasError()) {  // 检测是否有坏字符，例如路径分隔符
                    renameHasErr.value = true
                    renameErrText.value = fileOrFolderNameCheckRet.msg

                    //检测新旧文件名是否相同 以及 新文件名是否已经存在，两者都视为文件已存在
                } else if (newFileName == oldFileItemDto.name || isPathExists(File(oldFileItemDto.fullPath).parent, newFileName)) {
                    renameHasErr.value = true
                    renameErrText.value = activityContext.getString(R.string.file_already_exists)
                } else {  //执行重命名文件
                    showRenameDialog.value = false  //关闭弹窗
                    //执行重命名
                    doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
                        try {
                            val oldFile = File(oldFileItemDto.fullPath)
                            val newFile = File(File(oldFileItemDto.fullPath).parent, newFileName)
                            val renameSuccess = oldFile.renameTo(newFile)
                            if (renameSuccess) {
                                val newNameDto = FileItemDto.genFileItemDtoByFile(newFile, activityContext)

                                //更新已选中条目列表
                                val selectedItems = selectedItems.value
                                selectedItems.toList().forEachIndexedBetter { idx, item ->
                                    if (item == oldFileItemDto) {
                                        selectedItems[idx] = newNameDto
                                    }
                                }


                                //刷新页面 或 更新过滤列表
                                if (enableFilterState.value.not()) {
                                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                                } else {
                                    val filterList = filterList.value
                                    filterList.toList().forEachIndexedBetter { idx, item ->
                                        if (item == oldFileItemDto) {
                                            filterList[idx] = newNameDto
                                        }
                                    }
                                }

                                //提示用户成功
                                Msg.requireShow(activityContext.getString(R.string.success))
                            } else {
                                Msg.requireShow(activityContext.getString(R.string.error))
                            }
                        } catch (e: Exception) {
                            Msg.requireShowLongDuration("rename failed: " + e.localizedMessage)
                            MyLog.e(TAG, "rename failed: oldFile=${oldFileItemDto.fullPath}, newFileName=${newFileName}, err=${e.stackTraceToString()}")
                        }

                    }
                }
            } catch (outE: Exception) {
                renameHasErr.value = true
                renameErrText.value = outE.localizedMessage ?: errorStrRes
                MyLog.e(TAG, "#RenameDialog err: " + outE.stackTraceToString())
            }
        }

        ConfirmDialog(
            okBtnEnabled = !renameHasErr.value,
            cancelBtnText = stringResource(id = R.string.cancel),
            okBtnText = stringResource(id = R.string.ok),
            title = stringResource(R.string.rename),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding)
                            .focusRequester(focusRequester),
                        value = renameFileName.value,
                        singleLine = true,
                        isError = renameHasErr.value,
                        supportingText = {
                            if (renameHasErr.value) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = renameErrText.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (renameHasErr.value) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = renameErrText.value,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        onValueChange = {
                            updateRenameFileName(it)
                        },
                        label = {
                            Text(stringResource(R.string.file_name))
                        },
                        placeholder = {
                            Text(stringResource(R.string.input_new_file_name))
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            onOk()
                        }),
                    )
                }
            },
            onCancel = { showRenameDialog.value = false }
        ) {
            onOk()
        }

        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }


    val createFileOrFolderErrMsg = rememberSaveable { mutableStateOf("") }

    val fileNameForCreateDialog = mutableCustomStateOf(stateKeyTag, "fileNameForCreateDialog") { TextFieldValue("") }
//    val fileTypeOptionsForCreateDialog = remember {listOf(activityContext.getString(R.string.file), activityContext.getString(R.string.folder))}  // idx: 0 1
//    val selectedFileTypeOptionForCreateDialog = rememberSaveable{mutableIntStateOf(0)}

    if (showCreateFileOrFolderDialog.value) {
        CreateFileOrFolderDialog2(
            fileName = fileNameForCreateDialog,
            errMsg = createFileOrFolderErrMsg,
            onOk = f@{ fileOrFolderName: String, isDir: Boolean ->
                //do create file or folder
                try {
                    // if current path already deleted, then show err and abort create
                    if (!File(currentPath()).exists()) {
                        throw RuntimeException(activityContext.getString(R.string.current_dir_doesnt_exist_anymore))
                    }

                    val fileOrFolderNameCheckRet = checkFileOrFolderNameAndTryCreateFile(fileOrFolderName, activityContext)
                    if (fileOrFolderNameCheckRet.hasError()) {
                        createFileOrFolderErrMsg.value = fileOrFolderNameCheckRet.msg
                        return@f false
                    } else {  //文件名ok，检查文件是否存在
                        val file = File(currentPath(), fileOrFolderName)
                        if (file.exists()) {  //文件存在
                            createFileOrFolderErrMsg.value = fileAlreadyExistStrRes
                            return@f false
                        } else {  //文件不存在（且文件名ok
                            val isCreateSuccess = if (isDir) {  // create dir
                                file.mkdir()  //这不需要mkdirs()，用户肯定是在当前目录创建一层目录，所以mkdir()就够用了，而且路径分隔符 / 被检测文件名是否合法的函数当作非法字符，无法使用，所以用户其实也没法输入连环目录
                            } else {  // create file
                                file.createNewFile()
                            }

                            //检测创建是否成功并显示提醒
                            if (isCreateSuccess) {  //创建成功
                                Msg.requireShow(successStrRes)  //提示成功
                                createFileOrFolderErrMsg.value = ""  //清空错误信息
                                fileNameForCreateDialog.value = TextFieldValue("")  //清空文件名

                                //若创建的目录，创建完毕后打开
                                if (isDir) {
                                    goToPath(file.canonicalPath)
                                } else {
                                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                                }

                                return@f true
                            } else { //创建失败但原因不明
                                createFileOrFolderErrMsg.value = errorStrRes  //设置错误信息为err，不过没有具体信息，用户虽然不知道出了什么错，但知道出错了，而且仍可点取消关闭弹窗，所以问题不大
                                return@f false
                            }
                        }
                    }
                } catch (e: Exception) {
                    createFileOrFolderErrMsg.value = e.localizedMessage ?: errorStrRes
                    MyLog.e(TAG, "CreateFileOrFolderDialog in Files Page err: " + e.stackTraceToString())

                    return@f false
                }
            },
            onCancel = {
                showCreateFileOrFolderDialog.value = false
                createFileOrFolderErrMsg.value = ""
            }
        )
    }

    // 向下滚动监听，开始
//    val enableFilterState = rememberSaveable { mutableStateOf(false)}
//    val firstVisible = remember { derivedStateOf { if(enableFilterState.value) filterListState.value.firstVisibleItemIndex else curListState.value.firstVisibleItemIndex } }
//    ScrollListener(
//        nowAt = firstVisible.value,
//        onScrollUp = {filesPageScrollingDown.value = false}
//    ) { // onScrollDown
//        filesPageScrollingDown.value = true
//    }
//
//    val lastAt = remember { mutableIntStateOf(0) }
//    val lastIsScrollDown = remember { mutableStateOf(false) }
//    val forUpdateScrollState = remember {
//        derivedStateOf {
//            val nowAt = if(enableFilterState.value) {
//                filterListState.firstVisibleItemIndex
//            } else {
//                curListState.value.firstVisibleItemIndex
//            }
//
//            val scrolledDown = nowAt > lastAt.intValue  // scroll down
////            val scrolledUp = nowAt < lastAt.intValue
//
//            val scrolled = nowAt != lastAt.intValue  // scrolled
//            lastAt.intValue = nowAt
//
//            // only update state when this scroll down and last is not scroll down, or this is scroll up and last is not scroll up
//            if(scrolled && ((lastIsScrollDown.value && !scrolledDown) || (!lastIsScrollDown.value && scrolledDown))) {
//                filesPageScrolled.value = true
//            }
//
//            lastIsScrollDown.value = scrolledDown
//        }
//    }.value
    // 向下滚动监听，结束


    val findRepoThenGoToReposOrChangList = { fullPath: String, trueGoToReposFalseGoToChangeList: Boolean ->
        doJobThenOffLoading job@{
            try {
                val repo = Libgit2Helper.findRepoByPath(fullPath)
                if (repo == null) {
                    Msg.requireShow(activityContext.getString(R.string.not_found))
                } else {
                    val repoWorkDir = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                    val onlyReturnReadyRepo = !trueGoToReposFalseGoToChangeList  // if go to repos page, no need require repo ready; if go to changelist, require a ready repo
                    val target = AppModel.dbContainer.repoRepository.getByFullSavePath(
                        repoWorkDir,
                        onlyReturnReadyRepo = onlyReturnReadyRepo,
                        requireSyncRepoInfoWithGit = false
                    )
                    if (target == null) {
                        Msg.requireShow(activityContext.getString(R.string.not_found))
                    } else {
                        if (trueGoToReposFalseGoToChangeList) {
                            goToRepoPage(target.id)
                        } else {
                            goToChangeListPage(target)
                        }
                    }
                }

            } catch (e: Exception) {
                Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                MyLog.e(TAG, "#findRepoThenGoToReposOrChangList err: fullPath=$fullPath, trueGoToReposFalseGoToChangeList=$trueGoToReposFalseGoToChangeList, err=${e.localizedMessage}")
            }
        }
    }


    val showInRepos = { fullPath: String ->
        findRepoThenGoToReposOrChangList(fullPath, true)
    }

    val showInChangeList = { fullPath: String ->
        findRepoThenGoToReposOrChangList(fullPath, false)
    }


    val showViewAndSortDialog = rememberSaveable { mutableStateOf(false) }
    val viewAndSortState = mutableCustomStateOf(stateKeyTag, "viewAndSortState") { settingsSnapshot.value.files.defaultViewAndSort }
    val viewAndSortStateBuf = mutableCustomStateOf(stateKeyTag, "viewAndSortStateBuf") { settingsSnapshot.value.files.defaultViewAndSort }
    val onlyForThisFolderState = rememberSaveable { mutableStateOf(false) }
    val onlyForThisFolderStateBuf = rememberSaveable { mutableStateOf(false) }

    if (showViewAndSortDialog.value) {
        val height = 10.dp
        ConfirmDialog2(
            title = stringResource(R.string.view_and_sort),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    SingleSelection(
                        itemList = SortMethod.entries,
                        selected = { idx, item -> viewAndSortStateBuf.value.sortMethod == item.code },
                        text = { idx, item -> SortMethod.getText(item, activityContext) },
                        onClick = { idx, item -> viewAndSortStateBuf.value = viewAndSortStateBuf.value.copy(sortMethod = item.code) },
                        minHeight = MyStyleKt.RadioOptions.middleHeight,
                    )

                    Spacer(Modifier.height(height))
                    MyCheckBox2(stringResource(R.string.ascend), viewAndSortStateBuf.value.ascend) { newValue ->
                        viewAndSortStateBuf.value = viewAndSortStateBuf.value.copy(ascend = newValue)
                    }

                    MyCheckBox2(stringResource(R.string.folder_first), viewAndSortStateBuf.value.folderFirst) { newValue ->
                        viewAndSortStateBuf.value = viewAndSortStateBuf.value.copy(folderFirst = newValue)
                    }
                    Spacer(Modifier.height(height))

                    MyHorizontalDivider()
                    Spacer(Modifier.height(height))

                    MyCheckBox(stringResource(R.string.only_for_this_folder), onlyForThisFolderStateBuf)

                }

            },
            onCancel = { showViewAndSortDialog.value = false }
        ) {
            showViewAndSortDialog.value = false

            doJobThenOffLoading {
                // only update state and settings if has change
                // in kotlin, left != right, should call equals() too
                if (onlyForThisFolderStateBuf.value != onlyForThisFolderState.value || viewAndSortStateBuf.value.equals(viewAndSortState.value).not()) {

                    onlyForThisFolderState.value = onlyForThisFolderStateBuf.value

                    val newViewAndSort = viewAndSortStateBuf.value.copy()

                    //update state
                    viewAndSortState.value = newViewAndSort.copy()


                    //update settings
                    settingsSnapshot.value = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                        if (onlyForThisFolderStateBuf.value) {
                            it.files.dirAndViewSort_Map.set(currentPath(), newViewAndSort)
                        } else {  // set global sort method
                            // try remove, if hase dir specified sort method, if hasn't remove is ok as well, will not throw exception
                            it.files.dirAndViewSort_Map.remove(currentPath())

                            it.files.defaultViewAndSort = newViewAndSort
                        }
                    }!!
                }

                // refresh page whatever has change or no change
                changeStateTriggerRefreshPage(needRefreshFilesPage)
            }
        }
    }


    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeStateTriggerRefreshPage(needRefreshFilesPage) },

        ) {
        if (isLoading.value) {
//        LoadingDialog(loadingText.value)        //这个页面不适合用Dialog，页面会闪。

            LoadingTextCancellable(
                text = loadingText.value,
                contentPadding = contentPadding,
                showCancel = cancellableActRunning.value,
                onCancel = cancelAct,  //这里直接传函数即可，仅当showCancel为真，才有可能调用此函数，所以不用检查是否需要传null
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)

            ) {
                // bread crumb
                if (currentPathBreadCrumbList.value.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(5.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // noop, dead code, 之前可能进这里，但后来给面包屑强制添加了root path，所以其实应该不可能执行这里的代码了
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(5.dp),
                        state = breadCrumbListState,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //面包屑 (breadcrumb)
                        val breadList = currentPathBreadCrumbList.value.toList()
                        breadList.forEachIndexedBetter { idx, it ->
                            item {
                                val separator = Cons.slash
                                val breadCrumbDropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
                                val curItemIsRoot = idx == 0  // root path '/'
//                            val curPathIsRoot = currentPath.value == separator
//                            val curPathIsRootAndCurItemIsRoot = curPathIsRoot && curItemIsRoot
                                val textColor = if (it.fullPath.startsWith(currentPath() + separator)) Color.Gray else Color.Unspecified

                                //非根路径显示路径分割符
                                if (curItemIsRoot.not()) {
                                    Text(text = Cons.arrowToRight, color = textColor, fontWeight = FontWeight.Light)
                                }

                                Text(
                                    text = it.name,
                                    color = textColor,
                                    fontWeight = if (it.fullPath == currentPath()) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .combinedClickable(
                                            onLongClick = {  //long press will show menu for pressed path
//                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                breadCrumbDropDownMenuExpandState.value = true
                                            }
                                        ) { //onClick

                                            //清空过滤关键字
                                            filesPageSimpleFilterKeyWord.value = TextFieldValue("")

                                            //跳转路径
                                            goToPath(it.fullPath)

                                        }
                                        .padding(horizontal = 10.dp)  //整宽点，好点击，不然名字很短，手按不到
                                )


                                if (breadCrumbDropDownMenuExpandState.value) {
                                    Column {
                                        val enableMenuItem = true
                                        //菜单列表
                                        DropdownMenu(
                                            offset = DpOffset(x = 0.dp, y = 20.dp),
                                            expanded = breadCrumbDropDownMenuExpandState.value,
                                            onDismissRequest = { breadCrumbDropDownMenuExpandState.value = false }
                                        ) {

                                            DropdownMenuItem(
                                                enabled = enableMenuItem,
                                                text = { Text(stringResource(R.string.copy_full_path)) },
                                                onClick = {
                                                    breadCrumbDropDownMenuExpandState.value = false
                                                    copyThenShowCopied(it.fullPath)
                                                }
                                            )


                                            if (isFileChooser.not()) {
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.copy_repo_relative_path)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        copyRepoRelativePath(it.fullPath)
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.import_as_repo)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        initImportAsRepoDialog(listOf(it.fullPath))
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.init_repo)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        initInitRepoDialog(listOf(it.fullPath))
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.show_in_repos)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        showInRepos(it.fullPath)
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    enabled = enableMenuItem,
                                                    text = { Text(stringResource(R.string.show_in_changelist)) },
                                                    onClick = {
                                                        breadCrumbDropDownMenuExpandState.value = false
                                                        showInChangeList(it.fullPath)
                                                    }
                                                )
                                            }


                                            DropdownMenuItem(
                                                enabled = enableMenuItem,
                                                text = { Text(stringResource(R.string.add_storage_path)) },
                                                onClick = {
                                                    breadCrumbDropDownMenuExpandState.value = false
                                                    addStoragePath(it.fullPath)
                                                }
                                            )

                                            DropdownMenuItem(
                                                enabled = enableMenuItem,
                                                text = { Text(stringResource(R.string.details)) },
                                                onClick = {
                                                    breadCrumbDropDownMenuExpandState.value = false
                                                    // bread crumb dto lack some info for faster loading, so need requrey a new dto when show details
                                                    //面包屑的dto是缩水的，为了加载快而没查最后修改时间和大小等在面包屑用不到的信息，因此显示前需要重新查下dto
                                                    initDetailsDialog(listOf(FileItemDto.genFileItemDtoByFile(File(it.fullPath), activityContext)))
                                                }
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }

                    //make breadCrumb always scroll to end for show current path
                    val scrollToCurPath = remember {
                        derivedStateOf {
                            //若当前目录在面包屑中的条目不可见，滚动使其可见
                            val indexOfCurPath = currentPathBreadCrumbList.value.indexOfFirst { it.fullPath == currentPath() }
                            if (indexOfCurPath != -1) {
                                //滚动到当前条目前2个位置，不然当前条目在屏幕最左边，而比较顺眼的位置是最右边。。。。。不过滚动前2个位置只是粗略调整，没任何依据，如果当前文件夹名非常长的话，这样滚动就看不见了。。。。
                                UIHelper.scrollToItem(scope, breadCrumbListState, indexOfCurPath - 2)
                            }
                        }
                    }.value;  //调用.value才会触发懒计算
                }


                // foler is empty不算错误，只是省得再写个column，所以放这判断，显示时不会像错误一样红色
                val folderIsEmpty = currentPathFileList.value.isEmpty()
                val hasErr = hasErr()
                // if has err, show err, else show file list
                if (hasErr || folderIsEmpty) {
                    Column(
                        modifier = Modifier
                            .baseVerticalScrollablePageModifier(contentPadding, errScrollState)
                            .padding(MyStyleKt.defaultItemPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        MySelectionContainer {
                            if (hasErr) {
                                Text(getErr(), color = MyStyleKt.TextColor.error())
                            } else if (folderIsEmpty) {
                                Text(stringResource(R.string.folder_is_empty))
                            }  // else maybe
                        }
                    }
                } else {
                    val keyword = filesPageSimpleFilterKeyWord.value.text  //关键字
                    val enableFilter = filterModeActuallyEnabled(filterOn = filesPageSimpleFilterOn.value, keyword = keyword)

                    val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                    val currentPathFileList = filterTheList(
                        needRefresh = filterResultNeedRefresh.value,
                        lastNeedRefresh = lastNeedRefresh,
                        enableFilter = enableFilter,
                        keyword = keyword,
                        lastKeyword = filesPageLastKeyword,
                        searching = filesPageSearching,
                        token = filesPageSearchToken,
                        activityContext = activityContext,
                        filterList = filterList.value,
                        list = currentPathFileList.value,
                        resetSearchVars = resetFilesSearchVars,
                        match = { idx, it -> true },
                        customTask = {
                            val curDir = File(currentPath())
                            if (curDir.canRead().not()) {  //目录不可读，提示下
                                Msg.requireShow(activityContext.getString(R.string.err_read_path_failed))
                            } else { //目录可读，执行搜索
                                val canceled = initSearch(keyword = keyword, lastKeyword = filesPageLastKeyword, token = filesPageSearchToken)

                                val match = { idx: Int, it: File ->
                                    //匹配名称 或 "*.txt"之类的后缀
                                    it.name.contains(keyword, ignoreCase = true)
                                            || RegexUtil.matchWildcard(it.name, keyword)
                                }

                                filterList.value.clear()
                                filesPageSearching.value = true

                                DirSearchUtil.realBreadthFirstSearch(
                                    dir = curDir,
                                    match = match,
                                    matchedCallback = { idx, item -> filterList.value.add(FileItemDto.genFileItemDtoByFile(item, activityContext)) },
                                    canceled = canceled
                                )
                            }
                        }
                    )


                    val listState = if (enableFilter) filterListState else curListState.value

                    //更新是否启用filter
                    enableFilterState.value = enableFilter

                    MyLazyColumn(
                        contentPadding = PaddingValues(0.dp),  //外部padding了
                        list = currentPathFileList,
                        listState = listState,
                        requireForEachWithIndex = true,
                        requirePaddingAtBottom = true
                    ) { index, it ->
                        // 没测试，我看其他文件管理器针对目录都没open with，所以直接隐藏了) 需要测试：能否针对目录执行openwith？如果不能，对目录移除openwith选项
                        FileListItem(
                            fullPathOfTopNoEndSlash = if (enableFilter) currentPath() else "",
                            item = it,
                            lastPathByPressBack = lastPathByPressBack,
                            menuKeyTextList = if (it.isFile) fileMenuKeyTextList else dirMenuKeyTextList,
                            menuKeyActList = if (it.isFile) fileMenuKeyActList else dirMenuKeyActList,
                            iconOnClick = {  //点击文件或文件夹图标时的回调函数
                                if (isFileChooser.not() && !isPasteMode.value && !isImportMode.value) {
                                    switchItemSelected(it)
                                }
                            },
                            isItemInSelected = isItemInSelected,
                            itemOnLongClick = {
                                if (isFileChooser.not()) {
                                    //如果不是选择模式或粘贴模式，切换为选择模式
                                    if (!isFileSelectionMode.value && !isPasteMode.value && !isImportMode.value) {
//                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        switchItemSelected(it)

                                        //如果处于选择模式，长按执行连续选择
                                    } else if (isFileSelectionMode.value && !isPasteMode.value && !isImportMode.value) {
//                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        UIHelper.doSelectSpan(
                                            index, it,
                                            //这里调用 toList() 是为了拷贝下源list，避免并发修改异常
                                            selectedItems.value.toList(), currentPathFileList.toList(),
                                            switchItemSelected,
                                            selectItem
                                        )
                                    }
                                }
                            }
                        ) itemOnClick@{  //itemOnClick
                            if (isFileSelectionMode.value) {  //选择模式，切换选择
                                switchItemSelected(it)
                            } else {  //非选择模式，若文件则在编辑器打开，否则在当前页面打开目录

                                //关闭过滤模式的逻辑：如果是目录，一律关闭；如果是文件，判断是否用内部Editor打开，如果是关闭，否则不关闭。
                                if (it.isFile) {
                                    //单选文件模式，点击文件后直接返回
                                    if (isFileChooser) {
                                        if (fileChooserType == FileChooserType.SINGLE_FILE) {
                                            updateSelectedPath(it.fullPath)
                                            naviUp()
                                        }

                                        //不管单选文件还是单选目录，只要是文件选择器页面，点文件后都不需要执行后续逻辑
                                        return@itemOnClick
                                    }

                                    //粘贴或导入模式下点击文件无效，除非先退出对应模式(不过没禁止通过三个点的菜单打开文件)
                                    if (isPasteMode.value || isImportMode.value) {
                                        return@itemOnClick
                                    }

                                    //检查文件大小，如果太大，拒绝打开，考虑下大小设多少合适。
                                    // 如果是文本类型，用文本编辑器打开，其他类型弹窗提示用外部程序打开还是用文本编辑器打开
                                    //goto editor page with file path

                                    //若匹配内部Editor关联文件类型，则打开
                                    if (RegexUtil.equalsOrEndsWithExt(it.name, SettingsUtil.obtainEditorFileAssociationList())) {
                                        //请求打开文件
                                        val expectReadOnly = false
                                        requireInnerEditorOpenFile(it.fullPath, expectReadOnly)

                                    } else {  //非关联类型，尝试用外部软件打开

                                        //已废弃：若文件在只读目录，默认以只读方式打开
                                        //  readOnlyForOpenAsDialog.value = FsUtils.isReadOnlyDir(it.fullPath)

                                        //猜测文件mime类型并打开，若失败，显示open as弹窗

//                                        val hasExtension = it.name.contains(".")
                                        //猜测app mimeType
                                        //无后缀的会返回 */*，所以不用检测是否有后缀了，直接检测是否是 */* 就可以了
                                        val mimeType = MimeType.guessFromFileName(it.name).intentType
                                        MyLog.d(TAG, "fileName=${it.name}, guessed mimeType=$mimeType")

                                        //有后缀名并且不是 */*，则调用open file，请求系统显示能打开此类型文件的应用;
                                        // 如果是 */* 则代表检测文件类型失败，或者未知类型，这时应弹窗让用户选择具体类型
//                                        if(hasExtension && mimeType != MimeType.ANY.intentType) {
                                        if(mimeType.startsWith("text/")) {  // 文本类型，直接用内部编辑器打开
                                            val expectReadOnly = false
                                            requireInnerEditorOpenFile(it.fullPath, expectReadOnly)
                                        }else if (mimeType == MimeType.ANY.intentType) { // Any, */* ， 无后缀或没检测出具体类型就会是这个
                                            val showInnerEditor = true
                                            initOpenAsDialog(it.fullPath, showInnerEditor)
                                        } else { // 非 text 非*/* ，有具体类型，请求系统打开，若失败，显示open as弹窗，不过一般不会失败
                                            //请求外部打开（实际会显示支持此类型的app供用户选择）
                                            val openSuccess = FsUtils.openFile(
                                                activityContext,
                                                it.toFile(),
                                                mimeType,
                                                readOnly = false
                                            )

                                            //若打开失败，显示弹窗，用户可选择具体以哪种类型打开
                                            if (!openSuccess) {
                                                val showInnerEditor = true
                                                initOpenAsDialog(it.fullPath, showInnerEditor)
                                            }
                                        }
                                    }
                                } else {  // if(item.isDirectory) 点击目录，直接打开。
                                    //粘贴模式下点击被选中的文件夹无效，以免出现无限递归复制
                                    //导入模式不会选中PuppyGit app中的文件夹且所有文件夹都可点击，所以无需判断导入模式
                                    if (isPasteMode.value && isItemInSelected(it)) {
                                        return@itemOnClick
                                    }

                                    //关闭过滤模式
//                            filesPageFilterModeOff()

                                    filesPageSimpleFilterKeyWord.value = TextFieldValue("")  //清空过滤关键字

                                    //打开目录
                                    goToPath(it.fullPath)
                                }
                            }
                        }

                        MyHorizontalDivider()
                    }
                }

            }
        }

    }


    //x 废弃，一旋转屏幕，弹窗也会关闭，所以无所谓) 这个应该用remember，因为屏幕一旋转，选中列表会被清空，所以，就算显示删除对话框，也不知道该删什么
    val showRemoveFromGitDialog = rememberSaveable { mutableStateOf(false) }
    if (showRemoveFromGitDialog.value) {
        ConfirmDialog(
            title = stringResource(id = R.string.remove_from_git),
            text = stringResource(R.string.will_remove_selected_items_from_git_are_u_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showRemoveFromGitDialog.value = false }
        ) {
            //关闭弹窗
            showRemoveFromGitDialog.value = false

            //执行删除
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
                val selectedItems = selectedItems.value.toList()

                if (selectedItems.isEmpty()) {
                    Msg.requireShow(activityContext.getString(R.string.no_item_selected))
                    //退出选择模式和刷新页面
//                    filesPageQuitSelectionMode()
//                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                    return@doJobThenOffLoading  // 结束操作
                }

                //注意：如果选中的文件在不同的目录下，可能会失效，因为这里仅通过第一个元素查找仓库，多数情况下不会有问题，因为选择文件默认只能在同一目录选（虽然没严格限制导致有办法跳过）
                var repoWillUse = Libgit2Helper.findRepoByPath(selectedItems[0].fullPath)
                if (repoWillUse == null) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_dir_is_not_a_git_repo))
                    //退出选择模式和刷新页面
//                    filesPageQuitSelectionMode()
//                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                    return@doJobThenOffLoading  // 结束操作
                }

                //存在有效仓库

                // .use 的好处是用完会自动 close，省得手动关
                repoWillUse.use { repo ->
                    val repoWorkDirFullPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                    MyLog.d(TAG, "#RemoveFromGitDialog: will remove files from repo workdir: '${repoWorkDirFullPath}'")

                    val repoIndex = repo.index()

                    //开始循环，删除所有选中文件
                    selectedItems.forEachBetter {
                        val relativePathUnderRepo = getFilePathUnderParent(repoWorkDirFullPath, it.fullPath)
                        //存在有效仓库，且文件的仓库内相对路径不为空，且不是.git目录本身，且不是.git目录下的文件
                        Libgit2Helper.removeFromGit(repoIndex, relativePathUnderRepo, it.isFile)
                    }

                    //保存修改
                    repoIndex.write()

                }

                Msg.requireShow(activityContext.getString(R.string.success))

                //没必要退出选择模式，也没必要刷新目录，是从git移除，又不会影响这里显示的的文件，这显示的文件属于git的worktree区域
                //退出选择模式并刷新目录
//                filesPageQuitSelectionMode()
//                changeStateTriggerRefreshPage(needRefreshFilesPage)

            }
        }
    }


    val showCopyOrMoveOrExportFileErrDialog = rememberSaveable { mutableStateOf(false) }
//    val copyOrMoveOrExportFileErrList = mutableCustomStateListOf(stateKeyTag, "copyOrMoveOrExportFileErrList") { mutableListOf<PasteResult>() }
    val copyOrMoveOrExportFileErrMsg = rememberSaveable { mutableStateOf("") }
    val initCopyOrMoveOrExportFileErrDialog = { errList: List<PasteResult> ->
        val sb = StringBuilder()
        val suffix = "\n\n--------------------\n\n"
        sb.append("got ${errList.size} error(s):").append(suffix)
        errList.forEachBetter {
            sb.append("srcPath: ${it.srcPath}").append("\n\n")
            sb.append("targetPath: ${it.targetPath}").append("\n\n")
            sb.append("errMsg: ${it.exception?.localizedMessage ?: "unknow err"}").append(suffix)
        }

        copyOrMoveOrExportFileErrMsg.value = sb.removeSuffix(suffix).toString()
        showCopyOrMoveOrExportFileErrDialog.value = true
    }

    if (showCopyOrMoveOrExportFileErrDialog.value) {
        val closeAndRefreshPage = {
            //关弹窗
            showCopyOrMoveOrExportFileErrDialog.value = false

            //清空下错误信息
            copyOrMoveOrExportFileErrMsg.value = ""

            //退出选择模式，然后刷新页面
            filesPageQuitSelectionMode()  //退出选择模式是因为有可能部分已选中的条目可能已经被移动而失效了，懒得逐个检查，直接退出即可
            changeStateTriggerRefreshPage(needRefreshFilesPage)
        }

        val copyOrMoveOrExportFileErrMsg = copyOrMoveOrExportFileErrMsg.value
        CopyableDialog(
            title = stringResource(R.string.error),
            text = copyOrMoveOrExportFileErrMsg,
            onCancel = closeAndRefreshPage
        ) {
            clipboardManager.setText(AnnotatedString(copyOrMoveOrExportFileErrMsg))
            Msg.requireShow(activityContext.getString(R.string.copied))

            //这行最好放在拷贝msg后面，不然有可能先清空导致拷贝到空字符串
            closeAndRefreshPage()
        }
    }

    val copyOrMoveOrExportFile = copyOrMoveOrExportFile@{ srcList: List<FileItemDto>, targetFullPath: String, requireDeleteSrc: Boolean ->
//                if(pastMode.intValue == pastMode_Copy) {
//                    //执行拷贝
//                }else if(pastMode.intValue == pastMode_Move) {
//                    // 执行移动
//                }
//                fileNeedOverrideList.clear()
        //其实不管拷贝还是移动都要先拷贝，区别在于移动后需要删除源目录
        //如果发现同名，添加到同名列表，弹窗询问是否覆盖。
        doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff) {
            val ret = FsUtils.copyOrMoveOrExportFile(srcList.map { it.toFile() }, File(targetFullPath), requireDeleteSrc)
            if (ret.hasError()) {
                if (ret.code == Ret.ErrCode.srcListIsEmpty) {
                    Msg.requireShow(activityContext.getString(R.string.no_item_selected))
                } else if (ret.code == Ret.ErrCode.targetIsFileButExpectDir) {
                    Msg.requireShow(activityContext.getString(R.string.err_target_is_file_but_expect_dir))
                } else {  //若有错误信息则显示错误信息弹窗
                    val errList = ret.data
                    if (errList != null && errList.isNotEmpty()) {
                        initCopyOrMoveOrExportFileErrDialog(errList)
                    }
                }

                return@doJobThenOffLoading
            }

            //执行到这就说明操作成功完成了
            //显示成功提示
            Msg.requireShow(activityContext.getString(R.string.success))

            //退出选择模式和刷新页面
            filesPageQuitSelectionMode()
            changeStateTriggerRefreshPage(needRefreshFilesPage)
        }

    }

    val pasteMode_Move = 1
    val pasteMode_Copy = 2
    val pasteMode_None = 0  //不执行任何操作
    val pasteMode = rememberSaveable { mutableIntStateOf(pasteMode_None) }
    val setPasteModeThenShowPasteBar = { pastModeVal: Int ->
        pasteMode.intValue = pastModeVal
        isFileSelectionMode.value = false
        isPasteMode.value = true
    }


    val itemListForExport = mutableCustomStateListOf(stateKeyTag, "itemListForExport") { listOf<FileItemDto>() }
    val showSafImportDialog = rememberSaveable { mutableStateOf(false) }
    val showSafExportDialog = rememberSaveable { mutableStateOf(false) }
    val initSafExportDialog = { itemList: List<FileItemDto> ->
        itemListForExport.value.clear()
        itemListForExport.value.addAll(itemList)

        showSafImportDialog.value = false
        showSafExportDialog.value = true
    }
    val initSafImportDialog = {
        //检查目录是否可读
        val curPathReadable = try {
            File(currentPath()).canRead()
        } catch (e: Exception) {
            false
        }

        //若可读则显示弹窗，否则提示读取路径失败
        if (curPathReadable) {
            showSafExportDialog.value = false
            showSafImportDialog.value = true
        } else {
            Msg.requireShow(activityContext.getString(R.string.err_read_path_failed))
        }
    }
    // saf import export 共享这些状态
    val safImportExportOverwrite = rememberSaveable { mutableStateOf(false) }
    val choosenSafUri = mutableCustomStateOf<Uri?>(stateKeyTag, "choosenSafUri") { null }

    val chooseDirLauncher = rememberLauncherForActivityResult(MyOpenDocumentTree()) { uri ->
        //执行导出
        if (uri != null) {
            SafUtil.takePersistableRWPermission(activityContext.contentResolver, uri)

            choosenSafUri.value = uri
        }
    }

    val showImportExportErrorDialog = rememberSaveable { mutableStateOf(false) }
    val importExportErrorMsg = rememberSaveable { mutableStateOf("") }


    if (showSafExportDialog.value || showSafImportDialog.value) {
        val importOrExportText = if (showSafExportDialog.value) stringResource(R.string.export) else stringResource(R.string.import_str)
        val closeDialog = { showSafExportDialog.value = false; showSafImportDialog.value = false }
        ConfirmDialog2(
            title = importOrExportText,
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        CardButton(
                            onClick = {
                                chooseDirLauncher.launch(null)
                            },
                            content = {
                                Text(
                                    text = if (choosenSafUri.value == null) stringResource(R.string.select_path) else choosenSafUri.value.toString(),
                                    color = UIHelper.getCardButtonTextColor(enabled = true, inDarkTheme = inDarkTheme)
                                )
                            },
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    MyCheckBox(stringResource(R.string.overwrite_if_exist), safImportExportOverwrite)

                    MySelectionContainer {
                        DefaultPaddingText(stringResource(R.string.overwrite_files_note))
                    }
                }
            },
            onCancel = closeDialog,
            okBtnEnabled = choosenSafUri.value != null,
            okBtnText = importOrExportText
        ) {
            val trueExportFalseImport = showSafExportDialog.value

            closeDialog()

            val loadingText = activityContext.getString(if (trueExportFalseImport) R.string.exporting else R.string.importing)

            doJobThenOffLoading {
                val uri = choosenSafUri.value!!
                val conflictStrategy = if (safImportExportOverwrite.value) FsUtils.CopyFileConflictStrategy.OVERWRITE_FOLDER_AND_FILE else FsUtils.CopyFileConflictStrategy.SKIP
                val chosenDir = DocumentFile.fromTreeUri(activityContext, uri)
                if (chosenDir == null) {
                    Msg.requireShow(activityContext.getString(R.string.err_documentfile_is_null))
                    return@doJobThenOffLoading
                }

//            appContext.contentResolver.openOutputStream(chosenDir?.createFile("*/*", "test.txt")?.uri!!)
                try {
                    loadingOnCancellable(loadingText)

                    if (trueExportFalseImport) {
                        FsUtils.recursiveExportFiles_Saf(
                            contentResolver = activityContext.contentResolver,
                            targetDir = chosenDir,
                            srcFiles = itemListForExport.value.map<FileItemDto, File> { it.toFile() }.toTypedArray(),
                            canceled = { requireCancelAct.value },
                            conflictStrategy = conflictStrategy
                        )
                    } else {
                        FsUtils.recursiveImportFiles_Saf(
                            contentResolver = activityContext.contentResolver,
                            targetDir = File(currentPath()),
                            srcFiles = chosenDir.listFiles() ?: arrayOf<DocumentFile>(),
                            canceled = { requireCancelAct.value },
                            conflictStrategy = conflictStrategy
                        )
                    }

                    // throw RuntimeException("测试异常！")  passed
                    Msg.requireShow(activityContext.getString(R.string.success))
                } catch (cancelled: CancellationException) {
                    Msg.requireShow(activityContext.getString(R.string.canceled))
                } catch (e: Exception) {
                    MyLog.e(TAG, "#SafImportOrExportDialog err: " + e.stackTraceToString())
                    val errorMsg = "err: ${e.localizedMessage}"
                    //都显示弹窗了就不用toast了
//                    Msg.requireShow(errorMsg)

                    //设置错误信息，显示个错误弹窗
                    importExportErrorMsg.value = errorMsg
                    showImportExportErrorDialog.value = true
                } finally {
                    loadingOffCancellable()

                    //如果是导入模式，结束后需要刷新页面
                    if (!trueExportFalseImport) {
                        changeStateTriggerRefreshPage(needRefreshFilesPage)
                    }
                }
            }

        }
    }


    val showSafDiffDialog = rememberSaveable { mutableStateOf(false) }
    val safDiffResultStr = rememberSaveable { mutableStateOf("") }
    val initSafDiffDialog = {
        safDiffResultStr.value = ""
        showSafDiffDialog.value = true
    }

    if (showSafDiffDialog.value) {
        val closeDialog = { showSafDiffDialog.value = false; cancelAct() }

        ConfirmDialog2(
            title = DevFeature.safDiff_text,
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        CardButton(
                            onClick = {
                                chooseDirLauncher.launch(null)
                            },
                            content = {
                                Text(
                                    text = if (choosenSafUri.value == null) stringResource(R.string.select_path) else choosenSafUri.value.toString(),
                                    color = UIHelper.getCardButtonTextColor(enabled = true, inDarkTheme = inDarkTheme)
                                )
                            },
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    //正在运行，点击可取消；未运行，点击则执行比较
                    SingleLineCardButton(text = if (cancellableActRunning.value) "Cancel!" else "Diff!", enabled = true) {
                        if (cancellableActRunning.value) {  //正在运行，点击则取消
                            cancelAct()
                        } else {  //未运行则点击执行比较
                            // compare
                            doJobThenOffLoading(
                                loadingOn = { startCancellableAct() },

                                loadingOff = { stopCancellableAct() },

                                loadingText = "Comparing..."
                            ) {
                                val uri = choosenSafUri.value!!
                                val chosenDir = DocumentFile.fromTreeUri(activityContext, uri)
                                if (chosenDir == null) {
                                    Msg.requireShow(activityContext.getString(R.string.err_documentfile_is_null))
                                    return@doJobThenOffLoading
                                }

//            appContext.contentResolver.openOutputStream(chosenDir?.createFile("*/*", "test.txt")?.uri!!)
                                try {
                                    safDiffResultStr.value = "Comparing..."

                                    val startAt = System.currentTimeMillis()

                                    //递归结果会存到这个result里
                                    val result = SafAndFileCmpUtil.SafAndFileCompareResult()

                                    SafAndFileCmpUtil.recursiveCompareFiles_Saf(
                                        contentResolver = activityContext.contentResolver,
                                        safFiles = chosenDir.listFiles() ?: arrayOf(),
                                        files = File(currentPath()).listFiles() ?: arrayOf(),
                                        result = result,
                                        canceled = { requireCancelAct.value }
                                    )

                                    val spentTime = (System.currentTimeMillis() - startAt) / 1000  // 转换 毫秒 为 秒
                                    safDiffResultStr.value = "spent time: $spentTime second(s)\n\n------------\n\n$result"

                                    Msg.requireShow(activityContext.getString(R.string.done))
                                } catch (cancelled: CancellationException) {
                                    safDiffResultStr.value = ""
                                    Msg.requireShow(activityContext.getString(R.string.canceled))
                                } catch (openInputStreamFailed: OpenInputStreamFailed) {
                                    val errMsg = "open input stream for uri failed"
                                    safDiffResultStr.value = openInputStreamFailed.localizedMessage ?: errMsg
                                    Msg.requireShow(errMsg)
                                } catch (e: Exception) {
                                    MyLog.e(TAG, "#SafDiffDialog err: " + e.stackTraceToString())
                                    val errorMsg = "err: ${e.localizedMessage}"
                                    //都显示弹窗了就不用toast了
//                    Msg.requireShow(errorMsg)

                                    //设置错误信息
                                    safDiffResultStr.value = errorMsg
                                }
                            }

                        }
                    }

                    if (safDiffResultStr.value.isNotBlank()) {
                        Spacer(Modifier.height(20.dp))

                        MySelectionContainer {
                            CardButton(
                                enabled = false,
                                maxHeight = 500,
                                onClick = {},
                                content = {
                                    Text(safDiffResultStr.value)
                                },
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                }
            },
            onCancel = closeDialog,
            cancelBtnText = stringResource(R.string.close),
            showOk = false
        ) {  // onOk
        }
    }

//    val userChosenExportDirUri = StateUtil.getRememberSaveableState<Uri?>(initValue = null)
    //ActivityResultContracts.OpenMultipleDocuments() 多选文件，这个应该可用来导入，不过现在有分享，足够了，虽然分享不能导入目录

    val chooseDirLauncherThenExport = rememberLauncherForActivityResult(MyOpenDocumentTree()) exportSaf@{ uri ->
        //执行导出
        if (uri != null) {
            SafUtil.takePersistableRWPermission(activityContext.contentResolver, uri)

            val loadingText = activityContext.getString(R.string.exporting)

            doJobThenOffLoading {
                val chosenDir = DocumentFile.fromTreeUri(activityContext, uri)
                if (chosenDir == null) {
                    Msg.requireShow(activityContext.getString(R.string.err_get_export_dir_failed))
                    return@doJobThenOffLoading
                }
//            appContext.contentResolver.openOutputStream(chosenDir?.createFile("*/*", "test.txt")?.uri!!)
                try {
                    loadingOnCancellable(loadingText)

                    FsUtils.recursiveExportFiles_Saf(
                        contentResolver = activityContext.contentResolver,
                        targetDir = chosenDir,
                        srcFiles = selectedItems.value.map<FileItemDto, File> { it.toFile() }.toTypedArray(),
                        canceled = { requireCancelAct.value }
                    )
                    // throw RuntimeException("测试异常！")  passed
                    Msg.requireShow(activityContext.getString(R.string.export_success))
                } catch (cancelled: CancellationException) {
                    Msg.requireShow(activityContext.getString(R.string.canceled))
                } catch (e: Exception) {
                    MyLog.e(TAG, "#exportSaf@ err: " + e.stackTraceToString())
                    val exportErrStrRes = activityContext.getString(R.string.export_err)
                    Msg.requireShow(exportErrStrRes)
                    importExportErrorMsg.value = "$exportErrStrRes: " + e.localizedMessage
                    showImportExportErrorDialog.value = true
                } finally {
                    loadingOffCancellable()
                }
            }

        } else {  //用户如果没选目录，uri就会等于null
            Msg.requireShow(activityContext.getString(R.string.export_canceled))
        }
    }

    if (showInitRepoDialog.value) {
        val selctedDirs = initRepoList.value

        if (selctedDirs.isEmpty()) {
            showInitRepoDialog.value = false
            Msg.requireShow(stringResource(R.string.no_dir_selected))
        } else {
            ConfirmDialog(
                title = stringResource(R.string.init_repo),
                requireShowTextCompose = true,
                textCompose = {
                    ScrollableColumn {
                        MySelectionContainer {
                            Text(stringResource(R.string.will_init_selected_folders_to_git_repos_are_you_sure))
                        }

                        Spacer(Modifier.height(10.dp))

                        MyCheckBox(text = "bare", value = initRepoAsBare)
                    }
                },
                okBtnEnabled = selctedDirs.isNotEmpty(),
                onCancel = { showInitRepoDialog.value = false }
            ) {
                showInitRepoDialog.value = false
                val isBbare = initRepoAsBare.value
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                    try {
                        var successCnt = 0
                        selctedDirs.forEachBetter { dirPath ->
                            try {
                                Libgit2Helper.initGitRepo(dirPath, isBbare)
                                successCnt++
                            } catch (e: Exception) {
//                            Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                                MyLog.e(TAG, "init repo in FilesPage err: path=${dirPath}, err=${e.localizedMessage}")
                            }
                        }

                        Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_inited), listOf("" + successCnt)))
                    } finally {
                        if (enableFilterState.value.not()) {
                            changeStateTriggerRefreshPage(needRefreshFilesPage)
                        } else {
                            val filterList = filterList.value
                            filterList.toList().forEachIndexedBetter { idx, item ->
                                if (selctedDirs.contains(item.fullPath)) {
                                    filterList[idx] = FileItemDto.genFileItemDtoByFile(File(item.fullPath), activityContext)
                                }
                            }
                        }
                    }
                }
            }
        }

    }


    if (showImportAsRepoDialog.value) {
        val selctedDirs = importAsRepoList.value

        if (selctedDirs.isEmpty()) {
            showImportAsRepoDialog.value = false
            Msg.requireShow(stringResource(R.string.no_dir_selected))
        } else {
            ConfirmDialog(
                title = stringResource(R.string.import_repo),
                requireShowTextCompose = true,
                textCompose = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        GrantManageStoragePermissionClickableText(activityContext)

                        Spacer(Modifier.height(5.dp))

                        MyCheckBox(text = stringResource(R.string.paths_are_repo_parent_dir), value = isReposParentFolderForImport)

                        Spacer(Modifier.height(5.dp))

                        if (isReposParentFolderForImport.value) {
                            MySelectionContainer {
                                DefaultPaddingText(stringResource(R.string.will_scan_repos_under_folders))
                            }
                        }
                    }
                },
                okBtnText = stringResource(R.string.ok),
                cancelBtnText = stringResource(R.string.cancel),
                okBtnEnabled = selctedDirs.isNotEmpty(),
                onCancel = { showImportAsRepoDialog.value = false },
            ) {
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.importing)) {
                    val importRepoResult = ImportRepoResult()
                    try {
                        selctedDirs.forEachBetter { dirPath ->
                            val result = AppModel.dbContainer.repoRepository.importRepos(dir = dirPath, isReposParent = isReposParentFolderForImport.value)
                            importRepoResult.all += result.all
                            importRepoResult.success += result.success
                            importRepoResult.failed += result.failed
                            importRepoResult.existed += result.existed
                        }

                        showImportAsRepoDialog.value = false

                        Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_imported), listOf("" + importRepoResult.success)))
                    } catch (e: Exception) {
                        //出错的时候，importRepoResult的计数不一定准，有可能比实际成功和失败的少，不过不可能多
                        MyLog.e(TAG, "import repo from FilesPage err: importRepoResult=$importRepoResult, err=" + e.stackTraceToString())
                        Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    } finally {
                        // because import doesn't change Files page, so need not do anything yet
                    }
                }

            }
        }
    }


    if (showDetailsDialog.value) {
        val itemList = details_itemList.value
        ConfirmDialog2(
            title = stringResource(id = R.string.details),
            requireShowTextCompose = true,
            //用compose，这样就可仅更新大小记数，size(counting...): 变化的size
            textCompose = {
                MySelectionContainer {
                    ScrollableColumn {
                        //这显示的是选中条目数，如果size==1显示，会产生是文件夹内文件和文件夹数量的错觉，令人困惑
                        if (itemList.size > 1) {
                            Row {
                                Text(
                                    text = replaceStringResList(
                                        stringResource(R.string.items_n1_n2_folders_n3_files),
                                        listOf("" + details_AllCount.intValue, "" + details_FoldersCount.intValue, "" + details_FilesCount.intValue)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        Row {
                            // if counting not finished: "123MB..." else "123MB"
                            Text(
                                text = replaceStringResList(
                                    stringResource(R.string.size_n),
                                    listOf(getHumanReadableSizeStr(details_ItemsSize.longValue))
                                ) + (if (details_CountingItemsSize.value) Cons.oneChar3dots else "")
                            )
                        }


                        //when only selected 1 item, show it's name and path
                        if (itemList.size == 1) {
                            val item = itemList[0]

                            Spacer(modifier = Modifier.height(15.dp))

                            Row {
                                Text(text = stringResource(R.string.name) + ": " + item.name)
                            }

                            if (item.isDir) {
                                Spacer(modifier = Modifier.height(15.dp))
                                Row {
                                    Text(
                                        text = replaceStringResList(
                                            stringResource(R.string.item_count_n),
                                            listOf(replaceStringResList(stringResource(R.string.folder_n_file_m), listOf("" + item.folderCount, "" + item.fileCount)))
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Row {
                                Text(text = stringResource(R.string.path) + ": " + item.fullPath)
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Row {
                                Text(text = stringResource(R.string.last_modified) + ": " + item.lastModifiedTime)
                            }

//                            Spacer(modifier = Modifier.height(15.dp))

                        }

                    }
                }
            },

            //隐藏取消按钮，点击ok和点击弹窗外区域关闭弹窗
            showCancel = false,  //隐藏取消按钮
            onCancel = {},  //因为隐藏了取消按钮，所以执行操作传空即可
            onDismiss = { showDetailsDialog.value = false },  //点击弹窗外区域执行的操作
            okBtnText = stringResource(R.string.close),
        ) {  // onOk
            showDetailsDialog.value = false
        }
    }


    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }

    val goToParentAndScrollToItem = { item: FileItemDto ->
        showSelectedItemsShortDetailsDialog.value = false
        changeStateTriggerRefreshPage(needRefreshFilesPage, StateRequestType.goToParentAndScrollToItem, item.fullPath)
    }

    val showSelectedItemsShortDetailsDialogForImportMode = rememberSaveable { mutableStateOf(false) }
    if (showSelectedItemsShortDetailsDialog.value) {
        SelectedFileItemsDialog(
            list = selectedItems.value,
            itemName = { it.name },
            itemPath = { it.fullPath },
            itemIsDir = { it.isDir },
            removeItem = { switchItemSelected(it) },
            clearAll = { selectedItems.value.clear() },
            showFileIcon = true,
            fileIconOnClick = { goToParentAndScrollToItem(it) },
            closeDialog = { showSelectedItemsShortDetailsDialog.value = false }
        )
    }

    if (showSelectedItemsShortDetailsDialogForImportMode.value) {
        SelectedFileItemsDialog(
            title = stringResource(R.string.import_str),
            list = requireImportUriList.value,
            itemName = { FsUtils.getFileRealNameFromUri(activityContext, it) ?: it.toString() },
            itemPath = { it.toString() },

            // BEGIN: no need show folder icon for uri
            // because usually only can share files, even can try import folder, but will got an err at the end,
            //  and no action need to do when clicked the icon, so just hidden it
            itemIsDir = { false },
            showFileIcon = false,
            fileIconOnClick = {},
            // END: no need show folder icon for uri

            removeItem = { requireImportUriList.value.remove(it) },
            clearAll = { requireImportUriList.value.clear() },
            closeDialog = { showSelectedItemsShortDetailsDialogForImportMode.value = false },
        )
    }


    val showIgnoreDialog = rememberSaveable { mutableStateOf(false) }
    if (showIgnoreDialog.value) {
        val selectedItems = selectedItems.value

        GitIgnoreDialog(
            showIgnoreDialog = showIgnoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            getRepository = {
                try {
                    var repoWillUse = Libgit2Helper.findRepoByPath(selectedItems[0].fullPath)
                    if (repoWillUse == null) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.err_dir_is_not_a_git_repo))
                    }

                    repoWillUse
                } catch (e: Exception) {
                    MyLog.e(TAG, "#getRepository err: ${e.stackTraceToString()}")
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")

                    null
                }
            },
            getIgnoreItems = { repoWorkDirFullPath: String ->
                //这里不用捕获异常，若出错，会在onCatch处理
                val selectedItemList = selectedItems.toList()
                if (selectedItemList.isEmpty()) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_item_selected))
                }

                selectedItemList.map {
                    IgnoreItem(pathspec = getFilePathUnderParent(repoWorkDirFullPath, it.fullPath), isFile = it.isFile)
                }
            },
            onCatch = { e: Exception ->
                val errMsg = e.localizedMessage
                Msg.requireShowLongDuration("err: " + errMsg)
                MyLog.e(TAG, "ignore files err: ${e.stackTraceToString()}")
            },
            onFinally = { repoWorkDirFullPath ->
                //过滤模式关闭 且 当前目录是仓库workdir根目录则刷新页面
                //过滤模式不刷新页面：过滤模式涉及递归搜索，忽略文件没必要重新过滤，顶多遗漏一个新增的.gitignore文件，可以接受
                //当前目录非仓库workdir根目录不刷新页面：因为只有仓库根目录可能的.gitignore文件可能会被创建或更新，所以只有当前目录为仓库workdir根目录，才有必要刷新
                if (enableFilterState.value.not() && currentPath() == repoWorkDirFullPath) {
                    //因为有可能会添加 .gitignore 文件，所以需要刷新下页面
                    changeStateTriggerRefreshPage(needRefreshFilesPage)
                }
            }
        )
    }


    val countNumOnClickForSelectAndPasteModeBottomBar = {
//        val sb = StringBuilder()
//        selectedItems.value.forEach {
//            sb.appendLine("${it.name}, ${if(it.isDir) "dir" else "file"}, ${it.fullPath}").appendLine()
//        }
//        selectedItemsShortDetailsStr.value = sb.removeSuffix("\n").toString()
        showSelectedItemsShortDetailsDialog.value = true
    }

    //Bottom bar，一个是选择模式，一个是粘贴模式
    val isFileChooserAndSingleDirType = isFileChooser && fileChooserType == FileChooserType.SINGLE_DIR
    if (isFileChooserAndSingleDirType || isFileSelectionMode.value) {
        val selectionModeIconList = if (isFileChooserAndSingleDirType) {
            listOf(
                Icons.Filled.Check, // 确定选择这个目录
            )
        } else {
            listOf(
                Icons.Filled.Delete,
                Icons.Filled.ContentCut,
                Icons.Filled.ContentCopy,
                Icons.Filled.SelectAll,  //全选
            )
        }

        val selectAll = {
            val list = if (enableFilterState.value) filterList.value else currentPathFileList.value

            list.toList().forEachBetter {
                selectItem(it)
            }

            Unit
        }

        val selectionModeIconTextList = if (isFileChooserAndSingleDirType) {
            listOf(
                stringResource(R.string.confirm), // 确定选择
            )
        } else {
            listOf(
                stringResource(R.string.delete),
                stringResource(R.string.move),
                stringResource(R.string.copy),
                stringResource(R.string.select_all),
            )
        }

        val confirmForChooser = { path: String ->
            updateSelectedPath(path)
            naviUp()
        }

        val confirmForMultiChooser = { path: List<FileItemDto> ->
            // not impemented yet
        }

        val selectionModeIconOnClickList = if (isFileChooserAndSingleDirType) {
            listOf(
                confirm@{
                    confirmForChooser(currentPath())
                }
            )
        } else {
            listOf<() -> Unit>(
                delete@{
                    initDelFileDialog(selectedItems.value)
                },
                move@{
                    setPasteModeThenShowPasteBar(pasteMode_Move)
                },
                copy@{
                    setPasteModeThenShowPasteBar(pasteMode_Copy)
                },
                selectAll@{
                    selectAll()
                }
            )
        }

        val selectionModeIconEnableList = if (isFileChooserAndSingleDirType) {
            listOf(
                //单选模式永远启用确认，点击确认即代表选择当前目录，如果选文件的话，需要额外处理，点击文件条目时获取文件路径并返回，就不需要显示这个确认了
                confirm@{ true },  //是否启用确认
            )
        } else {
            listOf(
                delete@{ getSelectedFilesCount() > 0 },  //是否启用delete
                move@{ getSelectedFilesCount() > 0 },  //是否启用move
                copy@{ getSelectedFilesCount() > 0 },  //是否启用copy
                selectAll@{ true },  //是否启用全选
            )
        }

        val selectionModeMoreItemTextList = (listOf(
            stringResource(id = R.string.ignore),
            stringResource(id = R.string.remove_from_git),
            stringResource(id = R.string.details),
            stringResource(R.string.share),

            if (proFeatureEnabled(importReposFromFilesTestPassed)) stringResource(id = R.string.import_as_repo) else "",  // empty string will be ignore when display menu items
            if (proFeatureEnabled(initRepoFromFilesPageTestPassed)) stringResource(id = R.string.init_repo) else "",
            stringResource(R.string.add_storage_path),
            if (showImportForBottomBar) stringResource(R.string.import_str) else "", // 底栏没必要显示import，避免以后修改，保留代码，用开关变量控制是否显示
            stringResource(R.string.export),
        ))

        val selectionModeMoreItemOnClickList = (listOf(
            ignore@{
                showIgnoreDialog.value = true
            },
            removeFromGit@{
                showRemoveFromGitDialog.value = true
            },
            details@{
                initDetailsDialog(selectedItems.value.toList())
            },

            share@{
                shareFiles(selectedItems.value)
            },

            importAsRepo@{
                initImportAsRepoDialog(
                    selectedItems.value.filterAndMap(
                        predicate = { it.isDir },
                        transform = { it.fullPath }
                    )
                )
            },
            initRepo@{
                initInitRepoDialog(
                    selectedItems.value.filterAndMap(
                        predicate = { it.isDir },
                        transform = { it.fullPath }
                    )
                )
            },

            addStoragePath@{
                val selectedItems = selectedItems.value
                if(selectedItems.isEmpty()) {
                    return@addStoragePath
                }

                val storagePaths = StoragePathsMan.get()

                var needSave = false
                selectedItems.forEachBetter {
                    if(addStoragePath(
                        newPath =  it.fullPath,
                        storagePaths = storagePaths,
                        callSave = false,
                        showMsg = false
                    )) {
                        needSave = true
                    }
                }

                if(needSave) {
                    StoragePathsMan.save(storagePaths)
                }

                Msg.requireShow(activityContext.getString(R.string.done))
            },

            import@{
                initSafImportDialog()
//                choosenSafUri.value = null

                //显示选择导出目录的文件选择界面
//                chooseDirLauncher.launch(null)
            },

            export@{
                initSafExportDialog(selectedItems.value.toList())
//                choosenSafUri.value = null  //不设为null了，这样可以直接用上次的uri

                //显示选择导出目录的文件选择界面
//                chooseDirLauncher.launch(null)
            },
        ))

        val hasItemSelected = { getSelectedFilesCount() > 0 }
        val hasDirSelected = { selectedItems.value.any { it.isDir } }
        val hasFileSelected = { selectedItems.value.any { it.isFile } }

        val selectionModeMoreItemEnableList = (listOf(
            hasItemSelected, //是否启用ignore
            hasItemSelected, //是否启用remove from git
            hasItemSelected, //是否启用details
            hasFileSelected, // enable `Share` or not

//            {selectedItems.value.indexOfFirst{it.isDir} != -1}  //enable import as repo. (if has dirs in selected items, then enable else disbale) (after clicked then check better than check at every time selected list change)
            hasDirSelected,  // import as repos
            hasDirSelected, // init repo

            hasDirSelected, // addStoragePath

            //是否启用import （这个实际上已经弃用了，挪顶栏了，但这个函数要保留占位）
            {
                //只要当前目录可读，就启用import
                try {
                    File(currentPath()).canRead()
                } catch (_: Exception) {
                    false
                }
            },

            hasItemSelected, //是否启用export
        ))


        if (!isLoading.value) {
            if (isFileChooser) {
                BottomBar(
                    showClose = false,
                    showSelectedCount = false,
                    quitSelectionMode = {},
                    iconList = selectionModeIconList,
                    iconTextList = selectionModeIconTextList,
                    iconDescTextList = selectionModeIconTextList,
                    iconOnClickList = selectionModeIconOnClickList,
                    iconEnableList = selectionModeIconEnableList,
                    moreItemTextList = listOf(),
                    moreItemOnClickList = listOf(),
                    moreItemEnableList = listOf(),
                    getSelectedFilesCount = getSelectedFilesCount,
                    countNumOnClickEnabled = true,
                    countNumOnClick = countNumOnClickForSelectAndPasteModeBottomBar,
                    reverseMoreItemList = true
                )
            } else {
                BottomBar(
                    quitSelectionMode = filesPageQuitSelectionMode,
                    iconList = selectionModeIconList,
                    iconTextList = selectionModeIconTextList,
                    iconDescTextList = selectionModeIconTextList,
                    iconOnClickList = selectionModeIconOnClickList,
                    iconEnableList = selectionModeIconEnableList,
                    moreItemTextList = selectionModeMoreItemTextList,
                    moreItemOnClickList = selectionModeMoreItemOnClickList,
                    moreItemEnableList = selectionModeMoreItemEnableList,
                    getSelectedFilesCount = getSelectedFilesCount,
                    countNumOnClickEnabled = true,
                    countNumOnClick = countNumOnClickForSelectAndPasteModeBottomBar,
                    reverseMoreItemList = true
                )
            }
        }
    }


    //导入模式

    val quitImportMode = {
        isImportMode.value = false
        requireImportUriList.value.clear()
    }

    val getRequireUriFilesCount = { requireImportUriList.value.size }

    if (isImportMode.value) {
        val selectionModeIconList = listOf(
            Icons.Filled.FileDownload,
        )
        val selectionModeIconTextList = listOf(
            stringResource(R.string.import_str),
        )
        val selectionModeIconOnClickList = listOf(
            importFiles@{
                doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.importing)) {
//                    val successList = mutableListOf<String>()
//                    val failedList = mutableListOf<String>()
                    val sb = StringBuilder()
                    var succCnt = 0
                    var failedCnt = 0

                    val dest = currentPath()
                    var previousFilePath = ""
                    var curTarget: File? = null

                    requireImportUriList.value.toList().forEachBetter forEach@{ it: Uri? ->
                        try {
                            if (it != null && it.path != null && it.path!!.length > 0) {
                                //从这到更新curTarget，curTarget和perviousFilePath都应该相同

                                //有的文件管理器提供的路径即使执行到这里也是转码过的，
                                // 所以，File(Uri)没法获取到对应文件或目录的真实信息，文件名也依然是转码过的，
                                // 如果对应条目是个文件，依然能成功拷贝，不过文件名是带url编码的，例如 “%32abc”之类的，
                                //这里不能尝试再解码，因为有可能真实文件名本身就包含%等字符；
                                //如果带编码的文件是个目录，则有些特殊，上面说了，file无法获取带url编码的文件信息，
                                // 也无法判断对应条目是目录还是文件，所以会执行拷贝，然后因为对应条目是个目录而获取InputStream失败，最后进入异常代码块
                                //不过改用DocumentFile来判断的话，应该比File要更准确一些，判断错的可能性也更小一些(打脸了，经过我的测试，并不准，甚至会把目录判断成文件，还不如用file判断呢)
                                //经过我的测试：file判断有的文件既不是目录又不是文件，但基本可以确保如果isDirectory返回true就真的是目录，isFile返回true就真的是文件，而DocumentFile则有可能即使isFile返回true，也不是个文件而是目录。
//                                val srcDocumentFile = FsUtils.getDocumentFileFromUri(appContext, it)

                                val src = File(it.path!!)
                                if (src.isDirectory) {  //不支持拷贝目录
                                    failedCnt++
                                    sb.appendLine("'${it.path}'" + ": is a directory, only support import files!")
                                    return@forEach
                                }

                                //x 作废)执行到这有可能获取srcDocumentFile失败，其值为null；也有可能成功，且判断其是文件，下面不再做判断，直接拷贝，反正有异常捕获兜底

                                //尝试获取真实文件名
                                var srcFileName = FsUtils.getFileRealNameFromUri(activityContext, it)
                                if (srcFileName == null) {  //获取真实文件名失败
                                    //尝试获取uri中的文件名，可能和真实文件名不符
                                    srcFileName = src.name
                                    MyLog.w(TAG, "#importFiles@: getFileRealNameFromUri() return null, will use src.name:${srcFileName}")
                                    if (srcFileName.isNullOrEmpty()) {  //获取文件名失败
                                        //真实文件名和src.name都获取失败，生成个随机文件名，不过如果代码执行到这，我估计多半拷贝不了文件，连文件名都没有，一般是哪里出问题了才会这样
                                        val randomName = getShortUUID()
                                        srcFileName = randomName
                                        MyLog.w(TAG, "#importFiles@:src.name is null or empty, will use a random name:${srcFileName}")
                                    }
                                }

                                //拷贝
                                val target = FsUtils.getANonExistsFile(File(dest, srcFileName))  //获得一个不重名的文件
                                //从这里到操作成功，curTarget和previousFilePath都应该不同，操作成功后，两者相同
                                curTarget = File(target.canonicalPath)  //获得target后，立刻创建一个拷贝，用来在异常时判断是否更新了target来决定是否删除target
//                                println("target.canonicalPath:::"+target.canonicalPath)
                                val inputStream = activityContext.contentResolver.openInputStream(it)
                                if (inputStream == null) {
                                    failedCnt++
                                    sb.appendLine("'${it.path}'" + ": can't read!")
                                    return@forEach
                                }
                                //拷贝并自动关流
                                inputStream.use { input ->
                                    target.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
//                                inputStream.copyTo(target.outputStream())  //还得手动关流，麻烦
                                succCnt++
                                //只有操作成功后才更新上一文件path，这样的话，如果拷贝失败，上一路径和当前路径就会不同，就能判断是否换了target了，从而避免误删之前的target
                                previousFilePath == curTarget?.canonicalPath ?: ""
                            } else {
                                failedCnt++
                                sb.appendLine("uri is null!")
                            }

                        } catch (e: Exception) {
                            //检查目标文件是否存在，如果存在，删除 。 算了，不删了，让用户自己看着办吧！如果拷贝失败，有可能依然创建了文件，但文件大小为0，但如果不为0呢？我还得判断，算了，不管了，让用户自己看着办吧！算了，还是删一下吧！
                            failedCnt++
                            sb.appendLine((it?.path ?: "/fileNameIsNull/") + ": error: " + e.localizedMessage)

                            //检查，确保target更新了而不是上个文件，避免target更新前就异常导致删除之前成功复制的target，如果target确实更新了且执行失败，且文件大小为0，删除文件
                            try {
                                if (curTarget != null) {
                                    val curFilePath = curTarget!!.canonicalPath
                                    if (curFilePath.isNotBlank() && curFilePath != previousFilePath && curTarget!!.exists() && curTarget!!.length() <= 0) {
                                        curTarget!!.delete()
                                    }
                                }
                            } catch (e: Exception) {
                                // 不记录日志了，万一用户拷贝大量文件且出错，会记一堆，不太好
//                                e.printStackTrace()
                            }
                        }
                    }


                    //更新计数和错误条目字符串状态变量
                    successImportCount.intValue = succCnt
                    failedImportCount.intValue = failedCnt
                    failedImportListStr.value = sb.toString()

                    if (failedCnt < 1) {  //成功显示提示信息
                        Msg.requireShow(activityContext.getString(R.string.import_success))
                    } else {  //失败显示弹窗，可复制失败结果
//                        successImportList.clear()
//                        successImportList.addAll(successList)
//                        failedImportList.clear()
//                        failedImportList.addAll(failedList)

                        showImportResultDialog.value = true
                    }

                    quitImportMode()
                    changeStateTriggerRefreshPage(needRefreshFilesPage)

                }

                Unit
            },
        )
        val selectionModeIconEnableList = listOf(
            { requireImportUriList.value.isNotEmpty() },
        )

        val countNumOnClickForImportMode = {
//            val sb = StringBuilder()
//            requireImportUriList.value.forEach {
//                sb.appendLine(it.path).appendLine()
//            }
//
//            selectedItemsShortDetailsStrForImportMode.value = sb.removeSuffix("\n").toString()
            showSelectedItemsShortDetailsDialogForImportMode.value = true
        }

        if (!isLoading.value) {
            BottomBar(
                quitSelectionMode = quitImportMode,
                iconList = selectionModeIconList,
                iconTextList = selectionModeIconTextList,
                iconDescTextList = selectionModeIconTextList,
                iconOnClickList = selectionModeIconOnClickList,
                iconEnableList = selectionModeIconEnableList,
                moreItemTextList = listOf(),
                moreItemOnClickList = listOf(),
                moreItemEnableList = listOf(),
                getSelectedFilesCount = getRequireUriFilesCount,
                countNumOnClickEnabled = true,
                countNumOnClick = countNumOnClickForImportMode
            )
        }
    }



    if (showImportExportErrorDialog.value) {
        val closeDialog = { showImportExportErrorDialog.value = false; importExportErrorMsg.value = "" }

        //显示导出失败弹窗，包含错误信息且可拷贝
        CopyableDialog(
            title = stringResource(R.string.error),
            text = importExportErrorMsg.value,
            onCancel = closeDialog
        ) { //onOk
            //先取出错误信息
            val errMsg = importExportErrorMsg.value

            //关弹窗（会清空错误信息，所以之前要先取出）
            closeDialog()

            //拷贝信息
            clipboardManager.setText(AnnotatedString(errMsg))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }

    val showExportDialog = rememberSaveable { mutableStateOf(false) }

    if (showExportDialog.value) {
        ConfirmDialog(
            title = stringResource(id = R.string.export),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Row {
                        Text(text = stringResource(id = R.string.will_export_files_to) + ":")

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = FsUtils.appExportFolderNameUnderDocumentsDirShowToUser,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Text(text = stringResource(id = R.string.are_you_sure))
                    }

                }
            },
            onCancel = { showExportDialog.value = false }
        ) onOk@{
            showExportDialog.value = false
            val ret = FsUtils.getExportDirUnderPublicDocument()
            if (ret.hasError() || ret.data == null || !ret.data!!.exists()) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.get_default_export_folder_failed_plz_choose_one))
                //如果获取默认export目录失败，弹出文件选择器，让用户选个目录
                chooseDirLauncherThenExport.launch(null)  //这里传的null是弹出的界面的起始文件夹
                return@onOk
            }

            copyOrMoveOrExportFile(selectedItems.value, ret.data!!.canonicalPath, false)
        }

    }





    if (isPasteMode.value) {
        val iconList = listOf(
            Icons.Filled.ContentPaste,
            if (pasteMode.intValue == pasteMode_Move) Icons.Filled.ContentCut else Icons.Filled.ContentCopy
        )
        val iconTextList = listOf(
            stringResource(R.string.paste),
            if (pasteMode.intValue == pasteMode_Move) stringResource(R.string.cut) else stringResource(R.string.copy)
        )
        val iconOnClickList = listOf(
            paste@{
                copyOrMoveOrExportFile(selectedItems.value, currentPath(), pasteMode.intValue == pasteMode_Move)  //最后一个参数代表是否删除源，如果是move，则删除
                Unit
            },
            cutOrCopyIndicator@{},

            )

        val iconEnableList = listOf(
            { getSelectedFilesCount() > 0 },  //是否启用paste
            cutOrCopyIndicator@{ false },  //永远禁用，只用来指示是cut还是copy
        )


        val quitPasteAndBackToSelectionMode = {
            isPasteMode.value = false  //关闭粘贴模式
            isFileSelectionMode.value = true  //开启选择模式
        }

        if (!isLoading.value) {
            BottomBar(
                quitSelectionMode = quitPasteAndBackToSelectionMode,
                iconList = iconList,
                iconTextList = iconTextList,
                iconDescTextList = iconTextList,
                iconOnClickList = iconOnClickList,
                iconEnableList = iconEnableList,
                moreItemTextList = listOf(),
                moreItemOnClickList = listOf(),
                moreItemEnableList = listOf(),
                getSelectedFilesCount = getSelectedFilesCount,
                countNumOnClickEnabled = true,
                countNumOnClick = countNumOnClickForSelectAndPasteModeBottomBar
            )
        }

    }

//    if(showOverrideFilesDialog.value) {
//        ConfirmDialog(
//            title = stringResource(id = R.string.override),
//            requireShowTextCompose = true,
//            textCompose = {
//                Row {
//                    Text(text = stringResource(R.string.file_override_ask_text))
//                }
//              Column(modifier = Modifier
//                        .verticalScroll(rememberScrollState())
//              ) {
//
//                  fileNeedOverrideList.forEach {
//                      Row {
//                          Text(text = it.name+(if(it.isDirectory) File.separator else ""))  //如果是目录，后面加个 "/"
//                      }
//                  }
//
//              }
//            },
//            onCancel = { showOverrideFilesDialog.value=false }
//        ) {
//            //关闭弹窗
//            showOverrideFilesDialog.value=false
//            //执行删除
//            doJobThenOffLoading {
//                selectedItems.forEach {
//                    fileNeedOverrideList.forEach {
//                        val target = File(it.canonicalPath)
//                        if(target.exists()) {
//                            target.de  //这里本来想如果存在则删除，但是，不行，如果是目录的话，不行，得合并而不是覆盖！算了，如果重名直接自动重命名好了，覆盖太麻烦了
//                        }else {
//                            src.copyRecursively(target, false)  //false，禁用覆盖，不过，只有文件存在时才需要覆盖，而上面其实已经判断过了，所以执行到这，target肯定不存在，也用不着覆盖，但以防万一，这个值传false，避免错误覆盖文件
//                            if(pastMode.intValue == pastMode_Move) {  //如果是“移动(又名“剪切”)“，则删除源
//                                src.deleteRecursively()
//                            }
//                        }
//                    }
//
//                }
//
//                Msg.requireShow(appContext.getString(R.string.success))
//                //退出选择模式并刷新目录
//                filesPageQuitSelectionMode()
//                changeStateTriggerRefreshPage(needRefreshFilesPage)
//            }
//        }
//    }

    //有从标题页面请求执行的操作，执行一下
    //判断请求执行什么操作，然后执行
    if (filesPageRequestFromParent.value == PageRequest.goToTop) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            UIHelper.scrollToItem(scope, curListState.value, 0)
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.safDiff) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            initSafDiffDialog()
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.safImport) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            initSafImportDialog()
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.safExport) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            val curFile = File(currentPath())
            val curPathReadable = try {
                curFile.canRead()
            } catch (e: Exception) {
                false
            }

            if (curPathReadable) {
                val subFiles = curFile.listFiles()
                if (subFiles == null || subFiles.isEmpty()) { //空文件夹没什么好导出的
                    Msg.requireShow(activityContext.getString(R.string.folder_is_empty))
                } else { //执行导出
                    initSafExportDialog(subFiles.map { FileItemDto.genFileItemDtoByFile(it, activityContext) })
                }
            } else {
                Msg.requireShow(activityContext.getString(R.string.err_read_path_failed))
            }
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.switchBetweenTopAndLastPosition) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            UIHelper.switchBetweenTopAndLastVisiblePosition(scope, curListState.value, lastPosition)
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.requireShowPathDetails) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            initDetailsDialog(listOf(curPathFileItemDto.value))
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.createFileOrFolder) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            //选中文本
            fileNameForCreateDialog.apply {
                value = value.copy(selection = TextRange(0, value.text.length))
            }

            //显示新建文件或文件夹的弹窗，弹窗里可选择是创建文件还是文件夹
            createFileOrFolderErrMsg.value = ""  //初始化错误信息为空
            showCreateFileOrFolderDialog.value = true  //显示弹窗
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.showViewAndSortMenu) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            onlyForThisFolderStateBuf.value = onlyForThisFolderState.value
            viewAndSortStateBuf.value = viewAndSortState.value.copy()
            showViewAndSortDialog.value = true
        }
    }

    //注：匹配带数据的request应该用startsWith
//    if(filesPageRequestFromParent.value.startsWith(PageRequest.DataRequest.goToIndexWithDataSplit)) {
    if (PageRequest.DataRequest.isDataRequest(filesPageRequestFromParent.value, PageRequest.goToIndex)) {
        PageRequest.getRequestThenClearStateThenDoAct(filesPageRequestFromParent) { request ->
            val index = try {
                PageRequest.DataRequest.getDataFromRequest(request).toInt()
            } catch (e: Exception) {
                0
            }

            UIHelper.scrollToItem(scope, curListState.value, index)
        }
    }


    if (filesPageRequestFromParent.value == PageRequest.goToPath) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            //全选文本
            pathToGo.value = pathToGo.value.let { it.copy(selection = TextRange(0, it.text.length)) }

            // 显示弹窗，输入路径，跳转
            showGoToPathDialog.value = true
        }
    }

//    拷贝基于app内仓库路径的相对路径，由于后来改成可选仓库父目录了，所以仓库不一定在内部仓库路径下，因此此功能废弃
//    if(filesPageRequestFromParent.value==PageRequest.copyPath) {
//        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
//            copyPath(currentPath.value)
//        }
//    }

    if (filesPageRequestFromParent.value == PageRequest.copyFullPath) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            copyThenShowCopied(currentPath())
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.copyRepoRelativePath) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            copyRepoRelativePath(currentPath())
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.goToInternalStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            goToPath(FsUtils.getInternalStorageRootPathNoEndsWithSeparator())
        }
    }

    if (filesPageRequestFromParent.value == PageRequest.goToExternalStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            goToPath(FsUtils.getExternalStorageRootPathNoEndsWithSeparator())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToInnerDataStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            goToPath(FsUtils.getInnerStorageRootPathNoEndsWithSeparator())
        }
    }
    if (filesPageRequestFromParent.value == PageRequest.goToExternalDataStorage) {
        PageRequest.clearStateThenDoAct(filesPageRequestFromParent) {
            val targetPath = AppModel.externalDataDir?.canonicalPath ?: ""
            if (targetPath.isBlank()) {
                Msg.requireShow(activityContext.getString(R.string.invalid_path))
            } else {
                goToPath(targetPath)
            }
        }
    }

    // start: request external storage permission if access external storage path
    val refreshWhenResume = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(currentPath()) {
        runCatching {
            if (currentPath() == FsUtils.getExternalStorageRootPathNoEndsWithSeparator()) {
                val permissionRequestSent = requestStoragePermissionIfNeed(activityContext, TAG)

                // for refresh list to show files when come back from permission grant view
                refreshWhenResume.value = permissionRequestSent
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if(refreshWhenResume.value) {
            refreshWhenResume.value = false
            changeStateTriggerRefreshPage(needRefreshFilesPage)
        }
    }
    // end: request external storage permission if access external storage path


    // disabled reason: maybe cause unexpected reload, if search mode on, may take long time to do a non-senese searching again
    // start: refresh when come back from other app
//    val wasPaused = remember { mutableStateOf(false) }
//    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
//        wasPaused.value = true
//    }
//
//    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
//        if(wasPaused.value) {
//            wasPaused.value = false
//            changeStateTriggerRefreshPage(needRefreshFilesPage)
//        }
//    }
    // end: refresh when come back from other app


    LaunchedEffect(needRefreshFilesPage.value) {
        //如果只有协程的话，其实try catch没意义，协程内部不会往外抛异常；若没协程，trycatch有意义，不然若发生异常会抛到外部导致app崩溃
        try {
            val (requestType, requestData) = getRequestDataByState<String?>(needRefreshFilesPage.value)
            if(requestType == StateRequestType.goToParentAndScrollToItem && requestData != null) {
                File(requestData).canonicalFile.parent?.let {
                    updateCurrentPath(it)
                }
            }


            //只有当目录改变时(需要刷新页面)，才需要执行initFilesPage，选择文件之类的操作不需要执行此操作
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    doInit(
                        isFileChooser = isFileChooser,
                        currentPath = currentPath,
                        updateCurrentPath = updateCurrentPath,
                        currentPathFileList = currentPathFileList,
                        currentPathBreadCrumbList = currentPathBreadCrumbList,
                        settingsSnapshot = settingsSnapshot,
                        filesPageGetFilterModeOn = filesPageGetFilterMode,
                        filesPageFilterKeyword = filesPageFilterKeyword,
                        curListState = curListState,
                        getListState = getListState,
                        loadingOn = loadingOn,
                        loadingOff = loadingOff,
                        activityContext = activityContext,
                        requireImportFile = requireImportFile,
                        requireImportUriList = requireImportUriList,
                        filesPageQuitSelectionMode = filesPageQuitSelectionMode,
                        isImportedMode = isImportMode,
                        selectItem=selectItem,
                        filesPageRequestFromParent = filesPageRequestFromParent,
                        setErr = setErr,
                        viewAndSortState = viewAndSortState,
                        viewAndSortOnlyForThisFolderState = onlyForThisFolderState,
                        curPathFileItemDto = curPathFileItemDto,
                        quitImportMode = quitImportMode,
                        selectedItems = selectedItems.value,
                    )

                    //有些情况不用自动刷新用户体验更好，例如点击搜索结果，跳转到文本编辑器，再返回
                    if(keepFilterResultOnce.value) {
                        keepFilterResultOnce.value = false
                    }else {
                        triggerReFilter(filterResultNeedRefresh)
                    }

                    if(requestType == StateRequestType.goToParentAndScrollToItem && requestData != null) {
                        // quit filter mode
                        filesPageSimpleFilterOn.value = false
                        resetFilesSearchVars()

                        // scroll to item
                        val targetIndex = currentPathFileList.value.toList().indexOfFirst { it.fullPath == requestData}
                        if(targetIndex >= 0) {
                            // -2 is offset to make item not very top
                            UIHelper.scrollToItem(scope, curListState.value, targetIndex - 2)
                        }
                    }

                }catch (e:Exception) {
                    Msg.requireShowLongDuration("init Files err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#init Files page err: ${e.stackTraceToString()}")
                }
            }
        } catch (e: Exception) {
            // job cancelled maybe
            MyLog.e(TAG, "#LaunchedEffect err: ${e.stackTraceToString()}")
        }
    }
}




private suspend fun doInit(
    isFileChooser:Boolean,
    currentPath: ()->String,
    updateCurrentPath: (String)->Unit,
    currentPathFileList: CustomStateListSaveable<FileItemDto>,
    currentPathBreadCrumbList: CustomStateListSaveable<FileItemDto>,
    settingsSnapshot:CustomStateSaveable<AppSettings>,
    filesPageGetFilterModeOn:()->Int,
    filesPageFilterKeyword:CustomStateSaveable<TextFieldValue>,
    curListState: CustomStateSaveable<LazyListState>,
    getListState:(String)->LazyListState,
    loadingOn: (String) -> Unit,
    loadingOff: () -> Unit,
    activityContext: Context,
    requireImportFile:MutableState<Boolean>,
    requireImportUriList: CustomStateListSaveable<Uri>,
    filesPageQuitSelectionMode:()->Unit,
    isImportedMode:MutableState<Boolean>,
    selectItem:(FileItemDto) ->Unit,
    filesPageRequestFromParent:MutableState<String>,
    setErr:(String)->Unit,
    viewAndSortState:CustomStateSaveable<DirViewAndSort>,
    viewAndSortOnlyForThisFolderState:MutableState<Boolean>,
    curPathFileItemDto:CustomStateSaveable<FileItemDto>,
    quitImportMode:()->Unit,
    selectedItems:List<FileItemDto>
//    repoList:CustomStateListSaveable<RepoEntity>,
//    currentPathBreadCrumbList: MutableIntState,
//    currentPathBreadCrumbList1: SnapshotStateList<FileItemDto>,
//    currentPathBreadCrumbList2: SnapshotStateList<FileItemDto>
){

    //清空缩略图缓存
    ThumbCache.clear()


    //无选中条目则退出选择模式
    if(isFileChooser.not() && selectedItems.isEmpty()) {
        filesPageQuitSelectionMode()
    }

    val lastOpenedPathFromSettings = settingsSnapshot.value.files.lastOpenedPath

    //如果路径为空，从配置文件读取上次打开的路径
    if(currentPath().isBlank()) {
        updateCurrentPath(lastOpenedPathFromSettings)
    }

    //先清下列表，感觉在开头清比较好，如果加载慢，先白屏，然后出东西；放后面清的话，如果加载慢，会依然显示旧条目列表，感觉没变化，像卡了一样，用户可能重复点击
    currentPathFileList.value.clear()

    val repoBaseDirPath = AppModel.allRepoParentDir.canonicalPath

    updateCurrentPath(if(currentPath().isBlank()) {
        repoBaseDirPath
    }else {
        // make path canonical first, elst dir/ will return once dir, that make must press 2 times back for back to parent dir
        File(currentPath()).canonicalPath
    })

    //更新当前目录的文件列表
    var currentDir = File(currentPath())
    var currentFile:File? = null

    //无论对应文件是否存在，只要currentDir不是文件夹（其实也可能不是文件），都尝试取出其上级目录作为即将打开的目录
    // 之所以不判断文件是否存在是想在文件不存在时也尽量定位到其所在目录，不过只尝试定位一层上级目录，不会递归查找存在的上级目录，一层不存在就直接返回app根目录了
    if(currentDir.canRead() && currentDir.isFile) {  //注：若文件不存在 isDirectory和isFile 都为假，所以这个判断并不能确定目标路径就一定是个存在的文件
        //当curpath不是文件夹时，尝试取出其上级，若是目录，则定位并有可能选中对应文件，若不是目录，则忽略，然后在后面的代码中会定位到app根目录
        val parent = currentDir.parentFile
        if(parent!=null && parent.exists() && parent.isDirectory) {
            currentFile = currentDir
            currentDir = parent
            updateCurrentPath(currentDir.canonicalPath)
        }
    }

    //最终决定currentPath值的判断
    //xxx.contains(xxxxxx)判断是为了避免用户修改json文件中的lastOpenedPath越狱访问 allRepoParentDir 之外的目录
    //如果进入过上面的parent不等于null和是否目录和存在的判断，则执行到这里，只有判断路径是否越狱的条件可能为真
//        if(!currentDir.exists() || !currentDir.isDirectory || !currentDir.canonicalPath.startsWith(repoBaseDirPath)) {  //如果当前目录不存在，将路径设置为仓库根目录

    //because now(2024-09-23) support external path, so doesn't check startsWith repoBaseDirPath anymore
//        if(currentDir.canRead() && (!currentDir.exists() || !currentDir.isDirectory)) {  //如果当前目录不存在，将路径设置为仓库根目录
    //若可读，路径必然存在，所以不用再检查是否存在路径
    //其实这个和上面的canRead() && isFile 差不多，约等于又做了一次同样的检测
    if(currentDir.canRead() && !currentDir.isDirectory) {  //如果当前目录不存在，将路径设置为仓库根目录
//            Msg.requireShow(appContext.getString(R.string.invalid_path))  一边自己跳转到主页，一边提示无效path，产生一种主页是无效path的错觉，另人迷惑，故废弃

        currentFile=null  // 如果进入这个判断，currentFile已无意义，设为null，方便后面判断快速得到结果而不用继续比较path
        currentDir = File(repoBaseDirPath)
        updateCurrentPath(repoBaseDirPath)
    }


    //执行到这，路径一定存在（要么路径存在，要么不存在被替换成了所有仓库的父目录，然后路径存在）

    //更新配置文件中记录的最后打开路径，如果需要
    if(lastOpenedPathFromSettings != currentPath()) {
        //更新页面变量
        settingsSnapshot.value.files.lastOpenedPath = currentPath()

        //更新配置文件，避免卡顿，可开个协程，但其实没必要，因为最有可能造成卡顿的io操作其实已经放到协程里执行了
        SettingsUtil.update {  //这里并不是把页面的settingsSnapshot状态变量完全写入到配置文件，而是获取一份当下最新设置项的拷贝，然后修改我在这个代码块里修改的变量，再写入文件，所以，在这修改的设置项其实和页面的设置项可能会有出入，但我只需要currentPath关联的一个值而已，所以有差异也无所谓
            it.files.lastOpenedPath = currentPath()
        }
    }


    //文件列表排序算法
    val (viewAndSortOnlyForThisFolder, viewAndSort) = getViewAndSortForPath(currentPath(), settingsSnapshot.value)
    viewAndSortState.value = viewAndSort
    viewAndSortOnlyForThisFolderState.value = viewAndSortOnlyForThisFolder

    val sortMethod = viewAndSort.sortMethod
    val ascend = viewAndSort.ascend

    //排序
    //注意：文件夹大小不是0，可能是4096字节，所以按大小排序会在某些大小非0的文件上面！
    val comparator = { o1:FileItemDto, o2:FileItemDto ->  //不能让比较器返回0，不然就等于“去重”了，就会少文件
        val sortByName = {
            compareStringAsNumIfPossible(getFileNameOrEmpty(o1.name), getFileNameOrEmpty(o2.name))
        }
        val sortByType = {
            compareStringAsNumIfPossible(getFileExtOrEmpty(o1.name), getFileExtOrEmpty(o2.name))
        }

        var compareResult = if(sortMethod == SortMethod.NAME.code) {
            sortByName()
        }else if(sortMethod == SortMethod.TYPE.code) {
            sortByType()
        } else if(sortMethod == SortMethod.SIZE.code) {
            o1.sizeInBytes.compareTo(o2.sizeInBytes)
        } else { //sortMethod == SortMethod.LAST_MODIFIED
            o1.lastModifiedTimeInSec.compareTo(o2.lastModifiedTimeInSec)
        }

        //if equals and is not sort by name, try sort by name
        if(compareResult == 0 && sortMethod != SortMethod.NAME.code) {
            compareResult = sortByName()
        }

        if(compareResult == 0 && sortMethod != SortMethod.TYPE.code) {
            compareResult = sortByType()
        }

        if(compareResult > 0){
            if(ascend) 1 else -1
        } else {
            if(ascend) -1 else 1
        }
    }

    val fileSortedSet = sortedSetOf<FileItemDto>(comparator)
    val dirSortedSet = sortedSetOf<FileItemDto>(comparator)

    //注意，如果keyword为empty，正常来说不会进入搜索模式，不过如果进入，也会显示所有文件，因为任何字符串都包含空字符串
    //注意：只有当filterMode==2，也就是“显示根据关键字过滤的结果”的时候，才执行过滤，如果只是打开输入框，不会执行过滤。这个值不要改成不等于0，不然，打开输入框，输入内容，然后执行会触发刷新页面的操作（例如新建文件），就会执行过滤了，那样就会没点确定就开始过滤，会感觉有点混乱。比较好的交互逻辑是：要么就需要确认才过滤，要么就不需要确认边输入边过滤，不要两者混合。
//        val isFilterModeOn = filesPageGetFilterModeOn() == 2
//        val filterKeywordText = filesPageFilterKeyword.value.text

    // 当请求打开的curpath是个文件时会用到这几个变量 开始
    val needSelectFile = currentFile!=null && currentFile.exists()
    val curFilePath = currentFile?.canonicalPath
    var curFileFromCurPathAlreadySelected = false
    var curFileFromCurPathFileDto:FileItemDto? = null  //用来存匹配的dto，然后在列表查找index，然后滚动到指定位置
    // 当请求打开的curpath是个文件时会用到这几个变量 结束

    //获取文件列表之类的
    var folderCount = 0
    var fileCount = 0
    // 遍历文件列表
    currentDir.listFiles()?.let {
        for(file in it) {
            val fdto = FileItemDto.genFileItemDtoByFile(file, activityContext)

            if(fdto.isFile) {
                fileCount++
                //如果从请求打开的路径带来的文件存在，且和当前遍历的文件重合，选中
                if(needSelectFile && !curFileFromCurPathAlreadySelected && curFilePath == fdto.fullPath) {
//                        清空已选中条目列表
                    filesPageQuitSelectionMode()
//                        把当前条目添加进已选中列表并开启选择模式
                    selectItem(fdto)
                    curFileFromCurPathFileDto=fdto
                    //因为路径只有可能代表一个文件，所以此判断代码块只需执行一次，设置flag为已执行，这样下次就会跳过不必要的判断了
                    curFileFromCurPathAlreadySelected = true
                }

                fileSortedSet.add(fdto)
            }else {
                folderCount++
                if(viewAndSort.folderFirst) {
                    dirSortedSet.add(fdto)
                }else {
                    fileSortedSet.add(fdto)
                }
            }
        }
    }

//        val lastUsedPath = curPathFileItemDto.value.fullPath

    val curPathDtoTmp = FileItemDto.genFileItemDtoByFile(currentDir, activityContext)
    curPathDtoTmp.folderCount = folderCount
    curPathDtoTmp.fileCount = fileCount
    curPathFileItemDto.value = curPathDtoTmp

//    println("dirSortedSet:"+dirSortedSet)
//    println("fileSortedSet:"+fileSortedSet)

    //清文件列表。（如果在开头清了，这里就不用再清了）
//    currentPathFileList.value.clear()
    //添加文件列表
    //一级顺序：文件夹在上，文件在下；二级顺序：各自按时间降序排列
    currentPathFileList.value.addAll(dirSortedSet)
    currentPathFileList.value.addAll(fileSortedSet)
    //恢复或新建当前路径的list state
    curListState.value = getListState(currentPath())
//    currentPathFileList.requireRefreshView()
    //当curpath是文件时，如果文件确实存在并且已选中，则跳转到对应文件的索引
    if(curFileFromCurPathAlreadySelected && curFileFromCurPathFileDto!=null) {
        var indexForScrollTo = currentPathFileList.value.indexOf(curFileFromCurPathFileDto)

        //这个判断是可选的，考虑下要不要注释掉
        //这个判断是为了使文件滚动时避免把选中条目放到最顶端，因为那样看着不太舒服，不过，这样有个弊端，如果当前选中条目的上一个条目文件名无敌长，那当前条目可能就被顶到下面的不可见范围了
        if(indexForScrollTo>0) {
            indexForScrollTo-=1
        }

        //下次渲染时请求滚动页面到对应条目
        filesPageRequestFromParent.value = PageRequest.DataRequest.build(PageRequest.goToIndex, ""+indexForScrollTo)
    }

    //设置面包屑
    //每一层都显示自己的路径不就行了？

    //列出从仓库目录往后的目录，删掉前面的/storage/emu...之类的，返回结果形如[repo1,repo1Inside,otherDirs...]
//    currentPathStrList.addAll(getFilePathStrBasedRepoDir(curDirPath).split(File.separator))

    //这个函数其实只有在第一次进入Files页面且开启了记住上次退出路径的时候才有必要执行，这是一个针对当前路径初始化面包屑的操作，正常来说，更新面包屑不用执行这个函数，直接在点击文件夹时把路径添加到面包屑列表中即可
    //添加包含文件夹名和文件夹完整路径的对象到列表用来做面包屑路径导航

    //每次在这里更新面包屑其实也行，但不如点击目录时增量更新省事
    //值等于0，需要初始化
//    if(currentPathBreadCrumbList.intValue==0) {  //刚创建页面的时候，面包屑列表为空，需要初始化一下，后续由点击目录的onClick函数维护面包屑状态

//        val willUpdateList = if(currentPathBreadCrumbList.intValue==1) currentPathBreadCrumbList1 else currentPathBreadCrumbList2  //初始化时更新列表1，列表2由后续点击面包屑后的onClick函数更新

    val curDirPath = currentDir.canonicalPath
    val curBreadCrumbList = currentPathBreadCrumbList.value

    //重新生成面包屑的条件：上次路径为空 或 面包屑列表为空 或 上次路径非startsWith当前路径
//        if(lastUsedPath.isBlank() || curDirPath==FsUtils.rootPath || currentPathBreadCrumbList.value.isEmpty() || lastUsedPath.startsWith(curDirPath).not()) {

    val separator = Cons.slashChar

    //if breadCrumblist empty or last item full path not starts with current path, recreate the breadcrumblist
    //如果面包屑不为空或最后一个元素不是当前路径的子目录，重新创建面包屑列表
    if(breadCrumbPathNotCoverdCurPath(curBreadCrumbList, curDirPath, separator)) {
        curBreadCrumbList.clear()  //避免和之前的路径拼接在一起，先清空下列表
        curBreadCrumbList.add(FileItemDto.getRootDto())  //添加root条目

        //分割路径，不包含root，若用root path '/' 去分割，结果会是仅有1个元素且元素内容为空字符串的List
        val splitPath = curDirPath.trim(separator).split(separator)
        //分割后的字符串如果只有一个条目且是空字符串，则当前路径为root，由于上面已经添加了root，所以不需要再额外添加
        if(!(splitPath.size == 1 && splitPath[0].isEmpty())) {  //如果当前路径非root，则添加面包屑，否则不需要添加，因为上面已经添加了rootpath，这里再添加只会添加个空元素，无意义
            var lastPathName=StringBuilder(40)  // most time the path should more than 30 "/storage/emulated/0" , so set it to 40 I think is better than StringBuilder default size 16
            for(pathName in splitPath) {  //更新面包屑
                lastPathName.append(separator).append(pathName)  //拼接列表路径为仓库下的 完整相对路径
                // bread crumb为了生成快速创建的是阉割板的对象，只有少数必要参数，如果想获取path的完整dto，需要重新生成， 调用genFileItemDtoByFile(dto.toFile())即可
                val pathDto = FileItemDto()

                //breadCrumb must dir, if is file, will replace at up code
                //面包屑肯定是目录，如果是文件，在上面的代码中会被替换成目录
                pathDto.isDir=true

                pathDto.fullPath = lastPathName.toString()  // this will output path like "/abc/def" and should faster than `File(root, lastPathName).canonicalPath`


                pathDto.name = pathName
                curBreadCrumbList.add(pathDto)
            }
        }
    }


    // since require manage storage permission, no more need this, users can simple copy file at Files page between external and internal storage
    //检查是否请求导入文件
    if(requireImportFile.value) {
        //确保只启动一次导入模式
        requireImportFile.value = false

        //若待导入列表不为空，退出选择模式并启动导入模式
        if(requireImportUriList.value.isNotEmpty()) { //有条目待导入，开启导入模式
            //退出选择模式
            filesPageQuitSelectionMode()
            //切换到导入模式，显示导入栏
            isImportedMode.value = true
        }
        //刷新页面，改了状态应该会自动刷新，不需再刷新
//            changeStateTriggerRefreshPage(needRefreshFilesPage)
    }

    //如果待导入列表为空，退出导入模式，这个必须放在检查是否启动导入模式的后面+外面，若放前面会提前清空导入列表，直接使导入模式作废；
    // 若放判断是否启动导入模式的里面则大概率会使这段代码失效（除非进入的时候待导入列表为空），
    // 因为是否启动导入模式是一次性判断，进入代码块后就重置flag了，比如用户先导入文件，然后清空文件列表，再刷新，这时应退出导入模式，
    // 但如果放检查是否启动导入模式的代码块里，这时flag已设为假，就不会被执行了，于是就导致bug：待导入条目为0，却依然开着导入模式
    if(requireImportUriList.value.isEmpty()) { //待导入列表为空，退出导入模式
        //退出导入模式
        quitImportMode()
    }

    // set err if has
//        if(!File(currentPath.value).canRead()) {  // can't read dir, usually no permission for dir or dir doesn't exist
    setErr(if(currentDir.canRead() && currentDir.isDirectory) "" else activityContext.getString(R.string.err_read_path_failed))

}

@Composable
private fun getBackHandler(
    naviUp:()->Unit,
    isFileChooser: Boolean,
    appContext: Context,
    isFileSelectionMode: MutableState<Boolean>,
    filesPageQuitSelectionMode: () -> Unit,
    currentPath: ()->String,
    allRepoParentDir: File,
    needRefreshFilesPage: MutableState<String>,
    exitApp: () -> Unit,
    getFilterMode:()->Int,
    filesPageFilterModeOff:()->Unit,
    filesPageSimpleFilterOn: MutableState<Boolean>,
    openDrawer:()->Unit,
    goToPath:(String)->Unit,
    resetSearchVars:()->Unit,
): () -> Unit {
    val backStartSec =  rememberSaveable { mutableLongStateOf(0) }
    val ceilingPaths = rememberSaveable { FsUtils.getAppCeilingPaths() }

    val pressBackAgainForExitText = stringResource(R.string.press_back_again_to_exit);
    val showTextAndUpdateTimeForPressBackBtn = {
        openDrawer()
        showToast(appContext, pressBackAgainForExitText, Toast.LENGTH_SHORT)
        backStartSec.longValue = getSecFromTime() + Cons.pressBackDoubleTimesInThisSecWillExit
    }

    val backHandlerOnBack:()->Unit = {
        if (isFileChooser.not() && isFileSelectionMode.value) {
            filesPageQuitSelectionMode()
        } else if(filesPageSimpleFilterOn.value) {
            filesPageSimpleFilterOn.value = false
            resetSearchVars()
        }else if(getFilterMode() != 0) {
            // 20250107: 这个过滤模式好像已经废弃了？？？
            filesPageFilterModeOff()
            resetSearchVars()
//        }else if (currentPath.value.startsWith(FsUtils.getExternalStorageRootPathNoEndsWithSeparator()+"/")) { //如果在文件管理器页面且不在仓库根目录
        }else if (ceilingPaths.contains(currentPath()).not()) { //如果在文件管理器页面且未抵达任一天花板目录，则打开上级目录，否则显示“再按返回则退出”的提示
            //返回上级目录
            goToPath(currentPath().let { it.substring(0, it.lastIndexOf(Cons.slashChar).coerceAtLeast(0)) }.ifEmpty { FsUtils.rootPath })
        }else if(isFileChooser){
            naviUp()
        } else {
            //如果在两秒内按返回键，就会退出，否则会提示再按一次可退出程序
            if (Cons.disableDoublePressBackToExit || (backStartSec.longValue > 0 && getSecFromTime() <= backStartSec.longValue)) {  //大于0说明不是第一次执行此方法，那检测是上次获取的秒数，否则直接显示“再按一次退出app”的提示
                exitApp()
            } else {
                showTextAndUpdateTimeForPressBackBtn()
            }
        }
    }
    return backHandlerOnBack
}

private fun breadCrumbPathNotCoverdCurPath(curBreadCrumbList:List<FileItemDto>, curDirPath:String, separator:Char):Boolean {
    return if(curBreadCrumbList.isEmpty()) {
        true
    }else {
        // 若curBreadCrumbList为空，会在if返回，执行到这里，必然至少有一个元素，所以获取其最后一个元素不会越界
        val breadCrumbLastItemPath = curBreadCrumbList[curBreadCrumbList.size-1].fullPath

        //如果路径末尾没 / 就加个 /，避免先进入 /abc 再进入 /a 这种情况 startsWith() 误判导致面包屑不会刷新
        val breadCrumbPathForCompare = if(breadCrumbLastItemPath.endsWith(separator)) breadCrumbLastItemPath else "$breadCrumbLastItemPath$separator"
        val curDirPathForCompare = if(curDirPath.endsWith(separator)) curDirPath else "$curDirPath$separator"

        !breadCrumbPathForCompare.startsWith(curDirPathForCompare)
    }
}
