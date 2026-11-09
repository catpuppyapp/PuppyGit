package dev.jeziellago.compose.markdowntext

import android.util.TypedValue

/**
 * Requires API Level 26 to apply auto size
 * */
data class AutoSizeConfig(
    val autoSizeMinTextSize: Int,
    val autoSizeMaxTextSize: Int,
    val autoSizeStepGranularity: Int,
    val unit: Int = TypedValue.COMPLEX_UNIT_SP,
)
