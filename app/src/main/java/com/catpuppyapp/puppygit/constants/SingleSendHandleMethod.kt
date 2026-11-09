package com.catpuppyapp.puppygit.constants

/**
 * 外部分享单文件到app的处理策略
 */
enum class SingleSendHandleMethod(val code: String) {
    /**
     * 需要询问
     */
    NEED_ASK("1"),

    /**
     * 编辑，跳转到Editor
     */
    EDIT("2"),

    /**
     * 导入，跳转到Files
     */
    IMPORT("3");

    companion object {
        fun fromCode(code: String): SingleSendHandleMethod? {
            return entries.find { it.code == code }
        }
    }
}
