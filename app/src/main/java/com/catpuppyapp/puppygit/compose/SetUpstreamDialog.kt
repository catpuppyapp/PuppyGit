package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Repository


@Composable
fun SetUpstreamDialog(
    callerTag:String, //用来指示哪个页面调用的这个组件
    remoteList:List<String>,  //remote列表
    selectedOption: MutableIntState,  //选中的remote在列表中的索引
    upstreamBranchShortName: MutableState<String>,
    upstreamBranchShortNameSameWithLocal: MutableState<Boolean>,
    onOkText:String = stringResource(R.string.save),
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    curRepo:RepoEntity,
    curBranchShortName:String, //供显示的，让用户知道在为哪个分支设置上游
    curBranchFullName:String,
    isCurrentBranchOfRepo:Boolean,
    showClear:Boolean,
    closeDialog: () -> Unit,  //仅关闭弹窗
    onCancel:() -> Unit = closeDialog, // 点击取消或返回或空白处执行的操作，一般和closeDialog逻辑一样，但也可能会执行比closeDialog更多的操作
    onClearErrorCallback:suspend (Exception) -> Unit,
    onClearFinallyCallback:(() -> Unit)?,
    onClearSuccessCallback: suspend () -> Unit,
    onErrorCallback:suspend (Exception) -> Unit,
    onFinallyCallback:(() -> Unit)?,
    onSuccessCallback: suspend () -> Unit,
) {
    val funName = remember {"SetUpstreamDialog"}

    val activityContext = LocalContext.current

    val onClear = {
        closeDialog()

        val repoFullPath = curRepo.fullSavePath
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
            try {
                Repository.open(repoFullPath).use { repo ->
                    Libgit2Helper.clearUpstreamForBranch(repo, curBranchShortName)
                }

                onClearSuccessCallback()
            } catch (e: Exception) {
                onClearErrorCallback(e)
            } finally {
                onClearFinallyCallback?.invoke()
            }
        }

    }

    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.set_upstream_title))
        },
        text = {
            ScrollableColumn {
                SelectionRow {
                    Text(text = stringResource(R.string.set_upstream_for_branch)+":")
                }

                SelectionRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MyStyleKt.defaultHorizontalPadding)
                    ,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = curBranchShortName, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(15.dp))
                //这个文案感觉没太大必要，不显示了，省点空间
//                Row(modifier = Modifier.padding(10.dp)) {
//                    Text(text = stringResource(R.string.set_upstream_text))
//                }
                SelectionRow {
                    Text(text = stringResource(R.string.select_a_remote)+":")
                }

                Spacer(Modifier.height(5.dp))

                if(remoteList.isEmpty()) {  //remoteList为空，显示提示，同时应禁用ok按钮
                    SelectionRow {
                        Text(
                            text = stringResource(R.string.err_remote_list_is_empty),
                            color = MyStyleKt.TextColor.error()
                        )
                    }
                }else{
                    SingleSelectList(
                        basePadding = { PaddingValues(0.dp) },
                        optionsList = remoteList,
                        selectedOptionIndex = selectedOption
                    )
                }

                Spacer(Modifier.height(20.dp))

                SelectionRow {
                    Text(stringResource(R.string.upstream_branch_name)+":")
                }

                Spacer(Modifier.height(5.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,

                    enabled = !upstreamBranchShortNameSameWithLocal.value,
                    value = upstreamBranchShortName.value,
                    singleLine = true,
                    onValueChange = {
                        upstreamBranchShortName.value = it
                    },
                    placeholder = {
                        Text(stringResource(R.string.branch_name))
                    }
                )

                Spacer(Modifier.height(10.dp))
                MyCheckBox(stringResource(R.string.same_with_local), upstreamBranchShortNameSameWithLocal)


            }

        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = onOk@{
                    closeDialog()

                    val repoId = curRepo.id
                    val repoName = curRepo.repoName
                    val repoFullPath = curRepo.fullSavePath
                    val upstreamSameWithLocal = upstreamBranchShortNameSameWithLocal.value
                    val selectedRemoteIndex = selectedOption.intValue
                    val upstreamShortName = upstreamBranchShortName.value

                    //直接索引取值即可
                    val remote = try {
                        remoteList[selectedRemoteIndex]
                    } catch (e: Exception) {
                        // e.g. BranchListScreen: SetUpstreamDialog: err
                        MyLog.e(callerTag,"#$funName: err when get remote by index from remote list of '$repoName': remoteIndex=$selectedRemoteIndex, remoteList=$remoteList\nerr info:${e.stackTraceToString()}")
                        Msg.requireShowLongDuration(activityContext.getString(R.string.err_selected_remote_is_invalid))
                        return@onOk
                    }


                    // update git config
                    //设置上游太快了，显示loading没啥意义，闪一下，就没了，还不如不闪
//                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.setting_upstream)) {
                    doJobThenOffLoading {
                        try {
                            var branch = ""
                            var setUpstreamSuccess = false

                            Repository.open(repoFullPath).use { repo ->
                                branch = if (upstreamSameWithLocal) {  //勾选了使用和本地同名的分支，创建本地同名远程分支
                                    //取出repo当前分支长名。注意：这里不能直接用head.name()，因为不一定是为当前分支设置上游，不过若正好是未当前分支设置上游，其实可以用head.name()
                                    curBranchFullName
                                } else {  //否则取出用户输入的远程分支短名，然后生成长名
                                    Libgit2Helper.getRefsHeadsBranchFullRefSpecFromShortRefSpec(upstreamShortName)
                                }

                                MyLog.d(callerTag, "#$funName: set upstream dialog #onOk(): repo is '$repoName', will write to git config: remote=$remote, branch=$branch")

                                //把分支的upstream信息写入配置文件
                                setUpstreamSuccess = Libgit2Helper.setUpstreamForBranchByRemoteAndRefspec(
                                    repo = repo,
                                    remote = remote,
                                    fullBranchRefSpec = branch,
                                    targetBranchShortName = curBranchShortName
                                )
                            }


                            //如果是当前活跃分支，更新下db，否则不用更新
                            if (isCurrentBranchOfRepo) {
                                //更新数据库
                                val upstreamBranchShortName = Libgit2Helper.getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(remote, branch)

                                MyLog.d(callerTag, "#$funName: set upstream dialog #onOk(): upstreamBranchShortName=$upstreamBranchShortName")

                                AppModel.dbContainer.repoRepository.updateUpstream(repoId, upstreamBranchShortName)
                            }

                            if (setUpstreamSuccess) {
                                onSuccessCallback()
                            } else {
                                throw RuntimeException("unknown error, code '1c3f943a8e'")  //这code是用来定位出错原因在源代码中的位置的
                            }
                        } catch (e: Exception) {
                            //显示通知
                            onErrorCallback(e)

                        } finally {
                            onFinallyCallback?.invoke()
                        }

                    }
                },
                //如果没勾选上游使用和本地同名分支且上游分支引用为空（没填或删了默认的，就会空），返回假，没设计成留空自动生成，所以，必须要填个分支
                enabled = (!(!upstreamBranchShortNameSameWithLocal.value && upstreamBranchShortName.value.isBlank())) && remoteList.isNotEmpty(),
            ) {
                Text(text = onOkText)
            }
        },
        dismissButton = {
            ScrollableRow {
                if(showClear) {
                    TextButton(
                        onClick = {
                            onClear()
                        }
                    ) {
                        Text(stringResource(id = R.string.clear), color = MyStyleKt.TextColor.danger())
                    }
                }


                TextButton(
                    onClick = {
                        onCancel()
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        }
    )

}

