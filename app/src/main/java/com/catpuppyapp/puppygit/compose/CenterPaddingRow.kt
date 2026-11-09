package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun CenterPaddingRow(
    paddingValues: PaddingValues = PaddingValues(top = MyStyleKt.defaultItemPadding),
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.padding(paddingValues),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}
