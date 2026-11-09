package com.catpuppyapp.puppygit.utils

import com.catpuppyapp.puppygit.constants.Cons

object StrListUtil {

    fun strToList(str:String, splitSign:String):List<String> {
        val list = mutableListOf<String>()
        str.split(splitSign).forEachBetter {
            val s = it.trim()
            if(s.isNotBlank()) {
                list.add(s)
            }
        }

        return list
    }

    fun listToStr(list:List<String>, splitSign: String):String {
        val sb = StringBuilder()
        list.forEachBetter { sb.append(it).append(splitSign) }

        return sb.removeSuffix(splitSign).toString()
    }

    fun linesToList(str:String):List<String> {
        return strToList(str, Cons.lineBreak)
    }

    fun listToLines(list:List<String>):String {
        return listToStr(list, Cons.lineBreak)
    }

    fun csvStrToList(csvStr:String):List<String> {
        return strToList(csvStr, Cons.comma)
    }

    fun listToCsvStr(list:List<String>):String {
        return listToStr(list, Cons.comma)
    }

}
