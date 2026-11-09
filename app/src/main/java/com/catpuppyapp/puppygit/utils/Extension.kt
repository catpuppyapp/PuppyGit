package com.catpuppyapp.puppygit.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.core.app.ShareCompat
import com.catpuppyapp.puppygit.utils.mime.MimeType
import com.catpuppyapp.puppygit.utils.mime.intentType
import kotlinx.coroutines.delay

fun <T : CharSequence> T.takeIfNotBlank(): T? = if (isNotBlank()) this else null

fun <T : CharSequence> T.takeIfNotEmpty(): T? = if (isNotEmpty()) this else null


// I am not sure, maybe this will cause a NPE? check the log file "API31_12_20250617_TextInputServiceAndroid NPE.logcat"
fun SoftwareKeyboardController.hideForAWhile(timeoutInMillSec: Long = 200L) {
    doJobThenOffLoading {
        // stop popup soft keyboard when open a file
        val hideKbJob = doJobThenOffLoading {
            runCatching {
                while (true) {
                    // too short may cause NPE
                    delay(30)
                    hide()
                }
            }
        }

        delay(timeoutInMillSec)

        hideKbJob?.cancel()
    }
}

//fun SoftwareKeyboardController.hideWhenTimeout(timeoutInMillSec: Long = 200L) {
//    doJobThenOffLoading {
//        runCatching {
//            delay(timeoutInMillSec)
//
//            hide()
//        }
//    }
//}

// used to indicate a copy is called by a cut action
fun String.appendCutSuffix() = "$this (CUT)"

fun String.countSub(sub: String) = this.split(sub).size - 1

// used to calculate indent
fun String.pairClosed(openSign: String, closeSign:String) = (this.countSub(openSign).let { open ->
    this.countSub(closeSign).let { close ->
        if(open == 0) {
            true
        }else if(close == 0) {
            false
        }else if(closeSign.contains(openSign)) {
            // e.g. in html, openSign is "<", closeSign is "</", then the closeSign included openSign
            // open 是 close 的2倍，代表关闭了
            open == close || open / close == 2
        }else {
            open == close
        }
    }
})


fun Collection<Uri>.createSendStreamIntent(context: Context, mimeTypes: Collection<MimeType>): Intent =
// Use ShareCompat.IntentBuilder for its migrateExtraStreamToClipData() because
// Intent.migrateExtraStreamToClipData() won't promote child ClipData and flags to the chooser
// intent, breaking third party share sheets.
// The context parameter here is only used for passing calling activity information and starting
    // chooser activity, neither of which we care about.
    ShareCompat.IntentBuilder(context)
        .setType(mimeTypes.intentType)
        .apply { forEach { addStream(it) } }
        .intent
        // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET is unnecessarily added by ShareCompat.IntentBuilder.
        .apply {
            @Suppress("DEPRECATION")
            removeFlagsCompat(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        }


fun Intent.removeFlagsCompat(flags: Int) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        removeFlags(flags)
    } else {
        setFlags(this.flags andInv flags)
    }
}


fun Intent.withChooser(title: CharSequence? = null, vararg initialIntents: Intent): Intent =
    Intent.createChooser(this, title).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
    }

fun Intent.withChooser(vararg initialIntents: Intent) = withChooser(null, *initialIntents)


fun Int.hasBits(bits: Int): Boolean = this and bits == bits

infix fun Int.andInv(other: Int): Int = this and other.inv()

