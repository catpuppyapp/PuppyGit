package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ComposeHelper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier

private const val TAG = "SubscriptionPage"

@Composable
fun SubscriptionPage(contentPadding: PaddingValues, needRefresh: MutableState<String>, openDrawer: ()->Unit){

    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp;

    //这个页面暂时没用了，如果有用的话，也不要自己获取图标，改用AppIcon组件
//    val appIcon = AppModel.getAppIcon(activityContext)

    val clipboardManager = LocalClipboardManager.current

    val copy={text:String ->
        clipboardManager.setText(AnnotatedString(text))
        Msg.requireShow(activityContext.getString(R.string.copied))
    }

    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer=openDrawer, exitApp= exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end



    Column (
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, rememberScrollState())
            .padding(MyStyleKt.defaultItemPadding)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
//        //图标，app名，contact
//        Image(bitmap = appIcon, contentDescription = stringResource(id = R.string.app_icon))
//        if(Billing.productDetailsList==null || Billing.productDetailsList?.value?.isEmpty() == true) {
//            Row(modifier = Modifier.padding(10.dp)) {
//                Text(text = stringResource(R.string.err_query_product_details_failed), color = MyStyleKt.TextColor.error)
//            }
//        }else {
//            Billing.productDetailsList?.value?.toList()?.forEach { productDetails ->
//                val product = Billing.availableProductMap.get(productDetails.productId)
//                val price = Billing.getReplacedProductCurrencySymbol(productDetails)
//                if(product!=null) {  //查询，有此商品，则展示出来供用户购买
//                    Row(modifier = Modifier.padding(10.dp)) {  //商品名
//                        Text(text = product.name, fontWeight = FontWeight.ExtraBold)
//                    }
//                    Row(modifier = Modifier.padding(10.dp)) {  //价格
//                        Text(text = price+"/"+AppModel.getStringByResKey(appContext, product.purchasePeriodResKey))
//                    }
//                    Row(modifier = Modifier.padding(10.dp)) {  //卖点
//                        Text(text = AppModel.getStringByResKey(appContext, product.benefitsResKey), textAlign = TextAlign.Center)
//                    }
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {  //购买按钮
//                        Button(
//                            enabled = !UserUtil.isPro(),
////                            colors = ButtonDefaults.elevatedButtonColors().copy(contentColor = MaterialTheme.colorScheme.primary),
//                            onClick = buyButtonOnClick@{  // start a buy
//                                val caller="buyButtonOnClick@"
//                                Billing.doBuyIfReady(appContext, productDetails) { e:Exception ->
//                                    // exceptionHandler
//                                    Msg.requireShowLongDuration("buy err: "+e.localizedMessage)
//                                    MyLog.e(TAG, "#$caller: err: ${e.stackTraceToString()}")
//                                }
//                            }
//                        ) {
//                            Text(
//                                text = if(UserUtil.isPro()) stringResource(R.string.you_already_is_pro) else stringResource(R.string.buy_now),
////                                style = MyStyleKt.ClickableText.style,
////                                color = MyStyleKt.ClickableText.color,
////                    modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
////
////                    },
//
//                            )
//                        }
//
//                    }
//
//                }
//            }
//        }
//
//        if(!isReleaseMode()) {
//            Spacer(modifier = Modifier.height(30.dp))
//            MyHorizontalDivider()
//            Spacer(modifier = Modifier.height(5.dp))
//
//            Row {
//                Text(text = "Test Zone", fontWeight = FontWeight.ExtraBold)
//            }
//            Spacer(modifier = Modifier.height(15.dp))
//
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "isPro = ${UserUtil.isPro()}")
//            }
//
////            测试下点击一个按钮，修改状态，页面能否立即展示出来。期望：能。测试结果：能，看来这个状态可按预期工作。 另外，测试的时候切换一下很方便，所以就不删除了
//            Button(onClick = { UserUtil.updateUser(needUpdateUserByLock = false) {
//                it.isProState.value = !UserUtil.isPro()
//            }}) {
//                Text(text = "switch Pro state")
//            }
//        }
    }

    LaunchedEffect(needRefresh.value) {
        val funName = "LaunchedEffect"
        try {
//            doJobThenOffLoading {
//                Billing.queryInAppAndSubsPurchasesAsync()
//            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
        }
    }
}
