package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.AskGitUsernameAndEmailDialogWithSelection
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.CenterPaddingRow
import com.catpuppyapp.puppygit.compose.CheckoutDialog
import com.catpuppyapp.puppygit.compose.CheckoutDialogFrom
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyScrollableColumn
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CreateBranchDialog
import com.catpuppyapp.puppygit.compose.DefaultPaddingRow
import com.catpuppyapp.puppygit.compose.DefaultPaddingText
import com.catpuppyapp.puppygit.compose.FetchRemotesDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.ForcePushWithLeaseCheckBox
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ResetDialog
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SelectionRow
import com.catpuppyapp.puppygit.compose.SetUpstreamDialog
import com.catpuppyapp.puppygit.compose.checkoutOptionJustCheckoutForLocalBranch
import com.catpuppyapp.puppygit.compose.getDefaultCheckoutOption
import com.catpuppyapp.puppygit.compose.invalidCheckoutOption
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.branchListPagePublishBranchTestPassed
import com.catpuppyapp.puppygit.dev.branchRenameTestPassed
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dev.rebaseTestPassed
import com.catpuppyapp.puppygit.dev.resetByHashTestPassed
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.BranchNameAndTypeDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.BranchItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.goToCommitListScreen
import com.catpuppyapp.puppygit.screen.functions.goToTreeToTreeChangeList
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.CommitListFrom
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.showErrAndSaveLog
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Branch
import com.github.git24j.core.Repository

private const val TAG = "BranchListScreen"


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BranchListScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic:HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
    repoId:String,
//    branch:String?,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
//    val navController = AppModel.navController
    val activityContext = LocalContext.current
//    val haptic = LocalHapticFeedback.current
//    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

//    val inDarkTheme = Theme.inDarkTheme

    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    //获取假数据
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<BranchNameAndTypeDto>())

    //请求闪烁的条目，用来在定位某条目时，闪烁一下以便用户发现
    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val pageRequest = rememberSaveable{mutableStateOf("")}

    //这个页面的滚动状态不用记住，每次点开重置也无所谓
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false) }
    val showCreateBranchDialog = rememberSaveable { mutableStateOf(false) }
    // 创建分支，默认勾选checkout
    val requireCheckout = rememberSaveable { mutableStateOf(true) }
    val showCheckoutBranchDialog = rememberSaveable { mutableStateOf(false) }
    val forceCheckoutForCreateBranch = rememberSaveable { mutableStateOf(false) }
//    val showCheckoutRemoteBranchDialog = StateUtil.getRememberSaveableState(initValue = false)

    val initCreateBranchDialog = {
        forceCheckoutForCreateBranch.value = false
        // 显示添加分支的弹窗
        showCreateBranchDialog.value = true
    }

    val needRefresh = rememberSaveable { mutableStateOf("")}
    val branchName = rememberSaveable { mutableStateOf("")}
//    val curCommit = rememberSaveable{ mutableStateOf(CommitDto()) }
    val curObjInPage = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInPage", initValue = BranchNameAndTypeDto())
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))

    val showRebaseOrMergeDialog = rememberSaveable { mutableStateOf(false)}
    val rebaseOrMergeSrc = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "rebaseOrMergeSrc", initValue = BranchNameAndTypeDto())
    val requireRebase = rememberSaveable { mutableStateOf(false)}
    fun initRebaseOrMergeDialog(
        isRebase: Boolean,
        src: BranchNameAndTypeDto?,
        caller:BranchNameAndTypeDto,
    ) {
        val calledByCurrentBranchAndSrcIsUpstreamOfIt = caller.isCurrent

        if(src == null) {
            if(calledByCurrentBranchAndSrcIsUpstreamOfIt) {
                // current branch upstream is null, this maybe happen
                Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_not_set_or_not_published))
            }else {
                // branch is null, this should never happen
                Msg.requireShowLongDuration(activityContext.getString(R.string.resolve_reference_failed))
            }

            return
        }

        // 如果是当前分支调用的本方法，并且src是他的上游分支，并且它不落后于上游分支，则不需要合并
        if(calledByCurrentBranchAndSrcIsUpstreamOfIt && caller.behind == 0) {
            Msg.requireShow(activityContext.getString(R.string.already_up_to_date))
            return
        }

        requireRebase.value = isRebase
        rebaseOrMergeSrc.value = src

        showRebaseOrMergeDialog.value = true
    }

    // username and email start
    val username = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val showUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val afterSetUsernameAndEmailSuccessCallback = mutableCustomStateOf<(()->Unit)?>(keyTag = stateKeyTag, keyName = "afterSetUsernameAndEmailSuccessCallback") { null }
    val initSetUsernameAndEmailDialog = { curRepo:RepoEntity, callback:(()->Unit)? ->
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                //回显用户名和邮箱
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)
                username.value = usernameFromConfig
                email.value = emailFromConfig
            }

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
    // username and email end

    if(showUsernameAndEmailDialog.value) {
        val curRepo = curRepo.value
        val closeDialog = { showUsernameAndEmailDialog.value = false }

        //请求用户设置用户名和邮箱的弹窗
        AskGitUsernameAndEmailDialogWithSelection(
            curRepo = curRepo,
            username = username,
            email = email,
            closeDialog = closeDialog,
            onErrorCallback = { e->
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "set username and email err (from BranchList page): ${e.stackTraceToString()}")
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

//    val checkoutRemoteOptionDetachHead=0
//    val checkoutRemoteOptionCreateBranch=1
//    val checkoutRemoteOptionDefault=1  //默认选中创建分支，detach head如果没reflog，有可能丢数据
//    val checkoutRemoteOptions = listOf(appContext.getString(R.string.detach_head),
//        appContext.getString(R.string.new_branch)+"("+appContext.getString(R.string.recommend)+")")
//    val checkoutRemoteSelectedOption = StateUtil.getRememberSaveableIntState(initValue = checkoutRemoteOptionDefault)
//    val checkoutRemoteCreateBranchName = StateUtil.getRememberSaveableState(initValue = "")

    //这个变量代表当前仓库的“活跃分支”，不要用来干别的，只是用来在创建分支的时候让用户知道是基于哪个分支创建的。
    val repoCurrentActiveBranchOrShortDetachedHashForShown = rememberSaveable { mutableStateOf("")}  //用来显示给用户看的短分支名或提交号
    val repoCurrentActiveBranchFullRefForDoAct = rememberSaveable { mutableStateOf("")}  //分支长引用名，只有在非detached时，才用到这个变量
    val repoCurrentActiveBranchOrDetachedHeadFullHashForDoAct = rememberSaveable { mutableStateOf("")}  //合并detached head时用这个变量
    val curRepoIsDetached = rememberSaveable { mutableStateOf(false)}  //当前仓库是否detached

    val defaultLoadingText = stringResource(R.string.loading)
    val loading = rememberSaveable { mutableStateOf(false)}
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText)}
    val loadingOn = { text:String ->
        loadingText.value=text
        loading.value=true
    }
    val loadingOff = {
        loadingText.value = activityContext.getString(R.string.loading)
        loading.value=false
    }



    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }





    // merge param `src` into current branch of repo
    suspend fun doMerge(trueMergeFalseRebase:Boolean, src: BranchNameAndTypeDto):Ret<Unit?> {
        // avoid mistake use
        val curObjInPage = Unit

        //如果选中条目和仓库当前活跃分支一样，则不用合并
        if(src.oidStr == repoCurrentActiveBranchOrDetachedHeadFullHashForDoAct.value) {
//            requireShowToast(appContext.getString(R.string.merge_failed_src_and_target_same))
            Msg.requireShow(activityContext.getString(R.string.already_up_to_date))
            return Ret.createSuccess(null)  //源和目标一样不算错误，返回true
        }

        Repository.open(curRepo.value.fullSavePath).use { repo ->
            val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)

            //如果用户名或邮箱无效，无法创建commit，merge无法完成，所以，直接终止操作
            if(Libgit2Helper.isUsernameAndEmailInvalid(usernameFromConfig,emailFromConfig)) {
                return Ret.createError(null, activityContext.getString(R.string.plz_set_username_and_email_first))
            }


            val targetRefName = src.fullName  //如果是detached这个值是空，会用后面的hash来进行合并，如果非detached，即使这个传长引用名在冲突文件里显示的依然是短引用名
            val username = usernameFromConfig
            val email = emailFromConfig
            val requireMergeByRevspec = curRepoIsDetached.value  //如果是detached head，没当前分支，用下面的revspec（commit hash）进行合并，否则用上面的targetRefName(分支短或全名）进行合并
            val revspec = src.oidStr

            val mergeResult = if(trueMergeFalseRebase) {
                Libgit2Helper.mergeOneHead(
                    repo = repo,
                    targetRefName = targetRefName,
                    username = username,
                    email = email,
                    requireMergeByRevspec = requireMergeByRevspec,
                    revspec = revspec,
                    settings = settings
                )
            } else {
                Libgit2Helper.mergeOrRebase(
                    repo,
                    targetRefName = targetRefName,
                    username = username,
                    email = email,
                    requireMergeByRevspec = requireMergeByRevspec,
                    revspec = revspec,
                    trueMergeFalseRebase = false,
                    settings=settings
                )
            }

            if (mergeResult.hasError()) {
                //检查是否存在冲突条目
                //如果调用者想自己判断是否有冲突，可传showMsgIfHasConflicts为false
                val errMsg = if (mergeResult.code == Ret.ErrCode.mergeFailedByAfterMergeHasConfilts) {
                    activityContext.getString(R.string.has_conflicts)
//                    if(trueMergeFalseRebase) {
//                        appContext.getString(R.string.merge_has_conflicts)
//                    }else {
//                        appContext.getString(R.string.rebase_has_conflicts)
//                    }
                }else{
                    //显示错误提示
                    mergeResult.msg
                }

                //记到数据库error表(应由调用者负责记）
//                createAndInsertError(curRepo.value.id, mergeResult.msg)

                return Ret.createError(null, errMsg)
            }


            //执行到这，既没冲突，又没出错，要么 into 的那个分支已经是最新，要么就合并成功创建了新提交

            //这段代码废弃，改用 Libgit2Helper.updateDbAfterMergeSuccess() 了
            //如果操作成功，显示下成功提示
//            if(mergeResult.code == Ret.SuccessCode.upToDate) {  //合并成功，但什么都没改，因为into的那个分支已经领先或者和mergeTarget拥有相同的最新commit了(换句话说：接收合并的那个分支要么比请求合并的分支新，要么和它一样)
//                // up to date 时 hash没变，所以不用更新db，只显示下提示即可
//                requireShowToast(appContext.getString(R.string.already_up_to_date))
//            }else {  //合并成功且创建了新提交
//                //合并完了，创建了新提交，需要更新db
//                val repoDB = AppModel.dbContainer.repoRepository
//                val shortNewCommitHash = mergeResult.data.toString().substring(Cons.gitShortCommitHashRange)
//                //更新db
//                repoDB.updateCommitHash(
//                    repoId=curRepo.value.id,
//                    lastCommitHash = shortNewCommitHash,
//                )
//
//                //显示成功通知
//                requireShowToast(appContext.getString(R.string.merge_success))
//
//            }
            //合并成功清下仓库状态，要不然可能停留在Merging
            Libgit2Helper.cleanRepoState(repo)
            //合并完成后更新db，显示通知
            Libgit2Helper.updateDbAfterMergeSuccess(mergeResult,activityContext,curRepo.value.id, Msg.requireShow, trueMergeFalseRebase)
        }


        return Ret.createSuccess(null)
    }

    if (showCreateBranchDialog.value) {
        CreateBranchDialog(
            branchName = branchName,
            curRepo = curRepo.value,
            curBranchName = repoCurrentActiveBranchOrShortDetachedHashForShown.value,
            requireCheckout = requireCheckout,
            forceCheckout=forceCheckoutForCreateBranch,
            loadingOn=loadingOn,
            loadingOff = loadingOff,
            loadingText = stringResource(R.string.creating_branch),
            onCancel = {showCreateBranchDialog.value=false},
            onErr = {e->
                val branchName = branchName.value
                val errSuffix = " -(at create branch dialog, branch name=$branchName)"
                Msg.requireShowLongDuration(e.localizedMessage ?:"create branch err")
                createAndInsertError(repoId, ""+e.localizedMessage+errSuffix)
                MyLog.e(TAG, "create branch err: name=$branchName, requireCheckout=${requireCheckout.value}, forceCheckout=${forceCheckoutForCreateBranch.value}, err="+e.stackTraceToString())
            },
            onFinally = {
                changeStateTriggerRefreshPage(needRefresh)
            }
        )
    }

    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val initUpstreamForCheckoutRemoteBranch = rememberSaveable { mutableStateOf("") }
    val remotePrefixMaybe = rememberSaveable { mutableStateOf("") }
    val isCheckoutRemoteBranch = rememberSaveable { mutableStateOf(false) }
    val checkoutLocalBranch = rememberSaveable { mutableStateOf(false) }
    // will update when init checkout dialog
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(invalidCheckoutOption) }

    if(showCheckoutBranchDialog.value) {
        //注意：这种写法，如果curObjInPage.value被重新赋值，本代码块将会被重复调用！不过实际不会有问题，因为显示弹窗时无法再长按条目进而无法改变本对象。
        // 另外如果在onOk里取对象也会有此问题，假如显示弹窗后对象被改变，那视图会更新，变成新对象的值，onOk最终执行时取出的对象自然也会和“最初”弹窗显示的不一致 (onOk取出的和“现在”视图显示的对象是一致的，都是修改后的值，“最初”的值已被覆盖)，
        // 如果用户在按弹窗的确定按钮前的一瞬间改变了此对象，那就会造成视图显示的对象和onOk取出的对象不一致的问题，不过这种问题几乎不会发生。
        //避免方法：给每个弹窗设置独立的变量，并仅在onClick之类的不会自动执行的callback里为其赋值，但这样每个组件都要有自己的状态，还要有专门的初始化函数，代码更繁琐，也更费内存。
        val item = curObjInPage.value

        CheckoutDialog(
            checkoutSelectedOption = checkoutSelectedOption,

            showCheckoutDialog = showCheckoutBranchDialog,
            branchName = branchNameForCheckout,
            remoteBranchShortNameMaybe = initUpstreamForCheckoutRemoteBranch.value,
            remotePrefixMaybe = remotePrefixMaybe.value,
            isCheckoutRemoteBranch = isCheckoutRemoteBranch.value,
            from = CheckoutDialogFrom.BRANCH_LIST,
            showJustCheckout = checkoutLocalBranch.value,
            expectCheckoutType = if(checkoutLocalBranch.value) Cons.checkoutType_checkoutRefThenUpdateHead else Cons.checkoutType_checkoutRefThenDetachHead,
            shortName = item.shortName,
            fullName = item.fullName,
            curRepo = curRepo.value,
            curCommitOid = item.oidStr,
            curCommitShortOid = item.shortOidStr,
            requireUserInputCommitHash = false,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { _, _, _, _, ->
                changeStateTriggerRefreshPage(needRefresh)
            },
        )
    }




    val showSetUpstreamForLocalBranchDialog = rememberSaveable { mutableStateOf(false)}
    val upstreamRemoteOptionsList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "upstreamRemoteOptionsList",
        initValue = listOf<String>()
    )  //初始化页面时更新这个列表
    val upstreamSelectedRemote = rememberSaveable{mutableIntStateOf(0)}  //默认选中第一个remote，每个仓库至少有一个origin remote，应该不会出错
    //默认选中为上游设置和本地分支相同名
    val upstreamBranchSameWithLocal =rememberSaveable { mutableStateOf(true)}
    //是否显示清除按钮
    val showClearForSetUpstreamDialog =rememberSaveable { mutableStateOf(false)}
    //把远程分支名设成当前分支的完整名
    val upstreamBranchShortRefSpec = rememberSaveable { mutableStateOf("")}
    val afterSetUpstreamSuccessCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "afterSetUpstreamSuccessCallback") { null }
    val setUpstreamOnFinallyCallback = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "setUpstreamOnFinallyCallback") { null }

    //注意：如果callback不为null，设置上游的弹窗将不会在操作结束后自动刷新页面，这时应由callback负责刷新页面
    val initSetUpstreamDialog = { curObjInPage:BranchNameAndTypeDto, callback:(()->Unit)? ->
        // onClick()
        // 弹出确认框，为分支设置上游
        if(curObjInPage.type == Branch.BranchType.REMOTE) {  // remote分支不能设置上游
            //提示用户不能给remote设置上游
            Msg.requireShowLongDuration(activityContext.getString(R.string.cant_set_upstream_for_remote_branch))
        }else { //为本地分支设置上游
            //设置默认值
            var remoteIdx = 0   //默认选中第一个元素
            var shortBranch = curObjInPage.shortName  //默认分支名为当前选中的分支短名
            var sameWithLocal = true  //默认勾选和本地分支同名，除非用户的上游不为空且有值

            // 查询旧值，如果有的话
            val upstream = curObjInPage.upstream

            // show clear if upstream was configured
            showClearForSetUpstreamDialog.value = upstream!=null && (upstream.remote.isNotBlank() || upstream.branchRefsHeadsFullRefSpec.isNotBlank())

            if(upstream!=null) {
                MyLog.d(TAG,"set upstream menu item #onClick(): upstream is not null, old remote in config is: ${upstream.remote}, old branch in config is:${upstream.branchRefsHeadsFullRefSpec}")

                val oldRemote = upstream.remote
                //查询之前的remote
                if(oldRemote.isNotBlank()) {
                    //检查 remote 是否在列表里，万一remote被删或者无效，就依然默认选中第一个remote
                    for((idx, value) in upstreamRemoteOptionsList.value.toList().withIndex()) {
                        if(value == oldRemote) {
                            MyLog.d(TAG,"set upstream menu item #onClick(): found old remote: ${value}, idx in remote list is: $idx")
                            remoteIdx = idx
                            break
                        }
                    }
                }
                val oldUpstreamShortBranchNameNoPrefix = upstream.remoteBranchShortRefSpecNoPrefix
                //查询之前的分支
                if(!oldUpstreamShortBranchNameNoPrefix.isNullOrBlank()) {
                    MyLog.d(TAG,"set upstream menu item #onClick(): found old branch full refspec: ${upstream.branchRefsHeadsFullRefSpec}, short refspec: $oldUpstreamShortBranchNameNoPrefix")
                    shortBranch = oldUpstreamShortBranchNameNoPrefix
                    sameWithLocal = false  //有有效的分支值，就不勾选 same with local 了
                }

            }

            upstreamSelectedRemote.intValue = remoteIdx
            upstreamBranchShortRefSpec.value = shortBranch
            upstreamBranchSameWithLocal.value = sameWithLocal

            MyLog.d(TAG, "set upstream menu item #onClick(): after read old settings, finally, default select remote idx is:${upstreamSelectedRemote.intValue}, branch name is:${upstreamBranchShortRefSpec.value}, check 'same with local branch` is:${upstreamBranchSameWithLocal.value}")

            //设置onFinally
            //如果没callback，由当前弹窗负责刷新页面；若有callback，让callback负责刷新页面
            //务必确保使用initSetUpstreamDialog并在不需要callback时传null，否则这个判断将出错，可能导致该刷新的时候没刷新或者不该刷新的时候刷新
            setUpstreamOnFinallyCallback.value = if(callback != null) null else { {changeStateTriggerRefreshPage(needRefresh)} }

            //设置callback
            afterSetUpstreamSuccessCallback.value = callback

            //显示弹窗
            showSetUpstreamForLocalBranchDialog.value = true
        }
    }

    val doTaskOrShowSetUpstream = { curObjInPage:BranchNameAndTypeDto, task:(()->Unit)? ->
        if(curObjInPage.isUpstreamAlreadySet()) {
            task?.invoke()
        }else {
            initSetUpstreamDialog(curObjInPage) {
                task?.invoke()
            }
        }
    }

    if(showSetUpstreamForLocalBranchDialog.value) {
        SetUpstreamDialog(
            callerTag = TAG,
            curRepo = curRepo.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            curBranchFullName = curObjInPage.value.fullName,
            isCurrentBranchOfRepo = curObjInPage.value.isCurrent,
            curBranchShortName = curObjInPage.value.shortName,
            remoteList = upstreamRemoteOptionsList.value,
            selectedOption = upstreamSelectedRemote,
            upstreamBranchShortName = upstreamBranchShortRefSpec,
            upstreamBranchShortNameSameWithLocal = upstreamBranchSameWithLocal,
            showClear = showClearForSetUpstreamDialog.value,
            closeDialog = {
                //隐藏弹窗就行，相关状态变量会在下次弹窗前初始化
                showSetUpstreamForLocalBranchDialog.value = false
            },
            onClearSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            },
            onClearErrorCallback = { e ->
                val repoName = curRepo.value.repoName
                val curBranchShortName = curObjInPage.value.shortName

                //显示通知
                Msg.requireShowLongDuration("clear upstream err: " + e.localizedMessage)
                //给用户看到错误
                createAndInsertError(
                    repoId,
                    "clear upstream for '$curBranchShortName' err: " + e.localizedMessage
                )
                //给开发者debug看的错误
                MyLog.e(
                    TAG,
                    "clear upstream for '$curBranchShortName' of '$repoName' err: " + e.stackTraceToString()
                )
            },
            onClearFinallyCallback = {
                changeStateTriggerRefreshPage(needRefresh)
            },
            onSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.set_upstream_success))

                //更新 curObjInPage的upstream，后面的callback可能会用到此变量
                //注: curObjInPage指向的对象在当前页面条目列表里，所以这里更新后即使不刷新整个页面也会刷新页面当前条目 （不过！我记得以前更新嵌套对象不会刷新啊？怎么回事？）
                Repository.open(curRepo.value.fullSavePath).use { repo ->
                    curObjInPage.value.upstream = Libgit2Helper.getUpstreamOfBranch(repo, curObjInPage.value.shortName)
                }

                //调用callback，如果有的话
                val callback = afterSetUpstreamSuccessCallback.value
                afterSetUpstreamSuccessCallback.value = null
                callback?.invoke()
            },
            onErrorCallback = onErr@{ e->
                val repoName = curRepo.value.repoName
                val curBranchShortName = curObjInPage.value.shortName
                val upstreamSameWithLocal = upstreamBranchSameWithLocal.value
                val remoteList = upstreamRemoteOptionsList.value
                val selectedRemoteIndex = upstreamSelectedRemote.intValue
                val upstreamShortName = upstreamBranchShortRefSpec.value

                //直接索引取值即可
                val remote = try {
                    remoteList[selectedRemoteIndex]
                } catch (e: Exception) {
                    MyLog.e(TAG,"err when get remote by index from remote list of '$repoName': remoteIndex=$selectedRemoteIndex, remoteList=$remoteList\nerr info:${e.stackTraceToString()}")
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_selected_remote_is_invalid))
                    return@onErr
                }

                //显示通知
                Msg.requireShowLongDuration("set upstream err: " + e.localizedMessage)
                //给用户看到错误
                createAndInsertError(
                    repoId,
                    "set upstream for '$curBranchShortName' err: " + e.localizedMessage
                )
                //给开发者debug看的错误
                MyLog.e(
                    TAG,
                    "set upstream for '$curBranchShortName' of '$repoName' err! user input branch is '$upstreamShortName', selected remote is $remote, user checked use same name with local is '$upstreamSameWithLocal'\nerr: " + e.stackTraceToString()
                )


            },

            onFinallyCallback = setUpstreamOnFinallyCallback.value,

        )
    }

    // if is current branch, find is upstream; else, merge selected branch into current (merge `curObjInPage` into current branch)
    val resolveMergeSrc = { target: BranchNameAndTypeDto, branchList: List<BranchNameAndTypeDto> ->
        // avoid mistake use
        val curObjInPage = Unit
        val list = Unit

        if(target.isCurrent) {
            val upstreamFullName = target.upstream?.remoteBranchRefsRemotesFullRefSpec

            if(upstreamFullName == null) {
                null
            }else {
                branchList.find { it.fullName == upstreamFullName}
            }
        } else {
            target
        }
    }

    if(showRebaseOrMergeDialog.value) {
        // avoid mistake use
        val curObjInPage = Unit

        ConfirmDialog2(
            title = stringResource(if(requireRebase.value) R.string.rebase else R.string.merge),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
//                //if branch show "merge branch_a into branch_b"
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.Center,
                      verticalAlignment = Alignment.CenterVertically
                  ) {
                      val left = if(!requireRebase.value) rebaseOrMergeSrc.value.shortName else if(curRepoIsDetached.value) Cons.gitDetachedHead else repoCurrentActiveBranchOrShortDetachedHashForShown.value
                      val right = if(requireRebase.value) rebaseOrMergeSrc.value.shortName else if(curRepoIsDetached.value) Cons.gitDetachedHead else repoCurrentActiveBranchOrShortDetachedHashForShown.value
                      val text = if(requireRebase.value) {
                          replaceStringResList(stringResource(R.string.rebase_left_onto_right), listOf(left, right))
                      }else{
                          replaceStringResList(stringResource(R.string.merge_left_into_right), listOf(left, right))
                      }

                      MySelectionContainer {
                          Text(
                              text = text,
                              softWrap = true,
                              overflow = TextOverflow.Visible
                          )
                      }

                  }

                }
            },
            onCancel = { showRebaseOrMergeDialog.value = false }
        ) {  //onOk
            showRebaseOrMergeDialog.value=false
            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = if(requireRebase.value) activityContext.getString(R.string.rebasing) else activityContext.getString(R.string.merging),
            )  job@{
                try {
                    val mergeRet = doMerge(trueMergeFalseRebase = !requireRebase.value, rebaseOrMergeSrc.value)
                    if(mergeRet.hasError()) {
                        throw RuntimeException(mergeRet.msg)
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "MergeDialog#doMerge(trueMergeFalseRebase=${!requireRebase.value}) err: "+e.stackTraceToString())

                    val errMsg = "${if(requireRebase.value) "rebase" else "merge"} failed: "+e.localizedMessage
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(curRepo.value.id, errMsg)
                }finally {
                    //别忘了刷新页面！
                    changeStateTriggerRefreshPage(needRefresh)
                }

            }
        }
    }

    val showLocalBranchDelDialog = rememberSaveable { mutableStateOf(false)}
    val delUpstreamToo = rememberSaveable { mutableStateOf(false)}  //如果没上游，禁用“删除上游”勾选框，注意，这个勾选框控制的是删除本地的上游分支本地与否，配置文件中为当前本地分支配置的上游设置是一定会删除的(libgit2负责)，不管是否勾选这个选项。
    val delUpstreamPush = rememberSaveable { mutableStateOf(false)}
    val showRemoteBranchDelDialog = rememberSaveable { mutableStateOf(false)}
    val userSpecifyRemoteName = rememberSaveable { mutableStateOf("")}  //删除远程分支时，如果remote有歧义，让用户指定一个具体remote名字
    val curRequireDelRemoteNameIsAmbiguous = rememberSaveable { mutableStateOf(false)}

    if(showLocalBranchDelDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    Row {
                        Text(text = stringResource(id = R.string.del_branch) + ":")
                    }
                    Row(modifier = Modifier.padding(5.dp)) {
                        // spacer
                    }
                    MySelectionContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = curObjInPage.value.shortName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }

//                    Row {
//                        Text(text = activityContext.getString(R.string.are_you_sure))
//                    }

                    //若已设置上游且已发布，则可删除远程，否则不可
                    if (curObjInPage.value.isUpstreamValid()) {
                        Row(modifier = Modifier.padding(5.dp)) {

                        }
                        MyCheckBox(text = stringResource(R.string.del_upstream_too), value = delUpstreamToo)
                        if (delUpstreamToo.value) {  //如果能勾选这个选项其实基本就可以断定存在有效上游了
                            DefaultPaddingRow {
                                Text(text = stringResource(id = R.string.upstream) + ": ")
                                MySelectionContainer {
                                    Text(
                                        text = curObjInPage.value.upstream?.remoteBranchShortRefSpec ?: "",  //其实如果通过上面的判断，基本就能断定存在有效上游了，这里的?:空值判断只是以防万一
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            MyCheckBox(text = stringResource(R.string.push), value = delUpstreamPush)
                        }
                    }
                }
            },
            onCancel = {showLocalBranchDelDialog.value=false},

            okTextColor = MyStyleKt.TextColor.danger(),
            okBtnText = stringResource(R.string.delete),
            cancelBtnText = stringResource(R.string.cancel),
        ) {
            showLocalBranchDelDialog.value=false

            val curRepo = curRepo.value
            val curObjInPage = curObjInPage.value

            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = activityContext.getString(R.string.deleting_branch),
            ) {
                try {
                    //删除本地分支
                    Repository.open(curRepo.fullSavePath).use { repo ->

                        val deleteBranchRet = Libgit2Helper.deleteBranch(repo, curObjInPage.fullName);
                        if(deleteBranchRet.hasError()) {
                            throw RuntimeException(deleteBranchRet.msg)
                        }

                        Msg.requireShow(activityContext.getString(R.string.del_local_branch_success))

                        //检查当前选中对象是否有上游，如果有，检查用户是否勾选了删除上游，如果是执行删除上游
                        if (delUpstreamToo.value) {  //用户勾选了删除远程分支，检查下有没有有效的远程分支可以删
                            if (curObjInPage.isUpstreamValid()) {
                                //进入到这说明用户勾选了一并删除上游并且存在有效上游，提示正在删除远程分支
                                Msg.requireShow(activityContext.getString(R.string.deleting_upstream))

                                //通过上面的判断，upstream不可能是null
                                val upstream = curObjInPage.upstream!!
                                //先删除本地的远程分支
                                val delBranchRet = Libgit2Helper.deleteBranch(
                                    repo,
                                    upstream.remoteBranchRefsRemotesFullRefSpec
                                )
                                if (delBranchRet.hasError()) {
                                    throw RuntimeException("del upstream '${upstream.remoteBranchShortRefSpec}' for '${curObjInPage.shortName}' err: ${delBranchRet.msg}")
                                }

                                //删除本地的远程分支成功后才会push远程，若失败，不会push

                                //后删除服务器的远程分支
                                if(delUpstreamPush.value) {  //若勾选push，删除远程的分支
                                    //查询凭据
                                    val remoteDb = AppModel.dbContainer.remoteRepository
                                    val remoteFromDb = remoteDb.getByRepoIdAndRemoteName(
                                        curRepo.id,
                                        upstream.remote
                                    )
                                    if (remoteFromDb == null) {
                                        throw RuntimeException("delete upstream '${upstream.remoteBranchShortRefSpec}' push err: query remote from db failed")
                                    }
                                    var credential: CredentialEntity? = null
                                    if (!remoteFromDb.pushCredentialId.isNullOrBlank()) {
                                        val credentialDb = AppModel.dbContainer.credentialRepository
//                                        credential = credentialDb.getByIdWithDecrypt(remoteFromDb.pushCredentialId)
                                        credential = credentialDb.getByIdWithDecryptAndMatchByDomain(id = remoteFromDb.pushCredentialId, url = remoteFromDb.pushUrl)
                                    }

                                    //执行删除(push)
                                    val delRemotePushRet = Libgit2Helper.deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec(
                                            repo,
                                            upstream.remote,
                                            upstream.branchRefsHeadsFullRefSpec,
                                            credential
                                    )

                                    if (delRemotePushRet.hasError()) {
                                        throw RuntimeException("del upstream '${upstream.remoteBranchShortRefSpec}' push err: "+delRemotePushRet.msg)
                                    }
                                }

                                //执行到这，本地分支和其上游都已经删除并推送到服务器（未勾选push则不会推送）了
                                Msg.requireShow(activityContext.getString(R.string.del_upstream_success))
                            }else {  //进入这里说明没有有效的上游但又勾选了删除上游（正常来说无有效应该勾选不了），提示下，不用执行删除
                                throw RuntimeException(activityContext.getString(R.string.del_upstream_failed_upstream_is_invalid))
                            }
                        }


                    }
                    //删除远程分支不一定成功，因为名字可能有歧义，这时，提示即可，不弹窗让用户输分支名，如果用户还是想删，可找到对应的远程分支手动删除，那个删除时如果发现remote名歧义就会提示用户输入一个具体的remote名
                }catch (e:Exception) {
                    val errMsg = "del branch failed: "+e.localizedMessage
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(curRepo.id, errMsg)

                    MyLog.e(TAG, "#delLocalBranchDialog err: "+e.stackTraceToString())
                }finally {
                    //别忘了刷新页面！
                    changeStateTriggerRefreshPage(needRefresh)
                }

            }
        }
    }

    val pushCheckBoxForRemoteBranchDelDialog = rememberSaveable { mutableStateOf(false)}
    if(showRemoteBranchDelDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete),
            okBtnEnabled = !pushCheckBoxForRemoteBranchDelDialog.value || !curRequireDelRemoteNameIsAmbiguous.value || userSpecifyRemoteName.value.isNotBlank(),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    Row {
                        Text(text = stringResource(id = R.string.del_remote_branch)+":")
                    }
                    Row(modifier = Modifier.padding(5.dp)){
                        // spacer
                    }

                    //可选，方便复制remote名
                    MySelectionContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = curObjInPage.value.shortName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                    Row(modifier = Modifier.padding(5.dp)) {

                    }

//                    Row{
//                        Text(text = activityContext.getString(R.string.are_you_sure))
//                    }
//                    Row(modifier = Modifier.padding(5.dp)) {
//
//                    }

                    MyCheckBox(text = activityContext.getString(R.string.push), value = pushCheckBoxForRemoteBranchDelDialog)

                    //若勾选push，则删除远程分支，删除远程分支时有可能无法从分支名中取出remote，这时需要用户输入
                    if (pushCheckBoxForRemoteBranchDelDialog.value) {
//                        Row {
//                            Text(text = "(" + stringResource(id = R.string.del_remote_branch_require_network_connection) + ")")
//                        }

                        // 如果取不出remote name，说明分支名有歧义，这时，弹出一个输入框，让用户输入分支的remote名字
                        if (curRequireDelRemoteNameIsAmbiguous.value) {
                            Row(modifier = Modifier.padding(5.dp)) {

                            }
                            Row {
                                Text(text = stringResource(R.string.remote_name_ambiguous_plz_specify_remote_name))
                            }
                            Row(modifier = Modifier.padding(5.dp)) {

                            }
                            TextField(
                                modifier = Modifier.fillMaxWidth(),

                                value = userSpecifyRemoteName.value,
                                singleLine = true,
                                onValueChange = {
                                    userSpecifyRemoteName.value = it
                                },
                                label = {
                                    Text(stringResource(R.string.specify_remote_name))
                                },
                                placeholder = {
                                    Text(stringResource(R.string.remote_name))
                                }
                            )
                        }
                    }

                }
            },
            onCancel = { showRemoteBranchDelDialog.value=false},

            okTextColor = MyStyleKt.TextColor.danger(),
            okBtnText = stringResource(R.string.delete),
            cancelBtnText = stringResource(R.string.cancel),
        ) {
            showRemoteBranchDelDialog.value=false
            val curRepo = curRepo.value
            val curObjInPage = curObjInPage.value

            doJobThenOffLoading(
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                loadingText = activityContext.getString(R.string.deleting_branch),
            ) {
                try {
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        //先删除本地的远程分支
                        val delBranchRet = Libgit2Helper.deleteBranch(
                            repo,
                            curObjInPage.fullName
                        )
                        if (delBranchRet.hasError()) {
                            throw RuntimeException(delBranchRet.msg)
                        }

                        //若勾选push，删除远程
                        if(pushCheckBoxForRemoteBranchDelDialog.value) {
                            //或者把逻辑改成只要用户输入了remote名，就用用户输入的？
                            val remote = if(curRequireDelRemoteNameIsAmbiguous.value) userSpecifyRemoteName.value else curObjInPage.remotePrefixFromShortName

                            //如果remote无效，返回
                            if(remote.isNullOrBlank()) {
                                throw RuntimeException("del remote branch '${curObjInPage.fullName}' err: remote name invalid")
                            }

                            //再删除服务器的远程分支
                            //查询凭据
                            val remoteDb = AppModel.dbContainer.remoteRepository
                            val remoteFromDb = remoteDb.getByRepoIdAndRemoteName(
                                curRepo.id,
                                remote
                            )
                            if (remoteFromDb == null) {
                                throw RuntimeException("del remote branch '${curObjInPage.shortName}' err: query remote from db failed")
                            }
                            var credential: CredentialEntity? = null
                            if (!remoteFromDb.pushCredentialId.isNullOrBlank()) {
                                val credentialDb = AppModel.dbContainer.credentialRepository
//                                credential = credentialDb.getByIdWithDecrypt(remoteFromDb.pushCredentialId)
                                credential = credentialDb.getByIdWithDecryptAndMatchByDomain(id = remoteFromDb.pushCredentialId, url = remoteFromDb.pushUrl)
                            }

                            //例如：移除 origin/main 中的 origin/，然后拼接成 refs/heads/main
                            val branchRefsHeadsFullRefSpec = "refs/heads/"+Libgit2Helper.removeGitRefSpecPrefix("$remote/", curObjInPage.shortName)  // remote值形如：origin/，shortName值形如 origin/main

                            //执行删除
                            val delRemotePushRet = Libgit2Helper.deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec(repo, remote, branchRefsHeadsFullRefSpec, credential)
                            if (delRemotePushRet.hasError()) {
                                throw RuntimeException(delRemotePushRet.msg)
                            }
                        }


                        //执行到这，本地远程分支删除了，且远程服务器上的分支也删除了(push到服务器了)
                        Msg.requireShow(activityContext.getString(R.string.del_remote_branch_success))

                    }

                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")

                    val errPrefix = "del remote branch '${curObjInPage.shortName}' err: "
                    createAndInsertError(curRepo.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, errPrefix+e.stackTraceToString())
                }finally {
                    //别忘了刷新页面！
                    changeStateTriggerRefreshPage(needRefresh)
                }

            }
        }
    }

//    val acceptHardReset = StateUtil.getRememberSaveableState(initValue = false)
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val resetDialogOid = rememberSaveable { mutableStateOf("")}
//    val resetDialogShortOid = StateUtil.getRememberSaveableState(initValue = "")
    val closeResetDialog = {
        showResetDialog.value = false
    }

    if (showResetDialog.value) {
        ResetDialog(
            fullOidOrBranchOrTag = resetDialogOid,
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,
            repoId=curRepo.value.id,
            refreshPage = {_, _, _ ->
                changeStateTriggerRefreshPage(needRefresh, StateRequestType.forceReload)
            }
        )

    }

    val clipboardManager = LocalClipboardManager.current

    val showDetailsDialog = rememberSaveable { mutableStateOf(false)}
    val detailsString = rememberSaveable { mutableStateOf("")}
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


    val showRenameDialog = rememberSaveable { mutableStateOf(false)}
    val nameForRenameDialog = rememberSaveable { mutableStateOf("")}
    val forceForRenameDialog = rememberSaveable { mutableStateOf(false)}
    val errMsgForRenameDialog = rememberSaveable { mutableStateOf("")}
    if(showRenameDialog.value) {
        val curItem = curObjInPage.value

        ConfirmDialog(
            title = stringResource(R.string.rename),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        value = nameForRenameDialog.value,
                        singleLine = true,
                        isError = errMsgForRenameDialog.value.isNotBlank(),
                        supportingText = {
                            if (errMsgForRenameDialog.value.isNotBlank()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = errMsgForRenameDialog.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (errMsgForRenameDialog.value.isNotBlank()) {
                                Icon(imageVector=Icons.Filled.Error,
                                    contentDescription=null,
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        onValueChange = {
                            nameForRenameDialog.value = it

                            // clear err msg
                            errMsgForRenameDialog.value = ""
                        },
                        label = {
                            Text(stringResource(R.string.new_name))
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    MyCheckBox(text = stringResource(R.string.overwrite_if_exist), value = forceForRenameDialog)
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = nameForRenameDialog.value != curItem.shortName,
            onCancel = {showRenameDialog.value = false}
        ) {
            val newName = nameForRenameDialog.value
            val branchShortName = curItem.shortName
            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.renaming)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val renameRet = Libgit2Helper.renameBranch(repo, branchShortName, newName, forceForRenameDialog.value)
                        if(renameRet.hasError()) {
                            errMsgForRenameDialog.value = renameRet.msg
                            return@doJobThenOffLoading
                        }

                        showRenameDialog.value=false
                    }

                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errmsg = e.localizedMessage ?: "rename branch err"
                    Msg.requireShowLongDuration(errmsg)
                    createAndInsertError(curRepo.value.id, "err: rename branch '${curObjInPage.value.shortName}' to ${nameForRenameDialog.value} failed, err is $errmsg")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }



    //filter相关，开始
    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false)}
    //filter相关，结束

    // 向下滚动监听，开始
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }

//    val filterListState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "filterListState", LazyListState(0,0))
    val filterListState = rememberLazyListState()
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
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

    val showPublishDialog = rememberSaveable { mutableStateOf(false)}
    val forcePublish = rememberSaveable { mutableStateOf(false)}
    val forcePush_pushWithLease = rememberSaveable { mutableStateOf(false) }
    val forcePush_expectedRefspecForLease = rememberSaveable { mutableStateOf("") }

    if(showPublishDialog.value) {
        val curBranch = curObjInPage.value
        val upstream = curBranch.upstream

        //本质上是push实现，若没设置上游，提示先设置，若设置了，无论是否已发布都显示发布弹窗，因为即使发布了也有需要用本地覆盖远程的情况
        if(curBranch.type != Branch.BranchType.LOCAL) {  //百分之99的可能性不会进入这个代码块，因为在显示弹窗前入口处设置了enabled条件为仅限local，不过要是错误触发的话，关弹窗就行
            showPublishDialog.value = false
            Msg.requireShowLongDuration(stringResource(R.string.canceled))
        }else if(curBranch.isUpstreamAlreadySet().not()) {  //没设置上游
            showPublishDialog.value = false  //关弹窗，不然页面重新渲染时会反复执行这里的代码块
            Msg.requireShowLongDuration(stringResource(R.string.plz_set_upstream_first))
        }else {  //显示弹窗
            ConfirmDialog(
                title = stringResource(R.string.publish),
                requireShowTextCompose = true,
                textCompose = {
                    ScrollableColumn {
                        SelectionRow {
                            Text(text = stringResource(R.string.local) +": ")
                            Text(
                                text = curBranch.shortName,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        SelectionRow {
                            Text(text = stringResource(R.string.remote) +": ")
                            Text(
                                text = upstream?.remoteBranchShortRefSpec ?: "",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        SelectionRow {
                            Text(text = stringResource(R.string.will_push_local_branch_to_remote_are_you_sure))
                        }

                        //如果上游已经发布，则显示force push选项
                        // 注意：不要改成无脑显示force，不然有可能错误覆盖别人的提交，
                        // 例如：你本地没分支a，你发布前没fetch，别人push了分支a，
                        // 你勾选了force，就会错误覆盖，但如果本地没有有效上游则禁用force，这样就不会错误覆盖了
                        if(upstream != null && upstream.isPublished) {
                            Spacer(modifier = Modifier.height(10.dp))
                            MyCheckBox(text = stringResource(R.string.force), value = forcePublish)


                            //如果勾选force，显示注意事项和push with lease选项
                            if(forcePublish.value) {
                                SelectionRow {
                                    DefaultPaddingText(
                                        text = stringResource(R.string.will_force_overwrite_remote_branch_even_it_is_ahead_to_local),
                                        color = MyStyleKt.TextColor.danger(),
                                    )
                                }

                                Spacer(Modifier.height(15.dp))

                                // force push with lease
                                ForcePushWithLeaseCheckBox(forcePush_pushWithLease, forcePush_expectedRefspecForLease)

                                //不需要额外加，不知道是不是新版compose做了处理，好像键盘盖不住输入框了
                                //勾选的太多，还有输入框，若显示软键盘则加点底部padding，不然可能键盘盖住字
//                                if(forcePush_pushWithLease.value) {
//                                    Spacer(Modifier.height(80.dp))
//                                }
                            }
                        }
                    }
                },
                okBtnEnabled = forcePublish.value.not() || forcePush_pushWithLease.value.not() || forcePush_expectedRefspecForLease.value.isNotEmpty(),
                onCancel = { showPublishDialog.value=false}
            ) {
                showPublishDialog.value=false

                val curRepo = curRepo.value
                val repoId = curRepo.id
                val force = forcePublish.value
                val forcePush_pushWithLease = forcePush_pushWithLease.value
                val forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease.value

                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                    try {

                        val dbContainer = AppModel.dbContainer
                        Repository.open(curRepo.fullSavePath).use { repo->
                            // lease check
                            if(force && forcePush_pushWithLease) {
                                loadingText.value = activityContext.getString(R.string.checking)

                                Libgit2Helper.forcePushLeaseCheckPassedOrThrow(
                                    repoEntity = curRepo,
                                    repo = repo,
                                    forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease,
                                    upstream = upstream,
                                )

                            }

                            loadingText.value = activityContext.getString(if(force) R.string.force_pushing else R.string.pushing)

                            // push
                            val credential = Libgit2Helper.getRemoteCredential(
                                dbContainer.remoteRepository,
                                dbContainer.credentialRepository,
                                repoId,
                                upstream!!.remote,
                                trueFetchFalsePush = false
                            )

                            Libgit2Helper.pushSingleBranch(repo, upstream, credential, force)

                            // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
                            val repoDb = AppModel.dbContainer.repoRepository
                            repoDb.updateLastUpdateTime(repoId, getSecFromTime())

                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }catch (e:Exception) {
                        showErrAndSaveLog(TAG, "#PublishBranchDialog(force=$force) err: "+e.stackTraceToString(), "Publish branch error: "+e.localizedMessage, Msg.requireShowLongDuration, repoId)
                    }finally {
                        changeStateTriggerRefreshPage(needRefresh)
                    }

                }

            }

        }

    }

    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<BranchNameAndTypeDto>())

    // start: search states
    val lastKeyword = rememberSaveable { mutableStateOf("") }
    val token = rememberSaveable { mutableStateOf("") }
    val searching = rememberSaveable { mutableStateOf(false) }
    val resetSearchVars = {
        searching.value = false
        token.value = ""
        lastKeyword.value = ""
    }
    // end: search states

    val getActuallyListState = {
        if(enableFilterState.value) filterListState else listState
    }

    val getActuallyList = {
        if(enableFilterState.value) filterList.value else list.value
    }

    val goToUpstream = { curObj:BranchNameAndTypeDto ->
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
            if(!curObj.isUpstreamValid()) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_not_set_or_not_published))
            }else {
                val upstreamFullName = curObj.getUpstreamFullName(activityContext)
                val actuallyList = getActuallyList()
                val actuallyListState = getActuallyListState()
                val targetIdx = actuallyList.toList().indexOfFirst { it.fullName ==  upstreamFullName }
                if(targetIdx == -1) {  //未在当前实际展示的列表找到条目，尝试在源列表查找
                    //如果开启过滤模式且未在过滤列表找到，尝试在源列表查找
                    if(filterModeOn.value) {
                        //从源列表找
                        val indexInOriginList = list.value.toList().indexOfFirst { it.fullName ==  upstreamFullName }

                        if(indexInOriginList != -1){  // found in origin list
                            filterModeOn.value = false  //关闭过滤模式
                            showBottomSheet.value = false  //关闭菜单

                            //定位条目
                            UIHelper.scrollToItem(scope, listState, indexInOriginList)
                            requireBlinkIdx.intValue = indexInOriginList  //设置条目闪烁以便用户发现
                        }else {
                            Msg.requireShow(activityContext.getString(R.string.upstream_not_found))
                        }
                    }else {  //非filter mode且没找到，说明源列表根本没有，直接提示没找到
                        Msg.requireShow(activityContext.getString(R.string.upstream_not_found))
                    }

                }else {  //在当前实际展示的列表（filter或源列表）找到了，直接跳转
                    UIHelper.scrollToItem(scope, actuallyListState, targetIdx)
                    requireBlinkIdx.intValue = targetIdx  //设置条目闪烁以便用户发现
                }
            }
        }
    }

    // page requests
    if(pageRequest.value==PageRequest.goToUpstream) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            goToUpstream(curObjInPage.value)
        }
    }


    BackHandler {
        if(filterModeOn.value) {
            filterModeOn.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }


    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }

    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }

    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
    }

    val showFetchAllDialog = rememberSaveable { mutableStateOf(false) }
    val remoteList = mutableCustomStateListOf(stateKeyTag, "remoteList") { listOf<RemoteDto>() }
    val initFetchAllDialog = {
        doJobThenOffLoading {
            AppModel.dbContainer.remoteRepository.getRemoteDtoListByRepoId(repoId).let {
                remoteList.value.clear()
                remoteList.value.addAll(it)
            }

            showFetchAllDialog.value = true
        }
    }

    if(showFetchAllDialog.value) {
        FetchRemotesDialog(
            title = stringResource(R.string.fetch_all),
            text = stringResource(R.string.fetch_all_are_u_sure),
            remoteList = remoteList.value,
            closeDialog = { showFetchAllDialog.value = false },
            curRepo = curRepo.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { changeStateTriggerRefreshPage(needRefresh) },
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        val repoAndBranch = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value)
                        Column (modifier = Modifier.combinedClickable (
                                    onDoubleClick = {
                                        defaultTitleDoubleClick(scope, listState, lastPosition)
                                    },
                                    onLongClick = null
//                                    { // onLongClick
////                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
////                                        Msg.requireShow(repoAndBranch)
//                                    }
                                ){  //onClick
    //                        Msg.requireShow(repoAndBranch)
                                    showTitleInfoDialog.value=true
                                }
                        ){
                            ScrollableRow  {
                                Text(
                                    text= stringResource(R.string.branches),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ScrollableRow  {
                                Text(
                                    text= repoAndBranch,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = MyStyleKt.Title.secondLineFontSize
                                )
                            }
                        }

                    }
                },
                navigationIcon = {
                    if(filterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),

                        ) {
                            resetSearchVars()
                            filterModeOn.value = false
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
                    if(!filterModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.focus_current_branch),
                            icon =  Icons.Filled.CenterFocusWeak,
                            iconContentDesc = stringResource(R.string.focus_current_branch),

                            //非detached HEAD 则启用（注：detached HEAD无当前分支（活跃分支），所以没必要启用）
                            enabled = !dbIntToBool(curRepo.value.isDetached)
                        ) {
                            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                                val indexOfCurrent = list.value.toList().indexOfFirst {
                                    it.isCurrent
                                }

                                if(indexOfCurrent == -1) {
                                    Msg.requireShow(activityContext.getString(R.string.not_found))
                                }else {  // found
                                    // 注：直接在源list的list state跳转即可，不需要考虑filter模式是否开启，因为只有当filter模式关闭时才显示此按钮，显示此按钮才有可能被用户按，所以，正常情况下仅能在filter模式关闭时才能用此功能

                                    //跳转到对应条目
                                    UIHelper.scrollToItem(scope, listState, indexOfCurrent)
                                    //设置条目闪烁一下以便用户发现
                                    requireBlinkIdx.intValue = indexOfCurrent
                                }
                            }
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.filter),
                            icon =  Icons.Filled.FilterAlt,
                            iconContentDesc = stringResource(R.string.filter),
                        ) {
                            // filter item
                            filterKeyword.value = TextFieldValue("")
                            filterModeOn.value = true
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.refresh),
                            icon =  Icons.Filled.Refresh,
                            iconContentDesc = stringResource(R.string.refresh),
                        ) {
                            changeStateTriggerRefreshPage(needRefresh)
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.fetch),
                            icon =  Icons.Filled.Downloading,
                            iconContentDesc = stringResource(R.string.fetch),
                        ) {
                            initFetchAllDialog()
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.create_branch),
                            icon =  Icons.Filled.Add,
                            iconContentDesc = stringResource(R.string.create_branch),
                        ) {
                            initCreateBranchDialog()
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
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) },

        ) {

            if (loading.value) {
//            LoadingText(text = loadingText.value, contentPadding = contentPadding)
                LoadingDialog(text = loadingText.value)
            }

            if(showBottomSheet.value) {
                BottomSheet(showBottomSheet, sheetState, curObjInPage.value.shortName) {
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout),textDesc= stringResource(R.string.switch_branch),
                        enabled = curObjInPage.value.isCurrent.not()
                    ){
                        val curObjInPage = curObjInPage.value
                        doJobThenOffLoading {
                            val isCheckLocalBranch = curObjInPage.type == Branch.BranchType.LOCAL
                            checkoutLocalBranch.value = isCheckLocalBranch
                            val showJustCheckout = isCheckLocalBranch
                            if(checkoutSelectedOption.intValue == invalidCheckoutOption
                                // just checkout unavailable but selected it, need reset to other option
                                || (showJustCheckout.not() && checkoutSelectedOption.intValue == checkoutOptionJustCheckoutForLocalBranch)
                            ) {
                                checkoutSelectedOption.intValue = getDefaultCheckoutOption(showJustCheckout)
                            }

                            val isCheckoutRemote = curObjInPage.type == Branch.BranchType.REMOTE
                            isCheckoutRemoteBranch.value =  isCheckoutRemote

                            if(isCheckoutRemote) { // isRemote
                                //这个Remotes列表每次刷新页面后会更新
                                val maybeIsRemoteIfNoNameAmbiguous = upstreamRemoteOptionsList.value.find { curObjInPage.shortName.startsWith(it) }

                                initUpstreamForCheckoutRemoteBranch.value = if(maybeIsRemoteIfNoNameAmbiguous != null) {
                                    remotePrefixMaybe.value = maybeIsRemoteIfNoNameAmbiguous

                                    val branchNameNoRemotePrefix = curObjInPage.shortName.removePrefix("$maybeIsRemoteIfNoNameAmbiguous/")
                                    // checkout HEAD，不填名字
                                    if(branchNameNoRemotePrefix == Cons.gitHeadStr) {
                                        ""
                                    }else {
                                        branchNameNoRemotePrefix
                                    }
                                }else {
                                    remotePrefixMaybe.value = ""

                                    ""
                                }

                                // if find remote branch name, set it as init new branch name
                                if(initUpstreamForCheckoutRemoteBranch.value.isNotBlank()) {
                                    branchNameForCheckout.value = initUpstreamForCheckoutRemoteBranch.value
                                }

                            }else {  //非remote，清空相关字段
                                remotePrefixMaybe.value = ""
                                initUpstreamForCheckoutRemoteBranch.value = ""
                            }

                            showCheckoutBranchDialog.value = true

                        }
                    }
                    //merge into current 实际上是和HEAD进行合并，产生一个新的提交
                    //x 对当前分支禁用这个选项，只有其他分支才能用
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.merge),
//                        textDesc = repoCurrentActiveBranchOrShortDetachedHashForShown.value+(if(curRepoIsDetached.value) "[Detached]" else ""),
//                    textDesc = replaceStringResList(stringResource(id = R.string.merge_branch1_into_branch2), listOf(getStrShorterThanLimitLength(curObjInPage.value.shortName), (if(curRepoIsDetached.value) "Detached HEAD" else getStrShorterThanLimitLength(repoCurrentActiveBranchOrShortDetachedHashForShown.value)))) ,
                        // if is current branch, merge upstream into, else merge into current
                        textDesc = if(curObjInPage.value.isCurrent) stringResource(R.string.upstream) else stringResource(R.string.merge_into_current),
//                        enabled = curObjInPage.value.isCurrent.not()
                    ) {
                        val curObjInPage = curObjInPage.value
                        val list = list.value

                        doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
                            initRebaseOrMergeDialog(
                                isRebase = false,
                                src = resolveMergeSrc(curObjInPage, list),
                                caller = curObjInPage,
                            )
                        }
                    }

                    if(UserUtil.isPro() && (dev_EnableUnTestedFeature || rebaseTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.rebase),
//                        textDesc = repoCurrentActiveBranchOrShortDetachedHashForShown.value+(if(curRepoIsDetached.value) "[Detached]" else ""),
                            textDesc = if(curObjInPage.value.isCurrent) stringResource(R.string.upstream) else stringResource(R.string.rebase_current_onto),
//                            enabled = curObjInPage.value.isCurrent.not()
                        ) {
                            val curObjInPage = curObjInPage.value
                            val list = list.value

                            doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
                                initRebaseOrMergeDialog(
                                    isRebase = true,
                                    src = resolveMergeSrc(curObjInPage, list),
                                    caller = curObjInPage
                                )
                            }
                        }
                    }

                    if(curObjInPage.value.type == Branch.BranchType.LOCAL) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.set_upstream),
                            enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                        ){
                            //这里不管是否有上游都一定显示弹窗并且无成功callback，所以直接调用init弹窗而不是先检查是否有上游再决定是执行任务还是显示弹窗的doTaskOrShowSetUpstream函数
                            initSetUpstreamDialog(curObjInPage.value, null)
                        }

                        if(proFeatureEnabled(branchListPagePublishBranchTestPassed)) {
                            BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.publish),
                                enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                            ){
                                doTaskOrShowSetUpstream(curObjInPage.value) {
                                    // force push with lease选项
                                    //默认设为upstream，会先查本地upstream的值，再fetch，再检查，若匹配，则推送，若用户改成别的引用则按用户改的来检查是否与fetch后的upstrem的最新提交匹配
                                    forcePush_expectedRefspecForLease.value = curObjInPage.value.upstream?.remoteBranchShortRefSpec ?: ""
                                    //默认不勾选 with lease
                                    forcePush_pushWithLease.value = false
                                    // 默认不勾选强制推送
                                    forcePublish.value = false

                                    //显示弹窗
                                    showPublishDialog.value = true
                                }
                            }
                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.diff_to_upstream),
                            enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                        ){
                            val curObj = curObjInPage.value

                            if(!curObj.isUpstreamValid()) {  // invalid upstream
                                Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_not_set_or_not_published))
                            }else {
                                val upOid = curObj.upstream?.remoteOid ?: ""
                                if(upOid.isBlank()) {  // invalid upstream oid
                                    Msg.requireShowLongDuration(activityContext.getString(R.string.upstream_oid_is_invalid))
                                }else {
                                    val commit1 = curObj.oidStr
                                    val commit2 = upOid

                                    if(commit1 == commit2) {  // local and upstream are the same, no need compare
                                        Msg.requireShow(activityContext.getString(R.string.both_are_the_same))
                                    }else {   // necessary things are ready and local vs upstream ain't same, then , should go to diff page

                                        //注意是 parentTreeOid to thisObj.treeOid，也就是 旧提交to新提交，相当于 git diff abc...def，比较的是旧版到新版，新增或删除或修改了什么，反过来的话，新增删除之类的也就反了
                                        goToTreeToTreeChangeList(
                                            title = activityContext.getString(R.string.compare_to_upstream),
                                            repoId = curRepo.value.id,
                                            commit1 = commit1,
                                            commit2 = commit2,
                                            commitForQueryParents = Cons.git_AllZeroOidStr,
                                        )
                                    }

                                }
                            }
                        }

                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.go_upstream),
                            enabled = curObjInPage.value.type == Branch.BranchType.LOCAL
                        ){
                            goToUpstream(curObjInPage.value)
                        }
                    }

                    if(proFeatureEnabled(resetByHashTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset),
                        ){
                            //                    resetDialogShortOid.value = curObjInPage.value.shortOidStr
                            resetDialogOid.value = curObjInPage.value.oidStr
                            //                    acceptHardReset.value = false
                            showResetDialog.value = true
                        }
                    }

                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.details)){
                        val suffix = "\n\n"
                        val sb = StringBuilder()
                        val it = curObjInPage.value
                        sb.append(activityContext.getString(R.string.name)).append(": ").append(it.shortName).append(suffix)
                        sb.append(activityContext.getString(R.string.full_name)).append(": ").append(it.fullName).append(suffix)
                        sb.append(activityContext.getString(R.string.last_commit)).append(": ").append(it.shortOidStr).append(suffix)
                        sb.append(activityContext.getString(R.string.last_commit_full_oid)).append(": ").append(it.oidStr).append(suffix)
                        sb.append(activityContext.getString(R.string.type)).append(": ").append(it.getTypeString(activityContext, false)).append(suffix)
                        if(it.type==Branch.BranchType.LOCAL) {
                            sb.append(activityContext.getString(R.string.upstream)).append(": ").append(it.getUpstreamShortName(activityContext)).append(suffix)
                            if(it.isUpstreamValid()) {
                                sb.append(activityContext.getString(R.string.upstream_full_name)).append(": ").append(it.getUpstreamFullName(activityContext)).append(suffix)
                                sb.append(activityContext.getString(R.string.status)).append(": ").append(it.getAheadBehind(activityContext, false)).append(suffix)
                            }
                        }

                        if(it.isSymbolic) {
                            sb.append(activityContext.getString(R.string.symbolic_target)).append(": ").append(it.symbolicTargetShortName).append(suffix)
                            sb.append(activityContext.getString(R.string.symbolic_target_full_name)).append(": ").append(it.symbolicTargetFullName).append(suffix)
                        }

                        sb.append(activityContext.getString(R.string.other)).append(": ").append(it.getOther(activityContext, false)).append(suffix)



                        sb.append(Cons.flagStr).append(": ").append(it.getTypeString(activityContext, true)).append("; ${it.getAheadBehind(activityContext, true)}").append("; ${it.getOther(activityContext, true)}").append(suffix)

                        detailsString.value = sb.removeSuffix(suffix).toString()

                        showDetailsDialog.value = true
                    }

                    if(curObjInPage.value.type == Branch.BranchType.LOCAL && proFeatureEnabled(branchRenameTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.rename)) {
                            nameForRenameDialog.value = curObjInPage.value.shortName
                            forceForRenameDialog.value= false
                            showRenameDialog.value = true
                        }
                    }

                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger(),
                        //只能删除非当前分支，不过如果是detached，所有分支都能删。这个不做检测了，因为就算界面出了问题，用户针对当前分支执行了删除操作，libgit2也会抛异常，所以还是不会被执行。
                        enabled = curObjInPage.value.isCurrent.not()
                    ){
                        // onClick()
//                        该写删除了，别忘了删除上游的逻辑和提示删除远程分支需要联网
                        // 弹出确认框，删除分支
                        if(curObjInPage.value.type == Branch.BranchType.REMOTE) {
                            curRequireDelRemoteNameIsAmbiguous.value = curObjInPage.value.isRemoteNameAmbiguous()
                            userSpecifyRemoteName.value=""
                            pushCheckBoxForRemoteBranchDelDialog.value = false
                            showRemoteBranchDelDialog.value = true
                        }else {
                            //如果没上游或上游无效(例如没发布或者配置文件中没设置相关字段)，禁用删除上游勾选框，否则启用
//                        isUpstreamValidForDelLocalBranch.value = curObjInPage.value.isUpstreamValid()
                            delUpstreamToo.value = false
                            delUpstreamPush.value = false
                            showLocalBranchDelDialog.value = true

                        }
                    }

                }
            }


            if(list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    if(isInitLoading.value) {
                        Text(text = stringResource(R.string.loading))
                    }else {
                        Row {
                            Text(text = stringResource(R.string.item_list_is_empty))
                        }

                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Downloading,
                                tooltipText = stringResource(R.string.fetch),
                            ) {
                                initFetchAllDialog()
                            }

                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText =  stringResource(R.string.create),
                            ) {
                                initCreateBranchDialog()
                            }
                        }
                    }
                }

            }else {
                //根据关键字过滤条目
                val keyword = filterKeyword.value.text  //关键字
                val enableFilter = filterModeActuallyEnabled(filterOn = filterModeOn.value, keyword = keyword)

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
                    match = { idx: Int, it: BranchNameAndTypeDto ->
                        it.fullName.contains(keyword, ignoreCase = true)
                                || it.oidStr.contains(keyword, ignoreCase = true)
                                || it.symbolicTargetFullName.contains(keyword, ignoreCase = true)
                                || it.getUpstreamShortName(activityContext).contains(keyword, ignoreCase = true)

                                //如果加这个，一搜"remote"会把关联了远程分支的本地分支也显示出来，因为这些分支的上游完整名是 "refs/remotes/....."，其中包含了关键字"remote"
                                // || it.getUpstreamFullName(activityContext).contains(k, ignoreCase = true)

                                || it.getOther(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getOther(activityContext, true).contains(keyword, ignoreCase = true)
                                || it.getTypeString(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getTypeString(activityContext, true).contains(keyword, ignoreCase = true)
                                || it.getAheadBehind(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getAheadBehind(activityContext, true).contains(keyword, ignoreCase = true)
                    }
                )


                val listState = if(enableFilter) filterListState else listState
//        if(enableFilter) {  //更新filter列表state
//            filterListState.value = listState
//        }

                //更新是否启用filter
                enableFilterState.value = enableFilter


                MyLazyColumn (
                    contentPadding = contentPadding,
                    list = list,
                    listState = listState,
                    requireForEachWithIndex = true,
                    requirePaddingAtBottom = true,
                    forEachCb = {},
                ){idx, it->
                    //长按会更新curObjInPage为被长按的条目
                    BranchItem(
                        showBottomSheet = showBottomSheet,
                        curObjFromParent = curObjInPage,
                        idx = idx,
                        thisObj = it,
                        requireBlinkIdx = requireBlinkIdx,
                        lastClickedItemKey = lastClickedItemKey,
                        pageRequest = pageRequest
                    ) {  //onClick
                        //点击条目跳转到分支的提交历史记录页面
                        goToCommitListScreen(
                            repoId = repoId,
                            fullOid = it.oidStr,
                            shortBranchName = it.shortName,
                            isHEAD = it.isCurrent,
                            from = CommitListFrom.BRANCH,
                        )
                    }

                    MyHorizontalDivider()
                }

            }

        }

    }

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {
        try {
//            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.loading)) {
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {
                list.value.clear()  //先清一下list，然后可能添加也可能不添加

                if(!repoId.isNullOrBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb!=null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            curRepoIsDetached.value = repo.headDetached()
                            //更新用来显示的值
                            repoCurrentActiveBranchOrShortDetachedHashForShown.value = if(curRepoIsDetached.value) repoFromDb.lastCommitHashShort?:"" else repoFromDb.branch;
                            if(!curRepoIsDetached.value) { //分支长引用名，只有在非detached时，才用到这个变量
                                repoCurrentActiveBranchFullRefForDoAct.value = Libgit2Helper.resolveHEAD(repo)?.name()?:""
                            }
                            //更新实际用来执行操作的oid
                            repoCurrentActiveBranchOrDetachedHeadFullHashForDoAct.value = repo.head()?.id().toString()
                            val listAllBranch = Libgit2Helper.getBranchList(repo)
                            list.value.addAll(listAllBranch)
//                            list.requireRefreshView()

                            //更新remote列表，设置upstream时用
                            val remoteList = Libgit2Helper.getRemoteList(repo)
                            upstreamRemoteOptionsList.value.clear()
                            upstreamRemoteOptionsList.value.addAll(remoteList)

//                            upstreamRemoteOptionsList.requireRefreshView()

                        }
                    }
                }

                triggerReFilter((filterResultNeedRefresh))
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: "+e.stackTraceToString())
//            ("LaunchedEffect: job cancelled")
        }
    }

    //compose被销毁时执行的副作用。准确来说，这个组件会在它自己被销毁时执行某些操作。放到某组件根目录下，就间接实现了某个组件被销毁时执行某些操作
//    DisposableEffect(Unit) {
////        ("DisposableEffect: entered main")
//        onDispose {
//
//        }
//    }
}
