package com.catpuppyapp.puppygit.notification

import com.catpuppyapp.puppygit.notification.base.NotifyBase
import com.catpuppyapp.puppygit.notification.util.NotifyId

/**
 * 保活前台通知
 */
class AutomationServiceHoldNotify private constructor(
    override val notifyId:Int
): NotifyBase(
    TAG = "AutomationServiceHoldNotify",
    channelId="automation_service_hold_notify",
    channelName = "Automation Service",
    channelDesc = "Automation Foreground Service Notification",
){

    companion object {
        fun create(notifyId:Int):NotifyBase {
            return AutomationServiceHoldNotify(notifyId)
        }

        fun createForegroundServiceNotification(): NotifyBase {
            return create(NotifyId.foregroundServiceAutomation)
        }
    }

}
