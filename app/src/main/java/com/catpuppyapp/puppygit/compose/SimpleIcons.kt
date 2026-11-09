package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.catpuppyapp.puppygit.utils.UIHelper

@Composable
fun SimpleCheckBox(
    enabled:Boolean,
    contentDescription:String? = null,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = UIHelper.getCheckBoxByState(enabled),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}
