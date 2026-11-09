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

import androidx.room.Query
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ErrorRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllStream(): Flow<List<ErrorEntity?>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getStream(id: String): Flow<ErrorEntity?>

    /**
     * Insert item in the data source
     */
    suspend fun insert(item: ErrorEntity)

    /**
     * Delete item from the data source
     */
    suspend fun delete(item: ErrorEntity)

    /**
     * Update item in the data source
     */
    suspend fun update(item: ErrorEntity)



    fun getListByRepoId(repoId: String): List<ErrorEntity>

    fun getById(id: String): ErrorEntity?

    fun updateIsCheckedByRepoId(repoId: String, isChecked:Int)
    fun deleteErrOverTime(timeInSec:Long)

    fun deleteErrOverLimitTime()

    fun deleteByRepoId(repoId: String)

    suspend fun subtractTimeOffset(offsetInSec:Long)

}
