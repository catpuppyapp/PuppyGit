package com.catpuppyapp.puppygit.utils

import com.catpuppyapp.puppygit.constants.Cons

object RegexUtil {
    private const val extMatchFlag = "*."
    private const val extFlagLen = extMatchFlag.length
    private const val extFlagLenSubOne = extFlagLen-1  //减1是为了保留后缀名的"."，例如"*.txt"，substring 后得到 ".txt"，不减1就变成 "txt" 了

    private const val slash = Cons.slashChar
    private const val spaceChar = ' '

    /**
     *
     * *.ext 可匹配后缀，多个*.可匹配不同后缀，只要目标匹配任一后缀就返回真，若有其他关键词例如"*.md *.txt filename_part1 part2"则会变成目标匹配任一后缀且包含其他所有关键词则返回真
     *
     *
     * example:
     *     val input = "TARGET_FILE.md".lowercase()
     *     val pattern = listOf(
     *         "tar",  // match, true
     *         "tar fi*.md",   // match, true
     *         "tar file *.md",  // match, true
     *         "t?r",   // no-match, false, '?' is not treat as placeholder here, 这个不会把?当作占位符
     *         "*.md",  // match, true
     *     )
     *
     *     for (p in pattern) {
     *         println(matchWildcard(input, p))
     *     }
     *
     */
    fun matchWildcard(target: String, keyword: String, ignoreCase: Boolean = true): Boolean {
        //若input为空，不管pattern是什么（即使也为空），都返回false
        if(target.isEmpty() || keyword.isEmpty()) {
            return false
        }

        //完全匹配
        if(target == keyword) {
            return true
        }


        //不完全匹配，尝试匹配后缀名和关键词，若target匹配任一后缀名且包含所有关键词则返回true
        val splitKeyword = keyword.split(spaceChar)
        if(splitKeyword.isEmpty()) {
            return false
        }


        var needMatchExt = false
        var extMatched = false
        var validKeyword = false  //指示keyword是否有效（非空即有效）
        for (k in splitKeyword) {
            if(k.isEmpty()) {
                continue
            }

            validKeyword = true

            if(k.length > extFlagLen && k.startsWith(extMatchFlag, ignoreCase = ignoreCase)) {  //有后缀名。（注意："*." 会被认为无后缀名，然后去匹配else的 keyword part部分）
                needMatchExt = true

                if(!extMatched) {  //若已匹配后缀成功则不需要再匹配
                    extMatched = target.endsWith(k.substring(extFlagLenSubOne), ignoreCase = ignoreCase)  // `extFlagLen - 1` to keep '.', e.g. "*.txt" will got ".txt"
                }
            }else {  // keyword part，部分关键字
                if(target.contains(k, ignoreCase = ignoreCase).not()) {  //不包含某一非后缀名的关键词则直接返回假
                    return false
                }
            }
        }


        //执行到这里，非后缀名的关键字片段必然全部匹配或者不需要匹配（注：若关键字仅包含 "*.txt" 之类的后缀名则不需要匹配关键字片段），后面仅需要判断是否匹配后缀即可。


        //若需要匹配后缀且匹配成功，则返回真（keyword必然包含"*.txt"之类的后缀）；否则若关键字有效（包含非空的片段），则返回真（keyword无"*.txt"之类后缀，但包含有效关键字且全部匹配）。
        //配合上述已经全部匹配关键字的逻辑，这个return的含义为：后缀和关键字均匹配成功则返回真；关键字为空或匹配失败则返回假。
        // 若需要匹配ext，则返回ext是否匹配（实际为ext和关键词片段是否全部匹配）；否则返回关键词是否有效（关键词片段非空且是否全部匹配）
        return if(needMatchExt) extMatched else validKeyword
    }



    fun matchWildcardList(target: String, keywordList:List<String>, ignoreCase:Boolean = true):Boolean {
        return matchByPredicate(target, keywordList) { target, keyword ->
            matchWildcard(target, keyword, ignoreCase)
        }
    }


    fun matchByPredicate(target: String, keywordList:List<String>, predicate:(target:String, keyword:String)->Boolean):Boolean {
        if(target.isEmpty() || keywordList.isEmpty()) {
            return false
        }

        for (keyword in keywordList) {
            if(predicate(target, keyword)) {
                return true
            }
        }

        return false
    }

    fun equalsOrEndsWithExt(target: String, keywordList: List<String>, ignoreCase: Boolean = true): Boolean {
        for(k in keywordList) {
            if(k.equals(target, ignoreCase = ignoreCase)) {
                return true
            }

            // *.ext
            if(k.startsWith(extMatchFlag)) {
                // suffix is extension starts with '.', e.g. ".ext"
                val suffix = k.substring(extMatchFlag.length - 1)
                if(target.endsWith(suffix, ignoreCase = ignoreCase)) {
                    return true
                }
            }
        }

        return false
    }
}

