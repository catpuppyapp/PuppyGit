package com.catpuppyapp.puppygit.utils.compare.search


import com.catpuppyapp.puppygit.utils.compare.param.CompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import java.util.*

class SearchOnm: Search() {
    /**
     *
     * 时间复杂度 O(nm)，n 为addArr的长度，m为delArr的长度
     * 和O(n)版算法主要区别在于能匹配以下几种情况:
     * addArr: 12345或123b45，delArr: 123a45 这种情况，此算法可完整匹配到123和45
     * addArr: 12345, delArr: a12345b，能匹配到12345
     * addArr: a12345a, delArr: b12345b，能匹配到12345
     *
     * 另外：
     * 此算法如果正向无匹配，逆向必然也无匹配，所以在正向无匹配时调用逆向匹配意义不大，不过正向和逆向有可能匹配到不同的内容片段，如果想比较哪个匹配连续片段多就采用哪个结果的话，分别调用正向和逆向匹配是有意义的，但会消耗多一倍的时间。
     *
     * 注意：在能够匹配的情况下，正向匹配和逆向匹配的结果有可能不同，例如：
     * 在 ababcdefef 中查找 abef ，如果正向匹配，会匹配到第一个ab和ef；若逆向匹配则会匹配到最后一个ef和ab。
     */
    override fun<T:CharSequence> doSearch(add: CompareParam<T>, del: CompareParam<T>, reverse: Boolean): IndexModifyResult {
        val addList = LinkedList<IndexStringPart>()
        val delList = LinkedList<IndexStringPart>()

        var hasSameChars = false
        var addCur = if(reverse) add.getLen()-1 else 0
        val addEnd = if(reverse) -1 else add.getLen()
        var delCur = if(reverse) del.getLen()-1 else 0
        val delEnd = if(reverse) -1 else del.getLen()
        var matching = false
        var addStart=addCur
        var delStart=delCur

        var lastAdd: IndexStringPart? = null
        var lastDel: IndexStringPart? = null

        val updateAddIndex = if(reverse){{addCur--}} else {{addCur++}};
        val updateDelIndex = if(reverse){{delCur--}} else {{delCur++}};

        val addToList = getAddListMethod(reverse)

        while (true) {
            if(addCur==addEnd && delCur==delEnd) {
                addToList(addStart, addEnd, !matching, addList, lastAdd)

                addToList(delStart, delEnd, !matching, delList, lastDel)

                break
            }else if(addCur == addEnd) {//隐含  delCur!=delEnd  //如果add到结尾，del索引加1，add索引重置为addStart，未匹配内容添加到末尾；
                if(matching) {
                    addToList(addStart, addEnd, false, addList, lastAdd)

                    lastDel = addToList(delStart, delCur, false, delList, lastDel)

                    addToList(delCur, delEnd, true, delList, lastDel)

                    break
                }else { // note11
                    //如果没正在匹配的话，这时delCur和delStart应相等
                    //assert(delCur == delStart)  // should be true
                    lastDel = addToList(delStart, if(reverse) delStart-1 else delStart+1, true, delList, lastDel)

                    updateDelIndex()

                    delStart = delCur
                    addCur=addStart
                }

            }

            //这里不能用else if了，得单独判断这个条件才行，因为前面的代码有可能递增delCur
            if (delCur == delEnd) {  //如果del到结尾，把delstart到end内容添加为 !matching，把addstart到cur也添加为 !matching
                addToList(delStart, delEnd, !matching, delList, lastDel)

                lastAdd = addToList(addStart, addCur, !matching, addList, lastAdd)

                addToList(addCur, addEnd, true, addList, lastAdd)

                break
            }

            val addCurItem = add.getChar(addCur)
            val delCurItem = del.getChar(delCur)
            if(addCurItem == delCurItem) {
                hasSameChars=true

                if(!matching) {
                    // save non-match part at current start to the current first match index
                    lastAdd = addToList(addStart, addCur, true, addList, lastAdd)

                    addStart = addCur

                    delStart = delCur

                    matching=true
                }

                updateAddIndex()
                updateDelIndex()
            }else {
                if(matching) {
                    // save addLastMatchIndex..addCur to fake add
                    // save delLastMatchIndex..delCur to fake del
                    lastAdd = addToList(addStart, addCur, false, addList, lastAdd)
                    lastDel = addToList(delStart, delCur, false, delList, lastDel)

                    addStart = addCur
                    delStart = delCur

                    matching=false
                }

                updateAddIndex()  // update add cursor for match next char

            }
        }

        return IndexModifyResult(matched = hasSameChars, matchedByReverseSearch = reverse, addList, delList)

    }

}
