package com.catpuppyapp.puppygit.dto

interface ItemKey {
    /**
     * this function promise when `instanceA.equals(instanceB)` is true, return same key
     * 本函数确保在两个实例equals的情况下返回相同key
     *
     * 应用场景：主要是用来更新最后点击条目的，作为条目的唯一id之类的东西
     */
    fun getItemKey():String
}
