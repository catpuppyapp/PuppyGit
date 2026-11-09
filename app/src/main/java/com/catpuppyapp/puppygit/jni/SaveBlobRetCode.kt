package com.catpuppyapp.puppygit.jni


enum class SaveBlobRetCode(val code:Int) {
    SUCCESS(0),

    // c 代码出错
    /**
     * 把 java String的save path转成c字符串失败
     */
    ERR_CAST_SAVE_PATH_TO_C_STR_FAILED(-1),

    // java 代码出错
    ERR_RESOLVE_TREE_FAILED(-2),
    ERR_RESOLVE_ENTRY_FAILED(-3),
    ERR_RESOLVE_BLOB_FAILED(-4),

    ;

    companion object {
        fun fromCode(code: Int): SaveBlobRetCode? {
            return SaveBlobRetCode.entries.find { it.code == code }
        }
    }
}
