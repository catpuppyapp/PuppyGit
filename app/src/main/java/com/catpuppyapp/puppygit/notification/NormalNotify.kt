package com.catpuppyapp.puppygit.notification

import com.catpuppyapp.puppygit.notification.base.NotifyBase

class NormalNotify private constructor(
    override val notifyId:Int
): NotifyBase(
    TAG = "NormalNotify",
    channelId="normal_notify",
    channelName = "Normal",
    channelDesc = "Show Notifications",
) {
    companion object {
        fun create(notifyId:Int):NotifyBase {
            return NormalNotify(notifyId)
        }
    }
}
