package com.catpuppyapp.puppygit.utils.storagepaths

import kotlinx.serialization.Serializable

/**
 * remember custom storage paths for save repos
 */
@Serializable
data class StoragePaths (
    // storagePaths for clone
    val storagePaths:MutableList<String> = mutableListOf(),
    // last selected of storage paths
    var storagePathLastSelected:String="",

)
