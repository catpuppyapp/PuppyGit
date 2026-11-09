package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.dontCheckoutWhenCreateBranchAtCheckoutDialogTestPassed
import com.catpuppyapp.puppygit.dev.dontUpdateHeadWhenCheckoutTestPassed
import com.catpuppyapp.puppygit.dev.forceCheckoutTestPassed
import com.catpuppyapp.puppygit.dev.overwriteExistWhenCreateBranchTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.createAndInsertError
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository

private const val TAG = "CheckoutDialog"

// 对应option text索引
private const val checkoutOptionDontUpdateHead = 0
private const val checkoutOptionDetachHead = 1
private const val checkoutOptionCreateBranch = 2
const val checkoutOptionJustCheckoutForLocalBranch = 3

private const val maxCheckoutSelectedOptionIndex = checkoutOptionJustCheckoutForLocalBranch

const val invalidCheckoutOption = -1


fun getDefaultCheckoutOption(showJustCheckout: Boolean) =  if(showJustCheckout) checkoutOptionJustCheckoutForLocalBranch else checkoutOptionCreateBranch

@Composable
fun CheckoutDialog(
    checkoutSelectedOption: MutableIntState,
    branchName: MutableState<String>,
    // this may will be remote branch short name without remote prefix when check out any remote branch
    remoteBranchShortNameMaybe:String = "",

    isCheckoutRemoteBranch:Boolean=false,  // show set upstream checkbox if true
    remotePrefixMaybe:String="",
    showCheckoutDialog:MutableState<Boolean>,
    from: CheckoutDialogFrom,
    curRepo:RepoEntity,
    curCommitOid:String,  //长oid
    curCommitShortOid:String,  //短oid
    shortName:String,  //分支名 tag名之类的，用来显示给用户看和记log
    fullName:String, //完整引用名（分支名之类的），用来查找Reference，最好长名，精确
    requireUserInputCommitHash:Boolean, //是否请求用户输入hash，若请求显示输入框。（ps 其实当初应该设计为显示输入框，且输入框默认值为fullOid的值就行了
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    headChangedCallback:() -> Unit = {},
    refreshPage:(checkout:Boolean, targetOid:String, forceCreateBranch:Boolean, branchName:String) -> Unit,  //刷新页面，若不需要刷新可传空
    expectCheckoutType:Int,  //期望的checkout类型，不一定会采用，但可确定调用checkout的是谁，是远程分支还是本地分支还是别的
    showJustCheckout:Boolean= false,  //参数原名：`checkoutLocalBranch`。 作用：显示一个just checkout选项并默认选中，一般只有checkout本地分支时才需要设此值为true
) {

    val repoId = curRepo.id

    val activityContext = LocalContext.current

    val checkoutRemoteOptions = listOf(
        stringResource(R.string.dont_update_head),
//        activityContext.getString(R.string.detach_head),
        Cons.gitDetachHeadStr,  //这个感觉不翻译好一些
        stringResource(R.string.new_branch),
        stringResource(R.string.just_checkout)
    )


    val checkoutUserInputCommitHash = rememberSaveable { mutableStateOf("") }
    val forceCheckout = rememberSaveable { mutableStateOf(false) }
    val dontCheckout = rememberSaveable { mutableStateOf(false) }
    val overwriteIfBranchExist = rememberSaveable { mutableStateOf(false) }
    val setUpstream = rememberSaveable { mutableStateOf(isCheckoutRemoteBranch) }

    val getCheckoutOkBtnEnabled:()->Boolean = getCheckoutOkBtnEnabled@{
        //请求checkout时创建分支但没填分支，返回假
        if(checkoutSelectedOption.intValue == checkoutOptionCreateBranch && branchName.value.isBlank()) {
            return@getCheckoutOkBtnEnabled false
        }

        //请求checkout to hash但没填hash，返回假
        if(requireUserInputCommitHash && checkoutUserInputCommitHash.value.isBlank()) {
            return@getCheckoutOkBtnEnabled false
        }

        if(checkoutSelectedOption.intValue.let { it < 0 || it > maxCheckoutSelectedOptionIndex }) {
            return@getCheckoutOkBtnEnabled false
        }

        return@getCheckoutOkBtnEnabled true
    }

    val doCheckoutBranch: suspend (String, String, String, Boolean,Boolean, Int) -> Ret<Oid?> =
        doCheckoutLocalBranch@{ shortBranchNameOrHash: String, fullBranchNameOrHash: String, upstreamBranchShortNameParam: String , force:Boolean, updateHead:Boolean, checkoutType:Int->
            Repository.open(curRepo.fullSavePath).use { repo ->
                val ret = Libgit2Helper.doCheckoutBranchThenUpdateDb(
                    repo,
                    repoId,
                    shortBranchNameOrHash,
                    fullBranchNameOrHash,
                    upstreamBranchShortNameParam,
                    checkoutType,
                    force,
                    updateHead
                )

                return@doCheckoutLocalBranch ret
            }
        }

    //参数1，要创建的本地分支名；2是否基于HEAD创建分支，3如果不基于HEAD，提供一个引用名
    //只有在basedHead为假的时候，才会使用baseRefSpec
    val doCreateBranch: (String, String, Boolean) -> Ret<Triple<String, String, String>?> = doCreateBranch@{ branchNamePram: String, baseRefSpec: String, overwriteIfExisted:Boolean ->
            Repository.open(curRepo.fullSavePath).use { repo ->

                //第4个参数是base head，在提交页面创建，肯定不base head，base head是在分支页面用顶栏的按钮创建分支的默认选项
                val ret = Libgit2Helper.doCreateBranch(
                    activityContext,
                    repo,
                    repoId,
                    branchNamePram,
                    false,
                    baseRefSpec,
                    false,
                    overwriteIfExisted
                )

                return@doCreateBranch ret
            }
        }


    ConfirmDialog(
        title = activityContext.getString(R.string.checkout),
        requireShowTextCompose = true,
        textCompose = {
            //只能有一个节点，因为这个东西会在lambda后返回，而lambda只能有一个返回值，弄两个布局就乱了，和react组件只能有一个root div一个道理 。
            ScrollableColumn {
                Row(modifier = Modifier.padding(5.dp)) {
                    // spacer
                }
                Row {
                    Text(
                        text = activityContext.getString(R.string.checkout_to) + ": ",
                        overflow = TextOverflow.Visible
                    )
                }
                Row(modifier = Modifier.padding(5.dp)) {
                    // spacer
                }
                if(requireUserInputCommitHash) {  // require user input a hash for checkout
                    TextField(
                        modifier = Modifier.fillMaxWidth(),

                        value = checkoutUserInputCommitHash.value,
                        singleLine = true,
                        onValueChange = {
                            checkoutUserInputCommitHash.value = it
                        },
                        label = {
                            Text(stringResource(R.string.target))
                        },
                        placeholder = {
                            Text(stringResource(R.string.hash_branch_tag))
                        },
                    )
                }else {  //long press commit
                    MySelectionContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shortName,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }

                    }
                }


                Row(modifier = Modifier.padding(5.dp)) {
                    // spacer
                }
                Row {
                    Text(text = activityContext.getString(R.string.plz_choose_a_checkout_type) + ":")
                }

                Spacer(Modifier.height(5.dp))

                //单选框，选择检出类型
                SingleSelection(
                    itemList = checkoutRemoteOptions,
                    selected = {idx, item -> checkoutSelectedOption.intValue == idx},
                    text = {idx, item -> item},
                    onClick = {idx, item -> checkoutSelectedOption.intValue = idx},
                    skip = {idx, item -> (idx == checkoutOptionDontUpdateHead && !proFeatureEnabled(dontUpdateHeadWhenCheckoutTestPassed)) || (idx == checkoutOptionJustCheckoutForLocalBranch && !showJustCheckout)}
                )

                //如果选择的是创建分支，显示一个输入框
                if (checkoutSelectedOption.intValue == checkoutOptionCreateBranch) {
                    Row(modifier = Modifier.padding(5.dp)) {
                        // spacer
                    }
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
//                            placeholder = {
//                                Text(stringResource(R.string.branch_name))
//                            }
                    )

                    Spacer(Modifier.height(10.dp))

                    if(isCheckoutRemoteBranch && remotePrefixMaybe.isNotBlank() && remoteBranchShortNameMaybe.isNotBlank()) {
                        MyCheckBox(text = stringResource(R.string.set_upstream), value = setUpstream)
                    }

                    if(proFeatureEnabled(overwriteExistWhenCreateBranchTestPassed)) {
                        MyCheckBox(text = stringResource(R.string.overwrite_if_exist), value = overwriteIfBranchExist)
                        if(overwriteIfBranchExist.value) {
                            MySelectionContainer {
                                Row {
                                    DefaultPaddingText(
                                        text = stringResource(R.string.will_overwrite_if_branch_already_exists),
                                        color = MyStyleKt.TextColor.danger(),
                                    )
                                }
                            }

                        }
                    }

                    if(proFeatureEnabled(dontCheckoutWhenCreateBranchAtCheckoutDialogTestPassed)) {
                        MyCheckBox(text = stringResource(R.string.dont_checkout), value = dontCheckout)
                        if(dontCheckout.value) {
                            MySelectionContainer {
                                Row {
                                    DefaultPaddingText(
                                        text = stringResource(R.string.wont_checkout_only_create_branch),
                                    )
                                }
                            }

                        }
                    }

                    //如果用户选择detach head，显示一个警告，虽然如果丢了数据可以通过reflog恢复，但目前我的app不支持reflog，所以如果丢了，想恢复有点麻烦，得拷贝到电脑用pcgit操作
                } else if (checkoutSelectedOption.intValue == checkoutOptionDetachHead) {
//                        Row(modifier = Modifier.padding(5.dp)) {
//                            // spacer
//                        }
//                        Row {
//                            Text(
//                                text = appContext.getString(R.string.warn_detach_head),
//                                color = Color.Red
//                            )
//                        }

                }

                val showForceCheckout = (!(checkoutSelectedOption.intValue == checkoutOptionCreateBranch && dontCheckout.value)) && proFeatureEnabled(forceCheckoutTestPassed)
                //仅当选中createbranch且勾选dontCheckout时才隐藏force，否则显示
                if(showForceCheckout) {
                    Spacer(Modifier.height(10.dp))

                    // show force checkbox
                    MyCheckBox(text = stringResource(R.string.force), value = forceCheckout)

                    if(forceCheckout.value) {
                        MySelectionContainer {
                            Row {
                                DefaultPaddingText(
                                    text = stringResource(R.string.warn_force_checkout_will_overwrite_uncommitted_changes),
                                    color = MyStyleKt.TextColor.danger(),
                                )
                            }
                        }
                    }
                }

            }
        },
        okBtnText = stringResource(id = R.string.ok),
        cancelBtnText = stringResource(id = R.string.cancel),
//            okBtnEnabled = (!(checkoutSelectedOption.intValue==checkoutOptionCreateBranch && checkoutRemoteCreateBranchName.value.isBlank()) && !(requireUserInputCommitHash.value && checkoutUserInputCommitHash.value.isBlank())),
        okBtnEnabled = getCheckoutOkBtnEnabled(),
        onCancel = {
            showCheckoutDialog.value = false
        }
    ) {  //onOk
        //关弹窗
        showCheckoutDialog.value = false

        //避免用户长按其他条目导致这个动态获取的时候获取错，先把它取出来
        val dontCheckout = dontCheckout.value
        val useUserInputHash = requireUserInputCommitHash
        val checkoutUserInputCommitHash = checkoutUserInputCommitHash.value
        //如果使用用户输入，就使用用户输入；否则，如果请求检出commit，就用commit值；否则当作检出引用（分支 or tag），返回引用名
        val curCommitOidOrRefName = if(useUserInputHash) checkoutUserInputCommitHash else if(expectCheckoutType==Cons.checkoutType_checkoutCommitThenDetachHead) curCommitOid else fullName
        val curCommitShortOidOrShortRefName = if(useUserInputHash) checkoutUserInputCommitHash else if(expectCheckoutType==Cons.checkoutType_checkoutCommitThenDetachHead) curCommitShortOid else shortName
        val localBranchWillCreate = branchName.value
//        val repoFullPath = curRepo.fullSavePath
//                val curRepoId = curRepo.id  //这个和页面的repoId参数一样，所以直接用那个就行
//        val curCommitIndex = if(useUserInputHash || curCommitIndex<0) -1 else curCommitIndex  //用于在checkout长按选择的当前提交失败时更新当前提交信息，如果checkout为分支，其实分两个步骤，一个创建分支，一个checkout分支，若1成功，2失败，则checkout返回失败，但这时当前提交已经改变，至少分支列表会多出刚才创建的那个分支，所以需要重新获取commit信息。如果是用户输入提交号checkout，则无需更新提交条目，此值应设为一个无效索引值，例如-1（当然，用户可能手动输入提交号，而那个提交号显示在列表中，这时其实需要更新那个提交条目的信息，但是无法预判用户会输入什么，而且不更新也问题不大，所以忽略）

//        val repoName = curRepo.repoName

        //detach head和创建新分支都需要更新head，前者HEAD指向提交，后者指向新创建的分支。但如果是不更新head，则不会更新head，只会checkout文件到worktree
        //ps：把需要不需要更新head的条件列出来然后取反比较简单
        val updateHead = !(checkoutSelectedOption.intValue == checkoutOptionDontUpdateHead || (checkoutSelectedOption.intValue == checkoutOptionCreateBranch && dontCheckout))

        //若选择detach head则此变量为true，否则为false，但其实，如果选择Dont update head，此值无意义
//        val isDetachCheckout = checkoutSelectedOption.intValue == checkoutOptionDetachHead  //此值改由根据checkoutType自动判断了，remote就detach，本地就不detach
        //用来更新db分支上游参数的，但是，现在20240822已经是每次查完db后同步真实git仓库数据了，所以此值无所谓了
        val upstreamBranchShortNameParam = ""

        //如果选择新建分支，则类型为LocalBranch（因为这种是先创建本地分支，再checkout本地分支，所以类型为LocalBranch），否则为commit(detach head和dont update head的checkout类型都是commit)
        //checkoutOptionJustCheckoutForLocalBranch选项仅在checkoutLocalBranch为真时才会显示，所以只需要检查是否选择了此项，只要选了就隐含checkoutLocalBranch为真，不需要再检查checkoutLocalBranch的状态
        //如果使用用户输入，一律解析成commit hash处理，但checkout类型不一定是checkoutCommitThenDetachHead，因为有可能用户输入hash同时选择创建分支
        val checkoutType = if (checkoutSelectedOption.intValue == checkoutOptionCreateBranch || checkoutSelectedOption.intValue == checkoutOptionJustCheckoutForLocalBranch) Cons.checkoutType_checkoutRefThenUpdateHead else if(checkoutSelectedOption.intValue==checkoutOptionDetachHead && expectCheckoutType!=Cons.checkoutType_checkoutCommitThenDetachHead) Cons.checkoutType_checkoutRefThenDetachHead else expectCheckoutType



//        条件:
//        don't update head，不需要刷新页面
//
//        detached head 仅当来自repo list才刷新页面，把来自branch list时的is head更新成假
//
//        new branch and 不要检出，仅刷新目标hash关联的提交
//        new branch and checked overwrite if exist，刷新旧包含分支的提交和新的目标提交
//        new branch且不勾选不要检出，如果来自branch list，把is head更新为假

        val headWillChange = checkoutSelectedOption.intValue.let { it == checkoutOptionDetachHead || (it == checkoutOptionCreateBranch && !dontCheckout) }


        //后来添加了创建分支但不checkout，这种情况也仅需更新当前条目不用刷新完整页面，设置一下，不过目前（20240822）仅commitlist页面需要刷新单条目，分支和tag页面checkout完后都是直接刷新整个页面
//        val onlyUpdateCurItem = onlyUpdateCurItem || (checkoutSelectedOption.intValue == checkoutOptionCreateBranch && dontCheckout && from != CheckoutDialogFrom.BRANCH_LIST)

        //执行checkout
        doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.checking_out)) {
            try {

                //如果使用用户输入值，一律解析成hash
                val curCommitOidOrRefName =  if(useUserInputHash) {  //如果选的不是基于HEAD创建提交，查询一下
                    var r = checkoutUserInputCommitHash
                    //解析一下refspec
                    Repository.open(curRepo.fullSavePath).use { repo->
                        val ret = Libgit2Helper.resolveCommitByHashOrRef(repo, checkoutUserInputCommitHash)
                        if(ret.success() && ret.data != null) {  //如果查询成功，取下查出的commit id，如果查询失败，还用原来的值就行，当然，后面很可能会执行失败，不过无所谓
                            r = ret.data!!.id().toString()
                        }else {  //解析用户输入失败，后面没法进行，直接抛异常
                            throw RuntimeException(activityContext.getString(R.string.failed_resolve_commit_by_user_input))
                        }
                    }

                    r
                }else {
                    curCommitOidOrRefName
                }

                //检查用户选的是detached还是创建本地分支，如果是detached，直接checkout；
                // 如果是create branch，先基于当前选中的远程分支最新提交号，创建一个本地分支，然后更新配置文件把它的上游设置为这个远程分支，然后再checkout本地分支并更新数据库的upstream
//                    splitRemoteAndBranchFromRemoteShortRefName //获得 remote和分支名
//                    getRefsHeadsBranchFullRefSpecFromShortRefSpec  //生成 refs/heads 分支名
                //checkout detached head or dont update head，这俩的共同点是都不需要创建分支，可直接checkout
                if (checkoutSelectedOption.intValue == checkoutOptionDetachHead
                    || checkoutSelectedOption.intValue == checkoutOptionDontUpdateHead
                ) {
                    MyLog.d(TAG, "checkout commit as detached head: commitOidOrRefName=$curCommitOidOrRefName, fullOid=$curCommitOid")
                    //第一个参数是短分支名，但detached head不用传，会直接用hash
                    //远程分支，避免歧义，第二个查找用的分支名传长名
                    //第3个参数是upstream，detach head 时 upstream 肯定是空
                    //参数4指示detach checkout
                    val checkoutRet = doCheckoutBranch(curCommitShortOidOrShortRefName, curCommitOidOrRefName, upstreamBranchShortNameParam, forceCheckout.value, updateHead, checkoutType)
                    if(checkoutRet.success()) {
//                            fullOid.value = commitOid
//                            branchShortNameOrShortHashByFullOid.value = Libgit2Helper.getShortOidStrByFull(commitOid)  //这里加不加detached都行，因为这个是点击分支条目显示的分支列表，含义上来说，不一定与repo HEAD关联，以后可能实现成可基于某个提交为头查看其后的提交历史，所以虽然是checkout了提交号，但在含义上来说，这里显示的未必就是detached head，所以不必加Detached标识
                        curRepo.isDetached = Cons.dbCommonTrue
                        Msg.requireShow(activityContext.getString(R.string.checkout_success))
                    }else {  //checkout失败
                        val checkoutErrMsg = activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg
                        throw RuntimeException(checkoutErrMsg)
                    }

                    // checkout create branch
                } else if(checkoutSelectedOption.intValue == checkoutOptionCreateBranch){
                    //创建分支，应该用oid而不是引用名，若启用hash输入框，则使用用户输入的内容，因为不确定用户输入什么，所以采用的是解析commit oid或ref皆可的函数
                    val baseCommitOid = if(useUserInputHash) curCommitOidOrRefName else curCommitOid
                    MyLog.d(TAG, "checkout commit to new local branch: localBranchWillCreate=$localBranchWillCreate, baseCommitOid=$baseCommitOid")

                    //参数1，要创建的本地分支名；2是否基于HEAD创建分支，3如果不基于HEAD，提供一个引用名
                    val createBranchRet = doCreateBranch(localBranchWillCreate, baseCommitOid, overwriteIfBranchExist.value)  //创建分支
                    if (createBranchRet.success()) {
                        val (fullBranchRefspec, branchShortName, branchHeadFullHash) = createBranchRet.data!!  //第一个返回值是长分支名，二是短分支名，三是分支头hash

                        //检出远程分支勾选了设置上游并且存在可能有效的remote名
                        if(setUpstream.value) {
                            if(remotePrefixMaybe.isNotBlank()) {
                                if(remoteBranchShortNameMaybe.isNotBlank()) {
                                    //检出远程仓库时，这个值是上游分支去除remote后的短名
                                    Repository.open(curRepo.fullSavePath).use { repo->
                                        val success = Libgit2Helper.setUpstreamForBranchByRemoteAndRefspec(
                                            repo,
                                            remotePrefixMaybe,
                                            Libgit2Helper.getRefsHeadsBranchFullRefSpecFromShortRefSpec(remoteBranchShortNameMaybe),
                                            branchShortName
                                        )

                                        if(!success) {
                                            Msg.requireShowLongDuration(activityContext.getString(R.string.set_upstream_error))
                                        }  // else set upstream 成功就不显示通知了，这个操作显示的通知已经够多了
                                    }
                                }
                            }else { // remote is blank, can't set upstream for branch
                                Msg.requireShowLongDuration(activityContext.getString(R.string.set_upstream_err_remote_is_invalid))
                            }
                        }



                        Msg.requireShow(activityContext.getString(R.string.create_branch_success))

                        if(dontCheckout) {
                            refreshPage(false, branchHeadFullHash, overwriteIfBranchExist.value, localBranchWillCreate)

                            return@doJobThenOffLoading
                        }

                        Msg.requireShow(activityContext.getString(R.string.checking_out))

                        //检出新创建的本地分支
                        val checkoutRet = doCheckoutBranch(branchShortName, fullBranchRefspec, upstreamBranchShortNameParam, forceCheckout.value, updateHead, checkoutType)
                        if(checkoutRet.success()) {
//                                fullOid.value = branchHeadFullHash
//                                branchShortNameOrShortHashByFullOid.value = branchShortName
                            curRepo.isDetached = Cons.dbCommonFalse
                            Msg.requireShow(activityContext.getString(R.string.checkout_success))
                        }else {  //checkout失败
                            val checkoutErrMsg = activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg
                            throw RuntimeException(checkoutErrMsg)
                        }
                    }else {  //创建分支失败
                        val createBranchErrMsg = activityContext.getString(R.string.create_branch_err)+": "+createBranchRet.msg
                        throw RuntimeException(createBranchErrMsg)
                    }

                }else if(checkoutSelectedOption.intValue == checkoutOptionJustCheckoutForLocalBranch){
//                    val upstreamRefspec = curObjInPage.value.upstream?.remoteBranchShortRefSpec?:""
                    val upstreamRefspec = ""  //这个不更新应该也行，从数据库查仓库信息时会自动更新为实际的git仓库的信息
                    val isLocal=true
                    //第一个参数是分支名，会写入数据库，然后显示，所以最好用短名
                    //第二个参数用来查找分支(Reference)，最好用长名
                    //shortBranchNameOrHash: String, fullBranchNameOrHash: String, upstreamBranchShortNameParam: String, isDetachCheckout: Boolean , force:Boolean, updateHead:Boolean, checkoutType:Int->
                    //这种情况是直接检出本地分支，只有在分支列表长按分支一种处发方式，直接使用传进来的引用名fullName执行checkout即可
                    val checkoutRet = doCheckoutBranch(shortName, fullName, upstreamRefspec, forceCheckout.value, updateHead, checkoutType)
                    if(checkoutRet.success()) {
                        Msg.requireShow(activityContext.getString(R.string.checkout_success))
                    }else {  //checkout失败
                        val checkoutErrMsg = activityContext.getString(R.string.checkout_error)+": "+checkoutRet.msg
                        throw RuntimeException(checkoutErrMsg)
                    }
                }

                if(headWillChange) {
                    headChangedCallback()
                }

                //checkout成功强制刷新页面，失败的话，在上面就返回了，所以不会刷新页面
                if(checkoutSelectedOption.intValue != checkoutOptionDontUpdateHead) {  //从仓库卡片点击提交号进入的，操作成功直接刷新页面即可
                    val headOidStr = Repository.open(curRepo.fullSavePath).use { repo -> Libgit2Helper.resolveHEAD(repo)?.id()?.toString()?:"" }
                    refreshPage(true, headOidStr, overwriteIfBranchExist.value, localBranchWillCreate)
                }

//                    requireShowToast(appContext.getString(R.string.checkout_success))  // doCheckoutBranch里已经显示了成功通知了
            } catch (e: Exception) {
                //checkout失败不刷新页面，这样用户还能看到当前的commit列表，
                // 说不定他想checkout其他提交呢？但一刷新，可能就没了，
                // 说不准，如果checkout失败，有可能列表会变，有可能不会变，
                // 要不还是刷新一下？，算了，感觉checkout失败不刷新比较合理，
                // 不然一失败，列表滚动到头部，若用户在非头部的commit上，
                // 会感觉这滚动有点莫名其妙，想再找之前的commit也不好找。

                //checkout如果失败，重新获取一下当前checkout的提交号的信息，因为有可能checkout时有可能某个阶段失败，但提交已经被改变，例如：创建分支成功，但后续的checkout失败，这时候其实这个提交已经有关联的分支了
                refreshPage(false, curCommitOidOrRefName, overwriteIfBranchExist.value, localBranchWillCreate)


                //显示通知
                Msg.requireShowLongDuration("err: " + e.localizedMessage)
                val refName = if(shortName.isNotBlank() && shortName==curCommitOidOrRefName) shortName else "$shortName($curCommitOidOrRefName)"
                //给用户看的错误
                //"checkout main(abcdef1) err" or "checkout abcef12 err"
                createAndInsertError(repoId, "checkout '" + refName + "' err: " + e.localizedMessage)
                //给开发者debug看的错误
                MyLog.e(TAG, "checkout '" + refName + "' err: " + e.stackTraceToString())
            }
        }
    }
}

enum class CheckoutDialogFrom {
    OTHER,
    BRANCH_LIST,  //只有branch list页面在选择创建分支且不checkout时需要刷新整个页面以显示新添加的分支
//    TAG_LIST,
//    REFLOG_LIST,
//    COMMIT_LIST
}
