package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.MyHorizontalDivider
import com.catpuppyapp.puppygit.compose.MyToggleCard
import com.catpuppyapp.puppygit.compose.ScrollableRow
import com.catpuppyapp.puppygit.dto.FileDetail
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.FsUtils


//private val bgColorInDarkTheme = Color(0xFF343434)
private val contentTextColorInDarkTheme = Color(0xFFA8A8A8)
//private val bgColorInLightTheme = Color(0xFFDEDEDE)
private val contentTextColorInLightTheme = Color(0xFF595959)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileDetailItem(
    width:Dp,
    margin:Dp,
    idx:Int,
    item: FileDetail,
    selected:Boolean,
    onLongClick:(idx:Int, FileDetail)->Unit,
    onClick:(FileDetail)->Unit,
){
    val inDarkTheme = Theme.inDarkTheme

    MyToggleCard(
        modifier = Modifier
            .padding(margin)
//            .background(if(selected) MaterialTheme.colorScheme.primaryContainer else if(inDarkTheme) bgColorInDarkTheme else bgColorInLightTheme)
//            .background(if(selected) MaterialTheme.colorScheme.primaryContainer else UIHelper.defaultCardColor())
            .combinedClickable(
                onLongClick = { onLongClick(idx, item) },
            ) {
                onClick(item)
            }
            .width(width)
        ,

        selected = selected,
    ) {
//        val fontColor = UIHelper.getFontColor()
        val fontColor = Color.Unspecified

        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            ScrollableRow {
                Text(item.file.name, fontSize = MyStyleKt.Title.firstLineFontSize, color = fontColor, fontWeight = FontWeight.Bold)
            }
            ScrollableRow {
                Text(item.cachedAppRelatedPath(), fontSize = MyStyleKt.Title.secondLineFontSize, color = fontColor, fontWeight = FontWeight.Light)
            }
        }

        MyHorizontalDivider()

        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            Text(item.shortContent, color = if(inDarkTheme) contentTextColorInDarkTheme else contentTextColorInLightTheme, fontWeight = FontWeight.Light)
        }
    }
}
