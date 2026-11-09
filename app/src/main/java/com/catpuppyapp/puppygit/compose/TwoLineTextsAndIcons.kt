package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.style.MyStyleKt

private val minHeight = 40.dp

private fun getHorizontalPaddingForIcons(headIconIsNull: Boolean, trailIconIsNull: Boolean) = PaddingValues(start = if(headIconIsNull) 0.dp else 5.dp, end = if(trailIconIsNull) 0.dp else 5.dp)

@Composable
fun TwoLineTextsAndIcons(
    text1:String,
    text2:String = "",
    modifier: Modifier = Modifier,
    text1Color: Color = Color.Unspecified,
    text2Color: Color = Color.Unspecified,
    text1FontWeight: FontWeight? = FontWeight.Bold,
    text2FontWeight: FontWeight? = FontWeight.Light,
    text1FontSize: TextUnit = MyStyleKt.Title.firstLineFontSizeSmall,
    text2FontSize: TextUnit = MyStyleKt.Title.secondLineFontSize,
    text1Scrollable: Boolean = true,
    text2Scrollable: Boolean = true,
    basePadding: PaddingValues = PaddingValues(5.dp),
    headIconWidth: Dp = 0.dp,
    headIcons:  (@Composable BoxScope.(containerModifier: Modifier) -> Unit)? = null,
    trailIconWidth: Dp = 0.dp,
    trailIcons: (@Composable BoxScope.(containerModifier: Modifier) -> Unit)? = null,
) {
    val headIconIsNull = headIcons == null
    val trailIconIsNull = trailIcons == null

    Box(
        modifier = modifier
            // base margin
            .padding(basePadding)
            // avoid icon too closer with screen border
            .padding(getHorizontalPaddingForIcons(headIconIsNull, trailIconIsNull))
            .fillMaxWidth()
            .heightIn(min = minHeight)
        ,
    ) {
        if(!headIconIsNull) {
            headIcons(Modifier.align(Alignment.CenterStart))
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                // avoid text and icon too close
                .padding(getHorizontalPaddingForIcons(headIconIsNull, trailIconIsNull))

                .padding(start = headIconWidth, end = trailIconWidth)
//                .fillMaxWidth()  // no need fill max width
            ,
            verticalArrangement = Arrangement.Center,
        ) {
            SelectionRow(
                modifier = if(text1Scrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier
            ) {
                Text(text = text1, fontSize = text1FontSize, fontWeight = text1FontWeight, color = text1Color)
            }

            if(text2.isNotEmpty()) {
                SelectionRow(
                    modifier = if(text2Scrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier
                ) {
                    Text(text = text2, fontSize = text2FontSize, fontWeight = text2FontWeight, color = text2Color)
                }
            }
        }

        if(!trailIconIsNull) {
            trailIcons(Modifier.align(Alignment.CenterEnd))
        }

    }
}


@Composable
fun OneLineTextsAndIcons(
    text1:String,
    modifier: Modifier = Modifier,
    text1Color: Color = Color.Unspecified,
    text1FontWeight: FontWeight? = null,
    text1FontSize: TextUnit = MyStyleKt.Title.firstLineFontSizeSmall,
    text1Scrollable: Boolean = true,
    basePadding: PaddingValues = PaddingValues(0.dp),
    headIconWidth: Dp = 0.dp,
    headIcons:  (@Composable BoxScope.(containerModifier: Modifier) -> Unit)? = null,
    trailIconWidth: Dp = 0.dp,
    trailIcons: (@Composable BoxScope.(containerModifier: Modifier) -> Unit)? = null,
) {
    TwoLineTextsAndIcons(
        text1 = text1,
        modifier = modifier,
        basePadding = basePadding,
        text1Color = text1Color,
        text1FontWeight = text1FontWeight,
        text1FontSize = text1FontSize,
        text1Scrollable = text1Scrollable,
        headIconWidth = headIconWidth,
        headIcons = headIcons,
        trailIconWidth = trailIconWidth,
        trailIcons = trailIcons,
    )
}



@Composable
fun TwoLineSettingsItem(
    text1: String,
    text2: String = "",
    text1FontSize: TextUnit = MyStyleKt.Title.firstLineFontSizeSmall,
    text2FontSize: TextUnit = MyStyleKt.Title.secondLineFontSize,
    text1Scrollable: Boolean = false,
    text2Scrollable: Boolean = false,
    basePadding: PaddingValues = PaddingValues(0.dp),
    trailIcon: ImageVector? = null,
    trailIconTooltipText: String = "",
    trailIconWidth: Dp = 0.dp,
    trailIconOnClick: ()->Unit = {},
) {

    @Composable
    fun TrailIcon(modifier: Modifier) {
        if(trailIcon != null) {
            LongPressAbleIconBtn(
                modifier = modifier,
                icon = trailIcon,
                tooltipText = trailIconTooltipText,
            ) {
                trailIconOnClick()
            }
        }
    }


    if(text2.isBlank()) {
        OneLineTextsAndIcons(
            text1 = text1,
            text1FontSize = text1FontSize,
            text1Scrollable = text1Scrollable,
            basePadding = basePadding,
            trailIconWidth = trailIconWidth,
            trailIcons = { modifier ->
                TrailIcon(modifier)
            }
        )
    }else {
        TwoLineTextsAndIcons(
            text1 = text1,
            text1FontWeight = null,
            text1FontSize = text1FontSize,
            text1Scrollable = text1Scrollable,
            text2 = text2,
            text2FontSize = text2FontSize,
            text2FontWeight = FontWeight.Light,
            text2Scrollable = text2Scrollable,

            basePadding = basePadding,
            trailIconWidth = trailIconWidth,
            trailIcons = { modifier ->
                TrailIcon(modifier)
            }
        )
    }
}
