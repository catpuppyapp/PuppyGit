package com.catpuppyapp.puppygit.fileeditor.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.fileeditor.cons.EditorCons
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.utils.MyLog

object EditorUtil {
    private const val TAG = "EditorUtil"

    fun appendClosePairIfNeed(
        codeEditor: MyCodeEditor?,
        oldField: TextFieldValue,
        newField: TextFieldValue,
    ) : TextFieldValue {
        return try {
            appendClosePairIfNeedNoCatch(codeEditor, oldField, newField)
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
    fun appendClosePairIfNeedNoCatch(
        codeEditor: MyCodeEditor?,
        oldField: TextFieldValue,
        newField: TextFieldValue,
    ) : TextFieldValue {
        val newText = newField.text
        if(newText.isEmpty()) {
            return newField
        }

        // if user deleting or paste text, don't add closed symbol
        val oldText = oldField.text
        val oldSelection = oldField.selection
        // if length equals, maybe is select then paste, in that case, return
        // if selection is not collapse, maybe select then type open pair,
        //    e.g. select "abc", then input ", should not return if is that case
        if(newText.length <= oldText.length && oldSelection.collapsed) {
            return newField
        }

        // make sure only add one symbol
//        if((newText.length - oldText.length) != 1) {
//            return newField
//        }

        val newSelection = newField.selection
        val cursorAt = newSelection.start

        //if `start` is 0, the left of cursor is nothing, so return
        if(!newSelection.collapsed || cursorAt <= 0) {
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
            val openedCharOfPair = newText.get(cursorAt - 1)
            closedCharOfPair = resolveClosedCharOfPair(codeEditor, openedCharOfPair)

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

        return newField.copy(text = textAddedClosedPair, selection = newSelectionRange)

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

    private fun resolveClosedCharOfPair(codeEditor: MyCodeEditor?, openedCharOfPair: Char) : String? {
        var closedPair = codeEditor?.myLang?.symbolPairs?.matchBestPairBySingleChar(openedCharOfPair)?.close
        if(closedPair == null) {
            closedPair = EditorCons.symbolPairsMap.get(openedCharOfPair.toString())
        }

        return closedPair
    }

}
