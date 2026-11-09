package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
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
import com.catpuppyapp.puppygit.compose.InLineIcon
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SingleLineClickableText
import com.catpuppyapp.puppygit.git.FileHistoryDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.time.TimeZoneUtil
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
    shouldShowTimeZoneInfo:Boolean,

    showItemMsg:(FileHistoryDto)->Unit,
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
    
    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

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
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

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
            .listItemPadding()




    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {

            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.commit_id)
            )

//            Text(text = stringResource(R.string.commit_id) + ": ")

            Text(
                text = dto.getCachedCommitShortOidStr(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )

            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(dto.commitOidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {

            InLineIcon(
                icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                tooltipText = stringResource(R.string.entry_id)
            )

//            Text(text = stringResource(R.string.entry_id) + ": ")

            Text(
                text = dto.getCachedTreeEntryShortOidStr(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )

            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(dto.treeEntryOidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
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
//                fontWeight = defaultFontWeight
//
//            )
//        }
        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {

            InLineIcon(
                icon = Icons.Filled.Person,
                tooltipText = stringResource(R.string.author)
            )

//            Text(text = stringResource(R.string.author) + ": ")
            ScrollableRow {
                Text(
                    text = Libgit2Helper.getFormattedUsernameAndEmail(dto.authorUsername, dto.authorEmail),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }

        //如果committer和author不同，显示
        if (!dto.authorAndCommitterAreSame()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                InLineIcon(
                    icon = Icons.Outlined.Person,
                    tooltipText = stringResource(R.string.committer)
                )

//                Text(text = stringResource(R.string.committer) + ": ")
                ScrollableRow {
                    Text(
                        text = Libgit2Helper.getFormattedUsernameAndEmail(dto.committerUsername, dto.committerEmail),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {

            InLineIcon(
                icon = Icons.Filled.CalendarMonth,
                tooltipText = stringResource(R.string.date)
            )

//            Text(text = stringResource(R.string.date) + ": ")
            ScrollableRow {
                Text(
                    text = if(shouldShowTimeZoneInfo) TimeZoneUtil.appendUtcTimeZoneText(dto.dateTime, dto.originTimeOffsetInMinutes) else dto.dateTime,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }

//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//
//        ) {
//            //包含当前entry id的所有提交
//
//            InLineIcon(
//                icon = Icons.Filled.ViewCompact,
//                tooltipText = stringResource(R.string.commits)
//            )
//
////            Text(text = stringResource(R.string.commits) + ": ")
//
//            SingleLineClickableText(dto.cachedShortCommitListStr()) {
//                showCommits(dto)
//            }
//        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            InLineIcon(
                icon = Icons.AutoMirrored.Filled.Message,
                tooltipText = stringResource(R.string.msg)
            )


//            Text(text = stringResource(R.string.msg) + ": ")

            SingleLineClickableText(dto.getCachedOneLineMsg()) {
                lastClickedItemKey.value = dto.getItemKey()

                updateCurObjState()
                showItemMsg(dto)
            }
        }
    }
}
