package com.catpuppyapp.puppygit.fileeditor.ui.composable.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.play.pro.R

@Composable
fun MenuIcon(modifier: Modifier = Modifier, color:Color?=null) { //传null是为了在这个组件里获取颜色，要不然在外部调用LocalContentColor.current，可能获取到别的颜色
    val color = color?: LocalContentColor.current
    Icon(
        imageVector = Icons.Default.Menu,
        contentDescription = stringResource(R.string.menu),
        modifier = modifier,
        tint = color
    )
}

@Composable
fun CheckCircleIcon(modifier: Modifier = Modifier, color:Color?=null) {
    val color = color?: LocalContentColor.current

    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = stringResource(R.string.line_selected),
        modifier = modifier,
        tint = color,

    )
}

@Composable
fun CopyIcon(modifier: Modifier = Modifier, color:Color?=null) {
    val color = color?: LocalContentColor.current

    Icon(
        imageVector = Icons.Default.ContentCopy,
        contentDescription = stringResource(R.string.copy),
        modifier = modifier,
        tint = color

    )
}

@Composable
fun TrashIcon(modifier: Modifier = Modifier, color:Color?=null) {
    val color = color?: LocalContentColor.current

    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = stringResource(R.string.delete),
        modifier = modifier,
        tint = color

    )
}

@Composable
fun SelectAllIcon(modifier: Modifier = Modifier, color:Color?=null) {
    val color = color?: LocalContentColor.current

    Icon(
        imageVector = Icons.Default.SelectAll,
        contentDescription = stringResource(R.string.select_all),
        modifier = modifier,
        tint = color

    )
}

@Composable
fun CancelIcon(modifier: Modifier = Modifier, color:Color?=null) {
    val color = color?: LocalContentColor.current

    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = stringResource(R.string.cancel),
        modifier = modifier,
        tint = color

    )
}
