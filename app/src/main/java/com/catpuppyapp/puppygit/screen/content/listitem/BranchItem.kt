package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.InLineCopyIcon
import com.catpuppyapp.puppygit.compose.InLineIcon
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SingleLineClickableText
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.git.BranchNameAndTypeDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.github.git24j.core.Branch
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BranchItem(
    showBottomSheet: MutableState<Boolean>,
    curObjFromParent: CustomStateSaveable<BranchNameAndTypeDto>,
    idx:Int,
    thisObj:BranchNameAndTypeDto,
    requireBlinkIdx: MutableIntState,  //请求闪烁的索引，会闪一下对应条目，然后把此值设为无效
    lastClickedItemKey:MutableState<String>,
    pageRequest:MutableState<String>,
    onClick:()->Unit
) {

    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current

    val haptic = LocalHapticFeedback.current

    val setCurObj = {
        curObjFromParent.value = BranchNameAndTypeDto()

        //设置当前条目
        curObjFromParent.value = thisObj
    }

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    

    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisObj.fullName
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = thisObj.fullName

                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    setCurObj()

                    //显示底部菜单
                    showBottomSheet.value = true
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if (idx % 2 == 0) Color.Transparent else CommitListSwitchColor)
            .then(
                //如果是请求闪烁的索引，闪烁一下
                if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == idx) {
                    val highlightColor = Modifier.background(UIHelper.getHighlightingBackgroundColor())
                    //高亮2s后解除
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  //解除高亮倒计时
                        requireBlinkIdx.intValue = -1  //解除高亮
                    }
                    highlightColor
                } else if(thisObj.fullName == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                }else {
                    Modifier
                }
            )
            .listItemPadding()



    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            InLineIcon(
                icon = ImageVector.vectorResource(R.drawable.branch),
                tooltipText = stringResource(R.string.branch)
            )
//            Text(text = stringResource(R.string.name) +": ")

            ScrollableRow {
                Text(text = thisObj.shortName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if(thisObj.isCurrent) FontWeight.ExtraBold else defaultFontWeight,
                    color = if(thisObj.isCurrent) MyStyleKt.DropDownMenu.selectedItemColor() else Color.Unspecified
                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.last_commit)
            )
//            Text(text = stringResource(R.string.last_commit) +": ")

            Text(text = thisObj.shortOidStr,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )

            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(thisObj.oidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }

        //如果是本地分支，检查是否是当前活跃的分支。（远程分支就不需要检查了，因为远程分支一checkout就变成detached了，根本不可能是current活跃分支
//        if(thisObj.type == Branch.BranchType.LOCAL) {
//            Row (
//                verticalAlignment = Alignment.CenterVertically,
//            ){
//
//                Text(text = stringResource(R.string.current) +":")
//                Text(text = if (thisObj.isCurrent) stringResource(R.string.yes) else stringResource(R.string.no),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    fontWeight = defaultFontWeight
//
//                )
//            }
//        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            InLineIcon(
                icon = Icons.Filled.Category,
                tooltipText = stringResource(R.string.type)
            )

//            Text(text = stringResource(R.string.type) +": ")

            ScrollableRow {
                Text(text = thisObj.getTypeString(activityContext, false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        //显示上游信息
//        if(thisObj.type == Branch.BranchType.LOCAL && thisObj.upstream!=null) { //其实只要是local就一定有upstream，不会是null，顶多里面没值


//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//
//                ) {
//
//                Text(text = stringResource(R.string.published) + ":")
//                //上游已发布：是|否
//                Text(
//                    text = if (thisObj.upstream!!.isPublished) stringResource(R.string.yes) else stringResource(R.string.no),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    fontWeight = defaultFontWeight
//
//                )
//            }

//        }

        if(thisObj.type == Branch.BranchType.LOCAL) {
            if(thisObj.isUpstreamAlreadySet()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    InLineIcon(
                        icon = Icons.Filled.Cloud,
                        tooltipText = stringResource(R.string.upstream)
                    )

//                    Text(text = stringResource(R.string.upstream) + ": ")

                    ScrollableRow {
                        SingleLineClickableText(thisObj.getUpstreamShortName(activityContext)) {
                            lastClickedItemKey.value = thisObj.fullName

                            setCurObj()
                            pageRequest.value = PageRequest.goToUpstream
                        }
                    }
                }
            }
            //只有有效且发布的分支才会显示状态
            if(thisObj.isUpstreamValid()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    InLineIcon(
                        icon = Icons.Filled.Info,
                        tooltipText = stringResource(R.string.status)
                    )

//                    Text(text = stringResource(R.string.status) + ": ")

                    ScrollableRow {
                        Text(
                            text = thisObj.getAheadBehind(activityContext, false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = defaultFontWeight,
                            color = if(thisObj.alreadyUpToDate()) MyStyleKt.TextColor.getHighlighting() else Color.Unspecified

                        )
                    }
                }

            }
        }


        if (thisObj.isSymbolic) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                InLineIcon(
                    icon = Icons.Filled.Link,
                    tooltipText = stringResource(R.string.symbolic_target)
                )

//                Text(text = stringResource(R.string.symbolic_target) +": ")

                ScrollableRow {
                    Text(text = thisObj.symbolicTargetShortName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){

            InLineIcon(
                icon = Icons.AutoMirrored.Filled.Notes,
                tooltipText = stringResource(R.string.other)
            )

//            Text(text = stringResource(R.string.other) +": ")

            ScrollableRow {
                Text(text = thisObj.getOther(activityContext, false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
     }
}
