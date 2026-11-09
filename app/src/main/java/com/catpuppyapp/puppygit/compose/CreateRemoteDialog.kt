package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.data.entity.RemoteEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Remote
import com.github.git24j.core.Repository

@Composable
fun CreateRemoteDialog(
    show:MutableState<Boolean>,
    curRepo:RepoEntity,
    remoteName:MutableState<String>,
    remoteUrl:MutableState<String>,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    onCancel:()->Unit ={},
    onErr:suspend (e:Exception)->Unit,
    onOk:(()->Unit)? = null,
    onFinally:()->Unit,
) {
    val activityContext = LocalContext.current

    val onOk:()->Unit = onOk ?: { Msg.requireShow(activityContext.getString(R.string.success)) }

    val close = {show.value = false}

    AlertDialog(
        title = {
            DialogTitle(stringResource(R.string.create))
        },
        text = {
            ScrollableColumn {
                TextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = remoteName.value,
                    singleLine = true,
                    onValueChange = {
                        remoteName.value = it
                    },
                    label = {
                        Text(stringResource(R.string.name))
                    },
//                    placeholder = {}
                )
                Row(modifier = Modifier.padding(5.dp)) {

                }
                TextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = remoteUrl.value,
                    singleLine = true,
                    onValueChange = {
                        remoteUrl.value = it
                    },
                    label = {
                        Text(stringResource(R.string.url))
                    },
//                    placeholder = {}
                )
            }

        },
        onDismissRequest = {
            close()
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    close()

                    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.creating)) {
                        try {
                            val remoteName = remoteName.value
                            val remoteUrl = remoteUrl.value
                            val repoId = curRepo.id
                            val repoPath = curRepo.fullSavePath
                            Repository.open(repoPath).use { repo->
                                //在git创建
                                val ret = Libgit2Helper.createRemote(repo, remoteName, remoteUrl)

                                val remoteDb = AppModel.dbContainer.remoteRepository
                                val exists=ret.msg.startsWith("remote") && ret.msg.endsWith("already exists")  // remote 'xxxx' already exists
                                //如果在git仓库创建remote成功，或者提示remote已经存在(避免remote存在但db无记录的问题，这个问题类似源数据和缓存出现分歧的状况)，则尝试插入，插入时检查，若repoId+remoteName不存在则插入，否则什么都不做
                                if(ret.success() || exists) {
                                    val remoteUrl = if(exists){  //若remote存在，读取配置文件中的url，否则使用用户输入的url
                                        val r = Remote.lookup(repo, remoteName)
                                        r?.url() ?: ""
                                    } else remoteUrl

                                    //需确保insert前检查 repoId和remoteName是否存在，若存在就不插入
                                    //在数据库创建
                                    remoteDb.insert(
                                        RemoteEntity(
                                            remoteName = remoteName,
                                            remoteUrl= remoteUrl,
                                            repoId = repoId
                                        )
                                    )

                                }else{
                                    throw ret.exception ?: Exception(ret.msg)
                                }

                                if(exists){
                                    Msg.requireShowLongDuration(ret.msg)
                                }else {
                                    onOk()
                                }
                            }
                        }catch (e:Exception) {
                            onErr(e)
                        }finally {
                            onFinally()
                        }
                    }
                },
                enabled = remoteName.value.isNotBlank() && remoteUrl.value.isNotBlank()
            ) {
                Text(stringResource(id = R.string.create))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    close()
                    onCancel()
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )


}
