package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class TimeZone (
    /**
     * 为true则App跟随系统时区，否则使用 `offsetInMinutes` 指定的偏移量，由于github默认显示提交时间时跟随用户时区，所以我这默认值也设成true，为true应该比较符合直觉
     */
    var followSystem:Boolean = true,


    /**
     * 用户指定的时区偏移量，正数或负数或0，留空以使用GitObject中携带的时区偏移量，其他情况使用系统时区；非空则使用其指定的分钟数
     * 应通过 `str.trim().toInt()` 解析此字段并在解析后使用 `Utils.isValidOffsetInMinutes()` 检测其值是否在有效区间
     *
     * 注：如果留空，预览提交或其他包含Signature的GitObject时会使用Signature中的偏移量；其他情况例如创建提交、Files页面的文件最后修改时间、仓库的最后更新时间等，一律使用系统时区
     */
    var offsetInMinutes:String="",

)
