package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.forceCheckoutTestPassed
import com.catpuppyapp.puppygit.dev.overwriteExistWhenCreateBranchTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository

private const val TAG = "CreateBranchDialog"

@Composable
fun CreateBranchDialog(
    title: String = stringResource(R.string.create_branch),
    curBranchName:String,
    branchName: MutableState<String>,
    requireCheckout: MutableState<Boolean>,
    forceCheckout:MutableState<Boolean>,
    curRepo: RepoEntity,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    loadingText:String,
    onCancel:()->Unit,
    onErr:suspend (e:Exception)->Unit, //catch代码块末尾
    onFinally:()->Unit, //在 try...catch...finally，finally代码块里的代码
//    onOk: (branchName:String, baseRefSpec:String, basedHead:Boolean, createByRef:Boolean, needCheckout:Boolean, forceCheckout:Boolean, overwriteIfExist:Boolean) -> Unit,
) {
    val activityContext = LocalContext.current

    val repoId = curRepo.id
    //文案提示是“基于当前分支xxx（分支名）创建新分支(btw：如果想基于某个提交创建分支，可以去commit记录页面)”，以及强调“如果勾选checkout将立即检出分支，但如果有未提交数据，可能会丢失”
//    val textUnderCheckout = stringResource(R.string.warn_please_commit_your_change_before_checkout_or_merge)



    val optHEAD = 0;
    val optCommit = 1;
    val selectedOption = rememberSaveable{ mutableIntStateOf(optHEAD) }
    val createMethodList = listOf(activityContext.getString(R.string.head), activityContext.getString(R.string.commit))
    val userInputHash = rememberSaveable { mutableStateOf("") }


    val overwriteIfExist = rememberSaveable { mutableStateOf(false) }

    //参数1，要创建的本地分支名；2是否基于HEAD创建分支，3如果不基于HEAD，提供一个引用名
    //只有在basedHead为假的时候，才会使用baseRefSpec
    //返回值为：分支长名，短名，完整hash
    val doCreateBranch:suspend (String,Boolean,String, Boolean, Boolean)-> Ret<Triple<String, String, String>?> = doCreateBranch@{ branchNameParam:String, basedHead:Boolean, baseRefSpec:String, createByRef:Boolean, overwriteIfExist:Boolean ->
        Repository.open(curRepo.fullSavePath).use { repo ->

            //最后一个bool值代表是否根据引用创建分支，这个页面都是根据分支创建的，肯定是true
            val ret = Libgit2Helper.doCreateBranch(activityContext, repo, repoId, branchNameParam, basedHead, baseRefSpec, createByRef, overwriteIfExist)

            return@doCreateBranch ret
        }
    }

    //shortBranchName用来存数据库给用户看；fullBranchName用来查找Reference，传fullrefspec是为了避免歧义
    //upstreamBranchShortNameParam是当前分支的上游，有2种可能的值：1 创建分支或检出远程分支，传空。2检出本地分支，传被检出的分支的值。
    //第三个参数是指示是检出Local还是Remote分支，逻辑有点不同
    val doCheckoutBranch: suspend (String, String, String, force:Boolean) -> Ret<Oid?> = doCheckoutLocalBranch@{ shortBranchName:String, fullBranchName:String, upstreamBranchShortNameParam:String, force:Boolean ->
        Repository.open(curRepo.fullSavePath).use { repo ->
            val ret = Libgit2Helper.doCheckoutBranchThenUpdateDb(
                repo,
                repoId,
                shortBranchName,
                fullBranchName,
                upstreamBranchShortNameParam,
                Cons.checkoutType_checkoutRefThenUpdateHead,  //原本是 if(isLocal) checkoutTypeLocalBranch else checkoutTypeRemoteBranch，但创建分支然后checkout必然是本地分支，所以这里不用检查，直接传本地分支即可
                force=force,
                updateHead = true // 创建本地分支并 checkout，隐含使HEAD指向新创建的分支，所以必然需要updateHead，若不想更新head，应该不勾选checkout，这样就会仅创建分支了，而那样的话就不会调用本函数，所以，只要调用此函数，此值就应为true
            )

            return@doCheckoutLocalBranch ret
        }
    }
    AlertDialog(
        title = {
            DialogTitle(title)
        },
        text = {
            ScrollableColumn {
                SelectionRow {
                    //Create branch based on: your_cur_branch_name
                    Text(text = stringResource(R.string.create_branch_based_on) + ":")
                }

                Spacer(Modifier.height(5.dp))

                SingleSelection(
                    itemList = createMethodList,
                    selected = {idx, item -> selectedOption.intValue == idx},
                    text = {idx, item -> item + (if(idx == optHEAD) " ($curBranchName)" else "") },
                    onClick = {idx, item -> selectedOption.intValue = idx}
                )

                if(selectedOption.intValue == optCommit) {
                    Spacer(Modifier.height(5.dp))

                    TextField(
                        modifier = Modifier.fillMaxWidth(),

                        value = userInputHash.value,
                        singleLine = true,
                        onValueChange = {
                            userInputHash.value = it
                        },
                        label = {
                            Text(stringResource(R.string.target))
                        },
                        placeholder = {
                            Text(stringResource(R.string.hash_branch_tag))
                        },
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = branchName.value,
                    singleLine = true,
                    onValueChange = {
                        branchName.value = it
                    },
                    label = {
                        Text(stringResource(R.string.branch_name))
                    },
                    placeholder = {
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))


                if(proFeatureEnabled(overwriteExistWhenCreateBranchTestPassed)) {
                    MyCheckBox(text = stringResource(R.string.overwrite_if_exist), value = overwriteIfExist)
                    if(overwriteIfExist.value) {
                        SelectionRow {
                            DefaultPaddingText(
                                text = stringResource(R.string.will_overwrite_if_branch_already_exists),
                                color = MyStyleKt.TextColor.danger(),

                            )
                        }

                    }
                }


                MyCheckBox(text = stringResource(R.string.checkout), value = requireCheckout)

                //force checkout 仅对pro可用
                if(proFeatureEnabled(forceCheckoutTestPassed)) {
                    if(requireCheckout.value) {
                        // show force checkbox
                        MyCheckBox(text = stringResource(R.string.force), value = forceCheckout)

                        //如果勾选了force checkout，警告没提交的内容可能会丢失
                        if(forceCheckout.value) {
                            SelectionRow {
                                DefaultPaddingText(
                                    text = stringResource(R.string.warn_force_checkout_will_overwrite_uncommitted_changes),
                                    color = MyStyleKt.TextColor.danger()
                                )
                            }

                        }
                    }
                }
            }

        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCancel()  //关闭弹窗

                    //准备参数
                    //branchName:String, baseRefSpec:String, basedHead:Boolean, createByRef:Boolean, needCheckout:Boolean
                    val branchName = branchName.value
                    val basedHead = selectedOption.intValue == optHEAD
                    val baseRefSpec = if(basedHead) "" else userInputHash.value
                    val createByRef = false  //如果basedHead为true，此值被忽略，否则，如果此值为true，根据分支名（引用名）查提交号，为false，代表入参是提交号直接使用提交号。在这里，如果basedHead为true，此值无意义，如果basedHead为假，我需要此值为假以使用用户输入的提交号，所以此值在此设为常量假即可
                    val needCheckout = requireCheckout.value

                    //创建分支
                    doJobThenOffLoading(
                        loadingOn = loadingOn,
                        loadingOff = loadingOff,
                        loadingText = loadingText,
                    )  job@{
                        try {
                            //支持基于分支名、tag名创建提交
                            val actuallyBaseRefSpecCommitHash =  if(!basedHead) {  //如果选的不是基于HEAD创建提交，查询一下
                                var r = baseRefSpec
                                //解析一下refspec
                                Repository.open(curRepo.fullSavePath).use { repo->
                                    val ret = Libgit2Helper.resolveCommitByHashOrRef(repo, baseRefSpec)
                                    if(ret.success() && ret.data != null) {  //如果查询成功，取下查出的commit id，如果查询失败，还用原来的值就行，当然，后面很可能会执行失败，不过无所谓
                                        r = ret.data!!.id().toString()
                                    }
                                }

                                r
                            }else {
                                baseRefSpec
                            }

                            val createBranchRet = doCreateBranch(branchName, basedHead, actuallyBaseRefSpecCommitHash, createByRef, overwriteIfExist.value)

                            if(createBranchRet.hasError()) {  //创建分支失败
                                throw RuntimeException(activityContext.getString(R.string.create_branch_err)+": "+createBranchRet.msg)
                            }

                            //执行到这，分支创建成功，接下来检查是否勾选了checkout，若勾选了则执行checkout
                            Msg.requireShow(activityContext.getString(R.string.create_branch_success))



                            if(needCheckout) {
                                Msg.requireShow(activityContext.getString(R.string.checking_out))
                                //执行checkout
                                //createBranchRet.data 是创建成功后的分支的完整引用和短引用pair，只要创建分支返回成功，肯定不是null
                                //第3个参数是当前分支的上游，因为是刚创建的分支，所以上游肯定是空
                                //第4个参数是指示是检出Local还是Remote分支，逻辑有点不同
                                val (branchFullRefspec, _) = createBranchRet.data!!
                                val upstreamBranchShortNameParam = ""  //新分支，无上游
                                val checkoutRet = doCheckoutBranch(branchName, branchFullRefspec, upstreamBranchShortNameParam, forceCheckout.value)

                                if(checkoutRet.hasError()) {
                                    throw RuntimeException(activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg)
                                }

                                //checkout成功
                                Msg.requireShow(activityContext.getString(R.string.checkout_success))

                            }

                        }catch (e:Exception) {
                            onErr(e)
                        }finally {
                            onFinally()
                        }

                    }

                },
                enabled = branchName.value.isNotBlank() && (if(selectedOption.intValue == optCommit) userInputHash.value.isNotBlank() else true)
            ) {
                Text(stringResource(id = R.string.create))
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

//    LaunchedEffect(Unit) {
//
//    }
}

