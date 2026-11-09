package com.catpuppyapp.puppygit.git

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.dto.ItemKey
import com.catpuppyapp.puppygit.utils.getHumanReadableSizeStr
import com.catpuppyapp.puppygit.utils.getParentPathEndsWithSeparator
import com.catpuppyapp.puppygit.utils.mime.MimeType
import com.catpuppyapp.puppygit.utils.mime.guessFromFileName
import java.io.File

class StatusTypeEntrySaver(): ItemKey {
    var repoIdFromDb:String="";
    var fileName:String="";
    var relativePathUnderRepo:String="";  //仓库下的相对路径，包含文件名

    var fileParentPathOfRelativePath:String="";  //文件在仓库下的相对路径的父路径，例如 abc/123.txt，文件名是123.txt，父路径是abc/
    var repoWorkDirPath:String="";

    //modified, newfile,untracked, etc
    var changeType:String?=null;
    //entry item, for get delta , etc
//    var entry: Status.Entry?=null;  //这个其实不太靠谱，指针有可能被释放，
    //item canonicalPath
    var canonicalPath:String="";
    var fileSizeInBytes:Long=0;
    //file or dir or submodule
    var itemType:Int = Cons.gitItemTypeFile;
    var dirty:Boolean = false  // for submodule, if has uncommited changes, this should set to true

    private var mime:MimeType? = null

    private var changeTypeAndSuffix:String? = null
    private var itemTypeString:String? = null
//    var subs:MutableList<StatusTypeEntrySaver> = ArrayList();

//    fun isDir():Boolean {
//        return toFile().isDirectory
//    }

    fun getMime():MimeType {
        if(mime==null) {
            //注：MimiType依赖路径末尾的/来判断是文件夹还是文件，原本应该判断是文件夹则加个分隔符再guess Mime类型的，但是由于本类全是文件，没目录，所以这里无需做处理
            mime = MimeType.guessFromFileName(fileName)  // guessFromPath() 默认匹配不上就会返回 MimeType.TEXT_PLAIN ，所以这里不用做空值判断，也不用加空值则返回text plain的逻辑
        }
        return mime!!
    }


    fun getSizeStr():String {
        return getHumanReadableSizeStr(fileSizeInBytes)
    }

    fun getChangeTypeAndSuffix(isDiffToLocal:Boolean):String {
        if(changeTypeAndSuffix==null) {
            val item = this
            changeTypeAndSuffix = ((item.changeType?:"") + (if(item.itemType==Cons.gitItemTypeSubmodule) ", ${Cons.gitItemTypeSubmoduleStr+(if(isDiffToLocal && item.dirty) ", ${Cons.gitSubmoduleDirtyStr}" else "")}" else ""))
        }

        return changeTypeAndSuffix ?: ""
    }

    fun getChangeListItemSecondLineText(isDiffToLocal:Boolean):String {
        return getSizeStr() + ", " + getChangeTypeAndSuffix(isDiffToLocal)
    }

    fun getItemTypeString():String {
        if(itemTypeString==null) {
            itemTypeString = if(itemType == Cons.gitItemTypeDir) {
                Cons.gitItemTypeDirStr
            }else if(itemType == Cons.gitItemTypeFile) {
                Cons.gitItemTypeFileStr
            }else if(itemType == Cons.gitItemTypeSubmodule) {
                Cons.gitItemTypeSubmoduleStr
            }else {
                ""
            }
        }

        return itemTypeString ?: ""
    }

    /**
     * note: path ends with separator /
     */
    fun getParentDirStr():String{
        return getParentPathEndsWithSeparator(relativePathUnderRepo, trueWhenNoParentReturnEmpty = true)
    }


    fun toFile(): File {
        return File(canonicalPath)
    }


    fun toDiffableItem():DiffableItem {
        return DiffableItem(
            repoIdFromDb = repoIdFromDb,
            relativePath = relativePathUnderRepo,
            itemType = itemType,
            changeType = changeType?:"",
            isChangeListItem = true,
            isFileHistoryItem = false,

            // FileHistory专有条目
            entryId = "",
            // FileHistory专有条目
            commitId = "",

            sizeInBytes = fileSizeInBytes,
            shortCommitId = "",

            repoWorkDirPath = repoWorkDirPath,
            fileName = fileName,
            fullPath = canonicalPath,
            fileParentPathOfRelativePath = fileParentPathOfRelativePath,
        )
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatusTypeEntrySaver

        if (repoIdFromDb != other.repoIdFromDb) return false
        if (fileName != other.fileName) return false
        if (relativePathUnderRepo != other.relativePathUnderRepo) return false
        if (changeType != other.changeType) return false
        if (canonicalPath != other.canonicalPath) return false
        if (fileSizeInBytes != other.fileSizeInBytes) return false
        if (itemType != other.itemType) return false
        if (dirty != other.dirty) return false

        return true
    }

    override fun hashCode(): Int {
        var result = repoIdFromDb.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + relativePathUnderRepo.hashCode()
        result = 31 * result + (changeType?.hashCode() ?: 0)
        result = 31 * result + canonicalPath.hashCode()
        result = 31 * result + fileSizeInBytes.hashCode()
        result = 31 * result + dirty.hashCode()
        result = 31 * result + itemType
        return result
    }

    override fun getItemKey():String {
        return generateItemKey(repoIdFromDb, relativePathUnderRepo, changeType, itemType)
    }

    fun maybeIsFileAndExist():Boolean {
        return itemType == Cons.gitItemTypeFile || (itemType != Cons.gitItemTypeDir && itemType != Cons.gitItemTypeSubmodule)
    }

    fun maybeIsDirAndExist():Boolean {
        return itemType == Cons.gitItemTypeDir || itemType == Cons.gitItemTypeSubmodule || itemType != Cons.gitItemTypeFile
    }

    companion object {

        fun generateItemKey(repoId:String, relativePath:String, changeType:String?, itemType:Int):String {
            return repoId+ relativePath+changeType+itemType
        }
    }
}
