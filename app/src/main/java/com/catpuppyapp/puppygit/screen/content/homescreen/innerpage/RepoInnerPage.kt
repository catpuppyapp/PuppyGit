package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.AddRepoDropDownMenu
import com.catpuppyapp.puppygit.compose.AskGitUsernameAndEmailDialog
import com.catpuppyapp.puppygit.compose.AskGitUsernameAndEmailDialogWithSelection
import com.catpuppyapp.puppygit.compose.BottomBar
import com.catpuppyapp.puppygit.compose.CommitMsgMarkDownDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CopyableDialog2
import com.catpuppyapp.puppygit.compose.DefaultPaddingRow
import com.catpuppyapp.puppygit.compose.DefaultPaddingText
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.InternalFileChooser
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PageCenterIconButton
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoCard
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.SelectedItemDialog
import com.catpuppyapp.puppygit.compose.SelectionRow
import com.catpuppyapp.puppygit.compose.SetUpstreamDialog
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.data.AppContainer
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.etc.RepoPendingTask
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.Upstream
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.goToErrScreen
import com.catpuppyapp.puppygit.screen.functions.goToStashPage
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.server.bean.ConfigBean
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.PackageNameAndRepo
import com.catpuppyapp.puppygit.settings.PackageNameAndRepoSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ComposeHelper
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.RepoStatusUtil
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.doActIfIndexGood
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.genHttpHostPortStr
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.isLocked
import com.catpuppyapp.puppygit.utils.isRepoReadyAndPathExist
import com.catpuppyapp.puppygit.utils.pref.PrefUtil
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.showErrAndSaveLog
import com.catpuppyapp.puppygit.utils.state.CustomStateListSaveable
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.strHasIllegalChars
import com.catpuppyapp.puppygit.utils.updateSelectedList
import com.github.git24j.core.Repository
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val TAG = "RepoInnerPage"

private const val invalidIdx = -1


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoInnerPage(
    stateKeyTag:String,

    requireInnerEditorOpenFile:(filePath:String, expectReadOnly:Boolean)->Unit,

    lastSearchKeyword:MutableState<String>,
    searchToken:MutableState<String>,
    searching:MutableState<Boolean>,
    resetSearchVars:()->Unit,

    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    curRepo: CustomStateSaveable<RepoEntity>,
    curRepoIndex: MutableIntState,
    contentPadding: PaddingValues,
    repoPageListState: LazyListState,
    showSetGlobalGitUsernameAndEmailDialog:MutableState<Boolean>,
    needRefreshRepoPage:MutableState<String>,
//    changeListCurRepo:CustomStateSaveable<RepoEntity>,
//    currentHomeScreen:MutableIntState,
//    changeListNeedRefresh:MutableState<String>,
//    repoList:CustomStateSaveable<MutableList<RepoEntity>>
    repoList:CustomStateListSaveable<RepoEntity>,
//    filesPageCurrentPath:MutableState<String>,
//    filesPageNeedRefresh:MutableState<String>,
    goToFilesPage:(path:String) -> Unit,
    goToChangeListPage:(repoWillShowInChangeListPage: RepoEntity) -> Unit,
    repoPageScrolled:MutableState<Boolean>,
    repoPageFilterModeOn:MutableState<Boolean>,
    repoPageFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    filterListState:LazyListState,
    openDrawer:()->Unit,
    showImportRepoDialog:MutableState<Boolean>,
    goToThisRepoId:MutableState<String>,
    enableFilterState:MutableState<Boolean>,
    filterList:CustomStateListSaveable<RepoEntity>,
    isSelectionMode:MutableState<Boolean>,
    selectedItems:CustomStateListSaveable<RepoEntity>,
    unshallowList:CustomStateListSaveable<RepoEntity>,
    deleteList:CustomStateListSaveable<RepoEntity>,
    userInfoRepoList:CustomStateListSaveable<RepoEntity>,
    upstreamRemoteOptionsList:CustomStateListSaveable<String>,
    specifiedRefreshRepoList:CustomStateListSaveable<RepoEntity>,

    showWelcomeToNewUser:MutableState<Boolean>,
    closeWelcome:()->Unit,


) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp;
    val navController = AppModel.navController;
    val scope = rememberCoroutineScope()

    val settings = remember {
        val s = SettingsUtil.getSettingsSnapshot()
        repoPageScrolled.value = s.showNaviButtons
        s
    }

    // 这两个变量不用 rememberSaveable，如果设备配置改变，就希望这两个值重新计算
    val itemWidth = remember { UIHelper.getRepoItemWidth() }
    val configuration = AppModel.getCurActivityConfig()
    val repoCountEachRow = remember(configuration.screenWidthDp) { UIHelper.getRepoItemsCountEachRow(configuration.screenWidthDp.toFloat()) }


    val clipboardManager = LocalClipboardManager.current

    val cloningText = stringResource(R.string.cloning)
    val unknownErrWhenCloning = stringResource(R.string.unknown_err_when_cloning)

    val dbContainer = AppModel.dbContainer;
//    val repoDtoList = remember { mutableStateListOf<RepoEntity>() }

//    val activity = ActivityUtil.getCurrentActivity()

    val inDarkTheme = Theme.inDarkTheme

    val requireBlinkIdx = rememberSaveable { mutableIntStateOf(-1) }

    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading)) }
    val initLoadingOn = {text:String->
        initLoadingText.value = text
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
        initLoadingText.value = ""
    }



    val errWhenQuerySettingsFromDbStrRes = stringResource(R.string.err_when_querying_settings_from_db)
    val saved = stringResource(R.string.saved)

    val pageRequest = rememberSaveable { mutableStateOf("")}

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
    // username and email end

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
                MyLog.e(TAG, "set username and email err (from Repos page): ${e.stackTraceToString()}")
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


//    val showSetGlobalGitUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val setGlobalGitUsernameAndEmailStrRes = stringResource(R.string.set_global_username_and_email)
    val globalUsername = rememberSaveable { mutableStateOf("")}
    val globalEmail = rememberSaveable { mutableStateOf("")}

    // global username and email dialog
    if(showSetGlobalGitUsernameAndEmailDialog.value) {
        val curRepo = curRepo.value

        AskGitUsernameAndEmailDialog(
            title = if(showWelcomeToNewUser.value) stringResource(R.string.welcome) else stringResource(R.string.user_info),
            text = if(showWelcomeToNewUser.value) stringResource(R.string.welcome_please_set_git_username_and_email) else setGlobalGitUsernameAndEmailStrRes,
            username=globalUsername,
            email=globalEmail,
            isForGlobal=true,
            repos = listOf(), // global,不需要此list，传空即可
            onOk={
                if(showWelcomeToNewUser.value) {
                    closeWelcome()
                }

                doJobThenOffLoading(
                    //loadingOn = loadingOn, loadingOff=loadingOff,
//                    loadingText=appContext.getString(R.string.saving)
                ){
                    //save email and username
                    Libgit2Helper.saveGitUsernameAndEmailForGlobal(
                        requireShowErr=Msg.requireShowLongDuration,
                        username=globalUsername.value,
                        email=globalEmail.value
                    )

                    showSetGlobalGitUsernameAndEmailDialog.value=false
                    Msg.requireShow(saved)
                }
            },
            onCancel={
                if(showWelcomeToNewUser.value) {
                    closeWelcome()
                }

                showSetGlobalGitUsernameAndEmailDialog.value=false
                globalUsername.value=""
                globalEmail.value=""
            },

            //字段都可为空，所以确定键总是启用
            enableOk={true},
        )
    }



    val showSetCurRepoGitUsernameAndEmailDialog = rememberSaveable { mutableStateOf(false) }
    val curRepoUsername = rememberSaveable { mutableStateOf("") }
    val curRepoEmail = rememberSaveable { mutableStateOf("") }

    //这个不是显示global的，是针对单或多个仓库的
    val showSetUserInfoDialog = showSetUserInfoDialog@{repos:List<RepoEntity> ->
        if(repos.isEmpty()) {
            return@showSetUserInfoDialog
        }

        userInfoRepoList.value.clear()
        userInfoRepoList.value.addAll(repos)

        showSetCurRepoGitUsernameAndEmailDialog.value = true
    }

    // repo username and email dialog
    if(showSetCurRepoGitUsernameAndEmailDialog.value) {
        AskGitUsernameAndEmailDialog(
            title= stringResource(R.string.user_info),
            text=stringResource(R.string.set_username_and_email_for_repo),
            username=curRepoUsername,
            email=curRepoEmail,
            isForGlobal=false,
            repos=userInfoRepoList.value,
            onOk={
                showSetCurRepoGitUsernameAndEmailDialog.value=false

                // save email and username
                doJobThenOffLoading {
                    userInfoRepoList.value.toList().forEachBetter { curRepo ->
//                    MyLog.d(TAG, "curRepo.value.fullSavePath::"+curRepo.value.fullSavePath)
                        Repository.open(curRepo.fullSavePath).use { repo ->
                            //save email and username
                            Libgit2Helper.saveGitUsernameAndEmailForRepo(
                                repo = repo,
                                requireShowErr=Msg.requireShowLongDuration,
                                username=curRepoUsername.value,
                                email=curRepoEmail.value
                            )
                        }
                    }

                    Msg.requireShow(saved)
                }

            },
            onCancel={
                showSetCurRepoGitUsernameAndEmailDialog.value=false
                curRepoUsername.value=""
                curRepoEmail.value=""
            },

            //字段都可为空，所以确定键总是启用
            enableOk={true},
        )

    }

    val importRepoPath = rememberSaveable { SharedState.fileChooser_DirPath }
//    val safEnabledForSystemFolderChooser = rememberSaveable { mutableStateOf(false)}
//    val safPath = rememberSaveable { mutableStateOf("") }
//    val nonSafPath = rememberSaveable { mutableStateOf("") }
    val isReposParentFolderForImport = rememberSaveable { mutableStateOf(false) }

    if(showImportRepoDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.import_repo),
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        InternalFileChooser(activityContext, path = importRepoPath)

                        Spacer(Modifier.height(15.dp))

                        MyCheckBox(text = stringResource(R.string.the_path_is_a_repos_parent_dir), value = isReposParentFolderForImport)

                        Spacer(Modifier.height(5.dp))

                        if(isReposParentFolderForImport.value) {
                            DefaultPaddingText(stringResource(R.string.will_scan_repos_under_this_folder))
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = importRepoPath.value.isNotBlank(),
            onCancel = { showImportRepoDialog.value = false },
        ) {
            val importRepoPath = importRepoPath.value

            doJobThenOffLoading {
                try {
                    val newPathRet = FsUtils.userInputPathToCanonical(importRepoPath)

                    if(newPathRet.hasError()) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_path))
                        return@doJobThenOffLoading
                    }

                    val newPath = newPathRet.data!!

                    // path is not blank

                    val f = File(newPath)

                    if(!f.canRead()) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.cant_read_path))
                        return@doJobThenOffLoading
                    }

                    if(!f.isDirectory) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.path_is_not_a_dir))
                        return@doJobThenOffLoading
                    }


                    // close dialog
                    showImportRepoDialog.value = false

                    // start import
                    Msg.requireShowLongDuration(activityContext.getString(R.string.importing))


                    val importRepoResult = AppModel.dbContainer.repoRepository.importRepos(dir=newPath, isReposParent=isReposParentFolderForImport.value)

                    // show a result dialog may better?

                    Msg.requireShowLongDuration(replaceStringResList(activityContext.getString(R.string.n_imported), listOf(""+importRepoResult.success)))

                }catch (e:Exception) {
                    MyLog.e(TAG, "import repo from ReposPage err: "+e.stackTraceToString())
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                }finally {
                    changeStateTriggerRefreshPage(needRefreshRepoPage)

                    //这个判断可能不准
//                    if(importRepoResult.success>0) {
//                        changeStateTriggerRefreshPage(needRefreshRepoPage)
//                    }
                }
            }

        }

    }

    val doActAndLogErr:suspend (curRepo:RepoEntity, actName:String, act:suspend ()->Unit)->Unit = {curRepo, actName, act ->
        try {
            act()
        }catch (e:Exception) {
            //记录到日志
            //显示提示
            //保存数据库(给用户看的，消息尽量简单些)
            showErrAndSaveLog(
                logTag = TAG,
                logMsg = "do `$actName` from Repo Page err: "+e.stackTraceToString(),
                showMsg = "$actName err: "+e.localizedMessage,
                showMsgMethod = {},  //不显示错误信息toast
                repoId = curRepo.id
            )
        }
    }

    //执行完doFetch/doMerge/doPush/doSync记得刷新页面，刷新页面不会改变列表滚动位置，所以放心刷，不用怕一刷新列表元素又滚动回第一个，让正在浏览仓库列表的用户困扰
    val doFetch:suspend (String?,RepoEntity)->Unit = doFetch@{remoteNameParam:String?,curRepo:RepoEntity ->  //参数的remoteNameParam如果有效就用参数的，否则自己查当前head分支对应的remote
        Repository.open(curRepo.fullSavePath).use { repo ->
            var remoteName = remoteNameParam
            if(remoteName == null || remoteName.isBlank()) {
                val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)
                val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)
                remoteName = upstream.remote
                if(remoteName == null || remoteName.isBlank()) {  //fetch不需合并，只需remote有效即可，所以只检查remote
                    throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
//                        return@doFetch false
                }
            }

            //执行到这，upstream的remote有效，执行fetch
//            只fetch当前分支关联的remote即可，获取仓库当前remote和credential的关联，组合起来放到一个pair里，pair放到一个列表里，然后调用fetch
            val credential = Libgit2Helper.getRemoteCredential(
                dbContainer.remoteRepository,
                dbContainer.credentialRepository,
                curRepo.id,
                remoteName,
                trueFetchFalsePush = true
            )

            //执行fetch
            Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, curRepo)

        }

        // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
        val repoDb = AppModel.dbContainer.repoRepository
        repoDb.updateLastUpdateTime(curRepo.id, getSecFromTime())

    }

    suspend fun doMerge(upstreamParam: Upstream?, curRepo:RepoEntity):Unit {
        val trueMergeFalseRebase = !PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext)

        //这的repo不能共享，不然一释放就要完蛋了，这repo不是rc是box单指针
        Repository.open(curRepo.fullSavePath).use { repo ->
            var upstream = upstreamParam
            if(Libgit2Helper.isUpstreamInvalid(upstream)) {  //如果调用者没传有效的upstream，查一下
                val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  //获取当前分支短名，例如 main
                upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  //获取当前分支的上游，例如 remote=origin 和 merge=refs/heads/main，参见配置文件 branch.yourbranchname.remote 和 .merge 字段
                //如果查出的upstream还是无效，终止操作
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                    throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
//                        return@doMerge false
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
                throw RuntimeException(activityContext.getString(R.string.plz_set_username_and_email_first))
//                    return@doMerge false
            }

            val mergeResult = Libgit2Helper.mergeOrRebase(
                repo,
                targetRefName = remoteRefSpec,
                username = usernameFromConfig,
                email = emailFromConfig,
                requireMergeByRevspec = false,
                revspec = "",
                trueMergeFalseRebase = trueMergeFalseRebase,
                settings = settings
            )

            if (mergeResult.hasError()) {
                //检查是否存在冲突条目
                //如果调用者想自己判断是否有冲突，可传showMsgIfHasConflicts为false
                if (mergeResult.code == Ret.ErrCode.mergeFailedByAfterMergeHasConfilts) {
                    throw RuntimeException(activityContext.getString(R.string.has_conflicts))

//                        if(trueMergeFalseRebase) {
//                            throw RuntimeException(appContext.getString(R.string.merge_has_conflicts))
//                        }else {
//                            throw RuntimeException(appContext.getString(R.string.rebase_has_conflicts))
//                        }
                }

                //显示错误提示
                throw RuntimeException(mergeResult.msg)

                //记到数据库error日志
//                    createAndInsertError(curRepo.id, mergeResult.msg)

//                    return@doMerge false
            }

            //执行到这就合并成功了

            //清下仓库状态
            Libgit2Helper.cleanRepoState(repo)

            //更新db显示通知
            Libgit2Helper.updateDbAfterMergeSuccess(mergeResult, activityContext, curRepo.id, {}, trueMergeFalseRebase)  //最后一个参数是合并成功或者不需要合并(uptodate)的信息提示函数，这个页面就不要在成功时提示了，合并完刷新下页面显示在仓库卡片上就行了

        }
    }

    val doPush:suspend (Upstream?,RepoEntity) -> Unit  = doPush@{upstreamParam:Upstream?,curRepo:RepoEntity ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            if(repo.headDetached()) {
                throw RuntimeException(activityContext.getString(R.string.push_failed_by_detached_head))
            }


            var upstream:Upstream? = upstreamParam
            if(Libgit2Helper.isUpstreamInvalid(upstream)) {  //如果调用者没传有效的upstream，查一下
                val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)  //获取当前分支短名，例如 main
                upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)  //获取当前分支的上游，例如 remote=origin 和 merge=refs/heads/main，参见配置文件 branch.yourbranchname.remote 和 .merge 字段
                //如果查出的upstream还是无效，终止操作
                if(Libgit2Helper.isUpstreamInvalid(upstream)) {
                    throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
                }
            }

            // 执行到这里，必定有上游
            // 检查是否需要先提交
            if(PrefUtil.getGlobalGitConfigCommitOnPush(AppModel.realAppContext)) {
                val settings = SettingsUtil.getSettingsSnapshot()

                val (username, email) = Libgit2Helper.getGitUsernameAndEmail(repo)

                if (username == null || username.isBlank() || email == null || email.isBlank()) {
                    throw RuntimeException("push err(from repo page): auto commit err: username or email invalid")
                } else {
                    //检查是否存在冲突，如果存在，将不会创建提交
                    if (Libgit2Helper.hasConflictItemInRepo(repo)) {
                        throw RuntimeException("push err(from repo page): auto commit enabled but have conflicts, please resolve conflicts first")
                    } else {
                        // 有username 和 email，且无冲突

                        // stage worktree changes
                        Libgit2Helper.stageAll(repo, curRepo.id)


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
                                throw RuntimeException("commit on push error(from repo page): ${ret.msg}")
                            } else if(ret.data != null){
                                //更新上游本地oid为最新值
                                upstream!!.localOid = ret.data!!.toString()
                            }
                        }
                    }
                }
            }

            MyLog.d(TAG, "#doPush: upstream.remote="+upstream!!.remote+", upstream.branchFullRefSpec="+upstream!!.branchRefsHeadsFullRefSpec)

            // push
            val credential = Libgit2Helper.getRemoteCredential(
                dbContainer.remoteRepository,
                dbContainer.credentialRepository,
                curRepo.id,
                upstream!!.remote,
                trueFetchFalsePush = false
            )

            Libgit2Helper.pushSingleBranch(repo, upstream, credential, force = false)

            // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
            val repoDb = AppModel.dbContainer.repoRepository
            repoDb.updateLastUpdateTime(curRepo.id, getSecFromTime())
        }
    }

    val doClone = doClone@{repoList:List<RepoEntity> ->
        if(repoList.isEmpty()) {
            return@doClone
        }
//                        repoDtoList[idx].tmpStatus=""  //err状态，tmpStatus本来就没值，不用设
        doJobThenOffLoading {
            //更新仓库状态为待克隆
            repoList.forEachBetter { curRepo ->
                val repoLock = Libgit2Helper.getRepoLock(curRepo.id)
                if(isLocked(repoLock)) {
                    return@doJobThenOffLoading
                }

                repoLock.withLock {
                    val repoRepository = dbContainer.repoRepository
                    val repoFromDb = repoRepository.getById(curRepo.id)?:return@withLock
                    if(repoFromDb.workStatus == Cons.dbRepoWorkStatusCloneErr) {
                        repoFromDb.workStatus = Cons.dbRepoWorkStatusNotReadyNeedClone
//                                    repoFromDb.tmpStatus = appContext.getString(R.string.cloning)
                        repoFromDb.createErrMsg = ""
                        repoRepository.update(repoFromDb)

                        //这个可有可无，反正后面会刷新页面，刷新页面必须有，否则会变成cloning状态，然后就不更新了，还得手动刷新页面，麻烦
//                    repoList[idx]=repoFromDb  //刷新页面后会重新查询
                    }
                }
            }

            //刷新页面，然后就会执行克隆
            changeStateTriggerRefreshPage(needRefreshRepoPage)
        }

        Unit
    }

    val doCloneSingle = { targetRepo:RepoEntity ->
        doClone(listOf(targetRepo))
    }

    //sync之前，先执行stage，然后执行提交，如果成功，执行fetch/merge/push (= pull/push = sync)
    val doSync:suspend (RepoEntity)->Unit = doSync@{curRepo:RepoEntity ->

        Repository.open(curRepo.fullSavePath).use { repo ->
            if(repo.headDetached()) {
                throw RuntimeException(activityContext.getString(R.string.sync_failed_by_detached_head))
            }


            //检查是否有upstream，如果有，do fetch do merge，然后do push,如果没有，请求设置upstream，然后do push
            val hasUpstream = Libgit2Helper.isBranchHasUpstream(repo)
            val shortBranchName = Libgit2Helper.getRepoCurBranchShortRefSpec(repo)
            if (!hasUpstream) {  //不存在上游，提示先去分支页面设置
                //应该支持在仓库页面设置
                throw RuntimeException(activityContext.getString(R.string.err_upstream_invalid_plz_go_branches_page_set_it_then_try_again))
            }


            //存在上游

            //取出上游
            val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranchName)

            doFetch(upstream.remote, curRepo)


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
                doMerge(upstream, curRepo)
            }

            //如果执行到这，要么不存在上游，直接push(新建远程分支)；要么存在上游，但fetch/merge成功完成，需要push，所以，执行push
            //doPush
            doPush(upstream, curRepo)
        }

    }

    val doPull:suspend (RepoEntity)->Unit = {curRepo ->
        doFetch(null, curRepo)
        doMerge(null, curRepo)
    }


    val getCurActiveList = {
        if(enableFilterState.value) filterList.value else repoList.value
    }

    val getCurActiveListState = {
        if(enableFilterState.value) filterListState else repoPageListState
    }

    /**
     * the `_idx` was `idx`, but then changed to find idx by repoId, so deprecated it, kept for backward compatible
    */
    val doActAndSetRepoStatus:suspend (Int, String, String, suspend ()->Unit) -> Unit = {_idx:Int, repoId:String, status:String, act: suspend ()->Unit ->
        // should update repoList ever, the filtered list will follow the repoList
        //应该总是更新repoList，过滤后的列表变化总是跟随repoList变化而变化
        val repoList = repoList.value
        // find idx by id, instead param idx
        val idx = repoList.indexOfFirst { it.id == repoId }
        //设置数组中元素的临时状态(重新查列表后消失，但因为上面设置状态到缓存了，所以重查时可通过缓存恢复)，但无需重新加载页面，轻量级操作，就能让用户看到临时状态
        doActIfIndexGood(idx,repoList) {
//            it.tmpStatus = status
            //必须copy一下，要不然还得刷新页面才能显示状态（ps：刷新页面显示状态是通过map存临时状态实现的，比这个操作重量级，应能避免则避免）
            repoList[idx] = it.copyAllFields(settings, it.copy(tmpStatus = status))
//            repoList.requireRefreshView()
        }
        //设置仓库临时状态(把临时状态设置到缓存里，不退出app都有效，目的是为了使重新查列表后临时状态亦可见)，这样重新加载页面时依然能看到临时状态
        RepoStatusUtil.setRepoStatus(repoId, status)
        //刷新以显示刚设置的状态
//        changeStateTriggerRefreshPage(needRefreshRepoPage)
        //执行操作
        act()

        //test 有时候操作执行太快，但我需要执行慢点来测试某些东西，所以加个delay方便测试
        //delay(3000)
        //test

        //清除缓存中的仓库状态
        RepoStatusUtil.clearRepoStatus(repoId)
        //重查下repo数据
        doActIfIndexGood(idx,repoList) {
            //无法确定执行什么操作，也无法确定会影响到什么，所以无法精准更新某字段，干脆在操作成功时，重查下数据，拿到最新状态就行了
            doJobThenOffLoading {
                //重查数据
                val repoDb = dbContainer.repoRepository
                val reQueriedRepoInfo = repoDb.getById(it.id)?:return@doJobThenOffLoading

                //检查仓库是否有未提交的修改
                if(reQueriedRepoInfo.pendingTask == RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES) {
                    //捕获当前页面刷新状态值，相当于session id
                    val curRefreshValue = needRefreshRepoPage.value
                    checkGitStatusAndUpdateItemInList(
                        settings = settings,
                        item = reQueriedRepoInfo,
                        idx = idx,
                        repoList = repoList,
                        loadingText = activityContext.getString(R.string.loading),
                        pageChanged = {
                            needRefreshRepoPage.value != curRefreshValue
                        }
                    )
                }else { //不需要检查git status，直接更新卡片条目
                    repoList[idx] = reQueriedRepoInfo
                }


                //检查下如果当前长按菜单显示的是当前仓库，更新下和菜单项相关的字段。（这里不要赋值curRepo.value，以免并发冲突覆盖用户长按的仓库）
                val curRepoInMenu = curRepo.value  //这样修改的话，即使在下面赋值时用户长按了其他仓库也能正常工作，只是下面的赋值操作失去意义而已，但不会并发冲突，也不会显示或执行错误，但如果直接给curRepo.value赋值，则就有可能出错了，比如可能覆盖用户长按的仓库，发生用户长按了仓库a，但显示的却是仓库b的状态的情况
                if(curRepoInMenu.id == reQueriedRepoInfo.id) {  //如果，当前长按菜单显示的是当前修改的仓库，则更新下其tmpStatus以更新其菜单项的启用/禁用状态，例如执行fetch时，pull/push/fetch都禁用，但操作执行完后应重新启用，若不做这个检测，则只有重新长按菜单才会更新启用/禁用状态，有点不方便
                    //注意这里更新的只有和长按菜单相关的字段而已，其他字段无需更新
                    curRepoInMenu.tmpStatus = reQueriedRepoInfo.tmpStatus  //不出意外的话，这的tmpStatus应为空字符串，但保险起见，还是不要直接用空字符串赋值比较好
                    curRepoInMenu.isDetached = reQueriedRepoInfo.isDetached
                    curRepoInMenu.isShallow = reQueriedRepoInfo.isShallow
                }
//                repoList.requireRefreshView()
            }
        }

    }

    val showDelRepoDialog = rememberSaveable { mutableStateOf(false)}
    val willDeleteRepoNames = rememberSaveable { mutableStateOf("") }
    val requireDelFilesOnDisk = rememberSaveable { mutableStateOf(false)}
    val requireDelRepo = {expectDelRepos:List<RepoEntity> ->
        //仓库名
        val suffix = ", "
        val sb = StringBuilder()
        expectDelRepos.forEachBetter { sb.append(it.repoName).append(suffix) }
        willDeleteRepoNames.value = sb.removeSuffix(suffix).toString()

        //添加到待删除列表
        deleteList.value.clear()
        deleteList.value.addAll(expectDelRepos)

        //初始化删除硬盘文件为假
        requireDelFilesOnDisk.value = false

        //显示弹窗
        showDelRepoDialog.value = true
    }

    if(showDelRepoDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.delete),
//            text = stringResource(id = R.string.are_you_sure_to_delete)+": '"+willDeleteRepo.value.repoName+"' ?"+"\n"+ stringResource(R.string.will_delete_repo_and_all_its_files_on_disk),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    SelectionRow {
                        Text(text = stringResource(id = R.string.delete_repos)+":")
                    }

                    Spacer(Modifier.height(10.dp))

                    MySelectionContainer {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = willDeleteRepoNames.value,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 16.dp),
                            )

                        }

                    }

                    Spacer(Modifier.height(5.dp))

                    MyCheckBox(stringResource(R.string.del_files_on_disk), requireDelFilesOnDisk)

                    if(requireDelFilesOnDisk.value) {
                        MySelectionContainer {
                            DefaultPaddingRow {
                                Text(
                                    text = stringResource(R.string.will_delete_repo_and_all_its_files_on_disk),
                                    color = MyStyleKt.TextColor.danger()
                                )
                            }
                        }
                    }
                }

            },
            okBtnText = stringResource(R.string.delete),
            okTextColor = if(requireDelFilesOnDisk.value) MyStyleKt.TextColor.danger() else Color.Unspecified,
            onCancel = { showDelRepoDialog.value = false }
        ) {
            //关闭弹窗
            showDelRepoDialog.value = false

            val requireDelFilesOnDisk = requireDelFilesOnDisk.value
            val requireTransaction = true

            //执行删除
            doJobThenOffLoading {
                var curRepo:RepoEntity? = null
                try {
                    val settings = SettingsUtil.getSettingsSnapshot()

                    var updatedPackageNameAndRepoIdMap = settings.automation.packageNameAndRepoIdsMap
                    val tmpPackageNameAndRepoIdMap = mutableMapOf<String, List<String>>()
                    var updatedPackageNameAndRepoSettingsMap = settings.automation.packageNameAndRepoAndSettingsMap
                    val tmpPackageNameAndRepoSettingsMap = mutableMapOf<String, PackageNameAndRepoSettings>()
                    val pinnedRepos = settings.pinnedRepos

                    deleteList.value.toList().forEachBetter { willDeleteRepo ->
                        curRepo = willDeleteRepo

                        val repoDb = AppModel.dbContainer.repoRepository
                        //删除仓库
                        repoDb.delete(
                            item = willDeleteRepo,
                            requireDelFilesOnDisk = requireDelFilesOnDisk,
                            requireTransaction = requireTransaction
                        )

                        // remove pinned
                        pinnedRepos.remove(willDeleteRepo.id)

                        // update settings and app linked info
                        for(i in updatedPackageNameAndRepoIdMap) {
                            tmpPackageNameAndRepoIdMap.put(i.key, i.value.filter { it != willDeleteRepo.id })
                        }
                        updatedPackageNameAndRepoIdMap = tmpPackageNameAndRepoIdMap.toMutableMap()
                        tmpPackageNameAndRepoIdMap.clear()

                        // update settings of app and repo pair
                        for(i in updatedPackageNameAndRepoSettingsMap) {
                            val keySuffix = PackageNameAndRepo(repoId = willDeleteRepo.id).toKeySuffix()
                            if(!i.key.endsWith(keySuffix)) {
                                tmpPackageNameAndRepoSettingsMap.put(i.key, i.value)
                            }
                        }
                        updatedPackageNameAndRepoSettingsMap = tmpPackageNameAndRepoSettingsMap.toMutableMap()
                        tmpPackageNameAndRepoSettingsMap.clear()

                    }

                    // save updated settings
                    settings.automation.packageNameAndRepoIdsMap = updatedPackageNameAndRepoIdMap
                    settings.automation.packageNameAndRepoAndSettingsMap = updatedPackageNameAndRepoSettingsMap
                    settings.pinnedRepos = pinnedRepos
                    SettingsUtil.updateSettings(settings)

                    Msg.requireShow(activityContext.getString(R.string.success))

                } catch (e: Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?: "err")
                    MyLog.e(TAG, "del repo '${curRepo?.repoName}' in $TAG err: ${e.stackTraceToString()}")
                } finally {
                    //请求刷新列表
                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }
            }

        }

    }

    val showRenameDialog = rememberSaveable { mutableStateOf(false)}
    val repoNameForRenameDialog = mutableCustomStateOf(stateKeyTag, "repoNameForRenameDialog") { TextFieldValue("") }
    val errMsgForRenameDialog = rememberSaveable { mutableStateOf("")}
    if(showRenameDialog.value) {
        val focusRequester = remember { FocusRequester() }

        val curRepo = curRepo.value

        ConfirmDialog(
            title = stringResource(R.string.rename_repo),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                        ,
                        value = repoNameForRenameDialog.value,
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
                            repoNameForRenameDialog.value = it

                            // clear err msg
                            errMsgForRenameDialog.value = ""
                        },
                        label = {
                            Text(stringResource(R.string.new_name))
                        }
                    )
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = repoNameForRenameDialog.value.text.isNotBlank() && errMsgForRenameDialog.value.isEmpty() && repoNameForRenameDialog.value.text != curRepo.repoName,
            onCancel = {showRenameDialog.value = false}
        ) {
            val newName = repoNameForRenameDialog.value.text
            val repoId = curRepo.id

            doJobThenOffLoading {
                try {
                    val repoDb = AppModel.dbContainer.repoRepository
                    if(strHasIllegalChars(newName)) {
                        errMsgForRenameDialog.value = activityContext.getString(R.string.name_has_illegal_chars)
                        return@doJobThenOffLoading
                    }

                    if(repoDb.isRepoNameExist(newName)) {
                        errMsgForRenameDialog.value = activityContext.getString(R.string.name_already_exists)
                        return@doJobThenOffLoading
                    }

                    showRenameDialog.value = false

                    repoDb.updateRepoName(repoId, newName)

                    Msg.requireShow(activityContext.getString(R.string.success))

                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }catch (e:Exception) {
                    val errmsg = e.localizedMessage ?: "rename repo failed"
                    Msg.requireShowLongDuration("err: "+errmsg)
                    createAndInsertError(curRepo.id, "err: rename repo '${curRepo.repoName}' to ${repoNameForRenameDialog.value} failed, err message is '$errmsg'")

                    changeStateTriggerRefreshPage(needRefreshRepoPage)
                }
            }
        }

        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }


    val showUnshallowDialog = rememberSaveable { mutableStateOf(false) }
    val unshallowRepoNames = rememberSaveable { mutableStateOf("") }
    if(showUnshallowDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.unshallow),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Row {
                            Text(text = stringResource(R.string.will_do_unshallow_for_repos) + ":")
                        }
                    }
                    MySelectionContainer {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = unshallowRepoNames.value,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 16.dp),
                            )

                        }

                    }

                    Spacer(Modifier.height(10.dp))

                    MySelectionContainer {
                        Text(
                            text = stringResource(R.string.unshallow_success_cant_back),
                            color = MyStyleKt.TextColor.danger()
                        )
                    }
                }
            },
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showUnshallowDialog.value = false }
        ) {
            showUnshallowDialog.value = false

            val unshallowingText = activityContext.getString(R.string.unshallowing)

            unshallowList.value.toList().forEachBetter { curRepo ->
                doJobThenOffLoading {
                    val curRepoId = curRepo.id
                    val curRepoIdx = -1  //这个index不使用了，改用repoid更新仓库了
                    val curRepoFullPath = curRepo.fullSavePath
                    val curRepoVal =  curRepo

                    val lock = Libgit2Helper.getRepoLock(curRepoId)
                    if(isLocked(lock)) {
                        return@doJobThenOffLoading
                    }

                    lock.withLock {
                        doActAndSetRepoStatus(curRepoIdx, curRepoId, unshallowingText) {
                            Repository.open(curRepoFullPath).use { repo->
                                val ret = Libgit2Helper.unshallowRepo(
                                    repo,
                                    curRepoVal,
                                    AppModel.dbContainer.repoRepository,
                                    AppModel.dbContainer.remoteRepository,
                                    AppModel.dbContainer.credentialRepository
                                )
                                if(ret.hasError()) {
                                    Msg.requireShow(ret.msg)
                                }

                            }
                        }
                    }

                }
            }
        }
    }

    //点击某个仓库卡片上的status文案，把仓库存上，方便弹窗执行后续操作
    val statusClickedRepo =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "statusClickedRepo", RepoEntity(id=""))

    val showRequireActionsDialog = rememberSaveable { mutableStateOf(false)}
    if(showRequireActionsDialog.value) {
        val targetRepo = statusClickedRepo.value

        // invalid id
        if(targetRepo.id.isBlank()) {
            showRequireActionsDialog.value = false
            Msg.requireShow(stringResource(R.string.repo_id_invalid))
        }else {  // good repo id, show dialog
            ConfirmDialog(
                title = stringResource(R.string.require_actions),
                text = stringResource(R.string.will_go_to_changelist_then_you_can_continue_or_abort_your_merge_rebase_cherrpick),
                okBtnText = stringResource(id = R.string.ok),
                cancelBtnText = stringResource(id = R.string.cancel),
                onCancel = { showRequireActionsDialog.value = false }
            ) {
                showRequireActionsDialog.value = false

                //跳转到ChangeList页面
                goToChangeListPage(targetRepo)
            }
        }
    }



    val goToThisRepoAndHighlightingIt = goTo@{ targetId:String ->
        if(targetId.isBlank()) {
            Msg.requireShow(activityContext.getString(R.string.not_found))
            return@goTo
        }

        try {
            val list = getCurActiveList()
            val listState = getCurActiveListState()
            val targetIndex = list.toList().indexOfFirst { it.id == targetId }
            if(targetIndex != -1) {  // found in current active list
                // index / perRowCount 即条目所在的chunked后的索引行
                UIHelper.scrollToItem(scope, listState, targetIndex / repoCountEachRow)
                requireBlinkIdx.intValue = targetIndex
            }else{
                if(repoPageFilterModeOn.value) {
                    //从源列表找
                    val indexInOriginList = repoList.value.toList().indexOfFirst { it.id == targetId }

                    if(indexInOriginList != -1){  // found in origin list
                        repoPageFilterModeOn.value = false  //关闭过滤模式
                        showBottomSheet.value = false  //关闭菜单

                        //定位条目
                        UIHelper.scrollToItem(scope, repoPageListState, indexInOriginList / repoCountEachRow)
                        requireBlinkIdx.intValue = indexInOriginList  //设置条目闪烁以便用户发现
                    }else {
                        Msg.requireShow(activityContext.getString(R.string.not_found))
                    }
                }else {
                    Msg.requireShow(activityContext.getString(R.string.not_found))
                }
            }
        }catch (_:Exception) {

        }
    }

    //刷新指定仓库而不是全部（以前没想到用这种机制刷新指定仓库，对指定仓库执行完pull/sync等操作后，完全是根据id重查实现的仅刷新指定仓库，不过历史遗留问题，代码太散乱，不改了
    val refreshSpecifedRepos = { repos:List<RepoEntity> ->
        specifiedRefreshRepoList.value.clear()
        specifiedRefreshRepoList.value.addAll(repos)

        changeStateTriggerRefreshPage(needRefreshRepoPage)
    }


    val showSetUpstreamForLocalBranchDialog = rememberSaveable { mutableStateOf(false)}
    val upstreamSelectedRemote = rememberSaveable{mutableIntStateOf(0)}  //默认选中第一个remote，每个仓库至少有一个origin remote，应该不会出错
    //默认选中为上游设置和本地分支相同名
    val upstreamBranchSameWithLocal =rememberSaveable { mutableStateOf(true)}
    //把远程分支名设成当前分支的完整名
    val upstreamBranchShortRefSpec = rememberSaveable { mutableStateOf("")}
    val upstreamDialogOnOkText  =rememberSaveable { mutableStateOf("")}
    val curBranchShortNameForSetUpstreamDialog  =rememberSaveable { mutableStateOf("")}
    val curBranchFullNameForSetUpstreamDialog  =rememberSaveable { mutableStateOf("")}

    val doActAfterSetUpstreamSuccess = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "doActAfterSetUpstreamSuccess") { null }
    val setUpstreamOnFinally = mutableCustomStateOf<(()->Unit)?>(stateKeyTag, "setUpstreamOnFinally") { null }
    val showClearForSetUpstreamDialog = rememberSaveable { mutableStateOf(false) }

    val initSetUpstreamDialog: suspend (RepoEntity, String, (()->Unit)?) -> Unit = {targetRepo, onOkText, actAfterSuccess ->
        try {
            curRepo.value = targetRepo

            //为本地分支设置上游
            //设置默认值
            var remoteIdx = 0   //默认选中第一个元素
            var shortBranch = targetRepo.branch  //默认分支名为当前选中的分支短名
            var sameWithLocal = true  //默认勾选和本地分支同名，除非用户的上游不为空且有值

            Repository.open(targetRepo.fullSavePath).use { repo->

                val headRef = Libgit2Helper.resolveHEAD(repo) ?: throw RuntimeException("resolve HEAD failed")

                curBranchShortNameForSetUpstreamDialog.value = headRef.shorthand()
                curBranchFullNameForSetUpstreamDialog.value = headRef.name()

                //更新remote列表，设置upstream时用
                val remoteList = Libgit2Helper.getRemoteList(repo)
                upstreamRemoteOptionsList.value.clear()
                upstreamRemoteOptionsList.value.addAll(remoteList)


                val upstream = Libgit2Helper.getUpstreamOfBranch(repo, shortBranch)
                //若配置文件存在上游则显示清除按钮
                showClearForSetUpstreamDialog.value = upstream.remote.isNotBlank() || upstream.branchRefsHeadsFullRefSpec.isNotBlank()


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
                if(oldUpstreamShortBranchNameNoPrefix.isNotBlank()) {
                    MyLog.d(TAG,"set upstream menu item #onClick(): found old branch full refspec: ${upstream.branchRefsHeadsFullRefSpec}, short refspec: $oldUpstreamShortBranchNameNoPrefix")
                    shortBranch = oldUpstreamShortBranchNameNoPrefix
                    sameWithLocal = false  //有有效的分支值，就不勾选 same with local 了
                }


            }

            upstreamSelectedRemote.intValue = remoteIdx
            upstreamBranchShortRefSpec.value = shortBranch
            upstreamBranchSameWithLocal.value = sameWithLocal

            MyLog.d(TAG, "set upstream menu item #onClick(): after read old settings, finally, default select remote idx is:${upstreamSelectedRemote.intValue}, branch name is:${upstreamBranchShortRefSpec.value}, check 'same with local branch` is:${upstreamBranchSameWithLocal.value}")


            upstreamDialogOnOkText.value = onOkText

            //设置成功后的callback，可在设置完上游执行sync之类的
            doActAfterSetUpstreamSuccess.value = actAfterSuccess
            //若有回调，则不在finally里执行刷新页面（此时应由回调负责执行刷新），否则执行
            setUpstreamOnFinally.value = if(actAfterSuccess != null) null else { { refreshSpecifedRepos(listOf(targetRepo)) } }

            //显示弹窗
            showSetUpstreamForLocalBranchDialog.value = true

        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            createAndInsertError(targetRepo.id, "init set upstream dialog err: ${e.localizedMessage}")
            MyLog.e(TAG, "init set upstream dialog err: targetRepo='${targetRepo.repoName}', err=${e.stackTraceToString()}")
        }

    }

    if(showSetUpstreamForLocalBranchDialog.value) {
        val curRepo = curRepo.value
        SetUpstreamDialog(
            callerTag = TAG,
            curRepo = curRepo,

            // very fast, no need show loading
            loadingOn = {},
            loadingOff = {},

            onOkText = upstreamDialogOnOkText.value,
            remoteList = upstreamRemoteOptionsList.value,
            isCurrentBranchOfRepo = true,
            curBranchShortName = curBranchShortNameForSetUpstreamDialog.value, //供显示的，让用户知道在为哪个分支设置上游
            curBranchFullName = curBranchFullNameForSetUpstreamDialog.value,
            selectedOption = upstreamSelectedRemote,
            upstreamBranchShortName = upstreamBranchShortRefSpec,
            upstreamBranchShortNameSameWithLocal = upstreamBranchSameWithLocal,
            showClear = showClearForSetUpstreamDialog.value,
            closeDialog = {
                showSetUpstreamForLocalBranchDialog.value = false
            },
            onClearSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            },
            onClearErrorCallback = { e ->
                val repoId = curRepo.id
                val repoName = curRepo.repoName
                val curBranchShortName = curBranchShortNameForSetUpstreamDialog.value

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
            onSuccessCallback = {
                Msg.requireShow(activityContext.getString(R.string.set_upstream_success))
                //例如你设置完之后要执行同步啊之类的，就设置到这
                val cb = doActAfterSetUpstreamSuccess.value
                doActAfterSetUpstreamSuccess.value = null
                cb?.invoke()
            },
            onErrorCallback = onErr@{ e ->
                val repoId = curRepo.id

                val repoName = curRepo.repoName
                val upstreamSameWithLocal = upstreamBranchSameWithLocal.value
                val remoteList = upstreamRemoteOptionsList.value
                val selectedRemoteIndex = upstreamSelectedRemote.intValue
                val upstreamShortName = upstreamBranchShortRefSpec.value

                //本地分支名，就是为它设置上游
                val curBranchShortName = curBranchShortNameForSetUpstreamDialog.value

                //直接索引取值即可
                val remote = try {
                    remoteList[selectedRemoteIndex]
                } catch (e: Exception) {
                    MyLog.e(TAG,"err when get remote by index from remote list of '$repoName': remoteIndex=$selectedRemoteIndex, remoteList=$remoteList\nerr info: ${e.stackTraceToString()}")
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
            //clear后刷新当前仓库，若有success callback也不会调用，因为clear就清空了，与设置成功相反，这时就放弃调用callback了，刷新下当前仓库就完事了
            onClearFinallyCallback = { refreshSpecifedRepos(listOf(curRepo)) },

            //这个finally是否调用取决于是否有success callback，如果有，其值应为null（调用者负责执行刷新操作）；否则应刷新当前条目
            onFinallyCallback = setUpstreamOnFinally.value,
        )
    }

    val apiPullUrl = rememberSaveable { mutableStateOf("") }
    val apiPullUrlAsync = rememberSaveable { mutableStateOf("") }
    val apiPushUrl = rememberSaveable { mutableStateOf("") }
    val apiPushUrlAsync = rememberSaveable { mutableStateOf("") }
    val apiSyncUrl = rememberSaveable { mutableStateOf("") }
    val apiSyncUrlAsync = rememberSaveable { mutableStateOf("") }
    val apiAsync = rememberSaveable { mutableStateOf(false) }
    val showApiDialog2 = rememberSaveable { mutableStateOf(false) }
    fun getPullUrl() : String {
        if(apiAsync.value) {
            return apiPullUrlAsync.value
        }else {
            return apiPullUrl.value
        }
    }
    fun getPushUrl() : String {
        if(apiAsync.value) {
            return apiPushUrlAsync.value
        }else {
            return apiPushUrl.value
        }
    }
    fun getSyncUrl() : String {
        if(apiAsync.value) {
            return apiSyncUrlAsync.value
        }else {
            return apiSyncUrl.value
        }
    }

    if(showApiDialog2.value) {
        ConfirmDialog2(
            title = stringResource(R.string.api),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MyCheckBox(stringResource(R.string.async), apiAsync)

                    Spacer(Modifier.height(10.dp))

                    val pullurl = getPullUrl()
                    val pushurl = getPushUrl()
                    val syncurl = getSyncUrl()

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.pull)+": ", fontWeight = FontWeight.Bold)

                            MySelectionContainer {
                                Text(text = pullurl)
                            }
                        }

                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(.2f)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pullurl))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }

                    MyHorizontalDivider(Modifier.padding(top = 10.dp, bottom = 10.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        )  {
                            Text(stringResource(R.string.push)+": ", fontWeight = FontWeight.Bold)

                            MySelectionContainer {
                                Text(text = pushurl)
                            }
                        }

                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(.2f)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pushurl))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }

                    MyHorizontalDivider(Modifier.padding(top = 10.dp, bottom = 10.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.sync)+": ", fontWeight = FontWeight.Bold)

                            MySelectionContainer {
                                Text(text = syncurl)
                            }
                        }

                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth(.2f)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(syncurl))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }
                }
            },
            onCancel = { showApiDialog2.value = false },
            cancelBtnText = stringResource(R.string.close),
            showOk = false
        ) {}
    }






    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val detailsTitle = rememberSaveable { mutableStateOf("") }
    val detailsString = rememberSaveable { mutableStateOf("") }
    val initDetailsDialog = { title:String, text:String ->
        detailsTitle.value = title
        detailsString.value = text
        showDetailsDialog.value = true
    }
    if(showDetailsDialog.value) {
        CopyableDialog(
            title = detailsTitle.value,
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }


    // bottom bar block start
    val quitSelectionMode = {
        isSelectionMode.value = false
        selectedItems.value.clear()
    }

    val getSelectedFilesCount = {
        selectedItems.value.size
    }

    val selectedSingle = {
        getSelectedFilesCount() == 1
    }

    val hasSelectedItems = {
        getSelectedFilesCount() > 0
    }

    val containsForSelected = { srcList:List<RepoEntity>, item:RepoEntity ->
        srcList.indexOfFirst { it.equalsForSelected(item) } != -1
    }

    val switchItemSelected = { item: RepoEntity ->
        isSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItems.value, contains = containsForSelected, remove = { srcList, curItem ->
            val foundIdx = srcList.indexOfFirst { it.id == curItem.id }
            if(foundIdx != -1) {
                srcList.removeAt(foundIdx)
                // removed
                true
            }else {
                // not removed
                false
            }
        })
    }

    val selectItem = { item:RepoEntity ->
        isSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItems.value, contains = containsForSelected)
    }

    val repoCardTitleOnClick = { item:RepoEntity ->
        switchItemSelected(item)
    }

    val isRepoGoodAndActEnabled = {curRepo:RepoEntity ->
        val repoStatusGood = curRepo.gitRepoState!=null && !Libgit2Helper.isRepoStatusNotReadyOrErr(curRepo)

        val isDetached = dbIntToBool(curRepo.isDetached)
        //这个tmpStatus只是粗略判断仓库是否正在执行操作，不一定准，还是得靠lock，不过libgit2底层应该有锁，所以代码里有些地方若不确定是否需要加锁，可以不加
        //如果有设临时状态，说明在执行某个操作，比如正在fetching，所以这时应该不允许再执行fetch或pull之类的操作，我做了处理，即使用户去cl页面执行，也无法绕过此限制( ? 不确定，cl页面好像已经无视tmpStatus了，而且没加mutex，直接依靠libgit2底层的锁（好像有））
        //仅当requireAction不等于检查本地未提交修改时，tmpStatus的值才有意义，否则可能没意义。(因为如果requireAction等于检查未提交修改，这时tmpStatus有可能不为空，但检查本地未提交修改时不与pull/push之类的操作冲突，所以，这时其实仍然可以执行操作)
        val hasTmpStatus = curRepo.pendingTask != RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES && curRepo.tmpStatus.isNotBlank()
        val actionEnabled = !isDetached && !hasTmpStatus

        repoStatusGood && actionEnabled
    }

    val doActWithLockIfRepoGoodAndActEnabled = { curRepo:RepoEntity, act: suspend ()->Unit ->
        doJobThenOffLoading {
            Libgit2Helper.doActWithRepoLockIfPredicatePassed(curRepo, isRepoGoodAndActEnabled, act)
        }
    }

    val isRepoGood = {curRepo:RepoEntity ->
        curRepo.gitRepoState!=null && !Libgit2Helper.isRepoStatusNotReadyOrErr(curRepo)
    }

    val doActIfRepoGoodOrElse = { curRepo:RepoEntity, act:()->Unit, elseAct:()->Unit ->
        if(isRepoGood(curRepo)) {
            act()
        }else {
            elseAct()
        }
    }

    val doActIfRepoGood = { curRepo:RepoEntity, act:()->Unit ->
        doActIfRepoGoodOrElse(curRepo, act, {})
    }

    // No HEAD，但怕用户看不懂，所以说 no commit
    val showNoCommitDialog = rememberSaveable { mutableStateOf(false) }
    val repoNameOfNoCommitDialog = rememberSaveable { mutableStateOf("") }

    // 传仓库对象，现在用不到，日后可能用到
    val initNoCommitDialog = {curRepo:RepoEntity ->
        repoNameOfNoCommitDialog.value = curRepo.repoName
        showNoCommitDialog.value = true
    }

    if(showNoCommitDialog.value) {
        CopyableDialog2(
            title = repoNameOfNoCommitDialog.value,
            text = stringResource(R.string.repo_no_commit_note),
            onCancel = { showNoCommitDialog.value = false },
            cancelBtnText = stringResource(R.string.ok),
            //隐藏ok键
            okCompose = {}
        ) { }  // ok不执行操作，反正已经隐藏了
    }


    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false)}
//    val selectedItemsShortDetailsStr = rememberSaveable { mutableStateOf("")}
    if(showSelectedItemsShortDetailsDialog.value) {
        SelectedItemDialog(
//            detailStr = selectedItemsShortDetailsStr.value,
            selectedItems = selectedItems.value,
            formatter = {it.repoName},
            switchItemSelected = switchItemSelected,
            clearAll = {selectedItems.value.clear()},
            closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
        )
    }

    val showSelectedItems = {
//        val sb = StringBuilder()
//        selectedItems.value.toList().forEach {
//            sb.append(it.repoName).append("\n\n")
//        }
//        selectedItemsShortDetailsStr.value = sb.removeSuffix("\n").toString()
        showSelectedItemsShortDetailsDialog.value = true
    }

    // bottom bar block end




    val repoIdForErrMsgDialog = rememberSaveable { mutableStateOf("") }
    val errMsgDialogText = rememberSaveable { mutableStateOf("") }
    val showErrMsgDialog = rememberSaveable { mutableStateOf(false) }
    if(showErrMsgDialog.value) {
        val closeDialog = { showErrMsgDialog.value = false }

        CopyableDialog2(
            title = stringResource(R.string.error_msg),
            text = errMsgDialogText.value,
            // use to dismiss dialog
            onCancel = closeDialog,

            cancelCompose = {
                Row {
                    TextButton(
                        onClick = {
                            closeDialog()
                            goToErrScreen(repoIdForErrMsgDialog.value)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.all),
                        )
                    }

                    TextButton(
                        onClick = closeDialog
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                        )
                    }
                }
            }
        ) { //复制到剪贴板
            showErrMsgDialog.value=false

            clipboardManager.setText(AnnotatedString(errMsgDialogText.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }


    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val basePathNoEndSlashOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { repoDto: RepoEntity ->
        textOfItemMsgDialog.value = repoDto.latestCommitMsg
        basePathNoEndSlashOfItemMsgDialog.value = repoDto.fullSavePath
        showItemMsgDialog.value = true
    }

    if(showItemMsgDialog.value) {
        CommitMsgMarkDownDialog(
            dialogVisibleState = showItemMsgDialog,
            text = textOfItemMsgDialog.value,
            previewModeOn = previewModeOnOfItemMsgDialog,
            useSystemFonts = useSystemFontsForItemMsgDialog,
            basePathNoEndSlash = basePathNoEndSlashOfItemMsgDialog.value
        )
    }

    val initErrMsgDialog = { repoEntity: RepoEntity, errMsg:String ->
        val repoId = repoEntity.id

        //设置状态变量
        repoIdForErrMsgDialog.value = repoId
        errMsgDialogText.value = errMsg

        //显示弹窗
        showErrMsgDialog.value = true

        //清掉数据库仓库条目的错误信息
        doJobThenOffLoading {
            AppModel.dbContainer.repoRepository.updateErrFieldsById(repoId, Cons.dbCommonFalse, "")

            //这里就不刷新仓库了，暂时仍显示错误信息，除非手动刷新页面，这么设计是为了缓解用户偶然点开错误没看完就关了，再想点开发现错误信息已被清的问题
        }

        Unit
    }



    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }


    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp = exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {
        if(isSelectionMode.value){
            quitSelectionMode()
        } else if(repoPageFilterModeOn.value) {
            repoPageFilterModeOn.value = false
            resetSearchVars()
        }else {
            backHandlerOnBack()
        }
    })
    //back handler block end


    // this feel sick, screen flash then return, just sick, so disbale
//    if(isLoading.value) {
//
////        Column(
////            modifier = Modifier
////                .fillMaxSize()
////                .padding(contentPadding)
////                .verticalScroll(StateUtil.getRememberScrollState())
////            ,
////            verticalArrangement = Arrangement.Center,
////            horizontalAlignment = Alignment.CenterHorizontally,
////
////        ) {
////            Text(text = loadingText.value)
////        }
//
//        LoadingDialog(loadingText.value)
//
//    }

    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeStateTriggerRefreshPage(needRefreshRepoPage) }
    ) {

        if (repoList.value.isEmpty()) {  //无仓库，显示添加按钮
            if(isInitLoading.value) {
                FullScreenScrollableColumn(contentPadding) {
                    Text(initLoadingText.value)
                }
            }else {  // loading finished, but the repo list still empty
                val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }

                PageCenterIconButton(
                    contentPadding = contentPadding,
                    onClick = {
                        dropDownMenuExpandState.value = !dropDownMenuExpandState.value

                        //由于弹窗有隐形遮罩，所以其实点击设为true即可，不需要设为切换，
                        // 之后就算再点图标，其实也不会点到图标上而是点到遮罩上，
                        // 而是会触发弹窗的on dismiss，然后隐藏弹窗
//                        dropDownMenuExpandState.value = true
                    },
                    icon = Icons.Filled.Add,
                    text = stringResource(R.string.add_a_repo),
                    attachContent = {
                        AddRepoDropDownMenu(
                            showMenu = dropDownMenuExpandState.value,
                            closeMenu = { dropDownMenuExpandState.value = false },
                            importOnClick = {
                                showImportRepoDialog.value = true
                            }
                        )
                    }
                )
            }

        }else {  //有仓库
            //根据关键字过滤条目
            val keyword = repoPageFilterKeyWord.value.text  //关键字
            val enableFilter = filterModeActuallyEnabled(repoPageFilterModeOn.value, keyword)

            val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
            val filteredListTmp = filterTheList(
                needRefresh = filterResultNeedRefresh.value,
                lastNeedRefresh = lastNeedRefresh,
                enableFilter = enableFilter,
                keyword = keyword,
                lastKeyword = lastSearchKeyword,
                searching = searching,
                token = searchToken,
                activityContext = activityContext,
                filterList = filterList.value,
                list = repoList.value,
                resetSearchVars = resetSearchVars,
                match = { idx:Int, it: RepoEntity ->
                    it.repoName.contains(keyword, ignoreCase = true)
                            || it.branch.contains(keyword, ignoreCase = true)
                            || it.lastCommitHash.contains(keyword, ignoreCase = true)
                            || it.upstreamBranch.contains(keyword, ignoreCase = true)
                            || it.parentRepoName.contains(keyword, ignoreCase = true)
                            || it.fullSavePath.contains(keyword, ignoreCase = true)
                            || it.cachedAppRelatedPath().contains(keyword, ignoreCase = true)
                            || it.cachedLastUpdateTime().contains(keyword, ignoreCase = true)
                            || it.latestUncheckedErrMsg.contains(keyword, ignoreCase = true)
                            || it.tmpStatus.contains(keyword, ignoreCase = true)
                            || it.getOrUpdateCachedOneLineLatestCommitMsg().contains(keyword, ignoreCase = true)
                            || it.createErrMsgForView(activityContext).contains(keyword, ignoreCase = true)
                            || it.getOther().contains(keyword, ignoreCase = true)
                            || it.getRepoStateStr(activityContext).contains(keyword, ignoreCase = true)
                }
            )


            //若一行只有一个条目，fillMaxWidth()
            val requireFillMaxWidth = repoCountEachRow == 1
            //如果repoCountEachRow==1，永远不需要padding，因为list.size是整数，而任何整数以1取模结果都为0
            val paddingItemCount = filteredListTmp.size % repoCountEachRow
            val needPaddingItems = paddingItemCount != 0

            val filteredList =  filteredListTmp.chunked(repoCountEachRow)
            val lastChunkListIndex = filteredList.lastIndex


            val listState = if(enableFilter) filterListState else repoPageListState

            //更新是否启用filter
            enableFilterState.value = enableFilter

            MyLazyColumn(
                contentPadding = contentPadding,
                list = filteredList,
                listState = listState,
                requireForEachWithIndex = true,
                requirePaddingAtBottom = true
            ) {chunkedListIdx, chunkedList ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    chunkedList.forEachIndexedBetter { subListIdx, element ->
                        val idx = chunkedListIdx * repoCountEachRow + subListIdx
                        //状态小于errValStart意味着一切正常；状态大于等于errValStart，意味着出错，禁用长按功能，直接把可以执行的操作例如删除仓库和编辑仓库之类的显示在卡片上，方便用户处置出错的仓库
                        // 如果有必要细分状态，可以改成这样: if(it.workStatus==cloningStatus) go cloningCard, else if other status, go other card, else go normal RepoCard

                        //未出错的仓库和已出错的仓库都弄一起了，在组件内部会判断
                        RepoCard(
                            itemWidth = itemWidth,
                            requireFillMaxWidth = requireFillMaxWidth,
                            showBottomSheet = showBottomSheet,
                            curRepo = curRepo,
                            curRepoIndex = curRepoIndex,
                            repoDto = element,
                            repoDtoIndex = idx,
                            isSelectionMode = isSelectionMode.value,
                            itemSelected = containsForSelected(selectedItems.value, element),
                            titleOnClick = repoCardTitleOnClick,
                            pinnedRepos = settings.pinnedRepos,

                            goToFilesPage = goToFilesPage,
                            requireBlinkIdx = requireBlinkIdx,
                            pageRequest = pageRequest,
                            onClick = {
                                if (isSelectionMode.value) {  //选择模式，切换选择
                                    switchItemSelected(it)
                                }
                            },
                            onLongClick = {
                                //如果不是选择模式，则切换为选择模式
                                if (!isSelectionMode.value) {
                                    switchItemSelected(it)

                                    //如果处于选择模式，长按执行连续选择
                                }else if(isSelectionMode.value) {
                                    UIHelper.doSelectSpan(idx, it,
                                        //这里调用 toList() 是为了拷贝下源list，避免并发修改异常
                                        selectedItems.value.toList(), filteredListTmp.toList(),
                                        switchItemSelected,
                                        selectItem
                                    )
                                }
                            },
                            requireDelRepo = {curRepo -> requireDelRepo(listOf(curRepo))},
                            copyErrMsg = {msg->
                                clipboardManager.setText(AnnotatedString(msg))
                                Msg.requireShow(activityContext.getString(R.string.copied))
                            },
                            doCloneSingle = doCloneSingle,
                            initErrMsgDialog = initErrMsgDialog,
                            initCommitMsgDialog = showItemMsg,
                            refreshPage = {
                                changeStateTriggerRefreshPage(needRefreshRepoPage)
                            }

                        ) workStatusOnclick@{ clickedRepo, status ->  //这个是点击status的callback，这个status其实可以不传，因为这里的lambda能捕获到数组的元素，就是当前仓库

                            //把点击状态的仓库存下来
                            statusClickedRepo.value = clickedRepo  //其实这个clickedRepo直接用这里element替代也可，但用回调里参数感觉更合理

                            //目前status就三种状态：up-to-date/has conflicts/need sync，第1种不用处理
                            if(status == Cons.dbRepoWorkStatusMerging
                                || status == Cons.dbRepoWorkStatusRebasing
                                || status == Cons.dbRepoWorkStatusCherrypicking
                            ){ //merge/rebase/cherrypick弹窗提示需要continue或abort
                                showRequireActionsDialog.value = true
                            } else if (
                                status == Cons.dbRepoWorkStatusHasConflicts
                                || status == Cons.dbRepoWorkStatusNeedCommit
                            ) {
                                //导航到changelist并定位到当前仓库
                                goToChangeListPage(clickedRepo)
                            } else if (status == Cons.dbRepoWorkStatusNeedSync) {
                                val curRepo = clickedRepo
                                if(dbIntToBool(curRepo.isDetached)){  // detached, can't sync
                                    Msg.requireShow(activityContext.getString(R.string.sync_failed_by_detached_head))
                                }else {  // not detached HEAD
                                    if(curRepo.upstreamBranch.isBlank()) {  //无上游，先设置，再同步
                                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                            doJobThenOffLoading {
                                                initSetUpstreamDialog(curRepo, activityContext.getString(R.string.save_and_sync)) {
                                                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.syncing)) {
                                                            doActAndLogErr(curRepo, "sync") {
                                                                doSync(curRepo)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }else {  //有上游，直接同步
                                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                            doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                                doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.syncing)) {
                                                    doActAndLogErr(curRepo, "sync") {
                                                        doSync(curRepo)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if (status == Cons.dbRepoWorkStatusNeedPull) {
                                val curRepo = clickedRepo

                                doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pulling)) {
                                            doActAndLogErr(curRepo, "pull") {
                                                doPull(curRepo)
                                            }
                                        }
                                    }
                                }
                            }else if (status == Cons.dbRepoWorkStatusNeedPush) {
                                val curRepo = clickedRepo

                                doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                                    doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pushing)) {
                                        doActAndLogErr(curRepo, "push") {
                                            doPush(null, curRepo)
                                        }
                                    }
                                }
                            }else if (status == Cons.dbRepoWorkStatusNoHEAD) {
                                val curRepo = clickedRepo

                                initNoCommitDialog(curRepo)
                            }
                        }
                    }

                    // padding for make item alight to start(left or right)
                    // repoCountEachRow为1时，永远不需要padding，因为needPaddingItems在其为1时肯定为假
                    if(needPaddingItems && chunkedListIdx == lastChunkListIndex) {
                        for(i in 0 until (repoCountEachRow - chunkedList.size)) {
                            Column(modifier = Modifier.width(itemWidth.dp)) {}
                        }
                    }
                }
            }
        }
    }



    if(pageRequest.value == PageRequest.goParent) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            goToThisRepoAndHighlightingIt(curRepo.value.parentRepoId)
        }
    }



    if(isSelectionMode.value) {
        val selectionModeIconList = listOf(
            ImageVector.vectorResource(R.drawable.two_way_sync),  //sync
            Icons.Filled.Publish,  // push
            Icons.Filled.Download,  // pull
            Icons.Filled.Downloading,  // fetch
            Icons.Filled.SelectAll,  //Select All
        )

        val selectionModeIconTextList = listOf(
            stringResource(R.string.sync),
            stringResource(R.string.push),
            stringResource(R.string.pull),
            stringResource(R.string.fetch),
            stringResource(R.string.select_all),
        )

        val selectionModeIconOnClickList = listOf<()->Unit>(
            sync@{
                val needSetUpstreamBeforeSync = {curRepo:RepoEntity -> curRepo.upstreamBranch.isBlank() && !dbIntToBool(curRepo.isDetached)}
                val expectRepos = {it:RepoEntity -> it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) }
                val task = { curRepo: RepoEntity ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.syncing)) {
                            doActAndLogErr(curRepo, "sync") {
                                doSync(curRepo)
                            }
                        }
                    }
                }


                val list = selectedItems.value.toList()
                //若选中一个条目且未设置上游且非detached，则显示设置上游弹窗，然后执行同步
                if(list.size == 1) {
                    val curRepo = list.first()

                    if(expectRepos(curRepo)) {
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                            task(curRepo)
                        }
                    }else if(needSetUpstreamBeforeSync(curRepo)){ //需要设置上游
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                            doJobThenOffLoading {
                                //需要设置上游
                                initSetUpstreamDialog(curRepo, activityContext.getString(R.string.save_and_sync)) {
                                    task(curRepo)
                                }
                            }
                        }
                    }
                }else { //若选中多个条目或选中一个存在有效上游的条目，则不会弹出设置上游的弹窗，直接执行同步
                    list.filter { expectRepos(it) }.forEachBetter { curRepo ->
                        task(curRepo)
                    }
                }

                Unit
            },

            push@{
                selectedItems.value.toList().filter { it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) }.forEachBetter { curRepo ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pushing)) {
                            doActAndLogErr(curRepo, "push") {
                                doPush(null, curRepo)
                            }
                        }
                    }
                }

            },

            pull@{
                val expectRepos = {it:RepoEntity -> it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached)}
                val task = { curRepo:RepoEntity ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.pulling)) {
                            doActAndLogErr(curRepo, "pull") {
                                doPull(curRepo)
                            }
                        }
                    }
                }

                val list = selectedItems.value.toList()

                //如果只选了一个条目，检查执行任务前是否需要设置用户名和邮箱，若需要则弹窗；否则直接取出可用的仓库然后执行任务
                if(list.size == 1) {
                    val curRepo = list.first()
                    if(expectRepos(curRepo)){
                        doTaskOrShowSetUsernameAndEmailDialog(curRepo) {
                            task(curRepo)
                        }
                    }
                }else {
                    list.filter { expectRepos(it) }.forEachBetter {
                        task(it)
                    }
                }
            },

            fetch@{
                selectedItems.value.toList().filter { it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) }.forEachBetter { curRepo ->
                    doActWithLockIfRepoGoodAndActEnabled(curRepo) {
                        //fetch 当前仓库上游的remote
                        doActAndSetRepoStatus(invalidIdx, curRepo.id, activityContext.getString(R.string.fetching)) {
                            doActAndLogErr(curRepo, "fetch") {
                                doFetch(null, curRepo)
                            }
                        }
                    }
                }
            },


            selectAll@{
                val list = if(enableFilterState.value) filterList.value else repoList.value

                list.toList().forEachBetter {
                    selectItem(it)
                }

                Unit
            }
        )

        val bottomBarIconDefaultEnable =  { hasSelectedItems() && selectedItems.value.any { it.upstreamBranch.isNotBlank() && !dbIntToBool(it.isDetached) } }
        val selectionModeIconEnableList = listOf(
            syncEnable@{
                val list = selectedItems.value
                if(list.size == 1) {  //若只选中一个条目，仅判断是否detached，若无上游，弹窗设置并同步
                    //只有一个条目的时候检测下仓库是否有效，避免选中未克隆仓库仍启用此按钮；
                    //  但选中多个条目时不用检测，因为执行时会过滤出存在上游且非detached仓库，会把未克隆仓库筛掉，所以这里不用处理
                    val item = list.first()
                    //先执行轻量的detached HEAD检测，如果为真，肯定克隆并且至少曾经能用，那就没必要再执行后面的检测了
                    !dbIntToBool(item.isDetached) && isRepoReadyAndPathExist(item)
                }else { //若选中多个条目，不会弹出设置上游的弹窗，必须有至少一个存在上游的仓库才启用同步按钮
                    bottomBarIconDefaultEnable()
                }
            },
            pushEnable@{ bottomBarIconDefaultEnable() },
            pullEnable@{ bottomBarIconDefaultEnable() },
            fetchEnable@{ bottomBarIconDefaultEnable() },
            selectAllEnable@{ true },
        )

        val selectionModeMoreItemTextList = (listOf(
            stringResource(R.string.refresh), // multi
            stringResource(R.string.changelist), // single

//        stringResource(R.string.import_files),  // multi/single, 可多选，若多选会根据仓库名匹配导入
//        stringResource(R.string.export_files), // multi/single，如果多选仓库，强制为不同仓库创建文件夹名，如果单选仓库，会显示一个“清空”的按钮，若勾选，执行导入前会先清空git仓库；执行导出前会先清空输出目录。还有，这个导入导出只支持saf，非saf的可通过本app的Files页面完成。
            stringResource(R.string.user_info), // multi
            stringResource(R.string.rename), // single
            stringResource(R.string.set_upstream), // single
            stringResource(R.string.clone), // multi
            stringResource(R.string.unshallow), // multi
            stringResource(R.string.remotes), // single
            stringResource(R.string.tags),  // single
            stringResource(R.string.stash), // single
            stringResource(R.string.reflog), // single
            stringResource(R.string.submodules), // single
            stringResource(R.string.edit_config), // single
            stringResource(R.string.api), // multi
            stringResource(R.string.details), // multi
            stringResource(R.string.delete), // multi
        ))

        val selectionModeMoreItemOnClickList = (listOf(
            refresh@{
                refreshSpecifedRepos(selectedItems.value)
            },

//
//        importFiles@{
//
//        },
//
//        exportFiles@{
//
//        },

            changelist@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    goToChangeListPage(curRepo)
                }
            },

            userInfo@{
                showSetUserInfoDialog(selectedItems.value.filter { isRepoGood(it) })
            },

            rename@{
                // rename不需要检查仓库状态是否good，直接执行即可
                val selectedRepo = selectedItems.value.first()
                curRepo.value = RepoEntity()
                curRepo.value = selectedRepo

                // init rename dialog
                repoNameForRenameDialog.value = TextFieldValue(text = selectedRepo.repoName, selection = TextRange(0, selectedRepo.repoName.length))
                errMsgForRenameDialog.value = ""
                showRenameDialog.value = true
            },

            setUpstream@{
                doJobThenOffLoading {
                    initSetUpstreamDialog(selectedItems.value.first(), activityContext.getString(R.string.save), null)
                }

                Unit
            },

            // retry clone for cloned err repos
            clone@{
                doClone(selectedItems.value.filter { it.workStatus == Cons.dbRepoWorkStatusCloneErr })
            },

            unshallow@{
                //选出可以执行unshallow的list
                // unshallow不需要upstream，会针对所有remotes执行unshallow
                val unshallowableList = selectedItems.value.filter { curRepo ->
                    val repoStatusGood = curRepo.gitRepoState!=null && !Libgit2Helper.isRepoStatusNotReadyOrErr(curRepo)
                    repoStatusGood && dbIntToBool(curRepo.isShallow)
                }

                //若不为空，询问，然后执行unshallow
                if(unshallowableList.isNotEmpty()) {
                    unshallowList.value.clear()
                    unshallowList.value.addAll(unshallowableList)

                    //生成仓库名，用于显示
                    val sb = StringBuilder()
                    val suffix = ", "
                    unshallowableList.forEachBetter { sb.append(it.repoName).append(suffix) }
                    unshallowRepoNames.value = sb.removeSuffix(suffix).toString()

                    showUnshallowDialog.value = true
                }
            },

            remotes@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    //管理remote，右上角有个fetch all可fetch所有remote
                    navController.navigate(Cons.nav_RemoteListScreen+"/"+curRepo.id)
                }
            },
            tags@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    //跳转到tags页面
                    navController.navigate(Cons.nav_TagListScreen + "/" + curRepo.id)
                }
            },
            stash@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    doJobThenOffLoading {
                        goToStashPage(curRepo.id)
                    }
                }
            },
            reflog@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    navController.navigate(Cons.nav_ReflogListScreen+"/"+curRepo.id)
                }
            },

            submodules@{
                val curRepo = selectedItems.value.first()
                doActIfRepoGood(curRepo) {
                    navController.navigate(Cons.nav_SubmoduleListScreen + "/" + curRepo.id)
                }
            },

            editConfig@{
                try {
                    val curRepo = selectedItems.value.first()
                    Repository.open(curRepo.fullSavePath).use { repo ->
                        val dotGitDirPath = Libgit2Helper.getRepoGitDirPathNoEndsWithSlash(repo)
                        val configFile = File(dotGitDirPath, "config")
                        //能读取文件则代表文件存在并有权限，打开；否则提示错误
                        if(configFile.canRead()) {
                            val expectReadOnly = false
                            requireInnerEditorOpenFile(configFile.canonicalPath, expectReadOnly)
                        }else {
                            //这没必要抛异常，提示下用户就行
                            Msg.requireShowLongDuration(activityContext.getString(R.string.file_not_found))
                        }
                    }
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#editConfig err: ${e.stackTraceToString()}")
                }
            },

            //这个是多选仓库则生成可操作多仓库的url的api版本，更方便
            api@{
                val settings = SettingsUtil.getSettingsSnapshot()
                val host = settings.httpService.listenHost
                val port = settings.httpService.listenPort
                val token = settings.httpService.tokenList.let {
                    if(it.isEmpty()) "" else it.first()
                }

                val sbpull = StringBuilder("${genHttpHostPortStr(host, port.toString())}/pull?token=$token")
                val sbpush = StringBuilder("${genHttpHostPortStr(host, port.toString())}/push?token=$token")
                val sbsync = StringBuilder("${genHttpHostPortStr(host, port.toString())}/sync?token=$token")

                selectedItems.value.forEachBetter {
                    val repoNameOrId = "&repoNameOrId=${it.repoName}"
                    sbpull.append(repoNameOrId)
                    sbpush.append(repoNameOrId)
                    sbsync.append(repoNameOrId)
                }

                val apiPullUrlValue = sbpull.toString()
                val apiPushUrlValue = sbpush.toString()
                val apiSyncUrlValue = sbsync.toString()
                apiPullUrl.value = apiPullUrlValue + "&async=0"
                apiPushUrl.value = apiPushUrlValue + "&async=0"
                apiSyncUrl.value = apiSyncUrlValue + "&async=0"
                apiPullUrlAsync.value = apiPullUrlValue + "&async=1"
                apiPushUrlAsync.value = apiPushUrlValue + "&async=1"
                apiSyncUrlAsync.value = apiSyncUrlValue + "&async=1"

                showApiDialog2.value = true
            },

            //这个是每个仓库独立url的方案，没注释弹窗，仅注释了本段代码，取消注释并注释多仓库整合单url版api的代码即可启用
//        api@{
//            apiConfigBeanList.value.clear()
//            val settings = SettingsUtil.getSettingsSnapshot()
//            selectedItems.value.forEach {
//                apiConfigBeanList.value.add(genConfigDto(it, settings))
//            }
//
//            showApiDialog.value = true
//        },

            //这个是仅限单仓库且单条目单api的代码，已废弃
//        api@{
//            val sb = StringBuilder()
//            val spliter = Cons.itemDetailSpliter
//            selectedItems.value.forEach {
//                sb.append(HttpServer.getApiJson(it, SettingsUtil.getSettingsSnapshot()))
//                sb.append(spliter)
//            }
//
//            initDetailsDialog(activityContext.getString(R.string.api), sb.removeSuffix(spliter).toString())
//        },

            details@{
                val sb = StringBuilder()
                val lb = "\n"
                val spliter = Cons.itemDetailSpliter

                selectedItems.value.forEachBetter {
                    sb.append(activityContext.getString(R.string.name)).append(": ").append(it.repoName).append(lb).append(lb)
                    sb.append(activityContext.getString(R.string.id)).append(": ").append(it.id).append(lb).append(lb)
                    sb.append(activityContext.getString(R.string.state)).append(": ").append(it.getRepoStateStr(activityContext)).append(lb).append(lb)
                    sb.append(activityContext.getString(R.string.other)).append(": ").append(it.getOther())
                    sb.append(spliter)
                }

                initDetailsDialog(activityContext.getString(R.string.details), sb.removeSuffix(spliter).toString())
            },

            delete@{
                requireDelRepo(selectedItems.value.toList())
            }
        ))

        val selectionModeMoreItemEnableList = (listOf(
            refresh@{ hasSelectedItems() },

            changelist@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },
//        importFiles@{
//            //可多选，若多选，会根据导入源下的目录名和当前仓库的目录名（不是仓库名，是仓库的fullsavepath末尾的文件夹名）去匹配
//            //若选中未克隆仓库，可导入文件，然后刷新
//            hasSelectedItems()
//        },
//
//        exportFiles@ {
//            //这个无所谓了，不判断仓库是否能用了，直接如果目录存在，就导出，简化逻辑
//            hasSelectedItems()
//        },

            userInfo@{
                hasSelectedItems() && selectedItems.value.any { isRepoGood(it) }
            },

            rename@{
                selectedSingle()
            },

            setUpstream@{
                if(selectedSingle()) {
                    val item = selectedItems.value.first()
                    !dbIntToBool(item.isDetached) && isRepoReadyAndPathExist(item)
                }else {
                    false
                }
            },

            clone@{
                //至少选中一个需要克隆的仓库才显示此按钮
                hasSelectedItems() && selectedItems.value.any { it.workStatus == Cons.dbRepoWorkStatusCloneErr }
            },

            unshallow@{
                hasSelectedItems() && selectedItems.value.any { dbIntToBool(it.isShallow) }
            },

            remotes@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },

            tags@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },

            stash@{
                selectedSingle()&& isRepoGood(selectedItems.value.first())
            },

            reflog@{
                selectedSingle()&& isRepoGood(selectedItems.value.first())
            },

            submodules@{
                selectedSingle() && isRepoGood(selectedItems.value.first())
            },

            editConfig@{
                selectedSingle()
            },

            api@{
                hasSelectedItems()
            },

            details@{
                hasSelectedItems()
            },

            delete@{
                hasSelectedItems()
            }
        ))

        BottomBar(
            quitSelectionMode=quitSelectionMode,
            iconList=selectionModeIconList,
            iconTextList=selectionModeIconTextList,
            iconDescTextList=selectionModeIconTextList,
            iconOnClickList=selectionModeIconOnClickList,
            iconEnableList=selectionModeIconEnableList,
            moreItemTextList=selectionModeMoreItemTextList,
            moreItemOnClickList=selectionModeMoreItemOnClickList,
            moreItemEnableList = selectionModeMoreItemEnableList,
            moreItemVisibleList = selectionModeMoreItemEnableList,  //禁用即隐藏
            getSelectedFilesCount = getSelectedFilesCount,
            countNumOnClickEnabled = true,
            countNumOnClick = showSelectedItems,
            reverseMoreItemList = true
        )

    }


    //没换页面，但需要刷新页面，这时LaunchedEffect不会执行，就靠这个变量控制刷新页面了
//    if(needRefreshRepoPage.value) {
//        initRepoPage()
//        needRefreshRepoPage.value=false
//    }
    //compose创建时的副作用
    LaunchedEffect(needRefreshRepoPage.value) {
        try {
            val loadingText = activityContext.getString(R.string.loading)

            doJobThenOffLoading(initLoadingOn, initLoadingOff, loadingText) {
                try {
                    // 仓库页面检查仓库状态，对所有状态为notReadyNeedClone的仓库执行clone，卡片把所有状态为notReadyNeedClone的仓库都设置成不可操作，显示正在克隆loading信息
                    doInit(
                        dbContainer = dbContainer,
                        repoDtoList = repoList,
                        selectedItems = selectedItems.value,
                        quitSelectionMode = quitSelectionMode,
                        cloningText = cloningText,
                        unknownErrWhenCloning = unknownErrWhenCloning,
                        goToThisRepoId = goToThisRepoId,
                        goToThisRepoAndHighlightingIt = goToThisRepoAndHighlightingIt,
                        settings=settings,
                        refreshId=needRefreshRepoPage.value,
                        latestRefreshId = needRefreshRepoPage,
                        specifiedRefreshRepoList = specifiedRefreshRepoList.value,
                        loadingText = loadingText
                    )

                    //触发重新过滤，避免重命名或删除仓库后过滤模式下的仓库列表未更新
                    triggerReFilter(filterResultNeedRefresh)

                }catch (e:Exception) {
                    Msg.requireShowLongDuration("init Repos err: ${e.localizedMessage}")
                    MyLog.e(TAG, "#init Repos page err: ${e.stackTraceToString()}")
                }
            }

        } catch (e: Exception) {
//            LaunchedEffect job cancelled maybe?
            MyLog.e(TAG, "#LaunchedEffect err: ${e.stackTraceToString()}")
        }
    }
}

private suspend fun doInit(
    dbContainer: AppContainer,
    repoDtoList: CustomStateListSaveable<RepoEntity>,
    selectedItems: MutableList<RepoEntity>,
    quitSelectionMode:()->Unit,
    cloningText: String,
    unknownErrWhenCloning: String,
    goToThisRepoId: MutableState<String>,
    goToThisRepoAndHighlightingIt:(id:String) ->Unit,
    settings:AppSettings,
    refreshId:String,
    latestRefreshId:MutableState<String>,
    specifiedRefreshRepoList:MutableList<RepoEntity>,
    loadingText: String,
){
    val pageChanged = {
        refreshId != latestRefreshId.value
    }

    val specifiedRefreshRepoList:MutableList<RepoEntity> = if(specifiedRefreshRepoList.isNotEmpty()) {
        val copy = specifiedRefreshRepoList.toMutableList()
        specifiedRefreshRepoList.clear()  //清空以避免重复刷新，这样的话，即使出现无法刷新全部的bug，最多按两次顶栏的全部刷新也可变成刷新所有条目
        copy
    }else {
        mutableListOf()
    }

    //执行仓库页面的初始化操作
    val repoRepository = dbContainer.repoRepository
    //貌似如果用Flow，后续我更新数据库，不需要再次手动更新State数据就会自动刷新，也就是Flow会观测数据，如果改变，重新执行sql获取最新的数据，但最好还是手动更新，避免资源浪费
    val willReloadTheseRepos = specifiedRefreshRepoList.ifEmpty { repoRepository.getAll() }


    if(specifiedRefreshRepoList.isEmpty()) {
        repoDtoList.value.clear()
        // sort repos by pinned time then add to repo dto list
        repoDtoList.value.addAll(sortReposByPinnedTime(willReloadTheseRepos, settings))
    }else {
        val spCopy = specifiedRefreshRepoList.toList()
        specifiedRefreshRepoList.clear()
        //重查指定列表的仓库信息
        spCopy.forEachBetter forEach@{
            val reQueriedRepo = repoRepository.getById(it.id) ?: return@forEach

            specifiedRefreshRepoList.add(reQueriedRepo)
        }

        //更新仓库列表
        val newList = mutableListOf<RepoEntity>()
        //保持原始列表的顺序但替换为更新后的仓库（不过如果只调用LibgitHelper.updateRepoInfo()，那么仓库地址可能并没变化，更新要靠后面的clear()和addAll()，若从数据库重查则仓库地址会变化，但不管怎样，只要clear()再addAll()一下，最终结果应该都没问题）
        repoDtoList.value.toList().forEachBetter { i1->
            val found = specifiedRefreshRepoList.find { i2-> i2.id == i1.id }
            if(found != null) {
                newList.add(found)
            }else {
                newList.add(i1)
            }
        }

        repoDtoList.value.clear()
        repoDtoList.value.addAll(newList)
    }


    val pageChangedNeedAbort = updateSelectedList(
        selectedItemList = selectedItems,
        itemList = repoDtoList.value,
        quitSelectionMode = quitSelectionMode,
        match = { oldSelected, item-> oldSelected.id == item.id },
        pageChanged = pageChanged
    )

    // 这里本来就在最后，所以是否return没差别，但避免以后往下面加代码忘了return，这里还是return下吧
    if (pageChangedNeedAbort) return




    // complex code for update item and remove non-exist item, but the effect just no-difference with clear then addAll, so, disable it
//        if(repoDtoList.value.isEmpty()) {
//            repoDtoList.value.addAll(repoListFromDb)
//        }else if(repoListFromDb.isEmpty()){
//            repoDtoList.value.clear()
//        }else {
//            val needRemoveRepoId = mutableListOf<String>()
//            val repoListInPageCopy = repoDtoList.value.toList()
//            repoListInPageCopy.forEachIndexed { index, it->
//                var stillExist = false
//                for(repoFromDb in repoListFromDb) {
//                    if(repoFromDb.id == it.id) {
//                        // update repo info
//                        repoDtoList.value[index] = repoFromDb
//                        stillExist = true
//                        break
//                    }
//                }
//
//                if(stillExist.not()) {
//                    needRemoveRepoId.add(it.id)
//                }
//            }
//
//            needRemoveRepoId.forEach { removeId ->
//                repoDtoList.value.removeIf { removeId == it.id }
//            }
//        }




    if(goToThisRepoId.value.isNotBlank()) {
        val target = goToThisRepoId.value
        goToThisRepoId.value = ""

        goToThisRepoAndHighlightingIt(target)
    }

//        repoDtoList.requireRefreshView()
//        repoDtoList.requireRefreshView()
    //这里必须遍历 repoDtoList，不能遍历will reload那个list，不然索引可能会错（即使遍历repoDtoList，其实也可能索引会错，因为没严格的并发控制，不过一般没事）
    for ((idx,item) in repoDtoList.value.toList().withIndex()) {
        //若指定了要刷新的列表，则遵循列表，忽略其他元素
        if(specifiedRefreshRepoList.isNotEmpty() && specifiedRefreshRepoList.find { it.id == item.id } == null) {
            continue
        }

        //对需要克隆的仓库执行克隆
        if (item.workStatus == Cons.dbRepoWorkStatusNotReadyNeedClone) {
            //设置临时状态为 正在克隆...
            //我严重怀疑这里不需要拷贝元素赋值就可立即看到修改后的元素状态是因为上面addAll和这里的赋值操作刚好在视图的同一个刷新间隔里，所以到下一个刷新操作时，可看到在这修改后的状态
            repoDtoList.value[idx].tmpStatus = cloningText
//                repoDtoList.requireRefreshView()

            Libgit2Helper.cloneSingleRepo(
                targetRepo = item,
                repoDb = repoRepository,
                settings = settings,
                unknownErrWhenCloning = unknownErrWhenCloning,
                repoDtoList = repoDtoList.value,
                repoCurrentIndexInRepoDtoList = idx,
                selectedItems = selectedItems
            )


        }else if(item.pendingTask == RepoPendingTask.NEED_CHECK_UNCOMMITED_CHANGES) {
            checkGitStatusAndUpdateItemInList(
                settings = settings,
                item = item,
                idx = idx,
                repoList = repoDtoList.value,
                loadingText = loadingText,
                pageChanged = pageChanged
            )
        } else {
            //TODO: check git status with lock of every repo, get lock then query repo info from db,
            // if updatetime field changed, then update item in repodtolist, else do git status,
            // then update db and repodtolist
        }
    }


        //在这clear()很可能不管用，因为上面的闭包捕获了repoDtoList当时的值，而当时是有数据的，也就是数据在这被清，然后在上面的闭包被回调的时候，又被填充上了闭包创建时的数据，同时加上了闭包执行后的数据，所以，在这清这个list就“不管用”了，实际不是不管用，只是清了又被填充了
//                repoDtoList.clear()
}

private fun updateRepoListByIndexOrId(newItem:RepoEntity, idx: Int, list:MutableList<RepoEntity>, expectListSize:Int) {
    if(list.size == expectListSize) { //列表很可能没改变，直接赋值，若出错，刷新页面即可解决
        list[idx] = newItem
    }else {  //列表一定改变了，根据id查找仓库，若找到，更新，否则忽略
        val targetIdx = list.indexOfFirst { it.id == newItem.id }
        if(targetIdx != -1) {  // found
            list[targetIdx] = newItem
        }
    }
}

/**
 * 检查仓库是否有未提交修改并在检查完毕且页面没刷新时更新list中的对应条目
 */
private fun checkGitStatusAndUpdateItemInList(
    settings: AppSettings,
    item: RepoEntity,
    idx: Int,
    repoList: MutableList<RepoEntity>,
    loadingText: String,
    pageChanged: () -> Boolean
) {
    val funName = "checkGitStatusAndUpdateItemInList"
    val repoLock = Libgit2Helper.getRepoLock(item.id)

    //这个检查很快，不会导致阻塞
    //这里不需要检查lock，因为如果cl页面在执行操作，并不会影响这里的列表更新，虽然操作未完成的话状态有可能是错的，但这里的列表与那里的列表是完全独立的，
    //所以这里只要确保这个页面没在执行操作就行了，而这个页面执行操作会通过RepoStatusUtil更新状态，所以只检查那个状态就行了
//    if(runBlocking { isLocked(repoLock) }) {
    if(RepoStatusUtil.getRepoStatus(item.id)?.isNotBlank() == true) {
        MyLog.d(TAG, "#$funName: canceled check `git status`, because repo busy now")
        return
    }

    //用来粗略检查仓库是否已经改变
    val repoListSizeSnapshot = repoList.size

    val needUpdateTmpStatus = item.tmpStatus.isBlank()

    if(needUpdateTmpStatus) {
        val newRepo = item.copyAllFields(settings)

        //更新临时状态
        //这的状态直接更新到列表条目不会走 RepoStatusUtil 设置到Cache里，所以如果Cache里有其他状态，必然是其他任务设置的
        newRepo.tmpStatus = loadingText

        //更新列表条目
        updateRepoListByIndexOrId(newRepo, idx, repoList, repoListSizeSnapshot)
    }

    doJobThenOffLoading {
        try {
            Repository.open(item.fullSavePath).use { repo ->
                MyLog.d(TAG, "#checkRepoGitStatus: checking git status for repo '${item.repoName}'")

                val needCommit = Libgit2Helper.hasUncommittedChanges(repo)

                MyLog.d(TAG, "#checkRepoGitStatus: repoName=${item.repoName}, repoId=${item.id}, needCommit=$needCommit, pageChanged=${pageChanged()}")

                //如果页面没改变（没重新刷新） 且 仓库没有在执行其他操作（例如 pull），则 更新列表
                if(pageChanged().not() && RepoStatusUtil.getRepoStatus(item.id).let{ it == null || it.isBlank() }) {
                    val newRepo = item.copyAllFields(settings)
                    //操作已经执行完毕，清空需要执行的操作
                    newRepo.pendingTask = RepoPendingTask.NONE

                    //清空临时状态
                    if(needUpdateTmpStatus) {
                        newRepo.tmpStatus = ""
                    }

                    //如果需要提交，则更新状态为需要提交；否则检查ahead和behind状态，可能是up to date也可能需要sync
                    if(needCommit) {
                        newRepo.workStatus = Cons.dbRepoWorkStatusNeedCommit
                    }

                    updateRepoListByIndexOrId(newRepo, idx, repoList, repoListSizeSnapshot)
                }

            }
        }catch (e: Exception) {
            createAndInsertError(item.id, "check repo changes err: ${e.localizedMessage}")
            MyLog.e(TAG, "$TAG#$funName err: ${e.stackTraceToString()}")
        }
    }

}

// TODO 这个循环有点多，我假设用户仓库应该不会太多（小于20），所以性能应该问题不大，但以后可以优化下这个代码减少循环次数
private fun sortReposByPinnedTime(repos: List<RepoEntity>, settings: AppSettings): List<RepoEntity> {
    val pinnedRepos = settings.pinnedRepos
    if(pinnedRepos.isEmpty()) {
        return repos
    }

    // 排序pinned repos
    val sortedRepos = pinnedRepos.toSortedMap({ k1, k2 ->
        val r = pinnedRepos[k2]!!.compareTo(pinnedRepos[k1]!!)
        if(r != 0) {
            r
        }else {
            1
        }
    })

    // 创建 repoId to RepoEntity 的map，方便下面查询
    val reposMap = mutableMapOf<String, RepoEntity>()
    for(r in repos) {
        reposMap[r.id] = r
    }

    val repos2 = mutableListOf<RepoEntity>()
    // 先添加pinned仓库
    for(repoId in sortedRepos.keys) {
        // 一般不会为null，除非删除仓库时没更新settings里的map，不过?.let判断下保险
        reposMap[repoId]?.let {
            repos2.add(it)
        }
    }

    // 添加其余仓库
    for(r in repos) {
        if(!pinnedRepos.contains(r.id)) {
            repos2.add(r)
        }
    }

    return repos2
}
