package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class Files (
    //Files页面最后打开的路径
    var lastOpenedPath:String="",

    var defaultViewAndSort:DirViewAndSort=DirViewAndSort(),

    /**
     * struct: {path: viewAndSort}
     * if can find rule for path in this map, it will used, else, will use the `defaultViewAndSort`
     */
    val dirAndViewSort_Map:MutableMap<String, DirViewAndSort> = mutableMapOf()
)
