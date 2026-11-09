package com.catpuppyapp.puppygit.utils.cache

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.catpuppyapp.puppygit.utils.state.Saver.rememberSaveableString
import kotlinx.coroutines.sync.Mutex

/**
 * this is default cache instance
 */
object Cache:CacheStoreImpl(){
    const val keySeparator = ":"

    object Key {
        const val filesListStateKeyPrefix = "FilesPageListState"
//        val changeListInnerPage_SavePatchPath = "cl_patchsavepath"
//        val changeListInnerPage_RequireDoSyncAfterCommit = "cliprdsac"  //这个其实改用状态变量能达到同样的效果
        //            val changeListInnerPage_RequirePull = "cliprpul";
//            val changeListInnerPage_RequirePush = "cliprpus";
//            val changeListInnerPage_RequireSync = "cliprsyn";
        const val changeListInnerPage_requireDoActFromParent = "changeListInnerPage_requireDoActFromParent";
        const val repoTmpStatusPrefix = "repo_tmp_status"  // prefix+keyseparator+repoId，例如 "repo_tmp_status:a29388d9988"

        const val editorPageSaveLockPrefix = "editor_save_lock"

        //子页面状态变量的前缀，例如：DiffScreen的stateKeyTag遵循如下格式："sub_page:DiffScreen:随机编号"
        const val subPagesStateKeyPrefix = "sub_page_state"

//        const val diffScreen_underRepoPathKey = "diffScreen_underRepoPathKey"
//        const val diffScreen_diffableItemListKey = "diffScreen_diffableItemListKey"

//        const val fileHistory_fileRelativePathKey = "fileHistory_fileRelativePathKey"
//        const val subPageEditor_filePathKey = "subPageEditor_filePathKey"

        val diffableList_of_fromDiffScreenBackToWorkTreeChangeList = "diffableList_of_fromDiffScreenBackToWorkTreeChangeList"
//        val diffableList_of_fromDiffScreenBackToIndexChangeList = "diffableList_of_fromDiffScreenBackToIndexChangeList"
    }


    /**
     * 这个估计占不了多少内存，所以就不清了，如果清的话有点麻烦，需要避免清掉当前editor打开的文件的key，不然可能会有多个协程保存同一个文件
     * generate cache key for save lock, you can use this key get lock obj from Cache for saving file
     * @return a cache key, e.g. "editor_save_lock:/path/to/file"
     */
    private fun getKeyOfSaveLock(filePath:String):String {
        return Key.editorPageSaveLockPrefix + keySeparator + filePath
    }

    fun getSaveLockOfFile(filePath:String):Mutex {
        return getOrPutByType(getKeyOfSaveLock(filePath), default = { Mutex() })
    }

    /**
     * 一般这个不用清，占不了多少内存，用户估计打开不了几个文件
     * TODO 如果清的话有点麻烦，需要避免清掉当前editor打开的文件的key，不然可能会有多个协程保存同一个文件，考虑下怎么避免
     */
    fun clearFileSaveLock() {
        clearByKeyPrefix(Key.editorPageSaveLockPrefix + keySeparator)
    }

    private fun getFilesListStateKey(path:String):String {
        return Key.filesListStateKeyPrefix+keySeparator+path
    }

    fun clearFilesListStates() {
        clearByKeyPrefix(Key.filesListStateKeyPrefix+keySeparator)
    }

    fun getFilesListStateByPath(path:String): LazyListState {
        // key有点太长了
        val key = getFilesListStateKey(path)

        val restoreListState = Cache.getByType<LazyListState>(key)

        // 有则恢复，无则新建
        return if(restoreListState == null) {
            val newListState = LazyListState(0,0)
            Cache.set(key, newListState)
            newListState
        }else{
            restoreListState
        }
    }



    // 自定义状态存储器key相关函数：开始

    fun clearAllSubPagesStates() {
        clearByKeyPrefix(Key.subPagesStateKeyPrefix+keySeparator)
    }


    /**
     * 给子页面生成stateKeyTag的函数，这个函数最关键的操作是给【加子页面key前缀 和 给子页面生成随机id】，
     * 前缀的作用是用来清理内存时标识哪些是子页面的状态，具体实现就是在返回顶级页面后子页面已经全部弹出导航栈，这时就清理所有包含子页面前缀的key，释放内存；
     * 随机数则是为了避免 a->b-c->b 这样的路径上有重复的子页面实例出现导致状态冲突。
     *
     * 顶级页面和子页面和inner page的区别：
     * 顶级页面和子页面都是在导航组件导航进入的，其返回也是导航返回的（naviUp()），inner page本质上是组件
     * 例如：HomeScreen是顶级页面；BranchListScreen是子页面；ChangeListInnerPage是组件；Dialog弹窗之类的也是组件。
     */
    @Composable
    fun getSubPageKey(stateKeyTag:String):String {
        // use a custom saver for avoid rememberSaveable crashing the LazyColumn, is a bug of LazyColumn
        // e.g. "sub_pages_key_prefix:DiffScreen:ak1idkjgkk"
        return rememberSaveableString {
            StringBuilder(Key.subPagesStateKeyPrefix)
                .append(keySeparator)
                .append(stateKeyTag)
                .append(keySeparator)
                .append(getShortUUID())
                .toString()
        }
    }

    /**
     * 给组件用的，一个页面可能有多个组件，每个都需要单独生成，会"继承"父组件的stateKeyTag，
     *  是否会在返回顶级页面清理取决于其父组件是否是顶级页面，由parentKey(即父组件的stateKeyTag)判断，
     *  每个组件都应该有各自的stateKeyTag，子组件若需要，往下传递即可，inner page本质上也是组件而不是页面（不可导航进入），
     *  所以应使用此函数为其生成stateKeyTag。
     */
    @Composable
    fun getComponentKey(parentKey:String, stateKeyTag:String): String {
        // e.g. 子页面："sub_pages_key_prefix:DiffScreen:ak1idkjgkk:DiffRow:13idgiwkfd"
        //      顶级页面："HomeScreen:abcdef12345:DiffRow:abcdef12345"
        return rememberSaveableString {
            StringBuilder(parentKey)
                .append(keySeparator)
                .append(stateKeyTag)
                .append(keySeparator)
                .append(getShortUUID())
                .toString()
        }
    }
    // 自定义状态存储器key相关函数：结束


    fun combineKeys(vararg keys: String):String {
        val ret = StringBuilder()

        for (key in keys) {
            ret.append(key).append(keySeparator)
        }

        return ret.removeSuffix(keySeparator).toString()
    }
}
