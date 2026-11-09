package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ListItemRow(
    content:@Composable ()->Unit,
){
    Row(
        modifier = Modifier
            .defaultMinSize(minWidth = Dp.Unspecified, minHeight = 50.dp)
            .padding(5.dp)
            .fillMaxWidth(.85F),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start

    ){
        content()
    }
}
