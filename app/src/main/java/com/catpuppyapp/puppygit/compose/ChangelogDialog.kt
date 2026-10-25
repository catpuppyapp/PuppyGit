package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.content.homescreen.innerpage.donateLink
import com.catpuppyapp.puppygit.utils.ActivityUtil
import com.catpuppyapp.puppygit.utils.AppModel

private const val hahaNoteLink = "https://github.com/catpuppyapp/HahaNote-Release"

private val changelog = """
- update tls cert
- 更新tls证书
""".trimIndent()


private const val donateText = "❤ Donate to support develop! ❤"


@Composable
fun ChangelogDialog(
    onClose: () -> Unit,
) {
    val activityContext = LocalContext.current

    CopyableDialog2(
        // hide ok btn
        okCompose = {},
        onOk = {},


        cancelBtnText = stringResource(R.string.ok),
        onCancel = onClose,

        title = AppModel.getAppVersionNameAndCode(),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                MultiLineClickableText(donateText) {
                    ActivityUtil.openUrl(activityContext, donateLink)
                }

                Spacer(Modifier.height(15.dp))

                MultiLineClickableText("Try my new app: HahaNote, a cross-platform note-taking app, supports e2ee sync") {
                    ActivityUtil.openUrl(activityContext, hahaNoteLink)
                }

                Spacer(Modifier.height(15.dp))

                Text(changelog)
            }
        }
    )
}
