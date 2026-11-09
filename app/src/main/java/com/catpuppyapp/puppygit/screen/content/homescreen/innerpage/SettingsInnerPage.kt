package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.ClearMasterPasswordDialog
import com.catpuppyapp.puppygit.compose.ClickableText
import com.catpuppyapp.puppygit.compose.CommitMsgTemplateDialog
import com.catpuppyapp.puppygit.compose.ConfirmDialog2
import com.catpuppyapp.puppygit.compose.ConfirmDialog3
import com.catpuppyapp.puppygit.compose.CopyableDialog
import com.catpuppyapp.puppygit.compose.DefaultPaddingText
import com.catpuppyapp.puppygit.compose.MyCheckBox
import com.catpuppyapp.puppygit.compose.MySelectionContainer
import com.catpuppyapp.puppygit.compose.PasswordTextFiled
import com.catpuppyapp.puppygit.compose.ScrollableColumn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.SettingsContent
import com.catpuppyapp.puppygit.compose.SettingsContentSelector
import com.catpuppyapp.puppygit.compose.SettingsContentSwitcher
import com.catpuppyapp.puppygit.compose.SettingsTitle
import com.catpuppyapp.puppygit.compose.SingleSelectList
import com.catpuppyapp.puppygit.compose.SoftkeyboardVisibleListener
import com.catpuppyapp.puppygit.compose.SpacerRow
import com.catpuppyapp.puppygit.compose.TwoLineSettingsItem
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.StrCons
import com.catpuppyapp.puppygit.dev.DevFeature
import com.catpuppyapp.puppygit.dev.DevItem
import com.catpuppyapp.puppygit.dev.lfsTestPassed
import com.catpuppyapp.puppygit.jni.LibgitTwo
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsCons
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.ActivityUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ComposeHelper
import com.catpuppyapp.puppygit.utils.EditCache
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.HashUtil
import com.catpuppyapp.puppygit.utils.LanguageUtil
import com.catpuppyapp.puppygit.utils.Lg2HomeUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.StrListUtil
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.encrypt.MasterPassUtil
import com.catpuppyapp.puppygit.utils.fileopenhistory.FileOpenHistoryMan
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.getInvalidTimeZoneOffsetErrMsg
import com.catpuppyapp.puppygit.utils.getValidTimeZoneOffsetRangeInMinutes
import com.catpuppyapp.puppygit.utils.isValidOffsetInMinutes
import com.catpuppyapp.puppygit.utils.pref.PrefMan
import com.catpuppyapp.puppygit.utils.pref.PrefUtil
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.catpuppyapp.puppygit.utils.snapshot.SnapshotUtil
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateListOf
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.catpuppyapp.puppygit.utils.storagepaths.StoragePathsMan

private const val TAG = "SettingsInnerPage"

private val trailIconWidth = MyStyleKt.defaultLongPressAbleIconBtnPressedCircleSize

@Composable
fun SettingsInnerPage(
    stateKeyTag:String,

    contentPadding: PaddingValues,
    needRefreshPage:MutableState<String>,
    openDrawer:()->Unit,
    exitApp:()->Unit,
    listState:ScrollState,
    goToFilesPage:(path:String)->Unit,

){
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    // softkeyboard show/hidden relate start

    val view = LocalView.current
    val density = LocalDensity.current

    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    //indicate keyboard covered component
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    // which component expect adjust heghit or padding when softkeyboard shown
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    // the padding value when softkeyboard shown
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }

    // softkeyboard show/hidden relate end



    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val settingsState = mutableCustomStateOf(stateKeyTag, "settingsState", SettingsUtil.getSettingsSnapshot())

    val themeList = Theme.themeList
    val selectedTheme = rememberSaveable { mutableIntStateOf(PrefMan.getInt(activityContext, PrefMan.Key.theme, Theme.defaultThemeValue)) }

    val languageList = LanguageUtil.languageCodeList
    val selectedLanguage = rememberSaveable { mutableStateOf(LanguageUtil.getLangCode(activityContext)) }

    val logLevelList = MyLog.logLevelList
    val selectedLogLevel = rememberSaveable { mutableStateOf(MyLog.getCurrentLogLevel()) }

    val enableEditCache = rememberSaveable { mutableStateOf(settingsState.value.editor.editCacheEnable) }
    val showNaviButtons = rememberSaveable { mutableStateOf(settingsState.value.showNaviButtons) }
    val syntaxHighlightEnabled = rememberSaveable { mutableStateOf(settingsState.value.editor.syntaxHighlightEnabled) }
    val syntaxHighlightEnabled_DiffScreen = rememberSaveable { mutableStateOf(settingsState.value.diff.syntaxHighlightEnabled) }
    val useSystemFonts = rememberSaveable { mutableStateOf(settingsState.value.editor.useSystemFonts) }
    val useSystemFonts_DiffScreen = rememberSaveable { mutableStateOf(settingsState.value.diff.useSystemFonts) }
    val devModeOn = rememberSaveable { mutableStateOf(PrefUtil.getDevMode(activityContext)) }
    val enableSnapshot_File = rememberSaveable { mutableStateOf(settingsState.value.editor.enableFileSnapshot) }
    val enableSnapshot_Content = rememberSaveable { mutableStateOf(settingsState.value.editor.enableContentSnapshot) }
    val diff_CreateSnapShotForOriginFileBeforeSave = rememberSaveable { mutableStateOf(settingsState.value.diff.createSnapShotForOriginFileBeforeSave) }
    val pullWithRebase = rememberSaveable { mutableStateOf(PrefUtil.getGlobalGitConfigPullWithRebase(AppModel.realAppContext)) }
    val commitOnPush = rememberSaveable { mutableStateOf(PrefUtil.getGlobalGitConfigCommitOnPush(AppModel.realAppContext)) }
    val lfsEnabled = rememberSaveable { mutableStateOf(PrefUtil.getGlobalGitConfigLfsEnabled(AppModel.realAppContext)) }


    val fileAssociationList = mutableCustomStateListOf(stateKeyTag, "fileAssociationList") { settingsState.value.editor.fileAssociationList }
    val fileAssociationListBuf = rememberSaveable { mutableStateOf("") }
    val showSetFileAssociationDialog = rememberSaveable { mutableStateOf(false) }
    val initSetFileAssociationDialog = {
        fileAssociationListBuf.value = StrListUtil.listToLines(fileAssociationList.value)
        showSetFileAssociationDialog.value = true
    }

    if(showSetFileAssociationDialog.value) {
        val closeDialog = { showSetFileAssociationDialog.value = false }
        val cancelText = stringResource(R.string.cancel)

        ConfirmDialog3(
            title = stringResource(R.string.file_association),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    // get height for add bottom padding when showing softkeyboard
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
//                                println("layoutCoordinates.size.height:${layoutCoordinates.size.height}")
                        // 获取组件的高度
                        // unit is px ( i am not very sure)
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(stringResource(R.string.file_association_note), fontWeight = FontWeight.Light)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = fileAssociationListBuf.value,
                        onValueChange = {
                            fileAssociationListBuf.value = it
                        },
                        label = {
                            Text(stringResource(R.string.file_name_patterns))
                        },
                    )

                    Spacer(Modifier.height(10.dp))
                }
            },
            okBtnText = stringResource(R.string.save),
            cancelBtnText = cancelText,
            onCancel = closeDialog,
            customCancel = {
                ScrollableRow {
                    // reset to app default association list
                    TextButton(
                        onClick = {
                            fileAssociationListBuf.value = StrListUtil.listToLines(SettingsCons.editor_defaultFileAssociationList)
                            //这里没漏操作，故意不关弹窗的，感觉更好
                        }
                    ) {
                        Text(stringResource(R.string.reset), color = MyStyleKt.TextColor.danger())
                    }

                    // cancel
                    TextButton(
                        onClick = closeDialog
                    ) {
                        Text(cancelText)
                    }
                }
            }
        ) {
            showSetFileAssociationDialog.value = false

            doJobThenOffLoading {
                val newValue = fileAssociationListBuf.value
                val newList = StrListUtil.linesToList(newValue)
                fileAssociationList.value.clear()
                fileAssociationList.value.addAll(newList)
                SettingsUtil.update {
                    it.editor.fileAssociationList = newList
                }

                Msg.requireShow(activityContext.getString(R.string.success))
            }
        }
    }

//    val groupContentByLineNum = rememberSaveable { mutableStateOf(settingsState.value.diff.groupDiffContentByLineNum) }

    val showCommitMsgTemplateDialog = rememberSaveable { mutableStateOf(false) }
    if(showCommitMsgTemplateDialog.value) {
        CommitMsgTemplateDialog(
            stateKeyTag = stateKeyTag,
            closeDialog = { showCommitMsgTemplateDialog.value = false }
        )
    }

    val showCleanDialog = rememberSaveable { mutableStateOf(false) }
    val cleanCacheFolder = rememberSaveable { mutableStateOf(true) }
    val cleanEditCache = rememberSaveable { mutableStateOf(true) }
    val cleanSnapshot = rememberSaveable { mutableStateOf(true) }
    val cleanLog = rememberSaveable { mutableStateOf(true) }
//    val cleanContentSnapshot = rememberSaveable { mutableStateOf(true) }
    val cleanStoragePath = rememberSaveable { mutableStateOf(false) }
    val cleanFileOpenHistory = rememberSaveable { mutableStateOf(false) }

    val allowUnknownHosts = rememberSaveable { mutableStateOf(settingsState.value.sshSetting.allowUnknownHosts) }
    val httpSslVerify = rememberSaveable { mutableStateOf(settingsState.value.httpSetting.sslVerify) }

    //这几个本身就是state，不需要remember
//    val dev_singleDiffOn = rememberSaveable { DevFeature.singleDiff.state }
//    val dev_showMatchedAllAtDiff = rememberSaveable { DevFeature.showMatchedAllAtDiff.state }
//    val dev_showRandomLaunchingText = rememberSaveable { DevFeature.showRandomLaunchingText.state }
//    val dev_legacyChangeListLoadMethod = rememberSaveable { DevFeature.legacyChangeListLoadMethod.state }

//    val showResetKnownHostsDialog = rememberSaveable { mutableStateOf(false) }
    val showForgetHostKeysDialog = rememberSaveable { mutableStateOf(false) }
    if(showForgetHostKeysDialog.value) {
        ConfirmDialog3(
            requireShowTitleCompose = true,
            titleCompose = {},
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text = stringResource(R.string.after_forgetting_the_host_keys_may_ask_confirm_again),
                            fontSize = MyStyleKt.TextSize.medium
                        )
                    }
                }
            },
            okBtnText = stringResource(R.string.forget),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showForgetHostKeysDialog.value = false }
        ) {
            showForgetHostKeysDialog.value = false
            doJobThenOffLoading {
                try {
                    Lg2HomeUtils.resetUserKnownHostFile()
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?:"err")
                    MyLog.e(TAG, "ForgetHostKeysDialog err: ${e.stackTraceToString()}")
                }
            }
        }
    }


    val showImportSslCertsDialog = rememberSaveable { mutableStateOf(false) }
    if(showImportSslCertsDialog.value) {
        ConfirmDialog3(
            requireShowTitleCompose = true,
            titleCompose = {},
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text = stringResource(R.string.import_ssl_certs_intro_text),
                            fontSize = MyStyleKt.TextSize.medium
                        )
                    }
                }
            },
            onCancel = {
                showImportSslCertsDialog.value = false
            },
            customCancel = {
                IconButton(
                    onClick = {
                        goToFilesPage(AppModel.certUserDir.canonicalPath)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Folder,
                        contentDescription = "a folder icon for go to user's cert folder"
                    )
                }
            }
        ) {
            showImportSslCertsDialog.value = false
        }
    }



//    val updateMasterPassFailedList = mutableCustomStateOf(stateKeyTag, "updateMasterPassFailedList", listOf<String>())
    val updateMasterPassFailedListStr = rememberSaveable { mutableStateOf("") }
    val showFailedUpdateMasterPasswordsCredentialList = rememberSaveable { mutableStateOf(false) }
    if(showFailedUpdateMasterPasswordsCredentialList.value) {
        CopyableDialog(
            title = stringResource(R.string.warn),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    Text(stringResource(R.string.below_credential_password_update_failed))
                    Spacer(Modifier.height(10.dp))
                    Text(updateMasterPassFailedListStr.value, fontWeight = FontWeight.ExtraBold)
                }
            },
            onCancel = { showFailedUpdateMasterPasswordsCredentialList.value = false }
        ) {
            showFailedUpdateMasterPasswordsCredentialList.value = false

            doJobThenOffLoading {
                clipboardManager.setText(AnnotatedString(updateMasterPassFailedListStr.value))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
    }

    val showSetTimeZoneDialog = rememberSaveable { mutableStateOf(false) }
    val timeZone_followSystem = rememberSaveable { mutableStateOf(settingsState.value.timeZone.followSystem) }
    val timeZone_followSystemBuf = rememberSaveable { mutableStateOf(timeZone_followSystem.value) }
    val timeZone_offsetInMinute = rememberSaveable { mutableStateOf(settingsState.value.timeZone.offsetInMinutes) }
    val timeZone_offsetInMinuteBuf = rememberSaveable { mutableStateOf(timeZone_offsetInMinute.value) }
    val getTimeZoneStr = {
        try {
            val offsetInMinutes = if(timeZone_followSystem.value) {
                AppModel.getSystemTimeZoneOffsetInMinutesCached()
            } else {
                val offsetInMinuteFromSettings = timeZone_offsetInMinute.value.trim().toInt()

                if(isValidOffsetInMinutes(offsetInMinuteFromSettings)){
                    offsetInMinuteFromSettings
                } else{
                    //存储的时区偏移量无效，重置下
                    timeZone_offsetInMinute.value = ""
                    SettingsUtil.update {
                        it.timeZone.offsetInMinutes = ""
                    }

                    val errMsg = getInvalidTimeZoneOffsetErrMsg(offsetInMinuteFromSettings)
                    MyLog.e(TAG, "#getTimeZoneStr err: $errMsg")
                    throw RuntimeException(errMsg)
                }
            }

            val offsetInUtcFormat = formatMinutesToUtc(offsetInMinutes)

            if(timeZone_followSystem.value) {
                "${activityContext.getString(R.string.follow_system)} ($offsetInUtcFormat)"
            }else {
                offsetInUtcFormat
            }

//            val prefix = if(timeZone_followSystem.value) {
//                activityContext.getString(R.string.follow_system)
//            }else {
//                //如果有符号，直接返回，否则添加+号
//                if(offsetInMinutes < 0) {
//                    "$offsetInMinutes"
//                }else {
//                    //大于等于0
//                    "+$offsetInMinutes"
//                }
//            }
//
//            "$prefix ($offsetInUtcFormat)"
        }catch (_:Exception) {

            activityContext.getString(R.string.default_timezone_rule_description)
//            activityContext.getString(R.string.use_offset_from_commits)
        }
    }

    val initSetTimeZoneDialog = {
        timeZone_offsetInMinuteBuf.value = timeZone_offsetInMinute.value
        timeZone_followSystemBuf.value = timeZone_followSystem.value

        showSetTimeZoneDialog.value = true
    }


    if(showSetTimeZoneDialog.value) {
        ConfirmDialog2(title = stringResource(R.string.timezone),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    MyCheckBox(stringResource(R.string.follow_system), timeZone_followSystemBuf)

                    if(timeZone_followSystemBuf.value.not()) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MyStyleKt.defaultItemPadding)
                            ,
                            singleLine = true,

                            value = timeZone_offsetInMinuteBuf.value,
                            onValueChange = {
                                timeZone_offsetInMinuteBuf.value=it
                            },
                            label = {
                                Text(stringResource(R.string.offset_in_minutes))
                            },
//                        placeholder = {}
                        )

                        MySelectionContainer {
                            Column(modifier= Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                                    Text(stringResource(R.string.timezone_offset_example), fontWeight = FontWeight.Light)
                                }

                                Row(modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                                    Text(stringResource(R.string.set_timezone_leave_empty_note), color = MyStyleKt.TextColor.getHighlighting())
                                }
                            }
                        }

                        Column(
                            modifier= Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp)
                            ,
                            horizontalAlignment = Alignment.End
                        ) {

                            ClickableText(
                                text = stringResource(R.string.get_system_timezone_offset),
                                modifier = MyStyleKt.ClickableText.modifier.clickable {
                                    try {
                                        timeZone_offsetInMinuteBuf.value = AppModel.getSystemTimeZoneOffsetInMinutesCached().toString()
                                    } catch (e: Exception) {
                                        Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                                        MyLog.e(TAG, "#SetTimeZoneOffsetDialog: get system time zone offset err: ${e.stackTraceToString()}")
                                    }
                                },
                                fontWeight = FontWeight.Light
                            )

                            Spacer(Modifier.height(15.dp))

                            ClickableText(
                                text = stringResource(R.string.clear),
                                modifier = MyStyleKt.ClickableText.modifier.clickable {
                                    timeZone_offsetInMinuteBuf.value = ""
                                },
                                fontWeight = FontWeight.Light
                            )

                            Spacer(Modifier.height(10.dp))

                        }

                    }
                }


            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetTimeZoneDialog.value = false }
        ) {
            showSetTimeZoneDialog.value = false
            doJobThenOffLoading {
                val newOffset = try {
                    val offsetMinutes = timeZone_offsetInMinuteBuf.value.trim().toInt()
                    if(isValidOffsetInMinutes(offsetMinutes)) {
                        offsetMinutes.toString()
                    }else {
                        //显示给用户看的错误信息，尽量短
                        Msg.requireShowLongDuration("invalid offset, should in ${getValidTimeZoneOffsetRangeInMinutes()}")


                        //记日志的错误信息，尽量详细
                        val errMsg = getInvalidTimeZoneOffsetErrMsg(offsetMinutes)
                        MyLog.e(TAG, "user input invalid timezone offset: $errMsg")
                        throw RuntimeException(errMsg)
                    }
                }catch (_:Exception) {
                    ""
                }

                val newFollowSystem = timeZone_followSystemBuf.value

                // update if need
                if(newOffset != timeZone_offsetInMinute.value || newFollowSystem != timeZone_followSystem.value) {
                    timeZone_offsetInMinute.value = newOffset
                    timeZone_followSystem.value = newFollowSystem

                    val settingsUpdated = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                        it.timeZone.offsetInMinutes = newOffset
                        it.timeZone.followSystem = newFollowSystem
                    }!!

                    //更新下全局变量，不然还要重启app才能获取到app最新时区
                    AppModel.reloadTimeZone(settingsUpdated)
                }
            }
        }
    }


    val oldMasterPassword = rememberSaveable { mutableStateOf("") }
    val newMasterPassword = rememberSaveable { mutableStateOf("") }
    val oldMasterPasswordErrMsg = rememberSaveable { mutableStateOf("") }
    val newMasterPasswordErrMsg = rememberSaveable { mutableStateOf("") }
    val oldMasterPasswordVisible = rememberSaveable { mutableStateOf(false) }
    val newMasterPasswordVisible = rememberSaveable { mutableStateOf(false) }

    val masterPassEnabled = rememberSaveable { mutableStateOf(AppModel.masterPasswordEnabled()) }
    val masterPassStatus = rememberSaveable { mutableStateOf(if(masterPassEnabled.value) activityContext.getString(R.string.enabled) else activityContext.getString(R.string.disabled)) }

    val showSetMasterPasswordDialog = rememberSaveable { mutableStateOf(false) }
    if (showSetMasterPasswordDialog.value)  {
        // 这个废弃了，无论用户是否在启动时手动验证过主密码，在这再请求输入旧密码都是合理的
//        val requireOldPass = AppModel.requireMasterPassword()

        //最初，主密码每次启动都得输入，所以能打开app就代表主密码已验证，
        // 所以这里可能无需旧密码就能设置新密码，但后来，
        // 为了方便，改成了记住主密码经过编码后的值并和保存的hash验证，
        // 所以启动不再请求主密码，这意味着打开app不再代表手动输入过主密码，
        // 所以，修改主密码时若存在旧密码则应强制验证旧密码，
        // 代码实现上就把以前的逻辑 “若存在并已缓存旧密码就不需要用户再输入” 反转下，变成 “若存在主密码，则强制用户重新输入” 就行，
        // 或者“如果启用了主密码则必须输入旧密码”，也行。
        val requireOldPass = masterPassEnabled.value  //若设置了主密码，则需要验证旧密码才能设新的

        ConfirmDialog2(
            title = stringResource(R.string.set_master_password),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    // require old master password if exist
                    if(requireOldPass) {
                        PasswordTextFiled(oldMasterPassword, oldMasterPasswordVisible, stringResource(R.string.old_password), errMsg = oldMasterPasswordErrMsg)
                    }

                    // new password
                    PasswordTextFiled(newMasterPassword, newMasterPasswordVisible, stringResource(R.string.new_password), errMsg = newMasterPasswordErrMsg)

                    MySelectionContainer {
                        Column {
                            DefaultPaddingText(stringResource(R.string.leave_new_password_empty_if_dont_want_to_use_master_password), color = MyStyleKt.TextColor.getHighlighting())

                            if(newMasterPassword.value.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                DefaultPaddingText(stringResource(R.string.please_make_sure_you_can_remember_your_master_password), color = MyStyleKt.TextColor.danger())
                            }
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.save),
            onCancel = {showSetMasterPasswordDialog.value = false}
        ) {
            doJobThenOffLoading job@{
                try {
                    val oldPass  = if(requireOldPass) {  //使用用户输入的主密码验证
                        if(oldMasterPassword.value.isEmpty()){
                            oldMasterPasswordErrMsg.value = activityContext.getString(R.string.require_old_password)
                            return@job
                        }

                        if(HashUtil.verify(oldMasterPassword.value, settingsState.value.masterPasswordHash).not()) {
                            oldMasterPasswordErrMsg.value = activityContext.getString(R.string.wrong_password)
                            return@job
                        }

                        oldMasterPassword.value
                    }else {  //使用缓存的用户主密码验证（最初为首次启动时由用户手动输入，后来改成解码保存在本地的用户主密码而不再需要手动输入）
                        AppModel.masterPassword.value
                    }

                    //验证旧密码成功就可关闭弹窗了
                    showSetMasterPasswordDialog.value = false

                    val updatingStr = activityContext.getString(R.string.updating)
                    Msg.requireShow(updatingStr)

                    //新密码可以为空，不验证，若空等于清除凭据
                    val newPass = newMasterPassword.value
                    //密码一样，啥也不用干
                    if(newPass == oldPass) {
                        Msg.requireShow(activityContext.getString(R.string.old_and_new_passwords_are_the_same))
                        return@job
                    }

                    //检查密码是否能编码存储解码还原
                    if(!MasterPassUtil.goodToSave(newPass)) {
                        Msg.requireShow(activityContext.getString(R.string.encode_new_password_failed_plz_try_another_one))
                        return@job
                    }

                    //设置页相关条目显示“更新中...”
                    masterPassStatus.value = updatingStr


                    //对已有密码执行解密加密
                    val credentialDb = AppModel.dbContainer.credentialRepository
                    val failedList = credentialDb.updateMasterPassword(oldPass, newPass)

                    //解密加密结束，最起码没抛异常，再保存
                    val newSettings = MasterPassUtil.save(AppModel.realAppContext, newPass)


                    if(failedList.isEmpty()) { //全部解密然后加密成功
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }else {  //有解密失败的
                        val suffix = ", "
                        val sb = StringBuilder()
                        for (i in failedList) {
                            sb.append(i).append(suffix)
                        }

                        //显示弹窗，让用户知道哪些解密失败
                        updateMasterPassFailedListStr.value = sb.removeSuffix(suffix).toString()
                        showFailedUpdateMasterPasswordsCredentialList.value = true
                    }

                    //只要执行到这，不管是否有解密失败的凭据，主密码都已经更新了

                    //更新下主密码状态，让用户知道更新成功了
                    masterPassEnabled.value = AppModel.masterPasswordEnabled()
                    //若最新状态为启用，则检查是否请求旧密码，若请求，则是更新了密码，否则是创建了新密码；若状态为禁用，则代表已禁用主密码
                    masterPassStatus.value = activityContext.getString(if(masterPassEnabled.value) { if(requireOldPass) R.string.updated else R.string.enabled } else R.string.disabled)

                    //更新当前页面的settings state，不然不切换页面或退出app会无法验证旧密码
                    settingsState.value = newSettings

                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: "+e.localizedMessage)
                    MyLog.e(TAG, "SetMasterPasswordDialog err: ${e.stackTraceToString()}")
                }
            }
        }
    }


    val showClearMasterPasswordDialog = rememberSaveable { mutableStateOf(false) }
    if(showClearMasterPasswordDialog.value) {
        ClearMasterPasswordDialog(
            onCancel = {showClearMasterPasswordDialog.value = false},
            onOk = {
                showClearMasterPasswordDialog.value = false

                //把master pass状态设为禁用
                masterPassEnabled.value = false
                masterPassStatus.value = activityContext.getString(R.string.disabled)

                //提示成功
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        )
    }

//    val showForgetMasterPasswordDialog = rememberSaveable { mutableStateOf(false) }




//    if(showResetKnownHostsDialog.value) {
//        ConfirmDialog2(
//            title = stringResource(R.string.confirm),
//            requireShowTextCompose = true,
//            textCompose = {
//                ScrollableColumn {
//                    Text(stringResource(R.string.will_reset_the_unknown_hosts_file))
//                }
//            },
//            okBtnText = stringResource(R.string.reset),
//            okTextColor = MyStyleKt.TextColor.danger(),
//            onCancel = {showResetKnownHostsDialog.value = false}
//        ) {
//            showResetKnownHostsDialog.value = false
//            doJobThenOffLoading {
//                try {
//                    Lg2HomeUtils.resetKnownHostFile(appContext)
//                    Msg.requireShow(appContext.getString(R.string.success))
//                }catch (e:Exception) {
//                    Msg.requireShowLongDuration(e.localizedMessage ?:"err")
//                    MyLog.e(TAG, "ResetKnownHostsDialog err: ${e.stackTraceToString()}")
//                }
//            }
//        }
//    }

    if(showCleanDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.clean),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MyCheckBox(stringResource(R.string.log), cleanLog)
                    MyCheckBox(stringResource(R.string.cache), cleanCacheFolder)
                    MyCheckBox(stringResource(R.string.edit_cache), cleanEditCache)
                    MyCheckBox(stringResource(R.string.all_snapshots), cleanSnapshot)  // file snapshots and content snapshots all will be delete
                    MyCheckBox(stringResource(R.string.storage_paths), cleanStoragePath)
                    if(cleanStoragePath.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.the_storage_path_are_the_paths_you_chosen_and_added_when_cloning_repo))
                        }
                    }
                    MyCheckBox(stringResource(R.string.file_opened_history), cleanFileOpenHistory)
                    if(cleanFileOpenHistory.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.this_include_editor_opened_files_history_and_their_last_edited_position))
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.clean),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showCleanDialog.value = false}
        ) {
            showCleanDialog.value=false

            doJobThenOffLoading {
                Msg.requireShow(activityContext.getString(R.string.cleaning))

                if(cleanLog.value) {
                    try {
                        AppModel.getOrCreateLogDir().deleteRecursively()
                        AppModel.getOrCreateLogDir()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean log err: ${e.stackTraceToString()}")
                    }
                }

                if(cleanCacheFolder.value) {
                    // clean external data cache
                    try {
                        AppModel.externalCacheDir.deleteRecursively()
                        AppModel.externalCacheDir.mkdirs()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean external cache dir err: ${e.stackTraceToString()}")
                    }

                    // clean inner data cache
                    try {
                        AppModel.innerCacheDir?.deleteRecursively()
                        AppModel.innerCacheDir?.mkdirs()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean inner cache dir err: ${e.stackTraceToString()}")
                    }

                }

                if(cleanEditCache.value) {
                    try {
                        AppModel.getOrCreateEditCacheDir().deleteRecursively()
                        AppModel.getOrCreateEditCacheDir()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean edit cache err: ${e.stackTraceToString()}")
                    }
                }

                if(cleanSnapshot.value) {
                    try {
                        AppModel.getOrCreateFileSnapshotDir().deleteRecursively()
                        AppModel.getOrCreateFileSnapshotDir()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean file and content snapshot err: ${e.stackTraceToString()}")
                    }
                }


                // 只是清除路径列表，不会删除路径对应的文件或目录
                if(cleanStoragePath.value) {
                    try {
                        StoragePathsMan.reset()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean storage paths err: ${e.stackTraceToString()}")
                    }
                }

                if(cleanFileOpenHistory.value) {
                    try {
                        FileOpenHistoryMan.reset()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean file opened history err: ${e.stackTraceToString()}")
                    }
                }

                Msg.requireShow(activityContext.getString(R.string.done))
            }
        }
    }


    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp= exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end

    val itemFontSize = MyStyleKt.SettingsItem.itemFontSize
    val itemDescFontSize = MyStyleKt.SettingsItem.itemDescFontSize

    val trailFolderIcon = Icons.Filled.Folder
    val trailFolderIconTooltipText = stringResource(R.string.show_in_files)

    @Composable
    fun TwoLineTrailingFolderItem(text1:String, text2:String, trailIconOnClick:()->Unit) {
        TwoLineSettingsItem(
            text1 = text1,
            text1FontSize = itemFontSize,
            text2 = text2,
            text2FontSize = itemDescFontSize,

            trailIcon = trailFolderIcon,
            trailIconTooltipText = trailFolderIconTooltipText,
            trailIconWidth = trailIconWidth,
            trailIconOnClick = trailIconOnClick
        )
    }

    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, listState)
    ) {
        SettingsTitle(stringResource(R.string.general))

        SettingsContentSelector(
            left = {
                Text(stringResource(R.string.theme), fontSize = itemFontSize)
            },
            right = {
                SingleSelectList(
                    optionsList = themeList,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedTheme.intValue,
                    menuItemSelected = {_, value -> value == selectedTheme.intValue },
                    menuItemFormatter = {_, value -> Theme.getThemeTextByCode(value, activityContext)},
                    menuItemOnClick = { _, value ->
                        selectedTheme.intValue = value

                        Theme.updateThemeValue(activityContext, value)
                    }
                )
            }
        )


        SettingsContentSelector(
            left = {
                Text(stringResource(R.string.language), fontSize = itemFontSize)
                Text(stringResource(R.string.require_restart_app), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
            },
            right = {
                SingleSelectList(
                    optionsList = languageList,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedLanguage.value,
                    menuItemOnClick = { index, value ->
                        selectedLanguage.value = value

                        if(value != LanguageUtil.getLangCode(activityContext)) {
                            LanguageUtil.setLangCode(activityContext, value)
                        }
                    },
                    menuItemSelected = {index, value ->
                        value == selectedLanguage.value
                    },
                    menuItemFormatter = { index, value ->
                        LanguageUtil.getLanguageTextByCode(value?:"", activityContext)
                    }
                )
            }
        )

        SettingsContentSelector(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.log_level),
                    text2 = "",
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateLogDir().canonicalPath)
                    }
                )
            },
            right = {
                SingleSelectList(
                    optionsList = logLevelList,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedLogLevel.value,
                    menuItemOnClick = { index, value ->
                        selectedLogLevel.value = value

                        if(value != MyLog.getCurrentLogLevel()) {
                            // set in memory
                            MyLog.setLogLevel(value.get(0))

                            // save to disk
                            PrefMan.set(activityContext, PrefMan.Key.logLevel, value)
                        }
                    },
                    menuItemSelected = {index, value ->
                        value == selectedLogLevel.value
                    },
                    menuItemFormatter = { index, value ->
                        MyLog.getTextByLogLevel(value?:"", activityContext)
                    }
                )
            }
        )


        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.dynamic_color_scheme), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = Theme.dynamicColor.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                Theme.updateDynamicColor(activityContext, !Theme.dynamicColor.value)
            }
        )



        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.dev_mode), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = devModeOn.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                //获取新值
                val newValue = !devModeOn.value

                //更新页面状态
                devModeOn.value = newValue

                //更新app内存中存储的状态
                AppModel.devModeOn = newValue

                //保存到配置文件
                PrefUtil.setDevMode(activityContext, newValue)
            }
        )


        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.go_to_top_bottom_buttons), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = showNaviButtons.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !showNaviButtons.value

                //save
                showNaviButtons.value = newValue
                SettingsUtil.update {
                    it.showNaviButtons = newValue
                }
            }
        )



        SettingsContent(onClick = {
            initSetTimeZoneDialog()
        }) {
            Column {
                Text(stringResource(R.string.timezone), fontSize = itemFontSize)
                Text(getTimeZoneStr(), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, color = MyStyleKt.TextColor.getHighlighting())
//                Text(stringResource(R.string.timezone_when_viewing_commits), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)

            }
        }


        SettingsContent(onClick = {
            showCommitMsgTemplateDialog.value = true
        }) {
            Text(stringResource(R.string.commit_msg_template), fontSize = itemFontSize)
        }


        SettingsContent(onClick = {
            showCleanDialog.value = true
        }) {
            Text(stringResource(R.string.clean), fontSize = itemFontSize)
        }



        SettingsTitle(stringResource(R.string.editor))

        SettingsContent(onClick = {
            initSetFileAssociationDialog()
        }) {
            Text(stringResource(R.string.file_association), fontSize = itemFontSize)
        }


        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.syntax_highlighting), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = syntaxHighlightEnabled.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !syntaxHighlightEnabled.value

                //save
                syntaxHighlightEnabled.value = newValue
                SettingsUtil.update {
                    it.editor.syntaxHighlightEnabled = newValue
                }
            }
        )

        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.use_system_fonts), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = useSystemFonts.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !useSystemFonts.value

                //save
                useSystemFonts.value = newValue
                SettingsUtil.update {
                    it.editor.useSystemFonts = newValue
                }
            }
        )


        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.file_snapshot),
                    text2 = stringResource(R.string.file_snapshot_desc),
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = enableSnapshot_File.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !enableSnapshot_File.value
                enableSnapshot_File.value = newValue
                SettingsUtil.update {
                    it.editor.enableFileSnapshot = newValue
                }

                SnapshotUtil.update_enableFileSnapshotForEditor(newValue)
            }
        )

        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.content_snapshot),
                    text2 = stringResource(R.string.content_snapshot_desc),

                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = enableSnapshot_Content.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !enableSnapshot_Content.value
                enableSnapshot_Content.value = newValue
                SettingsUtil.update {
                    it.editor.enableContentSnapshot = newValue
                }

                SnapshotUtil.update_enableContentSnapshotForEditor(newValue)
            }
        )




        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.edit_cache),
                    text2 = replaceStringResList(stringResource(R.string.cache_your_input_into_editcache_dir_path), listOf("${Cons.defalutPuppyGitDataUnderAllReposDirName}/${Cons.defaultEditCacheDirName}")),
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateEditCacheDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = enableEditCache.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !enableEditCache.value

                //save
                enableEditCache.value = newValue
                val settings = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                    it.editor.editCacheEnable = newValue
                }!!

                //重新初始化EditCache
                EditCache.init(enableCache = newValue, cacheDir = AppModel.getOrCreateEditCacheDir(), keepInDays = settings.editor.editCacheKeepInDays)
            }
        )




        // diff settings block start
//
        SettingsTitle(stringResource(R.string.diff))


        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.syntax_highlighting), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = syntaxHighlightEnabled_DiffScreen.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !syntaxHighlightEnabled_DiffScreen.value

                //save
                syntaxHighlightEnabled_DiffScreen.value = newValue
                SettingsUtil.update {
                    it.diff.syntaxHighlightEnabled = newValue
                }
            }
        )


        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.use_system_fonts), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = useSystemFonts_DiffScreen.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !useSystemFonts_DiffScreen.value

                //save
                useSystemFonts_DiffScreen.value = newValue
                SettingsUtil.update {
                    it.diff.useSystemFonts = newValue
                }
            }
        )

        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.file_snapshot),
                    text2 = stringResource(R.string.file_snapshot_desc),

                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = diff_CreateSnapShotForOriginFileBeforeSave.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !diff_CreateSnapShotForOriginFileBeforeSave.value
                diff_CreateSnapShotForOriginFileBeforeSave.value = newValue
                SettingsUtil.update {
                    it.diff.createSnapShotForOriginFileBeforeSave = newValue
                }
                SnapshotUtil.update_enableFileSnapshotForDiff(newValue)
            }
        )

//
//        SettingsContent(onClick = {
//            val newValue = !groupContentByLineNum.value
//
//            groupContentByLineNum.value = newValue
//            SettingsUtil.update {
//                it.diff.groupDiffContentByLineNum = newValue
//            }
//        }) {
//            Column(modifier = Modifier.fillMaxWidth(itemLeftWidthForSwitcher)) {
//                Text(stringResource(R.string.group_content_by_line_num), fontSize = itemFontSize)
////                Text(stringResource(R.string.before_saving_a_file_create_a_snapshot_first), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
////                Text(stringResource(R.string.require_restart_app), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
//
//            }
//
//            Icon(
//                modifier = Modifier.size(switcherIconSize),
//                imageVector = UIHelper.getIconForSwitcher(groupContentByLineNum),
//                contentDescription = if(groupContentByLineNum.value) stringResource(R.string.enable) else stringResource(R.string.disable),
//                tint = UIHelper.getColorForSwitcher(groupContentByLineNum),
//
//            )
//        }
        // diff settings block end



        SettingsTitle(StrCons.git)

        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.pull_with_rebase), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = pullWithRebase.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !pullWithRebase.value

                //save
                pullWithRebase.value = newValue
                PrefUtil.setGlobalGitConfigPullWithRebase(AppModel.realAppContext, newValue)
            }
        )

        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.commit_on_push), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = commitOnPush.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !commitOnPush.value

                //save
                commitOnPush.value = newValue
                PrefUtil.setGlobalGitConfigCommitOnPush(AppModel.realAppContext, newValue)
            }
        )

        if(lfsTestPassed) {
            SettingsContentSwitcher(
                left = {
                    Text(stringResource(R.string.lfs), fontSize = itemFontSize)
                },
                right = {
                    Switch(
                        checked = lfsEnabled.value,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    val newValue = !lfsEnabled.value

                    //save
                    lfsEnabled.value = newValue
                    PrefUtil.setGlobalGitConfigLfsEnabled(AppModel.realAppContext, newValue)

                    try {
                        if(newValue) {
                            LibgitTwo.registerLfsFilter(FsUtils.getLfsBinPath())
                        }else {
                            LibgitTwo.unregisterLfsFilter()
                        }
                    }catch (e: Exception) {
                        Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                        MyLog.e(TAG, "register or unregister lfs filter err: register=$newValue, err:\n${e.stackTraceToString()}")
                    }
                }
            )
        }



        SettingsTitle(stringResource(R.string.ssh))

        SettingsContentSwitcher(
            left = {

                Text(stringResource(R.string.allow_unknown_hosts), fontSize = itemFontSize)
                //                Text("If enable, can connect host not in the 'known_hosts' file, else will reject, if you want to more safe, add trusted hosts info into the 'known_hosts' and disable this feature", fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                Text(stringResource(R.string.if_enable_will_allow_unknown_hosts_as_default_else_will_ask), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
            },
            right = {
                Switch(
                    checked = allowUnknownHosts.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !allowUnknownHosts.value
                allowUnknownHosts.value = newValue
                SettingsUtil.update {
                    it.sshSetting.allowUnknownHosts = newValue
                }
            }
        )

//        SettingsContent(onClick = {
//            openFileWithInnerSubPageEditor(
//                filePath = Lg2Utils.getKnownHostsFile(appContext).canonicalPath,
//                mergeMode = false,
//                readOnly = false
//            )
//        }) {
//            Column {
//                Text(stringResource(R.string.edit_known_hosts_file), fontSize = itemFontSize)
//            }
//        }
//        SettingsContent(onClick = {
//            showResetKnownHostsDialog.value =true
//        }) {
//            Column {
//                Text(stringResource(R.string.reset_known_hosts), fontSize = itemFontSize)
//            }
//        }
        SettingsContent(onClick = {
            showForgetHostKeysDialog.value = true
        }) {
            Column {
                Text(stringResource(R.string.forget_hostkeys), fontSize = itemFontSize)
            }
        }


        SettingsTitle(stringResource(R.string.http_https))

        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.ssl_verify), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = httpSslVerify.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !httpSslVerify.value
                httpSslVerify.value = newValue
                SettingsUtil.update {
                    it.httpSetting.sslVerify = newValue
                }
            }
        )

        SettingsContent(onClick = {
            showImportSslCertsDialog.value = true
        }) {
            Column {
                Text(stringResource(R.string.import_ssl_certs), fontSize = itemFontSize)
            }
        }

        SettingsContent(onClick = {
            goToFilesPage(AppModel.certUserDir.canonicalPath)
        }) {
            Column {
                Text(stringResource(R.string.manage_ssl_certs), fontSize = itemFontSize)
            }
        }



        SettingsTitle(stringResource(R.string.master_password))

        SettingsContent(onClick = {
            //初始化变量
            oldMasterPassword.value = ""
            newMasterPassword.value = ""
            oldMasterPasswordErrMsg.value = ""
            newMasterPasswordErrMsg.value = ""
            oldMasterPasswordVisible.value = false
            newMasterPasswordVisible.value = false

            //显示弹窗
            showSetMasterPasswordDialog.value = true
        }) {
            Column {
                Text(stringResource(R.string.set_master_password), fontSize = itemFontSize)
                Text(masterPassStatus.value, fontSize = itemDescFontSize, fontWeight = FontWeight.Light, color = if(masterPassEnabled.value) MyStyleKt.TextColor.getHighlighting() else MyStyleKt.TextColor.danger())
                Text(stringResource(R.string.if_set_will_require_master_password_when_launching_app), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)

            }
        }

        //如果有主密码，显示个忘记密码，点击弹窗询问是否清空密码
        if(masterPassEnabled.value) {
            SettingsContent(onClick = {
                //显示弹窗
                showClearMasterPasswordDialog.value = true
            }) {
                Column {
                    Text(stringResource(R.string.i_forgot_my_master_password), fontSize = itemFontSize)
                }
            }
        }


//        SettingsContent(onClick = {
//            oldMasterPassword.value = ""
//            newMasterPassword.value = ""
//            showForgetMasterPasswordDialog.value = true
//        }) {
//            Column {
//                Text(stringResource(R.string.i_forgot_my_master_password), fontSize = itemFontSize)
//            }
//        }


        SettingsTitle(stringResource(R.string.permissions))
        SettingsContent(onClick = {
            ActivityUtil.getManageStoragePermissionOrShowFailedMsg(activityContext)
        }) {
            Column {
                Text(stringResource(R.string.manage_storage), fontSize = itemFontSize)
                Text(stringResource(R.string.if_you_want_to_clone_repo_into_external_storage_this_permission_is_required), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
            }
        }

        //一些用来测试的功能
        if(devModeOn.value) {
            SettingsTitle("Dev Zone")

            // dev settings items
            DevFeature.settingsItemList.forEachBetter {
                DevBooleanSettingsItem(
                    item = it,
                    context = activityContext,
                    itemFontSize = itemFontSize,
                    itemDescFontSize = itemDescFontSize,
                )
            }



            // crash the app
            SettingsContent(onClick = {
                throw RuntimeException("App Crashed For Test Purpose")
            }) {
                Column {
                    Text("Crash App", fontSize = itemFontSize)
                }
            }

        }

        SpacerRow()
    }


    LaunchedEffect(needRefreshPage.value) {
        settingsState.value = SettingsUtil.getSettingsSnapshot()

    }


    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            showSetFileAssociationDialog.value.not()
        }
    )

}

@Composable
private fun DevBooleanSettingsItem(
    item: DevItem<Boolean>,
    context: Context,
    itemFontSize: TextUnit,
    itemDescFontSize: TextUnit,
) {
    SettingsContentSwitcher(
        left = {
            Text(item.text, fontSize = itemFontSize)

            // desc
            item.desc.let {
                if(it.isNotBlank()) {
                    Text(it, fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                }
            }
        },
        right = {
            Switch(
                checked = item.state.value,
                onCheckedChange = null
            )
        },
        onClick = {
            item.update(!item.state.value, context)
        }
    )
}
