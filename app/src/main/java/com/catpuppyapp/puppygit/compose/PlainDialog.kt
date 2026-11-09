package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
private fun PlainDialog(
    modifier: Modifier = Modifier.fillMaxWidth(),
    shape: Shape = RoundedCornerShape(16.dp),
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = modifier,
            shape = shape,
        ) {
            content()
        }
    }
}

@Composable
fun PlainDialogWithPadding(
    contentPadding: PaddingValues = MyStyleKt.defaultItemPaddingValues,
    scrollable: Boolean = false,
    scrollState: ScrollState? = null,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    PlainDialog(onClose = onClose) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .then(if(scrollable) Modifier.verticalScroll(scrollState ?: rememberScrollState()) else Modifier)
            ,
        ) {
            content()
        }
    }
}
