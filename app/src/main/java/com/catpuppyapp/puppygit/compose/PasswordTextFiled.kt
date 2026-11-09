package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R


@Composable
fun PasswordTextFiled(
    password: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    label:String,
    placeholder:String = "",
    singleLine:Boolean = true,
    focusRequest:FocusRequester? = null,
    errMsg:MutableState<String>? = null,
    enabled:Boolean = true,
    paddingValues:PaddingValues = PaddingValues(10.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    keyboardActions: KeyboardActions = KeyboardActions.Default,

    canSwitchPasswordVisible: Boolean = true,

    // press keyboard enter callback
    enterPressedCallback: (()->Unit)? = null,
) {

    val getErrMsg = { errMsg?.value ?: "" }
    val hasErr = { getErrMsg().isNotEmpty() }

    TextField(
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .then(
                if(focusRequest != null) {
                    Modifier.focusRequester(focusRequest)
                }else {
                    Modifier
                }
            ).then(
                if(enterPressedCallback != null) {
                    Modifier.onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) {
                            false
                        } else if(event.key == Key.Enter) {
                            enterPressedCallback()
                            true
                        }else {
                            false
                        }
                    }
                }else {
                    Modifier
                }
            )
        ,
        singleLine = singleLine,
        value = password.value,
        onValueChange = {
            password.value = it
            errMsg?.value = ""
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },


        isError = hasErr(),
        supportingText = {
            if(hasErr()) {
                //错误信息拷贝不了，不用加选择拷贝容器，如果click错误信息，会触发聚焦输入框
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = getErrMsg(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            if(canSwitchPasswordVisible) {
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    // contentDescription is for accessibility
                    Icon(
                        imageVector = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible.value) "password visible now" else "password invisible now"
                    )
                }
            }
        }

    )
}
