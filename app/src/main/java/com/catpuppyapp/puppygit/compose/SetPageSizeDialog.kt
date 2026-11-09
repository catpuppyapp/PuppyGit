package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable

private const val TAG = "SetPageSizeDialog"


private const val invalidPageSize = -1
private const val minPageSize = 1  // make sure it bigger than `invalidPageSize`

fun isInvalidPageSize(size:Int) = size < minPageSize

@Composable
fun SetPageSizeDialog(
    pageSizeBuf: CustomStateSaveable<TextFieldValue>,
    pageSize: MutableState<Int>,
    rememberPageSize: MutableState<Boolean>,
    trueCommitHistoryFalseFileHistory: Boolean,
    closeDialog:()->Unit,
) {

    val activityContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    ConfirmDialog2(
        title = stringResource(R.string.page_size),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                TextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),

                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                    value = pageSizeBuf.value,
                    singleLine = true,
                    onValueChange = {
                        pageSizeBuf.value = it
                    },
                    label = {
                        Text(stringResource(R.string.page_size))
                    },
                )

                Spacer(Modifier.height(10.dp))

                MyCheckBox(text= stringResource(R.string.save), rememberPageSize)
            }
        },
        onCancel = closeDialog
    ) {
        closeDialog()

        try {
            val newPageSize = try {
                pageSizeBuf.value.text.trim().toInt()
            }catch (_:Exception) {
                Msg.requireShow(activityContext.getString(R.string.invalid_number))
                invalidPageSize
            }

            if(!isInvalidPageSize(newPageSize)) {
                pageSize.value = newPageSize
//                pageSizeBuf.value = TextFieldValue(newPageSize.toString())

                if(rememberPageSize.value) {
                    SettingsUtil.update {
                        if(trueCommitHistoryFalseFileHistory) {
                            it.commitHistoryPageSize = newPageSize
                        }else {
                            it.fileHistoryPageSize = newPageSize
                        }
                    }
                }
            }

        }catch (e:Exception) {
            MyLog.e(TAG, "#SetPageSizeDialog err: ${e.localizedMessage}")
        }
    }

    Focuser(focusRequester, scope)
}
