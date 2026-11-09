package com.catpuppyapp.puppygit.git

import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.compare.CmpUtil
import com.catpuppyapp.puppygit.utils.compare.param.StringCompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.github.git24j.core.Diff.Line.OriginType

private const val TAG = "CompareLinePair"

//行号从1开始，小于等于0皆无效
private const val invalidLineNum = 0

object CompareLinePairHelper {
    //剪贴板用个无效的行号即可
    const val clipboardLineNum:Int = -10
    const val clipboardLineOriginType:String="clipboard_origin_type"
    const val clipboardLineKey:String="clipboard_key"

//    fun lineNumValid(lineNum:Int):Boolean {
//        return lineNum > invalidLineNum
//    }
}


data class CompareLinePair (
    var key:String = getShortUUID(),
    var line1Num:Int=invalidLineNum,
    var line1:String?=null,
    var line1OriginType:String="",
    var line2Num:Int=invalidLineNum,
    var line2:String?=null,
    var line2OriginType:String="",

    var line1Key:String="",
    var line2Key:String="",


//    var line1IsLogicAdd:Boolean = false,
    var compareResult:IndexModifyResult?=null,

//    var consumeCount:Int = 0
) {


    fun isEmpty():Boolean {
//        return line1.isEmpty() && line2.isEmpty()
        //行号1为初始化的值，代表没选中过任何东西，即此pair为空
        return line1Num == invalidLineNum
    }

    fun line1ReadyForCompare():Boolean {
        return line1 != null
    }

    fun line2ReadyForCompare():Boolean {
        return line2 != null
    }

    fun readyForCompare():Boolean {
        return  line1ReadyForCompare() && line2ReadyForCompare()
    }

    fun isCompared():Boolean {
        return compareResult != null
    }

    fun compare(betterCompare:Boolean, matchByWords:Boolean, map:MutableMap<String, CompareLinePairResult>) {
        // not ready
        if(readyForCompare().not()) {
            return
        }

        // compared
        if(isCompared()) {
            return
        }

        // bad origin type
        if(line1OriginType == OriginType.CONTEXT.toString() &&
            line2OriginType == OriginType.CONTEXT.toString()
        ) {
            MyLog.w(TAG, "compare both Context type lines are nonsense: line1OriginType=$line1OriginType, line2OriginType=$line2OriginType")
            return
        }


//        val line1fakeType = if(line1OriginType != OriginType.ADDITION.toString() && line1!=OriginType.DELETION.toString()) {
//            if(line2OriginType == OriginType.ADDITION.toString()) {
//                OriginType.DELETION.toString()
//            }else {
//                OriginType.ADDITION.toString()
//            }
//        }else line1OriginType
//
//        val line2fakeType = if(line1fakeType == line1OriginType) {
//            line2OriginType
//        }else {
//            if(line1fakeType == OriginType.ADDITION.toString()) {
//                OriginType.DELETION.toString()
//            }else {
//                OriginType.ADDITION.toString()
//            }
//        }

//        line1IsLogicAdd = line1fakeType == OriginType.ADDITION.toString()
//        val addContent = if(line1IsLogicAdd) line1 else line2
//        val delContent = if(line1IsLogicAdd) line2 else line1
//        line1IsLogicAdd = false
//        val add = if (line1OriginType == OriginType.ADDITION.toString() || line1OriginType == CompareLinePairHelper.clipboardLineOriginType || line1OriginType == OriginType.CONTEXT.toString()) {
//            line1IsLogicAdd = true
//            line1
//        } else {
//            line2
//        }
        val line1 = line1 ?: ""
        val line2 = line2 ?: ""

        val cmpResult = CmpUtil.compare(
            add = StringCompareParam(line1, line1.length),
            del = StringCompareParam(line2, line2.length),

            //为true则对比更精细，但是，时间复杂度乘积式增加，不开 O(n)， 开了 O(nm)
            requireBetterMatching = betterCompare,
            matchByWords = matchByWords
        )

        if(cmpResult.matched) {
            //有匹配，添加string part list
            map.put(line1Key, CompareLinePairResult(cmpResult.add))
            map.put(line2Key, CompareLinePairResult(cmpResult.del))
        }else {
            //无匹配，存null，使显示结果为无匹配的单纯颜色而不是深、浅颜色
            map.put(line1Key, CompareLinePairResult(null))
            map.put(line2Key, CompareLinePairResult(null))
        }

        compareResult = cmpResult
    }

    fun clear() {
//        consumeCount=0
        line1Num=invalidLineNum
        line2Num=invalidLineNum
        line1 = ""
        line2 = ""
        line1OriginType = ""
        line2OriginType = ""
        line1Key=""
        line2Key=""
//        line1IsLogicAdd = false
        compareResult = null
    }

//    private fun consumeLimit():Int {
//        if(line1OriginType == OriginType.CONTEXT.toString() || line2OriginType == OriginType.CONTEXT.toString()
//            || line1Num == CompareLinePairHelper.clipboardLineNum || line2Num == CompareLinePairHelper.clipboardLineNum
//            || line1OriginType == CompareLinePairHelper.clipboardLineOriginType || line2OriginType == CompareLinePairHelper.clipboardLineOriginType
//        ) {
//            return 1
//        }else {
//            return 2
//        }
//    }

//    fun consume():CompareLinePair {
//        val c = copy()
//        if(++consumeCount >= consumeLimit()){
//            clear()
//        }
//
//        return c
//    }
}

/**
 * 存储选择行比较的结果，只要比较过，对应的line.key就一定有这个对象，但只有stringPartList不为null才代表有匹配，否则代表无匹配
 */
data class CompareLinePairResult (
    val stringPartList:List<IndexStringPart>?=null
)
