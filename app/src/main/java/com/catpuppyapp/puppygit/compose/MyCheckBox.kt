package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme


@Composable
fun MyCheckBox(
    text: String,
    value: MutableState<Boolean>,
    enabled: Boolean = true,
    height: Dp = MyStyleKt.CheckoutBox.height,
    onValueChange: (newValue:Boolean)->Unit = {value.value = it}
) {
    val inDarkTheme = Theme.inDarkTheme

    Row(
        Modifier
            .fillMaxWidth()
            .height(height)
            .toggleable(
                enabled = enabled,
                value = value.value,
                onValueChange = { onValueChange(it) },  //话说这个it是不是新值，所以不反转原始值直接 value = it，不就行了？
                role = Role.Checkbox
            )
            .padding(horizontal = MyStyleKt.defaultHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            enabled=enabled,
            checked = value.value,
            onCheckedChange = null // null recommended for accessibility with screenreaders
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
            color = if(enabled) Color.Unspecified else if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
        )

    }
}

