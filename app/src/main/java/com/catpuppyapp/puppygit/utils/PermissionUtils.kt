package com.catpuppyapp.puppygit.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * src: https://github.com/NeoApplications/Neo-Backup/blob/main/src/main/java/com/machiav3lli/backup/utils/PermissionUtils.kt
 */

// request code, will return when operation success? idk actually
const val READ_PERMISSION = 2
const val WRITE_PERMISSION = 3

private fun trueRequestManageStorageFalseRequestRwStorage(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

fun Activity.getStoragePermission() {
    if(trueRequestManageStorageFalseRequestRwStorage()){
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    } else {
        requireWriteStoragePermission()
        requireReadStoragePermission()
    }
}

private fun Activity.requireReadStoragePermission() {
    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED
    )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_PERMISSION
        )
}

private fun Activity.requireWriteStoragePermission() {
    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED
    )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_PERMISSION
        )
}

//这个context传app context或activity或弹窗的context都行
fun hasManageStoragePermission(context: Context): Boolean {
    return if(trueRequestManageStorageFalseRequestRwStorage()) {
        //必须得用这个api，用checkSelfPermission权限对MANAGE_EXTERNAL_STORAGE无效
        Environment.isExternalStorageManager()
    }else {
        (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        &&
        (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }
}


/**
 * @return true if sent permission request, false otherwise. note, return true only means request was sent, doesn't means granted
 */
fun requestStoragePermissionIfNeed(activityContext: Context, TAG: String): Boolean {
    return try {
        //若没授权，请求授权
        if (hasManageStoragePermission(activityContext)) {
            MyLog.d(TAG, "already has manage storage permission")
            false
        } else {
            MyLog.d(TAG, "no manage storage permission, will request...")
            ActivityUtil.getManageStoragePermissionOrShowFailedMsg(activityContext)
            true
        }
    } catch (e: Exception) {
        MyLog.d(TAG, "check and request manage storage permission err: ${e.stackTraceToString()}")
        false
    }
}
