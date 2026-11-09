package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.CommitListDialog
import com.catpuppyapp.puppygit.compose.CommitMsgMarkDownDialog
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CreatePatchSuccessDialog
import com.catpuppyapp.puppygit.compose.FileHistoryRestoreDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LoadMore
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.RepoInfoDialogItemSpacer
import com.catpuppyapp.puppygit.compose.SetPageSizeDialog
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.commitsDiffCommitsTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dev.resetByHashTestPassed
import com.catpuppyapp.puppygit.git.FileHistoryDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.FileHistoryItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.getLoadText
import com.catpuppyapp.puppygit.screen.functions.goToDiffScreen
import com.catpuppyapp.puppygit.screen.functions.initSearch
import com.catpuppyapp.puppygit.screen.functions.search
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.DiffFromScreen
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.cache.NaviCache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doActIfIndexGood
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.getFileNameFromCanonicalPath
import com.catpuppyapp.puppygit.utils.getRequestDataByState
import com.catpuppyapp.puppygit.utils.isGoodIndexForList
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.time.TimeZoneUtil
import com.github.git24j.core.GitObject
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository
import com.github.git24j.core.Revwalk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "FileHistoryScreen"


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FileHistoryScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic:HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
    repoId: String,
    fileRelativePathKey:String,
    naviUp: () -> Unit,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)


    //已处理这种情况，传参时传有效key，但把value设为空字符串，就解决了
//    println("fullOidKey.isEmpty()="+fullOidKey.isEmpty())  //expect true when nav from repoCard, result: is not empty yet
//    println("fullOidKey="+fullOidKey)  //expect true when nav from repoCard

    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    val loadChannel = remember { Channel<Int>() }

    val fileRelativePath = rememberSaveable { NaviCache.getByType<String>(fileRelativePathKey) ?: "" }
    val lastVersionEntryOid = rememberSaveable { mutableStateOf<String?>(null) }


//    val sumPage = MockData.getCommitSum(repoId,branch)
    //获取假数据
//    val list = remember { mutableStateListOf<CommitDto>() };
//    val list = StateUtil.getCustomSaveableState(keyTag = stateKeyTag, keyDesc = "list", initValue = mutableStateListOf<CommitDto>())
    val list = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "list",
        initValue = listOf<FileHistoryDto>()
    )
//    val list = rememberSaveable(
//        stateSaver = getSaver()
//    ) {
//        mutableStateOf(getHolder(stateKeyTag, "list",  mutableListOf<CommitDto>()))
//    }
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }

    //page size for load more
    val pageSize = rememberSaveable{ mutableStateOf(settings.fileHistoryPageSize) }
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

    //这个页面的滚动状态不用记住，每次点开重置也无所谓
    val listState = rememberLazyListState()
    //如果再多几个"mode"，就改用字符串判断，直接把mode含义写成常量
    val showTopBarMenu = rememberSaveable { mutableStateOf(false)}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
//    val curCommit = rememberSaveable{ mutableStateOf(CommitDto()) }
    val curObj = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curObj",
        initValue = FileHistoryDto()
    )
    val curRepo = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "curRepo",
        initValue = RepoEntity(id = "")
    )

    val curObjShortOid = remember(curObj.value.commitOidStr) { derivedStateOf{
        Libgit2Helper.getShortOidStrByFull(curObj.value.commitOidStr)
    }}





    val loadMoreLoading = rememberSaveable { mutableStateOf(false)}
    val loadMoreText = rememberSaveable { mutableStateOf("")}
    val hasMore = rememberSaveable { mutableStateOf(false)}


    val needRefresh = rememberSaveable { mutableStateOf("FileHistory_refresh_init_value_c2k8")}


    //filter相关，开始

    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    // should use `enableFilterState` check filter mode really work or not, cause even this value true,
    // but maybe no filter text inputted, then actually filter mode still not really working
    val filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot = rememberSaveable { mutableStateOf(false) }

    //存储符合过滤条件的条目在源列表中的真实索引。本列表索引对应filter list条目索引，值对应原始列表索引
    val filterIdxList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "filterIdxList",
        listOf<Int>()
    )
    val filterList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "filterList",
        listOf<FileHistoryDto>()
    )

    //filter相关，结束

    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false)}

    val getActuallyList = {
        if(enableFilterState.value) {
            filterList.value
        }else{
            list.value
        }
    }


    val getActuallyListState = {
        if(enableFilterState.value) filterListState else listState
    }

    // 两个用途：1点击刷新按钮后回到列表顶部 2放到刷新按钮旁边，用户滚动到底部后，想回到顶部，可点击这个按钮
    val goToTop = {
        UIHelper.scrollToItem(scope, getActuallyListState(), 0)
    }

    val fullyRefresh = {
        goToTop()
        changeStateTriggerRefreshPage(needRefresh, StateRequestType.forceReload)
    }



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
        doJobThenOffLoading job@{
            loadLock.value.withLock {
                loadMoreLoading.value = true
                loadMoreText.value = activityContext.getString(R.string.loading)

                try {
                    if (firstLoad || forceReload || repositoryForRevWalk.value==null || revwalk.value==null) {
                        //需要重置这个值，不然查询会漏条目
                        lastVersionEntryOid.value = null

                        // do reset: clear list and release old repo instance
                        //如果是第一次加载或刷新页面（重新初始化页面），清下列表
                        // if is first load or refresh page, clear list
                        list.value.clear()

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
                        val (retLastVersionEntryOid, retNextCommitOid) = Libgit2Helper.getFileHistoryList(
                            repo = repo,
                            revwalk = revwalk.value!!,
                            initNext = nextCommitOid.value,
                            repoId = repoId,
                            pageSize = if(loadToEnd) Int.MAX_VALUE else pageSize.value,
                            retList = list.value,  //直接赋值给状态列表了，若性能差，可实现一个批量添加机制，比如查出50个条目添加一次，之类的
                            loadChannel = loadChannel,
                            checkChannelFrequency = settings.commitHistoryLoadMoreCheckAbortSignalFrequency,
                            lastVersionEntryOid = lastVersionEntryOid.value,
                            fileRelativePathUnderRepo = fileRelativePath,
                            settings = settings
                        )

                        //update state
                        lastVersionEntryOid.value = retLastVersionEntryOid
                        nextCommitOid.value = retNextCommitOid ?: Cons.git_AllZeroOid
//                        nextCommitOid.value = revwalk.value!!.next() ?: Cons.allZeroOid

                        hasMore.value = !nextCommitOid.value.isNullOrEmptyOrZero
                        loadMoreText.value = if (hasMore.value) activityContext.getString(R.string.load_more) else activityContext.getString(R.string.end_of_the_list)

                    }


                }catch (e:Exception) {
                    //经过我的测试用 !! 断言非null抛出的空指针异常的exception.message和localizedMessage都是null，
                    // 所以如果提示 "unknown err"，很可能是空指针异常NullPointerException，
                    // 不过用e.stackTraceToString()可以正常获取到错误信息，用e.printStackTrace()可在std err打印错误信息
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


    val showRestoreDialog = rememberSaveable { mutableStateOf(false) }
    if(showRestoreDialog.value) {
        FileHistoryRestoreDialog(
            targetCommitOidStr = curObj.value.commitOidStr,
            commitMsg = curObj.value.getCachedOneLineMsg(),
            showRestoreDialog = showRestoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            curRepo = curRepo,
            fileRelativePath = fileRelativePath,
            repoId = repoId
        )
    }

    val requireUserInputCommitHash = rememberSaveable { mutableStateOf(false)}
//    val forceCheckout = StateUtil.getRememberSaveableState(initValue = false)
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false)}
    //当前长按commit在列表中的索引，用来更新单个条目时使用，为-1时无效，不要执行操作
    val curObjIndex = rememberSaveable{mutableIntStateOf(-1)}
//    val initCheckoutDialog = { requireUserInputHash:Boolean ->
//        checkoutSelectedOption.intValue = checkoutOptionDefault
//        requireUserInputCommitHash.value = requireUserInputHash
//        forceCheckout.value = false
//        showCheckoutDialog.value = true
//    }

//
//    val updateCurCommitInfo = {repoFullPath:String, curCommitIdx:Int, commitOid:String, list:MutableList<CommitDto> ->
//        doActIfIndexGood(curCommitIdx, list) {
//            Repository.open(repoFullPath).use { repo ->
//                val reQueriedCommitInfo = Libgit2Helper.getSingleCommit(repo, repoId, commitOid, settings)
//                list[curCommitIdx] = reQueriedCommitInfo
//            }
//        }
//
//    }



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

    val nameOfNewTag = rememberSaveable { mutableStateOf("")}
    val overwriteIfNameExistOfNewTag = rememberSaveable { mutableStateOf(false)}  // force
    val showDialogOfNewTag = rememberSaveable { mutableStateOf(false)}
    val hashOfNewTag = rememberSaveable { mutableStateOf( "")}
    val msgOfNewTag = rememberSaveable { mutableStateOf( "")}
//    val requireUserInputHashOfNewTag = StateUtil.getRememberSaveableState(initValue = false)
    val annotateOfNewTag = rememberSaveable { mutableStateOf(false)}
    val initNewTagDialog = { hash:String ->
        hashOfNewTag.value = hash  //把hash设置为当前选中的commit的hash

        overwriteIfNameExistOfNewTag.value = false
        showDialogOfNewTag.value = true
    }


    //初始化组件版本的checkout对话框
    val initCheckoutDialogComposableVersion = { requireUserInputHash:Boolean ->
        requireUserInputCommitHash.value = requireUserInputHash
        showCheckoutDialog.value = true
    }


    val resetOid = rememberSaveable { mutableStateOf("")}
//    val acceptHardReset = StateUtil.getRememberSaveableState(initValue = false)
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {
        showResetDialog.value = false
    }

    val showDetailsDialog = rememberSaveable { mutableStateOf( false)}
    val detailsString = rememberSaveable { mutableStateOf( "")}
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = stringResource(id = R.string.details),
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }

    val showItemDetails = { curObj:FileHistoryDto ->
        val suffix = "\n\n"
        val sb = StringBuilder()

        sb.append("${activityContext.getString(R.string.path)}: "+curObj.filePathUnderRepo).append(suffix)
        sb.append("${activityContext.getString(R.string.commit_id)}: "+curObj.commitOidStr).append(suffix)
        sb.append("${activityContext.getString(R.string.entry_id)}: "+curObj.treeEntryOidStr).append(suffix)
        sb.append("${activityContext.getString(R.string.author)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curObj.authorUsername, curObj.authorEmail)).append(suffix)
        sb.append("${activityContext.getString(R.string.committer)}: "+ Libgit2Helper.getFormattedUsernameAndEmail(curObj.committerUsername, curObj.committerEmail)).append(suffix)
        //实际使用的时区偏移量
        sb.append("${activityContext.getString(R.string.date)}: "+curObj.dateTime +" (${curObj.getActuallyUsingTimeZoneUtcFormat(settings)})").append(suffix)
        // commit中携带的时区偏移量
        sb.append("${activityContext.getString(R.string.timezone)}: "+(formatMinutesToUtc(curObj.originTimeOffsetInMinutes))).append(suffix)

        //这个commits 可能会很长，可能上千个提交都包含同一个文件的同一版本，所以不在这显示，因为msg的长度同样无法确定，如果两个都很长，就完犊子了，想看哪个都费劲
//        sb.append("${activityContext.getString(R.string.commits)}: "+curObj.commitList).append(suffix)

        sb.append("${activityContext.getString(R.string.msg)}: "+curObj.msg).append(suffix)


        detailsString.value = sb.removeSuffix(suffix).toString()
        showDetailsDialog.value = true
    }

    val fileHistoryDtoOfCommitListDialog = mutableCustomStateOf(stateKeyTag, "fileHistoryDtoOfCommitListDialog") { FileHistoryDto() }
    val showCommitListDialog = rememberSaveable { mutableStateOf(false) }
    val showCommits = { curObj:FileHistoryDto ->
        fileHistoryDtoOfCommitListDialog.value = curObj
        showCommitListDialog.value = true
    }
    if(showCommitListDialog.value) {
        val item = fileHistoryDtoOfCommitListDialog.value
        CommitListDialog(
            title = stringResource(R.string.commits),
            firstLineLabel = stringResource(R.string.entry_id),
            firstLineText = item.treeEntryOidStr,
            commitListLabel = stringResource(R.string.commits),
            commits = item.commitList,
            closeDialog = {showCommitListDialog.value = false}
        )
    }

    // 向下滚动监听，开始
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }

    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}
    val lastClickedItemKey = rememberSaveable{ SharedState.fileHistory_LastClickedItemKey }



    val savePatchPath= rememberSaveable { mutableStateOf("")}
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}

    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }




    val showSetPageSizeDialog = rememberSaveable { mutableStateOf(false) }
    val pageSizeForDialog = mutableCustomStateOf(stateKeyTag, "pageSizeForDialog") { TextFieldValue("") }

    val initSetPageSizeDialog = {
        pageSizeForDialog.value = pageSize.value.toString().let { TextFieldValue(it, selection = TextRange(0, it.length)) }
        showSetPageSizeDialog.value = true
    }

    if(showSetPageSizeDialog.value) {
        SetPageSizeDialog(
            pageSizeBuf = pageSizeForDialog,
            pageSize = pageSize,
            rememberPageSize = rememberPageSize,
            trueCommitHistoryFalseFileHistory = false,
            closeDialog = {showSetPageSizeDialog.value=false}
        )
    }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    val titleInfo = rememberSaveable { mutableStateOf(fileRelativePath) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            curRepo = curRepo.value,
            showTitleInfoDialog = showTitleInfoDialog,
            prependContent = {
                Text(stringResource(R.string.file_name)+": "+ getFileNameFromCanonicalPath(fileRelativePath))
                RepoInfoDialogItemSpacer()
                Text(stringResource(R.string.file_path)+": "+fileRelativePath)
            }
        )
    }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }


    BackHandler {
        if(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value) {
            filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }


    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { curItem: FileHistoryDto ->
        textOfItemMsgDialog.value = curItem.msg
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
                        )
                    }else{
                        Column(
                            modifier = Modifier.combinedClickable(
                                onDoubleClick = {
                                    defaultTitleDoubleClick(scope, listState, lastPosition)
                                },
                                onLongClick = {
                                    //长按显示仓库和分支信息
//                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

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
                                    text = stringResource(R.string.file_history),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                            }
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Text(
                                    text = titleInfo.value,
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
                    Text(stringResource(if(loadMoreLoading.value) R.string.loading else R.string.file_hasnt_history_yet))
                }
            }else {
//        val commitLen = 10;
                if (showBottomSheet.value) {
//            var commitOid = curCommit.value.oidStr
//            if(commitOid.length > Cons.gitShortCommitHashRangeEndInclusive) {  //避免commitOid不够长导致抛异常，正常来说commitOid是40位，不会有问题，除非哪里出了问题
//                commitOid = commitOid.substring(Cons.gitShortCommitHashRange)+"..."
//            }
                    BottomSheet(showBottomSheet, sheetState, curObjShortOid.value) {

                        //如果是filter模式，显示show in list以在列表揭示filter条目以查看前后提交（或者说上下文）
                        if(enableFilterState.value) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.show_in_list)) {
                                filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value = false
                                showBottomSheet.value = false

                                doJobThenOffLoading {
//                            delay(100)  // wait rendering, may unnecessary yet
                                    val curItemIndex = curObjIndex.intValue  // 被长按的条目在 filterlist中的索引
                                    val idxList = filterIdxList.value  //取出存储filter索引和源列表索引的 index list，条目索引对应filter list条目索引，条目值对应的是源列表的真实索引

                                    doActIfIndexGood(curItemIndex, idxList) {  // it为当前被长按的条目在源列表中的真实索引
                                        UIHelper.scrollToItem(scope, listState, it)  //在源列表中定位条目
                                        requireBlinkIdx.intValue = it  //设置条目闪烁以便用户发现
                                    }
                                }
                            }

                        }


                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.restore)) {
                            showRestoreDialog.value = true
                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_prev)) label@{
//                            val list = getActuallyList()
                            //这里是和当前版本的上一版比较看当前版本相比上一版做了哪些修改，
                            // 这个上一版应该是完整文件历史中当前版本的上一版，而不是过滤结果中上一版
                            // 所以这里不用判断是否开启过滤模式，直接使用原始列表即可
                            val list = list.value
                            val indexAtDiffableList = curObjIndex.intValue
                            val previousIndex = indexAtDiffableList +1
                            if(!isGoodIndexForList(previousIndex, list)) {
                                if(hasMore.value) {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.plz_lode_more_then_try_again))
                                }else {
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.no_prev_to_compare))
                                }

                                return@label
                            }



                            val previous = list[previousIndex]
                            val commit1 = previous.commitOidStr
                            val commit2 = curObj.value.commitOidStr

//                    println("commit1:"+commit1)
//                    println("commit2:"+commit2)
//                    println("fileRelativePath:"+fileRelativePath)



                            //导航到diffScreen
                            goToDiffScreen(
//                            relativePathList = listOf(fileRelativePath),
                                diffableList = list.map { it.toDiffableItem() },
                                repoId = repoId,
                                fromTo = Cons.gitDiffFileHistoryFromTreeToPrev,
                                commit1OidStr = commit1,
                                commit2OidStr = commit2,
                                isDiffToLocal = false,
                                curItemIndexAtDiffableList = indexAtDiffableList,
                                localAtDiffRight = false,
                                fromScreen = DiffFromScreen.FILE_HISTORY_TREE_TO_PREV.code
                            )
                        }


//                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.msg)) {
//                            showItemMsg(curObj.value)
//                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.details)) {
                            showItemDetails(curObj.value)
                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.commits)) {
                            showCommits(curObj.value)
                        }

//                if(UserUtil.isPro() && (dev_EnableUnTestedFeature || commitsDiffToLocalTestPassed)) {
//                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_local)) {
//    //                    diff to local，点击跳转到tree to tree页面，然后diff
//                        //当前比较的描述信息的key，用来在界面显示这是在比较啥，值例如“和父提交比较”或者“比较两个提交”之类的
//                        Cache.set(Cache.Key.treeToTreeChangeList_titleDescKey, appContext.getString(R.string.compare_to_local))
//                        //这里需要传当前commit，然后cl页面会用当前commit查出当前commit的parents
//                        val commit2 = Cons.gitLocalWorktreeCommitHash
//                        val commitForQueryParents = Cons.allZeroOidStr
//                        // url 参数： 页面导航id/repoId/treeoid1/treeoid2/desckey
//                        navController.navigate(
//                            //注意是 parentTreeOid to thisObj.treeOid，也就是 旧提交to新提交，相当于 git diff abc...def，比较的是旧版到新版，新增或删除或修改了什么，反过来的话，新增删除之类的也就反了
//                            "${Cons.nav_TreeToTreeChangeListScreen}/${curRepo.value.id}/${curObj.value.oidStr}/$commit2/$commitForQueryParents"
//                        )
//                    }
//                }


                    }
                }

                //根据关键字过滤条目
                val keyword = filterKeyword.value.text  //关键字
                val enableFilter = filterModeActuallyEnabled(filterModeOn_dontUseThisCheckFilterModeReallyEnabledOrNot.value, keyword)

                val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
                val list = filterTheList(
                    needRefresh = filterResultNeedRefresh.value,
                    lastNeedRefresh = lastNeedRefresh,
                    enableFilter = enableFilter,
                    keyword = keyword,
                    lastKeyword = lastKeyword,
                    searching = searching,
                    token = token,
                    activityContext = activityContext,
                    filterList = filterList.value,
                    list = list.value,
                    resetSearchVars = resetSearchVars,
                    match = { idx, it -> true },
                    lastListSize = lastListSize,
                    filterIdxList = filterIdxList.value,
                    customTask = {
                        val canceled = initSearch(keyword = keyword, lastKeyword = lastKeyword, token = token)

                        val match = { idx:Int, it: FileHistoryDto ->
                            val found = it.treeEntryOidStr.contains(keyword, ignoreCase = true)
                                    || it.commitOidStr.contains(keyword, ignoreCase = true)
                                    || (it.commitList.find { commitOidStr -> commitOidStr.contains(keyword, ignoreCase = true) } != null)
                                    || it.authorEmail.contains(keyword, ignoreCase = true)
                                    || it.authorUsername.contains(keyword, ignoreCase = true)
                                    || it.committerEmail.contains(keyword, ignoreCase = true)
                                    || it.committerUsername.contains(keyword, ignoreCase = true)
                                    || it.dateTime.contains(keyword, ignoreCase = true)
                                    || it.msg.contains(keyword, ignoreCase = true)
                                    || formatMinutesToUtc(it.originTimeOffsetInMinutes).contains(keyword, ignoreCase = true)


                            found
                        }

                        searching.value = true

                        filterList.value.clear()
                        search(
                            src = list.value,
                            match = match,
                            matchedCallback = { idx, item ->
                                filterList.value.add(item)
                                filterIdxList.value.add(idx)
                            },
                            canceled = canceled)
                    }
                )


                val listState = if(enableFilter) filterListState else listState
//        if(enableFilter) {  //更新filter列表state
//            filterListState.value = listState
//        }

                //更新是否启用filter
                enableFilterState.value = enableFilter


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
                ) { idx, it ->
                    FileHistoryItem(
                        showBottomSheet = showBottomSheet,
                        curCommit = curObj,
                        curCommitIdx = curObjIndex,
                        idx = idx,
                        dto = it,
                        requireBlinkIdx = requireBlinkIdx,
                        lastClickedItemKey = lastClickedItemKey,
                        shouldShowTimeZoneInfo = shouldShowTimeZoneInfo,
                        showItemMsg = showItemMsg,
                    ) { thisObj ->
                        Msg.requireShow(activityContext.getString(R.string.diff_to_local))

                        //导航到diffScreen
                        goToDiffScreen(
//                        relativePathList = listOf(fileRelativePath),
                            diffableList = list.map { it.toDiffableItem() },
                            repoId = repoId,
                            fromTo = Cons.gitDiffFileHistoryFromTreeToLocal,
                            commit1OidStr = it.commitOidStr,
                            commit2OidStr = Cons.git_LocalWorktreeCommitHash,
                            isDiffToLocal = true,
                            curItemIndexAtDiffableList = idx,
                            localAtDiffRight = true,
                            fromScreen = DiffFromScreen.FILE_HISTORY_TREE_TO_LOCAL.code
                        )

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

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {

        doJobThenOffLoading job@{
            //这里只用来获取是否需要forceReload的值，且这个值只需获取一次，所以getThenDel设置为true（其实多次获取也没事，只是会导致无意义查询）
            val (requestType, data) = getRequestDataByState<Any?>(needRefresh.value)

            //滚动以使用户最后在Diff页面查看的条目可见
            val actuallyList = if(enableFilterState.value) filterList.value else list.value
            val actuallyListState = if(enableFilterState.value) filterListState else listState
            //最后一个else是TreeToTree
            val lastClickedItemKey = SharedState.fileHistory_LastClickedItemKey.value

            UIHelper.scrollByPredicate(scope, actuallyList, actuallyListState) { idx, item ->
                item.getItemKey() == lastClickedItemKey
            }



            val forceReload = (requestType == StateRequestType.forceReload)

            if(forceReload || curRepo.value.id.isBlank() || headOidOfThisScreen.value.isNullOrEmptyOrZero) {
                //从db查数据
                val repoDb = AppModel.dbContainer.repoRepository
                val repoFromDb = repoDb.getById(repoId)
                if (repoFromDb == null) {
                    MyLog.w(TAG, "#LaunchedEffect: query repo info from db error! repoId=$repoId}")
                    return@job
                }
                curRepo.value = repoFromDb
                val repoFullPath = repoFromDb.fullSavePath
                //"[fileName of RepoName]"
                titleInfo.value = "[${getFileNameFromCanonicalPath(fileRelativePath)} of ${repoFromDb.repoName}]"

//            val isDetached = dbIntToBool(repoFromDb.isDetached)

                Repository.open(repoFullPath).use { repo ->
                    val head = Libgit2Helper.resolveHEAD(repo)
                    if (head == null) {
                        MyLog.w(TAG, "#LaunchedEffect: head is null! repoId=$repoId}")
                        return@job
                    }
                    val headOid = head.peel(GitObject.Type.COMMIT)?.id()
                    if (headOid == null || headOid.isNullOrEmptyOrZero) {
                        MyLog.w(TAG, "#LaunchedEffect: headOid is null or invalid! repoId=$repoId, headOid=$headOid")
                        return@job
                    }


                    headOidOfThisScreen.value = headOid
                }

                //第一次查询，指向headOid，NO！不要这么做，不然compose销毁又重建，恢复数据时，指向原本列表之后的commit就又重新指向head了，就乱了
                //不要在这给nexCommitOid和条目列表赋值！要在doLoadMore里给它们赋值！
//                nextCommitOid.value = headOid

            }

            // do first load
            val firstLoad = true
            val loadToEnd = false

            //传repoFullPath是用来打开git仓库的
            doLoadMore(curRepo.value.fullSavePath, headOidOfThisScreen.value, firstLoad, forceReload, loadToEnd)
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

}
