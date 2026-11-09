package com.catpuppyapp.puppygit.utils.encrypt

val defaultEncryptor = object : Encryptor {
    override fun encrypt(raw: String, key: String): String {
        if(raw.isNullOrEmpty()) {
            return raw
        }

        return EncryptUtil.encryptString(raw,key)
    }

    override fun decrypt(encrypted: String, key: String): String {
        if(encrypted.isNullOrEmpty()) {
            return encrypted
        }

        return EncryptUtil.decryptString(encrypted, key)
    }

}

val encryptor_ver_1 = defaultEncryptor
val encryptor_ver_2 = defaultEncryptor
val encryptor_ver_3 = defaultEncryptor
val encryptor_ver_4 = defaultEncryptor
val encryptor_ver_5 = defaultEncryptor
