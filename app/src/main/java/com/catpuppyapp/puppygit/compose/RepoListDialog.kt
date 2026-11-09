package com.catpuppyapp.puppygit.compose
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.stringResource
//import com.catpuppyapp.puppygit.play.pro.R
//import com.catpuppyapp.puppygit.data.entity.RepoEntity
//import com.catpuppyapp.puppygit.utils.AppModel
//import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlin.coroutines.cancellation.CancellationException
//
////20240501：这个东西干嘛的？啊，我想起来了，ChangeList页面用来切换仓库的！但后来改用点击Title弹出下拉选单切换了，所以这个就没用了。
//
//@Deprecated("显示列表题目有点问题，先弃用")
//@Composable
//fun RepoListDialog(
//    curSelectedRepo:MutableState<RepoEntity>,  //如果在查询的列表中就高亮显示，否则就无视
//    itemOnClick:(RepoEntity)->Unit,
//    onClose:()->Unit
//) {
//    val repoList = remember{ mutableStateListOf<RepoEntity>() }
//
//    AlertDialog(
//        title = {
//        },
//        text = {
//            LazyColumn {
//                repoList.forEach { r ->
//                    item {
//                        Row(modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { itemOnClick(r) })
//                        {
//                            Text(text = r.repoName,
//                                color =
//                                if(r.repoName == curSelectedRepo.value.repoName) MaterialTheme.colorScheme.primary
//                                else Color.Unspecified
//                            )
//                        }
//
//                    }
//                }
//            }
//
//        },
//        //点击弹框外区域的时候触发此方法，一般设为和OnCancel一样的行为即可
//        onDismissRequest = {onClose()},
//        dismissButton = {
//            Row(modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically)
//            {
//                TextButton(
//                    onClick = {onClose()}
//                ) {
//                    Text(
//                        text = stringResource(R.string.close),
//                    )
//                }
//
//            }
//        },
//        confirmButton = {
//        },
//
//        )
//
//    LaunchedEffect(Unit) {
//        try {
//            doJobThenOffLoading {
//                val repoDb = AppModel.dbContainer.repoRepository
//                val readyRepoListFromDb = repoDb.getReadyRepoList()
////                println(readyRepoListFromDb.size)
//                repoList.clear()
//                repoList.addAll(readyRepoListFromDb)
//            }
//        } catch (cancel: Exception) {
////            println("LaunchedEffect: job cancelled")
//        }
//    }
//    //compose被销毁时执行的副作用
//    DisposableEffect(Unit) {
////        println("DisposableEffect: entered main")
//        onDispose {
////            println("DisposableEffect: exited repolistdialog")
//        }
//    }
//}