package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.InLineIcon
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.SpecialCredential
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.getFormatTimeFromSec
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CredentialItem(
    showBottomSheet: MutableState<Boolean>,
    curCredentialState: CustomStateSaveable<CredentialEntity>,
    idx:Int,
    thisItem:CredentialEntity,
    isLinkMode:Boolean,
    linkedFetchId:String,
    linkedPushId:String,
    lastClickedItemKey:MutableState<String>,
    onClick:(CredentialEntity)->Unit
) {
    val haptic = LocalHapticFeedback.current

    val isMatchByDomain = SpecialCredential.MatchByDomain.equals_to(thisItem)
    val isNone = SpecialCredential.NONE.equals_to(thisItem)
    val isNotMatchByDomainOrNone = !(isMatchByDomain || isNone)

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }
    

//    println("IDX::::::::::"+idx)
    Box(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = thisItem.id
                    onClick(thisItem)
                },
                onLongClick = {
                    if(isNotMatchByDomainOrNone) {
                        lastClickedItemKey.value = thisItem.id

                        //震动反馈
//                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        curCredentialState.value = CredentialEntity()

                        //设置当前条目，供bottomsheet使用
                        curCredentialState.value = thisItem

                        //显示底部菜单
                        showBottomSheet.value = true
                    }
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if (idx % 2 == 0) Color.Transparent else CommitListSwitchColor)
            .then(
                if(lastClickedItemKey.value == thisItem.id) {
                    Modifier.background(UIHelper.getLastClickedColor())
                }else Modifier
            )
            .listItemPadding()

        ,

//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        val trailIconSize = remember { MyStyleKt.trailIconSize + 10.dp }
        // 若是match by domain 或none，则尾部无按钮，占满屏幕；否则给按钮留点空间
        val trailIconPadding = if(isNotMatchByDomainOrNone) PaddingValues(end = trailIconSize) else PaddingValues()

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(trailIconPadding)
                .fillMaxWidth()
            ,
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {

            Row (
                verticalAlignment = Alignment.CenterVertically,

            ){
                val linked = isLinkMode && (linkedFetchId==thisItem.id || linkedPushId==thisItem.id);

                //x 改成设置高亮颜色了) 如果关联模式，对已绑定的前面加个*
                Text(text = stringResource(R.string.name) + ": ")

                val iconSize = if(linked) {
                    if(linkedFetchId == thisItem.id && linkedPushId == thisItem.id) {
                        trailIconSize * 2
                    }else {
                        trailIconSize
                    }
                } else {
                    0.dp
                }

                Box {
                    ScrollableRow(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = iconSize)
                    ) {
                        Text(
                            text = thisItem.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if(linked) FontWeight.ExtraBold else defaultFontWeight,
                            color = if(linked) MyStyleKt.DropDownMenu.selectedItemColor() else Color.Unspecified,

                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(iconSize)
                    ) {
                        // linked fetch
                        if(isLinkMode && linkedFetchId==thisItem.id) {
                            InLineIcon(
                                icon = Icons.Filled.Download,
                                tooltipText = stringResource(R.string.fetch),
                                enabled = false,
                            ) { }
                        }

                        // linked push
                        if(isLinkMode && linkedPushId==thisItem.id) {
                            InLineIcon(
                                icon = Icons.Filled.Upload,
                                tooltipText = stringResource(R.string.push),
                                enabled = false,
                            ) { }
                        }
                    }
                }
            }
//
//        Row (
//            verticalAlignment = Alignment.CenterVertically,
//
//        ){
//
//            Text(text = stringResource(R.string.type) +":")
//            Text(text = thisItem.getTypeStr(),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                fontWeight = defaultFontWeight
//
//            )
//        }

            //给match by domain和none显示个desc，他妈的这个match by domain名字怎么这么长？该起个简单的名字，妈的
            if(isMatchByDomain || isNone) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,

                ){
                    Text(text = stringResource(R.string.desc) +": ")
                    ScrollableRow {
                        Text(text = if(isMatchByDomain) stringResource(R.string.credential_match_by_domain_note_short) else stringResource(R.string.no_credential_will_be_used),
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = defaultFontWeight

                        )
                    }
                }
            }

            //显示编辑时间
            if(isNotMatchByDomainOrNone) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(text = stringResource(R.string.edited) +": ")
                    ScrollableRow {
                        Text(text = getFormatTimeFromSec(thisItem.baseFields.baseUpdateTime),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = defaultFontWeight

                        )
                    }
                }
            }

        }

        //显示编辑按钮
        if(isNotMatchByDomainOrNone) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(trailIconSize)
                ,
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
            ) {
                LongPressAbleIconBtn(
                    tooltipText = stringResource(R.string.edit),
                    icon = Icons.Filled.Edit,
                    iconContentDesc = stringResource(R.string.edit),
                ) {
                    //更新最后点击条目
                    lastClickedItemKey.value = thisItem.id

                    //跳转到编辑页面
                    AppModel.navController.navigate(Cons.nav_CredentialNewOrEditScreen+"/"+thisItem.id)
                }
            }
        }
    }
}
