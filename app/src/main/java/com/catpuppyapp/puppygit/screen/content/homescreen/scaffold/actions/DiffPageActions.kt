package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DensityLarge
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.SimpleCheckBox
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.dev.detailsDiffTestPassed
import com.catpuppyapp.puppygit.dev.proFeatureEnabled
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil

private const val TAG = "DiffPageActions"

@Composable
fun DiffPageActions(
    isMultiMode: Boolean,
    fromTo:String,
    refreshPage: () -> Unit,
    request:MutableState<String>,
//    fileFullPath:String,
    requireBetterMatchingForCompare:MutableState<Boolean>,
    readOnlyModeOn:MutableState<Boolean>,  // `readOnlyMode` was named `copyMode`，当时想的是只能拷贝不能编辑，所以叫拷贝模式，但后来发现这不就是只读模式吗，所以就改成只读模式了
    readOnlyModeSwitchable:Boolean,
    showLineNum:MutableState<Boolean>,
    showOriginType:MutableState<Boolean>,
    adjustFontSizeModeOn:MutableState<Boolean>,
    adjustLineNumSizeModeOn:MutableState<Boolean>,
    groupDiffContentByLineNum:MutableState<Boolean>,
    enableSelectCompare:MutableState<Boolean>,
    matchByWords:MutableState<Boolean>,
//    syntaxHighlightEnabled:MutableState<Boolean>,
) {

//    val navController = AppModel.navController
//    val activityContext = LocalContext.current

    //这个变量相关的判断都没什么鸟用，都是禁用或启用都无所谓的，索性设为true了
//    val fileChangeTypeIsModified = changeType == Cons.gitStatusModified
    val fileChangeTypeIsModified = remember { true }

    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }

//    if (fileChangeTypeIsModified && UserUtil.isPro()
//        && (dev_EnableUnTestedFeature || detailsDiffTestPassed)
//    ){
//        LongPressAbleIconBtn(
//            tooltipText = stringResource(R.string.better_but_slow_compare),
//            icon = Icons.Filled.没合适的图标,
//            iconContentDesc = stringResource(R.string.better_but_slow_compare),
//            iconColor = UIHelper.getIconEnableColorOrNull(requireBetterMatchingForCompare.value),
//            enabled = true,
//        ) {
//            requireBetterMatchingForCompare.value = !requireBetterMatchingForCompare.value
//
//            // show msg: "better but slow compare: ON/OFF"
////            Msg.requireShow(
////                appContext.getString(R.string.better_but_slow_compare)+": "
////                + (if(requireBetterMatchingForCompare.value) appContext.getString(R.string.on_str) else appContext.getString(R.string.off_str))
////            )
//
//            SettingsUtil.update {
//                it.diff.enableBetterButSlowCompare = requireBetterMatchingForCompare.value
//            }
//
//        }
//
//    }

    if(fromTo == Cons.gitDiffFileHistoryFromTreeToPrev || fromTo == Cons.gitDiffFileHistoryFromTreeToLocal) {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.restore),
            icon =  Icons.Filled.Restore,
            iconContentDesc = stringResource(R.string.restore),
        ) {
            request.value = PageRequest.showRestoreDialog
        }

    }

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(id = R.string.refresh),
    ) {
        refreshPage()
    }

    if(isMultiMode){

        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.expand_all),
            icon = Icons.Filled.DensityLarge,
            iconContentDesc = stringResource(R.string.expand_all),
        ) label@{
            request.value = PageRequest.expandAll
        }

        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.collapse_all),
            icon = Icons.Filled.DensitySmall,
            iconContentDesc = stringResource(R.string.collapse_all),
        ) label@{
            request.value = PageRequest.collapseAll
        }

        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.go_to_bottom),
            icon = Icons.Filled.KeyboardDoubleArrowDown,
            iconContentDesc = stringResource(R.string.go_to_bottom),
        ) label@{
            request.value = PageRequest.goToBottomOfCurrentFile
        }


    } else {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.open),
            icon = Icons.Filled.FileOpen,
            iconContentDesc = stringResource(id = R.string.open),
        ) label@{
            request.value = PageRequest.requireOpenInInnerEditor

        }


        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.open_as),
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            iconContentDesc = stringResource(id = R.string.open_as),
        ) label@{
            //显示OpenAs弹窗
            request.value = PageRequest.showOpenAsDialog
        }

    }

    //menu icon
    LongPressAbleIconBtn(
        //这种需展开的菜单，禁用内部的选项即可
//        enabled = enableAction.value,

        tooltipText = stringResource(R.string.menu),
        icon = Icons.Filled.MoreVert,
        iconContentDesc = stringResource(R.string.menu),
        onClick = {
            //切换菜单展开状态
            dropDownMenuExpandState.value = !dropDownMenuExpandState.value
        }
    )

    // menu items
    DropdownMenu(
        offset = DpOffset(x=100.dp, y=8.dp),
        expanded = dropDownMenuExpandState.value,
        onDismissRequest = { dropDownMenuExpandState.value=false }
    ) {

        DropdownMenuItem(
            // append a "All" suffix to clarify it is for all items when multi files mode on
            text = { Text(stringResource(R.string.create_patch) + (if(isMultiMode) " (${stringResource(R.string.all)})" else "")) },
            onClick = {
                request.value = PageRequest.createPatchForAllItems

                dropDownMenuExpandState.value = false
            }

        )

        // if is multi mode, will show this at each item bar dropdown menu
        DropdownMenuItem(
            text = { Text(stringResource(R.string.syntax_highlighting)) },
            onClick = {
                request.value = PageRequest.showSyntaxHighlightingSelectLanguageDialogForCurItem

                dropDownMenuExpandState.value = false
            }

        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.font_size)) },
            onClick = {
                adjustFontSizeModeOn.value = true

                dropDownMenuExpandState.value = false
            }

        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.line_num_size)) },

            onClick = {
                adjustLineNumSizeModeOn.value = true

                dropDownMenuExpandState.value = false
            }

        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.show_line_num)) },
            trailingIcon = {
                SimpleCheckBox(showLineNum.value)
            },
            onClick = {
                showLineNum.value = !showLineNum.value

                SettingsUtil.update {
                    it.diff.showLineNum = showLineNum.value
                }

//                dropDownMenuExpandState.value = false
            }

        )


        DropdownMenuItem(
            text = { Text(stringResource(R.string.show_change_type)) },
            trailingIcon = {
                SimpleCheckBox(showOriginType.value)
            },
            onClick = {
                showOriginType.value = !showOriginType.value

                SettingsUtil.update {
                    it.diff.showOriginType = showOriginType.value
                }

//                dropDownMenuExpandState.value = false
            }

        )


        DropdownMenuItem(
            text = { Text(stringResource(R.string.group_by_line)) },
            trailingIcon = {
                SimpleCheckBox(groupDiffContentByLineNum.value)
            },
            onClick = {
                groupDiffContentByLineNum.value = !groupDiffContentByLineNum.value

                SettingsUtil.update {
                    it.diff.groupDiffContentByLineNum = groupDiffContentByLineNum.value
                }

//                dropDownMenuExpandState.value = false
            }
        )

        //非modified也可以开关这些选项，就是可能没什么卵用，但如果用户手动选择两个行比较，就有卵用了
        if (fileChangeTypeIsModified && proFeatureEnabled(detailsDiffTestPassed)){
            DropdownMenuItem(
                text = { Text(stringResource(R.string.better_compare)) },
                trailingIcon = {
                    SimpleCheckBox(requireBetterMatchingForCompare.value)
                },
                onClick = {
                    requireBetterMatchingForCompare.value = !requireBetterMatchingForCompare.value

                    SettingsUtil.update {
                        it.diff.enableBetterButSlowCompare = requireBetterMatchingForCompare.value
                    }

//                    dropDownMenuExpandState.value = false
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.match_by_words)) },
                trailingIcon = {
                    SimpleCheckBox(matchByWords.value)
                },
                onClick = {
                    matchByWords.value = !matchByWords.value

                    SettingsUtil.update {
                        it.diff.matchByWords = matchByWords.value
                    }

//                    dropDownMenuExpandState.value = false
                }
            )

        }

        // disabled reason: almost useless and must update syntax highlighting for all items when switched, maintenance it is too much...
        // 启用这个才会显示另一个同名的点击选择语言弹窗的选项
//        DropdownMenuItem(
//            enabled = true,
//            text = { Text(stringResource(R.string.syntax_highlighting)) },
//            trailingIcon = {
//                SimpleCheckBox(syntaxHighlightEnabled.value)
//            },
//            onClick = {
//                syntaxHighlightEnabled.value = !syntaxHighlightEnabled.value
//
//                SettingsUtil.update {
//                    it.diff.syntaxHighlightEnabled = syntaxHighlightEnabled.value
//                }
//
//                refreshPage()
//            }
//
//        )

        DropdownMenuItem(
            enabled = readOnlyModeSwitchable,
            text = { Text(stringResource(R.string.read_only)) },
            trailingIcon = {
                SimpleCheckBox(readOnlyModeOn.value)
            },
            onClick = {
                readOnlyModeOn.value = !readOnlyModeOn.value

                SettingsUtil.update {
                    it.diff.readOnly = readOnlyModeOn.value
                }

//                dropDownMenuExpandState.value = false
            }

        )

        DropdownMenuItem(
            enabled = fileChangeTypeIsModified,
            text = { Text(stringResource(R.string.select_compare)) },
            trailingIcon = {
                SimpleCheckBox(enableSelectCompare.value)
            },
            onClick = {
                enableSelectCompare.value = !enableSelectCompare.value

                SettingsUtil.update {
                    it.diff.enableSelectCompare = enableSelectCompare.value
                }

//                dropDownMenuExpandState.value = false
            }

        )


    }
}

