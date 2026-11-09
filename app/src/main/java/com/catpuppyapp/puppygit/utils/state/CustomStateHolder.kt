package com.catpuppyapp.puppygit.utils.state

import androidx.compose.runtime.saveable.Saver
import com.catpuppyapp.puppygit.utils.cache.Cache

data class Holder<T>(var key: String, var data:T)

// random是用来在不改变data的情况下修改数据从而触发页面重新渲染的，不过一般用不到
//data class Holder<T>(var key: String, var data:T, var random:String = getShortUUID())
//fun <T> refreshState(state:MutableState<Holder<T>>) {
////    state.value = state.value
//    // 改变random，但data不变，存data的容器变了，触发页面刷新
//    state.value = state.value.copy(random = getShortUUID())
//}

//如果null，怎么办？
//怎么判断什么时候需要清key？
//如果是引用类型，指
fun <T> getSaver():Saver<Holder<T>, String> {
    return Saver(
        save = { holder ->
//            if(debugModeOn) {
//                println("save:$holder")
//            }
            Cache.set(holder.key, holder)
            holder.key
        },
        restore = { key ->
            val holder = Cache.getByType<Holder<T>>(key)
//            if(debugModeOn) {
//                println("restore value:$holder")
//            }
            holder
        }
    )
}

//keyTag+keyName 注意事项：1 组合起来必须唯一，不然数据会错乱；2 必须是常量，不然数据清除后key依然保存在内存会造成内存泄漏
// keyTag+keyName must unique and both are constants, else may cause memory leak
fun <T : Any> getHolder(keyTag:String, keyName:String, data: T):Holder<T> {
    val holder = Holder<T>(key = genKey(keyTag, keyName), data = data)
    return holder
}

fun genKey(keyTag:String, keyName:String):String {
    return "$keyTag:$keyName"
}
