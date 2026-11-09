package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R

@Composable
fun FooterPager(currentPage:MutableIntState, sumPage:Int) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.Transparent).padding(horizontal = 60.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.previous_page),
            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            iconContentDesc = stringResource(R.string.previous_page),
            iconModifier = Modifier.size(100.dp),
            enabled = currentPage.intValue>1,

        ) {
            currentPage.intValue-=1
        }

        //current page
        Text(text = ""+currentPage.intValue)

        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.next_page),
            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            iconContentDesc = stringResource(R.string.next_page),
            iconModifier = Modifier.size(100.dp),
            enabled = currentPage.intValue<sumPage,

        ) {
            currentPage.intValue+=1
        }

    }
}
