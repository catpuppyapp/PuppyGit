package com.catpuppyapp.puppygit.screen.shared

import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage

/**
 * 使用方法：
 * 在Activity需要处理新Intent时调用 `setNewIntent()` 即可，其他函数和状态都已经久位，不用改，只要调了 `setNewIntent()` 就会触发后续的处理机制
 */
object IntentHandler {
    private const val TAG = "IntentHandler"


    //这个类似need refresh状态变量，若获取到新intent，会给此变量赋新值，触发刷新，然后处理新intent
    val gotNewIntent = mutableStateOf("")

    val intentConsumed = mutableStateOf(false)
    val intent = mutableStateOf<Intent?>(null)

    //有新Intent时，调这个函数设置下，然后就会触发首页去处理
    fun setNewIntent(newIntent: Intent?) {
        intent.value = newIntent
        //刷新，触发请求处理intent
        changeStateTriggerRefreshPage(gotNewIntent)
    }

    //检查intent，若有效，请求HomeScreen处理
    fun requireHandleNewIntent() {
        val intent = intent.value ?: return;

        //data 或 extras是必须条件，若两者任有其一，则此intent需要消费
        if(needConsume(intent)) {
            MyLog.d(TAG, "will navigate to HomeScreen to handle new Intent")

            intentConsumed.value = false
            changeStateTriggerRefreshPage(SharedState.homeScreenNeedRefresh)

            //弹出栈直到顶级页面(HomeScreen)
            AppModel.navController.let {
                // false表示不弹出目标路径，目标路径是起始路径，若弹出会出问题，导航栈会错乱，所以传false
                it.popBackStack(it.graph.startDestinationId, inclusive = false)
            }

            //导航到home再重启activity，不好，废弃
//            AppModel.navController.navigate(Cons.nav_HomeScreen)
            //用新intent重启Activity
//            ActivityUtil.restartActivityByIntent(this, intent)
        }
    }

    fun needConsume(intent:Intent?) = intent?.extras != null || intent?.data != null;
}
