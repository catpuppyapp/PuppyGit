package com.catpuppyapp.puppygit.dev

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.pref.PrefUtil

object DevFeature {
    private const val prefix = "Dev: "
    fun appendDevPrefix(text:String):String {
        return prefix+text
    }


    val safDiff_text = appendDevPrefix("SAF Diff")
    val inner_data_storage = appendDevPrefix("Inner Data")  // app 在 /data/data 下的私有目录
    val external_data_storage = appendDevPrefix("External Data") // app 在 外部存储路径/Android/data/data 下的私有目录



    // dev settings items: start

    // single diff
    val singleDiff = object : DevItem<Boolean>(text = "Single Diff", state = mutableStateOf(false), desc="Enable for better performance") {
        override fun update(newValue: Boolean, context: Context?) {
            //更新状态变量，使用的时候就不用查配置文件了
            state.value = newValue

            //写入配置文件
            SettingsUtil.update {
                it.devSettings.singleDiffOn = newValue
            }
        }
    }

    // treatNoWordMatchAsNoMatched when diff
    val treatNoWordMatchAsNoMatchedForDiff = object : DevItem<Boolean>(text = "Treat No Words Matched as Non-Matched", state = mutableStateOf(false), desc="Treat none words matched as non-matched when Diff contents and enabled match by words") {
        override fun update(newValue: Boolean, context: Context?) {
            //更新状态变量，使用的时候就不用查配置文件了
            state.value = newValue

            //写入配置文件
            SettingsUtil.update {
                it.devSettings.treatNoWordMatchAsNoMatchedForDiff = newValue
            }
        }
    }

    // degrade match by words to match by chars when no matched
    val degradeMatchByWordsToMatchByCharsIfNonMatched = object : DevItem<Boolean>(text = "Degrade Match by words", state = mutableStateOf(false), desc="Degrade to Match by chars if Match by words was non-matched, not good for space-split language matching (like English), but good for non-space-split language (like Chinese)") {
        override fun update(newValue: Boolean, context: Context?) {
            //更新状态变量，使用的时候就不用查配置文件了
            state.value = newValue

            //写入配置文件
            SettingsUtil.update {
                it.devSettings.degradeMatchByWordsToMatchByCharsIfNonMatched = newValue
            }
        }
    }


    // matched all for selected compare at diff screen
    val setDiffRowToNoMatched = appendDevPrefix("No Matched")
    val setDiffRowToAllMatched = appendDevPrefix("All Matched")
    //这里的state的默认值其实无所谓，每次启动app都会初始化DevSettings里对应的值，所以若想改初始值，应去改那里，而不是这里
    val showMatchedAllAtDiff = object : DevItem<Boolean>(text = "Show 'No/All Matched' at Diff Screen", state = mutableStateOf(true)) {
        override fun update(newValue: Boolean, context: Context?) {
            //更新状态变量，使用的时候就不用查配置文件了
            state.value = newValue

            //写入配置文件
            SettingsUtil.update {
                it.devSettings.showMatchedAllAtDiff = newValue
            }
        }
    }




    // random launching text
    val showRandomLaunchingText = object : DevItem<Boolean>(text = "Show Random Launching Text", state = mutableStateOf(false)) {
        override fun update(newValue: Boolean, context: Context?) {
            //更新状态变量，使用的时候就不用查配置文件了
            state.value = newValue

            //写入配置文件
            PrefUtil.setShowRandomLaunchingText(context!!, newValue)
        }
    }

    // dev settings items: end

    // 旧change list 加载方法: start
    val legacyChangeListLoadMethod = object : DevItem<Boolean>(text = "Legacy ChangeList Load Method", state = mutableStateOf(true), desc = "Better enable this, new method are unstable and maybe slower") {
        override fun update(newValue: Boolean, context: Context?) {
            //更新状态变量，使用的时候就不用查配置文件了
            state.value = newValue

            //写入配置文件
            SettingsUtil.update { it.devSettings.legacyChangeListLoadMethod = newValue }
        }
    }

    // 旧change list 加载方法: end

    // dev menu items
    val settingsItemList = listOf(
        singleDiff,
        treatNoWordMatchAsNoMatchedForDiff,
        degradeMatchByWordsToMatchByCharsIfNonMatched,
        showMatchedAllAtDiff,
//        showRandomLaunchingText,
        legacyChangeListLoadMethod,
    )

}


abstract class DevItem<T>(
    val text:String,
    val state: MutableState<T>,
    //详细的描述文本，在设置项以小字显示
    val desc:String = "",
) {
    // context如果需要可以传，不需要可以不传
    abstract fun update(newValue:T, context: Context? = null)
}
