package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.user.UserUtil


@Composable
fun DepthTextField(depth: MutableState<String>) {
    val isPro = UserUtil.isPro()

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MyStyleKt.defaultHorizontalPadding),

        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        enabled = isPro,
        value = depth.value,
        onValueChange = {
            depth.value = it
        },
        label = {
            if (isPro) Text(stringResource(R.string.depth_optional)) else Text(stringResource(R.string.depth_optional_pro_only))
        },
        placeholder = {
            Text(stringResource(R.string.depth))
        }
    )
}

