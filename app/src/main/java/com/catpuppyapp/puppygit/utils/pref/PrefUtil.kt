package com.catpuppyapp.puppygit.utils.pref

import android.content.Context
import com.catpuppyapp.puppygit.ui.theme.Theme

object PrefUtil {
    private const val trueStr = "1"
    private const val falseStr = "0"


    private fun getBoolean(context:Context, key:String, default: Boolean): Boolean {
        return PrefMan.get(context, key, if(default) trueStr else falseStr) != falseStr
    }

    private fun setBoolean(context: Context, key:String, newValue:Boolean) {
        PrefMan.set(context, key, if(newValue) trueStr else falseStr)
    }

    // dev mode

    /**
     * 更新dev模式
     */
    fun setDevMode(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.devModeOn, enable)
    }

    /**
     * 获取是否启用了dev模式，初始值为禁用
     */
    fun getDevMode(context: Context):Boolean {
        return getBoolean(context, PrefMan.Key.devModeOn, false)
    }


    // show random launching text

    fun setShowRandomLaunchingText(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.showRandomLaunchingText, enable)
    }

    /**
     * 启动时是否显示随机加载文本，默认禁用
     */
    fun getShowRandomLaunchingText(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.showRandomLaunchingText, false)
    }


    fun setDynamicColorsScheme(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.dynamicColorsScheme, enable)
    }

    fun getDynamicColorsScheme(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.dynamicColorsScheme, Theme.defaultDynamicColorsValue)
    }

    fun setGlobalGitConfigPullWithRebase(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.globalGitConfigPullWithRebase, enable)
    }

    fun getGlobalGitConfigPullWithRebase(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.globalGitConfigPullWithRebase, false)
    }

    fun setGlobalGitConfigCommitOnPush(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.globalGitConfigCommitOnPush, enable)
    }

    fun getGlobalGitConfigCommitOnPush(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.globalGitConfigCommitOnPush, false)
    }

    fun setGlobalGitConfigLfsEnabled(context: Context, enable:Boolean) {
        setBoolean(context, PrefMan.Key.globalGitConfigLfsEnabled, enable)
    }

    fun getGlobalGitConfigLfsEnabled(context: Context): Boolean {
        return getBoolean(context, PrefMan.Key.globalGitConfigLfsEnabled, false)
    }
}
