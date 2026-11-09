package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.utils.UIHelper

@Composable
fun MyCard(
    modifier: Modifier,
    containerColor:Color? = null,
    content:@Composable ()->Unit,
) {
    val containerColor = containerColor ?: UIHelper.defaultCardColor()

    Card(
        modifier = modifier,

        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        content()
    }
}

@Composable
fun MyToggleCard(
    modifier: Modifier,
    selected: Boolean,
    content: @Composable () -> Unit,
) {
    MyCard(
        modifier = modifier,
        containerColor = if(selected) MaterialTheme.colorScheme.primaryContainer else null,
        content = content
    )
}
