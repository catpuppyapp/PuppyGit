package com.catpuppyapp.puppygit.utils.iterator

/**
 * no copy, no tread safe iterator, but faster
 * 无拷贝，线程不安全的iterator，但是快
 *
 * @isReversed indicate the iteration order is reversed or not
 * @author Bandeapart1964 of catpuppyapp
 */
open class NoCopyIterator<T>(
    val srcList:MutableList<T>,
    val isReversed:Boolean = false,
):MutableIterator<T> {

    private fun resetCurrentIndex() = if(isReversed) srcList.size - 1 else 0

    protected open var currentIndex:Int = resetCurrentIndex()

    open fun reset(){
        currentIndex = resetCurrentIndex()
    }

    open fun srcIsEmpty():Boolean {
        return srcList.isEmpty()
    }


    override fun hasNext(): Boolean {
        return currentIndex >= 0 && currentIndex < srcList.size
    }

    override fun next(): T {
        return if(isReversed) srcList[currentIndex--] else srcList[currentIndex++]
    }

    override fun remove() {
        //if reverse, shouldn't update current index,
        //   because next element which expect to compare,
        //   still at the `currentIndex`
        if(isReversed) srcList.removeAt(currentIndex + 1) else srcList.removeAt(--currentIndex)
    }
}
