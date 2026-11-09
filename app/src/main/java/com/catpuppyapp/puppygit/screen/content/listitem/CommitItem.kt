package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountTree
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.InLineCopyIcon
import com.catpuppyapp.puppygit.compose.InLineIcon
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SingleLineClickableText
import com.catpuppyapp.puppygit.dto.Box
import com.catpuppyapp.puppygit.git.CommitDto
import com.catpuppyapp.puppygit.git.DrawCommitNode
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.time.TimeZoneUtil
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommitItem(
    drawLocalAheadUpstreamCount: Int,
    commitHistoryGraph:Boolean,
    density: Density,
    nodeCircleRadiusInPx:Float,
    nodeCircleStartOffsetX:Float,
    nodeLineWidthInPx:Float,
    lineDistanceInPx:Float,
    showBottomSheet: MutableState<Boolean>,
    curCommit: CustomStateSaveable<CommitDto>,
    curCommitIdx:MutableIntState,
    idx:Int,
    commitDto:CommitDto,
    requireBlinkIdx:MutableIntState,  //请求闪烁的索引，会闪一下对应条目，然后把此值设为无效
    lastClickedItemKey:MutableState<String>,
    shouldShowTimeZoneInfo:Boolean,

//    showItemDetails:(CommitDto)->Unit,
    showItemMsg:(CommitDto)->Unit,
    onClick:(CommitDto)->Unit={}
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current

    val haptic = LocalHapticFeedback.current

    val updateCurObjState = {
        curCommit.value = CommitDto()
        curCommitIdx.intValue = -1

        //设置当前条目
        curCommit.value = commitDto
        curCommitIdx.intValue = idx
    }

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    

//    println("IDX::::::::::"+idx)
    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
            .drawNode(
                commitItemIdx = idx,
                drawLocalAheadUpstreamCount = drawLocalAheadUpstreamCount,
                commitHistoryGraph = commitHistoryGraph,
                c = commitDto,
                density = density,
                nodeCircleRadiusInPx = nodeCircleRadiusInPx,
                nodeCircleStartOffsetX = nodeCircleStartOffsetX,
                nodeLineWidthInPx = nodeLineWidthInPx,
                lineDistanceInPx = lineDistanceInPx,

            )
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = commitDto.oidStr
                    onClick(commitDto)
                },
                onLongClick = {  // x 算了)xTODO 把长按也改成短按那样，在调用者那里实现，这里只负责把dto传过去，不过好像没必要，因为调用者那里还是要写同样的代码，不然弹窗不知道操作的是哪个对象
                    lastClickedItemKey.value = commitDto.oidStr

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
                if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == idx) {
                    val highlightColor = Modifier.background(UIHelper.getHighlightingBackgroundColor())
                    //高亮2s后解除
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  //解除高亮倒计时
                        requireBlinkIdx.intValue = -1  //解除高亮
                    }
                    highlightColor
                } else if (commitDto.oidStr == lastClickedItemKey.value) {
                    Modifier.background(UIHelper.getLastClickedColor())
                } else {
                    Modifier
                }
            )
            .listItemPadding()




    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.commit)
            )

//            Text(text = stringResource(R.string.hash) +": ")

            Text(text = commitDto.shortOidStr,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight

            )

            InLineCopyIcon {
                clipboardManager.setText(AnnotatedString(commitDto.oidStr))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
//        Row (
//            verticalAlignment = Alignment.CenterVertically,
//
//            ){
//
//            Text(text = stringResource(R.string.email) +":")
//            Text(text = commitDto.email,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                fontWeight = defaultFontWeight
//
//            )
//        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Filled.Person,
                tooltipText = stringResource(R.string.author)
            )

//            Text(text = stringResource(R.string.author) +": ")

            ScrollableRow {
                Text(text = commitDto.getFormattedAuthorInfo(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }

        //如果committer和author不同，显示
        if(!commitDto.authorAndCommitterAreSame()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){

                InLineIcon(
                    icon = Icons.Outlined.Person,
                    tooltipText = stringResource(R.string.committer)
                )

//                Text(text = stringResource(R.string.committer) +": ")

                ScrollableRow {
                    Text(text = commitDto.getFormattedCommitterInfo(),
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
                icon = Icons.Filled.CalendarMonth,
                tooltipText = stringResource(R.string.date)
            )

//            Text(text = stringResource(R.string.date) +": ")

            ScrollableRow {
                Text(text = if(shouldShowTimeZoneInfo) TimeZoneUtil.appendUtcTimeZoneText(commitDto.dateTime, commitDto.originTimeOffsetInMinutes) else commitDto.dateTime,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){

            InLineIcon(
                icon = Icons.AutoMirrored.Filled.Message,
                tooltipText = stringResource(R.string.msg)
            )

//            Text(text = stringResource(R.string.msg) +": ")

            SingleLineClickableText(commitDto.getCachedOneLineMsg()) {
                lastClickedItemKey.value = commitDto.oidStr

                updateCurObjState()
                showItemMsg(commitDto)
            }
        }
        if(commitDto.branchShortNameList.isNotEmpty()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,

            ){

                InLineIcon(
                    icon = ImageVector.vectorResource(R.drawable.branch),
                    tooltipText = (if(commitDto.branchShortNameList.size > 1) stringResource(R.string.branches) else stringResource(R.string.branch))
                )

//                Text(text = (if(commitDto.branchShortNameList.size > 1) stringResource(R.string.branches) else stringResource(R.string.branch)) +": ")

                ScrollableRow {
                    Text(text = commitDto.cachedBranchShortNameList(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }

        }

        if(commitDto.tagShortNameList.isNotEmpty()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,

            ){

                InLineIcon(
                    icon = Icons.AutoMirrored.Filled.Label,
                    tooltipText = (if(commitDto.tagShortNameList.size > 1) stringResource(R.string.tags) else stringResource(R.string.tag))
                )

//                Text(text = (if(commitDto.tagShortNameList.size > 1) stringResource(R.string.tags) else stringResource(R.string.tag)) +": ")

                ScrollableRow {
                    Text(text = commitDto.cachedTagShortNameList(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }

        }

        if(commitDto.parentShortOidStrList.isNotEmpty()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,

            ){

                InLineIcon(
                    icon = Icons.Filled.AccountTree,
                    tooltipText = (if(commitDto.parentShortOidStrList.size > 1) stringResource(R.string.parents) else stringResource(R.string.parent))
                )

//                Text(text = (if(commitDto.parentShortOidStrList.size > 1) stringResource(R.string.parents) else stringResource(R.string.parent)) +": ")

                ScrollableRow {
                    Text(text = commitDto.cachedParentShortOidStrList(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }

        }

        if(commitDto.hasOther()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){

                InLineIcon(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    tooltipText = stringResource(R.string.other)
                )

//                Text(text = stringResource(R.string.other)+": ")

                ScrollableRow {
                    Text(text = commitDto.getOther(activityContext, false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }

        }
    }
}


@Composable
private fun Modifier.drawNode(
    commitItemIdx:Int,
    drawLocalAheadUpstreamCount: Int,
    commitHistoryGraph:Boolean,
    c: CommitDto,
    density: Density,
    nodeCircleRadiusInPx:Float,
    nodeCircleStartOffsetX:Float,
    nodeLineWidthInPx:Float,
    // 线和线之前的间距
    lineDistanceInPx:Float,
):Modifier {

    if(commitHistoryGraph.not()) return this;

    //把线画右边
    val isRtl = UIHelper.isRtlLayout()
    //把线画左边
//    val isRtl = !UIHelper.isRtlLayout()



    return drawBehind {
        val horizontalWidth = size.width
        val verticalHeight = size.height

        val startOffSetX = if(isRtl) 0f else horizontalWidth

        // 条目垂直中间线（高度的一半）
        val verticalCenter = verticalHeight/2
        var initInputLineStartX = getInitStartX(isRtl, startOffSetX, nodeCircleStartOffsetX)
        var inputLineStartX = initInputLineStartX
        var circleEndX = Box(inputLineStartX)

        var lastStartX = 0F;

        //输入线
        c.draw_inputs.forEachIndexedBetter { idx, node->
            lastStartX = inputLineStartX

            if(isRtl) {
                inputLineStartX = initInputLineStartX + (idx * lineDistanceInPx)
            }else {
                inputLineStartX = initInputLineStartX - (idx * lineDistanceInPx)
            }


            if(node.inputIsEmpty.not()) {
                //如果节点在此结束，则连接到当前节点的圆圈，否则垂直向下


                val color = getColor(idx, commitItemIdx, drawLocalAheadUpstreamCount)

                //画当前节点
                drawInputLinesAndCircle(
                    node,
                    nodeCircleRadiusInPx,
                    nodeLineWidthInPx,
                    inputLineStartX,
                    verticalCenter,
                    color,
                    circleEndX
                )



                //画合流节点
                node.mergedList.forEachBetter { node ->
                    drawInputLinesAndCircle(
                        node,
                        nodeCircleRadiusInPx,
                        nodeLineWidthInPx,
                        inputLineStartX,
                        verticalCenter,
                        color,
                        circleEndX
                    )
                }
            }

        }



        var initOutputLineStartX = getInitStartX(isRtl, startOffSetX, nodeCircleStartOffsetX)
        var outputLineStartX = initOutputLineStartX

        //输出线
        c.draw_outputs.forEachIndexedBetter { idx, node->
            lastStartX = outputLineStartX

            if(isRtl) {
                outputLineStartX = initOutputLineStartX + (idx * lineDistanceInPx)
            }else {
                outputLineStartX = initOutputLineStartX - (idx * lineDistanceInPx)
            }

            if(node.outputIsEmpty.not()) {
                val color = getColor(idx, commitItemIdx, drawLocalAheadUpstreamCount)

                //画当前节点
                drawOutputLinesAndCircle(
                    node,
                    nodeCircleRadiusInPx,
                    nodeLineWidthInPx,
                    outputLineStartX,
                    verticalHeight,
                    verticalCenter,
                    color,
                    circleEndX
                )

                //画合流节点
                node.mergedList.forEachBetter { node ->
                    drawOutputLinesAndCircle(
                        node,
                        nodeCircleRadiusInPx,
                        nodeLineWidthInPx,
                        outputLineStartX,
                        verticalHeight,
                        verticalCenter,
                        color,
                        circleEndX
                    )

                }
            }
        }
    }

}

private fun getInitStartX(isRtl: Boolean, startOffSetX: Float, nodeCircleStartOffsetX: Float): Float {
    return if(isRtl) startOffSetX + nodeCircleStartOffsetX else (startOffSetX - nodeCircleStartOffsetX)
}


private fun DrawScope.drawInputLinesAndCircle(
    node: DrawCommitNode,
    nodeCircleRadiusInPx:Float,
    nodeLineWidthInPx:Float,
    inputLineStartX:Float,
    verticalCenter:Float,
    color:Color,
    circleEndX:Box<Float>
) {
    val endX = if(node.endAtHere) circleEndX.value else inputLineStartX

    drawLine(
        color = color,
        blendMode = DrawCommitNode.colorBlendMode,

        strokeWidth = nodeLineWidthInPx,  //宽度
        //起始和结束点，单位应该是px
        start = Offset(inputLineStartX, 0f),
        end = Offset(endX, verticalCenter),
    )

    if(node.circleAtHere) {
        circleEndX.value = endX
        // 画代表当前提交的圆圈
        drawCircle(
            color = color, // 圆圈颜色
            blendMode = DrawCommitNode.colorBlendMode,
            radius = nodeCircleRadiusInPx, // 半径
            center = Offset(endX, verticalCenter) // 圆心
        )
    }
}

private fun DrawScope.drawOutputLinesAndCircle(
    node: DrawCommitNode,
    nodeCircleRadiusInPx:Float,
    nodeLineWidthInPx:Float,
    outputLineStartX:Float,
    verticalHeight:Float,
    verticalCenter:Float,
    color:Color,
    circleEndX:Box<Float>
) {

    //如果需要在这画圈，必然是HEAD第一个提交
    if(node.circleAtHere) {
        // 画代表当前提交的圆圈
        drawCircle(
            color = color, // 圆圈颜色
            blendMode = DrawCommitNode.colorBlendMode,
            radius = nodeCircleRadiusInPx, // 半径
            center = Offset(circleEndX.value, verticalCenter) // 圆心
        )
    }

    //如果节点在此结束，则连接到当前节点，否则垂直向下
    val startX = if(node.startAtHere) circleEndX.value else outputLineStartX

    drawLine(
        color = color,
        blendMode = DrawCommitNode.colorBlendMode,
        strokeWidth = nodeLineWidthInPx,  //宽度
        //起始和结束点，单位应该是px
        start = Offset(startX, verticalCenter),
        end = Offset(outputLineStartX, verticalHeight),
    )

}


/**
 * @param lineIdx 第几根线的索引，从0开始
 * @param commitItemIdx 第几个提交的索引，从0开始
 * @param drawLocalAheadUpstreamCount 本地领先远程几个提交，大于0则用不同于当前线的颜色显示对应数量的提交
 */
private fun getColor(lineIdx: Int, commitItemIdx:Int, drawLocalAheadUpstreamCount:Int) :Color {
    //给本地领先远程的提交使用特殊的颜色
    //第一条线 且 抵达上游分支 前的提交，使用不同于当前索引的颜色
//    return if(lineIdx == 0 && commitItemIdx < drawLocalAheadUpstreamCount) {
    // 没推送的提交的所有线都是同一种颜色
    return if(commitItemIdx < drawLocalAheadUpstreamCount) {
        DrawCommitNode.localAheadUpstreamColor()
    }else {
        DrawCommitNode.getNodeColorByIndex(lineIdx)
    }
}
