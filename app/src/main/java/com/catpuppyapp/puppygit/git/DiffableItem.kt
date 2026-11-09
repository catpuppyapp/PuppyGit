package com.catpuppyapp.puppygit.git

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.dto.ItemKey
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.github.git24j.core.Repository
import kotlinx.coroutines.channels.Channel
import java.io.File

/**
 * 函数名前加前缀是为了避免 getFiled() 这类名字和jvm默认的 getter 冲突导致报错
 */
data class DiffableItem(
    val repoIdFromDb:String="",
    val relativePath:String = "",

    val repoWorkDirPath:String="",
    val fileName:String="",
    val fullPath:String="",

    //文件在仓库下的相对路径的父路径，例如 abc/123.txt，文件名是123.txt，父路径是abc/，如果是文件在仓库根目录，返回/而不是空字符串
    // parent path of file's relative path, e.g. abc/123.txt, file name is '123.txt',
    // parent path is 'abc/', if file at root path of repository workdir,
    // will return '/', rather than empty string
    val fileParentPathOfRelativePath:String="",

    val itemType:Int = Cons.gitItemTypeFile,

    // modified/new/deleted之类的，
    // 不要把默认值改成有效的修改类型，
    // 因为源头StatusTypeEntrySaver里居然默认值是null！当时没考虑好，
    // 该弄成默认空字符串就对了，
    // 总之这个默认值设为null应该比设为一个有效的修改类型兼容性更强些，
    // 因为以前的代码可能有做若null则空字符串的判断
    val changeType:String = "",

    val isChangeListItem:Boolean = false,
    val isFileHistoryItem:Boolean = false,

    // FileHistory专有条目
    val entryId:String = "",
    // FileHistory专有条目
    val commitId:String = "",

    val sizeInBytes:Long = 0L,
    val shortCommitId:String = "",


    //页面状态用的变量
    val loading:Boolean = true,
    val loadChannel:Channel<Int> = Channel(),
    //严格来说是diff result才对，但当时命名的时候没想到，现在已经很多地方用这名字，积重难返了
    val diffItemSaver: DiffItemSaver = DiffItemSaver(),
    val stringPairMap: MutableMap<String, CompareLinePairResult> = mutableStateMapOf(),
    val compareLinePair:CompareLinePair = CompareLinePair(),
    val submoduleIsDirty:Boolean = false,
    val errMsg: String = "",
    //是否可见，收起不可见，展开可见
    val visible:Boolean = false,

    //暂时或真的没有diffitem可用，例如hunks为空、正在加载，之类的，这个就会为真
    var noDiffItemAvailable:Boolean = false,
    //自定义显示为什么没有diff item的组件，里面应该包含理由
    var whyNoDiffItem:(@Composable ()->Unit)? = null,
    //仅包含理由
    var whyNoDiffItem_msg:String = "",
):ItemKey {
    companion object {

        fun anInvalidInstance(): DiffableItem {
            return DiffableItem(repoIdFromDb = "an_invalid_DiffableItem_30bc0f41-63e8-461a-b48b-415d5584740d")
        }
    }

    override fun getItemKey(): String {
        //这个必须和StatusTypeEntrySaver 还有 FileHistoryItem的itemkey一样，不然会无法定位上次点击条目
        // else的是 isFileHistoryItem
        return if(isChangeListItem) StatusTypeEntrySaver.generateItemKey(repoIdFromDb, relativePath, changeType, itemType) else FileHistoryDto.generateItemKey(commitId)
    }


    fun getFileNameEllipsis(fileNameLimit:Int):String {
        //获取文件名，如果超过限制长度则截断并在前面追加省略号
        return relativePath.let {
            if(it.length > fileNameLimit) {
                "…${it.reversed().substring(0, fileNameLimit).reversed()}"
            }else {
                it
            }
        }
    }


    fun copyForLoading():DiffableItem {
        // 注意：loading时会把条目设为可见，一般比较符合逻辑，若感觉有违和的地方，可改成传参控制
        return copy(loading = true, visible = true, stringPairMap = mutableStateMapOf(), compareLinePair = CompareLinePair(), submoduleIsDirty = false, errMsg = "", loadChannel = Channel())
    }

    fun closeLoadChannel() {
        runCatching { loadChannel.close() }
    }

    protected fun finalize() {
        // finalization logic
        closeLoadChannel()
    }

    fun toChangeListItem(): StatusTypeEntrySaver {
        val stes = StatusTypeEntrySaver()
        stes.repoIdFromDb = repoIdFromDb
        stes.fileName = fileName
        stes.relativePathUnderRepo = relativePath
        stes.changeType = changeType
        stes.canonicalPath = fullPath
        stes.fileParentPathOfRelativePath = fileParentPathOfRelativePath
        stes.fileSizeInBytes = sizeInBytes
        stes.itemType = itemType
        stes.dirty = submoduleIsDirty
        stes.repoWorkDirPath = repoWorkDirPath

        return stes
    }

    fun toFileHistoryItem():FileHistoryDto {
        val fhi = FileHistoryDto()
        fhi.fileName = fileName
        fhi.filePathUnderRepo = relativePath
        fhi.fileFullPath = fullPath
        fhi.fileParentPathOfRelativePath = fileParentPathOfRelativePath
        fhi.commitOidStr = commitId
        fhi.repoId = repoIdFromDb
        fhi.repoWorkDirPath = repoWorkDirPath
        return fhi
    }

    fun toFile():File = File(fullPath)

    fun neverLoadedDifferences() : Boolean = diffItemSaver.relativePathUnderRepo.isEmpty()

    fun maybeLoadedAtLeastOnce() = !neverLoadedDifferences()

    /**
     * 当前文件是否在仓库根目录
     */
    fun atRootOfWorkDir() = fileParentPathOfRelativePath == "/";

    // 这个颜色其实可以自己获取，但调用本方法的地方都已经获取了这个颜色，所以索性直接传参了
    fun getAnnotatedAddDeletedAndParentPathString(colorOfChangeType: Color): AnnotatedString {
        return buildAnnotatedString {
            //若已加载过diff内容则显示添加和删除了多少行
            if(maybeLoadedAtLeastOnce()) {
                if(diffItemSaver.addedLines > 0) {
                    withStyle(style = SpanStyle(color = Theme.mdGreen)) { append("+"+diffItemSaver.addedLines) }
                    withStyle(style = SpanStyle(color = Theme.Gray1)) { append(", ") }
                }
                    
                if(diffItemSaver.deletedLines > 0) {
                    withStyle(style = SpanStyle(color = Theme.mdRed)) { append("-"+diffItemSaver.deletedLines) }
                    withStyle(style = SpanStyle(color = Theme.Gray1)) { append(", ") }
                }
            }

            //当前文件的父路径，以/结尾，无文件名，若文件在仓库根目录则为/
            withStyle(style = SpanStyle(color = colorOfChangeType)) { append(fileParentPathOfRelativePath) }
        }
    }

    // file history item commit msg
    private var cachedOneLineCommitMsg:String? = null
    fun oneLineCommitMsgOfCommitOid():String {
        return (cachedOneLineCommitMsg ?: (try {
            Repository.open(repoWorkDirPath).use { repo ->
                Libgit2Helper.getCommitMsgOneLine(repo, commitId)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            ""
        }).let { cachedOneLineCommitMsg = it; it })
    }
}
