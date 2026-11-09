package com.catpuppyapp.puppygit.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.StorageDirCons
import com.catpuppyapp.puppygit.data.entity.common.BaseFields
import com.catpuppyapp.puppygit.utils.getShortUUID

@Entity(tableName = "storageDir")
data class StorageDirEntity(
    @PrimaryKey
    var id: String= getShortUUID(),  //用途：例如仓库页面的设置、commit页面的设置、changelist页面的设置，等
    var name:String="",  //对外展示的名字，唯一，且创建后不可修改（不然更新子条目的virtualPath太麻烦了）
    var fullPath:String="",  //实际路径的canonicalPath，fullPath具有唯一性，且一旦创建sd就不可再更改，除非删除重建，所以，fullPath可用来标识一个sd条目
    var type:Int=StorageDirCons.Type.internal,
    var allowDel:Int= Cons.dbCommonTrue,  // 是否允许删除
    var parentId:String=StorageDirCons.DefaultStorageDir.rootDir.id, //parent StorageDir的id，注意：这个parentId只是为了防止以后实现嵌套困难，但实际上sd并不需要嵌套，也没计划要实现嵌套，嵌套只会增加复杂度

    //规范所有的virtualPath都应以/开头，非/结尾
    var virtualPath:String="",  //virtual应由代码自动生成，不要让用户填

    @Embedded
    var baseFields: BaseFields = BaseFields(
        baseStatus = StorageDirCons.Status.ok
    ),
)
