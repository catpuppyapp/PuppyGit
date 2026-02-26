package com.catpuppyapp.puppygit.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.notification.AutomationNotify
import com.catpuppyapp.puppygit.notification.base.ServiceNotify
import com.catpuppyapp.puppygit.notification.util.NotifyUtil
import com.catpuppyapp.puppygit.base.BaseAccessibilityService
import com.catpuppyapp.puppygit.receiver.ScreenOnOffReceiver
import com.catpuppyapp.puppygit.server.bean.NotificationSender
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.settings.util.AutomationUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.RepoActUtil
import com.catpuppyapp.puppygit.utils.cache.AutoSrvCache
import com.catpuppyapp.puppygit.utils.cache.NotifySenderMap
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.generateRandomString
import com.catpuppyapp.puppygit.utils.pref.PrefUtil
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class AutomationService: BaseAccessibilityService() {
    companion object {
        private const val TAG = "AutomationService"

        private val lock = Mutex()
        // key is package name, value is true=app opened, false=app closed, null=app never open
        val targetPackageTrueOpenedFalseCloseNullNeverOpenedList = ConcurrentMap<String, Boolean>()
        // key is package name, value is time app last open time in millseconds, if null, means never open
//        private val lastPullTime = ConcurrentMap<String, Long>()
        //app last leaving at, key is package name, value is time in millseconds
        val appLeaveTime = ConcurrentMap<String, Long>()

        private var lastTargetPackageName = ""  // use to check enter/leave app

        private val ignorePackageNames = listOf<String>(
            "com.android.systemui",  //通知栏或全面屏手势之类的
            "android",  //切换输入法的弹窗，有的系统（例如原生）会是这个包名，有的不是（例如国产color os）
            "com.oplus.notificationmanager",  //oneplus等oppo系系统，安装app，初次启动，弹窗，询问“是否允许当前app显示通知”
            "com.google.android.permissioncontroller",  //原生系统安装app后首次启动时询问是否允许通知的弹窗，不确定其他权限（例如“获取位置信息”）的弹窗是否也和这个有关


        )


        private fun createNotify(notifyId:Int):ServiceNotify {
            return ServiceNotify(AutomationNotify.create(notifyId))
        }


        private fun sendSuccessNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { title:String?, msg:String?, startPage:Int?, startRepoId:String? ->
            if(settings.automation.showNotifyWhenProgress) {
                serviceNotify.sendSuccessNotification(title, msg, startPage, startRepoId)
            }
        }

        private fun sendErrNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings)={ title:String, msg:String, startPage:Int, startRepoId:String ->
            if(settings.automation.showNotifyWhenProgress) {
                serviceNotify.sendErrNotification(title, msg, startPage, startRepoId)
            }
        }

        private fun sendProgressNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { repoNameOrId:String, progress:String ->
            if(settings.automation.showNotifyWhenProgress) {
                serviceNotify.sendProgressNotification(repoNameOrId, progress)
            }
        }


        private suspend fun pullRepoList(
            sessionId:String,
            settings: AppSettings,
            repoList:List<RepoEntity>,
        ) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#pullRepoList: generate notifyers for ${repoList.size} repos")
            }

            repoList.forEachBetter {
                //notify
                val serviceNotify = createNotify(NotifyUtil.genId())
                NotifySenderMap.set(
                    NotifySenderMap.genKey(it.id, sessionId),
                    NotificationSender(
                        sendErrNotificationIfEnable(serviceNotify, settings),
                        sendSuccessNotificationIfEnable(serviceNotify, settings),
                        sendProgressNotificationIfEnable(serviceNotify, settings),
                    )
                )
            }

            RepoActUtil.pullRepoList(
                sessionId,
                repoList,
                routeName = "'auto pull service'",
                gitUsernameFromUrl="",
                gitEmailFromUrl="",
                pullWithRebase = PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext),
            )
        }


        suspend fun pushRepoList(
            sessionId:String,
            settings: AppSettings,
            repoList:List<RepoEntity>,
        ) {

            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#pushRepoList: generate notifyers for ${repoList.size} repos")
            }

            repoList.forEachBetter {
                //notify
                val serviceNotify = createNotify(NotifyUtil.genId())
                NotifySenderMap.set(
                    NotifySenderMap.genKey(it.id, sessionId),
                    NotificationSender(
                        sendErrNotificationIfEnable(serviceNotify, settings),
                        sendSuccessNotificationIfEnable(serviceNotify, settings),
                        sendProgressNotificationIfEnable(serviceNotify, settings),
                    )
                )
            }

            RepoActUtil.pushRepoList(
                sessionId,
                repoList,
                routeName = "'auto push service'",
                gitUsernameFromUrl="",
                gitEmailFromUrl="",
                autoCommit=true,
                force=false,
            )
        }


    }



    private var screenOnOffReceiver:ScreenOnOffReceiver? = null

    override fun onCreate() {
        super.onCreate()

        // 初始化代码
        AppModel.init_1(realAppContext = applicationContext, exitApp = {}, initActivity = false)

        runBlocking {
            AppModel.init_2()
        }

        registerScreenOnOffReceiver()

        MyLog.d(TAG, "#onCreate() finished")

    }

    private fun registerScreenOnOffReceiver() {
        try {
            //接收屏幕熄灭和点量的广播，以在屏幕熄灭超时后执行push和pull
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }

            screenOnOffReceiver = ScreenOnOffReceiver().let {
                registerReceiver(it, filter)
                it
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#registerScreenOnOffReceiver() err: ${e.stackTraceToString()}")
        }
    }

    private fun unregisterScreenOnOffReceiver() {
        try {
            screenOnOffReceiver?.let { unregisterReceiver(it) }
            screenOnOffReceiver = null
        }catch (e:Exception) {
            MyLog.e(TAG, "#unregisterScreenOnOffReceiver() err: ${e.stackTraceToString()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterScreenOnOffReceiver()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event == null) {

            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#onAccessibilityEvent: event is null")
            }

            return
        }

        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            //必须在外部获取，放到协程里会null
            val packageName = event.packageName?.toString() ?: return

            if(AppModel.devModeOn) {
                MyLog.v(TAG, "TYPE_WINDOW_STATE_CHANGED: $packageName")
            }

            // 若是期望忽略的包名则返回
            if(ignorePackageNames.contains(packageName)) {

                if(AppModel.devModeOn) {
                    MyLog.d(TAG, "ignore package name: '$packageName'")
                }

                return
            }

            try {
                //ignore input method package names
                if((getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.enabledInputMethodList?.find { it.packageName == packageName } != null) {

                    if(AppModel.devModeOn) {
                        MyLog.d(TAG, "ignore input method (soft keyboard): '$packageName'")
                    }

                    return
                }
            }catch (e:Exception) {
                MyLog.d(TAG, "get enabledInputMethodList err: ${e.stackTraceToString()}")
            }

            val settings = SettingsUtil.getSettingsSnapshot()
            val targetPackageList = AutomationUtil.getPackageNames(settings.automation)

            //如果目标app列表为空，就不需要后续判断了，直接返回
            if(targetPackageList.isEmpty()) {
                return
            }


            doJobThenOffLoading {
                val event = Unit  //覆盖外部event变量名，避免误用

                lock.withLock {
                    val sessionId = generateRandomString()

                    if(targetPackageList.contains(packageName)) {  // 是我们关注的app
                        // 更新当前包名，用来检测待推送仓库是否与当前app关联仓库重叠，若重叠，则取消本次推送
                        AutoSrvCache.setCurPackageName(packageName)

                        lastTargetPackageName = packageName

                        val lastTargetPackageName = Unit  // to avoid mistake using

                        MyLog.d(TAG, "target packageName '$packageName' opened, checking need pull or no....")

                        val targetOpened = targetPackageTrueOpenedFalseCloseNullNeverOpenedList[packageName] == true
                        if(!targetOpened) { // was leave, now opened or first time opened （初次打开或离开又重新打开）（还有一种可能是停留在当前app灭屏又亮屏，灭屏的需要在灭屏广播接收器里处理，直接设定成app离开就行）
                            targetPackageTrueOpenedFalseCloseNullNeverOpenedList[packageName] = true
                            MyLog.d(TAG, "target packageName '$packageName' opened, need do pull")

                            val repoList = AutomationUtil.getRepos(settings.automation, packageName)
                            if(repoList.isNullOrEmpty()) {
                                return@withLock
                            }

                            val repoListWillDoAct = mutableListOf<RepoEntity>()
                            val nowInMillSec = System.currentTimeMillis()
                            //离开app后，在一定时间间隔内返回，将不会重复执行pull
                            val lastLeaveAt = appLeaveTime[packageName] ?: 0L ;

                            repoList.forEachBetter {
                                val pullIntervalInSec = AutomationUtil.getAppAndRepoSpecifiedSettings(packageName, it.id, settings).getPullIntervalActuallyValue(settings)

                                //负数将禁用pull
                                if(pullIntervalInSec >= 0L) {
                                    val pullIntervalInMillSec = pullIntervalInSec * 1000L

                                    // pullIntervalInMillSec == 0 代表用户设置的pull间隔为0，无间隔，直接执行
                                    // lastLeaveAt == 0 代表没离开过，初次打开app，这时应该直接执行操作，不用检测间隔
                                    // 减法那个条件是检测时间间隔
                                    if(pullIntervalInMillSec == 0L || lastLeaveAt == 0L || (nowInMillSec - lastLeaveAt) > pullIntervalInMillSec) {
                                        repoListWillDoAct.add(it)
                                    }
                                }else {
                                    MyLog.d(TAG, "Repo '${it.repoName}': pull interval less than 0, pull canceled")
                                }
                            }

                            repoListWillDoAct.let {
                                if(it.isNotEmpty()) {
                                    pullRepoList(sessionId, settings, it)
                                }
                            }
                        }else {
                            MyLog.d(TAG, "target packageName '$packageName' opened but no need do pull")
                        }
                    }else { //不是我们关注的app
                        //设包名为空，这样就省得查配置文件和数据库了
                        AutoSrvCache.setCurPackageName("")

                        if(lastTargetPackageName.isNotBlank()) { //当前app不是我们关注的app，但上个是
                            val packageName = Unit  //避免在这个代码块误调用这个变量名

                            val lastOpenedTarget = lastTargetPackageName
                            lastTargetPackageName = ""

                            val lastTargetPackageName = Unit  // for avoid mistake using this variable

                            //这里需要判断，不然用户更新列表后，这里若不判断会对之前在列表但后来移除的条目多执行一次pull
                            if(targetPackageList.contains(lastOpenedTarget)) {
                                MyLog.d(TAG, "target packageName '$lastOpenedTarget' leaved, checking need push or no...")
                                //这个应该百分百为真啊？
                                val targetOpened = targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] == true
                                if(targetOpened) { // was opened, now leave
                                    MyLog.d(TAG, "target packageName '$lastOpenedTarget' leaved, need do push")
                                    targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] = false
                                    appLeaveTime[lastOpenedTarget] = System.currentTimeMillis()

                                    val repoList = AutomationUtil.getRepos(settings.automation, lastOpenedTarget)
                                    if(repoList.isNullOrEmpty()) {
                                        return@withLock
                                    }

                                    // 按推送延迟时间分组，分成不同的仓库list，各等各的时间，各执行各的操作
                                    // group repos by push delay time, then do act for each
                                    AutomationUtil.groupReposByPushDelayTime(
                                        lastOpenedTarget,
                                        repoList,
                                        settings
                                    ).forEachBetter mark@{ pushDelayInSec, repoList ->
                                        if(repoList.isEmpty()) {
                                            return@mark
                                        }


                                        // do push, one package may bind multi repos, start a coroutine do push for them
                                        //负数将禁用push
                                        if(pushDelayInSec >= 0L) {
                                            doJobThenOffLoading pushTask@{
                                                val pushDelayInMillSec =  pushDelayInSec * 1000L
                                                var taskCanceled = false

                                                //大于0，等待超过延迟时间后再执行操作；若等于0，则不检查，直接跳过这段，执行后面的push
                                                if(pushDelayInMillSec > 0L) {
                                                    val startAt = System.currentTimeMillis()

                                                    while (true) {
                                                        // 每 2 秒检查一次是否需要push，虽然设置的单位是秒，但精度是2秒，避免太多无意义cpu空转，最多误差2秒，可接受
                                                        delay(Cons.pushDelayCheckFrquencyInMillSec)

                                                        // 若app重新打开 或者 app在当前任务启动后重新触发了离开（通过leave时间判断），则取消当前push任务
                                                        //这里的两个值必须每次都从map里获取最新的数据，因为map里的值会在每次离开app后更新
                                                        if(
                                                        // app重新打开，当前任务取消
                                                            targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] == true
                                                            // app再次离开，当前任务已“过期”，应取消
                                                            //正常来说，这里获取的app离开时间永远不可能为null，所以 `?: 0L` 应该不会派上用场，
                                                            // 如果出了问题，真的为null，则代表app没离开，这时不应执行push，或者忽略这个条件都行，这里采用后者，
                                                            // startAt肯定大于0，所以如果appLeaveTime取出的值为0的话，这个条件永远为假，相当于忽略了，其实就算错误执行一次push也问题不大
                                                            || startAt < (appLeaveTime[lastOpenedTarget] ?: 0L)
                                                        ) {
                                                            taskCanceled = true
                                                            break
                                                        }

                                                        //如果当前时间减起始时间超过了设定的延迟时间则执行push
                                                        if((System.currentTimeMillis() - startAt) > pushDelayInMillSec) {
                                                            break
                                                        }
                                                    }
                                                }

                                                if(!taskCanceled) {
                                                    //用要推送的仓库减当前显示的app关联的仓库，剩下的是需要推送的仓库，其他的待离开当前app后再计划推送
                                                    var repoList = repoList
                                                    val curShowingPackageName = AutoSrvCache.getCurPackageName()
                                                    if(curShowingPackageName.isNotBlank()) {
                                                        val repoListOfCurShowingPackage = AutomationUtil.getRepos(settings.automation, curShowingPackageName)
                                                        if(!repoListOfCurShowingPackage.isNullOrEmpty()) {
                                                            val newList = mutableListOf<RepoEntity>()

                                                            //移除当前显示的app关联的仓库，同步剩余仓库
                                                            repoList.forEachBetter { r1 ->
                                                                var contains = false

                                                                for(r2 in repoListOfCurShowingPackage) {
                                                                    if(r1.id == r2.id) {
                                                                        contains = true
                                                                        break
                                                                    }
                                                                }

                                                                //添加要推送的仓库
                                                                if(contains.not()) {
                                                                    newList.add(r1)
                                                                }
                                                            }

                                                            repoList = newList
                                                        }
                                                    }

                                                    //其实这里检查不检查是否为空都行，因为最终执行操作的函数会判断，若空直接返回，不过检查下更好
                                                    if(repoList.isEmpty()) {
                                                        //因为在外部检查了若repoList为空则不启动协程，所以执行到这里如果列表为空，必然是当前app全面覆盖了要推送的仓库
                                                        MyLog.d(TAG, "push cancelled, current app full-covered target repoList, will do push after current app leave")
                                                    }else {
                                                        pushRepoList(sessionId, settings, repoList)
                                                    }

                                                }
                                            }
                                        }else {
                                            MyLog.d(TAG, "push delay less than 0, push canceled")
                                        }

                                    }


                                }else {
                                    MyLog.d(TAG, "target packageName '$packageName' opened but no need do push")
                                }
                            }else {  //这里得设置下，如果一个条目之前在列表后来移除了，这里不更新下的话，下次打开目标app会忽略一次应该执行的pull
                                MyLog.d(TAG, "target packageName '$lastOpenedTarget' was in targetList but removed, will not do push for it")
                                targetPackageTrueOpenedFalseCloseNullNeverOpenedList[lastOpenedTarget] = false
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onInterrupt() {
        if(AppModel.devModeOn) {
            MyLog.w(TAG, "#onInterrupt: interrupted?")
        }
    }

}
