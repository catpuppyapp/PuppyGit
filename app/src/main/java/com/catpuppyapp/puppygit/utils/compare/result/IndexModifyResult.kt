package com.catpuppyapp.puppygit.utils.compare.result

data class IndexModifyResult (
    /**
     * 是否有匹配
     */
    val matched:Boolean,

    /**
     * 是否通过倒序匹配成功
     * 注：正序从左到右；倒序从右到左
     */
    val matchedByReverseSearch:Boolean,

    val add:List<IndexStringPart>,
    val del:List<IndexStringPart>

) {

    /**
     * 把索引转换成字符串例如：
     * 源字符串 "abc"，索引[0-1, 1-3]，返回数组 ["a", "bc"]
     */
    fun toStringModifyResult(addSrc:String, delSrc:String): StringModifyResult {
        val addList = mutableListOf<StringPart>()
        val delList = mutableListOf<StringPart>()

        for(s in add) {
            addList.add(s.toStringPart(addSrc))
        }

        for(s in del) {
            delList.add(s.toStringPart(delSrc))
        }

        return StringModifyResult(matched, matchedByReverseSearch, addList, delList)
    }

    /**
     * 和String版一样，只不过入参改成了数组
     */
    fun toStringModifyResult(addSrc:CharArray, delSrc:CharArray): StringModifyResult {
        val addList = mutableListOf<StringPart>()
        val delList = mutableListOf<StringPart>()

        for(s in add) {
            addList.add(s.toStringPart(addSrc))
        }

        for(s in del) {
            delList.add(s.toStringPart(delSrc))
        }

        return StringModifyResult(matched, matchedByReverseSearch, addList, delList)
    }

    // data class自动实现toString()，无需手动实现了
//    override fun toString(): String {
//        return "IndexModifyResult(matched=$matched, matchedByReverseSearch=$matchedByReverseSearch, add=$add, del=$del)"
//    }

}
