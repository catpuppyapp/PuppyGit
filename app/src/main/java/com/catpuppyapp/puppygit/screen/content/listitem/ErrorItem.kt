package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ErrorItem(
    showBottomSheet: MutableState<Boolean>,
    curObjInState: CustomStateSaveable<ErrorEntity>,
    idx:Int,
    lastClickedItemKey:MutableState<String>,
    curObj: ErrorEntity,
    onClick:()->Unit
) {
    val haptic = LocalHapticFeedback.current
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable (
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = curObj.id
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = curObj.id

                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    curObjInState.value = ErrorEntity()

                    //设置当前条目
                    curObjInState.value = curObj

                    //显示底部菜单
                    showBottomSheet.value = true
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if(idx%2==0)  Color.Transparent else CommitListSwitchColor)
            .then(
                if(lastClickedItemKey.value == curObj.id) {
                    Modifier.background(UIHelper.getLastClickedColor())
                }else Modifier
            )
            .listItemPadding()


    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.id) +": ")
            Text(text = curObj.id,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

            ){

            Text(text = stringResource(R.string.date) +": ")
            Text(text = curObj.date,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

            ){

            Text(text = stringResource(R.string.msg) +": ")
            Text(text = curObj.getCachedOneLineMsg(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )
        }

    }
}
