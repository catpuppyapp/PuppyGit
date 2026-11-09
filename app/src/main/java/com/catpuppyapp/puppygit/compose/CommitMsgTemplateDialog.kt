package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.template.CommitMsgTemplateUtil
import com.catpuppyapp.puppygit.template.PlaceHolder
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf

private const val TAG = "CommitMsgTemplateDialog"

@Composable
fun CommitMsgTemplateDialog(
    stateKeyTag:String,
    closeDialog: ()->Unit,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val settings = remember { SettingsUtil.getSettingsSnapshot() }
    val template = mutableCustomStateOf(stateKeyTag, "template") { TextFieldValue(text = settings.commitMsgTemplate, selection = TextRange(settings.commitMsgTemplate.length)) }

    val autoType = {text:String ->
        runCatching {
            template.apply {
                value = value.copy(
                    //在光标处插入text
                    text = value.text.replaceRange(value.selection.min, value.selection.max, text),
                    //将光标移动到刚插入的text后面
                    selection = TextRange(value.selection.min + text.length)
                )
            }
        }
    }

    ConfirmDialog3(
        requireShowTitleCompose = true,
        titleCompose = {},
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                TextField(
                    maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MyStyleKt.defaultHorizontalPadding),
                    value = template.value,
                    onValueChange = {
                        template.value = it
                    },

                )

                Spacer(Modifier.height(15.dp))

                MySelectionContainer {
                    Column {
                        CommitMsgTemplateUtil.apply {
                            val phListLastIndex = phList.size-1

                            phList.forEachIndexedBetter {idx, it ->
                                PlaceHolder(it) {
                                    autoType(it.pattern)
                                }

                                //加点间距
                                if(idx != phListLastIndex) {
                                    Spacer(Modifier.height(10.dp))
                                }
                            }

                        }
                    }
                }
            }
        },
        onCancel = closeDialog
    ) {
        closeDialog()

        doJobThenOffLoading {
            SettingsUtil.update {
                it.commitMsgTemplate = template.value.text
            }
        }
    }
}

@Composable
private fun PlaceHolder(
    ph: PlaceHolder,
    onClick:(ph: PlaceHolder)->Unit,
) {
    DefaultPaddingRow {
        SingleLineClickableText(ph.pattern) {
            onClick(ph)
        }

        Text(": e.g. ${ph.example}", fontWeight = FontWeight.Light)
    }
}
