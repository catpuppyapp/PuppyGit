package com.catpuppyapp.puppygit.jni

import com.github.git24j.core.IBitEnum
import com.github.git24j.core.Status

class StatusEntryDto (
    val indexToWorkDirOldFilePath:String? = "",
    val indexToWorkDirNewFilePath:String? = "",
    val headToIndexOldFilePath:String? = "",
    val headToIndexNewFilePath:String? = "",

    //文件大小，单位字节
    val indexToWorkDirOldFileSize:Long = 0L,
    val indexToWorkDirNewFileSize:Long = 0L,
    val headToIndexOldFileSize:Long = 0L,
    val headToIndexNewFileSize:Long = 0L,

    val entryStatusFlag: Int = 0,

) {
    fun statusFlagToSet() = IBitEnum.parse<Status.StatusT?>(entryStatusFlag ?: 0, Status.StatusT::class.java);

}
