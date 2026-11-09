package com.catpuppyapp.puppygit.screen.functions

import com.catpuppyapp.puppygit.jni.SshAskUserUnknownHostRequest
import com.catpuppyapp.puppygit.utils.getFirstOrNullThenRemove
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object KnownHostRequestStateMan {
    private lateinit var list: MutableList<SshAskUserUnknownHostRequest>
    private val mutex:Mutex = Mutex()
    fun init(requestList:MutableList<SshAskUserUnknownHostRequest>) {
        list = requestList
    }

    suspend fun getFirstThenRemove():SshAskUserUnknownHostRequest? {
        mutex.withLock {
            return getFirstOrNullThenRemove(list)
        }
    }

    suspend fun addToList(request: SshAskUserUnknownHostRequest) {
        mutex.withLock {
            list.add(request)
        }
    }
}
