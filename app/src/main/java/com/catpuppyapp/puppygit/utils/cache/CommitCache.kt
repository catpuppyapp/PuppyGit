package com.catpuppyapp.puppygit.utils.cache

import com.catpuppyapp.puppygit.git.CommitDto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * 缓存仓库的提交
 */
@Deprecated("废弃了，实际感觉没什么效果，加载并没变快，缓存数据还得维护，不然会在重置之类的操作后导致提交树出错，给我增加的负担大于带来的好处，不用了")
object CommitCache {
    private val cache = mutableMapOf<String, MutableMap<String, CommitDto>>()
    private val lock = Mutex()

    //每个仓库最多存多少个提交
    private const val EACH_REPO_CACHE_SIZE = 100

    suspend fun cacheIt(repoId:String, commitFullHash:String, commitDto: CommitDto) {
        //如果日后重新启用这个类，把return注释，就行了
        return;

        lock.withLock {
            val cacheOfRepo = getCacheMapOfRepo(repoId)
            if(cacheOfRepo.size < EACH_REPO_CACHE_SIZE) {
                cacheOfRepo.put(commitFullHash, commitDto)
            }
        }
    }

    suspend fun getCachedDataOrNull(repoId: String, commitFullHash: String): CommitDto? {
        return null;

        lock.withLock {
            val cacheOfRepo = getCacheMapOfRepo(repoId)
            return cacheOfRepo.get(commitFullHash)
        }
    }

    suspend fun clear() {
        return;

        lock.withLock {
            cache.clear()
        }
    }

    private fun getCacheMapOfRepo(repoId:String) = cache.getOrPut(repoId) { mutableMapOf() }

}
