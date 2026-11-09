package com.catpuppyapp.puppygit.fileeditor.texteditor.view

import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.AcceptButtons
import com.catpuppyapp.puppygit.compose.ClickableText
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.DefaultPaddingRow
import com.catpuppyapp.puppygit.compose.DisableSoftKeyboard
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.LineNum
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dev.bug_Editor_GoToColumnCantHideKeyboard_Fixed
import com.catpuppyapp.puppygit.dev.bug_Editor_SelectColumnRangeOfLine_Fixed
import com.catpuppyapp.puppygit.dto.UndoStack
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.FindDirection
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.LineChangeType
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.MyTextFieldState
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.SelectionOption
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.FileEditedPos
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.PatchUtil
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.fileopenhistory.FileOpenHistoryMan
import com.catpuppyapp.puppygit.utils.isGoodIndexForStr
import com.catpuppyapp.puppygit.utils.parseLineAndColumn
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

private const val TAG = "TextEditor"

//line offset when click line number to go to editor, for make text not very top when jump to editor,
// if line at the top of screen, looks terrible
//跳转到editor时的行偏移量，尽量让目标行在不要太顶端的位置，不然看着不舒服，如果可以的话，
// 在editor显示时的位置最好尽量和用户点行号的位置匹配，不过我没想到怎么实现，可能得记录点击位置，算偏移量，有点麻烦
internal const val lineNumOffsetForGoToEditor = Cons.scrollToItemOffset


@Parcelize
class ExpectConflictStrDto(
    var conflictStartStr: String = "",
    var conflictSplitStr: String = "",
    var conflictEndStr: String = "",

    var curConflictStr: String = conflictStartStr,
    var curConflictStrMatched: Boolean = false,
):Parcelable {
    fun reset() {
        curConflictStr = conflictStartStr
        curConflictStrMatched = false
    }

    fun getNextExpectConflictStr():String{
        return if(curConflictStr == conflictStartStr) {
            conflictSplitStr
        }else if(curConflictStr == conflictSplitStr) {
            conflictEndStr
        }else { // curStr == settings.editor.conflictEndStr
            conflictStartStr
        }
    }

    fun getCurAndNextExpect():Pair<Int,Int> {
        val curExpect = if(curConflictStr.startsWith(conflictStartStr)){
            0
        }else if(curConflictStr.startsWith(conflictSplitStr)) {
            1
        }else {
            2
        }

        val nextExcept = if(curExpect + 1 > 2) 0 else curExpect+1

        return Pair(curExpect, nextExcept)
    }
}

typealias DecorationBoxComposable = @Composable (
    index: Int,
    size: Int,  // list.size
    isSelected: Boolean,
    currentField: MyTextFieldState,
    focusingIdx:Int,
    isMultiSelectionMode: Boolean,
    innerTextField: @Composable (modifier: Modifier) -> Unit
) -> Unit

//选中文本时使用这个，自定义光标handle和选中文本颜色
//private val customTextSelectionColors = MyStyleKt.TextSelectionColor.customTextSelectionColors
//private val customTextSelectionColors_darkMode = MyStyleKt.TextSelectionColor.customTextSelectionColors_darkMode

//没选中文本时使用这个，隐藏光标
//private val customTextSelectionColors_hideCursorHandle = MyStyleKt.TextSelectionColor.customTextSelectionColors_cursorHandleInvisible

// show cursor handle
//private val customTextSelectionColors_cursorHandleVisible = MyStyleKt.TextSelectionColor.customTextSelectionColors_cursorHandleVisible




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextEditor(
    stateKeyTag:String,

    disableSoftKb: MutableState<Boolean>,
    updateLastCursorAtColumn:(Int)->Unit,
    getLastCursorAtColumnValue:()->Int,


    ignoreFocusOnce: MutableState<Boolean>,
    undoStack:UndoStack,
    curPreviewScrollState: ScrollState,
    requireEditorScrollToPreviewCurPos:MutableState<Boolean>,
    requestFromParent:MutableState<String>,
    fileFullPath:FilePath,
    lastEditedPos: FileEditedPos,
    textEditorState: TextEditorState,
//    onChanged:(newState:TextEditorState, trueSaveToUndoFalseRedoNullNoSave:Boolean?, clearRedoStack:Boolean)->Unit,
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues = PaddingValues(),
    lastScrollEvent: CustomStateSaveable<ScrollEvent?>,
    listState: LazyListState,
    editorPageIsInitDone:MutableState<Boolean>,
    goToLine:Int,  // is line number, not line index
    readOnlyMode:Boolean,
    searchMode:MutableState<Boolean>,
    mergeMode:Boolean,
    patchMode:Boolean,
    searchKeyword:String,
    fontSize: MutableIntState,
    fontColor: Color,
    scrollIfIndexInvisible: (index: Int) -> Unit,
    decorationBox: DecorationBoxComposable = { _, _, _, _, _,_, innerTextField -> innerTextField(Modifier) },
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val density = LocalDensity.current
    val deviceConfiguration = AppModel.getCurActivityConfig()
    val clipboardManager = LocalClipboardManager.current

    val conflictOursBlockBgColor = MyStyleKt.ConflictBlock.getConflictOursBlockBgColor()
    val conflictTheirsBlockBgColor = MyStyleKt.ConflictBlock.getConflictTheirsBlockBgColor()
    val conflictStartLineBgColor = MyStyleKt.ConflictBlock.getConflictStartLineBgColor()
    val conflictSplitLineBgColor = MyStyleKt.ConflictBlock.getConflictSplitLineBgColor()
    val conflictEndLineBgColor = MyStyleKt.ConflictBlock.getConflictEndLineBgColor()

    val acceptOursBtnColor = MyStyleKt.ConflictBlock.getAcceptOursIconColor()
    val acceptTheirsBtnColor = MyStyleKt.ConflictBlock.getAcceptTheirsIconColor()
    val acceptBothBtnColor = MyStyleKt.ConflictBlock.getAcceptBothIconColor()
    val rejectBothBtnColor = MyStyleKt.ConflictBlock.getRejectBothIconColor()

    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val inDarkTheme = Theme.inDarkTheme
//    val textEditorState by rememberUpdatedState(newValue = textEditorState)
    val (virtualWidth, virtualHeight) = UIHelper.Size.editorVirtualSpace()

//    val focusRequesters = remember { mutableStateMapOf<Int, FocusRequester>() }
//    val focusRequesters = remember { mutableStateListOf<FocusRequester>() }  //不能用list，滚动两下页面就会报错

    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val conflictKeyword = remember(settings.editor.conflictStartStr) { mutableStateOf(settings.editor.conflictStartStr) }

    val focusingLineIdx = rememberUpdatedState(textEditorState.focusingLineIdx)


    //最后显示屏幕范围的第一行的索引
//    var lastFirstVisibleLineIndexState  by remember { mutableIntStateOf(lastEditedPos.firstVisibleLineIndex) }

    val showGoToLineDialog  = rememberSaveable { mutableStateOf(false) }
    val goToLineValue  = mutableCustomStateOf(stateKeyTag, "goToLineValue") { TextFieldValue("") }
    val focusWhenGoToLine = rememberSaveable { mutableStateOf(false) }
    val lastVisibleLineState  = rememberSaveable { mutableStateOf(0) }

    //是否显示光标拖手(cursor handle
//    val needShowCursorHandle = rememberSaveable { mutableStateOf(false) }

    //这俩值会在组件销毁时写入配置文件以记录滚动位置(当前画面第一个可见行)和最后编辑位置
//    val lastEditedLineIndexState  = rememberSaveable { mutableIntStateOf(lastEditedPos.lineIndex) }
//    val lastEditedColumnIndexState = rememberSaveable { mutableIntStateOf(lastEditedPos.columnIndex) }


    val expectConflictStrDto = rememberSaveable(settings.editor.conflictStartStr, settings.editor.conflictSplitStr, settings.editor.conflictEndStr) {
        mutableStateOf(
            ExpectConflictStrDto(
                conflictStartStr = settings.editor.conflictStartStr,
                conflictSplitStr = settings.editor.conflictSplitStr,
                conflictEndStr = settings.editor.conflictEndStr
            )
        )
    }


    fun getFocusedLineIdxOrFirstVisibleLine() = focusingLineIdx.value ?: listState.firstVisibleItemIndex;

    // search states
    // must call `initSearchPos()` before use this value to do search
//    val nextSearchPos = rememberSaveable { mutableStateOf(SearchPos.NotFound) }
//
//    val lastFoundPos = rememberSaveable { mutableStateOf(SearchPos.NotFound) }
//    val lastFindDirectionIsToNext = rememberSaveable { mutableStateOf<Boolean?>(null) }
//    val lastSearchKeyword = rememberSaveable { mutableStateOf("") }

    // if focused, search from focused line and column; else search from first visible line
    fun resolveSearchStartPos(toNext:Boolean): SearchPos {
        //把起始搜索位置设置为当前第一个可见行的第一列
//        lastSearchPos.value = SearchPos(lazyColumnState.firstVisibleItemIndex, 0)

        // make sure position right before search
        var newLineIndex = getFocusedLineIdxOrFirstVisibleLine()
        var newColumnIndex = 0

        val (curIndex, curField) = textEditorState.getFieldByIdx(newLineIndex)

        if(curIndex != null && curField != null) {
            newLineIndex = curIndex
            newColumnIndex = curField.value.selection.let { if(toNext) it.max else it.min - 1 }

            if(!isGoodIndexForStr(newColumnIndex, curField.value.text)) {
                // invalid column index, will reset when `TextEditorState.doSearch()`
                newColumnIndex = -1
                // switch line, no need check is valid or not,
                //   it will reset in `TextEditorState.doSearch()` if it is invalid
                if(toNext) newLineIndex++ else newLineIndex--
            }
        }

        return SearchPos(newLineIndex, newColumnIndex)
    }

    fun jumpToLineIndex(
        lineIndex:Int,
        goColumn: Boolean=false,
        columnStartIndex:Int=0,
        columnEndIndexExclusive:Int=columnStartIndex,
        requireHideKeyboard:Boolean = false
    ){
        lastScrollEvent.value = ScrollEvent(
            index = lineIndex,
            forceGo = true,
            goColumn = goColumn,
            columnStartIndexInclusive = columnStartIndex,
            columnEndIndexExclusive = columnEndIndexExclusive,
            requireHideKeyboard = requireHideKeyboard
        )
    }

    suspend fun doSearchNoCatch(keyword:String, toNext:Boolean) {
        // editor fields should never empty, it always at least have an empty line
        if(keyword.isEmpty() || textEditorState.fields.isEmpty()) {
            return
        }

        val funName = "doSearchNoCatch"


        val startPos = resolveSearchStartPos(toNext)

        if(AppModel.devModeOn) {
            MyLog.w(TAG, "$TAG#$funName(): will use: toNext=$toNext, startPos=$startPos")
        }

        val keyWordLen = keyword.length

        val posResult = textEditorState.doSearch(keyword.lowercase(), toNext = toNext, startPos = startPos)
        val foundPos = posResult.foundPos
        if(foundPos == SearchPos.NotFound) {
            if(!searchMode.value && mergeMode) {
                Msg.requireShow(activityContext.getString(R.string.no_conflict_found))
            }else {
                Msg.requireShow(activityContext.getString(R.string.not_found))
            }
        }else {  //查找到了关键字

//            println("found:$foundPos")//test1791022120240812

            //显示选中文本背景颜色
//            needShowCursorHandle.value = true

            val keywordStartAtLine = foundPos.columnIndex
            val keywordEndExclusiveAtLine = foundPos.columnIndex + keyWordLen
            //跳转到对应行并选中关键字
            jumpToLineIndex(
                lineIndex = foundPos.lineIndex,
                goColumn = true,


                //注：让开头和结束索引调换是为了让输入光标在选中内容末尾而不是开头
                //注：让开头和结束索引调换是为了让输入光标在选中内容末尾而不是开头
                //注：让开头和结束索引调换是为了让输入光标在选中内容末尾而不是开头

                //有开头和结尾索引，原本是想做区域选中的，就像手动长按选中某段文本一样，但忘了是什么问题了，反正选中不了，所以最后仅定位某列，无区域选中了
                //若想定位到关键字开头，用上面的，定位到末尾用下面的
//                columnStartIndex = keywordStartAtLine, // to keyword start
                columnStartIndex = keywordEndExclusiveAtLine, // to keyword end

                //这个与定位到关键字开头还是末尾无关，不用动，因为有bug，所以就算这个和开头索引不同，也不会选中区域，实际还是会定位到开头那
//                columnEndIndexExclusive = keywordEndExclusiveAtLine,
                columnEndIndexExclusive = keywordStartAtLine,


                //请求关掉键盘，不然高亮会被textFieldValue的onValueChange事件刷新掉，不过就算关了键盘，当前正在编辑的行也无法取消聚焦，还是有可能会被刷新掉高亮颜色
//                requireHideKeyboard = true
                //无所谓，高亮颜色bug触发就触发吧，关了键盘，光标不在关键字后面，不方便修改，若不想编辑只想看关键字附近的内容，可开只读模式，那样就可避免弹出键盘，也可避免输入发触发TextField的onValueChange导致高亮闪一下就消失的bug
                requireHideKeyboard = false,  // requireLossFocus 相关
            )

        }

        if(AppModel.devModeOn) {
            MyLog.w(TAG, "$TAG#$funName(): found result: toNext=$toNext, foundPos=$foundPos")
        }

    }


    suspend fun doSearch(keyword:String, toNext:Boolean) {
        try {
            doSearchNoCatch(
                keyword = keyword,
                toNext = toNext,
            )
        }catch (e: Exception) {
            MyLog.e(TAG, "text editor `doSearch()` err: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }




    //每次计算依赖的状态变化就会重新计算这个变量的值
//    val firstLineIndexState = remember { derivedStateOf {
//        val visibleItems= lazyColumnState.layoutInfo.visibleItemsInfo
//        if(visibleItems.isEmpty()) {
//            0
//        }else {
//            //按索引排序，取出索引最小的元素的索引
//            visibleItems.minBy { it.index }.index
//        }
//    } }

    //执行一次LaunchedEffected后，此值才应为真，放到LaunchedEffect末尾修改为true即可。（目前 20240419 用来判断是否需要保存可见的第一行索引，刚打开文件要么默认0，要么恢复上次滚动位置，都不需要保存，只有打开文件后（初始化后）再滚动才需要保存）
    val isInitDone = editorPageIsInitDone

//    editableController.syncState(textEditorState)

//    val clipboardManager = LocalClipboardManager.current

    //上级页面发来的request，请求执行某些操作
    if(requestFromParent.value==PageRequest.hideKeyboardForAWhile) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            // if disable, will hide soft kb, so no need to hide it
//            if(disableSoftKb.value.not()) {
//                  // it may cause app crash by null input command
//                keyboardController?.hideForAWhile()
//            }

            // not work as expected, if timeout, will show soft keyboard immediately but expect show soft keyboard after user tap screen
//            doJobThenOffLoading(
//                loadingOn = { disableSoftKb.value = true },
//                loadingOff = {
//                    disableSoftKb.value = false
//                },
//            ) {
//                delay(300)
//            }
        }
    }

    if(requestFromParent.value==PageRequest.editorQuitSelectionMode) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#quitSelectionMode") { textEditorState ->

                textEditorState.quitSelectionMode()
            }
        }
    }
    if(requestFromParent.value==PageRequest.requestUndo) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                textEditorState.codeEditor?.doActWithLatestEditorState("#undo") { textEditorState ->
                    textEditorState.undo(undoStack = undoStack)
                }
            }
        }
    }
    if(requestFromParent.value==PageRequest.requestRedo) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                textEditorState.codeEditor?.doActWithLatestEditorState("#redo") { textEditorState ->
                    textEditorState.redo(undoStack=undoStack)
                }
            }
        }
    }
    if(requestFromParent.value==PageRequest.goToTop) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            lastScrollEvent.value = ScrollEvent(0, forceGo = true)
        }
    }
    if(requestFromParent.value==PageRequest.goToLine) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            focusWhenGoToLine.value = true
            goToLineValue.value = goToLineValue.value.let { it.copy(selection = TextRange(0, it.text.length)) }
            showGoToLineDialog.value = true
        }
    }
    if(requestFromParent.value==PageRequest.requireSearch) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            //初始化搜索位置
//            initSearchPos()
            //开启搜索模式
            searchMode.value = true
        }
    }
    if(requestFromParent.value==PageRequest.findPrevious) {
        PageRequest.clearStateThenDoAct(requestFromParent) findPrevious@{
            if(searchKeyword.isEmpty()) {
                return@findPrevious
            }

            doJobThenOffLoading {
                doSearch(searchKeyword, toNext = false)
            }
        }
    }
    if(requestFromParent.value==PageRequest.findNext) {
        PageRequest.clearStateThenDoAct(requestFromParent) findNext@{
            if(searchKeyword.isEmpty()) {
                return@findNext
            }

            doJobThenOffLoading {
                doSearch(searchKeyword, toNext = true)
            }
        }
    }
    //显示总共有多少关键字（关键字计数）
    if(requestFromParent.value==PageRequest.showFindNextAndAllCount) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val allCount = textEditorState.getKeywordCount(searchKeyword)
                Msg.requireShow(replaceStringResList(activityContext.getString(R.string.find_next_all_count), listOf(allCount.toString())))
            }
        }
    }


    if(requestFromParent.value==PageRequest.previousConflict) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val currentLineIdx = getFocusedLineIdxOrFirstVisibleLine()

                val nextSearchLine = textEditorState.fields.get(currentLineIdx).value.text
                if(nextSearchLine.startsWith(settings.editor.conflictStartStr)) {
                    conflictKeyword.value = settings.editor.conflictStartStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictSplitStr)) {
                    conflictKeyword.value = settings.editor.conflictSplitStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictEndStr)) {
                    conflictKeyword.value = settings.editor.conflictEndStr
                }

                val previousKeyWord = getPreviousKeyWordForConflict(conflictKeyword.value, settings)
                conflictKeyword.value = previousKeyWord
                doSearch(previousKeyWord, toNext = false)
            }
        }
    }
    if(requestFromParent.value==PageRequest.nextConflict) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
                val currentLineIdx = getFocusedLineIdxOrFirstVisibleLine()

                // update cur conflict keyword, if cursor on conflict str line, if dont do this, UX bad, e.g. I clicked conflict splict line, then click prev conflict, expect is go conflict start line, but if last search is start line, this time will go to end line, anti-intuition
                // 如果光标在冲突开始、分割、结束行之一，更新搜索关键字，如果不这样做，会出现一些反直觉的bug：我点击了conflict split line，然后点上，期望是查找conflict start line，但如果上次搜索状态是start line，那这次就会去搜索end line，反直觉
                val nextSearchLine = textEditorState.fields.get(currentLineIdx).value.text
                if(nextSearchLine.startsWith(settings.editor.conflictStartStr)) {
                    conflictKeyword.value = settings.editor.conflictStartStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictSplitStr)) {
                    conflictKeyword.value = settings.editor.conflictSplitStr
                }else if(nextSearchLine.startsWith(settings.editor.conflictEndStr)) {
                    conflictKeyword.value = settings.editor.conflictEndStr
                }

                val nextKeyWord = getNextKeyWordForConflict(conflictKeyword.value, settings)
                conflictKeyword.value = nextKeyWord
                doSearch(nextKeyWord, toNext = true)
            }
        }
    }

    if(requestFromParent.value==PageRequest.showNextConflictAndAllConflictsCount) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            doJobThenOffLoading {
//                val allCount = editableController.getKeywordCount(conflictKeyword.value)  // this keyword is dynamic change when press next or previous, used for count conflict is ok though, but use conflict start str count can be better
                val allCount = textEditorState.getKeywordCount(settings.editor.conflictStartStr)
                Msg.requireShow(replaceStringResList(activityContext.getString(R.string.next_conflict_all_count), listOf(allCount.toString())))
            }
        }
    }
    //20240507: 这个其实已经没用了，改用在第一行和最后编辑位置切换了，所以先注释了
//    if(requestFromParent.value==PageRequest.backLastEditedLine) {
//        PageRequest.clearStateThenDoAct(requestFromParent) {
//            val settings = SettingsUtil.getSettingsSnapshot()
//            val lastPos = settings.editor.filesLastEditPosition[fileFullPath]
////            if(debugModeOn) {
////                println("back to lastLineIndex: "+lastPos?.lineIndex)
////            }
//            lastScrollEvent = ScrollEvent(lastPos?.lineIndex?:0, forceGo = true)
////            lastScrollEvent = ScrollEvent(23, forceGo = true)  //测试跳转是否好使，结果：passed
//        }
//    }

    if(requestFromParent.value==PageRequest.switchBetweenFirstLineAndLastEditLine) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val notAtTop = listState.firstVisibleItemIndex != 0
            // if 不在顶部，go to 顶部 else go to 上次编辑位置(当前聚焦行)
            val position = if(notAtTop) 0 else getFocusedLineIdxOrFirstVisibleLine()
            lastScrollEvent.value = ScrollEvent(position, forceGo = true)
        }
    }

    if(requestFromParent.value==PageRequest.switchBetweenTopAndLastPosition) {
        PageRequest.clearStateThenDoAct(requestFromParent) {
            val lastVisibleLine = listState.firstVisibleItemIndex
            val notAtTop = lastVisibleLine != 0
            // if 不在顶部，go to 顶部 else go to 上次编辑位置
            val position = if(notAtTop) {
                lastVisibleLineState.value = lastVisibleLine
                0
            } else {
                lastVisibleLineState.value
            }

            lastScrollEvent.value = ScrollEvent(position, forceGo = true)
        }
    }



    /**
     * 入参是行号，不是索引，最小是1
     * param is line number, not index, min is 1
     */
    fun doGoToLine(line:String, focus:Boolean = true) {
        //x 会报错，提示index必须为非负数) 测试下如果是-1会怎样？是否会报错？
//        val lineIntVal = -1
        val linNumParseResult = parseLineAndColumn(line)
        val targetLineIdx = linNumParseResult.lineNumToIndex(
            curLineIndex = focusingLineIdx.value ?: listState.firstVisibleItemIndex,
            maxLineIndex = textEditorState.fields.size
        )

        if(focus) {
            //行号减1即要定位行的索引
            lastScrollEvent.value = ScrollEvent(
                index = targetLineIdx,
                forceGo = true,
                goColumn = true,
                columnStartIndexInclusive = linNumParseResult.columnNumToIndex(),
            )
        }else {  // only scroll, don't focus target line
            UIHelper.scrollToItem(scope, listState, targetLineIdx + lineNumOffsetForGoToEditor)
        }
    }

    if(showGoToLineDialog.value) {
        val onOK = {
            showGoToLineDialog.value = false
            doGoToLine(goToLineValue.value.text, focusWhenGoToLine.value)
        }

        val focusRequester = remember { FocusRequester() }

        val firstLine = "1"
        val lastLine = ""+textEditorState.fields.size
        val lineNumRange = "$firstLine-$lastLine"
        ConfirmDialog(
            title = stringResource(R.string.go_to_line),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,
                ) {

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding)
                            .focusRequester(focusRequester)
                            .onPreviewKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown) {
                                    false
                                } else if (event.key == Key.Enter) {
                                    onOK()
                                    true
                                } else {
                                    false
                                }
                            }
                        ,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            onOK()
                        }),
                        singleLine = true,

                        value = goToLineValue.value,
                        onValueChange = {
                            goToLineValue.value=it
                        },
                        label = {
                            Text(stringResource(R.string.line_number)+"($lineNumRange)")
                        },
                        placeholder = {
                            //显示行号范围，例如："Range: 1-123"
                            Text("e.g. 1:5")
                        }
                    )

                    DefaultPaddingRow {
                        MyCheckBox(stringResource(R.string.focus_line), focusWhenGoToLine)
                    }

                    Column(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, end = 10.dp)
                        ,
                        horizontalAlignment = Alignment.End
                    ) {

                        ClickableText(
                            text = stringResource(R.string.first_line),
                            modifier = MyStyleKt.ClickableText.modifier.clickable {
                                // selection to length of text field for make user append ':column' more convenient
                                goToLineValue.value = firstLine.let { TextFieldValue(it, selection = TextRange(it.length)) }
                            },
                            fontWeight = FontWeight.Light
                        )

                        Spacer(Modifier.height(15.dp))

                        ClickableText(
                            text = stringResource(R.string.last_line),
                            modifier = MyStyleKt.ClickableText.modifier.clickable {
                                goToLineValue.value = lastLine.let { TextFieldValue(it, selection = TextRange(it.length)) }
                            },
                            fontWeight = FontWeight.Light
                        )

                        Spacer(Modifier.height(10.dp))

                    }
                }


            },
            okBtnEnabled = goToLineValue.value.text.isNotBlank(),
            okBtnText = stringResource(id = R.string.go),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showGoToLineDialog.value = false }
        ) {
            onOK()
        }

        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }

//    val scrollTo = { lineIndex:Int ->
//        scope.launch {
//            lazyColumnState.scrollToItem(Math.max(0, lineIndex))
//        }
//        Unit
//    }


    // actually only need know which lines will delete, no need know which will kept
//    val keepStartIndex = remember { mutableStateOf(-1) }
//    val keepEndIndex = remember { mutableStateOf(-1) }

    val delStartIndex = rememberSaveable { mutableIntStateOf(-1) }
    val delEndIndex = rememberSaveable { mutableIntStateOf(-1) }
    val startConflictLineIndexState = rememberSaveable { mutableIntStateOf(-1) }
    val splitConflictLineIndexState = rememberSaveable { mutableIntStateOf(-1) }
    val endConflictLineIndexState = rememberSaveable { mutableIntStateOf(-1) }
    val delSingleIndex = rememberSaveable { mutableIntStateOf(-1) }
    val acceptOursState = rememberSaveable { mutableStateOf(false) }
    val acceptTheirsState = rememberSaveable { mutableStateOf(false) }
    val showAcceptConfirmDialog = rememberSaveable { mutableStateOf(false) }

    /**
     * find accept block indices,
     * find direction:
     * if curLineText starts with conflict start str: conflict start -> split -> end (all go down)
     * if starts with split str: split -> start -> end (go down, then go up)
     * if starts with end str: end -> split -> start (all go up)
     */
    val prepareAcceptBlock= label@{acceptOurs: Boolean, acceptTheirs:Boolean, index: Int, curLineText: String ->
//        println("index=$index, curLine=$curLineText")

        val curStartsWithStart = curLineText.startsWith(settings.editor.conflictStartStr)
        val curStartsWithSplit = curLineText.startsWith(settings.editor.conflictSplitStr)
        val curStartsWithEnd = curLineText.startsWith(settings.editor.conflictEndStr)
        if(!(curStartsWithStart || curStartsWithSplit || curStartsWithEnd)) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_conflict_block))
            return@label
        }


        val firstFindDirection = if(curStartsWithStart) {
            FindDirection.DOWN
        }else {
            FindDirection.UP
        }

        val firstExpectStr = if(curStartsWithStart) {
            settings.editor.conflictSplitStr
        }else if(curStartsWithSplit) {
            settings.editor.conflictStartStr
        }else {  // conflict end str like ">x7 branchName"
            settings.editor.conflictSplitStr
        }


        val (firstIndex, _) = textEditorState.indexAndValueOf(startIndex=index, direction=firstFindDirection, predicate={it.startsWith(firstExpectStr)}, includeStartIndex = false)

        if(firstIndex == -1) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_conflict_block))
            return@label
        }

        val secondFindDirection = if(curStartsWithEnd) {
            FindDirection.UP
        }else {
            FindDirection.DOWN
        }

        val secondExpectStr = if(curStartsWithEnd) {
            settings.editor.conflictStartStr
        }else {
            settings.editor.conflictEndStr
        }

        val secondStartFindIndexAt =if(curStartsWithSplit) {
            index
        }else {
            firstIndex
        }
        val (secondIndex, _) = textEditorState.indexAndValueOf(startIndex=secondStartFindIndexAt, direction=secondFindDirection, predicate={it.startsWith(secondExpectStr)}, includeStartIndex = false)

        if(secondIndex == -1) {
            Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_conflict_block))
            return@label
        }

        val startConflictLineIndex = if(curStartsWithStart) index else if(curStartsWithSplit) firstIndex else secondIndex  //this is start conflict str index
        val splitConflictLineIndex = if(curStartsWithStart || curStartsWithEnd) firstIndex else index  // this is split conflict str index
        val endConflictLineIndex = if(curStartsWithStart || curStartsWithSplit) secondIndex else index  // this is end conflict str index

        startConflictLineIndexState.value = startConflictLineIndex
        splitConflictLineIndexState.value = splitConflictLineIndex
        endConflictLineIndexState.value = endConflictLineIndex

        // special case: start may larger than end index, e.g. will keep 30 to 20, but, only shown wrong, will not err when deleting
        // 特殊情况：start index可能大于end index，例如：30 到 20，不过只是显示有误，实际执行无误
        if(acceptOurs && acceptTheirs.not()) {
            acceptOursState.value = true
            acceptTheirsState.value = false

            // remove single index and range [start, end]
            delSingleIndex.value = startConflictLineIndex
            delStartIndex.value = splitConflictLineIndex
            delEndIndex.value = endConflictLineIndex

//            keepStartIndex.value = startConflictLineIndex + 1  // this is startIndex+1
//            keepEndIndex.value = splitConflictLineIndex - 1  // this is splitIndex-1
        }else if(acceptOurs.not() && acceptTheirs) {
            acceptOursState.value = false
            acceptTheirsState.value = true

            // remove single index and range [start, end]
            delSingleIndex.value = endConflictLineIndex
            delStartIndex.value = startConflictLineIndex
            delEndIndex.value = splitConflictLineIndex

//            keepStartIndex.value = splitConflictLineIndex + 1  //this is split index +1
//            keepEndIndex.value = endConflictLineIndex - 1  // this is end index -1
        }else if(acceptOurs && acceptTheirs) {  // accept both
            acceptOursState.value = true
            acceptTheirsState.value = true

            // remove 3 indices, no range
            delSingleIndex.value = startConflictLineIndex
            delStartIndex.value= splitConflictLineIndex
            delEndIndex.value = endConflictLineIndex
        }else { // reject both
            acceptOursState.value = false
            acceptTheirsState.value = false

            // remove range [start, end]
            delStartIndex.value = startConflictLineIndex
            delEndIndex.value = endConflictLineIndex
        }

        // show dialog, make sure user confirm
        showAcceptConfirmDialog.value = true
    }

    if(showAcceptConfirmDialog.value) {
        ConfirmDialog2(
            title = if(acceptOursState.value && acceptTheirsState.value.not()) stringResource(R.string.accept_ours)
            else if(acceptOursState.value.not() && acceptTheirsState.value) stringResource(R.string.accept_theirs)
            else if(acceptOursState.value && acceptTheirsState.value) stringResource(R.string.accept_both)
            else stringResource(R.string.reject_both),

            text = if(acceptOursState.value && acceptTheirsState.value.not()) replaceStringResList(stringResource(R.string.will_accept_ours_and_delete_lines_line_indexs), listOf(""+(delSingleIndex.value + 1), ""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1)))
            else if(acceptOursState.value.not() && acceptTheirsState.value) replaceStringResList(stringResource(R.string.will_accept_theirs_and_delete_lines_line_indexs), listOf(""+(delSingleIndex.value + 1), ""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1)))
            else if(acceptOursState.value && acceptTheirsState.value) replaceStringResList(stringResource(R.string.will_accept_both_and_delete_lines_line_indexs), listOf(""+(delSingleIndex.value + 1), ""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1)))
            else replaceStringResList(stringResource(R.string.will_reject_both_and_delete_lines_line_indexs), listOf(""+(delStartIndex.value + 1), ""+(delEndIndex.value + 1))),

            onCancel = {showAcceptConfirmDialog.value = false}
        ) {
            showAcceptConfirmDialog.value=false

            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#deleteLineByIndices") { textEditorState ->
                try {
                    var baseFields:List<MyTextFieldState>? = null

                    if(acceptOursState.value) {
                        baseFields = textEditorState.setChangeTypeToFields(
                            IntRange(start = startConflictLineIndexState.value, endInclusive = splitConflictLineIndexState.value).toList(),
                            LineChangeType.ACCEPT_OURS,
                            baseFields,
                            applyNewSate = false,
                        )

                    }

                    if(acceptTheirsState.value) {
                        baseFields = textEditorState.setChangeTypeToFields(
                            IntRange(start = splitConflictLineIndexState.value, endInclusive = endConflictLineIndexState.value).toList(),
                            LineChangeType.ACCEPT_THEIRS,
                            baseFields,
                            applyNewSate = false,
                        )
                    }


                    val indicesWillDel = if((acceptOursState.value && acceptTheirsState.value.not()) || (acceptOursState.value.not() && acceptTheirsState.value)){
                        //accept ours/theirs
                        val tmp = mutableListOf(delSingleIndex.value)
                        tmp.addAll(IntRange(start = delStartIndex.value, endInclusive = delEndIndex.value).toList())
                        tmp
                    }else if(acceptOursState.value && acceptTheirsState.value) {  // accept both
                        val tmp = mutableListOf(delSingleIndex.value)
                        tmp.add(delStartIndex.value)
                        tmp.add(delEndIndex.value)
                        tmp
                    }else {  // reject both
                        IntRange(start = delStartIndex.value, endInclusive = delEndIndex.value).toList()
                    }

                    textEditorState.deleteLineByIndices(indicesWillDel, baseFields)

                }catch (e: Exception) {
                    val errPrefix = if(acceptOursState.value && acceptTheirsState.value) "Accept Both err"
                    else if(acceptOursState.value.not() && acceptTheirsState.value) "Accept Theirs err"
                    else if(acceptOursState.value && acceptTheirsState.value.not()) "Accept Ours err"
                    else "Reject Both err";

                    Msg.requireShowLongDuration("$errPrefix: ${e.localizedMessage}")
                    MyLog.e(TAG, "TextEditor#AcceptConfirmDialog: $errPrefix: ${e.stackTraceToString()}")
                }
            }
        }
    }


    if(requireEditorScrollToPreviewCurPos.value) {
        //一次性变量，立刻重置
        requireEditorScrollToPreviewCurPos.value = false

        try {
            val fontSizeInPx = UIHelper.spToPx(sp = fontSize.intValue, density = density)
            val screenWidthInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenWidthDp, density = density)
            val screenHeightInPx = UIHelper.dpToPx(dp = deviceConfiguration.screenHeightDp, density = density)
            val previewCurAt = curPreviewScrollState.value

            //计算目标滚动位置
            val targetLineIndex = textEditorState.pxToLineIdx(targetPx=previewCurAt, fontSizeInPx=fontSizeInPx, screenWidthInPx=screenWidthInPx, screenHeightInPx=screenHeightInPx)

            //滚动
            doGoToLine((targetLineIndex + 1).toString()) //index + 1变成行号
        }catch (e:Exception) {
            //并非很严重的错误，debug级别吧
            MyLog.d(TAG, "let editor scroll to current preview position failed: ${e.stackTraceToString()}")
        }
    }

    val lastValidFocusingLineIdx = rememberSaveable { mutableIntStateOf(lastEditedPos.lineIndex) }
    val lastValidEditedColumnLineIndex = rememberSaveable { mutableIntStateOf(lastEditedPos.columnIndex) }
    LaunchedEffect(focusingLineIdx.value) {
        val (lineIdx, field) = textEditorState.getCurrentField()

        if(lineIdx != null && field != null) {
            lastValidFocusingLineIdx.intValue = lineIdx
            if(textEditorState.isMultipleSelectionMode) {
                lastValidEditedColumnLineIndex.intValue = field.value.selection.end
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose TextEditorOnDispose@{
            try {

                //更新配置文件记录的滚动位置（当前屏幕可见第一行）和最后编辑行
                //如果当前索引不是上次定位的索引，更新页面状态变量和配置文件中记录的最后编辑行索引，不管是否需要滚动，反正先记上
                //先比较一下，Settings对象从内存取不用读文件，很快，如果没变化，就不用更新配置文件了，省了磁盘IO，所以有必要检查下
                val lastEditedLineIdx = lastValidFocusingLineIdx.intValue
                val oldLinePos = FileOpenHistoryMan.get(fileFullPath.ioPath)
                val needUpdateLastEditedLineIndex = oldLinePos?.lineIndex != lastEditedLineIdx
                val currentFirstVisibleIndex = listState.firstVisibleItemIndex
                val needUpdateFirstVisibleLineIndex = oldLinePos?.firstVisibleLineIndex != currentFirstVisibleIndex

                //记住最后编辑列
                val editedColumnIndex = lastValidEditedColumnLineIndex.intValue
//                println("lasteditcolumnindex:"+editedColumnIndex) //test2024081116726433
                val needUpdateLastEditedColumnIndex = oldLinePos?.columnIndex != editedColumnIndex

                if((needUpdateLastEditedLineIndex || needUpdateFirstVisibleLineIndex || needUpdateLastEditedColumnIndex)) {
//                    println("will save possssssssssssssssssssss")
//                    SettingsUtil.update {
                    val pos = oldLinePos
//                        if(pos==null) {
//                            pos = FileEditedPos()
//                        }
                    if(needUpdateLastEditedLineIndex) {
                        pos.lineIndex = lastEditedLineIdx
                    }
                    if(needUpdateFirstVisibleLineIndex) {
                        pos.firstVisibleLineIndex = currentFirstVisibleIndex
                    }
                    if(needUpdateLastEditedColumnIndex) {
                        pos.columnIndex = editedColumnIndex
                    }

                    // save
                    FileOpenHistoryMan.set(fileFullPath.ioPath, pos)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#TextEditorOnDispose@ save last edit position err: "+e.stackTraceToString())
            }
        }
    }


    // this can't immediately got keyboard hide or shown, so disabled
//    val softKbIsVisible = UIHelper.isSoftkeyboardVisible().let { remember(it) { derivedStateOf { it } } }

    CompositionLocalProvider(
//        LocalTextInputService provides (if(allowKeyboard.value && !readOnlyMode) LocalTextInputService.current else null),  //为null可阻止弹出键盘(compose 1.7.0之后已无效)
//        LocalTextSelectionColors provides (if(!needShowCursorHandle.value) customTextSelectionColors_NoSelections else if(inDarkTheme) customTextSelectionColors_darkMode else customTextSelectionColors),
        LocalTextSelectionColors provides MyStyleKt.TextSelectionColor.customTextSelectionColors_cursorHandleVisible,
    ) {

        expectConflictStrDto.value.reset()


        val size = textEditorState.fields.size
        val lastIndexOfFields = size - 1



        val createItem: LazyListScope.(Int)->Unit = ci@{ index:Int ->
            val textFieldState = textEditorState.fields.getOrNull(index) ?: return@ci

            val curLineText = textFieldState.value.text

            // patch开头的行（+ -）和merge开头的行（7个=号）并不冲突
            val patchColor = if(patchMode) PatchUtil.getColorOfLine(curLineText, inDarkTheme) else null;
            val bgColor = if(patchMode && patchColor != null) {
                patchColor
            } else if(mergeMode) {
                UIHelper.getBackgroundColorForMergeConflictSplitText(
                    text = curLineText,
                    settings = settings,
                    expectConflictStrDto = expectConflictStrDto.value,
                    oursBgColor = conflictOursBlockBgColor,
                    theirsBgColor = conflictTheirsBlockBgColor,
                    startLineBgColor= conflictStartLineBgColor,
                    splitLineBgColor= conflictSplitLineBgColor,
                    endLineBgColor= conflictEndLineBgColor
                )
            } else {
                Color.Unspecified
            }




            //很多时候改了内容，但没改id，懒得一个个改了，直接弃用id作key，让compose自己判断什么时候需要重组，有问题再说
//                    item(key = textFieldState.id) {
            item {
                if(mergeMode && curLineText.startsWith(settings.editor.conflictStartStr)) {
                    AcceptButtons(
                        lineIndex = index,
                        lineText = curLineText,
                        acceptOursColor = acceptOursBtnColor,
                        acceptTheirsColor = acceptTheirsBtnColor,
                        acceptBothColor = acceptBothBtnColor,
                        rejectBothColor = rejectBothBtnColor,
                        prepareAcceptBlock = prepareAcceptBlock,
                    )
                }

                decorationBox(
                    index,
                    size,
                    textEditorState.isFieldSelected(index),
                    textFieldState,

                    //用来判断是否选中当前行，若选中则加背景颜色，如果为null就当作-1，-1为无效索引，这样就不会选中任何行
                    focusingLineIdx.value ?: -1,
                    textEditorState.isMultipleSelectionMode,

                ) { modifier ->

                    val textFieldState = textEditorState.obtainHighlightedTextField(textFieldState).let { remember(it) { it } }

                    // FileEditor里的innerTextFiled()会执行这的代码
                    Box(
                        modifier = Modifier
                            .background(bgColor)
                            .combinedClickable(
                                //不显示点击效果（闪烁动画）
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,

                                onLongClick = clickable@{
                                    if (!textEditorState.isMultipleSelectionMode) return@clickable

                                    //(20250325新版compose似乎不会同时触发选择文本和当前行了，所以不会震两下了) 震动反馈，和长按选择文本的震动反馈冲突了，若开会振两下
//                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                    //执行区域选择
                                    textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectFieldSpan") { textEditorState ->
                                        textEditorState.selectFieldSpan(index)
                                    }
                                }
                            ) clickable@{
                                if (!textEditorState.isMultipleSelectionMode) return@clickable

                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                    textEditorState.selectField(targetIndex = index)
                                }
                            }

                            .then(modifier)
                    ) {
                        MyTextField(
                            scrollIfInvisible = {
                                scrollIfIndexInvisible(index)
                            },
                            readOnly = readOnlyMode,
                            //搜索模式已经没必要聚焦了，因为不需要光标定位行了，直接高亮关键字了，而且搜索模式会把focusingLineIdx设为null以避免聚焦行弹出键盘误判内容已改变从而触发重组导致高亮关键字功能失效
//                                    focusThisLine = if(textEditorState.isContentEdited.value) index == textEditorState.focusingLineIdx else false,
                            //仅当搜索模式，或者内容发生变化（比如换行）时光标才会自动聚焦，否则不聚焦，这样是为了避免切换页面再回来自动弹出键盘
//                                    focusThisLine = if(searchMode.value || textEditorState.isContentEdited.value) index == textEditorState.focusingLineIdx else false,
                            focusThisLine = index == focusingLineIdx.value,
                            //默认不自动聚焦任何行，不然一切换页面再回来弹出键盘，恶心
//                                    focusThisLine = false,

//                            needShowCursorHandle = needShowCursorHandle,
                            textFieldState = textFieldState,
                            enabled = !textEditorState.isMultipleSelectionMode,
                            fontSize = fontSize.intValue,
                            fontColor = fontColor,
                            onUpdateText = { newTextFieldValue ->
                                doJobThenOffLoading {
                                    textEditorState.codeEditor?.doActWithLatestEditorState("#onUpdateText") { textEditorState ->
                                        try{
                                            if(!textEditorState.isMultipleSelectionMode) {
                                                lastValidEditedColumnLineIndex.intValue = newTextFieldValue.selection.end
                                            }

                                            textEditorState.updateField(
                                                targetIndex = index,
                                                textFieldValue = newTextFieldValue,
                                            )

                                        }catch (e:IndexOutOfBoundsException) {
                                            // Undo/Redo后可能 出现 索引错误，没必要显示给用户，只记下日志就行
                                            MyLog.w(TAG, "#onUpdateText index out of bounds err, usually is ok, maybe the file changed after last save, so the line and column not match: "+e.localizedMessage)
                                        }catch (e:Exception) {
                                            // 其他错误，显示给用户
                                            Msg.requireShowLongDuration("#onUpdateText err: "+e.localizedMessage)

                                            //log
                                            MyLog.e(TAG, "#onUpdateText err: "+e.stackTraceToString())
                                        }

                                    }
                                }
                            },
                            onContainNewLine = cb@{ newTextFieldValue ->
                                doJobThenOffLoading {
                                    textEditorState.codeEditor?.doActWithLatestEditorState("#onContainNewLine") { textEditorState ->
                                        // This method no need to remember last edited column, cause the lines will split,
                                        //   then the `onUpdateText` and `onFocus` will update the last edited column


                                        try {
                                            textEditorState.splitNewLine(
                                                targetIndex = index,
                                                textFieldValue = newTextFieldValue,
                                                updater = { newLinesRange, newFields, newSelectedIndices ->
                                                    doJobThenOffLoading {
                                                        delay(200)
                                                        scrollIfIndexInvisible(newLinesRange.endInclusive)
                                                    }
                                                }
                                            )

                                        }catch (e:Exception) {
                                            Msg.requireShowLongDuration("#onContainNewLine err: "+e.localizedMessage)

                                            MyLog.e(TAG, "#onContainNewLine err: "+e.stackTraceToString())
                                        }

                                    }
                                }

                            },
                            onFocus = { newTextFieldValue: TextFieldValue ->
                                doJobThenOffLoading {
                                    textEditorState.codeEditor?.doActWithLatestEditorState("#onFocus") { textEditorState ->
                                        if(!textEditorState.isMultipleSelectionMode) {
                                            lastValidEditedColumnLineIndex.intValue = newTextFieldValue.selection.end
                                        }


                                        try {
                                            textEditorState.selectFieldValue(
                                                targetIndex = index,
                                                textFieldValue = newTextFieldValue
                                            )

                                        }catch (e:Exception) {
                                            Msg.requireShowLongDuration("#onFocus err: "+e.localizedMessage)
                                            MyLog.e(TAG, "#onFocus err: "+e.stackTraceToString())
                                        }

                                    }
                                }
                            },

                        )
                    }
                }

                if(mergeMode && curLineText.startsWith(settings.editor.conflictEndStr)) {
                    AcceptButtons(
                        lineIndex = index,
                        lineText = curLineText,
                        acceptOursColor = acceptOursBtnColor,
                        acceptTheirsColor = acceptTheirsBtnColor,
                        acceptBothColor = acceptBothBtnColor,
                        rejectBothColor = rejectBothBtnColor,
                        prepareAcceptBlock = prepareAcceptBlock,
                    )
                }

            }




            // show virtual space at the end of file
            if(index == lastIndexOfFields) {
                item {
                    Spacer(modifier = Modifier
                        .width(virtualWidth)
                        //设高度为屏幕高度-50dp，基本上能滚动到顶部，但会留出最后一行多一些的空间
                        .height(virtualHeight)
                        .clickable(
                            //隐藏点击效果（就是一点屏幕明暗变化一下那个效果）
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            //非选择模式，点空白区域，聚焦最后一行
                            textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                if (textEditorState.isMultipleSelectionMode.not()) {
                                    //点击空白区域定位到最后一行最后一个字符后面
                                    //第1个参数是行索引；第2个参数是当前行的哪个位置
                                    textEditorState.selectField(
                                        textEditorState.fields.lastIndex,
                                        SelectionOption.LAST_POSITION
                                    )


                                    //确保弹出键盘，不加的话“点击空白区域，关闭键盘，再点击空白区域”就不弹出键盘了
                                    //显示键盘，不在主线程运行也可以
                                    keyboardController?.show()

                                    //显示键盘（在主线程运行，可选，其实不在主线程也行）
//                                    withMainContext {
//                                        keyboardController?.show()
//                                    }
                                }

                            }
                        }
//                    .background(Color.Red)  //debug
                        ,
                    )
                }
            }
        }

        DisableSoftKeyboard(disableSoftKb.value) {
            LazyColumn(
                state = listState,
                //fillMaxSize是为了点哪都能触发滚动，这样点哪都能隐藏顶栏
                modifier = modifier.fillMaxSize(),
                contentPadding = contentPaddingValues
            ) {
                for(index in 0 until size) {
                    createItem(index)
                }
            }
        }


        LaunchedEffect(lastScrollEvent.value) TextEditorLaunchedEffect@{
            try {
                val lastScrollEvent = lastScrollEvent.value
//        if(debugModeOn) {
                //还行不是很长
//            println("lastScrollEvent.toString() + firstLineIndexState.value:"+(lastScrollEvent.toString() + firstLineIndexState.value))
//        }

//        if(debugModeOn) {
//            println("滚动事件更新了："+lastScrollEvent)
////            println("最后编辑行："+lastEditedPos)
//            println("第一个可见行："+firstLineIndexState.value)
//        }
                //如果值不是-1将会保存
//        var maybeWillSaveEditedLineIndex = -1  //最后编辑行
//        var maybeWillSaveFirstVisibleLineIndex = -1  //首个可见行

                //初始化（组件创建，第一次执行LaunchedEffect）之后，更新最新可见行
//        if(isInitDone.value) {
//            lastFirstVisibleLineIndexState = Math.max(0, firstLineIndexState.value)
//        }

                //刚打开文件，定位到上次记录的行，这个滚动只在初始化时执行一次
                if(lastScrollEvent==null && !isInitDone.value) {
                    //放到第一行是为了避免重入
                    isInitDone.value = true



                    // if is merge mode, try init position to first conflict
                    if(mergeMode) {
                        val fields = textEditorState.fields
                        // update cur conflict keyword, if cursor on conflict str line, if dont do this, UX bad, e.g. I clicked conflict splict line, then click prev conflict, expect is go conflict start line, but if last search is start line, this time will go to end line, anti-intuition
                        // 如果光标在冲突开始、分割、结束行之一，更新搜索关键字，如果不这样做，会出现一些反直觉的bug：我点击了conflict split line，然后点上，期望是查找conflict start line，但如果上次搜索状态是start line，那这次就会去搜索end line，反直觉
                        for(idx in fields.indices) {
                            val value = fields.getOrNull(idx) ?: continue
                            if(value.value.text.startsWith(settings.editor.conflictStartStr)) {
                                // scroll
                                UIHelper.scrollToItem(scope, listState, idx + lineNumOffsetForGoToEditor)
                                // focus line
                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                    textEditorState.selectField(
                                        idx,
                                        option = SelectionOption.FIRST_POSITION,
                                    )
                                }

                                // finished
                                return@TextEditorLaunchedEffect
                            }
                        }
                    }


                    // is merge mode, but not found conflict start str or not merge mode:
                    //    need go to line or restore last edited position


                    //goToLine触发场景：预览diff，发现某行需要改，点击行号，就会直接定位到对应行了
                    //会用goToLine的值减1得到索引，所以0也不行
                    val useLastEditPos = LineNum.shouldRestoreLastPosition(goToLine)

                    //滚动一定要放到scope里执行，不然这个东西一滚动，整个LaunchedEffect代码块后面就不执行了
                    //如果goToLine大于0，把行号减1换成索引；否则跳转到上次退出前的第一可见行
                    var targetFocusLineIndexWhenGoToLine = 0
                    UIHelper.scrollToItem(
                        coroutineScope = scope,
                        listState = listState,
                        index = if(useLastEditPos) {
                            lastEditedPos.firstVisibleLineIndex
                        } else if(goToLine == LineNum.EOF.LINE_NUM) {
                            targetFocusLineIndexWhenGoToLine = textEditorState.fields.size - 1
                            targetFocusLineIndexWhenGoToLine + lineNumOffsetForGoToEditor
                        } else {
                            targetFocusLineIndexWhenGoToLine = goToLine - 1
                            targetFocusLineIndexWhenGoToLine + lineNumOffsetForGoToEditor
                        }
                    )

                    //如果定位到上次退出位置，进一步检查是否需要定位到最后编辑列
                    //因为定位column会弹出键盘所以暂时不定位了，我不想一打开编辑器自动弹出键盘，因为键盘会自动读取上下文，可能意外获取屏幕上的文本泄漏隐私

                    //是否需要定位到上次编辑的列，若否，只定位到最后退出前的首个可见行
                    //如果是readOnly模式，就没必要定位到对应列了，就算定位了也无效，多此一举
                    if(bug_Editor_GoToColumnCantHideKeyboard_Fixed && useLastEditPos && settings.editor.restoreLastEditColumn && !readOnlyMode) {
                        // delay一下，等滚动到上次可见行，然后检查最后编辑行是否可见，若不可见，滚动到最后编辑行
                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                            delay(300)

                            val lastEditedLineIdx = lastEditedPos.lineIndex
                            // if target line invisible, scroll to it, e.g. edited line 100 then scroll to line 1 then quit,
                            //   in that case, when next time open file, will scroll to line 1 first, but when reached here, will found line 100 is invisible,
                            //   then need one more scroll to make line 100 visible
                            scrollIfIndexInvisible(lastEditedLineIdx)


                            // 定位到指定列。注意：会弹出键盘！没找到好的不弹键盘的方案，所以我曾经把定位列功能默认禁用了
                            // position to column of line, will popup soft keyboard, can't find properly way to hidden soft kb
                            textEditorState.selectField(
                                lastEditedLineIdx,
                                option = SelectionOption.CUSTOM,
                                columnStartIndexInclusive = lastEditedPos.columnIndex
                            )


                            requestFromParent.value = PageRequest.hideKeyboardForAWhile

                        }
                    }else {  // when go to line valid, focus target line
                        textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                            textEditorState.selectField(
                                targetFocusLineIndexWhenGoToLine,
                                option = SelectionOption.FIRST_POSITION,
                            )
                        }
                    }

                    return@TextEditorLaunchedEffect


                    //只有当滚动事件不为null且isConsumed为假时，才执行下面的代码块
                }else if(lastScrollEvent?.isConsumed == false) {  //编辑了文件，行号有更新，更新配置文件记录的行并定位到对应的行
                    //消费以避免重复执行（设置isConsumed为true）
                    lastScrollEvent?.consume()

                    //检查是否不检查行是否可见，直接强制跳转
                    val forceGo = lastScrollEvent?.forceGo == true
                    lastScrollEvent?.index?.let { index ->
//                val safeIndex = Math.max(0, index)
//                maybeWillSaveEditedLineIndex = safeIndex

                        //先跳转，然后更新配置文件
                        //跳转
                        //强制跳转，无论是否可见
                        if(forceGo) {
                            //强制跳转不应该更新最后编辑行，因为跳转后并不会自动focus跳转到的那行，最后编辑行其实还是之前的那个，所以，只更新可见行即可
//                    maybeWillSaveEditedLineIndex = -1

                            //强制跳转的话，第一个可见行就是跳转的那行
//                    lastFirstVisibleLineIndexState = index
                            //定位行
                            UIHelper.scrollToItem(scope, listState, index + lineNumOffsetForGoToEditor)

//                        println("lastScrollEvent!!.columnStartIndexInclusive:${lastScrollEvent!!.columnStartIndexInclusive}")  //test1791022120240812
                            //定位列，如果请求定位列的话
                            if(lastScrollEvent?.goColumn==true) {
                                textEditorState.codeEditor?.doActWithLatestEditorStateInCoroutine("#selectField") { textEditorState ->
                                    //选中关键字
                                    textEditorState.selectField(
                                        targetIndex = index,
                                        option = SelectionOption.CUSTOM,
                                        columnStartIndexInclusive = lastScrollEvent!!.columnStartIndexInclusive,
                                        //如果选中某行子字符串的功能修复了，就使用正常的endIndex；否则使用startIndex，定位光标到关键字出现的位置但不选中关键字
                                        columnEndIndexExclusive = if (bug_Editor_SelectColumnRangeOfLine_Fixed) lastScrollEvent!!.columnEndIndexExclusive else lastScrollEvent!!.columnStartIndexInclusive,
                                        requireSelectLine = false,
//                                    highlightingStartIndex = lastScrollEvent.highlightingStartIndex,
//                                    highlightingEndExclusiveIndex = lastScrollEvent.highlightingEndExclusiveIndex,
                                    )
                                }

                                //请求失焦则关闭键盘
                                if(lastScrollEvent.requireHideKeyboard) {
                                    keyboardController?.hide()
                                }
                            }
                        }else {
                            scrollIfIndexInvisible(index)
                        }
                    }
                }

            }catch (e:Exception) {
                MyLog.e(TAG, "TextEditor#LaunchedEffect err: "+e.stackTraceToString())
            }

//        if(debugModeOn) {
//            println("maybeSaveEditedLineIndex=$maybeWillSaveEditedLineIndex")
//            println("maybeSaveFirstVisibleLineIndex=$maybeWillSaveFirstVisibleLineIndex")
//            println("lastEditedLineIndexState=$lastEditedLineIndexState")
//            println("lastFirstVisibleLineIndexState=$lastFirstVisibleLineIndexState")
//        }

        }
    }
}




fun getNextKeyWordForConflict(curKeyWord:String, settings: AppSettings):String {
    if(curKeyWord == settings.editor.conflictStartStr) {
        return settings.editor.conflictSplitStr
    }else if(curKeyWord == settings.editor.conflictSplitStr) {
        return settings.editor.conflictEndStr
    }else { // curKeyWord == settings.editor.conflictEndStr
        return settings.editor.conflictStartStr
    }
}
fun getPreviousKeyWordForConflict(curKeyWord:String, settings: AppSettings):String {
    if(curKeyWord == settings.editor.conflictStartStr) {
        return settings.editor.conflictEndStr
    }else if(curKeyWord == settings.editor.conflictEndStr) {
        return settings.editor.conflictSplitStr
    }else { // curKeyWord == settings.editor.conflictSplitStr
        return settings.editor.conflictStartStr
    }
}

class ScrollEvent(
    val index: Int = -1,
    val forceGo:Boolean = false,
    val goColumn:Boolean=false,
    val columnStartIndexInclusive:Int=0,
    val columnEndIndexExclusive:Int=columnStartIndexInclusive,

    val requireHideKeyboard:Boolean = false

) {
    var isConsumed: Boolean = false
        private set

    fun consume() {
        isConsumed = true
    }
}

@Parcelize
data class SearchPos(var lineIndex:Int=-1, var columnIndex:Int=-1):Parcelable {
    companion object{
        val NotFound = SearchPos(-1, -1)
    }
}

data class SearchPosResult(val foundPos: SearchPos = SearchPos.NotFound, val nextPos: SearchPos = SearchPos.NotFound) {
    companion object{
        val NotFound = SearchPosResult(foundPos = SearchPos.NotFound, nextPos = SearchPos.NotFound)
    }
}
