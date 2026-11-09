package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.delay


// more menu icon always pinned, not in this count
// pinned icon can't scrollable, always at right of BottomBar
private const val pinnedIconsCount = 1

@Composable
fun BottomBar(
    modifier: Modifier=Modifier,
    showClose: Boolean = true,
    showSelectedCount: Boolean = true,
    height: Dp = MyStyleKt.BottomBar.height,
    color: Color = MaterialTheme.colorScheme.primaryContainer,

    quitSelectionMode: () -> Unit,

    // icon
    iconList:List<ImageVector>,
    iconTextList:List<String>,
    iconDescTextList:List<String>,
    iconOnClickList:List<()->Unit>,
    iconEnableList:List<()->Boolean>,
    iconVisibleList:List<()->Boolean> = listOf(),  //这个默认就用空list()让所有条目都可以显示就很好，不要把默认行为调整成启用则显示禁用则隐藏，因为这个按钮本来就只显示4、5个，用户可能会习惯按某个位置对应某个功能，若默认隐藏禁用按钮，可能会按错

    // count number
    getSelectedFilesCount:()->Int,
    countNumOnClickEnabled:Boolean=false,
    countNumOnClick:()->Unit={},


    // more menu
    moreItemTextList:List<String>,
    visibleMoreIcon:Boolean = moreItemTextList.isNotEmpty(),
    enableMoreIcon:Boolean = visibleMoreIcon && getSelectedFilesCount() > 0,
    moreItemOnClickList:List<()->Unit>,
    moreItemEnableList:List<()->Boolean>,
    moreItemVisibleList:List<()->Boolean> = moreItemEnableList,  //这个菜单条目默认启用即显示，禁用即隐藏，这样感觉比较节省心智，因为有可能有很多菜单条目而且这个菜单用户一般是先看一下才会按，就算菜单条目变化一般也不会按错
    reverseMoreItemList:Boolean = false,


) {
    val scope = rememberCoroutineScope()

    val dropDownMenuExpandState = rememberSaveable { mutableStateOf(false) }
    val showDropDownMenu = {
        dropDownMenuExpandState.value=true
    }
    val closeDropDownMenu = {
        dropDownMenuExpandState.value=false
    }
    val switchDropDownMenu = {
        dropDownMenuExpandState.value = !dropDownMenuExpandState.value
    }

    //开始：反转more菜单条目，如果设置了反转的话
    var moreItemTextList = moreItemTextList
    var moreItemOnClickList = moreItemOnClickList
    var moreItemEnableList = moreItemEnableList
    var moreItemVisibleList = moreItemVisibleList

    if(enableMoreIcon && reverseMoreItemList) {
        moreItemTextList = moreItemTextList.asReversed()
        moreItemOnClickList = moreItemOnClickList.asReversed()
        moreItemEnableList = moreItemEnableList.asReversed()
        moreItemVisibleList = moreItemVisibleList.asReversed()
    }
    //结束：反转more菜单条目，如果设置了反转的话

    val isIconHidden = { idx:Int ->
        iconVisibleList.isNotEmpty() && !iconVisibleList[idx]()
    }

    // 获取最右边的可见条目，固定显示
    val pinnedIconList = mutableListOf<Int>()
    if(pinnedIconsCount > 0 && iconTextList.isNotEmpty()) {
        for(idx in IntRange(0, iconTextList.size-1).reversed()) {
            if(isIconHidden(idx)) {
                continue
            }

            // index are reversed order, so prepend to head of list at here
            pinnedIconList.add(0, idx)
            if(pinnedIconList.size >= pinnedIconsCount) {
                break
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            // no hardware keyboard, can't test it,
            //   and I am not sure, if bottom bar lost focus, it can response the key event? if it can't, no need add it.
//            .onPreviewKeyEvent opke@{ keyEvent ->
//                if (keyEvent.type != KeyEventType.KeyDown) {
//                    return@opke false
//                }
//
//                if (keyEvent.key == Key.Escape
//                    && !keyEvent.isCtrlPressed
//                    && !keyEvent.isShiftPressed
//                    && !keyEvent.isAltPressed
//                    && !keyEvent.isMetaPressed
//                ) {
//                    quitSelectionMode()
//                    return@opke true
//                }
//
//                return@opke false
//            }
            .then(modifier)
        ,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                //禁用对BottomBar本身的点击，不然会穿透而点击到被BottomBar覆盖的条目，
                // 虽然禁用了对BottomBar本身的点击，不过Bar上按钮依然可以点击
                .clickable(enabled = false) {}
                .background(color)
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 5.dp)

            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if(showClose) {
                    //关闭按钮
                    LongPressAbleIconBtn(
//                        modifier = Modifier
//                            .padding(10.dp)
//                            .size(40.dp),
                        tooltipText = stringResource(R.string.close),
                        icon = Icons.Filled.Close,
                        iconContentDesc = stringResource(R.string.close),
                        onClick = {
                            //退出选择模式
                            quitSelectionMode()
                        },
                    )
                }

                if(showSelectedCount) {
                    //不显示左边的关闭按钮的话，需要加点宽度，不然太靠近屏幕边缘
                    if(showClose.not()) {
                        Spacer(Modifier.width(10.dp))
                    }

                    //选择的条目数
                    Text(
                        text = ""+getSelectedFilesCount(),
                        modifier = MyStyleKt.ClickableText.modifier
                            .clickable(enabled = countNumOnClickEnabled) {
                                countNumOnClick()
                            }
                            .padding(horizontal = 10.dp)
                    )
                }

            }

            Box {
                // size of an icon
                val baseIconSize = MyStyleKt.defaultLongPressAbleIconBtnPressedCircleSize

                val offsetForPinnedAndMenuIcons = baseIconSize.value.let {
                    pinnedIconList.size * it + (if(visibleMoreIcon) it else 0F)
                }.coerceAtLeast(0f).dp

                val scrollableIconListState = rememberScrollState()

                Row (
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = -offsetForPinnedAndMenuIcons)
                        .padding(start = offsetForPinnedAndMenuIcons)
                        .horizontalScroll(scrollableIconListState)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    for((idx, text) in iconTextList.withIndex()) {
                        // pinned icons will show after scrollable icons
                        if(pinnedIconList.contains(idx)) {
                            break
                        }

                        //显示逻辑：显示列表为空，一律显示，不为空，取出对应索引的值，为true则显示
                        //实现代码：如果显示列表不为空 且 显示对应条目为假，continue，不显示条目
                        if(isIconHidden(idx)) {
                            continue
                        }

                        LongPressAbleIconBtn(
                            enabled = iconEnableList[idx](),
    //                        enabled = selectedFilePathNameMapList.value.isNotEmpty(),
                            tooltipText = text,
                            icon = iconList[idx],
                            iconContentDesc = iconDescTextList[idx],
                            onClick = {
                               iconOnClickList[idx]()
                            }
                        )
                    }
                }

                LaunchedEffect(Unit) {
                    doJobThenOffLoading {
                        delay(200)
                        // trying scroll to the most right of the list
                        //  200 is a guessed number, any number is ok,
                        //  just make sure it big enough to reach the end of the list
                        UIHelper.scrollTo(scope, scrollableIconListState, 200 * iconTextList.size)
                    }
                }

                val offsetForMenuIcon = if(visibleMoreIcon) baseIconSize else 0.dp

                // pinned icons
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = -offsetForMenuIcon)
                        .padding(start = offsetForMenuIcon)

                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (idx in pinnedIconList) {
                        //显示逻辑：显示列表为空，一律显示，不为空，取出对应索引的值，为true则显示
                        //实现代码：如果显示列表不为空 且 显示对应条目为假，continue，不显示条目
                        if(isIconHidden(idx)) {
                            continue
                        }

                        LongPressAbleIconBtn(
                            enabled = iconEnableList[idx](),
                            tooltipText = iconTextList[idx],
                            icon = iconList[idx],
                            iconContentDesc = iconDescTextList[idx],
                            onClick = {
                                iconOnClickList[idx]()
                            }
                        )
                    }
                }


                // more menu icon (3dots)
                if (visibleMoreIcon) {
                    //菜单得单独开一行，不然DropdownMenu就定位到外部菜单的最左边了，就偏离菜单图标了，单独开一行就可以定位到菜单图标那里，完美
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        //菜单图标
                        LongPressAbleIconBtn(
                            enabled = enableMoreIcon,
                            tooltipText = stringResource(R.string.menu),
                            icon = Icons.Filled.MoreVert,
                            iconContentDesc = stringResource(R.string.menu),
                            onClick = {
                                switchDropDownMenu()
                            }
                        )
                        //菜单列表
                        DropdownMenu(
//                            offset = DpOffset(x=(-5).dp, y=0.dp),

                            expanded = dropDownMenuExpandState.value,
                            onDismissRequest = { closeDropDownMenu() }
                        ) {
                            var idxOffset = 0;
                            var showDivider = false

                            for ((idx, text) in moreItemTextList.withIndex()) {
                                if(text == UIHelper.bottomBarDividerPlaceHolder) {
                                    showDivider = true
                                    idxOffset--
                                    continue
                                }

                                val willShowDivider = showDivider
                                showDivider = false

                                val idx = idx + idxOffset

                                if(moreItemVisibleList.isNotEmpty() && !moreItemVisibleList[idx]()) {
                                    continue
                                }

                                // ignore blank item
                                if(text.isBlank()) {
                                    continue
                                }

                                if(willShowDivider) {
                                    MyHorizontalDivider()
                                }

                                DropdownMenuItem(
                                    enabled = moreItemEnableList[idx](),
                                    text = { Text(text) },
                                    onClick = {
                                        moreItemOnClickList[idx]()
                                        closeDropDownMenu()
                                    }
                                )

                            }
                        }
                    }

                }

            }


        }

    }
}
