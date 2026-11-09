package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

// more bigger, will have more chance to use `firstVisibleLineIndex`, else, will more chance use `lineIndex`,
// best of this value is most time show how many lines in the users device screen.
private const val defaultRestoreOffset = 16

@Serializable
data class FileEditedPos (
    //第一个可见行的索引，用来打开文件时定位
    var firstVisibleLineIndex:Int=0,
    //最后编辑行 (20240507: 实际上现在的最后编辑行就是光标最后所在行，即使没编辑，只要聚焦某行，就记上了)
    var lineIndex:Int=0,
    //最后编辑列
    var columnIndex:Int=0,
    //上次使用时间，变相等于文件最后打开时间，这个值暂时用不到，以后用来实现“如果超过指定期限未打开过文件，则删除对应位置信息条目”
    var lastUsedTime:Long= 0,
) {
    /**
     * get a line index for restore view
     * @return if the last edited line `lineIndex` may visible when scroll to `firstVisibleLineIndex`,
     * then return `firstVisibleLineIndex` else return `lineIndex`
     *
     * known bug: due to the `defaultRestoreOffset` is a guessed value, so, maybe `lineIndex` invisible but still return the `firstVisibleLineIndex`,
     *  in that case, when scroll to make lineIndex visible, the software keyboard will popup.
     */
    fun getLineIdxForRestoreView() = if((firstVisibleLineIndex - lineIndex).absoluteValue < defaultRestoreOffset) firstVisibleLineIndex else lineIndex
}
