package com.catpuppyapp.puppygit.utils

import com.catpuppyapp.puppygit.constants.StorageDirCons
import com.catpuppyapp.puppygit.data.entity.StorageDirEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object StorageDirUtil {
//
//    private const val TAG = "StorageDirUtil"
//
//    private val writeLock = Mutex()
//
//    private val cachedAllSds = mutableListOf<StorageDirEntity>()
//    private var cachedAllSdsCopy = cachedAllSds.toList()  // for avoid concurrent modified exception
//
//    suspend fun init() {
//        updateCachedListByDbData()
//    }
//
//    private suspend fun updateCachedListByDbData() {
//        try {
//            //channel外面加互斥锁，这样就相当于一个公平锁了（带队列的互斥锁）,即使误开多个writer也不用担心冲突了
//            writeLock.withLock {
//
//                val sddb = AppModel.dbContainer.storageDirRepository
//                cachedAllSds.clear()
//                cachedAllSds.addAll(sddb.getAll())
//
//                cachedAllSdsCopy = cachedAllSds.toList()
//            }
//        } catch (e: Exception) {
//
//            MyLog.e(TAG, "update cached storage dirs err: ${e.stackTraceToString()}")
//        }
//
//    }
//
//    fun getSdsSnapshot() {
//        cachedAllSdsCopy.toList()
//    }
//
//    fun updateCachedAllSds() {
//        doJobThenOffLoading {
//            updateCachedListByDbData()
//        }
//    }
//
//    /**
//     * 返回值为为传入的对象生成的virtual path
//     *
//     * 注：此方法不会修改传入的对象
//     */
//    fun genVirtualPath(target: StorageDirEntity, parent: StorageDirEntity):String {
////        val funName = "genVirtualPath"
//
//        //拼接当前条目的virtual path
//        val virtualPathForTarget = parent.virtualPath + StorageDirCons.separator + target.name
//
//        //log
//        MyLog.d(TAG, "gen virtual path for '${target.name}' success, path is '$virtualPathForTarget'")
//
//        return virtualPathForTarget
//    }
//
//    /**
//     * 根据parent name拼接virtualpath
//     *
//     * 返回值为为传入的对象生成的virtual path
//     *
//     * 注：此方法不会修改传入的对象
//     */
//    fun genVirtualPathByParentsName(
//        target: StorageDirEntity,
//        allMap: Map<String, StorageDirEntity>,
//        root: StorageDirEntity
//    ):String {
//        //递归生成parent virtual path
//        val parentVirtualPath = recursiveGenParentVirtualPath(target.parentId, root, allMap)
//        //拼接当前条目的virtual path
//        val virtualPathForTarget = parentVirtualPath + StorageDirCons.separator + target.name
//
//        //log
//        MyLog.d(TAG, "gen virtual path for '${target.name}' success, path is '$virtualPathForTarget'")
//
//        return virtualPathForTarget
//    }
//
//    /**
//     * 为某个条目(someItem)递归生成其parent virtual path，利用 `返回值+"/"+someItem.name` 就能得到其virtualPath了
//     *
//     * ps: 好像很酷炫，但是没有用，后来我发现直接用 父节点的virtualPath+"/"+当前节点的name 就生成当前节点的virtualPath了
//     */
//    private fun recursiveGenParentVirtualPath(parentId:String, root:StorageDirEntity, allMap:Map<String, StorageDirEntity>):String {
//        val parent = allMap[parentId]
//        if(parent==null) {  // should not be happen...
//            val errMsg = "err: invalid parentId '${parentId}' !"
//            MyLog.e(TAG, "#recursiveGenParentVirtualPath $errMsg")
//            throw RuntimeException(errMsg)
//        }
//
//        if(parent.id == root.id) {
//            return root.name  // or `return ""`
//        }
//
//        return recursiveGenParentVirtualPath(parent.parentId, root, allMap) + StorageDirCons.separator + parent.name
//    }
//
//    //废弃，禁止更改StorageDir的name
////    fun updateVirtualPathWithNewName(sd:StorageDirEntity, newName:String) {
////
////    }
//
//    fun isBadSdName(name:String):Boolean {
//        //不能包含空格或路径分隔符等特殊字符
//        //btw: 虽然新建的name不能为空，但实际上内置的rootDir的name是空，所以rootDir其实过不了这个检测
//        return name.isEmpty() || strHasSpaceChar(name) || strHasIllegalChars(name)
//    }
//
//
//    fun throwIfNameBad(item: StorageDirEntity, exception: Exception) {
//        if (StorageDirUtil.isBadSdName(item.name)) {
//            throw exception
//        }
//    }
//
//    fun throwIfIdAlreadyExistsInVirtualSdList(item: StorageDirEntity, exception: Exception) {
//        for (it in StorageDirCons.DefaultStorageDir.listOfAllDefaultSds) {
//            if (it.id == item.id) {
//                throw exception
//            }
//        }
//    }

}