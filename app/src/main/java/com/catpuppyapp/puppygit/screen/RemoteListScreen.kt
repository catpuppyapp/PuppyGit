package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.CenterPaddingRow
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CopyableDialog2
import com.catpuppyapp.puppygit.compose.CreateRemoteDialog
import com.catpuppyapp.puppygit.compose.FetchRemotesDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.SetBranchForRemoteDialog
import com.catpuppyapp.puppygit.compose.UnLinkCredentialAndRemoteDialogForRemoteListPage
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.createRemoteTestPassed
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dev.shallowAndSingleBranchTestPassed
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.RemoteItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.showErrAndSaveLog
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Remote
import com.github.git24j.core.Repository

private const val TAG = "RemoteListScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RemoteListScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic:HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)

    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val activityContext = LocalContext.current
    val navController = AppModel.navController
    val dbContainer = AppModel.dbContainer
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    //获取假数据
//    val list = MockData.getErrorList(repoId,1,100);
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<RemoteDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<RemoteDto>())
    val clipboardManager = LocalClipboardManager.current

//
//    SideEffect {
//        Msg.msgNotifyHost()
//    }

    //这个页面的滚动状态不用记住，每次点开重置也无所谓
    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
//    val curObjInState = rememberSaveable{ mutableStateOf(ErrorEntity()) }
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id="") )
    val curObjInState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInState", initValue = RemoteDto())
//    val showClearAllConfirmDialog = StateUtil.getRememberSaveableState(initValue = false)
//    val userIsPro = UserInfo.isPro()
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}


    val needRefresh = rememberSaveable { mutableStateOf("")}
    val showFetchAllDialog = rememberSaveable { mutableStateOf(false)}

    val showSetUrlDialog = rememberSaveable { mutableStateOf(false)}
    val isPushUrl = rememberSaveable { mutableStateOf(false)}
    val urlTextForSetUrlDialog = rememberSaveable { mutableStateOf("")}
    val oldUrlTextForSetUrlDialog = rememberSaveable { mutableStateOf("")}
    val urlErrMsg = rememberSaveable { mutableStateOf( "")}

    val showUnlinkCredentialDialog = rememberSaveable { mutableStateOf(false)}
    val showSetBranchDialog = rememberSaveable { mutableStateOf( false)}

    val defaultLoadingText = stringResource(R.string.loading)
    val isLoading = rememberSaveable { mutableStateOf( false)}
    val loadingText = rememberSaveable { mutableStateOf(defaultLoadingText)}
    val loadingOn = {msg:String ->
        loadingText.value=msg
        isLoading.value=true
    }
    val loadingOff = {
        isLoading.value=false
        loadingText.value = defaultLoadingText
    }

    val showCreateRemoteDialog = rememberSaveable { mutableStateOf(false)}
    val remoteNameForCreate = rememberSaveable { mutableStateOf( "")}
    val remoteUrlForCreate = rememberSaveable { mutableStateOf("")}

    if(showCreateRemoteDialog.value) {
        CreateRemoteDialog(
            show = showCreateRemoteDialog,
            curRepo = curRepo.value,
            remoteName = remoteNameForCreate,
            remoteUrl = remoteUrlForCreate,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            onErr = { e->
                val actionDesc = "create remote"
                val errMsgPrefix = "$actionDesc err: remoteName='${remoteNameForCreate.value}', remoteUrl=${remoteUrlForCreate.value}, err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#CreateRemoteDialog: $errMsgPrefix${e.stackTraceToString()}")
            },
            onFinally = {
                changeStateTriggerRefreshPage(needRefresh)
            }
        )
    }


    val renameFailedRefs = rememberSaveable { mutableStateOf("") }
    val showRenameFailedRefsDialog = rememberSaveable { mutableStateOf(false) }
    if(showRenameFailedRefsDialog.value) {
        CopyableDialog2(
            title = stringResource(R.string.error),
            text = renameFailedRefs.value,
            onCancel = { showRenameFailedRefsDialog.value = false }
        ) {
            showRenameFailedRefsDialog.value = false

            val renameFailedRefsStr = renameFailedRefs.value
            renameFailedRefs.value = ""
            val renameFailedRefs = Unit  // avoid mistake use
            clipboardManager.setText(AnnotatedString(renameFailedRefsStr))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }

    val showRenameDialog = rememberSaveable { mutableStateOf(false) }
    val oldName = rememberSaveable { mutableStateOf("") }
    val newName = rememberSaveable { mutableStateOf("") }
    if(showRenameDialog.value) {
        ConfirmDialog2(
            okBtnEnabled = newName.value.isNotBlank(),
            title = stringResource(R.string.rename),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Row {
                            Text(text = stringResource(id = R.string.remote)+": ")
                            Text(text = curObjInState.value.remoteName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newName.value,
                        singleLine = true,
                        onValueChange = {
                            newName.value = it
                        },
                        label = { Text(stringResource(R.string.new_name)) }
                    )
                }
            },
            onCancel = { showRenameDialog.value = false }
        ) onOK@{
            showRenameDialog.value = false
            val newName = newName.value.trim()
            val oldName = oldName.value
            renameFailedRefs.value = ""

            if(newName == oldName) {
                return@onOK
            }

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val problems = Remote.rename(repo, oldName, newName)
                        if(problems.isNotEmpty()) {
                            renameFailedRefs.value = "Below refs rename failed, you need to edit config by yourself:\n\n${problems.joinToString("\n\n")}\n"
                            showRenameFailedRefsDialog.value = true
                        }else { //成功
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }

                }catch (e: Exception) {
                    val actionDesc = "rename remote"
                    val errMsgPrefix = "$actionDesc err: remoteName='$oldName', newName='$newName' err="
                    Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                    createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                    MyLog.e(TAG, "$errMsgPrefix${e.stackTraceToString()}")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }


    val showDelRemoteDialog = rememberSaveable { mutableStateOf(false) }
    if(showDelRemoteDialog.value) {
        val remoteWillDel = curObjInState.value
        val remoteNameWillDel = remoteWillDel.remoteName
        ConfirmDialog(
            title = stringResource(id = R.string.delete) +" '$remoteNameWillDel'",
            text = stringResource(id = R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDelRemoteDialog.value = false }
        ) {
            showDelRemoteDialog.value = false

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.deleting)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        //删git仓库中的remote
                        val ret = Libgit2Helper.delRemote(repo, remoteNameWillDel)

                        val noExist = ret.msg.startsWith("remote") && ret.msg.endsWith("does not exist")
                        //如果从git仓库删除remote成功或者错误信息是 “remote 'xxx' does not exist”，则删除db中的remote
                        if(ret.success() || noExist) {
                            //删db中的remote
                            val remoteDb = AppModel.dbContainer.remoteRepository
                            remoteDb.delete(RemoteEntity(id=remoteWillDel.remoteId))  // delete其实是根据id删除的，所以只设id即可
                        }else{
                            throw ret.exception ?: Exception(ret.msg)
                        }

                        if(noExist){  //remote在git仓库不存在，但同时悄悄删除了db中的remote
                            Msg.requireShowLongDuration(ret.msg)
                        }else{ //成功
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }

                }catch (e:Exception) {
                    val actionDesc = "delete remote"
                    val errMsgPrefix = "$actionDesc err: remoteName='$remoteNameWillDel', err="
                    Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                    createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                    MyLog.e(TAG, "$errMsgPrefix${e.stackTraceToString()}")
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }

    val showViewDialog = rememberSaveable { mutableStateOf(false)}
    val viewDialogText = rememberSaveable { mutableStateOf("")}
    if(showViewDialog.value) {
        CopyableDialog(
            title = stringResource(id = R.string.remote_info),
            text = viewDialogText.value,
            onCancel = {
                showViewDialog.value=false
            }
        ) { //复制到剪贴板
            showViewDialog.value=false
            clipboardManager.setText(AnnotatedString(viewDialogText.value))
            Msg.requireShow(activityContext.getString(R.string.copied))

        }
    }

    if(showSetBranchDialog.value) {
        SetBranchForRemoteDialog(
            stateKeyTag = stateKeyTag,
            curRepo = curRepo.value,
            remoteName = curObjInState.value.remoteName,
            isAllInitValue = curObjInState.value.branchMode == Cons.dbRemote_Fetch_BranchMode_All,
            onCancel = {showSetBranchDialog.value=false},
        ) { remoteName:String, isAll: Boolean, branchCsvStr: String ->
            showSetBranchDialog.value=false

            doJobThenOffLoading onOk@{
                Repository.open(curRepo.value.fullSavePath).use { repo->
                    val config = Libgit2Helper.getRepoConfigForWrite(repo)
                    val ret = if(isAll) {
                        Libgit2Helper.setRemoteFetchRefSpecToGitConfig(
                            config = config,
                            fetch_BranchMode = Cons.dbRemote_Fetch_BranchMode_All,
                            remote = remoteName,
                            branchOrBranches = Cons.gitFetchAllBranchSign,
                            appContext = activityContext
                            )
                    }else {
                        Libgit2Helper.setRemoteFetchRefSpecToGitConfig(
                            config=config,
                            fetch_BranchMode = Cons.dbRemote_Fetch_BranchMode_CustomBranches,
                            remote = remoteName,
                            branchOrBranches = branchCsvStr,
                            appContext = activityContext
                        )

                    }

                    if(ret.hasError()) {
                        Msg.requireShowLongDuration(ret.msg)
                    }else{
                        Msg.requireShow(activityContext.getString(R.string.saved))
                    }

                    changeStateTriggerRefreshPage(needRefresh)

                }

            }
        }
    }

    if(showFetchAllDialog.value) {
        FetchRemotesDialog(
            title = stringResource(R.string.fetch_all),
            text = stringResource(R.string.fetch_all_are_u_sure),
            remoteList = list.value,
            closeDialog = { showFetchAllDialog.value = false },
            curRepo = curRepo.value,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { changeStateTriggerRefreshPage(needRefresh) },
        )
    }

    if(showSetUrlDialog.value) {
        ConfirmDialog(
            okBtnEnabled = isPushUrl.value || urlTextForSetUrlDialog.value.isNotEmpty(),
            title = if(isPushUrl.value) stringResource(id = R.string.set_push_url) else stringResource(id = R.string.set_url),
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Row {
                            Text(text = stringResource(id = R.string.remote)+": ")
                            Text(text = curObjInState.value.remoteName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),

                        value = urlTextForSetUrlDialog.value,
                        singleLine = true,
                        isError = urlErrMsg.value.isNotEmpty(),
                        supportingText = {
                            if (urlErrMsg.value.isNotEmpty()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = urlErrMsg.value,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (urlErrMsg.value.isNotEmpty()) {
                                Icon(imageVector=Icons.Filled.Error,
                                    contentDescription=urlErrMsg.value,
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        onValueChange = {
                            urlTextForSetUrlDialog.value = it
                            urlErrMsg.value="" //值一改变就清空错误信息，再点保存再检测，若有错误会再设置上
                        },
                        label = {
                            Text(if(isPushUrl.value) stringResource(id = R.string.push_url) else stringResource(R.string.url))
                        }
                    )

                    if(isPushUrl.value) {
                        MySelectionContainer {
                            Text(text = stringResource(R.string.leave_it_empty_will_use_url), color=MyStyleKt.TextColor.getHighlighting())
                        }
                    }
                }
            },
            onCancel = { showSetUrlDialog.value = false }
        ) onOk@{
            val remoteName = curObjInState.value.remoteName
            val remoteId = curObjInState.value.remoteId
            val newUrl = urlTextForSetUrlDialog.value
            val oldUrl = oldUrlTextForSetUrlDialog.value
            val repoFullPath = curRepo.value.fullSavePath

            //log use
            val setUrlPrefix = if(isPushUrl.value) "set pushUrl" else "set url"

            try {
                //pushUrl可为空，代表使用url；但如果非pushUrl，则强制不可为空
                if(!isPushUrl.value && newUrl.isBlank()) {
                    urlErrMsg.value = activityContext.getString(R.string.err_url_is_empty)
                    return@onOk
                }

                //新旧url相同，直接不保存就行了，但也不用报错
                if(newUrl == oldUrl) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.url_not_changed))
                    urlErrMsg.value=""
                    showSetUrlDialog.value=false
                    return@onOk
                }

                //清空pushUrl
                if(isPushUrl.value && newUrl.isBlank()) {
                    //弹窗可关了
                    showSetUrlDialog.value=false

                    //删除配置文件pushUrl字段
                    //设数据库pushUrl字段为空
                    doJobThenOffLoading {
                        try {
                            Repository.open(repoFullPath).use { repo->
                                //删除配置文件中的pushurl字段，若出错且错误不是key不存在，会抛异常
                                Libgit2Helper.deletePushUrl(Libgit2Helper.getRepoConfigForWrite(repo), remoteName)
                            }

                            // 更新数据库
                            val remoteDb = AppModel.dbContainer.remoteRepository
                            remoteDb.updatePushUrlById(remoteId, "")  //清空pushUrl

                            //操作完成

                            //提示成功
                            Msg.requireShow(activityContext.getString(R.string.success))

                        }catch (e:Exception) {
                            val err1 = e.localizedMessage ?: activityContext.getString(R.string.unknown_err)
                            Msg.requireShowLongDuration(err1)
                            val errWillSave = "$setUrlPrefix for remote '$remoteName' err (delete pushUrl): $err1"
                            createAndInsertError(repoId, errWillSave)
                        }finally {
                            //刷新页面
                            changeStateTriggerRefreshPage(needRefresh)
                        }

                    }

                    return@onOk
                }

//                执行到这，不管是设置url还是pushUrl，newUrl百分百都不为空或全空白字符串

                //设置非空的url或pushUrl
                //解析url，捎带检测url是否是有效uri

                //执行到这里，解析url成功且url不为空

                //关闭弹窗
                showSetUrlDialog.value=false

                //执行到这，url修改了，且不为空，且和旧值不同，需要执行保存
                //执行保存
                doJobThenOffLoading {
                    try {
                        //先更新配置文件，然后再更新db，因为在这里db相当于缓存，而配置文件是正式数据，应确保正式数据成功更新后再更新缓存
                        Repository.open(repoFullPath).use { repo ->
                            if(isPushUrl.value){
                                Remote.setPushurl(repo, remoteName, newUrl)
                            }else{
                                Remote.setUrl(repo, remoteName, newUrl)
                            }
                        }

                        //如果上面配置文件的代码抛异常，会抛异常，也就不会更新数据库了

                        // 更新数据库
                        val remoteDb = AppModel.dbContainer.remoteRepository
                        if(isPushUrl.value){
                            remoteDb.updatePushUrlById(remoteId, newUrl)
                        }else {
                            //这里不用开事务，因为里面就一个dao操作而那个dao已经开了事务，只有多个db操作弄到一起时才有必要在外部开事务
                            //为了和配置文件一致，数据库保存的是 用户输入转成的url再转成的uri再转成的字符串，可能包含需编码的内容时这么转有意义？
                            remoteDb.updateRemoteUrlById(remoteId, newUrl, requireTransaction=false)
                        }

                        //操作完成

                        //提示成功
                        Msg.requireShow(activityContext.getString(R.string.success))

                    }catch (e:Exception) {
                        val err1 = (e.localizedMessage ?: activityContext.getString(R.string.unknown_err))
                        Msg.requireShowLongDuration(err1)
                        val errWillSave = "$setUrlPrefix for remote '$remoteName' err: $err1"
                        createAndInsertError(repoId, errWillSave)
                    }finally {
                        //刷新页面
                        changeStateTriggerRefreshPage(needRefresh)
                    }
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "$setUrlPrefix in RemoteList err: remoteName=${remoteName}, newUrl=$newUrl, oldUrl=$oldUrl, err is:\n${e.stackTraceToString()}")
                val err1 = e.localizedMessage ?: activityContext.getString(R.string.unknown_err)

                //如果弹窗没关，在弹窗显示错误，如果关了，记到数据库
                if(showSetUrlDialog.value) {  //弹窗开着，直接在弹窗显示错误信息
                    urlErrMsg.value = err1  //设置错误信息
                }else { //弹窗已关闭，出了错误
                    urlErrMsg.value=""  //弹窗已经关了，所以清下错误信息

                    //显示下错误
                    Msg.requireShowLongDuration(err1)

                    //记录错误到数据库
                    val errWillSave = "$setUrlPrefix for remote '$remoteName' err: $err1"
                    doJobThenOffLoading {
                        createAndInsertError(repoId, errWillSave)
                    }

                    //刷新下页面(话说，同一个地方出错应该确保只刷新一次吗？如果需要，可在出错的代码块里获取一个随机id，然后刷新的时候指定那个id，这样就能确保在这个代码块出错时，只请求刷新一次了）
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        }
    }

    if(showUnlinkCredentialDialog.value) {
        UnLinkCredentialAndRemoteDialogForRemoteListPage(
            remoteId = curObjInState.value.remoteId,
            remoteName = curObjInState.value.remoteName,
            onCancel = { showUnlinkCredentialDialog.value = false }
        ) {
            showUnlinkCredentialDialog.value=false
            changeStateTriggerRefreshPage(needRefresh)
        }
    }

    val doFetch:suspend (String?,RepoEntity)->Boolean = doFetch@{remoteNameParam:String?, curRepo:RepoEntity ->  //参数的remoteNameParam如果有效就用参数的，否则自己查当前head分支对应的remote
        //x 废弃，逻辑已经改了) 执行isReadyDoSync检查之前要先do fetch，想象一下，如果远程创建了一个分支，正好和本地的关联，但如果我没先fetch，那我检查时就get不到那个远程分支是否存在，然后就会先执行push，但可能远程仓库已经领先本地了，所以push也可能失败，但如果先fetch，就不会有这种问题了
        var fetchSuccessRetVal = false
        try {
            Repository.open(curRepo.fullSavePath).use { repo ->
                //fetch成功返回true，否则返回false
                val remoteName = remoteNameParam
                if (remoteName.isNullOrBlank()) {
                    throw RuntimeException(activityContext.getString(R.string.remote_name_is_invalid))
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

                Libgit2Helper.fetchRemoteForRepo(repo, remoteName, credential, curRepo)
            }

            // 更新修改workstatus的时间，只更新时间就行，状态会在查询repo时更新
            val repoDb = AppModel.dbContainer.repoRepository
            repoDb.updateLastUpdateTime(curRepo.id, getSecFromTime())

            Msg.requireShow(activityContext.getString(R.string.success))

            fetchSuccessRetVal = true
        } catch (e: Exception) {
            //记录到日志
            //显示提示
            //保存数据库(给用户看的，消息尽量简单些)
            showErrAndSaveLog(
                TAG,
                "#doFetch() from RemoteList Page err: " + e.stackTraceToString(),
                "fetch err: " + e.localizedMessage,
                Msg.requireShowLongDuration,
                curRepo.id
            )

            fetchSuccessRetVal = false
        }

        return@doFetch fetchSuccessRetVal
    }

    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }

    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false)}

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


    // 向下滚动监听，开始
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }

    val filterListState = rememberLazyListState()
//    val filterListState = mutableCustomStateOf(
//        keyTag = stateKeyTag,
//        keyName = "filterListState",
//        LazyListState(0,0)
//    )
    val enableFilterState = rememberSaveable { mutableStateOf(false)}
//    val firstVisible = remember { derivedStateOf { if(enableFilterState.value) filterListState.value.firstVisibleItemIndex else lazyListState.firstVisibleItemIndex } }
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
//                lazyListState.firstVisibleItemIndex
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

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }

    BackHandler {
        if(filterModeOn.value) {
            filterModeOn.value = false
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
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        val repoName = curRepo.value.repoName
                        Column (modifier = Modifier.combinedClickable(onDoubleClick = {
                            defaultTitleDoubleClick(scope, lazyListState, lastPosition)
                        }) {
                            //onClick
                            showTitleInfoDialog.value = true
                        }){
                            Text(
                                text= stringResource(R.string.remotes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            //"[Index]|Merging" or "[Index]"
                            Text(text = "[$repoName]",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = MyStyleKt.Title.secondLineFontSize
                            )
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
                    if(!filterModeOn.value) {
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
                           //刷新
                            changeStateTriggerRefreshPage(needRefresh)
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.fetch_all),
                            icon =  Icons.Filled.Downloading,
                            iconContentDesc = stringResource(R.string.fetch_all),

                        ) {
                           //刷新
                            showFetchAllDialog.value = true
                        }

                        // add more remotes is a pro feature
                        if(proFeatureEnabled(createRemoteTestPassed)) {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.create),
                                icon =  Icons.Filled.Add,
                                iconContentDesc = stringResource(R.string.create),
                            ) {
                                showCreateRemoteDialog.value = true
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
                    listState = lazyListState,
                    filterListLastPosition = filterLastPosition,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )

            }
        }
    ) { contentPadding ->

        if(showBottomSheet.value) {
            BottomSheet(showBottomSheet, sheetState, curObjInState.value.remoteName) {
                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text= stringResource(R.string.fetch)){
                    //e.g. "fetching origin..."
                    val fetchingRemoteLoadingText = replaceStringResList(activityContext.getString(R.string.fetching_remotename), listOf(curObjInState.value.remoteName))
                    //执行fetch
                    doJobThenOffLoading(loadingOn, loadingOff, fetchingRemoteLoadingText) {
                        doFetch(curObjInState.value.remoteName, curRepo.value)
                        //刷新页面
                        changeStateTriggerRefreshPage(needRefresh)
                    }
                }
                if(dev_EnableUnTestedFeature || shallowAndSingleBranchTestPassed) {
                    val isPro = UserUtil.isPro()
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, enabled = isPro,
                        text=if(isPro) stringResource(R.string.set_branch_mode) else stringResource(R.string.set_branch_mode_pro_only)
                    ){
                        showSetBranchDialog.value=true
                    }
                }
                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.set_url)){
                    //设置remote url
                    //设置回显上次的url
                    val oldUrl = curObjInState.value.remoteUrl
                    urlTextForSetUrlDialog.value = oldUrl  //用来存储用户输入的url的状态变量
                    oldUrlTextForSetUrlDialog.value = oldUrl //保存旧值，在这设置后就不会更新了，这个变量用来在保存前检查新旧url是否相同，若相同就不更新了
                    isPushUrl.value = false

                    //清空错误信息
                    urlErrMsg.value=""
                    //显示弹窗
                    showSetUrlDialog.value=true
                }

                //set push url和link credential为pro功能，正好这两个选项挨在一起，所以放一个if判断里了
                if(UserUtil.isPro()) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.set_push_url)){
                        //设置pushurl
                        //设置回显上次的url
                        val oldUrl = if(curObjInState.value.pushUrlTrackFetchUrl) "" else curObjInState.value.pushUrl
                        urlTextForSetUrlDialog.value = oldUrl  //用来存储用户输入的url的状态变量
                        oldUrlTextForSetUrlDialog.value = oldUrl //保存旧值，在这设置后就不会更新了，这个变量用来在保存前检查新旧url是否相同，若相同就不更新了
                        isPushUrl.value = true

                        //清空错误信息
                        urlErrMsg.value=""
                        //显示弹窗
                        showSetUrlDialog.value=true
                    }

                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.link_credential)){
                        //跳转到凭据列表页面
                        //curObjInState当初应命名为“LongPressedItem”之类的名字，现在这我经常分不清哪个状态变量存储长按条目
                        navController.navigate(Cons.nav_CredentialManagerScreen+"/${curObjInState.value.remoteId}")
                    }

                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.rename)){
                        oldName.value = curObjInState.value.remoteName
                        newName.value = ""
                        showRenameDialog.value = true
                    }
                }

//              //20241130: 改成前往凭据关联页面绑定None实现解绑凭据了
//                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.unlink_credential)){
//                    //解除关联凭据
//                    showUnlinkCredentialDialog.value=true
//                }

                //添加remote和删除remote配套，都是pro feature，不能只开放其中一个，不然会有问题，想像一下，你就一个remote，而你删了它，但没法创建，是不是很不合理？所以删除和添加应该配套，一个是pro feature另一个也必须是
                if(proFeatureEnabled(createRemoteTestPassed)) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()){
                        showDelRemoteDialog.value=true
                    }
                }

            }
        }

        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {

            if(isLoading.value || list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    if(isLoading.value) {
                        Text(text = loadingText.value)
                    }else {
                        Row {
                            Text(text = stringResource(R.string.item_list_is_empty))
                        }

                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText =  stringResource(R.string.create),
                            ) {
                                showCreateRemoteDialog.value = true
                            }
                        }
                    }
                }
            }else {
                //根据关键字过滤条目
                val keyword = filterKeyword.value.text  //关键字
                val enableFilter = filterModeActuallyEnabled(filterModeOn.value, keyword)

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
                    match = { idx:Int, it: RemoteDto ->
                        it.remoteName.contains(keyword, ignoreCase = true)
                                || it.remoteUrl.contains(keyword, ignoreCase = true)
                                || it.pushUrl.contains(keyword, ignoreCase = true)
                                || it.credentialName?.contains(keyword, ignoreCase = true) == true
                                || it.pushCredentialName?.contains(keyword, ignoreCase = true) == true
                                || it.branchListForFetch.toString().contains(keyword, ignoreCase = true)
                    }
                )


                val listState = if(enableFilter) filterListState else lazyListState
//            if(enableFilter) {  //更新filter列表state
//                filterListState.value = listState
//            }
                //更新是否启用filter
                enableFilterState.value = enableFilter

                MyLazyColumn(
                    contentPadding = contentPadding,
                    list = list,
                    listState = listState,
                    requireForEachWithIndex = true,
                    requirePaddingAtBottom = true
                ) {idx,it->
                    //在这个组件里更新了 state curObj，所以长按后直接用curObj就能获取到当前对象了
                    RemoteItem(showBottomSheet,curObjInState,idx,it, lastClickedItemKey){ //onClick
                        //生成要显示的字符串
                        val sb = StringBuilder()
                        sb.append(activityContext.getString(R.string.name)+": "+it.remoteName)
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.url)+": "+it.remoteUrl)
                        sb.appendLine()
                        sb.appendLine()

//                    sb.append(appContext.getString(R.string.push_url)+": "+(it.pushUrl.ifEmpty { it.remoteUrl }))  // no more check pushUrl empty need after 20241007
                        // after 20241007, if need, pushUrl will replaced to fetch url when querying, so reached here, directly show pushUrl is ok
                        sb.append(activityContext.getString(R.string.push_url)+": "+it.pushUrl)

                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.fetch_credential)+": "+it.getLinkedFetchCredentialName())
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.push_credential)+": "+it.getLinkedPushCredentialName())
                        sb.appendLine()
                        sb.appendLine()
                        sb.append(activityContext.getString(R.string.branch_mode)+": "+(if(it.branchMode == Cons.dbRemote_Fetch_BranchMode_All) activityContext.getString(R.string.all) else activityContext.getString(R.string.custom)))
                        if(it.branchMode != Cons.dbRemote_Fetch_BranchMode_All) {
                            sb.appendLine()
                            sb.appendLine()
                            sb.append((if(it.branchListForFetch.size > 1) activityContext.getString(R.string.branches) else activityContext.getString(R.string.branch)) +": ${it.branchListForFetch}")
                        }


                        //更新状态，然后显示弹窗
                        viewDialogText.value = sb.toString()
                        showViewDialog.value = true
                    }
                    MyHorizontalDivider()
                }

            }
        }

    }

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {
        try {
            if(repoId.isNotBlank()) {
                doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb == null) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.repo_id_invalid))
                        return@doJobThenOffLoading
                    }

                    curRepo.value = repoFromDb

                    val remoteDb = AppModel.dbContainer.remoteRepository
                    val listFromDb = remoteDb.getRemoteDtoListByRepoId(repoId)

                    //改成在数据库查询函数里更新了
//                    Repository.open(repoFromDb.fullSavePath).use { repo->
//                        updateRemoteDtoList(repo, listFromDb)
//                    }

                    list.value.clear()
                    list.value.addAll(listFromDb)

                    triggerReFilter(filterResultNeedRefresh)
                }

            }
        } catch (e: Exception) {
            MyLog.e(TAG, "$TAG#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }


}
