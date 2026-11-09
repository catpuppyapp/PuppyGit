package com.catpuppyapp.puppygit.utils.time

import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.isValidOffsetInMinutes

private const val TAG = "TimeZoneUtil"

object TimeZoneUtil {
    /**
     * 建议使用 `AppModel.getAppTimeZoneModeCached()` 以提高性能
     */
    fun getAppTimeZoneMode(settings: AppSettings) : TimeZoneMode {
        return try{
            if(settings.timeZone.followSystem) {
                TimeZoneMode.FOLLOW_SYSTEM
            }else {
                val offsetMinutes = settings.timeZone.offsetInMinutes.trim().toInt()
                if(isValidOffsetInMinutes(offsetMinutes)){
                    TimeZoneMode.SPECIFY
                }else {
                    TimeZoneMode.UNSET
                }
            }
        }catch (_:Exception) {
            TimeZoneMode.UNSET
        }
    }

    /**
     * 这个方法主要用在显示提交或者reflog等包含时间信息的GitObject时判断是否需要显示时区，默认情况下，如果不跟随系统时区也没指定有效值，则应该显示，
     *   因为这时候app会从GitObject中读取时区，而每个GitObject都有各自的时区，若不显示会分不清这时间到底是哪个时区的。
     *
     * @param useCache true 使用缓存的时区模式判断，性能更好；false，从配置文件重新读取当前时区模式，更准确。建议：一般用缓存的即可。
     */
    fun shouldShowTimeZoneInfo(settings: AppSettings, useCache:Boolean = true) : Boolean {
        return if(useCache) {
            AppModel.getAppTimeZoneModeCached(settings) == TimeZoneMode.UNSET
        }else {
            getAppTimeZoneMode(settings) == TimeZoneMode.UNSET
        }
    }

    fun appendUtcTimeZoneText(str:String, offsetInMinutes:Int) : String {
        return "$str ${formatMinutesToUtc(offsetInMinutes)}"
    }
}
