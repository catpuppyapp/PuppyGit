package com.catpuppyapp.puppygit.screen.shared

import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.utils.MyLog


private const val TAG = "MainActivityLifeCycle"

private val currentMainActivityLifeCycle = mutableStateOf(MainActivityLifeCycle.NONE)

enum class MainActivityLifeCycle(val code: String) {
    NONE("NONE"),
    ON_CREATE("ON_CREATE"),
    ON_RESUME("ON_RESUME"),
    ON_PAUSE("ON_PAUSE"),
    IGNORE_ONCE_ON_RESUME("IGNORE_ONCE_ON_RESUME")
    ;


    companion object {
        fun fromCode(code: String): MainActivityLifeCycle? {
            return entries.find { it.code == code }
        }
    }
}



fun doActIfIsExpectLifeCycle(expectLifeCycle: MainActivityLifeCycle, nextLifeCycle:MainActivityLifeCycle = MainActivityLifeCycle.NONE, act:()->Unit) {
    MyLog.d(TAG, "#doActIfIsExpectLifeCycle(): current life cycle is '${currentMainActivityLifeCycle.value}'")
    MyLog.d(TAG, "#doActIfIsExpectLifeCycle(): expect life cycle is '$expectLifeCycle'")

    if(currentMainActivityLifeCycle.value == expectLifeCycle) {
        //判断完立刻重置，确保一个事件只消费一次
        setMainActivityLifeCycle(nextLifeCycle)

        act()
    }
}



fun setMainActivityLifeCycle(lifeCycle: MainActivityLifeCycle) {
    currentMainActivityLifeCycle.value = lifeCycle
}


fun setByPredicate(lifeCycle: MainActivityLifeCycle, predicate: (current: MainActivityLifeCycle)->Boolean) {
    if(predicate(currentMainActivityLifeCycle.value)) {
        setMainActivityLifeCycle(lifeCycle)
    }
}
