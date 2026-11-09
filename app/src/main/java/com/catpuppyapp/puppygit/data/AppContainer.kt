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

package com.catpuppyapp.puppygit.data

import android.content.Context
import com.catpuppyapp.puppygit.data.repository.CredentialRepository
import com.catpuppyapp.puppygit.data.repository.CredentialRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.DomainCredentialRepository
import com.catpuppyapp.puppygit.data.repository.DomainCredentialRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.ErrorRepository
import com.catpuppyapp.puppygit.data.repository.ErrorRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.PassEncryptRepository
import com.catpuppyapp.puppygit.data.repository.PassEncryptRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.RemoteRepository
import com.catpuppyapp.puppygit.data.repository.RemoteRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.RepoRepository
import com.catpuppyapp.puppygit.data.repository.RepoRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.SettingsRepository
import com.catpuppyapp.puppygit.data.repository.SettingsRepositoryImpl
import com.catpuppyapp.puppygit.data.repository.StorageDirRepository
import com.catpuppyapp.puppygit.data.repository.StorageDirRepositoryImpl

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val db:AppDatabase

    val repoRepository: RepoRepository
    val errorRepository: ErrorRepository
    val credentialRepository: CredentialRepository
    val remoteRepository: RemoteRepository

    @Deprecated("废弃")
    val settingsRepository: SettingsRepository

    @Deprecated("20241205 之后，采用主密码，此类已不再维护，但保留类并仍在启动时检查是否需要迁移密码以兼容旧版本app")
    val passEncryptRepository: PassEncryptRepository

    @Deprecated("废弃")
    val storageDirRepository: StorageDirRepository

    val domainCredentialRepository: DomainCredentialRepository
    // other repository write here
}

/**
 * [AppContainer] implementation that provides instance of [RepoRepositoryImpl]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val db: AppDatabase = AppDatabase.getDatabase(context)
    /**
     * Implementation for [RepoRepository]
     */
    override val repoRepository: RepoRepository by lazy {
        RepoRepositoryImpl(db.repoDao())
    }

    override val errorRepository: ErrorRepository by lazy {
        ErrorRepositoryImpl(db.errorDao())
    }

    override val credentialRepository: CredentialRepository by lazy {
        CredentialRepositoryImpl(db.credentialDao())
    }

    override val remoteRepository: RemoteRepository by lazy {
        RemoteRepositoryImpl(db.remoteDao())
    }

    @Deprecated("废弃")
    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(db.settingsDao())
    }

    @Deprecated("已废弃")
    override val passEncryptRepository: PassEncryptRepository by lazy {
        PassEncryptRepositoryImpl(db.passEncryptDao())
    }

    @Deprecated("废弃")
    override val storageDirRepository: StorageDirRepository by lazy {
        StorageDirRepositoryImpl(db.storageDirDao())
    }

    override val domainCredentialRepository: DomainCredentialRepository by lazy {
        DomainCredentialRepositoryImpl(db.domainCredentialDao())
    }
}
