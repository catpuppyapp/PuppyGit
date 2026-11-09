package com.catpuppyapp.puppygit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.service.AutomationService
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.settings.util.AutomationUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.cache.AutoSrvCache
import com.catpuppyapp.puppygit.utils.doJob
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.generateRandomString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

private const val TAG = "ScreenOnOffReceiver"

class ScreenOnOffReceiver : BroadcastReceiver() {
    private var job = mutableStateOf<Job?>(null)
//    private var screenOffAtInMillSec = mutableLongStateOf(0L)


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            //灭屏的推送必须得在这触发，因为无障碍监听屏幕切换无法监听到灭屏

//            val nowInMillSec = System.currentTimeMillis()
//            screenOffAtInMillSec.longValue = nowInMillSec

            // 屏幕熄灭
            MyLog.d(TAG, "Screen is OFF")

            val lastPackage = AutoSrvCache.getCurPackageName()
            if(lastPackage.isBlank()) { // 若blank，代表并非我们关注的app，直接返回即可
                return
            }


            //执行到这代表灭屏时停留的app是我们关注的app，需要处理下


            //灭屏，当作离开app，AutomationService会根据这些参数检测并决定是否执行pull
            //这个决定进AutomationService的pull还是push，因为灭屏当作离开，所以设为离开，这样下次就会进pull代码块
            AutomationService.targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastPackage] = false
            //这个决定用来判断是否超过设定的pull interval，若超过则会执行pull
            AutomationService.appLeaveTime[lastPackage] = System.currentTimeMillis()

            //创建push任务
            job.value = doJob job@{
                try {
                    val settings = SettingsUtil.getSettingsSnapshot()
                    val repoList = AutomationUtil.getRepos(settings.automation, lastPackage)

                    if(repoList.isNullOrEmpty()) {
                        return@job
                    }

                    AutomationUtil.groupReposByPushDelayTime(
                        lastPackage,
                        repoList,
                        settings
                    ).forEachBetter forEach@{ pushDelayInSec, repoList ->
                        if(repoList.isEmpty()) {
                            return@forEach
                        }

                        // do push, one package may bind multi repos, start a coroutine do push for them
                        //负数将禁用push
                        if (pushDelayInSec >= 0L) {
                            val pushDelayInMillSec = pushDelayInSec * 1000L
                            //大于0，等待超过延迟时间后再执行操作；若等于0，则不检查，直接跳过这段，执行后面的push
                            if (pushDelayInMillSec > 0L) {
                                val startAt = System.currentTimeMillis()

                                while (true) {
                                    // 每 2 秒检查一次是否需要push，虽然设置的单位是秒，但精度是2秒，避免太多无意义cpu空转，最多误差2秒，可接受
                                    //如果亮屏，任务会取消，会在此处抛出canceled异常
                                    delay(Cons.pushDelayCheckFrquencyInMillSec)

                                    //如果当前时间减起始时间超过了设定的延迟时间则执行push
                                    if ((System.currentTimeMillis() - startAt) > pushDelayInMillSec) {
                                        break
                                    }
                                }
                            }

                            AutomationService.pushRepoList(sessionId = "ScrOffAutoPush_"+generateRandomString(), settings, repoList, targetPackageName = lastPackage, currentPackageName = "screen off")
                        }else {
                            MyLog.d(TAG, "push delay less than 0, push canceled")
                        }
                    }

                }catch (e:Exception) {
                    MyLog.e(TAG, "push canceled by err: ${e.stackTraceToString()}")
                }finally {
                    //执行完把任务设为null
                    job.value = null
                }
            }
        } else if (intent?.action == Intent.ACTION_SCREEN_ON) {
            // 屏幕点亮
            MyLog.d(TAG, "Screen is ON")

            //亮屏后尝试取消推送任务
            runCatching {
                job.value?.cancel()
                job.value = null
            }
        }
    }
}
