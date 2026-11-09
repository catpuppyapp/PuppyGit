package com.catpuppyapp.puppygit.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.BarContainer
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialogAndDisableSelection
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.CreatePatchSuccessDialog
import com.catpuppyapp.puppygit.compose.DiffRow
import com.catpuppyapp.puppygit.compose.FileHistoryRestoreDialog
import com.catpuppyapp.puppygit.compose.FontSizeAdjuster
import com.catpuppyapp.puppygit.compose.GoToTopAndGoToBottomFab
import com.catpuppyapp.puppygit.compose.InLineIcon
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.MySelectionContainerPlaceHolder
import com.catpuppyapp.puppygit.compose.OneTimeFocusRightNow
import com.catpuppyapp.puppygit.compose.OpenAsAskReloadDialog
import com.catpuppyapp.puppygit.compose.OpenAsDialog
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.ReadOnlyIcon
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SelectSyntaxHighlightingDialog
import com.catpuppyapp.puppygit.compose.SelectionRow
import com.catpuppyapp.puppygit.compose.SingleLineCardButton
import com.catpuppyapp.puppygit.compose.SoftkeyboardVisibleListener
import com.catpuppyapp.puppygit.compose.TwoLineTextCardButton
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.LineNum
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.FlagFileName
import com.catpuppyapp.puppygit.dev.detailsDiffTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.dto.MenuIconBtnItem
import com.catpuppyapp.puppygit.dto.MenuTextItem
import com.catpuppyapp.puppygit.git.CompareLinePair
import com.catpuppyapp.puppygit.git.DiffItemSaver
import com.catpuppyapp.puppygit.git.DiffableItem
import com.catpuppyapp.puppygit.git.PuppyHunkAndLines
import com.catpuppyapp.puppygit.git.PuppyLine
import com.catpuppyapp.puppygit.git.PuppyLineOriginType
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.msg.OneTimeToast
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions.DiffPageActions
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title.DiffScreenTitle
import com.catpuppyapp.puppygit.screen.functions.ChangeListFunctions
import com.catpuppyapp.puppygit.screen.functions.openFileWithInnerSubPageEditor
import com.catpuppyapp.puppygit.screen.shared.DiffFromScreen
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.SettingsCons
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLFont
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.cache.NaviCache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.doJobWithMainContext
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.getFormattedLastModifiedTimeOfFile
import com.catpuppyapp.puppygit.utils.getHumanReadableSizeStr
import com.catpuppyapp.puppygit.utils.getRequestDataByState
import com.catpuppyapp.puppygit.utils.isGoodIndex
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.state.mutableCustomBoxOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Diff
import com.github.git24j.core.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val TAG = "DiffScreen"

//private const val defaultFileTitleFileNameLenLimit = 12
//private val fileTitleFileNameWidthLimit = 150.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffScreen(
    repoId: String,
    fromTo:String,
//    changeType:String,  //modification, new, del，之类的只有modification需要diff
//    fileSize:Long,
    treeOid1Str:String,
    treeOid2Str:String,
//    isSubmodule:Boolean,
    isDiffToLocal:Boolean,  // actually is diff with local, whether local at left or right, this gonna be true
    curItemIndexAtDiffableItemList:Int,
    localAtDiffRight:Boolean,
    fromScreen: DiffFromScreen, // from which screen
//    relativePathCacheKey:String,
    diffableListCacheKey:String,
    isMultiMode:Boolean,
    naviUp: () -> Unit,
) {
    //这参数没用了
//    val relativePathCacheKey = Unit // for avoid mistake using

    val stateKeyTag = Cache.getSubPageKey(TAG)


    val isSingleMode = isMultiMode.not();

    val inDarkTheme = remember(Theme.inDarkTheme) { Theme.inDarkTheme }

//    val isWorkTree = fromTo == Cons.gitDiffFromIndexToWorktree
    //废弃，改用diffContent里获取diffItem时动态计算了
//    val fileSizeOverLimit = isFileSizeOverLimit(fileSize)
    val dbContainer = AppModel.dbContainer
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior

    val configuration = AppModel.getCurActivityConfig()
    val activityContext = LocalContext.current
    val navController = AppModel.navController

    val clipboardManager = LocalClipboardManager.current







    val showEditLineDialog = rememberSaveable { mutableStateOf(false) }
    val showRestoreLineDialog = rememberSaveable { mutableStateOf(false) }


    // BEGIN: SoftKb visible Listener
    // softkeyboard listener for adjust height of component when softkb visible
    val view = LocalView.current
    val density = LocalDensity.current

    val isKeyboardVisible = remember { mutableStateOf(false) }
    //indicate keyboard covered component
    val isKeyboardCoveredComponent = remember { mutableStateOf(false) }
    // which component expect adjust heghit or padding when softkeyboard shown
    val componentHeight = remember { mutableIntStateOf(0) }
    // the padding value when softkeyboard shown
    val keyboardPaddingDp = remember { mutableIntStateOf(0) }

    // this code gen by chat-gpt, wow
    // except: this code may not work when use use a float keyboard or softkeyboard with single-hand mode
    // 监听键盘的弹出和隐藏 (listening keyboard visible/hidden)
    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            !(showEditLineDialog.value || showRestoreLineDialog.value)
        }
    )
    // END: SoftKb visible Listener




//    val screenHeightPx = remember (configuration.screenHeightDp) { UIHelper.dpToPx(configuration.screenHeightDp, density) }

    val isFileHistoryTreeToLocal = fromTo == Cons.gitDiffFileHistoryFromTreeToLocal
    val isFileHistoryTreeToPrev = fromTo == Cons.gitDiffFileHistoryFromTreeToPrev

    val isFileHistory = isFileHistoryTreeToLocal || isFileHistoryTreeToPrev;

    val scope = rememberCoroutineScope()
    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    val lastClickedItemKey = rememberSaveable {
        if(fromScreen == DiffFromScreen.HOME_CHANGELIST) {
            SharedState.homeChangeList_LastClickedItemKey
        }else if(fromScreen == DiffFromScreen.INDEX) {
            SharedState.index_LastClickedItemKey
        }else if(fromScreen == DiffFromScreen.TREE_TO_TREE) {
            SharedState.treeToTree_LastClickedItemKey
        }else { // file history
            SharedState.fileHistory_LastClickedItemKey
        }
    }


    val treeOid1Str = rememberSaveable { mutableStateOf(
        if(fromTo == Cons.gitDiffFromIndexToWorktree) {
            Cons.git_IndexCommitHash
        }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
            Cons.git_HeadCommitHash
        }else{
            treeOid1Str
        }
    ) }
    val treeOid2Str = rememberSaveable { mutableStateOf(
        if(fromTo == Cons.gitDiffFromIndexToWorktree) {
            Cons.git_LocalWorktreeCommitHash
        }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
            Cons.git_IndexCommitHash
        }else {
            treeOid2Str
        }
    ) }

    val tree1FullHash = rememberSaveable { mutableStateOf("") }
    val tree2FullHash = rememberSaveable { mutableStateOf("") }

    //这个值存到状态变量里之后就不用管了，与页面共存亡即可，如果旋转屏幕也没事，返回rememberSaveable可恢复
//    val relativePathUnderRepoDecoded = (Cache.Map.getThenDel(Cache.Map.Key.diffScreen_UnderRepoPath) as? String)?:""

    val needRefresh = rememberSaveable { mutableStateOf("DiffScreen_refresh_init_value_4kc9") }
    val listState = rememberLazyListState()

    val goToTop = {
        UIHelper.scrollToItem(scope, listState, 0)
    }

    //避免高强度并发赋值导致报错，虽然一般没事，但墨菲定律，你懂的
    val diffableListLock = mutableCustomBoxOf<Mutex>(stateKeyTag, "diffableListLock", Mutex()).value

    val diffableItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "diffableItemList") {
        NaviCache.getByType<List<DiffableItem>>(diffableListCacheKey) ?: listOf()
    }




    //之前有两种类型的list，所以创建了这个函数，历史遗留问题
//    val getActuallyList:()->List<DiffableItem> = {
//        diffableItemList.value
//    }

    val subDiffableItemList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "subDiffableItemList") {
        //里面存的是diffableList里的索引，用来加载指定条目的
        //最初进入当前页面，只加载当前条目
        listOf<Int>(curItemIndexAtDiffableItemList)
    }


    // key 是文件在仓库下的相对路径，value是文件在lazy column的索引
//    val itemIdxAtLazyColumn_Map = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "itemIdxAtLazyColumn_Map") { mapOf<String, Int>() }



    val firstTimeLoad = rememberSaveable { mutableStateOf(true) }

//    val useSubList = rememberSaveable { mutableStateOf(false) }

    //刷新部分条目，若传空索引list，不会执行任何操作
    // 传非空list会刷新页面且仅重载指定索引的条目
    //若对应条目在刷新前不可见，加载时会将其设为可见
    val requireRefreshSubList = { itemIndices:List<Int> ->
        if(itemIndices.isNotEmpty()) {
            subDiffableItemList.value.clear()
            //被添加到sub list的索引，对应的条目如果在刷新前不可见，刷新时会将其设为可见
            subDiffableItemList.value.addAll(itemIndices)

            changeStateTriggerRefreshPage(needRefresh)
        }
    }

    val titleFileNameFontSize = remember { MyStyleKt.Title.firstLineFontSizeSmall }
    val titleRelativePathFontSize = remember { MyStyleKt.Title.secondLineFontSize }

    //标题长度限制到屏幕的一半
    val fileTitleFileNameWidthLimit = remember(configuration.screenWidthDp) {(configuration.screenWidthDp / 2).dp}

//    val titleFileNameLenLimit = remember(configuration.screenWidthDp) { with(UIHelper) {
//        val scrWidthPx = dpToPx(configuration.screenWidthDp, density)
//        // title标题使用的字体大小
//        val fontWidthPx = spToPx(titleFontSize, density)
//        try {
//            //根据屏幕宽度计算标题栏能显示的最大文件名，最后除以几就是限制不要超过屏幕的几分之1
//            //最后除的数越大，显示的字数越少
//            (scrWidthPx / fontWidthPx / 2.2).toInt().coerceAtLeast(defaultFileTitleFileNameLenLimit)
//        }catch (e: Exception) {
//            //这个不太危险，出错也没事，所以没必要记到error级别
//            MyLog.w(TAG, "#titleFileNameLenLimit: calculate title font length limit err: ${e.localizedMessage}")
//            defaultFileTitleFileNameLenLimit
//        }
//    } }

    // key: relative path under repo, value: loading boolean
//    val loadingMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "loadingMap", ) { mapOf<String, Boolean>() }
    // key: relative path under repo, value: loading Channel
//    val loadingChannelMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "loadingChannelMap", ) { mapOf<String, Channel<Int>>() }



//    val diffItemMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "diffItemMap") { mapOf<String, DiffItemSaver>() }

    val curItemIndex = rememberSaveable { mutableIntStateOf(curItemIndexAtDiffableItemList) }
    val curItemIndexAtDiffableItemList = Unit  // avoid mistake using



    val getTargetIdxOfLazyColumnByRelativePath = { relativePath:String ->
        diffableItemList.value.toList().let {
            var count = 0
            for((idx, item) in it.withIndex()) {
                if(relativePath == item.relativePath) {
                    // update current item index
                    curItemIndex.intValue = idx
                    break
                }

                count += if(item.visible) {
                    // file header 和 footer 和 用于填充的spacer占的item数
                    // 1 header + 1 spacers + 1 footer
                    val fileHeaderAndFooterAndSpacer = 3
                    // hunks * 2是因为每个hunk都有一个header(diff结果的hunk header)和一个spliter，总共两个items
                    // 最后减1是因为最后一个hunk不会显示末尾的spliter，因为有文件的footer兜底，所以到最后一个hunk就不需要显示分割线了
                    val hunksHeadersAndSpliters = item.diffItemSaver.hunks.size * 2 - 1;
                    // noDiffItemAvailable 只占1个条目，submoduleIsDirty 需要多加1个条目
                    (hunksHeadersAndSpliters + item.diffItemSaver.allLines + fileHeaderAndFooterAndSpacer) + (if(item.noDiffItemAvailable || item.submoduleIsDirty) 1 else 0)
                }else {
                    //只有 file header
                    1
                }
            }

            count
        }
    }



    val scrollToCurrentItemHeader = { relativePath:String ->
        val targetIdx = getTargetIdxOfLazyColumnByRelativePath(relativePath)


        UIHelper.scrollToItem(scope, listState, targetIdx)
    }

    val naviUp = {
        //把当前条目设为上次点击条目，这样返回列表后就会滚动到在这个页面最后看的条目了
        diffableItemList.value.getOrNull(curItemIndex.intValue)?.let {
            lastClickedItemKey.value = it.getItemKey()
        }

        // cl页面会针对这个列表执行操作，不过若在index，不管列表有几个条目都总是提交index所有条目
        if(fromScreen == DiffFromScreen.HOME_CHANGELIST) {
            Cache.set(Cache.Key.diffableList_of_fromDiffScreenBackToWorkTreeChangeList, diffableItemList.value.map {it.toChangeListItem()})
        }
        // index用不到这个
//        else if(fromScreen == DiffFromScreen.INDEX) {
//            Cache.set(Cache.Key.diffableList_of_fromDiffScreenBackToIndexChangeList, diffableItemList.value.map {it.toChangeListItem()})
//        }

        doJobWithMainContext {
            naviUp()
        }
    }


//    val curRepo = rememberSaveable { mutableStateOf(RepoEntity()) }
    val curRepo = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "curRepo", initValue = RepoEntity())

    // single mode不显示header；multi 则为每个条目显示标题栏
    val showMyFileHeader = isMultiMode


    //跳转到SubEditor页面
    //冲突条目无法进入diff页面，所以能预览diff定不是冲突条目，因此跳转到editor时应将mergemode初始化为假
    //diff页面不可能显示app内置目录下的文件，所以一率可编辑
    val openFileWithInnerSubPageEditor = { filePath:String ->
        openFileWithInnerSubPageEditor(
            context = activityContext,
            filePath = filePath,
            mergeMode = false,
            readOnly = false,
            onlyGoToWhenFileExists = true,
        )
    }


    //这些主要是用来在single mode时在当前页面显示的状态变量，在传给DiffContent时会直接使用列表里的对象的对应值；multi mode用不到这些，所以直接默认值就行，不用查list
//    val relativePathUnderRepoState = rememberSaveable(isMultiMode, curItemIndex.intValue) { mutableStateOf(
//        (if(isMultiMode) null else { diffableItemList.value.getOrNull(curItemIndex.intValue)?.let {
//                if(isFileHistory) (it as FileHistoryDto).filePathUnderRepo
//                else (it as StatusTypeEntrySaver).relativePathUnderRepo }
//        }) ?: "" ) };

//    val changeType = rememberSaveable(isMultiMode, curItemIndex.intValue) { mutableStateOf(
//        (if(isMultiMode) null else diffableItemList.value.getOrNull(curItemIndex.intValue)?.let {
//        if(isFileHistory) Cons.gitStatusModified else (it as StatusTypeEntrySaver).changeType
//    }) ?: "") }
//    val fileSize = rememberSaveable(isMultiMode, curItemIndex.intValue) { mutableLongStateOf(
//        (if(isMultiMode) null else diffableItemList.value.getOrNull(curItemIndex.intValue)?.let {
//        if(isFileHistory) 0L else (it as StatusTypeEntrySaver).fileSizeInBytes
//    }) ?: 0L) }
//    val isSubmodule = rememberSaveable(isMultiMode, curItemIndex.intValue) { mutableStateOf(
//        ( if(isMultiMode) null else diffableItemList.value.getOrNull(curItemIndex.intValue)?.let {
//        if(isFileHistory) false else (it as StatusTypeEntrySaver).itemType == Cons.gitItemTypeSubmodule
//    }) ?: false) }

    //根据类型判断没什么必要，就算非modified类型，想比较也可以比较，所以后来改成仅根据设置项判断了
//    val enableSelectCompare = rememberSaveable(changeType.value) { mutableStateOf(changeType.value == Cons.gitStatusModified && settings.diff.enableSelectCompare) }
    val enableSelectCompare = rememberSaveable(settings.diff.enableSelectCompare) { mutableStateOf(settings.diff.enableSelectCompare) }
//    val fileNameOnly = remember(relativePathUnderRepoState.value) { derivedStateOf {  getFileNameFromCanonicalPath(relativePathUnderRepoState.value)} }
//    val fileParentPathOnly = remember(relativePathUnderRepoState.value) { derivedStateOf {getParentPathEndsWithSeparator(relativePathUnderRepoState.value)}}
//    val fileFullPath = remember(curRepo.value.fullSavePath, relativePathUnderRepoState.value) { derivedStateOf{  } }

//    val isFileAndExist = remember(fileFullPath.value) { derivedStateOf {
//        File(fileFullPath.value).let { f ->
//            f.exists() && f.isFile
//        }
//    } }

    // "read-only" mode switchable, if is single, depend on condition; is is multi mode, 仅返回右边是否是local,然后会在遍历时和实际文件是否存在组成完整规则，决定当前条目是否只读
    // 不能切换，强制启用readonly
    val readOnlySwitchable = remember(localAtDiffRight) { derivedStateOf {
//        if(isSingleMode) localAtDiffRight else localAtDiffRight
        localAtDiffRight
    }}

    // cant switch = force readonly, else init value set to off
    // 不能切换等于强制只读，否则初始值设为关闭只读模式
    //配置文件启用了只读模式，或者被比较的右边的对象不是本地而强制启用只读模式
    val readOnlyModeOn = rememberSaveable(settings.diff.readOnly, readOnlySwitchable.value) { mutableStateOf(settings.diff.readOnly || readOnlySwitchable.value.not()) }

    val removeTargetFromChangeList = {targetItems: List<StatusTypeEntrySaver> ->
        //从cl页面的列表移除条目(根据仓库下相对路径移除）
        SharedState.homeChangeList_itemList.removeIf {
            targetItems.any {it2 -> it.relativePathUnderRepo == it2.relativePathUnderRepo}
        }
    }


    // hasIndex为null则不更新change list的index变量
    val handleChangeListPageStuffs:suspend (targetItems:List<StatusTypeEntrySaver> , hasIndex:Boolean?) ->Unit = { targetItems, hasIndex ->
        diffableListLock.withLock {
            //从当前列表移除已操作完毕的条目
            val noItems = if(targetItems.size == diffableItemList.value.size) {
                //清空列表再执行返回上级页面，不然一些已经用不到的数据还会占用内存
                diffableItemList.value.clear()
                //如果对所有条目执行了操作，则需要返回上级页面，因为留在这也没什么好看的了
                true
            }else {
                //bug: 有时候移除的是条目a，但在页面里却少了条目b，虽然操作没错但显示错了，原因不明
                val listIsEmpty = diffableItemList.value.let { list ->
                    list.removeIf { targetItems.any { it2 -> it.relativePath == it2.relativePathUnderRepo } }
                    list.isEmpty()
                }

                // 拷贝的效果：经过我的测试，bug有所缓解，但没完全解决，现状是：在index，multi files diff模式，
                // 在文件标题栏unstage单条目后还是有可能页面不刷新，但是，通过下拉或顶栏按钮刷新或展开收起条目后，
                // 列表会更新为最新状态，unstage的条目会消失，比之前强点，之前就算点刷新按钮，
                // 列表依然显示已经移除的条目，必须返回上级页面再进入才能正常。
                // 简而言之：修改之后，现在操作条目后，列表依然有可能错误显示已经移除的条目，但刷新后正常；修改之前，出错了刷新也没用。

                //拷贝每个条目，触发页面显示最新列表，不然有可能显示错
                if(listIsEmpty.not()) {
                    diffableItemList.value.toList().forEachIndexedBetter { idx, item ->
                        diffableItemList.value[idx] = item.copy()
                    }
                }

                listIsEmpty
            }


            //更新cl页面状态变量
            //从cl页面的列表移除条目
            removeTargetFromChangeList(targetItems)
            //cl页面设置索引有条目，因为上面stage成功了，所以大概率index至少有一个条目
            hasIndex?.let {
                SharedState.homeChangeList_indexHasItem.value = hasIndex
            }

            //若当前页面已经无条目则返回上级页面
            if(noItems) {
                naviUp()
            }
        }
    }

    val stageItem:suspend (List<StatusTypeEntrySaver>)->Unit= { targetItems ->
        val curRepo = curRepo.value
        try {
            // stage 条目
            Repository.open(curRepo.fullSavePath).use { repo ->
                Libgit2Helper.stageStatusEntryAndWriteToDisk(repo, targetItems)
            }

            //执行到这里，实际上已经stage成功了
            Msg.requireShow(activityContext.getString(R.string.success))


            //下面处理cl页面相关的变量
            handleChangeListPageStuffs(targetItems, true)

        }catch (e:Exception) {
            val errMsg = "err: ${e.localizedMessage}"
            Msg.requireShowLongDuration(errMsg)
            createAndInsertError(curRepo.id, errMsg)

            MyLog.e(TAG, "stage items for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
        }
    }

    val revertItem:suspend (List<StatusTypeEntrySaver>)->Unit = { targetItems ->
        val curRepo = curRepo.value

        try {
            //取出数据库路径
            Repository.open(curRepo.fullSavePath).use { repo ->
                val untrakcedFileList = mutableListOf<String>()  // untracked list，在我的app里这种修改类型显示为 "New"
                val pathspecList = mutableListOf<String>()  // modified、deleted 列表

                targetItems.forEachBetter { targetItem->
                    //新文件(Untracked)在index里不存在，若revert，只能删除文件，所以单独加到另一个列表
                    if(targetItem.changeType == Cons.gitStatusNew) {
                        untrakcedFileList.add(targetItem.canonicalPath)  //删除文件，添加全路径（但其实用仓库内相对路径也行，只是需要把仓库路径和仓库下相对路径拼接一下，而这个全路径是我在查询status list的时候拼好的，所以直接用就行）
                    }else if(targetItem.changeType != Cons.gitStatusConflict){  //冲突条目不可revert！其余index中有的文件，也就是git tracked的文件，删除/修改 之类的，都可恢复为index中的状态
                        pathspecList.add(targetItem.relativePathUnderRepo)
                    }
                }

                //如果列表不为空，恢复文件
                if(pathspecList.isNotEmpty()) {
                    Libgit2Helper.revertFilesToIndexVersion(repo, pathspecList)
                }
                //如果untracked列表不为空，删除文件
                if(untrakcedFileList.isNotEmpty()) {
                    Libgit2Helper.rmUntrackedFiles(untrakcedFileList)
                }
            }


            //操作完成，显示提示
            Msg.requireShow(activityContext.getString(R.string.success))

            // revert不需要操作index，所以参数2设为null
            handleChangeListPageStuffs(targetItems, null)

        }catch (e:Exception) {
            val errMsg = "err: ${e.localizedMessage}"
            Msg.requireShowLongDuration(errMsg)
            createAndInsertError(curRepo.id, errMsg)

            MyLog.e(TAG, "revert items for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
        }
    }

    // 参数是 relative path list
    val unstageItem:suspend (List<StatusTypeEntrySaver>)->Unit = { targetItems ->
        val curRepo = curRepo.value

        try {
            //menu action: unstage
            Repository.open(curRepo.fullSavePath).use { repo ->
                //do unstage
                Libgit2Helper.unStageItems(repo, targetItems.map { it.relativePathUnderRepo })
            }

            //操作完成，显示提示
            Msg.requireShow(activityContext.getString(R.string.success))

            handleChangeListPageStuffs(targetItems, null)
        }catch (e:Exception) {
            val errMsg = "err: ${e.localizedMessage}"
            Msg.requireShowLongDuration(errMsg)
            createAndInsertError(curRepo.id, errMsg)

            MyLog.e(TAG, "unstage items for repo '${curRepo.repoName}' err: ${e.stackTraceToString()}")
        }

    }


    val showRevertDialog = rememberSaveable { mutableStateOf(false) }
    val showUnstageDialog = rememberSaveable { mutableStateOf(false) }
    val revertDialogList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName="revertDialogList") { listOf<StatusTypeEntrySaver>() }
    val unstageDialogList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName="unstageDialogList") { listOf<StatusTypeEntrySaver>() }
    val revertCallback = remember { mutableStateOf<suspend ()->Unit>({}) }
    val unstageCallback = remember { mutableStateOf<suspend ()->Unit>({}) }
    val initRevertDialog = { items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit ->
        revertDialogList.value.apply {
            clear()
            addAll(items)
        }
        revertCallback.value = callback
        showRevertDialog.value = true
    }

    val initUnstageDialog = { items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit ->
        unstageDialogList.value.apply {
            clear()
            addAll(items)
        }
        unstageCallback.value = callback
        showUnstageDialog.value = true
    }

    //revert弹窗
    if(showRevertDialog.value) {
        ConfirmDialog(
            title=stringResource(R.string.revert),
            text=stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showRevertDialog.value=false}
        ) {  //onOk
            showRevertDialog.value=false

            doJobThenOffLoading {
                revertItem(revertDialogList.value.toList())
                revertCallback.value()
            }
        }
    }

    //unstage弹窗
    if(showUnstageDialog.value) {
        ConfirmDialog(
            title=stringResource(R.string.unstage),
            text=stringResource(R.string.are_you_sure),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showUnstageDialog.value=false}
        ) {  //onOk
            showUnstageDialog.value=false

            doJobThenOffLoading {
                unstageItem(unstageDialogList.value.toList())
                unstageCallback.value()
            }
        }
    }





    //考虑将这个功能做成开关，所以用状态变量存其值
    //ps: 这个值要么做成可在设置页面关闭（当然，其他与预览diff不相关的页面也行，总之别做成只能在正在执行O(nm)的diff页面开关就行），要么就默认app启动后重置为关闭，绝对不能做成只能在预览diff的页面开关，不然万一O(nm)算法太慢卡死导致这个东西关不了就尴尬了
    //20240618:目前临时开启O(nm)算法的机制是在预览diff页面三击屏幕，但app启动时会重置为关闭，日后需要添加相关设置项以方便用户使用
    val requireBetterMatchingForCompare = rememberSaveable { mutableStateOf(settings.diff.enableBetterButSlowCompare) }
    val matchByWords = rememberSaveable { mutableStateOf(settings.diff.matchByWords) }
//    val syntaxHighlightEnabled = rememberSaveable { mutableStateOf(settings.diff.syntaxHighlightEnabled) }
    val adjustFontSizeModeOn = rememberSaveable { mutableStateOf(false) }
    val adjustLineNumSizeModeOn = rememberSaveable { mutableStateOf(false) }
    val showLineNum = rememberSaveable { mutableStateOf(settings.diff.showLineNum) }
    val showOriginType = rememberSaveable { mutableStateOf(settings.diff.showOriginType) }
    val fontSize = rememberSaveable { mutableIntStateOf(settings.diff.fontSize) }
    val lineNumFontSize = rememberSaveable { mutableIntStateOf(settings.diff.lineNumFontSize) }
    val groupDiffContentByLineNum = rememberSaveable { mutableStateOf(settings.diff.groupDiffContentByLineNum) }

    // this loading not shown as default, only show when executing action,
    //  use DiffContent's loading state indicating is loading diff content or not
    val loadingForAction= rememberSaveable { mutableStateOf(false)}
    val loadingText = rememberSaveable { mutableStateOf("")}
    val loadingOn = { text:String ->
        loadingText.value = text
        loadingForAction.value = true
    }
    val loadingOff = {
        loadingForAction.value = false
        loadingText.value = ""
    }


    val pageRequest = rememberSaveable { mutableStateOf("") }










    // remember for make sure only have one instance bundle with a composable function's one life time
    //用remember是为了确保组件生命周期内只创建一个channel实例, 用 mutableStateOf() 是因为切换文件后需要创建新Channel
//    val loadChannelLock = Mutex()

    val refreshPage = { changeStateTriggerRefreshPage(needRefresh) }

//    val refreshPageIfComparingWithLocal = {
//        if(isDiffToLocal) {
//            changeStateTriggerRefreshPage(needRefresh)
//        }
//    }

    val getCurItem = {
        diffableItemList.value.getOrNull(curItemIndex.intValue) ?: DiffableItem.anInvalidInstance()
    }


    val showBackFromExternalAppAskReloadDialog = rememberSaveable { mutableStateOf(false)}
    val backFromExternalApp_ItemIdx = rememberSaveable { mutableIntStateOf(0) }
    val initBackFromExtAppDialog = { itemIdx:Int ->
        backFromExternalApp_ItemIdx.intValue = itemIdx
        showBackFromExternalAppAskReloadDialog.value = true
    }
    if(showBackFromExternalAppAskReloadDialog.value) {
        OpenAsAskReloadDialog(
            onCancel = { showBackFromExternalAppAskReloadDialog.value=false }
        ) { // doReload
            showBackFromExternalAppAskReloadDialog.value=false

            val targetIdx = backFromExternalApp_ItemIdx.intValue
            val target = diffableItemList.value.getOrNull(targetIdx)
            //将目标展开并刷新
            if(target != null) {
                //加载条目时会自动展开并刷新
                requireRefreshSubList(listOf(targetIdx))
            }else {
                Msg.requireShowLongDuration("reload err: index invalid")
            }
        }
    }

    val savePatchPath= rememberSaveable { mutableStateOf("") }
    val showSavePatchSuccessDialog = rememberSaveable { mutableStateOf(false)}

    if(showSavePatchSuccessDialog.value) {
        CreatePatchSuccessDialog(
            path = savePatchPath.value,
            closeDialog = {showSavePatchSuccessDialog.value = false}
        )
    }


    val showCreatePatchDialog = rememberSaveable { mutableStateOf(false)}
    val createPatchList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "createPatchList") { listOf<String>() }
    val initCreatePatchDialog = { relativePaths:List<String> ->
        createPatchList.value.clear()
        createPatchList.value.addAll(relativePaths)

        showCreatePatchDialog.value=true
    }
    if(showCreatePatchDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.create_patch),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(text = stringResource(R.string.are_you_sure))
                    }
                }
            },
            onCancel = { showCreatePatchDialog.value = false }
        ){
            showCreatePatchDialog.value = false

            val curRepo = curRepo.value
//            val relativePathUnderRepo = getCurItem().relativePath
            val leftCommit = treeOid1Str.value
            val rightCommit = treeOid2Str.value
            val createPatchList = createPatchList.value.toList()
            //因为存在 fromTo 等于 tree to tree，但比较的是index to local的情况，所以这里需要判断下
            //fromTo有点败笔，当初应该凭leftCommit和rightCommit来判断，决定采用哪个 diff 函数就对了
            val fromTo = if(leftCommit == Cons.git_IndexCommitHash && rightCommit == Cons.git_LocalWorktreeCommitHash) Cons.gitDiffFromIndexToWorktree else fromTo;

            doJobThenOffLoading(loadingOn,loadingOff, activityContext.getString(R.string.creating_patch)) job@{
                try {
                    val savePatchRet = ChangeListFunctions.createPath(
                        curRepo = curRepo,
                        leftCommit = leftCommit,
                        rightCommit = rightCommit,
                        fromTo = fromTo,
                        relativePaths = createPatchList
                    );

                    if(savePatchRet.success()) {
//                            savePatchPath.value = getFilePathStrBasedRepoDir(outFile.canonicalPath, returnResultStartsWithSeparator = true)
                        savePatchPath.value = savePatchRet.data?.outFileFullPath ?: ""
                        //之前app给savePatchPath赋值会崩溃，所以用了Cache规避，后来升级gradle解决了
//                            Cache.set(Cache.Key.changeListInnerPage_SavePatchPath, getFilePathStrBasedRepoDir(outFile.canonicalPath, returnResultStartsWithSeparator = true))
                        showSavePatchSuccessDialog.value = true
                    }else {
                        //抛异常，catch里会向用户显示错误信息 (btw: exception.message或.localizedMessage都不包含异常类型名，对用户展示比较友好)
                        throw (savePatchRet.exception ?: RuntimeException(savePatchRet.msg))
                    }
                }catch (e:Exception) {
                    val errPrefix = "create patch err: "
                    Msg.requireShowLongDuration(e.localizedMessage ?: errPrefix)
                    createAndInsertError(curRepo.id, errPrefix+e.localizedMessage)
                    MyLog.e(TAG, "$errPrefix${e.stackTraceToString()}")
                }

            }
        }

    }

    val showOpenAsDialog = rememberSaveable { mutableStateOf(false)}
    val readOnlyForOpenAsDialog = rememberSaveable { mutableStateOf(false)}
    val openAsDialogItem = mutableCustomStateOf(stateKeyTag, "openAsDialogItem") { DiffableItem() }
    val openAsDialogItemIdx = rememberSaveable { mutableIntStateOf(0) }

    val initOpenAsDialog = { idx:Int ->
        try {
            val target = diffableItemList.value.getOrNull(idx)
            //目标不存在
            if(target == null) {
                throw RuntimeException("index invalid")
            }else {
               val fileFullPath = target.fullPath

                //目标存在但文件不存在
                if(!File(fileFullPath).exists()) {
                    throw RuntimeException(activityContext.getString(R.string.file_doesnt_exist))
                }else { //目标存在且文件存在
                    openAsDialogItem.value = target
                    openAsDialogItemIdx.intValue = idx

                    showOpenAsDialog.value=true
                }
            }
        }catch (e:Exception) {
            val errPrefix = "'Open As' err: "
            Msg.requireShowLongDuration(errPrefix+e.localizedMessage)
            MyLog.e(TAG, errPrefix+e.stackTraceToString())
        }
    }

    if(showOpenAsDialog.value) {
        val openAsDialogItem = openAsDialogItem.value
        OpenAsDialog(readOnly=readOnlyForOpenAsDialog,fileName=openAsDialogItem.fileName, filePath = openAsDialogItem.fullPath,
            openSuccessCallback = {
                //只有在worktree的diff页面才有必要显示弹窗，在index页面没必要显示，在diff commit的页面更没必要显示，因为若修改，肯定是修改worktree的文件，你在index页面就算重载也看不到修改后的内容，所以没必要提示
//                if(isDiffToLocal || fromTo == Cons.gitDiffFromIndexToWorktree) {
                if(isDiffToLocal) {
                    //如果请求外部打开成功，不管用户有无选择app（想实现成选择才询问是否重新加载，但无法判断）都询问是否重载文件
                    // 显示询问是否重载的弹窗
                    initBackFromExtAppDialog(openAsDialogItemIdx.intValue)
                }
            }
        ) {
            //onClose
            showOpenAsDialog.value=false
        }
    }



    val detailsString = rememberSaveable { mutableStateOf("")}
    val showDetailsDialog = rememberSaveable { mutableStateOf(false)}
    if(showDetailsDialog.value){
        CopyableDialog(
            title = stringResource(R.string.details),
            text = detailsString.value,
            onCancel = { showDetailsDialog.value = false }
        ) {
            showDetailsDialog.value = false
            clipboardManager.setText(AnnotatedString(detailsString.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }

    //若itemIdx传-1表示不需要弹窗显示条目相关信息
    val initDetailsDialog = { itemIdx:Int ->
        val curItem = diffableItemList.value.getOrNull(itemIdx)

        val suffix = "\n\n"
        val sb = StringBuilder()

        //这个判断好像没什么卵用，之前是在这个if代码块里显示的左、右提交信息，后来感觉没什么判断的必要，就注释这个if了
//        if(treeOid1Str.value != Cons.git_AllZeroOidStr || treeOid2Str.value != Cons.git_AllZeroOidStr) {
//        }


        sb.append(activityContext.getString(R.string.comparing_label)+": ").append(Libgit2Helper.getLeftToRightDiffCommitsText(treeOid1Str.value, treeOid2Str.value, false)).append(suffix)
        sb.append(activityContext.getString(R.string.left)+": ").append(tree1FullHash.value).append(suffix)
        sb.append(activityContext.getString(R.string.right)+": ").append(tree2FullHash.value).append(suffix)

        // 显示数量，例如： "当前：1，总数：10"
        sb.append(replaceStringResList(activityContext.getString(R.string.current_n_all_m), listOf((itemIdx+1).toString(), diffableItemList.value.size.toString()))).append(suffix)


        //有效则显示条目信息，否则仅显示粗略信息
        if(curItem != null) {
            sb.append(activityContext.getString(R.string.name)+": ").append(curItem.fileName).append(suffix)


            if(isFileHistoryTreeToLocal || isFileHistoryTreeToPrev) {
                // if isFileHistoryTreeToLocal==true: curItemIndex is on FileHistory, which item got clicked , else curItemIndex is on FileHistory, which got long pressed
                //如果为true，则是从file history页面点击条目进来的，这时是 curItemIndex对应的条目 to local，所以当前提交是左边的提交也就是treeOid1Str；
                // 否则，是长按file history通过diff to prev进来的，这时，实际上是 prev to curItemIndex对应的条目，所以当前提交是右边的提交，即treeOid2Str
                val commitId = if(isFileHistoryTreeToLocal) treeOid1Str.value else treeOid2Str.value
                //这两个都是用户在文件历史列表点击或长按的条目的属性
                sb.append(activityContext.getString(R.string.commit_id)+": ").append(commitId).append(suffix)
                sb.append(activityContext.getString(R.string.entry_id)+": ").append(curItem.entryId).append(suffix)
            }else {  // 从changelist进到diff页面
                // cl 如果和index或worktree比较，无entry id，但如果和提交比较其实是有entry id的，不过一般没必要显示
                sb.append(activityContext.getString(R.string.change_type)+": ").append(curItem.diffItemSaver.changeType).append(suffix)
            }


            sb.append(activityContext.getString(R.string.path)+": ").append(curItem.relativePath).append(suffix)

            val fileFullPath = curItem.fullPath
            sb.append(activityContext.getString(R.string.full_path)+": ").append(fileFullPath).append(suffix)

            val file = File(fileFullPath)
            if(file.exists()) {
                if(file.isFile) {
                    sb.append(activityContext.getString(R.string.size)+": ").append(getHumanReadableSizeStr(file.length())).append(suffix)
                }

                sb.append(activityContext.getString(R.string.last_modified)+": ").append(getFormattedLastModifiedTimeOfFile(file)).append(suffix)
            }

        }



        detailsString.value = sb.removeSuffix(suffix).toString()

        showDetailsDialog.value = true
    }


    val showRestoreDialog = rememberSaveable { mutableStateOf(false) }
    val oidForRestoreDialog = rememberSaveable { mutableStateOf("") }
    val msgForRestoreDialog = rememberSaveable { mutableStateOf("") }
    val initRestoreDialog = {
        val targetCommitOid = if(isFileHistoryTreeToLocal){
            treeOid1Str.value
        }else { // isFileHistoryTreeToTree
            treeOid2Str.value
        }

        oidForRestoreDialog.value = targetCommitOid
        msgForRestoreDialog.value = try {
            Repository.open(curRepo.value.fullSavePath).use { repo ->
                Libgit2Helper.getCommitMsgOneLine(repo, targetCommitOid)
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "initRestoreDialog err: ${e.stackTraceToString()}")
            ""
        }

        showRestoreDialog.value = true
    }

    // only show restore for file history
    if(showRestoreDialog.value) {
        FileHistoryRestoreDialog(
            targetCommitOidStr = oidForRestoreDialog.value,
            commitMsg = msgForRestoreDialog.value,
            showRestoreDialog = showRestoreDialog,
            loadingOn = loadingOn,
            loadingOff = loadingOff,
            activityContext = activityContext,
            curRepo = curRepo,
            fileRelativePath = getCurItem().relativePath,
            repoId = repoId,
            onSuccess = {
                if(isFileHistoryTreeToLocal) {
                    changeStateTriggerRefreshPage(needRefresh)
                }
            }
        )
    }



    if(pageRequest.value == PageRequest.createPatchForAllItems) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            //从顶栏发起的请求，针对所有条目
            initCreatePatchDialog(diffableItemList.value.map { it.relativePath })
        }
    }

    if(pageRequest.value == PageRequest.goToBottomOfCurrentFile) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            val curItemRelativePath = getCurItem().relativePath
            val diffableItemList = diffableItemList.value
            val indexOfCurItem = diffableItemList.indexOfFirst { it.relativePath ==  curItemRelativePath }

            //仅在索引有效时跳转
            if(indexOfCurItem >= 0) {
                //计算当前条目的尾部需要获取下个条目的头部，这样计算简单些
                val nextItem = diffableItemList.getOrNull(indexOfCurItem + 1)
                val targetIdx = if(nextItem == null) {  //若当前条目是最后一个条目，下个条目就会是null，这时滚动到页面末尾即可
                    Int.MAX_VALUE
                }else {
                    // 获得当前条目底部索引（下个条目的头部索引 - 1）
                    getTargetIdxOfLazyColumnByRelativePath(nextItem.relativePath) - 1
                }

                //跳转
                UIHelper.scrollToItem(scope, listState, targetIdx)
            }
        }
    }


    //这个是页面顶栏请求的，现在只有单文件模式才用
    if(pageRequest.value == PageRequest.showOpenAsDialog) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initOpenAsDialog(curItemIndex.intValue)
        }
    }

    if(pageRequest.value == PageRequest.requireOpenInInnerEditor) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            // go editor sub page
            openFileWithInnerSubPageEditor(getCurItem().fullPath)
        }
    }

    val showOrHideAll = { show:Boolean ->
        //刷新页面
        if(show) {  //展开当前未展开的条目并重载未初始化的条目的内容
            //仅加载未展开条目，所以需要过滤下，用户如果想确保重载所有条目，先展开所有条目再点顶部刷新按钮即可
            val hideAndNeverLoadedList = mutableListOf<Int>()
            diffableItemList.value.toList().forEachIndexedBetter { idx, it ->
                if(it.visible.not()) {
                    if(it.neverLoadedDifferences()) {
                        hideAndNeverLoadedList.add(idx)
                    }else {  //当前条目虽然现在没展开，但是内容已经加载，仅展开，不重新加载
                        diffableItemList.value[idx] = it.copy(visible = true)
                    }
                }
            }

            requireRefreshSubList(hideAndNeverLoadedList)
        }else {  //隐藏所有条目，这个不用重载，直接把visible设下再更新下state list就可以了
            val allHideList = diffableItemList.value.map {it.copy(visible = false) }
            diffableItemList.value.clear()
            diffableItemList.value.addAll(allHideList)
        }
    }

    if(pageRequest.value == PageRequest.expandAll) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            showOrHideAll(true)
        }
    }

    if(pageRequest.value == PageRequest.collapseAll) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            showOrHideAll(false)
        }
    }

    if(pageRequest.value == PageRequest.showRestoreDialog) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initRestoreDialog()
        }
    }

    //点击title显示详情会请求这里，本页面内部显示详情直接调init，不走这里
    if(pageRequest.value == PageRequest.showDetails) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initDetailsDialog(curItemIndex.intValue)
        }
    }

    if(pageRequest.value == PageRequest.goToCurItem) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            scrollToCurrentItemHeader(getCurItem().relativePath)
        }
    }


    val lineClickedMenuOffset = remember(configuration.screenWidthDp) {
        DpOffset(x = (configuration.screenWidthDp/1.5f).coerceAtLeast(50f).dp, y=0.dp)
    }



    // 向下滚动监听，开始
    val pageScrolled = rememberSaveable { mutableStateOf(settings.showNaviButtons) }
//    val firstVisible = remember { derivedStateOf { listState.value } }
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
//            val nowAt = listState.value
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

    // newItem和newItemIndex都是要切换的文件的，不是当前文件的
    //newItem 可通过 newItemIndex 在 diffableList获得，这里传只是方便使用
    // the `oldItem` shouldn't get from diffablelist at here,
    //   because when call `switchItem` the list maybe updated (if did stage/revert/unstage to item),
    //   so the `oldItem` should get before update the list then pass to this method
    val switchItem = { oldItem:DiffableItem?, newItem:DiffableItem, newItemIndex:Int, isToNext:Boolean ->
        doJobThenOffLoading j@{
            // send close signal to old channel to abort loading
            oldItem?.closeLoadChannel()


            // switch to new item
            if(DiffFromScreen.isFromFileHistory(fromScreen)) {
                if(fromScreen == DiffFromScreen.FILE_HISTORY_TREE_TO_LOCAL) {
                    treeOid1Str.value = newItem.commitId
                }else {  // tree to prev
                    val leftOidStr = if(isToNext) {
                        diffableItemList.value.getOrNull(newItemIndex + 1)?.commitId
                    } else {
                        // when reache here, oldItem should never be null
                        oldItem?.commitId
                    }

                    if(leftOidStr.isNullOrBlank()) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.plz_lode_more_then_try_again))
                        return@j
                    }

                    treeOid1Str.value = leftOidStr
                    treeOid2Str.value = newItem.commitId
                }

            }

            curItemIndex.intValue = newItemIndex

            changeStateTriggerRefreshPage(needRefresh, requestType = StateRequestType.requireGoToTop)
        }

        Unit
    }




    val saveFontSizeAndQuitAdjust = {
        adjustFontSizeModeOn.value = false

        SettingsUtil.update {
            it.diff.fontSize = fontSize.intValue
        }

        Unit
    }
    val saveLineNumFontSizeAndQuitAdjust = {
        adjustLineNumSizeModeOn.value = false

        SettingsUtil.update {
            it.diff.lineNumFontSize = lineNumFontSize.intValue
        }

        Unit
    }

    val errorStrRes = stringResource(R.string.error)

//    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }


    //由于有多个条目，状态也变成多个，统一遍历参数传来的那个列表，但每个条目的属性存到map里，以relative path为key查询
    // key=relativePath, value={key: line.key, value:CompareLinePairResult}
    //只要比较过，就一定有 CompareLinePairResult 这个对象，但不代表匹配成功，若 CompareLinePairResult 存的stringpartlist不为null，则匹配成功，否则匹配失败
//    val stringPairMapMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "stringPairMapMap") { mapOf<String, ConcurrentMap<String, CompareLinePairResult>>() }
//    val comparePairBufferMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "comparePairBufferMap") { mapOf<String, CompareLinePair>() }
//    val submoduleIsDirtyMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "submoduleIsDirtyMap") { mapOf<String, Boolean>() }
//    val errMsgMap = mutableCustomStateMapOf(keyTag = stateKeyTag, keyName = "errMsgMap") { mapOf<String, String>() }

//    val reForEachDiffContent = { relativePath:String ->
////        diffItemMap.value.get(relativePath)?.let {
////           // 改key触发刷新
////            diffItemMap.value.put(relativePath, it.copy(keyForRefresh = getShortUUID()))
////        }
//
//        val index = diffableItemList.value.indexOfFirst { it.relativePath == relativePath }
//        if(index != -1) {
//            diffableItemList.value[index] = diffableItemList.value[index].copy()
//        }
//
//        Unit
//    }

    val updateCurrentViewingIdx = { viewingIndex:Int ->
        curItemIndex.intValue = viewingIndex
    }

    BackHandler(
        onBack = getBackHandler(
            naviUp = naviUp,
            adjustFontSizeMode=adjustFontSizeModeOn,
            adjustLineNumFontSizeMode=adjustLineNumSizeModeOn,
            saveFontSizeAndQuitAdjust = saveFontSizeAndQuitAdjust,
            saveLineNumFontSizeAndQuitAdjust = saveLineNumFontSizeAndQuitAdjust,
        )
    )

    val lastPosition = rememberSaveable { mutableStateOf(0) }

    //当只读模式开启或文件不存在则视为只读模式实际开启，这时在点击行的菜单里不显示编辑、恢复之类可修改文件的选项
    val fileActuallyReadOnly = { diffableItem: DiffableItem -> readOnlyModeOn.value || !diffableItem.toFile().exists()}



    val showSelectSyntaxLangDialog = rememberSaveable { mutableStateOf(false) }
    val diffableItemIndexForSelctSyntaxLangDialog = rememberSaveable { mutableStateOf(0) }
    val plScopeForSelctSyntaxLangDialog = rememberSaveable { mutableStateOf(PLScope.AUTO) }
    val diffableItemForSelectSyntaxLangDialog = mutableCustomStateOf<DiffableItem?>(stateKeyTag, "diffableItemForSelectSyntaxLangDialog") { null }
    // isshld is varibale name abbrev
    val initSelectSyntaxHighlightLanguagDialog = isshld@{ diffableItem: DiffableItem, diffableItemIndex: Int ->
        // disabled reason: even let user select language for an empty file, still nothing goes terrible, just will reload file once, no big deal
        // 禁用原因：如果用户停留在本app的diff页面，然后在外部改变了文件，这时如果用户点语法高亮选单，期望的是文件展开并以他选择的语法高亮语言显示最新的修改，
        //   如果继续用旧数据是否为空来判定，就会误判；最关键的是，就算文件内容为空，让用户选一下语言，其实也没什么坏的影响, no big deal
        // 如果文件已展开且hunks为空，应该没东西，直接返回即可；如果文件未展开，可能未加载，无法判断是否有内容。
//        if(diffableItem.visible && diffableItem.diffItemSaver.hunks.isEmpty()) {
//            Msg.requireShowLongDuration(activityContext.getString(R.string.file_is_empty))
//            return@isshld
//        }

        // make it as current item
        curItemIndex.value = diffableItemIndex

        // update dialog related states
        diffableItemIndexForSelctSyntaxLangDialog.value = diffableItemIndex
        diffableItemForSelectSyntaxLangDialog.value = diffableItem
        // 如果文件没展开,diffableItem.diffItemSaver没东西，所以这里保险起见，应该用diffableItem.fileName
        plScopeForSelctSyntaxLangDialog.value = diffableItem.diffItemSaver.getAndUpdateScopeIfIsAuto(diffableItem.fileName)

        showSelectSyntaxLangDialog.value = true
    }

    if(showSelectSyntaxLangDialog.value) {
        SelectSyntaxHighlightingDialog(
            plScope = plScopeForSelctSyntaxLangDialog.value,
            closeDialog = { showSelectSyntaxLangDialog.value = false }
        ) { newScope ->
            // the null-check `?:` just in case, actually the left hand variable never be null when reached here
            val item = diffableItemForSelectSyntaxLangDialog.value ?: getCurItem()
            // help gc free memory
            diffableItemForSelectSyntaxLangDialog.value = null

            // change language scope
            val scopeChanged = item.diffItemSaver.changeScope(newScope) != false

            val itemIndex = diffableItemIndexForSelctSyntaxLangDialog.value


            try {
                // if visible, reload;
                //   else let it visible.
                //   after switched visible, it will reload with new language scope
                // those code for single and multi file mode both are worked well
                // 这段代码对single和multi file mode 都适用
                if(scopeChanged || !item.visible) {
                    // must copy, because we maybe using a item that from a copied list
                    diffableItemList.value[itemIndex] = item.copy(visible = true)

                    requireRefreshSubList(listOf(itemIndex))
                }
            }catch (e: Exception) {
                val errPrefix = "switch language scope for '${item.fileName}' err: selected scope is `$newScope`, err="
                Msg.requireShowLongDuration("switch language scope err: ${e.localizedMessage}")
                MyLog.e(TAG, errPrefix + e.stackTraceToString())

                // create err for Repo
                doJobThenOffLoading {
                    createAndInsertError(repoId, errPrefix + e.localizedMessage)
                }
            }
        }
    }


    if(pageRequest.value == PageRequest.showSyntaxHighlightingSelectLanguageDialogForCurItem) {
        PageRequest.clearStateThenDoAct(pageRequest) {
            initSelectSyntaxHighlightLanguagDialog(getCurItem(), curItemIndex.intValue)
        }
    }




    val filePathOfEditLineDialog = rememberSaveable { mutableStateOf("") }
    val lineContentOfEditLineDialog = rememberSaveable { mutableStateOf("") }
    val lineNumOfEditLineDialog = rememberSaveable { mutableStateOf(LineNum.invalidButNotEof) }  // this is line number not index, should start from 1
    val lineNumStrOfEditLineDialog = rememberSaveable { mutableStateOf("") }  // this is line number not index, should start from 1

    //因为自定义存储器会把数据存到mutableState里，所以Cache里存的实际不是null，因此可以存null值
    val truePrependFalseAppendNullReplace = rememberSaveable { mutableStateOf<Boolean?>(null) }
    val showDelLineDialog = rememberSaveable { mutableStateOf(false) }
    val trueRestoreFalseReplace = rememberSaveable { mutableStateOf(false) }

    val initEditLineDialog = { content:String, lineNum:Int, prependOrAppendOrReplace:Boolean?, filePath:String ->
        if(lineNum == LineNum.invalidButNotEof){
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
        }else {
            filePathOfEditLineDialog.value = filePath
            truePrependFalseAppendNullReplace.value = prependOrAppendOrReplace
            lineContentOfEditLineDialog.value = content
            lineNumOfEditLineDialog.value = lineNum
            showEditLineDialog.value = true
        }
    }
    val initDelLineDialog = { lineNum:Int, filePath:String ->
        if(lineNum == LineNum.invalidButNotEof){
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
        }else {
            filePathOfEditLineDialog.value = filePath
            lineNumOfEditLineDialog.value = lineNum
            showDelLineDialog.value = true
        }
    }
    val initRestoreLineDialog = { content:String, lineNum:Int, trueRestoreFalseReplace_param:Boolean, filePath:String ->
        if(lineNum == LineNum.invalidButNotEof){
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
        }else {
            filePathOfEditLineDialog.value = filePath
            lineContentOfEditLineDialog.value = content
            lineNumOfEditLineDialog.value = lineNum
            lineNumStrOfEditLineDialog.value = ""+lineNum
            trueRestoreFalseReplace.value = trueRestoreFalseReplace_param
            showRestoreLineDialog.value = true
        }
    }


    if(showEditLineDialog.value) {
        val focusRequester = remember { FocusRequester() }

        ConfirmDialogAndDisableSelection(
            //禁用点击弹窗外部区域或按返回键关闭弹窗，这样可避免误操作而丢失正在编辑的数据
            onDismiss = {},

            title = if(truePrependFalseAppendNullReplace.value == true) stringResource(R.string.insert) else if(truePrependFalseAppendNullReplace.value == false) stringResource(R.string.append) else stringResource(R.string.edit),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    // get height for add bottom padding when showing softkeyboard
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
//                                println("layoutCoordinates.size.height:${layoutCoordinates.size.height}")
                        // 获取组件的高度
                        // unit is px ( i am not very sure)
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(
                            replaceStringResList(
                                stringResource(if (truePrependFalseAppendNullReplace.value == null) R.string.line_at_n else R.string.new_line_at_n),
                                listOf(""+(if(lineNumOfEditLineDialog.value == LineNum.EOF.LINE_NUM) LineNum.EOF.TEXT else if (truePrependFalseAppendNullReplace.value != false) lineNumOfEditLineDialog.value else { lineNumOfEditLineDialog.value + 1 }))
                            )
                        )
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = lineContentOfEditLineDialog.value,
                        onValueChange = {
                            lineContentOfEditLineDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.content))
                        },
                    )
                }
            },
            okBtnText = stringResource(R.string.save),
            cancelTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showEditLineDialog.value = false}
        ) {
            showEditLineDialog.value = false

            val fileFullPath = filePathOfEditLineDialog.value

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.saving)) job@{
                try {
                    val lineNum = lineNumOfEditLineDialog.value
                    if(lineNum<1 && lineNum!=LineNum.EOF.LINE_NUM) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
                        return@job
                    }

                    val lines = FsUtils.stringToLines(lineContentOfEditLineDialog.value)
                    val file = FilePath(fileFullPath).toFuckSafFile(activityContext)
                    if(truePrependFalseAppendNullReplace.value == true) {
                        FsUtils.prependLinesToFile(file, lineNum, lines, settings)
                    }else if (truePrependFalseAppendNullReplace.value == false) {
                        FsUtils.appendLinesToFile(file, lineNum, lines, settings)
                    }else {
                        FsUtils.replaceLinesToFile(file, lineNum, lines, settings)
                    }

                    Msg.requireShow(activityContext.getString(R.string.success))

                    refreshPage()
                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?:"err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, errMsg)
                }
            }
        }

        OneTimeFocusRightNow(focusRequester)

    }

    if(showDelLineDialog.value) {
        ConfirmDialogAndDisableSelection(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            replaceStringResList(
                                stringResource(R.string.line_at_n),
                                listOf(
                                    if (lineNumOfEditLineDialog.value != LineNum.EOF.LINE_NUM) {
                                        "" + lineNumOfEditLineDialog.value
                                    } else {
                                        LineNum.EOF.TEXT
                                    }
                                )
                            )
                        )
                    }
                }
            },
            okBtnText = stringResource(R.string.delete),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showDelLineDialog.value = false}
        ) {
            showDelLineDialog.value = false

            val fileFullPath = filePathOfEditLineDialog.value

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.deleting)) job@{
                try {
                    val lineNum = lineNumOfEditLineDialog.value
                    if(lineNum<1 && lineNum!=LineNum.EOF.LINE_NUM) {
                        Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_line_number))
                        return@job
                    }

                    val file = FilePath(fileFullPath).toFuckSafFile(activityContext)
                    FsUtils.deleteLineToFile(file, lineNum, settings)

                    Msg.requireShow(activityContext.getString(R.string.success))

                    refreshPage()

                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?:"err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, errMsg)
                }
            }
        }
    }

    if(showRestoreLineDialog.value) {
        val focusRequester = remember { FocusRequester() }

        ConfirmDialogAndDisableSelection(
            onDismiss = {},

            title = if(trueRestoreFalseReplace.value) stringResource(R.string.restore) else stringResource(R.string.replace),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    // get height for add bottom padding when showing softkeyboard
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
//                                println("layoutCoordinates.size.height:${layoutCoordinates.size.height}")
                        // 获取组件的高度
                        // unit is px ( i am not very sure)
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(stringResource(R.string.note_if_line_number_doesnt_exist_will_append_content_to_the_end_of_the_file), color = MyStyleKt.TextColor.getHighlighting())
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = lineNumStrOfEditLineDialog.value,
                        onValueChange = {
                            lineNumStrOfEditLineDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.line_number))
                        },
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = lineContentOfEditLineDialog.value,
                        onValueChange = {
                            lineContentOfEditLineDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.content))
                        },
                    )
                }
            },
            okBtnText = if(trueRestoreFalseReplace.value) stringResource(R.string.restore) else stringResource(R.string.replace),
            cancelTextColor = MyStyleKt.TextColor.danger(),

            onCancel = {showRestoreLineDialog.value = false}
        ) {
            showRestoreLineDialog.value = false

            val fileFullPath = filePathOfEditLineDialog.value

            doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.restoring)) job@{
                try {
                    var lineNum = try {
                        lineNumStrOfEditLineDialog.value.toInt()
                    }catch (_:Exception) {
                        LineNum.invalidButNotEof
                    }

                    if(lineNum<1) {
                        // for append content to EOF
                        lineNum=LineNum.EOF.LINE_NUM
                    }

                    val lines = FsUtils.stringToLines(lineContentOfEditLineDialog.value)
                    val file = FilePath(fileFullPath).toFuckSafFile(activityContext)
                    if(trueRestoreFalseReplace.value) {
                        FsUtils.prependLinesToFile(file, lineNum, lines, settings)
                    }else {
                        FsUtils.replaceLinesToFile(file, lineNum, lines, settings)
                    }

                    Msg.requireShow(activityContext.getString(R.string.success))

                    refreshPage()

                }catch (e:Exception) {
                    val errMsg = e.localizedMessage ?:"err"
                    Msg.requireShowLongDuration(errMsg)
                    createAndInsertError(repoId, errMsg)
                }
            }
        }

        OneTimeFocusRightNow(focusRequester)
    }






    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColorsSimple(),
                title = {
                    val curItem = getCurItem()

                    DiffScreenTitle(
                        isMultiMode = isMultiMode,
                        listState = listState,
                        scope = scope,
                        request = pageRequest,
                        readOnly = fileActuallyReadOnly(curItem),
                        lastPosition = lastPosition,
                        curItem = curItem
                    )
                },
                navigationIcon = {
                    if(adjustFontSizeModeOn.value || adjustLineNumSizeModeOn.value) {
                        LongPressAbleIconBtn(
                            tooltipText = stringResource(R.string.close),
                            icon = Icons.Filled.Close,
                            iconContentDesc = stringResource(R.string.close),

                        ) {
                            if(adjustFontSizeModeOn.value) {
                                saveFontSizeAndQuitAdjust()
                            }else {
                                saveLineNumFontSizeAndQuitAdjust()
                            }
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
                    if(adjustFontSizeModeOn.value) {
                        FontSizeAdjuster(fontSize = fontSize, resetValue = SettingsCons.defaultFontSize)
                    }else if(adjustLineNumSizeModeOn.value){
                        FontSizeAdjuster(fontSize = lineNumFontSize, resetValue = SettingsCons.defaultLineNumFontSize)
                    }else {
                        DiffPageActions(
                            isMultiMode = isMultiMode,
                            fromTo=fromTo,
                            refreshPage = refreshPage,
                            request = pageRequest,
//                            fileFullPath = getCurItem().fullPath,
                            requireBetterMatchingForCompare = requireBetterMatchingForCompare,
                            readOnlyModeOn = readOnlyModeOn,
                            readOnlyModeSwitchable = readOnlySwitchable.value,
                            showLineNum=showLineNum,
                            showOriginType=showOriginType,
                            adjustFontSizeModeOn = adjustFontSizeModeOn,
                            adjustLineNumSizeModeOn = adjustLineNumSizeModeOn,
                            groupDiffContentByLineNum=groupDiffContentByLineNum,
                            enableSelectCompare=enableSelectCompare,
                            matchByWords=matchByWords,
//                            syntaxHighlightEnabled = syntaxHighlightEnabled
                        )

                    }

                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
        floatingActionButton = {
            if(pageScrolled.value) {
                GoToTopAndGoToBottomFab(
                    scope = scope,
                    listState = listState,
                    listLastPosition = lastPosition,
                    showFab = pageScrolled
                )
            }
        }
    ) { contentPadding ->

        if(loadingForAction.value) {
            LoadingDialog(loadingText.value)
        }

//        if(fileSizeOverLimit) {  // 文件过大不加载
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
////                    .verticalScroll(rememberScrollState())
//                    .padding(contentPadding)
//                ,
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//            ) {
//                Row {
//                    Text(text = stringResource(R.string.file_size_over_limit)+"("+Cons.editorFileSizeMaxLimitForHumanReadable+")")
//                }
//            }
//        }else {  //文件大小ok

        //改成统一在DiffContent里检查实际diff需要获取的内容的大小了，和文件大小有所不同，有时候文件大小很大，但需要diff的内容大小实际很小，这时其实可以diff，性能不会太差

        //以前是如果启用编辑就不启用选择拷贝，因为如果在开了SelectionContainer的情况下显示弹窗，并在弹窗非输入框区域长按文字，会导致app崩溃，错误信息为内容不在一个层级之类的，但那是compose这个库的问题，不是我的问题，按逻辑上来说，就算启用编辑行的菜单，也应该可以选择拷贝文本，所以现在不做判断了，直接套可选择容器启用长按选择拷贝功能



        val loadingOnParent=loadingOn
        val loadingOffParent=loadingOff
        val enableSelectCompare = enableSelectCompare.value;

        PullToRefreshBox(
            contentPadding = contentPadding,
            onRefresh = { changeStateTriggerRefreshPage(needRefresh) }
        ) {
            MySelectionContainerPlaceHolder {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    state = listState
                ) {
                    val diffableItemList = diffableItemList.value
//                val itemIdxAtLazyColumn_Map = itemIdxAtLazyColumn_Map.value
//                itemIdxAtLazyColumn_Map.clear()  //没必要清，存在的路径每次循环都会覆盖，不存在的路径也不可能跳转，所以，没必要清
//                val itemsCount = IntBox(0)  //别用state系列变量，会死循环
//                println("itemsCount.intValue: ${itemsCount.intValue}")
                    diffableItemList.toList().forEachIndexedBetter diffableItemListLoop@{ diffableItemIdx, diffableItem ->
                        if(isSingleMode && diffableItemIdx != curItemIndex.intValue) return@diffableItemListLoop;

                        val diffItem = diffableItem.diffItemSaver
                        val changeType = diffableItem.diffItemSaver.changeType
                        val visible = diffableItem.visible
                        val diffableItemFile = diffableItem.toFile()
                        val relativePath = diffableItem.relativePath

                        // 启用了只读模式，或者当前文件不存在（不存在无法编辑，所以启用只读）
                        val readOnlyModeOn = fileActuallyReadOnly(diffableItem)

                        val switchVisible = {
                            val newVisible = visible.not()
                            //切换条目
                            diffableItemList[diffableItemIdx] = diffableItem.copy(visible = newVisible)

                            //点了谁就把当前条目更新成谁
                            curItemIndex.intValue = diffableItemIdx

                            //如果展开当前条目 且 当前条目未加载则加载(懒加载)
                            if(newVisible && diffableItem.neverLoadedDifferences()) {
                                requireRefreshSubList(listOf(diffableItemIdx))
                            }
                        }

                        val colorOfChangeType = UIHelper.getChangeTypeColor(changeType)


                        val iconSize = MenuIconBtnItem.defaultIconSize
                        val pressedCircleSize = MenuIconBtnItem.defaultPressedCircleSize

                        //这header得调一下，左边加个箭头点击能收起
                        if (showMyFileHeader) {

                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃
                            // item里最好就一个root组件，还有，如果使用HorizontalDivider，最好将其放到单独item里，否则有可能崩溃

                            item {
//                            itemsCount.intValue++

//                            itemIdxAtLazyColumn_Map.put(relativePath, itemsCount.intValue)
                                val moreMenuExpandState = remember { mutableStateOf(false) }
                                // x 放弃，不写footer了）把这个抽成 infobar，footer用同样的样式写
                                // LazyColumn里不能用rememberSaveable，会崩，用remember也有可能会不触发刷新，除非改外部的list触发遍历
                                BarContainer(
                                    modifier = Modifier
                                        .onGloballyPositioned { layoutCoordinates ->
                                            if(visible) {
                                                val position = layoutCoordinates.positionInRoot()
                                                //从屏幕上方消失了，就表示在看这个条目
                                                if(position.y < 0) {
                                                    updateCurrentViewingIdx(diffableItemIdx)
                                                }
                                            }
                                        }
                                    ,
                                    onClick = switchVisible,
                                    showMoreIcon = true,
                                    moreMenuExpandState = moreMenuExpandState,
                                    moreMenuItems = (if(fromScreen == DiffFromScreen.HOME_CHANGELIST) {
                                        mutableListOf(
                                            MenuTextItem(
                                                text = stringResource(R.string.stage),
                                                onClick = {
                                                    doJobThenOffLoading {
                                                        stageItem(listOf(diffableItem.toChangeListItem()))
                                                    }
                                                }
                                            ),

                                            MenuTextItem(
                                                text = stringResource(R.string.revert),
                                                onClick = {
                                                    initRevertDialog(listOf(diffableItem.toChangeListItem())) {}
                                                }
                                            )
                                        )
                                    }else if(fromScreen == DiffFromScreen.INDEX) {
                                        mutableListOf(
                                            MenuTextItem(
                                                text = stringResource(R.string.unstage),
                                                onClick = {
                                                    initUnstageDialog(listOf(diffableItem.toChangeListItem())) {}
                                                }
                                            )
                                        )
                                    }else {
                                        mutableListOf<MenuTextItem>()
                                    }).apply {
                                        add(
                                            //每个页面都显示导出patch
                                            MenuTextItem(
                                                text = stringResource(R.string.create_patch),
                                                onClick = {
                                                    initCreatePatchDialog(listOf(relativePath))
                                                }
                                            )
                                        )


                                        // always visible, but, if the diff screen syntax highlighting settings is disabled,
                                        //   then the default value is NONE, ranther than AUTO,
                                        //   so detect language scope will not available when it disabled,
                                        //   but users still can select syntax highligting manually to enable it
                                        add(
                                            MenuTextItem(
                                                text = stringResource(R.string.syntax_highlighting),
                                                onClick = {
                                                    initSelectSyntaxHighlightLanguagDialog(diffableItem, diffableItemIdx)
                                                }
                                            )
                                        )
                                    },
                                    actions = listOf(
                                        // refresh
                                        MenuIconBtnItem(
                                            icon = Icons.Filled.Refresh,
                                            text = stringResource(R.string.refresh),
                                            onClick = {
                                                //点刷新若条目没展开，会展开
                                                //不需要在这设置，加载子列表时会设置
//                                                   val newItem = diffableItem.copy(visible = true)
//                                                   diffableItemList[idx] = newItem

                                                requireRefreshSubList(listOf(diffableItemIdx))
                                            }
                                        ),

                                        // open
                                        MenuIconBtnItem(
                                            icon = Icons.Filled.FileOpen,
                                            text = stringResource(R.string.open),
                                            onClick = {
                                                openFileWithInnerSubPageEditor(diffableItem.fullPath)
                                            }
                                        ),

                                        // open as
                                        MenuIconBtnItem(
                                            icon = Icons.AutoMirrored.Filled.OpenInNew,
                                            text = stringResource(R.string.open_as),
                                            onClick = {
                                                initOpenAsDialog(diffableItemIdx)
                                            }
                                        ),

                                        ),
                                ) {
                                    val loadedAtLeastOnce = diffableItem.maybeLoadedAtLeastOnce()

                                    Row(
                                        modifier = Modifier.widthIn(max = fileTitleFileNameWidthLimit),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {

                                        //收起/展开 的箭头图标
                                        InLineIcon(
                                            iconModifier = Modifier.size(iconSize),
                                            pressedCircleSize = pressedCircleSize,
                                            icon = if(visible) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowRight ,
                                            tooltipText = "",
                                        )


                                        Column {
                                            //标题：显示文件名、添加了几行、删除了几行
                                            ScrollableRow(
                                                //点击文件名显示详情
                                                //确保最小可点击范围，这个不能放到外面的row里，外面的row还算了下面添加删除行的长度，多半会超，所以就没意义了
                                                modifier = Modifier
                                                    .clickable { initDetailsDialog(diffableItemIdx) }
                                                    .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
                                                ,
                                            ) {
                                                //显示：“文件名: +添加的行数, -删除的行数"，例如： "abc.txt: +1, -10"


                                                //如果只读，显示个图标让用户知道只读
                                                if(readOnlyModeOn) {
                                                    ReadOnlyIcon()
                                                }


                                                Text(
                                                    text = diffableItem.fileName,
                                                    fontSize = titleFileNameFontSize,
                                                    color = colorOfChangeType,
                                                )
                                            }




                                            //如果加载过，则显示添加删除了多少行
                                            ScrollableRow {
                                                Text(
                                                    text = diffableItem.getAnnotatedAddDeletedAndParentPathString(colorOfChangeType),
                                                    fontSize = titleRelativePathFontSize,
                                                )
                                            }

                                        }
                                    }

                                }
                            }
                        }

                        //不显示的话，后面就不用管了，让用户看个标题栏就行
                        if(!visible) {
                            return@diffableItemListLoop
                        }



//                    val mapKey = relativePath

                        //没diff条目的话，可能正在loading？
                        //就算没diff条目，也改显示个标题，证明有这么个条目存在

                        val isSubmodule = diffableItem.itemType == Cons.gitItemTypeSubmodule
                        val errMsg = diffableItem.errMsg
                        val submoduleIsDirty = diffableItem.submoduleIsDirty
                        val loading = diffableItem.loading
                        val loadChannel = diffableItem.loadChannel
//                        val fileChangeTypeIsModified = changeType == Cons.gitStatusModified

                        //不管什么change type，都应该走详情diff，因为允许 "左/右 to local" 后，changeType可能不准。
                        // 例如：一个文件的 changeType 是 New，但点击条目的 left to local后，可能会有修改，所以实际的change type可能会变成 modified
                        val fileChangeTypeIsModified = true




                        // only check when local as diff right(xxx..local)
                        // 若是文件且存在则启用
                        //用来检测是否启用 行点击菜单，但当时名字没起好，所以就叫这名了
//                        val enableLineEditActions = if(localAtDiffRight.not() || readOnlyModeOn || isSubmodule || (changeType != Cons.gitStatusNew && changeType != Cons.gitStatusModified)) {
                        // 允许比较左/右 to local后，检测 change type没意义了，所以不用检测了，只要实际存在就应该可编辑
                        val enableLineEditActions = if(localAtDiffRight.not() || readOnlyModeOn || isSubmodule) {
                            false
                        }else {
                            //存在且是文件且不匹配if里不能编辑的情况，就能编辑
                            diffableItemFile.let { f ->
                                f.exists() && f.isFile
                            }
                        }




                        //不支持预览二进制文件、超出限制大小、文件未修改，等
                        if (diffableItem.noDiffItemAvailable) {
                            item {
//                            itemsCount.intValue++

                                DisableSelection {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 100.dp, horizontal = 20.dp)
                                        ,
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        MySelectionContainer {
                                            diffableItem.whyNoDiffItem?.invoke()
                                        }

                                    }

                                    //多文件模式有footer，不需要显示
                                    if(isSingleMode) {
                                        HunkDivider()
                                    }
                                }
                            }
                        } else {  //文本类型且没超过大小且文件修改过，正常显示diff信息

                            // {key: line.key, value:CompareLinePairResult}
                            //只要比较过，就一定有 CompareLinePairResult 这个对象，但不代表匹配成功，若 CompareLinePairResult 存的stringpartlist不为null，则匹配成功，否则匹配失败
                            val indexStringPartListMapForComparePair = diffableItem.stringPairMap
                            //    val comparePair = mutableCustomStateOf(stateKeyTag, "comparePair") {CompareLinePair()}
                            val comparePairBuffer = diffableItem.compareLinePair



                            //这个显示在正常的内容顶部，占一行，不能挪到上面的if里
                            // show a notice make user know submodule has uncommitted changes
                            if (submoduleIsDirty) {
                                item {
                                    ScrollableRow (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                        ,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(stringResource(R.string.submodule_is_dirty_note_short), fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
                                    }
                                }
                            }


                            //TODO 实现预览图片需要在这做判断，如果两边有一个是img，就用DiffImg预览，DiffImg内部检查，哪边是图片就预览哪边，另一边如果是文本类型，就打印文本，如果不是就提示不支持的类型。


                            val groupDiffContentByLineNum = groupDiffContentByLineNum.value
                            val itemFile = File(curRepo.value.fullSavePath, relativePath)
                            val fileFullPath = itemFile.canonicalPath

                            val showOriginType = showOriginType.value
                            val showLineNum = showLineNum.value
                            val fontSize = fontSize.intValue.sp
                            val lastHunkIndex = diffItem.hunks.size - 1;
                            val lineNumSize = lineNumFontSize.intValue.sp
                            val getComparePairBuffer = { diffableItem.compareLinePair }
                            val setComparePairBuffer = { newCompareLinePair:CompareLinePair ->
                                diffableItemList[diffableItemIdx] = diffableItemList[diffableItemIdx].copy(compareLinePair = newCompareLinePair)
                            }

//                            val reForEachDiffContent = {reForEachDiffContent(relativePath)}
//                            val reForEachDiffContent = {}  //这函数用不到了，但先保留，日后若有用取消注释相关方法即可

                            // modified，并且设置项启用，则启用
                            val enableSelectCompare = fileChangeTypeIsModified && enableSelectCompare;
                            // 设置项启用则启用，不管文件类型，就算是删除的文件也可使用菜单的拷贝功能，之前判断不是修改就禁用
//                            val enableSelectCompare = enableSelectCompare.value;

                            //顶部padding，要把这个颜色弄得和当前行的类型（context/add/del）弄得一样才不违和，但处理起来有点麻烦，算了
//                    item { Spacer(Modifier.height(3.dp)) }

                            //数据结构是一个hunk header N 个行
                            diffItem.hunks.forEachIndexedBetter { hunkIndex, hunkAndLines: PuppyHunkAndLines ->

                                // hunk header
                                item {
                                    SelectionRow(
                                        modifier = Modifier
                                            .background(UIHelper.getHunkColor())
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = hunkAndLines.hunk.cachedNoLineBreakHeader(),
                                            fontFamily = PLFont.diffCodeFont(),
                                            fontSize = fontSize,
                                            fontStyle = FontStyle.Italic,
                                            color = UIHelper.getFontColor(),
                                        )
                                    }
                                }

                                //如果有EOF且最大行号不如EOF length长就设为eof的长度
                                val lineNumExpectLength = diffItem.maxLineNum.toString().length.let { if(diffItem.hasEofLine) it.coerceAtLeast(LineNum.EOF.TEXT.length) else it }

                                if (fileChangeTypeIsModified && proFeatureEnabled(detailsDiffTestPassed)) {  //增量diff
                                    if (!groupDiffContentByLineNum || FlagFileName.flagFileExist(FlagFileName.disableGroupDiffContentByLineNum)) {
                                        //this method need use some caches, clear them before iterate lines
                                        //这种方式需要使用缓存，每次遍历lines前都需要先清下缓存，否则可能多显示或少显示某些行
                                        hunkAndLines.clearCachesForShown()

                                        hunkAndLines.lines.forEachIndexedBetter printLine@{ lineIndex, line: PuppyLine ->
                                            //若非 新增行、删除行、上下文 ，不显示
                                            if (line.originType != PuppyLineOriginType.ADDITION
                                                && line.originType != PuppyLineOriginType.DELETION
                                                && line.originType != PuppyLineOriginType.CONTEXT
                                            ) {
                                                return@printLine
                                            }

                                            // true or fake context
                                            if (line.originType == PuppyLineOriginType.CONTEXT) {
                                                item {
//                                                    itemsCount.intValue++

                                                    DiffRow(
                                                        index = lineIndex,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = line,
                                                        fileFullPath = fileFullPath,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,
                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
//                                                        reForEachDiffContent = reForEachDiffContent,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                            } else {  // add or del

                                                // fake context
                                                // ignore which lines has ADD and DEL 2 types, but only difference at has '\n' or has not
                                                // 合并只有末尾是否有换行符的添加和删除行为context等于显示一个没修改的行，既然没修改，直接不显示不就行了？反正本来就自带context，顶多差一行
                                                //随便拷贝下del或add（不拷贝只改类型也行但不推荐以免有坏影响）把类型改成context，就行了
                                                //如果已经显示过，第2次获取result.data会是null，这时就不用再显示了
                                                val mergeAddDelLineResult = hunkAndLines.needShowAddOrDelLineAsContext(line.lineNum)
                                                //需要把add和del行转换为上下文行，这种情况发生在 add和del行仅一个有末尾换行符另一个没有时
                                                if (mergeAddDelLineResult.needShowAsContext) {
                                                    //若已经显示过，第2次再执行到这这个值就会是null，无需再显示，例如 add/del除了末尾换行符其他都一样，就会被转化为上下文，del先转换为上下文并显示了，等后面遍历到add时就无需再显示了
                                                    if (mergeAddDelLineResult.line != null) {
                                                        item {
//                                                            itemsCount.intValue++

                                                            DiffRow(
                                                                index = lineIndex,
                                                                lineNumExpectLength = lineNumExpectLength,
                                                                line = mergeAddDelLineResult.line,
                                                                fileFullPath = fileFullPath,
                                                                enableLineEditActions = enableLineEditActions,
                                                                clipboardManager = clipboardManager,
                                                                loadingOn = loadingOnParent,
                                                                loadingOff = loadingOffParent,
                                                                repoId = repoId,
                                                                showOriginType = showOriginType,
                                                                showLineNum = showLineNum,
                                                                fontSize = fontSize,
                                                                lineNumSize = lineNumSize,

                                                                getComparePairBuffer = getComparePairBuffer,
                                                                setComparePairBuffer = setComparePairBuffer,
                                                                betterCompare = requireBetterMatchingForCompare.value,
//                                                                reForEachDiffContent = reForEachDiffContent,
                                                                indexStringPartListMap = indexStringPartListMapForComparePair,
                                                                enableSelectCompare = enableSelectCompare,
                                                                matchByWords = matchByWords.value,
                                                                settings = settings,
                                                                navController = navController,
                                                                activityContext = activityContext,
                                                                stateKeyTag = stateKeyTag,
                                                                lineClickedMenuOffset = lineClickedMenuOffset,
                                                                diffItemSaver = diffItem,
                                                                initEditLineDialog = initEditLineDialog,
                                                                initDelLineDialog = initDelLineDialog,
                                                                initRestoreLineDialog = initRestoreLineDialog,
                                                            )
                                                        }
                                                    }

                                                    return@printLine
                                                }


                                                //                                val pair = comparePair.value
                                                // use pair
                                                val compareResult = indexStringPartListMapForComparePair[line.key]
                                                val stringPartListWillUse = if (compareResult == null) {
                                                    //没发现选择比较的结果，比较下实际相同行号不同类型（add、del）的行
                                                    val modifyResult = hunkAndLines.getModifyResult(
                                                        line = line,
                                                        requireBetterMatchingForCompare = requireBetterMatchingForCompare.value,
                                                        matchByWords = matchByWords.value
                                                    )

                                                    if (modifyResult?.matched == true) {
                                                        if (line.originType == PuppyLineOriginType.ADDITION) modifyResult.add else modifyResult.del
                                                    } else {
                                                        null
                                                    }

                                                } else {
                                                    compareResult.stringPartList
                                                }

                                                item {
//                                                    itemsCount.intValue++

                                                    DiffRow(
                                                        index = lineIndex,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = line,
                                                        fileFullPath = fileFullPath,
                                                        stringPartList = stringPartListWillUse,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,

                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
//                                                        reForEachDiffContent = reForEachDiffContent,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                            }
                                        }
                                    } else {  // grouped lines by line num
                                        //由于这个是一对一对的，所以如果第一行是一对，实际上两行都会有顶部padding，不过问题不大，看着不太难受
//                                        val lineIndex = mutableIntStateOf(-1) //必须用个什么东西包装一下，不然基本类型会被闭包捕获，值会错
//                                        val lineIndex = Box(-1) //必须用个什么东西包装一下，不然基本类型会被闭包捕获，值会错
                                        hunkAndLines.groupedLines.forEachBetter printLine@{ _lineNum: Int, lines: Map<String, PuppyLine> ->
//                                            lineIndex.value += 1;
//                                            val lineIndex = lineIndex.value

                                            //若非 新增行、删除行、上下文 ，不显示
                                            if (!(lines.contains(PuppyLineOriginType.ADDITION)
                                                        || lines.contains(PuppyLineOriginType.DELETION)
                                                        || lines.contains(PuppyLineOriginType.CONTEXT)
                                                        )
                                            ) {
                                                return@printLine
                                            }


                                            val add = lines.get(PuppyLineOriginType.ADDITION)
                                            val del = lines.get(PuppyLineOriginType.DELETION)
                                            val context = lines.get(PuppyLineOriginType.CONTEXT)
                                            //(deprecated:) 若 context del add同时存在，打印顺序为 context/del/add，不过不太可能3个同时存在，顶多两个同时存在
                                            //20250224 change: 若 context del add同时存在，打印顺序为 del/add/context ，不过不太可能3个同时存在，顶多两个同时存在
                                            val mergeDelAndAddToFakeContext = add != null && del != null && add.getContentNoLineBreak().equals(del.getContentNoLineBreak());


                                            // show `del` and `add` or `fake context `start
                                            if (mergeDelAndAddToFakeContext.not()) {  //分别显示add和del
                                                //show `del` and `add` start
                                                //                            val pair = comparePair.value
                                                //不存在的key是为了使返回值为null
                                                val addCompareLinePairResult = indexStringPartListMapForComparePair.get(add?.key ?: "nonexist keyadd")
                                                val delCompareLinePairResult = indexStringPartListMapForComparePair.get(del?.key ?: "nonexist keydel")
                                                var addUsedPair = false
                                                var delUsedPair = false

                                                var delStringPartListWillUse: List<IndexStringPart>? = null
                                                var addStringPartListWillUse: List<IndexStringPart>? = null

                                                if (delCompareLinePairResult != null && del != null) {
                                                    delUsedPair = true
                                                    delStringPartListWillUse = delCompareLinePairResult.stringPartList
                                                }

                                                if (addCompareLinePairResult != null && add != null) {
                                                    addUsedPair = true
                                                    addStringPartListWillUse = addCompareLinePairResult.stringPartList
                                                }


                                                // add and del, which not use the compare pair result by select compare, get the defalut compare result with it's default related line
                                                // 添加和删除，谁不使用选择比较的比较结果，就获取其和默认关联行的比较结果
                                                if(del != null && !delUsedPair) {
                                                    //没发现选择比较的结果，比较下实际相同行号不同类型（add、del）的行
                                                    val modifyResult = hunkAndLines.getModifyResult(
                                                        line = del,
                                                        requireBetterMatchingForCompare = requireBetterMatchingForCompare.value,
                                                        matchByWords = matchByWords.value
                                                    )

                                                    if (modifyResult?.matched == true) {
                                                        delStringPartListWillUse = modifyResult.del
                                                    }
                                                }

                                                if(add != null && !addUsedPair) {
                                                    //没发现选择比较的结果，比较下实际相同行号不同类型（add、del）的行
                                                    val modifyResult = hunkAndLines.getModifyResult(
                                                        line = add,
                                                        requireBetterMatchingForCompare = requireBetterMatchingForCompare.value,
                                                        matchByWords = matchByWords.value
                                                    )

                                                    if (modifyResult?.matched == true) {
                                                        addStringPartListWillUse = modifyResult.add
                                                    }
                                                }


                                                if (del != null) {
                                                    item {
//                                                        itemsCount.intValue++

                                                        DiffRow(
                                                            index = del.fakeIndexOfGroupedLine,
                                                            lineNumExpectLength = lineNumExpectLength,
                                                            line = del,
                                                            stringPartList = delStringPartListWillUse,
                                                            fileFullPath = fileFullPath,
                                                            enableLineEditActions = enableLineEditActions,
                                                            clipboardManager = clipboardManager,
                                                            loadingOn = loadingOnParent,
                                                            loadingOff = loadingOffParent,
                                                            repoId = repoId,
                                                            showOriginType = showOriginType,
                                                            showLineNum = showLineNum,
                                                            fontSize = fontSize,
                                                            lineNumSize = lineNumSize,

                                                            getComparePairBuffer = getComparePairBuffer,
                                                            setComparePairBuffer = setComparePairBuffer,
                                                            betterCompare = requireBetterMatchingForCompare.value,
//                                                            reForEachDiffContent = reForEachDiffContent,
                                                            indexStringPartListMap = indexStringPartListMapForComparePair,
                                                            enableSelectCompare = enableSelectCompare,
                                                            matchByWords = matchByWords.value,
                                                            settings = settings,
                                                            navController = navController,
                                                            activityContext = activityContext,
                                                            stateKeyTag = stateKeyTag,
                                                            lineClickedMenuOffset = lineClickedMenuOffset,
                                                            diffItemSaver = diffItem,
                                                            initEditLineDialog = initEditLineDialog,
                                                            initDelLineDialog = initDelLineDialog,
                                                            initRestoreLineDialog = initRestoreLineDialog,
                                                        )
                                                    }
                                                }

                                                if (add != null) {
                                                    item {
//                                                        itemsCount.intValue++

                                                        DiffRow(
                                                            index = add.fakeIndexOfGroupedLine,
                                                            lineNumExpectLength = lineNumExpectLength,
                                                            line = add,
                                                            stringPartList = addStringPartListWillUse,
                                                            fileFullPath = fileFullPath,
                                                            enableLineEditActions = enableLineEditActions,
                                                            clipboardManager = clipboardManager,
                                                            loadingOn = loadingOnParent,
                                                            loadingOff = loadingOffParent,
                                                            repoId = repoId,
                                                            showOriginType = showOriginType,
                                                            showLineNum = showLineNum,
                                                            fontSize = fontSize,
                                                            lineNumSize = lineNumSize,

                                                            getComparePairBuffer = getComparePairBuffer,
                                                            setComparePairBuffer = setComparePairBuffer,
                                                            betterCompare = requireBetterMatchingForCompare.value,
//                                                            reForEachDiffContent = reForEachDiffContent,
                                                            indexStringPartListMap = indexStringPartListMapForComparePair,
                                                            enableSelectCompare = enableSelectCompare,
                                                            matchByWords = matchByWords.value,
                                                            settings = settings,
                                                            navController = navController,
                                                            activityContext = activityContext,
                                                            stateKeyTag = stateKeyTag,
                                                            lineClickedMenuOffset = lineClickedMenuOffset,
                                                            diffItemSaver = diffItem,
                                                            initEditLineDialog = initEditLineDialog,
                                                            initDelLineDialog = initDelLineDialog,
                                                            initRestoreLineDialog = initRestoreLineDialog,
                                                        )
                                                    }
                                                }

                                                // show `del` and `add` end

                                            } else if (context == null) { //需要合并add和del且没有 real context，显示个fake context
                                                //如果mergeDelAndAddToFakeContext为假，且context!=null，则不会进入此代码块，这时，context和add和del去掉末尾换行符后的内容应该是一样的，所以不需要进入此代码块，直接执行后面代码显示真正的context即可
                                                // 但正常来说这种情况并不会发生，因为如果同时存在add和del，不太可能再次出现相同行号的context，就算出现，其内容也必然和add一样，而这时又分两种情况：1 add和del一样，那么add del context三者相同，直接显示context即可；
                                                // 2 add和del不同，则正常显示add和del和context，但这时add和context显示的内容是相同的，会重复，不过问题不大

                                                // show `fake context` start (就是那种add和del一个有换行符一个没有，其他都一样的情况，这种情况显示一个context文本取代两个看起来完全一样的add和del，但这个context其实是假的，不是真的)
                                                //只有在第一次执行比较时才执行此检查，且一旦转换，add和del行将消失，变成相同行号的上下文行
                                                //潜在bug：如果存在一个行号，同时有add/del/context 3种类型的行号且 add和del 仅末尾行号不同，而和context内容上有不同，就会有bug，会少显示add del行的内容，不过，应该不会存在这种情况

                                                //解决：两行除了末尾换行符没任何区别的情况仍显示diff的bug（有红有绿但没区别，令人迷惑）
                                                //如果转换成context，其实就不能触发select compare了，不过并没bug，因为如果转换为context，一开始就转换了，后面就不会再有选择行比较了？不对，有bug，必须得把原先的添加和删除类型的行删掉，换成一个上下文行，这样才能在之后选择比较行时无bug，我已经处理了

                                                //添加和删除行仅一个有换行符，另一个没有，当作没区别，移除添加和删除类型，并添加一个新的上下文行
                                                //转换后的行有无换行符无所谓，DiffRow显示前会先移除末尾的换行符
                                                //不能remove，不然切换group by line后，会有问题
                                                //                                lines.remove(Diff.Line.OriginType.ADDITION.toString())
                                                //                                lines.remove(Diff.Line.OriginType.DELETION.toString())

                                                //如果已经存在真context，就不显示这个假context了，否则显示
                                                //这里可以假设 add 和 context 是一样的，所以如果add和del一样，其实隐含了add == del == context，因此若context已经显示，就不需要再显示了
                                                //                                    val newContextLineFromAddAndDelOnlyLineBreakDifference = del.copy(originType = Diff.Line.OriginType.CONTEXT.toString())
                                                //                                    lines.put(Diff.Line.OriginType.CONTEXT.toString(), newContextLineFromAddAndDelOnlyLineBreakDifference)

                                                item {
//                                                    itemsCount.intValue++

                                                    DiffRow(
                                                        index = del.fakeIndexOfGroupedLine,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        //随便拷贝下del或add（不拷贝只改类型也行但不推荐以免有坏影响）把类型改成context，就行了
                                                        //这里del肯定不为null，因为 mergeDelAndAddToFakeContext 的条件包含了del和add都不为null
                                                        line = del!!.copy(originType = PuppyLineOriginType.CONTEXT),
                                                        fileFullPath = fileFullPath,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,

                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
//                                                        reForEachDiffContent = reForEachDiffContent,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )

                                                }


                                                //add和del合并成fake context了，同时没有真context需要显示，return，加载下一行
                                                return@printLine

                                                // show `fake context` end

                                            }

                                            // show `del` and `add` or `fake context `start

                                            // show real `context` start
                                            // true context
                                            if (context != null) {
                                                item {
//                                                    itemsCount.intValue++

                                                    //打印context
                                                    DiffRow(
                                                        index = context.fakeIndexOfGroupedLine,
                                                        lineNumExpectLength = lineNumExpectLength,
                                                        line = context,
                                                        fileFullPath = fileFullPath,
                                                        enableLineEditActions = enableLineEditActions,
                                                        clipboardManager = clipboardManager,
                                                        loadingOn = loadingOnParent,
                                                        loadingOff = loadingOffParent,
                                                        repoId = repoId,
                                                        showOriginType = showOriginType,
                                                        showLineNum = showLineNum,
                                                        fontSize = fontSize,
                                                        lineNumSize = lineNumSize,

                                                        getComparePairBuffer = getComparePairBuffer,
                                                        setComparePairBuffer = setComparePairBuffer,
                                                        betterCompare = requireBetterMatchingForCompare.value,
//                                                        reForEachDiffContent = reForEachDiffContent,
                                                        indexStringPartListMap = indexStringPartListMapForComparePair,
                                                        enableSelectCompare = enableSelectCompare,
                                                        matchByWords = matchByWords.value,
                                                        settings = settings,
                                                        navController = navController,
                                                        activityContext = activityContext,
                                                        stateKeyTag = stateKeyTag,
                                                        lineClickedMenuOffset = lineClickedMenuOffset,
                                                        diffItemSaver = diffItem,
                                                        initEditLineDialog = initEditLineDialog,
                                                        initDelLineDialog = initDelLineDialog,
                                                        initRestoreLineDialog = initRestoreLineDialog,
                                                    )
                                                }
                                            }

                                            // show real `context` end

                                        }

                                    }


                                } else { //普通预览，非pro或关闭细节compare时走这里
                                    //遍历行
                                    hunkAndLines.lines.forEachIndexedBetter printLine@{ lineIndex, line: PuppyLine ->
                                        //若非 新增行、删除行、上下文 ，不显示
                                        if (line.originType == PuppyLineOriginType.ADDITION
                                            || line.originType == PuppyLineOriginType.DELETION
                                            || line.originType == PuppyLineOriginType.CONTEXT
                                        ) {
                                            item {
//                                                itemsCount.intValue++

                                                DiffRow(
                                                    index = lineIndex,
                                                    lineNumExpectLength = lineNumExpectLength,
                                                    line = line,
                                                    fileFullPath = fileFullPath,
                                                    enableLineEditActions = enableLineEditActions,
                                                    clipboardManager = clipboardManager,
                                                    loadingOn = loadingOnParent,
                                                    loadingOff = loadingOffParent,
                                                    repoId = repoId,
                                                    showOriginType = showOriginType,
                                                    showLineNum = showLineNum,
                                                    fontSize = fontSize,
                                                    lineNumSize = lineNumSize,

                                                    getComparePairBuffer = getComparePairBuffer,
                                                    setComparePairBuffer = setComparePairBuffer,
                                                    betterCompare = requireBetterMatchingForCompare.value,
//                                                    reForEachDiffContent = reForEachDiffContent,
                                                    indexStringPartListMap = indexStringPartListMapForComparePair,
                                                    enableSelectCompare = enableSelectCompare,
                                                    matchByWords = matchByWords.value,
                                                    settings = settings,
                                                    navController = navController,
                                                    activityContext = activityContext,
                                                    stateKeyTag = stateKeyTag,
                                                    lineClickedMenuOffset = lineClickedMenuOffset,
                                                    diffItemSaver = diffItem,
                                                    initEditLineDialog = initEditLineDialog,
                                                    initDelLineDialog = initDelLineDialog,
                                                    initRestoreLineDialog = initRestoreLineDialog,
                                                )
                                            }
                                        }
                                    }
                                }


                                //EOF_NL only appear at last hunk, so better check index avoid non-sense iterate
                                if (hunkIndex == lastHunkIndex) {
                                    // if delete EOFNL or add EOFNL , show it
                                    val indexOfEOFNL = hunkAndLines.lines.indexOfFirst { it.originType == PuppyLineOriginType.ADD_EOFNL || it.originType == PuppyLineOriginType.DEL_EOFNL }

                                    if (indexOfEOFNL != -1) {  // found originType EOFNL
                                        val eofLine = hunkAndLines.lines.get(indexOfEOFNL)
                                        val fakeIndex = -1
                                        item {
//                                            itemsCount.intValue++

                                            DiffRow(
                                                // for now, the index only used to add top padding to first line, so passing a invalid fakeIndex is ok
                                                index = fakeIndex,

                                                lineNumExpectLength = lineNumExpectLength,
                                                line = LineNum.EOF.transLineToEofLine(eofLine, add = eofLine.originType == PuppyLineOriginType.ADD_EOFNL),
                                                fileFullPath = fileFullPath,
                                                enableLineEditActions = enableLineEditActions,
                                                clipboardManager = clipboardManager,
                                                loadingOn = loadingOnParent,
                                                loadingOff = loadingOffParent,
                                                repoId = repoId,
                                                showOriginType = showOriginType,
                                                showLineNum = showLineNum,
                                                fontSize = fontSize,
                                                lineNumSize = lineNumSize,

                                                getComparePairBuffer = getComparePairBuffer,
                                                setComparePairBuffer = setComparePairBuffer,
                                                betterCompare = requireBetterMatchingForCompare.value,
//                                                reForEachDiffContent = reForEachDiffContent,
                                                indexStringPartListMap = indexStringPartListMapForComparePair,
                                                enableSelectCompare = enableSelectCompare,
                                                matchByWords = matchByWords.value,
                                                settings = settings,
                                                navController = navController,
                                                activityContext = activityContext,
                                                stateKeyTag = stateKeyTag,
                                                lineClickedMenuOffset = lineClickedMenuOffset,
                                                diffItemSaver = diffItem,
                                                initEditLineDialog = initEditLineDialog,
                                                initDelLineDialog = initDelLineDialog,
                                                initRestoreLineDialog = initRestoreLineDialog,
                                            )
                                        }
                                    }
                                }

                                // multi files diff 不显示最后一个hunk spliter，因为有文件footer作分割了，没必要显示
                                if(isSingleMode || hunkIndex != lastHunkIndex) {
                                    item {
                                        //每个hunk之间显示个分割线，本来想弄成最后一个不显示，但判断索引不太好使，因为有的在上面就return了，索性都显示算了
                                        HunkDivider()
                                    }
                                }
                            }


                        }

                        item {
                            Spacer(Modifier.height(80.dp))
                        }

                        if(isMultiMode) {
                            item {
//                                itemsCount.intValue++

                                BarContainer(
                                    modifier = Modifier
                                        .onGloballyPositioned { layoutCoordinates ->
                                            if(visible) {
                                                val position = layoutCoordinates.positionInRoot()
                                                //从屏幕上方消失了，就表示在看这个条目
                                                if(position.y < 0) {
                                                    updateCurrentViewingIdx(diffableItemIdx)
                                                }
                                            }
                                        }
                                    ,
                                    horizontalArrangement = Arrangement.Center,
                                    onClick = {scrollToCurrentItemHeader(relativePath)}
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        ScrollableRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                        ) {
                                            Text(
                                                text = diffableItem.fileName,
                                                fontSize = titleFileNameFontSize,
                                                color = colorOfChangeType
                                            )
                                        }

                                        InLineIcon(
                                            iconModifier = Modifier.size(iconSize),
                                            pressedCircleSize = pressedCircleSize,
                                            icon = Icons.Filled.KeyboardDoubleArrowUp,
                                            tooltipText = "",  // empty to disable long pressed show toast
                                        )
                                    }
                                }
                            }

                        }
                    }


                    //切换上下文件和执行操作的按钮
                    item {
                        Spacer(Modifier.height(if(isMultiMode) 150.dp else 50.dp))
                    }

                    item {
                        DisableSelection {
                            NaviButton(
                                stateKeyTag = stateKeyTag,
                                isMultiMode = isMultiMode,
                                fromScreen = fromScreen,
                                diffableItemList = diffableItemList,
                                curItemIndex = curItemIndex,
                                switchItem = switchItem,
                                fromTo = fromTo,
                                naviUp = naviUp,
                                lastClickedItemKey = lastClickedItemKey,
                                pageRequest = pageRequest,
                                stageItem = stageItem,
                                initRevertDialog = initRevertDialog,
                                initUnstageDialog = initUnstageDialog,
                                goToTop = goToTop,
                            )

                        }
                    }

                    item {
                        Spacer(Modifier.height(100.dp))
                    }




                }
            }
        }




    }


    LaunchedEffect(needRefresh.value) {
        val (requestType, requestData) = getRequestDataByState<Any?>(needRefresh.value)

        //切换条目后回到列表顶部
        if(requestType == StateRequestType.requireGoToTop) {
            goToTop()
        }

        //如果想只加载指定条目，可设置条目到sub列表，否则加载全部条目
//        val willLoadList = if(useSubList.value) subDiffableItemList.value else diffableItemList.value

        val subList = subDiffableItemList.value.toList()
        subDiffableItemList.value.clear()  //确保用一次就清空，不然下次刷新还是只刷新这几个
        val subDiffableItemList = Unit  // avoid mistake using


        //从数据库查询repo，记得用会自动调用close()的use代码块
        val repoDb = dbContainer.repoRepository
        val repoFromDb = repoDb.getById(repoId)
        if(repoFromDb == null) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.repo_id_invalid))
            //没什么必要记，无非就是id无效，仓库不存在
//            MyLog.e(TAG, "#LaunchedEffect: query repo entity failed, repoId=$repoId")
            return@LaunchedEffect
        }

        curRepo.value = repoFromDb


        val treeOid1Str = treeOid1Str.value
        val treeOid2Str = treeOid2Str.value

        // resolve left and right to full hash
        try {
            Repository.open(repoFromDb.fullSavePath).use { repo ->
                Libgit2Helper.resolveCommitByHashOrRef(repo, treeOid1Str).let {
                    tree1FullHash.value = it.data?.id()?.toString() ?: treeOid1Str
                }

                Libgit2Helper.resolveCommitByHashOrRef(repo, treeOid2Str).let {
                    tree2FullHash.value = it.data?.id()?.toString() ?: treeOid2Str
                }
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "resolve tree1 and tree2 full hash failed: treeOid1Str=$treeOid1Str, treeOid2Str=$treeOid2Str, err=${e.stackTraceToString()}")
        }

        //初次进页面，滚动到目标条目，例如：点击了文件a进入的diff页面，则滚动到文件a那里
        val firstLoad = firstTimeLoad.value
        if(isMultiMode && firstTimeLoad.value) {
            firstTimeLoad.value = false

            scope.launch {
                runCatching {
                    //等下列表加载，不然对应条目还没加载出来呢，你就滚，不一定会滚到哪！
                    delay(500)
                    // *3是因为每个条目显示header会使用3个item，原本是显示一个的，但lazy column有bug，如果把divider和row放一起，
                    // 且写成 item {row, divider} 这个顺序，滑动列表，会崩溃，所以只好把divider单独列出来了
//                UIHelper.scrollToItem(scope, listState, curItemIndex.intValue*3+1)
                    UIHelper.scrollToItem(scope, listState, curItemIndex.intValue)
                }
            }
        }

        val noMoreMemToaster = OneTimeToast()


        diffableItemList.value.toList().forEachIndexedBetter label@{ idx, item ->
            //single mode仅加载当前查看的条目
            if(isSingleMode && idx != curItemIndex.intValue) return@label;

            //如果设置了子列表则只加载子列表条目
            //初次加载会把初始索引设置到subList里，这时条目未展开也一样加载并且只会加载一个条目，但是后续加载时则仅加载展开（visible)的条目，不可见的条目不会加载
            //若想立刻加载所有，可通过顶栏的展开全部按钮再点刷新
            //仅加载指定列表条目(即使不可见，也加载，加载前会设为可见)，若指定列表为空，则只加载展开的条目
            if(!isSingleMode && ((subList.isNotEmpty() && subList.contains(idx).not()) || (subList.isEmpty() && !item.visible))) return@label;

            //创建新条目前，把旧条目的loadChannel关了，否则如果之前的任务(加载diffItemSaver)未完成，不会取消，会继续执行
            item.closeLoadChannel()

            // loading时会改状态，所以需要创建个新的条目
            val item  = item.copyForLoading()
            diffableItemList.value[idx] = item  //更新 state list，不然页面可能不知道当前条目更改了，那你就只能继续看旧的了

            val relativePath = item.relativePath
            val isSubmodule = item.itemType == Cons.gitItemTypeSubmodule;
//            val mapKey = relativePath
            val channelForThisJob = item.loadChannel
            //x 不会）后来发现是LazyColumn里使用rememberSaveable导致的，随机发生，是个bug，这里不能用doJobThenOffLoading，会导致app崩溃
//            scope.launch {
            doJobThenOffLoading launch@{
//                delay(5000) //scope.launch不会导致App界面线程阻塞
//                throw RuntimeException("abc")  // scope.lauch必须妥善处理异常否则会导致App界面线程崩溃

    //      设置页面loading为true
    //      从数据库异步查询repo数据，调用diff方法获得diff内容，然后使用diff内容更新页面state
    //      最后设置页面loading 为false
                //这里用这个会导致app随机崩溃，报错："java.lang.ClassCastException: 包名.MainActivity cannot be cast to androidx.compose.runtime.saveable.SaveableHolder"
//            doJobThenOffLoading launch@{
                val loadedDiffableItem = try {
                    // set loading
//                    loadingMap.value.put(mapKey, true)
                    // reset before loading

                    // init
//                    val indexStringPartListMapForComparePair = stringPairMapMap.value.getOrPut(mapKey) { ConcurrentMap() }
//                    comparePairBufferMap.value.put(mapKey, CompareLinePair())
//                    submoduleIsDirtyMap.value.put(mapKey, false)
//                    errMsgMap.value.put(mapKey, "")
//                    indexStringPartListMapForComparePair.clear()


                    if(channelForThisJob.tryReceive().isClosed) {
                        return@launch
                    }


                    val languageScope = item.diffItemSaver.languageScope.value

                    Repository.open(repoFromDb.fullSavePath).use { repo->
                        val diffItemSaver = if(treeOid1Str == treeOid2Str) {
                            //两hash一样，不可能有差异，返回空结果即可
                            // 设上必须的字段，若不设置，会误判没加载过diff内容而不显示添加删除了多少行
                            DiffItemSaver(relativePathUnderRepo = relativePath, fromTo = fromTo)
                        } else if(fromTo == Cons.gitDiffFromTreeToTree || fromTo == Cons.gitDiffFileHistoryFromTreeToLocal || fromTo == Cons.gitDiffFileHistoryFromTreeToPrev){  //从提交列表点击提交进入
                            val diffItemSaver = if(Libgit2Helper.CommitUtil.isLocalCommitHash(treeOid1Str) || Libgit2Helper.CommitUtil.isLocalCommitHash(treeOid2Str)) {  // tree to work tree, oid1 or oid2 is local, both local will cause err
                                val reverse = Libgit2Helper.CommitUtil.isLocalCommitHash(treeOid1Str)

                                //前面已经确定了有个是local，再判断下是否有个是index，若是，就是实际上的 index to worktree
                                val isActuallyIndexToLocal = if(reverse) treeOid2Str == Cons.git_IndexCommitHash else (treeOid1Str == Cons.git_IndexCommitHash);
                                val tree1 = if(isActuallyIndexToLocal) {
                                    null
                                }else {
                                    Libgit2Helper.resolveTree(repo, if(reverse) treeOid2Str else treeOid1Str)
                                }

                                MyLog.d(TAG, "treeOid1Str:$treeOid1Str, treeOid2Str:$treeOid2Str, reverse=$reverse")

                                Libgit2Helper.getSingleDiffItem(
                                    repo,
                                    relativePath,
                                    if(isActuallyIndexToLocal) Cons.gitDiffFromIndexToWorktree else fromTo,
                                    tree1,
                                    null,
                                    reverse=reverse,
                                    treeToWorkTree = true,
                                    maxSizeLimit = settings.diff.diffContentSizeMaxLimit,
                                    loadChannel = channelForThisJob,
                                    checkChannelLinesLimit = settings.diff.loadDiffContentCheckAbortSignalLines,
                                    checkChannelSizeLimit = settings.diff.loadDiffContentCheckAbortSignalSize,
                                    languageScope = languageScope,

                                )
                            }else { // tree to tree, no local(worktree)
                                val tree1 = Libgit2Helper.resolveTree(repo, treeOid1Str)
                                val tree2 = Libgit2Helper.resolveTree(repo, treeOid2Str)

                                Libgit2Helper.getSingleDiffItem(
                                    repo,
                                    relativePath,
                                    fromTo,
                                    tree1,
                                    tree2,
                                    maxSizeLimit = settings.diff.diffContentSizeMaxLimit,
                                    loadChannel = channelForThisJob,
                                    checkChannelLinesLimit = settings.diff.loadDiffContentCheckAbortSignalLines,
                                    checkChannelSizeLimit = settings.diff.loadDiffContentCheckAbortSignalSize,
                                    languageScope = languageScope,

                                )
                            }

                            if(channelForThisJob.tryReceive().isClosed) {
                                return@launch
                            }

//                            diffItemMap.value.put(mapKey, diffItemSaver)
                            diffItemSaver
                        }else {  //indexToWorktree or headToIndex
                            val diffItemSaver = Libgit2Helper.getSingleDiffItem(
                                repo,
                                relativePath,
                                fromTo,
                                maxSizeLimit = settings.diff.diffContentSizeMaxLimit,
                                loadChannel = channelForThisJob,
                                checkChannelLinesLimit = settings.diff.loadDiffContentCheckAbortSignalLines,
                                checkChannelSizeLimit = settings.diff.loadDiffContentCheckAbortSignalSize,
                                languageScope = languageScope,

                            )

                            if(channelForThisJob.tryReceive().isClosed) {
                                return@launch
                            }

//                            diffItemMap.value.put(mapKey, diffItemSaver)
                            diffItemSaver
                        }

                        // only when compare to work tree need check submodule is or is not dirty. because only non-dirty(clean) submodule can be stage to index, and can be commit to log.
                        val submdirty = if(isDiffToLocal && isSubmodule) {
                            val submdirty = Libgit2Helper.submoduleIsDirty(parentRepo = repo, submoduleName = relativePath)

                            if(channelForThisJob.tryReceive().isClosed) {
                                return@launch
                            }

//                            submoduleIsDirtyMap.value.put(mapKey, submdirty)
                            submdirty
                        }else {
                            false
                        }


                        diffItemSaver.startAnalyzeSyntaxHighlight(noMoreMemToaster)


                        item.copy(loading = false, submoduleIsDirty = submdirty, diffItemSaver = diffItemSaver)
                    }


                }catch (e:Exception) {
                    if(channelForThisJob.tryReceive().isClosed) {
                        return@launch
                    }

                    val errMsg = errorStrRes + ": " + e.localizedMessage


                    createAndInsertError(repoId, errMsg)
                    MyLog.e(TAG, "#LaunchedEffect err: "+e.stackTraceToString())

                    item.copy(loading = false, errMsg = errMsg)
                }


                val loading = loadedDiffableItem.loading
                val errMsg = loadedDiffableItem.errMsg
                val changeType = item.diffItemSaver.changeType
                val diffItem = loadedDiffableItem.diffItemSaver
                val submoduleIsDirty = loadedDiffableItem.submoduleIsDirty

                //判断是否是支持预览的修改类型
                // 注意：冲突条目不能diff，会提示unmodified！所以支持预览冲突条目没意义，若支持的话，在当前判断条件后追加后面的代码即可: `|| changeType == Cons.gitStatusConflict`
                val isSupportedChangeType = (
                        changeType == Cons.gitStatusModified
                                || changeType == Cons.gitStatusUnmodified
                                || changeType == Cons.gitStatusNew
                                || changeType == Cons.gitStatusDeleted
                                || changeType == Cons.gitStatusTypechanged  // e.g. submodule folder path change to a file, will show type changed, view this is ok
                        )

                val loadingFinishedButHasErr = (loading.not() && errMsg.isNotBlank())
                val unsupportedChangeType = !isSupportedChangeType
                val isBinary = diffItem?.flags?.contains(Diff.FlagT.BINARY) ?: false
                val fileNoChange = !(diffItem?.isFileModified ?: false)
                val isContentSizeOverLimit = diffItem?.isContentSizeOverLimit == true
                val noHunks = loadedDiffableItem.diffItemSaver.hunks.isEmpty();

                loadedDiffableItem.noDiffItemAvailable = loading || loadingFinishedButHasErr || unsupportedChangeType || isBinary || fileNoChange || isContentSizeOverLimit || noHunks;

                if (loadedDiffableItem.noDiffItemAvailable) {
                    //这个顺序很重要，不然可能在loading的时候显示其他信息，类似之前提交历史页面的判断没弄好，导致在loading时显示列表为空

                    loadedDiffableItem.whyNoDiffItem_msg = if (loading) {
                        activityContext.getString(R.string.loading)
                    } else if (loadingFinishedButHasErr) {
                        errMsg
                    } else if (unsupportedChangeType) {
                        activityContext.getString(R.string.unknown_change_type)
                    } else if (isBinary) {
                        activityContext.getString(R.string.doesnt_support_view_binary_file)
                    } else if (fileNoChange) {
                        if (isSubmodule && submoduleIsDirty) {  // submodule no diff for shown, give user a hint
                            activityContext.getString(R.string.submodule_is_dirty_note)
                        } else {
                            activityContext.getString(R.string.the_file_has_not_changed)
                        }
                    } else if (isContentSizeOverLimit) {
                        activityContext.getString(R.string.content_size_over_limit) + "(" + getHumanReadableSizeStr(settings.diff.diffContentSizeMaxLimit) + ")"
                    } else if(noHunks) {
                        activityContext.getString(R.string.file_is_empty)
                    } else "";

                    //组件，用来在当前页面展示的
                    loadedDiffableItem.whyNoDiffItem = {
                        Row {
                            Text(
                                text = loadedDiffableItem.whyNoDiffItem_msg,
                                color = if(loadingFinishedButHasErr) MyStyleKt.TextColor.error() else Color.Unspecified,
                            )
                        }
                    }
                }


                //加载完毕，把加载的对象存上，更新loading状态，赋新实例给list，触发页面刷新
                diffableListLock.withLock {
                    diffableItemList.value[idx] = loadedDiffableItem
                }


            }
        }


//        }


    }


    DisposableEffect(Unit) {
        onDispose {
            doJobThenOffLoading {
                //关闭所有的load channel
                diffableItemList.value.forEachBetter { it.closeLoadChannel() }
            }
        }
    }

}


@Composable
private fun getBackHandler(
    naviUp:()->Unit,
    adjustFontSizeMode: MutableState<Boolean>,
    adjustLineNumFontSizeMode: MutableState<Boolean>,
    saveFontSizeAndQuitAdjust:()->Unit,
    saveLineNumFontSizeAndQuitAdjust:()->Unit,
): () -> Unit {
    val backHandlerOnBack = {
        if(adjustFontSizeMode.value) {
            saveFontSizeAndQuitAdjust()
        }else if(adjustLineNumFontSizeMode.value) {
            saveLineNumFontSizeAndQuitAdjust()
        }else {
            naviUp()
        }

        Unit
    }

    return backHandlerOnBack
}


private val headIconWidth = MyStyleKt.trailIconSize

@Composable
private fun NaviButton(
    isMultiMode: Boolean,
    fromScreen: DiffFromScreen,
    stateKeyTag:String,
//    activityContext: Context,
//    curRepo:RepoEntity,
    fromTo: String,
    diffableItemList: MutableList<DiffableItem>,
    curItemIndex: MutableIntState,
    lastClickedItemKey: MutableState<String>,
    pageRequest:MutableState<String>,
//    revertItem:suspend (StatusTypeEntrySaver)->Unit,
//    unstageItem:suspend (StatusTypeEntrySaver)->Unit,
    stageItem:suspend (List<StatusTypeEntrySaver>)->Unit,
    initRevertDialog:(items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit)->Unit,
    initUnstageDialog:(items:List<StatusTypeEntrySaver>, callback:suspend ()->Unit)->Unit,
    naviUp:()->Unit,
    switchItem: (oldItem: DiffableItem?, newItem:DiffableItem, newItemIndex: Int, isToNext:Boolean) -> Unit,
    goToTop:()->Unit,
) {
    val isFileHistoryTreeToLocalOrTree = fromTo == Cons.gitDiffFileHistoryFromTreeToLocal || fromTo == Cons.gitDiffFileHistoryFromTreeToPrev
    val size = diffableItemList.size
    val previousIndex = curItemIndex.intValue - 1
    val nextIndex = curItemIndex.intValue + 1
    val hasPrevious = previousIndex >= 0 && previousIndex < size
    // diff to prev require next next item compare to next item, so need check next index plus 1 is good index or not
    val hasNext = if(fromTo != Cons.gitDiffFileHistoryFromTreeToPrev) isGoodIndex(nextIndex, size) else isGoodIndex(nextIndex + 1, size);

    val noneText = Pair(stringResource(R.string.none), "")

    fun getItemTextByIdx(idx:Int):Pair<String, String> {
        return diffableItemList.getOrNull(idx)?.let {
            if(isFileHistoryTreeToLocalOrTree) {
                Pair(it.shortCommitId, it.oneLineCommitMsgOfCommitOid())
            } else {
                Pair(it.fileName, it.fileParentPathOfRelativePath)
            }
        } ?: noneText
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // show restore for file history
        if(isFileHistoryTreeToLocalOrTree) {
            SingleLineCardButton(
                text = stringResource(R.string.restore),
                enabled = true
            ) {
                pageRequest.value = PageRequest.showRestoreDialog
            }

            Spacer(Modifier.height(20.dp))
        }


//        if(size > 0 && fromTo != Cons.gitDiffFileHistoryFromTreeToTree) {
        if(size > 0) {
            val getCurItem = {
                val targetItem = diffableItemList.getOrNull(curItemIndex.intValue)

                if(targetItem == null) {
                    Msg.requireShowLongDuration("err: bad index ${curItemIndex.intValue}")
                }

                targetItem
            }

            //执行操作，然后如果存在下个条目或上个条目，跳转；（??否则返回上级页面??（貌似实现multi mode后，不在这里决定是否跳转回上级页面了，而是在执行的操作里判断，所以这里只剩切换条目的逻辑了））
            val doActThenSwitchItem:suspend (targetIndex:Int, targetItem: DiffableItem?, act:suspend ()->Unit)->Unit = { targetIndex, targetItem, act ->
                act()

                // because `act` was comsumed current item and removed it form the list,
                //   so next index need -1 at here, if got valid index, means still has next,
                //   then switch to it, else switch to previous
                val nextOrPreviousIndex = if(hasNext) (nextIndex - 1) else previousIndex
                if(nextOrPreviousIndex >= 0 && nextOrPreviousIndex < diffableItemList.size) {  // still has next or previous, switch to it
                    //切换条目
                    val nextOrPrevious = diffableItemList[nextOrPreviousIndex]
                    lastClickedItemKey.value = nextOrPrevious.getItemKey()
                    switchItem(targetItem, nextOrPrevious, nextOrPreviousIndex, hasNext)
                }
            }

            val targetChangeListItemState = mutableCustomStateOf(stateKeyTag, "targetChangeListItemState") { StatusTypeEntrySaver() }
            val targetIndexState = rememberSaveable { mutableIntStateOf(-1) }


            //各种按钮

            // if is index to work tree, show stage button
            if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                SingleLineCardButton(
                    text = stringResource(R.string.stage),
                    enabled = true
                ) onClick@{
                    val targetIndex = curItemIndex.intValue
                    val targetItem = getCurItem() ?: return@onClick

                    doJobThenOffLoading {
                        if(isMultiMode) {
                            stageItem(diffableItemList.map { it.toChangeListItem() })
                        }else {
                            doActThenSwitchItem(targetIndex, targetItem) {
                                stageItem(listOf(targetItem.toChangeListItem()))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // show revert for worktreeToIndex
                SingleLineCardButton(
                    text = stringResource(R.string.revert),
                    enabled = true
                ) onClick@{
                    val targetIndex = curItemIndex.intValue
                    val targetItem = getCurItem() ?: return@onClick

                    targetChangeListItemState.value = targetItem.toChangeListItem()
                    targetIndexState.intValue = targetIndex

                    if(isMultiMode) {
                        initRevertDialog(diffableItemList.map { it.toChangeListItem() }) {}
                    }else {
                        initRevertDialog(listOf(targetChangeListItemState.value)) {
                            // callback
                            doActThenSwitchItem(targetIndexState.intValue, targetItem) {}
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }else if(fromTo == Cons.gitDiffFromHeadToIndex) {  // show unstage for indexToHead
                SingleLineCardButton(
                    text = stringResource(R.string.unstage),
                    enabled = true
                ) onClick@{
                    val targetIndex = curItemIndex.intValue
                    val targetItem = getCurItem() ?: return@onClick

                    targetChangeListItemState.value = targetItem.toChangeListItem()
                    targetIndexState.intValue = targetIndex

                    if(isMultiMode) {
                        initUnstageDialog(diffableItemList.map { it.toChangeListItem() }) {}
                    }else {
                        initUnstageDialog(listOf(targetChangeListItemState.value)) {
                            doActThenSwitchItem(targetIndexState.intValue, targetItem) {}
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }


            // show commit all for indexToWorktree or headToIndex (虽然显示同一按钮，但两者实现不同，indexToWorktree的commit all是先stage all再提交，headToIndex的commit all则是直接提交index，不会stage未添加到index但存在于indexToWorktree ChangeList页面的条目)
            if(fromTo == Cons.gitDiffFromIndexToWorktree || fromTo == Cons.gitDiffFromHeadToIndex) {
                val (state, requestType) = if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                    Pair(SharedState.homeChangeList_Refresh, StateRequestType.indexToWorkTree_CommitAll)
                } else {
                    Pair(SharedState.indexChangeList_Refresh, StateRequestType.headToIndex_CommitAll)
                }


                SingleLineCardButton(
                    text = stringResource(R.string.commit),
                    enabled = true
                ) {
                    changeStateTriggerRefreshPage(state, requestType)
                    naviUp()
                }

                Spacer(Modifier.height(20.dp))
            }


            Spacer(Modifier.height(20.dp))


            // 条目数和切换条目按钮

            if(isMultiMode.not()) {

                //当前是第几个条目，总共几个条目，这玩意必须和上一下一按钮在同一代码块并一起显示，要不然怪怪的
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        replaceStringResList(stringResource(R.string.current_n_all_m), listOf("" + (curItemIndex.intValue+1), "" + size)),
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = UIHelper.getSecondaryFontColor()
                    )
                }

                //切换上个下个条目按钮
                // button for switch to prev item
                TwoLineTextCardButton(
                    enabled = hasPrevious,
                    textPair = if(hasPrevious) getItemTextByIdx(previousIndex) else noneText,
                    headIcon = Icons.Filled.KeyboardArrowUp,
                    headIconWidth = headIconWidth,
                    headIconDesc = "Previous",
                ) {
                    val item = diffableItemList[previousIndex]
                    lastClickedItemKey.value = item.getItemKey()
                    switchItem(getCurItem(), item, previousIndex, false)
                }

                Spacer(Modifier.height(10.dp))

                // button for show current item info, but cant click
                TwoLineTextCardButton(
                    enabled = true,
                    textPair = getItemTextByIdx(curItemIndex.intValue),
                    headIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    headIconWidth = headIconWidth,
                    headIconDesc = "Current",
                ) {
                    goToTop()
                }

                Spacer(Modifier.height(10.dp))

                // button for switch to next item
                TwoLineTextCardButton(
                    enabled = hasNext,
                    textPair = if(hasNext) getItemTextByIdx(nextIndex) else noneText,
                    headIcon = Icons.Filled.KeyboardArrowDown,
                    headIconWidth = headIconWidth,
                    headIconDesc = "Next",
                ) {
                    val item = diffableItemList[nextIndex]
                    lastClickedItemKey.value = item.getItemKey()
                    switchItem(getCurItem(), item, nextIndex, true)
                }
            }
        }


//        Spacer(Modifier.height(150.dp))

    }
}

@Composable
private fun HunkDivider() {
    MyHorizontalDivider(
        modifier = Modifier.padding(vertical = 30.dp),
        thickness = 3.dp
    )
}
