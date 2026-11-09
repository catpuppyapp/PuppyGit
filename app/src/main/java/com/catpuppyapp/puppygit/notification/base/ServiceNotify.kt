package com.catpuppyapp.puppygit.notification.base

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.notification.util.NotifyUtil

open class ServiceNotify(val notify: NotifyBase) {
    /**
     * 启动app并定位到ChangeList和指定仓库
     * @param startPage 是页面id, `Cons.selectedItem_` 开头的那几个变量
     * @param startRepoId 虽然是repo id，但实际上查询的时候可能会匹配id和repoName，但是，这里还是应该尽量传id而不是repoName
     */
    private fun sendNotification(title:String, msg:String, startPage:Int, startRepoId:String) {
        NotifyUtil.sendNotificationClickGoToSpecifiedPage(notify, title, msg, startPage, startRepoId)
    }

    fun sendErrNotification(title:String, msg:String, startPage:Int, startRepoId:String) {
        sendNotification(title, msg, startPage, startRepoId)
    }

    fun sendSuccessNotification(title:String?, msg:String?, startPage:Int?, startRepoId:String?) {
        //Never页面永远不会被处理，变相等于启动app时回到上次退出时的页面
        sendNotification(title ?: "PuppyGit", msg ?: "Success", startPage ?: Cons.selectedItem_Never, startRepoId ?: "")
    }

    fun sendProgressNotification(repoNameOrId:String, progress:String) {
        sendNotification(repoNameOrId, progress, Cons.selectedItem_Never, "")
    }

    fun sendNormalNotification(title:String, msg:String) {
        sendNotification(title, msg, Cons.selectedItem_Never, "")
    }

}
