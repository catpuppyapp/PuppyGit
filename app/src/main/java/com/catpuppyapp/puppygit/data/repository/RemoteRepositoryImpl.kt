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
import com.catpuppyapp.puppygit.data.dao.RemoteDao
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.dto.RemoteDtoForCredential
import com.catpuppyapp.puppygit.dto.updateRemoteDtoList
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.github.git24j.core.Remote
import com.github.git24j.core.Repository

private const val TAG = "RemoteRepositoryImpl"

class RemoteRepositoryImpl(private val dao: RemoteDao) : RemoteRepository {

    override suspend fun getById(id: String): RemoteEntity? {
        val remoteFromDb = dao.getById(id)?:return null

        try{
            syncRemoteInfoWithGit(remoteFromDb)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getById err: ${e.stackTraceToString()}")
        }

        return remoteFromDb
    }

    override suspend fun insert(item: RemoteEntity) {
        //仓库+remoteName必须唯一
        if(getByRepoIdAndRemoteName(item.repoId, item.remoteName) == null) {
            dao.insert(item)
        }
    }

    override suspend fun delete(item: RemoteEntity) = dao.delete(item)

    override suspend fun update(item: RemoteEntity) = dao.update(item)

    override suspend fun deleteByRepoId(repoId: String) {
        dao.deleteByRepoId(repoId)
    }

    override suspend fun getByRepoIdAndRemoteName(repoId: String, remoteName:String): RemoteEntity? {
        val remoteFromDb = dao.getByRepoIdAndRemoteName(repoId,remoteName)?:return null

        try{
            syncRemoteInfoWithGit(remoteFromDb)
        }catch (e:Exception) {
            MyLog.e(TAG, "#getByRepoIdAndRemoteName err: ${e.stackTraceToString()}")
        }

        //即使同步不了信息，若数据库有，也应返回数据库的对象，要不然用来检查repoId和remoteName的代码就会出错，可能重复插入相同repoId+remoteName
        return remoteFromDb
    }

    override suspend fun getLinkedRemoteDtoForCredentialList(credentialId: String): List<RemoteDtoForCredential> {
        val ret = dao.getLinkedRemoteDtoForCredentialList(credentialId)
        setRemoteInfoForDtoList(ret)
        return ret
    }

    override suspend fun getUnlinkedRemoteDtoForCredentialList(credentialId: String): List<RemoteDtoForCredential> {
        val ret = dao.getUnlinkedRemoteDtoForCredentialList(credentialId)
        setRemoteInfoForDtoList(ret)
        return ret
    }

    private suspend fun setRemoteInfoForDtoList(dtoList:List<RemoteDtoForCredential>) {
        if(dtoList.isEmpty()) {
            return
        }

        val repoDb = AppModel.dbContainer.repoRepository

        for(it in dtoList){
            try {
                val repoFromDb = repoDb.getById(it.repoId)
                if (repoFromDb == null) {
                    MyLog.w(TAG, "#setRemoteInfoForDtoList warn: an invalid repo id '${it.repoId}' related by remote '${it.remoteName}', remote id =${it.remoteId}")
                    continue
                }

                Repository.open(repoFromDb.fullSavePath).use { repo ->
                    val remote = Libgit2Helper.resolveRemote(repo, it.remoteName)!!
                    it.remoteFetchUrl = Libgit2Helper.getRemoteFetchUrl(remote)
                    it.remotePushUrl = Libgit2Helper.getRemoteActuallyUsedPushUrl(remote)
                }

            } catch (e: Exception) {
                MyLog.e(TAG, "#setRemoteInfoForDtoList err: update info for remote '${it.remoteName}' err, remoteId=${it.remoteId}, err=${e.stackTraceToString()}")
            }
        }
    }

    override suspend fun updateCredentialIdByRemoteId(remoteId: String, credentialId: String) {
        dao.updateCredentialIdByRemoteId(remoteId, credentialId)
    }

    override suspend fun updateCredentialIdByCredentialId(oldCredentialId: String, newCredentialId: String) {
        dao.updateCredentialIdByCredentialId(oldCredentialId,newCredentialId)
    }

    override suspend fun linkCredentialIdByRemoteId(remoteId: String, credentialId: String) {
        updateCredentialIdByRemoteId(remoteId, credentialId)
    }

    override suspend fun unlinkCredentialIdByRemoteId(remoteId: String) {
        updateCredentialIdByRemoteId(remoteId, "")
    }

    override suspend fun unlinkAllCredentialIdByCredentialId(credentialId: String) {
        updateCredentialIdByCredentialId(credentialId,"")
    }

    override suspend fun getRemoteDtoListByRepoId(repoId: String): List<RemoteDto> {
        val listFromDb =  dao.getRemoteDtoListByRepoId(repoId)
        val repoDb = AppModel.dbContainer.repoRepository
        val repoFromDb = repoDb.getById(repoId)?:throw RuntimeException("no repo found by repoId '$repoId'")
        Repository.open(repoFromDb.fullSavePath).use { repo->
            val listFromGit = Libgit2Helper.getRemoteList(repo)
            //已在git仓库实际不存在但在db仍存在的remotes
            val willDelFromDb = listFromDb.filter { !listFromGit.contains(it.remoteName) }
            val willInsertToDb = listFromGit.filter {gitRemote-> listFromDb.none { it.remoteName == gitRemote } }
            if(willDelFromDb.isEmpty() && willInsertToDb.isEmpty()) {
                syncRemoteDtoInfoWithGit(repo, listFromDb)
                return listFromDb
            }

            if(willDelFromDb.isNotEmpty()) {
                willDelFromDb.forEachBetter { delete(RemoteEntity(id=it.remoteId)) }
            }
            if(willInsertToDb.isNotEmpty()) {
                willInsertToDb.forEachBetter mark@{
                    try{
                        //查remote，若无，不插入db
                        val remote = Remote.lookup(repo, it)?:return@mark

                        val remoteWillInsert = RemoteEntity(
                            repoId = repoId,
                            remoteName = it,
                            remoteUrl = remote.url(),
                            pushUrl = remote.pushurl()?:""
                        )

                        insert(remoteWillInsert)
                    }catch (e:Exception) {
                        val errPrefix="lookup remote '$it' err, err="
                        createAndInsertError(repoId, errPrefix+e.localizedMessage)
                        MyLog.e(TAG, "$errPrefix${e.stackTraceToString()}")
                    }
                }
            }

            //重查同步后的list
            val syncedList = dao.getRemoteDtoListByRepoId(repoId)
            syncRemoteDtoInfoWithGit(repo, syncedList)
            return syncedList
        }

    }

    private suspend fun syncRemoteInfoWithGit(remoteFromDb:RemoteEntity) {
        val repoDb = AppModel.dbContainer.repoRepository
        val repoFromDb = repoDb.getById(remoteFromDb.repoId)?:throw RuntimeException("no repo found by remoteFromDb.repoId '${remoteFromDb.repoId}'")
        Repository.open(repoFromDb.fullSavePath).use { repo->
            val remoteFromGit = Libgit2Helper.resolveRemote(repo, remoteFromDb.remoteName)?:throw RuntimeException("resolve remote '${remoteFromDb.remoteName}' from git failed")
            //查RemoteEntity一般都是为了凭据，需要更新的字段其实很少
            remoteFromDb.remoteUrl = remoteFromGit.url()
            remoteFromDb.pushUrl = remoteFromGit.pushurl()?:""

            // if push url invalid, actually will use fetch url(remoteUrl), so set it to fetch url
            if(remoteFromDb.pushUrl.isBlank()) {
                remoteFromDb.pushUrl = remoteFromDb.remoteUrl
                remoteFromDb.pushUrlTrackFetchUrl = true
            }
        }
    }

    private suspend fun syncRemoteDtoInfoWithGit(repo:Repository, remotes:List<RemoteDto>) {
        updateRemoteDtoList(repo, remotes)
    }

    override suspend fun updateRemoteUrlById(id: String, remoteUrl: String, requireTransaction: Boolean) {
        val act = suspend {
            //就一个sql的情况开不开事务其实意义不大，因为daoImpl里本身会针对update/insert等dmlsql开事务，出异常会回滚，在ServiceImpl里只有执行多个sql或多个操作时，才有必要在外部开事务以回滚全部操作
            dao.updateRemoteUrlById(id,remoteUrl)
        }

        if(requireTransaction) {
            AppModel.dbContainer.db.withTransaction {
                act()
            }
        }else {
            act()
        }
    }

    override suspend fun updatePushUrlById(id: String, url: String) {
        dao.updatePushUrlById(id, url)
    }

    override suspend fun updatePushCredentialIdByRemoteId(
        remoteId: String,
        credentialId: String
    ) {
        dao.updatePushCredentialIdByRemoteId(remoteId, credentialId)
    }

    override suspend fun updatePushCredentialIdByCredentialId(
        oldCredentialId: String,
        newCredentialId: String
    ) {
        dao.updatePushCredentialIdByCredentialId(oldCredentialId, newCredentialId)
    }

    override suspend fun updateFetchAndPushCredentialIdByRemoteId(
        remoteId: String,
        fetchCredentialId: String,
        pushCredentialId: String
    ) {
        AppModel.dbContainer.db.withTransaction {
            dao.updateCredentialIdByRemoteId(remoteId, fetchCredentialId)
            dao.updatePushCredentialIdByRemoteId(remoteId, pushCredentialId)
        }
    }

    override suspend fun updateFetchAndPushCredentialIdByCredentialId(
        oldFetchCredentialId: String,
        oldPushCredentialId: String,
        newFetchCredentialId: String,
        newPushCredentialId: String
    ) {
        AppModel.dbContainer.db.withTransaction {
            dao.updateCredentialIdByCredentialId(oldFetchCredentialId, newFetchCredentialId)
            dao.updatePushCredentialIdByCredentialId(oldPushCredentialId, newPushCredentialId)
        }
    }


    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

    override suspend fun updateAllFetchAndPushCredentialId(fetchCredentialId: String, pushCredentialId: String) {
        dao.updateAllFetchAndPushCredentialId(fetchCredentialId, pushCredentialId)
    }

    override suspend fun updateAllFetchCredentialId(fetchCredentialId: String) {
        dao.updateAllFetchCredentialId(fetchCredentialId)
    }

    override suspend fun updateAllPushCredentialId(pushCredentialId: String) {
        dao.updateAllPushCredentialId(pushCredentialId)
    }

}
