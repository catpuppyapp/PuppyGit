package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.DefaultPaddingText
import com.catpuppyapp.puppygit.compose.FilterTextField
import com.catpuppyapp.puppygit.compose.ItemListIsEmpty
import com.catpuppyapp.puppygit.compose.LoadingTextUnScrollable
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PullToRefreshBox
import com.catpuppyapp.puppygit.compose.SelectedUnSelectedDialog
import com.catpuppyapp.puppygit.compose.SelectionColumn
import com.catpuppyapp.puppygit.compose.SettingsContent
import com.catpuppyapp.puppygit.compose.SettingsContentSwitcher
import com.catpuppyapp.puppygit.compose.SettingsTitle
import com.catpuppyapp.puppygit.compose.SizeIcon
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dto.AppInfo
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.listitem.AppItem
import com.catpuppyapp.puppygit.screen.content.listitem.RepoNameAndIdItem
import com.catpuppyapp.puppygit.screen.functions.maybeIsGoodKeyword
import com.catpuppyapp.puppygit.screen.shared.SharedState
import com.catpuppyapp.puppygit.settings.PackageNameAndRepo
import com.catpuppyapp.puppygit.settings.PackageNameAndRepoSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.settings.util.AutomationUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.ActivityUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ComposeHelper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.appendSecondsUnit
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.parseLongOrDefault
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "AutomationInnerPage"
private val trailIconSize = MyStyleKt.trailIconSize

@Composable
fun AutomationInnerPage(
    stateKeyTag:String,
    contentPadding: PaddingValues,
    needRefreshPage:MutableState<String>,
    listState: LazyListState,
    pageScrolled: MutableState<Boolean>,
    refreshPage:()->Unit,
    openDrawer:()->Unit,
    exitApp:()->Unit,
){
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val scope = rememberCoroutineScope()
    val activityContext = LocalContext.current
//    val clipboardManager = LocalClipboardManager.current
//    val haptic = LocalHapticFeedback.current
    val configuration = AppModel.getCurActivityConfig()
    val screenHeightDp = configuration.screenHeightDp.dp

//    val density = LocalDensity.current

    //两个作用：1离开页面，返回后重新显示导航按钮；2在设置页面开启、关闭导航按钮后使其立即生效（因为remember离开页面就会销毁，所以每次重进页面都会读取最新的settings值）。
    //20250222: 其实这个pageScrolled现在已经仅代表是否显示navi buttons 了，跟是否滚动没关系了
    val _for_update_pageScrolled = remember {
        pageScrolled.value = SettingsUtil.getSettingsSnapshot().showNaviButtons
        pageScrolled.value
    }


    val settingsState = mutableCustomStateOf(stateKeyTag, "settingsState", SettingsUtil.getSettingsSnapshot())


    val getServiceStatus = { AutomationUtil.isAccessibilityServiceEnabled(AppModel.realAppContext) }

    val runningStatus = rememberSaveable { mutableStateOf<Boolean?>(getServiceStatus()) }

    val updateServiceStatus = {
        runningStatus.value = getServiceStatus()
    }

    val appsFilterKeyword = mutableCustomStateOf(stateKeyTag, "appsFilterKeyword", TextFieldValue(""))
    val reposFilterKeyword = mutableCustomStateOf(stateKeyTag, "reposFilterKeyword", TextFieldValue(""))


    val addedAppList = mutableCustomStateListOf(stateKeyTag, "addedAppList") { listOf<AppInfo>() }
    val notAddedAppList = mutableCustomStateListOf(stateKeyTag, "notAddedAppList") { listOf<AppInfo>() }
    val appListLoading = rememberSaveable { mutableStateOf(SharedState.defaultLoadingValue) }

    val progressNotify = rememberSaveable { mutableStateOf(settingsState.value.automation.showNotifyWhenProgress) }
    val progressNotifyAutoDismiss = rememberSaveable { mutableStateOf(settingsState.value.automation.progressNotifyAutoDismiss) }
//    val errNotify = rememberSaveable { mutableStateOf(settingsState.value.automation.showNotifyWhenErr) }
//    val successNotify = rememberSaveable { mutableStateOf(settingsState.value.automation.showNotifyWhenSuccess) }

    val packageNameForSelectReposDialog = rememberSaveable { mutableStateOf("") }
    val appNameForSelectReposDialog = rememberSaveable { mutableStateOf("") }
    val selectedRepoList = mutableCustomStateListOf(stateKeyTag, "selectedRepoList") { listOf<RepoEntity>() }
    val unselectedRepoList = mutableCustomStateListOf(stateKeyTag, "unselectedRepoList") { listOf<RepoEntity>() }
    val selectedRepoListLoading = rememberSaveable { mutableStateOf(false) }
    val showSelectReposDialog = rememberSaveable { mutableStateOf(false) }
    val initSelectReposDialog = { packageName:String, appName:String ->
        packageNameForSelectReposDialog.value = packageName
        appNameForSelectReposDialog.value = appName

        doJobThenOffLoading(
            loadingOn = { selectedRepoListLoading.value = true },
            loadingOff = { selectedRepoListLoading.value = false }
        ) {
            try {
                val packageNameLinkedRepos = AutomationUtil.getRepoIds(SettingsUtil.getSettingsSnapshot().automation, packageName)
                val removedNonExistsPackageNameLinkedRepos = packageNameLinkedRepos.toMutableList()
                val repos = AppModel.dbContainer.repoRepository.getAll(updateRepoInfo = false).toMutableList()
                val selectedRepos = mutableListOf<RepoEntity>()

                packageNameLinkedRepos.forEachBetter { savedId ->
                    val foundRepo = repos.find { it.id == savedId }
                    if(foundRepo != null) {
                        selectedRepos.add(foundRepo)
                        // remove selected, then the rest of repos will only left unselected repos
                        repos.removeIf { it.id == savedId }
                    }else {
                        // remove repo which doesn't exist anymore
                        removedNonExistsPackageNameLinkedRepos.remove(savedId)
                    }
                }

                selectedRepoList.value.clear()
                selectedRepoList.value.addAll(selectedRepos)
                unselectedRepoList.value.clear()
                unselectedRepoList.value.addAll(repos)

                //善后操作
                // update config, remove nonexistent repos
                SettingsUtil.update { s ->
                    s.automation.packageNameAndRepoIdsMap.set(packageName, removedNonExistsPackageNameLinkedRepos)
                }

            }catch (e: Exception) {
                Msg.requireShowLongDuration("load repos err: ${e.localizedMessage}")
                MyLog.e(TAG, "Automation#initSelectReposDialog: load repos err: ${e.stackTraceToString()}")
            }
        }

        showSelectReposDialog.value = true
    }


    val showRepoOfAppSettingsDialog = rememberSaveable { mutableStateOf(false) }
    val appPackageNameOfRepoOfAppDialog = rememberSaveable { mutableStateOf("") }
    val pullIntervalBufOfRepoOfAppDialog = rememberSaveable { mutableStateOf("") }
    val pushDelayBufOfRepoOfAppDialog = rememberSaveable { mutableStateOf("") }
    val repoOfRepoOfAppDialog = mutableCustomStateOf(stateKeyTag, "repoOfRepoOfAppDialog") { RepoEntity(id="") }
    val initRepoOfAppSettingsDialog = { appPackageName:String, targetRepo: RepoEntity ->
        appPackageNameOfRepoOfAppDialog.value = appPackageName
        repoOfRepoOfAppDialog.value = targetRepo

        // 获取app关联的仓库的拉取和推送延迟设置，不要获取全局，若没直接留空
        // get app linked repo pull interval and push delay settings
        val packageNameAndRepoSettings = AutomationUtil.getAppAndRepoSpecifiedSettings(appPackageName, targetRepo.id)
        pullIntervalBufOfRepoOfAppDialog.value = packageNameAndRepoSettings.getPullIntervalFormatted()
        pushDelayBufOfRepoOfAppDialog.value = packageNameAndRepoSettings.getPushDelayFormatted()

        showRepoOfAppSettingsDialog.value = true
    }

    if(showRepoOfAppSettingsDialog.value) {
        ConfirmDialog2(
            title = repoOfRepoOfAppDialog.value.repoName,
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    val padding = MyStyleKt.defaultHorizontalPadding

                    MySelectionContainer {
                        DefaultPaddingText(stringResource(R.string.leave_empty_to_use_global_settings), color = MyStyleKt.TextColor.getHighlighting())
                    }

                    Spacer(Modifier.height(10.dp))

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = pullIntervalBufOfRepoOfAppDialog.value,
                        onValueChange = {
                            //直接更新，不管用户输入什么，点确定后再检查值是否有效
                            pullIntervalBufOfRepoOfAppDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.pull_interval_in_seconds))
                        },
                    )


                    MySelectionContainer {
                        Text(text = stringResource(R.string.pull_interval_note), fontWeight = FontWeight.Light, modifier = Modifier.padding(padding))
                    }

                    Spacer(Modifier.height(15.dp))

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = pushDelayBufOfRepoOfAppDialog.value,
                        onValueChange = {
                            //直接更新，不管用户输入什么，点确定后再检查值是否有效
                            pushDelayBufOfRepoOfAppDialog.value = it
                        },
                        label = {
                            Text(stringResource(R.string.push_delay_in_seconds))
                        },
                    )


                    MySelectionContainer {
                        Text(text = stringResource(R.string.push_delay_note), fontWeight = FontWeight.Light, modifier = Modifier.padding(padding))
                    }

                }


            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showRepoOfAppSettingsDialog.value = false }
        ) {
            showRepoOfAppSettingsDialog.value = false

            doJobThenOffLoading {
                //解析
                val newPullInterval = PackageNameAndRepoSettings.formatPullIntervalBeforeSaving(pullIntervalBufOfRepoOfAppDialog.value)
                val newPushDelay = PackageNameAndRepoSettings.formatPushDelayBeforeSaving(pushDelayBufOfRepoOfAppDialog.value)


                //保存
                settingsState.value = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                    it.automation.packageNameAndRepoAndSettingsMap.put(
                        PackageNameAndRepo(appPackageNameOfRepoOfAppDialog.value, repoOfRepoOfAppDialog.value.id).toKey(),
                        PackageNameAndRepoSettings(newPullInterval, newPushDelay)
                    )
                }!!


                Msg.requireShow(activityContext.getString(R.string.saved))
            }
        }

    }



    val filterRepos = { keyword:String, list:List<RepoEntity> ->
        list.filter { it.repoName.contains(keyword, ignoreCase = true) || it.id.contains(keyword, ignoreCase = true) }
    }

    val filterApps = { keyword:String, list:List<AppInfo> ->
        list.filter { it.appName.contains(keyword, ignoreCase = true) || it.packageName.contains(keyword, ignoreCase = true) }
    }

    if(showSelectReposDialog.value) {
        SelectedUnSelectedDialog(
            title = appNameForSelectReposDialog.value,
            loading = selectedRepoListLoading.value,
            selectedTitleText = stringResource(R.string.linked_repos),
            unselectedTitleText = stringResource(R.string.unlinked_repos),
            selectedItemList = selectedRepoList.value,
            unselectedItemList = unselectedRepoList.value,
            filterKeyWord = reposFilterKeyword,
            selectedItemFormatter={ clickedRepo ->
                val splitSpacerWidth = MyStyleKt.trailIconSplitSpacerWidth

                RepoNameAndIdItem(
                    settings = settingsState.value,
                    selected = true,
                    appPackageName = packageNameForSelectReposDialog.value,
                    repoEntity = clickedRepo,
                    trailIconWidth = trailIconSize * 2 + splitSpacerWidth
                ) { containerModifier ->
                    Row(
                        modifier = containerModifier
                            .fillMaxWidth()
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {

                        SizeIcon(
                            size = trailIconSize,
                            modifier=Modifier.clickable {
                                initRepoOfAppSettingsDialog(packageNameForSelectReposDialog.value, clickedRepo)
                            },
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )

                        Spacer(modifier = Modifier.width(splitSpacerWidth))

                        SizeIcon(
                            size = trailIconSize,
                            modifier=Modifier.clickable {
                                selectedRepoList.value.remove(clickedRepo)

                                val tmp = unselectedRepoList.value.toList()
                                //添加到未选中列表头部
                                unselectedRepoList.value.clear()
                                unselectedRepoList.value.add(clickedRepo)
                                unselectedRepoList.value.addAll(tmp)

                                //保存
                                settingsState.value = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                                    it.automation.packageNameAndRepoIdsMap.let {
                                        it.put(
                                            packageNameForSelectReposDialog.value,
                                            //按仓库名排序，然后把id存上
//                                            selectedRepoList.value.toList().sortedBy { it.repoName }.map { it.id }
                                            // keep added order
                                            selectedRepoList.value.map { it.id }
                                        )
                                    }

                                    it.automation.packageNameAndRepoAndSettingsMap.remove(PackageNameAndRepo(packageNameForSelectReposDialog.value, clickedRepo.id).toKey())
                                }!!
                            },
                            imageVector = Icons.Filled.HorizontalRule,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }


                }

                MyHorizontalDivider()
            },
            unselectedItemFormatter = { clickedRepo ->
                RepoNameAndIdItem(
                    settings = settingsState.value,
                    selected = false,
                    appPackageName = packageNameForSelectReposDialog.value,
                    repoEntity = clickedRepo,
                    trailIconWidth = trailIconSize
                ) { containerModifier ->

                    Row(
                        modifier = containerModifier
                            .fillMaxWidth()
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {

                        SizeIcon(
                            size = trailIconSize,
                            modifier=Modifier.clickable {
                                unselectedRepoList.value.remove(clickedRepo)
                                //添加到已选中列表末尾
                                selectedRepoList.value.add(clickedRepo)

                                //保存
                                SettingsUtil.update {
                                    it.automation.packageNameAndRepoIdsMap.let {
                                        it.put(
                                            packageNameForSelectReposDialog.value,
//                                            selectedRepoList.value.toList().sortedBy { it.repoName }.map { it.id }
                                            selectedRepoList.value.map { it.id }
                                        )
                                    }
                                }
                            },
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.add)
                        )
                    }



                }

                MyHorizontalDivider()
            },
            filterSelectedItemList = {keyword -> filterRepos(keyword, selectedRepoList.value)},
            filterUnselectedItemList = {keyword -> filterRepos(keyword, unselectedRepoList.value)},
            cancel = {showSelectReposDialog.value = false}
        )
    }

    val pullIntervalInSec = rememberSaveable { mutableStateOf(settingsState.value.automation.pullIntervalInSec.toString()) }
    val pushDelayInSec = rememberSaveable { mutableStateOf(settingsState.value.automation.pushDelayInSec.toString()) }
    val pullIntervalOrPushDelayInSecBuf = mutableCustomStateOf(stateKeyTag, "pullIntervalOrPushDelayInSecBuf") { TextFieldValue("") }
    val truePullIntervalFalsePushDelay = rememberSaveable { mutableStateOf(false) }
    val showSetPullInternalOrPushDelayDialog = rememberSaveable { mutableStateOf(false) }

    val initPullIntervalOrPushDelayDialog = { isPullInterval:Boolean ->
        if(isPullInterval) {
            truePullIntervalFalsePushDelay.value = true
            pullIntervalOrPushDelayInSecBuf.value = pullIntervalInSec.value.let { TextFieldValue(text = it, selection = TextRange(0, it.length)) }
        }else {
            truePullIntervalFalsePushDelay.value = false
            pullIntervalOrPushDelayInSecBuf.value = pushDelayInSec.value.let { TextFieldValue(text = it, selection = TextRange(0, it.length)) }
        }

        showSetPullInternalOrPushDelayDialog.value = true
    }

    if(showSetPullInternalOrPushDelayDialog.value) {
        val focusRequester = remember { FocusRequester() }

        val truePullIntervalFalsePushDelay = truePullIntervalFalsePushDelay.value

        val title = if(truePullIntervalFalsePushDelay) stringResource(R.string.pull_interval) else stringResource(R.string.push_delay)
        val label = if(truePullIntervalFalsePushDelay) stringResource(R.string.pull_interval_in_seconds) else stringResource(R.string.push_delay_in_seconds)

        ConfirmDialog2(
            title = title,
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    val padding = 10.dp

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = pullIntervalOrPushDelayInSecBuf.value,
                        onValueChange = {
                            //直接更新，不管用户输入什么，点确定后再检查值是否有效
                            pullIntervalOrPushDelayInSecBuf.value = it
                        },
                        label = {
                            Text(label)
                        },
//                        placeholder = {}
                    )


                    val note = if(truePullIntervalFalsePushDelay) {
                        stringResource(R.string.pull_interval_note)
                    }else {
                        stringResource(R.string.push_delay_note)
                    }

                    MySelectionContainer {
                        Text(text = note, fontWeight = FontWeight.Light, modifier = Modifier.padding(padding))
                    }

                }


            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetPullInternalOrPushDelayDialog.value = false }
        ) {
            showSetPullInternalOrPushDelayDialog.value = false

            doJobThenOffLoading {
                //解析
                val newValue = parseLongOrDefault(pullIntervalOrPushDelayInSecBuf.value.text, default = null)

                //检查
                //注：只要解析成功，正数、负数、0，均可：正数，延迟指定时间执行；负数，不执行；0，立即执行。
                if(newValue == null) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.invalid_number))
                    return@doJobThenOffLoading
                }

                //保存
                settingsState.value = if(truePullIntervalFalsePushDelay) {
                    pullIntervalInSec.value = newValue.toString()

                    SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                        it.automation.pullIntervalInSec = newValue
                    }
                }else {
                    pushDelayInSec.value = newValue.toString()

                    SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                        it.automation.pushDelayInSec = newValue
                    }
                }!!


                Msg.requireShow(activityContext.getString(R.string.saved))
            }
        }

        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }

    }



    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp= exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end

    val itemFontSize = MyStyleKt.SettingsItem.itemFontSize
    val itemDescFontSize = MyStyleKt.SettingsItem.itemDescFontSize


    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeStateTriggerRefreshPage(needRefreshPage) },

    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
            ,

            contentPadding = contentPadding,
            state = listState,
        ) {

            item {
                SettingsContentSwitcher(
                    left = {
                        val runningStatus = runningStatus.value

                        Text(stringResource(R.string.status), fontSize = itemFontSize)
                        Text(
                            text = UIHelper.getRunningStateText(activityContext, runningStatus),
                            fontSize = itemDescFontSize,
                            fontWeight = FontWeight.Light,
                            color = UIHelper.getRunningStateColor(runningStatus)
                        )

                    },
                    right = {
                        Switch(
                            //此值有可能为null，所以用等于true来判断是否真为true
                            checked = runningStatus.value == true,
                            onCheckedChange = null
                        )
                    },
                    onClick = {
                        //跳转到无障碍服务页面
                        Msg.requireShowLongDuration(activityContext.getString(R.string.please_find_and_enable_disable_service_for_app))

                        ActivityUtil.openAccessibilitySettings(activityContext)
                    }
                )
            }

            item {
                SettingsContentSwitcher(
                    left = {
                        Text(stringResource(R.string.progress_notification), fontSize = itemFontSize)
                    },
                    right = {
                        Switch(
                            checked = progressNotify.value,
                            onCheckedChange = null
                        )
                    },
                    onClick = {
                        val newValue = !progressNotify.value

                        //save
                        progressNotify.value = newValue
                        SettingsUtil.update {
                            it.automation.showNotifyWhenProgress = newValue
                        }
                    }
                )


            }

            item {
                SettingsContentSwitcher(
                    left = {
                        Text(stringResource(R.string.progress_notification_auto_dismiss), fontSize = itemFontSize)
                        Text(stringResource(R.string.progress_notification_auto_dismiss_desc), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                    },
                    right = {
                        Switch(
                            checked = progressNotifyAutoDismiss.value,
                            onCheckedChange = null
                        )
                    },
                    onClick = {
                        val newValue = !progressNotifyAutoDismiss.value

                        //save
                        progressNotifyAutoDismiss.value = newValue
                        SettingsUtil.update {
                            it.automation.progressNotifyAutoDismiss = newValue
                        }
                    }
                )
            }

            // pull interval
            item {
                SettingsContent(onClick = {
                    initPullIntervalOrPushDelayDialog(true)
                }) {
                    Column {
                        Text(stringResource(R.string.pull_interval), fontSize = itemFontSize)
                        Text(appendSecondsUnit(pullIntervalInSec.value), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                    }
                }

            }

            // push delay
            item {
                SettingsContent(onClick = {
                    initPullIntervalOrPushDelayDialog(false)
                }) {
                    Column {
                        Text(stringResource(R.string.push_delay), fontSize = itemFontSize)
                        Text(appendSecondsUnit(pushDelayInSec.value), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                    }
                }
            }

            item {
                SettingsContent(onClick = {
                    ActivityUtil.openUrl(activityContext, automationDocUrl)
                }) {
                    Column {
                        Text(stringResource(R.string.document), fontSize = itemFontSize)
                    }
                }
            }

//
//        item {
//            SettingsContent(onClick = {
//                val newValue = !successNotify.value
//
//                //save
//                successNotify.value = newValue
//                SettingsUtil.update {
//                    it.automation.showNotifyWhenSuccess = newValue
//                }
//            }) {
//                Column(modifier = Modifier.fillMaxWidth(itemLeftWidthForSwitcher)) {
//                    Text(stringResource(R.string.success_notification), fontSize = itemFontSize)
//                }
//
//                Icon(
//                    modifier = Modifier.size(switcherIconSize),
//                    imageVector = UIHelper.getIconForSwitcher(successNotify.value),
//                    contentDescription = if(successNotify.value) stringResource(R.string.enable) else stringResource(R.string.disable),
//                    tint = UIHelper.getColorForSwitcher(successNotify.value),
//                )
//            }
//
//        }
//
//
//        item {
//            SettingsContent(onClick = {
//                val newValue = !errNotify.value
//
//                //save
//                errNotify.value = newValue
//                SettingsUtil.update {
//                    it.automation.showNotifyWhenErr = newValue
//                }
//            }) {
//                Column(modifier = Modifier.fillMaxWidth(itemLeftWidthForSwitcher)) {
//                    Text(stringResource(R.string.err_notification), fontSize = itemFontSize)
//                }
//
//                Icon(
//                    modifier = Modifier.size(switcherIconSize),
//                    imageVector = UIHelper.getIconForSwitcher(errNotify.value),
//                    contentDescription = if(errNotify.value) stringResource(R.string.enable) else stringResource(R.string.disable),
//                    tint = UIHelper.getColorForSwitcher(errNotify.value),
//                )
//            }
//
//        }

            item {
                SelectionColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 10.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.app_list), fontSize = 30.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.will_auto_pull_push_linked_repos_when_selected_apps_enter_exit), textAlign = TextAlign.Center)
                }

            }

            val addItemBarHeight = 40.dp
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding)
                ) {

                    //注意：这个过滤没开协程，直接在渲染线程过滤的，因为感觉用户应该不会装超过500个应用，就算真有500个，也很快就过滤完，所以感觉没必要加代码
                    //普通的过滤，加不加清空无所谓，一按返回就清空了，但这个常驻显示，得加个清空按钮
                    FilterTextField(filterKeyWord = appsFilterKeyword, requireFocus = false)
                }

            }

            if(appListLoading.value) {
                item {
                    LoadingTextUnScrollable(stringResource(R.string.loading), PaddingValues(top = addItemBarHeight+30.dp, bottom = 30.dp))
                }
            }else {
                // 旧版compose有bug，用else有可能会忽略条件，如果这里有问题可直接改成if判断相反条件

                //根据关键字过滤条目
                val k = appsFilterKeyword.value.text  //关键字
                val enableFilter = maybeIsGoodKeyword(k)
                val filteredAddedAppList = if(enableFilter){
                    val tmpList = filterApps(k, addedAppList.value)
                    tmpList
                }else {
                    addedAppList.value
                }

                val filteredNotAddedAppList = if(enableFilter){
                    val tmpList = filterApps(k, notAddedAppList.value)
                    tmpList
                }else {
                    notAddedAppList.value
                }


                item {
                    SettingsTitle(stringResource(R.string.selected_str)+"("+filteredAddedAppList.size+")")
                }

                if(filteredAddedAppList.isEmpty()) {
                    item {
                        ItemListIsEmpty()
                    }

                }

                val iconEndPadding = 5.dp

                if(filteredAddedAppList.isNotEmpty()) {
                    filteredAddedAppList.toList().forEachBetter { appInfo ->
                        item {
                            val splitSpacerWidth = 20.dp

                            AppItem(
                                appInfo = appInfo,
                                trailIconWidth = trailIconSize * 2 + splitSpacerWidth,
                                trailIcons = { containerModifier ->
                                    Row(
                                        modifier = containerModifier
                                            .fillMaxWidth()
                                            .padding(end = iconEndPadding)
                                        ,
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {

                                        SizeIcon(
                                            size = trailIconSize,
                                            modifier=Modifier.clickable {
                                                initSelectReposDialog(appInfo.packageName, appInfo.appName)
                                            },
                                            imageVector = Icons.Outlined.Settings,
                                            contentDescription = stringResource(R.string.settings)
                                        )

                                        Spacer(modifier = Modifier.width(splitSpacerWidth))

                                        SizeIcon(
                                            size = trailIconSize,
                                            modifier=Modifier.clickable {
                                                addedAppList.value.remove(appInfo)

                                                val tmp = notAddedAppList.value.toList()
                                                //添加到未选中列表头部
                                                notAddedAppList.value.clear()
                                                notAddedAppList.value.add(appInfo)
                                                notAddedAppList.value.addAll(tmp)

                                                val appPackageAndRepoKeyPrefix = PackageNameAndRepo(appInfo.packageName).toKeyPrefix()
                                                //保存，从列表移除
                                                settingsState.value = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                                                    it.automation.packageNameAndRepoIdsMap.remove(appInfo.packageName)

                                                    // update app and repo specified settings
                                                    val newPackageNameAndRepoAndSettingsMap = mutableMapOf<String, PackageNameAndRepoSettings>()
                                                    for (i in it.automation.packageNameAndRepoAndSettingsMap) {
                                                        if(!i.key.startsWith(appPackageAndRepoKeyPrefix)) {
                                                            newPackageNameAndRepoAndSettingsMap.put(i.key, i.value)
                                                        }
                                                    }

                                                    it.automation.packageNameAndRepoAndSettingsMap = newPackageNameAndRepoAndSettingsMap
                                                }!!
                                            },
                                            imageVector = Icons.Filled.HorizontalRule,
                                            contentDescription = stringResource(R.string.delete)
                                        )
                                    }
                                },
                            )

                            MyHorizontalDivider()
                        }

                    }
                }


                item {
                    SettingsTitle(stringResource(R.string.unselected)+"("+filteredNotAddedAppList.size+")")
                }

                if(filteredNotAddedAppList.isEmpty()) {
                    item {
                        ItemListIsEmpty()
                    }

                }


                if(filteredNotAddedAppList.isNotEmpty()) {
                    filteredNotAddedAppList.toList().forEachBetter { appInfo ->
                        item {
                            AppItem(
                                appInfo = appInfo,
                                trailIconWidth = trailIconSize ,
                                trailIcons = { containerModifier ->
                                    Row(
                                        modifier = containerModifier
                                            .fillMaxWidth()
                                            .padding(end = iconEndPadding)
                                        ,
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {

                                        SizeIcon(
                                            size = trailIconSize,
                                            modifier=Modifier.clickable {
                                                notAddedAppList.value.remove(appInfo)
                                                //添加到已选中列表末尾
                                                addedAppList.value.add(appInfo)

                                                //保存，添加到列表
                                                SettingsUtil.update {
                                                    it.automation.packageNameAndRepoIdsMap.put(appInfo.packageName, listOf())
                                                }
                                            },
                                            imageVector = Icons.Outlined.Add,
                                            contentDescription = stringResource(R.string.add)
                                        )
                                    }

                                },
                            )

                            MyHorizontalDivider()
                        }
                    }
                }
            }


            item {
                //高点，不然输入包名后还得关键盘才看得到东西
                Spacer(Modifier.height(screenHeightDp * 0.7f))
            }
        }


    }

    LaunchedEffect(needRefreshPage.value) {
        val newSettings = SettingsUtil.getSettingsSnapshot()
        settingsState.value = newSettings

        doJobThenOffLoading {
            appListLoading.value = true

            // update app list
            val (userAddedAppList, userNotAddedAppList) = AutomationUtil.getSelectedAndUnSelectedAppList(activityContext, newSettings.automation)

            addedAppList.value.clear()
            addedAppList.value.addAll(userAddedAppList)
            notAddedAppList.value.clear()
            notAddedAppList.value.addAll(userNotAddedAppList)
            appListLoading.value = false
        }
    }

    LaunchedEffect(Unit) {
        //定时检查状态，不然从无障碍页面返回后状态不会更新
        scope.launch {
            runCatching {
                while (true) {
                    updateServiceStatus()
                    delay(1000)
                }
            }
        }
    }

}
