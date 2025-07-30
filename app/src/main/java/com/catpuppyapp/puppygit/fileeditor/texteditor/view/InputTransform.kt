package com.catpuppyapp.puppygit.fileeditor.texteditor.view

import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.fileeditor.util.EditorUtil
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor

class InputTransform {
    fun transform(codeEditor: MyCodeEditor?, oldField: TextFieldValue, newField: TextFieldValue) {
        if(SettingsUtil.isEditorAutoCloseSymbolPairEnabled()) {
            EditorUtil.appendClosePairIfNeed(codeEditor, oldField, newField)
        }else {
            newField
        }
    }
}
