package com.catpuppyapp.puppygit.jni

import java.util.Base64


private const val TAG = "SshCert"

private const val dbStringSplitBy = "#"


data class SshCert(
    var hostname:String="",
    var md5:String="",
    var sha1:String="",
    var sha256:String="",
    var hostKey:String="",
) {

    fun formattedMd5():String {
        if(md5.isBlank()) {
            return ""
        }
        try {
            return md5.chunked(2).joinToString(":"){it}
        }catch (e:Exception) {
            return ""
        }

    }

    fun formattedSha1():String {
        if(sha1.isBlank()) {
            return ""
        }

        try {
            return sha1.chunked(2).joinToString(":"){it}
        }catch (e:Exception) {
            return ""
        }
    }

    fun formattedSha256():String {
        if(sha256.isBlank()) {
            return ""
        }

        try {
            // 将十六进制字符串转换为字节数组
            val byteArray = sha256.chunked(2) // 每两个字符分为一组
                .map { it.toInt(16).toByte() } // 将每组转换为字节
                .toByteArray()

            // 将字节数组转换为 Base64 编码
            // remove padding "="
            return Base64.getEncoder().encodeToString(byteArray).removeSuffix("=").removeSuffix("=")
        }catch (e:Exception) {
            return ""
        }
    }

    fun isEmpty():Boolean {
        return md5.isBlank() && sha1.isBlank() && sha256.isBlank() && hostKey.isBlank()
    }

    fun toDbString():String {
        return hostname+dbStringSplitBy+md5+dbStringSplitBy+sha1+dbStringSplitBy+sha256+dbStringSplitBy+hostKey
    }

    companion object {
        fun parseDbString(dbString: String):SshCert? {
            if(dbString.isBlank()) {
                return null
            }

            try {
                val arr = dbString.split(dbStringSplitBy)
                if(arr.size<5) {
                    return null
                }

                return SshCert(
                    hostname = arr[0],
                    md5 = arr[1],
                    sha1 = arr[2],
                    sha256 = arr[3],
                    hostKey = arr[4]
                )
            }catch (e:Exception) {
                return null
            }
        }
    }
}
