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

import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.utils.AppModel

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface CredentialRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
//    fun getAllStream(): Flow<List<CredentialEntity?>>
    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
//    fun getStream(id: String): Flow<CredentialEntity?>


    suspend fun getAllWithDecrypt(includeNone:Boolean = false, includeMatchByDomain:Boolean = false, masterPassword: String = AppModel.masterPassword.value): List<CredentialEntity>

    /**
     * 不加密也不解密 密码字段，把查出的数据简单返回
     */
    suspend fun getAll(includeNone:Boolean = false, includeMatchByDomain:Boolean = false): List<CredentialEntity>


    /**
     * Insert item in the data source
     */
    suspend fun insertWithEncrypt(item: CredentialEntity, masterPassword: String = AppModel.masterPassword.value)
    /**
     * 不加密也不解密 密码字段，把传入的数据简单插入数据库
     */
    suspend fun insert(item: CredentialEntity)

    /**
     * Delete item from the data source
     */
    suspend fun delete(item: CredentialEntity)

    /**
     * Update item in the data source
     */
    suspend fun updateWithEncrypt(item: CredentialEntity, touchTime: Boolean = true, masterPassword: String = AppModel.masterPassword.value)
    /**
     * 不加密也不解密 密码字段，把传入的数据简单更新到数据库
     */
    suspend fun update(item: CredentialEntity, touchTime: Boolean = true)

    suspend fun isCredentialNameExist(name: String): Boolean

    suspend fun getByIdWithDecrypt(id: String, masterPassword: String = AppModel.masterPassword.value): CredentialEntity?

    /**
     * if id is `SpecialCredential.MatchByDomain.credentialId` will try match credential by url's domain
     *  if you are sure id is not match by domain id, just simple passing empty str as url
     */
    suspend fun getByIdWithDecryptAndMatchByDomain(id: String, url:String, masterPassword: String = AppModel.masterPassword.value): CredentialEntity?
    suspend fun getByIdAndMatchByDomain(id: String, url:String): CredentialEntity?

    /**
     * 不加密也不解密 密码字段，把查出的数据简单返回
     */
    suspend fun getById(id: String, includeNone:Boolean = false, includeMatchByDomain:Boolean = false): CredentialEntity?

    //20241003 disabled, because only support https, get by type is nonsense for now
//    suspend fun getListByType(type:Int): List<CredentialEntity>

//    suspend fun getSshList(): List<CredentialEntity>
//    suspend fun getHttpList(includeNone:Boolean = false, includeMatchByDomain:Boolean = false): List<CredentialEntity>

    suspend fun deleteAndUnlink(item:CredentialEntity)

    /**
     * 加密凭据，一律使用最新版本的加密器，并更新凭据中相关字段
     */
    fun encryptPassIfNeed(item:CredentialEntity?, masterPassword:String)

    /**
     * 使用凭据对应的加密器版本解密凭据
     */
    fun decryptPassIfNeed(item:CredentialEntity?, masterPassword:String)

    /**
     * @return 更新失败的凭据名单
     */
    suspend fun updateMasterPassword(oldMasterPassword:String, newMasterPassword:String):List<String>


    /**
     * 调用此方法检查是否需要升级加密器版本，若需要，会用旧加密器解密密码并用新加密器重新加密
     */
    suspend fun migrateEncryptVerIfNeed(masterPassword: String)

    /**
     * 返回加密器版本与传入参数不符的列表，用来在迁移密码时获取需要迁移的凭据列表
     */
    suspend fun getByEncryptVerNotEqualsTo(encryptVer:Int):List<CredentialEntity>

    suspend fun subtractTimeOffset(offsetInSec:Long)

}
