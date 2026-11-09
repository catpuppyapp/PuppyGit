package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.UIHelper


@Composable
fun SingleLineCardButton(
    modifier: Modifier = Modifier,
//    paddingValues: PaddingValues = PaddingValues(30.dp),
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val inDarkTheme = Theme.inDarkTheme

    CardButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        content = {
            Text(
                text = text,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                color = UIHelper.getCardButtonTextColor(enabled, inDarkTheme)
            )
        },
    )
}


@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight:Int = 50,
    maxHeight:Int = 120,
    onClick: () -> Unit,
    content:@Composable RowScope.()->Unit,
) {

    val maxHeight = maxHeight.coerceAtLeast(minHeight)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)  //顶部加点margin，不然有时候显示不全
            .then(modifier)
        ,

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MyCard(
            //0.9f 占父元素宽度的百分之90
            modifier = Modifier
                .clickable(enabled = enabled) {
                    onClick()
                }
            ,

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .heightIn(min = minHeight.dp, max = maxHeight.dp)
                    .padding(5.dp)
                ,

                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScrollableColumn {
                    content()
                }
            }

        }

//        Spacer(modifier = Modifier.height(95.dp))

    }

}


@Composable
fun TwoLineTextCardButton(
    enabled: Boolean,
    textPair: Pair<String, String>,
    headIcon: ImageVector,
    headIconWidth: Dp,
    headIconDesc:String,
    onClick: () -> Unit
) {
    CardButton(
        enabled = enabled,
        onClick = onClick,
        content = {
            val color = if(enabled) Color.Unspecified else UIHelper.getDisableTextColor()
            val iconColor = if(enabled) LocalContentColor.current else color

            TwoLineTextsAndIcons(
                text1 = textPair.first,
                text2 = textPair.second,
                text1Color = color,
                text2Color = color,
                headIconWidth = headIconWidth,
                headIcons = { containerModifier ->
                    Row(
                        modifier = containerModifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {

                        SizeIcon(
                            size = headIconWidth,
                            imageVector = headIcon,
                            contentDescription = headIconDesc,
                            tint = iconColor
                        )

                    }
                }
            )
        },
    )
}

