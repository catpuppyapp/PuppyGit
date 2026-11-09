package com.catpuppyapp.puppygit.settings

import com.catpuppyapp.puppygit.utils.parseLongOrDefault
import kotlinx.serialization.Serializable

@Serializable
data class PackageNameAndRepoSettings(
    private val pullInterval:String="",
    private val pushDelay:String="",
) {

    // get valid number or empty string, never return invalid number and non-empty string
    fun getPullIntervalFormatted() = parseLongOrDefault(pullInterval, null)?.toString() ?: ""
    fun getPushDelayFormatted() = parseLongOrDefault(pushDelay, null)?.toString() ?: ""

    // this should never is null
    fun getPullIntervalActuallyValue(settings: AppSettings) = parseLongOrDefault(pullInterval, settings.automation.pullIntervalInSec) ?: 0L
    fun getPushDelayActuallyValue(settings: AppSettings) = parseLongOrDefault(pushDelay, settings.automation.pushDelayInSec) ?: 0L

    companion object {
        // if is not a valid number, save empty string to settings
        fun formatPullIntervalBeforeSaving(value:String) = parseLongOrDefault(value, null)?.toString() ?: ""
        fun formatPushDelayBeforeSaving(value:String) = parseLongOrDefault(value, null)?.toString() ?: ""
    }
}
