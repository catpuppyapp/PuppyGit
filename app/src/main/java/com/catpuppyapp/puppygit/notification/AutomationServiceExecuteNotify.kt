package com.catpuppyapp.puppygit.notification

import com.catpuppyapp.puppygit.notification.base.NotifyBase

/**
 * 执行自动化行为的通知，就是app打开、关闭时发送通知的渠道
 */
class AutomationServiceExecuteNotify private constructor(
    override val notifyId:Int
): NotifyBase(
    TAG = "AutomationServiceExecuteNotify",
    channelId="automation_service_execute_notify",
    channelName = "Automation Service Execute",
    channelDesc = "Automation Service Execute Notifications",
) {
    companion object {
        fun create(notifyId:Int):NotifyBase {
            return AutomationServiceExecuteNotify(notifyId)
        }
    }
}
