package com.catpuppyapp.puppygit.utils.compare.search

import com.catpuppyapp.puppygit.utils.compare.param.CompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import java.util.*


class SearchOn: Search() {
    /**
     *
     * reverse为true时，从末尾向开头搜索（从右到左）；为false时，从开头向末尾搜索（从左到右）。
     * 主要思路：
     *  尝试在add中查找del，如果匹配不到，则更新add索引，如果匹配到，则将匹配到的内容添加为“伪删除和伪添加列表”，
     *  然后更新索引，继续匹配，直到匹配到add或del的末尾
     *
     *  时间复杂度O(n) ，如果调用时采用`正向搜索无匹配则反向搜索 或 反向搜索无匹配则正向搜索`的逻辑，则时间复杂度翻倍为O(2n)。 ps: n 为addArr的长度
     *
     *  可匹配举例：
     *  addArr: 123, delArr:13，能匹配到1和3
     *  add: 12345, del: 012345，forwardSearch无法匹配，但reverseSearch能匹配到12345
     *  add: 12345或123b45, del: 123a45, forwardSearch匹配123，reverseSearch匹配45。注：时间复杂度为O(nm)的版本能匹配到这种字符串，能成功匹配到123和45。
     *
     *
     *  本函数正向逆向均无法匹配，但O(nm)时间复杂度版本可匹配的情况：
     *  add: a123a, del: b123b
     *  add: 123, del:b123b
     */
    /*
       一些隐含matching状态的情况(注意：如果以后修改代码，若不确定是否可推理出matching状态，可直接把字面量false或true替换为matching的值，例如代码中的note1和note2其实按逻辑来说，应写成 "!matching")：
         - delCur!=delStart时matching必然为true，反之，若两者相等，matching必然为false: 因为delCur只在matching时更新，而delStart会在非matching时更新为delCur的值，所以，如果两个值不一样，必然matching为true，代码中对此情况的应用参见"note1"。
         - delCur==delEnd时matching必然为true，但注意，若两者不相等，并不能确定matching的值: 因为delCur只在matching时更新，而delEnd是常量，所以，如果两者相等，delCur必然发生了变化，此时matching必然为true。代码中对此情况的应用参见"note2"
     */
    override fun<T:CharSequence> doSearch(
        add: CompareParam<T>,
        del: CompareParam<T>,
        reverse: Boolean
    ): IndexModifyResult {
        val addList = LinkedList<IndexStringPart>()
        val delList = LinkedList<IndexStringPart>()

        var hasSameChars = false
        //如果反向查找，初始化索引为最后一个元素的索引，否则为第一个元素索引
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
            if(addCur==addEnd && delCur==delEnd) {  // implicit `matching` is true
                //正常情况下，两个同时抵达end，必然是正在matching，所以modified应该为false
                //注意：这里不要写成!matching，直接写false比较合理，不然如果两个都是空字符串没执行到后面的代码，而modified又为假，就会错误认为字符串修改过。
                addToList(addStart, addEnd, false, addList, lastAdd)

                addToList(delStart, delEnd, false, delList, lastDel)

                break
            }else if(addCur == addEnd) {
                addToList(addStart, addEnd, !matching, addList, lastAdd)

                lastDel = addToList(delStart, delCur, !matching, delList, lastDel)

                addToList(delCur, delEnd, true, delList, lastDel)

                break
            }else if (delCur == delEnd) {  //note2 //implicit `matching` is true, because delCur only changed when matching is true
                addToList(delStart, delEnd, false, delList, lastDel)

                lastAdd = addToList(addStart, addCur, false, addList, lastAdd)

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
                    matching = false

                }

                updateAddIndex()  // update add cursor for match next char

            }
        }

        return IndexModifyResult(matched = hasSameChars, matchedByReverseSearch = reverse, addList, delList)
    }
}
