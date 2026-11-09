package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.ui.theme.Theme


/**
 * 遮罩loading，适合有希望拦截用户输入的loading，例如保存文本编辑器内容时；但如果loading时间很短则不建议使用此组件，因为屏幕会闪一下，看着很难受
 * 仅应在希望屏蔽用户输入时用，例如保存文件，其余场景建议使用LoadingText
 * 注：这个不需要写成 if(loading) show this; else show list; 那样，因为这个东西会直接罩在屏幕上，拦截用户输入，所以，直接和普通list在一个级别就好
 * 注：这个放不放到Scaffold里好像无所谓，加载圆圈的背景颜色我已经根据是否darkTheme调了，大背景好像没什么好调的因为是半透明的
 */
@Composable
fun LoadingDialog(text:String = stringResource(R.string.loading)) {

    val inDarkTheme = Theme.inDarkTheme

    Dialog(
        onDismissRequest = {/* showLoadingDialog.value = false */ },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
//                .size(100.dp)
                .fillMaxHeight(.2f)
                .fillMaxWidth(.5f)
                .background(
                    color = if(inDarkTheme) Color.DarkGray else Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = text, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, softWrap = true)

            }
        }

    }
}
