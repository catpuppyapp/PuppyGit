package com.catpuppyapp.puppygit.utils.compare

import com.catpuppyapp.puppygit.utils.compare.param.CompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import com.catpuppyapp.puppygit.utils.compare.search.Search
import com.catpuppyapp.puppygit.utils.compare.search.SearchDirection
import com.catpuppyapp.puppygit.utils.iterator.NoCopyIterator


// 这里不要把泛型写到class上，除非class里有字段是泛型的类型，否则还是写到函数上更好，这样可以用一个类实例处理不同类型的参数，若写类上，得为不同参数创建不同的类实例，麻烦。
class SimilarCompareImpl: SimilarCompare {
    override fun<T:CharSequence> doCompare(
        add: CompareParam<T>,
        del: CompareParam<T>,
        emptyAsMatched:Boolean,
        emptyAsModified:Boolean,
//        onlyLineSeparatorAsEmpty:Boolean,
        searchDirection: SearchDirection,
        requireBetterMatching: Boolean,
        search: Search,
        betterSearch: Search,
        matchByWords:Boolean,
        ignoreEndOfNewLine:Boolean,
        degradeToCharMatchingIfMatchByWordFailed:Boolean,
        treatNoWordMatchAsNoMatchedWhenMatchByWord:Boolean,
    ): IndexModifyResult {
        //比较地址，完全一样
        if(add.identical(del)) {
            return IndexModifyResult(
                matched = true,
                matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), false)),
                listOf(IndexStringPart(0, del.getLen(), false))
            )
        }

        //忽略末尾 \n
        val addWillUse = if(ignoreEndOfNewLine) {
            add.getTextNoEndOfNewLine()
        }else {
            add
        }

        val delWillUse = if(ignoreEndOfNewLine) {
            del.getTextNoEndOfNewLine()
        }else {
            del
        }


        // empty check
        if((addWillUse.isEmpty() && delWillUse.isEmpty())) {  // both are empty and equals
            return IndexModifyResult(matched = emptyAsMatched, matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), emptyAsModified)),
                listOf(IndexStringPart(0, del.getLen(), emptyAsModified)))
        }else if(addWillUse.isEmpty() && delWillUse.isEmpty().not()) {
            return IndexModifyResult(matched = emptyAsMatched, matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), emptyAsModified)),
                listOf(IndexStringPart(0, del.getLen(), true)))
        }else if(addWillUse.isEmpty().not() && delWillUse.isEmpty()) {
            return IndexModifyResult(matched = emptyAsMatched, matchedByReverseSearch = false,
                listOf(IndexStringPart(0, add.getLen(), true)),
                listOf(IndexStringPart(0, del.getLen(), emptyAsModified)))
        }



        // when reached here, `add` 100% is not empty, `del` neither







        var result:IndexModifyResult? = null

            // match by words
        if(matchByWords) {
            //如果按单词比较为真，尝试以单词比较，如果有匹配，直接返回，如果无匹配，则继续往下执行普通比较
            result = doMatchByWords(addWillUse, delWillUse, requireBetterMatching, treatNoWordMatchAsNoMatchedWhenMatchByWord)
        }

        // if match by words not enabled or not matched, try match by chars
        if(result == null || (result.matched.not() && degradeToCharMatchingIfMatchByWordFailed)) {
            // match by chars
            val reverse = searchDirection == SearchDirection.REVERSE || searchDirection == SearchDirection.REVERSE_FIRST

            val reverseMatchIfNeed = searchDirection == SearchDirection.REVERSE_FIRST || searchDirection == SearchDirection.FORWARD_FIRST

            //用On算法最坏的情况是正向匹配一次，然后逆向匹配一次，时间复杂度为 O(2n)，正常情况时间复杂度为O(n)，O(nm)若匹配两次，也会翻倍，最坏时间复杂度变成O(2nm)
            result = if(requireBetterMatching) {
                betterSearch.doSearch(addWillUse, delWillUse, reverse)
            }else {
                search.doSearch(addWillUse, delWillUse, reverse)
            }

            //反向查找，如果需要的话。判断条件是：如果 “允许反向匹配 且 正向没匹配到”
            if(reverseMatchIfNeed && !result.matched) {
                result = if(requireBetterMatching) betterSearch.doSearch(addWillUse, delWillUse, !reverse) else search.doSearch(addWillUse, delWillUse, !reverse)
            }

        }

        // add newline back to result
        if(ignoreEndOfNewLine) {
            if (add.hasEndOfNewLine()) {
                val addList = result.add as MutableList
                addList.add(
                    IndexStringPart(
                        start = add.getLen()-1,
                        end = add.getLen(),
                        modified = del.hasEndOfNewLine().not()
                    )
                )
            }

            if (del.hasEndOfNewLine()) {
                val delList = result.del as MutableList
                delList.add(
                    IndexStringPart(
                        start = del.getLen()-1,
                        end = del.getLen(),
                        modified = add.hasEndOfNewLine().not()
                    )
                )

            }
        }

        return result

    }

    /**
     * @param requireBetterMatching if true, will try index of for not-matched words
     * @param treatNoWordMatchAsNoMatched if true, will return no match when only spaces and punctuations matched ( haven't any non-space words matched )
     */
    private fun <T:CharSequence> doMatchByWords(
        add: CompareParam<T>,
        del: CompareParam<T>,
        requireBetterMatching: Boolean,
        treatNoWordMatchAsNoMatched:Boolean,
    ):IndexModifyResult {
        val isAppendContentMaybe = maybeIsAppendContent(add, del)

        val addWordSpacePair = getWordAndIndexList(add, requireBetterMatching)
        val delWordSpacePair = getWordAndIndexList(del, requireBetterMatching)

        val addIter = NoCopyIterator(srcList = addWordSpacePair.words as MutableList)
        val delIter = NoCopyIterator(srcList = delWordSpacePair.words as MutableList)


        // save result which will return
        val addIndexResultList = mutableListOf<IndexStringPart>()
        val delIndexResultList = mutableListOf<IndexStringPart>()

//        val addNotMatchedList = mutableListOf<WordAndIndex>()
//        val delNotMatchedList = mutableListOf<WordAndIndex>()
//        addNotMatchedList.addAll(addWordAndIndexList)
//        delNotMatchedList.addAll(delWordAndIndexList)

        // index equals this, will remove
//        val removeMark = -1

//        var delMatchedCount = 0
//        val delAllCount = delWordAndIndexList.size

        // match words by equals
        var matched = doEqualsMatchWordsOrSpaces(addIter, delIter, addIndexResultList, delIndexResultList)

        if(treatNoWordMatchAsNoMatched && !matched) {
            return IndexModifyResult(
                matched = false,
                matchedByReverseSearch = false,
                add = addIndexResultList,
                del = delIndexResultList
            )
        }


        // use reversed order to compare punctuations when prepend content can be better,
        //   have more chance to increase "neighbor" matched range,
        //   e.g. if append content, have 2 strings: "abc, def" and "abc, def, ghi",
        //   it will matched ", ghi", but if is prepend content and don't use reverse match,
        //   it will matched as "abc" and "def" and the ", " before the "ghi",
        //   rather than the ", " after the "abc"
        val addSpaceIter = NoCopyIterator(srcList = addWordSpacePair.spaces as MutableList, isReversed = !isAppendContentMaybe)
        val delSpaceIter = NoCopyIterator(srcList = delWordSpacePair.spaces as MutableList, isReversed = !isAppendContentMaybe)

        // match spaces by equals
        matched = doEqualsMatchWordsOrSpaces(addSpaceIter, delSpaceIter, addIndexResultList, delIndexResultList) || matched


        //add or del, 没有一个被完全匹配完（为空）
        val wordsNotAllMatched = !(addIter.srcIsEmpty() || delIter.srcIsEmpty())
        val spacesNotAllMatched = !(addSpaceIter.srcIsEmpty() || delSpaceIter.srcIsEmpty())
        //if requireBetterMatching is true, try use indexOf matching the not-matched items
        if(requireBetterMatching && (wordsNotAllMatched || spacesNotAllMatched)) {
            if(wordsNotAllMatched) {
                matched = doIndexOfMatchWordsOrSpaces(addIter, delIter, addIndexResultList, delIndexResultList) || matched
            }

            if(spacesNotAllMatched) {
                matched = doIndexOfMatchWordsOrSpaces(addSpaceIter, delSpaceIter, addIndexResultList, delIndexResultList) || matched
            }
        }



        // when reached here, all matched items already added in the `addIndexResultList` and `delIndexResultList`,
        //  and not-matched items in the `addStillNotMatchedList` and `delStillNotMatchedList`
        //  and the order is not sorted yet

        // add not matched items, these items not matched by equals nor by indexOf, they are really `modified` items

        // at last, may still have not matched items, they are `modified`
        addAllToIndexResultList(addIter, delIter, addIndexResultList, delIndexResultList)
        addAllToIndexResultList(addSpaceIter, delSpaceIter, addIndexResultList, delIndexResultList)


        // sortedWith return new List and keep origin list unchanged, sortWith sort in place, here no need new list, so using sortWith
        //根据索引从小到大，原地排序
        addIndexResultList.sortWith(comparator)
        delIndexResultList.sortWith(comparator)


        return IndexModifyResult(
            matched = matched,
            matchedByReverseSearch = false,
            add = addIndexResultList,
            del = delIndexResultList
        )
    }

    private fun <T:CharSequence> maybeIsAppendContent(
        add: CompareParam<T>,
        del: CompareParam<T>,
        //此值越大，越难判定为append，越难判定为append则越大概率使用反转的iterator；
        //  反之，减小此值可增加使用正序iterator的概率
        expectedAppendCount: Int = 4,
    ): Boolean {
        if(expectedAppendCount < 1) {
            return true
        }

        if(add.getLen() < expectedAppendCount
            || del.getLen() < expectedAppendCount
        ) {
            return false
        }


        // try to treat "abc, def);" and "abc, def, ghi);" as append text
        val luckyOffset = -2

        // because prefix maybe have spaces indent, so reverser match is better
        var addIndex = add.getLen() - 1 + luckyOffset
        val delIndex = del.getLen() - 1 + luckyOffset
        // check index out of bounds
        if(addIndex < 0 || addIndex >= add.getLen() || delIndex < 0 || delIndex >= del.getLen()) {
            return false
        }

        val lastCharOfDel = del.getChar(delIndex)
        var appendCount = 0
        while(appendCount < expectedAppendCount) {
            if(add.getChar(addIndex--) == lastCharOfDel) {
                return false
            }

            appendCount++


            if(addIndex < 0 || addIndex >= add.getLen()) {
                return appendCount >= expectedAppendCount
            }

        }

        return true
    }

    private fun addAllToIndexResultList(
        addIter: NoCopyIterator<WordAndIndex>,
        delIter: NoCopyIterator<WordAndIndex>,
        addIndexResultList: MutableList<IndexStringPart>,
        delIndexResultList: MutableList<IndexStringPart>
    ) {
        addIter.reset()
        while (addIter.hasNext()) {
            val item = addIter.next()
            addIndexResultList.add(
                IndexStringPart(
                    start = item.index,
                    end = item.index + item.getWordStr().length,
                    modified = true
                )
            )
        }

        delIter.reset()
        while (delIter.hasNext()) {
            val item = delIter.next()
            delIndexResultList.add(
                IndexStringPart(
                    start = item.index,
                    end = item.index + item.getWordStr().length,
                    modified = true
                )
            )
        }
    }

    private fun doIndexOfMatchWordsOrSpaces(
        addIter: NoCopyIterator<WordAndIndex>,
        delIter: NoCopyIterator<WordAndIndex>,
        addIndexResultList: MutableList<IndexStringPart>,
        delIndexResultList: MutableList<IndexStringPart>
    ): Boolean {
        var matched = false
        addIter.reset()
        while (addIter.hasNext()) {
            val a = addIter.next()
            val addStr = a.getWordStr()

            delIter.reset()
            while (delIter.hasNext()) {
                val d = delIter.next()
                val delStr = d.getWordStr()

                // should try `bigLengthStr.indexOf(smallLengthStr)`
                // 应该用长的indexOf短的，所以这里有的大于判断
                if (addStr.length > delStr.length) {
                    val indexOf = addStr.indexOf(delStr)
                    if (indexOf != -1) {
                        addIter.remove()
                        delIter.remove()

                        matched = true

                        a.matched = true
                        d.matched = true

                        val aStartIndex = a.index + indexOf
                        val aEndIndex = aStartIndex + delStr.length
                        addIndexResultList.add(
                            IndexStringPart(
                                start = aStartIndex,
                                end = aEndIndex,
                                modified = false
                            )
                        )

                        delIndexResultList.add(
                            IndexStringPart(
                                start = d.index,
                                end = d.index + delStr.length,
                                modified = false
                            )
                        )

                        val beforeMatched = addStr.substring(0, indexOf)
                        val afterMatched = addStr.substring(indexOf + delStr.length)
                        if (beforeMatched.isNotEmpty()) {
                            addIndexResultList.add(
                                IndexStringPart(
                                    start = a.index,
                                    end = a.index + beforeMatched.length,
                                    modified = true
                                )
                            )
                        }
                        if (afterMatched.isNotEmpty()) {
                            addIndexResultList.add(
                                IndexStringPart(
                                    start = aEndIndex,
                                    end = aEndIndex + afterMatched.length,
                                    modified = true
                                )
                            )
                        }

                        break
                    }
                } else {
                    val indexOf = delStr.indexOf(addStr)
                    if (indexOf != -1) {
                        addIter.remove()
                        delIter.remove()

                        matched = true

                        a.matched = true
                        d.matched = true

                        addIndexResultList.add(
                            IndexStringPart(
                                start = a.index,
                                end = a.index + addStr.length,
                                modified = false
                            )
                        )

                        val dStartIndex = d.index + indexOf
                        val dEndIndex = dStartIndex + addStr.length
                        delIndexResultList.add(
                            IndexStringPart(
                                start = dStartIndex,
                                end = dEndIndex,
                                modified = false
                            )
                        )

                        val beforeMatched = delStr.substring(0, indexOf)
                        val afterMatched = delStr.substring(indexOf + addStr.length)
                        if (beforeMatched.isNotEmpty()) {
                            delIndexResultList.add(
                                IndexStringPart(
                                    start = d.index,
                                    end = d.index + beforeMatched.length,
                                    modified = true
                                )
                            )
                        }
                        if (afterMatched.isNotEmpty()) {
                            delIndexResultList.add(
                                IndexStringPart(
                                    start = dEndIndex,
                                    end = dEndIndex + afterMatched.length,
                                    modified = true
                                )
                            )
                        }

                        break
                    }
                }

            }


            if (delIter.srcIsEmpty()) {
                break
            }

        }

        return matched
    }

    private fun doEqualsMatchWordsOrSpaces(
        addIter: NoCopyIterator<WordAndIndex>,
        delIter: NoCopyIterator<WordAndIndex>,
        addIndexResultList: MutableList<IndexStringPart>,
        delIndexResultList: MutableList<IndexStringPart>
    ): Boolean {
        var matched = false
        while (addIter.hasNext()) {
            val addWord = addIter.next()
            val addStr = addWord.getWordStr()

            delIter.reset()
            while (delIter.hasNext()) {
                val delWord = delIter.next()
                val delStr = delWord.getWordStr()

                if (addStr == delStr) {
                    addIter.remove()
                    delIter.remove()

                    matched = true

                    addWord.matched = true
                    delWord.matched = true

                    // because is addStr equals delStr, so the addStr.length should equals delStr.length
                    addIndexResultList.add(
                        IndexStringPart(
                            start = addWord.index,
                            end = addWord.index + addStr.length,
                            modified = false
                        )
                    )

                    delIndexResultList.add(
                        IndexStringPart(
                            start = delWord.index,
                            end = delWord.index + delStr.length,
                            modified = false
                        )
                    )

                    break
                }
            }

            if (delIter.srcIsEmpty()) {
                break
            }

        }

        return matched
    }

    private fun<T:CharSequence> getWordAndIndexList(compareParam:CompareParam<T>, requireBetterMatching: Boolean):WordsSpacesPair {
        //拼接相邻的非单词字符（空格、标点符号）：
        //若false，则禁用连接响相邻符号，高匹配率，高内存占用，低性能；
        //若true，则启用连接相邻符号，低匹配率，低内存占用，高性能。
        //注意：连在一起的不一定是相同字符，例如："), "，是有可能存在的，因为它们全都是word separator
        //实际效果：实际上在内存和性能方面差别不大，主要差在匹配率，一般建议设为false（在ui上启用better matching即可）。
        val concatNeighborNonWordChars = !requireBetterMatching

        var wordMatching = false
        var spaceMatching = false
        var wordAndIndex:WordAndIndex? = null

        //20250217 update: 实际上这里已经不是最初的只存空白字符了（空格、tab、换行之类的），现在是把常见的分隔单词的标点符号都存里面了。
        var spaceAndIndex:WordAndIndex? = null


        val wordAndIndexList= mutableListOf<WordAndIndex>()
        val spaceAndIndexList = mutableListOf<WordAndIndex>()

        for(i in 0 until compareParam.getLen()) {
            val char = compareParam.getChar(i)
            if(isWordSeparator(char)) {
                wordMatching = false

                if(concatNeighborNonWordChars && spaceMatching) {  //拼接相邻的非单词字符（空格、标点符号）
                    spaceAndIndex!!.word.append(char)
                }else {
                    spaceAndIndex = WordAndIndex(index = i)
                    spaceAndIndex.word.append(char)
                    spaceAndIndexList.add(spaceAndIndex)
                    spaceMatching = true
                }
            }else {  // not a blank char
                spaceMatching = false

                if(wordMatching) {
                    wordAndIndex!!.word.append(char)
                }else {
                    wordAndIndex = WordAndIndex(index = i)
                    wordAndIndex.word.append(char)
                    wordAndIndexList.add(wordAndIndex)
                    wordMatching = true
                }
            }
        }

//        wordAndIndexList.addAll(spaceAndIndexList)

        return WordsSpacesPair(words = wordAndIndexList, spaces = spaceAndIndexList)
    }


}

private data class WordAndIndex(
    val index:Int=0,
    val word:StringBuilder=StringBuilder(),

    // full match(by equals) or part match(by indexof)
    //完整匹配或部分匹配，只要匹配过，就为true
    var matched:Boolean=false,
) {
    private var wordStrCached:String? = null

    fun getWordStr():String {
        if(wordStrCached==null) {
            wordStrCached = word.toString()
        }

        return wordStrCached!!
    }

}

private data class WordsSpacesPair(
    val words:List<WordAndIndex>,
    val spaces:List<WordAndIndex>
)

// create comparator for sort list by index
// p.s. list compare will not remove same elements
private val comparator = { o1:IndexStringPart, o2:IndexStringPart ->
    o1.start.compareTo(o2.start)
}


//TODO 要不要搞个设置项，让用户可添加自定义的单词分隔符？不过好像意义不大，算了。
private val wordSeparators = listOf(
    // english words separators
    ' ',
    '\n',
    '\r',
    '\t',
    ',',
    '.',
    '<',
    '>',
    '/',
    '?',
    '\\',
    '|',
    '!',
    '(',
    ')',
    '{',
    '}',
    '[',
    ']',
    '`',
    '~',
    '@',
    '#',
    '$',
    '%',
    '^',
    '&',
    '*',
    '+',
    '=',
    '\'',
    '\"',
    ';',
    ':',
    '-',
    '_',



    //chinese words separators
    // 中文这个严格来说是句子分隔符不是单词分隔符！中文根本没有单词分隔符！但这些符号又不是数字也不是字母，所以char.isLetterOrDigit()会返回假
    //  有两种解决方案：1. 禁用中文标点符号 并且 不判断isLetterOrDigit()。 2. 加个排除列表，把中文标点符号加进去，若某个字符在排除列表，直接当作非单词分隔符。
    // 方案1可能会在某些需要判断分隔符的语言中误判，方案2可能会在不需要分隔符的语言中误判。所以关键在于如何知道用户所用的语言是否需要分隔符？若不确定，是当作有分隔符好，还是当作没有好？
    // 最终采用了方案1，若不确定某个字符是否是英文用的那些标点符号，就直接当作这个语言没分隔符好了，若当作有，可能会发生"，我不知道，"和"，你不知道，"完全无法匹配上的问题，但期望的是只有"我"和"你"不同，其他被匹配。
    // 但方案1还是有缺陷，如果我用中文，但使用英文标点符号，就会误判，没有完美的解决方案。
    // 20250413 重新启用：这个可以当作按句子匹配，当按单词匹配打开的时候，对中文就按句子匹配，否则按字匹配，若不加中文标点符号，匹配中文时只能按单字匹配，虽然有时候匹配率还好，但有时候不如按句子匹配，所以启用这个，这样可多一种不同的匹配方式
    '，',
    '。',
    '、',
    '；',
    '：',
    '“',
    '”',
    '‘',
    '’',
    '《',
    '》',
    '〈',
    '〉',
    '【',
    '】',
    '（',
    '）',
    '—',
    '！',
    '？',


)

private fun isWordSeparator(char:Char):Boolean {
    //若启用 !char.isLetterOrDigit() 则会把中文标点符号之类的也当作单词分隔符，在其他不需要单词分隔符的语言中也可能误判，不如直接禁用。
    // 另外：注意我说的是“单词分隔符”不是“句子分隔符”，在有单词分隔符的语言中，例如英语，标点符号既区分句子也区分单词，这时将其视为word separator有意义；
    // 但在没单词分隔符的语言中，例如中文，这时区分标点符号就没意义，还会降低匹配率。
//    return char.isWhitespace() || wordSeparators.contains(char) || !char.isLetterOrDigit()

    return char.isWhitespace() || wordSeparators.contains(char)
}
