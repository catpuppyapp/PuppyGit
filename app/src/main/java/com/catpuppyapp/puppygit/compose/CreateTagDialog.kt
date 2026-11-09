package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Repository


private const val TAG = "CreateTagDialog"

@Composable
fun CreateTagDialog(
    showDialog:MutableState<Boolean>,
    curRepo:RepoEntity,
    tagName:MutableState<String>,
    commitHashShortOrLong:MutableState<String>,

    // annotation tag所需字段
    annotate:MutableState<Boolean>,
    tagMsg:MutableState<String>,

    force:MutableState<Boolean>,  // will override if tagName already exists

    onOkDoneCallback:(newTagFullOidStr:String)->Unit
) {

    // softkeyboard show/hidden relate start

    val view = LocalView.current
    val density = LocalDensity.current

    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    //indicate keyboard covered component
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    // which component expect adjust heghit or padding when softkeyboard shown
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    // the padding value when softkeyboard shown
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }

    // softkeyboard show/hidden relate end



    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        //若在弹窗外部，则不显示弹窗时跳过监听；若在弹窗内部使用并且不显示弹窗时不执行此组件函数，则设为假即可，因为只要显示此弹窗就一定需要监听，因此skip应永远为假，不跳过。
        skipCondition = { false }
    )




    val activityContext = LocalContext.current

    val tagNameErrMsg = rememberSaveable { mutableStateOf("") }
    val commitHashShortOrLongErrMsg = rememberSaveable { mutableStateOf("") }
    val tagMsgErrMsg = rememberSaveable { mutableStateOf("") }
    val gitConfigUsername = rememberSaveable { mutableStateOf("") }
    val gitConfigEmail = rememberSaveable { mutableStateOf("") }

    val settings = remember { SettingsUtil.getSettingsSnapshot() }

    ConfirmDialog2(
        title = activityContext.getString(R.string.new_tag),
        requireShowTextCompose = true,
        textCompose = {
            //只能有一个节点，因为这个东西会在lambda后返回，而lambda只能有一个返回值，弄两个布局就乱了，和react组件只能有一个root div一个道理 。
            ScrollableColumn {
                Row(modifier = Modifier.padding(5.dp)) {
                    // spacer
                }
                TextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = tagName.value,
                    singleLine = true,
                    onValueChange = {
                        tagName.value = it
                        tagNameErrMsg.value=""
                    },
                    label = {
                        Text(stringResource(R.string.tag_name))
                    },

                    isError = tagNameErrMsg.value.isNotEmpty(),
                    supportingText = {
                        if(tagNameErrMsg.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = tagNameErrMsg.value,
                                color = MaterialTheme.colorScheme.error
                            )

                        }
                    },
                    trailingIcon = {
                        if(tagNameErrMsg.value.isNotEmpty()) {
                            Icon(imageVector= Icons.Filled.Error,
                                contentDescription="err icon",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                )
                Row(modifier = Modifier.padding(5.dp)) {
                    // spacer
                }
                TextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = commitHashShortOrLong.value,
                    singleLine = true,
                    onValueChange = {
                        commitHashShortOrLong.value = it
                        commitHashShortOrLongErrMsg.value=""
                    },
                    label = {
                        Text(stringResource(R.string.target))
                    },
                    placeholder = {
                        Text(stringResource(R.string.hash_branch_tag))
                    },

                    isError = commitHashShortOrLongErrMsg.value.isNotEmpty(),
                    supportingText = {
                        if(commitHashShortOrLongErrMsg.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = commitHashShortOrLongErrMsg.value,
                                color = MaterialTheme.colorScheme.error
                            )

                        }
                    },
                    trailingIcon = {
                        if(commitHashShortOrLongErrMsg.value.isNotEmpty()) {
                            Icon(imageVector= Icons.Filled.Error,
                                contentDescription="err icon",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                )

                MyCheckBox(text = stringResource(R.string.annotate), value = annotate)

                if(annotate.value) {
                    if(gitConfigUsername.value.isBlank() || gitConfigEmail.value.isBlank()) {  //未设置用户名和邮箱
                        MySelectionContainer {
                            DefaultPaddingText(
                                text = stringResource(R.string.err_must_set_username_and_email_before_create_annotate_tag),
                                color = MyStyleKt.TextColor.error()
                            )
                        }
                    }else {  //设置了用户名和邮箱，显示msg输入框
                        Row(modifier = Modifier.padding(5.dp)) {
                            // spacer
                        }
                        TextField(
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

                            value = tagMsg.value,
                            onValueChange = {
                                tagMsg.value = it
                                tagMsgErrMsg.value=""
                            },
                            label = {
                                Text(stringResource(R.string.tag_msg))
                            },

                            isError = tagMsgErrMsg.value.isNotEmpty(),
                            supportingText = {
                                if(tagMsgErrMsg.value.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = tagMsgErrMsg.value,
                                        color = MaterialTheme.colorScheme.error
                                    )

                                }
                            },
                            trailingIcon = {
                                if(tagMsgErrMsg.value.isNotEmpty()) {
                                    Icon(imageVector= Icons.Filled.Error,
                                        contentDescription="err icon",
                                        tint = MaterialTheme.colorScheme.error)
                                }
                            },
                        )

                    }
                }

                MyCheckBox(text = stringResource(R.string.force), value = force)

                if(force.value) {
                    MySelectionContainer {
                        Row {
                            DefaultPaddingText(
                                text = stringResource(R.string.warn_will_override_if_tag_name_already_exists),
                                color = MyStyleKt.TextColor.danger()
                            )
                        }
                    }
                }

            }
        },
        okBtnEnabled = tagNameErrMsg.value.isEmpty() && commitHashShortOrLongErrMsg.value.isEmpty() && tagMsgErrMsg.value.isEmpty() && (!annotate.value || (gitConfigUsername.value.isNotBlank() && gitConfigEmail.value.isNotBlank())),
        onCancel = {
            showDialog.value = false
        }
    ) onOk@{
        //检查
        if(tagName.value.isBlank()) {
            tagNameErrMsg.value = activityContext.getString(R.string.tag_name_is_empty)
            return@onOk
        }

        if(commitHashShortOrLong.value.isBlank()) {
            commitHashShortOrLongErrMsg.value = activityContext.getString(R.string.commit_hash_is_empty)
            return@onOk
        }

        if(annotate.value) {
            //annotate需要用到git config用户名和邮箱，检查下是否为空，其实本应由调用者检查，但这里也检查下
            //这里提示下即可，不需要设置红色的错误信息，因为当username或email为空且勾选了annotate时，已经在弹窗显示红色错误信息了
            if(gitConfigUsername.value.isBlank() || gitConfigEmail.value.isBlank()) {
                Msg.requireShowLongDuration(activityContext.getString(R.string.plz_set_git_username_and_email_first))
//                showDialog.value=false
                return@onOk
            }

            if(tagMsg.value.isBlank()) {
                tagMsgErrMsg.value = activityContext.getString(R.string.tag_msg_is_empty)
                return@onOk
            }
        }

        doJobThenOffLoading job@{
            try {
                Repository.open(curRepo.fullSavePath).use { repo->
                    val commit = Libgit2Helper.resolveCommitByHashOrRef(repo, commitHashShortOrLong.value).data

                    //无效commit
                    if(commit==null) {
                        commitHashShortOrLongErrMsg.value = activityContext.getString(R.string.invalid_commit_hash)
                        return@job
                    }

                    //可以关弹窗了
                    showDialog.value=false

                    if(annotate.value) {
                        Libgit2Helper.createTagAnnotated(
                            repo,
                            tagName.value,
                            commit,
                            tagMsg.value,
                            gitConfigUsername.value,
                            gitConfigEmail.value,
                            force.value,
                            settings
                        )
                    }else {
                        Libgit2Helper.createTagLight(
                            repo,
                            tagName.value,
                            commit,
                            force.value
                        )
                    }

                    Msg.requireShowLongDuration(activityContext.getString(R.string.success))
                    onOkDoneCallback(commit?.id()?.toString() ?: "")  //执行完成onOk后的回调，一般是刷新页面或刷新条目列表之类的

                }

            }catch (e:Exception) {
                val errMsg = "create tag '${tagName.value}' err: ${e.localizedMessage}"
                Msg.requireShowLongDuration(errMsg)
                createAndInsertError(curRepo.id, errMsg)
                MyLog.e(TAG, "#onOk err: $errMsg\n${e.stackTraceToString()}")
            }
        }

    }

    LaunchedEffect(Unit) {
        doJobThenOffLoading {
            //查git用户名和邮箱
            Repository.open(curRepo.fullSavePath).use { repo->
                val (username, email) = Libgit2Helper.getGitUsernameAndEmail(repo)
                gitConfigUsername.value = username
                gitConfigEmail.value = email
            }
        }
    }

}
