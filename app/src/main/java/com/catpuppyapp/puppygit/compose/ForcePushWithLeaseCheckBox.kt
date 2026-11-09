package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.StrCons
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun ForcePushWithLeaseCheckBox(
    forcePush_pushWithLease: MutableState<Boolean>,
    forcePush_expectedRefspecForLease: MutableState<String>,

) {

    // push with lease
    MyCheckBox(StrCons.withLease, forcePush_pushWithLease)


    if(forcePush_pushWithLease.value) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MyStyleKt.defaultHorizontalPadding),
            value = forcePush_expectedRefspecForLease.value,
            singleLine = true,
            onValueChange = {
                forcePush_expectedRefspecForLease.value = it
            },
            label = {
                Text(stringResource(R.string.expected_refspec))
            },
        )

        Spacer(Modifier.height(10.dp))

        SelectionRow {
            DefaultPaddingText(stringResource(R.string.push_force_with_lease_note))
        }
    }
}

