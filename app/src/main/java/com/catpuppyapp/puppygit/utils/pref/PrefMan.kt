package com.catpuppyapp.puppygit.utils.pref

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences

/**
 * 最好用applicationContext存取变量，没特别原因，因为applicationContext生命周期比activityContext长，不过应该存取的都是同一个文件，所以问题不大
 */
object PrefMan {
    //配置文件名"${fileName}.xml"
    private const val fileName = "settings"

    object Key {
        /**
         * the value of this key:
         * empty string: auto detect language
         * country code: will use locale of country code, e.g. us will use english
         *
         * 自动检测或指定国家代码
         */
        const val lang = "lang"
        /**
         * e: err
         * w: warn
         * i: info
         * d: debug
         * v: verbose
         */
        const val logLevel = "log_level"
        /**
         * log file keep in days
         * 保留几天的日志
         */
        const val logKeepDays = "log_keep_days"
        /**
         * 0 auto; 1 light; 2 dark.
         * reference SettingsInnerPage.themeList, this value should match with the themeList indices
         * 这的值应该和设置页面的themeList索引匹配
         */
        const val theme = "theme"

        /**
         * launch service on system start up: 0 disable, 1 enable, default 0
         */
        const val launchServiceOnSystemStartup = "launch_service_on_system_startup"

        /**
         * master password，名字抽象一点，别让人一眼就看出是什么，妈的不是自欺欺人吗，呵呵
         */
        const val masterPass = "mpwd"

        const val firstUse = "firstUse"

        /**
         * 0 disable, 1 enable, default 0
         */
        const val devModeOn = "devModeOn"


        /**
         * 启动app时显示随机加载文本, 0 disable, 1 enable, default 0
         */
        const val showRandomLaunchingText = "showRandomLaunchingText"

        /**
         *
         * enabled to follow system color scheme, 0 disable, 1 enable, default 1
         * note: enable may cause colors difficult to distinguish.
         * see: https://github.com/catpuppyapp/PuppyGit/issues/66
         */
        const val dynamicColorsScheme = "dynamicColorsScheme"

        const val globalGitConfigUser = "globalGitConfigUser"
        const val globalGitConfigEmail = "globalGitConfigEmail"
        const val globalGitConfigPullWithRebase = "globalGitConfigPullWithRebase"
        const val globalGitConfigCommitOnPush = "globalGitConfigCommitOnPush"
        const val globalGitConfigLfsEnabled = "globalGitConfigLfsEnabled"
    }

    private fun getPrefs(appContext: Context) = appContext.getSharedPreferences(fileName, MODE_PRIVATE)

    fun get(appContext: Context, key:String, defaultValue:String):String {
        try {
            // 获取 SharedPreferences
            val prefs = getPrefs(appContext)
            val value = prefs.getString(key, defaultValue)
            return value ?: defaultValue
        }catch (_:Exception) {
            return defaultValue
        }
    }

    fun getInt(appContext: Context, key:String, defaultValue:Int):Int {
        try {
            val value = get(appContext, key, ""+defaultValue)
            return value.toInt()
        }catch (_:Exception) {
            return defaultValue
        }
    }

    fun getChar(appContext: Context, key:String, defaultValue:Char):Char {
        try {
            val value = get(appContext, key, ""+defaultValue)
            return value.get(0)
        }catch (_:Exception) {
            return defaultValue
        }
    }


    fun set(appContext: Context, key:String, value:String) {
        // 获取 SharedPreferences 实例
        val prefs: SharedPreferences = getPrefs(appContext)
        // 获取编辑器
        val editor = prefs.edit()


        // 更新语言代码
        editor.putString(key, value)


        // 提交更改
        // apply is async, commit is sync
        editor.apply() // 或者 editor.commit();
    }



    fun isFirstUse(context: Context):Boolean {
        return PrefMan.get(context, PrefMan.Key.firstUse, "1") == "1"
    }

    fun updateFirstUse(context: Context, newValue:Boolean) {
        PrefMan.set(context, PrefMan.Key.firstUse, if(newValue) "1" else "0")
    }

}
