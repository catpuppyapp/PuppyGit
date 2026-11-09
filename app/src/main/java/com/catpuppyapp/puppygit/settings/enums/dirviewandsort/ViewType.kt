package com.catpuppyapp.puppygit.settings.enums.dirviewandsort

enum class ViewType(val code: Int) {
    LIST(1),
    GRID(2);

    companion object {
        fun fromCode(code: Int): ViewType? {
            return entries.find { it.code == code }
        }
    }
}
