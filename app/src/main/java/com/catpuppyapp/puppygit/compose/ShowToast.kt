package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.showToast

@Composable
fun ShowToast(
    showToast:MutableState<Boolean>,
    msg:MutableState<String>,
) {
    val activityContext = LocalContext.current

    if(showToast.value) {
        //显示提示信息：
        showToast(activityContext, msg.value)


        //reset msg
        showToast.value=false
        msg.value=""
    }
}
