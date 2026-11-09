package com.catpuppyapp.puppygit.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.catpuppyapp.puppygit.notification.base.NotifyBase
import com.catpuppyapp.puppygit.notification.bean.Action
import com.catpuppyapp.puppygit.notification.util.NotifyId
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.service.HttpService

/**
 * 保活前台通知
 */
class HttpServiceHoldNotify private constructor(
    override val notifyId:Int
): NotifyBase(
    TAG = "HttpServiceHoldNotify",
    channelId="http_service_hold_notify",
    channelName = "Http Service",
    channelDesc = "Http Foreground Service Notification",
    actionList = { context ->
        listOf(
            // "stop" action, click stop service
            Action(
                iconId = R.drawable.baseline_close_24,
                text = context.getString(R.string.stop),
                pendingIntent = createPendingIntentWithAct(context, HttpService.command_stop)
            ),

            // copy addr
            Action(
                iconId = R.drawable.baseline_content_copy_24,
                text = context.getString(R.string.copy),
                pendingIntent = createPendingIntentWithAct(context, HttpService.command_copy_addr)
            )
        )
    }
){

    companion object {
        // 入参int值是 service前台通知的通知id，app内全局唯一，必须不能是0，调用NotifyUtil.genId()生成即可
        fun create(notifyId:Int):NotifyBase {
            return HttpServiceHoldNotify(notifyId)
        }

        // 创建前台服务通知，这个id是常量，必须独占，若和其他通知id冲突，后发送的通知会覆盖之前的
        fun createForegroundServiceNotification(): NotifyBase {
            return create(NotifyId.foregroundServiceHttp)
        }

        fun createPendingIntentWithAct(context: Context, action:String): PendingIntent {
            val intent = Intent(context, HttpService::class.java) // 替换为您的主活动

            intent.action = action

            //flag作用是如果pendingIntent已经存在，则取消之前的然后创建个新的，没验证，可能是
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

    }

}
