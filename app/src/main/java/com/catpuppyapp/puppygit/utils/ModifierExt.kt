package com.catpuppyapp.puppygit.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt


@Composable
fun Modifier.fabBasePadding(
    density: Density = LocalDensity.current,
    direction: LayoutDirection=LocalLayoutDirection.current,
): Modifier {
    return padding(
        UIHelper.getNaviBarsPadding(density, direction).let {
            PaddingValues(
                //底部不用加，因为脚手架替你加上了，也可能是fab组件自己加的，我不知道，反正加上了
                bottom = 0.dp,
                top = it.calculateTopPadding(),
                start = it.calculateLeftPadding(direction),
                end = it.calculateRightPadding(direction),
            )
        }
    )
}


@Composable
fun Modifier.addTopPaddingIfIsFirstLine(index:Int, topPadding:Dp = MyStyleKt.Padding.firstLineTopPaddingValuesInDp):Modifier {
    return if(index == 0) padding(top = topPadding) else this
}

private fun Modifier.basePageModifier(contentPadding: PaddingValues):Modifier {
    //这个顺序不能换，fillMaxSize必须在padding和滚动前面，不然居中会作废
    return fillMaxSize()
        .padding(contentPadding)
}

fun Modifier.baseVerticalScrollablePageModifier(contentPadding: PaddingValues, scrollState: ScrollState):Modifier {
    return basePageModifier(contentPadding).verticalScroll(scrollState)
}

@Composable
fun Modifier.dropDownItemContainerColor(selected:Boolean):Modifier {
    return if(selected) {
        background(MyStyleKt.DropDownMenu.selectedItemContainerColor())
    } else {
        this
    }
}

fun Modifier.listItemPadding() = padding(MyStyleKt.defaultItemPadding)
