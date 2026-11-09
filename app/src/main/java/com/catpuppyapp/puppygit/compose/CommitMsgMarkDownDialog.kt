package com.catpuppyapp.puppygit.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.Msg

@Composable
fun CommitMsgMarkDownDialog(
    dialogVisibleState: MutableState<Boolean>,
    text: String,
    previewModeOn: MutableState<Boolean>,
    useSystemFonts: MutableState<Boolean>,
    basePathNoEndSlash: String,
) {
    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    MarkDownDialog(
        text = text,
        previewModeOn = previewModeOn,
        useSystemFonts = useSystemFonts,
        basePathNoEndSlash = basePathNoEndSlash,
        close = {
            dialogVisibleState.value = false

            // update settings if need
            val previewModeOn = previewModeOn.value
            val useSystemFonts = useSystemFonts.value
            if(previewModeOn != SettingsUtil.isCommitMsgPreviewModeOn()
                || useSystemFonts != SettingsUtil.isCommitMsgUseSystemFonts()
            ) {
                SettingsUtil.update {
                    it.commitMsgPreviewModeOn = previewModeOn
                    it.commitMsgUseSystemFonts = useSystemFonts
                }
            }
        },
        copy = {
            clipboardManager.setText(AnnotatedString(text))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    )
}
