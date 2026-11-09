package com.catpuppyapp.puppygit.server

import kotlinx.serialization.Serializable


fun createSuccessResult(msg: String="", data: Map<String, String> = mapOf()): Result {
    return Result(Code.success, msg, data)
}

fun createErrResult(msg:String, data: Map<String, String> = mapOf()): Result {
    return Result(Code.err, msg, data)
}

@Serializable
data class Result(
    val code:Int = Code.success,  //success or err code
    val msg:String = "",  // msg, usually about err msg
    val data:Map<String, String>,  // data, if complex, can save json string into it
)

object Code {
//    code < 100 means success
    const val success = 0

    // code > 100 means error
    const val err=100
    const val err_RepoBusy = 101 //仓库正在执行其他操作，无法执行当前请求的操作
    // other err code maybe，100往后加
    // val otehrErr = 101
}
