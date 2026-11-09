package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier

@Composable
fun FullScreenScrollableColumn(
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content:@Composable ()->Unit,
) {
    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())

            // avoid text reached screen border
            .padding(MyStyleKt.defaultItemPadding)
        ,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        content()
    }
}
