package com.catpuppyapp.puppygit.data.repository

import com.catpuppyapp.puppygit.constants.StorageDirCons
import com.catpuppyapp.puppygit.data.entity.StorageDirEntity

/**
 * Repository that provides insert, update, delete, and retrieve of [StorageDirEntity] from a given data source.
 */
@Deprecated("原本是用来创建不同的存储仓库的位置的，本想兼容saf，但saf不能在c里用就废弃了，再后来不用这个类了，放弃上架傻逼谷歌商店，直接获取管理所有文件权限，想存哪存哪")
interface StorageDirRepository {
//
//    /**
//     * Insert item in the data source
//     */
//    suspend fun insert(item: StorageDirEntity)
//
//    /**
//     * Delete item from the data source
//     */
//    suspend fun delete(item: StorageDirEntity, requireDelFilesOnDisk:Boolean=false, requireTransaction: Boolean=true)
//
//    /**
//     * Update item in the data source
//     */
//    suspend fun update(item: StorageDirEntity)
//
//    suspend fun getById(id:String): StorageDirEntity?
//    suspend fun getAll(): List<StorageDirEntity>
//
//    suspend fun getListByStatus(status:Int = StorageDirCons.Status.ok): List<StorageDirEntity>
//
//    /**
//     * 获取一个列表，可用通过路径匹配反查sd条目
//     */
//    suspend fun getListForMatchFullPath(): List<StorageDirEntity>
//    suspend fun getByFullPath(fullPath:String): StorageDirEntity?
//
//    suspend fun isFullPathExists(fullPath:String): Boolean
//    suspend fun getByName(name:String): StorageDirEntity?
//    suspend fun isNameExists(name:String): Boolean
//    suspend fun getByNameOrFullPath(name:String, fullPath:String): StorageDirEntity?
//    suspend fun isNameOrFullPathExists(name:String, fullPath:String): Boolean
//    suspend fun getByNameOrFullPathExcludeId(name: String, fullPath: String, excludeId:String): StorageDirEntity?
//    suspend fun isNameOrFullPathAlreadyUsedByOtherItem(name: String, fullPath: String, excludeId: String): Boolean
//    suspend fun getAllNoDefault(): List<StorageDirEntity>
////    suspend fun reGenVirtualPathForAllItemsInDb()
//

    suspend fun subtractTimeOffset(offsetInSec:Long)

}
