package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Repository

private const val TAG = "FetchRemotesDialog"

@Composable
fun FetchRemotesDialog(
    title:String = stringResource(R.string.fetch),
    text:String,
    remoteList:List<RemoteDto>,
    okText:String = stringResource(R.string.yes),
    cancelText:String = stringResource(R.string.no),
    closeDialog:()->Unit,
    curRepo:RepoEntity,
    loadingOn:(loadingMsg:String)->Unit,
    loadingOff:()->Unit,
    refreshPage:()->Unit,
){
    val activityContext = LocalContext.current

    ConfirmDialog(
        title = title,
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                SelectionRow {
                    Text(text)
                }
            }
        },
        onCancel = closeDialog,
        okBtnText = okText,
        cancelBtnText = cancelText,
    ) {
        closeDialog()

        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.fetching)) {
            try {
                if(remoteList.isEmpty()) {  // remotes列表为空，无需执行操作
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_remote_list_is_empty))
                }else {  //remote列表如果是空就不用fetch了
                    //获取remote名和凭据组合的列表
                    val remoteCredentialList = Libgit2Helper.genRemoteCredentialPairList(
                        remoteList,
                        AppModel.dbContainer.credentialRepository,
                        requireFetchCredential = true,
                        requirePushCredential = false
                    )

                    Repository.open(curRepo.fullSavePath).use { repo ->
                        //执行fetch
                        Libgit2Helper.fetchRemoteListForRepo(repo, remoteCredentialList, curRepo)

                        //显示成功通知
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }
                }

            }catch (e:Exception){
                val errMsg = "fetch remotes err: "+e.localizedMessage
                Msg.requireShowLongDuration(errMsg)
                createAndInsertError(curRepo.id, errMsg)

                MyLog.e(TAG, "fetch remotes err: "+e.stackTraceToString())

            }finally {
                refreshPage()
            }
        }

    }
}
