package com.catpuppyapp.puppygit.utils.saf

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.createDirIfNonexists
import java.io.File

private const val TAG = "SafUtil"

object SafUtil {
    /**
     * saf和ppgit内部目录的缓冲区，其下每个目录都应该关联一个saf目录
     */
    const val safDirName = "saf"

    var safDir:File? = null

    // format: "content://<authority>/<path>"
    const val safContentPrefix = "content://"
    /**
     * 非saf模式的路径必然是/开头的，只是在显示时区分internal://和external://，但saf则不同，存储的时候会加上saf前缀以和普通路径区分
     */
//    const val safPathPrefix = "Saf://"

//    fun toAppSpecifiedSafFormat(originPath:String):String {
//        return "$safPathPrefix$originPath"
//    }

//    fun getOriginPathFromAppSpecifiedSafPath(appSpecifiedSafPath:String):String {
//        return appSpecifiedSafPath.removePrefix(safPathPrefix)
//    }

    fun init(puppyGitDataDir: File) {
        safDir = createDirIfNonexists(puppyGitDataDir, safDirName)
    }

    /**
     * 把uri转换成适合存到db的格式，虽然只是简单调用toString()，但为了以后要有什么变化，修改方便，所以，还是单独写个函数处理
     */
    fun uriToDbSupportedFormat(uri: Uri):String {
        return uri.toString()
    }

    fun isSafPath(path:String):Boolean {
        return path.startsWith(safContentPrefix)
    }

    @Deprecated("")
    fun uriUnderSafDir(uri:Uri):Pair<Boolean, String?> {
        //他妈的这个uri怎么转换？
        //方法1，代码简单，死板：
//        return uri.toString().startsWith("content://${FsUtils.getAuthorityOfUri()}/internal/PuppyGitRepos/PuppyGit-Data/saf/")

        //方法2，代码复杂，灵活：
        //首先把uri转换成绝对路径，再检查是否位于 puppgit-data/saf/目录下
        return try {
            val realPath = appCreatedUriToPath(uri)
            Pair(realPath!!.startsWith(safDir!!.canonicalPath + "/"), realPath)
        }catch (e:Exception) {
            Pair(false, null)
        }
    }

    @Deprecated("")
    fun realPathToExternalAppsUri(realPath:String):Uri? {
        return try {
            var realPath = realPath.removePrefix(safDir!!.canonicalPath+"/")
            realPath = safContentPrefix+realPath
            Uri.parse(realPath)
        }catch (_:Exception) {
            null
        }
    }

    /**
     * @see res/xml/file_paths.xml
     */
    @Deprecated("")
    fun getAppInternalUriPrefix():String {
        return "content://${FsUtils.getAuthorityOfUri()}/internal/"
    }

    /**
     * @see res/xml/file_paths.xml
     */
    @Deprecated("")
    fun getAppExternalUriPrefix():String {
        return "content://${FsUtils.getAuthorityOfUri()}/external/"
    }

    /**
     * 把本app分享出去的uri转换为本app认识的path，固定开头content://，接authority，接 res/xml/file_paths.xml下的路径name，后面是文件路径
     * 例如 "content://com.catpuppyapp.puppygit.play.pro/internal/abc.txt"，对应 /storage/emulated/0/Android/com.catpuppyapp.puppygit.play.pro/files/PuppyGitRepos/abc.txt
     *   其中 internal 对应的实际路径在 res/xml/file_paths.xml里配置，external的路径和上述路径的区别就是把internal以及其对应的实际路径替换下。
     */
    @Deprecated("")
    fun appCreatedUriToPath(uri: Uri):String? {
        val uriStr = uri.toString()

        return if(uriStr.startsWith(getAppInternalUriPrefix())) {
            AppModel.externalFilesDir.canonicalPath + "/" + uriStr.substring(getAppInternalUriPrefix().length)
        }else if(uriStr.startsWith(getAppExternalUriPrefix())) {
            FsUtils.getExternalStorageRootPathNoEndsWithSeparator() + "/" + uriStr.substring(getAppExternalUriPrefix().length)
        }else {
            null
        }
    }

    fun takePersistableRWPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            true
        }catch (e:Exception) {
            MyLog.d(TAG, "#takePersistableRWPermission() try take RW permissions err: ${e.stackTraceToString()}")
            false
        }
    }

    fun takePersistableReadOnlyPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            true
        }catch (e:Exception) {
            MyLog.d(TAG, "#takePersistableReadOnlyPermission() try take ReadOnly permissions err: ${e.stackTraceToString()}")
            false
        }
    }


    fun releasePersistableRWPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            true
        } catch (e: SecurityException) {
            MyLog.d(TAG, "#releasePersistableRWPermission() try release RW permissions err: ${e.stackTraceToString()}")
            false
        }
    }

    fun releasePersistableReadOnlyPermission(contentResolver: ContentResolver, uri: Uri):Boolean {
        return try {
            contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            true
        } catch (e: SecurityException) {
            MyLog.d(TAG, "#releasePersistableReadOnlyPermission() try release ReadOnly permissions err: ${e.stackTraceToString()}")
            false
        }
    }

}
