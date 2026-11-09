package com.catpuppyapp.puppygit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.catpuppyapp.puppygit.service.HttpService


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            //如果设置了开机自启，则启动，否则不启动
            //开机可能会很久才启动，甚至超过5分钟
            if(HttpService.launchOnSystemStartUpEnabled(context)) {
                HttpService.start(context)
            }
        }
    }
}
