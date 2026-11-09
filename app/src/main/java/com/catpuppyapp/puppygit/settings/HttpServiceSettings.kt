package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class HttpServiceSettings (
    /**
     * App启动时自动启动服务
     */
    var launchOnAppStartup:Boolean = false,

    var listenHost:String = "127.0.0.1",

    var listenPort:Int = 52520,

    /**
     * token, use to verify the requester, a request must ip in the white list and bearer the token
     *
     * will use first token in the list when copy api config in the Repos Screen
     * note: if token list is empty, will reject all request
     *
     */
    var tokenList:List<String> = listOf("default_puppygit_token"),

    /**
     * only respond the request which ip in the white list and bearer the token
     *
     * if include "*", will match all ips, it useful if you want to allow all ips
     */
    var ipWhiteList:List<String> = listOf("127.0.0.1"),

    /**
     * never do nothing and never response if ip in the list
     */
    @Deprecated("ipWhiteList and token fair enough")
    var ipBlackList:List<String> = listOf(),


    /**
     * show notify if server do act err
     *
     * 注意：这是系统通知，不是toast！
     * 最好能够实现：点击可启动app并前往仓库页面，定位到发生错误的仓库卡片
     */
    @Deprecated("now, only using show notify when progress")
    var showNotifyWhenErr:Boolean = true,

    /**
     * show notify if server do act success
     */
    @Deprecated("now, only using show notify when progress")
    var showNotifyWhenSuccess:Boolean = true,

    /**
     * show notify if server acting
     * 进度通知，例如正在推送、正在拉取之类的
     */
    var showNotifyWhenProgress:Boolean = true,

)
