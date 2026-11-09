package com.catpuppyapp.puppygit.screen

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dto.FileItemDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.homescreen.innerpage.FilesInnerPage
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions.FilesPageActions
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title.FilesTitle
import com.catpuppyapp.puppygit.screen.functions.getFilesGoToPath
import com.catpuppyapp.puppygit.screen.shared.FileChooserType
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import java.io.File

private const val TAG = "FileChooserScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileChooserScreen(
    type: FileChooserType,
    naviUp: () -> Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)

    val isFileChooser = remember { true }

    val updateSelectedPath = { path:String->
        if(type == FileChooserType.SINGLE_DIR) {
            SharedState.fileChooser_DirPath.value = path
        }else {
            SharedState.fileChooser_FilePath.value = path
        }
    }



    val activityContext = LocalContext.current
    val allRepoParentDir = AppModel.allRepoParentDir

    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior

    val scope = rememberCoroutineScope()

    val settings = remember {SettingsUtil.getSettingsSnapshot()}

    val needRefreshFilesPage = rememberSaveable { mutableStateOf("") }
    val refreshPage = {
        changeStateTriggerRefreshPage(needRefreshFilesPage)
    }

    val filesPageIsFileSelectionMode = rememberSaveable { mutableStateOf(false)}
    val filesPageIsPasteMode = rememberSaveable { mutableStateOf(false)}
    val filesPageSelectedItems = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageSelectedItems", initValue = listOf<FileItemDto>())

    val filesPageLastKeyword = rememberSaveable{ mutableStateOf("") }
    val filesPageSearchToken = rememberSaveable{ mutableStateOf("") }
    val filesPageSearching = rememberSaveable{ mutableStateOf(false) }
    val resetFilesSearchVars = {
        // search loading
        filesPageSearching.value = false
        // empty token to stop running search task
        filesPageSearchToken.value = ""
        filesPageLastKeyword.value = ""
    }
    //这个filter有点重量级，比较适合做成全局搜索之类的功能
    val filesPageFilterMode = rememberSaveable{mutableIntStateOf(0)}  //0关闭，1正在搜索，显示输入框，2显示搜索结果
    val filesPageFilterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filesPageFilterKeyword",
        initValue = TextFieldValue("")
    )
    val filesPageFilterTextFieldFocusRequester = remember { FocusRequester() }
    val filesPageFilterOn = {
        filesPageFilterMode.intValue = 1

        //若不为空，选中关键字
        val text = filesPageFilterKeyword.value.text
        if(text.isNotEmpty()) {
            filesPageFilterKeyword.value = filesPageFilterKeyword.value.copy(
                //这个TextRange，左闭右开，话说kotlin这一会左闭右闭一会左闭右开，有点难受
                selection = TextRange(0, text.length)
            )
        }
    }
    val filesPageFilterOff = {
        filesPageFilterMode.intValue = 0
        changeStateTriggerRefreshPage(needRefreshFilesPage)
    }
    val filesPageGetFilterMode = {
        filesPageFilterMode.intValue
    }
    val filesPageDoFilter= doFilter@{ keyWord:String ->
        //传参的话，优先使用，参数若为空，检查状态变量是否有数据，若有使用，若还是空，中止操作
        var needUpdateFieldState = true  //如果不是从状态变量获取的关键字，则更新状态变量为关键字

        var key = keyWord
        if(key.isEmpty()) {  //注意是empty，不要用blank，不然不能搜索空格，但文件名可能包含空格
            key = filesPageFilterKeyword.value.text
            if(key.isEmpty()) {
                Msg.requireShow(activityContext.getString(R.string.keyword_is_empty))
                return@doFilter
            }

            needUpdateFieldState = false  //这时，key本身就是从state获取的，所以不用再更新state了
        }

        if(needUpdateFieldState){
            filesPageFilterKeyword.value = TextFieldValue(key)  //设置关键字
        }

        filesPageFilterMode.intValue=2  // 设置搜索模式为显示结果
        changeStateTriggerRefreshPage(needRefreshFilesPage)  //刷新页面
    }


    val filesPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons)}
    val filesPageListState = mutableCustomStateOf(stateKeyTag, "filesPageListState", initValue = LazyListState(0,0))

    val filesPageSimpleFilterOn = rememberSaveable { mutableStateOf(false)}
    val filesPageSimpleFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filesPageSimpleFilterKeyWord",
        initValue = TextFieldValue("")
    )

    val filesPageCurrentPath = rememberSaveable {
        val initPath = runCatching {
            val tmpPath = if(type == FileChooserType.SINGLE_DIR) {
                    SharedState.fileChooser_DirPath.value
                }else {
                    SharedState.fileChooser_FilePath.value
                }

            //如果为空，再用File创建对象，再获取canonicalPath，会获取到根目录，所以只有当非空才获取规范路径，否则直接返回空字符串，不然若路径为空会错误的初始定位到根目录
            if(tmpPath.isBlank()) {
                ""
            }else {
                File(tmpPath).let { (if(it.isFile) it.canonicalFile.parent else it.canonicalPath) ?: "" }
            }
        }.getOrDefault("");

        initPath.ifBlank { settings.files.lastOpenedPath.ifBlank { FsUtils.getExternalStorageRootPathNoEndsWithSeparator() } }

        mutableStateOf(initPath)
    }

    val filesGetCurrentPath = {
        filesPageCurrentPath.value
    }
    val filesUpdateCurrentPath = { path:String ->
        filesPageCurrentPath.value = path
    }

    val filesPageLastPathByPressBack = rememberSaveable { mutableStateOf("")}
    val showCreateFileOrFolderDialog = rememberSaveable { mutableStateOf(false)}
    val filesPageCurPathFileItemDto = mutableCustomStateOf(stateKeyTag, "filesPageCurPathFileItemDto") { FileItemDto() }
    val filesPageCurrentPathBreadCrumbList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageCurrentPathBreadCrumbList", initValue = listOf<FileItemDto>())

    //这个页面其实用不到这个变量，一直都是假
    val filesPageKeepFilterResultOnce = rememberSaveable { mutableStateOf(false) }


    //上次滚动位置
    val filesLastPosition = rememberSaveable { mutableStateOf(0) }
    val fileListFilterLastPosition = rememberSaveable { mutableStateOf(0) }

    //判定过滤模式是否真开启的状态变量，例如虽然filterModeOn为真，但是，如果没输入任何关键字，过滤模式其实还是没开，这时这个变量会为假，但filterModeOn为真
    val filesPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val filesPageFilterList = mutableCustomStateListOf(stateKeyTag, "filesPageFilterList", listOf<FileItemDto>())
//    val repoFilterListState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "repoFilterListState", LazyListState(0,0))
    val filesFilterListState = rememberLazyListState()

    val filesPageRequireImportFile = rememberSaveable { mutableStateOf( false)}
    val filesPageRequireImportUriList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageRequireImportUriList", initValue = listOf<Uri>())
    val filesPageCurrentPathFileList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filesPageCurrentPathFileList", initValue = listOf<FileItemDto>()) //路径字符串，用路径分隔符分隔后的list
    val filesPageRequestFromParent = rememberSaveable { mutableStateOf("")}
    val filesPageCheckOnly = rememberSaveable { mutableStateOf(false)}
    val filesPageSelectedRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "filesPageSelectedRepo", RepoEntity(id="") )


    //无用变量，开始：这些变量在这个页面无用，但占位置
    val currentHomeScreen = rememberSaveable { mutableIntStateOf(Cons.selectedItem_Files) }
    val editorPageShowingFilePath = rememberSaveable { mutableStateOf(FilePath("")) }
    val editorPageShowingFileIsReady = rememberSaveable { mutableStateOf(false) }
    val requireInnerEditorOpenFile = {filepath:String, expectReadOnly:Boolean ->}
    //无用变量，结束

    val filesPageErrScrollState = rememberScrollState()
    val filesErrLastPosition = rememberSaveable { mutableStateOf(0) }
    val filesPageOpenDirErr = rememberSaveable { mutableStateOf("") }
    val filesPageGetErr = { filesPageOpenDirErr.value }
    val filesPageSetErr = { errMsg:String -> filesPageOpenDirErr.value = errMsg }
    val filesPageHasErr = { filesPageOpenDirErr.value.isNotBlank() }


    val filesGoToPath = getFilesGoToPath(filesPageLastPathByPressBack, filesGetCurrentPath, filesUpdateCurrentPath, needRefreshFilesPage)

    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    FilesTitle(
                        stateKeyTag = stateKeyTag,
                        currentPath = filesGetCurrentPath,
                        goToPath = filesGoToPath,
                        allRepoParentDir = allRepoParentDir,
                        needRefreshFilesPage = needRefreshFilesPage,
                        filesPageGetFilterMode = filesPageGetFilterMode,
                        filterKeyWord = filesPageFilterKeyword,
                        filterModeOn = filesPageFilterOn,
                        doFilter = filesPageDoFilter,
                        requestFromParent = filesPageRequestFromParent,
                        filterKeywordFocusRequester = filesPageFilterTextFieldFocusRequester,
                        filesPageSimpleFilterOn = filesPageSimpleFilterOn.value,
                        filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord,
                        curPathItemDto = filesPageCurPathFileItemDto.value,
                        searching = filesPageSearching.value
                    )
                },
                navigationIcon = {
                    if(filesPageGetFilterMode() != 0 || filesPageSimpleFilterOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon =  Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),

                        ) {
                            resetFilesSearchVars()
                            // close input text field
                            filesPageSimpleFilterOn.value = false
                        }
                    }else {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.back),
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            iconContentDesc = stringResource(R.string.back),
                        ) {
                            naviUp()
                        }

                    }

                },

                actions = {
                    FilesPageActions(
                        isFileChooser = isFileChooser,
//                        showCreateFileOrFolderDialog = showCreateFileOrFolderDialog,
                        refreshPage = refreshPage,
                        filterOn = filesPageFilterOn,
                        filesPageGetFilterMode = filesPageGetFilterMode,
                        doFilter = filesPageDoFilter,
                        requestFromParent = filesPageRequestFromParent,
                        filesPageSimpleFilterOn = filesPageSimpleFilterOn,
                        filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord
                    )

                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(filesPageScrolled.value) {
                if(filesPageHasErr()) {
                    GoToTopAndGoToBottomFab(
                        scope = scope,
                        listState = filesPageErrScrollState,
                        listLastPosition = filesErrLastPosition,
                        showFab = filesPageScrolled
                    )
                }else {
                    GoToTopAndGoToBottomFab(
                        filterModeOn = filesPageEnableFilterState.value,
                        scope = scope,
                        filterListState = filesFilterListState,
                        listState = filesPageListState.value,
                        filterListLastPosition = fileListFilterLastPosition,
                        listLastPosition = filesLastPosition,
                        showFab = filesPageScrolled
                    )
                }
            }
        }
    ) { contentPadding ->

        FilesInnerPage(
//            stateKeyTag = Cache.combineKeys(stateKeyTag, "FilesInnerPage"),
            stateKeyTag = stateKeyTag,

            errScrollState = filesPageErrScrollState,
            getErr = filesPageGetErr,
            setErr = filesPageSetErr,
            hasErr = filesPageHasErr,

            naviUp = naviUp,
            updateSelectedPath = updateSelectedPath,
            isFileChooser = isFileChooser,
            fileChooserType = type,
            filesPageLastKeyword=filesPageLastKeyword,
            filesPageSearchToken=filesPageSearchToken,
            filesPageSearching=filesPageSearching,
            resetFilesSearchVars=resetFilesSearchVars,
            contentPadding = contentPadding,
            currentHomeScreen=currentHomeScreen,
            editorPageShowingFilePath = editorPageShowingFilePath,
            editorPageShowingFileIsReady = editorPageShowingFileIsReady,
            needRefreshFilesPage = needRefreshFilesPage,
            currentPath=filesGetCurrentPath,
            showCreateFileOrFolderDialog=showCreateFileOrFolderDialog,
            requireImportFile=filesPageRequireImportFile,
            requireImportUriList=filesPageRequireImportUriList,
            filesPageGetFilterMode=filesPageGetFilterMode,
            filesPageFilterKeyword=filesPageFilterKeyword,
            filesPageFilterModeOff = filesPageFilterOff,
            currentPathFileList = filesPageCurrentPathFileList,
            updateCurrentPath = filesUpdateCurrentPath,
            filesPageRequestFromParent = filesPageRequestFromParent,
            requireInnerEditorOpenFile = requireInnerEditorOpenFile,
            filesPageSimpleFilterOn = filesPageSimpleFilterOn,
            filesPageSimpleFilterKeyWord = filesPageSimpleFilterKeyWord,
            filesPageScrolled = filesPageScrolled,
            curListState = filesPageListState,
            filterListState = filesFilterListState,
            openDrawer = {},
            isFileSelectionMode= filesPageIsFileSelectionMode,
            isPasteMode = filesPageIsPasteMode,
            selectedItems = filesPageSelectedItems,
            checkOnly = filesPageCheckOnly,
            selectedRepo = filesPageSelectedRepo,
            goToRepoPage={},
            goToChangeListPage={},
            lastPathByPressBack=filesPageLastPathByPressBack,
            curPathFileItemDto=filesPageCurPathFileItemDto,
            currentPathBreadCrumbList=filesPageCurrentPathBreadCrumbList,
            enableFilterState = filesPageEnableFilterState,
            filterList = filesPageFilterList,
            lastPosition = filesLastPosition,
            keepFilterResultOnce = filesPageKeepFilterResultOnce,  //这个页面其实用不到这个变量
            goToPath = filesGoToPath
        )

    }
}



