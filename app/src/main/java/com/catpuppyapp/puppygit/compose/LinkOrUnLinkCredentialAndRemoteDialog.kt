package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.constants.SpecialCredential
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.dto.RemoteDtoForCredential
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@Composable
fun LinkOrUnLinkCredentialAndRemoteDialog(
    curItemInPage:CustomStateSaveable<CredentialEntity>,
    requireDoLink:Boolean, // true require do link, else require do unlink
    targetAll:Boolean,
    title:String,
    thisItem: RemoteDtoForCredential,
    onCancel: () -> Unit,
    onErrCallback:suspend (err:Exception)->Unit,
    onFinallyCallback:()->Unit,
    onOkCallback:()->Unit,
) {

    val fetchChecked = rememberSaveable { mutableStateOf(if(targetAll) true else if(requireDoLink) thisItem.credentialId!=curItemInPage.value.id else thisItem.credentialId==curItemInPage.value.id)}
    val pushChecked = rememberSaveable { mutableStateOf(if(targetAll) true else if(requireDoLink) thisItem.pushCredentialId!=curItemInPage.value.id else thisItem.pushCredentialId==curItemInPage.value.id)}


    AlertDialog(
        title = {
            DialogTitle(title.ifEmpty { if (requireDoLink) stringResource(R.string.link) else stringResource(R.string.unlink) })
        },
        text = {
            ScrollableColumn {
                MyCheckBox(text = stringResource(R.string.fetch), value = fetchChecked)
                MyCheckBox(text = stringResource(R.string.push), value = pushChecked)
            }

        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                enabled = fetchChecked.value || pushChecked.value,  //at least checked 1, else dont enable

                onClick = onOk@{
                    if(!fetchChecked.value && !pushChecked.value) {
                        return@onOk
                    }

                    val remoteId = thisItem.remoteId
                    val curCredentialId = curItemInPage.value.id
                    val remoteDb = AppModel.dbContainer.remoteRepository
                    doJobThenOffLoading {
                        try {
                            val targetCredentialId = if(requireDoLink) curCredentialId else SpecialCredential.NONE.credentialId;

                            if(requireDoLink) {  //link
                                if(targetAll) {
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateAllFetchAndPushCredentialId(targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateAllFetchCredentialId(targetCredentialId)
                                    }else {  //pushChecked.value is true
                                        remoteDb.updateAllPushCredentialId(targetCredentialId)
                                    }
                                }else {
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateFetchAndPushCredentialIdByRemoteId(remoteId, targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }else {  //pushChecked.value is true
                                        remoteDb.updatePushCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }
                                }
                            }else {  // unlink
                                if(targetAll) {  // unlink all by current credentialId
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateFetchAndPushCredentialIdByCredentialId(curCredentialId, curCredentialId, targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateCredentialIdByCredentialId(curCredentialId, targetCredentialId)
                                    }else {  //pushChecked.value is true
                                        remoteDb.updatePushCredentialIdByCredentialId(curCredentialId, targetCredentialId)
                                    }
                                }else {  //unlink single item
                                    if(fetchChecked.value && pushChecked.value) {
                                        remoteDb.updateFetchAndPushCredentialIdByRemoteId(remoteId, targetCredentialId, targetCredentialId)
                                    }else if(fetchChecked.value) {
                                        remoteDb.updateCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }else {  //pushChecked.value is true
                                        remoteDb.updatePushCredentialIdByRemoteId(remoteId, targetCredentialId)
                                    }
                                }
                            }

                            onOkCallback()
                        }catch (e:Exception){
                            onErrCallback(e)
                        }finally {
                            onFinallyCallback()
                        }
                    }
                }
            ) {
                Text(stringResource(id = R.string.ok))
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

