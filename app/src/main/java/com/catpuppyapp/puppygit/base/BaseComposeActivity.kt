package com.catpuppyapp.puppygit.base

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import androidx.core.view.WindowCompat
import com.catpuppyapp.puppygit.activity.CrashActivity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ContextUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.showToast
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseComposeActivity : ComponentActivity() {

    fun init(
        TAG: String,
        funName: String = "onCreate()",
        requireSetExceptionHandler:Boolean = true,
        requireEnableEdgeToEdge:Boolean = false,
        allowImePadding:Boolean = true,
    ) {
        if(requireEnableEdgeToEdge) {
            enableEdgeToEdge()
        }

        AppModel.init_1(realAppContext = applicationContext, exitApp = { finish() }, initActivity = true)


        if(requireSetExceptionHandler) {
            setExceptionHandler(TAG, funName)
        }

        //for make `Modifier.imePadding()` work
        if(allowImePadding) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }


    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextUtil.getLocalizedContext(newBase))
    }

    override fun onDestroy() {
        super.onDestroy()

        AppModel.destroyer()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        AppModel.handleActivityConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()

        // 销毁Activity（并不会杀进程，所以是假退出，下次启动还会重建Activity还不如切换到后台好）
//        AppModel.updateExitApp { finish() }

        AppModel.updateExitApp { moveTaskToBack(true) }
    }

    /**
     * can't promise 100% catch app crash, but have maybe 30% chance to caught
     */
    protected fun setExceptionHandler(TAG: String, funName: String) {
        // catch exception, block start。( refer: https://stackoverflow.com/questions/76061623/how-to-restart-looper-when-exception-throwed-in-jetpack-compose
        window.decorView.compositionContext = window.decorView.createLifecycleAwareWindowRecomposer(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                try {
                    //                throwable.printStackTrace();
                    val errMsg = throwable.stackTraceToString()
                    MyLog.e(TAG, "#$funName err: $errMsg")

                    //出错提示下用户就行，经我测试，画面会冻结，但数据不会丢，问题不大
                    showToast(applicationContext, getString(R.string.err_restart_app_may_resolve), Toast.LENGTH_LONG)  //测试了下，能显示Toast

                    //不重新创建Activity的话，页面会freeze，按什么都没响应，不过系统导航键还是可用的
                    //重新创建不一定成功，有可能会返回桌面
                    //                    ActivityUtil.restartActivityByIntent(this)

                    //启动crash activity显示错误信息
                    CrashActivity.start(this, errMsg)

                    // 不重建当前Activity，直接退出
                    finish()

                    // 如果想显示错误弹窗，参见文档 “下一步-20240120.txt” 中的："compose错误处理 compose出错弹窗实现 20240505"

                } catch (e: Exception) {
                    e.printStackTrace()  //再出异常，管不了了，随便吧，打印下就行
                }
            }, lifecycle
        )

        // catch exception, block end
    }

}
