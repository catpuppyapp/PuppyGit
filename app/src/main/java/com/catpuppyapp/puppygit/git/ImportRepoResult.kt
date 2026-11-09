package com.catpuppyapp.puppygit.git

data class ImportRepoResult (
    var all:Int=0,
    var success:Int=0,
    var existed:Int=0,
    var failed:Int=0,
)
