package com.catpuppyapp.puppygit.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.catpuppyapp.puppygit.utils.encrypt.PassEncryptHelper

@Deprecated("废弃了，这个类不用了，改把版本号加到每个凭据后面了，解密的时候根据凭据使用的加密器版本解密")
@Entity(tableName = "passEncrypt")
data class PassEncryptEntity (
    @PrimaryKey
    var id: Int= 1,

    var ver: Int= PassEncryptHelper.passEncryptCurrentVer,
    var reserve1:String="",  //保留字段1
    var reserve2:String="",  //保留字段2
)
