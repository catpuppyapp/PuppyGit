/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.catpuppyapp.puppygit.data.repository

import androidx.room.withTransaction
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.StorageDirCons
import com.catpuppyapp.puppygit.data.dao.StorageDirDao
import com.catpuppyapp.puppygit.data.entity.StorageDirEntity
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StorageDirUtil
import com.catpuppyapp.puppygit.utils.getSecFromTime

private const val TAG = "StorageDirRepositoryImpl"

class StorageDirRepositoryImpl(private val dao: StorageDirDao) : StorageDirRepository {
//    override suspend fun insert(item: StorageDirEntity) {
//        val funName = "insert"
//
//        StorageDirUtil.throwIfNameBad(item, RuntimeException("#$funName err: bad name, plz del illegal chars"))
//
//        //检查是否和内部id重名
//        StorageDirUtil.throwIfIdAlreadyExistsInVirtualSdList(item, RuntimeException("#$funName err: id already exists"))
//
//        //fullPath必须唯一，若存在，禁止插入
//        if(isNameOrFullPathExists(name=item.name, fullPath = item.fullPath)) {
//            MyLog.w(TAG, "#$funName: warn: item's name or fullPath already exists! operation abort...")
//            throw RuntimeException("#$funName err: name/fullPath already exists")
//        }
//
//        //virtual path应由代码自动生成，确保不会出错
//        //获取一手资料，不用缓存，以免出错，但其实这样也有可能出错，呵呵
//        // key=item.id, value=item,转换list为map，如果用associate则可自定义kv
//        val parent = getById(item.parentId)
//        if(parent==null) {
//            val errMsg = "err: invalid parentId '${item.parentId}' !"
//            MyLog.e(TAG, "#$funName $errMsg")
//            throw RuntimeException(errMsg)
//        }
//        //为item生成virtual path
//        item.virtualPath = StorageDirUtil.genVirtualPath(item, parent)
//
//        //准备插入条目
//        val timeInSec = getSecFromTime()
//        item.baseFields.baseCreateTime = timeInSec
//        item.baseFields.baseUpdateTime = timeInSec
//
//        dao.insert(item)
//
//        StorageDirUtil.updateCachedAllSds()
//    }
//
//    override suspend fun delete(item: StorageDirEntity, requireDelFilesOnDisk:Boolean, requireTransaction: Boolean) {
//        val funName = "delete"
//
//        //虚拟的全都是不允许删除，而且就算删除，下次重启也会出现，所以无需特别检查虚拟sd条目
//
//        if(item.allowDel == Cons.dbCommonFalse) {
//            MyLog.w(TAG, "#$funName: warn: StorageDir '${item.name}' is not allow to del, operation abort")
//            throw RuntimeException("#$funName err: item not allow del")
//        }
//
//        if(true) {
//            TODO("我还没想好如果存在子sd和关联仓库应该怎么处理，有以下方案：" +
//                    "/ 感觉禁止删除是最省事也最不容易出错的，但对用户来说最不方便。 (如果采用禁止删除的方案，需要：提供能分别删除无子sd的sd和仓库的途径，要方便，要能批量删，那样的话，就可以用这个机制) /" +
//                    "1: 禁止删除 (配套： “方便批量删除仓库和无子sd的sd的机制”)" +
//                    "2: 递归删除所有子sd和所有与当前sd和子sd或子sd的子sd关联的所有仓库，并在ui上提供“删除硬盘上的文件”的勾选框，若勾选则删除硬盘文件" +
//                    "3: 把当前模块的所有子模块和仓库都关联到other sd。(ps:这种实现需要在在通过路径反向查找关联的sd时优先匹配仓库表条目的fullpath，若匹配成功则使用仓库的sdid，若匹配失败，才去匹配sd的fullpath，还匹配失败就判定路径无效，重定向到虚拟root目录，不然可能出现越狱访问的漏洞")
//        }
//
//        //检查，如果有子sd或者有关联仓库，禁止删除(需要提供能分别删除无子sd的sd和仓库的途径，要方便，要能批量删，那样的话，就可以用这个机制)
//        //检查是否有子sd
//        val all = getAll()
//        for(other in all) {
//            //如果有某个条目的parentId等于当前条目的id，禁止删除当前条目（当然，可实现递归删除，但没必要，在界面上提供多选删除即可，这样用户就可先多选子条目，再删除父条目）
//            if(other.parentId == item.id) {  //item存在子节点
//                MyLog.w(TAG, "#$funName: warn: item has children, delete failed")
//                throw RuntimeException("#$funName err: item has children, del failed")
//            }
//        }
//        //检查是否有关联仓库
//        val repoDb = AppModel.dbContainer.repoRepository
//        val repos = repoDb.getByStorageDirId(item.id)
//        if(repos.isNotEmpty()) {
//            MyLog.w(TAG, "#$funName: warn: item has related repos, delete failed")
//            throw RuntimeException("#$funName err: item has related repos, del failed")
//        }
//
//
//        //这里不开事务了，反正删除的硬盘文件也无法 rollback，随便吧，执行成什么样算什么样
//        //算了，还是开个事务吧
//
//        //把被调方法内部的事务关闭，是否开启事务，仅取决于当前方法requireTransaction是否为真
//        val requireInnerTransaction = false
//        val act = suspend{
//            //删除所有关联当前storageDir的仓库
//            //还要删除关联条目，不能这么简单删除，用循环吧，虽然性能差点，但应该在可接受范围内
////            repoDb.deleteByStorageDirId(item.id)
//
//            //删除storageDir关联的所有仓库
//            val repoDb = AppModel.dbContainer.repoRepository
//            val repos = repoDb.getByStorageDirId(item.id)
//            repos.forEach {
//                repoDb.delete(it, requireDelFilesOnDisk, requireInnerTransaction)
//            }
//
//            //删除storageDir条目
//            dao.delete(item)
//        }
//
//        if(requireTransaction) {
//            AppModel.dbContainer.db.withTransaction {
//                act()
//            }
//
//        }else {
//            act()
//        }
//
//        StorageDirUtil.updateCachedAllSds()
//
//    }
//
//    override suspend fun update(item: StorageDirEntity){
//        val funName="update"
//
//        StorageDirUtil.throwIfNameBad(item, RuntimeException("#$funName err: bad name, plz del illegal chars"))
//
//        //不允许修改虚拟item，理应在界面禁止，在这判断只是以防万一
//        StorageDirUtil.throwIfIdAlreadyExistsInVirtualSdList(item, RuntimeException("$funName err: item not allow update"))
//
//        //这个检测只是兜底，调用者应在调用update前自行检查是否已存在相同fullpath或name
//        if(isNameOrFullPathAlreadyUsedByOtherItem(name=item.name, fullPath = item.fullPath, excludeId = item.id)) {
//            MyLog.w(TAG, "#$funName: warn: item's name or fullPath already used by other item! operation abort...")
//
//            throw RuntimeException("#$funName err: name/fullPath already exists")
//        }
//
//        val old = getById(item.id)
//        if(old?.name != item.name) {  //如果name变化，抛异常
//            //在界面上就应该禁止修改名字，这里检测只是以防万一，这个名字不能改，不然virtualpath受影响，很麻烦
//            throw RuntimeException("#$funName err: disallow change name")
//        }
//
//        item.baseFields.baseUpdateTime = getSecFromTime()
//        dao.update(item)
//
//        StorageDirUtil.updateCachedAllSds()
//
//    }
//
//    override suspend fun getById(id: String): StorageDirEntity? {
//        for(it in StorageDirCons.DefaultStorageDir.listOfAllDefaultSds) {
//            if(it.id==id) {
//                return it
//            }
//        }
//
//        return dao.getById(id)
//    }
//
//    override suspend fun getAll(): List<StorageDirEntity> {
//        val allFormDb = dao.getAll()
//
//        val retList = mutableListOf<StorageDirEntity>()
//
//        retList.addAll(StorageDirCons.DefaultStorageDir.listOfAllDefaultSds)
//        retList.addAll(allFormDb)
//
//        return retList
//    }
//
//    override suspend fun getListByStatus(status: Int): List<StorageDirEntity> {
//        return getAll().filter { it.baseFields.baseStatus == status }
//    }
//
//    override suspend fun getListForMatchFullPath(): List<StorageDirEntity> {
////        getListByStatus() //这里应该用getall，因为就算某sd条目被禁用，实际其下的内容也有可能被访问，所以应该能匹配上才对，尽管返回根目录后看不到对应条目
//        val all = getAll()
//        return all.filter { !StorageDirCons.DefaultStorageDir.idListUnusedForMatchPath.contains(it.id) }
//    }
//
//    override suspend fun getByFullPath(fullPath: String): StorageDirEntity? {
//        var item:StorageDirEntity? = null
//
//        //查内置sd
//        for(it in StorageDirCons.DefaultStorageDir.listOfAllDefaultSds) {
//            if(it.fullPath==fullPath) {
//                item=it
//                break
//            }
//        }
//
//        //查数据库sd
//        if(item == null) {
//            item = dao.getByFullPath(fullPath)
//        }
//
//        return item
//    }
//
//    override suspend fun isFullPathExists(fullPath: String): Boolean {
//        return getByFullPath(fullPath) != null
//    }
//
//    override suspend fun getByName(name: String): StorageDirEntity? {
//        var item:StorageDirEntity? = null
//
//        //查内置sd
//        for(it in StorageDirCons.DefaultStorageDir.listOfAllDefaultSds) {
//            if(it.name==name) {
//                item=it
//                break
//            }
//        }
//
//        //查数据库sd
//        if(item == null) {
//            item = dao.getByName(name)
//        }
//
//        return item
//    }
//
//    override suspend fun isNameExists(name: String): Boolean {
//        return getByName(name) != null
//
//    }
//
//    override suspend fun getByNameOrFullPath(name: String, fullPath: String): StorageDirEntity? {
//        var item:StorageDirEntity? = null
//
//        //查内置sd
//        for(it in StorageDirCons.DefaultStorageDir.listOfAllDefaultSds) {
//            if(it.name==name || it.fullPath==fullPath) {
//                item=it
//                break
//            }
//        }
//
//        //查数据库sd
//        if(item == null) {
//            item = dao.getByNameOrFullPath(name, fullPath)
//        }
//
//        return item
//    }
//
//    override suspend fun isNameOrFullPathExists(name: String, fullPath: String): Boolean {
//        return getByNameOrFullPath(name, fullPath) != null
//    }
//
//
//    override suspend fun getByNameOrFullPathExcludeId(name: String, fullPath: String, excludeId:String): StorageDirEntity? {
//        var item:StorageDirEntity? = null
//
//        //查内置sd
//        for(it in StorageDirCons.DefaultStorageDir.listOfAllDefaultSds) {
//            if(it.id!=excludeId && (it.name==name || it.fullPath==fullPath)) {
//                item=it
//                break
//            }
//        }
//
//        //查数据库sd
//        if(item == null) {
//            item = dao.getByNameOrFullPathExcludeId(name, fullPath, excludeId)
//        }
//
//        return item
//    }
//
//    /**
//     * 检查是否存在其他条目拥有相同的fullPath或name，用于在更新时检查是否重名或重复路径
//     */
//    override suspend fun isNameOrFullPathAlreadyUsedByOtherItem(name: String, fullPath: String, excludeId: String): Boolean {
//        return getByNameOrFullPathExcludeId(name, fullPath, excludeId) != null
//    }
//
//    /**
//     * 获取所有非内置的条目
//     */
//    override suspend fun getAllNoDefault(): List<StorageDirEntity> {
//        return dao.getAll()
//    }
//
//    /**
//     * 为数据库中所有条目重新生成virtualPath，注意：是“为数据库中的条目”，不会为那几个默认内置的在代码里写死的sd重新生成哦
//     */
//    override suspend fun reGenVirtualPathForAllItemsInDb() {
//        if(true){
//            TODO("改成用仅更新virtual path和update time的sql来更新数据库")
//        }
//
//        val funName = "reGenVirtualPathForAllItemsInDb"
//        //获取一手资料，不用缓存，以免出错，但其实这样也有可能出错，呵呵
//        // key=item.id, value=item,转换list为map，如果用associate则可自定义kv
//        val allMap = getAll().associateBy { it.id }  //这个是包含默认条目的，真正的all，用来生成virtualpath，必须包含所有条目才行
//        val root =StorageDirCons.DefaultStorageDir.rootDir
//
//        //需要更新的条目（只有数据库里的条目，代码写死的默认的那几个不包含在内）
//        val allNoDefault = getAllNoDefault()  //这个是不包含默认条目的，只有这些条目的virtualpath会被更新
//
//        //开事务，更新db
//        AppModel.dbContainer.db.withTransaction {
//            for(it in allNoDefault) {
//                try {
//                    it.virtualPath = StorageDirUtil.genVirtualPathByParentsName(it, allMap, root)
//                    TODO("就是改下面这个sql，改成仅更新 virtualpath和update time")
//                    update(it)  //更新db
//                }catch (e:Exception) {
//                    MyLog.e(TAG, "#$funName err: reGen virtual path for '${it.name}' failed!")
//                    //考虑：如果一个path失败，其他成功，行不行？还是要么都更新成功，要么都失败？
//                    // 考虑下，如果允许部分成功，就不用抛异常，否则抛异常，并且在外部开一个事务
//                    // 拿不准主意，暂时先抛异常吧，话说这会回滚吗？
//                    throw e  //如果某个条目更新失败，抛异常回滚
//                }
//            }
//
//        }
//    }


    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

}
