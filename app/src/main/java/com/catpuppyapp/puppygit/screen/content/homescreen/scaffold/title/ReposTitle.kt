package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.replaceStringResList
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReposTitle(
    listState: LazyListState,
    scope:CoroutineScope,
    allRepoCount:Int,
    lastPosition:MutableState<Int>
) {
    Column (modifier = Modifier.combinedClickable(onDoubleClick = {
        defaultTitleDoubleClick(scope, listState, lastPosition)
    }) {
            // onClick
    }){
        ScrollableRow {
            Text(
                text = stringResource(id = R.string.repos),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if(allRepoCount > 0) {
            ScrollableRow {
                Text(
                    text= replaceStringResList(stringResource(R.string.count_n), listOf(""+allRepoCount)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = MyStyleKt.Title.secondLineFontSize
                )

            }
        }
    }

}
