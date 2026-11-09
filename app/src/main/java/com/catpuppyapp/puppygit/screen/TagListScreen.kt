package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.AskGitUsernameAndEmailDialogWithSelection
import com.catpuppyapp.puppygit.compose.BottomBar
import com.catpuppyapp.puppygit.compose.CenterPaddingRow
import com.catpuppyapp.puppygit.compose.CheckoutDialog
import com.catpuppyapp.puppygit.compose.CheckoutDialogFrom
import com.catpuppyapp.puppygit.compose.CommitMsgMarkDownDialog
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CreateTagDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ResetDialog
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SelectedItemDialog
import com.catpuppyapp.puppygit.compose.TagFetchPushDialog
import com.catpuppyapp.puppygit.compose.getDefaultCheckoutOption
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.git.TagDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.TagItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.fromTagToCommitHistory
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doActIfIndexGood
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.time.TimeZoneUtil
import com.catpuppyapp.puppygit.utils.updateSelectedList
import com.github.git24j.core.Repository

private const val TAG = "TagListScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TagListScreen(
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
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }
    val clipboardManager = LocalClipboardManager.current

    val inDarkTheme = Theme.inDarkTheme






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





    //获取假数据
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<TagDto>())

    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<TagDto>())

    //这个页面的滚动状态不用记住，每次点开重置也无所谓
    val listState = rememberLazyListState()
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))

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



    val nameOfNewTag = rememberSaveable { mutableStateOf("")}
    val overwriteIfNameExistOfNewTag = rememberSaveable { mutableStateOf(false)}
    val showDialogOfNewTag = rememberSaveable { mutableStateOf(false)}
    val hashOfNewTag = rememberSaveable { mutableStateOf("HEAD")}  // set init value to HEAD
    val msgOfNewTag = rememberSaveable { mutableStateOf("")}
//    val requireUserInputHashOfNewTag = StateUtil.getRememberSaveableState(initValue = false)
    val annotateOfNewTag = rememberSaveable { mutableStateOf(false)}
    val initNewTagDialog = { hash:String ->
//        hashOfNewTag.value = hash  //这里不重置hash值了，感觉不重置用户体验更好？

        doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
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
            force = overwriteIfNameExistOfNewTag
        ) {
            changeStateTriggerRefreshPage(needRefresh)
        }
    }

    // BottomBar相关变量，开始
    val multiSelectionMode = rememberSaveable { mutableStateOf(false) }
    val selectedItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "selectedItemList", listOf<TagDto>())

    val getSelectedFilesCount = {
        selectedItemList.value.size
    }

    val quitSelectionMode = {
        multiSelectionMode.value=false  //关闭选择模式
        selectedItemList.value.clear()  //清空选中文件列表
    }

    // BottomBar相关变量，结束

    //多选模式相关函数，开始
    val switchItemSelected = { item: TagDto ->
        //如果元素不在已选择条目列表则添加
        UIHelper.selectIfNotInSelectedListElseRemove(item, selectedItemList.value)
        //开启选择模式
        multiSelectionMode.value = true
    }

    val selectItem = { item:TagDto ->
        multiSelectionMode.value = true
        UIHelper.selectIfNotInSelectedListElseNoop(item, selectedItemList.value)
    }

    val isItemInSelected= { item:TagDto ->
        selectedItemList.value.contains(item)
    }
    // 多选模式相关函数，结束


    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}


    // reset start
//    val acceptHardReset = StateUtil.getRememberSaveableState(initValue = false)
    val resetOid = rememberSaveable { mutableStateOf("") }
    val showResetDialog = rememberSaveable { mutableStateOf(false) }
    val closeResetDialog = {
        showResetDialog.value = false
    }
    val initResetDialog = { resetOidParam:String ->
        //初始化弹窗默认选项
//        acceptHardReset.value = false
        resetOid.value = resetOidParam
        showResetDialog.value = true
    }

    if (showResetDialog.value) {
        //调用者需确保至少选中一个条目，不然会报错，这里由界面“没选中任何条目则禁用选项”的逻辑来控制，所以不需要判断
        val item = selectedItemList.value.first()

        ResetDialog(
            fullOidOrBranchOrTag = resetOid,
            closeDialog=closeResetDialog,
            repoFullPath = curRepo.value.fullSavePath,
            repoId=repoId,
            refreshPage = { _, isDetached, _, ->
                //更新下仓库信息以使title在仓库为detached HEAD时显示出reset后的hash。非detached HEAD时只是更新分支指向的提交号分支本身不变，所以不用更新
                if(isDetached) {
                    curRepo.value = curRepo.value.let {
                        it.copyAllFields(
                            settings,

                            it.copy(
                                isDetached = Cons.dbCommonTrue,
                                lastCommitHash = item.targetFullOidStr
                            ),
                        )
                    }
                }
            }
        )
    }
    // reset end


    //checkout start
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false) }
    val invalidCurItemIndex = -1  //本页面不要更新被选中执行checkout的条目，所以设个无效id即可

    //初始化 checkout对话框
    val initCheckoutDialogComposableVersion = {
        showCheckoutDialog.value = true
    }

    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(getDefaultCheckoutOption(false)) }

    if(showCheckoutDialog.value) {
        val item = selectedItemList.value.first()

        CheckoutDialog(
            checkoutSelectedOption = checkoutSelectedOption,

            showCheckoutDialog=showCheckoutDialog,
            branchName = branchNameForCheckout,
            from = CheckoutDialogFrom.OTHER,
//            expectCheckoutType = Cons.checkoutTypeCommit,  //用这个reflog不会包含tag名
            expectCheckoutType = Cons.checkoutType_checkoutRefThenDetachHead,  //用这个会包含tag名
            shortName = item.shortName,
            fullName = item.name,
            curRepo = curRepo.value,
            curCommitOid = item.targetFullOidStr,
            curCommitShortOid = Libgit2Helper.getShortOidStrByFull(item.targetFullOidStr),
            requireUserInputCommitHash = false,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            refreshPage = { _, _, _, _, ->
               //更新当前仓库信息即可，目的是在title显示出最新的分支或提交信息
                doJobThenOffLoading job@{
                    curRepo.value = AppModel.dbContainer.repoRepository.getById(repoId) ?: return@job
                }
            },
        )
    }
    //checkout end


    val showDetailsDialog = rememberSaveable { mutableStateOf(false) }
    val detailsString = rememberSaveable { mutableStateOf("") }
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

    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }

    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false) }

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


    val showTagFetchPushDialog = rememberSaveable { mutableStateOf(false) }
    val showForce = rememberSaveable { mutableStateOf( false) }
    val remoteList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "remoteList",
        listOf<String>()
    )
    val selectedRemoteList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "selectedRemoteList",
        listOf<String>()
    )

    val remoteCheckedList = mutableCustomStateListOf(
        keyTag = stateKeyTag,
        keyName = "remoteCheckedList",
        listOf<Boolean>()
    )

    val fetchPushDialogTitle = rememberSaveable { mutableStateOf("") }

    val trueFetchFalsePush = rememberSaveable { mutableStateOf(true) }
    val requireDel = rememberSaveable { mutableStateOf(false) }
    val requireDelRemoteChecked = rememberSaveable { mutableStateOf(false) }

    val loadingTextForFetchPushDialog = rememberSaveable { mutableStateOf("") }

    if(showTagFetchPushDialog.value) {
        TagFetchPushDialog(
            title = fetchPushDialogTitle.value,
            remoteList = remoteList.value,
            selectedRemoteList = selectedRemoteList.value,
            remoteCheckedList = remoteCheckedList.value,
            enableOk = if(requireDel.value) true else selectedRemoteList.value.isNotEmpty(),   //如果是删除模式，可能只删本地也可能删本地和远程，而显示此弹窗有必须至少选中一个条目的前置判断，所以执行到这里一律启用ok即可；如果是fetch/push，则必须至少选一个remote，否则禁用ok
            showForce = showForce.value,
            requireDel = requireDel.value,
            requireDelRemoteChecked = requireDelRemoteChecked,
            trueFetchFalsePush = trueFetchFalsePush.value,
            showTagFetchPushDialog=showTagFetchPushDialog,
            loadingOn=loadingOn,
            loadingOff=loadingOff,
            loadingTextForFetchPushDialog=loadingTextForFetchPushDialog,
            curRepo=curRepo.value,
            selectedTagsList=selectedItemList.value,
            allTagsList= list.value,
            onCancel = { showTagFetchPushDialog.value=false },
            onSuccess = {
                Msg.requireShow(activityContext.getString(R.string.success))
            },
            onErr = { e->
                val errMsgPrefix = "${fetchPushDialogTitle.value} err: "
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(curRepo.value.id, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#TagFetchPushDialog onOK error when '${fetchPushDialogTitle.value}': ${e.stackTraceToString()}")
            },
            onFinally = {
                changeStateTriggerRefreshPage(needRefresh)
            },
            pushFailedListHandler = { pushFailedList ->
                val prefix = "${pushFailedList.size} remotes are push failed"

                val toastMsg = StringBuilder("$prefix: ")
                val repoLogMsg = StringBuilder("$prefix:\n")
                val logMsg = StringBuilder("$prefix:\n")
                val suffix = ", "
                val spliter = "\n----------\n"

                pushFailedList.forEachBetter {
                    toastMsg.append(it.remoteName).append(suffix)
                    repoLogMsg.append("remoteName='${it.remoteName}', err=${it.exception?.localizedMessage}").append(spliter)
                    logMsg.append("remoteName='${it.remoteName}', err=${it.exception?.stackTraceToString()}").append(spliter)
                }

                //提示用户
                Msg.requireShowLongDuration(toastMsg.removeSuffix(suffix).toString())

                //记错误到仓库卡片
                createAndInsertError(curRepo.value.id, repoLogMsg.removeSuffix(spliter).toString()+"\n\n")

                //记到日志
                MyLog.e(TAG, "#TagFetchPushDialog: ${logMsg.removeSuffix(spliter)}\n\n")
            }
        )
    }

    val initDelTagDialog = {
        requireDel.value = true
        requireDelRemoteChecked.value = false  //默认不要勾选同时删除远程分支，要不然容易误删
        trueFetchFalsePush.value = false
        fetchPushDialogTitle.value = activityContext.getString(R.string.delete_tags)
        showForce.value = false

        loadingTextForFetchPushDialog.value = activityContext.getString(R.string.deleting)

        showTagFetchPushDialog.value = true

    }

    val initPushTagDialog= {
        requireDel.value = false
        trueFetchFalsePush.value = false
        fetchPushDialogTitle.value = activityContext.getString(R.string.push_tags)
        showForce.value = true

        loadingTextForFetchPushDialog.value = activityContext.getString(R.string.pushing)

        showTagFetchPushDialog.value = true
    }

    val initFetchTagDialog = {
        requireDel.value = false
        trueFetchFalsePush.value = true
        fetchPushDialogTitle.value = activityContext.getString(R.string.fetch_tags)
        showForce.value = true

        loadingTextForFetchPushDialog.value = activityContext.getString(R.string.fetching)

        showTagFetchPushDialog.value = true

    }



    // 向下滚动监听，开始
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }

    val filterListState = rememberLazyListState()
//    val filterListState = mutableCustomStateOf(
//        keyTag = stateKeyTag,
//        keyName = "filterListState",
//        initValue = LazyListState(0,0)
//    )
    val enableFilterState = rememberSaveable { mutableStateOf(false) }
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

    val showSelectedItemsShortDetailsDialog = rememberSaveable { mutableStateOf(false) }
//    val selectedItemsShortDetailsStr = rememberSaveable { mutableStateOf("") }
    if(showSelectedItemsShortDetailsDialog.value) {
        SelectedItemDialog(
//            detailStr = selectedItemsShortDetailsStr.value,
            selectedItems = selectedItemList.value,
            formatter = {it.shortName},
            switchItemSelected = switchItemSelected,
            clearAll = {selectedItemList.value.clear()},
            closeDialog = {showSelectedItemsShortDetailsDialog.value = false}
        )
    }

    val countNumOnClickForBottomBar = {
//        val sb = StringBuilder()
//        selectedItemList.value.forEach {
//            sb.appendLine(it.shortName).appendLine()
//        }
//        selectedItemsShortDetailsStr.value = sb.removeSuffix("\n").toString()
        showSelectedItemsShortDetailsDialog.value = true
    }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }


    val showDetails = { selectedItemList:List<TagDto> ->
        val sb = StringBuilder()
        val itemSuffix = "\n\n"
        val spliter = "--------------\n\n"

        selectedItemList.forEachBetter {
            sb.append(activityContext.getString(R.string.name)).append(": ").append(it.shortName).append(itemSuffix)
            sb.append(activityContext.getString(R.string.full_name)).append(": ").append(it.name).append(itemSuffix)
            sb.append(activityContext.getString(R.string.target)).append(": ").append(it.targetFullOidStr).append(itemSuffix)
            sb.append(activityContext.getString(R.string.type)).append(": ").append(it.getType(activityContext, false)).append(itemSuffix)

            sb.append(Cons.flagStr).append(": ").append(it.getType(activityContext, true)).append(itemSuffix)

            if(it.isAnnotated) {
                sb.append(activityContext.getString(R.string.tag_oid)).append(": ").append(it.fullOidStr).append(itemSuffix)
                sb.append(activityContext.getString(R.string.author)).append(": ").append(it.getFormattedTaggerNameAndEmail()).append(itemSuffix)
                sb.append(activityContext.getString(R.string.date)).append(": ").append(it.getFormattedDate()+" (${it.getActuallyUsingTimeOffsetInUtcFormat()})").append(itemSuffix)
                sb.append(activityContext.getString(R.string.timezone)).append(": ").append(formatMinutesToUtc(it.originTimeOffsetInMinutes)).append(itemSuffix)
                sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(itemSuffix)
            }


            sb.append(spliter)
        }

        detailsString.value = sb.removeSuffix(itemSuffix+spliter).toString()

        showDetailsDialog.value = true
    }


    BackHandler {
        if(multiSelectionMode.value) {
            quitSelectionMode()
        } else if(filterModeOn.value) {
            filterModeOn.value = false
            resetSearchVars()
        } else {
            naviUp()
        }
    }

    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
    }


    val showItemMsgDialog = rememberSaveable { mutableStateOf(false) }
    val textOfItemMsgDialog = rememberSaveable { mutableStateOf("") }
    val previewModeOnOfItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgPreviewModeOn) }
    val useSystemFontsForItemMsgDialog = rememberSaveable { mutableStateOf(settings.commitMsgUseSystemFonts) }
    val showItemMsg = { msg: String ->
        textOfItemMsgDialog.value = msg
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
                    if(filterModeOn.value) {
                        FilterTextField(filterKeyWord = filterKeyword, loading = searching.value)
                    }else {
                        val repoAndBranch = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value)
                        Column (modifier = Modifier.combinedClickable (
                            onDoubleClick = {
                                defaultTitleDoubleClick(scope, listState, lastPosition)
                            },
                        ){  //onClick
                            showTitleInfoDialog.value = true
                        }){
                            ScrollableRow  {
                                Text(
                                    text= stringResource(R.string.tags),
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
                            tooltipText = stringResource(R.string.fetch_tags),
                            icon =  Icons.Filled.Downloading,
                            iconContentDesc = stringResource(R.string.fetch_tags),
                        ) {
                            initFetchTagDialog()
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.create_tag),
                            icon =  Icons.Filled.Add,
                            iconContentDesc = stringResource(R.string.create_tag),
                        ) {
                            val hash = ""
                            initNewTagDialog(hash)
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
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {

            if (loading.value) {
//            LoadingText(text = loadingText.value, contentPadding = contentPadding)
                LoadingDialog(text = loadingText.value)
            }


            if(list.value.isEmpty()) {  //无条目，显示可创建或fetch
                FullScreenScrollableColumn(contentPadding) {
                    if(isInitLoading.value) {
                        Text(text = stringResource(R.string.loading))
                    }else {
                        Row {
                            Text(text = stringResource(R.string.no_tags_found))
                        }

                        CenterPaddingRow {
                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Downloading,
                                tooltipText = stringResource(R.string.fetch),
                            ) {
                                initFetchTagDialog()
                            }

                            LongPressAbleIconBtn(
                                icon = Icons.Filled.Add,
                                tooltipText = stringResource(R.string.create),
                            ) {
                                val hash = ""
                                initNewTagDialog(hash)
                            }
                        }
                    }
                }

            }else {  //有条目
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
                    match = { idx:Int, it: TagDto ->
                        it.shortName.contains(keyword, ignoreCase = true)
                                || it.name.contains(keyword, ignoreCase = true)
                                || it.msg.contains(keyword, ignoreCase = true)
                                || it.targetFullOidStr.contains(keyword, ignoreCase = true)
                                || it.taggerName.contains(keyword, ignoreCase = true)
                                || it.taggerEmail.contains(keyword, ignoreCase = true)

                                // annotated tag对象的oid；非annotated tag此值和targetFullOidStr一样
                                || it.fullOidStr.contains(keyword, ignoreCase = true)

                                || it.pointedCommitDto.let { commit ->
                                    if(commit == null) {
                                        false
                                    }else {
                                        commit.msg.contains(keyword, ignoreCase = true)
                                                || commit.getFormattedAuthorInfo().contains(keyword, ignoreCase = true)
                                                || commit.dateTime.contains(keyword, ignoreCase = true)
                                    }
                                }
                                || it.getFormattedDate().contains(keyword, ignoreCase = true)
                                || it.getFormattedTaggerNameAndEmail().contains(keyword, ignoreCase = true)
                                || it.getType(activityContext, false).contains(keyword, ignoreCase = true)
                                || it.getType(activityContext, true).contains(keyword, ignoreCase = true)
                                || it.getOriginTimeOffsetFormatted().contains(keyword, ignoreCase = true)
                    }
                )


                val listState = if(enableFilter) filterListState else listState
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
                    requirePaddingAtBottom = true,
                    forEachCb = {},
                ){idx, it->
                    //长按会更新curObjInPage为被长按的条目
                    TagItem(
                        thisObj = it,
                        lastClickedItemKey = lastClickedItemKey,
                        shouldShowTimeZoneInfo = shouldShowTimeZoneInfo,
                        showItemMsg = showItemMsg,
                        isItemInSelected = isItemInSelected,
                        onLongClick = {
                            if(multiSelectionMode.value) {  //多选模式
                                //在选择模式下长按条目，执行区域选择（连续选择一个范围）
                                UIHelper.doSelectSpan(idx, it,
                                    selectedItemList.value, list,
                                    switchItemSelected,
                                    selectItem
                                )
                            }else {  //非多选模式
                                //启动多选模式
                                switchItemSelected(it)
                            }
                        }
                    ) {  //onClick
                        if(multiSelectionMode.value) {  //选择模式
                            UIHelper.selectIfNotInSelectedListElseRemove(it, selectedItemList.value)
                        }else {  //非选择模式
                            //点击条目跳转到分支的提交历史记录页面
                            fromTagToCommitHistory(
                                fullOid = it.targetFullOidStr,
                                shortName = it.shortName,
                                repoId = repoId
                            )
                        }
                    }

                    MyHorizontalDivider()
                }

                if (multiSelectionMode.value) {

                    val iconList:List<ImageVector> = listOf(
                        Icons.Filled.Delete,  //删除
                        Icons.Filled.Upload,  //上传（push）
                        Icons.Filled.Info,  //详情
                        Icons.Filled.SelectAll,  //全选
                    )
                    val iconTextList:List<String> = listOf(
                        stringResource(id = R.string.delete),
                        stringResource(id = R.string.push),
                        stringResource(id = R.string.details),
                        stringResource(id = R.string.select_all),
                    )
                    val iconEnableList:List<()->Boolean> = listOf(
                        {selectedItemList.value.isNotEmpty()},  // delete
                        {selectedItemList.value.isNotEmpty()},  // push
                        {selectedItemList.value.isNotEmpty()},  // details
                        {true} // select all
                    )

                    val moreItemTextList = (listOf(
                        stringResource(R.string.checkout),
                        stringResource(R.string.reset),  //日后改成reset并可选模式 soft/mixed/hard
//        stringResource(R.string.details),  //可针对单个或多个条目查看details，多个时，用分割线分割多个条目的信息
                    ))

                    val moreItemEnableList:List<()->Boolean> = (listOf(
                        {selectedItemList.value.size==1},  // checkout
                        {selectedItemList.value.size==1},  // hardReset
                        {selectedItemList.value.isNotEmpty()}  // details
                    ))

                    val iconOnClickList:List<()->Unit> = listOf(  //index页面的底栏选项
                        delete@{
                            initDelTagDialog()
                        },

                        push@{
                            initPushTagDialog()
                        },
                        details@{
                            showDetails(selectedItemList.value)
                        },
                        selectAll@{
//                        val list = if(enableFilterState.value) filterList.value else list.value

                            list.forEachBetter {
                                selectItem(it)
                            }

                            Unit
                        },
                    )


                    val moreItemOnClickList:List<()->Unit> = (listOf(
                        checkout@{
                            initCheckoutDialogComposableVersion()
                        },
                        hardReset@{
                            doActIfIndexGood(0, selectedItemList.value) { item ->
                                initResetDialog(item.targetFullOidStr)
                            }

                            Unit
                        },

                        ))

                    BottomBar(
                        quitSelectionMode=quitSelectionMode,
                        iconList=iconList,
                        iconTextList=iconTextList,
                        iconDescTextList=iconTextList,
                        iconOnClickList=iconOnClickList,
                        iconEnableList=iconEnableList,
                        moreItemTextList=moreItemTextList,
                        moreItemOnClickList=moreItemOnClickList,
                        moreItemEnableList = moreItemEnableList,
                        getSelectedFilesCount = getSelectedFilesCount,
                        countNumOnClickEnabled = true,
                        countNumOnClick = countNumOnClickForBottomBar,
                        reverseMoreItemList = true
                    )
                }
            }

        }

    }

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {
        try {
            val refreshId = needRefresh.value
            val pageChanged = {
                refreshId != needRefresh.value
            }

//            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.loading)) {
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {
                list.value.clear()  //先清一下list，然后可能添加也可能不添加

                if(!repoId.isNullOrBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb!=null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            val tags = Libgit2Helper.getAllTags(repoId, repo, settings)
                                .sortedByDescending {
                                    it.pointedCommitDto?.originTimeInSecs ?: 0L
                                }

                            list.value.clear()
                            list.value.addAll(tags)


                            //更新已选中条目列表，将仍在列表中元素更新为新查询的数据
                            val pageChangedNeedAbort = updateSelectedList(
                                selectedItemList = selectedItemList.value,
                                itemList = list.value,
                                quitSelectionMode = quitSelectionMode,
                                match = { oldSelected, item-> oldSelected.name == item.name },
                                pageChanged = pageChanged
                            )

                            // 这里本来就在最后，所以是否return没差别，但避免以后往下面加代码忘了return，这里还是return下吧
                            if (pageChangedNeedAbort) return@doJobThenOffLoading






                            //查询remotes，fetch/push/del用
                            val remotes = Libgit2Helper.getRemoteList(repo)
                            selectedRemoteList.value.clear()
                            remoteCheckedList.value.clear()
                            remoteList.value.clear()


                            //test, start
//                            remotes = remotes.toMutableList()
//                            for(i in 1..50) {
//                                remotes.add(remotes[0]+i)
//                            }
                            //test, end

                            remotes.forEachBetter { remoteCheckedList.value.add(false) }  //有几个remote就创建几个Boolean
                            remoteList.value.addAll(remotes)
                        }
                    }
                }


                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: "+e.stackTraceToString())
        }
    }


}
