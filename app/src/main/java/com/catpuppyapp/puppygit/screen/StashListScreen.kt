package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.AskGitUsernameAndEmailDialogWithSelection
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MultiLineClickableText
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.PageCenterIconButton
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SoftkeyboardVisibleListener
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.git.StashDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.StashItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "StashListScreen"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StashListScreen(
    repoId:String,
    naviUp: () -> Boolean,
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



    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    val inDarkTheme = Theme.inDarkTheme

    //获取假数据
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<StashDto>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<StashDto>())


    //这个页面的滚动状态不用记住，每次点开重置也无所谓
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}

    val needRefresh = rememberSaveable { mutableStateOf("")}

    val curObjInPage = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInPage", initValue =StashDto())
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

    //filter相关，开始
    val filterResultNeedRefresh = rememberSaveable { mutableStateOf("") }

    val filterKeyword = mutableCustomStateOf(
        keyTag = stateKeyTag,
        keyName = "filterKeyword",
        initValue = TextFieldValue("")
    )
    val filterModeOn = rememberSaveable { mutableStateOf(false)}
    //filter相关，结束

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

    //Details弹窗，开始
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
    //Details弹窗，结束

    val showPopDialog = rememberSaveable { mutableStateOf(false) }
    val showApplyDialog = rememberSaveable { mutableStateOf(false) }
    val showDelDialog = rememberSaveable { mutableStateOf(false) }
    val showCreateDialog = rememberSaveable { mutableStateOf(false) }

    val stashMsgForCreateDialog = mutableCustomStateOf(stateKeyTag, "stashMsgForCreateDialog") { TextFieldValue("") }


    if(showPopDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.pop),
            text = stringResource(R.string.will_apply_then_delete_item_are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showPopDialog.value=false}
        ) {
            showPopDialog.value=false

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        Libgit2Helper.stashPop(repo, curObjInPage.value.index)
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errPrefix = "pop stash err: index=${curObjInPage.value.index}, stashId=${curObjInPage.value.stashId}, err="
                    Msg.requireShowLongDuration(e.localizedMessage?:"err")
                    createAndInsertError(curRepo.value.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, errPrefix+e.stackTraceToString())
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }

        }
    }

    if(showApplyDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.apply),
            text = stringResource(R.string.will_apply_item_are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showApplyDialog.value=false}
        ) {
            showApplyDialog.value=false

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        Libgit2Helper.stashApply(repo, curObjInPage.value.index)
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errPrefix = "apply stash err: index=${curObjInPage.value.index}, stashId=${curObjInPage.value.stashId}, err="
                    Msg.requireShowLongDuration(e.localizedMessage?:"err")
                    createAndInsertError(curRepo.value.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, errPrefix+e.stackTraceToString())
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }

        }
    }

    if(showDelDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.apply),
            text = stringResource(R.string.will_delete_item_are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDelDialog.value=false}
        ) {
            showDelDialog.value=false

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        Libgit2Helper.stashDrop(repo, curObjInPage.value.index)
                    }
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errPrefix = "delete stash err: index=${curObjInPage.value.index}, stashId=${curObjInPage.value.stashId}, err="
                    Msg.requireShowLongDuration(e.localizedMessage?:"err")
                    createAndInsertError(curRepo.value.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, errPrefix+e.stackTraceToString())
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }

        }
    }

    val clearCommitMsg = {
        stashMsgForCreateDialog.value = TextFieldValue("")
    }

    val genStashMsg = {
        Libgit2Helper.stashGenMsg()
    }

    if(showCreateDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.create),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    TextField(
                        maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                        modifier = Modifier.fillMaxWidth()
                            .onGloballyPositioned { layoutCoordinates ->
//                                println("layoutCoordinates.size.height:${layoutCoordinates.size.height}")
                                // 获取组件的高度
                                // unit is px ( i am not very sure)
                                componentHeight.intValue = layoutCoordinates.size.height
                            }
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            )
                        ,

                        value = stashMsgForCreateDialog.value,
                        onValueChange = {
                            stashMsgForCreateDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.msg))
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        MultiLineClickableText(stringResource(R.string.you_can_leave_msg_empty_will_auto_gen_one)) {
                            Repository.open(curRepo.value.fullSavePath).use { repo ->
                                stashMsgForCreateDialog.value = TextFieldValue(genStashMsg())
                            }
                        }
                    }
                }
            },
            onCancel = { showCreateDialog.value=false}
        ) onOk@{
            showCreateDialog.value=false

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
                try {
                    Repository.open(curRepo.value.fullSavePath).use { repo->
                        val (username, email) = Libgit2Helper.getGitUsernameAndEmail(repo)
                        if(username.isBlank() || email.isBlank()) {
                            Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_git_username_and_email_first))
                            return@doJobThenOffLoading
                        }

                        val msg = stashMsgForCreateDialog.value.text.ifBlank { genStashMsg() }
                        Libgit2Helper.stashSave(repo, stasher = Libgit2Helper.createSignature(username, email, settings), msg=msg)
                    }

                    clearCommitMsg()
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    val errPrefix = "create stash err: "
                    Msg.requireShowLongDuration(e.localizedMessage?:"err")
                    createAndInsertError(curRepo.value.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, errPrefix+e.stackTraceToString())
                }finally {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }

        }
    }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }



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


    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
    }

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
                        val repoAndBranch = Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value)
                        Column (modifier = Modifier.combinedClickable (
                            onDoubleClick = {
                                defaultTitleDoubleClick(scope, listState, lastPosition)
                            },
                        ){  //onClick
                            showTitleInfoDialog.value = true
                        }){
                            ScrollableRow {
                                Text(
                                    text= stringResource(R.string.stash),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ScrollableRow {
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
                            tooltipText = stringResource(R.string.create),
                            icon =  Icons.Filled.Add,
                            iconContentDesc = stringResource(R.string.create),
                        ) {
                            doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
                                showCreateDialog.value = true
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
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {

            if (loading.value) {
//            LoadingText(text = loadingText.value, contentPadding = contentPadding)
                LoadingDialog(text = loadingText.value)
            }

            if(showBottomSheet.value) {
                // index@shortOid, e.g. 0@abc1234
                val sheetTitle = ""+curObjInPage.value.index+"@"+Libgit2Helper.getShortOidStrByFull(curObjInPage.value.stashId.toString())
                BottomSheet(showBottomSheet, sheetState, sheetTitle) {
                    //merge into current 实际上是和HEAD进行合并，产生一个新的提交
                    //x 对当前分支禁用这个选项，只有其他分支才能用
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.pop),
                    ){
                        //弹出确认框，如果确定，执行merge，否则不执行
                        showPopDialog.value = true
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.apply),
                    ){
                        showApplyDialog.value = true
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger(),
                    ){
                        showDelDialog.value = true
                    }

                }
            }

            if(list.value.isEmpty()) {
                if(isInitLoading.value) {
                    FullScreenScrollableColumn(contentPadding) {
                        Text(text = stringResource(R.string.loading))
                    }
                }else {
                    PageCenterIconButton(
                        contentPadding = contentPadding,
                        onClick = {
                            doTaskOrShowSetUsernameAndEmailDialog(curRepo.value) {
                                showCreateDialog.value = true
                            }
                        },
                        icon = Icons.Filled.Add,
                        text = stringResource(R.string.create),
                    )
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
                    match = { idx:Int, it: StashDto ->
                        it.index.toString().contains(keyword, ignoreCase = true)
                                || it.stashId.toString().contains(keyword, ignoreCase = true)
                                || it.msg.contains(keyword, ignoreCase = true)
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
                    requirePaddingAtBottom = true,
                    forEachCb = {},
                ){idx, it->
                    //长按会更新curObjInPage为被长按的条目
                    StashItem(repoId, showBottomSheet, curObjInPage, idx, lastClickedItemKey, it) {  //onClick
                        val suffix = "\n\n"
                        val sb = StringBuilder()
                        sb.append(activityContext.getString(R.string.index)).append(": ").append(it.index).append(suffix)
                        sb.append(activityContext.getString(R.string.stash_id)).append(": ").append(it.stashId).append(suffix)
                        sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(suffix)


                        detailsString.value = sb.removeSuffix(suffix).toString()
                        showDetailsDialog.value = true
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
                            Libgit2Helper.stashList(repo, list.value)
                        }
                    }
                }

                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "BranchListScreen#LaunchedEffect() err: "+e.stackTraceToString())
//            ("LaunchedEffect: job cancelled")
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
            //不显示弹窗的时候则忽略监听
            showCreateDialog.value.not()
        }
    )
}
