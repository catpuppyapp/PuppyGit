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

import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.dto.RemoteDtoForCredential
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface RemoteRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
//    fun getAllStream(): Flow<List<RemoteEntity?>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
//    fun getStream(id: String): Flow<RemoteEntity?>

    suspend fun getById(id: String): RemoteEntity?

    /**
     * Insert item in the data source
     */
    suspend fun insert(item: RemoteEntity)

    /**
     * Delete item from the data source
     */
    suspend fun delete(item: RemoteEntity)
    suspend fun deleteByRepoId(repoId: String)

    /**
     * Update item in the data source
     */
    suspend fun update(item: RemoteEntity)

    suspend fun getByRepoIdAndRemoteName(repoId: String, remoteName:String): RemoteEntity?

    suspend fun getLinkedRemoteDtoForCredentialList(credentialId:String):List<RemoteDtoForCredential>
    suspend fun getUnlinkedRemoteDtoForCredentialList(credentialId:String):List<RemoteDtoForCredential>
    //没必要写这个，直接getByRepoIdAndRemoteName，修改，再update就行了
//    suspend fun updateByRepoIdAndRemoteName(repoId: String, remoteName: String, item: RemoteEntity)

    //可实现 关联/取消关联 凭据
    suspend fun updateCredentialIdByRemoteId(remoteId:String,credentialId:String)

    //可实现解除某个凭据的所有关联，或者将某个凭据所有关联条目关联到另一个凭据
    suspend fun updateCredentialIdByCredentialId(oldCredentialId:String,newCredentialId:String)

    suspend fun linkCredentialIdByRemoteId(remoteId:String,credentialId:String)
    suspend fun unlinkCredentialIdByRemoteId(remoteId:String)
    suspend fun unlinkAllCredentialIdByCredentialId(credentialId:String)
    suspend fun getRemoteDtoListByRepoId(repoId: String):List<RemoteDto>
    suspend fun updateRemoteUrlById(id:String, remoteUrl:String, requireTransaction: Boolean = true)
    suspend fun updatePushUrlById(id:String, url:String)
    suspend fun updatePushCredentialIdByRemoteId(remoteId:String, credentialId:String)
    suspend fun updatePushCredentialIdByCredentialId(oldCredentialId:String, newCredentialId:String)

    suspend fun updateFetchAndPushCredentialIdByRemoteId(remoteId:String, fetchCredentialId:String, pushCredentialId:String)
    suspend fun updateFetchAndPushCredentialIdByCredentialId(oldFetchCredentialId: String, oldPushCredentialId: String, newFetchCredentialId:String, newPushCredentialId:String)

    suspend fun subtractTimeOffset(offsetInSec:Long)

    suspend fun updateAllFetchAndPushCredentialId(fetchCredentialId: String, pushCredentialId: String)
    suspend fun updateAllFetchCredentialId(fetchCredentialId: String)
    suspend fun updateAllPushCredentialId(pushCredentialId: String)

}
