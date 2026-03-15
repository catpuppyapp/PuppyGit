package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshBox(
    contentPadding: PaddingValues,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }

    // 包装触发动作
    val wrappedOnRefresh = {
        doJobThenOffLoading {
            isRefreshing.value = true
            delay(500)
            isRefreshing.value = false
        }
        onRefresh()
    }

    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = wrappedOnRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                isRefreshing = isRefreshing.value,
                state = state,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(contentPadding) 
            )
        }
    ) {
        content()
    }
}
