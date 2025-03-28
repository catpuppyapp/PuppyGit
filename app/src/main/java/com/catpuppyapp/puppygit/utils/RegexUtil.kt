package com.catpuppyapp.puppygit.utils

object RegexUtil {

    /**
     * e.g. input abc.txt, pattern *.txt, will matched
     */
    fun matchWildcard(input: String, pattern: String): Boolean {
        //若input为空，不管pattern是什么（即使也为空），都返回false
        if(input.isEmpty() || pattern.isEmpty()) {
            return false
        }

        // 将通配符转换为正则表达式
        val regexPattern = pattern
            .replace(".", "\\.")  // 转义点号
            .replace("*", ".*")   // 将 * 替换为 .*
            .replace("?", ".")    // 将 ? 替换为 .
        return Regex(regexPattern).matches(input)
    }

    fun matchForIgnoreFile(input:String, pattern:String):Boolean {
        if(input.isEmpty() || pattern.isEmpty()) {
            return false
        }

        if(input.startsWith(pattern)) {
            return true
        }

        return matchWildcard(input, pattern)
    }


    fun matchWildcardList(input: String, patternList:List<String>, ignoreCase:Boolean):Boolean {
        return matchByPredicate(input, patternList, ignoreCase) { i, p ->
            matchWildcard(i, p)
        }
    }

    /**
     * @param ignoreCase if false, will pass origin input and pattern to predicate , else pass lowercased input and pattern.
     */
    fun matchByPredicate(input: String, patternList:List<String>, ignoreCase:Boolean, predicate:(input:String, pattern:String)->Boolean):Boolean {
        if(input.isEmpty() || patternList.isEmpty()) {
            return false
        }

        val input = if(ignoreCase) input.lowercase() else input

        for (pattern in patternList) {
            if(predicate(input, pattern.let { if(ignoreCase) pattern.lowercase() else pattern })) {
                return true
            }
        }

        return false
    }
}

