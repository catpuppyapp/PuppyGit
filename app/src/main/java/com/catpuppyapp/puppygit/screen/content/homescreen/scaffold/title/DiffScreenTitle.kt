package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.ReadOnlyIcon
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.git.DiffableItem
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiffScreenTitle(
    isMultiMode:Boolean,
    listState: LazyListState,
    scope: CoroutineScope,
    request:MutableState<String>,
    readOnly:Boolean,
    lastPosition:MutableState<Int>,
    curItem: DiffableItem,
) {

    val fileName = curItem.fileName
    val changeType = curItem.diffItemSaver.changeType
    val relativePath = curItem.relativePath

    if(relativePath.isNotEmpty()) {
        Column(
            modifier = Modifier.combinedClickable(
                            //double click go to top of list
                            onDoubleClick = {
                                defaultTitleDoubleClick(scope, listState, lastPosition)
                            },
                        ) {
                            // 多选模式下点击标题栏返回当前条目顶部
                            //对于多选模式来说，点击顶栏回到当前条目标题，比显示详情有用，至于显示详情，可通过点击当前条目的标题栏文件名启用
                            request.value = if(isMultiMode) {
                                PageRequest.goToCurItem
                            }else {
                                //show details , include file name and path
                                PageRequest.showDetails
                            }
                        }
                        .widthIn(min=MyStyleKt.Title.clickableTitleMinWidth)
        ) {

                val changeTypeColor = UIHelper.getChangeTypeColor(changeType)

                ScrollableRow {
                    if(readOnly) {
                        ReadOnlyIcon()
                    }

                    Text(
                        text = fileName,
                        fontSize = MyStyleKt.Title.firstLineFontSizeSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,  //可滚动条目永远不会over flow，所以这个在这其实没啥意义
                        color = changeTypeColor
                    )
                }

                ScrollableRow  {
                    Text(
                        text = curItem.getAnnotatedAddDeletedAndParentPathString(changeTypeColor),
                        fontSize = MyStyleKt.Title.secondLineFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
//                        color = changeTypeColor
                    )
                }
            }

    }else {
        Text(
            text = stringResource(id = R.string.diff_screen_default_title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
