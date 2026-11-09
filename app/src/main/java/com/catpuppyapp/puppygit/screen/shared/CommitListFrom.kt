package com.catpuppyapp.puppygit.screen.shared

enum class CommitListFrom(val code: String) {
    BRANCH("1"),

    // from repos or changelist, need follow head changes
    FOLLOW_HEAD("2"),
    OTHER("3"),
    TAG("4"),
    ;

    companion object {
        fun fromCode(code: String): CommitListFrom? {
            return entries.find { it.code == code }
        }
    }
}
