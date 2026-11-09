package com.catpuppyapp.puppygit.screen.content.editor

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.compose.MyLazyVerticalStaggeredGrid
import com.catpuppyapp.puppygit.dto.FileDetail
import com.catpuppyapp.puppygit.screen.content.listitem.FileDetailItem
import com.catpuppyapp.puppygit.screen.functions.filterModeActuallyEnabled
import com.catpuppyapp.puppygit.screen.functions.filterTheList
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.RegexUtil
import com.catpuppyapp.puppygit.utils.forEachIndexedBetter
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable


private const val itemWidth = 150
private const val itemMargin = 10
private val itemMarginDp = itemMargin.dp
// 只考虑左右需要的水平外边距
private const val oneItemRequiredMargin = itemMargin*2
private const val oneItemRequiredWidth = (itemWidth + oneItemRequiredMargin)



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileDetailList(
    contentPadding: PaddingValues,
    state: LazyStaggeredGridState,
    list:List<FileDetail>,

    filterListState: LazyStaggeredGridState,
    filterList: MutableList<FileDetail>,
    filterOn: MutableState<Boolean>,  // filter on but may haven't a valid keyword, so actually not enabled filter
    enableFilterState: MutableState<Boolean>,  // indicate filter mode actually enabled or not
    filterKeyword: CustomStateSaveable<TextFieldValue>,
    lastSearchKeyword: MutableState<String>,
    filterResultNeedRefresh: MutableState<String>,
    searching: MutableState<Boolean>,
    searchToken: MutableState<String>,
    resetSearchVars: ()->Unit,

    onClick:(FileDetail)->Unit,
    itemOnLongClick:(idx:Int, FileDetail)->Unit,
    isItemSelected: (FileDetail) -> Boolean,
) {

    val activityContext = LocalContext.current

    val configuration = AppModel.getCurActivityConfig()
    val screenWidthDp = configuration.screenWidthDp

    // calculate item width and counts in each row
//    val (width, maxItemsInEachRow) = remember(configuration.screenWidthDp) {
    val width = remember(configuration.screenWidthDp) {
        val width = if(screenWidthDp < oneItemRequiredWidth) {
            (screenWidthDp - oneItemRequiredMargin).coerceAtLeast(screenWidthDp)
        }else {  // at least can include 2 items width with margin
            itemWidth
        }

//        val actuallyOneItemWidthAndMargin = width + oneItemRequiredMargin
//
//        val maxItemsInEachRow = screenWidthDp / actuallyOneItemWidthAndMargin

//        Pair(width.dp, maxItemsInEachRow)

        width.dp
    }



    //有仓库
    //根据关键字过滤条目
    val keyword = filterKeyword.value.text  //关键字
    val enableFilter = filterModeActuallyEnabled(filterOn.value, keyword)

    val lastNeedRefresh = rememberSaveable { mutableStateOf("") }
    val filteredList = filterTheList(
        needRefresh = filterResultNeedRefresh.value,
        lastNeedRefresh = lastNeedRefresh,
        enableFilter = enableFilter,
        keyword = keyword,
        lastKeyword = lastSearchKeyword,
        searching = searching,
        token = searchToken,
        activityContext = activityContext,
        filterList = filterList,
        list = list,
        resetSearchVars = resetSearchVars,
        match = { idx: Int, it: FileDetail ->
            it.file.name.let {
                it.contains(keyword, ignoreCase = true) || RegexUtil.matchWildcard(it, keyword)
            } || it.file.path.ioPath.contains(keyword, ignoreCase = true)
                    || it.cachedAppRelatedPath().contains(keyword, ignoreCase = true)
                    || it.shortContent.contains(keyword, ignoreCase = true)
        }
    )



    val listState = if(enableFilter) filterListState else state

    //更新是否启用filter
    enableFilterState.value = enableFilter




    MyLazyVerticalStaggeredGrid(
        contentPadding = contentPadding,
        itemMinWidth = width,
        state = listState,
    ) {
        // toList() is necessary , else, may cause concurrent exception
        filteredList.forEachIndexedBetter { idx, it ->
            item {
                FileDetailItem(
                    width = width,
                    margin = itemMarginDp,
                    idx = idx,
                    item = it,
                    onLongClick = itemOnLongClick,
                    onClick = onClick,
                    selected = isItemSelected(it)
                )
            }
        }

    }

}
