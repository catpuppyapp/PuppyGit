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

package com.catpuppyapp.puppygit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.catpuppyapp.puppygit.data.entity.CredentialEntity

/**
 * Database access object to access the Inventory database
 */
@Dao
interface CredentialDao {
//
//    @Query("SELECT * from credential ORDER BY id ASC")
//    fun getAllStream(): Flow<List<CredentialEntity?>>
//
//    @Query("SELECT * from credential WHERE id = :id")
//    fun getStream(id: String): Flow<CredentialEntity?>



    @Query("SELECT * from credential order by baseCreateTime DESC")
    suspend fun getAll(): List<CredentialEntity>

    @Insert
    suspend fun insert(item: CredentialEntity)

    @Update
    suspend fun update(item: CredentialEntity)

    @Delete
    suspend fun delete(item: CredentialEntity)

    //用来检测条目是否存在的
    @Query("SELECT id from credential WHERE name = :name LIMIT 1")
    suspend fun getIdByCredentialName(name: String): String?

    @Query("SELECT * from credential WHERE id = :id")
    suspend fun getById(id: String): CredentialEntity?

    @Query("SELECT * from credential WHERE type = :type order by baseCreateTime DESC")
    suspend fun getListByType(type:Int): List<CredentialEntity>

    @Query("SELECT * from credential WHERE encryptVer != :encryptVer")
    suspend fun getByEncryptVerNotEqualsTo(encryptVer:Int): List<CredentialEntity>

    /**
     * 减秒数
     * App版本 48更新了时区设置，之前的时区是加了系统时区偏移量的秒数，全部更新成utc时区的秒数
     */
    @Query("UPDATE credential set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
