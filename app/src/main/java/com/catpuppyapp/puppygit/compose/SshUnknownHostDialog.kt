package com.catpuppyapp.puppygit.compose

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.jni.SshAskUserUnknownHostRequest
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Lg2HomeUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable

private const val TAG = "SshUnknownHostDialog"

@Composable
fun SshUnknownHostDialog(
    currentSshAskUserUnknownHostRequest: CustomStateSaveable<SshAskUserUnknownHostRequest?>,
    iTrustTheHost: MutableState<Boolean>,
    closeSshDialog: () -> Unit,
    allowOrRejectSshDialogCallback: () -> Unit,
    appContext: Context
) {
    val item = currentSshAskUserUnknownHostRequest.value
    val spacerHeight = 10.dp
    ConfirmDialog2(
        title = stringResource(R.string.unknown_host),
        requireShowTextCompose = true,
        textCompose = {
            CopyScrollableColumn {
                Text(
                    stringResource(R.string.trying_connect_unknown_host_only_allow_if_trust) + "\n",
                    fontWeight = FontWeight.Bold,
                    color = MyStyleKt.TextColor.danger()
                )
                Spacer(Modifier.height(spacerHeight + 5.dp))

                Row {
                    Text("hostname: ")
                    Text((item?.sshCert?.hostname ?: "") + "\n", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(spacerHeight))

                val formattedSha256 = item?.sshCert?.formattedSha256()
                if (formattedSha256?.isNotBlank() == true) {
                    Row {
                        Text("sha256: ")
                        Text(formattedSha256 + "\n", fontWeight = FontWeight.Bold)

                    }
                    Spacer(Modifier.height(spacerHeight))
                }


                val formattedSha1 = item?.sshCert?.formattedSha1()
                if (formattedSha1?.isNotBlank() == true) {
                    Row {
                        Text("sha1: ")
                        Text(formattedSha1 + "\n", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(spacerHeight))
                }


                val formattedMd5 = item?.sshCert?.formattedMd5()
                if (formattedMd5?.isNotBlank() == true) {
                    Row {
                        Text("md5: ")
                        Text(formattedMd5 + "\n", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(spacerHeight))
                }

                if (item?.sshCert?.hostKey?.isNotBlank() == true) {
                    Row {
                        Text("host key: ")
                        Text(item.sshCert.hostKey + "\n", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(spacerHeight))
                }

                MyCheckBox(stringResource(R.string.i_trust_the_host), iTrustTheHost)

                if (iTrustTheHost.value) {
                    DefaultPaddingText(stringResource(R.string.operation_aborted_after_allowing_maybe_retry), color = MyStyleKt.TextColor.getHighlighting())
                }

                Spacer(Modifier.height(spacerHeight))

            }
        },
        okBtnEnabled = iTrustTheHost.value,
        okTextColor = if (iTrustTheHost.value) MyStyleKt.TextColor.danger() else Color.Unspecified,
        okBtnText = stringResource(R.string.allow),
        cancelBtnText = stringResource(R.string.reject),
        onCancel = {  // reject
            closeSshDialog()
            allowOrRejectSshDialogCallback()
        }
    ) {  // allow
        closeSshDialog()

        doJobThenOffLoading {
            try {
                if(item == null) {
                    Msg.requireShowLongDuration("err: ssh cert is `null`")
                    return@doJobThenOffLoading
                }

                // add to file
                Lg2HomeUtils.addItemToUserKnownHostsFile(item.sshCert)

                // show success msg
                Msg.requireShowLongDuration(appContext.getString(R.string.allowed_plz_re_try_clone_fetch_push))
            }catch (e:Exception){
                Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                MyLog.e(TAG, "allow host key(ssh cert) failed: ${e.stackTraceToString()}")
            }finally {
                allowOrRejectSshDialogCallback()
            }
        }
    }
}
