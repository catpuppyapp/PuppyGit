package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Focuser(
    focusRequester: FocusRequester,
    scope: CoroutineScope,
){
    LaunchedEffect(Unit) {
        scope.launch {
            runCatching {
                //等半秒，不然页面还没渲染完，容易聚焦失败
                delay(500)
                //弹出键盘
                focusRequester.requestFocus()
            }
        }
    }
}


@Composable
fun OneTimeFocusRightNow(focusRequester: FocusRequester) {
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }
}
