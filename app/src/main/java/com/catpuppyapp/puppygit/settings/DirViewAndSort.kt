package com.catpuppyapp.puppygit.settings

import com.catpuppyapp.puppygit.settings.enums.dirviewandsort.ViewType
import com.catpuppyapp.puppygit.settings.enums.dirviewandsort.SortMethod
import kotlinx.serialization.Serializable

@Serializable
data class DirViewAndSort (
    var viewType:Int = ViewType.LIST.code,
    var sortMethod:Int = SortMethod.NAME.code,
    var ascend:Boolean=true,
    var folderFirst:Boolean = true,
)

