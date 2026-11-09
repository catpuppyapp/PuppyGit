package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.IconOfItem
import com.catpuppyapp.puppygit.compose.ListItemRow
import com.catpuppyapp.puppygit.compose.ListItemSpacer
import com.catpuppyapp.puppygit.compose.ListItemToggleButton
import com.catpuppyapp.puppygit.compose.ListItemTrailingIconRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.UIHelper


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangeListItem(
    item: StatusTypeEntrySaver,
//    selectedItemList:SnapshotStateList<StatusTypeEntrySaver>,
    isFileSelectionMode: MutableState<Boolean>,
//    filesPageAddFileToSelectedListIfAbsentElseRemove: (StatusTypeEntrySaver) -> Unit,
    menuKeyTextList: List<String>,
    menuKeyActList: List<(StatusTypeEntrySaver)->Unit>,
    menuKeyEnableList: List<(StatusTypeEntrySaver)->Boolean>,
    menuKeyVisibleList: List<(StatusTypeEntrySaver)->Boolean> = listOf(),
    fromTo:String,
    //此参数用来确认是否diff to local，因为from为tree to tree时有可能和local diff也可能不是，所以无法单凭from to 判断
    isDiffToLocal:Boolean,  // fromTo are treeToTree or indexToWorkdir all maybe diff to local, but tree to tree maybe is not diff to local, so make sure is diff to local or not, by this param
    lastClickedItemKey:MutableState<String>,
    switchItemSelected:(StatusTypeEntrySaver)->Unit,
    isItemInSelected:(StatusTypeEntrySaver)->Boolean,
//    treeOid1Str:String,
//    treeOid2Str:String,
    onLongClick:(StatusTypeEntrySaver)->Unit,
    onClick:(StatusTypeEntrySaver) -> Unit
){
//    val navController = AppModel.navController
//    val appContext = AppModel.appContext
//    val haptic = AppModel.haptic

    val activityContext = LocalContext.current
    val itemIsDir = item.itemType == Cons.gitItemTypeDir || item.itemType == Cons.gitItemTypeSubmodule


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = {
                    lastClickedItemKey.value = item.getItemKey()

                    onLongClick(item)
                }
            ){  //onClick
                lastClickedItemKey.value = item.getItemKey()

                onClick(item)
            }
            .then(
                //如果条目被选中，切换高亮颜色
                if (isItemInSelected(item)) Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer

                    //then 里传 Modifier不会有任何副作用，还是当前的Modifier(即调用者自己：this)，相当于什么都没改，后面可继续链式调用其他方法
                ) else if(item.getItemKey() == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
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

//                enabled = fromTo!=Cons.gitDiffFromTreeToTree,  //diff提交时，禁用点击图标启动长按模式，按钮会变灰色，太难看了，弃用，改成点击后判断是否需要执行操作了，若不需要直接返回
                checked = isItemInSelected(item),
                onCheckedChange = cc@{
//                    if(fromTo!=Cons.gitDiffFromTreeToTree) {
//                    }

                    // tree to tree页面且底栏功能未测试通过，直接返回，不显示底栏
//                    if(fromTo == Cons.gitDiffFromTreeToTree && !proFeatureEnabled(treeToTreeBottomBarActAtLeastOneTestPassed())) {
//                        return@cc
//                    }

                    switchItemSelected(item)

                }
            ) {
                IconOfItem(
                    fileName = item.fileName,
                    filePath = item.canonicalPath,
                    context = activityContext,
                    //最后的else返回null并不是不显示图片，组件内部会判断若为null则使用mime对应的图标
                    defaultIconWhenLoadFailed = if(item.changeType == Cons.gitStatusDeleted) ImageVector.vectorResource(R.drawable.outline_unknown_document_24) else if(itemIsDir) Icons.Filled.Folder else null,
                    contentDescription = if(item.changeType == Cons.gitStatusDeleted) null else if(itemIsDir) stringResource(R.string.folder_icon) else stringResource(R.string.file_icon),
                )
            }

            ListItemSpacer()

            Column {
                val changeTypeColor = UIHelper.getChangeTypeColor(item.changeType ?: "")

                Text(text = item.fileName, fontSize = 20.sp, color = changeTypeColor)

                val secondLineFontSize = 12.sp
                Text(text = item.getChangeListItemSecondLineText(isDiffToLocal), fontSize = secondLineFontSize, color = changeTypeColor)

                val parentPath = item.getParentDirStr()
                if(parentPath.isNotEmpty()) {
                    Text(text = parentPath, fontSize = secondLineFontSize, color = changeTypeColor)
                }
            }

        }

        //每个条目都有自己的菜单项，这样有点费资源哈，不过实现起来最简单，如果只用一个菜单项也行，但难点在于把菜单项定位到点菜单按钮的地方
        val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }  // typo: "Expend" should be "Expand"

        ListItemTrailingIconRow {
            //三点图标
            IconButton(onClick = { dropDownMenuExpandState.value = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.file_or_folder_menu)
                )
            }

            //菜单项，open/openAs/showInFiles之类的选项
            DropdownMenu(
                expanded = dropDownMenuExpandState.value,
                onDismissRequest = { dropDownMenuExpandState.value = false }
            ) {
                for((idx,v) in menuKeyTextList.withIndex()) {
                    if(menuKeyVisibleList.isNotEmpty() && !menuKeyVisibleList[idx](item)) {
                        continue
                    }

                    DropdownMenuItem(
                        enabled = menuKeyEnableList[idx](item),
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


