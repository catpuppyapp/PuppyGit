package com.catpuppyapp.puppygit.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.ConfirmDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.CopyScrollableColumn
import com.catpuppyapp.puppygit.compose.DefaultPaddingRow
import com.catpuppyapp.puppygit.compose.DepthTextField
import com.catpuppyapp.puppygit.compose.InternalFileChooser
import com.catpuppyapp.puppygit.compose.LoadingDialog
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PasswordTextFiled
import com.catpuppyapp.puppygit.compose.SingleSelectList
import com.catpuppyapp.puppygit.compose.SingleSelection
import com.catpuppyapp.puppygit.compose.TokenInsteadOfPasswordHint
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.SpecialCredential
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.dev_EnableUnTestedFeature
import com.catpuppyapp.puppygit.dev.shallowAndSingleBranchTestPassed
import com.catpuppyapp.puppygit.dto.NameAndPath
import com.catpuppyapp.puppygit.dto.NameAndPathType
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.title.ScrollableTitle
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.boolToDbInt
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.checkFileOrFolderNameAndTryCreateFile
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.filterAndMap
import com.catpuppyapp.puppygit.utils.getRepoNameFromGitUrl
import com.catpuppyapp.puppygit.utils.isPathExists
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.storagepaths.StoragePathsMan
import com.catpuppyapp.puppygit.utils.withMainContext
import java.io.File

private const val TAG = "CloneScreen"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneScreen(
    // if id is blank or null path to "CloneScreen/null", else path to "CloneScreen/repoId"
    //repoId传null，等于新建模式；非null repoId则代表编辑对应repo
    repoId: String,

    naviUp: () -> Boolean,
) {

    val stateKeyTag = Cache.getSubPageKey(TAG)


    val activityContext = LocalContext.current
    val inDarkTheme = Theme.inDarkTheme


    val isEditMode = repoId.isNotBlank() && repoId != Cons.dbInvalidNonEmptyId
    val repoFromDb = mutableCustomStateOf(keyTag=stateKeyTag, keyName = "repoFromDb", initValue = RepoEntity(id = ""))
    //克隆完成后更新此变量，然后在重新渲染时直接返回。（注：因为无法在coroutine里调用naviUp()，所以才这样实现“存储完成返回上级页面”的功能）
//    val isTimeNaviUp = rememberSaveable { mutableStateOf(false) }
//
//    if(isTimeNaviUp.value) {
//        naviUp()
//    }

//    val userIsPro = UserInfo.isPro()

    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior

    val allRepoParentDir = AppModel.allRepoParentDir

    val gitUrl = rememberSaveable { mutableStateOf("")}
//    val repoName = remember { mutableStateOf(TextFieldValue("")) }
    val repoName = mutableCustomStateOf(keyTag=stateKeyTag, keyName = "repoName",  initValue = TextFieldValue(""))
    val branch = rememberSaveable { mutableStateOf("")}
    val depth = rememberSaveable { mutableStateOf("")}  //默认depth 为空，克隆全部；不为空则尝试解析，大于0，则传给git；小于0则克隆全部
//    val credentialName = remember { mutableStateOf(TextFieldValue("")) }  //旋转手机，画面切换后值会被清，因为不是 rememberSaveable，不过rememberSaveable不适用于TextFieldValue，所以改用我写的自定义状态存储器了
    val credentialName = mutableCustomStateOf(keyTag=stateKeyTag, keyName = "credentialName", initValue = TextFieldValue(""))
    val credentialVal = rememberSaveable { mutableStateOf("")}
    val credentialPass = rememberSaveable { mutableStateOf("")}

    val gitUrlType = rememberSaveable { mutableIntStateOf(Cons.gitUrlTypeHttp) }

    val curCredentialType = rememberSaveable { mutableIntStateOf(Cons.dbCredentialTypeHttp) }
//    val credentialListHttp = MockData.getAllCredentialList(type = Cons.dbCredentialTypeHttp)
//    val credentialListSsh = MockData.getAllCredentialList(type = Cons.dbCredentialTypeSsh)
    val allCredentialList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "allCredentialList", initValue = listOf<CredentialEntity>())
    val selectedCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "selectedCredential", initValue = CredentialEntity(id = ""))
//    val credentialHttpList = mutableCustomStateListOf(keyTag=stateKeyTag, keyName = "credentialHttpList", initValue = listOf<CredentialEntity>())
//    val credentialSshList = mutableCustomStateListOf(keyTag=stateKeyTag, keyName = "credentialSshList", initValue = listOf<CredentialEntity>())
    //这个用我写的自定义状态存储器没意义，因为如果屏幕旋转（手机的显示设置改变），本质上就会重新创建组件，重新加载列表，除非改成如果列表不为空，就不查询，但那样意义不大
//    val curCredentialList:SnapshotStateList<CredentialEntity> = remember { mutableStateListOf() }  //切换http和ssh后里面存对应的列表


    //获取输入焦点，弹出键盘
    val focusRequesterGitUrl = remember { FocusRequester() }  // 1
    val focusRequesterRepoName = remember { FocusRequester() }  // 2
    val focusRequesterCredentialName = remember { FocusRequester() }  // 3
    val focusToNone = 0
    val focusToGitUrl = 1
    val focusToRepoName = 2
    val focusToCredentialName = 3
    val requireFocusTo = rememberSaveable{ mutableIntStateOf(focusToNone) }  //初始值0谁都不聚焦，修改后的值： 1聚焦url；2聚焦仓库名；3聚焦凭据名

    val noCredential = stringResource(R.string.no_credential)
    val newCredential = stringResource(R.string.new_credential)
    val selectCredential = stringResource(R.string.select_credential)
    val matchCredentialByDomain = stringResource(R.string.match_credential_by_domain)

    val optNumNoCredential = 0  //这个值就是对应的选项在选项列表的索引
    val optNumNewCredential = 1
    val optNumSelectCredential = 2
    val optNumMatchCredentialByDomain = 3
    val credentialRadioOptions = listOf(noCredential, newCredential, selectCredential, matchCredentialByDomain)  // 编号: 文本
    val (credentialSelectedOption, onCredentialOptionSelected) = rememberSaveable{ mutableIntStateOf(optNumNoCredential) }

    val (isRecursiveClone, onIsRecursiveCloneStateChange) = rememberSaveable { mutableStateOf(false)}
    val (isSingleBranch, onIsSingleBranchStateChange) = rememberSaveable { mutableStateOf(false)}

    val isReadyForClone = rememberSaveable { mutableStateOf(false)}

    val passwordVisible =rememberSaveable { mutableStateOf(false)}


    val showRepoNameAlreadyExistsErr = rememberSaveable { mutableStateOf(false)}
    val showCredentialNameAlreadyExistsErr =rememberSaveable { mutableStateOf(false)}
    val showRepoNameHasIllegalCharsOrTooLongErr = rememberSaveable { mutableStateOf(false)}

    val updateRepoName:(TextFieldValue)->Unit = {
        val newVal = it
        val oldVal = repoName.value

        //只有当值改变时，才解除输入框报错
        if(oldVal.text != newVal.text) {
            //用户一改名，就取消字段错误设置，允许点击克隆按钮，点击后再次检测，有错再设置为真
            showRepoNameAlreadyExistsErr.value = false
            showRepoNameHasIllegalCharsOrTooLongErr.value = false
        }

        //这个变量必须每次都更新，不能只凭text是否相等来判断是否更新此变量，因为选择了哪些字符、光标在什么位置 等信息也包含在这个TextFieldValue对象里
        repoName.value = newVal

    }
    val updateCredentialName:(TextFieldValue)->Unit = {
        val newVal = it
        val oldVal = credentialName.value

        if(oldVal.text != newVal.text) {
            if (showCredentialNameAlreadyExistsErr.value) {
                showCredentialNameAlreadyExistsErr.value = false
            }
        }

        credentialName.value = newVal
    }
    val focusRepoName:()->Unit = {
        //全选输入框字符
        val text = repoName.value.text
        repoName.value = repoName.value.copy(
            selection = TextRange(0, text.length)
        )

        //聚焦输入框，弹出键盘（如果本身光标就在输入框，则不会弹出键盘）
//        focusRequesterRepoName.requestFocus()
        requireFocusTo.intValue = focusToRepoName
    }
    val setCredentialNameExistAndFocus:()->Unit = {
        //设置错误state
        showCredentialNameAlreadyExistsErr.value=true

        //全选输入框字符
        val text = credentialName.value.text
        credentialName.value = credentialName.value.copy(
            //设置选择范围
            selection = TextRange(0, text.length)
        )

        //另一种写法，测试过，可行，上面的copy方法内部其实就是这么实现的，差不多
//        credentialName.value = TextFieldValue(text = credentialName.value.text,
//                                          selection = TextRange(0, credentialName.value.text.length)
//                                          )

        //聚焦输入框，弹出键盘（如果本身光标就在输入框，则不会弹出键盘）
//        focusRequesterCredentialName.requestFocus()
        requireFocusTo.intValue = focusToCredentialName
    }


    //vars of storage select begin
//    val settings = SettingsUtil.getSettingsSnapshot()
    val getStoragePathList = {
        // internal storage at first( index 0 )
//        val list = mutableListOf<String>(appContext.getString(R.string.internal_storage))
        val list = mutableListOf<NameAndPath>(NameAndPath(activityContext.getString(R.string.internal_storage), allRepoParentDir.canonicalPath, NameAndPathType.APP_ACCESSIBLE_STORAGES))

        // add other paths if have
        list.addAll(StoragePathsMan.get().storagePaths.map { NameAndPath.genByPath(it, NameAndPathType.REPOS_STORAGE_PATH, activityContext) })

        list
    }

    val storagePathList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "storagePathList", initValue = getStoragePathList())

    val storagePathSelectedPath = rememberSaveable { mutableStateOf(
        StoragePathsMan.get().storagePathLastSelected.let { selectedPath ->
            storagePathList.value.find { it.path == selectedPath } ?: storagePathList.value.getOrNull(0) ?: NameAndPath()
        }
    )}

    val storagePathSelectedIndex = rememberSaveable{ mutableIntStateOf(
        try {
            // if not found, just use first item, it is must existed internal storage
            storagePathList.value.indexOfFirst { storagePathSelectedPath.value.path == it.path }.coerceAtLeast(0)
        }catch (_: Exception) {
            // 0 to select app internal repos storage path
            0
        }
    )}

    val showAddStoragePathDialog = rememberSaveable { mutableStateOf(false)}

    val storagePathForAdd = rememberSaveable { SharedState.fileChooser_DirPath }
//    val safEnabledForSystemFolderChooser = rememberSaveable { mutableStateOf(false)}
//    val safPath = rememberSaveable { mutableStateOf("") }
//    val nonSafPath = rememberSaveable { mutableStateOf("") }

    val findStoragePathItemByPath = { path:String ->
        var ret = Pair<Int, NameAndPath?>(-1, null)
        for((idx, item) in storagePathList.value.withIndex()) {
            if(item.path == path) {
                ret = Pair(idx, item)
                break
            }
        }
        ret
    }

    //vars of  storage select end


    if(showAddStoragePathDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.add_storage_path),
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState())
                    ) {
                        InternalFileChooser(activityContext, path = storagePathForAdd)
                    }
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = storagePathForAdd.value.isNotBlank(),
            onCancel = {
                showAddStoragePathDialog.value = false

                // even close, still need reload list, because users maybe add storage path in the FileChooser Screen
                getStoragePathList().let { latestList ->
                    storagePathList.value.apply {
                        clear()
                        addAll(latestList)
                    }
                }
            },
        ) {
            //关弹窗
            showAddStoragePathDialog.value = false

            val storagePathForAdd = storagePathForAdd.value

            doJobThenOffLoading {
                try {
                    val newPathRet = FsUtils.userInputPathToCanonical(storagePathForAdd)

                    if(newPathRet.hasError()) {
                        throw RuntimeException(activityContext.getString(R.string.invalid_path))
                    }


                    val newPath = newPathRet.data!!

                    if(File(newPath).isDirectory.not()) {
                        throw RuntimeException(activityContext.getString(R.string.path_is_not_a_dir))
                    }

                    // here don't need check this, because storage paths included internal storage, so add it just let it be selected, no bad effect
//                    if(StoragePathsMan.allowAddPath(newPath).not()) {
//                        throw RuntimeException("The path disallowed to add")
//                    }

                    // reload the storage path list, else, if added path in the File Chooser, here will lost them
                    val latestList = getStoragePathList()
                    val storagePathList = storagePathList.value
                    storagePathList.clear()
                    storagePathList.addAll(latestList)

                    // used to save path
                    val spForSave = StoragePathsMan.get()


                    // add to list
                    val (indexOfStoragePath, existedStoragePath) = findStoragePathItemByPath(newPath)

                    if(indexOfStoragePath != -1) { // contains, only need update last selected
                        storagePathSelectedPath.value = existedStoragePath!!
                        storagePathSelectedIndex.intValue = indexOfStoragePath

                        spForSave.storagePathLastSelected = newPath
                    }else { // not contains, need add to config
                        val newItem = NameAndPath.genByPath(newPath, NameAndPathType.REPOS_STORAGE_PATH, activityContext)
                        storagePathList.add(newItem)
                        val newItemIndex = storagePathList.size - 1
                        // select new added
                        storagePathSelectedIndex.intValue = newItemIndex
                        storagePathSelectedPath.value = newItem

                        // update settings
                        spForSave.storagePaths.add(newPath)
                        spForSave.storagePathLastSelected = newPath
                    }

                    StoragePathsMan.save(spForSave)
                }catch (e: Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.e(TAG, "add storage path at `$TAG` err: ${e.stackTraceToString()}")
                }
            }

        }
    }

    val indexForDeleteStoragePathDialog = rememberSaveable { mutableStateOf(-1) }
    val showDeleteStoragePathListDialog = rememberSaveable { mutableStateOf(false) }
    val initDeleteStoragePathListDialog = { index:Int ->
        indexForDeleteStoragePathDialog.value = index
        showDeleteStoragePathListDialog.value = true
    }

    if(showDeleteStoragePathListDialog.value) {
        val targetPath = storagePathList.value.getOrNull(indexForDeleteStoragePathDialog.value)?.path ?: ""

        val closeDialog = { showDeleteStoragePathListDialog.value = false }

        val deleteStoragePath = j@{ index:Int ->
            if(storagePathList.value.getOrNull(index)?.type != NameAndPathType.REPOS_STORAGE_PATH) {
                Msg.requireShowLongDuration("can't remove item")
                return@j
            }

            storagePathList.value.removeAt(index)
            val spForSave = StoragePathsMan.get()
            val removedCurrent = index == storagePathSelectedIndex.intValue
            if(removedCurrent) {
                val newCurrentIndex = 0
                storagePathSelectedIndex.intValue = newCurrentIndex
                val newCurrent = storagePathList.value[newCurrentIndex]
                storagePathSelectedPath.value = newCurrent
                spForSave.storagePathLastSelected = newCurrent.path
            }

            spForSave.storagePaths.clear()
            val list = storagePathList.value.filterAndMap({ it.type == NameAndPathType.REPOS_STORAGE_PATH }) { it.path }
            if(list.isNotEmpty()) {
                spForSave.storagePaths.addAll(list)
            }

            StoragePathsMan.save(spForSave)
        }

        ConfirmDialog2(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Text(targetPath)
                }
            },
            okBtnText = stringResource(R.string.delete),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = closeDialog
        ) {
            closeDialog()

            val targetIndex = indexForDeleteStoragePathDialog.value
            doJobThenOffLoading {
                deleteStoragePath(targetIndex)
            }
        }
    }

    val showLoadingDialog = rememberSaveable { mutableStateOf(false)}

    val doSave:()->Unit = {
        /*查询repo名以及repo在仓库存储目录是否已经存在，若存在，设 isReadyForClone为假，调用setRepoNameExistAndFocus()提示用户改名
            查询credentialName是否已经存在，若存在，设 isReadyForClone为假，调用setCredentialNameExistAndFocus()提示用户改名
            通过检测以后，若是newCredential则存储credential
            存储仓库信息，设置仓库状态为notReadyNeedClone，然后返回仓库页面

            下一步，但不在此页面执行：仓库页面检查仓库状态，对所有状态为notReadyNeedClone的仓库执行clone（可能会发生一个克隆未完成就执行另一个克隆的问题，需要考虑下怎么解决）
        */


        doJobThenOffLoading launch@{
            showLoadingDialog.value=true

            val repoNameText = repoName.value.text
            //检查是否存在非法字符，例如路径分隔符\:之类的
            val repoNameCheckRet = checkFileOrFolderNameAndTryCreateFile(repoNameText, activityContext)
            if(repoNameCheckRet.hasError()) {
                Msg.requireShowLongDuration(repoNameCheckRet.msg)

                focusRepoName()
                showRepoNameHasIllegalCharsOrTooLongErr.value=true
                showLoadingDialog.value=false
                return@launch
            }

            val repoDb = AppModel.dbContainer.repoRepository
            val credentialDb = AppModel.dbContainer.credentialRepository

//            val fullSavePath = if(storagePathSelectedIndex.intValue == 0) { // internal storage
//                allRepoParentDir.canonicalPath+ File.separator +repoNameText
//            }else { // external storage, -1 or non-zero index, -1 only occured when edited mode, the fullpath in db but is not in list
//                storagePathSelectedPath.value.removeSuffix(File.separator) + File.separator + repoNameText
//            }

            val fullSavePath = File(storagePathSelectedPath.value.path, repoNameText).canonicalPath

            //如果不是编辑模式 或者 是编辑模式但用户输入的仓库名不是当前仓库已经保存的名字（用户修改了仓库名） 则 检查仓库名和文件夹是否已经存在
            //判断数据库是否已经存在相同名字的条目
            val isRepoNameExist = if(!isEditMode || repoNameText != repoFromDb.value.repoName) {
                //检查仓库名是否已经存在
                repoDb.isRepoNameExist(repoNameText)
            }else {
                //否则直接当不存在（允许克隆）
                false
            }

            //仓库在数据库存在或者路径已经存在，则报错
            if(isRepoNameExist || isPathExists(null, fullSavePath)) {  //无论是否编辑模式，都需要判断下isPathExists()不然会错误覆盖其他目录
                focusRepoName()
                showRepoNameAlreadyExistsErr.value=true
                showLoadingDialog.value=false
                return@launch
            }

            var credentialIdForClone = ""  //如果选的是 no credential，则就是这个值，否则，会在下面的判断里更新其值为新增或选择的credentialid

            //如果选择的是新建Credential，则新建
            var credentialForSave:CredentialEntity? = null
            if(credentialSelectedOption==optNumNewCredential) {
                val credentialNameText = credentialName.value.text
                val isCredentialNameExist = credentialDb.isCredentialNameExist(credentialNameText)
                if(isCredentialNameExist) {
                    setCredentialNameExistAndFocus()
                    showLoadingDialog.value=false
                    return@launch
                }

                credentialForSave = CredentialEntity(
                    name = credentialNameText,
                    value = credentialVal.value,
                    pass = credentialPass.value,
                    type = curCredentialType.intValue,
                )
                credentialDb.insertWithEncrypt(credentialForSave)

                //为仓库更新credentialId
                credentialIdForClone = credentialForSave.id
            } else if(credentialSelectedOption == optNumSelectCredential) {
                credentialIdForClone = selectedCredential.value.id
            } else if(credentialSelectedOption == optNumMatchCredentialByDomain) {
                credentialIdForClone = SpecialCredential.MatchByDomain.credentialId
            }



            var intDepth = 0
            var isShallow = Cons.dbCommonFalse
            if(depth.value.isNotBlank()) {
                try {  //如果在这不出错，intDepth大于等于0
                    //虽然输入限制了仅限数字，但用户依然可以粘贴非数字内容，所以parse还是有可能出错，因此需要try catch
                    //注：toInt内部调用的其实还是 Integer.parseInt()
                    //注：coerceAtLeast(0)确保解析出的数字不小于0
                    intDepth = depth.value.toInt().coerceAtLeast(0)
                }catch (e:Exception) {  //如果try代码块出错，intDepth将等于0
                    intDepth = 0
                    Log.d(TAG,"invalid depth value '${depth.value}', will use default value '0', err=${e.localizedMessage}")
                }

                //执行到这intDepth必然大于等于0，所以不需再判断
//                intDepth = if(intDepth>0) intDepth else 0  //避免intDepth小于0

                //执行到这intDepth必然大于等于0，等于0等于非shallow，大于0等于shallow(暂且等于，实际上如果其值大于所有提交数，最终仓库依然是非shallow状态)
                if(intDepth > 0) {  //注：这里的状态只是预判，如果depth大于仓库实际的提交数，克隆后仓库依然是非shallow的，isShallow也会被更新为假，可通过检测仓库.git目录是否存在shallow文件来判断仓库是否处于shallowed状态，我已经在克隆仓库实现了这个功能
                    isShallow = Cons.dbCommonTrue
                }
            }

            //这里不用判断repoFromDb.id，如果没成功更新repoFromDb为数据库中的值，那它的id会是空字符串，不会匹配到任何记录，而isEditMode为true时，会执行update操作，是按id匹配的，所以，最终不会影响任何数据，顶多就是用户输入的内容没保存上而已。
            val repoForSave:RepoEntity = if(isEditMode) repoFromDb.value else RepoEntity(createBy = Cons.dbRepoCreateByClone)
            //设置repo字段
            repoForSave.repoName = repoNameText
            repoForSave.fullSavePath = fullSavePath
            repoForSave.cloneUrl = gitUrl.value
            repoForSave.workStatus = Cons.dbRepoWorkStatusNotReadyNeedClone
            repoForSave.credentialIdForClone = credentialIdForClone
            repoForSave.isRecursiveCloneOn = boolToDbInt(isRecursiveClone)
            repoForSave.depth = intDepth
            repoForSave.isShallow = isShallow

            //设置分支和singlebranch字段
            //设置分支
            if(branch.value.isNotBlank()) {  //只有分支字段不为空时，才存储isSingleBranch的值，否则强制把isSingleBranch设置为关闭
                repoForSave.branch=branch.value
                repoForSave.isSingleBranch=boolToDbInt(isSingleBranch)
            }else{  //没填branch
                repoForSave.branch = ""
                repoForSave.isSingleBranch=Cons.dbCommonFalse  //没填branch，忽略isSingleBranch状态的值，强制设置为false
            }

            //编辑模式，更新，否则插入
            if(isEditMode){
                repoDb.update(repoForSave)
            }else{
                repoDb.insert(repoForSave)
            }

            showLoadingDialog.value=false

            //设置此变量，下次重新渲染就会直接返回上级页面了（之前写这个是因为在协程返回上级页面会报错，后来发现withMainContext就行了，所以不需要这个了）
//            isTimeNaviUp.value = true

            withMainContext {
                naviUp()
            }
        }
    }


    val loadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading))}
    val listState = rememberScrollState()
    val lastPosition = rememberSaveable { mutableStateOf(0) }



    val spacerPadding = 2.dp
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    ScrollableTitle(stringResource(R.string.clone), listState, lastPosition)
                },
                navigationIcon = {
                    LongPressAbleIconBtn(
                        tooltipText = stringResource(R.string.back),
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        iconContentDesc = stringResource(R.string.back),

                        ) {
                        naviUp()
                    }
                },
                actions = {
                    LongPressAbleIconBtn(
                        tooltipText = stringResource(R.string.save),
                        icon =  Icons.Filled.Check,
                        iconContentDesc = stringResource(id = R.string.save),
                        enabled = isReadyForClone.value,

                        ) {
                        doSave()
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
    ) { contentPadding->
        //遮罩loading，这里不用做if loading else show page 的判断，直接把loading遮盖在页面上即可
//        showLoadingDialog.value=true  //test
        if (showLoadingDialog.value) {
            LoadingDialog(loadingText.value)
        }

        Column (
            modifier = Modifier
                .baseVerticalScrollablePageModifier(contentPadding, listState)
                .padding(bottom = MyStyleKt.Padding.PageBottom)
                .imePadding()
            ,
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding)
                    .focusRequester(focusRequesterGitUrl),
                singleLine = true,

                value = gitUrl.value,
                onValueChange = {
                    gitUrl.value = it
                    val repoNameFromGitUrl = getRepoNameFromGitUrl(it)
                    // 若仓库名为空，更新为url中的名字
                    if(repoNameFromGitUrl.isNotBlank() && repoName.value.text.isBlank()) {
                        updateRepoName(TextFieldValue(repoNameFromGitUrl))
                    }

                    //获取当前凭据类型并检查是否发生了变化，如果变化，需要清些字段
                    val newGitUrlType = Libgit2Helper.getGitUrlType(it)  //获取当前url类型（http or ssh）

                    // 20240414 废弃ssh支持，修改开始
                    val newCredentialType = Libgit2Helper.getCredentialTypeByGitUrlType(newGitUrlType)  //根据url类型获取credential类型（http or ssh）  //ssh
//                    val newCredentialType = Cons.dbCredentialTypeHttp  //nossh
                    // 20240414 废弃ssh支持，修改结束


                    // 20251101更新：由于目前实际上架空了凭据类型字段，在连接远程仓库时会自动url类型决定创建http还是ssh凭据对象，因此不需要判断这个了
                    // 开始：url类型改变，则清除已选择凭据并选中无凭据
//                    val oldCredentialType = curCredentialType.intValue
//                    if(newCredentialType != oldCredentialType) {  //为true代表url类型改变了，credential类型也需要跟着改变，并且重置一些状态变量
//                        //选择凭据相关字段，这两个有必要清，因为类型一换，凭据列表就变了，而且不同类型的凭据也不通用，所以这个得在凭据类型改变时清一下
//                        selectedCredential.value = CredentialEntity(id = "")
//                        //如果url类型改变 且 凭据选的是选择凭据，则将其改为无凭据，因为ssh和http的凭据不通用
//                        if(credentialSelectedOption == optNumSelectCredential) {
//                            //如果当前是'选择凭据'，则改成'无凭据'（若是无凭据或新建凭据，则不执行操作）
//                            onCredentialOptionSelected(optNumNoCredential)
//                        }
//                    }
                    // 结束

                    //更新状态，最好在最后更新状态，感觉在上面更新，如果渲染周期。。。不，应该也不会有问题，总之就在这更新吧
                    //更新凭据类型和giturl状态变量
                    curCredentialType.intValue = newCredentialType
                    gitUrlType.intValue = newGitUrlType

                },
                label = {
                    Row {
                        Text(stringResource(R.string.git_url))
                        Text(text = " ("+stringResource(id = R.string.http_https_ssh)+")",
//                            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, end = 10.dp),
//                            fontSize = 11.sp

                        )

                    }
                },
                placeholder = {
                    Text(stringResource(R.string.git_url_placeholder))
                }
            )
            Spacer(modifier = Modifier.padding(spacerPadding))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding)
                    .focusRequester(focusRequesterRepoName)
                ,
                value = repoName.value,
                singleLine = true,
                isError = showRepoNameAlreadyExistsErr.value || showRepoNameHasIllegalCharsOrTooLongErr.value,
                supportingText = {
                    val errMsg = if(showRepoNameAlreadyExistsErr.value) stringResource(R.string.repo_name_exists_err)
                                else if(showRepoNameHasIllegalCharsOrTooLongErr.value) stringResource(R.string.err_repo_name_has_illegal_chars_or_too_long)
                                else ""

                    if (showRepoNameAlreadyExistsErr.value || showRepoNameHasIllegalCharsOrTooLongErr.value) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errMsg,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    val errMsg = if(showRepoNameAlreadyExistsErr.value) stringResource(R.string.repo_name_exists_err)
                                else if(showRepoNameHasIllegalCharsOrTooLongErr.value) stringResource(R.string.err_repo_name_has_illegal_chars_or_too_long)
                                else ""
                    if (showRepoNameAlreadyExistsErr.value || showRepoNameHasIllegalCharsOrTooLongErr.value) {
                        Icon(imageVector=Icons.Filled.Error,
                            contentDescription=errMsg,
                            tint = MaterialTheme.colorScheme.error)
                    }
                },
                onValueChange = {
                    updateRepoName(it)
                },
                label = {
                    Text(stringResource(R.string.repo_name))
                },
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(10.dp)
                ,
            ) {
                val addIconSize = MyStyleKt.defaultIconSizeSmaller

                SingleSelectList(
                    outterModifier = Modifier.align(Alignment.CenterStart),
                    basePadding = { defaultHorizontalPadding -> PaddingValues(end = addIconSize + 5.dp + defaultHorizontalPadding, start = defaultHorizontalPadding) },
                    optionsList = storagePathList.value,
                    selectedOptionIndex = storagePathSelectedIndex,
                    selectedOptionValue = storagePathSelectedPath.value,
                    menuItemFormatter = { _, value ->
                        value?.name?:""
                    },
                    menuItemFormatterLine2 = { _, value ->
                        FsUtils.getPathWithInternalOrExternalPrefix(value?.path ?: "")
                    },

                    menuItemOnClick = { index, value ->
                        storagePathSelectedIndex.intValue = index
                        storagePathSelectedPath.value = value

                        StoragePathsMan.update {
                            it.storagePathLastSelected = value.path
                        }
                    },
                    menuItemTrailIcon = Icons.Filled.DeleteOutline,
                    menuItemTrailIconDescription = stringResource(R.string.trash_bin_icon_for_delete_item),
                    menuItemTrailIconEnable = {index, value->
                        index!=0
                    },
                    menuItemTrailIconOnClick = { index, value ->
                        if(index == 0) {
                            Msg.requireShowLongDuration(activityContext.getString(R.string.cant_delete_internal_storage))
                        }else {
                            initDeleteStoragePathListDialog(index)
                        }
                    }
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { showAddStoragePathDialog.value = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_storage_path),
                        modifier = Modifier.size(addIconSize)
                    )
                }

            }

            Spacer(modifier = Modifier.padding(spacerPadding))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding),

                value = branch.value,
                singleLine = true,

                onValueChange = {
                    branch.value=it
                },
                label = {
                    Text(stringResource(R.string.branch_optional))
                },
                placeholder = {
                    Text(stringResource(R.string.branch_name))
                }
            )

            //开发者模式 或 功能测试通过且用户是pro付费用户，则启用depth和singlebranch功能
//            改成启用未测试特性和 is shallowpassed控制功能是否显示，isPro决定功能是否enabled以及显示不同的文案( eg: depth and depth(Pro))
            if(dev_EnableUnTestedFeature || shallowAndSingleBranchTestPassed) {
                val isPro = UserUtil.isPro()
                val enableSingleBranch =  isPro && branch.value.isNotBlank()
                //single branch checkbox 开始
                //single branch选择框，如果branch值不为空，则可以启用或禁用，如果branch值为空，checkbox状态本身不变，但存储时忽略其值，默认当成禁用。
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(MyStyleKt.CheckoutBox.height)
                        .toggleable(
                            enabled = enableSingleBranch,
                            value = isSingleBranch,
                            onValueChange = { onIsSingleBranchStateChange(!isSingleBranch) },
                            role = Role.Checkbox
                        )
                        .padding(horizontal = MyStyleKt.defaultHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        enabled = enableSingleBranch,
                        checked = isSingleBranch,
                        onCheckedChange = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        text = if(isPro) stringResource(R.string.single_branch) else stringResource(R.string.single_branch_pro_only),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                        color = if(enableSingleBranch) Color.Unspecified else if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
                    )
                }
                //single branch checkbox 结束

                Spacer(modifier = Modifier.padding(spacerPadding))
//      depth输入框开始
                //20240414 测试 发现depth有问题，虽能成功克隆，但之后莫名其妙出问题提示找不到object id的概率非常大！而且在手机处理不了(不过电脑上的git虽也报错但能正常pull/push)，考虑过后，决定暂时放弃支持depth功能
                DepthTextField(depth)

                Spacer(modifier = Modifier.padding(spacerPadding))

                //depth输入框结束

            }

            //递归克隆checkbox开始
            //暂不支持递归克隆
            /*
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(MyStyleKt.CheckoutBox.height)
                    .toggleable(
                        value = isRecursiveClone,
                        onValueChange = { onIsRecursiveCloneStateChange(!isRecursiveClone) },
                        role = Role.Checkbox
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isRecursiveClone,
                    onCheckedChange = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = stringResource(R.string.recursive_clone),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        */
            //递归克隆checkbox结束


            MyHorizontalDivider(modifier = Modifier.padding(spacerPadding))
            Spacer(Modifier.height(10.dp))
            // if empty, no credentials, no need show select credential option
            val credentialListIsEmpty = allCredentialList.value.isEmpty()

            //choose credential
            SingleSelection(
                itemList = credentialRadioOptions,
                selected = {idx, item -> credentialSelectedOption == idx},
                text = {idx, item -> item},
                onClick = {idx, item -> onCredentialOptionSelected(idx)},
                beforeShowItem = {idx, item ->
                    // update credential type when click "New Credential" to make sure show private key/passphrase for ssh url
                    if(idx == optNumNewCredential) {
                        curCredentialType.intValue = Libgit2Helper.getCredentialTypeByUrl(gitUrl.value)
                    }
                },
                skip = {idx, item ->
                    credentialListIsEmpty && idx == optNumSelectCredential
                }
            )

            if(credentialSelectedOption == optNumNewCredential) {
                //显示新建credential的输入框
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MyStyleKt.defaultItemPadding)
                        .focusRequester(focusRequesterCredentialName)
                    ,
                    isError = showCredentialNameAlreadyExistsErr.value,
                    supportingText = {
                        if (showCredentialNameAlreadyExistsErr.value) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.credential_name_exists_err),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (showCredentialNameAlreadyExistsErr.value)
                            Icon(imageVector=Icons.Filled.Error,
                                contentDescription= stringResource(R.string.credential_name_exists_err),
                                tint = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,

                    value = credentialName.value,
                    onValueChange = {
                        updateCredentialName(it)
                    },
                    label = {
                        Text(stringResource(R.string.credential_name))
                    },
                    placeholder = {
                        Text(stringResource(R.string.credential_name_placeholder))
                    }
                )
                TextField(
                    modifier =
                    //如果type是ssh，让private-key输入框高点
                    if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 300.dp)
                            .padding(MyStyleKt.defaultItemPadding)

                    }else{
                        Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding)
                    }
                        ,
                    singleLine = curCredentialType.intValue != Cons.dbCredentialTypeSsh,

                    value = credentialVal.value,
                    onValueChange = {
                        credentialVal.value=it
                    },
                    label = {
                        if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                            Text(stringResource(R.string.private_key))
                        }else{
                            Text(stringResource(R.string.username))
                        }
                    },
                    placeholder = {
                        if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                            Text(stringResource(R.string.paste_your_private_key_here))
                        }else{
                            Text(stringResource(R.string.username))
                        }
                    }
                )
                PasswordTextFiled(
                    password = credentialPass,
                    label = if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                        stringResource(R.string.passphrase_if_have)
                    }else{
                        stringResource(R.string.password)
                    },
                    placeholder = if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                        stringResource(R.string.input_passphrase_if_have)
                    }else{
                        stringResource(R.string.password)
                    },
                    passwordVisible = passwordVisible,

                )

                // 若是http，提示用户可能需要用token替代密码
                if(curCredentialType.intValue == Cons.dbCredentialTypeHttp) {
                    TokenInsteadOfPasswordHint()
                }

            }else if(credentialSelectedOption == optNumSelectCredential) {
                Spacer(Modifier.height(MyStyleKt.defaultItemPadding))

                SingleSelectList(
                    optionsList = allCredentialList.value,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedCredential.value,
                    menuItemSelected = { _, item ->
                        item.id == selectedCredential.value.id
                    },
                    menuItemFormatter = { _, item ->
                        item?.name?:""
                    },
                    menuItemOnClick = { _, item ->
                        selectedCredential.value = item
                    },
                )
            }else if(credentialSelectedOption == optNumMatchCredentialByDomain) {
                MySelectionContainer {
                    DefaultPaddingRow {
                        Text(stringResource(R.string.credential_match_by_domain_note), color = MyStyleKt.TextColor.getHighlighting(), fontWeight = FontWeight.Light)
                    }
                }
            }
        }
    }

    if(requireFocusTo.intValue==focusToGitUrl) {
        requireFocusTo.intValue=focusToNone
        focusRequesterGitUrl.requestFocus()
    }else if(requireFocusTo.intValue==focusToRepoName) {
        requireFocusTo.intValue=focusToNone
        focusRequesterRepoName.requestFocus()
    }else if(requireFocusTo.intValue==focusToCredentialName) {
        requireFocusTo.intValue=focusToNone
        focusRequesterCredentialName.requestFocus()
    }

    LaunchedEffect(Unit) {
//        MyLog.d(TAG, "#LaunchedEffect: repoId=" + repoId)
        //编辑已存在repo
        // TODO 设置页面loading为true
        //      从数据库异步查询repo数据，更新页面state
        //      设置页面loading 为false
        doJobThenOffLoading(
//            loadingOn = { showLoadingDialog.value = true },
//            loadingOff = { showLoadingDialog.value = false }
        ) job@{
            if (isEditMode) {  //如果是编辑模式，查询仓库信息
                val repoDb = AppModel.dbContainer.repoRepository
                val credentialDb = AppModel.dbContainer.credentialRepository
                val repo = repoDb.getById(repoId)
                if(repo == null) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.repo_id_invalid))
                    return@job
                }
                gitUrlType.intValue = Libgit2Helper.getGitUrlType(repo.cloneUrl)  //更新下giturl type
                gitUrl.value = repo.cloneUrl
                repoName.value = TextFieldValue(repo.repoName)
                branch.value = repo.branch
                //设置是否单分支
                onIsSingleBranchStateChange(dbIntToBool(repo.isSingleBranch))
                //设置是否递归克隆
                onIsRecursiveCloneStateChange(dbIntToBool(repo.isRecursiveCloneOn))
                //depth只有大于0时设置才有意义
                if (Libgit2Helper.needSetDepth(repo.depth)) {
                    depth.value = "" + repo.depth
                }

                //把repo存到状态变量，保存时就不用再查询了
                repoFromDb.value = repo

                //show back repo saved path
                val storagePath = File(repo.fullSavePath).parent ?: ""
                val (selectedStoragePathIdx, selectedStoragePathItem) = findStoragePathItemByPath(storagePath)
                storagePathSelectedIndex.intValue = selectedStoragePathIdx
                // if non-exists, it must not be the app's default internal storage path, so the type should be repos storage path
                storagePathSelectedPath.value = selectedStoragePathItem ?: NameAndPath.genByPath(storagePath, NameAndPathType.REPOS_STORAGE_PATH, activityContext)

                //检查是否存在credential，如果存在，设置下相关状态变量
                val credentialIdForClone = repo.credentialIdForClone
                //注意，如果仓库存在关联的credential，在克隆页面编辑仓库时，不能编辑credential，只能新建或选择之前的credential，若想编辑credential，需要去credential页面，这样是为了简化实现逻辑
                if (!credentialIdForClone.isNullOrBlank()) {  //更新credential相关字段
                    if(credentialIdForClone == SpecialCredential.MatchByDomain.credentialId) {
                        onCredentialOptionSelected(optNumMatchCredentialByDomain)
                    }else {
                        val credential = credentialDb.getById(credentialIdForClone)
//                        MyLog.d(TAG, "#LaunchedEffect:credential==null:" +(credential==null))

                        if (credential == null) {  //要么没设置，要么设置了但被删除了，所以是无效id，这两种情况都会查不出对应的credential
                            onCredentialOptionSelected(optNumNoCredential)
                        } else {  //存在之前设置的credential
                            //设置选中的credential
                            onCredentialOptionSelected(optNumSelectCredential)  //选中“select credential”单选项
                            selectedCredential.value = credential
//                            curCredentialType.intValue = credential.type  // deprecated
                            curCredentialType.intValue = Libgit2Helper.getCredentialTypeByUrl(repo.cloneUrl)  //设置当前credential类型
                        }
                    }
                }
            } else {  //如果是新增模式，简单聚焦下第一个输入框，弹出键盘即可
                //聚焦第一个输入框，算了不聚焦了，在协程里聚焦会报异常，虽然可以设置状态，然后在compose里判断，聚焦，再把状态关闭，但是，太麻烦了，而且感觉聚焦与否其实意义不大，甚至就连报错时的聚焦意义都不大，不过报错时的聚焦不需要在协程里执行也不会抛异常，所以暂且保留
                requireFocusTo.intValue = focusToGitUrl
            }

            //查询credential列表，无论新增还是编辑都需要查credential列表
            val credentialDb = AppModel.dbContainer.credentialRepository
//            credentialHttpList.value.clear()
//            credentialSshList.value.clear()
            allCredentialList.value.clear()

            //注：这里不需要显示密码，只是列出已保存的凭据供用户选择，顶多需要个凭据名和凭据id，所以查询的是未解密密码的list
//            credentialHttpList.value.addAll(credentialDb.getHttpList())
//            credentialSshList.value.addAll(credentialDb.getSshList())
            allCredentialList.value.addAll(credentialDb.getAll())

//            credentialHttpList.requireRefreshView()
//            credentialSshList.requireRefreshView()
//            MyLog.d(TAG, "#LaunchedEffect:credentialHttpList.size=" + credentialHttpList.value.size + ", credentialSshList.size=" + credentialSshList.value.size)

        }
    }



    //判定是否启用执行克隆的按钮，每次状态改变重新渲染页面都会执行这段代码更新此值
    isReadyForClone.value = ((gitUrl.value.isNotBlank() && repoName.value.text.isNotBlank())
        &&
        ((credentialSelectedOption==optNumNoCredential || credentialSelectedOption==optNumMatchCredentialByDomain)  //新凭据的情况
                || ((credentialSelectedOption==optNumNewCredential && credentialName.value.text.isNotBlank())  //必填字段
                    //要么是http且填了密码字段，要么是ssh且填了privatekey字段
//                    && (curCredentialType.intValue==Cons.dbCredentialTypeHttp && credentialPass.value.isNotBlank()) || (curCredentialType.intValue==Cons.dbCredentialTypeSsh && credentialVal.value.isNotBlank())
                   )
                || (credentialSelectedOption==optNumSelectCredential && selectedCredential.value.id.isNotBlank() && selectedCredential.value.name.isNotBlank()))
        && !showRepoNameAlreadyExistsErr.value && !showRepoNameHasIllegalCharsOrTooLongErr.value && !showCredentialNameAlreadyExistsErr.value
        )

}
