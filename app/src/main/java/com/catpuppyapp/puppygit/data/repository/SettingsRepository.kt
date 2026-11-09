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

import com.catpuppyapp.puppygit.data.entity.SettingsEntity

/**
 * Repository that provides insert, update, delete, and retrieve of [SettingsEntity] from a given data source.
 */
@Deprecated("废弃了，改用json存AppSettings了")
interface SettingsRepository {
//
//    /**
//     * Insert item in the data source
//     */
//    suspend fun insert(item: SettingsEntity)
//
//    /**
//     * Delete item from the data source
//     */
//    suspend fun delete(item: SettingsEntity)
//
//    /**
//     * Update item in the data source
//     */
//    suspend fun update(item: SettingsEntity)
//
//    suspend fun getOrInsertByUsedFor(usedFor:Int): SettingsEntity?
//

    suspend fun subtractTimeOffset(offsetInSec:Long)

}
