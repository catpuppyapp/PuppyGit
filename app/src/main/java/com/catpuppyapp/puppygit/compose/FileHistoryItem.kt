package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
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
import com.catpuppyapp.puppygit.git.FileHistoryDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileHistoryItem(
    showBottomSheet: MutableState<Boolean>,
    curCommit: CustomStateSaveable<FileHistoryDto>,
    curCommitIdx:MutableIntState,
    idx:Int,
    dto:FileHistoryDto,
    requireBlinkIdx:MutableIntState,  //请求闪烁的索引，会闪一下对应条目，然后把此值设为无效
    lastClickedItemKey:MutableState<String>,
    showItemDetails:(FileHistoryDto)->Unit,
    onClick:(FileHistoryDto)->Unit={}
) {

    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current

    val haptic = LocalHapticFeedback.current

    val updateCurObjState = {
        curCommit.value = FileHistoryDto()
        curCommitIdx.intValue = -1

        //设置当前条目
        curCommit.value = dto
        curCommitIdx.intValue = idx
    }

//    println("IDX::::::::::"+idx)
    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = dto.getItemKey()
                    onClick(dto)
                },
                onLongClick = {  // x 算了)TODO 把长按也改成短按那样，在调用者那里实现，这里只负责把dto传过去，不过好像没必要，因为调用者那里还是要写同样的代码，不然弹窗不知道操作的是哪个对象
                    lastClickedItemKey.value = dto.getItemKey()

                    //震动反馈
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    updateCurObjState()

                    //显示底部菜单
                    showBottomSheet.value = true
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if(idx%2==0)  Color.Transparent else CommitListSwitchColor)
            .then(
                //如果是请求闪烁的索引，闪烁一下
                if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue==idx) {
                    val highlightColor = Modifier.background(UIHelper.getHighlightingBackgroundColor())
                    //高亮2s后解除
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  //解除高亮倒计时
                        requireBlinkIdx.intValue = -1  //解除高亮
                    }
                    highlightColor
                } else if(dto.getItemKey() == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                }else {
                    Modifier
                }
            )
            .padding(10.dp)




    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Text(text = stringResource(R.string.commit_id) + ":")
            ClickableText(dto.getCachedCommitShortOidStr()){
                clipboardManager.setText(AnnotatedString(dto.commitOidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Text(text = stringResource(R.string.entry_id) + ":")
            Text(
                text = dto.getCachedTreeEntryShortOidStr(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }
//        Row (
//            verticalAlignment = Alignment.CenterVertically,
//
//            ){
//
//            Text(text = stringResource(R.string.email) +":")
//            Text(text = FileHistoryDto.email,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                fontWeight = FontWeight.Light
//
//            )
//        }
        Row(
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(text = stringResource(R.string.author) + ":")
            Text(
                text = Libgit2Helper.getFormattedUsernameAndEmail(dto.authorUsername, dto.authorEmail),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }

        //如果committer和author不同，显示
        if (!dto.authorAndCommitterAreSame()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.committer) + ":")
                Text(
                    text = Libgit2Helper.getFormattedUsernameAndEmail(dto.committerUsername, dto.committerEmail),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Light

                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Text(text = stringResource(R.string.date) + ":")
            Text(
                text = dto.dateTime,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light

            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(text = stringResource(R.string.msg) + ":")
            ClickableText(dto.msg) {
                lastClickedItemKey.value = dto.getItemKey()

                updateCurObjState()
                showItemDetails(dto)
            }
        }
    }
}
