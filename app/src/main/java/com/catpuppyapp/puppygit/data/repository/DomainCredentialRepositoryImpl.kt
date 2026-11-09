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

import com.catpuppyapp.puppygit.data.dao.DomainCredentialDao
import com.catpuppyapp.puppygit.data.entity.DomainCredentialEntity
import com.catpuppyapp.puppygit.dto.DomainCredentialDto
import com.catpuppyapp.puppygit.utils.getSecFromTime

class DomainCredentialRepositoryImpl(private val dao: DomainCredentialDao) : DomainCredentialRepository {
    override suspend fun getAll(): List<DomainCredentialEntity> = dao.getAll()

    override suspend fun getAllDto(): List<DomainCredentialDto> = dao.getAllDto()

    override suspend fun isDomainExist(domain: String): Boolean = getByDomain(domain) != null

    override suspend fun getByDomain(domain: String): DomainCredentialEntity? = dao.getByDomain(domain)

    override suspend fun insert(item: DomainCredentialEntity){
        if(isDomainExist(item.domain)) {
            throw RuntimeException("dc#insert: domain name already exists")
        }

        dao.insert(item)
    }

    override suspend fun delete(item: DomainCredentialEntity) = dao.delete(item)

    override suspend fun update(item: DomainCredentialEntity) {
        val checkExist = getByDomain(item.domain)
        if(checkExist!=null && checkExist.id!=item.id) {
            throw RuntimeException("dc#update: domain name already exists")
        }

        item.baseFields.baseUpdateTime = getSecFromTime()

        dao.update(item)
    }

    override fun getById(id: String): DomainCredentialEntity? = dao.getById(id)

    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

}
