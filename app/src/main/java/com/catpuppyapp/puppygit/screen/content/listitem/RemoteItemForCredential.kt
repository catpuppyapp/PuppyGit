package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.LongPressAbleIconBtn
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.dto.RemoteDtoForCredential
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.listItemPadding


@Composable
fun RemoteItemForCredential(
    isShowLink:Boolean,
    idx:Int,
    thisItem:RemoteDtoForCredential,
    actIcon:ImageVector,
    actText:String,
    showUrlDialog:(title:String, url:String) -> Unit,
    actAction:(()->Unit)?,
) {
    
//    val activityContext = LocalContext.current

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

    Box(
        //0.9f 占父元素宽度的百分之90
        modifier = Modifier
            .fillMaxWidth()
//            .defaultMinSize(minHeight = 100.dp)
            //padding要放到 combinedClickable后面，不然点按区域也会padding
//            .background(if (idx % 2 == 0) Color.Transparent else CommitListSwitchColor)
            .listItemPadding()
        ,
    ) {

        val trailIconSize = remember { MyStyleKt.trailIconSize + 10.dp }
        val trailIconPadding = if(actAction != null) PaddingValues(end = trailIconSize) else PaddingValues()

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(trailIconPadding)
                .fillMaxWidth()
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){

                Text(text = stringResource(R.string.repo) +": ")

                ScrollableRow {
                    Text(text = thisItem.repoName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(text = stringResource(R.string.remote) +": ")

                ScrollableRow {
                    Text(
                        text = thisItem.remoteName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight

                    )
                }
            }

            val fetchUrlTitle = stringResource(R.string.fetch_url)
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(text = fetchUrlTitle+": ")

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                        .clickable {
                           showUrlDialog(fetchUrlTitle, thisItem.remoteFetchUrl)
                        }
                ) {
                    Text(text = thisItem.remoteFetchUrl,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }

            val pushUrlTitle = stringResource(R.string.push_url)
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(text = pushUrlTitle+": ")

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                        .clickable {
                           showUrlDialog(pushUrlTitle, thisItem.remotePushUrl)
                        }
                ){
                    Text(text = thisItem.remotePushUrl,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(text = stringResource(R.string.fetch_linked) +": ")

                ScrollableRow {
                    Text(text = thisItem.getCredentialNameOrNone(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(text = stringResource(R.string.push_linked) +": ")

                ScrollableRow {
                    Text(text = thisItem.getPushCredentialNameOrNone(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = defaultFontWeight
                    )
                }
            }

        }

        if(actAction != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(trailIconSize)
            ) {
                LongPressAbleIconBtn(
                    tooltipText = actText,
                    iconContentDesc = actText,
                    icon = actIcon,
                ) {
                    actAction()
                }
            }
        }
    }
}
