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

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.dao.ErrorDao
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import com.catpuppyapp.puppygit.utils.daysToSec
import com.catpuppyapp.puppygit.utils.getSecFromTime
import kotlinx.coroutines.flow.Flow

class ErrorRepositoryImpl(private val dao: ErrorDao) : ErrorRepository {
    override fun getAllStream(): Flow<List<ErrorEntity?>> = dao.getAllStream()

    override fun getStream(id: String): Flow<ErrorEntity?> = dao.getStream(id)

    override suspend fun insert(item: ErrorEntity) = dao.insert(item)

    override suspend fun delete(item: ErrorEntity) = dao.delete(item)

    override suspend fun update(item: ErrorEntity) = dao.update(item)

    override fun getListByRepoId(repoId: String): List<ErrorEntity> {

        //话说用户如果一直不点列表怎么办？日志一直留着？应该不会吧？总会点的吧！？
        //获取列表之前，删除一下以前的记录
        // 这个有点搞笑了，获取之前删除，用户一点错误信息，看到一片空白，会懵，
        //  如果非要获取时删除，应该先获取，后删除，这样最起码用户第一次进入列表还能看到错误信息，还稍微合理些，
        //  但还是不好，所以废弃自动删除方案，让用户手动删除比较好，反正错误信息也不占多少硬盘空间
//        deleteErrOverLimitTime()

        //返回当前的列表
        return dao.getListByRepoId(repoId)
    }

    override fun getById(id: String): ErrorEntity? {
        return dao.getById(id)
    }

    override fun updateIsCheckedByRepoId(repoId: String, isChecked: Int) {
        dao.updateIsCheckedByRepoId(repoId,isChecked)
    }

    override fun deleteErrOverTime(timeInSec: Long) {
        dao.deleteErrOverTime(timeInSec)
    }

    override fun deleteErrOverLimitTime() {
        val limitTimeInSec = daysToSec(Cons.dbDeleteErrOverThisDay)
        val nowInSec = getSecFromTime()
        //将删除这天以前的记录
        val willDeleteBeforeThisDayInSec = nowInSec - limitTimeInSec
        if(nowInSec>0) {
            //删除
            deleteErrOverTime(willDeleteBeforeThisDayInSec)
        }
    }

    override fun deleteByRepoId(repoId: String) {
        dao.deleteByRepoId(repoId)
    }

    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

}
