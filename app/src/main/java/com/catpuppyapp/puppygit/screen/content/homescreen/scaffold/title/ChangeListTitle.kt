package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.puppygit.compose.DropDownMenuItemText
import com.catpuppyapp.puppygit.compose.IconOfRepoState
import com.catpuppyapp.puppygit.compose.RepoInfoDialog
import com.catpuppyapp.puppygit.compose.TitleDropDownMenu
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.StateRequestType
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.getRequestDataByState
import com.catpuppyapp.puppygit.utils.state.CustomStateListSaveable
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository
import kotlinx.coroutines.CoroutineScope


@Composable
fun ChangeListTitle(
    changeListCurRepo: CustomStateSaveable<RepoEntity>,
    dropDownMenuItemOnClick: (RepoEntity) -> Unit,
    repoState: MutableIntState,
    isSelectionMode: MutableState<Boolean>,
    listState: LazyListState,
    scope: CoroutineScope,
    enableAction:Boolean,
    repoList:CustomStateListSaveable<RepoEntity>,
    needReQueryRepoList:MutableState<String>,
    goToChangeListPage:(RepoEntity)->Unit,
) {

    // 测试下，不禁用点击title是否会出错，不出错就永远启用，不然有时候一点某个仓库文件很多，加载等半天，然后我想切换到别的仓库也切换不了，恶心
    //如果有bug，直接注释此行即可，不需要改其他地方
    val enableAction = true


//    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current

    val inDarkTheme = Theme.inDarkTheme

    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }

    val showTitleInfoDialog = rememberSaveable { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(
            curRepo = changeListCurRepo.value,
            showTitleInfoDialog = showTitleInfoDialog,
            prependContent = {
                Text(stringResource(R.string.comparing_label)+": "+ Libgit2Helper.getLeftToRightFullHash(Cons.git_IndexCommitHash, Cons.git_LocalWorktreeCommitHash))
            }
        )
    }


    //设置仓库状态，主要是为了显示merge
    val repoStateText = rememberSaveable(repoState.intValue) { mutableStateOf(Libgit2Helper.getRepoStateText(repoState.intValue, activityContext)) }




    val switchDropDownMenu = {
        if(dropDownMenuExpandState.value) {  // need close
            dropDownMenuExpandState.value = false
        } else {  // need open
            changeStateTriggerRefreshPage(needReQueryRepoList)
            dropDownMenuExpandState.value = true
        }
    }

    val getTitleColor = {
//                UIHelper.getChangeListTitleColor(repoState.intValue)
        UIHelper.getTitleColor(enabled = enableAction)
    }

    if(repoList.value.isEmpty()) {
        Text(
            text = stringResource(id = R.string.changelist),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }else {
        TitleDropDownMenu(
            dropDownMenuExpandState = dropDownMenuExpandState,
            curSelectedItem = changeListCurRepo.value,
            itemList = repoList.value.toList(),
            titleClickEnabled = enableAction,
            switchDropDownMenuShowHide = switchDropDownMenu,
            titleFirstLine = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconOfRepoState(repoState.intValue)


                    Text(
                        text = changeListCurRepo.value.repoName,
//                    style=MyStyleKt.clickableText.style,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = MyStyleKt.Title.firstLineFontSize,
                        //如果是在合并（或者有冲突），仓库名变成红色，否则变成默认颜色
                        color = getTitleColor()
                    )
                }
            },
            titleSecondLine = {
                Text(
                    //  判断仓库是否处于detached，然后显示在这里(例如： "abc1234(detached)" )
                    // "main|StateT" or "main", eg, when merging show: "main|Merging", when 仓库状态正常时 show: "main"；如果是detached HEAD状态，则显示“提交号(Detached)|状态“，例如：abc2344(Detached) 或 abc2344(Detached)|Merging
                    text = (if(dbIntToBool(changeListCurRepo.value.isDetached)) Libgit2Helper.genDetachedText(changeListCurRepo.value.lastCommitHashShort) else Libgit2Helper.genLocalBranchAndUpstreamText(changeListCurRepo.value.branch, changeListCurRepo.value.upstreamBranch)) + (if(repoStateText.value.isNotBlank()) " | ${repoStateText.value}" else ""),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = MyStyleKt.Title.secondLineFontSize,
                    color = getTitleColor()
                )
            },
            titleRightIcon = {
                Icon(
                    imageVector = if (dropDownMenuExpandState.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft,
                    contentDescription = stringResource(R.string.switch_repo),
                    tint = if (enableAction) LocalContentColor.current else UIHelper.getDisableBtnColor(inDarkTheme)
                )
            },
            isItemSelected = { it.id == changeListCurRepo.value.id },
            menuItem = { r, selected ->
                DropDownMenuItemText(
                    text1 = r.repoName,

                    //仓库状态若不是NONE，则显示 (其实等于 仓库状态等于null的仓库并不会显示在这里，查询的时候就过滤掉了，不过为了逻辑完整，还是保留null判断
                    text2 = r.gitRepoState.let { if(it == null) stringResource(R.string.invalid) else if(it != Repository.StateT.NONE) it.toString() else "" },

                )
            },
            titleOnLongClick = { showTitleInfoDialog.value = true },
            itemOnClick = { r ->
                //如果点击其他仓库(切换仓库)，则退出选择模式
                if(changeListCurRepo.value.id != r.id) {
                    isSelectionMode.value=false
                }

                //这个函数里会清空选中条目列表，和上面的退出选择模式互补了
                dropDownMenuItemOnClick(r)
            },
        )
    }

    LaunchedEffect(needReQueryRepoList.value) {
        try {
            doJobThenOffLoading {
                val repoDb = AppModel.dbContainer.repoRepository
                val readyRepoListFromDb = repoDb.getReadyRepoList(requireSyncRepoInfoWithGit = false)
                repoList.value.clear()
                repoList.value.addAll(readyRepoListFromDb)

                // 如果请求跳转到目标仓库，跳转
                val (requestType, targetRepoFullPath) = getRequestDataByState<String?>(needReQueryRepoList.value)
                if(requestType == StateRequestType.jumpAfterImport && targetRepoFullPath.let { it != null && it.isNotBlank() }) {
                    readyRepoListFromDb.find { it.fullSavePath == targetRepoFullPath }?.let {
                        goToChangeListPage(it)
                    }
                }

            }
        } catch (cancel: Exception) {

        }
    }
}
