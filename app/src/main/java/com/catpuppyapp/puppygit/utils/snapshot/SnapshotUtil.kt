package com.catpuppyapp.puppygit.utils.snapshot

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.EncodingUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.getNowInSecFormatted
import com.catpuppyapp.puppygit.utils.getShortUUID
import java.io.File

object SnapshotUtil:SnapshotCreator {
    private const val TAG = "SnapshotUtil"

    /*
        禁用快照时，返回Ret Success，但由于没创建快照，文件名和路径都是空字符串，但有的地方会用文件名或路径是否为空来判断快照是否创建成功，这样就会误判导致某些方法无法正常执行，
      因此，这里创建几个常量名用来作为禁用创建快照功能时的文件名和路径。
     */
    private val fileSnapshotDisable_FileNamePlaceHolder = "FileSnapshotDisable-name"  //文件快照禁用时，一律返回此文件名
    private val fileSnapshotDisable_FilePathPlaceHolder = "FileSnapshotDisable-path"  //文件快照禁用时，一律返回此路径
    private val contentSnapshotDisable_FileNamePlaceHolder = "ContentSnapshotDisable-name"  //内容快照禁用时，一律返回此文件名
    private val contentSnapshotDisable_FilePathPlaceHolder = "ContentSnapshotDisable-path"  //内容快照禁用时，一律返回此路径


    private var enableFileSnapshotForEditor = true
    private var enableContentSnapshotForEditor = true
    private var enableFileSnapshotForDiff = true

    fun init(enableFileSnapshotForEditorInitValue:Boolean, enableContentSnapshotForEditorInitValue:Boolean, enableFileSnapshotForDiffInitValue:Boolean) {
        enableFileSnapshotForEditor = enableFileSnapshotForEditorInitValue
        enableContentSnapshotForEditor = enableContentSnapshotForEditorInitValue
        enableFileSnapshotForDiff = enableFileSnapshotForDiffInitValue
    }

    fun update_enableFileSnapshotForEditor(newValue:Boolean) {
        enableFileSnapshotForEditor = newValue
    }

    fun update_enableContentSnapshotForEditor(newValue: Boolean) {
        enableContentSnapshotForEditor = newValue
    }

    fun update_enableFileSnapshotForDiff(newValue: Boolean) {
        enableFileSnapshotForDiff = newValue
    }


    override fun createSnapshotByContentAndGetResult(
        srcFileName:String,
        fileContent:String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        flag:SnapshotFileFlag
    ): Ret<Pair<String, String>?> {
        if(!enableContentSnapshotForEditor) {
            return Ret.createSuccess(Pair(contentSnapshotDisable_FileNamePlaceHolder, contentSnapshotDisable_FilePathPlaceHolder))
        }

        val funName = "createSnapshotByContentAndGetResult"

        try {
            if((trueUseContentFalseUseEditorState && fileContent!!.isNotEmpty()) || (trueUseContentFalseUseEditorState.not() && editorState!!.contentIsEmpty().not())) {
                val (snapshotFileName, snapFileFullPath, snapFile) = getSnapshotFileNameAndFullPathAndFile(srcFileName, flag)
                MyLog.d(TAG, "#$funName: will save snapFile to: '$snapFileFullPath'")
                val snapRet = if(trueUseContentFalseUseEditorState) {
                    FsUtils.saveFileAndGetResult(
                        fileFullPath = snapFileFullPath,
                        text = fileContent!!,
                        charsetName = editorState.codeEditor?.editorCharset?.value
                    )
                } else {
                    editorState.dumpLinesAndGetRet(File(snapFileFullPath).outputStream())
                }

                if(snapRet.hasError()) {
                    MyLog.e(TAG, "#$funName: save snapFile '$snapshotFileName' failed: ${snapRet.msg}")
                    return Ret.createError(null, snapRet.msg)
                }else {
                    //把快照文件名和文件完整路径设置到snapRet里
                    return Ret.createSuccess(Pair(snapshotFileName, snapFileFullPath))
                }
            }else {  //文件内容为空
                val msg = "file content is empty, will not create snapshot for it($srcFileName)"
                MyLog.d(TAG, "#$funName: $msg")
                return Ret.createSuccess(null, msg, Ret.SuccessCode.fileContentIsEmptyNeedNotCreateSnapshot)
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName() err: srcFileName=$srcFileName, err=${e.stackTraceToString()}")
            return Ret.createError(null, "save file snapshot failed: ${e.localizedMessage}", Ret.ErrCode.saveFileErr)
        }
    }


    override fun createSnapshotByFileAndGetResult(srcFile:FuckSafFile, flag:SnapshotFileFlag):Ret<Pair<String,String>?>{
        //如果未开启文件快照功能 或 开启了diff文件但flag不是diff文件快照 或 开启了editor文件快照但flag不是editor文件快照 则 直接返回成功
        if((!enableFileSnapshotForEditor && !enableFileSnapshotForDiff)
            || (!enableFileSnapshotForEditor && enableFileSnapshotForDiff && !flag.isDiffFileSnapShot())
            || (enableFileSnapshotForEditor && !enableFileSnapshotForDiff && !flag.isEditorFileSnapShot())
        ) {
            return Ret.createSuccess(Pair(fileSnapshotDisable_FileNamePlaceHolder, fileSnapshotDisable_FilePathPlaceHolder))  // 1是file name，2是file path
        }

        val funName = "createSnapshotByFileAndGetResult"

        var srcFileNameForLog = ""
        var snapshotFileNameForLog = ""
        try {
            if(!srcFile.exists()) {
                throw RuntimeException("`srcFile` doesn't exist!, path=${srcFile.canonicalPath}")
            }

            val srcFileName = srcFile.name
            srcFileNameForLog = srcFileName
            val (snapshotFileName, snapFileFullPath, snapFile) = getSnapshotFileNameAndFullPathAndFile(srcFileName, flag)
            snapshotFileNameForLog = snapshotFileName

            MyLog.d(TAG, "#$funName: will save snapFile to: '$snapFileFullPath'")

            // copy src to snap file
            srcFile.copyTo(snapFile.outputStream())

            if(!snapFile.exists()) {  //拷贝失败
                MyLog.e(TAG, "#$funName: save snapFile '$snapshotFileName' failed!")
                throw RuntimeException("copy src to snapshot failed!")
            }else{  //拷贝成功
                //把快照文件名设置到snapRet里
                return Ret.createSuccess(Pair(snapshotFileName, snapFileFullPath))
            }

        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName() err: srcFileName=$srcFileNameForLog, snapshotFileName=$snapshotFileNameForLog, err=${e.stackTraceToString()}")
            return Ret.createError(null, "save file snapshot failed: ${e.localizedMessage}", Ret.ErrCode.saveFileErr)
        }
    }

    fun createSnapshotByContentWithRandomFileName(
        fileContent: String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        flag:SnapshotFileFlag
    ): Ret<Pair<String, String>?> {
        return createSnapshotByContentAndGetResult(
            srcFileName = getShortUUID(10),
            fileContent = fileContent,
            editorState = editorState,
            trueUseContentFalseUseEditorState = trueUseContentFalseUseEditorState,
            flag = flag
        )
    }

    private fun getSnapshotFileNameAndFullPathAndFile(
        srcFileName: String,
        flag: SnapshotFileFlag
    ): Triple<String, String, File> {
        val snapshotFileName = getANonexistsSnapshotFileName(srcFileName, flag.flag)
        val snapDir = AppModel.getOrCreateFileSnapshotDir()
        val snapFile = File(snapDir.canonicalPath, snapshotFileName)
        //返回 filename, fileFullPath, file
        return Triple(snapshotFileName, snapFile.canonicalPath, snapFile)
    }


    private fun getANonexistsSnapshotFileName(srcFileName:String, flag: String):String {
        var count = 0
        val limit = 100

        //精确到秒的时间戳。
        // 由于这函数应该会在1秒内执行完毕，所以在循环内重复生成时间戳没什么意义，在外部生成一个即可
        val timestamp = getNowInSecFormatted(Cons.dateTimeFormatterCompact)

        while(true) {
            // 初始化n为0，然后 `++n > limit`会执行`limit`次，而`n++ > limit` 这种写法实际会执行 `limit+1` 次，不过其实在这，多那一次少那一次无所谓
            if(++count > limit) {  // should not happen
                throw RuntimeException("err: generate snapshot filename failed")
            }

            val fileName = genSnapshotFileName(srcFileName, flag, timestamp, getShortUUID(6))
            //如果file.canRead()为假，file.exists()我记得也会返回假，所以就算没读取文件的权限，应该也不会卡循环
            if(!File(fileName).exists()) {
                return fileName
            }
        }
    }

    /**
    获取快照文件名

    目标文件名（快照文件名）格式为：“srcNameIncludeExt(包含后缀名）-flag-年月日时分秒-6位随机uid.bak”
    例如："abc.txt-content_saveErrFallback-20240421012203-ace123"
     */
    private fun genSnapshotFileName(srcFileName:String, flag: String, timestamp:String, uid:String):String {
        //目标文件名（快照文件名）格式为：“srcNameIncludeExt(包含后缀名）-flag-年月日时分秒-6位随机uid.bak”
//        val sb = StringBuilder(srcFileName)
//        return sb.append("-").append(flag).append("-").append(getNowInSecFormatted(Cons.dateTimeFormatterCompact)).append("-").append(getShortUUID(len = uidLen)).append(".bak").toString()

        return "$srcFileName-$flag-$timestamp-$uid.bak"
    }

}
