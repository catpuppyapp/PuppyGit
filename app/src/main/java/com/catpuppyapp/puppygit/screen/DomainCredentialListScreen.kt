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
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CredentialSelector
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.FullScreenScrollableColumn
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.InfoDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.data.entity.DomainCredentialEntity
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dto.DomainCredentialDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.DomainCredItem
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
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf


private const val TAG = "DomainCredentialListScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DomainCredentialListScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic: HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
//    remoteId:String,

    naviUp: () -> Boolean
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)

    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val navController = AppModel.navController
    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<DomainCredentialDto>() )
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<DomainCredentialDto>() )
    val listState = rememberLazyListState()
    val curCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curCredential", initValue = DomainCredentialDto())
    val needRefresh = rememberSaveable { mutableStateOf("")}
//    val showLoadingDialog = rememberSaveable { mutableStateOf(SharedState.defaultLadingValue)}

    val loadingStrRes = stringResource(R.string.loading)
    val loadingText = rememberSaveable { mutableStateOf( loadingStrRes)}
//    val loadingOn = {text:String ->
//        loadingText.value=text
//        showLoadingDialog.value=true
//    }
//    val loadingOff = {
//        showLoadingDialog.value=false
//        loadingText.value=""
//    }

    val remote =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "remote", initValue = RemoteEntity(id=""))
    val curRepo =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))

    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val doDelete = {
        doJobThenOffLoading {
            try{
                val dcDb = AppModel.dbContainer.domainCredentialRepository
                dcDb.delete(DomainCredentialEntity(id=curCredential.value.domainCredId))
            }finally{
                changeStateTriggerRefreshPage(needRefresh)
            }
        }
    }


    val credentialList = mutableCustomStateListOf(stateKeyTag, "credentialList", listOf<CredentialEntity>())
    val selectedCredentialIdx = rememberSaveable{mutableIntStateOf(0)}
    val selectedSshCredentialIdx = rememberSaveable{mutableIntStateOf(0)}


    val showCreateOrEditDialog = rememberSaveable { mutableStateOf( false)}
    val isCreate = rememberSaveable { mutableStateOf(false)}
    val curDomainNameErr = rememberSaveable { mutableStateOf("")}
    val curDomainName = rememberSaveable { mutableStateOf("")}
    val curDomainCredItemId = rememberSaveable { mutableStateOf("")}  // current edit item id

    fun initCreateOrEditDialog(isCreateParam:Boolean, curDomainNameParam:String, curDomainCredItemIdParam:String, curCredentialId:String, curSshCredentialId:String){
        isCreate.value = isCreateParam
        curDomainName.value = curDomainNameParam
        curDomainNameErr.value=""
        // domain credential item id in db
        curDomainCredItemId.value = curDomainCredItemIdParam

        var indexOfHttp = -1
        var indexOfSsh = -1
        for ((i,v) in credentialList.value.withIndex()) {
            if(indexOfHttp==-1 && v.id==curCredentialId) {
                indexOfHttp = i
            }

            if(indexOfSsh==-1 && v.id==curSshCredentialId) {
                indexOfSsh = i
            }

            if(indexOfHttp!=-1 && indexOfSsh!=-1) {
                break
            }
        }

        selectedCredentialIdx.intValue = indexOfHttp.coerceAtLeast(0)  // if not found, set to 0, else use found index
        selectedSshCredentialIdx.intValue = indexOfSsh.coerceAtLeast(0)  // if not found, set to 0, else use found index

        showCreateOrEditDialog.value = true
    }

    if(showCreateOrEditDialog.value) {
        ConfirmDialog2(
            title = stringResource(if(isCreate.value) R.string.create else R.string.edit),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {

                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(MyStyleKt.defaultHorizontalPadding),

                        value = curDomainName.value,
                        onValueChange = {
                            curDomainName.value = it
                            curDomainNameErr.value=""
                        },
                        label = {
                            Text(stringResource(R.string.domain))
                        },

                        isError = curDomainNameErr.value.isNotEmpty(),
                        supportingText = {
                            if(curDomainNameErr.value.isNotEmpty()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = curDomainNameErr.value,
                                    color = MaterialTheme.colorScheme.error
                                )

                            }
                        },
                        trailingIcon = {
                            if(curDomainNameErr.value.isNotEmpty()) {
                                Icon(imageVector= Icons.Filled.Error,
                                    contentDescription="err icon",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        },
                    )

                    Spacer(Modifier.height(5.dp))

                    CredentialSelector(credentialList.value, selectedCredentialIdx, stringResource(R.string.http_s_credential))
                    CredentialSelector(credentialList.value, selectedSshCredentialIdx, stringResource(R.string.ssh_credential))

                }
            },
            okBtnText = stringResource(R.string.save),
            okBtnEnabled = curDomainName.value.isNotBlank() && curDomainNameErr.value.isEmpty(),
            onCancel = { showCreateOrEditDialog.value = false }
        ) {
            doJobThenOffLoading {
                try {
                    val newDomain = curDomainName.value
                    val newCredentialId = credentialList.value[selectedCredentialIdx.intValue].id
                    val newSshCredentialId = credentialList.value[selectedSshCredentialIdx.intValue].id
                    val dcDb = AppModel.dbContainer.domainCredentialRepository
                    if(isCreate.value) {
                        dcDb.insert(
                            DomainCredentialEntity(
                                domain = newDomain,
                                credentialId = newCredentialId,
                                sshCredentialId = newSshCredentialId
                            )
                        )
                    }else {
                        val old = dcDb.getById(curDomainCredItemId.value) ?: throw RuntimeException("invalid id for update")
                        old.domain = newDomain
                        old.credentialId =newCredentialId
                        old.sshCredentialId = newSshCredentialId

                        dcDb.update(old)
                    }

                    showCreateOrEditDialog.value=false

                    Msg.requireShow(activityContext.getString(R.string.success))

                    changeStateTriggerRefreshPage(needRefresh)
                }catch (e:Exception) {
                    curDomainNameErr.value = e.localizedMessage ?:"err"
                }
            }
        }
    }


    val showDeleteDialog = rememberSaveable { mutableStateOf(false)}

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
    val titleSecondaryString = rememberSaveable { mutableStateOf("")}
    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        InfoDialog(showTitleInfoDialog) {
            Text(titleString.value)

            if(titleSecondaryString.value.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(titleSecondaryString.value)
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


    val isInitLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }
    val initLoadingOn = { msg:String ->
        isInitLoading.value = true
    }
    val initLoadingOff = {
        isInitLoading.value = false
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
                        }) {    // onClick
                            showTitleInfoDialog.value = true
                        }
                        ) {
                            ScrollableRow {
                                titleString.value = stringResource(id = R.string.domains)
                                Text(
                                    text = titleString.value,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                            }
                            if (remote.value.id.isNotEmpty()) {  //隐含 remoteId.isNotEmpty() 为 true
                                titleSecondaryString.value = stringResource(id = R.string.link_mode) + ": [" + remote.value.remoteName + ":${curRepo.value.repoName}]"
                                ScrollableRow {
                                    Text(
                                        text = titleSecondaryString.value,
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
                                tooltipText = stringResource(R.string.create),
                                icon =  Icons.Filled.Add,
                                iconContentDesc = stringResource(id = R.string.create_new_credential),
                            ) {
                                initCreateOrEditDialog(
                                    isCreateParam = true,
                                    curDomainNameParam = "",
                                    curDomainCredItemIdParam = "",
                                    curCredentialId="",
                                    curSshCredentialId = ""
                                )
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
                BottomSheet(showBottomSheet, sheetState, curCredential.value.domain) {
                    //改成在关联页面有这个功能了，在这就不显示了
                    //            BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.unlink_all)){
                    //                //显示弹窗，询问将会与所有remotes解除关联，是否确定？
                    //            }

                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()){
                        showDeleteDialog.value=true
                    }
                }

            }


//            if (showLoadingDialog.value) {
//                LoadingDialog(text = loadingText.value)  //这个东西太阴间了，还是用LoadingText吧
//            }



            if(list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    Text(stringResource(if(isInitLoading.value) R.string.loading else R.string.item_list_is_empty))
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
                    match = { idx:Int, it: DomainCredentialDto ->
                        it.domain.contains(keyword, ignoreCase = true) || (it.credName?.contains(keyword, ignoreCase = true) == true)
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
                    DomainCredItem(
                        showBottomSheet = showBottomSheet,
                        curCredentialState = curCredential,
                        idx = idx,
                        thisItem = value,
                        lastClickedItemKey = lastClickedItemKey
                    ) {
                        initCreateOrEditDialog(
                            isCreateParam = false,
                            curDomainNameParam = value.domain,
                            curDomainCredItemIdParam = value.domainCredId,
                            curCredentialId=value.credId?:"",
                            curSshCredentialId=value.sshCredId?:""
                        )
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
//            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText=activityContext.getString(R.string.loading)) job@{
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {

                list.value.clear()
                credentialList.value.clear()

                val dcDb = AppModel.dbContainer.domainCredentialRepository
                list.value.addAll(dcDb.getAllDto())

                val credentialDb = AppModel.dbContainer.credentialRepository
                // link to none = no link
                val credentialListFromDb = credentialDb.getAll(includeNone = true, includeMatchByDomain = false)
                if(credentialListFromDb.isNotEmpty()) {
                    credentialList.value.addAll(credentialListFromDb)
                }

                triggerReFilter(filterResultNeedRefresh)
//                list.requireRefreshView()
            }
//            读取配置文件，初始化状态之类的
        } catch (cancel: Exception) {
            MyLog.e(TAG, "#LaunchedEffect: ${cancel.localizedMessage}")
//            println("LaunchedEffect: job cancelled")
        }
    }
//
//    //compose被销毁时执行的副作用
//    DisposableEffect(Unit) {
////        println("DisposableEffect: entered main")
//        onDispose {
////            println("DisposableEffect: exited main")
//        }
//    }
}