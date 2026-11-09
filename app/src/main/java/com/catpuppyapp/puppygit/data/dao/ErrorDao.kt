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
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ErrorDao {

    @Query("SELECT * from error ORDER BY id ASC")
    fun getAllStream(): Flow<List<ErrorEntity?>>

    @Query("SELECT * from error WHERE id = :id")
    fun getStream(id: String): Flow<ErrorEntity?>

    @Insert
    suspend fun insert(item: ErrorEntity)

    @Update
    suspend fun update(item: ErrorEntity)

    @Delete
    suspend fun delete(item: ErrorEntity)

    //要不要写个分页查询？进入页面先加载几条，点击按钮后加载更多？

    @Query("SELECT * from error WHERE repoId = :repoId order by baseCreateTime DESC")
    fun getListByRepoId(repoId: String): List<ErrorEntity>

    @Query("SELECT * from error WHERE id = :id")
    fun getById(id: String): ErrorEntity?

    @Query("update error set isChecked= :isChecked WHERE repoId = :repoId")
    fun updateIsCheckedByRepoId(repoId: String, isChecked:Int)

    //删除超过指定时间的记录
    @Query("delete from error WHERE baseCreateTime < :timeInSec")
    fun deleteErrOverTime(timeInSec:Long)

    @Query("delete from error WHERE repoId = :repoId")
    fun deleteByRepoId(repoId: String)

    @Query("UPDATE error set baseCreateTime = baseCreateTime-(:offsetInSec), baseUpdateTime = baseUpdateTime-(:offsetInSec)")
    suspend fun subtractTimeOffset(offsetInSec:Long)
}
