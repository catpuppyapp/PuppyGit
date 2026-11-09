package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R


private val iconShape = RoundedCornerShape(8.dp)
private val borderWidth = 2.dp



@Composable
fun AcceptButtons(
    lineIndex: Int,
    lineText: String,
    acceptOursColor:Color,
    acceptTheirsColor:Color,
    acceptBothColor:Color,
    rejectBothColor:Color,
    prepareAcceptBlock: (Boolean, Boolean, Int, String) -> Unit,
) {
    AcceptButtons_LongPressedIcon(
        lineIndex = lineIndex,
        lineText = lineText,
        acceptOursColor = acceptOursColor,
        acceptTheirsColor = acceptTheirsColor,
        acceptBothColor = acceptBothColor,
        rejectBothColor = rejectBothColor,
        prepareAcceptBlock = prepareAcceptBlock,
    )
}


/**
 * this smaller than iconText, usually will not over-sized
 */
@Composable
private fun AcceptButtons_LongPressedIcon(
    lineIndex: Int,
    lineText: String,
    acceptOursColor:Color,
    acceptTheirsColor:Color,
    acceptBothColor:Color,
    rejectBothColor:Color,
    prepareAcceptBlock: (Boolean, Boolean, Int, String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.accept_ours),
            icon = Icons.Filled.ChevronLeft,
            iconContentDesc = stringResource(R.string.accept_ours),
            iconColor = acceptOursColor,
            iconModifier = Modifier.border(width = borderWidth, color = acceptOursColor, shape = iconShape)
        ) {
            prepareAcceptBlock(true, false, lineIndex, lineText)
        }


        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.accept_theirs),
            icon = Icons.Filled.ChevronRight,
            iconContentDesc = stringResource(R.string.accept_theirs),
            iconColor = acceptTheirsColor,
            iconModifier = Modifier.border(width = borderWidth, color = acceptTheirsColor, shape = iconShape)
        ) {
            prepareAcceptBlock(false, true, lineIndex, lineText)
        }


        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.accept_both),
            icon = Icons.Filled.Code,
            iconContentDesc = stringResource(R.string.accept_both),
            iconColor = acceptBothColor,
            iconModifier = Modifier.border(width = borderWidth, color = acceptBothColor, shape = iconShape)
        ) {
            prepareAcceptBlock(true, true, lineIndex, lineText)
        }


        LongPressAbleIconBtn(
            tooltipText = stringResource(R.string.reject_both),
            icon = Icons.Filled.CodeOff,
            iconContentDesc = stringResource(R.string.reject_both),
            iconColor = rejectBothColor,
            iconModifier = Modifier.border(width = borderWidth, color = rejectBothColor, shape = iconShape)
        ) {
            prepareAcceptBlock(false, false, lineIndex, lineText)
        }

    }
}
