package com.catpuppyapp.puppygit.utils

import com.catpuppyapp.puppygit.utils.cache.Cache

object RepoStatusUtil {
    private fun getRepoStatusKey(repoId: String):String {
        return Cache.Key.repoTmpStatusPrefix + Cache.keySeparator + repoId
    }

    fun setRepoStatus(repoId:String, status:String) {
        val statusKey = getRepoStatusKey(repoId)
        //执行操作前设置临时状态
        Cache.set(statusKey, status)
    }

    /**
     * @return repo status or null
     */
    fun getRepoStatus(repoId: String):String? {
        return Cache.getByType<String>(getRepoStatusKey(repoId))
    }

    fun clearRepoStatus(repoId: String) {
        //执行操作后清除临时状态。（这里不用刷新页面，finally代码里会刷新）

        //用删除实现其实不太好，省内存，但效率低。不如设成空字符串，因为本来就不占多少内存，当不在乎内存的时候，就应该保证效率，所以不如用set值为空字符串代替删除
//        Cache.ConcurrentMap.del(Cache.ConcurrentMap.Key.repoTmpStatusPrefix+repoId)

        setRepoStatus(repoId, "")
    }
}
