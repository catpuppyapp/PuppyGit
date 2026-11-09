package com.catpuppyapp.puppygit.screen.shared

enum class DiffFromScreen(val code: String) {
    HOME_CHANGELIST("1"),
    INDEX("2"),
    TREE_TO_TREE("3"),
    FILE_HISTORY_TREE_TO_LOCAL("4"),
    FILE_HISTORY_TREE_TO_PREV("5")

    ;

    companion object {
        fun fromCode(code: String): DiffFromScreen? {
            return entries.find { it.code == code }
        }

        fun isFromFileHistory(item: DiffFromScreen) = item == FILE_HISTORY_TREE_TO_LOCAL || item == FILE_HISTORY_TREE_TO_PREV
        fun isFromFileHistory(code: String) = code == FILE_HISTORY_TREE_TO_LOCAL.code || code == FILE_HISTORY_TREE_TO_PREV.code
    }
}
