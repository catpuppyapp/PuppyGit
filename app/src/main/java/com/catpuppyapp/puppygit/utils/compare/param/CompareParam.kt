package com.catpuppyapp.puppygit.utils.compare.param

abstract class CompareParam<T: CharSequence>(
    protected val chars:T,
    protected val length:Int,
) {
    fun getLen(): Int {
        return length
    }

    fun getChar(index:Int): Char {
        if(index < 0 || index >= length) {
            // [0, length) 左闭右开
            throw IndexOutOfBoundsException("index=$index, range is [0, $length)")
        }

        return chars[index]
    }

    fun isEmpty(): Boolean {
        return length < 1
    }

    fun isOnlyLineSeparator(): Boolean {
        return getLen()==1 && getChar(0)=='\n'
    }

    /**
     * at the end of text, has '\n' or not
     */
    fun hasEndOfNewLine(): Boolean {
        return length > 0 && getChar(length - 1) == '\n'
    }

    /**
     * @return return the data without end of line break "\n", no promise copy data or just return a view(aka window) of origin data
     */
    abstract fun getTextNoEndOfNewLine():CompareParam<T>

    fun identical(other:CompareParam<T>):Boolean {
        return this.length == other.length && this.chars === other.chars
    }
}
