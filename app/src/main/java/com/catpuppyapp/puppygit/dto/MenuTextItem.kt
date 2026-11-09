package com.catpuppyapp.puppygit.dto

import androidx.compose.runtime.Composable

data class MenuTextItem (
    val text:String,

    // before show menu text, prepend or append content, for show a divider or somethings
    // 在显示文本前前置或追加内容，例如加的divider之类的
    val prependContent:(@Composable ()->Unit)? = null,
    val appendContent:(@Composable ()->Unit)? = null,

    //点击条目后是否关闭菜单
    val closeMenuAfterClick:()->Boolean = {true},

    val enabled:()->Boolean = {true},
    val visible:()->Boolean = {true},
    val onClick:()->Unit,
)
