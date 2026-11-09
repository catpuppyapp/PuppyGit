package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme


@Composable
fun SettingsTitle(text:String){
    val inDarkTheme = Theme.inDarkTheme
    Row(modifier = Modifier
        .background(color = if (inDarkTheme) Color.DarkGray else Color.LightGray)
        .fillMaxWidth()
        .padding(start = 5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
    }
}

@Composable
fun SettingsContent(onClick:(()->Unit)?=null, content:@Composable ()->Unit) {
    Row(
        modifier = (if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 60.dp)
            .padding(MyStyleKt.defaultItemPadding)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()
    }

    MyHorizontalDivider()
}

@Composable
fun SettingsContentSelector(
    left:@Composable ColumnScope.() -> Unit,
    right:@Composable ColumnScope.() -> Unit,
) {
    SettingsContentBox(
        leftModifier = MyStyleKt.SettingsItem.selectorLeftBaseModifier,
        rightModifier = MyStyleKt.SettingsItem.selectorRightBaseModifier,
        left = left,
        right = right,
    )
}

@Composable
fun SettingsContentSwitcher(
    left:@Composable ColumnScope.() -> Unit,
    right:@Composable ColumnScope.() -> Unit,
    onClick: (() -> Unit)? = null,
) {
    SettingsContentBox(
        leftModifier = MyStyleKt.SettingsItem.switcherLeftBaseModifier,
        rightModifier = MyStyleKt.SettingsItem.switcherRightBaseModifier,
        left = left,
        right = right,
        onClick = onClick,
    )
}

@Composable
fun SettingsContentBox(
    leftModifier: Modifier,
    rightModifier:Modifier,
    left:@Composable ColumnScope.() -> Unit,
    right:@Composable ColumnScope.() -> Unit,
    onClick: (() -> Unit)? = null,
) {
    SettingsContent(onClick) {
        Box(Modifier.fillMaxWidth()) {
            Column(modifier = leftModifier.align(Alignment.CenterStart)) {
                left()
            }


            Column(modifier = rightModifier.align(Alignment.CenterEnd)) {
                right()
            }
        }
    }
}
