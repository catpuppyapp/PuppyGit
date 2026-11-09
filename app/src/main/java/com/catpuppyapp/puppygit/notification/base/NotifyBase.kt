package com.catpuppyapp.puppygit.notification.base

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.catpuppyapp.puppygit.notification.bean.Action
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.forEachBetter


abstract class NotifyBase(
    private val TAG:String, // used for log
    //注册通知渠道用
    private val channelId:String,
    private val channelName:String,
    private val channelDesc:String,

    private val actionList:((context:Context) -> List<Action>)? = null
) {
    companion object {
        lateinit var appContext:Context
    }

    //每条通知此id必须唯一，否则覆盖上条同id通知
    //子类应该覆盖此字段，每次发通知会为当前操作生成一个唯一通知id，若通知id一样会覆盖
    abstract val notifyId:Int

    /**
     * if > 0, notification will auto dismiss after this milliseconds
     */
    var timeoutAfterMs:Long = 0

    /**
     * 用来避免重复执行init出错
     */
    private val inited:MutableState<Boolean> = mutableStateOf(false)

    /**
     * applicationContext，不要用activityContext，
     * init时会更新此变量为非null值
     *
     */


    /**
     * 必须先init，否则执行其他方法可能报错并且无效
     * @param context 建议传 applicationContext
     */
    fun init(context: Context) {
        if(inited.value) {
            return
        }

        inited.value = true

        appContext = context

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        // see: https://developer.android.com/develop/ui/views/notifications/build-notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDesc

            // Register the channel with the system.
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            MyLog.d(TAG, "notify channel '$channelId' registered")
        }

        MyLog.d(TAG, "notification '$channelId' inited")
    }

    // 发送通知
    fun sendNotification(context: Context?, title: String?, message: String?, pendingIntent: PendingIntent?) {
        val context = context ?: appContext

        val builder = getNotificationBuilder(context, title, message, pendingIntent) // 点击后自动消失

        if (timeoutAfterMs > 0) {
            builder.setTimeoutAfter(timeoutAfterMs)
        }

        NotificationManagerCompat.from(context).apply {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                MyLog.e(TAG, "#sendNotification: send notification failed, permission 'POST_NOTIFICATIONS' not granted")
                return
            }

            //notify
            notify(notifyId, builder.build())
        }

        // untested code, another way to send notification
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(notifyId, builder.build())
    }

    fun getNotificationBuilder(context: Context?, title: String?, message: String?, pendingIntent: PendingIntent?): NotificationCompat.Builder {
        val context = context ?: appContext

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.dog_head) // 替换为您的图标
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent) // 设置点击通知时的意图
            .setAutoCancel(true) // 点击后通知将自动消失，除非你是foreground service启动的通知

        actionList?.invoke(context)?.forEachBetter { action ->
            builder.addAction(action.iconId, action.text, action.pendingIntent)
        }

        return builder
    }

}
