package com.catpuppyapp.puppygit.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.catpuppyapp.puppygit.data.entity.common.BaseFields

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    var usedFor: Int,  //用途：例如仓库页面的设置、commit页面的设置、changelist页面的设置，等
    //设置项值，具体值和用途有关
    var jsonVal:String="",

    @Embedded
    var baseFields: BaseFields = BaseFields(),
)
