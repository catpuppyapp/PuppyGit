package com.catpuppyapp.puppygit.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import com.catpuppyapp.puppygit.base.BaseService
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.notification.HttpServiceHoldNotify
import com.catpuppyapp.puppygit.notification.base.NotifyBase
import com.catpuppyapp.puppygit.notification.util.NotifyUtil
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.server.HttpServer
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.copyTextToClipboard
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.genHttpHostPortStr
import com.catpuppyapp.puppygit.utils.pref.PrefMan
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class HttpService : BaseService() {
    companion object {

        private const val TAG = "HttpService"

        private var httpServer: HttpServer? = null
        const val command_stop = "STOP"
        const val command_copy_addr = "COPY_ADDR"
        private val lock = Mutex()

        /**
         * expect do start/stop/restart server in `act`
         */
        private suspend fun doActWithLock(act:suspend () -> Unit) {
            lock.withLock {
                act()
            }
        }

        /**
         * 这操作应该在 doActWithLock 里执行
         */
        private suspend fun runNewHttpServer(settings:AppSettings) {
            // 避免停止启动同一个端口的服务器冲突，所以stop和start应该同步执行，不应该并行

            //停止旧的
            stopCurrentServer()

            //创建新的
            val newServer = HttpServer(
                settings.httpService.listenHost,
                settings.httpService.listenPort
            )

            //更新类变量
            httpServer = newServer

            //启动新的
            newServer.startServer()
        }

        /**
         * 这操作应该在 doActWithLock 里执行
         */
        private suspend fun stopCurrentServer() {
            httpServer?.stopServer()
        }

        fun launchOnSystemStartUpEnabled(context: Context):Boolean {
            return PrefMan.get(context, PrefMan.Key.launchServiceOnSystemStartup, "0") == "1"
        }

        fun setLaunchOnSystemStartUp(context: Context, enable:Boolean) {
            PrefMan.set(context, PrefMan.Key.launchServiceOnSystemStartup, if (enable) "1" else "0")
        }

        /**
         * 调这个方法不需要HttpServer.doActWithLock()，其内部会自己调用
         */
        fun start(appContext: Context) {
            if(isRunning()) {
                MyLog.w(TAG, "HttpService already running, start canceled")
            }else {
                val serviceIntent = Intent(appContext, HttpService::class.java)
                appContext.startForegroundService(serviceIntent)
                MyLog.d(TAG, "HttpService started")
            }

            TileHttpService.sendUpdateTileRequest(appContext, true)
        }

        //没运行service调用此方法应该不会报错，chatgpt说的，我没测试
        fun stop(appContext: Context) {
            val serviceIntent = Intent(appContext, HttpService::class.java)
            appContext.stopService(serviceIntent)
            MyLog.d(TAG, "HttpService stopped")

            TileHttpService.sendUpdateTileRequest(appContext, false)
        }

        fun isRunning() :Boolean {
            return httpServer?.isServerRunning() == true
        }


//        fun getApiJson(repoEntity: RepoEntity, settings: AppSettings):String {
//            return JsonUtil.j2PrettyPrint.let {
//                it.encodeToString(
//                    it.serializersModule.serializer(),
//
//                    genConfigDto(repoEntity, settings)
//                )
//            }
//        }


        //service启动Activity似乎需要弹窗权限，算了，点击通知启动也很方便
//        fun launchApp() {
//            val context = AppModel.realAppContext
//
//            val intent = Intent(context, MainActivity::class.java) // 替换为您的主活动
//            //创建个新Activity并清掉之前的Activity，不然可能存在多个Activity，有点混乱
//            //这个 or 是 bitwise，相当于 '1 | 0' 的 '|'
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//
////            intent.putExtra(IntentCons.ExtrasKey.startPage, Cons.selectedItem_Repos)
////            intent.putExtra(IntentCons.ExtrasKey.startRepoId, )
//
//           context.startActivity(intent)
//        }


    }

    override fun onCreate() {
        super.onCreate()

        // 初始化代码
        AppModel.init_1(realAppContext = applicationContext, exitApp = {}, initActivity = false)

        runBlocking {
            AppModel.init_2()
        }

        MyLog.w(TAG, "#onCreate() finished")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if(action == command_stop){ // stop
            stop(AppModel.realAppContext)

            MyLog.w(TAG, "#onStartCommand() stop finished")
        }else if(action == command_copy_addr){ // copy addr
            val settings = SettingsUtil.getSettingsSnapshot()
            copyTextToClipboard(
                context = applicationContext,
                label = "PuppyGit Http Addr",
                text = genHttpHostPortStr(settings.httpService.listenHost, settings.httpService.listenPort.toString())
            )

            //这toast有可能被展开的通知栏挡住
            Msg.requireShow(applicationContext.getString(R.string.copied))
        }else { // start
            val settings = SettingsUtil.getSettingsSnapshot()

            val serviceNotify = HttpServiceHoldNotify.createForegroundServiceNotification()
            // 启动前台服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // UPSIDE_DOWN_CAKE is sdk 34
                startForeground(serviceNotify.notifyId, getNotification(serviceNotify, settings), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            }else {
                startForeground(serviceNotify.notifyId, getNotification(serviceNotify, settings))
            }

            doJobThenOffLoading {
                doActWithLock {
                    runNewHttpServer(settings)
                }
            }

            //严格来说start并没finished，只是调用了，可能得等会HttpServer.isServerRunning()才能检测到true
            MyLog.w(TAG, "#onStartCommand() start finished")

        }

        return START_STICKY
    }



    override fun onDestroy() {
        super.onDestroy()

        doJobThenOffLoading {
            doActWithLock {
                stopCurrentServer()
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        MyLog.w(TAG, "#onDestroy() finished")
    }

    /**
     * 在通知栏显示的常驻通知
     */
    private fun getNotification(notifyBase: NotifyBase, settings: AppSettings): Notification {
        val builder = notifyBase.getNotificationBuilder(
            this,
            "PuppyGit Service",
            "Listen on: ${genHttpHostPortStr(settings.httpService.listenHost, settings.httpService.listenPort.toString())}",

            //启动app并定位到Service页面
            NotifyUtil.createPendingIntentGoToSpecifiedPage(applicationContext, Cons.selectedItem_Service, startRepoId = ""),
        )

        return builder.build()
    }
}
