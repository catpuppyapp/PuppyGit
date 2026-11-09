package com.catpuppyapp.puppygit.fileeditor.ui.composable.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.UIHelper

@Composable
fun EditorMenus(
    modifier: Modifier = Modifier,
    selectedLinesCount:Int,
    onCopy: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSelectAll: ()->Unit={},
    onCancel: () -> Unit = {},
) {
    val hasLinesSelected = selectedLinesCount > 0
    val inDarkTheme = Theme.inDarkTheme

    val elementCommonWeight = 0.2f
    val elementCommonPadding=4.dp

    val disableColor = UIHelper.getDisableBtnColor(inDarkTheme)
//    val defaultColor = LocalContentColor.current  //在这获取到的颜色和在xxxIcon组件里获取到的不一样
    val getColor = { enabled:Boolean ->
        if(enabled) null else disableColor
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            EditorMenu(
                icon = { TrashIcon(color=getColor(hasLinesSelected)) },
                label = {
                    if(hasLinesSelected) {
                        Text(text = stringResource(R.string.delete),
                            //                               color = getColor(hasLinesSelected)
                        )
                    }else{
                        Text(text = stringResource(R.string.delete),
                            color = disableColor
                        )
                    }
                },
                modifier = Modifier
                    .weight(elementCommonWeight)
                    .align(Alignment.CenterVertically)
                    .clickable(enabled = hasLinesSelected) { onDelete() }
                    .padding(elementCommonPadding)
            )
            EditorMenu(
                icon = { CopyIcon(color=getColor(hasLinesSelected)) },
                label = {
                        if(hasLinesSelected) {
                            Text(text = stringResource(R.string.copy),
    //                               color = getColor(hasLinesSelected)
                              )

                        }else{
                            Text(text = stringResource(R.string.copy),
                               color = disableColor
                            )
                        }
                },
                modifier = Modifier
                    .weight(elementCommonWeight)
                    .padding(start = elementCommonPadding)
                    .align(Alignment.CenterVertically)
                    .clickable(enabled = hasLinesSelected) { onCopy() }
                    .padding(
                        top = elementCommonPadding,
                        end = elementCommonPadding,
                        bottom = elementCommonPadding
                    )
            )
            EditorMenu(
                icon = { SelectAllIcon() },
                label = { Text(text = stringResource(R.string.select_all)) },
                modifier = Modifier
                    .weight(elementCommonWeight)
                    .align(Alignment.CenterVertically)
                    .clickable { onSelectAll() }
                    .padding(elementCommonPadding)
            )
            EditorMenu(
                icon = { CancelIcon() },
                label = { Text(text = stringResource(R.string.cancel)) },
                modifier = Modifier
                    .weight(elementCommonWeight)
                    .padding(end = elementCommonPadding)
                    .align(Alignment.CenterVertically)
                    .clickable { onCancel() }
                    .padding(
                        start = elementCommonPadding,
                        top = elementCommonPadding,
                        bottom = elementCommonPadding
                    )
            )
        }
    }
}

@Preview
@Composable
private fun EditorMenus_Preview() {
    EditorMenus(selectedLinesCount = 0)
}
