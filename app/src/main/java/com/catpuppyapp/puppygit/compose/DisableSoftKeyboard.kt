package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.InterceptPlatformTextInput
import kotlinx.coroutines.awaitCancellation

/**
 * A function that disables the soft keyboard for any text field within its content.
 *
 * The keyboard is re-enabled by removing this modifier or passing `disable = false`.
 *
 * source: https://stackoverflow.com/a/79064874
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisableSoftKeyboard(
    disable: Boolean,
    content: @Composable () -> Unit
) {
    InterceptPlatformTextInput(
        interceptor = { request, nextHandler ->
            // If this flag is changed while an input session is active, a new lambda instance
            // that captures the new value will be passed to InterceptPlatformTextInput, which
            // will automatically cancel the session upstream and restart it with this new
            // interceptor.
            if (disable) {
                // This function has to return Nothing, and since we don't have any work to do
                // in this case, we just suspend until cancelled.
                awaitCancellation()
            } else {
                // Forward the request to the system.
                nextHandler.startInputMethod(request)
            }
        },
        content = content
    )
}
