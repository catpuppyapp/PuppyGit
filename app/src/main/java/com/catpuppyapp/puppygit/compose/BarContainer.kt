package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.dto.MenuIconBtnItem
import com.catpuppyapp.puppygit.dto.MenuTextItem
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper

@Composable
fun BarContainer(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    actions:List<MenuIconBtnItem>? = null,
    showMoreIcon: Boolean = false,
    moreMenuExpandState: MutableState<Boolean>? = null,
    moreMenuItems:List<MenuTextItem>? = null,
    moreMenuIconBtnItem: MenuIconBtnItem? = if(showMoreIcon) {
        MenuIconBtnItem(
            icon = Icons.Filled.MoreVert,
            text = stringResource(R.string.menu),
            onClick = {
                if(moreMenuExpandState != null) {
                    moreMenuExpandState.value = !moreMenuExpandState.value
                }
            }
        )
    }else null,
    onClick: (()->Unit)? = null,
    content: @Composable ()->Unit,
) {
    Row(
        modifier = modifier
            .border(BorderStroke(2.dp, UIHelper.getDividerColor()))
            .then(
                if(onClick != null) {
                    Modifier.clickable { onClick() }
                }else {
                    Modifier
                }
            )
            .background(MaterialTheme.colorScheme.surfaceDim)
            .fillMaxWidth()
            .padding(MyStyleKt.defaultItemPadding)
        ,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        content()

        /**
         *     val icon:ImageVector,
         *     val text:String,
         *     val desc:String, // for accessbility
         *     val enabled:()->Boolean,
         *     val visible:()->Boolean,
         *     val onClick:()->Unit,
         */
        if(actions != null && actions.isNotEmpty()) {
            ScrollableRow {
                for(a in actions) {
                    if(a.visible().not()) continue

                    LongPressAbleIconBtn(
                        iconModifier = modifier.size(a.size),
                        pressedCircleSize = a.pressedCircleSize,
                        icon = a.icon,
                        tooltipText = a.text,
                        iconContentDesc = a.desc,
                        enabled = a.enabled(),
                        onClick = a.onClick
                    )
                }


                // 菜单
                if(showMoreIcon && moreMenuIconBtnItem != null) {
                    //菜单图标
                    val a = moreMenuIconBtnItem
                    LongPressAbleIconBtn(
                        iconModifier = modifier.size(a.size),
                        pressedCircleSize = a.pressedCircleSize,
                        icon = a.icon,
                        tooltipText = a.text,
                        iconContentDesc = a.desc,
                        enabled = a.enabled(),
                        onClick = a.onClick
                    )

                    //菜单项，点击图标显示
                    if(moreMenuExpandState != null && !moreMenuItems.isNullOrEmpty()) {
                        DropdownMenu(
                            offset = DpOffset(x = 30.dp, y = 0.dp),
                            expanded = moreMenuExpandState.value,
                            onDismissRequest = { moreMenuExpandState.value = false }
                        ) {
                            for(it in moreMenuItems) {
                                if(it.visible().not()) continue;

                                DropdownMenuItem(
                                    text = { Text(it.text) },
                                    enabled = it.enabled(),
                                    onClick = {
                                        it.onClick()

                                        if(it.closeMenuAfterClick()) {
                                            moreMenuExpandState.value = false
                                        }
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
