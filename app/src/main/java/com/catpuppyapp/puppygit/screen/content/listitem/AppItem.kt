package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.catpuppyapp.puppygit.compose.TwoLineTextsAndIcons
import com.catpuppyapp.puppygit.dto.AppInfo


@Composable
fun AppItem(
    appInfo:AppInfo,
    trailIconWidth: Dp,
    trailIcons: @Composable BoxScope.(containerModifier:Modifier)->Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,
    ) {

        Image(
            bitmap = appInfo.appIcon.toBitmap().asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(start = 5.dp)
        )

        TwoLineTextsAndIcons(
            text1 = appInfo.appName,
            text2 = appInfo.packageName,
            trailIconWidth = trailIconWidth,
            trailIcons = trailIcons
        )

    }

}
