package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.maybeIsGoodKeyword
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable

@Composable
fun <T> SelectedUnSelectedDialog(
    title:String,
    loading:Boolean,
    selectedTitleText:String,
    unselectedTitleText:String,
    selectedItemList:MutableList<T>,
    unselectedItemList:MutableList<T>,
    filterKeyWord: CustomStateSaveable<TextFieldValue>,
    selectedItemFormatter:@Composable (T)->Unit,
    unselectedItemFormatter:@Composable (T)->Unit,
    filterSelectedItemList: (keyword:String)->List<T>,
    filterUnselectedItemList: (keyword:String)->List<T>,
    cancel:()->Unit,
){
    ConfirmDialog3(
        title = title,
        requireShowTextCompose = true,
        textCompose = {
            SelectedUnSelectedList(
                loading = loading,
                selectedTitleText = selectedTitleText,
                unselectedTitleText = unselectedTitleText,
                selectedItemList = selectedItemList,
                unselectedItemList = unselectedItemList,
                filterKeyWord = filterKeyWord,
                selectedItemFormatter = selectedItemFormatter,
                unselectedItemFormatter = unselectedItemFormatter,
                filterSelectedItemList=filterSelectedItemList,
                filterUnselectedItemList=filterUnselectedItemList,
            )
        },
        cancelBtnText = stringResource(R.string.close),
        onCancel = cancel,
        showOk = false,
        onOk = {}
    )
}

@Composable
fun <T> SelectedUnSelectedList(
    loading:Boolean,
    selectedTitleText:String,
    unselectedTitleText:String,
    selectedItemList:MutableList<T>,
    unselectedItemList:MutableList<T>,
    filterKeyWord: CustomStateSaveable<TextFieldValue>,
    selectedItemFormatter:@Composable (T)->Unit,
    unselectedItemFormatter:@Composable (T)->Unit,

    filterSelectedItemList: (keyword:String)->List<T>,
    filterUnselectedItemList: (keyword:String)->List<T>,
) {
    MySelectionContainerPlaceHolder {
        if(loading) {
            LoadingTextBase(modifier = Modifier.fillMaxWidth().padding(top=20.dp), text = { Text(stringResource(R.string.loading)) })
        }else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                //注意：这个过滤没开协程，直接在渲染线程过滤的，目前这个组件用来过滤仓库，我估计用户不会克隆超过100个仓库，性能损耗很小，没必要开协程处理
                //普通的过滤，加不加清空无所谓，一按返回就清空了，但这个常驻显示，得加个清空按钮
                FilterTextField(filterKeyWord = filterKeyWord, requireFocus = false)

                Spacer(Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {

                    //根据关键字过滤条目
                    val k = filterKeyWord.value.text  //关键字
                    val enableFilter = maybeIsGoodKeyword(k)
                    val filteredSelectedList = if(enableFilter){
                        filterSelectedItemList(k)
                    }else {
                        selectedItemList
                    }

                    val filteredUnselectedList = if(enableFilter){
                        filterUnselectedItemList(k)
                    }else {
                        unselectedItemList
                    }

                    item {
                        SettingsTitle(selectedTitleText+"(${filteredSelectedList.size})")
                    }

                    if(filteredSelectedList.isEmpty()) {
                        item {
                            MySelectionContainer {
                                ItemListIsEmpty()
                            }
                        }
                    }else {
                        filteredSelectedList.forEachBetter {
                            item {
                                Column {
                                    selectedItemFormatter(it)
                                }
                            }
                        }
                    }

                    item {
                        SettingsTitle(unselectedTitleText+"(${filteredUnselectedList.size})")
                    }

                    if(filteredUnselectedList.isEmpty()) {
                        item {
                            MySelectionContainer {
                                ItemListIsEmpty()
                            }
                        }
                    }else {
                        filteredUnselectedList.forEachBetter {
                            item {
                                Column {
                                    unselectedItemFormatter(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

