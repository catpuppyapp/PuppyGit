package com.catpuppyapp.puppygit.compose

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository

@Composable
fun FileHistoryRestoreDialog(
    targetCommitOidStr:String,
    showRestoreDialog: MutableState<Boolean>,
    loadingOn: (String) -> Unit,
    loadingOff: () -> Unit,
    appContext: Context,
    curRepo: CustomStateSaveable<RepoEntity>,
    fileRelativePath: String,
    repoId: String,
    onSuccess:()->Unit = {}
) {
    ConfirmDialog2(
        title = stringResource(R.string.restore),
        requireShowTextCompose = true,
        textCompose = {
            Column {
                MySelectionContainer {
                    Text(
                        replaceStringResList(stringResource(R.string.target_ph), listOf(targetCommitOidStr))
                    )
                }
            }
        },
        onCancel = { showRestoreDialog.value = false },
        okBtnText = stringResource(R.string.restore)
    ) {
        showRestoreDialog.value = false
        doJobThenOffLoading(loadingOn, loadingOff, appContext.getString(R.string.restoring)) {
            try {
                Repository.open(curRepo.value.fullSavePath).use { repo ->
                    //fun checkoutFiles(repo: Repository, targetCommitHash:String, pathSpecs: List<String>, force: Boolean, checkoutOptions: Checkout.Options?=null): Ret<Unit?> {
                    Libgit2Helper.checkoutFiles(repo, targetCommitOidStr, pathSpecs = listOf(fileRelativePath), force = true)

                }

                Msg.requireShow(appContext.getString(R.string.success))

                onSuccess()
            } catch (e: Exception) {
                val errMsg = e.localizedMessage ?: "unknown err"
                Msg.requireShowLongDuration(errMsg)
                createAndInsertError(repoId, errMsg)
            }
        }
    }
}
