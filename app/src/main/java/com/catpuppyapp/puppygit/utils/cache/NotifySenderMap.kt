package com.catpuppyapp.puppygit.utils.cache


object NotifySenderMap:CacheStoreImpl(){
    fun genKey(repoId:String, sessionId:String):String {
        return "$repoId-$sessionId"
    }
}
