package com.catpuppyapp.puppygit.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R

/**
 * 当resetValue大于0时，显示重置按钮
 */
@Composable
fun FontSizeAdjuster(fontSize:MutableIntState, resetValue:Int = -1) {
    LongPressAbleIconBtn(
        enabled = fontSize.intValue > 0,
        tooltipText = stringResource(R.string.decrease),
        icon = Icons.Filled.TextDecrease,
        iconContentDesc = stringResource(R.string.decrease),
    ) {
        fontSize.intValue -= 1
    }

    Text(text = fontSize.intValue.toString())

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.increase),
        icon = Icons.Filled.TextIncrease,
        iconContentDesc = stringResource(R.string.increase),
    ) {
        fontSize.intValue += 1
    }

    if(resetValue > 0) {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.reset),
            icon = Icons.Filled.SettingsBackupRestore,
            iconContentDesc = stringResource(R.string.reset),
        ) {
            fontSize.intValue = resetValue
        }
    }
}
