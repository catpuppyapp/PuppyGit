package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.InLineIcon
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.git.SubmoduleDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.listItemPadding


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubmoduleItem(
    thisObj:SubmoduleDto,
    lastClickedItemKey: MutableState<String>,

    isItemInSelected:(SubmoduleDto) -> Boolean,
    onLongClick:(SubmoduleDto)->Unit,
    onClick:(SubmoduleDto)->Unit
) {
    val activityContext = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisObj.name

                    onClick(thisObj)
                },
                onLongClick = {
                    lastClickedItemKey.value = thisObj.name

                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    onLongClick(thisObj)
                },
            )
            .then(
                //如果条目被选中，切换高亮颜色
                if (isItemInSelected(thisObj)) Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer

                    //then 里传 Modifier不会有任何副作用，还是当前的Modifier(即调用者自己：this)，相当于什么都没改，后面可继续链式调用其他方法
                ) else if(thisObj.name == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                } else Modifier
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding；
            // padding要放到背景颜色后面，不然padding的区域不会着色
            .listItemPadding()




    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Outlined.GridView,
                tooltipText = stringResource(R.string.submodule)
            )

//            Text(text = stringResource(R.string.name) +": ")

            ScrollableRow {
                Text(text = thisObj.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Outlined.Cloud,
                tooltipText = stringResource(R.string.url)
            )

//            Text(text = stringResource(R.string.url) +": ")

            ScrollableRow {
                Text(text = thisObj.remoteUrl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Outlined.Folder,
                tooltipText = stringResource(R.string.path)
            )

//            Text(text = stringResource(R.string.path) +": ")
            ScrollableRow {
                Text(text = thisObj.relativePathUnderParent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Filled.Commit,
                tooltipText = stringResource(R.string.target)
            )

//            Text(text = stringResource(R.string.target) +": ")

            Text(text = thisObj.getShortTargetHashCached(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = defaultFontWeight
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Outlined.LocationOn,
                tooltipText = stringResource(R.string.location)
            )

//            Text(text = stringResource(R.string.location) +": ")

            ScrollableRow {
                Text(text = thisObj.location.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            InLineIcon(
                icon = Icons.Filled.Info,
                tooltipText = stringResource(R.string.status)
            )

//            Text(text = stringResource(R.string.status) +": ")

            ScrollableRow {
                Text(text = thisObj.getStatus(activityContext),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight,
                    color = thisObj.getStatusColor()
                )
            }
        }

        if(thisObj.hasOther()) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){

                InLineIcon(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    tooltipText = stringResource(R.string.other)
                )

//                Text(text = stringResource(R.string.other) +": ")
                ScrollableRow {
                    Text(text = thisObj.getOther(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight,
                    )
                }
            }
        }

     }
}
