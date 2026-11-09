package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import com.catpuppyapp.puppygit.style.MyStyleKt

@Composable
fun MyLazyVerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState,
    contentPadding: PaddingValues,
    itemMinWidth:Dp,
    content: LazyStaggeredGridScope.() -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    LazyVerticalStaggeredGrid (
        modifier = modifier,
        contentPadding = PaddingValues(
            start = contentPadding.calculateLeftPadding(layoutDirection),
            end = contentPadding.calculateRightPadding(layoutDirection),
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + MyStyleKt.BottomBar.outsideContentPadding
        ),
        columns = StaggeredGridCells.Adaptive(minSize = itemMinWidth),
        state = state,
    ) {
        content()
    }
}
