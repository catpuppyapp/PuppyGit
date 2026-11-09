package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.CheckoutDialog
import com.catpuppyapp.puppygit.compose.CheckoutDialogFrom
import com.catpuppyapp.puppygit.compose.CopyableDialog
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
import com.catpuppyapp.puppygit.compose.SimpleTitleDropDownMenu
import com.catpuppyapp.puppygit.compose.getDefaultCheckoutOption
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dev.resetByHashTestPassed
import com.catpuppyapp.puppygit.git.ReflogEntryDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.ReflogItem
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
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.time.TimeZoneUtil
import com.github.git24j.core.Repository

private const val TAG = "ReflogListScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflogListScreen(
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

    val inDarkTheme = Theme.inDarkTheme

    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val shouldShowTimeZoneInfo = rememberSaveable { TimeZoneUtil.shouldShowTimeZoneInfo(settings) }

    val refName = rememberSaveable { mutableStateOf(Cons.gitHeadStr) }

    //获取假数据
    val curClickItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curClickItem", initValue = ReflogEntryDto())
    val curLongClickItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curLongClickItem", initValue = ReflogEntryDto())

    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<ReflogEntryDto>())
    val allRefList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "allRefList", initValue = listOf<String>())

    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<ReflogEntryDto>())

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
        loading.value=false
        loadingText.value = defaultLoadingText
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


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}

    val checkoutNew = rememberSaveable { mutableStateOf( false)}
    val requireUserInputCommitHash = rememberSaveable { mutableStateOf(false)}
    val showCheckoutDialog = rememberSaveable { mutableStateOf(false)}
    //初始化组件版本的checkout对话框
    val initCheckoutDialog = { requireUserInputHash:Boolean ->
        requireUserInputCommitHash.value = requireUserInputHash
        showCheckoutDialog.value = true
    }

    val branchNameForCheckout = rememberSaveable { mutableStateOf("") }
    val checkoutSelectedOption = rememberSaveable{ mutableIntStateOf(getDefaultCheckoutOption(false)) }

    if(showCheckoutDialog.value) {
        val id = (if(checkoutNew.value) curLongClickItem.value.idNew else curLongClickItem.value.idOld) ?: Cons.git_AllZeroOid

        if(id.isNullOrEmptyOrZero) {  //id无效，关弹窗，显提示
            showCheckoutDialog.value = false
            Msg.requireShow(stringResource(R.string.invalid_oid))
        }else {
            val fullOidStr = id.toString()
            val shortOidStr = Libgit2Helper.getShortOidStrByFull(fullOidStr)

            CheckoutDialog(
                checkoutSelectedOption = checkoutSelectedOption,

                showCheckoutDialog=showCheckoutDialog,
                branchName = branchNameForCheckout,
                from = CheckoutDialogFrom.OTHER,
                expectCheckoutType = Cons.checkoutType_checkoutCommitThenDetachHead,
                curRepo = curRepo.value,
                shortName = shortOidStr,
                fullName = fullOidStr,
                curCommitOid = fullOidStr,
                curCommitShortOid = shortOidStr,
                requireUserInputCommitHash = requireUserInputCommitHash.value,
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                refreshPage = { _, _, _, _, ->
                    changeStateTriggerRefreshPage(needRefresh)
                },
            )
        }

    }


    val resetOid = rememberSaveable { mutableStateOf("")}
    val resetNew = rememberSaveable { mutableStateOf( false)}
    val showResetDialog = rememberSaveable { mutableStateOf(false)}
    val closeResetDialog = {
        showResetDialog.value = false
    }

    if (showResetDialog.value) {
        val id = (if(resetNew.value) curLongClickItem.value.idNew else curLongClickItem.value.idOld) ?: Cons.git_AllZeroOid

        if(id.isNullOrEmptyOrZero) {  //id无效，关弹窗，显提示
            showResetDialog.value = false
            Msg.requireShow(stringResource(R.string.invalid_oid))
        }else{
            resetOid.value = id.toString()

            ResetDialog(
                fullOidOrBranchOrTag = resetOid,
                closeDialog=closeResetDialog,
                repoFullPath = curRepo.value.fullSavePath,
                repoId=curRepo.value.id,
                refreshPage = { _, _, _ ->
                    changeStateTriggerRefreshPage(needRefresh)
                }
            )
        }

    }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false)}
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            prependContent = {
                Row {
                    Text(stringResource(R.string.reference) + ": " + refName.value)
                }
            },
            curRepo = curRepo.value,
            showTitleInfoDialog = showTitleInfoDialog
        )
    }

    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val switchRef = { newRef: String ->
        refName.value = newRef
        changeStateTriggerRefreshPage(needRefresh)
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
                    }else {
                        SimpleTitleDropDownMenu(
                            dropDownMenuExpandState = dropDownMenuExpandState,
                            curSelectedItem = refName.value,
                            itemList = allRefList.value,
                            titleClickEnabled = true,
                            showHideMenuIconContentDescription = stringResource(R.string.switch_reference),
                            titleFirstLineFormatter = { it },
                            titleSecondLineFormatter = { Libgit2Helper.getRepoOnBranchOrOnDetachedHash(curRepo.value) },
                            menuItemFormatter= { it },
                            titleOnLongClick = { showTitleInfoDialog.value = true },
                            itemOnClick = { switchRef(it) },
                            isItemSelected = { it == refName.value }
                        )
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

            if (showBottomSheet.value) {
                // title form: oldOid..newOid, means oldOid to newOid, eg abc1234..def1234
                val title = Libgit2Helper.getShortOidStrByFull((curLongClickItem.value.idOld ?: Cons.git_AllZeroOid).toString())+".."+Libgit2Helper.getShortOidStrByFull((curLongClickItem.value.idNew ?: Cons.git_AllZeroOid).toString())
                BottomSheet(showBottomSheet, sheetState, title) {
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout_new)) {
                        // onClick()
                        // 弹出确认框，询问是否确定执行checkout，可detach head，可创建分支，类似checkout remote branch
                        //初始化弹窗默认选项
                        checkoutNew.value = true

                        val requireUserInputHash = false
                        initCheckoutDialog(requireUserInputHash)
                    }
                    BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.checkout_old)) {
                        // onClick()
                        // 弹出确认框，询问是否确定执行checkout，可detach head，可创建分支，类似checkout remote branch
                        //初始化弹窗默认选项
                        checkoutNew.value = false

                        val requireUserInputHash = false
                        initCheckoutDialog(requireUserInputHash)
                    }

                    if(proFeatureEnabled(resetByHashTestPassed)) {
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset_new)) {
                            resetNew.value=true
                            showResetDialog.value = true
                        }
                        BottomSheetItem(sheetState, showBottomSheet, stringResource(R.string.reset_old)) {
                            resetNew.value=false
                            showResetDialog.value = true
                        }
                    }

                }
            }

            if(list.value.isEmpty()) {
                FullScreenScrollableColumn(contentPadding) {
                    Text(stringResource(if(isInitLoading.value) R.string.loading else R.string.item_list_is_empty))
                }
            }else {

                //有条目
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
                    match = { idx:Int, it: ReflogEntryDto ->
                        it.username.contains(keyword, ignoreCase = true)
                                || it.email.contains(keyword, ignoreCase = true)
                                || it.date.contains(keyword, ignoreCase = true)
                                || it.msg.contains(keyword, ignoreCase = true)
                                || it.idNew.toString().contains(keyword, ignoreCase = true)
                                || it.idOld.toString().contains(keyword, ignoreCase = true)
                                || formatMinutesToUtc(it.originTimeZoneOffsetInMinutes).contains(keyword, ignoreCase = true)
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
                    ReflogItem(repoId, showBottomSheet, curLongClickItem, lastClickedItemKey, shouldShowTimeZoneInfo, it) {  //onClick
                        val suffix = "\n\n"
                        val sb = StringBuilder()
                        sb.append(activityContext.getString(R.string.new_oid)).append(": ").append(it.idNew).append(suffix)
                        sb.append(activityContext.getString(R.string.old_oid)).append(": ").append(it.idOld).append(suffix)
                        sb.append(activityContext.getString(R.string.date)).append(": ").append(it.date+" (${formatMinutesToUtc(it.actuallyUsingTimeZoneOffsetInMinutes)})").append(suffix)
                        sb.append(activityContext.getString(R.string.timezone)).append(": ").append(formatMinutesToUtc(it.originTimeZoneOffsetInMinutes)).append(suffix)
                        sb.append(activityContext.getString(R.string.author)).append(": ").append(Libgit2Helper.getFormattedUsernameAndEmail(it.username, it.email)).append(suffix)
                        sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(suffix)


                        detailsString.value = sb.removeSuffix(suffix).toString()

                        curClickItem.value = it
                        showDetailsDialog.value=true
                    }

                    MyHorizontalDivider()
                }


            }

        }


    }


    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {
        try {
            //这个加载很快，没必要显示loading
//            doJobThenOffLoading(loadingOn = loadingOn, loadingOff = loadingOff, loadingText = defaultLoadingText) {
            doJobThenOffLoading(initLoadingOn, initLoadingOff) {
                list.value.clear()  //先清一下list，然后可能添加也可能不添加
                allRefList.value.clear()

                if(repoId.isNotBlank()) {
                    val repoDb = AppModel.dbContainer.repoRepository
                    val repoFromDb = repoDb.getById(repoId)
                    if(repoFromDb != null) {
                        curRepo.value = repoFromDb
                        Repository.open(repoFromDb.fullSavePath).use {repo ->
                            Libgit2Helper.getReflogList(repo, refName.value, out = list.value, settings)
                            allRefList.value.addAll(Libgit2Helper.getAllRefs(repo, includeHEAD = true))
                        }
                    }else {
                        Msg.requireShowLongDuration("err: query repo failed")
                    }
                }else {
                    Msg.requireShowLongDuration("err: invalid repo id")
                }

                triggerReFilter(filterResultNeedRefresh)
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: "+e.stackTraceToString())
//            LaunchedEffect: job cancelled
        }
    }

}
