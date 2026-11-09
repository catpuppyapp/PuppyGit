package com.catpuppyapp.puppygit.screen

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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.homescreen.innerpage.ChangeListInnerPage
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions.ChangeListPageActions
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title.IndexScreenTitle
import com.catpuppyapp.puppygit.screen.functions.ChangeListFunctions
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "IndexScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexScreen(
//    context: Context,
//    navController: NavController,
//    scope: CoroutineScope,
//    scrollBehavior: TopAppBarScrollBehavior,
//    repoPageListState: LazyListState,
//    filePageListState: LazyListState,
//    haptic: HapticFeedback,
    naviUp: () -> Unit
) {

    val stateKeyTag = Cache.getSubPageKey(TAG)

    val navController = AppModel.navController
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior

    val allRepoParentDir = AppModel.allRepoParentDir
    val scope = rememberCoroutineScope()

    val settings = remember {SettingsUtil.getSettingsSnapshot()}

    //替换成我的cusntomstateSaver，然后把所有实现parcellzier的类都取消实现parcellzier，改成用我的saver
//    val curRepo = rememberSaveable{ mutableStateOf(RepoEntity()) }
//    val curRepo = mutableCustomStateOf(value = RepoEntity())

    val changeListRefreshRequiredByParentPage = rememberSaveable { SharedState.indexChangeList_Refresh }
    val changeListRequireRefreshFromParentPage = { whichRepoRequestRefresh:RepoEntity ->
        ChangeListFunctions.changeListDoRefresh(changeListRefreshRequiredByParentPage, whichRepoRequestRefresh)
    }
//    val changeListCurRepo = rememberSaveable{ mutableStateOf(RepoEntity()) }
    val changeListCurRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "changeListCurRepo", initValue = RepoEntity(id=""))
    val changeListIsShowRepoList = rememberSaveable { mutableStateOf(false)}
    val changeListPageHasIndexItem = rememberSaveable { mutableStateOf(false)}
    val changeListShowRepoList = {
        changeListIsShowRepoList.value = true
    }
    val changeListIsFileSelectionMode = rememberSaveable { mutableStateOf( false)}
    val changeListPageNoRepo = rememberSaveable { mutableStateOf( false)}
    val changeListPageHasNoConflictItems = rememberSaveable { mutableStateOf(false)}

    val changeListPageRebaseCurOfAll = rememberSaveable { mutableStateOf( "")}
    val changeListNaviTarget = rememberSaveable { mutableStateOf(Cons.ChangeListNaviTarget_InitValue)}



    val changeListPageFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListPageFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val changeListPageFilterModeOn = rememberSaveable { mutableStateOf(false)}

    // start: search states
    val changeListLastSearchKeyword = rememberSaveable { mutableStateOf("") }
    val changeListSearchToken = rememberSaveable { mutableStateOf("") }
    val changeListSearching = rememberSaveable { mutableStateOf(false) }
    val resetChangeListSearchVars = {
        changeListSearching.value = false
        changeListSearchToken.value = ""
        changeListLastSearchKeyword.value = ""
    }
    // end: search states

//    val changelistFilterListState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "changelistFilterListState", LazyListState(0,0))
    val changelistFilterListState = rememberLazyListState()

    val swap =rememberSaveable { mutableStateOf(false)}

    val changeListErrScrollState = rememberScrollState()
    val changeListHasErr = rememberSaveable { mutableStateOf(false) }
    val changeListErrLastPosition = rememberSaveable { mutableStateOf(0) }

//    val editorPageRequireOpenFilePath = rememberSaveable{ mutableStateOf("") } // canonicalPath
////    val needRefreshFilesPage = rememberSaveable { mutableStateOf(false) }
//    val needRefreshFilesPage = rememberSaveable { mutableStateOf("") }
//    val currentPath = rememberSaveable { mutableStateOf(allRepoParentDir.canonicalPath) }
//    val showCreateFileOrFolderDialog = rememberSaveable{ mutableStateOf(false) }
//
//    val showSetGlobalGitUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
//
//    val editorPageShowingFilePath = rememberSaveable{ mutableStateOf("") } //当前展示的文件的canonicalPath
//    val editorPageShowingFileIsReady = rememberSaveable{ mutableStateOf(false) } //当前展示的文件是否已经加载完毕
//    //TextEditor用的变量
//    val editorPageTextEditorState = remember { mutableStateOf(TextEditorState.create("")) }
//    val editorPageShowSaveDoneToast = rememberSaveable { mutableStateOf(false) }
////    val needRefreshEditorPage = rememberSaveable { mutableStateOf(false) }
//    val needRefreshEditorPage = rememberSaveable { mutableStateOf("") }
//    val changeListRequirePull = rememberSaveable { mutableStateOf(false) }
//    val changeListRequirePush = rememberSaveable { mutableStateOf(false) }
    val requireDoActFromParent = rememberSaveable { mutableStateOf(false)}
    val requireDoActFromParentShowTextWhenDoingAct = rememberSaveable { mutableStateOf("")}
    val enableAction = rememberSaveable { mutableStateOf(true)}
    val repoState = rememberSaveable{mutableIntStateOf(Repository.StateT.NONE.bit)}  //初始状态是NONE，后面会在ChangeListInnerPage检查并更新状态，只要一创建innerpage或刷新（重新执行init），就会更新此状态
    val fromTo = Cons.gitDiffFromHeadToIndex
    val changeListPageItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListPageItemListState = rememberLazyListState()
    val changeListPageSelectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageSelectedItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changelistPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val changelistNewestPageId = rememberSaveable { mutableStateOf("") }
    val changeListPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val changeListFilterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListFilterList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListLastClickedItemKey = rememberSaveable{ SharedState.index_LastClickedItemKey }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(changeListPageFilterModeOn.value) {
                        FilterTextField(filterKeyWord = changeListPageFilterKeyWord, loading = changeListSearching.value)
                    }else {
                        IndexScreenTitle(changeListCurRepo, repoState, scope, changeListPageItemListState, lastPosition)
                    }
                },
                navigationIcon = {
                    if(changeListPageFilterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),

                        ) {
                            resetChangeListSearchVars()
                            changeListPageFilterModeOn.value = false
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
                    if(!changeListPageFilterModeOn.value) {
                        ChangeListPageActions(
                            changeListCurRepo,
                            changeListRequireRefreshFromParentPage,
                            changeListPageHasIndexItem,
                            requireDoActFromParent,
                            requireDoActFromParentShowTextWhenDoingAct,
                            enableAction,
                            repoState,
                            fromTo,
                            changeListPageItemListState,
                            scope,
                            changeListPageNoRepo=changeListPageNoRepo,
                            hasNoConflictItems = changeListPageHasNoConflictItems.value,
                            changeListPageFilterModeOn=changeListPageFilterModeOn,
                            changeListPageFilterKeyWord=changeListPageFilterKeyWord,
                            rebaseCurOfAll = changeListPageRebaseCurOfAll.value,
                            naviTarget = changeListNaviTarget
                        )

                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(changelistPageScrolled.value) {
                if(changeListHasErr.value) {
                    GoToTopAndGoToBottomFab(
                        scope = scope,
                        listState = changeListErrScrollState,
                        listLastPosition = changeListErrLastPosition,
                        showFab = changelistPageScrolled
                    )
                }else {
                    GoToTopAndGoToBottomFab(
                        filterModeOn = changeListPageEnableFilterState.value,
                        scope = scope,
                        filterListState = changelistFilterListState,
                        listState = changeListPageItemListState,
                        filterListLastPosition = filterLastPosition,
                        listLastPosition = lastPosition,
                        showFab = changelistPageScrolled
                    )
                }

            }
        }
    ) { contentPadding ->
//        val commit1OidStr = rememberSaveable { mutableStateOf("") }
//        val commitParentList = remember { mutableStateListOf<String>() }

        ChangeListInnerPage(
//            stateKeyTag = Cache.combineKeys(stateKeyTag, "ChangeListInnerPage"),
            stateKeyTag = stateKeyTag,

            errScrollState = changeListErrScrollState,
            hasError = changeListHasErr,

            commit1OidStr = Cons.git_HeadCommitHash,
            commit2OidStr = Cons.git_IndexCommitHash,

            lastSearchKeyword=changeListLastSearchKeyword,
            searchToken=changeListSearchToken,
            searching=changeListSearching,
            resetSearchVars=resetChangeListSearchVars,

            contentPadding = contentPadding,
            fromTo = fromTo,
            curRepoFromParentPage = changeListCurRepo,
            isFileSelectionMode = changeListIsFileSelectionMode,

            refreshRequiredByParentPage = changeListRefreshRequiredByParentPage.value,
            changeListRequireRefreshFromParentPage = changeListRequireRefreshFromParentPage,
            changeListPageHasIndexItem = changeListPageHasIndexItem,
//                requirePullFromParentPage = changeListRequirePull,
//                requirePushFromParentPage = changeListRequirePush,
            requireDoActFromParent = requireDoActFromParent,
            requireDoActFromParentShowTextWhenDoingAct = requireDoActFromParentShowTextWhenDoingAct,
            enableActionFromParent = enableAction,
            repoState = repoState,
            naviUp=naviUp,
            itemList = changeListPageItemList,
            itemListState = changeListPageItemListState,
            selectedItemList = changeListPageSelectedItemList,
            changeListPageNoRepo=changeListPageNoRepo,
            hasNoConflictItems = changeListPageHasNoConflictItems,
            changelistPageScrolled=changelistPageScrolled,
            changeListPageFilterModeOn= changeListPageFilterModeOn,
            changeListPageFilterKeyWord=changeListPageFilterKeyWord,
            filterListState = changelistFilterListState,
            swap=swap.value,
            commitForQueryParents = "",
            rebaseCurOfAll = changeListPageRebaseCurOfAll,
            openDrawer = {}, //非顶级页面按返回键不需要打开抽屉
            newestPageId = changelistNewestPageId,
            naviTarget = changeListNaviTarget,
            enableFilterState = changeListPageEnableFilterState,
            filterList = changeListFilterList,
            lastClickedItemKey = changeListLastClickedItemKey


//            commit1OidStr=commit1OidStr,
//            commitParentList=commitParentList
        )

    }
//    }

    //compose创建时的副作用
//    LaunchedEffect(currentPage.intValue) {
//    LaunchedEffect(Unit) {
//        try {
//
//        } catch (cancel: Exception) {
////            ("LaunchedEffect: job cancelled")
//        }
//    }
//
//
//    //compose被销毁时执行的副作用
//    DisposableEffect(Unit) {
////        ("DisposableEffect: entered main")
//        onDispose {
////            ("DisposableEffect: exited main")
//        }
//    }

}



