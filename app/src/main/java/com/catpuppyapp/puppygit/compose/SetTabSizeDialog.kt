package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable

@Composable
fun SetTabSizeDialog(
    tabSizeBuf: CustomStateSaveable<TextFieldValue>,
    onCancel: () -> Unit,
    onOk: (newTabSize: String) -> Unit
) {
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    ConfirmDialog2(
        title = stringResource(R.string.tab_size),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),

                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                    value = tabSizeBuf.value,
                    singleLine = true,
                    onValueChange = {
                        tabSizeBuf.value = it
                    },
                    label = {
                        Text(stringResource(R.string.tab_size))
                    },
                )

                Spacer(Modifier.height(10.dp))

                MySelectionContainer {
                    Text(stringResource(R.string.set_tab_size_note))
                }

            }
        },
        onCancel = onCancel
    ) {
        onOk(tabSizeBuf.value.text)
    }

    Focuser(focusRequester, scope)
}
