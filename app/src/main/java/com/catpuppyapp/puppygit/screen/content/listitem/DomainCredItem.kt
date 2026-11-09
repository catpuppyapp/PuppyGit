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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.dto.DomainCredentialDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DomainCredItem(
    showBottomSheet: MutableState<Boolean>,
    curCredentialState: CustomStateSaveable<DomainCredentialDto>,
    idx:Int,
    lastClickedItemKey:MutableState<String>,
    thisItem:DomainCredentialDto,
    onClick:(DomainCredentialDto)->Unit
) {
//    val haptic = LocalHapticFeedback.current

    val none = "[${stringResource(R.string.none)}]"

//    println("IDX::::::::::"+idx)
    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisItem.domainCredId
                    onClick(thisItem)
                },
                onLongClick = {
                    lastClickedItemKey.value = thisItem.domainCredId

                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    curCredentialState.value = DomainCredentialDto()

                    //设置当前条目，供bottomsheet使用
                    curCredentialState.value = thisItem

                    //显示底部菜单
                    showBottomSheet.value = true
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if (idx % 2 == 0) Color.Transparent else CommitListSwitchColor)
            .then(
                if(lastClickedItemKey.value == thisItem.domainCredId) {
                    Modifier.background(UIHelper.getLastClickedColor())
                }else Modifier
            )
            .listItemPadding()



    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.domain) +": ")
            Text(text = thisItem.domain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.http_s) +": ")
            Text(text = thisItem.credName ?: none,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.ssh) +": ")
            Text(text = thisItem.sshCredName ?: none,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }

    }
}
