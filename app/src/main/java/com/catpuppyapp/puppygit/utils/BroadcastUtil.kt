package com.catpuppyapp.puppygit.utils

import android.content.Context
import android.content.Intent
import java.io.Serializable


object BroadcastUtil {
    private const val TAG = "BroadcastUtil"

    private fun sendMsg(ctx: Context, action: String, what: Int, content: Serializable) {
        try {
            val intent = Intent()
            intent.action = action
            intent.`package` = AppModel.appPackageName
            intent.putExtra("key", what)
            intent.putExtra("content", content)
            ctx.sendBroadcast(intent)
        } catch (e: Exception) {
            MyLog.e(TAG, "#sendMsg: ${e.stackTraceToString()}")
        }
    }
}
