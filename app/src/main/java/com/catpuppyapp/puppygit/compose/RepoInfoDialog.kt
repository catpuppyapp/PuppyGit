package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.git.CommitDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.dbIntToBool

@Composable
fun RepoInfoDialog(
    curRepo: RepoEntity,
    showTitleInfoDialog: MutableState<Boolean>,
    prependContent:@Composable (()->Unit)? = null,
    appendContent:@Composable (()->Unit)? = null
) {
    val context = LocalContext.current

    InfoDialog(showTitleInfoDialog) {
        if(prependContent != null) {
            prependContent()
            RepoInfoDialogItemSpacer()
        }

        Row {
            Text(stringResource(id = R.string.repo) + ": " + curRepo.repoName)
        }
        RepoInfoDialogItemSpacer()

        if (dbIntToBool(curRepo.isDetached)) {
            Row {
                Text(stringResource(R.string.branch) + ": " + Cons.gitDetachedHead)
            }
        } else {
            Row {
                Text(stringResource(R.string.branch) + ": " + (curRepo.branch))
            }

            if (curRepo.upstreamBranch.isNotBlank()) {
                RepoInfoDialogItemSpacer()

                Row {
                    Text(stringResource(R.string.upstream) + ": " + (curRepo.upstreamBranch))
                }
            }
        }

        RepoInfoDialogItemSpacer()
        Row {
            Text(stringResource(R.string.repo_state) + ": " + curRepo.getRepoStateStr(context))
        }

        if(appendContent != null) {
            RepoInfoDialogItemSpacer()
            appendContent()
        }

    }
}

@Composable
fun RepoInfoDialogItemSpacer() {
    Spacer(Modifier.height(10.dp))
}

@Composable
fun ShortCommitInfo(title:String, name:String, commitDto:CommitDto, msgMaxLines:Int=6) {
    if(title.isNotBlank()) {
        Row { InDialogTitle(title) }
    }

    RepoInfoDialogItemSpacer()

    // name用来显示条目名字，例如用户在比较引用 origin/a 和 main 的差异，这两个就是name，name一般是用户输入的，比如在diff弹窗输入的引用名或hash，
    // 如果不加这个参数，解析之后会变成commit hash，用户可能不知道在比较什么
    Row { Text(stringResource(R.string.name)+": "+name) }


    if(commitDto.oidStr.let { it != Cons.git_AllZeroOidStr && it != Cons.git_LocalWorktreeCommitHash && it != Cons.git_IndexCommitHash }) {
        RepoInfoDialogItemSpacer()
        Row { Text(stringResource(R.string.hash)+": "+commitDto.oidStr) }
        RepoInfoDialogItemSpacer()
        Row { Text(stringResource(R.string.author)+": "+ Libgit2Helper.getFormattedUsernameAndEmail(commitDto.author, commitDto.email)) }
        RepoInfoDialogItemSpacer()
        Row { Text(stringResource(R.string.committer)+": "+ Libgit2Helper.getFormattedUsernameAndEmail(commitDto.committerUsername, commitDto.committerEmail)) }
        RepoInfoDialogItemSpacer()
        Row { Text(stringResource(R.string.date)+": "+commitDto.dateTime) }
        RepoInfoDialogItemSpacer()
        Row { Text(stringResource(R.string.msg)+": "+commitDto.msg, maxLines = msgMaxLines, overflow = TextOverflow.Ellipsis) }
    }
}

@Composable
fun CompareInfo(leftName:String, leftCommitDto: CommitDto, rightName:String, rightCommitDto: CommitDto) {
    Row {
        Text(
            stringResource(R.string.comparing_label) + ": " +Libgit2Helper.getLeftToRightDiffCommitsText(leftName, rightName, false)
        )
    }

    RepoInfoDialogItemSpacer()

    MyHorizontalDivider()

    RepoInfoDialogItemSpacer()
    ShortCommitInfo(stringResource(R.string.left), leftName, leftCommitDto)
    RepoInfoDialogItemSpacer()

    MyHorizontalDivider()

    RepoInfoDialogItemSpacer()
    ShortCommitInfo(stringResource(R.string.right), rightName, rightCommitDto)
}

/**
 * 弹窗内部的标题文本，一般比普通文本大些
 */
@Composable
fun InDialogTitle(text:String) {
    Text(text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
}
