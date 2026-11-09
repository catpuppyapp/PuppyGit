package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.git.DrawCommitNode
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter

@Composable
fun PrintNodesInfo(
    title:String,
    nodes:List<DrawCommitNode>,
    appendEndNewLine:Boolean,
) {
    val thickness = remember {5.dp}
    val spacerHeight = remember {10.dp}

    //用 "\n" 是为了复制时保持格式，不然一复制就变成一行了
    Text("$title\n", fontWeight = FontWeight.ExtraBold)
    nodes.forEachIndexedBetter { idx, it->
        MyHorizontalDivider(thickness = thickness, color = DrawCommitNode.getNodeColorByIndex(idx))
        Spacer(Modifier.height(spacerHeight))
        Text(it.toStringForView())
    }


    //如果有输出，就在输入末尾加个换行符，增加间隔，不然看着太挤
    if(appendEndNewLine) {
        Text("\n")
    }

}
