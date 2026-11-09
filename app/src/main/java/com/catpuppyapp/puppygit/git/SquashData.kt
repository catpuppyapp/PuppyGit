package com.catpuppyapp.puppygit.git

data class SquashData(
    val username:String,
    val email:String,
    val headFullOid:String,
    val headFullName:String,  // branch full name or "HEAD"(when detached)
)
