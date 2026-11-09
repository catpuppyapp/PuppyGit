package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.InfoDialog
import com.catpuppyapp.puppygit.compose.LinkOrUnLinkCredentialAndRemoteDialog
import com.catpuppyapp.puppygit.compose.LoadingTextSimple
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dto.RemoteDtoForCredential
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.CredentialItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf


private const val TAG = "CredentialManagerScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CredentialManagerScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic: HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
    remoteId:String,

    naviUp: () -> Boolean
) {

    val stateKeyTag = Cache.getSubPageKey(TAG)


    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()

    // for link credential to remote
    val isLinkMode = remoteId.isNotBlank()
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}

    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<CredentialEntity>() )
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<CredentialEntity>() )
    val listState = rememberLazyListState()
    val curCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curCredential", initValue = CredentialEntity(id=""))
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val showLoadingDialog = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue)}

    val loadingStrRes = stringResource(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf(loadingStrRes)}
    val loadingOn = {text:String ->
        loadingText.value=text
        showLoadingDialog.value=true
    }
    val loadingOff = {
        showLoadingDialog.value=false
        loadingText.value=""
    }


    val remote =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "remote", initValue = RemoteEntity(id=""))
    val curRepo =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
    val showLinkOrUnLinkDialog = rememberSaveable { mutableStateOf(false)}
    val onClickCurItem =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "onClickCurItem", initValue = CredentialEntity(id=""))  //非点击跳转页面的情况下，点击条目后更新此变量
    val requireDoLink = rememberSaveable { mutableStateOf(false)}
    val targetAll = rememberSaveable { mutableStateOf( false)}
    val remoteDtoForCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "remoteDtoForCredential", initValue = RemoteDtoForCredential(remoteId = remoteId))

    val linkOrUnLinkDialogTitle = rememberSaveable { mutableStateOf("")}

    if(showLinkOrUnLinkDialog.value) {
        LinkOrUnLinkCredentialAndRemoteDialog(
            curItemInPage = onClickCurItem,
            requireDoLink = requireDoLink.value,
            targetAll = targetAll.value,
            title = linkOrUnLinkDialogTitle.value,  //若空字符串，会根据上面的flag自动判断显示link还是unlink
            thisItem = remoteDtoForCredential.value,
            onCancel = { showLinkOrUnLinkDialog.value=false},
            onFinallyCallback = {
                showLinkOrUnLinkDialog.value=false
                //关联模式下，似乎，没必要刷新页面啊？但刷新下其实也没什么
                changeStateTriggerRefreshPage(needRefresh)
            },
            onErrCallback = { e->
                val errMsgPrefix = "${linkOrUnLinkDialogTitle.value} err: remote='${remote.value.remoteName}', credential=${onClickCurItem.value.name}, err="
                Msg.requireShowLongDuration(e.localizedMessage ?: errMsgPrefix)
                createAndInsertError(remote.value.repoId, errMsgPrefix + e.localizedMessage)
                MyLog.e(TAG, "#LinkOrUnLinkCredentialAndRemoteDialog err: $errMsgPrefix${e.stackTraceToString()}")
            },
            onOkCallback = {
                Msg.requireShow(activityContext.getString(R.string.success))
            }

        )

    }


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val doDelete = {
        doJobThenOffLoading {
            try{
                val credentialDb = AppModel.dbContainer.credentialRepository
                credentialDb.deleteAndUnlink(curCredential.value)
                Msg.requireShow(activityContext.getString(R.string.success))
            }finally{
                changeStateTriggerRefreshPage(needRefresh)
            }
        }
    }

    val showDeleteDialog = rememberSaveable { mutableStateOf( false)}
    if(showDeleteDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.delete_credential),
            text = stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showDeleteDialog.value = false }
        ) {   // onOk
            showDeleteDialog.value=false
            doDelete()
        }
    }

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


    val titleString = rememberSaveable { mutableStateOf("")}
    val titleSecondaryString = rememberSaveable { mutableStateOf("")}  // title secondary line string
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        InfoDialog(showTitleInfoDialog) {
            Text(titleString.value)

            if(titleSecondaryString.value.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(titleSecondaryString.value)
            }

            if(isLinkMode) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(stringResource(R.string.repo)+": "+curRepo.value.repoName)
                Text(stringResource(R.string.remote)+": "+remote.value.remoteName)
                val fetchCredential = list.value.find { remote.value.credentialId == it.id }
                val pushCredential = list.value.find { remote.value.pushCredentialId == it.id }
                Text(stringResource(R.string.fetch_credential)+": "+fetchCredential?.name)
                Text(stringResource(R.string.push_credential)+": "+pushCredential?.name)
            }
        }
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
                    }else{
                        Column(modifier = Modifier.combinedClickable(onDoubleClick = {
                            defaultTitleDoubleClick(scope, listState, lastPosition)
                        }) {
                            showTitleInfoDialog.value = true
                        }) {
                            titleString.value = stringResource(id = R.string.credential_manager)

                            ScrollableRow {
                                Text(
                                    text = titleString.value,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                            }
                            if(remote.value.id.isNotEmpty()) {  //隐含 remoteId.isNotEmpty() 为 true
                                titleSecondaryString.value = "${stringResource(R.string.link_mode)}: [${remote.value.remoteName} of ${curRepo.value.repoName}]";

                                ScrollableRow  {
                                    Text(
                                        text= titleSecondaryString.value,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = MyStyleKt.Title.secondLineFontSize
                                    )
                                }

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
                        Row {
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
                                iconContentDesc = stringResource(id = R.string.refresh),
                            ) {
                                changeStateTriggerRefreshPage(needRefresh)
                            }
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.domains),
                                icon =  Icons.Filled.Domain,
                                iconContentDesc = stringResource(id = R.string.domains),
                            ) {
                                navController.navigate(Cons.nav_DomainCredentialListScreen)
                            }

                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.create),
                                icon =  Icons.Filled.Add,
                                iconContentDesc = stringResource(id = R.string.create_new_credential),
                            ) {
                                //从这跳是新建，直接传null
                                navController.navigate(Cons.nav_CredentialNewOrEditScreen+"/"+null)
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

            if(showBottomSheet.value) {
                BottomSheet(showBottomSheet, sheetState, curCredential.value.name) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.edit)){
                        //跳转到编辑页面
                        //在这里可以直接用state curObj取到当前选中条目，curObjInState在长按条目后会被更新为当前被长按的条目
                        navController.navigate(Cons.nav_CredentialNewOrEditScreen+"/"+curCredential.value.id)

                    }

                    //改成在关联页面有这个功能了，在这就不显示了
                    //            BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.unlink_all)){
                    //                //显示弹窗，询问将会与所有remotes解除关联，是否确定？
                    //            }

                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()){
                        showDeleteDialog.value=true
                    }
                }

            }

            if (showLoadingDialog.value) {
//            LoadingDialog()  //这个东西太阴间了，还是用LoadingText吧

                LoadingTextSimple(text = loadingText.value, contentPadding = contentPadding)

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
                    match = { idx:Int, it: CredentialEntity ->
                        it.name.contains(keyword, ignoreCase = true) || it.value.contains(keyword, ignoreCase = true)
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
                    requirePaddingAtBottom = true
                ) {idx, value->
                    CredentialItem(
                        showBottomSheet = showBottomSheet,
                        curCredentialState = curCredential,
                        idx = idx,
                        thisItem = value,
                        isLinkMode = isLinkMode,
                        linkedFetchId = remote.value.credentialId,
                        linkedPushId = remote.value.pushCredentialId,
                        lastClickedItemKey = lastClickedItemKey
                    ) {

                        //这里value和it和传给CredentialItem的thisItem值都一样，只是参数传来传去而已

                        if(remoteId.isEmpty()) {  //若remoteId为空，跳转到remote和凭据绑定页面
                            // 点击跳转到关联列表，传1表示显示的是关联列表
                            navController.navigate(Cons.nav_CredentialRemoteListScreen+"/"+it.id+"/1")
                        }else {  //若remoteId不为空，则代表为此remoteId绑定凭据，点击条目弹窗
                            //为弹窗准备参数
                            onClickCurItem.value = it  //虽然这里是被点击的条目，但其实用保存长按条目的状态变量curCredential也可以，不过为了避免混淆，没用那个
                            requireDoLink.value = true
                            targetAll.value=false

                            remoteDtoForCredential.value = RemoteDtoForCredential(
                                remoteId = remoteId,
                                credentialId = remote.value.credentialId,
                                pushCredentialId = remote.value.pushCredentialId
                            )

                            linkOrUnLinkDialogTitle.value = activityContext.getString(R.string.link)+" '${it.name}'"
                            //显示弹窗
                            showLinkOrUnLinkDialog.value=true
                        }
                    }

                    MyHorizontalDivider()

                }
            }
        }

    }

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {
//        println("LaunchedEffect: entered main")
        // Just an example of coroutines usage
        // don't use this way to track screen disappearance
        // DisposableEffect is better for this
        try {
            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = activityContext.getString(R.string.loading)) job@{
                val credentialDb = AppModel.dbContainer.credentialRepository
                list.value.clear()
//                list.value.addAll(credentialDb.getAll(includeMatchByDomain = isLinkMode, includeNone = isLinkMode))
                list.value.addAll(credentialDb.getAll(includeMatchByDomain = true, includeNone = true))

                if(isLinkMode) {
                    val remoteFromDb = AppModel.dbContainer.remoteRepository.getById(remoteId)
                    if(remoteFromDb!=null){
                        remote.value=remoteFromDb
                        val repoFromDb = AppModel.dbContainer.repoRepository.getById(remoteFromDb.repoId)
                        if(repoFromDb!=null) {
                            curRepo.value = repoFromDb
                        }
                    }
                }

                triggerReFilter((filterResultNeedRefresh))
//                list.requireRefreshView()
            }
//            读取配置文件，初始化状态之类的
        } catch (cancel: Exception) {
//            println("LaunchedEffect: job cancelled")
        }
    }
    //compose被销毁时执行的副作用
//    DisposableEffect(Unit) {
////        println("DisposableEffect: entered main")
//        onDispose {
////            println("DisposableEffect: exited main")
//        }
//    }
}