package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Repository

private const val TAG = "AskGitUsernameAndEmailDialogWithSelection"

@Composable
fun AskGitUsernameAndEmailDialogWithSelection(
    curRepo: RepoEntity,
    username: MutableState<String>,
    email: MutableState<String>,

    closeDialog: () -> Unit,  //仅关闭弹窗
    onCancel:() -> Unit = closeDialog, // 点击取消或返回或空白处执行的操作，一般和closeDialog逻辑一样，但也可能会执行比closeDialog更多的操作
    onErrorCallback:suspend (Exception) -> Unit,
    onFinallyCallback:() -> Unit,
    onSuccessCallback: suspend () -> Unit,
) {
    val activityContext = LocalContext.current
    val setUserAndEmailForGlobal = stringResource(R.string.set_for_global)
    val setUserAndEmailForCurRepo = stringResource(R.string.set_for_current_repo) + " (${curRepo.repoName})"
    val errWhenQuerySettingsFromDbStrRes = stringResource(R.string.err_when_querying_settings_from_db)
    val invalidUsernameOrEmail = stringResource(R.string.invalid_username_or_email)

    val optNumSetUserAndEmailForGlobal = 0  //为全局设置用户名和密码，值是对应选项在选项列表中的索引，这个变量其实相当于是索引的别名。关联列表：usernameAndEmailDialogOptionList
    val optNumSetUserAndEmailForCurRepo = 1  //为当前仓库设置用户名和密码
    //num: optText，例如 "1: 选项1文本"
//    val usernameAndEmailDialogOptionList = listOf(RadioOptionsUtil.formatOptionKeyAndText(optNumSetUserAndEmailForGlobal,setUserAndEmailForGlobal), RadioOptionsUtil.formatOptionKeyAndText(optNumSetUserAndEmailForCurRepo,setUserAndEmailForCurRepo))
    //这个列表两个条目，一个代表为全局设置用户名和邮箱，另一个代表为当前仓库设置用户名和邮箱
    val optionsList = listOf(  // idx关联选项：optNumSetUserAndEmailForGlobal, optNumSetUserAndEmailForCurRepo
        setUserAndEmailForGlobal,  //值的存放顺序要和选项值匹配 (这个值对应的是 optNumSetUserAndEmailForGlobal )
        setUserAndEmailForCurRepo
    )

    //代表选中了optionsList中的哪个条目
    val selectedOption = rememberSaveable{ mutableIntStateOf(optNumSetUserAndEmailForGlobal) }

    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.user_info))
        },
        text = {
            ScrollableColumn {

                //如果设置了有效gitUrl，显示新建和选择凭据，否则只显示无凭据
                //key should like: "1"; value should like "1: balbalba"
                SingleSelection(
                    itemList = optionsList,
                    selected = {idx, item -> selectedOption.intValue == idx},
                    text = {idx, item -> item},
                    onClick = {idx, item -> selectedOption.intValue = idx}
                )

                Row(modifier = Modifier.padding(5.dp)) {

                }

                TextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),

//                            modifier = Modifier.focusRequester(focusRequester),
                    value = username.value,
                    onValueChange = {
                        username.value = it
                    },
                    label = {
                        Text(stringResource(R.string.username))
                    },
                    placeholder = {
                        Text(stringResource(R.string.username))
                    }
                )
                Row(modifier = Modifier.padding(5.dp)) {

                }
                TextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),

                    value = email.value,
                    onValueChange = {
                        email.value = it
                    },
                    label = {
                        Text(stringResource(R.string.email))
                    },
                    placeholder = {
                        Text(stringResource(R.string.email))
                    }
                )
            }

        },

        onDismissRequest = {
            onCancel()
        },

        confirmButton = {
            TextButton(
                onClick = {
                    //关闭弹窗
                    closeDialog()

                    //取出用户名和邮箱
                    val usernameValue = username.value
                    val emailValue = email.value
                    val forGlobal = selectedOption.intValue == optNumSetUserAndEmailForGlobal

                    doJobThenOffLoading {
                        try {
                            if(usernameValue.isBlank() || emailValue.isBlank()) {
                                throw RuntimeException(invalidUsernameOrEmail)
                            }

                            // username and email are not blank

                            val requireShowErr = Msg.requireShowLongDuration
                            //save username and email to global or repo, then redo commit
                            if (forGlobal) {  //为全局设置用户名和邮箱
                                //如果保存失败，return
                                if (!Libgit2Helper.saveGitUsernameAndEmailForGlobal(
                                        requireShowErr = requireShowErr,
                                        username = usernameValue,
                                        email = emailValue
                                    )
                                ) {
                                    throw RuntimeException("set username and email for global err")
                                }

                            } else {  //为当前仓库设置用户名和邮箱
                                Repository.open(curRepo.fullSavePath).use { repo ->
                                    //如果保存失败，return，方法内会提示错误信息，这里就不用再提示了
                                    if (!Libgit2Helper.saveGitUsernameAndEmailForRepo(
                                            repo = repo,
                                            requireShowErr = requireShowErr,
                                            username = usernameValue,
                                            email = emailValue
                                        )
                                    ) {
                                        throw RuntimeException("set username and email for repo err")
                                    }
                                }
                            }

                            //显示"已保存"提示信息
                            Msg.requireShow(activityContext.getString(R.string.saved))

                            //已经保存，调用回调
                            onSuccessCallback()

                        }catch (e:Exception) {
                            onErrorCallback(e)
                        }finally {
                            onFinallyCallback()
                        }
                    }

                },

                //这个组件是不允许留空的，若想清空仓库的用户信息，可去仓库页面选中其卡片设置其用户信息；若想清空全局信息，可去仓库页面顶栏设置用户信息。
                //这个组件用来在必须设置用户名和邮箱才能继续操作的情况，所以不允许留空。
                enabled = username.value.isNotBlank() && email.value.isNotBlank()
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )

}

