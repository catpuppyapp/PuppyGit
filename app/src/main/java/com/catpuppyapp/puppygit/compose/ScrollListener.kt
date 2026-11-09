package com.catpuppyapp.puppygit.compose

//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.remember

//
//@Deprecated("deprecated by performance issue")
//@Composable
//fun ScrollListener(nowAt:Int, lastAtInitValue:Int=0, onScrollUp:()->Unit, onScrollDown:()->Unit) {
//    val lastAt = remember { mutableIntStateOf(lastAtInitValue) }
//
//    if(nowAt < lastAt.intValue) {  // go up
////        Msg.requireShow("向上滚动中 nowAt:$nowAt, lastFirstVisible:${lastFirstVisible.value}")
//        onScrollUp()
//        lastAt.intValue = nowAt
//    }else if(nowAt > lastAt.intValue) {  // go down
////        Msg.requireShow("向下滚动中 nowAt:$nowAt, lastFirstVisible:${lastFirstVisible.intValue}")
//        onScrollDown()
//        lastAt.intValue = nowAt
//    }
//}
