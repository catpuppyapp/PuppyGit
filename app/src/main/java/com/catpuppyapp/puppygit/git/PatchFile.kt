package com.catpuppyapp.puppygit.git

data class PatchFile(
    /**
     * output file path
     */
    val outFileFullPath:String? = null,

    /**
     * diff content (patch content)
     */
    val content:String? = null,
)
