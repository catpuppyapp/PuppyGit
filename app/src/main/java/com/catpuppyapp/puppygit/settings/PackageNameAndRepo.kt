package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class PackageNameAndRepo(
    val appPackageName:String = "",
    val repoId:String = "",
) {
    fun toKey() = toKeyPrefix()+repoId

    fun toKeyPrefix() = "$appPackageName:"

    fun toKeySuffix() = ":$repoId"
}
