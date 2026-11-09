package com.catpuppyapp.puppygit.base

import android.content.Context
import android.service.quicksettings.TileService
import com.catpuppyapp.puppygit.utils.ContextUtil

open class BaseTileService : TileService() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }
}
