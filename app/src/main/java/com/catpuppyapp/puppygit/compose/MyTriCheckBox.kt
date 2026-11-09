package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme


@Composable
fun MyTriCheckBox(
    text: String,
    state: ToggleableState,
    enabled:Boolean = true,
    height: Dp = MyStyleKt.CheckoutBox.height,
    onValueChange: ()->Unit
) {
    val inDarkTheme = Theme.inDarkTheme

    Row(
        Modifier
            .fillMaxWidth()
            .height(height)
            .triStateToggleable(
                enabled = enabled,
                state = state,
                role = Role.Checkbox
            ){
                onValueChange()
            }
            .padding(horizontal = MyStyleKt.defaultHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TriStateCheckbox(
            enabled = enabled,
            state = state,
            onClick = null
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
            color = if(enabled) Color.Unspecified else if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
        )

    }
}

