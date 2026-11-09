package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.catpuppyapp.puppygit.compose.AddRepoDropDownMenu
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@Composable
fun RepoPageActions(
    navController: NavHostController,
    curRepo: CustomStateSaveable<RepoEntity>,
    showGlobalUsernameAndEmailDialog: MutableState<Boolean>,
    needRefreshRepoPage: MutableState<String>,
    repoPageFilterModeOn:MutableState<Boolean>,
    repoPageFilterKeyWord:CustomStateSaveable<TextFieldValue>,
    showImportRepoDialog:MutableState<Boolean>
) {
    /*  TODO 添加个设置按钮
     * 跳转到仓库全局设置页面，至少两个开关：
     * Auto Fetch                default:Off
     * Auto check Status         default:Off
     */


    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false)}


    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.filter),
        icon =  Icons.Filled.FilterAlt,
        iconContentDesc = stringResource(id = R.string.filter),

    ) {
        repoPageFilterKeyWord.value = TextFieldValue("")
        repoPageFilterModeOn.value = true
    }

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(id = R.string.refresh),
    ) {
        changeStateTriggerRefreshPage(needRefreshRepoPage)
    }

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.user_info),
        icon = Icons.Filled.Person,
        iconContentDesc = stringResource(R.string.user_info),
    ) {
        showGlobalUsernameAndEmailDialog.value=true
    }

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.credential_manager),
        icon = Icons.Filled.Key,
        iconContentDesc = stringResource(R.string.credential_manager),
    ) {
        navController.navigate(Cons.nav_CredentialManagerScreen+"/${Cons.dbInvalidNonEmptyId}")
    }

    Column {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.clone),
            icon = Icons.Filled.Add,
            iconContentDesc = stringResource(R.string.clone),
        ) {
            dropDownMenuExpandState.value = !dropDownMenuExpandState.value
        }

        AddRepoDropDownMenu(
            showMenu = dropDownMenuExpandState.value,
            closeMenu = { dropDownMenuExpandState.value = false },
            importOnClick = {
                showImportRepoDialog.value = true
            }
        )

    }
}
