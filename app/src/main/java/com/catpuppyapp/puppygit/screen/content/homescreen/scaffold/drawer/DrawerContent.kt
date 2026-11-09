package com.catpuppyapp.puppygit.screen.content.homescreen.scaffold.drawer

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun drawerContent(
    currentHomeScreen: MutableIntState,
    scope: CoroutineScope,
    drawerState: DrawerState,
    drawerItemShape: Shape,
    drawTextList: List<String>,
    drawIdList: List<Int>,
    drawIconList: List<ImageVector>,
    drawerItemOnClick:List<()->Unit>,
    showExit:Boolean, // 是否在末尾显示退出按钮
    filesPageKeepFilterResultOnce:MutableState<Boolean>,

): @Composable() (ColumnScope.() -> Unit) =
    {
        var drawTextList = drawTextList
        var drawIdList = drawIdList
        var drawIconList = drawIconList
        var drawerItemOnClick = drawerItemOnClick

        //添加退出按钮
        if(showExit) {
            drawTextList = drawTextList.toMutableList()
            drawIdList = drawIdList.toMutableList()
            drawIconList = drawIconList.toMutableList()
            drawerItemOnClick = drawerItemOnClick.toMutableList()

            (drawTextList as MutableList).add(stringResource(R.string.exit))
            (drawIdList as MutableList).add(Cons.selectedItem_Exit)
            (drawIconList as MutableList).add(Icons.AutoMirrored.Filled.ExitToApp)
            (drawerItemOnClick as MutableList).add(AppModel.exitApp)
        }

        val m = Modifier.padding(5.dp)

        for((index, text) in drawTextList.withIndex()) {
            val id = drawIdList[index]
            NavigationDrawerItem(
                modifier = m,

                //设置选中条目背景颜色
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                icon = {
                    Icon(imageVector = drawIconList[index], contentDescription = text)
                },
                shape = drawerItemShape,
                label = { Text(text) },
                selected = id == currentHomeScreen.intValue,
                onClick = {
                    //如果是从非Files页面点击Files，则不会触发过滤结果的刷新（隐藏feature：如果已经在Files页面，重新点击Files则会触发刷新）
                    if(Cons.selectedItem_Files.let { id == it && currentHomeScreen.intValue != it }) {
                        filesPageKeepFilterResultOnce.value = true
                    }

                    drawerItemOnClick[index]()
                    currentHomeScreen.intValue = id
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }
            )
        }

        //给免费用户看下广告
//        if(!UserUtil.isPro()) {
//            //这个Column只是为了把广告定位到底部
//            Column(modifier = Modifier.fillMaxHeight(),
//                verticalArrangement = Arrangement.Bottom
//            ){
//                AdmobBanner(modifier = Modifier.fillMaxWidth())
//            }
//        }

        //settings 页面暂时隐藏，日后再启用
//        Divider()
//        NavigationDrawerItem(
//            label = { Text(text = stringResource(id = R.string.settings)) },
//            selected = Cons.selectedItem_Settings == currentHomeScreen.intValue,
//            onClick = {
//                currentHomeScreen.intValue = Cons.selectedItem_Settings
//                scope.launch {
//                    drawerState.apply {
//                        if (isClosed) open() else close()
//                    }
//                }
//            }
//        )
        //other drawerItem
    }
