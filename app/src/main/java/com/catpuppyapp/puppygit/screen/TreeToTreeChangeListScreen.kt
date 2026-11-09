package com.catpuppyapp.puppygit.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.CompareInfo
import com.catpuppyapp.puppygit.compose.DropDownMenuItemText
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.InDialogTitle
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.RepoInfoDialogItemSpacer
import com.catpuppyapp.puppygit.compose.TitleDropDownMenu
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.commitsTreeToTreeDiffReverseTestPassed
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.git.CommitDto
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.homescreen.innerpage.ChangeListInnerPage
import com.catpuppyapp.puppygit.screen.functions.ChangeListFunctions
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.cache.NaviCache
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository


private const val TAG = "TreeToTreeChangeListScreen"


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreeToTreeChangeListScreen(
//    context: Context,
//    navController: NavController,
//    scope: CoroutineScope,
//    scrollBehavior: TopAppBarScrollBehavior,
//    repoPageListState: LazyListState,
//    filePageListState: LazyListState,
//    haptic: HapticFeedback,
    repoId:String,

    // show differences of commit1 to commit2 (git cmd format: 'commit1..commit2', or 'left..right')
    //这个万一包含/呢，例如 'origin/main' 这种，所以为了避免导航出错，必须不用导航传参
    commit1OidStrCacheKey:String,  // left
    commit2OidStrCacheKey:String,  // right
    commitForQueryParentsCacheKey:String,  // commit for query parents, if empty ,will not query parents for commits. ps: only need this param when compare to parents, other cases, should pass empty string
    titleCacheKey:String,

    naviUp: () -> Unit
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)

    //避免导航出现 "//" 导致导航失败
    //因为title要改变这个值，所以用State
    val commit1OidStrState = rememberSaveable(commit1OidStrCacheKey) { mutableStateOf((NaviCache.getByType<String>(commit1OidStrCacheKey) ?:"").ifBlank { Cons.git_AllZeroOidStr }) }

    val commit2OidStr = rememberSaveable(commit2OidStrCacheKey) { (NaviCache.getByType<String>(commit2OidStrCacheKey) ?:"").ifBlank { Cons.git_AllZeroOidStr } }

    val commitForQueryParents = rememberSaveable(commitForQueryParentsCacheKey){ NaviCache.getByType<String>(commitForQueryParentsCacheKey) ?:"" }

    val commitParentList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "commitParentList",
        initValue = listOf<String>()
    )

    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

//    SideEffect {
//        Msg.msgNotifyHost()
//    }

    val navController = AppModel.navController
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior

    val allRepoParentDir = AppModel.allRepoParentDir
    val activityContext = LocalContext.current
    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    //取出title desc，存到状态变量里，与页面共存亡就行
    val titleDesc = rememberSaveable { mutableStateOf(NaviCache.getByType<String>(titleCacheKey) ?: "") }

    //替换成我的cusntomstateSaver，然后把所有实现parcellzier的类都取消实现parcellzier，改成用我的saver
//    val curRepo = rememberSaveable{ mutableStateOf(RepoEntity()) }
//    val curRepo = mutableCustomStateOf(value = RepoEntity())

    val changeListRefreshRequiredByParentPage = rememberSaveable { mutableStateOf("TreeToTree_ChangeList_refresh_init_value_63wk") }
    val changeListRequireRefreshFromParentPage = { whichRepoRequestRefresh:RepoEntity ->
        ChangeListFunctions.changeListDoRefresh(changeListRefreshRequiredByParentPage, whichRepoRequestRefresh)
    }
//    val changeListCurRepo = rememberSaveable{ mutableStateOf(RepoEntity()) }
    val changeListCurRepo = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListCurRepo",
        initValue = RepoEntity(id="")
    )
    val changeListIsShowRepoList = rememberSaveable { mutableStateOf(false) }
    val changeListPageHasIndexItem = rememberSaveable { mutableStateOf(false) }
    val changeListShowRepoList = {
        changeListIsShowRepoList.value = true
    }
    val changeListIsFileSelectionMode = rememberSaveable { mutableStateOf(false) }
    val changeListPageNoRepo = rememberSaveable { mutableStateOf(false) }
    val changeListPageHasNoConflictItems = rememberSaveable { mutableStateOf(false) }

    val swap = rememberSaveable { mutableStateOf(false) }
//    val isDiffToHead = StateUtil.getRememberSaveableState(initValue = false)


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
    val requireDoActFromParent = rememberSaveable { mutableStateOf(false) }
    val requireDoActFromParentShowTextWhenDoingAct = rememberSaveable { mutableStateOf("") }
    val enableAction = rememberSaveable { mutableStateOf(true) }
    val repoState = rememberSaveable{mutableIntStateOf(Repository.StateT.NONE.bit)}  //初始状态是NONE，后面会在ChangeListInnerPage检查并更新状态，只要一创建innerpage或刷新（重新执行init），就会更新此状态
    val fromTo = Cons.gitDiffFromTreeToTree
    val changeListPageItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListPageItemListState = rememberLazyListState()
    val changeListPageSelectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListPageSelectedItemList", initValue = listOf<StatusTypeEntrySaver>())
    val changelistPageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
    val changelistNewestPageId = rememberSaveable { mutableStateOf("") }
    val changeListNaviTarget = rememberSaveable { mutableStateOf(Cons.ChangeListNaviTarget_InitValue)}

    val changeListPageFilterKeyWord = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "changeListPageFilterKeyWord",
        initValue = TextFieldValue("")
    )
    val changeListPageFilterModeOn = rememberSaveable { mutableStateOf(false) }

//    val changelistFilterListState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "changelistFilterListState", LazyListState(0,0))
    val changelistFilterListState = rememberLazyListState()


    val showParentListDropDownMenu = rememberSaveable { mutableStateOf(false) }

    val showInfoDialog = rememberSaveable { mutableStateOf(false) }
    val actuallyLeftName = rememberSaveable { mutableStateOf("") }
    val actuallyRightName = rememberSaveable { mutableStateOf("") }
    val actuallyLeftCommitDto = mutableCustomStateOf(stateKeyTag, "actuallyLeftCommitDto") { CommitDto() }
    val actuallyRightCommitDto = mutableCustomStateOf(stateKeyTag, "actuallyRightCommitDto") { CommitDto() }
    val initInfoDialog = {
        runCatching {
            val curRepo = changeListCurRepo.value
            val repoId = curRepo.id
            val actuallyLeftCommit = if(swap.value) commit2OidStr else commit1OidStrState.value
            val actuallyRightCommit = if(swap.value) commit1OidStrState.value else commit2OidStr
            actuallyLeftName.value = actuallyLeftCommit
            actuallyRightName.value = actuallyRightCommit

            Repository.open(curRepo.fullSavePath).use { repo->
                val (left, right) = Libgit2Helper.getLeftRightCommitDto(repo, actuallyLeftCommit, actuallyRightCommit, repoId, settings)
                actuallyLeftCommitDto.value = left
                actuallyRightCommitDto.value = right
            }
        }

        //出错一样显示，无所谓，顶多左右提交信息空白或有误
        showInfoDialog.value = true
    }

    if(showInfoDialog.value) {
        RepoInfoDialog(changeListCurRepo.value, showInfoDialog, prependContent = {
            Row {
                InDialogTitle(titleDesc.value)
            }

            RepoInfoDialogItemSpacer()

            CompareInfo(
                leftName = actuallyLeftName.value,
                leftCommitDto = actuallyLeftCommitDto.value,
                rightName = actuallyRightName.value,
                rightCommitDto = actuallyRightCommitDto.value,
            )

            RepoInfoDialogItemSpacer()

            MyHorizontalDivider()
            RepoInfoDialogItemSpacer()

            //下面会显示仓库信息，这里弄个标题，看着和上面的样式比较搭
            Row {
                InDialogTitle(stringResource(R.string.repo))
            }

        })
    }

    val changeListPageEnableFilterState = rememberSaveable { mutableStateOf(false)}
    val changeListFilterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "changeListFilterList", initValue = listOf<StatusTypeEntrySaver>())
    val changeListLastClickedItemKey = rememberSaveable{ SharedState.treeToTree_LastClickedItemKey }


    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }

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


    val changeListErrScrollState = rememberScrollState()
    val changeListHasErr = rememberSaveable { mutableStateOf(false) }
    val changeListErrLastPosition = rememberSaveable { mutableStateOf(0) }


    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(changeListPageFilterModeOn.value) {
                        FilterTextField(filterKeyWord = changeListPageFilterKeyWord, loading = changeListSearching.value)
                    }else {
                        val titleText = Libgit2Helper.getLeftToRightDiffCommitsText(commit1OidStrState.value, commit2OidStr, swap.value)
                        val titleSecondLineText = "[${changeListCurRepo.value.repoName}]"
                        // 判断父提交是否有效，实现如果是和父提交比较，则可展开下拉菜单，否则不能
                        val expandable = Libgit2Helper.CommitUtil.mayGoodCommitHash(commitForQueryParents)

                        TitleDropDownMenu(
                            dropDownMenuExpandState = showParentListDropDownMenu,
                            curSelectedItem = commit1OidStrState.value,
                            itemList = commitParentList.value.toList(),
                            titleClickEnabled = true,
                            switchDropDownMenuShowHide = { showParentListDropDownMenu.apply { value = !value } },
                            titleFirstLine = {
                                Text(
                                    text = titleText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.firstLineFontSize,
                                )
                            },
                            titleSecondLine = {
                                Text(
                                    text = titleSecondLineText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize,
                                )
                            },
                            titleRightIcon = {
                                Icon(
                                    imageVector = if (showParentListDropDownMenu.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft,
                                    contentDescription = stringResource(R.string.switch_item),
//                                    tint = LocalContentColor.current
                                )
                            },
                            isItemSelected = { it == commit1OidStrState.value },
                            menuItem = { it, selected ->
                                DropDownMenuItemText(
                                    text1 = Libgit2Helper.getShortOidStrByFull(it),
                                )
                            },
                            titleOnLongClick = { initInfoDialog() },
                            itemOnClick = {
                                // close menu
                                showParentListDropDownMenu.value=false

                                val curRepo = changeListCurRepo.value

                                //切换父提交则退出选择模式(现在20240420没用，但日后可能在TreeToTree页面也添加多选功能，比如可选择文件checkout or hard reset到worktree之类的，所以这里先把需要退出选择模式的逻辑写上)(20240818有用了)
                                if(commit1OidStrState.value != it) {
                                    changeListIsFileSelectionMode.value=false  //退出选择模式
                                    changeListPageSelectedItemList.value.clear() //清空已选条目
                                }

                                commit1OidStrState.value=it

                                changeListRequireRefreshFromParentPage(curRepo)
                            },
                            titleOnClick = {
                                // 当比较模式为比较指定的两个提交时(无parents)，点击不会展开下拉菜单(和parents比较才会展开)
                                if (expandable) {
                                    showParentListDropDownMenu.value = true
                                }
                            },
                            showExpandIcon = expandable
                        )
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
                    }else{
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
                        Row {
                            LongPressAbleIconBtn(
                                enabled = enableAction.value && !changeListPageNoRepo.value,

                                tooltipText = stringResource(R.string.filter),
                                icon =  Icons.Filled.FilterAlt,
                                iconContentDesc = stringResource(id = R.string.filter),

                            ) {
                                changeListPageFilterKeyWord.value=TextFieldValue("")
                                changeListPageFilterModeOn.value = true
                            }

                            //go to top
//                            LongPressAbleIconBtn(
//                                tooltipText = stringResource(R.string.go_to_top),
//                                icon =  Icons.Filled.VerticalAlignTop,
//                                iconContentDesc = stringResource(id = R.string.go_to_top),
//                                enabled = true,
//
//                            ) {
//                                UIHelper.scrollToItem(scope, changeListPageItemListState, 0)
//                            }

                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.refresh),
                                icon = Icons.Filled.Refresh,
                                iconContentDesc = stringResource(R.string.refresh),
                            ) {
                                changeListRequireRefreshFromParentPage(changeListCurRepo.value)
                            }

                            if(UserUtil.isPro() && (dev_EnableUnTestedFeature || commitsTreeToTreeDiffReverseTestPassed)) {
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.swap_commits),
                                    icon = Icons.Filled.SwapHoriz,
                                    iconContentDesc = stringResource(R.string.swap_commits),
                                    iconColor = UIHelper.getIconEnableColorOrNull(swap.value)
                                ) {
//                                作用是交换比较的和被比较的提交号(交换左右提交)
                                    swap.value = !swap.value
                                    Msg.requireShow(activityContext.getString(if (swap.value) R.string.swap_commits_on else R.string.swap_commits_off))

                                    val curRepo = changeListCurRepo.value
                                    //swap值不在cl页面的LaunchedEffects key中，所以得刷新下
                                    changeListRequireRefreshFromParentPage(curRepo)
                                }
                            }
                        }

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
        ChangeListInnerPage(
//            stateKeyTag = Cache.combineKeys(stateKeyTag, "ChangeListInnerPage"),
            stateKeyTag = stateKeyTag,

            errScrollState= changeListErrScrollState,
            hasError = changeListHasErr,

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
            commit1OidStr=commit1OidStrState.value,
            commit2OidStr=commit2OidStr,
            commitParentList = commitParentList.value,
            repoId=repoId,
            changeListPageNoRepo=changeListPageNoRepo,
            hasNoConflictItems = changeListPageHasNoConflictItems,  //这选项是worktree和Index页面用的，TreeToTree其实用不到这个选项，只是占位
            changelistPageScrolled=changelistPageScrolled,
            changeListPageFilterModeOn= changeListPageFilterModeOn,
            changeListPageFilterKeyWord=changeListPageFilterKeyWord,
            filterListState = changelistFilterListState,
            swap=swap.value,
            commitForQueryParents = commitForQueryParents,
            openDrawer = {}, //非顶级页面按返回键不需要打开抽屉
            newestPageId = changelistNewestPageId,
            naviTarget = changeListNaviTarget,
            enableFilterState = changeListPageEnableFilterState,
            filterList = changeListFilterList,
            lastClickedItemKey = changeListLastClickedItemKey

        )

    }

}
