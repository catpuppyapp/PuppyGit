package com.catpuppyapp.puppygit.datastruct

import java.util.LinkedList

class Stack<T>(val store:LinkedList<T> = LinkedList()) {
    fun getFirst():T? {
        // peek的好处是不会抛异常，不需要我trycatch
        return store.peek()
    }

    fun getLast():T? {
        return store.peekLast()
    }

    fun push(item:T) {
        store.push(item)
    }

    fun pop():T? {
        return store.pollFirst()
    }

    fun size():Int {
        return store.size
    }

    fun clear() {
        store.clear()
    }

    fun isEmpty():Boolean {
        return store.isEmpty()
    }

    fun isNotEmpty():Boolean {
        return store.isNotEmpty()
    }
}
