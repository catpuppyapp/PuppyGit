package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel


@Composable
fun AppIcon(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val inDarkTheme = remember(Theme.inDarkTheme) { Theme.inDarkTheme }
    val appIcon = AppModel.getAppIcon(context, inDarkTheme)
    Image(appIcon, contentDescription = stringResource(R.string.app_icon), modifier = modifier)
}

@Composable
fun AppIconMonoChrome(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val context = LocalContext.current
    val appIcon = AppModel.getAppIconMonoChrome(context)
    Icon(appIcon, contentDescription = stringResource(R.string.app_icon), modifier = modifier, tint = tint)
}
