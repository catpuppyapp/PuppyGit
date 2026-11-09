package com.catpuppyapp.puppygit.compose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.showToast

@Composable
fun LongPressAbleIconBtn(
    modifier: Modifier = Modifier,
    iconModifier:Modifier=Modifier,
    tooltipText: String,
    icon: ImageVector,
    iconContentDesc:String? = tooltipText.ifBlank { null },
    enabled:Boolean=true,
    iconColor:Color? = null,
    isInDarkTheme:Boolean = Theme.inDarkTheme,
    haptic: HapticFeedback = LocalHapticFeedback.current,
    activityContext:Context =  LocalContext.current,
    pressedCircleSize:Dp = MyStyleKt.defaultLongPressAbleIconBtnPressedCircleSize,

    //空白提示文字，代表不想显示提示文案
    onLongClick:(()->Unit)? = if(tooltipText.isEmpty()) null else ({
        //震动反馈，显示tooltip提示
//        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        showToast(AppModel.realAppContext, tooltipText, Toast.LENGTH_SHORT)

    }),

    onClick: (()->Unit)? = null,
) {
    val iconColor = iconColor ?: LocalContentColor.current

    LongPressAbleIconBtnToastVersion(context=activityContext, modifier=modifier,iconModifier=iconModifier,
        tooltipText=tooltipText,icon=icon,iconContentDesc=iconContentDesc,haptic=haptic,
        enabled=enabled, iconColor=iconColor, isInDarkTheme=isInDarkTheme, pressedCircleSize=pressedCircleSize,
        onLongClick=onLongClick, onClick=onClick,
    )
}


////自己实现一个支持长按显示按钮功能的button
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LongPressAbleIconBtnToastVersion(
    context: Context,
    modifier: Modifier = Modifier,
    iconModifier:Modifier=Modifier,
    tooltipText: String,
    icon: ImageVector,
    iconContentDesc:String?=null,
    haptic: HapticFeedback = LocalHapticFeedback.current,
    enabled:Boolean=true,
    iconColor: Color = LocalContentColor.current,
    isInDarkTheme:Boolean = Theme.inDarkTheme,
    pressedCircleSize: Dp,
    onLongClick:(()->Unit)?,
    onClick: (()->Unit)?,
) {

    Box(
        modifier = modifier
            // inline的时候如果加这个最小可交互size，会带padding，间距会过大，不好
//            .minimumInteractiveComponentSize()

            //圆角
            .size(pressedCircleSize)  //参见 IconButton 的Box的size
            .background(color = Color.Transparent)
            .graphicsLayer {
                clip = true
                shape = CircleShape
            }
            //长按震动反馈，点按执行操作
            .then(
                if(onLongClick != null) {
                    Modifier
                        .combinedClickable(
                            enabled = enabled,
                            onClick = onClick ?: {},
                            onLongClick = onLongClick
                        )
                } else if(onClick != null) {
                    Modifier.clickable(enabled = enabled) { onClick() }
                } else {
                    Modifier
                }
            )

        ,
        contentAlignment = Alignment.Center
    ) {
//        CompositionLocalProvider( content = content)

            Icon(
                modifier=iconModifier,
                tint= if(enabled) iconColor else UIHelper.getDisableBtnColor(isInDarkTheme),
                imageVector = icon,
                contentDescription = iconContentDesc
            )

    }
}
