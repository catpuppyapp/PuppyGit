package com.catpuppyapp.puppygit.screen.shared

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.catpuppyapp.puppygit.compose.FileChangeListenerState
import com.catpuppyapp.puppygit.constants.LineBreak
import com.catpuppyapp.puppygit.etc.PathType
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.EncodingUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.cancellation.CancellationException


private const val TAG = "FuckSafFile"

/**
 * fuck the saf api, saf should full-compatible with File api and support jni, but it didn't, so it just shit!
 *
 * note: the saf api `canRead()` `isFile()` and maybe others, they are maybe return incorrect result!!
 *   e.g. a file can read, but the `canRead()` may return false.
 *
 * @param context if only use File, no need the content uri, then can pass null to context
 *
 */
class FuckSafFile(val context: Context?, val path: FilePath) {
    companion object {
        fun fromFile(f:File):FuckSafFile {
            return FuckSafFile(null, FilePath(f.canonicalPath))
        }
    }

    var isSaf:Boolean = false

    var file:File? = null

    var safFile:DocumentFile? = null
    var safUri:Uri? = null

    //注：saf判断isFile并不准确，有可能目标是文件，但返回假
    val isFile:Boolean
        get() = (if(isSaf) safFile?.isFile else file?.isFile) == true

    val isDirectory:Boolean
        get() = (if(isSaf) safFile?.isDirectory else file?.isDirectory) == true

    val name:String
        get() = (if(isSaf) safFile?.name else file?.name) ?: ""

    val canonicalPath:String
        get() = (if(isSaf) safUri?.toString() else file?.canonicalPath) ?: ""


    init {
        val ioPath = path.ioPath
        val pathType = path.ioPathType

        if(pathType == PathType.ABSOLUTE) {
            initFile(ioPath)
        }else if(pathType == PathType.CONTENT_URI) {
            initDocFile(ioPath)
        }else if(pathType == PathType.FILE_URI) {
            val realPath = ioPath.removePrefix(FsUtils.fileUriPathPrefix)
            initFile(realPath)
        }
    }

    /**
     * 最后修改时间，单位毫秒
     */
    fun lastModified():Long {
        return (if (isSaf) safFile?.lastModified() else file?.lastModified()) ?: 0L
    }

    /**
     * android doesn't support get creation time yet
     */
    fun creationTime():Long {
        return lastModified()
    }

    fun length():Long {
        return (if (isSaf) safFile?.length() else file?.length()) ?: 0L
    }

    private fun initFile(path:String) {
        isSaf = false
        file = File(path)
    }

    private fun initDocFile(path:String) {
        isSaf = true
        val currentUri = Uri.parse(path)
        safUri = currentUri
        safFile = FsUtils.getDocumentFileFromUri(context!!, currentUri)
    }

    fun inputStream():InputStream {
        return if(isSaf) context!!.contentResolver.openInputStream(safUri!!)!! else file!!.inputStream()
    }

    fun outputStream():OutputStream {
        return if(isSaf) context!!.contentResolver.openOutputStream(safUri!!)!! else file!!.outputStream()
    }

    fun bufferedReader(charsetName: String?):BufferedReader {
        return EncodingUtil.ignoreBomIfNeed(
            newInputStream = { inputStream() },
            charsetName = charsetName
        ).inputStream.bufferedReader(EncodingUtil.resolveCharset(charsetName))
    }

    fun bufferedWriter(charsetName: String?): BufferedWriter {
        val output = outputStream()
        EncodingUtil.addBomIfNeed(output, charsetName)
        return output.bufferedWriter(EncodingUtil.resolveCharset(charsetName))
    }

    fun exists():Boolean {
        return (if(isSaf) safFile?.exists() else file?.exists()) == true
    }

    fun renameFileTo(newPath:String):Boolean {
        return renameFileTo(File(newPath))
    }

    fun renameFileTo(newFile:File):Boolean {
        return file?.renameTo(newFile) == true
    }

    fun renameSafFile(newName:String):Boolean {
        return safFile?.renameTo(newName) == true
    }

    fun delete():Boolean {
        return (if(isSaf) safFile?.delete() else file?.delete()) == true
    }

    fun renameTo(newFuckSafFile:FuckSafFile):Boolean {
        inputStream().copyTo(newFuckSafFile.outputStream())

        return delete()
    }

    fun copyTo(target:OutputStream) {
        FsUtils.copy(inputStream(), target)
    }

    fun canRead():Boolean {
        // 注：saf的 canRead并不准，有时候明明能读，告诉你不能
        return (if(isSaf) safFile?.canRead() else file?.canRead()) == true
    }

    override fun toString(): String {
        return canonicalPath
    }

    fun createChangeListener(
        fileChangeListenerState: FileChangeListenerState,
        taskName: String? = null,
        onChange:()->Unit
    ):Job? {
        if(path.ioPathType == PathType.INVALID) {
            return null
        }

        val taskName = taskName ?: "FileChangeListener(fileName: $name)"

        val intervalInMillSec = fileChangeListenerState.intervalInMillSec

        return doJobThenOffLoading {
            try {
                // restore last file length and modified time when app on resumed(come back from background)
                // 在app从后台切回来时恢复上次记录的状态
                var oldFileLen = fileChangeListenerState.lastLength ?: length()
                var oldFileModified = fileChangeListenerState.lastModified ?: lastModified()
                while (true) {
                    delay(intervalInMillSec)

                    val newFileLen = length()
                    val newFileModified = lastModified()
                    fileChangeListenerState.lastLength = newFileLen
                    fileChangeListenerState.lastModified = newFileModified
                    MyLog.v(TAG, "$taskName: oldFileLen=$oldFileLen, newFileLen=$newFileLen, oldFileModified=$oldFileModified, newFileModified=$newFileModified")
                    if (oldFileLen != newFileLen || oldFileModified != newFileModified) {
                        oldFileLen = newFileLen
                        oldFileModified = newFileModified
                        MyLog.d(TAG, "$taskName: file changed, will call `onChange()`")

                        onChange()
                    }
                }
            } catch (_: CancellationException) {
                // task may be canceled normally, just ignore

            } catch (e: Exception) {
                MyLog.d(TAG, "$taskName: listen change of file err: fileName='$name', filePath='${path.ioPath}', err=${e.stackTraceToString()}")
            }
        }
    }


    // due to the saf apis are unreliable, so need a simple test to check the file is actually readable and exist or not
    fun isActuallyReadable(): Boolean {
        return try {
            inputStream().use { it.read() }
            true
        }catch(_: Exception) {
            false
        }
    }

    fun detectEncoding(): String {
        return try {
            EncodingUtil.detectEncoding(newInputStream = { inputStream() })
        }catch (e: Exception) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "$TAG#detectEncoding err: ${e.localizedMessage}")
            }

            EncodingUtil.defaultCharsetName
        }
    }

    fun detectLineBreak(charsetName: String?): LineBreak {
        var lineBreak = ""
        val arrBuf = CharArray(2048)

        bufferedReader(charsetName).use { reader ->
            while (lineBreak.isEmpty()) {
                val readSize = reader.read(arrBuf)
                if(readSize == -1) {
                    break
                }

                for (i in 0 until readSize) {
                    val char = arrBuf[i]

                    if(char == '\r') {
                        val nextChar = arrBuf.getOrNull(i+1)

                        lineBreak = if(nextChar == null) {

                            // if is null, maybe is the end of the buffer, so try another read once,
                            //   then whatever was read, break the loop
                            if(reader.read() == '\n'.code) {
                                "\r\n"
                            }else {  // is not '\n' or is -1 EOF
                                "\r"
                            }
                        }else if(nextChar == '\n') {
                            "\r\n"
                        }else {
                            "\r"
                        }

                        break
                    }else if(char == '\n') {
                        lineBreak = "\n"

                        break
                    }
                }
            }
        }

        return LineBreak.getType(lineBreak, default = LineBreak.LF)!!
    }

}
