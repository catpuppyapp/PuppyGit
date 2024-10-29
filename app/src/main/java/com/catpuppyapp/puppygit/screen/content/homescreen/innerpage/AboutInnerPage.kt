package com.catpuppyapp.puppygit.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.compose.PaddingRow
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.ActivityUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ComposeHelper
import com.catpuppyapp.puppygit.utils.Msg


const val authorMail = "luckyclover33xx@gmail.com"
const val authorMailLink = "mailto:$authorMail"

const val madeBy = "Made by Bandeapart1964 of catpuppyapp"
const val madeByLink = "https://github.com/Bandeapart1964"

const val sourceCodeLink = "https://github.com/catpuppyapp/PuppyGit"
const val privacyPolicyLink = "$sourceCodeLink/blob/main/PrivacyPolicy.md"
const val discussionLink = "$sourceCodeLink/discussions"
const val reportBugsLink = "$sourceCodeLink/issues/new"
const val donateLink = "$sourceCodeLink/blob/main/donate.md"


var versionCode: Int = AppModel.getAppVersionCode()
var versionName: String = AppModel.getAppVersionName()

data class OpenSource(
    val projectName:String,
    val projectLink:String,
    val licenseLink:String,
)

private val openSourceList= listOf(
    OpenSource(projectName = "Libgit2", projectLink = "https://github.com/libgit2/libgit2", licenseLink = "https://raw.githubusercontent.com/libgit2/libgit2/main/COPYING"),
    OpenSource(projectName = "Git24j", projectLink = "https://github.com/git24j/git24j", licenseLink = "https://raw.githubusercontent.com/git24j/git24j/master/LICENSE"),
    OpenSource(projectName = "text-editor-compose", projectLink = "https://github.com/kaleidot725/text-editor-compose", licenseLink = "https://raw.githubusercontent.com/kaleidot725/text-editor-compose/main/LICENSE"),
    OpenSource(projectName = "OpenSSL", projectLink = "https://github.com/openssl/openssl", licenseLink = "https://raw.githubusercontent.com/openssl/openssl/master/LICENSE.txt"),
)

@Composable
fun AboutInnerPage(contentPadding: PaddingValues,
                   openDrawer:() -> Unit,
){

    val appContext = LocalContext.current
    val exitApp = AppModel.singleInstanceHolder.exitApp;

    val appIcon = AppModel.getAppIcon(appContext)

    val clipboardManager = LocalClipboardManager.current

    val copy={text:String ->
        clipboardManager.setText(AnnotatedString(text))
        Msg.requireShow(appContext.getString(R.string.copied))
    }

    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(appContext = appContext, openDrawer = openDrawer, exitApp= exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end


    Column(
        modifier = Modifier
            .padding(contentPadding)
            .padding(top = 10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ){
        //图标，app名，contact
        Image(bitmap = appIcon, contentDescription = stringResource(id = R.string.app_icon))
        Column(modifier = Modifier.padding(10.dp)

            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.ExtraBold)
            Text(text ="$versionName ($versionCode)", fontSize = 12.sp)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    ActivityUtil.openUrl(appContext, madeByLink)
                }
            ) {
                Text(
                    text = madeBy,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
//
//        Row(
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TextButton(
//                onClick = {
//                    ActivityUtil.openUrl(appContext, donateLink)
//                }
//            ) {
//                Text(
//                    text = stringResource(R.string.donate),
//                    fontStyle = FontStyle.Italic,
//                )
//            }
//        }

//        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.source_code),
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                    ActivityUtil.openUrl(appContext, sourceCodeLink)
                },

            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.discussions),
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                    //                    copy(authorMail)
                    ActivityUtil.openUrl(appContext, discussionLink)
                },

                )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.report_bugs),
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                    ActivityUtil.openUrl(appContext, reportBugsLink)
                },

            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Text(text = stringResource(R.string.contact_author)+":")
            Text(
                text = stringResource(R.string.contact_author),
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
//                    copy(authorMail)
                    ActivityUtil.openUrl(appContext, authorMailLink)
                },

            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Text(text = stringResource(R.string.contact_author)+":")
            Text(
                text = stringResource(R.string.donate),
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
//                    copy(authorMail)
                    ActivityUtil.openUrl(appContext, donateLink)
                },

            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.privacy_policy),
                style = MyStyleKt.ClickableText.style,
                color = MyStyleKt.ClickableText.color,
                modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
//                    copy(authorMail)
                    ActivityUtil.openUrl(appContext, privacyPolicyLink)
                },

                )
        }
        HorizontalDivider(modifier = Modifier.padding(10.dp))
        //开源项目列表
        Row (modifier = Modifier.padding(10.dp)){
            Text(text = stringResource(id = R.string.powered_by_open_source)+":")
        }
        openSourceList.forEach {
            Column (
                modifier = Modifier.padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text(
                    text = it.projectName,
//                    fontSize = 14.sp,
                    style = MyStyleKt.ClickableText.style,
                    color = MyStyleKt.ClickableText.color,
                    modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                        //                        copy(it.projectLink)
                        ActivityUtil.openUrl(appContext, it.projectLink)
                    },
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "("+stringResource(R.string.license)+")",
                    fontSize = 12.sp,
                    style = MyStyleKt.ClickableText.style,
                    color = MyStyleKt.ClickableText.color,
                    modifier = MyStyleKt.ClickableText.modifierNoPadding.clickable {
                        //                        copy(it.projectLink)
                        ActivityUtil.openUrl(appContext, it.licenseLink)
                    },
                )

                Spacer(Modifier.height(10.dp))

            }
        }

        PaddingRow(PaddingValues(30.dp))
    }

}
