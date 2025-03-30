package com.catpuppyapp.puppygit.utils.saf

import android.net.Uri
import com.bytedance.android.bytehook.ByteHook
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog

private const val TAG = "SafJni"

object SafJni {
    
    private const val NULL = 0

    fun getFdFromSafPath(path:String, mode:String):Int {
        try {
            val context = AppModel.realAppContext
            val uri = Uri.parse(path)
            val fd = context.contentResolver.openFileDescriptor(uri, mode)
            // 0是NULL，无效地址
            return fd?.detachFd() ?: NULL
        }catch (e:Exception) {
            MyLog.e(TAG, "#getFdFromSafPath err: path=$path, mode=$mode, err=${e.stackTraceToString()}")
            return NULL
        }
    }
}
