package com.catpuppyapp.puppygit.constants

enum class IndentChar(val char: Char) {
    TAB('\t'),
    SPACE(' '),

    ;
    companion object {
        fun isIndent(char: Char) = char == TAB.char || char == SPACE.char
    }
}
