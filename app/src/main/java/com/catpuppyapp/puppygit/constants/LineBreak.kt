package com.catpuppyapp.puppygit.constants

enum class LineBreak(val value: String, val visibleValue: String) {
    // old mac
    CR("\r", "\\r"),

    // unix and modern mac
    LF("\n", "\\n"),

    // windows
    CRLF("\r\n", "\\r\\n")

    ;

    companion object {
        val list = listOf(CR, LF, CRLF)

        fun getType(value:String, default: LineBreak?): LineBreak? {
            return if(value == CR.value) {
                CR
            }else if(value == LF.value) {
                LF
            }else if(value == CRLF.value) {
                CRLF
            }else {
                default
            }
        }
    }
}
