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

import com.catpuppyapp.puppygit.data.entity.DomainCredentialEntity
import com.catpuppyapp.puppygit.dto.DomainCredentialDto

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface DomainCredentialRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    suspend fun getAll(): List<DomainCredentialEntity>

    suspend fun getAllDto(): List<DomainCredentialDto>

    suspend fun isDomainExist(domain:String):Boolean
    suspend fun getByDomain(domain:String):DomainCredentialEntity?

    /**
     * Insert item in the data source
     */
    suspend fun insert(item: DomainCredentialEntity)

    /**
     * Delete item from the data source
     */
    suspend fun delete(item: DomainCredentialEntity)

    /**
     * Update item in the data source
     */
    suspend fun update(item: DomainCredentialEntity)


    fun getById(id: String): DomainCredentialEntity?

    suspend fun subtractTimeOffset(offsetInSec:Long)

}
