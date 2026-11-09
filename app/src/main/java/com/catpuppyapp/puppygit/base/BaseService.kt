package com.catpuppyapp.puppygit.base


import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.catpuppyapp.puppygit.utils.ContextUtil

open class BaseService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }
}
