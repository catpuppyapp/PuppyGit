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
import com.catpuppyapp.puppygit.data.dao.RepoDao
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.ImportRepoResult
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.getNowInSecFormatted
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.catpuppyapp.puppygit.utils.isRepoReadyAndPathExist
import com.catpuppyapp.puppygit.utils.strHasIllegalChars
import com.github.git24j.core.Repository
import kotlinx.coroutines.flow.Flow
import java.io.File

private const val TAG = "RepoRepositoryImpl"

class RepoRepositoryImpl(private val dao: RepoDao) : RepoRepository {
    @Deprecated("不用这个")
    override fun getAllStream(): Flow<List<RepoEntity?>> = dao.getAllStream()

    @Deprecated("不用这个")
    override fun getStream(id: String): Flow<RepoEntity?> = dao.getStream(id)

    override suspend fun insert(item: RepoEntity) {
        val funName = "insert"
        if(isRepoNameExist(item.repoName)) {
            MyLog.w(TAG, "#$funName: warn: item's repoName '${item.repoName}' already exists! operation abort...")

            throw RuntimeException("#$funName err: repoName already exists")

        }

        // make sure saved is canonical path
        item.fullSavePath = File(item.fullSavePath).canonicalPath

        item.createErrMsg = addTimeStampIfErrMsgIsNotBlank(item.createErrMsg)
        item.latestUncheckedErrMsg = addTimeStampIfErrMsgIsNotBlank(item.latestUncheckedErrMsg)

        val timeNowInSec = getSecFromTime()
        item.baseFields.baseCreateTime = timeNowInSec
        item.baseFields.baseUpdateTime = timeNowInSec

        //清掉临时状态，这个不用往数据库存
        val tmpStatus = item.tmpStatus
        item.tmpStatus=""
        dao.insert(item)
        item.tmpStatus = tmpStatus
    }

    /**
     * 如果err msg不为空，添加时间戳，否则返回原字符串
     */
    private fun addTimeStampIfErrMsgIsNotBlank(errMsg:String):String {
        val timestampSuffix = ",timestamp)"
        // errmsg不为空且不以时间戳后缀结尾则追加时间戳和后缀，否则返回原字符串。注意：不要返回空字符串，因为有可能传入带时间戳的errMsg，这时传空字符串就不对了
        return if(errMsg.isBlank() || errMsg.endsWith(timestampSuffix)) errMsg else "$errMsg (${getNowInSecFormatted()}$timestampSuffix"
    }

    override suspend fun delete(item: RepoEntity, requireDelFilesOnDisk:Boolean, requireTransaction: Boolean) {
        MyLog.d(TAG, "will delete repo, repoId=${item.id}, repoFullPath=${item.fullSavePath}")

        val repoFullPath = item.fullSavePath  //其实不用在这保存，但我有点担心下面的函数太傻逼会修改item，所以先存上路径

        //删除数据库记录
        val errDb = AppModel.dbContainer.errorRepository
        val remoteDb = AppModel.dbContainer.remoteRepository
        val act = suspend {
            //其实NotReady的仓库应该没remote和error记录，不过这里不做检测，反正就算执行了也不会误删
            errDb.deleteByRepoId(item.id)  //删除仓库所有错误记录
            remoteDb.deleteByRepoId(item.id)  //删除仓库所有remote
            dao.delete(item)  //删除仓库本身

        }
        if(requireTransaction) {
            AppModel.dbContainer.db.withTransaction {
                act()
    //            throw RuntimeException("测试抛异常是否会“回滚”以及“不执行异常后位于本代码块外的代码”，结论：1会回滚，2不会执行异常后的代码无论是在本代码块内还是外。两者都符合直觉，很好。")
            }
        }else {
            act()
        }

//        println("抛异常我就不会被执行到了！！")
        //最后，删除仓库文件夹
        if(requireDelFilesOnDisk) {
            // 如果能确定目标是submodule，则删除文件后创建空目录
            //这里不要用parentRepoValid来判断，就用parentRepoId，因为就算id无效（比如父仓库先删除后导入导致id无效），只要id非空，其实就能证明当前仓库至少是其他仓库的submodule，所以应在删除后创建空目录占位置
            Libgit2Helper.removeRepoFiles(repoFullPath, createEmptyWorkDirAfterRemove = item.parentRepoId.isNotBlank())
        }

        //log
        MyLog.d(TAG, "success delete repo, repoId=${item.id}, repoFullPath=${repoFullPath}")
    }

    override suspend fun update(item: RepoEntity, requeryAfterUpdate:Boolean) {
        val funName ="update"
        if(isRepoNameAlreadyUsedByOtherItem(item.repoName, item.id)) {
            MyLog.w(TAG, "#$funName: warn: item's repoName '${item.repoName}' already used by other item! operation abort...")
            throw RuntimeException("#$funName err: repoName already exists")

        }

        item.fullSavePath = File(item.fullSavePath).canonicalPath

        //粗略判断，如果错误信息不为空且没追加时间戳（以英文半角括号结尾）则追加时间戳
        item.createErrMsg = addTimeStampIfErrMsgIsNotBlank(item.createErrMsg)
        item.latestUncheckedErrMsg = addTimeStampIfErrMsgIsNotBlank(item.latestUncheckedErrMsg)

        item.baseFields.baseUpdateTime = getSecFromTime()

        val tmpStatus = item.tmpStatus
        item.tmpStatus=""
        dao.update(item)
        item.tmpStatus = tmpStatus

        //重新查询仓库信息
        if(requeryAfterUpdate){
            Libgit2Helper.updateRepoInfo(item)
        }

    }

    override suspend fun isRepoNameExist(repoName: String): Boolean {
        return dao.getIdByRepoName(repoName) != null
    }

    override suspend fun getById(id: String): RepoEntity? {
        if(id.isEmpty()) {
            return null
        }

        val repoFromDb = dao.getById(id)?:return null

        Libgit2Helper.updateRepoInfo(repoFromDb)

        return repoFromDb
    }

    override suspend fun getByName(name: String): RepoEntity? {
        if(name.isEmpty()) {
            return null
        }

        val items = dao.getByName(name)
        return if(items.isEmpty()) {
            null
        }else {
            val repoFromDb = items.get(0)
            Libgit2Helper.updateRepoInfo(repoFromDb)

            repoFromDb
        }
    }

    override suspend fun getByNameOrId(repoNameOrId: String, forceUseIdMatchRepo: Boolean): Ret<RepoEntity?> {
        if(repoNameOrId.isEmpty()) {
            return Ret.createError(null, "repoNameOrId is empty")
        }

        val repo = if (forceUseIdMatchRepo) {
            val repo = getById(repoNameOrId)
            if (repo == null) {
                return Ret.createError(null, "no repo matched the id '$repoNameOrId'")
            }

            repo
        } else {
            // match by name first是因为我感觉多数人应该会记住仓库名，然后调用的时候传参数名而不是id
            var repo = getByName(repoNameOrId)
            if (repo == null) {
                repo = getById(repoNameOrId)
                if (repo == null) {
                    return Ret.createError(null, "no repo matched the name or id '$repoNameOrId'")
                }
            }

            repo
        }

        return Ret.createSuccess(repo)
    }

    override suspend fun getByFullSavePath(fullSavePath: String, onlyReturnReadyRepo:Boolean, requireSyncRepoInfoWithGit:Boolean): RepoEntity? {
        val repo = dao.getByFullSavePath(fullSavePath)
        if(repo==null) {
            return null
        }

        if(onlyReturnReadyRepo && repoIsNotReady(repo)) {
            return null
        }

        if(requireSyncRepoInfoWithGit) {
            Libgit2Helper.updateRepoInfo(repo)
        }

        return repo
    }

    override suspend fun getByIdNoSyncWithGit(id: String): RepoEntity? {
        return dao.getById(id)
    }

    override suspend fun getAll(updateRepoInfo:Boolean): List<RepoEntity> {
        val list = dao.getAll()

        if(updateRepoInfo) {
            val settings = SettingsUtil.getSettingsSnapshot()

            list.forEachBetter {
                Libgit2Helper.updateRepoInfo(it, settings = settings)
            }
        }

        return list
    }

    override suspend fun cloneDoneUpdateRepoAndCreateRemote(item: RepoEntity) {
        val remoteRepository = AppModel.dbContainer.remoteRepository
        val db = AppModel.dbContainer.db

        val remoteForSave = RemoteEntity(
            remoteName = item.pullRemoteName,
            remoteUrl = item.pullRemoteUrl,
            isForPull = Cons.dbCommonTrue,
            isForPush = Cons.dbCommonTrue,
            credentialId = item.credentialIdForClone,
            pushCredentialId = item.credentialIdForClone,
            repoId = item.id,
        )

        //判断是否是singleBranch模式
        if(dbIntToBool(item.isSingleBranch)) {
            remoteForSave.fetchMode=Cons.dbRemote_Fetch_BranchMode_SingleBranch
            remoteForSave.singleBranch=item.branch
        }

        //在事务中新增remote和更新repo
        db.withTransaction {
            //疑惑：这里是否有必要删除一下remote表中repoId为当前item.id的记录？正常来说是不需要删的，
            // 因为克隆只会成功一次，这个方法也只会被调用一次，所以应该不会重复创建相同remoteName和repoId的Remote，
            // 但删一下的话，容错性似乎更好？尤其是在测试的时候，可能直接改数据库，导致这个方法被调用多次，
            // 插入多个重名的remote，删一下似乎更好
            // 但正常来说应该是不需要删的
            remoteRepository.deleteByRepoId(item.id)  //这个其实正常来说不需要调用，但也有可能发生并发问题，导致错误调用这个代码块两次，那样remote表的记录会有错误，所以删一下保险，正常来说刚克隆完的仓库本来就是要有且只有一个remote origin的

            remoteRepository.insert(remoteForSave)
            update(item)
        }
    }

    override suspend fun getAReadyRepo(): RepoEntity? {
        val repos = getAll()
        if(repos.isEmpty()) {
            return null
        }

        for(r in repos) {
            if (repoIsReady(r)){
                Libgit2Helper.updateRepoInfo(r)

                return r
            }
        }

        return null;
    }

    override suspend fun getReadyRepoList(requireSyncRepoInfoWithGit:Boolean): List<RepoEntity> {
        val repoList = mutableListOf<RepoEntity>()

        val repos = getAll()

        val settings = SettingsUtil.getSettingsSnapshot()

        for(r in repos) {
            if (repoIsReady(r)) {
                if(requireSyncRepoInfoWithGit) {
                    Libgit2Helper.updateRepoInfo(r, settings = settings)
                }

                repoList.add(r)
            }
        }

        return repoList;
    }

    override suspend fun updateCredentialIdByCredentialId(
        oldCredentialIdForClone: String,
        newCredentialIdForClone: String
    ) {
        dao.updateCredentialIdByCredentialId(oldCredentialIdForClone,newCredentialIdForClone)
    }

    override suspend fun unlinkCredentialIdByCredentialId(credentialIdForClone: String) {
        updateCredentialIdByCredentialId(credentialIdForClone, "")
    }

    override suspend fun updateErrFieldsById(
        repoId: String,
        hasUncheckedErr: Int,
        latestUncheckedErrMsg: String
    ) {
        dao.updateErrFieldsById(
            repoId,
            hasUncheckedErr,  //这个字段其实有点多余，err字段不为空就等于有错误否则就当作没有不就行了？
            // "errorMsg (错误记录创建时间戳,时间戳为了方便检查有无加时间戳而加的后缀)"
            addTimeStampIfErrMsgIsNotBlank(latestUncheckedErrMsg)
        )
    }

    override suspend fun checkedAllErrById(repoId: String) {
        //更新repo表
        //设置hasUncheckedErr为假，最新未检消息为空字符串
        updateErrFieldsById(repoId, Cons.dbCommonFalse, "")

        //更新error表
        val errDb = AppModel.dbContainer.errorRepository
        //更新error表所有匹配repoId的条目的isChecked状态为true
        errDb.updateIsCheckedByRepoId(repoId, Cons.dbCommonTrue)
    }

    override suspend fun setNewErrMsg(repoId: String, errMsg: String) {
        updateErrFieldsById(repoId, Cons.dbCommonTrue, errMsg)
    }

    override suspend fun updateBranchAndCommitHash(
        repoId: String,
        branch: String,
        lastCommitHash: String,
        isDetached:Int,
        upstreamBranch:String
    ) {
        dao.updateBranchAndCommitHash(repoId, branch, lastCommitHash, isDetached, upstreamBranch)
    }

    //checkout commit和tag用的
    override suspend fun updateDetachedAndCommitHash(
        repoId: String,
        lastCommitHash: String,
        isDetached: Int
    ) {
        dao.updateDetachedAndCommitHash(repoId, lastCommitHash, isDetached)
    }

    override suspend fun updateCommitHash(repoId: String, lastCommitHash: String) {
        dao.updateCommitHash(repoId,lastCommitHash)
    }

    override suspend fun updateUpstream(repoId:String, upstreamBranch: String) {
        dao.updateUpstream(repoId, upstreamBranch)
    }

    override suspend fun updateLastUpdateTime(repoId: String, lastUpdateTime: Long) {
        // 更新最后和服务器同步数据的时间，只更新时间就行，workStatus状态会在查询repo时更新
        dao.updateLastUpdateTime(repoId,lastUpdateTime)
    }

    override suspend fun updateIsShallow(repoId: String, isShallow: Int) {
        dao.updateIsShallow(repoId, isShallow)
    }

    override suspend fun getByStorageDirId(storageDirId: String): List<RepoEntity> {
        return dao.getByStorageDirId(storageDirId)
    }

    override suspend fun deleteByStorageDirId(storageDirId: String) {
        dao.deleteByStorageDirId(storageDirId)
    }

    override suspend fun importRepos(
        dir: String,
        isReposParent: Boolean,
        repoNamePrefix:String,
        repoNameSuffix:String,
        parentRepoId:String?,
        credentialId:String?,

    ): ImportRepoResult {
        val repos = getAll(updateRepoInfo = false).toMutableList()

        var all = 0  // count of all repos found. btw: this is not all subdirs count under dir, this only mean how many git repos found, it may less than subdirs under dir
        var success=0  // count of success imported
        var existed = 0  // count of already exists in db, need not import
        var failed = 0  // count of import failed (target is a repo, but import err)


        if(isReposParent) {  // scan repos under this folder(only scan top level, won't do recursive), in this case may found many repos
            val subdirs = File(dir).listFiles { it -> it.isDirectory }
            //has sub directories, try scan folder
            if(subdirs!=null && subdirs.isNotEmpty()) {
                subdirs.forEachBetter { sub ->
                    try {
                        Repository.open(sub.canonicalPath).use { repo->
                            all++

                            val repoWorkDirPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                            if(repos.indexOfFirst {it.fullSavePath == repoWorkDirPath} != -1) {
                                existed++
                            }else {
                                val importSuccess = importSingleRepo(
                                    repo = repo,
                                    repoWorkDirPath = repoWorkDirPath,
                                    initRepoName = repoNamePrefix + sub.name + repoNameSuffix,
                                    addRepoToThisListIfSuccess = repos,
                                    parentRepoId=parentRepoId,
                                    credentialId=credentialId
                                )

                                if(importSuccess) {
                                    success++
                                }else {
                                    failed++
                                }
                            }
                        }
                    }catch (_:Exception) {
                        // not a repo yet
                    }

                }
            }
        }else {  //try open this folder as repo, in this case most only can be has 1 repo
            val dirFile = File(dir)

            try {
                Repository.open(dirFile.canonicalPath).use {repo ->
                    all=1

                    val repoWorkdirPath = Libgit2Helper.getRepoWorkdirNoEndsWithSlash(repo)
                    if(repos.indexOfFirst {it.fullSavePath == repoWorkdirPath} != -1) {  // repo already exists
                        existed = 1
                    }else { // repo not exist, import
                        val importSuccess = importSingleRepo(
                            repo = repo,
                            repoWorkDirPath = repoWorkdirPath,
                            initRepoName = repoNamePrefix + dirFile.name + repoNameSuffix,
                            parentRepoId=parentRepoId,
                            credentialId = credentialId
                        )
                        if(importSuccess) {
                            success = 1
                        }else {
                            failed = 1
                        }
                    }
                }
            }catch (_:Exception) {
                // not a repo yet
            }
        }

        return ImportRepoResult(all=all, success=success, existed=existed, failed=failed)

    }

    /**
     * @param addRepoToThisListIfSuccess when possible import more than 1 repo,
     *  should update all repos list after every imported for avoid add repo redundant,
     *  in that case, should pass all repos list to this param;
     *  but if add only 1 repo, needn't this param yet, pass null or ignore is ok
     */
    private suspend fun importSingleRepo(
        repo:Repository,
        repoWorkDirPath:String,
        initRepoName:String,
        addRepoToThisListIfSuccess:MutableList<RepoEntity>?=null,
        parentRepoId: String?,
        credentialId:String?,

    ):Boolean {
        val funName = "importSingleRepo"

        try {
            // make sure repoName not exists
            var repoName = initRepoName
            if(isRepoNameExist(repoName)) {
                repoName = initRepoName+ "_"+getShortUUID(6)
                if(isRepoNameExist(repoName)) {
                    repoName = initRepoName+ "_"+getShortUUID(8)
                    if(isRepoNameExist(repoName)) {
                        repoName = initRepoName+ "_"+getShortUUID(10)
                        if(isRepoNameExist(repoName)) {
                            repoName = initRepoName+ "_"+getShortUUID(12)
                            if(isRepoNameExist(repoName)) {
                                repoName = initRepoName+ "_"+getShortUUID(16)
                            }
                        }
                    }
                }
            }

            val repoEntity = RepoEntity()
            val remoteEntityList = mutableListOf<RemoteEntity>()

            repoEntity.repoName = repoName
//                repoEntity.fullSavePath = repoDir.canonicalPath // bad, cause "repo/.git" and "repo", both can open as repo, and is same repo, but difference path
            repoEntity.fullSavePath = repoWorkDirPath
            repoEntity.workStatus = Cons.dbRepoWorkStatusUpToDate
            repoEntity.createBy = Cons.dbRepoCreateByImport

            if(parentRepoId != null) {
                repoEntity.parentRepoId = parentRepoId
            }

            val remotes = Libgit2Helper.getRemoteList(repo)
            remotes.forEachBetter { remoteName ->
                val remoteEntity = RemoteEntity()
                remoteEntity.remoteName = remoteName
                remoteEntity.repoId = repoEntity.id

                //严格来说应该选两个凭据，一个在fetch时使用，另一个在push时使用，但多数情况fetch/push的url相同，用的凭据也相同，
                // 所以实现的时候就做成了只能选一个凭据。（导入时选不选凭据其实无所谓，反正之后都可以手动关联）
                if(credentialId!=null) {
                    remoteEntity.credentialId = credentialId
                    remoteEntity.pushCredentialId = credentialId
                }

                remoteEntityList.add(remoteEntity)
            }

            val remoteDb = AppModel.dbContainer.remoteRepository
            AppModel.dbContainer.db.withTransaction {
                // insert repo
                insert(repoEntity)

                // insert remotes
                remoteEntityList.forEachBetter {remote ->
                    remoteDb.insert(remote)
                }
            }

            // in most case, this list is all repos list, if success, add repo into it, or dont add if is not necessary
            try {
                addRepoToThisListIfSuccess?.add(repoEntity)
            }catch (addToListException:Exception) {
                // concurrent exception, maybe, but is ok, because repo is inserted, so ignore add to list exception
                MyLog.e(TAG, "#$funName: err when add repo to list, err=${addToListException.localizedMessage}")
            }

            return true
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: import repo err, err=${e.stackTraceToString()}")
            return false
        }
    }

    override suspend fun isGoodRepoName(name: String): Boolean {
        return !strHasIllegalChars(name) && !isRepoNameExist(name)
    }

    override suspend fun updateRepoName(repoId:String, name: String) {
        dao.updateRepoName(repoId, name)
    }

    private fun repoIsReady(repoEntity: RepoEntity?): Boolean {
        return isRepoReadyAndPathExist(repoEntity)
    }

    private fun repoIsNotReady(repoEntity: RepoEntity?) = repoIsReady(repoEntity).not();

    /*
        suspend fun exampleWithTransaction(){
            //get other dao
            val otherDao = AppModel.dbContainer.db.credentialDao()
            //get other repository
            val otherRepository = AppModel.dbContainer.credentialRepository

            // do transaction
            AppModel.dbContainer.db.withTransaction {
                //use dao
                dao.insert(RepoEntity())
                otherDao.delete(CredentialEntity())

                //use repository function
                insert(RepoEntity())  // this repository's function
                otherRepository.delete(CredentialEntity()) // other repository's function
            }
        }

         */
    override suspend fun getIdByRepoNameAndExcludeId(repoName: String, excludeId: String): String? {
        return dao.getIdByRepoNameAndExcludeId(repoName, excludeId)
    }

    override suspend fun isRepoNameAlreadyUsedByOtherItem(repoName: String, excludeId: String): Boolean {
        return getIdByRepoNameAndExcludeId(repoName, excludeId) != null
    }


    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

}
