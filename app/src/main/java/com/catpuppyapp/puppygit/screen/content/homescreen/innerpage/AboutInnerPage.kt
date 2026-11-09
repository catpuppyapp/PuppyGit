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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.AppIcon
import com.catpuppyapp.puppygit.compose.AppIconMonoChrome
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.compose.ShowThumbnailForImage
import com.catpuppyapp.puppygit.compose.SpacerRow
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.ActivityUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.ComposeHelper
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.baseVerticalScrollablePageModifier
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter


const val authorMail = "luckyclover33xx@gmail.com"
const val authorMailLink = "mailto:$authorMail"

const val madeBy = "Made by Bandeapart1964 of catpuppyapp"
const val madeByLink = "https://github.com/Bandeapart1964"

const val sourceCodeLink = "https://github.com/catpuppyapp/PuppyGit"
const val privacyPolicyLink = "$sourceCodeLink/blob/main/PrivacyPolicy.md"
const val discussionLink = "$sourceCodeLink/discussions"
const val appReleasesLink = "$sourceCodeLink/releases"
const val reportBugsLink = "$sourceCodeLink/issues/new"
const val faqLink = "$sourceCodeLink/blob/main/FAQ.md"
const val httpServiceApiUrl = "$sourceCodeLink/blob/main/http_service_api.md"
const val automationDocUrl = "$sourceCodeLink/blob/main/automation_doc.md"

const val donateLink = "https://github.com/catpuppyapp/PuppyGit/blob/main/donate.md"


private data class OpenSource(
    val projectName:String,
    val projectLink:String,
    val licenseLink:String,
)

private data class Contributor(
    val name:String,
    val link:String,
    val desc:String,
)

private data class Link(
    val title: String,
    val link: String,
)

private data class App(
    val name: String,
    val desc: String,
    val link: String,
    val iconLink: String,
)

private val myOtherAppList = listOf(
    App(
        name = "HahaNote",
        desc = "HahaNote is an e2ee note sync app",
        link = "https://github.com/catpuppyapp/HahaNote-Release",
        iconLink = "https://raw.githubusercontent.com/catpuppyapp/HahaNote-Release/main/icon.png",
    ),
    App(
        name = "SshKeyMan",
        desc = "SshKeyMan is an ed25519/rsa key generator",
        link = "https://github.com/catpuppyapp/SshKeyMan",
        iconLink = "https://raw.githubusercontent.com/catpuppyapp/SshKeyMan/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png",
    ),

)

private val openSourceList = listOf(
    OpenSource(projectName = "libgit2", projectLink = "https://github.com/libgit2/libgit2", licenseLink = "https://raw.githubusercontent.com/libgit2/libgit2/main/COPYING"),
    OpenSource(projectName = "git24j", projectLink = "https://github.com/git24j/git24j", licenseLink = "https://raw.githubusercontent.com/git24j/git24j/master/LICENSE"),
    OpenSource(projectName = "text-editor-compose", projectLink = "https://github.com/kaleidot725/text-editor-compose", licenseLink = "https://raw.githubusercontent.com/kaleidot725/text-editor-compose/main/LICENSE"),
    OpenSource(projectName = "OpenSSL", projectLink = "https://github.com/openssl/openssl", licenseLink = "https://raw.githubusercontent.com/openssl/openssl/master/LICENSE.txt"),
    OpenSource(projectName = "libssh2", projectLink = "https://github.com/libssh2/libssh2", licenseLink = "https://raw.githubusercontent.com/libssh2/libssh2/refs/heads/master/COPYING"),
    OpenSource(projectName = "compose-markdown", projectLink = "https://github.com/jeziellago/compose-markdown", licenseLink = "https://github.com/jeziellago/compose-markdown/blob/main/LICENSE"),
    OpenSource(projectName = "swipe", projectLink = "https://github.com/saket/swipe", licenseLink = "https://github.com/saket/swipe/blob/trunk/LICENSE.txt"),
    OpenSource(projectName = "sora-editor", projectLink = "https://github.com/Rosemoe/sora-editor", licenseLink = "https://github.com/Rosemoe/sora-editor/blob/main/LICENSE"),
)


private val contributorList = listOf(
    Contributor(name = "triksterr", link = "https://github.com/triksterr", desc = "Russian translator"),
    Contributor(name = "mikropsoft", link = "https://github.com/mikropsoft", desc = "Turkish translator"),
    Contributor(name = "Hussain96o", link = "https://github.com/Hussain96o", desc = "Arabic translator"),
    Contributor(name = "sebastien46", link = "https://github.com/sebastien46", desc = "Monochrome app icon"),
    Contributor(name = "kamilhussen24", link = "https://github.com/kamilhussen24", desc = "Bangla translator"),
    Contributor(name = "OutlinedArc217", link = "https://github.com/OutlinedArc217", desc = "UI improvement"),
    Contributor(name = "RokeJulianLockhart", link = "https://github.com/RokeJulianLockhart", desc = "UI improvement"),
    Contributor(name = "jiesou", link = "https://github.com/jiesou", desc = "Fix automation service crash"),
    Contributor(name = "clach04", link = "https://github.com/clach04", desc = "Optimize english translation"),
    Contributor(name = "IzzySoft", link = "https://github.com/IzzySoft", desc = "Build workflow improvement"),
    Contributor(name = "msmt2018", link = "https://github.com/msmt2018", desc = "Contribute code"),
)


@Composable
fun AboutInnerPage(
    listState:ScrollState,
    contentPadding: PaddingValues,
    openDrawer:() -> Unit,
){

    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp

    val donateLink = Link(title = "💖 "+stringResource(R.string.donate)+" 💖", link = donateLink)

    val links = listOf(
        Link(title = stringResource(R.string.update), link = appReleasesLink),
        Link(title = stringResource(R.string.source_code), link = sourceCodeLink),
        Link(title = stringResource(R.string.discussions), link = discussionLink),
        Link(title = stringResource(R.string.report_bugs), link = reportBugsLink),
        Link(title = stringResource(R.string.contact_author), link = authorMailLink),
        Link(title = stringResource(R.string.faq), link = faqLink),
        Link(title = stringResource(R.string.privacy_policy), link = privacyPolicyLink),
    )


    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp= exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end

    val appLogoEasterEggOn = rememberSaveable { mutableStateOf(false) }
    val appLogoEasterEggIconColor = remember { mutableStateOf(Color.Magenta) }

    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, listState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // App Icon etc
        CardContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Icon with Easter Egg
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (appLogoEasterEggOn.value) {
                                appLogoEasterEggIconColor.value = UIHelper.randomRainbowColor()
                            } else {
                                appLogoEasterEggOn.value = true
                            }
                        },
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    if(appLogoEasterEggOn.value) {
                        AppIconMonoChrome(tint = appLogoEasterEggIconColor.value)
                    } else {
                        AppIcon()
                    }
                }

                Spacer(Modifier.height(24.dp))

                // App Name and Version
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = AppModel.getAppVersionNameAndCode(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Made By
                TextButton(
                    onClick = {
                        ActivityUtil.openUrl(activityContext, madeByLink)
                    }
                ) {
                    Text(
                        text = madeBy,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                    )
                }

                // donate link
                TextButton(
                    onClick = {
                        ActivityUtil.openUrl(activityContext, donateLink.link)
                    }
                ) {
                    Text(
                        text = donateLink.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = MyStyleKt.TextSize.medium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        SectionSpacer()

        SectionCard(
            title = "My Other Apps",
            icon = Icons.Outlined.Apps
        ) {
            val lastIndex = myOtherAppList.size - 1
            myOtherAppList.forEachIndexedBetter { index, it ->
                CardItem(
                    context = activityContext,
                    line1 = it.name,
                    line2 = it.desc,
                    isLast = index == lastIndex,
                    headingIconLink = it.iconLink,
                    onClick = { ActivityUtil.openUrl(activityContext, it.link) }
                )
            }
        }

        SectionSpacer()

        // Links Section
        SectionCard(
            title = stringResource(R.string.url_links),
            icon = Icons.Outlined.Link
        ) {
            val lastIndex = links.size - 1
            links.forEachIndexedBetter { index, it ->
                CardItem(
                    context = activityContext,
                    line1 = it.title,
                    line2 = "",
                    isLast = index == lastIndex,
                    onClick = { ActivityUtil.openUrl(activityContext, it.link) }
                )
            }
        }

        SectionSpacer()

        // Open Source Section
        SectionCard(
            title = stringResource(R.string.powered_by_open_source),
            icon = Icons.Outlined.Code
        ) {
            val lastIndex = openSourceList.size - 1
            openSourceList.forEachIndexedBetter { index, openSource ->
                CardItem(
                    context = activityContext,
                    line1 = openSource.projectName,
                    line2 = stringResource(R.string.license),
                    isLast = index == lastIndex,
                    line2OnClick = { ActivityUtil.openUrl(activityContext, openSource.licenseLink) },
                    onClick = { ActivityUtil.openUrl(activityContext, openSource.projectLink) },
                )
            }
        }

        SectionSpacer()

        // Contributors Section
        SectionCard(
            title = stringResource(R.string.contributors),
            icon = Icons.Outlined.Favorite
        ) {
            val lastIndex = contributorList.size - 1
            contributorList.forEachIndexedBetter { index, contributor ->
                CardItem(
                    context = activityContext,
                    line1 = contributor.name,
                    line2 = contributor.desc,
                    isLast = index == lastIndex,
                    onClick = { ActivityUtil.openUrl(activityContext, contributor.link) }
                )
            }
        }

        SpacerRow()
    }
}


@Composable
private fun CardContainer(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        content()
    }
}


@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            ScrollableRow(
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MyStyleKt.TextSize.medium
                )
            }

            content()
        }
    }
}

@Composable
private fun CardItem(
    context: Context,
    line1: String,
    line2: String,
    isLast: Boolean,
    headingIconLink: String = "",
    line2OnClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(headingIconLink.isNotBlank()) {
            ShowThumbnailForImage(
                context = context,
                filePath = headingIconLink,
                contentDescription = null,
                fallbackIcon = Icons.Outlined.Android,
                modifier = Modifier.padding(end = 10.dp),
            )
        }

        Column(
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            ScrollableRow {
                Text(
                    text = line1,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if(line2.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))

                ScrollableRow {
                    Text(
                        text = line2,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = if(line2OnClick == null) Modifier
                            else MyStyleKt.ClickableText.modifierNoPadding.clickable { line2OnClick() }
                    )
                }
            }
        }
    }

    if (!isLast) {
        MyHorizontalDivider()
    }
}

@Composable
private fun SectionSpacer() {
    Spacer(Modifier.height(24.dp))
}
