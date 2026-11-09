package com.catpuppyapp.puppygit.syntaxhighlight.codeeditor

import com.catpuppyapp.puppygit.fileeditor.texteditor.state.FieldsId
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.syntaxhighlight.base.MyStyleReceiver
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import io.github.rosemoe.sora.lang.styling.Styles
import kotlinx.coroutines.runBlocking

private const val TAG = "MyEditorStyleReceiver"

class MyEditorStyleReceiver(
    val codeEditor: MyCodeEditor,
    val inDarkTheme: Boolean,
    val stylesMap: MutableMap<String, StylesResult>,
    val editorState: TextEditorState?,
    val languageScope: PLScope,
): MyStyleReceiver(TAG,  codeEditor.myLang?.analyzeManager) {

    override fun handleStyles(styles: Styles) {
        if(editorState == null || editorState.fieldsId.isBlank()) {
            return
        }

        // 如果当前这个类使用的editorState不等于状态变量的state的fieldsId，
        //    又不在undo/redo stack里，
        //    并且，其时间戳早于editor state状态变量的时间戳，就在这里直接返回
        // 这个检测可节省内存，但同时可能导致性能问题，若有性能问题，可尝试禁用，对app影响不大，就是多费点内存
        // this check can save memory, but may cause performance issue, if happened, disabled it is ok, just maybe will use more memories
        val latestFieldsId = codeEditor.editorState.value.fieldsId
        val carriedFieldsId = editorState.fieldsId

        if(AppModel.devModeOn) {
            MyLog.i(TAG, "latestFieldsId: $latestFieldsId")
            MyLog.i(TAG, "carriedFieldsId: $carriedFieldsId")
        }

        val isUnusedFieldsId = (latestFieldsId != carriedFieldsId
                // 这个条件有可能不准，导致存上几个无效状态，例如：正在分支状态2的styles，
                //   然后执行undo，最新状态回退到状态1，然后状态2分析完毕，调用此方法，
                //   执行到这里，发现其时间戳大于状态1的，这时这个判断就会失效
                // this check may invalid after undo did, but not big problem, just will save few invalid styles
                && FieldsId.parse(latestFieldsId).timestamp > FieldsId.parse(carriedFieldsId).timestamp
                && runBlocking { codeEditor.undoStack.value.contains(carriedFieldsId).not()}
        )

        if(isUnusedFieldsId) {
            if(AppModel.devModeOn) {
                MyLog.i(TAG, "will drop unused styles for fieldsId: $carriedFieldsId")
            }

            return
        }



//        (resolved) 不行，有问题：
//        lang + editorState1 调用分析，未结束时，lang + editorState2 重新设置了receiver，然后 组合1的结果出来了，就被组合2拿到了，而组合2无法得知组合1 到底是谁
//        啊，这个问题其实可以解决，因为这里无论如何都会put，所以我可以在获取某个字段的annotatedString的时候检查，如果有style没高亮，执行apply，就行了

        // cache只有一个实例，需要并发安全，key为fieldsId，全局唯一
        val stylesResult = StylesResult(inDarkTheme, styles, StylesResultFrom.CODE_EDITOR, fieldsId = editorState.fieldsId, languageScope = languageScope)
        if(codeEditor.isGoodStyles(stylesResult, editorState).not()) {
            MyLog.i(TAG, "`stylesResult` doesn't match with editor state: stylesResult=$stylesResult, styles.spans.lineCount=${styles.spans.lineCount}, editorState.fields.size=${editorState.fields.size}")
            return
        }

        codeEditor.latestStyles = stylesResult
        val copiedStyles = stylesResult.copyWithDeepCopyStyles()
        stylesMap.put(editorState.fieldsId, copiedStyles)

        if(AppModel.devModeOn) {
            MyLog.i(TAG, "will apply styles for fieldsId: ${editorState.fieldsId}")
        }

        doJobThenOffLoading {
            // apply copied for editor state to avoid styles changed when applying
            editorState.applySyntaxHighlighting(copiedStyles)
        }
    }
}
