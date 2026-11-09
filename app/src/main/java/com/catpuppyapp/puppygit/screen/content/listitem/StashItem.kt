package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.InLineCopyIcon
import com.catpuppyapp.puppygit.compose.InLineHistoryIcon
import com.catpuppyapp.puppygit.git.StashDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.fromTagToCommitHistory
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StashItem(
    repoId:String,
    showBottomSheet: MutableState<Boolean>,
    curObjFromParent: CustomStateSaveable<StashDto>,
    idx:Int,
    lastClickedItemKey:MutableState<String>,

    thisObj:StashDto,
    onClick:()->Unit
) {

    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current

    val haptic = LocalHapticFeedback.current

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

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
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    //设置当前条目
                    curObjFromParent.value = StashDto()
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
            .listItemPadding()


    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(text = stringResource(R.string.index) + ": ")
            Text(
                text = thisObj.index.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // stash id 本质上是提交号，应该也能和其他提交对比或checkout、reset等，不过这个功能一般是临时用下，所以没添加复杂功能
            Text(text = stringResource(R.string.stash_id) + ": ")

            Text(
                text = thisObj.getCachedShortStashId(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )

            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(thisObj.stashId.toString()))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }

            InLineHistoryIcon {
                lastClickedItemKey.value = thisObj.getItemKey()

                fromTagToCommitHistory(
                    fullOid = thisObj.stashId.toString(),
                    shortName = thisObj.getCachedShortStashId(),
                    repoId = repoId
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(text = stringResource(R.string.msg) + ": ")
            Text(
                text = thisObj.getCachedOneLineMsg(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )
        }
    }
}
