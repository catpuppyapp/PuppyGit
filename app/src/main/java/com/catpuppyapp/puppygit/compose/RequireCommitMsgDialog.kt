package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.state.mutableCustomBoxOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Repository

private const val TAG = "RequireCommitMsgDialog"


@Composable
fun RequireCommitMsgDialog(
    stateKeyTag:String,

    curRepo:RepoEntity,
    repoPath:String,
    repoState:Int,
    overwriteAuthor:MutableState<Boolean>,
    amend:MutableState<Boolean>,
    commitMsg: CustomStateSaveable<TextFieldValue>,
    indexIsEmptyForCommitDialog:MutableState<Boolean>,
    showPush:Boolean,
    showSync:Boolean,
    commitBtnText:String = stringResource(R.string.commit),
    onOk: (curRepo: RepoEntity, msg:String, requirePush:Boolean, requireSync:Boolean) -> Unit,
    onCancel: (curRepo: RepoEntity) -> Unit,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val activityContext = LocalContext.current

    val repoStateIsRebase= repoState == Repository.StateT.REBASE_MERGE.bit

    val repoStateIsCherrypick = repoState == Repository.StateT.CHERRYPICK.bit

    val settings = remember { SettingsUtil.getSettingsSnapshot() }


    //勾选amend时用此变量替代commitMsg
    //由于判断amend是否勾选的布尔值在外部，
    // 所以，有可能存在显示弹窗前就设amend为真的情况，
    // 那样就会变成amend勾选但amendMsg为空的情况，
    // 这时，可点"加载原始提交信息" 或 切换一下amend勾选状态 来载入上个提交信息
    val amendMsg = mutableCustomStateOf(stateKeyTag, "amendMsg") { TextFieldValue("") }
    val getCommitMsg = {
        if(amend.value) amendMsg.value.text else commitMsg.value.text
    }

    //确保amend msg只在初次切换amend状态时获取一次
    val amendMsgAlreadySetOnce = mutableCustomBoxOf(stateKeyTag, "amendMsgAlreadySetOnce") { false }


    val view = LocalView.current
    val density = LocalDensity.current

    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    //indicate keyboard covered component
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    // which component expect adjust heghit or padding when softkeyboard shown
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    // the padding value when softkeyboard shown
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }

    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        paddingAdjustValue = 180.dp,
        skipCondition = { false }
    )


    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.commit_message))
        },
        text = {
            ScrollableColumn {
                if(repoState==Repository.StateT.NONE.bit && amend.value.not() && indexIsEmptyForCommitDialog.value) {
                    MySelectionContainer {
                        Row(modifier = Modifier.padding(5.dp)) {
                            Text(text = stringResource(R.string.warn_index_is_empty_will_create_a_empty_commit), color = MyStyleKt.TextColor.danger())
                        }
                    }
                }

                TextField(
                    maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                    modifier = Modifier.fillMaxWidth()
                        .onGloballyPositioned { layoutCoordinates ->
//                                println("layoutCoordinates.size.height:${layoutCoordinates.size.height}")
                            // 获取组件的高度
                            // unit is px ( i am not very sure)
                            componentHeight.intValue = layoutCoordinates.size.height
                        }
                        .then(
                            if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                        )
                    ,

                    value = if(amend.value) amendMsg.value else commitMsg.value,
                    onValueChange = {
                        if(amend.value) {
                            amendMsg.value = it
                        }else {
                            commitMsg.value = it
                        }
                    },
                    label = {
                        Text(stringResource(R.string.commit_message))
                    },
                    placeholder = {
                        Text(stringResource(R.string.input_your_commit_message))
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                //提示提交信息可留空的文案
                MySelectionContainer {
                    Column {
                        //正常来说这几个条件不会同时为真
                        if(repoStateIsRebase || repoStateIsCherrypick || amend.value) {
                            MultiLineClickableText(stringResource(R.string.leave_msg_empty_will_use_origin_commit_s_msg)) {
                                Repository.open(repoPath).use { repo ->
                                    val oldMsg = if (repoStateIsRebase) Libgit2Helper.rebaseGetCurCommitMsg(repo) else if(repoStateIsCherrypick) Libgit2Helper.getCherryPickHeadCommitMsg(repo) else Libgit2Helper.getHeadCommitMsg(repo)

                                    if(amend.value) {
                                        amendMsg.value = TextFieldValue(oldMsg)
                                    }else {
                                        commitMsg.value = TextFieldValue(oldMsg)
                                    }
                                }
                            }
                        }else {
                            MultiLineClickableText(stringResource(R.string.you_can_leave_msg_empty_will_auto_gen_one)) {
                                Repository.open(repoPath).use { repo ->
                                    commitMsg.value = TextFieldValue(Libgit2Helper.genCommitMsgNoFault(repo, itemList = null, settings.commitMsgTemplate))
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                    }

                }

                //repo状态正常才显示amend，rebase和merge时不会显示
                if(repoState == Repository.StateT.NONE.bit) {
                    MyCheckBox(text = stringResource(R.string.amend), value = amend, onValueChange = { amendOn ->
                        //如果新状态为启用amend 且 是初次启用amend 且 当前amendMsg为空，则 设置提交信息为上个提交(HEAD)的信息
                        if(amendOn && amendMsgAlreadySetOnce.value.not() && amendMsg.value.text.isEmpty()) {
                            amendMsgAlreadySetOnce.value = true

                            runCatching {
                                Repository.open(repoPath).use { repo ->
                                    amendMsg.value = TextFieldValue(Libgit2Helper.getHeadCommitMsg(repo))
                                }
                            }
                        }

                        //更新 checkbox 状态
                        amend.value = amendOn
                    })
                }


                // amend或rebase时可覆盖旧commit的作者信息，按钮一样，但含义不同，普通状态下commit覆盖的是上一个commit的信息，rebase状态下覆盖的是被pick的commit的信息
                if(repoStateIsRebase || repoStateIsCherrypick || amend.value) {
                    MyCheckBox(text = stringResource(R.string.overwrite_author), value = overwriteAuthor)
                }

                //这个放外面，和checkbox分开，这样如果overwriteAuthor状态不该为true的时候为true，
                // 就能看到这段文字，就能发现有问题了，不然若状态有误，有可能在用户不知情的情况下覆盖提交作者
                if(overwriteAuthor.value) {
                    MySelectionContainer {
                        DefaultPaddingText(text = stringResource(R.string.will_use_your_username_and_email_overwrite_original_commits_author_info))
                    }
                }

            }

        },
        onDismissRequest = {
            onCancel(curRepo)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val requirePush = false
                    val requireSync = false
                    onOk(curRepo, getCommitMsg(), requirePush, requireSync)
                },
                enabled = true
            ) {
                Text(commitBtnText)
            }
        },
        dismissButton = {
            ScrollableRow {
                if(showSync) {
                    TextButton(
                        onClick = {
                            val requirePush = false
                            val requireSync = true
                            onOk(curRepo, getCommitMsg(), requirePush, requireSync)
                        }
                    ) {
                        Text(stringResource(id = R.string.sync))
                    }
                }


                if(showPush) {
                    TextButton(
                        onClick = {
                            val requirePush = true
                            val requireSync = false
                            onOk(curRepo, getCommitMsg(), requirePush, requireSync)
                        }
                    ) {
                        Text(stringResource(R.string.push))
                    }
                }

                TextButton(
                    onClick = {
                        onCancel(curRepo)
                    }
                ) {
                    Text(stringResource(R.string.cancel), color = MyStyleKt.TextColor.danger())
                }
            }

        }
    )
}

