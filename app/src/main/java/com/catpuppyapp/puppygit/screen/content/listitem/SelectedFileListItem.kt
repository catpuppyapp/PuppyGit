package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.compose.SelectedItemDialog3
import com.catpuppyapp.puppygit.compose.SizeIcon
import com.catpuppyapp.puppygit.compose.TwoLineTextsAndIcons
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun <T> SelectedFileItemsDialog(
    list: List<T>,
    itemName: (T) -> String,
    itemPath: (T) -> String,
    itemIsDir: (T) -> Boolean,
    showFileIcon: Boolean,
    fileIconOnClick: (T) -> Unit,
    removeItem: (T) -> Unit,
    clearAll:() -> Unit,
    closeDialog:() -> Unit,
    title: String = stringResource(R.string.selected_str),
    textFormatterForCopy:(T) -> String = { itemName(it) + "\n" + itemPath(it) + "\n\n" },
) {
    val trailIconSize = remember { MyStyleKt.trailIconSize }
    val splitSpacerWidth = remember { MyStyleKt.trailIconSplitSpacerWidth }

    SelectedItemDialog3(
        title = title,
        selectedItems = list,
        text = {},
        customText = {
            TwoLineTextsAndIcons(
                text1 = itemName(it),
                text2 = itemPath(it),
                trailIconWidth = if(showFileIcon) trailIconSize * 2 + splitSpacerWidth else trailIconSize,
                trailIcons = { containerModifier ->
                    Row(
                        modifier = containerModifier
                            .fillMaxWidth()
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if(showFileIcon) {
                            SizeIcon(
                                size = trailIconSize,
                                modifier = Modifier.clickable {
                                    fileIconOnClick(it)
                                },
                                imageVector = if(itemIsDir(it)) Icons.Outlined.Folder else Icons.AutoMirrored.Outlined.InsertDriveFile,
                                contentDescription = if(itemIsDir(it)) stringResource(R.string.folder) else stringResource(R.string.file)
                            )

                            Spacer(modifier = Modifier.width(splitSpacerWidth))
                        }

                        SizeIcon(
                            size = trailIconSize,
                            modifier = Modifier.clickable {
                                removeItem(it)
                            },
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            )
        },
        customTrailIcon = {},
        textFormatterForCopy = textFormatterForCopy,
        switchItemSelected = removeItem,
        clearAll = clearAll,
        closeDialog = closeDialog,
    )

}
