package com.catpuppyapp.puppygit.server

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.notification.HttpServiceExecuteNotify
import com.catpuppyapp.puppygit.notification.base.ServiceNotify
import com.catpuppyapp.puppygit.notification.util.NotifyUtil
import com.catpuppyapp.puppygit.server.bean.NotificationSender
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.NetUtil
import com.catpuppyapp.puppygit.utils.RepoActUtil
import com.catpuppyapp.puppygit.utils.cache.NotifySenderMap
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.genHttpHostPortStr
import com.catpuppyapp.puppygit.utils.generateRandomString
import com.catpuppyapp.puppygit.utils.pref.PrefUtil
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.host
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

private const val TAG = "HttpServer"

/**
 * 这个符号用来在ip列表匹配所有ip
 */
private const val matchAllIpSign = "*"

/**
 * 如果ip同时在白名单和黑名单，将允许连接
 * 如果设置项中的token为空字符串，将允许所有连接
 */
private fun isGoodTokenAndIp(token:String?, requestIp:String, settings: AppSettings): Ret<Unit?> {
    //这个错误信息不要写太具体哪个出错，不然别人可以探测你的ip和token名单
    val errMsg = "invalid token or ip blocked"
    val errRet:Ret<Unit?> = Ret.createError(null, errMsg)

    // check token
    val tokenList = settings.httpService.tokenList
    //全空格token不被允许，不过"a b c"这种可以允许，首尾空格将被去除，但中间的会保留
    if(token == null || tokenList.isEmpty() || token.isBlank() || tokenList.contains(token).not()) {
        // token empty will reject all requests
        return errRet
    }

    val whiteList = settings.httpService.ipWhiteList
    //匹配到通配符 或者 匹配到请求者的ip 则 放行
    if(whiteList.isEmpty() || requestIp.isBlank() || whiteList.find { it == matchAllIpSign || it == requestIp } == null) {
        return errRet
    }

    return Ret.createSuccess(null)
}


internal class HttpServer(
    val host:String,
    val port:Int
) {
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine. Configuration>? = null

    private fun createNotify(notifyId:Int) : ServiceNotify {
        return ServiceNotify(HttpServiceExecuteNotify.create(notifyId))
    }

    private fun sendSuccessNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { title:String?, msg:String?, startPage:Int?, startRepoId:String? ->
        if(settings.httpService.showNotifyWhenProgress) {
            serviceNotify.sendSuccessNotification(title, msg, startPage, startRepoId)
        }
    }

    private fun sendErrNotificationIfEnable(serviceNotify: ServiceNotify, settings:AppSettings)={ title:String, msg:String, startPage:Int, startRepoId:String ->
        if(settings.httpService.showNotifyWhenProgress) {
            serviceNotify.sendErrNotification(title, msg, startPage, startRepoId)
        }
    }

    private fun sendProgressNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { repoNameOrId:String, progress:String ->
        if(settings.httpService.showNotifyWhenProgress) {
            serviceNotify.sendProgressNotification(repoNameOrId, progress)
        }
    }


    suspend fun startServer():Exception? {
        if(isServerRunning()) return null

        try {
            server = embeddedServer(Netty, host = host, port = port) {
                install(ContentNegotiation) {
                    //忽略对象里没有的key；编码默认值；紧凑格式
                    json(Json{ ignoreUnknownKeys = true; encodeDefaults=true; prettyPrint = false})
                }
                routing {
                    /**
                     * 用于检测服务器是否在线
                     */
                    get("/status") {
                        call.respond(createSuccessResult("online"))
                    }

                    // for test
//                    get("/echo/{msg}") {
//                        call.respond(createSuccessResult(call.parameters.get("msg") ?: ""))
//                    }

                    // not work, service launch app may require float window permission
//                    get("/launchApp") {
//                        HttpService.launchApp()
//                    }

                    /**
                     * query params:
                     *  repoNameOrId: repo name or id, match by name first, if none, will match by id
                     *  gitUsername: using for create commit, if null, will use PuppyGit settings
                     *  gitEmail: using for create commit, if null, will use PuppyGit settings
                     *  forceUseIdMatchRepo: 1 enable or 0 disable, default 0, if enable, will force match repo by repo id, else will match by name first, if no match, then match by id
                     *  token: token is required
                     *  async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished

                     * e.g.
                     * request: http://127.0.0.1/pull?repoNameOrId=abc
                     */
                    get("/pull") {
                        val sessionId = generateRandomString()

                        val repoNameOrIdForLog = mutableListOf<String>()
                        var repoForLog:RepoEntity? = null
                        val routeName = "'/pull'"
                        val settings = SettingsUtil.getSettingsSnapshot()

                        //notify
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)


                        try {
                            //检查token和ip
                            tokenAndIpPassedOrThrowException(call, routeName, settings)

                            //取参数
                            val forceUseIdMatchRepo = call.request.queryParameters.get("forceUseIdMatchRepo") == "1"
                            // get git username and email for merge, if request doesn't contains them, will use PuppyGit app settings
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""

                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext)


                            //从数据库查询仓库列表
                            val validRepoListFromDb = getRepoListFromDb(
                                call.request.queryParameters.getAll("repoNameOrId"),
                                repoNameOrIdForLog,
                                forceUseIdMatchRepo
                            )


                            //如果只有一个仓库，出错的话记录到数据库，显示在仓库卡片上
                            if(validRepoListFromDb.size == 1) {
                                repoForLog = validRepoListFromDb.first()
                            }

                            val asyncRunTask = call.request.queryParameters.get("async") != "0"

                            //执行请求，可能时间很长，所以开个协程，直接返回响应即可
                            val task = suspend {

                                MyLog.d(TAG, "generate notifyers for ${validRepoListFromDb.size} repos")

                                validRepoListFromDb.forEachBetter {
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

                                MyLog.d(TAG, "will do pull for ${validRepoListFromDb.size} repos: $validRepoListFromDb")
                                pullRepoList(
                                    sessionId = sessionId,
                                    repoList = validRepoListFromDb,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    pullWithRebase = pullWithRebase,
                                    asyncRunTask = asyncRunTask
                                )
                            }


                            if(asyncRunTask) {
                                doJobThenOffLoading {
                                    task()
                                }
                            }else {
                                task()
                            }

                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))

                            if(repoForLog!=null) {
                                createAndInsertError(repoForLog.id, "pull by api $routeName err: $errMsg")
                            }

                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_ChangeList, repoForLog?.id ?: "")


                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, repoNameOrId.size=${repoNameOrIdForLog.size}, repoNameOrId=$repoNameOrIdForLog, err=${e.stackTraceToString()}")
                        }

                    }


                    /**
                     * query params:
                     *  repoNameOrId: repo name or id，优先查name，若无匹配，查id
                     *  gitUsername: using for create commit, if null, will use PuppyGit settings
                     *  gitEmail: using for create commit, if null, will use PuppyGit settings
                     *  force: force push, 1 enable , 0 disable, if null, will disable (as 0)
                     *  forceUseIdMatchRepo: 1 enable or 0 disable, default 0, if enable, will force match repo by repo id, else will match by name first, if no match, then match by id
                     *  token: token is required
                     *  autoCommit: 1 enable or 0 disable, default 1: if enable and no conflict items exists, will auto commit all changes,
                     *    and will check index, if index empty, will not pushing;
                     *    if disable, will only do push, no commit changes,
                     *    no index empty check, no conflict items check.
                     *  async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
                     *  resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
                     *  cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)
                     * e.g.
                     * request: http://127.0.0.1/push?repoNameOrId=abc
                     */
                    get("/push") {
                        val sessionId = generateRandomString()

                        val repoNameOrIdForLog = mutableListOf<String>()
                        var repoForLog:RepoEntity? = null
                        val routeName = "'/push'"
                        val settings = SettingsUtil.getSettingsSnapshot()

                        //notify
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)

                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)

                            //默认禁用，不明确启用就当作禁用
                            val forceUseIdMatchRepo = call.request.queryParameters.get("forceUseIdMatchRepo") == "1"

                            //这个只要不明确传0，就是启用
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"

                            // force push or no
                            val force = call.request.queryParameters.get("force") == "1"


                            // get git username and email for merge
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""

                            // 查询仓库是否存在
                            // 尝试获取仓库锁，若获取失败，返回仓库正在执行其他操作

                            //从数据库查询仓库列表
                            val validRepoListFromDb = getRepoListFromDb(
                                call.request.queryParameters.getAll("repoNameOrId"),
                                repoNameOrIdForLog,
                                forceUseIdMatchRepo
                            )


                            //如果只有一个仓库，出错的话记录到数据库，显示在仓库卡片上
                            if(validRepoListFromDb.size == 1) {
                                repoForLog = validRepoListFromDb.first()
                            }

                            val asyncRunTask = call.request.queryParameters.get("async") != "0"

                            // reset
                            val resetIfErr = call.request.queryParameters.get("resetIfErr") ?:""
                            val cmtMsgPrefix = call.request.queryParameters.get("cmtMsgPrefix") ?:""

                            val task = suspend {

                                MyLog.d(TAG, "generate notifyers for ${validRepoListFromDb.size} repos")

                                validRepoListFromDb.forEachBetter {
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

                                MyLog.d(TAG, "will do push for ${validRepoListFromDb.size} repos: $validRepoListFromDb")

                                pushRepoList(
                                    sessionId = sessionId,
                                    repoList = validRepoListFromDb,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                    asyncRunTask = asyncRunTask,
                                    resetIfErr = resetIfErr,
                                    cmtMsgPrefix = cmtMsgPrefix,
                                )
                            }



                            if(asyncRunTask) {
                                doJobThenOffLoading {
                                    task()
                                }
                            }else {
                                task()
                            }

                            call.respond(createSuccessResult())

                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))

                            if(repoForLog!=null) {
                                createAndInsertError(repoForLog!!.id, "push by api $routeName err: $errMsg")
                            }

                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_ChangeList, repoForLog?.id ?: "")


                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, repoNameOrId.size=${repoNameOrIdForLog.size}, repoNameOrId=$repoNameOrIdForLog, err=${e.stackTraceToString()}")
                        }

                    }

                    /**
                     * query params:
                     *  repoNameOrId: repo name or id, match by name first, if none, will match by id
                     *  gitUsername: using for create commit, if null, will use PuppyGit settings
                     *  gitEmail: using for create commit, if null, will use PuppyGit settings
                     *  force: force push, 1 enable , 0 disable, if null, will disable (as 0)
                     *  forceUseIdMatchRepo: 1 enable or 0 disable, default 0, if enable, will force match repo by repo id, else will match by name first, if no match, then match by id
                     *  token: token is required
                     *  autoCommit: 1 enable or 0 disable, default 1: if enable and no conflict items exists, will auto commit all changes,
                     *    and will check index, if index empty, will not pushing;
                     *    if disable, will only do push, no commit changes,
                     *    no index empty check, no conflict items check.
                     *  async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
                     *  resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
                     *  cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)

                     */
                    get("/sync") {
                        val sessionId = generateRandomString()
                        val repoNameOrIdForLog = mutableListOf<String>()
                        var repoForLog:RepoEntity? = null
                        val routeName = "'/sync'"
                        val settings = SettingsUtil.getSettingsSnapshot()

                        //notify
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)

                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)

                            //默认禁用，不明确启用就当作禁用
                            val forceUseIdMatchRepo = call.request.queryParameters.get("forceUseIdMatchRepo") == "1"

                            //这个只要不明确传0，就是启用
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"

                            // force push or no
                            val force = call.request.queryParameters.get("force") == "1"


                            // get git username and email for merge
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""

                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext)

                            // 查询仓库是否存在
                            // 尝试获取仓库锁，若获取失败，返回仓库正在执行其他操作

                            //从数据库查询仓库列表
                            val validRepoListFromDb = getRepoListFromDb(
                                call.request.queryParameters.getAll("repoNameOrId"),
                                repoNameOrIdForLog,
                                forceUseIdMatchRepo
                            )


                            //如果只有一个仓库，出错的话记录到数据库，显示在仓库卡片上
                            if(validRepoListFromDb.size == 1) {
                                repoForLog = validRepoListFromDb.first()
                            }

                            val asyncRunTask = call.request.queryParameters.get("async") != "0"

                            val resetIfErr = call.request.queryParameters.get("resetIfErr") ?:""
                            val cmtMsgPrefix = call.request.queryParameters.get("cmtMsgPrefix") ?:""

                            val task = suspend {

                                MyLog.d(TAG, "generate notifyers for ${validRepoListFromDb.size} repos")

                                validRepoListFromDb.forEachBetter {
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

                                MyLog.d(TAG, "will do sync for ${validRepoListFromDb.size} repos: $validRepoListFromDb")

                                syncRepoList(
                                    sessionId = sessionId,
                                    repoList = validRepoListFromDb,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                    pullWithRebase = pullWithRebase,
                                    asyncRunTask = asyncRunTask,
                                    resetIfErr = resetIfErr,
                                    cmtMsgPrefix = cmtMsgPrefix,
                                )
                            }



                            if(asyncRunTask) {
                                doJobThenOffLoading {
                                    task()
                                }
                            }else {
                                task()
                            }

                            call.respond(createSuccessResult())

                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))

                            if(repoForLog!=null) {
                                createAndInsertError(repoForLog!!.id, "sync by api $routeName err: $errMsg")
                            }

                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_ChangeList, repoForLog?.id ?: "")


                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, repoNameOrId.size=${repoNameOrIdForLog.size}, repoNameOrId=$repoNameOrIdForLog, err=${e.stackTraceToString()}")
                        }

                    }

                    /**
                     * query params:
                     *  gitUsername: using for create commit, if null, will use PuppyGit settings
                     *  gitEmail: using for create commit, if null, will use PuppyGit settings
                     *  token: token is required
                     *  async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished

                     * e.g.
                     * request: http://127.0.0.1/pullAll?gitUsername=username&gitEmail=email&token=your_token
                     */
                    get("/pullAll") {
                        val sessionId = generateRandomString()

                        val routeName = "'/pullAll'"
                        val settings = SettingsUtil.getSettingsSnapshot()

                        //notify
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)

                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)





                            // get git username and email for merge, if request doesn't contains them, will use PuppyGit app settings
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""

                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext)

                            val asyncRunTask = call.request.queryParameters.get("async") != "0"

                            //执行请求，可能时间很长，所以开个协程，直接返回响应即可
                            val task = suspend {

                                val allRepos = AppModel.dbContainer.repoRepository.getAll()
                                MyLog.d(TAG, "generate notifyers for ${allRepos.size} repos")

                                allRepos.forEachBetter {
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

                                pullRepoList(
                                    sessionId = sessionId,
                                    repoList = allRepos,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    pullWithRebase = pullWithRebase,
                                    asyncRunTask = asyncRunTask,
                                )
                            }



                            if(asyncRunTask) {
                                doJobThenOffLoading {
                                    task()
                                }
                            }else {
                                task()
                            }

                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))

                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_Repos ,"")


                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, err=${e.stackTraceToString()}")
                        }
                    }

                    /**
                     * query params:
                     *  gitUsername: using for create commit, if null, will use PuppyGit settings
                     *  gitEmail: using for create commit, if null, will use PuppyGit settings
                     *  autoCommit: same as '/push'
                     *  force: 1 enable , 0 disable, default 0
                     *  token: token is required
                     *  async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
                     *  resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
                     *  cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)
                     *
                     * e.g.
                     * request: http://127.0.0.1/pushAll?token=your_token
                     */
                    get("/pushAll") {
                        val sessionId = generateRandomString()

                        val routeName = "'/pushAll'"
                        val settings = SettingsUtil.getSettingsSnapshot()

                        //notify
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)

                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)



                            //这个只要不明确传0，就是启用
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"

                            // force push or no
                            val force = call.request.queryParameters.get("force") == "1"


                            // get git username and email for merge
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""

                            val asyncRunTask = call.request.queryParameters.get("async") != "0"

                            val resetIfErr = call.request.queryParameters.get("resetIfErr") ?:""
                            val cmtMsgPrefix = call.request.queryParameters.get("cmtMsgPrefix") ?:""

                            // 查询仓库是否存在
                            // 尝试获取仓库锁，若获取失败，返回仓库正在执行其他操作
                            val task = suspend {
                                val allRepos = AppModel.dbContainer.repoRepository.getAll()
                                MyLog.d(TAG, "generate notifyers for ${allRepos.size} repos")

                                allRepos.forEachBetter {
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

                                pushRepoList(
                                    sessionId = sessionId,
                                    repoList = allRepos,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                    asyncRunTask = asyncRunTask,
                                    resetIfErr = resetIfErr,
                                    cmtMsgPrefix = cmtMsgPrefix,
                                )
                            }



                            if(asyncRunTask) {
                                doJobThenOffLoading {
                                    task()
                                }
                            }else {
                                task()
                            }

                            call.respond(createSuccessResult())

                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))

                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_Repos, "")


                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, err=${e.stackTraceToString()}")
                        }


                    }

                    // please to check document
                    get("/syncAll") {
                        val sessionId = generateRandomString()

                        val routeName = "'/syncAll'"
                        val settings = SettingsUtil.getSettingsSnapshot()

                        //notify
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)

                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)



                            //这个只要不明确传0，就是启用
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"

                            // force push or no
                            val force = call.request.queryParameters.get("force") == "1"


                            // get git username and email for merge
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""

                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext)

                            val asyncRunTask = call.request.queryParameters.get("async") != "0"

                            val resetIfErr = call.request.queryParameters.get("resetIfErr") ?:""
                            val cmtMsgPrefix = call.request.queryParameters.get("cmtMsgPrefix") ?:""

                            // 查询仓库是否存在
                            // 尝试获取仓库锁，若获取失败，返回仓库正在执行其他操作
                            val task = suspend {

                                val allRepos = AppModel.dbContainer.repoRepository.getAll()
                                MyLog.d(TAG, "generate notifyers for ${allRepos.size} repos")

                                allRepos.forEachBetter {
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

                                syncRepoList(
                                    sessionId=sessionId,
                                    repoList = allRepos,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                    pullWithRebase = pullWithRebase,
                                    asyncRunTask = asyncRunTask,
                                    resetIfErr = resetIfErr,
                                    cmtMsgPrefix = cmtMsgPrefix,
                                )
                            }



                            if(asyncRunTask) {
                                doJobThenOffLoading {
                                    task()
                                }
                            }else {
                                task()
                            }

                            call.respond(createSuccessResult())

                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))

                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_Repos, "")


                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, err=${e.stackTraceToString()}")
                        }

                    }
                }
            }.start(wait = false) // 不能传true，会block整个程序

            MyLog.w(TAG, "Http Server started on '${genHttpHostPortStr(host, port.toString())}'")
            return null
        }catch (e:Exception) {
            //端口占用之类的能捕获到错误，看来即使非阻塞也并非一启动就立即返回，应该是成功绑定端口ip后才返回
            MyLog.e(TAG, "Http Server start failed, err=${e.stackTraceToString()}")
            return e
        }
    }

    private suspend fun getRepoListFromDb(
        repoNameOrIdList:List<String>?,
        repoNameOrIdForLog: MutableList<String>,
        forceUseIdMatchRepo: Boolean
    ): MutableList<RepoEntity> {
        //取 仓库名或id 列表
        //如果没这个参数，list会是null而不是空list
        if (repoNameOrIdList == null || repoNameOrIdList.isEmpty()) {
            throw RuntimeException("require repo name or id")
        }

        MyLog.d(TAG, "raw repoNameOrId list size is: ${repoNameOrIdList.size}, values are: $repoNameOrIdList")

        //这个列表用来在出错的时候日志记一下用户传了什么参数
        repoNameOrIdForLog.addAll(repoNameOrIdList)


        val db = AppModel.dbContainer
        val validRepoListFromDb = mutableListOf<RepoEntity>()
        repoNameOrIdList.forEachBetter { repoNameOrId ->
            val repoRet = db.repoRepository.getByNameOrId(repoNameOrId, forceUseIdMatchRepo)
            if (repoRet.hasError() || repoRet.data == null) {
                MyLog.d(TAG, "query repo '$repoNameOrId' from db err: " + repoRet.msg)
            }else {
                validRepoListFromDb.add(repoRet.data!!)
            }

        }


        //虽然有repo name or id，但全部无效，数据库无匹配条目
        if(validRepoListFromDb.isEmpty()) {
            throw RuntimeException("no valid Repo matched")
        }


        return validRepoListFromDb
    }

    /**
     * will throw exception if token bad
     */
    private fun tokenAndIpPassedOrThrowException(call: RoutingCall, routeName: String, settings: AppSettings) {
        val token = call.request.queryParameters.get("token")
        val requestIp = call.request.host()
        val tokenCheckRet = isGoodTokenAndIp(token, requestIp, settings)
        if (tokenCheckRet.hasError()) {
            // log the query params maybe better?
            MyLog.e(TAG, "request rejected: routeName=$routeName, requestIp=$requestIp, token=$token, reason=${tokenCheckRet.msg}")
            throw RuntimeException(tokenCheckRet.msg)
        }
    }

    suspend fun stopServer():Exception? {
        if(server == null) {
            MyLog.w(TAG, "server is null, stop canceled")
            return null
        }

        try {
            server?.stop(0, 0)  //立即停止服务器
            server = null

            MyLog.w(TAG, "Http Server stopped")
            return null
        }catch (e:Exception) {
            MyLog.e(TAG, "Http Server stop failed: ${e.stackTraceToString()}")
            return e
        }
    }

    suspend fun restartServer():Exception? {
        stopServer()
        return startServer()
    }

    fun isServerRunning():Boolean {
        //这检查的是协程是否Active，协程还在运行，服务器就在运行，大概是这个逻辑吧？
        try {
            // if server stopped, even not null, still throw, so need catch
            return server?.application?.isActive == true
        }catch (e: Exception) {
            MyLog.e(TAG, "check server running err, will return false to treat as server is not running: ${e.stackTraceToString()}")
            return false;
        }

        //这个不行，服务器正在启动，连接不通，但不久就上线了，用这个会误认为服务器不在线，误启动
//        val settings = SettingsUtil.getSettingsSnapshot()
//        return checkApiRunning("${genHttpHostPortStr(settings.httpService.listenHost, settings.httpService.listenPort)}/ping", 2)
    }


    private suspend fun pullRepoList(
        sessionId: String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,
        gitEmailFromUrl:String,
        pullWithRebase: Boolean, // true rebase, else merge
        asyncRunTask: Boolean,
    ) {
        RepoActUtil.pullRepoList(
            sessionId = sessionId,

            repoList = repoList,
            routeName = routeName,
            gitUsernameFromUrl = gitUsernameFromUrl,
            gitEmailFromUrl = gitEmailFromUrl,

            pullWithRebase = pullWithRebase,
            asyncRunTask = asyncRunTask
        )
    }


    private suspend fun pushRepoList(
        sessionId: String,

        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,
        gitEmailFromUrl:String,
        autoCommit:Boolean,
        force:Boolean,
        asyncRunTask: Boolean,

        resetIfErr:String,
        cmtMsgPrefix:String,
    ) {
        RepoActUtil.pushRepoList(
            sessionId = sessionId,
            repoList = repoList,
            routeName = routeName,
            gitUsernameFromUrl = gitUsernameFromUrl,
            gitEmailFromUrl = gitEmailFromUrl,
            autoCommit = autoCommit,
            force = force,
            asyncRunTask = asyncRunTask,
            resetIfErr = resetIfErr,
            cmtMsgPrefix = cmtMsgPrefix,
        )
    }

    private suspend fun syncRepoList(
        sessionId:String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,
        gitEmailFromUrl:String,
        autoCommit:Boolean,
        force:Boolean,
        pullWithRebase: Boolean, // true rebase, else merge
        asyncRunTask: Boolean,
        resetIfErr:String,
        cmtMsgPrefix: String,
    ) {
        RepoActUtil.syncRepoList(
            sessionId = sessionId,
            repoList = repoList,
            routeName = routeName,
            gitUsernameFromUrl = gitUsernameFromUrl,
            gitEmailFromUrl = gitEmailFromUrl,
            autoCommit = autoCommit,
            force = force,
            pullWithRebase = pullWithRebase,
            asyncRunTask = asyncRunTask,
            resetIfErr = resetIfErr,
            cmtMsgPrefix = cmtMsgPrefix,
        )
    }
}



fun isHttpServerOnline(host: String, port:String): Ret<Unit?> {
    val targetUrl = "${genHttpHostPortStr(host, port)}/status"
    val success = NetUtil.checkApiRunning(targetUrl)
    MyLog.d(TAG, "#isHttpServerOnline: test url '$targetUrl', success=$success")
    return success
}
