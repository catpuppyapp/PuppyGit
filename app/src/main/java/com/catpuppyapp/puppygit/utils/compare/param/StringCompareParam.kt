package com.catpuppyapp.puppygit.utils.compare.param

class StringCompareParam(
    chars:String,
    length:Int,
): CompareParam<String>(chars, length) {
    override fun getTextNoEndOfNewLine(): CompareParam<String> {
        return if(hasEndOfNewLine()) {
            StringCompareParam(chars, length - 1)
        }else {
            this
        }
    }
}
