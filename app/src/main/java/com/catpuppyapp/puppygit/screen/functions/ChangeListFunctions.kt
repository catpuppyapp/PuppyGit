package com.catpuppyapp.puppygit.screen.functions

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.AppContainer
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.PatchFile
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.git.Upstream
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.pref.PrefUtil
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.showErrAndSaveLog
import com.github.git24j.core.Repository
import com.github.git24j.core.Reset
import com.github.git24j.core.Tree

private const val TAG = "ChangeListFunctions"

object ChangeListFunctions {

    //变量1是请求显示输入提交信息的弹窗，变量2是提交信息。是否显示弹窗只受变量1控制，只要变量1为true，就会显示弹窗，为false就不会显示弹窗，无论变量2有没有值。
    suspend fun doCommit(
        requireShowCommitMsgDialog:Boolean,
        cmtMsg:String,
        requireCloseBottomBar:Boolean,
//        requireDoSync:Boolean,
        curRepoFromParentPage:RepoEntity,
        refreshChangeList:(RepoEntity) -> Unit,

        //因为会从配置文件读取用户名和邮箱，所以这两个变量其实没用了
        username:MutableState<String>,
        email:MutableState<String>,

        requireShowToast:(String)->Unit,
        pleaseSetUsernameAndEmailBeforeCommit:String,
        initSetUsernameAndEmailDialog:(curRepo:RepoEntity, callback:(()->Unit)?)->Unit,
        amendCommit: MutableState<Boolean>,
        overwriteAuthor: MutableState<Boolean>,
        showCommitMsgDialog: MutableState<Boolean>,
        repoState:MutableIntState,
        activityContext:Context,
        loadingText:MutableState<String>,
        repoId:String,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        fromTo:String,
        itemList:List<StatusTypeEntrySaver>?,
        successCommitStrRes:String,
        indexIsEmptyForCommitDialog:MutableState<Boolean>,
        commitBtnTextForCommitDialog:MutableState<String>,
//        showPushForCommitDialog:MutableState<Boolean>
    ):Boolean{
        val settings = SettingsUtil.getSettingsSnapshot()

//        commitBtnTextForCommitDialog.value = appContext.getString(if(requireDoSync) R.string.sync else R.string.commit)
//        showPushForCommitDialog.value = !requireDoSync

        commitBtnTextForCommitDialog.value = activityContext.getString(R.string.commit)
//        showPushForCommitDialog.value = true


        indexIsEmptyForCommitDialog.value = false // will update this after check
        //可以用context获取string resource
//        requireShowToast("test context getsTring:"+appContext.getString(R.string.n_files_staged))
//            MyLog.d(TAG, "#doCommit, start")

        // commit
        //检查选中列表是否为空，这个需要 doStage检查，commit不管
//            if(selectedListIsEmpty()) {
//                MyLog.d(TAG, "#doCommit, selected item list is empty")
//                requireShowToast(noItemSelectedStrRes)
//                return@doCommit false
//            }

        //更新这个变量，供输入提交信息后的回调用来判断接下来执行提交还是sync
//        Cache.set(Cache.Key.changeListInnerPage_RequireDoSyncAfterCommit, requireDoSync)

        //执行commit
        Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
            //检查是否存在冲突条目，有可能已经stage了，就不存在了，就能提交，否则，不能提交
            val readyCreateCommit = Libgit2Helper.isReadyCreateCommit(repo, activityContext)
            if(readyCreateCommit.hasError()) {
                //20240819 支持index为空时创建提交，因为目前实现了amend而amend可仅修改之前的提交且不提交新内容，所以index非空检测就不能强制了，显示个提示就够了，但仍允许提交
                if(readyCreateCommit.code == Ret.ErrCode.indexIsEmpty) {
                    // allow create empty commit, but show a warning
                    // (only show warning when repostate==NONE and amend==false, when MERGE/CHERRYPICK/REBASE,
                    // will not show warning, cuz MERGE allow empty is necessary,
                    // CHERRYPICK and REABSE will try ignore empty commit when continue)
                    indexIsEmptyForCommitDialog.value = true

                }else {  //若错误不是index为空，则结束提交
                    //显示错误信息
                    Msg.requireShowLongDuration(readyCreateCommit.msg)
                    //若有错，必刷新页面
                    refreshChangeList(curRepoFromParentPage)
                    return@doCommit false
                }

            }

            //执行到这说明通过检测了，可创建提交了

            //检查仓库配置文件是否有email，若没有，检查全局git配置项中是否有email，若没有，询问是否现在设置，三个选项：1设置全局email，2设置仓库email，点取消则放弃设置并终止提交
            val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)


            //没必要执行这个判断，正常流程应该是存到全局或仓库配置文件里，然后在上面的代码就能取出用户名和邮箱，若执行到这还没有效邮箱，应该弹窗让用户设置
//                //如果仓库和全局都没username和email，尝试从状态变量获取，状态变量可能有用户在弹窗输入的username和email，不过，如果成功保存了username和email，应该不会执行到这里，这段代码其实是可有可无的
//                if(usernameFromConfig.isBlank() || emailFromConfig.isBlank()) {
//                    usernameFromConfig = username.value
//                    emailFromConfig = email.value
//                }

            //废弃，20250307之后会在初始化用户名和邮箱弹窗前更新状态：更新下状态，这样用户在输入的时候如果之前设置过用户名或密码，就不用重新输了
//            username.value = usernameFromConfig
//            email.value = emailFromConfig


            //如果仓库、全局、状态变量(用户刚输入的)都没有username和email，显示弹窗请求用户输入
            if(usernameFromConfig.isBlank() || emailFromConfig.isBlank()){
                MyLog.d(TAG, "#doCommit, username and email not set, will show dialog for set them")
                //显示提示信息
                requireShowToast(pleaseSetUsernameAndEmailBeforeCommit)
                //弹窗提示没用户名和邮箱，询问是否设置，用户设置好再重新调用此方法即可
                initSetUsernameAndEmailDialog(curRepoFromParentPage) {
                    //这里因为还要显示提交信息的弹窗，所以没必要在这显示loading，应由那个弹窗的成功回调来显示
                    doJobThenOffLoading {
                        doCommit(
                            requireShowCommitMsgDialog = requireShowCommitMsgDialog,
                            cmtMsg = cmtMsg,
                            requireCloseBottomBar = requireCloseBottomBar,
                            curRepoFromParentPage = curRepoFromParentPage,
                            refreshChangeList = refreshChangeList,
                            username = username,
                            email = email,
                            requireShowToast = requireShowToast,
                            pleaseSetUsernameAndEmailBeforeCommit = pleaseSetUsernameAndEmailBeforeCommit,
                            initSetUsernameAndEmailDialog = initSetUsernameAndEmailDialog,
                            amendCommit = amendCommit,
                            overwriteAuthor = overwriteAuthor,
                            showCommitMsgDialog = showCommitMsgDialog,
                            repoState = repoState,
                            activityContext = activityContext,
                            loadingText = loadingText,
                            repoId = repoId,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                            fromTo = fromTo,
                            itemList = itemList,
                            successCommitStrRes = successCommitStrRes,
                            indexIsEmptyForCommitDialog = indexIsEmptyForCommitDialog,
                            commitBtnTextForCommitDialog = commitBtnTextForCommitDialog
                        )
                    }
                }

                return@doCommit false
            }

            //提交信息
            //执行到这，用户名和邮箱就有了，接下来获取提交信息
            var commitMsgWillUse = ""
            //还没设置过提交信息，显示弹窗，请求用户输入提交信息
            if(requireShowCommitMsgDialog) {  //显示弹窗，结束
//                    MyLog.d(TAG, "#doCommit, require show commit msg dialog")
                amendCommit.value = false
                overwriteAuthor.value = false

                showCommitMsgDialog.value = true
                return@doCommit false
            }else { // 已经请求过输入提交信息了，这里直接获取
                commitMsgWillUse = cmtMsg
            }

            //把变量都取出来，准备执行操作
            val amendCommit = amendCommit.value
            val overwriteAuthor = overwriteAuthor.value
            val username = usernameFromConfig
            val email = emailFromConfig
            //20240815:改成由createCommit生成提交信息了
            //如果用户输入的commitMsg全是空白字符或者没填，将会自动生成一个
//                if(tmpCommitMsg.isBlank()) {
//                    val genCommitMsgRet = Libgit2Helper.genCommitMsg(repo,
//                        //如果是在changelist页面，需要查询实际index条目来生成提交信息不然只包含你选中的文件列表不包含已经在index区的文件列表，这时传null，方法内部会去查；如果是在index页面，直接把当前所有条目传过去即可，就不用查了
//                        if(fromTo == Cons.gitDiffFromHeadToIndex) itemList.value else null
//                    )
//                    val cmtmsg = genCommitMsgRet.data
//                    if(genCommitMsgRet.hasError() || cmtmsg.isNullOrBlank()) {
//                        MyLog.d(TAG, "#doCommit, genCommitMsg Err, commit abort!")
//                        //显示提示信息
//                        requireShowToast(appContext.getString(R.string.gen_commit_msg_err_commit_abort))
//
//                        //出错，刷新页面
//                        changeStateTriggerRefreshPage(needRefreshChangeListPage)
//                        return@doCommit false
//                    }
//
//                    //执行到这说明生成提交信息成功，取出提交信息
//                    tmpCommitMsg = cmtmsg
//                }

            //开始创建提交
            MyLog.d(TAG, "#doCommit, before createCommit")
            //do commit
            val ret = if(repoState.intValue== Repository.StateT.REBASE_MERGE.bit) {    //执行rebase continue
                loadingText.value = activityContext.getString(R.string.rebase_continue) + Cons.oneChar3dots

                Libgit2Helper.rebaseContinue(
                    repo,
                    activityContext,
                    username,
                    email,
                    commitMsgForFirstCommit = commitMsgWillUse,
                    overwriteAuthorForFirstCommit = overwriteAuthor,
                    settings = settings
                )

            }else if(repoState.intValue == Repository.StateT.CHERRYPICK.bit) {
                loadingText.value = activityContext.getString(R.string.cherrypick_continue)+Cons.oneChar3dots

                Libgit2Helper.cherrypickContinue(
                    activityContext,
                    repo,
                    msg=commitMsgWillUse,
                    username = usernameFromConfig,
                    email=emailFromConfig,
                    autoClearState = false,  //这里后续会检查若操作成功会清状态，所以此处传false，不然会清两次状态
                    overwriteAuthor = overwriteAuthor,
                    settings=settings
                )
            }else {
                loadingText.value = activityContext.getString(R.string.committing)

                Libgit2Helper.createCommit(
                    repo = repo,
                    msg = commitMsgWillUse,
                    username = username,
                    email = email,

                    //如果是index页面的条目列表，直接使用，否则无视并强制传null触发重查index条目，不过如果从index页面调用此方法又想触发重查，也可直接在调用此函数时传null给itemList
//                    indexItemList = if(fromTo == Cons.gitDiffFromHeadToIndex) itemList else null,
                    //20250218: 因为现在在diff页面或者filehistory有可能影响到index的itemlist，所以就算itemlist来自index页面我也无法确保那个列表就一定靠谱，所以这里传null，重查下
                    indexItemList = null,

                    amend = amendCommit,
                    overwriteAuthorWhenAmend = overwriteAuthor,
                    settings = settings,
                    cleanRepoStateIfSuccess = false,
                )

            }

            if(ret.hasError()) {  //创建commit失败
                MyLog.d(TAG, "#doCommit, createCommit failed, has error: "+ret.msg)

                Msg.requireShowLongDuration(ret.msg)

                //显示的时候只显示简短错误信息，例如"请先解决冲突！"，存的时候存详细点，存上什么操作导致的错误，例如：“merge continue err:请先解决冲突”
                val errPrefix= activityContext.getString(R.string.commit_err)
                createAndInsertError(repoId, "$errPrefix: ${ret.msg}")

                //若出错，必刷新页面
                if(requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }else {
                    //刷新页面，之所以放到else里是因为如果请求关闭底栏，关闭时会自动刷新，如果不放else里就重复刷新了，无意义
                    refreshChangeList(curRepoFromParentPage)
                }
                return@doCommit false
            }else {  //创建成功
                MyLog.d(TAG, "#doCommit, createCommit success")

                //如果没stage所有冲突条目，不能执行commit，函数入口有判断，所以如果能执行到这里，肯定是stage了所有冲突条目
                Libgit2Helper.cleanRepoState(repo)  //清理仓库状态，例如存在冲突的时候如果创建完不清理状态，会一直处于merge state，就是pc git 显示 merging的情况


                //更新仓库状态变量，要不然标题可能还是merge时的红色
                repoState.intValue = repo.state()?.bit?: Cons.gitRepoStateInvalid  //如果state是null，返回一个无效值

                // 更新db
                val repoDb = AppModel.dbContainer.repoRepository
                val shortNewCommitHash = ret.data.toString().substring(Cons.gitShortCommitHashRange)
                //更新db
                repoDb.updateCommitHash(
                    repoId=curRepoFromParentPage.id,
                    lastCommitHash = shortNewCommitHash,
                )
                // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
                repoDb.updateLastUpdateTime(curRepoFromParentPage.id, getSecFromTime())


                requireShowToast(successCommitStrRes)
                //操作成功不一定刷新页面，因为可能commit和其他操作组合，一般在最后一个操作完成后才刷新页面，例如commit then sync，应由最后的sync负责关底栏和刷新页面
                if(requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }
                return@doCommit true
            }


            //弹窗确认是否提交，有个勾选框“我想编辑提交信息”，勾选显示一个输入框让用户输入提交信息，否则自动生成提交信息(文案提醒用户不输入就自动生成)
            //获取提交信息后，先stage选中的文件，然后再创建commit
        }
        //设置好email后，弹窗，提示将创建提交，是否确定，有个勾选框“我想填写提交信息”，勾选显示一个输入框可填写提交信息，不勾选将自动生成提交信息“更新了n个文件，列举前3个，最后加句 commit msg generated by PuppyGit”
        //创建提交

        //提示操作完成
//        val msg = replaceStringResList(revertStrRes,
//            listOf(pathspecList.size.toString(),untrakcedFileList.size.toString()))
//        bottomBarActDoneCallback(msg)
//        showRevertAlert.value=false

    }

    suspend fun doFetch(
        remoteNameParam:String?,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        loadingText:MutableState<String>,
        dbContainer:AppContainer,

    ):Boolean{   //参数的remoteNameParam如果有效就用参数的，否则自己查当前head分支对应的remote
        //x 废弃，逻辑已经改了) 执行isReadyDoSync检查之前要先do fetch，想象一下，如果远程创建了一个分支，正好和本地的关联，但如果我没先fetch，那我检查时就get不到那个远程分支是否存在，然后就会先执行push，但可能远程仓库已经领先本地了，所以push也可能失败，但如果先fetch，就不会有这种问题了
        Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
            //fetch成功返回true，否则返回false
            try {
                var remoteName = remoteNameParam
                if(remoteName == null || remoteName.isBlank()) {
                    val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)
                    val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
                    remoteName = upstream.remote
                    if(remoteName == null || remoteName.isBlank()) {  //fetch不需合并，只需remote有效即可，所以只检查remote
                        requireShowToast(activityContext.getString(R.string.err_upstream_invalid_plz_try_sync_first))
                        return@doFetch false
                    }
                }

                loadingText.value = activityContext.getString(R.string.fetching)

                //执行到这，upstream的remote有效，执行fetch
                //            只fetch当前分支关联的remote即可，获取仓库当前remote和credential的关联，组合起来放到一个pair里，pair放到一个列表里，然后调用fetch
                val credential = Libgit2Helper.getRemoteCredential(
                    dbContainer.remoteRepository,
                    dbContainer.credentialRepository,
                    curRepoFromParentPage.id,
                    remoteName,
                    trueFetchFalsePush = true
                )
                Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, curRepoFromParentPage)

                // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
                val repoDb = AppModel.dbContainer.repoRepository
                repoDb.updateLastUpdateTime(curRepoFromParentPage.id, getSecFromTime())


                return@doFetch true
            }catch (e:Exception) {
                //记录到日志
                //显示提示
                //保存数据库(给用户看的，消息尽量简单些)
                showErrAndSaveLog(TAG, "#doFetch() err: "+e.stackTraceToString(), "fetch err: "+e.localizedMessage, requireShowToast, curRepoFromParentPage.id)

                return@doFetch false
            }
        }
    }

    suspend fun doMerge(
        requireCloseBottomBar:Boolean,
        upstreamParam: Upstream?,
        showMsgIfHasConflicts:Boolean,
        trueMergeFalseRebase:Boolean,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        loadingText:MutableState<String>,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
    ):Boolean {
        try {
            val settings = SettingsUtil.getSettingsSnapshot()
            //这的repo不能共享，不然一释放就要完蛋了，这repo不是rc是box单指针
            Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
                var upstream = upstreamParam
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {  //如果调用者没传有效的upstream，查一下
                    val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  //获取当前分支短名，例如 main
                    upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  //获取当前分支的上游，例如 remote=origin 和 merge=refs/heads/main，参见配置文件 branch.yourbranchname.remote 和 .merge 字段
                    //如果查出的upstream还是无效，终止操作
                    if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                        requireShowToast(activityContext.getString(R.string.err_upstream_invalid_plz_try_sync_first))
                        return false
                    }
                }

                // doMerge
                val remoteRefSpec = Libgit2Helper.getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(
                    upstream!!.remote,
                    upstream.branchRefsHeadsFullRefSpec
                )
                MyLog.d(TAG, "doMerge: remote="+upstream.remote+", branchFullRefSpec=" + upstream.branchRefsHeadsFullRefSpec +", trueMergeFalseRebase=$trueMergeFalseRebase")
                val (usernameFromConfig, emailFromConfig) = Libgit2Helper.getGitUsernameAndEmail(repo)

                //如果用户名或邮箱无效，无法创建commit，merge无法完成，所以，直接终止操作
                if(Libgit2Helper.isUsernameAndEmailInvalid(usernameFromConfig,emailFromConfig)) {
                    requireShowToast(activityContext.getString(R.string.plz_set_username_and_email_first))
                    return false
                }

                val mergeResult = if(trueMergeFalseRebase) {
                    loadingText.value = activityContext.getString(R.string.merging)

                    Libgit2Helper.mergeOneHead(
                        repo,
                        remoteRefSpec,
                        usernameFromConfig,
                        emailFromConfig,
                        settings = settings
                    )
                }else {
                    loadingText.value = activityContext.getString(R.string.rebasing)

                    Libgit2Helper.mergeOrRebase(
                        repo,
                        targetRefName = remoteRefSpec,
                        username = usernameFromConfig,
                        email = emailFromConfig,
                        requireMergeByRevspec = false,
                        revspec = "",
                        trueMergeFalseRebase = false,
                        settings = settings
                    )
                }

                if (mergeResult.hasError()) {
                    //检查是否存在冲突条目
                    //如果调用者想自己判断是否有冲突，可传showMsgIfHasConflicts为false
                    if (mergeResult.code == Ret.ErrCode.mergeFailedByAfterMergeHasConfilts) {
                        if(showMsgIfHasConflicts){
                            requireShowToast(activityContext.getString(R.string.has_conflicts))

//                            if(trueMergeFalseRebase) {
//                                requireShowToast(appContext.getString(R.string.merge_has_conflicts))
//                            }else {
//                                requireShowToast(appContext.getString(R.string.rebase_has_conflicts))
//                            }

                        }
                    }else {
                        //显示错误提示
                        requireShowToast(mergeResult.msg)
                    }

                    //记到数据库error日志
                    createAndInsertError(curRepoFromParentPage.id, mergeResult.msg)
                    //关闭底栏，如果需要的话
                    if (requireCloseBottomBar) {
                        bottomBarActDoneCallback("", curRepoFromParentPage)
                    }
                    return false
                }

                //执行到这就合并成功了

                //清下仓库状态
                Libgit2Helper.cleanRepoState(repo)

                //更新db显示成功通知
                Libgit2Helper.updateDbAfterMergeSuccess(mergeResult, activityContext, curRepoFromParentPage.id, requireShowToast, trueMergeFalseRebase)

                //关闭底栏，如果需要的话
                if (requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }

                return true
            }
        }catch (e:Exception) {

            //关闭底栏，如果需要的话
            if (requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepoFromParentPage)
            }

            //log
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "#doMerge(trueMergeFalseRebase=$trueMergeFalseRebase) err: "+e.stackTraceToString(),
                showMsg = "${if(trueMergeFalseRebase) "merge" else "rebase"} err: "+e.localizedMessage,
                showMsgMethod = requireShowToast,
                repoId = curRepoFromParentPage.id,
            )

            return false
        }

    }

    suspend fun doPush(
        requireCloseBottomBar:Boolean,
        upstreamParam:Upstream?,
        force:Boolean=false,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        loadingText:MutableState<String>,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        dbContainer: AppContainer,
        forcePush_pushWithLease: Boolean = false,
        forcePush_expectedRefspecForLease:String = "",
        commitOnPush: Boolean = PrefUtil.getGlobalGitConfigCommitOnPush(AppModel.realAppContext),
    ) : Boolean {
        try {
//            MyLog.d(TAG, "#doPush: start")
            Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
                if(repo.headDetached()) {
                    requireShowToast(activityContext.getString(R.string.push_failed_by_detached_head))
                    return@doPush false
                }

                var upstream:Upstream? = upstreamParam
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {  //如果调用者没传有效的upstream，查一下
                    val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  //获取当前分支短名，例如 main
                    upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  //获取当前分支的上游，例如 remote=origin 和 merge=refs/heads/main，参见配置文件 branch.yourbranchname.remote 和 .merge 字段
                    //如果查出的upstream还是无效，终止操作
                    if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                        requireShowToast(activityContext.getString(R.string.err_upstream_invalid_plz_try_sync_first))
                        return@doPush false
                    }
                }

                MyLog.d(TAG, "#doPush: upstream.remote="+upstream!!.remote+", upstream.branchFullRefSpec="+upstream!!.branchRefsHeadsFullRefSpec)

                //如果是force push with lease，检查下提交是否和期望匹配
                if(force && forcePush_pushWithLease) {
                    loadingText.value = activityContext.getString(R.string.checking)

                    Libgit2Helper.forcePushLeaseCheckPassedOrThrow(
                        repoEntity = curRepoFromParentPage,
                        repo = repo,
                        forcePush_expectedRefspecForLease = forcePush_expectedRefspecForLease,
                        upstream = upstream,
                    )

                }

                // 执行到这里，必定有上游
                // 检查是否需要先提交
                if(commitOnPush) {
                    val settings = SettingsUtil.getSettingsSnapshot()

                    val (username, email) = Libgit2Helper.getGitUsernameAndEmail(repo)

                    if (username == null || username.isBlank() || email == null || email.isBlank()) {
                        throw RuntimeException("push err(from changelist): auto commit err: username or email invalid")
                    } else {
                        //检查是否存在冲突，如果存在，将不会创建提交
                        if (Libgit2Helper.hasConflictItemInRepo(repo)) {
                            throw RuntimeException("push err(from changelist): auto commit enabled but have conflicts, please resolve conflicts first")
                        } else {
                            loadingText.value = activityContext.getString(R.string.committing)

                            // 有username 和 email，且无冲突

                            // stage worktree changes
                            Libgit2Helper.stageAll(repo, curRepoFromParentPage.id)


                            //如果index不为空，则创建提交
                            if (!Libgit2Helper.indexIsEmpty(repo)) {
                                val ret = Libgit2Helper.createCommit(
                                    repo = repo,
                                    msg = "",  // empty to auto generate
                                    cmtMsgPrefix = "",  // prefix for auto generated msg
                                    username = username,
                                    email = email,
                                    indexItemList = null,
                                    amend = false,
                                    overwriteAuthorWhenAmend = false,
                                    settings = settings,
                                    cleanRepoStateIfSuccess = true,
                                )

                                if (ret.hasError()) {
                                    throw RuntimeException("commit on push error(from changelist): ${ret.msg}")
                                } else if(ret.data != null){
                                    //更新上游本地oid为最新值
                                    upstream.localOid = ret.data!!.toString()
                                }
                            }
                        }
                    }
                }

                loadingText.value = activityContext.getString(if(force) R.string.force_pushing else R.string.pushing)

                // push
                val credential = Libgit2Helper.getRemoteCredential(
                    dbContainer.remoteRepository,
                    dbContainer.credentialRepository,
                    curRepoFromParentPage.id,
                    upstream!!.remote,
                    trueFetchFalsePush = false
                )

                Libgit2Helper.pushSingleBranch(repo, upstream, credential, force)

                // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
                val repoDb = AppModel.dbContainer.repoRepository
                repoDb.updateLastUpdateTime(curRepoFromParentPage.id, getSecFromTime())


                //关闭底栏，如果需要的话
                if (requireCloseBottomBar) {
                    bottomBarActDoneCallback("", curRepoFromParentPage)
                }
                return@doPush true
            }
        }catch (e:Exception) {

            //关闭底栏，如果需要的话
            if (requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepoFromParentPage)
            }

            //log
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "#doPush(force=$force) err: "+e.stackTraceToString(),
                showMsg = "${if(force) "Push(Force)" else "Push"} error: "+e.localizedMessage,
                showMsgMethod = requireShowToast,
                repoId = curRepoFromParentPage.id
            )

            return@doPush false
        }

    }

    suspend fun doSync(
        loadingOn:(String)->Unit,
        loadingOff:()->Unit,
        requireCloseBottomBar:Boolean,
        trueMergeFalseRebase:Boolean,
        curRepoFromParentPage:RepoEntity,
        requireShowToast:(String)->Unit,
        activityContext:Context,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        plzSetUpStreamForCurBranch:String,
        initSetUpstreamDialog:(remoteList: List<String>, curBranchShortName: String, curBranchFullName: String, onOkText: String, successCallback: (()->Unit)?) -> Unit,
        loadingText:MutableState<String>,
        dbContainer:AppContainer,
    ) {
        Repository.open(curRepoFromParentPage.fullSavePath).use { repo ->
            if(repo.headDetached()) {
                requireShowToast(activityContext.getString(R.string.sync_failed_by_detached_head))
                return@doSync
            }


            //检查是否有upstream，如果有，do fetch do merge，然后do push,如果没有，请求设置upstream，然后do push
            val hasUpstream = Libgit2Helper.isBranchHasUpstream(repo)
            val headRef = Libgit2Helper.resolveHEAD(repo) ?: throw RuntimeException("resolve HEAD failed")
            val curBranchShortName = headRef.shorthand()
            val curBranchFullName = headRef.name()
            if (!hasUpstream) {  //不存在上游，弹窗设置一下
                requireShowToast(plzSetUpStreamForCurBranch)  //显示请设置上游的提示

                //显示弹窗
                initSetUpstreamDialog(Libgit2Helper.getRemoteList(repo), curBranchShortName, curBranchFullName, activityContext.getString(R.string.save_and_sync)) {
                    //设置上游成功的callback
                    //这里得设置loading，因为sync一般是顶级操作，它不开loading没人帮它开
                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.syncing)) {
                        doSync(
                            loadingOn = loadingOn,
                            loadingOff = loadingOff,
                            requireCloseBottomBar = requireCloseBottomBar,
                            trueMergeFalseRebase = trueMergeFalseRebase,
                            curRepoFromParentPage = curRepoFromParentPage,
                            requireShowToast = requireShowToast,
                            activityContext = activityContext,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                            plzSetUpStreamForCurBranch = plzSetUpStreamForCurBranch,
                            initSetUpstreamDialog = initSetUpstreamDialog,
                            loadingText = loadingText,
                            dbContainer = dbContainer
                        )
                    }

                    Unit
                }

            }else {  //存在上游
                try {
                    loadingText.value = activityContext.getString(R.string.syncing)

                    //取出上游
                    val upstream = Libgit2Helper.getUpstreamOfBranch(repo, curBranchShortName)
                    val fetchSuccess = doFetch(
                        upstream.remote,
                        curRepoFromParentPage = curRepoFromParentPage,
                        requireShowToast = requireShowToast,
                        activityContext = activityContext,
                        loadingText = loadingText,
                        dbContainer = dbContainer
                    )
                    if(!fetchSuccess) {
                        requireShowToast(activityContext.getString(R.string.fetch_failed))
                        if(requireCloseBottomBar) {
                            bottomBarActDoneCallback("", curRepoFromParentPage)
                        }
                        return@doSync
                    }

                    //检查配置文件设置的remote和branch是否实际存在，
                    val isUpstreamExistOnLocal = Libgit2Helper.isUpstreamActuallyExistOnLocal(
                        repo,
                        upstream.remote,
                        upstream.branchRefsHeadsFullRefSpec
                    )

                    //如果存在上游，执行merge 若没冲突则push，否则终止操作直接return
                    //如果不存在上游，只需执行push，相当于pc的 git push with -u (--set-upstream)，但上面已经把remote和branch设置到gitconfig里了(执行到这里都能取出来upstream所以肯定设置过了，在 对话框或分支管理页面设置的)，所以这里正常推送即可，不用再设置什么
                    MyLog.d(TAG, "@doSync: isUpstreamExistOnLocal="+isUpstreamExistOnLocal)
                    if(isUpstreamExistOnLocal) {  //上游分支在本地存在
                        // doMerge
                        val mergeSuccess = doMerge(
                            requireCloseBottomBar = false,
                            upstreamParam = upstream,
                            showMsgIfHasConflicts = false,
                            trueMergeFalseRebase = trueMergeFalseRebase,
                            curRepoFromParentPage = curRepoFromParentPage,
                            requireShowToast = requireShowToast,
                            activityContext = activityContext,
                            loadingText = loadingText,
                            bottomBarActDoneCallback = bottomBarActDoneCallback,
                        )

                        if(!mergeSuccess) {  //merge 失败，终止操作
                            //如果merge完存在冲突条目，就不要执行push了
                            if(Libgit2Helper.hasConflictItemInRepo(repo)) {  //检查失败原因是否是存在冲突，若是则显示提示
                                requireShowToast(activityContext.getString(R.string.has_conflicts))
                            }

                            if(requireCloseBottomBar) {
                                bottomBarActDoneCallback("", curRepoFromParentPage)
                            }

                            return@doSync
                        }
                    }

                    //如果执行到这，要么不存在上游，直接push(新建远程分支)；要么存在上游，但fetch/merge成功完成，需要push，所以，执行push
                    //doPush
                    val pushSuccess = doPush(
                        requireCloseBottomBar = false,
                        upstreamParam = upstream,
                        force = false,
                        curRepoFromParentPage = curRepoFromParentPage,
                        requireShowToast = requireShowToast,
                        activityContext = activityContext,
                        loadingText = loadingText,
                        bottomBarActDoneCallback = bottomBarActDoneCallback,
                        dbContainer = dbContainer
                    )
                    if(pushSuccess) {
                        requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.sync_merge_success else R.string.sync_rebase_success))
                    }else {
                        requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.sync_merge_failed else R.string.sync_rebase_failed))
                    }

                    if(requireCloseBottomBar) {
                        bottomBarActDoneCallback("", curRepoFromParentPage)
                    }
                }catch (e:Exception) {

                    //close if require
                    if(requireCloseBottomBar) {
                        bottomBarActDoneCallback("", curRepoFromParentPage)
                    }

                    //log
                    showErrAndSaveLog(TAG, "#doSync() err: "+e.stackTraceToString(), "sync err: "+e.localizedMessage, requireShowToast, curRepoFromParentPage.id)

                }

            }
        }

    }


    //impl Stage selected files
    fun doStage(
        curRepo:RepoEntity,
        requireCloseBottomBar:Boolean,
        userParamList:Boolean,
        paramList:List<StatusTypeEntrySaver>?,
        fromTo: String,
        selectedListIsEmpty:()->Boolean,
        requireShowToast:(String)->Unit,
        noItemSelectedStrRes:String,
        activityContext:Context,
        selectedItemList:List<StatusTypeEntrySaver>,
        loadingText:MutableState<String>,
        nFilesStagedStrRes:String,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit
    ):Boolean{
        //在index页面是不需要stage的，只有在首页抽屉那个代表worktree的changelist页面才需要stage，但为了简化逻辑，少改代码，直接在这加个非worktree就返回true的判断，这样调用此函数的地方就都不用改了，当作stage成功，然后继续执行后续操作即可
        //只有在indexToWorktree(默认的ChangeList页面)才需要执行stage，其他页面调用此方法直接返回true，无需执行任何操作
        if(fromTo != Cons.gitDiffFromIndexToWorktree) {
            return true
        }

        //如果不使用参数列表，检查下选中条目，没有选中条目则显示提示，否则添加条目
        if (!userParamList && selectedListIsEmpty()) {  //因为无选中项时按钮禁用，所以一般不会执行这块，只是以防万一
            requireShowToast(noItemSelectedStrRes)
            return false
        }

        //如果请求使用参数传来的列表，则检查列表是否为null或空
        if(userParamList && paramList.isNullOrEmpty()) {
            requireShowToast(activityContext.getString(R.string.item_list_is_empty))
            return false
        }

        val actuallyStageList = if(userParamList) paramList!! else selectedItemList

        loadingText.value = activityContext.getString(R.string.staging)
        //执行到这，要么请求使用参数列表，要么有选中条目
        //添加选中条目到index
        //打开仓库
        Repository.open(curRepo.fullSavePath).use { repo ->
            Libgit2Helper.stageStatusEntryAndWriteToDisk(repo, actuallyStageList)
        }

        //准备提示信息
        //替换资源字符串的占位符1为选中条目数，生成诸如：“已 staged 5 个文件“ 这样的字符串
        val msg = replaceStringResList(
            nFilesStagedStrRes,
            listOf(actuallyStageList.size.toString())
        )

        //关闭底栏，显示提示
        if(requireCloseBottomBar) {
            bottomBarActDoneCallback(msg, curRepo)
        }

        return true
    }

    fun changeListDoRefresh(stateForRefresh:MutableState<String>, whichRepoRequestRefresh:RepoEntity) {
        changeStateTriggerRefreshPage(stateForRefresh, requestType= StateRequestType.withRepoId, data = whichRepoRequestRefresh.id)
    }


    suspend fun doPull(
        curRepo:RepoEntity,
        trueMergeFalseRebase: Boolean,
        activityContext:Context,
        requireCloseBottomBar:Boolean,
        dbContainer: AppContainer,
        requireShowToast:(String)->Unit,
        loadingText:MutableState<String>,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        changeListRequireRefreshFromParentPage:(RepoEntity) -> Unit,
        force: Boolean = false,
    ) {
        try {
            //执行操作
            val fetchSuccess = doFetch(
                remoteNameParam = null,
                curRepoFromParentPage = curRepo,
                requireShowToast = requireShowToast,
                activityContext = activityContext,
                loadingText = loadingText,
                dbContainer = dbContainer,
            )

            if(!fetchSuccess) {
                requireShowToast(activityContext.getString(R.string.fetch_failed))
            }else if(force) {
                // force pull, reset local branch to upstream
                Repository.open(curRepo.fullSavePath).use { gitRepo ->
                    val curBranch = Libgit2Helper.getRepoCurBranchShortRefSpec(gitRepo)
                    val upstream = Libgit2Helper.getUpstreamOfBranch(gitRepo, curBranch)
                    val resetType = Reset.ResetT.HARD
                    val result = Libgit2Helper.resetToRevspec(gitRepo, upstream.remoteOid, resetType)
                    if(result.hasError()) {
                        throw result.exception ?: RuntimeException(result.msg)
                    }
                }

                requireShowToast(activityContext.getString(R.string.pull_force_success))
            }else {
//                val mergeSuccess = doMerge(true, null, true)
                val mergeSuccess = doMerge(
                    requireCloseBottomBar = false,
                    upstreamParam = null,
                    showMsgIfHasConflicts = true,
                    trueMergeFalseRebase = trueMergeFalseRebase,
                    curRepoFromParentPage = curRepo,
                    requireShowToast = requireShowToast,
                    activityContext = activityContext,
                    loadingText = loadingText,
                    bottomBarActDoneCallback = bottomBarActDoneCallback
                )
                if(!mergeSuccess){
                    requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.merge_failed else R.string.rebase_failed))
                }else {
                    requireShowToast(activityContext.getString(if(trueMergeFalseRebase) R.string.pull_merge_success else R.string.pull_rebase_success))
                }
            }

            if(requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepo)
            }
        }catch (e:Exception){
            if(requireCloseBottomBar) {
                bottomBarActDoneCallback("", curRepo)
            }

            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "doPull(trueMergeFalseRebase=$trueMergeFalseRebase, force=$force) err: "+e.stackTraceToString(),
                showMsg = activityContext.getString(if(force) R.string.pull_force_failed else if(trueMergeFalseRebase) R.string.pull_merge_failed else R.string.pull_rebase_failed)+": "+e.localizedMessage,
                showMsgMethod = requireShowToast,
                repoId = curRepo.id
            )
        }
    }


    suspend fun doAccept(
        curRepo:RepoEntity,
        acceptTheirs:Boolean,
        loadingText:MutableState<String>,
        activityContext:Context,
        hasConflictItemsSelected:()->Boolean,
        requireShowToast:(String)->Unit,
        selectedItemList:List<StatusTypeEntrySaver>,
        repoState:MutableIntState,
        repoId:String,
        fromTo:String,
        selectedListIsEmpty:()->Boolean,
        noItemSelectedStrRes:String,
        nFilesStagedStrRes:String,
        bottomBarActDoneCallback:(String, RepoEntity)->Unit,
        changeListRequireRefreshFromParentPage:(RepoEntity)->Unit,
    ) {
        loadingText.value = (if(acceptTheirs) activityContext.getString(R.string.accept_theirs) else activityContext.getString(R.string.accept_ours)) + Cons.oneChar3dots

        val repoFullPath = curRepo.fullSavePath
        if(!hasConflictItemsSelected()) {
            requireShowToast(activityContext.getString(R.string.err_no_conflict_item_selected))
        }

        val conflictList = selectedItemList.toList().filter { it.changeType == Cons.gitStatusConflict }
        val pathspecList = conflictList.map { it.relativePathUnderRepo }

        Repository.open(repoFullPath).use { repo->
            val acceptRet = if(repoState.intValue == Repository.StateT.MERGE.bit) {
                Libgit2Helper.mergeAccept(repo, pathspecList, acceptTheirs)
            }else if(repoState.intValue == Repository.StateT.REBASE_MERGE.bit) {
                Libgit2Helper.rebaseAccept(repo, pathspecList, acceptTheirs)
            }else if(repoState.intValue == Repository.StateT.CHERRYPICK.bit) {
                Libgit2Helper.cherrypickAccept(repo, pathspecList, acceptTheirs)
            }else {
                Ret.createError(null, "bad repo state")
            }

            if(acceptRet.hasError()) {
                requireShowToast(acceptRet.msg)
                createAndInsertError(repoId, acceptRet.msg)
            }else {  // accept成功，stage条目
                //在这里stage不存在的路径会报错，所以过滤下，似乎checkout后会自动stage已删除文件？我不确定
                val existConflictItems = conflictList.filter { it.toFile().exists() }
                val stageSuccess = if(existConflictItems.isEmpty()) { //列表为空，无需stage，直接返回true即可
                    true
                }else {  //列表有条目，执行stage
                    ChangeListFunctions.doStage(
                        curRepo=curRepo,
                        requireCloseBottomBar = false,
                        userParamList = true,
                        paramList = existConflictItems,

                        fromTo = fromTo,
                        selectedListIsEmpty = selectedListIsEmpty,
                        requireShowToast = requireShowToast,
                        noItemSelectedStrRes = noItemSelectedStrRes,
                        activityContext = activityContext,
                        selectedItemList = selectedItemList,
                        loadingText = loadingText,
                        nFilesStagedStrRes = nFilesStagedStrRes,
                        bottomBarActDoneCallback = bottomBarActDoneCallback
                    )
                }

                if(stageSuccess) {  //stage成功
                    requireShowToast(activityContext.getString(R.string.success))
                }else{  //当初设计的时候没在这返回错误信息，懒得改了，提示下stage失败即可
                    requireShowToast(activityContext.getString(R.string.stage_failed))
                }
            }

            changeListRequireRefreshFromParentPage(curRepo)
        }
    }


    /**
     * ChangeListInnerPage用的创建补丁的方法，和CommitListScreen创建补丁的逻辑有些不同，需判断fromTo
     */
    fun createPath(
        curRepo: RepoEntity,
        leftCommit:String,  //逻辑上在左边的commit，如果有swap，这里应传swap后的值而不是原始值
        rightCommit:String,  //逻辑上在右边的commit，如果有swap，这里应传swap后的值而不是原始值
        fromTo: String,
        relativePaths: List<String>
    ):Ret<PatchFile?> {
        Repository.open(curRepo.fullSavePath).use { repo ->
            var treeToWorkTree = false  //默认设为假，若和worktree对比（local），会改为真

            val (reverse: Boolean, tree1: Tree?, tree2: Tree?) = if (fromTo == Cons.gitDiffFromIndexToWorktree || fromTo == Cons.gitDiffFromHeadToIndex) {
                Triple(false, null, null)
            } else if (Libgit2Helper.CommitUtil.isLocalCommitHash(leftCommit) || Libgit2Helper.CommitUtil.isLocalCommitHash(rightCommit)) {
                treeToWorkTree = true

                //其中一个是local路径，为local的可不是有效tree
                val reverse = Libgit2Helper.CommitUtil.isLocalCommitHash(leftCommit)  //若左边的commit是local，需要反转一下，因为默认api只有 treeToWorkdir，即treeToLocal，而我们需要的是localToTree，所以不反转不行

                val tree1 = if (reverse) {
                    Libgit2Helper.resolveTree(repo, rightCommit)
                } else {
                    Libgit2Helper.resolveTree(repo, leftCommit)
                }

                if (tree1 == null) {
                    throw RuntimeException("resolve tree1 failed, 11982433")
                }

                val tree2 = null

                Triple(reverse, tree1, tree2)
            } else {
                //没有local路径，全都是有效tree
                val reverse = false
                val tree1 = Libgit2Helper.resolveTree(repo, leftCommit) ?: throw RuntimeException("resolve tree1 failed, 12978960")
                val tree2 = Libgit2Helper.resolveTree(repo, rightCommit) ?: throw RuntimeException("resolve tree2 failed, 17819020")
                Triple(reverse, tree1, tree2)
            }

            //获取输出文件，可能没创建，执行输出时会自动创建，重点是文件路径
            val (left: String, right: String) = if (fromTo == Cons.gitDiffFromIndexToWorktree) {
                Pair(Cons.git_IndexCommitHash, Cons.git_LocalWorktreeCommitHash)
            } else if (fromTo == Cons.gitDiffFromHeadToIndex) {
                Pair(Cons.git_HeadCommitHash, Cons.git_IndexCommitHash)
            } else {
                Pair(leftCommit, rightCommit)
            }

            val outFile = FsUtils.Patch.newPatchFile(curRepo.repoName, left, right)

            return Libgit2Helper.savePatchToFileAndGetContent(
                outFile = outFile,
                pathSpecList = relativePaths,
                repo = repo,
                tree1 = tree1,
                tree2 = tree2,
                fromTo = fromTo,
                reverse = reverse,
                treeToWorkTree = treeToWorkTree,
                returnDiffContent = false  //是否返回输出的内容，若返回，可在ret中取出字符串
            )

        }
    }

}
