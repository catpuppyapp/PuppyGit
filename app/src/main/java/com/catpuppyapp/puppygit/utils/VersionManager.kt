package com.catpuppyapp.puppygit.utils

import java.io.File

/**
 * if file has a version, return it, else return empty String
 */
fun readVersionFromFile(file: File):String{
    try{
        if(file.exists()) {
            //只读第一行
            return file.inputStream().bufferedReader().readLine().trim()
        }else {
            return ""
        }
    }catch (e:Exception) {
        return ""
    }
}

fun writeVersionToFile(file:File, version:String) {
    file.outputStream().writer().use {
        //没必要追加换行符，只记版本号即可
//        it.appendLine(version)

        it.write(version)
    }
}

/**
 * get a valid Int version or -1
 */
fun readIntVersionFromFile(file: File):Int{
    val v = readVersionFromFile(file)
    if(v.isEmpty()) {
        return -1
    }

    return try {
        v.toInt()
    }catch (_:Exception) {
        -1
    }
}

fun writeIntVersionToFile(file:File, version:Int) {
    writeVersionToFile(file, ""+version)
}
