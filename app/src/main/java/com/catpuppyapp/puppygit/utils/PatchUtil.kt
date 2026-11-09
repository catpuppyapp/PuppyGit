package com.catpuppyapp.puppygit.utils

import androidx.compose.ui.graphics.Color

object PatchUtil {
    //这些背景色，最关键的一个原则就是要浅，半透明的感觉，不要喧宾夺主让人看不清字本身
    //BEGIN: line background colors
    private val color_fileHeader_dark = Color(0x4B4D4414)
    private val color_fileHeader = Color(0x4BC7BB81)
    private val color_add_dark = Color(0x4D1B5E20)
    private val color_add = Color(0x5981C784)
    private val color_del_dark = Color(0x4B6B3E3E)
    private val color_del = Color(0x46834040)
    private val color_hunkHeader_dark = Color(0x5901579B)
    private val color_hunkHeader = Color(0x5181B1C7)
    //END: line background colors

    // 判断一段字符串有没有可能是 patch文件的内容。（也有叫diff文件的，总之就是git 导出的patch）
    fun maybeIsPatchLine(str:String) = isFileHeader(str) || isAdd(str) || isDelete(str) || isHunkHeader(str);

    fun isAdd(str: String) = str.startsWith("+");
    fun isDelete(str: String) = str.startsWith("-") || str.trimEnd() == "\\ No newline at end of file";
    fun isHunkHeader(str: String) = str.startsWith("@@");
    fun isFileHeader(str: String) = str.startsWith("diff --git") || str.startsWith("---") || str.startsWith("+++") || str.startsWith("index") || str.startsWith("new file");

    fun getColorOfLine(str: String, inDarkTheme:Boolean):Color? = if(isFileHeader(str)) {
        if(inDarkTheme) {
            color_fileHeader_dark
        }else {
            color_fileHeader
        }
    } else if(isAdd(str)) {
        if(inDarkTheme) {
            color_add_dark
        }else {
            color_add
        }
    }  else if(isDelete(str)) {
        if(inDarkTheme) {
            color_del_dark
        }else {
            color_del
        }
    } else if(isHunkHeader(str)) {
        if(inDarkTheme) {
            color_hunkHeader_dark
        }else {
            color_hunkHeader
        }
    }else null;

}
