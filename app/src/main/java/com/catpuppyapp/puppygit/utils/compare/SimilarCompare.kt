package com.catpuppyapp.puppygit.utils.compare

import com.catpuppyapp.puppygit.utils.compare.param.CompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.search.Search
import com.catpuppyapp.puppygit.utils.compare.search.SearchDirection

interface SimilarCompare {
    companion object {
        val INSTANCE: SimilarCompare = SimilarCompareImpl()
    }

    /**
     * try find `del` in `add`, if you want to find `add` in `del` just simple swap params order when calling.
     * 虽然参数add在前del在后，但我在实际使用过一段时间后发现del先add后的匹配率更高，所以后来我改成了先传del后传add
     *
     * must match:
     * add:12345, del: 1245, matched: 12, 45
     * add:12356, del: 12, matched:12
     *
     * if `requireBetterMatching` is true,  matching:
     * add: 123a45, del:123b45,
     *
     * in most cases, when `requireBetterMatching` is true, means bad performance and good matching, in other word, when it is false, good performance and bad matching
     *
     * if `reverseMatchIfNeed`, maybe will try reverse matching when forward matching failed, if do reverse matching, the time complex may x2
     *
     * @param matchByWords if true, will try compare text by words, the text should split by blank char like space/tab/lineBreak etc
     * @param ignoreEndOfNewLine if true, will ignore end of line break '\n', only ignore '\n'
     *          and only ignore when '\n' at end of line, if both has '\n', will treat '\n' as no-modified, else modified,
     *          but it will not effect the `matched` of `IndexModifyResult`, so, modified or no-modified, maybe not effect for view (因为新行的匹配状态不会影响结果是否匹配，所以显示上可能没差)
     * @param treatNoWordMatchAsNoMatchedWhenMatchByWord if true, will treat as no matched when match by words and only non-word (e.g. punctuations) chars matched
     *
     * @param emptyAsMatched if true, when any line is empty, return a matched result; else return a unmatched result
     * @param emptyAsModified if true, when a line is empty, return a string part that set modified to true
     *
     * @author Bandeapart1964 of catpuppyapp
     */
    fun<T:CharSequence> doCompare(
        add: CompareParam<T>,
        del: CompareParam<T>,

        // 空行当作匹配（使用 string part，若emptyAsModified为假，则背景为浅色，否则深色），若为假则当作不匹配（不使用string part，背景一定为深色）
        // treat empty line as matched or not, if matched and `emptyAsModified` is false, the background color will be shadow, else deep
        emptyAsMatched: Boolean = false,
        // 把空字符当作修改过（深色背景）还是没修改过（浅色背景）
        // treat empty str as modified or not, if as modified, the background will be deep, else shadow
        emptyAsModified: Boolean = true,

        // this param not in use, may will remove in the future, the `ignoreEndOfNewLine` can cover it's use case
//        onlyLineSeparatorAsEmpty: Boolean = true,

        searchDirection: SearchDirection = SearchDirection.FORWARD_FIRST,
        requireBetterMatching: Boolean = false,
        search: Search = Search.INSTANCE,
        betterSearch: Search = Search.INSTANCE_BETTER_MATCH_BUT_SLOW,
        matchByWords:Boolean,
        ignoreEndOfNewLine:Boolean = true,
        degradeToCharMatchingIfMatchByWordFailed:Boolean = false,
        treatNoWordMatchAsNoMatchedWhenMatchByWord:Boolean = false,
    ): IndexModifyResult

}
