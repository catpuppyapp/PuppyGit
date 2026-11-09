package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.AskGitUsernameAndEmailDialogWithSelection
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.CheckoutDialog
import com.catpuppyapp.puppygit.compose.CheckoutDialogFrom
import com.catpuppyapp.puppygit.compose.ClickableText
import com.catpuppyapp.puppygit.compose.CommitMsgMarkDownDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyScrollableColumn
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CopyableDialog2
import com.catpuppyapp.puppygit.compose.CreatePatchSuccessDialog
import com.catpuppyapp.puppygit.compose.CreateTagDialog
import com.catpuppyapp.puppygit.compose.DefaultPaddingText
import com.catpuppyapp.puppygit.compose.DiffCommitsDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LoadMore
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MultiLineClickableText
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PrintNodesInfo
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ResetDialog
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.SetPageSizeDialog
import com.catpuppyapp.puppygit.compose.SimpleCheckBox
import com.catpuppyapp.puppygit.compose.SingleSelectList
import com.catpuppyapp.puppygit.compose.SoftkeyboardVisibleListener
import com.catpuppyapp.puppygit.compose.getDefaultCheckoutOption
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.cherrypickTestPassed
import com.catpuppyapp.puppygit.dev.commitsDiffCommitsTestPassed
import com.catpuppyapp.puppygit.dev.commitsDiffToLocalTestPassed
import com.catpuppyapp.puppygit.dev.createPatchTestPassed
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.dev.diffToHeadTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dev.resetByHashTestPassed
import com.catpuppyapp.puppygit.dev.tagsTestPassed
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.CommitDto
import com.catpuppyapp.puppygit.git.DrawCommitNode
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.CommitItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.getLoadText
import com.catpuppyapp.puppygit.screen.functions.goToTreeToTreeChangeList
import com.catpuppyapp.puppygit.screen.functions.initSearch
import com.catpuppyapp.puppygit.screen.functions.maybeIsGoodKeyword
import com.catpuppyapp.puppygit.screen.functions.search
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.CommitListFrom
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.boolToDbInt
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.cache.NaviCache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doActIfIndexGood
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.getRequestDataByState
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.state.mutableCustomBoxOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.time.TimeZoneUtil
import com.github.git24j.core.Branch
import com.github.git24j.core.GitObject
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository
import com.github.git24j.core.Revwalk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "CommitListScreen"

//TODO 备忘：修改这个页面为可多选的形式，记得加一个 filterList remmeber变量，在过滤模式点击全选或span选择时，操作filterList


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommitListScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic:HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
    repoId: String,



    from: CommitListFrom,
//    isCurrent:Boolean, // HEAD是否指向当前分支

    /*
        `isHEAD` indicate this page is loaded commit history for the HEAD
        only 3 places this value should be true:
         1 from RepoCard
         2 non-detached HEAD and from current branch of branch list
         3 from ChangeList
     */
    isHEAD:Boolean,
    fullOidCacheKey:String,
    shortBranchNameCacheKey:String,  //我忘了，好像这个不一定是分支名，只是代表短名，tag短名应该也行
    naviUp: () -> Unit,
) {

    val stateKeyTag = Cache.getSubPageKey(TAG)


    // softkeyboard show/hidden relate start

    val view = LocalView.current
    val density = LocalDensity.current

    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    //indicate keyboard covered component
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    // which component expect adjust heghit or padding when softkeyboard shown
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    // the padding value when softkeyboard shown
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }

    // softkeyboard show/hidden relate end


    // mayeb will update after checkout, so sate tu mutable state
    val isHEAD = rememberSaveable { mutableStateOf(isHEAD) }
    val onlyUpdateRepoInfoOnce = rememberSaveable { mutableStateOf(false) }







    //画提交树相关变量：开始

    //线和线之间间距
    val lineDistanceInPx = remember(density) { with(density){ 30.dp.toPx() } }

    //圆圈半径
    val nodeCircleRadiusInPx = remember(density) { with(density){ 8.dp.toPx() } }

    //设圆圈x轴起始点为圆圈半径的n倍（一般在2倍以内即可）
    val nodeCircleStartOffsetX = remember(nodeCircleRadiusInPx) { nodeCircleRadiusInPx*1.8f }

    //设圆圈x轴起始点为线和线间距
//    val nodeCircleStartOffsetX = remember(lineDistanceInPx) { lineDistanceInPx }

    //线宽度
    val nodeLineWidthInPx = remember(density) { with(density){ 5.dp.toPx() } }

    //画提交树相关变量：结束












    //已处理这种情况，传参时传有效key，但把value设为空字符串，就解决了
//    println("fullOidKey.isEmpty()="+fullOidKey.isEmpty())  //expect true when nav from repoCard, result: is not empty yet
//    println("fullOidKey="+fullOidKey)  //expect true when nav from repoCard

    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val fullOidValue =  rememberSaveable { NaviCache.getByType<String>(fullOidCacheKey) ?: "" }
    val shortBranchName = rememberSaveable { NaviCache.getByType<String>(shortBranchNameCacheKey) ?: "" }

    //"main" or "origin/main", get by ref#shorthand(), don't use full branchName, such as "refs/remotes/origin/main", will cause resolve branch failed
    val fullOid = rememberSaveable { mutableStateOf(fullOidValue)}  //这个值需要更新，但最终是否使用，取决于常量 useFullOidParam
    val branchShortNameOrShortHashByFullOid =rememberSaveable { mutableStateOf(shortBranchName)}  //如果checkout会改变此状态的值
    val branchShortNameOrShortHashByFullOidForShowOnTitle = rememberSaveable { mutableStateOf(shortBranchName)}  //显示在标题上的 "branch of repo" 字符串，当刷新页面时会更新此变量，此变量依赖branchShortNameOrShortHashByFullOid的值，所以，必须在checkout成功后更新其值（已更新），不然会显示过时信息

    //测试旋转屏幕是否能恢复getThendel的值。测试结果：能
//    println("fullOid: "+fullOid.value)
//    println("branchShortNameOrShortHashByFullOid: "+branchShortNameOrShortHashByFullOid.value)
//    assert(fullOid.value.isNotBlank())

    //TODO 这channel其实非必须，可以修改成：把上次的load Job对象存下来，
    // 离开页面时调用下job.cancel()即可，然后协程内部就会抛异常，可以直接在协程内部try catch到取消异常，不捕获也不会导致外部崩溃
    // 但如果要让函数响应cancel()的话，必须设置调度点或者叫暂停点，死循环可能会长期占用cpu不响应调度，最简单设置调度点的方式就是delay(1)，另外channel.receive()等api也能响应job.cancel()
    val loadChannel = remember { Channel<Int>() }


    //filter相关，开始
    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot = rememberSaveable { mutableStateOf(false) }
    // indicate filter really working
    val enableFilterState = rememberSaveable { mutableStateOf(false)}

    //存储符合过滤条件的条目在源列表中的真实索引。本列表索引对应filter list条目索引，值对应原始列表索引
    val filterIdxList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "filterIdxList",
        listOf<Int>()
    )

    //filter相关，结束

    val filterListState = rememberLazyListState()
    val listState = rememberLazyListState()

    val getActuallyListState = {
        if(enableFilterState.value) filterListState else listState
    }
    // 两个用途：1点击刷新按钮后回到列表顶部 2放到刷新按钮旁边，用户滚动到底部后，想回到顶部，可点击这个按钮
    val goToTop = {
        UIHelper.scrollToItem(scope, getActuallyListState(), 0)
    }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val needRefresh = rememberSaveable { mutableStateOf("CommitList_refresh_init_value_et5c")}

    val fullyRefresh = {
        goToTop()
        changeStateTriggerRefreshPage(needRefresh, StateRequestType.forceReload)
    }


    val getActuallyLastPosition = {
        if(enableFilterState.value) filterLastPosition else lastPosition
    }


    // username and email start
    val repoOfSetUsernameAndEmailDialog = mutableCustomStateOf(stateKeyTag, "repoOfSetUsernameAndEmailDialog") { RepoEntity(id = "") }
    val username = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val showUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val afterSetUsernameAndEmailSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUsernameAndEmailSuccessCallback") { null }
    val initSetUsernameAndEmailDialog = { targetRepo:RepoEntity, callback:(()->Unit)? ->
        try {
            Repository.open(targetRepo.fullSavePath).use { repo ->
                //回显用户名和邮箱
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                username.value = usernameFromConfig
                email.value = emailFromConfig
            }

            repoOfSetUsernameAndEmailDialog.value = targetRepo

            afterSetUsernameAndEmailSuccessCallback.value = callback
            showUsernameAndEmailDialog.value = true
        }catch (e:Exception) {
            Msg.requireShowLongDuration("init username and email dialog err: ${e.localizedMessage}")
            MyLog.e(TAG, "#initSetUsernameAndEmailDialog err: ${e.stackTraceToString()}")
        }
    }

    //若仓库有有效用户名和邮箱，执行task，否则弹窗设置用户名和邮箱，并在保存用户名和邮箱后调用task
    val doTaskOrShowSetUsernameAndEmailDialog = { curRepo:RepoEntity, task:(()->Unit)? ->
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                if(Libgit2Helper.repoUsernameAndEmailInvaild(repo)) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_username_and_email_first))

                    initSetUsernameAndEmailDialog(curRepo, task)
                }else {
                    task?.invoke()
                }
            }
        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#doTaskOrShowSetUsernameAndEmailDialog err: ${e.stackTraceToString()}")
        }
    }

    if(showUsernameAndEmailDialog.value) {
        val curRepo = repoOfSetUsernameAndEmailDialog.value
        val closeDialog = { showUsernameAndEmailDialog.value = false }

        //请求用户设置用户名和邮箱的弹窗
        AskGitUsernameAndEmailDialogWithSelection(
            curRepo = curRepo,
            username = username,
            email = email,
            closeDialog = closeDialog,
            onErrorCallback = { e->
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "set username and email err: ${e.stackTraceToString()}")
            },
            onFinallyCallback = {},
            onSuccessCallback = {
                //已经保存成功，调用回调

                //取出callback
                val successCallback = afterSetUsernameAndEmailSuccessCallback.value
                afterSetUsernameAndEmailSuccessCallback.value = null

                successCallback?.invoke()
            },

            )
    }
    // username and email end



//    val sumPage = MockData.getCommitSum(repoId,branch)
    //获取假数据
//    val list = remember { mutableStateListOf<CommitDto>() };
//    val list = StateUtil.getCustomSaveableState(keyTag = stateKeyTag, keyDesc = "list", initValue = mutableStateListOf<CommitDto>())
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<CommitDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<CommitDto>())

    // start: search states
    val lastListSize = rememberSaveable { mutableIntStateOf(0) }
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    // end: search states

//    val list = rememberSaveable(
//        stateSaver = getSaver()
//    ) {
//        mutableStateOf(getHolder(stateKeyTag, "list",  mutableListOf<CommitDto>()))
//    }
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }
    val commitHistoryRTL = rememberSaveable { mutableStateOf(settings.commitHistoryRTL) }
    val commitHistoryGraph = rememberSaveable { mutableStateOf(settings.commitHistoryGraph) }

    //page size for load more
    val pageSize = rememberSaveable{ mutableStateOf(settings.commitHistoryPageSize) }
    val rememberPageSize = rememberSaveable { mutableStateOf(true) }

    val nextCommitOid = mutableCustomStateOf<Oid>(
        keyTag = stateKeyTag,
        keyName = "nextCommitOid",
        initValue = Cons.git_AllZeroOid
    )

    /*
        first oid in the list of this screen
     */
    val headOidOfThisScreen = mutableCustomStateOf<Oid>(
        keyTag = stateKeyTag,
        keyName = "headOidOfThisScreen",
        initValue = Cons.git_AllZeroOid
    )



    val showTopBarMenu = rememberSaveable { mutableStateOf(false)}
    val isSearchingMode = rememberSaveable { mutableStateOf(false)}
    val isShowSearchResultMode = rememberSaveable { mutableStateOf(false)}
//    val searchKeyword = rememberSaveable { mutableStateOf("")}
    val repoOnBranchOrDetachedHash = rememberSaveable { mutableStateOf("")}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
//    val curCommit = rememberSaveable{ mutableStateOf(CommitDto()) }
    val curCommit = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curCommit",
        initValue = CommitDto()
    )
    val curRepo = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curRepo",
        initValue = RepoEntity(id = "")
    )


    val showFilterByPathsDialog = rememberSaveable { mutableStateOf(false) }
//    val pathsForFilterByPathsDialog = mutableCustomStateListOf(stateKeyTag, "pathsForFilterByPathsDialog") { listOf<String>() }
    val pathsForFilterBuffer = rememberSaveable { mutableStateOf("") }  // cache the paths until user clicked the ok, then assign the value to `pathsForFilter`
    val pathsForFilter = rememberSaveable { mutableStateOf("") }
    val pathsListForFilter = mutableCustomStateListOf(stateKeyTag, "pathsListForFilter") { listOf<String>() }
    val lastPathsListForFilter = mutableCustomStateListOf(stateKeyTag, "lastPathsListForFilter") { listOf<String>() }
    val filterByEntryName = rememberSaveable { mutableStateOf(false) }
    val filterByEntryNameBuffer = rememberSaveable { mutableStateOf(false) }

    if(showFilterByPathsDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.filter_by_paths),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    // get height for add bottom padding when showing softkeyboard
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
//                                println("layoutCoordinates.size.height:${layoutCoordinates.size.height}")
                        // 获取组件的高度
                        // unit is px ( i am not very sure)
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    //这个文字和其他条目间距大些，因为没什么关联
                    MySelectionContainer {
                        Text(stringResource(R.string.filter_commits_which_included_the_paths_leave_empty_for_show_all))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //这个文字和输入框举例小些，因为文字和输入框有点关联
                    MySelectionContainer {
                        Text(stringResource(R.string.per_line_one_path), fontWeight = FontWeight.Light)
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    //输入框
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = pathsForFilterBuffer.value,
                        onValueChange = {
                            pathsForFilterBuffer.value = it
                        },
                        label = {
                            Text(stringResource(R.string.paths))
                        },
                    )

                    Spacer(Modifier.height(10.dp))

                    Column(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                        ,
                        horizontalAlignment = Alignment.End
                    ) {
                        ClickableText (
                            text = stringResource(R.string.clear),
                            modifier = MyStyleKt.ClickableText.modifier.clickable {
                                pathsForFilterBuffer.value = ""
                            },
                            fontWeight = FontWeight.Light
                        )

                        Spacer(Modifier.height(10.dp))

                    }

                    // libgit2 1.8.4, still has this bug
                    //@bug TODO libgit2 1.7.2 Tree.entryByName() has bug, it always return null, when fixed in futrue, can uncomment this code
//                    MyCheckBox(stringResource(R.string.i_input_file_names), filterByEntryNameBuffer)
//                    if(filterByEntryNameBuffer.value) {
//                        Text(stringResource(R.string.will_filter_by_file_name_not_by_path), fontWeight = FontWeight.Light)
//                    }
                }
            },
            onCancel = {showFilterByPathsDialog.value = false}
        ) {
            showFilterByPathsDialog.value = false

            doJobThenOffLoading {
                // assigning new value
                filterByEntryName.value = filterByEntryNameBuffer.value
                pathsForFilter.value = pathsForFilterBuffer.value
                pathsListForFilter.value.clear()
                pathsForFilter.value.lines().forEachBetter {
                    if(it.isNotEmpty()) {
                        pathsListForFilter.value.add(it)
                    }
                }

            }
        }
    }



//    val doSearch = {
//        isShowSearchResultMode.value = true;
//        isSearchingMode.value = false;
//        // TODO 搜索提交时判断 “如果包含 / 或者 非hex字符” 就只搜分支列表（然后判断分支是否符号引用若是peel commit，若否直接取出id）。
//        // do search with keyword, may need async and give user a loading anime when querying data
//        // use "searchKeyword.value" do search
////        println("doSearch with:::"+searchKeyword.value)
//    }


    val loadMoreLoading = rememberSaveable { mutableStateOf(false)}
    val loadMoreText = rememberSaveable { mutableStateOf("")}
    val hasMore = rememberSaveable { mutableStateOf(false)}




    val loadingStrRes = stringResource(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(loadingStrRes)}
    val showLoadingDialog = rememberSaveable { mutableStateOf(false)}

    val loadingOn = { msg:String->
        loadingText.value = msg
        showLoadingDialog.value=true
    }
    val loadingOff = {
        loadingText.value=loadingStrRes
        showLoadingDialog.value=false
    }

//    val loadingMore = StateUtil.getRememberSaveableState(initValue = false)
//    val hasMore = {
//        nextCommitOid.value != null &&
//    }

    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }


    val revwalk = mutableCustomStateOf<Revwalk?>(stateKeyTag, "revwalk", null)
    val repositoryForRevWalk = mutableCustomStateOf<Repository?>(stateKeyTag, "repositoryForRevWalk", null)
    val loadLock = mutableCustomStateOf<Mutex>(stateKeyTag, "loadLock", Mutex())
    val draw_lastOutputNodes = mutableCustomBoxOf(stateKeyTag, "draw_lastOutputNodes") { listOf<DrawCommitNode>() }
    val resetDrawNodesInfo = {
        draw_lastOutputNodes.value = listOf()
    }

    //显示本地分支历史记录时，此值存储本地领先远程的提交数，这些提交线将用不同于已推送的提交的颜色显示（同一条线，两种颜色）
    // 注：由于此变量不会在渲染时修改，所以可以用state，若在渲染时需要修改此变量则需要改用自定义存储器的rememberSaveable Box来避免循环渲染
    val drawLocalAheadUpstreamCount = rememberSaveable { mutableStateOf(0) }

    val doLoadMore = doLoadMore@{ repoFullPath: String, oid: Oid, firstLoad: Boolean, forceReload: Boolean, loadToEnd:Boolean ->
        //第一次查询的时候是用head oid查询的，所以不会在这里返回
        //用全0oid替代null
        if (oid.isNullOrEmptyOrZero) {  //已经加载到最后一个元素了，其实正常来说，如果加载到最后一个元素，应该已经赋值给nextCommitOid了，然后加载更多按钮也会被禁用，所以多半不会再调用这个方法，这的判断只是为了以防万一
            nextCommitOid.value = oid
            return@doLoadMore
        }
        //无效仓库储存路径
        if (repoFullPath.isBlank()) {
            return@doLoadMore
        }
        //恢复数据
        if (firstLoad && list.value.isNotEmpty() && !forceReload) {
//            if(debugModeOn) {
//                //如果列表能恢复，那上次的oid应该也能恢复，问题不大
//                println("nextCommitOid.value="+nextCommitOid.value)
//            }
//            list.requireRefreshView()
            return@doLoadMore
        }

        //加载更多
        // x 不推荐用scope.launch，有可能卡住) 这个如果用scope.launch，似乎会随页面释放而取消任务，正在执行中的代码会在调度点抛出任务已取消异常，
        // 但是，若用scope.launch，页面有可能短暂卡住，不如用io协程流畅
        doJobThenOffLoading job@{
            loadLock.value.withLock {
                loadMoreLoading.value = true
                loadMoreText.value = activityContext.getString(R.string.loading)

                try {
                    if (firstLoad || forceReload || repositoryForRevWalk.value==null || revwalk.value==null) {
                        // do reset: clear list and release old repo instance
                        //如果是第一次加载或刷新页面（重新初始化页面），清下列表
                        // if is first load or refresh page, clear list
                        list.value.clear()

                        //重置绘图节点信息
                        resetDrawNodesInfo()

                        //先初始化为0，若有必要，后面会更新
                        drawLocalAheadUpstreamCount.value = 0

                        runCatching {
                            //如果是当前浏览的是本地分支的提交记录并且其存在有效的上游分支，检查是否领先上游分支，若领先，领先的提交将会在同一条线上用另一种颜色显示
                            Repository.open(repoFullPath).use { repo ->
                                //有短分支参数则用，无则使用仓库对象里的分支名
                                val shortBranchName = shortBranchName.ifEmpty { curRepo.value.branch }

                                //因为只有本地分支需要显示这个差异，所以这里只需解析本地分支
                                val localBranchOrNull = Libgit2Helper.resolveBranch(repo, shortBranchName, Branch.BranchType.LOCAL)
                                //在浏览本地分支的提交历史记录
                                if(localBranchOrNull != null) {
                                    val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
                                    if(upstream.isPublished) {
                                        val upstreamCommitRet = Libgit2Helper.resolveCommitByHashOrRef(repo, upstream.remoteBranchRefsRemotesFullRefSpec)
                                        if(upstreamCommitRet.success()) {
                                            val (ahead, behind) = Libgit2Helper.getAheadBehind(repo, oid, upstreamCommitRet.data!!.id())
                                            //若本地领先远程且不落后
                                            if(ahead > 0 && behind == 0) {
                                                //需要绘制领先的提交的线的颜色的节点数
                                                drawLocalAheadUpstreamCount.value = ahead
                                            }
                                        }
                                    }
                                }

                            }
                        }

                        // close old repo, release resource
                        repositoryForRevWalk.value?.close()
                        repositoryForRevWalk.value = null  // if don't set to null, when assign new instance to state, implicitly call equals(), the closed repo will thrown an err


                        // do init: create new repo instance
                        val repo = Repository.open(repoFullPath)
                        //get new revwalk instance
                        val newRevwalk = Libgit2Helper.createRevwalk(repo, oid)
                        if(newRevwalk == null) {
                            val oidStr = oid.toString()
                            Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.create_revwalk_failed_oid), listOf(Libgit2Helper.getShortOidStrByFull(oidStr))))
                            createAndInsertError(repoId, "create Revwalk failed, oid=$oidStr")
                            return@job
                        }

//                    println("repo.equals(repositoryForRevWalk.value):${repo.equals(repositoryForRevWalk.value)}")  // expect: false, output: false

                        // the revwalk must use with the repo instance which created it, else will throw an err "signed...prefix -..." something
                        // revwalk必须与创建它的仓库一起使用，否则会报错，报什么"signed...prefix -..."之类的错误
                        repositoryForRevWalk.value = repo
                        revwalk.value = newRevwalk
                        nextCommitOid.value = newRevwalk.next() ?: Cons.git_AllZeroOid

//                    println("oldRepoInstance == repositoryForRevWalk.value:${oldRepoInstance == repositoryForRevWalk.value}")  // expect:false, output:false
                        // release memory
//                    oldRepoInstance?.close()
                    }

                    val repo = repositoryForRevWalk.value ?: throw RuntimeException("repo for revwalk is null")

                    if(nextCommitOid.value.isNullOrEmptyOrZero) {
                        //更新变量
                        hasMore.value = false
                        loadMoreText.value = activityContext.getString(R.string.end_of_the_list)
                    }else {
                        //start travel commit history
                        Libgit2Helper.getCommitList(
                            repo,
                            revwalk.value!!,
                            nextCommitOid.value,
                            repoId,
                            if(loadToEnd) Int.MAX_VALUE else pageSize.value,
                            retList = list.value,  //直接赋值给状态列表了，若性能差，可实现一个批量添加机制，比如查出50个条目添加一次，之类的
                            loadChannel = loadChannel,
                            checkChannelFrequency = settings.commitHistoryLoadMoreCheckAbortSignalFrequency,
                            settings,
                            draw_lastOutputNodes,
                        )

                        //update state
                        nextCommitOid.value = revwalk.value!!.next() ?: Cons.git_AllZeroOid
                        hasMore.value = !nextCommitOid.value.isNullOrEmptyOrZero
                        loadMoreText.value = if (hasMore.value) activityContext.getString(R.string.load_more) else activityContext.getString(R.string.end_of_the_list)

                    }


                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?: "unknown err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, "err: $errMsg")
                    MyLog.e(TAG, "#doLoadMore: err: ${e.stackTraceToString()}")
                }finally {
                    loadMoreLoading.value = false

                    triggerReFilter(filterResultNeedRefresh)
                }
            }
        }
    }

    val clipboardManager = LocalClipboardManager.current

    val showSquashDialog = rememberSaveable { mutableStateOf(false) }
    val forceSquash = rememberSaveable { mutableStateOf(false) }
    val headFullNameForSquashDialog = rememberSaveable { mutableStateOf("") } // branch full name or HEAD
    val headCommitFullOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val headCommitShortOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val targetCommitFullOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val targetCommitShortOidForSquashDialog = rememberSaveable { mutableStateOf("") }
    val commitMsgForSquashDialog = mutableCustomStateOf(stateKeyTag, "commitMsgForSquashDialog") { TextFieldValue("") }
    val usernameForSquashDialog = rememberSaveable { mutableStateOf("") }
    val emailForSquashDialog = rememberSaveable { mutableStateOf("") }
    val closeSquashDialog = {
        showSquashDialog.value = false
    }
    val initShowSquashDialog = {targetFullOid:String, targetShortOid:String, headFullOid:String, headFullName:String, username:String, email:String->
        headFullNameForSquashDialog.value = headFullName

        headCommitFullOidForSquashDialog.value= headFullOid
        headCommitShortOidForSquashDialog.value = Libgit2Helper.getShortOidStrByFull(headFullOid)

        targetCommitFullOidForSquashDialog.value = targetFullOid
        targetCommitShortOidForSquashDialog.value = targetShortOid



        usernameForSquashDialog.value = username
        emailForSquashDialog.value = email

        forceSquash.value = false

        showSquashDialog.value = true
    }

    val clearCommitMsg = {
        commitMsgForSquashDialog.value = TextFieldValue("")
    }

    val genSquashCommitMsg = {
        Libgit2Helper.squashCommitsGenCommitMsg(targetCommitShortOidForSquashDialog.value, headCommitShortOidForSquashDialog.value)
    }

    if(showSquashDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.squash),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text = replaceStringResList(
                                stringResource(R.string.squash_commits_not_include_the_left_commit),
                                listOf(targetCommitShortOidForSquashDialog.value, headCommitShortOidForSquashDialog.value)
                            ),
                            fontWeight = FontWeight.Light
                        )
                    }

                    Spacer(Modifier.height(15.dp))

                    TextField(
                        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                        modifier = Modifier.fillMaxWidth(),
                        value = commitMsgForSquashDialog.value,
                        onValueChange = {
                            commitMsgForSquashDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.commit_message))
                        },
                        placeholder = {
                            Text(stringResource(R.string.input_your_commit_message))
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    MultiLineClickableText(stringResource(R.string.you_can_leave_msg_empty_will_auto_gen_one)) {
                        Repository.open(curRepo.value.fullSavePath).use { repo ->
                            commitMsgForSquashDialog.value = TextFieldValue(genSquashCommitMsg())
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    MyCheckBox(stringResource(R.string.force), forceSquash)

                    if(forceSquash.value) {
                        MySelectionContainer {
                            DefaultPaddingText(
                                text = stringResource(R.string.if_index_contains_uncommitted_changes_will_commit_as_well),
                                color = MyStyleKt.TextColor.danger(),
                            )
                        }
                    }

                }
            },
            onCancel = closeSquashDialog
        ) {
            closeSquashDialog()

            val commitMsg = commitMsgForSquashDialog.value.text.ifBlank { genSquashCommitMsg() }
            val targetFullOid = targetCommitFullOidForSquashDialog.value
//            val headOid = headCommitFullOidForSquashDialog.value
            val headFullName = headFullNameForSquashDialog.value
            val username = usernameForSquashDialog.value
            val email = emailForSquashDialog.value

//            println("ffffffffffffffff-headFullName = $headFullName")  // same with expect: when detached show "HEAD", else show current branche name

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) job@{
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val checkRet = Libgit2Helper.squashCommitsCheckBeforeExecute(repo, forceSquash.value)
                        if(checkRet.hasError()) {
                            throw checkRet.exception ?: RuntimeException(checkRet.msg)
                        }

                        val ret = Libgit2Helper.squashCommits(
                            repo = repo,
                            targetFullOidStr = targetFullOid,
                            commitMsg = commitMsg,
                            username = username,
                            email = email,
                            currentBranchFullNameOrHEAD = headFullName,
                            settings = settings
                        )

                        if(ret.hasError()) {
                            throw ret.exception ?: RuntimeException(ret.msg)
                        }

                        // update fullOid for refresh list of this screen
                        fullOid.value = ret.data!!.toString()
                    }

                    clearCommitMsg()

                    Msg.requireShow(activityContext.getString(R.string.success))

                    fullyRefresh()
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    createAndInsertError(curRepo.value.id, "squash err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#SquashDialog err: " + e.stackTraceToString())
                }
            }
        }
    }

//
//    val showViewDialog = rememberSaveable { mutableStateOf(false)}
//    val viewDialogText = rememberSaveable { mutableStateOf("")}
//    val viewDialogTitle = rememberSaveable { mutableStateOf("")}
//
//    val requireShowViewDialog = { title: String, text: String ->
//        viewDialogTitle.value = title
//        viewDialogText.value = text
//        showViewDialog.value = true
//    }
//
//    if (showViewDialog.value) {
//        ConfirmDialog(
//            title = viewDialogTitle.value,
//            requireShowTextCompose = true,
//            textCompose = {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .verticalScroll(rememberScrollState())
//                ) {
//                    Text(
//                        text = viewDialogText.value
//                    )
//                }
//            },
//            cancelBtnText = stringResource(id = R.string.close),
//            okBtnText = stringResource(id = R.string.copy),
//            onCancel = {
//                showViewDialog.value = false
//
//            }
//        ) { //复制到剪贴板
//            showViewDialog.value = false
//            clipboardManager.setText(AnnotatedString(viewDialogText.value))
//            requireShowToast(appContext.getString(R.string.copied))
//
//        }
//    }

    //参数1，要创建的本地分支名；2是否基于HEAD创建分支，3如果不基于HEAD，提供一个引用名
    //只有在basedHead为假的时候，才会使用baseRefSpec
//    val doCreateBranch: (String, String, Boolean) -> Ret<Triple<String, String, String>?> = doCreateBranch@{ branchNamePram: String, baseRefSpec: String, overwriteIfExists:Boolean ->
//            Repository.open(curRepo.value.fullSavePath).use { repo ->
//
//                //第4个参数是base head，在提交页面创建，肯定不base head，base head是在分支页面用顶栏的按钮创建分支的默认选项
//                val ret = Libgit2Helper.doCreateBranch(
//                    repo,
//                    repoId,
//                    branchNamePram,
//                    false,
//                    baseRefSpec,
//                    false,
//                    overwriteIfExists
//                )
//
//                return@doCreateBranch ret
//            }
//        }
//
//    val doCheckoutBranch: suspend (String, String, String, Boolean, Boolean,Boolean, Int) -> Ret<Oid?> =
//        doCheckoutLocalBranch@{ shortBranchNameOrHash: String, fullBranchNameOrHash: String, upstreamBranchShortNameParam: String, isDetachCheckout: Boolean , force:Boolean, updateHead:Boolean, checkoutType:Int->
//            Repository.open(curRepo.value.fullSavePath).use { repo ->
//                val ret = Libgit2Helper.doCheckoutBranchThenUpdateDb(
//                    repo,
//                    repoId,
//                    shortBranchNameOrHash,
//                    fullBranchNameOrHash,
//                    upstreamBranchShortNameParam,
//                    checkoutType,
//                    force,
//                    updateHead
//                )
//
//                return@doCheckoutLocalBranch ret
//            }
//        }
//
//    val checkoutOptionDontUpdateHead = 0
//    val checkoutOptionDetachHead = 1
//    val checkoutOptionCreateBranch = 2
//    val checkoutOptionDefault = checkoutOptionCreateBranch  //默认选中创建分支，detach head如果没reflog，有可能丢数据
//    val checkoutRemoteOptions = listOf(
//        appContext.getString(R.string.dont_update_head),
//        appContext.getString(R.string.detach_head),
//        appContext.getString(R.string.new_branch) + "(" + appContext.getString(R.string.recommend) + ")"
//    )

//    val checkoutSelectedOption = StateUtil.getRememberSaveableIntState(initValue = checkoutOptionDefault)
//    val checkoutRemoteCreateBranchName = StateUtil.getRememberSaveableState(initValue = "")
//    val checkoutUserInputCommitHash = StateUtil.getRememberSaveableState(initValue = "")
    val requireUserInputCommitHash = rememberSaveable { mutableStateOf(false)}
//    val forceCheckout = StateUtil.getRememberSaveableState(initValue = false)
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false)}
    //当前长按commit在列表中的索引，用来更新单个条目时使用，为-1时无效，不要执行操作
    val curCommitIndex = rememberSaveable{mutableIntStateOf(-1)}
//    val initCheckoutDialog = { requireUserInputHash:Boolean ->
//        checkoutSelectedOption.intValue = checkoutOptionDefault
//        requireUserInputCommitHash.value = requireUserInputHash
//        forceCheckout.value = false
//        showCheckoutDialog.value = true
//    }


    // 用索引更新bug频出，废弃
//    val updateCurCommitInfo = {repoFullPath:String, curCommitIdx:Int, commitOid:String, list:MutableList<CommitDto> ->
//        doActIfIndexGood(curCommitIdx, list) {
//            Repository.open(repoFullPath).use { repo ->
//                val reQueriedCommitInfo = Libgit2Helper.getSingleCommit(repo, repoId, commitOid, settings)
//                val oldCommit = list[curCommitIdx]
//                list[curCommitIdx] = reQueriedCommitInfo.copy(draw_inputs = oldCommit.draw_inputs, draw_outputs=oldCommit.draw_outputs)
//            }
//        }
//    }




    val refreshCommitByPredicate = { curRepo:RepoEntity, predicate:(CommitDto)->Boolean ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            var commitQueryCache:CommitDto? = null

            //更新过滤列表
            if(enableFilterState.value) {
                val filterListIndex = filterList.value.indexOfFirst { predicate(it) }
                if(filterListIndex >= 0) {
                    //重查条目信息并更新列表
                    // 单查条目无法重建图形信息，所以保留原提交的图形信息
                    filterList.value[filterListIndex] = filterList.value[filterListIndex].let {
                        Libgit2Helper.getSingleCommit(repo, repoId = curRepo.id, commitOidStr = it.oidStr, settings)
                            .copy(draw_inputs = it.draw_inputs, draw_outputs = it.draw_outputs).let { commitQueryCache = it; it }
                    }
                }
            }

            //更新源列表
            val srcListIndex = list.value.indexOfFirst { predicate(it) }
            if(srcListIndex >= 0) {
                list.value[srcListIndex] = list.value[srcListIndex].let {
                    // if same commit, use, else requery, most time should same, so no need re-query at here
                    if(commitQueryCache != null && commitQueryCache.oidStr == it.oidStr) {
                        commitQueryCache
                    }else {  // not same, requery
                        Libgit2Helper.getSingleCommit(repo, repoId = curRepo.id, commitOidStr = it.oidStr, settings)
                            .copy(draw_inputs = it.draw_inputs, draw_outputs = it.draw_outputs)
                    }
                }
            }
        }
    }



    val nameOfNewTag = rememberSaveable { mutableStateOf("")}
    val overwriteIfNameExistOfNewTag = rememberSaveable { mutableStateOf(false)}  // force
    val showDialogOfNewTag = rememberSaveable { mutableStateOf(false)}
    val hashOfNewTag = rememberSaveable { mutableStateOf( "")}
    val msgOfNewTag = rememberSaveable { mutableStateOf( "")}
//    val requireUserInputHashOfNewTag = StateUtil.getRememberSaveableState(initValue = false)
    val annotateOfNewTag = rememberSaveable { mutableStateOf(false)}
    val initNewTagDialog = { hash:String ->
        doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
            hashOfNewTag.value = hash  //把hash设置为当前选中的commit的hash

            overwriteIfNameExistOfNewTag.value = false
            showDialogOfNewTag.value = true
        }
    }
    
    if(showDialogOfNewTag.value) {
        CreateTagDialog(
            showDialog = showDialogOfNewTag,
            curRepo = curRepo.value,
            tagName = nameOfNewTag,
            commitHashShortOrLong = hashOfNewTag,
            annotate = annotateOfNewTag,
            tagMsg = msgOfNewTag,
            force = overwriteIfNameExistOfNewTag,
        ) success@{ newTagOidStr ->
            if(newTagOidStr.isBlank()) {  //should never into here
                Msg.requireShowLongDuration(activityContext.getString(R.string.tag_oid_invalid))
                return@success
            }

            fullOid.value = newTagOidStr

            // tag name same but create still succeed, it means force create tag checked, need refresh full list
            val tagNameIfFromTagList = shortBranchName
            if(from == CommitListFrom.TAG && nameOfNewTag.value == tagNameIfFromTagList) {
                fullyRefresh()
            }else {
                // force checked, need remove old tag if exists
                if(overwriteIfNameExistOfNewTag.value) {
                    // update old commit which pointed by tag
                    try {
                        refreshCommitByPredicate(curRepo.value) {
                            it.tagShortNameList.contains(nameOfNewTag.value)
                        }
                    }catch (e: Exception) {
                        //记不记没啥意义
//                    MyLog.d(TAG, "remove tag from commit err")
                    }
                }

                // update commit to let it add new tag to its tag list,
                // the target commit is pointed by new tag (this commit is long pressed target by user)
                runCatching {
                    refreshCommitByPredicate(curRepo.value) {
                        it.oidStr == newTagOidStr
                    }
                }
                //创建tag后没必要刷新整个页面，更新对应commit即可
            }
        }
    }

//    val getCheckoutOkBtnEnabled:()->Boolean = getCheckoutOkBtnEnabled@{
//        //请求checkout时创建分支但没填分支，返回假
//        if(checkoutSelectedOption.intValue == checkoutOptionCreateBranch && checkoutRemoteCreateBranchName.value.isBlank()) {
//            return@getCheckoutOkBtnEnabled false
//        }
//
//        //请求checkout to hash但没填hash，返回假
//        if(requireUserInputCommitHash.value && checkoutUserInputCommitHash.value.isBlank()) {
//            return@getCheckoutOkBtnEnabled false
//        }
//
//        return@getCheckoutOkBtnEnabled true
//    }


    //初始化组件版本的checkout对话框
    val initCheckoutDialogComposableVersion = { requireUserInputHash:Boolean ->
        requireUserInputCommitHash.value = requireUserInputHash
        showCheckoutDialog.value = true
    }

    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(getDefaultCheckoutOption(false)) }

    if(showCheckoutDialog.value) {
        CheckoutDialog(
            checkoutSelectedOption = checkoutSelectedOption,
            showCheckoutDialog=showCheckoutDialog,
            branchName = branchNameForCheckout,
            from = CheckoutDialogFrom.OTHER,
            expectCheckoutType = Cons.checkoutType_checkoutCommitThenDetachHead,
            curRepo = curRepo.value,
            shortName = curCommit.value.shortOidStr,
            fullName = curCommit.value.oidStr,
            curCommitOid = curCommit.value.oidStr,
            curCommitShortOid = curCommit.value.shortOidStr,
            requireUserInputCommitHash = requireUserInputCommitHash.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            headChangedCallback = j@{
                if(from == CommitListFrom.BRANCH) {
                    isHEAD.value = Repository.open(curRepo.value.fullSavePath).use { repo ->
                        Libgit2Helper.resolveHEAD(repo)?.shorthand() == shortBranchName
                    }

                    // current branch was not HEAD, but now it is
                    if(isHEAD.value) {
                        fullyRefresh()
                        return@j
                    }
                }

                onlyUpdateRepoInfoOnce.value = true
                changeStateTriggerRefreshPage(needRefresh)
            },
            refreshPage = { checkout:Boolean, targetOid:String, forceCreateBranch:Boolean, branchName:String ->
                MyLog.d(TAG, "CommitListScreen#CheckoutDialog#refreshPage(): checkout=$checkout, targetOid=$targetOid, forceCreateBranch=$forceCreateBranch, branchName=$branchName")

                val targetMaybeIsHash = Libgit2Helper.maybeIsHash(targetOid)
                if(targetMaybeIsHash) {
                    fullOid.value = targetOid
                }

                if((from != CommitListFrom.FOLLOW_HEAD || !checkout) && (forceCreateBranch.not() || branchName != shortBranchName)) {
                    // remove branch from commit list if force created checked
                    if(forceCreateBranch) {
                        runCatching {
                            refreshCommitByPredicate(curRepo.value) {
                                it.branchShortNameList.contains(branchName)
                            }
                        }
                    }

                    // update target branch which created branch
                    if(targetMaybeIsHash) {
                        runCatching {
                            refreshCommitByPredicate(curRepo.value) {
                                it.oidStr == targetOid
                            }
                        }
                    }
                }else {
                    fullyRefresh()
                }
            },
        )
    }


    val resetOid = rememberSaveable { mutableStateOf("")}
//    val acceptHardReset = StateUtil.getRememberSaveableState(initValue = false)
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {
        showResetDialog.value = false
    }

    if (showResetDialog.value) {
        ResetDialog(
            fullOidOrBranchOrTag = resetOid,
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,
            repoId=curRepo.value.id,
            refreshPage = { oldHeadCommitOid, isDetached, resetTargetCommitOid ->
                val curRepo = curRepo.value

                //顺便更新下仓库的detached状态，因为若从分支条目进来，不怎么常刷新页面，所以仓库状态可能过时
                curRepo.isDetached = boolToDbInt(isDetached)

                // maybe not use, but update is ok
                fullOid.value = resetTargetCommitOid

                // if is HEAD, after reset, need fully refresh whole list;
                //  else only need update branch list when repo not under detached HEAD
                if(isHEAD.value) {
                    fullyRefresh()
                }else if(!isDetached) {
                    // if is not detached, update old and target commit to let them show branch list correctly;
                    //  if is detached, no branch need update, because, detached HEAD, you know

                    // update commit which contains cur branch to remove branch from it's branch list
                    runCatching {
                        val curBranch = curRepo.branch

                        refreshCommitByPredicate(curRepo) {
                            it.branchShortNameList.contains(curBranch)
                        }
                    }

                    // update reset target to make it's branch list contains current branch
                    runCatching {
                        refreshCommitByPredicate(curRepo) {
                            it.oidStr == resetTargetCommitOid
                        }
                    }

                }
            }
        )

    }

    val showNodesInfoDialog = rememberSaveable { mutableStateOf(false) }
    val commitOfNodesInfo = mutableCustomStateOf(stateKeyTag, "commitOfNodesInfo") { CommitDto() }
    val showNodesInfo = { curCommit:CommitDto ->
        commitOfNodesInfo.value = curCommit

        showNodesInfoDialog.value = true
    }
    if(showNodesInfoDialog.value) {
        val commitOfNodesInfo = commitOfNodesInfo.value

        CopyableDialog2(
            title = stringResource(R.string.nodes),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    val hasOutputs = commitOfNodesInfo.draw_outputs.isNotEmpty()

                    if(commitOfNodesInfo.draw_inputs.isNotEmpty()) {
                        PrintNodesInfo(
                            title = "Inputs",
                            nodes = commitOfNodesInfo.draw_inputs,
                            appendEndNewLine = hasOutputs,
                        )
                    }


                    if(hasOutputs) {
                        PrintNodesInfo(
                            title = "Outputs",
                            nodes = commitOfNodesInfo.draw_outputs,
                            appendEndNewLine = false,
                        )
                    }
                }
            },
            onCancel = { showNodesInfoDialog.value = false },
            cancelBtnText = stringResource(R.string.close),
            okCompose = {},
            onOk = {}
        )
    }

    val showDetailsDialog = rememberSaveable { mutableStateOf( false)}
    val detailsString = rememberSaveable { mutableStateOf( "")}
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(R.string.details),
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }

    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { curCommit:CommitDto ->
        textOfItemMsgDialog.value = curCommit.msg
        showItemMsgDialog.value = true
    }
    if(showItemMsgDialog.value) {
        CommitMsgMarkDownDialog(
            dialogVisibleState = showItemMsgDialog,
            text = textOfItemMsgDialog.value,
            previewModeOn = previewModeOnOfItemMsgDialog,
            useSystemFonts = useSystemFontsForItemMsgDialog,
            basePathNoEndSlash = curRepo.value.fullSavePath,
        )
    }

    val showItemDetails = { curCommit:CommitDto ->
        // onClick()
//                    requireShowViewDialog(appContext.getString(R.string.view_hash), curCommit.value.oidStr)
        val suffix = "\n\n"
        val sb = StringBuilder()

        sb.append("${activityContext.getString(R.string.hash)}: "+curCommit.oidStr).append(suffix)
        sb.append("${activityContext.getString(R.string.author)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curCommit.author, curCommit.email)).append(suffix)
        sb.append("${activityContext.getString(R.string.committer)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curCommit.committerUsername, curCommit.committerEmail)).append(suffix)
        //实际使用的时区偏移量
        sb.append("${activityContext.getString(R.string.date)}: "+curCommit.dateTime +" (${curCommit.getActuallyUsingTimeZoneUtcFormat(settings)})").append(suffix)
        // commit中携带的时区偏移量
        sb.append("${activityContext.getString(R.string.timezone)}: "+(formatMinutesToUtc(curCommit.originTimeOffsetInMinutes))).append(suffix)

        if(curCommit.branchShortNameList.isNotEmpty()){
            sb.append((if(curCommit.branchShortNameList.size > 1) activityContext.getString(R.string.branches) else activityContext.getString(R.string.branch)) +": "+curCommit.cachedLineSeparatedBranchList()).append(suffix)
        }
        if(curCommit.tagShortNameList.isNotEmpty()) {
            sb.append((if(curCommit.tagShortNameList.size > 1) activityContext.getString(R.string.tags) else activityContext.getString(R.string.tag)) +": "+curCommit.cachedLineSeparatedTagList()).append(suffix)
        }
        if(curCommit.parentOidStrList.isNotEmpty()) {
            sb.append((if(curCommit.parentOidStrList.size > 1) activityContext.getString(R.string.parents) else activityContext.getString(R.string.parent)) +": "+curCommit.cachedLineSeparatedParentFullOidList()).append(suffix)
        }

        if(curCommit.hasOther()) {
            sb.append("${activityContext.getString(R.string.other)}: ${curCommit.getOther(activityContext, false)}").append(suffix)
        }



        //追加可过滤的flag
        sb.append("${Cons.flagStr}: ${curCommit.getOther(activityContext, true)}").append(suffix)

        //可变长度的commit msg放最后，以免太长滑半天滑不到底影响看其他条目
        sb.append("${activityContext.getString(R.string.msg)}: "+curCommit.msg).append(suffix)

        detailsString.value = sb.removeSuffix(suffix).toString()

        showDetailsDialog.value = true
    }

    // 向下滚动监听，开始
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }

    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}

//    val filterListState =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "filterListState", LazyListState(0,0))
//    val firstVisible = remember { derivedStateOf { if(enableFilterState.value) filterListState.value.firstVisibleItemIndex else listState.firstVisibleItemIndex } }
//    ScrollListener(
//        nowAt = firstVisible.value,
//        onScrollUp = {scrollingDown.value = false}
//    ) { // onScrollDown
//        scrollingDown.value = true
//    }
//
//    val lastAt = remember { mutableIntStateOf(0) }
//    val lastIsScrollDown = remember { mutableStateOf(false) }
//    val forUpdateScrollState = remember {
//        derivedStateOf {
//            val nowAt = if(enableFilterState.value) {
//                filterListState.firstVisibleItemIndex
//            } else {
//                listState.firstVisibleItemIndex
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
//                pageScrolled.value = true
//            }
//
//            lastIsScrollDown.value = scrolledDown
//        }
//    }.value
    // 向下滚动监听，结束


    val showDiffCommitDialog = rememberSaveable { mutableStateOf(false) }
    val diffCommitsDialogCommit1 = mutableCustomStateOf(stateKeyTag, "diffCommitsDialogCommit1") { TextFieldValue("") }
    val diffCommitsDialogCommit2 = mutableCustomStateOf(stateKeyTag, "diffCommitsDialogCommit2") { TextFieldValue("") }
    val diffCommitsDialogTrueFocusCommit1FalseFocus2 = rememberSaveable { mutableStateOf(false) }
    if(showDiffCommitDialog.value) {
        DiffCommitsDialog(
            showDialog = showDiffCommitDialog,
            commit1 = diffCommitsDialogCommit1,
            commit2 = diffCommitsDialogCommit2,
            trueFocusCommit1FalseFocus2 = diffCommitsDialogTrueFocusCommit1FalseFocus2.value,
            curRepo = curRepo.value,
        )
    }

    val initDiffCommitsDialog = { commit1:String?, commit2:String?, focus1:Boolean ->
        if(commit1 != null) {
            diffCommitsDialogCommit1.value = TextFieldValue(commit1)
        }

        if(commit2 != null) {
            diffCommitsDialogCommit2.value = TextFieldValue(commit2)
        }

        if(focus1) {
            diffCommitsDialogCommit1.apply {
                value = value.copy(selection = TextRange(0, value.text.length))
            }
        }else {
            diffCommitsDialogCommit2.apply {
                value = value.copy(selection = TextRange(0, value.text.length))
            }
        }

        diffCommitsDialogTrueFocusCommit1FalseFocus2.value = focus1
        showDiffCommitDialog.value = true
    }


    val savePatchPath= rememberSaveable { mutableStateOf("")}
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}

    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }



    val showCreatePatchDialog = rememberSaveable { mutableStateOf(false)}
    val createPatchTargetHash = rememberSaveable { mutableStateOf("")}
    val createPatchParentHash = rememberSaveable { mutableStateOf("")}
    val createPatchParentList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "createPatchParentList", listOf<String>())

    val initCreatePatchDialog = { targetFullHash:String, defaultParentFullHash:String, parentList:List<String> ->
        createPatchParentList.value.clear()
        createPatchParentList.value.addAll(parentList)

        createPatchTargetHash.value = targetFullHash
        createPatchParentHash.value = defaultParentFullHash


        showCreatePatchDialog.value = true
    }


    if(showCreatePatchDialog.value) {
        val shortTarget = Libgit2Helper.getShortOidStrByFull(createPatchTargetHash.value)
        val shortParent = Libgit2Helper.getShortOidStrByFull(createPatchParentHash.value)

        val padding=10.dp

        ConfirmDialog(
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            title = stringResource(R.string.create_patch),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text =  buildAnnotatedString {
                                append(stringResource(R.string.target)+": ")

                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append(shortTarget)
                                }
                            },

                            modifier = Modifier.padding(horizontal = padding)
                        )
                    }

                    Row(modifier = Modifier.padding(padding)) {
                        Text(text = stringResource(R.string.select_a_parent_for_find_changes)+":")
                    }

                    SingleSelectList(
                        optionsList = createPatchParentList.value,
                        selectedOptionIndex = null,
                        selectedOptionValue = createPatchParentHash.value,
                        menuItemSelected = {_, value-> value==createPatchParentHash.value},
                        menuItemOnClick = {idx, value ->
                            createPatchParentHash.value = value
                        },
                        menuItemFormatter = {_, value ->
                            Libgit2Helper.getShortOidStrByFull(value?:"")
                        }
                    )


                }
            },
            onCancel = { showCreatePatchDialog.value = false }
        ) {
            showCreatePatchDialog.value = false

            doJobThenOffLoading(
                loadingOn,
                loadingOff,
                activityContext.getString(R.string.creating_patch)
            ) {
                try {
                    val left = createPatchParentHash.value
                    val right = createPatchTargetHash.value

                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val tree1 = Libgit2Helper.resolveTree(repo, left) ?: throw RuntimeException("resolve left tree failed, 10137466")
                        val tree2 = Libgit2Helper.resolveTree(repo, right) ?: throw RuntimeException("resolve right tree failed, 11015534")

                        // 注意应该是：parent..target，parent在左
                        val outFile = FsUtils.Patch.newPatchFile(curRepo.value.repoName, left, right)

                        val ret = Libgit2Helper.savePatchToFileAndGetContent(
                            outFile=outFile,
                            repo = repo,
                            tree1 = tree1,
                            tree2 = tree2,
                            fromTo = Cons.gitDiffFromTreeToTree,
                            reverse = false,
                            treeToWorkTree = false,
                            returnDiffContent = false  //是否返回输出的内容，若返回，可在ret中取出字符串
                        )

                        if(ret.hasError()) {
                            Msg.requireShowLongDuration(ret.msg)
                            if(ret.code != Ret.ErrCode.alreadyUpToDate) {  //如果错误码不是 Already up-to-date ，就log下

                                //选提交时记日志把files改成commit用来区分
                                createAndInsertError(repoId, "create patch of '$shortParent..$shortTarget' err: "+ret.msg)
                            }
                        }else {
                            //输出格式： /puppygitDataDir/patch/xxxx..xxxx，可前往Files页面通过Go To功能跳转到对应目录并选中文件
//                            savePatchPath.value = getFilePathStrBasedRepoDir(outFile.canonicalPath, returnResultStartsWithSeparator = true)
                            savePatchPath.value = outFile.canonicalPath
                            showSavePatchSuccessDialog.value = true
                        }
                    }
                }catch (e:Exception) {
                    val errPrefix = "create patch err: "
                    Msg.requireShowLongDuration(e.localizedMessage ?: errPrefix)
                    createAndInsertError(curRepo.value.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, "$errPrefix${e.stackTraceToString()}")
                }

            }
        }
    }





    val showCherrypickDialog = rememberSaveable { mutableStateOf(false)}
    val cherrypickTargetHash = rememberSaveable { mutableStateOf("")}
    val cherrypickParentHash = rememberSaveable { mutableStateOf("")}
    val cherrypickParentList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "cherrypickParentList", listOf<String>())
    val cherrypickAutoCommit = rememberSaveable { mutableStateOf(false)}

    val initCherrypickDialog = { targetFullHash:String, defaultParentFullHash:String, parentList:List<String> ->
        cherrypickParentList.value.clear()
        cherrypickParentList.value.addAll(parentList)

        cherrypickTargetHash.value = targetFullHash
        cherrypickParentHash.value = defaultParentFullHash

        cherrypickAutoCommit.value = false

        showCherrypickDialog.value = true
    }

    if(showCherrypickDialog.value) {
        val shortTarget = Libgit2Helper.getShortOidStrByFull(cherrypickTargetHash.value)
        val shortParent = Libgit2Helper.getShortOidStrByFull(cherrypickParentHash.value)

        val padding=10.dp

        ConfirmDialog(
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            title = stringResource(R.string.cherrypick),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Text(
                        text =  buildAnnotatedString {
                            append(stringResource(R.string.target)+": ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                append(shortTarget)
                            }
                        },
                        modifier = Modifier.padding(horizontal = padding)
                    )

                    Row(modifier = Modifier.padding(padding)) {
                        Text(text = stringResource(R.string.select_a_parent_for_find_changes)+":")
                    }

                    DisableSelection {
                        SingleSelectList(
                            optionsList = cherrypickParentList.value,
                            selectedOptionIndex = null,
                            selectedOptionValue = cherrypickParentHash.value,
                            menuItemSelected = {_, value-> value==cherrypickParentHash.value},
                            menuItemOnClick = {idx, value ->
                                cherrypickParentHash.value = value
                            },
                            menuItemFormatter = {_, value ->
                                Libgit2Helper.getShortOidStrByFull(value?:"")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(padding))

                    MyCheckBox(text = stringResource(R.string.auto_commit), value = cherrypickAutoCommit)

                }
            },
            onCancel = { showCherrypickDialog.value = false }
        ) {
            showCherrypickDialog.value = false

            doJobThenOffLoading(
                loadingOn,
                loadingOff,
                activityContext.getString(R.string.cherrypicking)
            ) {
                Repository.open(curRepo.value.fullSavePath).use { repo->
                    val ret = Libgit2Helper.cherrypick(
                        repo,
                        targetCommitFullHash = cherrypickTargetHash.value,
                        parentCommitFullHash = cherrypickParentHash.value,
                        autoCommit = cherrypickAutoCommit.value,
                        settings = settings
                    )

                    if(ret.hasError()) {
                        Msg.requireShowLongDuration(ret.msg)
                        if(ret.code != Ret.ErrCode.alreadyUpToDate) {  //如果错误码不是 Already up-to-date ，就log下

                            //选提交时记日志把files改成commit用来区分
                            createAndInsertError(repoId, "cherrypick commit changes of '$shortParent..$shortTarget' err: "+ret.msg)
                        }
                    }else {
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }
                }
            }
        }
    }



    val showSetPageSizeDialog = rememberSaveable { mutableStateOf(false) }
    val pageSizeForDialog =mutableCustomStateOf(stateKeyTag, "pageSizeForDialog") { TextFieldValue("") }

    val initSetPageSizeDialog = {
        pageSizeForDialog.value = pageSize.value.toString().let { TextFieldValue(it, selection = TextRange(0, it.length)) }
        showSetPageSizeDialog.value = true
    }

    if(showSetPageSizeDialog.value) {
        SetPageSizeDialog(
            pageSizeBuf = pageSizeForDialog,
            pageSize = pageSize,
            rememberPageSize = rememberPageSize,
            trueCommitHistoryFalseFileHistory = true,
            closeDialog = {showSetPageSizeDialog.value=false}
        )
    }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }

    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}



    BackHandler {
        if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
            filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
                        FilterTextField(
                            filterKeyWord = filterKeyword,
                            loading = searching.value,
                            trailingIcon = {
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.filter_by_paths),
                                    icon = Icons.AutoMirrored.Filled.List,
                                    iconContentDesc = stringResource(R.string.a_list_icon_lor_filter_commits_by_paths),
                                    iconColor = UIHelper.getIconEnableColorOrNull(pathsForFilter.value.isNotEmpty()),
                                ) {
                                    // show filter by path dialog
                                    pathsForFilterBuffer.value = pathsForFilter.value  // assign current working filter paths to paths cache for accept user input
                                    filterByEntryNameBuffer.value = filterByEntryName.value

                                    showFilterByPathsDialog.value = true
                                }
                            },
                        )
                    }else{
                        val repoAndBranchText = if(isHEAD.value) repoOnBranchOrDetachedHash.value else branchShortNameOrShortHashByFullOidForShowOnTitle.value
                        Column(
                            modifier = Modifier.combinedClickable(
                                onDoubleClick = {
                                    // due to fiter mode on, cant double click title, so actually here only use normal list and its state is ok
                                    defaultTitleDoubleClick(scope, getActuallyListState(), getActuallyLastPosition())
                                },
                                onLongClick = {
                                    //长按显示仓库和分支信息
//                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//                                    Msg.requireShow(repoAndBranchText)
                                    val count = if(enableFilterState.value) filterIdxList.value.size else list.value.size
                                    // show loaded how many items
                                    Msg.requireShow(replaceStringResList(activityContext.getString(R.string.item_count_n), listOf(""+count)))
                                }
                            ) { // onClick
                                showTitleInfoDialog.value = true
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    text = stringResource(R.string.commit_history),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                            }
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    text = repoAndBranchText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize
                                )
                            }

                        }
                    }
                },
                navigationIcon = {
                    if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),

                        ) {
                            // empty token to cancel searching
                            resetSearchVars()
                            filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
                        }
                    } else {
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
                    if(!filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
                        Row {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.filter),
                                icon = Icons.Filled.FilterAlt,
                                iconContentDesc = stringResource(R.string.filter),
                            ) {
                                // filter item
                                filterKeyword.value = TextFieldValue("")
                                pathsForFilter.value = ""

                                filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = true
                            }

                            //刷新按钮
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.refresh),
                                icon = Icons.Filled.Refresh,
                                iconContentDesc = stringResource(id = R.string.refresh),
                                enabled = true,

                            ) {
                                fullyRefresh()
                            }

                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.checkout),
                                icon = Icons.Filled.MoveToInbox,
                                iconContentDesc = stringResource(R.string.checkout),
                                enabled = true,

                            ) {
                                val requireUserInputHash = true
                                initCheckoutDialogComposableVersion(
                                    requireUserInputHash
                                )
                            }


                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.reverse),
                                icon = Icons.Filled.Compare,
                                iconContentDesc = stringResource(R.string.reverse),
                                enabled = true,
                                iconColor = UIHelper.getIconEnableColorOrNull(commitHistoryRTL.value),
                            ) {
                                val newValue = !commitHistoryRTL.value
                                commitHistoryRTL.value = newValue
                                SettingsUtil.update { it.commitHistoryRTL = newValue }
                            }

                            if((proFeatureEnabled(commitsDiffCommitsTestPassed) || proFeatureEnabled(resetByHashTestPassed))) {
    //                            显示more三点菜单
                                LongPressAbleIconBtn(
                                    tooltipText = stringResource(R.string.menu),
                                    icon = Icons.Filled.MoreVert,
                                    iconContentDesc = stringResource(id = R.string.menu),
                                    enabled = true,
                                ) {
                                    showTopBarMenu.value = true
                                }

                            }
                        }

                        if(showTopBarMenu.value) {
                            Row (modifier = Modifier.padding(top = MyStyleKt.TopBar.dropDownMenuTopPaddingSize)) {
                                DropdownMenu(
                                    expanded = showTopBarMenu.value,
                                    onDismissRequest = { showTopBarMenu.value=false }
                                ) {
    //                                    选项“diff commits”，点击弹窗，让用户输入两个提交号，跳转到tree to tree页面比较这两个提交
                                    if(proFeatureEnabled(commitsDiffCommitsTestPassed)) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.diff_commits)) },
                                            onClick = {
                                                initDiffCommitsDialog(null, null, true)

                                                //关闭顶栏菜单
                                                showTopBarMenu.value = false
                                            }
                                        )
                                    }

                                    if(proFeatureEnabled(resetByHashTestPassed)){
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.reset)) },
                                            onClick = {
                                                showResetDialog.value = true

                                                //关闭顶栏菜单
                                                showTopBarMenu.value = false
                                            }
                                        )
                                    }

                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.graph)) },
                                        trailingIcon = {
                                            SimpleCheckBox(commitHistoryGraph.value)
                                        },
                                        onClick = {
                                            val newValue = !commitHistoryGraph.value
                                            commitHistoryGraph.value = newValue
                                            SettingsUtil.update { it.commitHistoryGraph = newValue }

                                            //关闭顶栏菜单
//                                            showTopBarMenu.value = false
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.page_size)) },
                                        onClick = {
                                            initSetPageSizeDialog()

                                            //关闭顶栏菜单
                                            showTopBarMenu.value = false
                                        }
                                    )
                                }



                            }

                        }
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(pageScrolled.value) {

                GoToTopAndGoToBottomFab(
                    filterModeOn = enableFilterState.value,
                    scope = scope,
                    filterListState = filterListState,
                    listState = listState,
                    filterListLastPosition = filterLastPosition,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )

            }
        }
    ) { contentPadding ->

        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { fullyRefresh() }
        ) {

            if (showLoadingDialog.value) {
                LoadingDialog(loadingText.value)
            }

            if(list.value.isEmpty()) {
                Column(
                    modifier = Modifier
                        .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
                        .padding(MyStyleKt.defaultItemPadding)
                    ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(if(loadMoreLoading.value) R.string.loading else R.string.commit_history_is_empty))
                }
            }else {

//        val commitLen = 10;
                if (showBottomSheet.value) {
//            var commitOid = curCommit.value.oidStr
//            if(commitOid.length > Cons.gitShortCommitHashRangeEndInclusive) {  //避免commitOid不够长导致抛异常，正常来说commitOid是40位，不会有问题，除非哪里出了问题
//                commitOid = commitOid.substring(Cons.gitShortCommitHashRange)+"..."
//            }
                    BottomSheet(showBottomSheet, sheetState, curCommit.value.shortOidStr) {

                        //如果是filter模式，显示show in list以在列表揭示filter条目以查看前后提交（或者说上下文）
                        if(enableFilterState.value) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.show_in_list)) {
                                filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
                                showBottomSheet.value = false

                                doJobThenOffLoading {
//                            delay(100)  // wait rendering, may unnecessary yet
                                    val curItemIndex = curCommitIndex.intValue  // 被长按的条目在 filterlist中的索引
                                    val idxList = filterIdxList.value  //取出存储filter索引和源列表索引的 index list，条目索引对应filter list条目索引，条目值对应的是源列表的真实索引

                                    doActIfIndexGood(curItemIndex, idxList) {  // it为当前被长按的条目在源列表中的真实索引
                                        UIHelper.scrollToItem(scope, listState, it)  //在源列表中定位条目
                                        requireBlinkIdx.intValue = it  //设置条目闪烁以便用户发现
                                    }
                                }
                            }

                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout)) {
                            // onClick()
                            // 弹出确认框，询问是否确定执行checkout，可detach head，可创建分支，类似checkout remote branch
                            //初始化弹窗默认选项
                            val requireUserInputHash = false
                            initCheckoutDialogComposableVersion(requireUserInputHash)
                        }
                        if(dev_EnableUnTestedFeature || tagsTestPassed) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.new_tag)) {
                                // onClick()
                                // 弹出确认框，询问是否确定执行checkout，可detach head，可创建分支，类似checkout remote branch
                                //初始化弹窗默认选项
                                initNewTagDialog(curCommit.value.oidStr)
                            }
                        }
                        if(proFeatureEnabled(resetByHashTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset)) {
                                // onClick()
                                //初始化弹窗默认选项
//                    acceptHardReset.value = false
                                resetOid.value = curCommit.value.oidStr
                                showResetDialog.value = true
                            }
                        }

                        if(isHEAD.value) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.squash)) {
                                val targetCommitFullOid = curCommit.value.oidStr
                                val targetCommitShortOid = curCommit.value.shortOidStr

                                doJobThenOffLoading {
                                    Repository.open(curRepo.value.fullSavePath).use { repo ->
                                        val ret = Libgit2Helper.squashCommitsCheckBeforeShowDialog(
                                            repo = repo,
                                            targetFullOidStr = targetCommitFullOid,
                                            isShowingCommitListForHEAD = isHEAD.value
                                        )

                                        if(ret.hasError()) {
                                            Msg.requireShowLongDuration(ret.msg)
                                            createAndInsertError(curRepo.value.id, "squash commits pre-check err: "+ret.msg)
                                        }else {
                                            val squashData = ret.data!!
                                            initShowSquashDialog(targetCommitFullOid, targetCommitShortOid, squashData.headFullOid, squashData.headFullName, squashData.username, squashData.email)
                                        }
                                    }

                                }
                            }
                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff)) {
                            // 把当前提交号填入第1个输入框，然后，聚焦第2个输入框
                            initDiffCommitsDialog(curCommit.value.oidStr, null, false)
                        }

                        if(UserUtil.isPro() && (dev_EnableUnTestedFeature || commitsDiffToLocalTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_local)) {
                                //                    diff to local，点击跳转到tree to tree页面，然后diff
                                goToTreeToTreeChangeList(
                                    title = activityContext.getString(R.string.compare_to_local),
                                    repoId = curRepo.value.id,
                                    commit1 = curCommit.value.oidStr,
                                    commit2 = Cons.git_LocalWorktreeCommitHash,
                                    commitForQueryParents = Cons.git_AllZeroOidStr,
                                )
                            }
                        }

                        if(proFeatureEnabled(diffToHeadTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_head)) {
                                //直接跳转到t2t页面，再解析HEAD
                                goToTreeToTreeChangeList(
                                    title = activityContext.getString(R.string.compare_to_head),
                                    repoId = curRepo.value.id,
                                    commit1 = curCommit.value.oidStr,
                                    commit2 = Cons.git_HeadCommitHash,
                                    commitForQueryParents = Cons.git_AllZeroOidStr,
                                )

                                // 解析HEAD为提交hash，再跳转到t2t页面
//                                doJobThenOffLoading job@{
//                                    Repository.open(curRepo.value.fullSavePath).use { repo->
//                                        //这里需要传当前commit，然后cl页面会用当前commit查出当前commit的parents
//                                        val commit2Ret = Libgit2Helper.getHeadCommit(repo)
//                                        if(commit2Ret.hasError()) {
//                                            Msg.requireShowLongDuration(commit2Ret.msg)
//                                            return@job
//                                        }
//
//                                        val commit2 = commit2Ret.data!!.id().toString()
//                                        val commit1 = curCommit.value.oidStr
//                                        if(commit2 == commit1) {  //避免 Compare HEAD to HEAD
//                                            Msg.requireShowLongDuration(activityContext.getString(R.string.num2_commits_same))
//                                            return@job
//                                        }
//
//                                        //当前比较的描述信息的key，用来在界面显示这是在比较啥，值例如“和父提交比较”或者“比较两个提交”之类的
//
//
//                                        goToTreeToTreeChangeList(
//                                            title = activityContext.getString(R.string.compare_to_head),
//                                            repoId = curRepo.value.id,
//                                            commit1 = commit1,
//                                            commit2 = commit2,
//                                            commitForQueryParents = Cons.git_AllZeroOidStr,
//                                        )
//
//                                    }
//
//                                }
                            }
                        }

                        if(proFeatureEnabled(cherrypickTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.cherrypick)) {
                                //弹窗，让选parent，默认选中第一个
                                if(curCommit.value.parentOidStrList.isEmpty()) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_parent_for_find_changes_for_cherrypick))
                                }else {
                                    //默认选中第一个parent
                                    initCherrypickDialog(curCommit.value.oidStr, curCommit.value.parentOidStrList[0], curCommit.value.parentOidStrList)
                                }
                            }
                        }

                        if(proFeatureEnabled(createPatchTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.create_patch)) {
                                //弹窗，让选parent，默认选中第一个
                                if(curCommit.value.parentOidStrList.isEmpty()) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_parent_for_find_changes_for_create_patch))
                                }else {
                                    //默认选中第一个parent
                                    initCreatePatchDialog(curCommit.value.oidStr, curCommit.value.parentOidStrList[0], curCommit.value.parentOidStrList)
                                }
                            }
                        }


//                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.msg)){
//                            showItemMsg(curCommit.value)
//                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.details)){
                            showItemDetails(curCommit.value)
                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.refresh)) {
                            val curCommit = curCommit.value

                            try {
                                refreshCommitByPredicate(curRepo.value) {
                                    it.oidStr == curCommit.oidStr
                                }

                                Msg.requireShow(activityContext.getString(R.string.success))
                            }catch (e: Exception) {
                                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                                MyLog.e(TAG, "refresh commit err: commitOid=${curCommit.oidStr}, err=${e.stackTraceToString()}")
                            }

                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.nodes)){
                            showNodesInfo(curCommit.value)
                        }


//                BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.create_branch)){
//                // 这个创建分支的特点是“仅创建分支，但不checkout”，但checkout本来就能创建分支并且可选是否检出，所以不需要重复实现了
//                }
//                BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.view_hash)){
//                    // onClick()
//                    requireShowViewDialog(appContext.getString(R.string.view_hash), curCommit.value.oidStr)
//
//                }
//                BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.view_messages)){
//                    // 弹窗显示分支列表
//                    requireShowViewDialog(appContext.getString(R.string.view_messages), curCommit.value.msg)
//                }
//                BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.view_branches)){
//                    // 弹窗显示分支列表
//                    requireShowViewDialog(appContext.getString(R.string.view_branches), curCommit.value.branchShortNameList.toString())
//
//                }
//                BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.view_parents)){
//                    // 弹窗显示父提交长id列表
//                    requireShowViewDialog(appContext.getString(R.string.view_parents), curCommit.value.parentOidStrList.toString())
//
//                }

                    }
                }

                //根据关键字过滤条目
                val keyword = filterKeyword.value.text  //关键字
                val pathList = pathsListForFilter.value
                val needFilterByPath = pathList.isNotEmpty()
                val enableFilter = filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value && (maybeIsGoodKeyword(keyword) || needFilterByPath)

                val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                val list = filterTheList(
                    needRefresh = filterResultNeedRefresh.value,
                    lastNeedRefresh = lastNeedRefresh,

                    orCustomDoFilterCondition = if(needFilterByPath.not()) {
                        { false }
                    }else {
                        {
                            //拷贝，避免通过size比较后条目发生变化导致索引越界
                            val lastCopy = lastPathsListForFilter.value.toList()
                            val curCopy = pathList.toList()

                            //检测path列表是否发生了变化，若是返回真；否则返回假。返回真将会触发重新执行过滤
                            lastCopy.size != curCopy.size || run {
                                var changed = false
                                for (idx in lastCopy.indices) {
                                    if (lastCopy[idx] != curCopy[idx]) {
                                        changed = true
                                        break
                                    }
                                }

                                changed
                            }
                        }
                    },
                    beforeSearchCallback = {
                        lastPathsListForFilter.value.clear()
                        lastPathsListForFilter.value.addAll(pathList)
                    },
                    enableFilter = enableFilter,
                    keyword = keyword,
                    lastKeyword = lastKeyword,
                    searching = searching,
                    token = token,
                    activityContext = activityContext,
                    filterList = filterList.value,
                    list = list.value,
                    resetSearchVars = resetSearchVars,
                    match = {idx,item -> true},
                    lastListSize = lastListSize,
                    filterIdxList = filterIdxList.value,
                    customTask = {
                        //如果filter by path启用，则打开一个仓库对象用来查找路径
                        val repo = if(needFilterByPath) {
                            try{
                                Repository.open(curRepo.value.fullSavePath)
                            }catch (_:Exception) {
                                null
                            }
                        }else {
                            null
                        }

                        val canceled = initSearch(keyword = keyword, lastKeyword = lastKeyword, token = token)

                        val match = { idx:Int, it: CommitDto ->
                            var found = it.oidStr.contains(keyword, ignoreCase = true)
                                    || it.getFormattedCommitterInfo().contains(keyword, ignoreCase = true)
                                    || it.getFormattedAuthorInfo().contains(keyword, ignoreCase = true)
                                    || it.email.contains(keyword, ignoreCase = true)
                                    || it.author.contains(keyword, ignoreCase = true)
                                    || it.committerEmail.contains(keyword, ignoreCase = true)
                                    || it.committerUsername.contains(keyword, ignoreCase = true)
                                    || it.dateTime.contains(keyword, ignoreCase = true)
                                    || it.branchShortNameList.toString().contains(keyword, ignoreCase = true)
                                    || it.tagShortNameList.toString().contains(keyword, ignoreCase = true)
                                    || it.parentOidStrList.toString().contains(keyword, ignoreCase = true)
                                    || it.treeOidStr.contains(keyword, ignoreCase = true)
                                    || it.msg.contains(keyword, ignoreCase = true)
                                    || it.getOther(activityContext, false).contains(keyword, ignoreCase = true)
                                    || it.getOther(activityContext, true).contains(keyword, ignoreCase = true)
                                    || formatMinutesToUtc(it.originTimeOffsetInMinutes).contains(keyword, ignoreCase = true)


                            if(found) {
                                // filter by path
                                if(needFilterByPath && repo!=null) {
                                    val tree = Libgit2Helper.resolveTreeByTreeId(repo, Oid.of(it.treeOidStr))
                                    if(tree != null) {
                                        found = Libgit2Helper.isTreeIncludedPaths(tree, pathList, filterByEntryName.value)
                                    }
                                }
                            }

                            found
                        }

                        searching.value = true

                        filterList.value.clear()

                        // repo.use == try...finally{repo.close()}
                        // match里面使用repo了，所以在搜索结束前不能释放repo
                        // 这里用repo.use代表代码块里直接或间接使用了repo对象，并希望在代码块结束后释放repo，和try代码块finally释放repo效果一样
                        repo.use { repo ->
                            search(
                                src = list.value,
                                match = match,
                                matchedCallback = { idx, item ->
                                    filterList.value.add(item)

                                    // add src idx for show in list, use `srcList[filterIdxList[idxOfFilterList]]` to get item of filter list related item in src list
                                    // 为“在列表显示”功能添加这个索引，使用 `srcList[filterIdxList[idxOfFilterList]]` 可获得过滤后列表的元素在源列表关联的元素
                                    filterIdxList.value.add(idx)
                                },
                                canceled = canceled
                            )
                        }
                    },
                )


                val listState = if(enableFilter) filterListState else listState
//        if(enableFilter) {  //更新filter列表state
//            filterListState.value = listState
//        }

                //更新是否启用filter
                enableFilterState.value = enableFilter


                CompositionLocalProvider(
                    LocalLayoutDirection.provides(if(commitHistoryRTL.value) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {

                    MyLazyColumn(
                        contentPadding = contentPadding,
                        list = list,
                        listState = listState,
                        requireForEachWithIndex = true,
                        requirePaddingAtBottom = false,
                        requireCustomBottom = true,
                        customBottom = {
                            LoadMore(
                                initSetPageSizeDialog = initSetPageSizeDialog,
                                text = loadMoreText.value,
                                enableLoadMore = !loadMoreLoading.value && hasMore.value, enableAndShowLoadToEnd = !loadMoreLoading.value && hasMore.value,
                                btnUpsideText = getLoadText(list.size, enableFilter, activityContext),
                                loadToEndOnClick = {
                                    val firstLoad = false
                                    val forceReload = false
                                    val loadToEnd = true
                                    doLoadMore(
                                        curRepo.value.fullSavePath,
                                        nextCommitOid.value,
                                        firstLoad,
                                        forceReload,
                                        loadToEnd
                                    )
                                }
                            ) {
                                val firstLoad = false
                                val forceReload = false
                                val loadToEnd = false
                                doLoadMore(
                                    curRepo.value.fullSavePath,
                                    nextCommitOid.value,
                                    firstLoad,
                                    forceReload,
                                    loadToEnd
                                )

                            }
                        }
                    ) { idx, it ->
                        CommitItem(
                            drawLocalAheadUpstreamCount = drawLocalAheadUpstreamCount.value,
                            commitHistoryGraph = commitHistoryGraph.value,
                            density = density,
                            nodeCircleRadiusInPx = nodeCircleRadiusInPx,
                            nodeCircleStartOffsetX = nodeCircleStartOffsetX,
                            nodeLineWidthInPx = nodeLineWidthInPx,
                            lineDistanceInPx = lineDistanceInPx,
                            showBottomSheet = showBottomSheet,
                            curCommit = curCommit,
                            curCommitIdx = curCommitIndex,
                            idx = idx,
                            commitDto = it,
                            requireBlinkIdx = requireBlinkIdx,
                            lastClickedItemKey = lastClickedItemKey,
                            shouldShowTimeZoneInfo = shouldShowTimeZoneInfo,
                            showItemMsg = showItemMsg
                        ) { thisObj ->
                            val parents = thisObj.parentOidStrList
                            if (parents.isEmpty()) {  // 如果没父提交，例如最初的提交就没父提交，提示没parent可比较
                                //TODO 改成没父提交时列出当前提交的所有文件？
                                Msg.requireShowLongDuration(activityContext.getString(R.string.no_parent_to_compare))
                            } else {  //有父提交，取出第一个父提交和当前提交进行比较
                                val commit2 = thisObj.oidStr
                                goToTreeToTreeChangeList(
                                    title = activityContext.getString(R.string.compare_to_parent),
                                    repoId = curRepo.value.id,
                                    commit1 = parents[0],
                                    commit2 = commit2,
                                    commitForQueryParents = commit2,
                                )

                            }
                        }

                        MyHorizontalDivider()
                    }

                    // filter mode 有可能查无条目，但是可继续加载更多，这时也应显示加载更多按钮
                    if(enableFilter && list.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(Modifier.height(50.dp))
                            Text(stringResource(if(searching.value) R.string.loading else R.string.no_matched_item), fontWeight = FontWeight.Light)

                            LoadMore(
                                modifier = Modifier.padding(top = 30.dp),
                                initSetPageSizeDialog = initSetPageSizeDialog,
                                text = loadMoreText.value,
                                btnUpsideText = getLoadText(list.size, enableFilter, activityContext),
                                enableLoadMore = !loadMoreLoading.value && hasMore.value, enableAndShowLoadToEnd = !loadMoreLoading.value && hasMore.value,
                                loadToEndOnClick = {
                                    val firstLoad = false
                                    val forceReload = false
                                    val loadToEnd = true
                                    doLoadMore(
                                        curRepo.value.fullSavePath,
                                        nextCommitOid.value,
                                        firstLoad,
                                        forceReload,
                                        loadToEnd
                                    )
                                }
                            ) {
                                val firstLoad = false
                                val forceReload = false
                                val loadToEnd = false
                                doLoadMore(
                                    curRepo.value.fullSavePath,
                                    nextCommitOid.value,
                                    firstLoad,
                                    forceReload,
                                    loadToEnd
                                )

                            }
                        }
                    }
                }
            }

        }

    }

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {

        doJobThenOffLoading job@{
            //这里只用来获取是否需要forceReload的值，且这个值只需获取一次，所以getThenDel设置为true（其实多次获取也没事，只是会导致无意义查询）
            val (requestType, data) = getRequestDataByState<Any?>(needRefresh.value)
            val forceReload = (requestType == StateRequestType.forceReload)

            if(forceReload || curRepo.value.id.isBlank() || headOidOfThisScreen.value.isNullOrEmptyOrZero || onlyUpdateRepoInfoOnce.value) {
                //从db查数据
                val repoDb = AppModel.dbContainer.repoRepository
                val repoFromDb = repoDb.getById(repoId)
                if (repoFromDb == null) {
                    MyLog.w(TAG, "#LaunchedEffect: query repo info from db error! repoId=$repoId}")
                    return@job
                }
                curRepo.value = repoFromDb
                val repoFullPath = repoFromDb.fullSavePath
                val repoName = repoFromDb.repoName
                repoOnBranchOrDetachedHash.value = repoName
                branchShortNameOrShortHashByFullOidForShowOnTitle.value = repoName
//            val isDetached = dbIntToBool(repoFromDb.isDetached)

                Repository.open(repoFullPath).use { repo ->
                    headOidOfThisScreen.value = if(isHEAD.value) {  // resolve head
                        val head = Libgit2Helper.resolveHEAD(repo)
                        if (head == null) {
                            MyLog.w(TAG, "#LaunchedEffect: head is null! repoId=$repoId}")
                            return@job
                        }
                        val headOid = head.peel(GitObject.Type.COMMIT)?.id()
                        if (headOid == null || headOid.isNullOrEmptyOrZero) {
                            MyLog.w(TAG, "#LaunchedEffect: head oid is null or invalid! repoId=${repoId}, headOid=${headOid.toString()}")
                            return@job
                        }

                        repoOnBranchOrDetachedHash.value = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(repoFromDb)

                        fullOid.value = headOid.toString()

                        headOid
                    }else {  // resolve branch to commit
//                    val ref = Libgit2Helper.resolveRefByName(repo, fullOid.value, trueUseDwimFalseUseLookup = true)  // useDwim for get direct ref, which is point to a valid commit
                        val commit = Libgit2Helper.resolveCommitByHash(repo, fullOid.value)
                        val commitOid = commit?.id() ?: throw RuntimeException("CommitListScreen#LaunchedEffect: resolve commit err!, fullOid='${fullOid.value}'")
                        //注：虽然这个变量名是分支短名和短hash名blabala，但实际上，如果通过分支条目进入，只会有短分支名，不会有短提交号，短提交号是之前考虑欠佳即使分支条目点进来的提交历史也一checkout就刷新页面更新标题而残留下的东西
                        branchShortNameOrShortHashByFullOidForShowOnTitle.value = Libgit2Helper.getBranchNameOfRepoName(repoName, branchShortNameOrShortHashByFullOid.value)

                        commitOid
                    }

                }

                //第一次查询，指向headOid，NO！不要这么做，不然compose销毁又重建，恢复数据时，指向原本列表之后的commit就又重新指向head了，就乱了
                //不要在这给nexCommitOid和条目列表赋值！要在doLoadMore里给它们赋值！
//                nextCommitOid.value = headOid

            }

            if(onlyUpdateRepoInfoOnce.value) {
                onlyUpdateRepoInfoOnce.value = false
            }else {
                // do first load
                val firstLoad = true
                val loadToEnd = false

                //传repoFullPath是用来打开git仓库的
                doLoadMore(curRepo.value.fullSavePath, headOidOfThisScreen.value, firstLoad, forceReload, loadToEnd)
            }
        }
    }


    // multi DisposableEffect is ok, each DisposableEffect manage self's code block

    //compose被销毁时执行的副作用(SideEffect)
    DisposableEffect(Unit) {  // param changed or DisposableEffect destroying will run onDispose
        onDispose {
            doJobThenOffLoading {
                loadChannel.close()
            }
        }
    }


    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            showFilterByPathsDialog.value.not()
        }
    )


}

