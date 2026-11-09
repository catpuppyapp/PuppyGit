package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.BottomSheet
import com.catpuppyapp.puppygit.compose.BottomSheetItem
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyLazyColumn
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.ErrorItem
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.screen.functions.triggerReFilter
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


private const val TAG = "ErrorListScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ErrorListScreen(
//    context: Context,
//    navController: NavHostController,
//    scope: CoroutineScope,
//    haptic:HapticFeedback,
//    homeTopBarScrollBehavior: TopAppBarScrollBehavior,
    repoId:String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)


    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val scope = rememberCoroutineScope()

    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    //获取假数据
//    val list = MockData.getErrorList(repoId,1,100);
    val list = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "list", initValue = listOf<ErrorEntity>())
    val filterList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "filterList", initValue = listOf<ErrorEntity>())
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity(id=""))
//    val sumPage = MockData.getErrorSum(repoId)


    //这个页面的滚动状态不用记住，每次点开重置也无所谓
    val lazyListState = rememberLazyListState()
    val needRefresh = rememberSaveable { mutableStateOf("")}
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
//    val curObjInState = rememberSaveable{ mutableStateOf(ErrorEntity()) }
    val curObjInState = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curObjInState",initValue = ErrorEntity())
    val showClearAllConfirmDialog = rememberSaveable { mutableStateOf(false)}



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

    val removeItemByPredicate = { predicate:(ErrorEntity)->Boolean ->
        if(enableFilterState.value) {
            filterList.value.removeIf { predicate(it) }
        }

        list.value.removeIf { predicate(it) }
    }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }

    val lastClickedItemKey = rememberSaveable{mutableStateOf(Cons.init_last_clicked_item_key)}
    val getActuallyListState = {
        if(enableFilterState.value) filterListState else lazyListState
    }

    val filterLastPosition = rememberSaveable { mutableStateOf(0) }
    val lastPosition = rememberSaveable { mutableStateOf(0) }

    if(showClearAllConfirmDialog.value) {
        ConfirmDialog(
            title=stringResource(R.string.clear_all),
            text=stringResource(R.string.clear_all_ask_text),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showClearAllConfirmDialog.value=false },
            onOk = {
                //关闭弹窗
                showClearAllConfirmDialog.value = false

                doJobThenOffLoading {
                    AppModel.dbContainer.errorRepository.deleteByRepoId(repoId)

                    Msg.requireShow(activityContext.getString(R.string.success))

                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        )
    }


    val viewDialogText = rememberSaveable { mutableStateOf("") }
    val showViewDialog = rememberSaveable { mutableStateOf(false) }
    if(showViewDialog.value) {
        CopyableDialog(
            title = stringResource(R.string.error_msg),
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
                        Column(modifier = Modifier.combinedClickable(onDoubleClick = {
                            //能点这个必然没开过滤模式，必然是普通的listState，所以无需判断是否filterListState
                            defaultTitleDoubleClick(scope, lazyListState, lastPosition)
                        }) {
                            // onClick
                            showTitleInfoDialog.value=true
                        }) {
                            ScrollableRow {
                                Text(
                                    text= stringResource(R.string.error),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                            }

                            ScrollableRow {
                                Text(
                                    text= "[${curRepo.value.repoName}]",
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
                            icon = Icons.Filled.Refresh,
                            iconContentDesc = stringResource(R.string.refresh),

                        ) {
                            changeStateTriggerRefreshPage(needRefresh)
                        }

                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.clear_all),
                            icon =  Icons.Filled.DeleteSweep,
                            iconContentDesc = stringResource(R.string.clear_all),

                        ) {
                            //点击垃圾箱按钮，显示清空所有对话框，若确认，删除所有错误，然后继续停留在当前页面，但错误列表被清空
                            showClearAllConfirmDialog.value = true
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

        // bottom sheet放不放pull to refresh box都行，不会冲突
        if(showBottomSheet.value) {
            BottomSheet(showBottomSheet, sheetState, stringResource(R.string.id)+": "+curObjInState.value.id) {
//                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.view_msg)){
//                    //弹窗显示错误信息，可复制
//                }
                BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.delete), textColor = MyStyleKt.TextColor.danger()) {
                    val target = curObjInState.value

                    doJobThenOffLoading {
                        // remove from db
                        AppModel.dbContainer.errorRepository.delete(target)

                        // remove from list/filterList
                        removeItemByPredicate {
                            it.id == target.id
                        }

                        Msg.requireShow(activityContext.getString(R.string.success))
                    }
                }
            }
        }

        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {

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
                match = { idx: Int, it: ErrorEntity ->
                    it.msg.contains(keyword, ignoreCase = true)
                            || it.date.contains(keyword, ignoreCase = true)
                            || it.id.contains(keyword, ignoreCase = true)
                }
            )


            val listState = if(enableFilter) filterListState else lazyListState
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
                requirePaddingAtBottom = true
            ) { idx, it ->
                //在这个组件里更新了 state curObj，所以长按后直接用curObj就能获取到当前对象了
                ErrorItem(showBottomSheet,curObjInState,idx, lastClickedItemKey, it) {
                    val suffix = "\n\n"
                    val sb = StringBuilder()

                    sb.append(activityContext.getString(R.string.id)).append(": ").append(it.id).append(suffix)
                    sb.append(activityContext.getString(R.string.date)).append(": ").append(it.date).append(suffix)
                    sb.append(activityContext.getString(R.string.msg)).append(": ").append(it.msg).append(suffix)

                    viewDialogText.value = sb.removeSuffix(suffix).toString()

                    showViewDialog.value = true
                }

                MyHorizontalDivider()
            }

        }



    }

    //compose创建时的副作用
    LaunchedEffect(needRefresh.value) {
        try {
            if (repoId.isNotBlank()) {
                doJobThenOffLoading {
                    //先清下列表
                    list.value.clear()

                    // here only need repo name, no need sync repo info with under git, and if query failed, return a repo with empty id and empty name, but it shouldn't happen though
                    curRepo.value = AppModel.dbContainer.repoRepository.getByIdNoSyncWithGit(repoId) ?: RepoEntity(id="")

                    //查询错误列表
                    val errDb = AppModel.dbContainer.errorRepository
                    val errList = errDb.getListByRepoId(repoId)
                    //添加到页面状态列表
                    list.value.addAll(errList)
//                    list.requireRefreshView()

                    //更新数据库
                    val repoDb = AppModel.dbContainer.repoRepository
                    if (errList.isNotEmpty()) { //如果记录不为空: 清空仓库表的错误信息; 把当前仓库关联的所有错误记录标记成已读
                        repoDb.checkedAllErrById(repoId)
                    }else {  // 如果err list为空，仅清空仓库表的错误信息
                        repoDb.updateErrFieldsById(repoId, Cons.dbCommonFalse, "")
                    }

                    triggerReFilter(filterResultNeedRefresh)
                }
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect() err: repoId=$repoId , err is:${e.stackTraceToString()}")
        }
    }
    //compose被销毁时执行的副作用
    DisposableEffect(Unit) {
//        println("DisposableEffect: entered main")
        onDispose {
//            println("DisposableEffect: exited main")
        }
    }

}