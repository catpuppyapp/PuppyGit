package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.dropDownItemContainerColor
import com.catpuppyapp.puppygit.utils.isGoodIndexForList

//下拉单选框，不过好像在弹窗使用会崩溃，可能是谷歌bug(20241003 fixed)
//@OptIn(ExperimentalFoundationApi::class)
//@Deprecated("may crashed if use this in dialog")  // 20241003 update: new version of jetpack compose are fixed this bug
@Composable
fun<T> SingleSelectList(
//    outterModifier: Modifier = Modifier.fillMaxWidth(),
//    dropDownMenuModifier:Modifier=Modifier.fillMaxWidth(),
    outterModifier: Modifier = Modifier,
    dropDownMenuModifier:Modifier = Modifier,

    basePadding: (defaultHorizontalPadding:Dp) -> PaddingValues = { defaultHorizontalPadding -> PaddingValues(horizontal = defaultHorizontalPadding) },

    optionsList:List<T>,   // empty list will show "null" and no item for select
    selectedOptionIndex:MutableIntState?,
    selectedOptionValue:T? = if(selectedOptionIndex!=null && isGoodIndexForList(selectedOptionIndex.intValue, optionsList)) optionsList[selectedOptionIndex.intValue] else null,

    // 显示已选中条目时，会传selectedOptionIndex作为index值，其值可能为null
    // when show selected item, will passing selectedOptionIndex as index, the value maybe null
    // for the formatter, index will be null if value not in the list, and index maybe invalid, like -1,-2 something... usually happened when you was selected a item, but remove it from list later, the the selected item will haven't index of the list, should handle this case, if you overwrite this formatter
    menuItemFormatter:(index:Int?, value:T?)->String = {index, value-> value?.toString() ?: ""},
    menuItemOnClick:(index:Int, value:T)->Unit = {index, value-> selectedOptionIndex?.intValue = index},
    menuItemSelected:(index:Int, value:T) -> Boolean = {index, value -> selectedOptionIndex?.intValue == index},

    menuItemFormatterLine2:(index:Int?, value:T?)->String = {index, value-> ""},

    menuItemTrailIcon:ImageVector?=null,
    menuItemTrailIconDescription:String?=null,
    menuItemTrailIconEnable:(index:Int, value:T)->Boolean = {index, value-> true},
    menuItemTrailIconOnClick:(index:Int, value:T) ->Unit = {index, value->},
) {
    val expandDropdownMenu = rememberSaveable { mutableStateOf(false) }

    val containerSize = remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    Surface (
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .padding(basePadding(MyStyleKt.defaultHorizontalPadding))
            .clickable {
                expandDropdownMenu.value = !expandDropdownMenu.value
            }
            .onSizeChanged {
                // unit is pixel
                containerSize.value = it
            }
            .then(outterModifier)
        ,

//        colors = CardDefaults.cardColors(
//            containerColor = UIHelper.defaultCardColor(),
//        ),

//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 3.dp
//        )

    ) {
        val trailIconWidth = 20.dp
        //用box的好处是如果整体宽度过小，不会把右边的箭头顶没，但箭头会和文本内容重叠
        Box(
            modifier = Modifier

                // selected item container (not dropdown menu)
                // 已选择容器的颜色 (不是下拉菜单的已选择，而是展示已选择条目的那个容器，点击可展开菜单的那个）
                .background(UIHelper.defaultCardColor())

                .padding(horizontal = 10.dp)
                .defaultMinSize(minHeight = 50.dp)
                .fillMaxWidth()
            ,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = trailIconWidth)
                    .align(Alignment.CenterStart)
                ,

                verticalArrangement = Arrangement.Center,
            ) {
                val index = selectedOptionIndex?.intValue
                val value = selectedOptionValue
                SelectionRow(Modifier.horizontalScroll(rememberScrollState())) {
                    Text(text = menuItemFormatter(index, value))
                }

                menuItemFormatterLine2(index, value).let {
                    if(it.isNotBlank()) {
                        SelectionRow(Modifier.horizontalScroll(rememberScrollState())) {
                            Text(text = it, fontSize = MyStyleKt.Title.secondLineFontSize, fontWeight = FontWeight.Light)
                        }
                    }
                }
            }

            Row (
                modifier = Modifier
                    .width(trailIconWidth)
                    .align(Alignment.CenterEnd)
                ,

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(imageVector = if(expandDropdownMenu.value) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft
                    , contentDescription = null
                )
            }
        }


        DropdownMenu(
            //I forgot whey limit the width, actually is unnecessary
//            modifier = dropDownMenuModifier.width((containerSize.value.width/2).coerceAtLeast(MyStyleKt.DropDownMenu.minWidth).dp),
//            modifier = dropDownMenuModifier.widthIn(min = MyStyleKt.DropDownMenu.minWidth),
            modifier = dropDownMenuModifier.width(UIHelper.pxToDpAtLeast0(containerSize.value.width, density)),
//            modifier = dropDownMenuModifier,

            expanded = expandDropdownMenu.value,
            onDismissRequest = { expandDropdownMenu.value=false }
        ) {
            val lastIndex = optionsList.size - 1
            for ((index, value) in optionsList.withIndex()) {
                //忽略当前显示条目
                //不忽略了，没必要，显示的是选中条目，一点击，展开的菜单里是所有条目，也很合理
//            if(k == selectedOption.intValue) {
//                continue
//            }

                val selected = menuItemSelected(index, value)

                Column(
                    modifier = Modifier
                        .dropDownItemContainerColor(selected)
                        .fillMaxSize()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //列出其余条目
                        DropdownMenuItem(
                            text = {
                                DropDownMenuItemText(
                                    text1 = menuItemFormatter(index, value),
                                    text2 = menuItemFormatterLine2(index, value),
                                )
                            },
                            onClick = {
                                expandDropdownMenu.value=false

                                menuItemOnClick(index, value)
                            },
                            trailingIcon = (
                                    //如果icon不为null，返回一个compose，否则返回null
                                    if(menuItemTrailIcon != null) ({
                                        IconButton(
                                            enabled = menuItemTrailIconEnable(index, value),
                                            onClick = {
                                                menuItemTrailIconOnClick(index, value)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = menuItemTrailIcon,
                                                contentDescription = menuItemTrailIconDescription
                                            )
                                        }
                                    })else {
                                        null
                                    }
                            )
                        )

                    }

//                    if(index != lastIndex) {
//                        MyHorizontalDivider()
//                    }
                }

            }
        }

    }

}
