package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.IconOfItem
import com.catpuppyapp.puppygit.compose.ListItemRow
import com.catpuppyapp.puppygit.compose.ListItemSpacer
import com.catpuppyapp.puppygit.compose.ListItemToggleButton
import com.catpuppyapp.puppygit.compose.ListItemTrailingIconRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.dto.FileItemDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.getParentPathEndsWithSeparator


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    /**
     * 若不为空字符串，将针对所有条目以此path为基础显示其相对路径，否则不显示。
     * 应用场景：在递归搜索时使用此变量
    */
    fullPathOfTopNoEndSlash:String,

    item: FileItemDto,
    lastPathByPressBack:MutableState<String>,
    menuKeyTextList: List<String>,
    menuKeyActList: List<(FileItemDto)->Unit>,
    iconOnClick:()->Unit,
    isItemInSelected:(FileItemDto)->Boolean,
    itemOnLongClick:(FileItemDto)->Unit,
    itemOnClick:(FileItemDto)->Unit,
){
    val activityContext = LocalContext.current

    val inDarkTheme = Theme.inDarkTheme
    val alpha = 0.6f
    val iconColor = if(item.isHidden) LocalContentColor.current.copy(alpha = alpha) else LocalContentColor.current
    val fontColor = if(item.isHidden) {if(inDarkTheme) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha)} else Color.Unspecified


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    lastPathByPressBack.value = item.fullPath

                    itemOnClick(item)
                },
                onLongClick = {
                    lastPathByPressBack.value = item.fullPath

                    itemOnLongClick(item)
                }
            )
            .then(
                if (isItemInSelected(item)) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer
                    )
                }else if(lastPathByPressBack.value == item.fullPath){  // show light background for last clicked path
                    Modifier.background(
                        UIHelper.getLastClickedColor()
                    )
                } else Modifier
            )
            ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ListItemRow{
            //在左侧加个复选框，会影响布局，有缺陷，别用了
//                                    if(isFileSelectionMode.value) {
//                                        IconToggleButton(checked = JSONObject(selFilePathListJsonObjStr.value).has(item.name), onCheckedChange = {
//                                            addIfAbsentElseRemove(item)
//                                        }) {
//                                            Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = stringResource(R.string.file_checked_indicator_icon))
//                                        }
//                                    }

            ListItemToggleButton(

//                enabled = fromTo!=Cons.gitDiffFromTreeToTree,  //diff提交时，禁用点击图标启动长按模式，按钮会变灰色，太难看了，弃用
                checked = isItemInSelected(item),
                onCheckedChange = {
                    lastPathByPressBack.value = item.fullPath

                    iconOnClick()
                },
            ) {
                IconOfItem(
                    fileName = item.name,
                    filePath = item.fullPath,
                    context = activityContext,
                    contentDescription = if(item.isDir) stringResource(R.string.folder_icon) else stringResource(R.string.file_icon),
                    iconColor = iconColor,
                )
            }

            ListItemSpacer()

            Column {
                Row {
                    Text(
                            text = item.name,
                            fontSize = 20.sp,
                            color = fontColor
                    )
                }

                Row{
                    Text(item.getShortDesc(), fontSize = 12.sp, color = fontColor)
                }

                if(fullPathOfTopNoEndSlash.isNotBlank()) {
                    //末尾必须分别移除path和/，若合并移除"path/"，当path和当前路径相同时会漏
                    val relativePath = item.fullPath.removePrefix(fullPathOfTopNoEndSlash).removePrefix(Cons.slash)
                        .let { getParentPathEndsWithSeparator(it, trueWhenNoParentReturnEmpty = true) }

                    if(relativePath.isNotEmpty()) {
                        Row {
                            Text(text = relativePath, fontSize = 12.sp, color = fontColor)
                        }
                    }
                }

            }

        }
        //每个条目都有自己的菜单项，这样有点费资源哈，不过实现起来最简单，如果只用一个菜单项也行，但难点在于把菜单项定位到点菜单按钮的地方
        val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }

        ListItemTrailingIconRow{
            IconButton(onClick = {
                lastPathByPressBack.value = item.fullPath

                dropDownMenuExpandState.value = true
            }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.file_or_folder_menu)
                )
            }
            DropdownMenu(
                expanded = dropDownMenuExpandState.value,
                onDismissRequest = { dropDownMenuExpandState.value = false }
            ) {
                for ((idx,v) in menuKeyTextList.withIndex()) {
                    //忽略空白选项，这样未启用的feature就可直接用空白替代了，方便
                    if(v.isBlank()) {
                        continue
                    }

                    DropdownMenuItem(
                        text = { Text(v) },
                        onClick = {
                            //调用onClick()
                            menuKeyActList[idx](item)
                            //关闭菜单
                            dropDownMenuExpandState.value = false
                        }
                    )

                }
            }
        }

    }
}


