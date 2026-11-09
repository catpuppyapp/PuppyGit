package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt

// just forgot why create this, keep for now
//@Composable
//fun AddItemBar(height: Dp = 100.dp, onClick:() -> Unit) {
//    Column(modifier = Modifier.height(height).fillMaxWidth()
//        .clickable(onClick = onClick)
//        ,
//
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ){
//        Row{
//            Icon(
//                modifier = Modifier.fillMaxHeight(.6f).fillMaxWidth(),
//                imageVector = Icons.Filled.Add,
//                contentDescription = stringResource(R.string.add),
//                tint = MyStyleKt.IconColor.normal
//            )
//        }
//        Row {
//            Text(text = stringResource(id = R.string.add_an_app),
//                style = MyStyleKt.ClickableText.getStyle(),
//                color = MyStyleKt.ClickableText.getColor(),
//                fontSize = MyStyleKt.TextSize.default
//            )
//        }
//    }
//}
