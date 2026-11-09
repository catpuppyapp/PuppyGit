package com.catpuppyapp.puppygit.fileeditor.texteditor.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.constants.IndentChar
import com.catpuppyapp.puppygit.dto.UndoStack
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.SearchPos
import com.catpuppyapp.puppygit.fileeditor.texteditor.view.SearchPosResult
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.StylesResult
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.StylesUpdateRequest
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.SyntaxHighlightResult
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.scopeInvalid
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.EditCache
import com.catpuppyapp.puppygit.utils.EncodingUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.doActIfIndexGood
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.generateRandomString
import com.catpuppyapp.puppygit.utils.getNextIndentByCurrentStr
import com.catpuppyapp.puppygit.utils.isGoodIndexForList
import com.catpuppyapp.puppygit.utils.isGoodIndexForStr
import com.catpuppyapp.puppygit.utils.parseLongOrDefault
import com.catpuppyapp.puppygit.utils.tabToSpaces
import io.github.rosemoe.sora.text.CharPosition
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.OutputStream


private const val TAG = "TextEditorState"
private const val lb = "\n"

private fun targetIndexValidOrThrow(targetIndex:Int, listSize:Int) {
    if (targetIndex < 0 || targetIndex >= listSize) {
        throw IndexOutOfBoundsException("targetIndex '$targetIndex' out of range '[0, $listSize)'")
    }
}


//不实现equals，直接比较指针地址，反而性能可能更好，不过状态更新可能并不准确，例如fields没更改的情况也会触发页面刷新
@Immutable
class TextEditorState(


    /**
     the `fieldsId` only about fields, same fieldsId should has same fields
     */
    val fieldsId: String = newId(),
    val fields: List<MyTextFieldState> = listOf(),

    val selectedIndices: List<Int> = listOf(),
    val isMultipleSelectionMode: Boolean = false,

    val focusingLineIdx: Int? = null,

    //话说这几个状态如果改变是否会触发重组？不过就算重组也不会创建新数据拷贝，性能影响应该不大
    //这个东西可以每个状态独有，但会增加额外拷贝，也可所有状态共享，但无法判断每个状态是否已经创建快照或者是否有未保存修改，目前采用方案2，所有Editor状态共享相同的指示内容是否修改的状态
    val isContentEdited: MutableState<Boolean>,  // old name `isContentChanged`
    val editorPageIsContentSnapshoted:MutableState<Boolean>,

    // caller from: 正常来说是null，其他类型参见枚举类
    // caller from: usually is null, other types see that enum class
    private val onChanged: suspend (newState:TextEditorState, trueSaveToUndoFalseRedoNullNoSave:Boolean?, clearRedoStack:Boolean, caller: TextEditorState, from: EditorStateOnChangeCallerFrom?) -> Unit,

    val codeEditor: MyCodeEditor?,

//    val uniId: String = newId(),
    // temporary use when syntax highlighting analyzing
//    var temporaryStyles: StylesResult? = null,

) {
    private val lock = Mutex()

//    private var stylesAppliedByView = false

    fun copy(
        fieldsId: String = this.fieldsId,
        fields: List<MyTextFieldState> = this.fields,
        selectedIndices: List<Int> = this.selectedIndices,
        isMultipleSelectionMode: Boolean = this.isMultipleSelectionMode,
        focusingLineIdx: Int? = this.focusingLineIdx,
        isContentEdited: MutableState<Boolean> = this.isContentEdited,
        editorPageIsContentSnapshoted:MutableState<Boolean> = this.editorPageIsContentSnapshoted,
        onChanged: suspend (newState:TextEditorState, trueSaveToUndoFalseRedoNullNoSave:Boolean?, clearRedoStack:Boolean, caller: TextEditorState, from: EditorStateOnChangeCallerFrom?) -> Unit = this.onChanged,
        codeEditor: MyCodeEditor? = this.codeEditor,
//        uniId: String = this.uniId,
//        temporaryStyles: StylesResult? = this.temporaryStyles,
    ) = TextEditorState(
        fieldsId = fieldsId,
        fields = fields,
        selectedIndices = selectedIndices,
        isMultipleSelectionMode = isMultipleSelectionMode,
        focusingLineIdx = focusingLineIdx,
        isContentEdited = isContentEdited,
        editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
        onChanged = onChanged,
        codeEditor = codeEditor,
//        uniId = uniId,
//        temporaryStyles = temporaryStyles,
    )


    suspend fun undo(undoStack: UndoStack) {
        undoOrRedo(undoStack, true)
    }

    suspend fun redo(undoStack: UndoStack) {
        undoOrRedo(undoStack, false)
    }

    private suspend fun undoOrRedo(undoStack: UndoStack, trueUndoFalseRedo:Boolean) {
        lock.withLock {
            val lastState = if(trueUndoFalseRedo) undoStack.undoStackPop() else undoStack.redoStackPop()
            if(lastState != null) {
                //如果未编辑且fieldsId没变，则为真，否则为假
                isContentEdited.value = isContentEdited.value || fieldsId != lastState.fieldsId
                editorPageIsContentSnapshoted?.value=false
//                _fieldsId = TextEditorState.newId()

                //无论执行redo还是undo，都不需要清redoStack，所以这里此值传false
                //ps. 清redoStack的时机是先undo，再编辑内容，这时才需要清
                val clearRedoStack = false
                // 第2个值需要取反，因为执行redo的时候期望更新undoStack，执行undo的时候期望更新redoStack
                onChanged(lastState, !trueUndoFalseRedo, clearRedoStack, this, null)
            }
        }
    }


    /**
     * x 已修复） 20241203: 发现有bug，无法找到 " w " 这样字符，已经修复，在无匹配时将被查找的文件中的字符串索引向后移一位再和关键字从头匹配即可（反向查找时是向前移一位然后和关键字末尾匹配）
    @param toNext : if `toNext` is false, will search to previous
    缺陷：不支持查找包含换行符的关键字
     * @return SearchPos : if not found, will return SearchPos(-1,-1)
     */
    fun doSearch(keyword:String, toNext:Boolean, startPos: SearchPos): SearchPosResult {
        val funName="doSearch"

        fun getCurTextOfIndex(idx:Int, list:List<MyTextFieldState>):String{
            return list[idx].value.text.lowercase()
        }

        try {
            //搜索关键字
            //开始从当前位置查找关键字，直到找到，或者从头到尾都找不到
            val f = fields
            if(f.isEmpty() || keyword.isEmpty()) {
                return SearchPosResult.NotFound
            }

            val goodIndex = isGoodIndexForList(startPos.lineIndex, f)
            //如果行号有误，把列号重置为0或最后一行把列号重置为0或最后一列；否则使用原行号和列号（不过列号仍有可能有误
            val curPos = startPos.copy(lineIndex = if(goodIndex) startPos.lineIndex else {if(toNext) 0 else f.size-1})
            var curText = getCurTextOfIndex(curPos.lineIndex, f)
            curPos.columnIndex = if(goodIndex) startPos.columnIndex else {if(toNext) 0 else curText.length-1}
            val endPosOverThis = curPos.copy()  //越过这一行这一列，就查到开头了，结束了
            var curIndexOfKeyword= if(toNext) 0 else keyword.length-1

            curPos.columnIndex = if(goodIndex && isGoodIndexForStr(curPos.columnIndex, curText)){
                curPos.columnIndex
            }else{
                if(toNext){
                    0
                }else {
                    curText.length-1
                }
            }

            var char = if(curText.isEmpty()) null else curText[curPos.columnIndex]
            var charOfKeyword = keyword[curIndexOfKeyword]
            var looped = false

            val invalidIndex = -1

            // last value of matched a char
            var resetIndex = invalidIndex
            while(true) {
//                println("在搜索:lineIndex=${curPos.lineIndex}, columnIndex=${curPos.columnIndex}")  //test1791022120240812
                //matched 1 char
                if(char != null && char == charOfKeyword) {
//                    println(curPos) //test1791022120240812
                    if(toNext){
                        curIndexOfKeyword++
                    }else {
                        curIndexOfKeyword--
                    }

                    //found!
                    if(!isGoodIndexForStr(curIndexOfKeyword, keyword)) {
                        val foundLineIdx = curPos.lineIndex
                        val foundColumnIdx = if(toNext) curPos.columnIndex+1-keyword.length else curPos.columnIndex

                        var nextColumn = if(toNext) foundColumnIdx+keyword.length else foundColumnIdx-1
                        var nextLineIdx = foundLineIdx
                        //需要换行
                        if(!isGoodIndexForStr(nextColumn, curText)) {
                            nextLineIdx = if(toNext) nextLineIdx+1 else nextLineIdx-1
                            if(!isGoodIndexForList(nextLineIdx, f)) {
                                // the file at least have an empty line, so the `f` never will be empty, so the else block is safe
                                // 文件至少有个空行，所以f永远不会为空，所以else代码块至少为0，不会负数
                                nextLineIdx = if(toNext) 0 else f.size-1
                            }

                            // if is empty line, the else case will got -1, but is fine, when next search, if use this index, will reset,
                            //   let this index coerceAtLeast(0) doesn't make sense, due to 0 or -1, all is invalid index for an empty line
                            nextColumn = if(toNext) 0 else getCurTextOfIndex(nextLineIdx, f).length-1
                        }
//                        println("posResult:${SearchPosResult(foundPos = curPos.copy(lineIndex = foundLineIdx, columnIndex = foundColumnIdx), nextPos = SearchPos(lineIndex =  nextLineIdx, columnIndex = nextColumn))}")  //test1791022120240812
                        return SearchPosResult(
                            foundPos = curPos.copy(lineIndex = foundLineIdx, columnIndex = foundColumnIdx),
                            nextPos = SearchPos(lineIndex =  nextLineIdx, columnIndex = nextColumn)
                        )
                    }


                    //更新被搜索的字符串索引
                    if(toNext) {
                        curPos.columnIndex++
                    }else {
                        curPos.columnIndex--
                    }

                    // 第一次匹配，若匹配关键字失败，重置到当前字符的下一个字符(若反向查找，则是上一个字符)
                    if(resetIndex == invalidIndex) {
                        resetIndex = curPos.columnIndex
                    }


                }else {  // not matched!
                    // reset index of keyword
                    curIndexOfKeyword= if(toNext) 0 else keyword.length-1

                    // if last is matched, keep target text index at same place for matching to head or tail of keyword

                    //如果上次未匹配到，正常往前推进，否则重置索引指向上次匹配到的字符串之后的第一个字符
                    if(resetIndex == invalidIndex) {  //上次未匹配到
                        //更新被搜索的字符串索引
                        if(toNext) {
                            curPos.columnIndex++
                        }else {
                            curPos.columnIndex--
                        }
                    }else {  //上次匹配到过部分字符
                        //匹配过，将索引重置为下一个索引，例如：被搜索字符串：abbbbcd，关键字bbc，若在索引1开始匹配到，索引3匹配失败，那下一个被搜索的索引应该是2
                        curPos.columnIndex = resetIndex
                        //重置索引每次使用一次后，需将其置为无效索引
                        resetIndex = invalidIndex
                    }

                }

                //这里取出的字符有两种情况，要么是关键字中的下一个字符，要么是重置后的开头或末尾字符
                // 情况1： 索引仍有效，说明只匹配了部分关键字，需要继续查找
                // 情况2： 无匹配，索引已重置到关键字开头或末尾
                charOfKeyword = keyword[curIndexOfKeyword]

                //查找到当前行开头或末尾了，该换行了
                if(!isGoodIndexForStr(curPos.columnIndex, curText)) {  //需要换行
                    if(looped) {  //过了最初查找的那行且需要换行，说明查找完了，可返回了
//                        val lineOverLooped = if(toNext) curPos.lineIndex > endPosOverThis.lineIndex else curPos.lineIndex < endPosOverThis.lineIndex
//                        if(lineOverLooped) {
//                            return SearchPos.NotFound
//                        }

                        //如果循环查找，还是进入到换行的这块代码，就可以直接返回了，已经搜索过头了
                        return SearchPosResult.NotFound

                    }

                    //执行到这，looped必定为假，意味着还没查找到起点，还得继续找

                    //换行后将重置索引重新设为无效
                    resetIndex = invalidIndex

                    //换行，需要reset index of keyword，不然搜索时行末尾和下行开头会被认为是连续的而导致结果出错
                    curIndexOfKeyword= if(toNext) 0 else keyword.length-1
                    charOfKeyword = keyword[curIndexOfKeyword]


                    //换行
                    if(toNext) curPos.lineIndex++ else curPos.lineIndex--

                    //查找到文件第一行或最后一行了，该从头或从尾重新查了 （循环查找）
                    if(!isGoodIndexForList(curPos.lineIndex, f)){
                        curPos.lineIndex = if(toNext) 0 else f.size-1
                    }

                    curText = getCurTextOfIndex(curPos.lineIndex, f)

                    curPos.columnIndex = if(toNext) 0 else curText.length-1

                    //换行换到最初查找的那行了
                    if(curPos.lineIndex==endPosOverThis.lineIndex) { //隐含 `&& !looped`
                        looped = true
                    }
                }

                //注释这段代码可确保完整查找起始搜索那行以免光标在起始行的关键字中间时漏掉关键字
                //光标在关键字中间时搜索会有bug：如果启用此代码块，将会在匹配到起始搜索行的起始列时终止，导致无法匹配以下情况：例如：关键字"1234"，全文只有一行匹配文本为"1[光标]2345"，这时，向上搜索，将会无法匹配到"1234"，因为代码会在抵达光标处时返回not found，只要光标在关键字中间皆可复现此bug（例如把前面的匹配文本改为"12[光标]345"，也会not found），不过注意：因为搜索模式下更新列索引有bug所以禁用了搜索模式下更新列索引的机制，所以需要在非搜索模式把光标放到关键字中间才能复现此bug。
                //找了一轮了，在起始行，检查是否已经超过起始列，如果超了直接返回，如果没超，继续搜索，直到超了起始列（在这返回）或者超了起始行（在上面的代码返回）
//                if(looped) {
//                    //循环查找，行
//                    //这里加等号是正确的，另外：即使把等号去掉，也无法修复上面提到的光标在关键字中间时搜索会有bug的问题，因为只要光标位置离起始列的距离大于1个字符，就还是无法完整匹配整行，所以还是会有那个bug
//                    val end = if(toNext) curPos.columnIndex >= endPosOverThis.columnIndex else curPos.columnIndex <= endPosOverThis.columnIndex
//                    if(end){
//                        return SearchPosResult.NotFound
//                    }
//                }

                //把空行当作null，无匹配即可
                char = if(curText.isEmpty()) null else curText[curPos.columnIndex]
            }


        }catch (e:Exception) {
            Msg.requireShowLongDuration("err: "+e.localizedMessage)
            MyLog.e(TAG, "#$funName err: keyword=$keyword, toNext=$toNext, startPos=$startPos, err=${e.stackTraceToString()}")
            return SearchPosResult.NotFound
        }

    }


    suspend fun splitNewLine(
        targetIndex: Int,
        textFieldValue: TextFieldValue,

        // do some changes for new text field states, will call it after new fields added to newFields
        updater:((newLinesRange: IntRange, newFields: MutableList<MyTextFieldState>, newSelectedIndices: MutableList<Int>) -> Unit)? = null
    ) {
        lock.withLock {
            try {
                targetIndexValidOrThrow(targetIndex, fields.size)
            }catch (e: Exception) { // will throw when line dost, maybe changed by external or other cases, but it's fine, no need throw exception
                MyLog.d(TAG, "TextEditorState.splitNewLine() err: ${e.stackTraceToString()}")
                return
            }

            if (!textFieldValue.text.contains(lb)) {
//                throw InvalidParameterException("textFieldValue doesn't contains newline")
                return
            }

            val oldLine = fields[targetIndex]

            val newText = textFieldValue.text

            //get old line text
            val (oldTextLeft, oldTextRight) = oldLine.value.let {
                it.text.let { text ->
                    Pair(text.substring(0, it.selection.min), text.substring(it.selection.max))
                }
            }
            val oldText = oldTextLeft + oldTextRight

            val splitFieldValues = splitTextsByNL(newText, oldText)

            //创建新字段集合
            val newFields = fields.toMutableList()

            //处理第一行
            val splitFirstLine = splitFieldValues.first()
            // 如果第一行为空，则说明在旧行的开头插入了换行符，可能是粘贴了多行，也可能是输入或粘贴了单行，这两种情况有个共同点，
            // 就是新内容的最后一行就是旧内容本身，而且这一行的changeType状态应该保持不变
            //这里加oldLine text 非空判断是为了使旧行为空时按回车让旧行changeType保持不变，追加的行为新增
            val newLineAtOldLineHead = oldLine.value.text.isNotEmpty() && splitFirstLine.text.isEmpty()
            newFields[targetIndex] = oldLine.copy(value = splitFirstLine).apply {
                if(newLineAtOldLineHead) {  //在旧行头部添加了一换行符
                    // 如果新行开头是空行，必然是新行，所以强制更新而不用if none则更新
                    updateLineChangeType(LineChangeType.NEW)

                    //这里用oldLine或this的changeType都行，因为 this是oldLine的copy，而且没有拷贝changeType，所以他们两个的changeType应该相同，但从逻辑上来说，应该用oldLine的
                    //为真则是在旧行中间添加的换行符，否则是在旧行末尾（无需处理，维持旧行changeType即可）
                }else if(oldLine.changeType == LineChangeType.NONE && this.value.text.let { t ->
                        oldLine.value.text.let { oldT ->
                            // the String.equals() should compare length first, if didn't match,
                            //   then compare the string? if it is, the length check at here can be remove
                            t.length != oldT.length || t != oldT
                        }
                    }
                ) {
                    // this是拷贝了新内容的新字段，如果新字段的文本和旧字段的文本不同，说明内容改变了，更新changeType，否则不用更新
                    updateLineChangeType(LineChangeType.UPDATED)
                } // else 在旧行末尾添加了换行符，无需处理

            }

            //追加新行（注意：新行从第二行开始，并且，如果新行是从旧行的中间分割的，则新行的第一行可能有旧行的后半段内容）
            val newSplitFieldValues = splitFieldValues.subList(1, splitFieldValues.size)
            //把新内容转换成text field对象，其类型必然是NEW，因为是新增的
            val newSplitFieldStates = newSplitFieldValues.map { MyTextFieldState(value = it, changeType = LineChangeType.NEW) }

            //如果是从头部插入的新行，则需要检查最后一行的内容是否和旧行的内容相同，如果相同，其changeType应维持旧行，否则改成updated，但不管怎么，它都不应该是NEW，因为本质上它是原来的旧行修改而来的
            if(newLineAtOldLineHead) {
                newSplitFieldStates[newSplitFieldStates.size - 1].apply {
                    //注意这里是强制更新行changeType，因为上面已经更新成NEW了，这里如果不强制，则无法更新
                    updateLineChangeType(oldLine.changeType)
                    //在旧行的基础上，决定最终状态
                    if(oldLine.changeType == LineChangeType.NONE && this.value.text != oldLine.value.text) {
                        updateLineChangeType(LineChangeType.UPDATED)
                    }
                }
            }

            //addAll若待插入值，最大值是size，不会越界，若超过就越了
            newFields.addAll(targetIndex + 1, newSplitFieldStates)


            //追加行后的目标索引，实际对应的是 newSplitFieldValues 最后一个元素在当前文件的所有行中的位置
            val lastNewSplitFieldIndex = targetIndex + newSplitFieldValues.size

//            val newFocusingLineIdx = mutableStateOf(focusingLineIdx)
//            val newSelectedIndices = selectedIndices.toMutableList()

            // 判断是定位到新内容最后一行开头还是末尾
            val oldTargetText = fields[targetIndex].value.text  //旧行
            val newTargetFirstText = splitFirstLine.text  //分割后的第一行对应旧的目标行
            val newTargetLastText = splitFieldValues.last().text  //分割后的最后一行，光标会focus到此行，此行与 newSplitFieldValues 的最后一个元素相同，因为 newSplitFieldValues 是 splitFieldValues 的一个sub list

            val newSelectedIndices = selectedIndices.toMutableList()

            val sfiRet = selectFieldInternal(
                init_fields = newFields,
                init_selectedIndices = newSelectedIndices,
                isMutableFields = true,
                isMutableSelectedIndices = true,
                targetIndex = lastNewSplitFieldIndex,

                // cursor position
                option = SelectionOption.CUSTOM,
                columnStartIndexInclusive = newTargetLastText.length - oldTextRight.length,
            )

            val newContentRangeInNewFields = IntRange(targetIndex, targetIndex + newSplitFieldStates.size)

            updater?.invoke(newContentRangeInNewFields, newFields, newSelectedIndices)



            //更新状态变量
            isContentEdited?.value=true
            editorPageIsContentSnapshoted?.value=false
            EditCache.writeToFile(newText)


            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = newId(),
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )

            updateStyles(splitFieldValues.size, newState) { baseStyles ->
                // delete current line
                updateStylesAfterDeleteLine(
                    baseFields = fields,
                    stylesResult = baseStyles,
                    startLineIndex = targetIndex,
                    ignoreThis = true,
                    newTextEditorState = newState
                )

                // add new content to current line
                updateStylesAfterInsertLine(
                    baseFields = fields,
                    stylesResult = baseStyles,
                    ignoreThis = false,
                    insertedContent = splitFieldValues.joinToString(lb) { it.text },
                    insertedContentRangeInNewFields = newContentRangeInNewFields,
                    newTextEditorState = newState
                )
            }

            onChanged(newState, true, true, this, null)
        }
    }


//    @Deprecated("这个和splitNewLine一样，可转化为splitNewLien处理，省得维护两个相同的逻辑，头疼")
//    suspend fun splitAtCursor(targetIndex: Int, textFieldValue: TextFieldValue) {
//        lock.withLock {
//
//            targetIndexValidOrThrow(targetIndex, fields.size)
//
//            val splitPosition = textFieldValue.selection.start  //光标位置，有可能在行末尾，这时和text.length相等，并不会越界
//            val maxOfPosition = textFieldValue.text.length
//            if (splitPosition < 0 || splitPosition > maxOfPosition) {  //第2个判断没错，就是大于最大位置，不是大于等于，没写错，光标位置有可能在行末尾，这时其索引和text.length相等，所以只有大于才有问题
//                throw InvalidParameterException("splitPosition '$splitPosition' out of range '[0, $maxOfPosition]'")
//            }
//
//            val newText = textFieldValue.text
//            val firstStart = 0
//            val firstEnd = if (splitPosition == 0) 0 else splitPosition
//            val first = newText.substring(firstStart, firstEnd)
//
//            val newLineAtOldLineHead = first.isEmpty()  // or `firstEnd == 0`
//
//            val secondEnd = newText.length
//            val second = newText.substring(firstEnd, secondEnd)
//
//            val firstValue = textFieldValue.copy(first)
//            val newFields = fields.toMutableList()
//
//            //更新第一行
//            val oldField = newFields[targetIndex]
//            newFields[targetIndex] = oldField.copy(value = firstValue, isSelected = false).apply {
//                if(newLineAtOldLineHead) {
//                    updateLineChangeType(LineChangeType.NEW)
//                }else if(this.value.text != oldField.value.text) {
//                    // 如果新内容不等于旧内容，说明发生了修改
//                    // 注：这两个内容都不包含换行符
//                    updateLineChangeTypeIfNone(LineChangeType.UPDATED)
//                }  // else，就是在旧行末尾插入了换行符，保持当前行changeType不变即可，无需修改
//            }
//
//            //添加第2行
//            newFields.add(
//                targetIndex + 1,
//                TextFieldState(
//                    value = TextFieldValue(second, TextRange.Zero),
//                    isSelected = false,
//                ).apply {
//                    if(新旧text相等则使用原changetype，否则使用新的)
//                    updateLineChangeTypeIfNone(if(newLineAtOldLineHead) this.changeType else LineChangeType.NEW)
//                }
//            )
//
//
////            val newFocusingLineIdx = mutableStateOf(focusingLineIdx)
////            val newSelectedIndices = selectedIndices.toMutableList()
//
//            val sfiRet = selectFieldInternal(
//                init_fields = newFields,
//                init_selectedIndices = selectedIndices,
//                isMutableFields = true,
//                isMutableSelectedIndices = false,
////                out_focusingLineIdx = newFocusingLineIdx,
////                init_focusingLineIdx = focusingLineIdx,
//
//                targetIndex = targetIndex + 1
//            )
//
//
//            isContentEdited?.value=true
//            editorPageIsContentSnapshoted?.value=false
//
//            val newState = internalCreate(
//                fields = sfiRet.fields,
//                fieldsId = newId(),
//                selectedIndices = sfiRet.selectedIndices,
//                isMultipleSelectionMode = isMultipleSelectionMode,
//                focusingLineIdx = sfiRet.focusingLineIdx
//            )
//
//            onChanged(newState, true, true)
//        }
//    }


    /**
     * @param textChanged if already checked text change
     *   before calling this method, then can pass the value
     *   to avoid redundant check.
     */
    suspend fun updateField(
        targetIndex: Int,
        textFieldValue: TextFieldValue,
        textChanged: Boolean? = null,
        requireLock: Boolean = true,
        closePairIfNeed: Boolean = SettingsUtil.isEditorAutoCloseSymbolPairEnabled(),
        updater: ((newLinesRange: IntRange, newFields: MutableList<MyTextFieldState>, newSelectedIndices: MutableList<Int>) -> Unit)? = null
    ) {
        val act = suspend p@{
            try {
                targetIndexValidOrThrow(targetIndex, fields.size)
            }catch (e: Exception) { // will throw when line dost, maybe changed by external or other cases, but it's fine, no need throw exception
                MyLog.d(TAG, "TextEditorState.updateField() err: ${e.stackTraceToString()}")
                return@p
            }

            // contains new line should call `splitNewLine`, reached here means has bug
            if (textFieldValue.text.contains(lb)) {
//                throw InvalidParameterException("textFieldValue contains newline")
                return@p
            }

            val newText = textFieldValue.text

            val oldField = fields[targetIndex]
            //检查字段内容是否改变，由于没改变也会调用此方法，所以必须判断下才能知道内容是否改变
            val textChanged = textChanged ?: oldField.value.text.let { it.length != newText.length || it != newText }  // 旧值 != 新值

            //没什么意义，两者总是不相等，似乎相等根本不会触发updateField
//            if(oldField.equals(textFieldValue)) {
//                return
//            }

            var maybeNewId = fieldsId

            val newFields = fields.toMutableList()

            val updatedField = newFields[targetIndex].copy(value = textFieldValue)

            //判断文本是否相等，注意：就算文本不相等也不能在这返回，不然页面显示有问题，比如光标位置会无法更新
            newFields[targetIndex] = if(textChanged) {
                isContentEdited?.value = true
                editorPageIsContentSnapshoted?.value = false
                maybeNewId = newId()

                //缓存新值
                EditCache.writeToFile(newText)

                //更新当前行状态为已修改
                updatedField.updateLineChangeTypeIfNone(LineChangeType.UPDATED)

                if(closePairIfNeed) {
                    appendClosePairIfNeed(oldField, updatedField)
                }else {
                    updatedField
                }
            }else {
                updatedField
            }



            val newSelectedIndices = selectedIndices.toMutableList()
            val newState = internalCreate(
                fields = newFields,
                fieldsId = maybeNewId,
                selectedIndices = newSelectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,

                // when selection mode on, the `focusingLineIdx` will update by `selectField`
                focusingLineIdx = if(isMultipleSelectionMode) focusingLineIdx else targetIndex,
            )

            val newContentRangeInNewFields = IntRange(targetIndex, targetIndex)

            updater?.invoke(newContentRangeInNewFields, newFields, newSelectedIndices)

            if(textChanged) {
                updateStyles(1, newState) { baseStyles->
                    // delete current line
                    updateStylesAfterDeleteLine(
                        baseFields = fields,
                        stylesResult = baseStyles,
                        startLineIndex = targetIndex,
                        ignoreThis = true,
                        newTextEditorState = newState
                    )

                    // add new content to current line
                    updateStylesAfterInsertLine(
                        baseFields = fields,
                        stylesResult = baseStyles,
                        ignoreThis = false,
                        insertedContent = newFields[targetIndex].value.text,
                        insertedContentRangeInNewFields = newContentRangeInNewFields,
                        newTextEditorState = newState
                    )
                }
            }

            onChanged(newState, if(textChanged) true else null, textChanged, this, null)
        }

        if(requireLock) {
            lock.withLock {
                act()
            }
        }else {
            act()
        }
    }

    private fun appendClosePairIfNeed(
        oldField: MyTextFieldState,
        newField: MyTextFieldState,
    ) : MyTextFieldState {
        return try {
            appendClosePairIfNeedNoCatch(oldField, newField)
        }catch (e: Exception) {
            MyLog.e(TAG, "#appendClosePairIfNeed err: ${e.localizedMessage}")
            e.printStackTrace()

            newField
        }
    }


    /**
     * append close pair if need
     * NOTE: make sure call this only when field text changed, else will have unexpected chars inserted
     *
     * background: add this feature is not I want actually, but if user input an opened symbol(e.g. "),
     *     it will take big effect for syntax highlighting, maybe let it very slow,
     *     so I add this feature to let the pair closed if possible
     */
    private fun appendClosePairIfNeedNoCatch(
        oldField: MyTextFieldState,
        newField: MyTextFieldState,
    ) : MyTextFieldState {
        val newText = newField.value.text
        if(newText.isEmpty()) {
            return newField
        }

        // if user deleting or paste text, don't add closed symbol
        val oldText = oldField.value.text
        val oldSelection = oldField.value.selection

        if(
            // haven't selection
            // if newText.length < oldText.length, is deleting, just return;
            // if equals, means no changed
            (oldSelection.collapsed && newText.length <= oldText.length)
            // have selection
            // if the right condition is true, is a "Cut" or "Delete" action, just return
            || (!oldSelection.collapsed && (oldText.length - oldSelection.end + oldSelection.start == newText.length))
        ) {
            return newField
        }

        // make sure only add one symbol
//        if((newText.length - oldText.length) != 1) {
//            return newField
//        }

        val newSelection = newField.value.selection
        val newCursorAt = newSelection.start

        //if `start` is 0, the left of cursor is nothing, so return
        if(!newSelection.collapsed || newCursorAt <= 0) {
            return newField
        }

//        // BEGIN: make sure char before sign is space char
//        // selection maximum value is length, so need subtract 2 to get the char before user last input
//        val charBeforeSign = newText.getOrNull(cursorAt - 2)
//        val charAfterSign = newText.getOrNull(cursorAt)
//        if((charBeforeSign != null && !charBeforeSign.isWhitespace())
//            || (charAfterSign != null && !charAfterSign.isWhitespace())
//        ) {
//            return newField
//        }
//        // END: make sure char before sign is space char

        val leftTextOfCursor = newText.substring(0, newSelection.start)
        // note: must match string first, then char, cause string may include char,
        //     e.g. "</" included '<', and '<' is a opened char, if reverse order, will never match "</"
        var closedCharOfPair = resolveClosedStringOfPair(leftTextOfCursor)


        if(closedCharOfPair == null) {
            val openedCharOfPair = newText.get(newCursorAt - 1)
            closedCharOfPair = resolveClosedCharOfPair(openedCharOfPair)

            if(closedCharOfPair == null) {
                return newField
            }
        }

        // when reached here, must found pair

        var newSelectionRange = newSelection
        var midText = closedCharOfPair
        val rightText = newText.substring(newSelection.start)
        // user selected text, then input opened symbol, e.g. select abc then input ", expected output "abc"
        if(!oldSelection.collapsed) {
            val oldSelectedText = oldText.substring(oldSelection.min, oldSelection.max)
            midText = oldSelectedText + midText

            // let old selected text keep selected in new text field
            newSelectionRange = TextRange(start = newSelectionRange.start, end = newSelectionRange.end + oldSelectedText.length)
            if(oldSelection.start > oldSelection.end) {  // the range was reversed
                newSelectionRange = TextRange(start = newSelectionRange.end, end = newSelectionRange.start)
            }
        }


        // insert close pair into current position
        val textAddedClosedPair = StringBuilder().let {
            it.append(leftTextOfCursor)
            it.append(midText)
            // if start index is length of string, will not throw an exception, but returned empty string
            it.append(rightText)
            it.toString()
        }

        return newField.copy(value = newField.value.copy(text = textAddedClosedPair, selection = newSelectionRange))

    }

    private fun resolveClosedStringOfPair(text: String) : String? {
        // conflicts with single match char '<'
//        if(text.endsWith("</")) {
//            return ">"
//        }

        if(text.endsWith("/*")) {
            return "*/"
        }

        // conflicts with single match char '<'
//        if(text.endsWith("<!--")) {
//            return "-->"
//        }

        // conflicts with single match char '<'
//        if(text.endsWith("<![CDATA[")) {
//            return "]]>"
//        }

        return null
    }

    private fun resolveClosedCharOfPair(openedCharOfPair: Char) : String? {
        var closedPair = codeEditor?.myLang?.symbolPairs?.matchBestPairBySingleChar(openedCharOfPair)?.close
        if(closedPair == null) {
            closedPair = symbolPairsMap.get(openedCharOfPair.toString())
        }

        return closedPair
    }

    private fun updateStyles(
        howManyLinesNeedUpdate: Int,
        nextState: TextEditorState,
        act: (baseStyles: StylesResult) -> Unit
    ) {
        if(codeEditor.scopeInvalid()) {
            return
        }

        try {
            // if run a fully analyze is a better choice than incremental analyze, then run fully
            val halfLines = (fields.size / 2).coerceAtLeast(1)
            if(howManyLinesNeedUpdate >= halfLines
                || howManyLinesNeedUpdate > SettingsUtil.editorThresholdLinesCountOfIncrementAnalyze()
            ) {
                codeEditor?.analyze(nextState)
                return
            }


            // do incremental analyze

            val baseStyles = tryGetStylesResult()
            if(baseStyles == null) {
                MyLog.d(TAG, "#updateStyles: Styles of current field '$fieldsId' not found, maybe not exists or theme/languageScop are not matched, will re-run analyze after user stop input for a while")
                codeEditor?.startAnalyzeWhenUserStopInputForAWhile(nextState)
            }else {
                // even accept ours/theirs,
                // fields still is what we want,
                // because accept ours/theirs only
                // changed baseFields LineChangeType
                // for line number indicator color, the text value doesn't changed,
                // and due to the baseStyles is based fields, so, if we use another
                // fields which not matched with baseStyles, will cause an error
                act(baseStyles)
//            nextState.temporaryStyles = baseStyles

                // apply styles for next state
//            doJobThenOffLoading {
//                // onChange will call after updateStyles() finished, so here no need call it again
//                nextState.applySyntaxHighlighting(baseStyles, requireCallOnChange = false)
//            }
            }

        }catch (e: Exception) {
            MyLog.e(TAG, "#updateStyles err: ${e.stackTraceToString()}")
        }
    }


    private suspend fun appendOrReplaceFields(targetIndex: Int, text: String, trueAppendFalseReplace:Boolean) {
        if(!isGoodIndexForList(targetIndex, fields)) {
            MyLog.d(TAG, "#appendOrReplaceFields(): invalid index: $targetIndex, list.size=${fields.size}")
            return
        }

        // unselect all but new lines
        // 选中所有新行并取消选中所有非新行
        val updater = { newLinesRange: IntRange, newFields: MutableList<MyTextFieldState>, newSelectedIndices: MutableList<Int> ->
            newSelectedIndices.clear()

            for(i in newFields.indices) {
                if(!(((trueAppendFalseReplace && i <= newLinesRange.start) || (trueAppendFalseReplace.not() && i < newLinesRange.start)) || i > newLinesRange.endInclusive)) {
                    newSelectedIndices.add(i)
                }
            }
        }

        if(trueAppendFalseReplace) {
            splitNewLine(targetIndex, TextFieldValue(fields[targetIndex].value.text + lb + text), updater = updater)
        }else {
            if(text.contains(lb)) {
                splitNewLine(targetIndex, TextFieldValue(text), updater = updater)
            }else {
                updateField(
                    targetIndex = targetIndex,
                    textFieldValue = TextFieldValue(text),
                    updater = updater
                )
            }
        }
    }

    suspend fun deleteNewLine(targetIndex: Int) {
        lock.withLock {
            try {
                targetIndexValidOrThrow(targetIndex, fields.size)
            }catch (e: Exception) { // will throw when line dost, maybe changed by external or other cases, but it's fine, no need throw exception
                MyLog.d(TAG, "TextEditorState.deleteNewLine() err: ${e.stackTraceToString()}")
            }

            if (targetIndex <= 0) {
                return
            }

            val newFields = fields.toMutableList()

            val toLineIdx = targetIndex - 1
            val toField = newFields[toLineIdx]
            val fromField = newFields[targetIndex]
            val toText = toField.value.text
            val fromText = fromField.value.text

            val concatText = StringBuilder(toText.length + fromText.length).append(toText).append(fromText).toString()

            val concatSelection = TextRange(toText.length)  // end of `toText`
            val concatTextFieldValue = TextFieldValue(text = concatText, selection = concatSelection)
            val toTextFieldState = newFields[targetIndex - 1].copy(value = concatTextFieldValue).apply {
                val newChangeType = if(toText.isEmpty() && fromText.isNotEmpty()) {
                    fromField.changeType
                }else if(toText.isNotEmpty() && fromText.isEmpty()) {
                    toField.changeType
                }else if(toText.isEmpty() && fromText.isEmpty()) {
                    toField.changeType
                }else { // if(toText.isNotEmpty() && fromText.isNotEmpty())
                    if(toField.changeType == LineChangeType.NONE) LineChangeType.UPDATED else toField.changeType
                }

                updateLineChangeType(newChangeType)
            }

            newFields[targetIndex - 1] = toTextFieldState

            newFields.removeAt(targetIndex)

//            val newFocusingLineIdx = mutableStateOf(focusingLineIdx)
//            val newSelectedIndices = selectedIndices.toMutableList()
            val sfiRet = selectFieldInternal(
                init_fields = newFields,
                init_selectedIndices = selectedIndices,
//                out_focusingLineIdx = newFocusingLineIdx,
                isMutableFields = true,
                isMutableSelectedIndices = false,
//                init_focusingLineIdx = focusingLineIdx,
                targetIndex = targetIndex - 1
            )

            isContentEdited?.value=true
            editorPageIsContentSnapshoted?.value= false

            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = newId(),
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )



            updateStyles(2, newState) { baseStyles ->
                // delete current and previous lines
                updateStylesAfterDeletedLineBreak(fields, baseStyles, toLineIdx, ignoreThis = false, newState)
            }



            onChanged(newState, true, true, this, null)
        }
    }

    //第1个参数是行索引；第2个参数是当前行的哪个位置
    //若只传columnStartIndexInclusive，相当于定位到某一行的某一列；若传columnStartIndexInclusive 和 columnEndIndexExclusive，相当于选中某行的某一段文字
    suspend fun selectField(
        targetIndex: Int,
        option: SelectionOption = SelectionOption.NONE,
        columnStartIndexInclusive:Int=0,
        columnEndIndexExclusive:Int=columnStartIndexInclusive,
        requireSelectLine: Boolean=true, // if true, will add targetIndex into selected indices, set false if you don't want to select this line(e.g. when you just want go to line)
        requireLock: Boolean = true,
        forceAdd: Boolean = false,

        // if provide, will select field and update value to it
        provideTextFieldValue: TextFieldValue? = null,
    ) {
        val act = suspend {
//            val newFields = fields.toMutableList()
//            val newFocusingLineIdx = mutableStateOf(focusingLineIdx)
//            val newSelectedIndices = selectedIndices.toMutableList()
            val sfiRet = selectFieldInternal(
                init_fields = fields,
                init_selectedIndices = selectedIndices,
                isMutableFields = false,
                isMutableSelectedIndices = false,
//                out_focusingLineIdx = newFocusingLineIdx,
//                init_focusingLineIdx = focusingLineIdx,
                targetIndex = targetIndex,
                option = option,
                columnStartIndexInclusive=columnStartIndexInclusive,
                columnEndIndexExclusive=columnEndIndexExclusive,
                requireSelectLine = requireSelectLine,
                forceAdd = forceAdd,
                provideTextFieldValue = provideTextFieldValue,
            )

            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = fieldsId,  //选择某行，fields实际内容未改变，顶多影响光标位置或者选中字段之类的，所以不需要生成新fieldsId
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )

            onChanged(newState, null, false, this, null)
        }

        if(requireLock) {
            lock.withLock {
                act()
            }
        }else {
            act()
        }
    }

    //第1个参数是行索引；第2个参数是当前行的哪个位置
    suspend fun selectFieldSpan(targetIndex: Int) {
        lock.withLock {
            var sfiRet:SelectFieldInternalRet? = null

            if(selectedIndices.isEmpty()) {  //如果选中列表为空，仅选中当前行
                sfiRet = selectFieldInternal(
                    init_fields = fields,
                    init_selectedIndices = selectedIndices,
                    isMutableFields = false,
                    isMutableSelectedIndices = false,
//                    out_focusingLineIdx = newFocusingLineIdx,
//                    init_focusingLineIdx = focusingLineIdx,
                    targetIndex = targetIndex,
                    forceAdd = true
                )
            }else {  //选中列表不为空，执行区域选择
                val lastSelectedIndex = focusingLineIdx ?: selectedIndices.last()
                val startIndex = targetIndex.coerceAtMost(lastSelectedIndex)
                val endIndexInclusive = targetIndex.coerceAtLeast(lastSelectedIndex)
                val range = IntRange(startIndex, endIndexInclusive)
                if(range.isEmpty()) {
                    return
                }

//                val newFields = fields.toMutableList()
                val newSelectedIndices = selectedIndices.toMutableList()
                for(i in range) {
                    sfiRet = selectFieldInternal(
                        init_fields = fields,
                        init_selectedIndices = newSelectedIndices,
                        isMutableFields = false,
                        isMutableSelectedIndices = true,
//                        out_focusingLineIdx = newFocusingLineIdx,
//                        init_focusingLineIdx = focusingLineIdx,
                        targetIndex = i,
                        forceAdd = true
                    )
                }
            }

            val newState = internalCreate(
                fields = sfiRet?.fields ?: fields,
                fieldsId = fieldsId,
                selectedIndices = sfiRet?.selectedIndices ?: selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
//                focusingLineIdx = newFocusingLineIdx.value
                // don't use the ret focusingLineIdx at here, if current clicked is a
                //   small line number to former, then we will still focusing the greater index
//                focusingLineIdx = sfiRet?.focusingLineIdx ?: focusingLineIdx

                // make sure select last clicked line
                focusingLineIdx = targetIndex
            )

            onChanged(newState, null, false, this, null)
        }
    }

    suspend fun selectPrevOrNextField(
        isNext:Boolean,
        updateLastCursorAtColumn:(Int)->Unit,
        getLastCursorAtColumnValue:()->Int,
    ) {
        lock.withLock {
            // expect can move select prev/next line in selection mode, but maybe won't work
//            if (isMultipleSelectionMode) return

            val selectedIndex = focusingLineIdx ?: return

            // reached start or end, can't move any further
            if((isNext && selectedIndex >= fields.lastIndex)
                || (isNext.not() && selectedIndex <= 0)
            ) {
                return
            }

            val moveTargetIndex = if(isNext) selectedIndex + 1 else (selectedIndex - 1)

            val curField = fields.getOrNull(selectedIndex) ?: return

            // use max, because the end index of selection range maybe is not the max one
            // A thing I am not sure: when layout direction is RTL, maybe need use min instead of max? (not important, because most time navigate by Down/Up key without selection of text)
            val targetColumn = curField.value.selection.max.let {
                updateLastCursorAtColumn(it)
                getLastCursorAtColumnValue()
            }

            //更新状态
//            val newFields = fields.toMutableList()
//            val newSelectedIndices = selectedIndices.toMutableList()
//            val newFocusingLineIdx = mutableStateOf(focusingLineIdx)
            val sfiRet = selectFieldInternal(
                init_fields = fields,
                init_selectedIndices = selectedIndices,
                isMutableFields = false,
                isMutableSelectedIndices = false,
//                out_focusingLineIdx = newFocusingLineIdx,
//                init_focusingLineIdx = focusingLineIdx,
                targetIndex = moveTargetIndex,
                option = SelectionOption.CUSTOM,
                columnStartIndexInclusive = targetColumn
            )

            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = fieldsId,
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
//                focusingLineIdx = newFocusingLineIdx.value
                focusingLineIdx = sfiRet.focusingLineIdx
            )

            onChanged(newState, null, false, this, null)
        }
    }


    /**
     * 区域选中有bug，高亮也有bug，就算选中或高亮，后面会自动触发TextField OnValueChange，然后就丢失选中或样式了，不过写代码的时候如果需要选中区域或高亮区域，正常传参就行，这样以后修复bug就不需要再改了
     */
    private fun selectFieldInternal(
        init_fields:List<MyTextFieldState>,
        init_selectedIndices:List<Int>,
        isMutableFields:Boolean,
        isMutableSelectedIndices:Boolean,
//        init_focusingLineIdx:Int?,//废弃

        targetIndex: Int,
        option: SelectionOption = SelectionOption.NONE,
        forceAdd:Boolean=false,  // 为true：若没添加则添加，添加了则什么都不做；为false：已添加则移除，未添加则添加
        columnStartIndexInclusive:Int=0,
        columnEndIndexExclusive:Int=columnStartIndexInclusive,
        requireSelectLine:Boolean = true,
        provideTextFieldValue: TextFieldValue? = null,

    ):SelectFieldInternalRet {
        try {
            targetIndexValidOrThrow(targetIndex, init_fields.size)
        }catch (e: Exception) { // will throw when line dost, maybe changed by external or other cases, but it's fine, no need throw exception
            MyLog.d(TAG, "TextEditorState.selectFieldInternal() err: ${e.localizedMessage}")
        }

        val targetIndex = targetIndex.coerceAtMost(init_fields.lastIndex).coerceAtLeast(0)

        //后面会根据需要决定是否创建拷贝
        var ret_fields = init_fields
        var ret_selectedIndices = init_selectedIndices
        val ret_focusingLineIdx = targetIndex

        // avoid mistake using
        val out_fileds = Unit
        val out_selectedIndices = Unit
        val init_focusingLineIdx = Unit




        val targetInFieldList = ret_fields[targetIndex]


        val selection = provideTextFieldValue?.selection ?: run {
            val textLenOfTarget = targetInFieldList.value.text.length
            val columnStartIndexInclusive = columnStartIndexInclusive.coerceAtMost(textLenOfTarget).coerceAtLeast(0)
            val columnEndIndexExclusive = columnEndIndexExclusive.coerceAtMost(textLenOfTarget).coerceAtLeast(0)

            when (option) {
                SelectionOption.CUSTOM -> {
                    TextRange(columnStartIndexInclusive, columnEndIndexExclusive)
                }
                SelectionOption.NONE -> targetInFieldList.value.selection
                SelectionOption.FIRST_POSITION -> TextRange.Zero
                SelectionOption.LAST_POSITION -> {
                    TextRange(textLenOfTarget)
                }
            }
        }


//        val requireLossFocus = requireHighlighting  // 高亮关键字不聚焦是为了避免有的语言（例如英语）输入法，明明没编辑，也会触发onValueChange导致字段被更新，然后高亮闪一下消失，但不聚焦又不会弹出键盘和定位光标，用起来不方便，所以取消，如果想避免高亮关键字闪一下就消失，可开只读模式再搜索
//        val requireLossFocus = false  //定位光标到关键字后面，英语可能会闪烁一下就消失，具体应该取决于输入法是否自动读写光标范围内的输入

        //如果请求取消高亮非当前行，把非当前行的样式都更新下
//        val unFocusNonTarget = { mutableFields:MutableList<TextFieldState> ->
//            val ret_fields = Unit  // avoid mistake using
//
//            for(i in mutableFields.indices) {
//                if(i != targetIndex) {
//                    mutableFields[i] = mutableFields[i].let {
//                        //重新拷贝下text以取消高亮；重新拷贝下selection以取消选中。
//
//                        // （因为这里的选中范围并非像ide那样指示关键字被匹配到了，
//                        // 这里出现多个选中范围，仅仅是因为切换到下个匹配位置后没清上个而出现的“残留”，
//                        // 若不清，会对用户造成困扰，感觉上会像蓝色选中的字是匹配关键字但非当前聚焦，
//                        // 黄色高亮的是选中关键字且是当前聚焦条目，但实际并非如此）
//                        it.copy(value = it.value.copy(selection = TextRange.Zero, text = it.value.text))
//                    }
//                }
//            }
//
//        }


        if(isMultipleSelectionMode && requireSelectLine) {  //多选模式，添加选中行列表或从列表移除
//            ret_fields = if(isMutableFields) (ret_fields as MutableList) else ret_fields.toMutableList()
            ret_selectedIndices = if(isMutableSelectedIndices) (ret_selectedIndices as MutableList) else ret_selectedIndices.toMutableList()

            if(forceAdd) {  //强制添加
//                val notSelected = ret_fields[targetIndex].isSelected.not()
//                val notInSelectedIndices = ret_selectedIndices.contains(targetIndex).not()
                //未添加则添加，否则什么都不做

                if(!ret_selectedIndices.contains(targetIndex)) {
                    ret_selectedIndices.add(targetIndex)
                }

            }else {  //切换添加和移除
                // switch selected
                if (!ret_selectedIndices.remove(targetIndex)) {
                    ret_selectedIndices.add(targetIndex)
                }
            }

        }else {  //非多选模式，定位光标到对应行，并选中行，或者定位到同一行但选中不同范围，原本是想用来高亮查找的关键字的，但有点毛病，目前实现成仅将光标定位到关键字前面，但无法选中范围，不过我忘了现在定位到关键字前面是不是依靠的这段代码了
            //更新行选中范围并focus行

            // use provided value
            if(provideTextFieldValue != null) {
                if(provideTextFieldValue != targetInFieldList.value) {
                    ret_fields = if(isMutableFields) (ret_fields as MutableList) else ret_fields.toMutableList()
                    ret_fields[targetIndex] = targetInFieldList.copy(
                        value = provideTextFieldValue
                    )
                }
            }else { // use value in list, but update the selection range if needs
                ret_fields = if(isMutableFields) (ret_fields as MutableList) else ret_fields.toMutableList()
                val selectionRangeChanged = selection != targetInFieldList.value.selection
                if(selectionRangeChanged) {
                    ret_fields[targetIndex] = targetInFieldList.copy(
                        value = targetInFieldList.value.copy(selection = selection)
                    )
                }
            }
        }



        return SelectFieldInternalRet(
            fields = ret_fields,
            selectedIndices = ret_selectedIndices,
            focusingLineIdx = ret_focusingLineIdx

            //如果不传null，会自动弹键盘，会破坏请求高亮时设置的颜色，高亮颜色在弹出键盘后，闪下就没了
//            focusingLineIdx = ret_focusingLineIdx
        )
    }

    private fun splitTextsByNL(
        text: String,
        oldText: String,
    ): List<TextFieldValue> {
        val lines = text.lines()
        // if pressed enter, most have 2 lines, else, maybe is pasted content
        val maybeIsPaste = lines.size > 2 || (
            lines.get(0).let { first ->
                lines.get(1).let { second ->
                    first.length + second.length != oldText.length || first + second != oldText
                }
            }
        )

        if(AppModel.devModeOn) {
            MyLog.d(TAG, "maybeIsPaste=$maybeIsPaste")
        }

        val ret = mutableListOf<TextFieldValue>()
        val tabIndentSpaceCount = SettingsUtil.editorTabIndentCount()
        var autoIndentSpacesCount = ""

        for((index, text) in lines.withIndex()) {
            ret.add(
                if(maybeIsPaste) {  // if it is pasted content, doesn't add intent to avoid unexpected intents
                    TextFieldValue(text, TextRange(text.length))
                } else if (index == 0) {  //第一行，光标在换行的位置
                    autoIndentSpacesCount = getNextIndentByCurrentStr(text, tabIndentSpaceCount)
                    TextFieldValue(text, TextRange(text.length))
                } else {  //后续行，光标在开头
                    val currentLineIndentCount = getNextIndentByCurrentStr(text, tabIndentSpaceCount)
                    val diff = autoIndentSpacesCount.length - currentLineIndentCount.length

                    //如果首行缩进大于后续行，添加缩进使其和首行对齐，否则不添加。
                    //注：添加的是空格，但计算的时候其实是tab和空格一起算，所以如果两种缩进混用，
                    // 这里可能会添加错误的缩进，结果就会导致后续行和首行对不齐了，但数据不会丢
                    val text = if(diff > 0) {
                        val sb = StringBuilder()
                        for (i in 0 until diff) {
                            sb.append(IndentChar.SPACE.char)
                        }
                        sb.append(text).toString()
                    }else {
                        text
                    }

                    // 不用传selection，光标位置默认就是期望的0
                    TextFieldValue(text)
                }
            )
        }

        return ret
    }

    /**
     * 本函数可能会很费时间，所以加了suspend
     */
    suspend fun getKeywordCount(keyword: String): Int {
        val f = fields
        var count = 0

        f.forEachBetter {
            val text = it.value.text
            var startIndex=0

            while (true) {
                //text为空字符串或索引加过头都会在这返回
                if(!isGoodIndexForStr(startIndex, text)) {
                    break
                }

                //执行到这，索引一定good，所以可放心indexOf
                val indexOf = text.indexOf(string=keyword, startIndex=startIndex, ignoreCase=true)

                if(indexOf == -1) {  //未找到
                    break
                }else {  //如果找到了，更新计数，更新下次查找起点
//                    println("line=${idx+1}, column=$indexOf")  //debug

                    count++
                    startIndex = indexOf+keyword.length
                }
            }
        }

        return count
    }

    /**
     * @return Pair(chars count, lines count)
     */
    fun getCharsAndLinesCount(): Pair<Int, Int> {
        val f= fields
        val lines = f.size

        var chars = 0
        f.forEachBetter { chars+=it.value.text.length }

        return Pair(chars, lines)
    }

    /**
     * 从startIndex开始根据FindDirection查找，找到predicate返回true的行后，返回其索引和内容
     */
    fun indexAndValueOf(startIndex:Int, direction: FindDirection, predicate: (text:String) -> Boolean, includeStartIndex:Boolean): Pair<Int, String> {
        val list = fields
        var retPair = Pair(-1, "")

        try {
            val range = if(direction == FindDirection.UP) {
                val endIndex = if(includeStartIndex) startIndex else (startIndex-1)
                if(!isGoodIndexForList(endIndex, list)) {
                    throw RuntimeException("bad endIndex: $endIndex , list.size: ${list.size}")
                }

                (0..endIndex).reversed()
            }else {
                val tempStartIndex =if(includeStartIndex) startIndex else (startIndex+1)
                if(!isGoodIndexForList(tempStartIndex, list)) {
                    throw RuntimeException("bad tempStartIndex: $tempStartIndex , list.size: ${list.size}")
                }

                tempStartIndex..list.lastIndex
            }

            for(i in range) {
                val item = list[i]
                val text = item.value.text
                if(predicate(text)) {
                    retPair = Pair(i, text)
                    break
                }
            }

        }catch (e:Exception) {
            MyLog.d(TAG, "TextEditorState.indexAndValueOf() err: ${e.stackTraceToString()}")
        }

        return retPair

    }

    suspend fun deleteLineByIndices(
        indices:List<Int>,

        // null to use current fields
        baseFields:List<MyTextFieldState>? = null,

        // if deleteSelected, should be true, if is accept ours/theirs call this method, should be false,
        //   if not sure, use true is safe, will only deleted indices lines and clear selected indices;
        //   if use false, may selected unexpected lines after deleted lines
        // 删除内容可能导致索引移动，因此，建议设置此值为true，只要删除内容，就直接取消选中所有剩余的行，这样一般不会出错
        trueClearAllSelectedIndicesFalseOnlyClearWhichDeleted:Boolean = true,
    ) {
        if(indices.isEmpty()) {
            return
        }

        lock.withLock {
            val newFields = mutableListOf<MyTextFieldState>();
            (baseFields ?: fields).forEachIndexedBetter { index, field ->
                if(!indices.contains(index)) {
                    // set rest fields `isSelected` to false to avoid show wrong selected state
                    newFields.add(field)
                }
            }

            // update focusingLineIdx and create an empty line if need:
            // if deleted all lines, add an empty line
            // if was focusing a line, update focusing line index to ensure it still focusing a valid line
            var focusingLineIdx = focusingLineIdx
            if(newFields.isEmpty()) {
                newFields.add(MyTextFieldState())

                // if was null, no need update the focusing index
                if(focusingLineIdx != null) {
                    focusingLineIdx = 0
                }
            }else if(focusingLineIdx != null) {
                val lastDeletedIndex = indices.lastOrNull()
                val newLinesCoveredOldIndex = if (lastDeletedIndex != null && lastDeletedIndex < newFields.size) { // old index still valid
                    focusingLineIdx = lastDeletedIndex
                    true
                }else {  // old index invalid, means was deleted the last line, so, now should move to new last line of file
                    focusingLineIdx = newFields.lastIndex
                    false
                }

                // make sure cursor at first column of line
                newFields[focusingLineIdx] = newFields[focusingLineIdx].let {
                    it.copy(
                        // if new lines covered last deleted index, then move cursor to new line's first column, else move to last
                        value = it.value.copy(selection = if(newLinesCoveredOldIndex) TextRange.Zero else TextRange(it.value.text.length))
                    )
                }
            }


            isContentEdited?.value = true
            editorPageIsContentSnapshoted?.value = false

            val newState = internalCreate(
                fields = newFields,
                fieldsId = newId(),
                selectedIndices = if(trueClearAllSelectedIndicesFalseOnlyClearWhichDeleted) {
                    listOf()
                } else {
                    selectedIndices.filter { !indices.contains(it) }
                },
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = focusingLineIdx
            )

            // if deleted lines count more than rest of lines, then re-analyze syntax highlight may better than incremental update,
            // else deleted lines
            if(newFields.size <= indices.size) {
                codeEditor?.analyze(newState)
            }else {
                updateStyles(indices.size, newState) { baseStyles ->
                    // 降序删除不用算索引偏移
                    // delete current line
                    val indices = indices.sortedDescending()
                    val lastIdx = indices.size - 1
                    indices.forEachIndexed { idx, lineIdxWillDel ->
                        // 仅当删除最后一个条目时更新一次样式
                        updateStylesAfterDeleteLine(fields, baseStyles, lineIdxWillDel, ignoreThis = idx != lastIdx, newState, keepLine = false)
                    }

                }
            }

            onChanged(newState, true, true, this, null)
        }
    }


    /**
     * 注：若 targetIndex 为无效索引，初始不会选择任何行
     */
    suspend fun createMultipleSelectionModeState(targetIndex:Int) {
        lock.withLock {
            //用14431行的近2mb文件简单测试了下，性能还行
            //进入选择模式，数据不变，只是 MultipleSelectionMode 设为true且对应行的isSelected设为true

            //必须创建拷贝，只要调用synsState的地方都必须创建拷贝，不然会和源list冲突
//            val newFields = fields.toMutableList()
            val newSelectedIndices = mutableListOf<Int>()
            //若索引有效，选中对应行
            if(targetIndex >= 0 && targetIndex < fields.size) {
                newSelectedIndices.add(targetIndex)
            }

            val newState = internalCreate(
                fieldsId = fieldsId,  //这个似乎是用来判断是否需要将当前状态入撤销栈的，如果文件内容不变，不用更新此字段
                fields = fields,  //把当前点击而开启选择行模式的那行的选中状态设为真了
                selectedIndices = newSelectedIndices,  //默认选中的索引包含当前选中行即可，因为肯定是点击某一行开启选中模式的，所以只会有一个索引
                isMultipleSelectionMode = true,
                focusingLineIdx = targetIndex

            )

            onChanged(newState, null, false, this, null)
        }

    }


//    suspend fun createCopiedState() {
//        lock.withLock {
//            val newState = create(
//                fieldsId = fieldsId,
//                fields = fields.toList(),
//                selectedIndices = selectedIndices,
//                isMultipleSelectionMode = isMultipleSelectionMode,  //拷贝和删除不要退出选择模式(准确来说是不要改变是否处于选择模式，若以后以非多选模式创建CopiedState，也不会自动进入选择模式)，这样用户可间接实现剪切功能，因为选择很多行有时候很废力，所以除非用户明确按取消，否则不要自动解除选择模式
//                focusingLineIdx = focusingLineIdx
//
//            )
//
//            onChanged(newState, null, false)
//        }
//
//    }


    suspend fun createSelectAllState() {
        lock.withLock {
            val selectedIndexList = fields.indices.toList()

            //（更新：浅的）我不太确定 data类的copy是深还是浅，但我可以确定这里不需要深拷贝，所以用下面创建浅拷贝的方法创建对象
            val newState = internalCreate(
                fieldsId = fieldsId,
                fields = fields,
                selectedIndices = selectedIndexList,
                isMultipleSelectionMode = true,
                focusingLineIdx = focusingLineIdx

            )

            onChanged(newState, null, false, this, null)
        }
    }

    suspend fun deleteSelectedLines() {
        if(selectedIndices.isEmpty()) {
            return
        }

        deleteLineByIndices(selectedIndices, trueClearAllSelectedIndicesFalseOnlyClearWhichDeleted = true)
    }



    /**
     * 清空选中行的内容
     */
    suspend fun clearSelectedFields(){
        if(selectedIndices.isEmpty()) {
            return
        }

        lock.withLock {
            //清空已选中行
            val newFields = fields.mapIndexed { index, field ->
                if(selectedIndices.contains(index)) {
                    field.copy(value = field.value.copy(text = ""))
                }else {
                    field
                }
            }


            //即使全删了，完全创建新状态，也不要影响选择模式，要不然有的情况自动退选择模式，有的不退，容易让人感到混乱
            val newState = internalCreate(
                fieldsId = newId(),
                fields = newFields,
                selectedIndices = selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,  //一般来说此值在这会是true，不过，这里的语义是“不修改是否选择模式”，所以把这个字段传过去比直接设为true要合适
                focusingLineIdx = focusingLineIdx
            )

            isContentEdited?.value = true
            editorPageIsContentSnapshoted?.value = false

            updateStyles(selectedIndices.size, newState) { baseStyles  ->
                val selectedIndices = selectedIndices.sortedDescending()
                // delete current line
                val lastIdx = selectedIndices.size - 1
                //必须倒序，不然文本在全文中的索引会计算错误，除非真模拟删数据，但那样还得拷贝fields，浪费内存
                selectedIndices.forEachIndexed { idx, lineIdxWillDel ->
                    // 仅当删除最后一个条目时更新一次样式
                    updateStylesAfterDeleteLine(fields, baseStyles, lineIdxWillDel, ignoreThis = idx != lastIdx, newState)
                }

            }


            onChanged(newState, true, true, this, null)
        }
    }

    suspend fun quitSelectionMode(keepSelectionModeOn:Boolean = false){
        lock.withLock {
            val newState = internalCreate(
                fieldsId = fieldsId,
                fields = fields,
                selectedIndices = emptyList(),
                isMultipleSelectionMode = keepSelectionModeOn,
                focusingLineIdx = focusingLineIdx

            )

            onChanged(newState, null, false, this, null)
        }
    }

    fun clearSelectedItemList(){
        runBlocking { quitSelectionMode(keepSelectionModeOn = true) }
    }

    /**
     * 计算行索引对应的以像素为单位大概滚动位置。
     * 注：只能算个大概，如果有图片之类的，文字可能只占一行，但预览可能占很多行，这时滚动可能会有较大偏差
     */
    fun lineIdxToPx(lineIndex: Int, fontSizeInPx:Float, screenWidthInPx:Float, screenHeightInPx:Float):Float {
        // 好，最简单的情况，直接在开头，不用换算
        if(lineIndex == 0) {
            return  0f
        }

        var count = 0

        val lineHeight = UIHelper.guessLineHeight(fontSizeInPx)
        val (oneLineHowManyChars, luckyOffset) = getOneLineHowManyCharsAndLuckyOffset(lineHeight = lineHeight, screenWidthInPx = screenWidthInPx, screenHeightInPx = screenHeightInPx, indexToPx = true)
        var targetPx = luckyOffset

        val iterator = fields.iterator()
        while(iterator.hasNext()) {
            if(count++ > lineIndex) {
                break
            }

            val it = iterator.next()

            val lineChars = it.value.text.length
            //软换行行数 (soft-wrap lines)
            val softLineCount = (lineChars / oneLineHowManyChars).coerceAtLeast(1)
            //行数 乘 字体大小，粗略得到当前物理行占用多少像素
            targetPx += (softLineCount * lineHeight)
        }

        return targetPx
    }


    /**
     * 计算索引对应的大概行索引。
     * 注：只能算个大概，如果有图片之类的，文字可能只占一行，但预览可能占很多行，这时滚动可能会有较大偏差
     */
    fun pxToLineIdx(targetPx: Int, fontSizeInPx:Float, screenWidthInPx:Float, screenHeightInPx:Float):Int {
        // 好，最简单的情况，直接在开头，不用换算
        if(targetPx == 0) {
            return  0
        }

        val lineHeight = UIHelper.guessLineHeight(fontSizeInPx)
        val (oneLineHowManyChars, luckyOffset) = getOneLineHowManyCharsAndLuckyOffset(lineHeight = lineHeight, screenWidthInPx = screenWidthInPx, screenHeightInPx = screenHeightInPx, indexToPx = false)

        var pos = luckyOffset

        var targetLineIndex = 0
        val iterator = fields.iterator()
        while (iterator.hasNext()) {
            if(pos >= targetPx) {
                break
            }

            targetLineIndex++

            val it = iterator.next()

            val lineChars = it.value.text.length
            //软换行行数 (soft-wrap lines)
            val softLineCount = (lineChars / oneLineHowManyChars).coerceAtLeast(1)
            //行数 乘 字体大小，粗略得到当前物理行占用多少像素
            pos += (softLineCount * lineHeight)
        }

        return targetLineIndex
    }

    private fun getOneLineHowManyCharsAndLuckyOffset(lineHeight: Float, screenWidthInPx: Float, screenHeightInPx: Float, indexToPx:Boolean): Pair<Int, Float> {
        val oneLineHowManyChars = (screenWidthInPx / lineHeight).toInt()
        val luckyOffset = UIHelper.getLuckyOffset(indexToPx = indexToPx, screenWidthInPx = screenWidthInPx, screenHeightInPx = screenHeightInPx)
        return Pair(oneLineHowManyChars, luckyOffset)
    }

    fun focusingLineIndexInvalid() = focusingLineIdx == null || focusingLineIdx < 0 || focusingLineIdx >= fields.size

    /**
     * 若forceAppend为假：如果当前行为空行，用text替换当前行；否则追加到当前行后面。
     * 若forceAppend为真：强制追加，无论当前行是否为空行。
     *
     * targetIndex: `focusingLineIdx` 有效则用它，无效则用 `selectedIndices` 最后一个条目，两个都无效则直接返回
     *
     *
     * If `forceAppend` is false: If the current line is empty, replace the current line with text; otherwise, append text to the end of the current line.
     * If `forceAppend` is true: Force append, regardless of whether the current line is empty or not.
     *
     * targetIndex: will use `focusingLineIdx` if it is valid, or last item of `selectedIndices`, if both are invalid, will return
     *
     *
     */
    suspend fun appendTextToLastSelectedLine(
        text: String,
        forceAppend: Boolean = true,
        afterAppendThenDoAct: ((targetIndex: Int) -> Unit)? = null
    ) {
        //若取消注释这个，复制空行再粘贴会作废，除非在复制那里处理下，ifEmpty返回个空行，
        // 但是不如在这处理，直接忽略这个条件，
        // 然后用空字符串调用.lines()会得到一个空行，就符合复制一个空行粘贴一个空行的需求了，
        // 别的地方也省得改了
//        if(text.isEmpty()) {
//            return
//        }

        //因为要粘贴到选中行后边，所以必须得有选中条目
        val selectedIndices = selectedIndices
        val focusedLineInvalid = focusingLineIndexInvalid()
        if(focusedLineInvalid && selectedIndices.isEmpty()) {
            return
        }

        //selectedIndices里存的是fields的索引，若是无效索引，直接返回
        val lastSelectedIndexOfLine = if(focusedLineInvalid) selectedIndices.last() else (focusingLineIdx!!)
        if(lastSelectedIndexOfLine < 0 || lastSelectedIndexOfLine >= fields.size) {
            MyLog.w(TAG, "#appendTextToLastSelectedLine: invalid index '$lastSelectedIndexOfLine' of `fields`")
            return
        }


        //空字符串会拆分出一个空行，一个"\n"会拆分出两个空行
//        val lines = text.lines()

        //目标若是空行则replace，否则append
        val trueAppendFalseReplace = if(forceAppend) true else fields[lastSelectedIndexOfLine].value.text.isNotEmpty()

        //执行添加，到这才真正修改Editor的数据
        appendOrReplaceFields(targetIndex = lastSelectedIndexOfLine, text = text, trueAppendFalseReplace = trueAppendFalseReplace)

        afterAppendThenDoAct?.invoke(lastSelectedIndexOfLine)
    }

    fun getContentOfLineIndex(lineIndex: Int): String {
        val f = fields
        return if(lineIndex >= 0 && lineIndex < f.size) {
            f[lineIndex].value.text
        }else {
            ""
        }
    }


    private fun lbForDump() = codeEditor?.lineBreak?.value ?: lb;

    /**
     * note: if you want to save lines to file, recommend to use `dumpLines` instead
     *
     * WARN: Don't cache this function's returned value, because:
     *   1.(the main reason) this function at most call twice,
     *        one for syntax highlighting, one for saving, but if cache it, it will cache for every instance,
     *        usually, it will duplicate `n * undoStack.size`, but after syntax highlighting finished,
     *        we only need another 1 copy for saving, so other copies just waste memory.
     *   2.(this just so so) usually users edit a small file, so `getAllText()` will run very fast,
     *        so no need cache the returned value
     *   (第1个理由是主要原因，第2个其实有点牵强，总之，主要还是浪费内存，所以不建议缓存)
     *
     *
     */
    fun getAllText(): String {
        val sb = StringBuilder()
        val lineBreak = lbForDump()

        fields.forEachBetter { sb.append(it.value.text).append(lineBreak) }

        //移除遍历时多添加的末尾换行符，然后返回
        return sb.removeSuffix(lineBreak).toString()

        //below code very slow when file over 1MB，主要原因是字符串拼接，次要是隐含多次循环
//        return fields.map { it.value.text }.foldIndexed("") { index, acc, s ->
//            if (index == 0) acc + s else acc + lb + s
//        }
    }

    fun dumpLinesAndGetRet(output: OutputStream): Ret<Unit?> {
        try {
            dumpLines(output)
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            return Ret.createError(null, e.localizedMessage ?: "dump lines err", exception = e)
        }
    }

    fun dumpLines(output: OutputStream) {
        val lineBreak = lbForDump()

        val fieldsSize = fields.size
        var count = 0
        val charsetName = codeEditor?.editorCharset?.value

        // add bom if need
        EncodingUtil.addBomIfNeed(output, charsetName)

        output.bufferedWriter(EncodingUtil.resolveCharset(charsetName)).use { bw ->
            for(f in fields) {
                bw.write(f.value.text)

                // if is not the last line of the file,
                //   append a line break
                if(++count != fieldsSize) {
                    bw.write(lineBreak)
                }
            }
        }
    }

    fun getSelectedText(indices: List<Int>? = null, keepEndLineBreak: Boolean = false): String {
        // 把索引排序，然后取出文本，拼接，返回
        val sb = StringBuilder()
        (indices ?: selectedIndices).toSortedSet().forEach { selectedLineIndex->
            doActIfIndexGood(selectedLineIndex, fields) { field ->
                sb.append(field.value.text).append(lb)
            }
        }

        return if(keepEndLineBreak) sb.toString() else sb.removeSuffix(lb).toString()
    }

    //获取选择行记数（获取选择了多少行）
    fun getSelectedCount():Int{
//        return selectedIndices.toSet().filter{ it>=0 }.size  //toSet()是为了去重，我不确定是否一定没重复，去下保险；filter {it>=0} 是为了避免里面有-1，我记得初始值好像是往selectedIndices里塞个-1。
        return selectedIndices.size  //toSet()是为了去重，我不确定是否一定没重复，去下保险；filter {it>=0} 是为了避免里面有-1，我记得初始值好像是往selectedIndices里塞个-1。
    }

    fun contentIsEmpty(): Boolean {
        return fields.isEmpty() || (fields.size == 1 && fields[0].value.text.isEmpty())
    }

    /**
     * 此函数不比较fields数组，缺点是返回结果并不准确(真或假都不靠谱)，优点是快
     * 用来粗略判断当前状态是否需要入撤销栈（undoStack）
     */
    fun maybeNotEquals(other: TextEditorState):Boolean {
//      don't use `this.fields !== other.fields` instead of `fieldsId` compare,
        //      because maybe fields value no change, but the selection range changed,
        //      so the fields will have a new copy, but the text still are the same
//        return this.fieldsId != other.fieldsId || this.isMultipleSelectionMode != other.isMultipleSelectionMode || this.selectedIndices != other.selectedIndices
        return this.fieldsId != other.fieldsId
    }

    private fun internalCreate(
        fields: List<MyTextFieldState>,
        fieldsId: String,
        selectedIndices: List<Int>,
        isMultipleSelectionMode: Boolean,
        focusingLineIdx: Int?,
    ): TextEditorState {
//        if(temporaryStyles != null) {
//            MyLog.d(TAG, "temporaryStyles is not null")
//        }

        return TextEditorState(
            fieldsId= fieldsId,
            fields = fields,
            selectedIndices = selectedIndices,
            isMultipleSelectionMode = isMultipleSelectionMode,
            focusingLineIdx = focusingLineIdx,

            //这几个变量是共享的，一般不用改
            codeEditor = codeEditor,
            isContentEdited = isContentEdited,
            editorPageIsContentSnapshoted = editorPageIsContentSnapshoted,
            onChanged = onChanged,

            // temp styles will update after styles updated, so here just set to null
//            temporaryStyles = null,
        )
    }


    suspend fun goToEndOrTopOfFile(goToTop:Boolean) {
        lock.withLock {
            val targetIndex = if(goToTop) 0 else fields.lastIndex.coerceAtLeast(0)

            val sfiRet = selectFieldInternal(
                init_fields = fields,
                init_selectedIndices = selectedIndices,
                isMutableFields = false,
                isMutableSelectedIndices = false,
                targetIndex = targetIndex,
                option = if(goToTop) SelectionOption.FIRST_POSITION else SelectionOption.LAST_POSITION,
            )

            val newState = internalCreate(
                fields = sfiRet.fields,
                fieldsId = fieldsId,
                selectedIndices = sfiRet.selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = sfiRet.focusingLineIdx
            )

            onChanged(newState, null, false, this, null)
        }
    }

    suspend fun setChangeTypeToFields(
        indices: List<Int>,
        changeType: LineChangeType,
        baseFields:List<MyTextFieldState>?,
        applyNewSate:Boolean,
    ):List<MyTextFieldState>? {
        if(indices.isEmpty()) return null;

        lock.withLock {
            val newFields = (baseFields ?: fields).toMutableList()
            indices.forEachBetter forEach@{ idx ->
                val textField = newFields.getOrNull(idx) ?: return@forEach;
                newFields[idx] = textField.copy(changeType = changeType)
            }

            if(applyNewSate) {
                val newState = internalCreate(
                    fields = newFields,
                    fieldsId = fieldsId,
                    selectedIndices = selectedIndices,
                    isMultipleSelectionMode = isMultipleSelectionMode,
                    focusingLineIdx = focusingLineIdx
                )

                onChanged(newState, null, false, this, null)
            }

            return newFields
        }
    }

    fun getFieldByIdx(idx: Int?) = Pair(idx, idx?.let { fields.getOrNull(idx) })

    // return current index and filed, both are maybe null
    fun getCurrentField() = getFieldByIdx(focusingLineIdx)

    /**
     * @return true means handled, false otherwise
     */
    suspend fun handleTabIndent(idx:Int, f:MyTextFieldState, tabIndentSpacesCount:Int, trueTabFalseShiftTab:Boolean):Boolean {
        return lock.withLock {
            val handled =  try {
                val handleTabRet = if(trueTabFalseShiftTab) {
                    doTab(tabIndentSpacesCount,  f)
                }else {
                    doShiftTab(tabIndentSpacesCount, f)
                }

                if(handleTabRet.changed) {
                    updateField(
                        targetIndex = idx,
                        textFieldValue = f.value.copy(
                            text = handleTabRet.newText,
                            selection = handleTabRet.newSelection
                        ),
                        requireLock = false
                    )

                    true
                }else {
                    false
                }
            }catch (e: Exception) {
                MyLog.e(TAG, "$TAG#handleTabIndent err: replace ${if(trueTabFalseShiftTab) "TAB" else "SHIFT+TAB"} to indent spaces err: ${e.stackTraceToString()}")
                false
            }

            // avoid press tab make text field loss focus
            if(!handled) {
                selectField(idx, requireLock = false)
            }

            true
        }
    }

    private fun doTab(tabIndentSpacesCount: Int, f: MyTextFieldState): HandleTabRet {
        val fv = f.value
//        val cursorAt = if (fv.selection.collapsed) fv.selection.start else fv.selection.min
        // if select anything, move the whole line
        val cursorAt = if (fv.selection.collapsed) fv.selection.start else 0
        val sb = StringBuilder(fv.text.substring(0, cursorAt))
        val newText = sb.append(tabToSpaces(tabIndentSpacesCount)).append(fv.text.substring(cursorAt, fv.text.length)).toString()
        //at least 1char, because even set tabIndentSpacesCount to less than 0, it still will insert a real tab
        val cursorOffset = tabIndentSpacesCount.coerceAtLeast(1)
        val newSelection = if (fv.selection.collapsed) TextRange(cursorAt + cursorOffset)
        else TextRange(start = fv.selection.start + cursorOffset, end = fv.selection.end + cursorOffset)

        return HandleTabRet(newText, newSelection, true)
    }

    /**
     * @return new text and new selection for `fields[idx]`
     *
     * the outdent behavior just like jet brains ide
     */
    private fun doShiftTab(
        tabIndentSpacesCount: Int,
        f: MyTextFieldState
    ): HandleTabRet {
        val fv = f.value
        // if empty or not starts with tab or space, return
        if(fv.text.let { it.isEmpty() || (it.startsWith(IndentChar.SPACE.char).not() && it.startsWith(IndentChar.TAB.char).not()) }) {
            return HandleTabRet(newText = fv.text, newSelection = fv.selection, changed = false)
        }

        // when reached here, must starts with at least 1 tab or space

        var useSubString = false
        var leftSub = ""
        val text = if(fv.selection.collapsed && fv.selection.start.let { it > 0 && it < fv.text.length }) {
            leftSub = fv.text.substring(0, fv.selection.start)
            val rightSub = fv.text.substring(fv.selection.start)
            rightSub.let {
                // here should use isBlank() rather than isEmpty()
                if(leftSub.isNotBlank() || it.isBlank() || (it.startsWith(IndentChar.SPACE.char).not() && it.startsWith(IndentChar.TAB.char).not())) {
                    useSubString = false
                    fv.text
                } else {
                    useSubString = true
                    it
                }
            }
        } else {
            useSubString = false
            fv.text
        }

        // remove till non-space char
        val (newTextTmp, removedCount) = if (tabIndentSpacesCount < 1 || text.startsWith(IndentChar.TAB.char)) {  // remove a tab or a space
            // if `tabIndentSpacesCount < 1`, then it means use a real tab instead of few spaces,
            // in logically, we should remove a tab at here,
            // but, in practically, we expect remove a space or a tab after pressed shift+tab
            Pair(text.substring(1, text.length), 1)
        } else {  // remove spaces
            var removed = 0
            for (i in text) {
                if (i == IndentChar.SPACE.char) {
                    if (++removed >= tabIndentSpacesCount) {
                        break
                    }
                } else {
                    break
                }
            }

            Pair(if (removed == 0) text else text.substring(removed, text.length), removed)
        }

        val newText = if(useSubString) {
            StringBuilder().apply {
                append(leftSub)
                append(newTextTmp)
            }.toString()
        }else {
            newTextTmp
        }


        return if (removedCount < 1) {
            HandleTabRet(newText = newText, newSelection = fv.selection, changed = false)
        } else {
            val newSelection = if (fv.selection.collapsed) {
                if(useSubString) {
                    // if use sub string, we operate the sub string which on the right side of the text, so the cursor haven't moved
                    TextRange(fv.selection.start)
                } else {
                    TextRange((fv.selection.start - removedCount).coerceAtLeast(0))
                }
            } else {
                TextRange(start = (fv.selection.start - removedCount).coerceAtLeast(0), end = (fv.selection.end - removedCount).coerceAtLeast(0))
            }

            HandleTabRet(newText, newSelection, changed = true)
        }
    }

    // this called when selection mode on
    suspend fun indentLines(
        tabIndentSpacesCount: Int,
        targetIndices:List<Int>,
        trueTabFalseShiftTab: Boolean,
    ) {
        lock.withLock {
            val fields = fields
            val newFields = mutableListOf<MyTextFieldState>()
//            val targetIndices = targetIndices.toMutableList()
            val targetIndices = targetIndices

            fields.forEachIndexedBetter { i, f ->
                val newF = if(targetIndices.contains(i)) {
                    // 批量缩进，从行首开始
                    // batch indents, always start at line column index 0
                    val f = f.copy(value = f.value.copy(selection = TextRange(0)))

                    val handleTabRet = if(trueTabFalseShiftTab) {
                        doTab(tabIndentSpacesCount, f)
                    }else {
                        doShiftTab(tabIndentSpacesCount, f)
                    }

                    f.copy(value = f.value.copy(text = handleTabRet.newText, selection = handleTabRet.newSelection))
                }else {
                    f
                }

                newFields.add(newF)
            }


            val newState = internalCreate(
                fields = newFields,
                fieldsId = newId(),
                selectedIndices = selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = focusingLineIdx
            )

            //通知页面状态变化
            isContentEdited?.value = true
            editorPageIsContentSnapshoted?.value = false

            updateStyles(targetIndices.size, newState) { baseStyles ->
                val targetIndices = targetIndices.sortedDescending()
                val lastIdx = targetIndices.size - 1
                targetIndices.forEachIndexed { idx, targetIndex ->
                    // delete current line
                    updateStylesAfterDeleteLine(fields, baseStyles, targetIndex, ignoreThis = true, newState)

                    // add new content to current line
                    updateStylesAfterInsertLine(
                        baseFields = fields,
                        stylesResult = baseStyles,
                        ignoreThis = idx != lastIdx,
                        insertedContent = newState.fields.get(targetIndex).value.text,
                        insertedContentRangeInNewFields = IntRange(targetIndex, targetIndex),
                        newTextEditorState = newState
                    )
                }
            }

            onChanged(newState, true, true, this, null)
        }
    }

    suspend fun applySyntaxHighlighting(stylesResult: StylesResult, requireCallOnChange: Boolean = true) {
        val funName = "applySyntaxHighlighting"

        val spansLineCount = stylesResult.styles.spans.lineCount
        MyLog.d(TAG, "#$funName: StylesResult may will be apply: $stylesResult, spans.LineCount=$spansLineCount, fields.size=${fields.size}")

        // will not be null
        if(codeEditor == null) {
            MyLog.w(TAG, "#$funName: codeEditor is null, will not apply styles")

            return
        }


        // already checked before apply, no more check needed at here
//        if(spansLineCount != fields.size) {
//            MyLog.w(TAG, "#$funName: will not apply stylesResult, because spans.lineCount doesn't match the fields.size: spans.lineCount=$spansLineCount, fields.size=${fields.size}")
//            // will re-run analyze in the onChanged, nothing need to do at here
//            return
//        }

        val expectedFieldsId = stylesResult.fieldsId

        if(expectedFieldsId.isBlank()) {
            return
        }

        val styles = stylesResult.styles
        val inDarkTheme = stylesResult.inDarkTheme
        val isForThisInstance = expectedFieldsId == fieldsId && inDarkTheme == Theme.inDarkTheme
        //避免应用其他实例的样式
        if(!isForThisInstance) {
            return
        }

        // already applied
//        if(stylesResult.from == StylesResultFrom.CODE_EDITOR && stylesResult.uniqueId == temporaryStyles?.uniqueId) {
//            MyLog.d(TAG, "already applied styles (from code editor), uniqueId=${stylesResult.uniqueId}")
//            return
//        }

        // only styles sent from code editor can override bundled styles
        // 只有来自code editor的styles能覆盖临时styles字段，反之不能，如果应用临时styles，应在外部调用此方法的地方决定是否应用temporaryStyles
//        if(stylesResult.from == StylesResultFrom.CODE_EDITOR) {
//            temporaryStyles = stylesResult.copyWithDeepCopyStyles()
//        }


        // 比锁轻量且能保证几乎没问题，够用了
        if(!stylesResult.applied.compareAndSet(false, true)) {
            MyLog.d(TAG, "styles already applied, uniqueId=${stylesResult.uniqueId}")
            return
        }



        lock.withLock {
//                创建一个TextFieldState的highlighting cache map，根据field syntax highlight id把样式存上，
//                遍历text field时，根据其id取annotated string，
//                若无，使用原text field，若有，使用缓存的带语法高亮的text field

            // if has err, just return, then will not save the styles map, so UI will show a normal text without syntax highlighting
            // 如果出错，让它抛，不用存shMap，省内存，页面显示普通文本，没毛病
            val syntaxHighlightStorage = mutableMapOf<String, AnnotatedString>()
            try {
                val spansReader = styles.spans.read()
                fields.forEachIndexedBetter { idx, value ->
                    val spans = spansReader.getSpansOnLine(idx)
                    val annotatedString = codeEditor.generateAnnotatedStringForLine(value, spans).let {
                        codeEditor.ifSameReturnCachedAnnotatedString(value.syntaxHighlightId, it)
                    }

                    syntaxHighlightStorage.put(
                        value.syntaxHighlightId,
                        annotatedString
                    )
                }
            }catch (e: Exception) {
                MyLog.e(TAG, "#$funName: apply styles err: fieldsId=$fieldsId, stylesResult=$stylesResult, err=${e.localizedMessage}")
                e.printStackTrace()

                return
            }

            // save or update styles for current's fieldsId,
            //    usually is save, but if do analyze for same
            //    fields more than once, then it becomes update
            codeEditor?.putSyntaxHighlight(fieldsId, SyntaxHighlightResult(inDarkTheme = inDarkTheme, storage = syntaxHighlightStorage))

        }

        // must free this instance's lock before try apply style, else will dead lock
        if(requireCallOnChange) {
            // in this case, first param `nextState` even is  `this`, but it will not be applied, will use copied latest state as new state
            codeEditor?.doActWithLatestEditorState("#applySyntaxHighlighting") { latestState ->
                if(latestState.fieldsId == fieldsId) {
                    MyLog.d(TAG, "will call `onChanged()` for fieldsId: $fieldsId")

                    // copy for refresh view
                    onChanged(latestState.copy(), null, false, this, EditorStateOnChangeCallerFrom.APPLY_SYNTAX_HIGHLIGHTING)
                }else {
                    MyLog.d(TAG, "editor state already changed, ignore style update request by previous style's apply syntax highlighting method, newStateFieldsId=${latestState.fieldsId}, currentAppliedFieldsId=${fieldsId}")
                }
            }
        }
    }




    fun tryGetStylesResult(): StylesResult? {
        // if scope is invalid, will not use any styles
        // 如果 scope无效，代表用户选择了NONE或无效语法高亮语言，这时不应用任何语法高亮，即使已经存在也不用
        if(codeEditor.scopeInvalid()) {
            return null
        }

        // have a little bug (its acceptable, no fix still ok): if users clicked undo/redo, then change content,
        // here will still return the expired latestStyles and do last once afterDelete/afterInsert,
        // even it doesn't make sense, because a new full analyze will run in soon after
        return codeEditor?.getLatestStylesResultIfMatch(this)

//        val cachedStyles = codeEditor?.stylesMap?.get(fieldsId)
//        return if(isGoodStyles(cachedStyles)) {
//            cachedStyles
//        } else {
//            null
//        }

//        return temporaryStyles.let { tmpStyle ->
//            if(isGoodStyles(tmpStyle)) {
//                tmpStyle
//            }else {
//                val cachedStyles = codeEditor?.stylesMap?.get(fieldsId)
//                if(isGoodStyles(cachedStyles)) {
//                    cachedStyles
//                } else {
//                    null
//                }
//            }
//        }
    }

    fun obtainHighlightedTextField(raw: MyTextFieldState): MyTextFieldState {
        if(codeEditor.scopeInvalid()) {
            return raw
        }

        return codeEditor?.obtainSyntaxHighlight(fieldsId, raw) ?: raw
    }


    // note: this was a copy method, but then changed behavior, this method maybe remove in the future
//    fun copyStyles(nextState: TextEditorState): StylesResult? = tryGetStylesResult()



    // only remove line break
    private fun updateStylesAfterDeletedLineBreak(
        baseFields: List<MyTextFieldState>,
        stylesResult: StylesResult,
        startLineIndex: Int,
        ignoreThis: Boolean,
        newTextEditorState: TextEditorState
    ) {
        val funName = "updateStylesAfterDeletedLineBreak"
        MyLog.d(TAG, "$funName: $stylesResult")


        // 如果删完了，就重新执行分析
        if(reAnalyzeBetterThanIncremental(newTextEditorState.fields, ignoreThis)) {
            codeEditor?.analyze(newTextEditorState)
            return
        }


        // 删除某行逻辑是从上一行的末尾到这一行的末尾
        val endLineIndexInclusive = startLineIndex + 1
        if(!isGoodIndexForList(startLineIndex, baseFields) || !isGoodIndexForList(endLineIndexInclusive, baseFields)) {
            MyLog.d(TAG, "$funName: bad index: start=$startLineIndex, end=$endLineIndexInclusive, list.size=${baseFields.size}")
            return
        }

        val startIdxOfText = getIndexOfText(baseFields, startLineIndex, trueStartFalseEnd = false)
        val endIdxOfText = startIdxOfText + 1
        if(startIdxOfText == -1 || endIdxOfText == -1) {
            return
        }

        val start = CharPosition(startLineIndex, baseFields[startLineIndex].value.text.length, startIdxOfText)
        val end = CharPosition(endLineIndexInclusive, 0, endIdxOfText)


        // even same, still need call adjustAfterDelete for received a new styles to re-render the view
//        if(start == end) {
//            MyLog.w(TAG, "#$funName: start == end: $start, nothing need to delete")
//            return
//        }
//
//        val isDelLastLine = endLineIndexInclusive == baseFields.lastIndex


        MyLog.d(TAG, "#$funName: start=$start, end=$end, baseFields.size=${baseFields.size}, newState.fields.size=${newTextEditorState.fields.size}, spansCount=${stylesResult.styles.spans.lineCount}")

//        println("baseFields[insertIndex].value.text: ${baseFields.getOrNull(end.line)?.value?.text}")
//        MyLog.d(TAG, "#$funName: will delete range: start=$start, end=$end")

        // style will update spans
        stylesResult.styles.adjustOnDelete(start, end)
        MyLog.d(TAG, "#$funName: adjusted on delete, spans.lineCount = ${stylesResult.styles.spans.lineCount}, baseFields.size = ${baseFields.size}")


        val selectedText = lb
        val lang = codeEditor?.myLang
        if(lang != null) {
            codeEditor.sendUpdateStylesRequest(
                StylesUpdateRequest(
                    ignoreThis = ignoreThis,
                    targetEditorState = newTextEditorState,
                    act = { lang.analyzeManager.delete(start, end, selectedText, it) }
                )
            )
        }

    }


    // 增删内容需要调用 spans的after change，然后调用lang.analyzeManager()的insert/ delete重新执行分析
    //  其中需要用到 char position，有3个字段，line为行索引，column为列索引，均为0开始，index为文本在全文中的索引
    //  如果删除多行，需要把行排序，然后逐行删除，每删一行把后面的索引减1（倒序删除就不需要减1了）
    //删除一行：上行length到这行length，不考虑 \n
    //添加一行：和删除一样
    //index是包含\n的
    // if delete lines, use descend order to avoid calculate index wrong,
    //   explain: delete index 8 will not affect index before 8, so need descend, else the delete logic must change
    private fun updateStylesAfterDeleteLine(
        baseFields: List<MyTextFieldState>,
        stylesResult: StylesResult,
        startLineIndex: Int,
        ignoreThis: Boolean,
        newTextEditorState: TextEditorState,

        // clear content but keep line (\n)
        keepLine: Boolean = true,
    ) {
        val funName = "updateStylesAfterDeleteLine"
        MyLog.d(TAG, "$funName: $stylesResult")

        // 如果删完了，就重新执行分析
        if(reAnalyzeBetterThanIncremental(newTextEditorState.fields, ignoreThis)) {
            codeEditor?.analyze(newTextEditorState)
            return
        }


        // 删除某行逻辑是从上一行的末尾到这一行的末尾
        val startLineIndex = if(keepLine) startLineIndex else (startLineIndex - 1).coerceAtLeast(0)
        val endLineIndex = if(keepLine) startLineIndex else (startLineIndex + 1).coerceAtMost(baseFields.lastIndex)
        MyLog.d(TAG, "$funName: startLineIndex: $startLineIndex, endLineIndex=$endLineIndex, list.size=${baseFields.size}")
        if(!isGoodIndexForList(startLineIndex, baseFields) || !isGoodIndexForList(endLineIndex, baseFields)) {
            MyLog.d(TAG, "$funName: bad range: startLineIndex: $startLineIndex, endLineIndex=$endLineIndex, list.size=${baseFields.size}")
            return
        }

        val startIdxOfText = getIndexOfText(baseFields, startLineIndex, trueStartFalseEnd = keepLine)
        val endIdxOfText = getIndexOfText(baseFields, endLineIndex, trueStartFalseEnd = false)
        if(startIdxOfText == -1 || endIdxOfText == -1) {
            return
        }

        val start = CharPosition(startLineIndex, if(keepLine) 0 else baseFields[startLineIndex].value.text.length, startIdxOfText)
        val end = CharPosition(endLineIndex, baseFields[endLineIndex].value.text.length, endIdxOfText)


//        if(start == end) {
//            MyLog.w(TAG, "#$funName: start == end: $start, nothing need to delete")
//            return
//        }


        // the fields in the baseFields will
        // not use as next state,
        // it just emulate real action
        // for later condition check
        val deletedContent = if(keepLine) {  // only clear field but keep the line
            baseFields.get(startLineIndex).value.text
        } else {  // remove the line
            baseFields.get(endLineIndex).value.text + lb
        }

        // baseFields size should - 1 after deleted
        MyLog.d(TAG, "#$funName: start=$start, end=$end, baseFields.size=${baseFields.size - 1}, newState.fields.size=${newTextEditorState.fields.size}, spansCount=${stylesResult.styles.spans.lineCount}")


//        println("baseFields[insertIndex].value.text: ${baseFields.getOrNull(end.line)?.value?.text}")
//        MyLog.d(TAG, "#$funName: will delete range: start=$start, end=$end")

        // style will update spans
        stylesResult.styles.adjustOnDelete(start, end)
        MyLog.d(TAG, "#$funName: adjusted on delete, spans.lineCount = ${stylesResult.styles.spans.lineCount}, baseFields.size = ${baseFields.size - 1}")


        val lang = codeEditor?.myLang
        if(lang != null) {
            codeEditor.sendUpdateStylesRequest(
                StylesUpdateRequest(
                    ignoreThis = ignoreThis,
                    targetEditorState = newTextEditorState,
                    act = { lang.analyzeManager.delete(start, end, deletedContent, it) }
                )
            )
        }

    }

    private fun updateStylesAfterInsertLine(
        baseFields: List<MyTextFieldState>,
        stylesResult: StylesResult,
        ignoreThis: Boolean,
        insertedContent: String,
        insertedContentRangeInNewFields: IntRange,
        newTextEditorState: TextEditorState,
    ) {
        val funName = "updateStylesAfterInsertLine"
        val startLineIndex = insertedContentRangeInNewFields.start
        MyLog.d(TAG, "$funName: startLineIndex=$startLineIndex, baseFields.size=${baseFields.size}, $stylesResult")

        if(reAnalyzeBetterThanIncremental(newTextEditorState.fields, ignoreThis)) {
            codeEditor?.analyze(newTextEditorState)
            return
        }

        //start line must valid
        if(startLineIndex >= baseFields.size) {
            MyLog.d(TAG, "$funName: startLineIndex '$startLineIndex' is invalid, baseFields.size=${baseFields.size}")
            return
        }


        // the insert position is start of the line, and the line should clear by `afterDelete` before insert,
        //   else the content for analyze will not match with the real content
        val startIdxOfText = getIndexOfText(baseFields, startLineIndex, trueStartFalseEnd = true)
        if(startIdxOfText < 0) {
            MyLog.w(TAG, "`startIndexOfText` invalid: $startIdxOfText")
            return
        }



        val start = CharPosition(startLineIndex, 0, startIdxOfText)

        // end index in the new fields
        // 插入内容的最后一行在新fields中的索引
        val insertIndex = insertedContentRangeInNewFields.endInclusive
        val end = CharPosition(insertIndex, newTextEditorState.fields.get(insertIndex).value.text.length, startIdxOfText + insertedContent.length)



        MyLog.d(TAG, "#$funName: start=$start, end=$end, baseFields.size=${baseFields.size}, newState.fields.size=${newTextEditorState.fields.size}, spansCount=${stylesResult.styles.spans.lineCount}")


//        if(start == end) {
//            MyLog.w(TAG, "#$funName: start == end: $start, nothing need to insert")
//            return
//        }

        // style will update spans
        stylesResult.styles.adjustOnInsert(start, end)
        MyLog.d(TAG, "#$funName: adjusted on insert, spans.lineCount = ${stylesResult.styles.spans.lineCount}, baseFields.size = ${baseFields.size}")

        val selectedText = insertedContent
        val lang = codeEditor?.myLang
        if(lang != null) {
            codeEditor.sendUpdateStylesRequest(
                StylesUpdateRequest(
                    ignoreThis = ignoreThis,
                    targetEditorState = newTextEditorState,
                    act = {
                        lang.analyzeManager.insert(start, end, selectedText, it)
                    }
                )
            )
        }
    }

    private fun reAnalyzeBetterThanIncremental(baseFields: List<MyTextFieldState>, ignoreThis: Boolean) : Boolean {
        return ignoreThis.not() && (baseFields.isEmpty() || (baseFields.size == 1 && baseFields[0].value.text.isBlank()))
    }

    // lineIdx is index(since 0), not line number(since 1)
    fun getIndexOfText(
        baseFields: List<MyTextFieldState>,
        lineIdx: Int,
        trueStartFalseEnd: Boolean,
    ):Int {
        if(lineIdx < 0 || baseFields.isEmpty()) {
            return 0
        }

        if(lineIdx >= baseFields.size) {
            // + 1 for line break, - 1 for remove line break for last line
            return baseFields.sumOf { it.value.text.length + 1 } - 1
        }

        // a line's start is previous line's end, so - 1 if find start index of line
        val lineIdx = if(trueStartFalseEnd) lineIdx - 1 else lineIdx
        var li = -1
        var charIndex = 0

        while (++li <= lineIdx) {
            val f = baseFields.getOrNull(li) ?: return -1
            // +1 for '\n'
            charIndex += (f.value.text.length + 1)
        }

        // due to find to line's start will - 1, so if find index of start, the lineIdx range is [0, lastIndexOfFields - 1],
        //  so, if the lineIdx == lastIndexOfFields is true, it must find end of the line, hence, should - 1 for remove last line break count for the last line
        return charIndex + (if(lineIdx == baseFields.lastIndex) -1 else 0)
    }

    suspend fun moveCursor(
        trueToLeftFalseRight: Boolean,
        textFieldState: MyTextFieldState,
        targetFieldIndex: Int,
        headOrTail: Boolean
    ) {
        if(isMultipleSelectionMode) {
            return
        }

        if(targetFieldIndex < 0 || targetFieldIndex >= fields.size) {
            return
        }

        val textRange = textFieldState.value.selection
        if(textRange.collapsed.not()) {
            return
        }

        val atLineHead = textRange.start == 0
        val atLineTail = textRange.start == textFieldState.value.text.length
        val atFileHead = targetFieldIndex == 0
        val atFileTail = targetFieldIndex == fields.lastIndex

        if(trueToLeftFalseRight && ((headOrTail && atLineHead) || (!headOrTail && atLineHead && atFileHead))) {
            return
        }

        if(!trueToLeftFalseRight && ((headOrTail && atLineTail) || (!headOrTail && atLineTail && atFileTail))) {
            return
        }

        if(atLineHead && trueToLeftFalseRight) {
            selectField(
                targetIndex = targetFieldIndex - 1,
                option = SelectionOption.LAST_POSITION
            )
            return
        }


        if(atLineTail && !trueToLeftFalseRight) {
            selectField(
                targetIndex = targetFieldIndex + 1,
                option = SelectionOption.FIRST_POSITION
            )
            return
        }




        // move cursor at `idx` line
        lock.withLock {
            val newTextRange = if(trueToLeftFalseRight) {
                if(headOrTail) TextRange(0) else TextRange((textRange.start-1).coerceAtLeast(0))
            }else {
                if(headOrTail) TextRange(textFieldState.value.text.length) else TextRange((textRange.start+1).coerceAtMost(textFieldState.value.text.length))
            }

            val newFields = fields.toMutableList()
            newFields[targetFieldIndex] = newFields[targetFieldIndex].copy(value = textFieldState.value.copy(selection = newTextRange))

            val newState = internalCreate(
                fields = newFields,
                fieldsId = fieldsId,
                selectedIndices = selectedIndices,
                isMultipleSelectionMode = isMultipleSelectionMode,
                focusingLineIdx = focusingLineIdx
            )

            onChanged(newState, null, false, this, null)

        }
    }


    /**
     * if selected all, replace; else append to the last selected line
     * after pasted, will select pasted content
     */
    suspend fun paste(
        text: String,
        afterReplacedAllThenDoAct: ((newFields: List<MyTextFieldState>) -> Unit)? = null
    ) {
        if(isSelectedAllFields()) {  // replace all
            lock.withLock {
                val newFields = textToFields(text)
                val newSelectedIndices = newFields.indices.toList()
                val newState = copy(
                    fieldsId = newId(),
                    fields = newFields,
                    selectedIndices = newSelectedIndices,

                    // focus last line of pasted content
                    focusingLineIdx = newFields.lastIndex
                )

                //更新状态变量
                isContentEdited?.value=true
                editorPageIsContentSnapshoted?.value=false
                EditCache.writeToFile(text)

                // update state
                onChanged(newState, true, true, this, null)

                // analyze syntax highlighting
                codeEditor?.startAnalyzeWhenUserStopInputForAWhile(newState)

                afterReplacedAllThenDoAct?.invoke(newFields)
            }
        }else {  // append to last selected line
            appendTextToLastSelectedLine(text)
        }
    }

    fun isSelectedAllFields() = isMultipleSelectionMode && selectedIndices.size == fields.size

    fun isFieldSelected(idx: Int) = selectedIndices.contains(idx)

    suspend fun selectFieldValue(
        targetIndex: Int,
        textFieldValue: TextFieldValue,
    ) {
        selectField(targetIndex, provideTextFieldValue = textFieldValue)
    }

    // haven't changed the content, just mark as unsaved.
    // it is useful in case of change encoding or line break
    suspend fun asUnsaved() {
        lock.withLock {
            //更新状态变量
            isContentEdited?.value=true
            editorPageIsContentSnapshoted?.value=false

            //create new fieldsId for enable save button, but needn't update the undo stack because content no change
            onChanged(copy(fieldsId = newId()), null, false, this, null)
        }
    }

    companion object {
        fun linesToFields(lines: List<String>) = createInitTextFieldStates(lines)

        fun fuckSafFileToFields(file: FuckSafFile, charsetName: String?) = linesToFields(FsUtils.readLinesFromFile(file, charsetName, addNewLineIfFileEmpty = true))

        fun textToFields(text: String) = linesToFields(text.lines())


        fun newId():String {
            return FieldsId.generateString()
        }

    }
}


private fun createInitTextFieldStates(list: List<String>) = if(list.isEmpty()) {
    listOf(MyTextFieldState())
}else {
    list.map { s ->
        MyTextFieldState(
            value = TextFieldValue(s),
        )
    }
}

data class FieldsId(
    val id: String = generateRandomString(20),
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        val EMPTY = FieldsId("", 0L)

        fun generateString():String {
            return FieldsId().toString()
        }


        fun parse(fieldsId: String?):FieldsId {
            if(fieldsId == null) {
                return EMPTY
            }

            return fieldsId.split(":").let {
                FieldsId(
                    id = it.get(0),
                    timestamp = it.getOrNull(1)?.let { parseLongOrDefault(it, null) } ?: 0L
                )
            }
        }
    }

    override fun toString(): String {
        return "$id:$timestamp"
    }
}


enum class SelectionOption {
    FIRST_POSITION,
    LAST_POSITION,
    NONE,
    CUSTOM, // should provide start and end index when use this value
}

enum class FindDirection {
    UP,
    DOWN
}

// return type of `selectFieldInternal`
private class SelectFieldInternalRet(
    val fields: List<MyTextFieldState>,
    val selectedIndices: List<Int>,
    val focusingLineIdx:Int?,
)

private data class HandleTabRet(
    val newText:String,
    val newSelection: TextRange,
    val changed: Boolean
)

enum class EditorStateOnChangeCallerFrom {
    // if onChanged called by a normal action(e.g. user input),
    // the param will be null, so here don't need a NONE type yet
//    NONE,

    APPLY_SYNTAX_HIGHLIGHTING,
}

val symbolPairsMap = mapOf<String, String>(

    "\"" to "\"",
    "{" to "}",
    "(" to ")",
    "[" to "]",
    "\'" to "\'",
    "<" to ">",

    // sometime expect ```, so disable it
//    "`" to "`",


    // chinese
    "“" to "”",
    "（" to "）",
    "《" to "》",
    "〈" to "〉",
    "‘" to "’",
    "【" to "】",
    "『" to "』",
    "「" to "」",
    "［" to "］",
)
