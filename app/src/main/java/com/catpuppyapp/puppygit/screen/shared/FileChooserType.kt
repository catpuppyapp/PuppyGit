package com.catpuppyapp.puppygit.screen.shared

enum class FileChooserType(val code: String) {
    SINGLE_FILE("1"),
    SINGLE_DIR("2"),
    ;

    companion object {
        fun fromCode(code: String): FileChooserType? {
            return FileChooserType.entries.find { it.code == code }
        }
    }
}
