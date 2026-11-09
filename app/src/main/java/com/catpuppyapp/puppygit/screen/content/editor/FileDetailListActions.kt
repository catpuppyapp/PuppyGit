package com.catpuppyapp.puppygit.screen.content.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.play.pro.R

@Composable
fun FileDetailListActions(
    request: MutableState<String>,
    filterModeOn: Boolean,
    initFilterMode: ()->Unit,
) {
    if(filterModeOn) {
        return
    }

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.filter),
        icon = Icons.Filled.FilterAlt,
    ) {
        initFilterMode()
    }
    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
    ) {
        request.value = PageRequest.reloadRecentFileList
    }
}
