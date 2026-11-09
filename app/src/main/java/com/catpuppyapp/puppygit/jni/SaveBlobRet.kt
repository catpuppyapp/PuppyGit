package com.catpuppyapp.puppygit.jni


data class SaveBlobRet (
    val code: SaveBlobRetCode = SaveBlobRetCode.SUCCESS,
    val savePath: String="",
)
