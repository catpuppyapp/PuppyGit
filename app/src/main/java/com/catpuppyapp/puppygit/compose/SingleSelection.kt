package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt


private val defaultMinHeight = MyStyleKt.RadioOptions.minHeight


/**
 * 左边一个圆圈的那种单选列表，适合条目不太多的选项，条目太多建议用dropdown list
 */
@Composable
fun <T> SingleSelection(
    itemList:List<T>,
    selected:(idx:Int, T)->Boolean,
    text:(idx:Int, T)->String,
    onClick:(idx:Int, T)->Unit,
    beforeShowItem:((idx:Int, T)->Unit)? = null, // run for each item before it rendering
    skip:(idx:Int, T)->Boolean = {idx, item -> false }, // will not show item if skip return true
    itemDescContext: (@Composable (idx:Int, T) ->Unit)? = null,
    minHeight:Dp = defaultMinHeight
) {
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        for ((idx, item) in itemList.withIndex()) {
            beforeShowItem?.invoke(idx, item)

            if(skip(idx, item)) {
                continue
            }

            val selected = selected(idx, item)

            SingleSelectionItem(
                idx = idx,
                item = item,
                selected = selected,
                text = text,
                onClick = onClick,
                itemDescContext = itemDescContext,
                minHeight = minHeight,
            )
        }
    }

}



@Composable
fun <T> SingleSelectionItem(
    idx: Int,
    item: T,
    selected: Boolean,
    text: (Int, T) -> String,
    onClick: (Int, T) -> Unit,
    itemDescContext: @Composable ((Int, T) -> Unit)? = null,
    minHeight: Dp = defaultMinHeight,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .selectable(
                selected = selected,
                onClick = {
                    onClick(idx, item)
                },
                role = Role.RadioButton
            )
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with screenreaders
        )

        MySelectionContainer {
            Column {
                ScrollableRow {
                    Text(
                        text = text(idx, item),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                itemDescContext?.invoke(idx, item)
            }
        }

    }
}
