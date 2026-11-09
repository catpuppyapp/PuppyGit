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
import com.catpuppyapp.puppygit.data.entity.StorageDirEntity

/**
 * Database access object to access the Inventory database
 */
@Dao
interface StorageDirDao {

    @Insert
    suspend fun insert(item: StorageDirEntity)

    //update by id
    @Update
    suspend fun update(item: StorageDirEntity)

    //delete by id
    @Delete
    suspend fun delete(item: StorageDirEntity)

    @Query("SELECT * from storageDir where id = :id")
    suspend fun getById(id:String): StorageDirEntity?

    @Query("SELECT * from storageDir where fullPath = :fullPath LIMIT 1")
    suspend fun getByFullPath(fullPath:String): StorageDirEntity?

    @Query("SELECT * from storageDir where name = :name LIMIT 1")
    suspend fun getByName(name:String): StorageDirEntity?

    @Query("SELECT * from storageDir where fullPath = :fullPath or name=:name LIMIT 1")
    suspend fun getByNameOrFullPath(name:String, fullPath:String): StorageDirEntity?

    @Query("SELECT * from storageDir where id!=:excludeId and (fullPath = :fullPath or name=:name) LIMIT 1")
    suspend fun getByNameOrFullPathExcludeId(name: String, fullPath: String, excludeId:String): StorageDirEntity?

    @Query("SELECT * from storageDir")
    suspend fun getAll(): List<StorageDirEntity>

    @Query("SELECT * from storageDir where baseStatus=:status")
    suspend fun getListByStatus(status:Int): List<StorageDirEntity>

    @Query("UPDATE storageDir set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
