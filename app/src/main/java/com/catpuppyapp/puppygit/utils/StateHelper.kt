package com.catpuppyapp.puppygit.utils

import androidx.compose.runtime.MutableState
import com.catpuppyapp.puppygit.utils.cache.Cache

/**
 * 这个类的作用是为某些字符串state关联外部数据，目前20240428仅用于为needRefresh状态变量附带数据，实际上过度设计了，根本没必要整这么复杂
 */

private val storage = Cache

enum class StateRequestType {
    invalid,

    //强制重新加载数据，就算有能恢复的数据也不使用，一般用来加载更多的那种列表上，例如 CommitListScreen
    forceReload,

    //cl页面，刷新页面携带了仓库id，以检查当前仓库和请求刷新页面的仓库是不是同一个仓库，若切换过页面，就可能不同
    withRepoId,

    //diff页面切换条目后跳转到列表顶部
    requireGoToTop,

    /**
     * 从diff页面返回到changelist(index to worktree)，请求执行提交所有
     * 提交index和worktree的修改
     */
    indexToWorkTree_CommitAll,

    /**
     * 从diff页面返回到changelist(index to worktree)，请求执行提交所有
     * 仅提交index修改
     */
    headToIndex_CommitAll,

    /**
     * 导入仓库后，跳转到对应仓库。
     * 此request type携带的data为跳转所需的数据
     */
    jumpAfterImport,

    /**
     * go to parent dir of a full path and scroll to let it visible
     */
    goToParentAndScrollToItem,
}

//改变值触发执行刷新页面的代码
fun changeStateTriggerRefreshPage(needRefresh: MutableState<String>, requestType:StateRequestType = StateRequestType.invalid, data:Any? =null, newStateValue: String = getShortUUID()) {
    setStateWithRequestData(state = needRefresh, requestType=requestType, data = data, newStateValue = newStateValue)
}

fun setStateWithRequestData(state: MutableState<String>, requestType:StateRequestType = StateRequestType.invalid, data:Any? =null, newStateValue:String = getShortUUID()) {
    if(requestType != StateRequestType.invalid) {
        storage.set(newStateValue, Pair(requestType, data))
    }

    state.value = newStateValue
}


/**
 * 注意：携带数据的refresh state变量，初始值建议使用非空随机常量，避免覆盖其他页面的请求参数或cache里同名key（一般不会发生，因为只有改变量值时才有可能携带数据然后往cache里存东西，初始值不存）
 */
fun<T> getRequestDataByState(stateValue:String, getThenDel:Boolean = true):Pair<StateRequestType, T?> {
    //这样不行，不能保证每个请求只执行一次
    if(stateValue.isBlank()) {
        return Pair(StateRequestType.invalid, null)
    }

    val requestTypeAndData = if(getThenDel) {
        storage.getByTypeThenDel<Pair<StateRequestType, T?>>(stateValue)
    } else {
        storage.getByType<Pair<StateRequestType, T?>>(stateValue)
    }

    return requestTypeAndData ?: Pair(StateRequestType.invalid, null)
}

fun delRequestDataByState(stateValue:String) {
    //这样不行，不能保证每个请求只执行一次
    if(stateValue.isBlank()) {
        return
    }

    storage.del(stateValue)
}
