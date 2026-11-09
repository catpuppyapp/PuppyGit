package com.catpuppyapp.puppygit.compose


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter


private val trailingIconSize = MyStyleKt.defaultIconSize

@Composable
fun <T> SelectedItemDialog(
    title: String = stringResource(R.string.selected_str),
    selectedItems:List<T>,
    formatter:(T)->String,
    switchItemSelected:(T)->Unit,  //用switch而不是单纯的remove是为了日后可实现撤销删除方便，只要再把条目传给switchItemSelected函数，就能重新选中条目，但这样的话，恢复后的条目会在列表末尾，若想回到原位呢？难道删除后做整个列表的备份？或者这个函数改成能指定索引的？

    //清空条目列表
    clearAll:()->Unit,

    //此函数应该仅关闭弹窗，不要执行多余操作
    closeDialog:()->Unit,

    // cancel可能包含比closeDialog更多的操作
    onCancel:()->Unit = closeDialog
) {
    SelectedItemDialog3(
        title = title,
        selectedItems = selectedItems,
        text = { Text(text = formatter(it)) },
        textFormatterForCopy = formatter,
        switchItemSelected = switchItemSelected,
        clearAll = clearAll,
        closeDialog = closeDialog,
        onCancel = onCancel,

    )
}

/**
 * 相比第一版，提供更多自定义的可能性，可用 Compose组件自定义文本和尾部图标
 *
 * 为什么本方法是private: 之前直接用可复制弹窗实现，但后来改成了每行文本用各自的SelectionContainer，而被selection container包围的内容无法用Box的对齐，导致文字无法居中，所以把这个方法改成private，然后在“必经之路” SelecteeItemDialog3上做手脚了
 */
@Composable
private fun <T> SelectedItemDialog2(
    selectedItems:List<T>,
    title:String,
    text:@Composable BoxScope.(T) -> Unit,
    trailIcon:@Composable BoxScope.(T) -> Unit,
    clearAll:()->Unit,
    closeDialog:()->Unit,
    onCancel:()->Unit,
    onCopy:()->Unit
) {
    ConfirmDialog3(
        title = title,
//            text = selectedItemsShortDetailsStr.value,
        requireShowTextCompose = true,
        textCompose = {
            LazyColumn {
                selectedItems.forEachBetter {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            text(it)

                            trailIcon(it)
                        }

                        MyHorizontalDivider()
                    }
                }

            }
        },
        customCancel = {
            ScrollableRow {
                //清空按钮
                if(selectedItems.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            closeDialog()
                            clearAll()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.clear),
                            color = MyStyleKt.TextColor.danger(),
                        )
                    }
                }

                //关闭按钮
                TextButton(
                    onClick = onCancel
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        color = Color.Unspecified,
                    )
                }
            }
        },

        //就算上面自定义了cancel样式，也得传onCancel，因为onDismiss要用
        onCancel = onCancel,

        //仅有条目时才可复制
        okBtnEnabled = selectedItems.isNotEmpty(),
        okBtnText = stringResource(R.string.copy),
    ) {  //点击拷贝按钮的回调
        onCopy()
    }
}



/**
 * SelectedItemDialog2的简化版，SelectedItemDialog的增强版
 */
@Composable
fun <T> SelectedItemDialog3(
    title: String = stringResource(R.string.selected_str),
    selectedItems:List<T>,
    switchItemSelected:(T)->Unit = {},  //用switch而不是单纯的remove是为了日后可实现撤销删除方便，只要再把条目传给switchItemSelected函数，就能重新选中条目，但这样的话，恢复后的条目会在列表末尾，若想回到原位呢？难道删除后做整个列表的备份？或者这个函数改成能指定索引的？

    //自定义text
    text:@Composable RowScope.(T) -> Unit,
    textFormatterForCopy:(T)->String,

    // more customizable
    customText:@Composable (BoxScope.(T) -> Unit)? = null,
    customTrailIcon:@Composable (BoxScope.(T) -> Unit)? = null,

    textPadding: PaddingValues = PaddingValues(start = 5.dp, end = trailingIconSize),
    //清空条目列表
    clearAll:()->Unit,

    //此函数应该仅关闭弹窗，不要执行多余操作
    closeDialog:()->Unit,

    // cancel可能包含比closeDialog更多的操作
    onCancel:()->Unit = closeDialog
) {
    val clipboardManager = LocalClipboardManager.current
    val activityContext = LocalContext.current

    SelectedItemDialog2(
        selectedItems = selectedItems,
        title = title,
        text = {
            if(customText != null) {
                customText(it)
            }else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(textPadding).align(Alignment.CenterStart).horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MySelectionContainer {
                        text(it)
                    }
                }
            }
        },
        trailIcon = {
            if(customTrailIcon != null) {
                customTrailIcon(it)
            }else {
                Row(
                    modifier = Modifier.size(trailingIconSize).align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { switchItemSelected(it) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = stringResource(R.string.trash_bin_icon_for_delete_item)
                        )
                    }
                }
            }
        },
        clearAll = clearAll,
        closeDialog = closeDialog,
        onCancel = onCancel,
        onCopy = {
            closeDialog()

            doJobThenOffLoading {
                val lb = Cons.lineBreak
                val sb = StringBuilder()
                selectedItems.forEachBetter {
                    sb.append(textFormatterForCopy(it)).append(lb)
                }

                clipboardManager.setText(AnnotatedString(sb.removeSuffix(lb).toString()))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
    )
}
