package com.catpuppyapp.puppygit.git

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import com.catpuppyapp.puppygit.dto.Box
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.forEachBetter
import io.ktor.util.collections.ConcurrentMap


// 0-255，越小越透明
private const val alpha = 80;
// alpha / 255，把int的alpha换算成小数
private const val alphaFloat = alpha / 255f;

// 本地领先远程的未推送的节点的线的颜色
private val unPushedColorLight = Color.Gray.copy(alpha = alphaFloat)
private val unPushedColorDark = Color.LightGray.copy(alpha = alphaFloat)

// 避免并发冲突，没用普通的list
private val cachedColors = ConcurrentMap<Int, Color>().apply {
    put(0, Color(red = 0xF, green = 0xA2, blue = 0x72, alpha = alpha))
    put(1, Color(red = 0xE0, green = 0x62, blue = 0x56, alpha = alpha))
    put(2, Color(red = 0xB6, green = 0x9F, blue = 0xB, alpha = alpha))
    put(3, Color(red = 0xB6, green = 0xB, blue = 0x69, alpha = alpha))
    put(4, Color(red = 0xB6, green = 0x55, blue = 0xB, alpha = alpha))
    put(5, Color(red = 0x5C, green = 0x18, blue = 0x8C, alpha = alpha))
    put(6, Color(red = 0xB, green = 0xB0, blue = 0xB6, alpha = alpha))
    put(7, Color(red = 0xA7, green = 0x3B, blue = 0xAB, alpha = alpha))
    put(8, Color(red = 0x66, green = 0xA, blue = 0x88, alpha = alpha))
    put(9, Color(red = 0x1C, green = 0x4C, blue = 0xC7, alpha = alpha))
    put(10, Color(red = 0x25, green = 0xB6, blue = 0xB, alpha = alpha))
    put(11, Color(red = 0xB6, green = 0x91, blue = 0xB, alpha = alpha))
    put(12, Color(red = 0xB6, green = 0xB, blue = 0x91, alpha = alpha))
    put(13, Color(red = 0xB, green = 0xB6, blue = 0x9C, alpha = alpha))
    put(14, Color(red = 0x9C, green = 0xB6, blue = 0xB, alpha = alpha))
    put(15, Color(red = 0xB8, green = 0x25, blue = 0xC2, alpha = alpha))
    put(16, Color(red = 0xB, green = 0x9C, blue = 0xB6, alpha = alpha))
    put(17, Color(red = 0x5B, green = 0xB, blue = 0xB6, alpha = alpha))
    put(18, Color(red = 0xB6, green = 0xB, blue = 0x4F, alpha = alpha))
    put(19, Color(red = 0xB, green = 0xB6, blue = 0xB0, alpha = alpha))
    put(20, Color(red = 0xB6, green = 0x4F, blue = 0xB, alpha = alpha))
}


@Stable
@Immutable
data class DrawCommitNode (
    //顺序
    // 用集合中的索引替代
//    val order:Int,

    //如果当前节点endAtHere但其位于列表中间，
    // 前后皆有条目，则此节点仍保持，但仅占位，不再绘制线条，
    // 如果当前节点有多个parent，可占用空节点的位置
    val outputIsEmpty:Boolean,

    //不需要画线，仅占位
    val inputIsEmpty:Boolean,

    //需要在这个节点这一列画个圈
    val circleAtHere:Boolean,

    //这条线是否在当前commit结束（线连接到圆圈）
    val endAtHere:Boolean,

    //这条线是否从当前提交开始（从圆圈起始）
    val startAtHere:Boolean,

    //这个merged并不是合并分支，而是由于toCommitHash一样而合并在一起的线，目标一样，所以“合流”了，以节省屏幕空间
    //最多只有一层，合并节点列表的条目不可能包含合并节点列表，因为添加到这个列表的都是新创建的当前节点的父节点。（真庆幸不需要多层嵌套）
    // 合流的节点起点(fromCommitHash)可能不同，但终点(toCommitHash)一定相同
    //是否和上条线合流，仅适用于只有一个节点长度的短流
    val mergedList:List<DrawCommitNode>,

    //线的颜色
//    val color: Color,

    //关联的commit的完整hash
    val fromCommitHash:String,

    val toCommitHash:String,
) {
    // 如果是 node，会在条目中间画的圆圈。
    // 在以单head为基准的视图中，只有提交本身是node，在以仓库所有引用为基准的视图中，每个引用的起点也是node（也要画圆圈）
//    fun isNode() = order == 0;

    // 不是node就是line，line就是直接顺着路径往下画，中间没圆圈的那种
//    fun isLine() = isNode().not();




    fun toStringForView():String {
        return "from: $fromCommitHash\nto: $toCommitHash\ncircleAtHere: $circleAtHere\nstartAtHere: $startAtHere\nendAtHere: $endAtHere\ninputIsEmpty: $inputIsEmpty\noutputIsEmpty: $outputIsEmpty\n\n"+(
                if(mergedList.isEmpty()) "" else {
                    val sb = StringBuilder()
                    // 合流
                    sb.append("\nConfluences:\n")

                    mergedList.forEachBetter {
                        sb.append(it.toStringForView())
                    }

                    sb.toString()
                }
        )
    }







    companion object {
        //设为SrcAtop使颜色叠加时变重
        val colorBlendMode = BlendMode.SrcAtop

        //未推送到上游的提交的线的颜色，此线必须不同于cache color 索引 0 的颜色
        fun localAheadUpstreamColor(inDarkTheme:Boolean = Theme.inDarkTheme) = if(inDarkTheme) unPushedColorDark else unPushedColorLight;


        /**
         * 找output节点找一个已存在的可插入的列，如果中间有empty节点，会返回那个节点的索引（占它的位置，继续画线），否则返回 -1
         */
        fun getAnInsertableIndex(list:List<DrawCommitNode>, toCommitHash: String):InsertablePosition {
            var index = -1

            var afterTheCircle = false
            var isMergedToPrevious = false


            for((idx, node) in list.withIndex()) {
                //可插入的索引位置必须在当前画圈的线的后面，例如画圈在索引2，则可插入位置必须大于2，原因如下：
                //这个方法用来给输出节点找插入位置，输出节点即当前节点的父节点，所以肯定是startAtHere，因此需要计算七点偏移量，
                // 如果可插队的索引位置可以小于当前画圈的节点，那我就必须区分是插队到前面还是后面，来决定是加偏移量还是减偏移量，
                // 会增加代码复杂度，所以直接写成单方向往后加，省事
                if(afterTheCircle.not()) {
                    afterTheCircle = node.circleAtHere
                    continue
                }

                if(node.outputIsEmpty
                    || (node.toCommitHash == toCommitHash).let { isMergedToPrevious = it; it }
                ) {
                    index = idx
                    break
                }
            }

            return InsertablePosition(
                isMergedToPrevious,
                index
            )
        }

        fun getNodeColorByIndex(i: Int): Color {
            //缓存颜色的集合得是可变的，运行时如果发现颜色索引不存在，会插入个新颜色存进去
            // alpha越大越不透明，范围 0-255，128是半透明
            return cachedColors.get(i) ?: UIHelper.getRandomColorForDrawNode(alpha = alpha).let {
                cachedColors.set(i, it)

                it
            }
        }

        fun transOutputNodesToInputs(node: DrawCommitNode, currentCommitOidStr:String, idx:Int, circleAt:Box<Int>): DrawCommitNode {
            return if(node.outputIsEmpty) {  // outputIsEmpty的，在上个节点就断了，保留只是为了占位置，设个flag，到时候计算位置时会加上它
                node.copy(inputIsEmpty = true, startAtHere = false)
            }else if(node.toCommitHash == currentCommitOidStr) {  // circleAtHere ，需要在这个线上画圈
                if(circleAt.value == -1) {  //还没被别人画圈，自己先画上
                    //更新索引，不然后续节点会和当前节点争夺画圈的权利，导致发生类似九子夺嫡的不和谐情况
                    circleAt.value = idx
                    //这条线要继续传香火，所有后续目标一致的线都要和它合流
                    node.copy(circleAtHere = true, endAtHere = false, outputIsEmpty = false, startAtHere = false)
                }else {  // 圈被别人画了，这条线没活路了
                    //这条线已经走到了尽头，最终会连接到圆圈，与圆圈所在的线融为一体，
                    // 如果有同时期的线仍然存活并且没有后生抢占地位，它曾战斗过的队列将继续以空白的形式流传下去
                    node.copy(circleAtHere = false, endAtHere = true, outputIsEmpty = true, startAtHere = false)
                }
            }else {  //这条线气数未尽，依然可以特立独行，并且还能至少再战一个回合
                node.copy(circleAtHere = false, endAtHere = false, outputIsEmpty = false, startAtHere = false)
            }
        }
    }
}

class InsertablePosition(
    val isMergedToPrevious:Boolean,
    val index:Int,
)
