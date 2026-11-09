package com.catpuppyapp.puppygit.utils.compare.search

import com.catpuppyapp.puppygit.utils.compare.param.CompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import java.util.*

abstract class Search {
    companion object {
        val INSTANCE = SearchOn()  // normal for match, but fast
        val INSTANCE_BETTER_MATCH_BUT_SLOW = SearchOnm()  // better for match, but slow
    }

    abstract fun<T:CharSequence> doSearch(add: CompareParam<T>, del: CompareParam<T>, reverse: Boolean): IndexModifyResult

    protected fun getAddListMethod(reverse: Boolean) : (Int, Int, Boolean, LinkedList<IndexStringPart>, IndexStringPart?)-> IndexStringPart? {
        return if(reverse) { start:Int, end:Int, modified:Boolean, list: LinkedList<IndexStringPart>, lastItem: IndexStringPart? ->
            var last = lastItem
            val len = start - end
            if(len>0) {
                //先判断last条目是否被更新过，再判断modified是否相同，若相同则更新旧条目索引，否则创建新条目并添加到列表
                if(last?.modified == modified) {
                    last.start = end+1
                }else {
                    val new = IndexStringPart(end+1, start+1, modified)
                    list.addFirst(new)
                    last=new
                }
            }

            last  //返回 last，应将其赋值给lastItem参数的源变量
        }else { start:Int, end:Int, modified:Boolean, list: LinkedList<IndexStringPart>, lastItem: IndexStringPart? ->
            var last = lastItem
            val len = end-start
            if(len>0) {
                if(last?.modified == modified) {
                    last!!.end = end
                }else {
                    val new = IndexStringPart(start, end, modified)
                    list.add(new)
                    last=new
                }
            }

            last
        }
    }

}
