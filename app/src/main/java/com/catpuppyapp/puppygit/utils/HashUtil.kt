package com.catpuppyapp.puppygit.utils

import at.favre.lib.crypto.bcrypt.BCrypt

private const val TAG = "HashUtil"

object HashUtil {
    fun hash(str:String):String {
        return BCrypt.withDefaults().hashToString(12, str.toCharArray())
    }

    fun verify(str:String, hash:String):Boolean {
        try {
            return BCrypt.verifyer().verify(str.toCharArray(), hash).verified
        }catch (e:Exception) {
            MyLog.e(TAG, "verify hash err: ${e.stackTraceToString()}")
            return false
        }
    }
}
