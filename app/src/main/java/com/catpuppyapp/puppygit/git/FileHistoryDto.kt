package com.catpuppyapp.puppygit.git

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.dto.ItemKey
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.readTimeZoneOffsetInMinutesFromSettingsOrDefault

class FileHistoryDto(
    var fileName:String="",
    var filePathUnderRepo:String="",
    var fileFullPath:String="",
    var fileParentPathOfRelativePath:String = "",  //文件在仓库下的相对路径的父路径，例如 abc/123.txt，文件名是123.txt，父路径是abc/


    var treeEntryOidStr:String="",
    var commitOidStr: String="",  //最初引入此版本的提交
    var dateTime: String="",
    var originTimeOffsetInMinutes:Int = 0,  // commit的时间偏移量，单位分钟 (btw 实际上是 commit使用的是commit对象中的committer signature中的时间和偏移量)
    var authorUsername: String="",  // username
    var authorEmail: String="",
    var committerUsername:String="",
    var committerEmail:String="",
    var shortMsg:String="", //只包含第一行
    var msg: String="",  //完整commit信息
    var repoId:String="",  //数据库的repoId，用来判断当前是在操作哪个仓库
    var repoWorkDirPath:String="",

    //所有包含此entry id的提交的oid列表
    // 降序，最后一个条目即是最初引入此版本的提交，也就是当前对象的`commitOidStr`的值，如果不是，就代表有bug
    var commitList:List<String> = listOf(),
): ItemKey {

    private var commitShortOidStr:String?=null
    private var treeEntryShortOidStr:String?=null
    private var cached_OneLineMsg:String? = null
    private var cached_ShortCommitListStr:String? = null


    fun authorAndCommitterAreSame():Boolean {
        return authorUsername==committerUsername && authorEmail==committerEmail
    }

    fun getCachedCommitShortOidStr():String {
        if(commitShortOidStr==null) {
            commitShortOidStr = Libgit2Helper.getShortOidStrByFull(commitOidStr)
        }
        return commitShortOidStr ?:""
    }
    fun getCachedTreeEntryShortOidStr():String {
        if(treeEntryShortOidStr==null) {
            treeEntryShortOidStr = Libgit2Helper.getShortOidStrByFull(treeEntryOidStr)
        }
        return treeEntryShortOidStr ?:""
    }


    fun getActuallyUsingTimeZoneUtcFormat(settings: AppSettings): String {
        val minuteOffset = readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings, originTimeOffsetInMinutes)

        return formatMinutesToUtc(minuteOffset)
    }


    override fun getItemKey():String {
        return generateItemKey(commitOidStr)
    }




    fun toDiffableItem():DiffableItem {
        return DiffableItem(
            repoIdFromDb = repoId,
            relativePath = filePathUnderRepo,
            itemType = Cons.gitItemTypeFile,
            changeType = Cons.gitStatusModified,
            isChangeListItem = false,
            isFileHistoryItem = true,

            // FileHistory专有条目
            entryId = treeEntryOidStr,
            // FileHistory专有条目
            commitId = commitOidStr,

            sizeInBytes = 0L,
            shortCommitId = getCachedCommitShortOidStr(),

            repoWorkDirPath = repoWorkDirPath,
            fileName = fileName,
            fullPath = fileFullPath,
            fileParentPathOfRelativePath = fileParentPathOfRelativePath,

        )
    }

    fun getCachedOneLineMsg(): String = (cached_OneLineMsg ?: Libgit2Helper.zipOneLineMsg(msg).let { cached_OneLineMsg = it; it });

    fun cachedShortCommitListStr(): String = cached_ShortCommitListStr ?: commitList.joinToString { Libgit2Helper.getShortOidStrByFull(it) }.let { cached_ShortCommitListStr=it; it };



    companion object {
        // 写这个是为了给diffable item调用
        fun generateItemKey(commitOidStr:String ):String {
            return commitOidStr
        }

    }

}
