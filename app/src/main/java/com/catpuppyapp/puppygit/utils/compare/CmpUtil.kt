package com.catpuppyapp.puppygit.utils.compare

import com.catpuppyapp.puppygit.dev.DevFeature
import com.catpuppyapp.puppygit.dto.Box
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.compare.param.CompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart

object CmpUtil {
    private const val TAG = "CmpUtil"

    /**
     * @param swap (20250210之后)我调换后用了段时间发现很多时候add在前del在后匹配率更高，可能差不多，没必要调换了，调换反而浪费性能。
     *   (20250210之前)我发现del在前add在后匹配率更高，可能因为修改某行一般不会完全删除旧行内容？
     *   总之，因为感觉匹配率高所以我加了这个参数，若传true，则会在比较前调用add和del，
     *   并在返回结果前再调换回来，调用者只需正常传add和del即可，无需再手动调换；
     *   若传false，则在比较时不会调用add和del的顺序。
     */
    fun <T: CharSequence> compare(
        add: CompareParam<T>,
        del: CompareParam<T>,
        requireBetterMatching: Boolean,
        matchByWords: Boolean,
        swap: Boolean = false,
        degradeMatchByWordsToMatchByCharsIfNonMatched: Boolean = DevFeature.degradeMatchByWordsToMatchByCharsIfNonMatched.state.value,
        treatNoWordMatchAsNoMatchedWhenMatchByWord: Boolean = DevFeature.treatNoWordMatchAsNoMatchedForDiff.state.value,

    ): IndexModifyResult {
        if(!SettingsUtil.isEnabledDetailsCompareForDiff()) {
            return createEmptyIndexModifyResult(add, del)
        }

        return try {
            val (add, del) = if(swap) {
                Pair(del, add)
            }else {
                Pair(add, del)
            }

            val result = SimilarCompare.INSTANCE.doCompare(
                add = add,
                del = del,

                //为true则对比更精细，但是，时间复杂度乘积式增加，不开 O(n)， 开了 O(nm)
                requireBetterMatching = requireBetterMatching,

                //根据单词匹配
                matchByWords = matchByWords,

                // 降级按单词匹配为按字符匹配，如果按单词匹配无匹配
                degradeToCharMatchingIfMatchByWordFailed = degradeMatchByWordsToMatchByCharsIfNonMatched,
                treatNoWordMatchAsNoMatchedWhenMatchByWord = treatNoWordMatchAsNoMatchedWhenMatchByWord,
            )

            if(swap) {
                result.copy(add = result.del, del = result.add)
            }else {
                result
            }
        }catch (e: Exception) {
            // this method will call in view, better short err msg to avoid huge log file
            // 这个函数在视图调用，错误信息的重复性和数量都较大，所以最好短点，避免产生大量错误信息导致log非常大
            MyLog.e(TAG, "$TAG#compare() err: ${e.localizedMessage}")
            e.printStackTrace()

            createEmptyIndexModifyResult(add, del)
        }
    }

    private fun <T: CharSequence> createEmptyIndexModifyResult(
        add: CompareParam<T>,
        del: CompareParam<T>,
    ) = IndexModifyResult(
        matched = false,
        matchedByReverseSearch = false,
        add = listOf(IndexStringPart(0, add.getLen(), modified = false)),
        del = listOf(IndexStringPart(0, del.getLen(), modified = false)),
    )

    /**
     * note: order of str1 and str2 doesn't matter, should return same value
     *
     * @param targetMatchCount if has these chars matched, will abort matching and return matched count.
     *   larger for more text matching(bad performance), smaller for more faster handling(good performance).
     *
     * @param targetMatchCount 匹配到此变量所代表的字数将终止匹配并返回匹配数（这个变量的值）。这个值越大，性能越差，但判断两个字符串重合度越越准；值越小，性能越好，但判断结果则更不准
     *
     * @return at least matched chars count. greater than 0 means matched, non-matched otherwise, the returned value may grater than `targetMatchCount`
     */
    fun roughlyMatch(str1NoLineBreak:String, str2NoLineBreak:String, targetMatchCount:Int): Int {
        if(targetMatchCount < 1) {
            throw RuntimeException("`targetMatchCount` should be greater than 0")
        }

        if(str1NoLineBreak.isEmpty() || str2NoLineBreak.isEmpty()) {
            return 0
        }

        // use box as pointer to avoid value capture by closure
        var str1NonBlankStartIndex = getFirstNonBlankIndexOfStr(str1NoLineBreak, false)
        if(str1NonBlankStartIndex == -1){
            return 0
        }

        var str1NonBlankEndIndex = getFirstNonBlankIndexOfStr(str1NoLineBreak, true)
        if(str1NonBlankEndIndex == -1){
            return 0
        }

        var str2NonBlankStartIndex = getFirstNonBlankIndexOfStr(str2NoLineBreak, false)
        if(str2NonBlankStartIndex == -1){
            return 0
        }

        var str2NonBlankEndIndex = getFirstNonBlankIndexOfStr(str2NoLineBreak, true)
        if(str2NonBlankEndIndex == -1){
            return 0
        }

        // not enough to expected match count
//        if(((str1NonBlankEndIndex - str1NonBlankStartIndex) < targetMatchCount) || ((str2NonBlankEndIndex - str2NonBlankStartIndex) < targetMatchCount)) {
//            return false
//        }

        // long str contains part of short str
        val containsCount = longerContainsPartOfShorter(str1NoLineBreak, str2NoLineBreak, targetMatchCount, IntRange(str1NonBlankStartIndex, str1NonBlankEndIndex), IntRange(str2NonBlankStartIndex, str2NonBlankEndIndex))
//        if(containsCount > 0) {
//            return containsCount
//        }

        // match starts with
        val startsMatchedCount = matchStartsOrEndsWith(str1NoLineBreak, str2NoLineBreak, false, targetMatchCount, str1NonBlankStartIndex, str2NonBlankStartIndex)
//        if(startsMatchedCount > 0) {
//            return startsMatchedCount
//        }

        // match ends with
        val endsMatchedCount = matchStartsOrEndsWith(str1NoLineBreak, str2NoLineBreak, true, targetMatchCount, str1NonBlankEndIndex, str2NonBlankEndIndex)
//        if(endsMatchedCount > 0) {
//            return endsMatchedCount
//        }

        // return maximum roughly matched chars
        return containsCount.coerceAtLeast(startsMatchedCount).coerceAtLeast(endsMatchedCount)
    }

    /**
     * @param reverse true ends with else starts with
     * @param targetMatchCount match how many chars as matched success, if this value greater than str1 or str2 's length after trimStarts/Ends, this method will return false as not matched
     * @return matched chars count, range is [0, targetMatchCount], both are inclusive
     */
    private fun matchStartsOrEndsWith(str1:String, str2:String, reverse: Boolean, targetMatchCount:Int, str1InitIndex:Int, str2InitIndex:Int): Int {
        val str1Index = Box(str1InitIndex)
        val str2Index = Box(str2InitIndex)

        var matchCount = 0

        val condition = if(reverse) {
            { str1Index.value >= 0 && str2Index.value >= 0 }
        }else {
            { str1Index.value < str1.length && str2Index.value < str2.length }
        }

        val updateStr1Index = if(reverse) {
            { str1Index.value-- }
        }else {
            { str1Index.value++ }
        }

        val updateStr2Index = if(reverse) {
            { str2Index.value-- }
        }else {
            { str2Index.value++ }
        }

        while (condition()) {
            if(str1[updateStr1Index()] == str2[updateStr2Index()]) {
                matchCount++

                if(matchCount >= targetMatchCount) {
                    break
                }
            }else {
                break
            }
        }

        return matchCount
    }

    private fun getFirstNonBlankIndexOfStr(str:String, reverse: Boolean):Int {
        val endIndex = str.length-1
        // empty string
        if(endIndex < 0) {
            return -1
        }

        val idxRange = IntRange(0, endIndex).let { if(reverse) it.reversed() else it }
        for(idx in idxRange) {
            if(str[idx].isWhitespace().not()) {
                return idx
            }
        }

        return -1
    }

    /**
     * @return matched chars count, 0 means non-matched or grater than 0 means matched. and, the returned value may grater than `targetMatchCount`
     */
    private fun longerContainsPartOfShorter(str1: String, str2:String, targetMatchCount:Int, str1NonBlankRange: IntRange, str2NonBlankRange: IntRange): Int {
        var longerRange = str1NonBlankRange
        var shorterRange = str2NonBlankRange

        val (longer, shorter) = if(str1NonBlankRange.let { it.endInclusive - it.start } >= str2NonBlankRange.let { it.endInclusive - it.start }) {
            Pair(str1, str2)
        }else {
            longerRange = str2NonBlankRange
            shorterRange = str1NonBlankRange

            Pair(str2, str1)
        }

        val start = (shorterRange.start + 4).coerceAtMost(shorterRange.endInclusive)
        val end = (shorterRange.endInclusive - 4).coerceAtLeast(shorterRange.start)

        // treat sub string that less than expected match count as not contains
        //不足期望的匹配字符数，当作无匹配
        // targetMatchCount通常大于0，所以如果end - start小于等于0，会在这返回，而等于0即涵盖了subString为空字符串的情况，所以后面不需要专门在sub string为empty时专门返回false了，
        // 另一个这里不判断empty的原因是为了完全遵从targetMatchCount，不然当期望匹配0则当作两个字符串匹配时（只要调用此函数就当作匹配而不管是否真的匹配），代码逻辑会不对
//        if((end - start) < targetMatchCount) {
//            return false
//        }

        // if end-start equals to 0, has 1 chars; if greater than 0, more than 1 char; else, bad range, return 0
        if((end - start) < 0) {
            return 0
        }

        // reached here, at least sub string of `shorter` has 1 char

        val subShorter = shorter.substring(IntRange(start, end))

        // if longer contains sub of shorter, return contained chars count else return 0 (non-matched)
        // if end - start == 0, means subShorter has 1 char, and longer contains it, so should return 1 as matched count rather than 0
        // note: `end - start` may grater than `targetMatchCount`
        return if(longer.contains(subShorter)) (end - start).coerceAtLeast(1) else 0
    }

}
