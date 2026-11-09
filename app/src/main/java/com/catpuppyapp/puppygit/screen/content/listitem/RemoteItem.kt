package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.listItemPadding
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RemoteItem(
    showBottomSheet: MutableState<Boolean>,
    curObjInState: CustomStateSaveable<RemoteDto>,
    idx:Int,
    curObj: RemoteDto,
    lastClickedItemKey:MutableState<String>,

    onClick:()->Unit
) {
    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current


    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

    Column(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            .combinedClickable(
                enabled = true,
                onClick = {
                    lastClickedItemKey.value = curObj.remoteId
                    onClick()
                },
                onLongClick = {
                    lastClickedItemKey.value = curObj.remoteId

                    //震动反馈
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    curObjInState.value = RemoteDto()

                    //设置当前条目
                    curObjInState.value = curObj

                    //显示底部菜单
                    showBottomSheet.value = true
                },
            )
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if(idx%2==0)  Color.Transparent else CommitListSwitchColor)

            .then(
                if(curObj.remoteId == lastClickedItemKey.value){
                    Modifier.background(UIHelper.getLastClickedColor())
                }else {
                    Modifier
                }
            )
            .listItemPadding()


    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.name) +": ")

            ScrollableRow {
                Text(text = curObj.remoteName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){

            Text(text = stringResource(R.string.url) +": ")

            ScrollableRow {
                Text(
                    text = curObj.remoteUrl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.push_url) +": ")

            ScrollableRow {
                Text(text = if(curObj.pushUrlTrackFetchUrl) stringResource(R.string.use_url) else curObj.pushUrl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.fetch_credential) +": ")

            ScrollableRow {
                Text(text = curObj.getLinkedFetchCredentialName(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.push_credential) +": ")

            ScrollableRow {
                Text(
                    text = curObj.getLinkedPushCredentialName(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight
                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,

        ){
            Text(text = stringResource(R.string.branch_mode) +": ")

            ScrollableRow {
                Text(text = if(curObj.branchMode == Cons.dbRemote_Fetch_BranchMode_All) activityContext.getString(R.string.all) else activityContext.getString(R.string.custom),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = defaultFontWeight

                )
            }
        }
        if(curObj.branchMode != Cons.dbRemote_Fetch_BranchMode_All) {
            Row (
                verticalAlignment = Alignment.CenterVertically,

            ){
                Text(text = (if(curObj.branchListForFetch.size > 1) stringResource(R.string.branches) else stringResource(R.string.branch)) +": ")

                ScrollableRow {
                    Text(text = curObj.branchListForFetch.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }
        }


    }
}
