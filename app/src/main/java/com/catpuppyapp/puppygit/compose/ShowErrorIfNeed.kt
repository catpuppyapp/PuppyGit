package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.showToast

@Composable
fun ShowErrorIfNeed(
    hasErr:MutableState<Boolean>,
    errMsg:MutableState<String>,
    useErrorPrefix:Boolean=true
) {
    val activityContext = LocalContext.current

    if(hasErr.value) {
        //显示提示信息：
        if(useErrorPrefix){
            // use Prefix, show: “Error: 错误信息”
            showToast(activityContext, stringResource(R.string.error)+": "+ errMsg.value)
        }else {
            //no Prefix, show: "错误信息"
            showToast(activityContext, errMsg.value)
        }

        //reset err，避免下次发生同样错误时，不显示提示信息
        hasErr.value=false
        errMsg.value=""
    }
}
