package com.catpuppyapp.puppygit.utils

//class MsgQueue {
//    companion object {
//        val storage = ConcurrentLinkedQueue<String>()
//
//        //这个代码需要放到SideEffect里，这样每次重组时才会执行
//        fun showAndClearAllMsg() {
////            println("msgqueue size:"+storage.size)
//            val appContext = AppModel.activityContext
//
//            while (true) {
////            println("msgqueue value:"+storage.peek())
//                val m = storage.poll() ?: break
//                showToast(appContext, m)
//            }
//        }
//
//        fun addToTail(msg:String) {
//            storage.offer(msg)
//        }
//
//    }
//}
