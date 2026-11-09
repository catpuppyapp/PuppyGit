package com.catpuppyapp.puppygit.base


import android.accessibilityservice.AccessibilityService
import android.content.Context
import com.catpuppyapp.puppygit.utils.ContextUtil

abstract class BaseAccessibilityService : AccessibilityService() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }
}
