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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.catpuppyapp.puppygit.data.dao.CredentialDao
import com.catpuppyapp.puppygit.data.dao.DomainCredentialDao
import com.catpuppyapp.puppygit.data.dao.ErrorDao
import com.catpuppyapp.puppygit.data.dao.PassEncryptDao
import com.catpuppyapp.puppygit.data.dao.RemoteDao
import com.catpuppyapp.puppygit.data.dao.RepoDao
import com.catpuppyapp.puppygit.data.dao.SettingsDao
import com.catpuppyapp.puppygit.data.dao.StorageDirDao
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.data.entity.DomainCredentialEntity
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import com.catpuppyapp.puppygit.data.entity.PassEncryptEntity
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.data.entity.SettingsEntity
import com.catpuppyapp.puppygit.data.entity.StorageDirEntity
import com.catpuppyapp.puppygit.data.migration.MIGRATION_16_17
import com.catpuppyapp.puppygit.data.migration.MIGRATION_17_18
import com.catpuppyapp.puppygit.data.migration.MIGRATION_18_19
import com.catpuppyapp.puppygit.data.migration.MIGRATION_19_20
import com.catpuppyapp.puppygit.data.migration.MIGRATION_20_21
import com.catpuppyapp.puppygit.data.migration.MIGRATION_21_22
import com.catpuppyapp.puppygit.data.migration.MIGRATION_22_23
import com.catpuppyapp.puppygit.data.migration.MIGRATION_23_24
import com.catpuppyapp.puppygit.data.migration.MIGRATION_24_25

/**
 * Database class with a singleton Instance object.
 */
//entities是个数组，多个表多个类可直接写个逗号分隔列表
@Database(entities = [
                        RepoEntity::class,
                        ErrorEntity::class,
                        CredentialEntity::class,
                        RemoteEntity::class,
                        SettingsEntity::class,
                        PassEncryptEntity::class,
                        StorageDirEntity::class,
                        DomainCredentialEntity::class
                     ],
    version = 25,
    //如果支持生成迁移sql，必须设置exportSchema为true，不然就得自己写sql了
    //自动迁移是根据导出的方案生成sql的
    exportSchema = true,
    autoMigrations = [
//    AutoMigration(from=4, to=5, spec=From4To5DelRepoColumn::class),
    // other migration if have
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun repoDao(): RepoDao
    abstract fun errorDao(): ErrorDao
    abstract fun credentialDao(): CredentialDao
    abstract fun remoteDao(): RemoteDao
    abstract fun settingsDao(): SettingsDao
    abstract fun passEncryptDao(): PassEncryptDao
    abstract fun storageDirDao(): StorageDirDao
    abstract fun domainCredentialDao(): DomainCredentialDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null



        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "puppygitdb")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */

                        //迁移失败时删除所有数据重新建表，数据会丢，慎用，调试时频繁改表结构，开启这个很有用，生产环境最好别开，实在不行可通过清除app信息删除旧表
//                    //谨慎使用，最好别用 // ///////.fallbackToDestr///////uctiveMigration()


                    .addMigrations(
//                        MIGRATION_3_4,
//                        MIGRATION_12_13,
                        MIGRATION_16_17,
                        MIGRATION_17_18,
                        MIGRATION_18_19,
                        MIGRATION_19_20,
                        MIGRATION_20_21,
                        MIGRATION_21_22,
                        MIGRATION_22_23,
                        MIGRATION_23_24,
                        MIGRATION_24_25
                        //more migration if have
                        )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
