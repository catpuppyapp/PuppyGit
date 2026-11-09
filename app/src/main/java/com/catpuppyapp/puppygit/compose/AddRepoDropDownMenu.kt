package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.goToCloneScreen

@Composable
fun AddRepoDropDownMenu(
    showMenu:Boolean,
    closeMenu:()->Unit,
    cloneOnClick:()->Unit = { goToCloneScreen() },
    importOnClick:()->Unit,
) {

    //菜单列表
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { closeMenu() }
    ) {

        DropdownMenuItem(
            text = { Text(stringResource(R.string.clone)) },
            onClick = {
                closeMenu()
                cloneOnClick()
            }
        )

        //之前觉得这里的功能和文件管理页面的导入仓库重复就设为开发者功能了，
        // 但至少有过两个用户问怎么导入本地仓库，他们都觉得导入应该在仓库顶栏，索性加回来了
        DropdownMenuItem(
//                text = { Text(DevFeature.appendDevPrefix(stringResource(R.string.import_repo))) },
            text = { Text(stringResource(R.string.import_str)) },
            onClick = {
                closeMenu()
                importOnClick()
            }
        )


    }
}
