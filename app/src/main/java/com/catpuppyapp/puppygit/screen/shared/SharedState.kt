package com.catpuppyapp.puppygit.screen.shared

import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.dto.Box
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.utils.MyLog


private const val TAG = "SharedState"

object SharedState {
    // 若false，有时候会不显示loading，效果不好
    const val defaultLoadingValue = true

    val homeScreenNeedRefresh = mutableStateOf("")


    // indexToWorktree or headToIndex页面的列表，无论从上述哪个页面跳转到diff页面，都会把那个页面的列表地址存到这个变量中。(所以，导航链不能是 indexToWorktree ChangeList->Diff->headToIndex ChangeList->Diff，不过正常来说，并没从Diff前进到Index的路径，所以无此bug)
    var homeChangeList_itemList = mutableListOf<StatusTypeEntrySaver>()

    //用于在indexToWorkTree那个页面指示index是否有条目
    var homeChangeList_indexHasItem = mutableStateOf(false)

    // index to worktree
    val homeChangeList_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val index_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val treeToTree_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)
    val fileHistory_LastClickedItemKey = mutableStateOf(Cons.init_last_clicked_item_key)

    val homeChangeList_Refresh = mutableStateOf("IndexToWorkTree_ChangeList_refresh_init_value_5hpn")
    val indexChangeList_Refresh = mutableStateOf("HeadToIndex_ChangeList_refresh_init_value_ts7n")

    //用来在请求选择和执行选择的FileChooser页面之间共享选中的路径
    val fileChooser_DirPath = mutableStateOf("")  //如果用app内置 File Picker 选目录，用这个做state
    val fileChooser_FilePath = mutableStateOf("")  //如果用app内置 File Picker 选文件，用这个做state


//    val editor_softKeyboardIsVisible = Box(false)

    // save home code editor for release when activity destroyer
    // sub page's code editor release when navi up, so don't need store them,
    //  but home page's code editor release when app destroy,
    //  and it only have 1 instance, so we can simple store it, and release when app destroy
    var homeCodeEditor: MyCodeEditor? = null
        private set

    fun updateHomeCodeEditor(newCodeEditor: MyCodeEditor) {
        homeCodeEditor?.let {
            // the if should be true after Activity destroyed then re-create app,
            //   if is be true but the app never re-create, is bug maybe
            if(it.uid != newCodeEditor.uid) {
                it.release()
            }
        }

        homeCodeEditor = newCodeEditor
    }

}
