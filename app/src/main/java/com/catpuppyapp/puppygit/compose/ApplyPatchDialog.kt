package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository
import java.io.File



@Composable
fun ApplyPatchDialog(
    errMsg: String,
    selectedRepo:CustomStateSaveable<RepoEntity>,
    checkOnly:MutableState<Boolean>,
    patchFileFullPath:String,
    repoList:List<RepoEntity>,
    loadingRepoList: Boolean,
    onCancel: () -> Unit,
    onErrCallback:suspend (err:Exception, selectedRepoId:String)->Unit,
    onFinallyCallback:()->Unit,
    onOkCallback:()->Unit,
) {

    ConfirmDialog2(
        okBtnEnabled = loadingRepoList.not() && repoList.isNotEmpty() && selectedRepo.value.id.isNotBlank(),
        title = stringResource(R.string.apply_patch),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                val hasErr = errMsg.isNotEmpty()
                if(hasErr || loadingRepoList || repoList.isEmpty()) {  //正在加载仓库列表，或者加载完了，但仓库列表为空
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MySelectionContainer {
                            if(hasErr) {
                                Text(errMsg, color = MyStyleKt.TextColor.error())
                            }else {
                                Text(stringResource(if(loadingRepoList) R.string.loading else R.string.repo_list_is_empty))
                            }
                        }
                    }
                } else {  //加载仓库列表完毕，并且列表非空
                    MySelectionContainer {
                        DefaultPaddingText(text = stringResource(R.string.select_target_repo)+":")
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    SingleSelectList(
                        optionsList = repoList,
                        menuItemSelected = {idx, value -> value.id == selectedRepo.value.id},
                        menuItemOnClick = {idx, value -> selectedRepo.value = value},
                        menuItemFormatter = {idx, value -> value?.repoName ?: ""},
                        selectedOptionIndex = null,
                        selectedOptionValue = selectedRepo.value
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MyCheckBox(stringResource(R.string.check_only), checkOnly)
                    if(checkOnly.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.apply_patch_check_note))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        },
        okBtnText = stringResource(R.string.apply),
        onCancel = { onCancel() }
    ) {  // onOK

        doJobThenOffLoading {
            try {
                Repository.open(selectedRepo.value.fullSavePath).use { repo ->
                    /*
                     *(
                            inputFile:File,
                            repo:Repository,
                            applyOptions: Apply.Options?=null,
                            location:Apply.LocationT = Apply.LocationT.WORKDIR,  // default same as `git apply`
                            checkWorkdirCleanBeforeApply: Boolean = true,
                            checkIndexCleanBeforeApply: Boolean = false
                        )
                     */
                    val inputFile = File(patchFileFullPath)
                    val ret = Libgit2Helper.applyPatchFromFile(
                        inputFile,
                        repo,
                        checkOnlyDontRealApply = checkOnly.value
                    )

                    if(ret.hasError()) {
                        if(ret.exception!=null) {
                            throw ret.exception!!
                        }else {
                            throw RuntimeException(ret.msg)
                        }
                    }
                }


                onOkCallback()
            }catch (e:Exception){
                onErrCallback(e, selectedRepo.value.id)
            }finally {
                onFinallyCallback()
            }
        }
    }
}
