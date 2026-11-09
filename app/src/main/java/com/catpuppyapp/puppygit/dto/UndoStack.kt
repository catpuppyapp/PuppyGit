package com.catpuppyapp.puppygit.dto

import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.getSecFromTime
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList

private const val TAG = "UndoStack"

// small size, big interval = less mem use but less steps
// big size, small interval = more mem use but more steps
// size越小，间隔越大，越省内存记录的步骤越粗略;
// size越大，间隔越小，越费内存，记录的步骤越精细
private const val defaultSizeLimit = 12
private const val defaultSaveIntervalInSec = 60

class UndoStack(
    /**
    用来标记这是哪个文件的stack
     */
    var filePath:String,
    /**
     * 记多少步
     */
    var sizeLimit: Int = defaultSizeLimit,

    /**
     * 保存间隔，秒数，为0则不限只要状态变化就立即存一版
     */
    var undoSaveIntervalInSec:Int = defaultSaveIntervalInSec,

    /**
     * utc秒数，上次保存时间，用来和时间间隔配合实现在几秒内只保存一次，若为0，无视时间间隔，立即存一版本，然后更新时间为当前秒数
     */
    var undoLastSaveAt: Long = 0L,


    private var undoStack:LinkedList<TextEditorState> = LinkedList(),
    private var redoStack:LinkedList<TextEditorState> = LinkedList(),
    private var undoLock: Mutex = Mutex(),
    private var redoLock: Mutex = Mutex(),
    var codeEditor: MyCodeEditor? = null,
) {

    suspend fun reset(filePath:String, force:Boolean, cleanUnusedStyles: Boolean = true) {
        if(cleanUnusedStyles) {
            this.cleanUnusedStyles()
        }

        if(force.not() && filePath == this.filePath) {
            return
        }

        this.filePath = filePath
        sizeLimit = defaultSizeLimit
        undoSaveIntervalInSec = defaultSaveIntervalInSec
        undoLastSaveAt = 0L
        undoStack = LinkedList()
        redoStack = LinkedList()
        undoLock = Mutex()
        redoLock = Mutex()
    }

    private suspend fun cleanUnusedStyles() {
        codeEditor?.let { codeEditor ->
            if(codeEditor.stylesMap.isNotEmpty()) {
                undoLock.withLock {
                    redoLock.withLock {
                        // don't read state in for-each, if have many iterations, may cause performance issue
                        // 如果循环过多，读取state可能导致性能问题，比存到变量再读可能差2倍
                        val latestEditorStateFieldsId = codeEditor.editorState?.value?.fieldsId
                        for ((_, v) in codeEditor.stylesMap) {
                            // clean if undo/redo stacks doesn't contains fieldsId and the fieldsId neither equals to latest editor state fieldsId
                            if(!containsNoLock(v.fieldsId) && v.fieldsId != latestEditorStateFieldsId) {
                                codeEditor.cleanStylesByFieldsId(v.fieldsId)
                            }
                        }
                    }
                }
            }
        }
    }

//    fun copyFrom(other:UndoStack) {
//        codeEditor = other.codeEditor
//        filePath = other.filePath
//        sizeLimit = other.sizeLimit
//        undoSaveIntervalInSec = other.undoSaveIntervalInSec
//        undoLastSaveAt = other.undoLastSaveAt
//        undoStack = other.undoStack
//        redoStack = other.redoStack
//        undoLock = other.undoLock
//        redoLock = other.redoLock
//    }

    fun undoStackIsEmpty():Boolean {
        return undoStack.isEmpty()
    }

    fun redoStackIsEmpty():Boolean {
        return redoStack.isEmpty()
    }

    fun undoStackSize():Int {
        return undoStack.size
    }

    fun redoStackSize():Int {
        return redoStack.size
    }

    /**
     * @return true saved, false not saved
     */
    suspend fun undoStackPush(state: TextEditorState, force: Boolean = false) {
        undoLock.withLock {
            redoLock.withLock {
                undoStackPushNoLock(state, force)
            }
        }
    }

    private fun undoStackPushNoLock(state: TextEditorState, force: Boolean = false):Boolean {
//        val headState = peek(undoStack)
        // first time save or switched multi selection mode, save without interval check
//        val selectModeChanged = headState.isMultipleSelectionMode != state.isMultipleSelectionMode

        val now = getSecFromTime()

        //在时间间隔内只存一版
        // force || disabled save interval || first time push || over the save interval
        if(force || undoStackIsEmpty() || undoSaveIntervalInSec == 0 || undoLastSaveAt == 0L || (now - undoLastSaveAt) > undoSaveIntervalInSec) {
            push(undoStack, state)
            undoLastSaveAt = now

            //若超过数量限制移除第一个
            if(undoStack.size.let { it > 0 && it > sizeLimit }) {
                val lastHead = undoStack.removeAt(0)

                // remove styles if fieldsId Unused
                if(lastHead.fieldsId != state.fieldsId
                    && lastHead.fieldsId != codeEditor?.editorState?.value?.fieldsId
                    && !containsNoLock(lastHead.fieldsId)
                ) {
                    codeEditor?.cleanStylesByFieldsId(lastHead.fieldsId)
                }
            }

            return true
        }

        // not into stack, clear it's styles
        if(state.fieldsId != codeEditor?.editorState?.value?.fieldsId
            && !containsNoLock(state.fieldsId)
        ) {
            codeEditor?.cleanStylesByFieldsId(state.fieldsId)
        }

        return false
    }

    suspend fun undoStackPop(): TextEditorState? {
        return undoLock.withLock {
            undoStackPopNoLock()
        }
    }

    private fun undoStackPopNoLock(): TextEditorState? {
        return pop(undoStack)
    }

    /**
     * @return true saved, false not saved
     */
    suspend fun redoStackPush(state: TextEditorState):Boolean {
        redoLock.withLock {
            push(redoStack, state)
            return true
        }
    }

    suspend fun redoStackPop(): TextEditorState? {
        redoLock.withLock {
            undoLock.withLock {
                //为使弹出的状态可立刻被undo stack存上，所以将上次存储时间清0
                undoLastSaveAt = 0
            }

//            remainOnceRedoStackCount()

//            val last = pop(redoStack)
//            redoLastPop.value = last
//            return last

            // 这里只需pop redoStack，不需push undoStack，
            // editor state会在执行pop后触发一次onChanged，
            // 会在其中执行push undoStack，配合上面的存储时间清0，就可立刻将pop的redo状态入undo栈
            return pop(redoStack)
        }
    }

    suspend fun redoStackClear() {
        redoLock.withLock {
            codeEditor?.let { codeEditor ->
                undoLock.withLock {
                    val latestStateFieldsId = codeEditor.editorState?.value?.fieldsId
                    for (i in redoStack) {
                        if(i.fieldsId != latestStateFieldsId && !undoContainsNoLock(i.fieldsId)) {
                            codeEditor.cleanStylesByFieldsId(i.fieldsId)
                        }
                    }
                }
            }

            redoStack.clear()
        }
    }

    private fun push(stack: MutableList<TextEditorState>, state: TextEditorState) {
        try {
            // add to tail
            stack.add(state)
        }catch (e:Exception) {
            MyLog.e(TAG, "#push, err: ${e.stackTraceToString()}")
        }
    }

//    @Deprecated("弃用，感觉不需要做太多判断，应该调用者自己判断是否需要入栈")
//    private fun pushDeprecated(stack: MutableList<TextEditorState>, state: TextEditorState) {
//        try {
//            //第2个判断是个粗略判断，避免在只有一个条目且内容相等时重复添加内容到栈
//            if(stack.isEmpty() || !(state.fields.size==1 && stack.last().fields.size == 1 && state.fields.first()==stack.last().fields.first()) || stack.last().fieldsId != state.fieldsId) {
////            if(stack.isEmpty() || state.fields != peek(stack)?.fields) {
//                stack.add(state)
//
//                if(stack.size > sizeLimit) {
//                    stack.removeAt(0)
//                }
//            }
//        }catch (e:Exception) {
//            MyLog.e(TAG, "#push, err: ${e.stackTraceToString()}")
//        }
//    }

    private fun pop(stack: MutableList<TextEditorState>): TextEditorState? {
        return try {
            stack.removeAt(stack.size - 1)
        }catch (e:Exception) {
            null
        }
    }

    private fun peek(stack: MutableList<TextEditorState>): TextEditorState? {
        return try {
            stack.get(stack.size - 1)
        }catch (e:Exception) {
            null
        }
    }

    suspend fun updateUndoHeadIfNeed(latestState: TextEditorState) {
        if(undoStack.isEmpty()) {
            return
        }

        undoStackPopThenPush(latestState)
    }

    private suspend fun undoStackPopThenPush(state: TextEditorState) {
        undoLock.withLock {
            undoStackPopNoLock()
            undoStackPushNoLock(state)
        }
    }

    suspend fun contains(fieldsId: String):Boolean {
        return undoLock.withLock {
            redoLock.withLock {
                containsNoLock(fieldsId)
            }
        }
    }

    private fun containsNoLock(fieldsId: String):Boolean {
        return undoContainsNoLock(fieldsId) || redoContainsNoLock(fieldsId)
    }

    private fun undoContainsNoLock(fieldsId: String) : Boolean {
        return undoStack.find { it.fieldsId == fieldsId } != null
    }

    private fun redoContainsNoLock(fieldsId:String) : Boolean {
        return redoStack.find { it.fieldsId == fieldsId } != null
    }

    suspend fun clear() {
        undoLock.withLock {
            undoStack.clear()
        }
        redoLock.withLock {
            redoStack.clear()
        }
    }

    suspend fun clearRedoStackThenPushToUndoStack(state: TextEditorState, force: Boolean) {
        // order is important, clear stack first can have better performance,
        //   because it will reduce contains check spent time when push
        // 顺序很重要，先清stack再push可有更好的性能，因为在push时会进行contains检测来决定是否清除state关联的styles
        redoStackClear()
        undoStackPush(state, force)
    }

    fun makeSureNextChangeMustSave() {
        undoLastSaveAt = 0
    }

}
