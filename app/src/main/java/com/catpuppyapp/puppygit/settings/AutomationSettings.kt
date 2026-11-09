package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class AutomationSettings (
    var packageNameAndRepoIdsMap:MutableMap<String, List<String>> = mutableMapOf(),

    var packageNameAndRepoAndSettingsMap: MutableMap<String, PackageNameAndRepoSettings> =  mutableMapOf(),

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

    /**
     * auto dismiss progress notification after a timeout
     * 进度通知自动消除，开启后通知会在一段时间后自动消失
     */
    var progressNotifyAutoDismiss:Boolean = false,


    /**
     * 在一段时间间隔内重复进入app只有第一次才会执行pull
     * 设为负数将禁用pull
     */
    var pullIntervalInSec:Long = 0L,

    /**
     * 离开app后延迟多久才执行推送，若在此时间间隔内重新进入app将会取消推送。
     * 设为负数将禁用push
     */
    var pushDelayInSec:Long = 0L,

)
