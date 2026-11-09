package com.catpuppyapp.puppygit.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.catpuppyapp.puppygit.activity.findActivity
import com.catpuppyapp.puppygit.play.pro.R

private const val TAG = "ActivityUtil"

object ActivityUtil {

//    @Composable
//    fun getCurrentActivity(): Activity? {
//        val context = LocalContext.current
        // 必须再递归查找一下，不然有可能无法转换为Activity，例如在弹窗获取LocalContext.current，可能就会无法直接转换为Activity
//        return context.findActivity()
//    }

    //貌似必须在主线程执行此方法
    //重要：这个不一定能显示文件是否保存的toast，所以注释了，可用 `restartActivityByIntent` 替代
    //注：recreate适用于 Build.VERSION.SDK_INT >= 11，即安卓3.0
//    fun restartActivityByRecreate(activity: Activity) {
//        activity.recreate()
//    }

    //这个重启几乎能百分百显示是否保存的Toast
    fun restartActivityByIntent(activity:Activity, intent: Intent?) {
        activity.apply {
            val intent = intent ?: getIntent()
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)  //禁用切换动画，不知道禁用有什么意义，所以注释了
            finish()
//            overridePendingTransition(0, 0)  //禁用切换动画
            startActivity(intent)
//            overridePendingTransition(0, 0)  //禁用切换动画
        }

    }

    //打开网址、mailto链接等url，但因为url和uri太像，所以参数名改成linkUrl了
    fun openUrl(context: Context, linkUrl:String) {
        val uri = Uri.parse(linkUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    /**
     * this method may throw exception when packageName invalid, better try catch when using it
     */
    fun openSpecifedAppInfoPage(context: Context, packageName:String) {
        if(packageName.isBlank()) {
            return
        }

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }

    fun openThisAppInfoPage(context: Context) {
        openSpecifedAppInfoPage(context, AppModel.appPackageName)
    }

    fun getManageStoragePermissionOrShowFailedMsg(context: Context) {
        val activity = context.findActivity()

        if(activity == null) {
            Msg.requireShowLongDuration(context.getString(R.string.please_go_to_system_settings_allow_manage_storage))
        }else {
            activity.getStoragePermission()
        }
    }

    fun startActivitySafe(activity: Activity?, intent: Intent, options: Bundle? = null) {
        if(activity == null) {
            Msg.requireShowLongDuration("Can't found Activity for action.")

            return
        }

        try {
            activity.startActivity(intent, options)
        } catch (e: ActivityNotFoundException) {
            Msg.requireShowLongDuration(activity.getString(R.string.activity_not_found))
        } catch (e: Exception) {
            Msg.requireShowLongDuration("err: ${e.localizedMessage}")
            MyLog.e(TAG, "#startActivitySafe() err: ${e.localizedMessage}")
        }
    }


    fun getActivitiesOfPackage(context: Context, packageName: String) : List<String> {
        val activities = mutableListOf<String>()
        try {
            val pkgInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            pkgInfo.activities?.forEachBetter {
                activities.add(it.name)
            }
        }catch (e: Exception) {
            // package not installed or other errors
            MyLog.e(TAG, "getActivitiesOfPackage() err: ${e.stackTraceToString()}")
        }

        return activities
    }
}
