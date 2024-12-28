package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.git.ReflogEntryDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReflogItem(
    showBottomSheet: MutableState<Boolean>,
    curObjFromParent: CustomStateSaveable<ReflogEntryDto>,
//    idx:Int,
    lastClickedItemKey:MutableState<String>,

    thisObj:ReflogEntryDto,
    onClick:()->Unit
) {

    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current

    val haptic = LocalHapticFeedback.current

    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisObj.getItemKey()
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = thisObj.getItemKey()

                    //震动反馈
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    curObjFromParent.value = ReflogEntryDto()

                    //设置当前条目
                    curObjFromParent.value = thisObj

                    //显示底部菜单
                    showBottomSheet.value = true
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if (idx % 2 == 0) Color.Transparent else CommitListSwitchColor)
            .then(
                if(thisObj.getItemKey() == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                }else {
                    Modifier
                }
            )
            .padding(10.dp)


    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.new_oid) +":")
            Text(
                text = Libgit2Helper.getShortOidStrByFull(thisObj.idNew.toString()),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light,
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                fontSize = 16.sp,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                    clipboardManager.setText(AnnotatedString(thisObj.idNew.toString()))
                    Msg.requireShow(activityContext.getString(R.string.copied))
                },
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.old_oid) +":")
            Text(
                text = Libgit2Helper.getShortOidStrByFull(thisObj.idOld.toString()),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light,
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                fontSize = 16.sp,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                    clipboardManager.setText(AnnotatedString(thisObj.idOld.toString()))
                    Msg.requireShow(activityContext.getString(R.string.copied))
                },
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.date) +":")
            Text(text = thisObj.date,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.author) +":")
            Text(text = Libgit2Helper.getFormattedUsernameAndEmail(thisObj.username, thisObj.email),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.msg) +":")
            Text(text = thisObj.msg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }

     }
}
