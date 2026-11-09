package com.catpuppyapp.puppygit.settings.util

import androidx.compose.runtime.MutableState
import com.catpuppyapp.puppygit.settings.SettingsUtil

object EditorSettingsUtil {

    fun updateDisableSoftKb(newValue:Boolean, state: MutableState<Boolean>?) {
        state?.value = newValue

        SettingsUtil.update {
            it.editor.disableSoftwareKeyboard = newValue
        }
    }
}
