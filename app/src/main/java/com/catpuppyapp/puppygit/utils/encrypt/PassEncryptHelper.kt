package com.catpuppyapp.puppygit.utils.encrypt

class PassEncryptHelper {
    companion object {
        //如果修改加密实现，更新这个版本号，并添加对应的key和encryptor
        val passEncryptCurrentVer = 5

        //迁移机制大概就是找到旧版本号的密钥和加密解密器，解密数据；然后使用新版本号的密钥和加密解密器加密数据，最后把加密后的数据写入数据库，就完了。
        //所以，记住：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，记住：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，记住：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，记住：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，记住：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！

        //新版本的app包含旧版的所有密钥和加密器
        //key = ver, value = 密钥
        //注：20241206 之后增加了主密码机制，若有主密码，则使用主密码，否则将使用此处的默认密钥，默认密钥是公开的，因此并不够安全
        val keyMap:Map<Int,String> = mapOf(
            Pair(1, "3LHLpwTQ9uEyP9MCqgYNqncKxmQJsww9L4A7T7wK"),
            Pair(2, "ffHuzkprZY9b5PbYxaHPgHZ5UJxsqsL5MjqvCn7rQH3q7p7shz"),
            Pair(3, "qaWxActsnqiD2D5CmYroUcMRjYr4KDAiiNYHPs2RVs7DLTcU3y"),
            Pair(4, "C8mNzgW5Pwq3bFcaHP2WrwtZXA9bWniKgz9SeKRHxDbTyJ9LnZ"),
            Pair(5, "Sy7JW_S4N3Fq5xFPzSK7tNvfXeUFRqUJaC4hmyjNm_XtfbK4dW"),
            // other...
        )

        //key = ver, value = 加密解密器
        val encryptorMap:Map<Int,Encryptor> = mapOf(
            Pair(1, encryptor_ver_1),
            Pair(2, encryptor_ver_2),
            Pair(3, encryptor_ver_3),
            Pair(4, encryptor_ver_4),
            Pair(5, encryptor_ver_5),
            // other...
        )


//
//        val currentVerKey:String = keyMap[passEncryptCurrentVer]!!
//        val currentVerEncryptor:Encryptor = encryptorMap[passEncryptCurrentVer]!!


        private fun chooseMasterPasswordOrDefaultPass(encryptorVersion:Int, masterPassword: String):String {
            //版本1到4没有主密码机制，所以强制使用默认密码，不过这几个版本基本没人用，所以这的代码实际只是为了兼容旧代码
            return if(masterPassword.isEmpty() || (encryptorVersion >= 1 && encryptorVersion <= 4)) {
                //如果用户没设置主密码，或者用户设置了，但传的主密码有误，则尝试使用默认密码。 ps 如果用户设置了主密码但调用此函数时传的空值，那么会解密失败，然后报错。除非用户设置的密码刚好和默认密码一致....不过一般不会那么巧除非刻意为之，所以不用处理
                keyMap[encryptorVersion]!!
            }else {  // 5以及之后的版本，若有主密码则使用主密码
                //用指定的密码，若有误，将解密失败
                masterPassword
            }
        }

        fun encryptWithSpecifyEncryptorVersion(encryptorVersion:Int, raw:String, masterPassword:String):String {
            return encryptorMap[encryptorVersion]!!.encrypt(raw, chooseMasterPasswordOrDefaultPass(encryptorVersion, masterPassword))

        }

        fun decryptWithSpecifyEncryptorVersion(encryptorVersion:Int,  encryptedStr:String, masterPassword: String):String {
            return encryptorMap[encryptorVersion]!!.decrypt(encryptedStr, chooseMasterPasswordOrDefaultPass(encryptorVersion, masterPassword))
        }

        fun encryptWithCurrentEncryptor(raw:String, masterPassword:String):String {
            return encryptWithSpecifyEncryptorVersion(passEncryptCurrentVer, raw, masterPassword)
        }

        fun decryptWithCurrentEncryptor(encryptedStr:String, masterPassword: String):String {
            return decryptWithSpecifyEncryptorVersion(passEncryptCurrentVer, encryptedStr, masterPassword)
        }
    }
}
