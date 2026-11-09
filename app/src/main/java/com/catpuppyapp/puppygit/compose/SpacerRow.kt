package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun SpacerRow(paddingValues: PaddingValues = PaddingValues(MyStyleKt.BottomBar.outsideContentPadding)) {
//    Row(
//        modifier = Modifier.padding(paddingValues).fillMaxSize(),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//    }

    Spacer(Modifier.padding(paddingValues))
}
