package com.catpuppyapp.puppygit.utils.temp

private const val fromDiffPrefix = "diff_"

enum class TempFileFlag(val flag:String) {
    //RLTF =  acronym of the "Replace-Lines-To-File" (RLTF=函数名首字母缩写)
    //在diff页面编辑行，创建临时文件使用此flag
    FROM_DIFF_SCREEN_REPLACE_LINES_TO_FILE("${fromDiffPrefix}_RLTF")
}
