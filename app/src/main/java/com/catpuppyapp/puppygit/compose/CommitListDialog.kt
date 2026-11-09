package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.forEachBetter


@Composable
fun CommitListDialog(
    title:String,
    firstLineLabel:String,
    firstLineText:String,
    commitListLabel:String,
    commits:List<String>,
    closeDialog:()->Unit,
){
    ConfirmDialog2(
        title = title,
        requireShowTextCompose = true,
        textCompose = {
            Column {
                //用 \n 是为了在复制文本的时候包含换行符
                MySelectionContainer {
                    Row {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append("$firstLineLabel: ")
                                }

                                append("$firstLineText\n")
                            }
                        )
                    }
                }

                MyHorizontalDivider()

                MySelectionContainer {
                    Text("\n${commitListLabel}:\n", fontWeight = FontWeight.ExtraBold)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    commits.forEachBetter {
                        item {
                            MySelectionContainer {
                                Text(it+"\n")
                            }
                        }
                    }
                }
            }
        },
        onCancel = closeDialog,
        cancelBtnText = stringResource(R.string.close),
        showOk = false
    ) {}
}
