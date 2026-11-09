package com.catpuppyapp.puppygit.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.activity.findActivity
import com.catpuppyapp.puppygit.syntaxhighlight.base.TextMateUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.pref.PrefMan
import com.catpuppyapp.puppygit.utils.pref.PrefUtil

private const val TAG = "Theme"

object Theme {
    val Orange = Color(0xFFFF5722)
    val darkLightBlue = Color(0xDF456464)
    val mdGreen = Color(0xFF13831C)
    val mdRed = Color(0xFFAD2A2A)
    val Gray1 = Color(0xFF8C8C8C)
    val Gray2 = Color(0xFF7C7C7C)


    const val auto = 0
    const val light = 1
    const val dark = 2

//    val invalidThemeValue: Int = -1
    const val defaultThemeValue: Int = auto // default is auto

    // this value will update when theme ready
    var inDarkTheme = false

    // inDarkTheme的state版，方便触发页面重新渲染
    val inDarkThemeState = mutableStateOf(false)

    val themeList = listOf(
        auto,  // auto, 实际就是跟随系统
        light,  // light
        dark,  // dark
    )

    /**
     * 存储当前Activity的主题的状态变量，弄状态变量是为了实现切换主题后不需要重启即可生效
     */
    var theme:MutableState<Int> = mutableStateOf(defaultThemeValue)

    const val defaultDynamicColorsValue = true
    val dynamicColor = mutableStateOf(defaultDynamicColorsValue)

    fun init(themeValue: Int, dynamicColorEnabled: Boolean) {
        theme.value = getALegalThemeValue(themeValue)
        dynamicColor.value = dynamicColorEnabled
    }

    fun updateThemeValue(context: Context, newValue:Int) {
        val themeValue = getALegalThemeValue(newValue)
        // `Theme.inDarkTheme` will update after state changed, so need not update it at here
        theme.value = themeValue
        PrefMan.set(context, PrefMan.Key.theme, ""+themeValue)
    }

    fun getALegalThemeValue(themeValue:Int) = if(themeList.contains(themeValue)) themeValue else defaultThemeValue;

    fun updateDynamicColor(context:Context, newValue:Boolean) {
        dynamicColor.value = newValue
        PrefUtil.setDynamicColorsScheme(context, newValue)
    }


    fun getThemeTextByCode(themeCode:Int?, appContext: Context):String {
        if(themeCode == auto) {
            return appContext.getString(R.string.auto)
//            return appContext.getString(R.string.follow_system)
        }else if(themeCode == light) {
            return appContext.getString(R.string.light)
        }else if(themeCode == dark) {
            return appContext.getString(R.string.dark)
        }else {
            return appContext.getString(R.string.unknown)
        }

    }
}

private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun InitContent(context: Context, content: @Composable ()->Unit) {
    Theme.init(
        themeValue = PrefMan.getInt(context, PrefMan.Key.theme, Theme.defaultThemeValue),
        dynamicColorEnabled = PrefUtil.getDynamicColorsScheme(context),
    )
    PuppyGitAndroidTheme {
        content()
    }
}

@Composable
fun PuppyGitAndroidTheme(
    theme:Int = Theme.theme.value,
    // Dynamic color is available on Android 12+, but maybe will cause app color weird, e.g. difficult to distinguish
    dynamicColor: Boolean = Theme.dynamicColor.value,
    content: @Composable () -> Unit
) {
    val darkTheme = if(theme == Theme.auto) isSystemInDarkTheme() else (theme == Theme.dark)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                Theme.inDarkTheme = true;
                Theme.inDarkThemeState.value = true
                getDynamicColor(true, context)
            }else {
                Theme.inDarkTheme = false
                Theme.inDarkThemeState.value = false
                getDynamicColor(false, context)
            }
        }

        darkTheme -> {
            Theme.inDarkTheme = true;
            Theme.inDarkThemeState.value = true
            DarkColorScheme
        }

        else -> {
            Theme.inDarkTheme = false;
            Theme.inDarkThemeState.value = false
            LightColorScheme
        }
    }

    // update text mate theme to avoid forget set it before syntax highlight analyze,
    //   then set theme before analyze or not, will all be fine
    TextMateUtil.updateTheme(Theme.inDarkTheme)

    // actually idk code at below for what purpose, it is bundled when I created the project
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            if(activity != null) {
                val window = activity.window
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    //test
//    Theme.inDarkTheme=true
    //test

    MaterialTheme(
        //test
//        colorScheme = DarkColorScheme,
        //test
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun getDynamicColor(inDarkTheme: Boolean, context: Context): ColorScheme {
    return (if(inDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)).let {
        if(isNeutralColorScheme(it)) {
            MyLog.d(TAG, "Neutral (gray) color scheme detected, will use secondary replaced primary colors")

            // default primary color of neutral color scheme(gray)
            //   maybe will cause difficult to distinguish,
            //   so use secondary color instead of it
            it.copy(
                primary = it.secondary,
                primaryContainer = it.secondaryContainer,
                onPrimary = it.onSecondary,
                onPrimaryContainer = it.onSecondaryContainer
            )
        }else {
            MyLog.d(TAG, "Not Neutral (gray) color scheme, will use default primary colors")

            it
        }
    }
}

// rgb same = gray = Neutral color scheme
fun isNeutralColorScheme(colorScheme: ColorScheme) = colorScheme.primary.let { it.red == it.green && it.red == it.blue }
