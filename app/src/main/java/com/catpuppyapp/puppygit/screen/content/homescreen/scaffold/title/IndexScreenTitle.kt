package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.IconOfRepoState
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.defaultTitleDoubleClick
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndexScreenTitle(
    curRepo: CustomStateSaveable<RepoEntity>,
    repoState: MutableIntState,
    scope: CoroutineScope,
    changeListPageItemListState: LazyListState,
    lastPosition: MutableState<Int>,
) {
//    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            curRepo = curRepo.value,
            showTitleInfoDialog = showTitleInfoDialog,
            prependContent = {
                Text(stringResource(R.string.comparing_label)+": "+ Libgit2Helper.getLeftToRightFullHash(Cons.git_HeadCommitHash, Cons.git_IndexCommitHash))
            }
        )
    }

    //设置仓库状态，主要是为了显示merge
    val repoStateText = rememberSaveable(repoState.intValue) { mutableStateOf(Libgit2Helper.getRepoStateText(repoState.intValue, activityContext)) }



    val getTitleColor = {
//        UIHelper.getChangeListTitleColor(repoState.intValue)
        Color.Unspecified
    }

    Column(modifier = Modifier
        .combinedClickable(
            onDoubleClick = {
                defaultTitleDoubleClick(scope, changeListPageItemListState, lastPosition)
            },
            onLongClick = null
//            {  //长按显示仓库名
////                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//            }
        ) { // onClick
            showTitleInfoDialog.value = true
        }
        .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
    ) {
        ScrollableRow {
            IconOfRepoState(repoState.intValue)

            Text(
                text = curRepo.value.repoName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MyStyleKt.Title.firstLineFontSize,
                color = getTitleColor()
            )
        }
        ScrollableRow  {
            //"[Index]|Merging" or "[Index]"
            Text(text = "["+stringResource(id = R.string.index)+"]" + (if(repoStateText.value.isNotBlank()) " | ${repoStateText.value}" else ""),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MyStyleKt.Title.secondLineFontSize,
                color = getTitleColor()
            )
        }

    }
}
