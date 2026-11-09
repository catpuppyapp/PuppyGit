package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import kotlinx.coroutines.launch


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun BottomSheet(
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    title: String,
    showCancel:Boolean=true,  // 是否显示取消按钮
    onCancel:()->Unit={showBottomSheet.value = false},  //取消按钮执行的操作，默认值是关闭弹窗
    footerPaddingSize:Int=10,
    content:@Composable ()->Unit
) {

        ModalBottomSheet(
            modifier = Modifier.systemBarsPadding()  // x 20240501 打脸了，加了什么false还是没效) x 20240501 在Activity的onCreate()加上什么WindowInsets false就有用了，imePadding()也是) 这个systemBarsPadding，理应会padding出顶部状态栏和底部导航栏的空间，但实际上，没用，还是手动加footer做padding了


            ,

            onDismissRequest = {
                //菜单开启时点空白处或返回键会触发这个方法
//                println("dismissrequest执行了")

                onCancel()
            },
            sheetState = sheetState,
        ) {
            BottomSheetTitle(title)
//            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                maxItemsInEachRow = 2,

            ) {
                content()  //菜单条目

                //如果显示取消按钮为true，则显示
                if(showCancel) {
                    BottomSheetItem(sheetState=sheetState, showBottomSheet=showBottomSheet, text=stringResource(R.string.cancel),
//                    textColor = MyStyleKt.TextColor.danger
                    ){
                        onCancel()
                    }
                }

                //这个footer的作用主要是让菜单比底部导航栏高
                BottomSheetPaddingFooter(footerPaddingSize)
            }

//                    Divider()
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetItem(
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    text: String,
    textColor:Color = Color.Unspecified,
    textDesc:String="",  //文字描述，小字
    textDescColor:Color = Color.Gray,  //文字描述颜色
    enabled:Boolean=true,
    onClick:()->Unit,
) {
    val scope = rememberCoroutineScope()


    val closeBottomSheet = { //scope: CoroutineScope, sheetState: SheetState, showBottomSheet: MutableState<Boolean> ->
        //隐藏bottomSheet，然后判断state如果是隐藏，把显示bottomsheet设为假
        scope.launch {
            //隐藏sheet
            sheetState.hide()
        }.invokeOnCompletion {
            //执行完上面的操作后，判断sheet状态，如果是隐藏，就把控制显示和隐藏的变量设为假
            if (!sheetState.isVisible) {
                showBottomSheet.value = false
            }
        }
    }

    val inDarkTheme = Theme.inDarkTheme


    Row(
        modifier = Modifier
            .fillMaxWidth(.5f)  //每个条目占屏幕宽度一半，两列，正好占整个屏幕宽度
            .clickable(
                enabled = enabled
            ) {
                //关闭菜单
                closeBottomSheet()
                //调用传入的函数
                onClick()
            }
            .height(50.dp)
            .padding(top = 2.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color=if(enabled) textColor else {if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable}
            )
            if(textDesc.isNotBlank()) {
                ScrollableRow {
                    Text(
                        text = "($textDesc)",
                        textAlign = TextAlign.Center,

                        fontSize = 12.sp,
                        color=if(enabled) textDescColor else {if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable}
                    )
                }
            }

        }
    }
}

@Composable
private fun BottomSheetTitle(title:String) {
    MySelectionContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 10.dp)
                .horizontalScroll(rememberScrollState())
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,

                fontSize = MyStyleKt.Title.firstLineFontSizeSmall,
                color = Color.Gray,

                )
        }
    }

    MyHorizontalDivider()
}

//这个Footer的作用主要是让菜单比底部导航栏高，不然有可能底部导航栏会盖住菜单，如果使用的是手势导航，这个东西可能会导致有点丑，不过我觉得可以接受
@Composable
private fun BottomSheetPaddingFooter(paddingSize:Int=30) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
    }
}

