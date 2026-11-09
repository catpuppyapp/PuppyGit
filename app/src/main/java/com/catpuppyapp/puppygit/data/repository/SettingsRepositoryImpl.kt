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

import com.catpuppyapp.puppygit.data.dao.SettingsDao
import com.catpuppyapp.puppygit.data.entity.SettingsEntity
import com.catpuppyapp.puppygit.utils.getSecFromTime

class SettingsRepositoryImpl(private val dao: SettingsDao) : SettingsRepository {
//
//    override suspend fun insert(item: SettingsEntity) {
//        val timeInSec = getSecFromTime()
//        item.baseFields.baseCreateTime = timeInSec
//        item.baseFields.baseUpdateTime = timeInSec
//
//        dao.insert(item)
//    }
//
//    override suspend fun delete(item: SettingsEntity) = dao.delete(item)
//
//    override suspend fun update(item: SettingsEntity){
//        item.baseFields.baseUpdateTime = getSecFromTime()
//        dao.update(item)
//    }
//
//    override suspend fun getOrInsertByUsedFor(usedFor: Int) :SettingsEntity?{
//        val s = dao.getByUsedFor(usedFor)
//
//        //若条目不存在，创建并返回
////        if(s==null) {  //不要管这个黄色警告，s是有可能为null的，查询不出数据的时候就会
////            if(usedFor == Cons.dbSettingsUsedForChangeList) {  //新插入的条目，肯定不用迁移，直接返回即可
////                return insertChangeListSettingsThenReturnIt(usedFor)
////            }//else if usedFor == other settings
////
////            if(usedFor == Cons.dbSettingsUsedForCommonGitConfig) {
////                return insertCommonGitConfigSettingsThenReturnIt(usedFor)
////            }
////
////            if(usedFor == Cons.dbSettingsUsedForEditor) {
////                return insertEditorSettingsThenReturnIt(usedFor)
////            }
////        }
//
//        s?:throw RuntimeException("no settings for usedFor:"+usedFor)
//
////        //检查是否需要迁移设置项
////        val needUpdate = SettingsMigrator.migrateSettingsIfNeed(s)
////        if(needUpdate) {
////            update(s)
////        }
//
//        return s
//    }
//
//    private suspend fun insertChangeListSettingsThenReturnIt(usedFor: Int): SettingsEntity {
//        val cs = ChangeListSettings()
//        cs.version = SettingsVersion.changeListSettingsCurrentVer
//        cs.lastUsedRepoId = ""
//        val csStr = MoshiUtil.changeListSettingsJsonAdapter.toJson(cs)
//        val newS = SettingsEntity(usedFor = usedFor, jsonVal = csStr)
//        insert(newS)
//        return newS
//    }
//
//    private suspend fun insertEditorSettingsThenReturnIt(usedFor: Int): SettingsEntity {
//        val cs = EditorSettings()
//        cs.version = SettingsVersion.editorSettingsCurrentVer  //创建时设成当前版本
//        cs.lastEditedFilePath = ""
//        val csStr = MoshiUtil.editorSettingsJsonAdapter.toJson(cs)
//        val newS = SettingsEntity(usedFor = usedFor, jsonVal = csStr)
//        insert(newS)
//        return newS
//    }
//
//    private suspend fun insertCommonGitConfigSettingsThenReturnIt(usedFor: Int): SettingsEntity {
//        //修改这为你的类
//        val cs = CommonGitConfigSettings()
//        //修改这行为你的类的当前版本号
//        cs.version = SettingsVersion.commonGitConfigSettingsCurrentVer
//        //为你的类设置默认值
//        cs.username = ""
//        cs.email = ""
//
//        //转成json存到数据库
//        val csStr = MoshiUtil.commonGitConfigSettingsJsonAdapter.toJson(cs)
//        val newS = SettingsEntity(usedFor = usedFor, jsonVal = csStr)
//        insert(newS)
//
//        //返回刚刚创建的SettingsEntity
//        return newS
//    }

    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

}
