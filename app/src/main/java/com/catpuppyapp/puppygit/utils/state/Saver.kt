package com.catpuppyapp.puppygit.utils.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * LazyColumn has a bug, if use rememberSaveable in it, may cause an err,
 * but custom saver can avoid it, so, this class created
 *
 * the bug: https://issuetracker.google.com/issues/181880855
 */
object Saver {
    val STRING = Saver<String, String>(
        save = { it },
        restore = { it }
    )

    @Composable
    fun rememberSaveableString(init: ()->String):String {
        return rememberSaveable(saver = STRING, init = init)
    }

}
