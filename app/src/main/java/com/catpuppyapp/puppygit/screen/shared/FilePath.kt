package com.catpuppyapp.puppygit.screen.shared

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.etc.PathType
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.MyLog
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File


private const val TAG = "FilePath"

private const val emptyPath = ""

private val knownSystemFilesManagerUris = listOf(
    "content://com.android.externalstorage.documents/tree/primary",
    "content://com.android.externalstorage.documents/document/primary",

)

// 这里的uri前缀末尾必须带 '/'，因为转换为绝对路径时会移动索引以使用uri中的/，如果不带/的话，得特殊处理
private val knownUris = listOf(
    "content://net.gsantner.markor.provider/external_files/",
    "content://com.blacksquircle.ui.provider/root/",
    "content://com.raival.compose.file.explorer.provider/storage_root/",

    //支持解析我自己的app的uri，很有必要，因为有时候在app内点击未知类型，
    // 再打开，这时候可以选择我的app，但给到Editor的路径却是uri，脱裤子放屁，多此一举，所以解析下
    "content://${FsUtils.getAuthorityOfUri()}/root/",

)

@Parcelize
class FilePath(
    private val rawPath:String,
):Parcelable {
    @IgnoredOnParcel
    private val rawPathType = PathType.getType(rawPath)

    @IgnoredOnParcel
    var ioPath:String = rawPath

    @IgnoredOnParcel
    var ioPathType = rawPathType

    init {
        initIoPath()
    }


    //这里必须用fun不能用lambda，否则会出错，判断不准，具体原因没测试，总之改成fun就行了，或者把lambda放到parcel类外应该也行（未测试）
    private fun pathOrEmpty(tryThisPath :String):String {
        val file = File(tryThisPath)
        return if (file.canRead()) {file.canRead()
            file.canonicalPath
        } else {
            emptyPath
        }
    }

    private fun readableCanonicalPathOrDefault(file:File, default:String):String {
        return file.let {
            if(it.canRead()) {
                it.canonicalPath
            }else {
                default
            }
        }
    }

    private fun initIoPath() {
        ioPath = if(rawPathType == PathType.CONTENT_URI) {
            //尝试匹配已知的uri，若匹配上，解析成绝对路径，否则返回rawPath
            try {
                MyLog.d(TAG, "#initIoPath: type=CONTENT_URI, rawPath=$rawPath")

                FsUtils.translateContentUriToRealPath(Uri.parse(rawPath))
                    ?: resolveFileSlashSlashUri()
                    .ifBlank { tryResolveKnownUriToRealPath() }
                    .ifBlank { rawPath }
            }catch (_: Exception) {
                rawPath
            }
        }else if(rawPathType == PathType.FILE_URI) {
            rawPath.removePrefix(FsUtils.fileUriPathPrefix)
        }else if(rawPathType == PathType.ABSOLUTE) {
            rawPath
        }else {
            rawPath
        }

        ioPathType = PathType.getType(ioPath)
    }

    //若解析失败返回空字符串
    private fun tryResolveKnownUriToRealPath(): String {
        val funName = "tryResolveKnownUriToRealPath"

        try {
            //有的软件会先编码再发uri，例如 raival 的 文件管理器和material files(不过material files的uri没在这处理）
            val uriStr = decodeTheFuckingUriPath(rawPath)

            //安卓系统自带的文件选择器选择/storage/emulated/0下的文件就会是这个路径
            //这里不能以 / 结尾去匹配，因为primary后面可能是冒号或者%百分号开头的编码之类的玩意

            //安卓系统的自带文件管理器路径
            val maybeCanGetPathFromUri = knownSystemFilesManagerUris.any { uriStr.startsWith(it) }


            return if(maybeCanGetPathFromUri) {
                readableCanonicalPathOrDefault(File(FsUtils.getRealPathFromUri(Uri.parse(rawPath))), emptyPath)
            } else {
                //注意：匹配的时候尽量以 / 结尾，避免文件名包含前缀而匹配错误
                //注意：匹配的时候尽量以 / 结尾，避免文件名包含前缀而匹配错误
                //注意：匹配的时候尽量以 / 结尾，避免文件名包含前缀而匹配错误
                //注意：匹配的时候尽量以 / 结尾，避免文件名包含前缀而匹配错误

                var resolvedPath = emptyPath
                for (uriPrefix in knownUris) {
                    // try resolve markor uri to real path
                    val indexOfPrefix = uriStr.indexOf(uriPrefix)
                    // indexOf == 0, means `include` and `starsWith`, both are `true`
                    if(indexOfPrefix == 0) {
                        //20250505修改：之前这里写的是 `uriStr.substring(length-1)`，这样是是为了保留之前的 "/"，
                        // 但后来我想，万一以后这个鸟uri前缀有变化呢？比如变成"content://packname.abc.def:absolute_path"，
                        // 变成这个鸟样的话我还得改代码，所以不如直接匹配前缀，再把/加上，完美
                        val realPathNoRootPrefix = uriStr.substring(uriPrefix.length)  //注：substring从length开始，其值为最大索引+1，所以不包含最后一个字符
                        val file = File("/", realPathNoRootPrefix)
                        //打印的两个路径：第1个应该是没 / 前缀的， 第2个是有的
                        MyLog.d(TAG, "resolved a known uri: realPathNoRootPrefix=$realPathNoRootPrefix, the path which will using is '${file.canonicalPath}'")
                        resolvedPath = readableCanonicalPathOrDefault(file, emptyPath)
                        if(resolvedPath.isNotBlank()) {
                            break
                        }
                    }
                }

                resolvedPath
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: resolved uri err: ${e.localizedMessage}")

            return emptyPath
        }
    }

    //若解析失败，返回空字符串
    private fun resolveFileSlashSlashUri():String {
        val funName = "resolveFileSlashSlashUri"

        return try {
            // try resolve "content://" to real path like "/storage/emulated/0/path"
            val safUri = Uri.parse(rawPath)
            MyLog.d(TAG, "#$funName: safUri: $safUri, safUri.path=${safUri.path}")

            // uri.path是去掉authority（一般是包名）后的路径，例如：uri为：content://com.android.externalstorage.documents/tree/primary%3ARepos, 则uri.path为 /tree/primary:Repos
            val uriPath = safUri?.path ?: emptyPath

            if(uriPath.isBlank()) {
                emptyPath
            }else {
                val decodedPath = decodeTheFuckingUriPath(uriPath)
                MyLog.d(TAG, "#$funName: decodePath=$decodedPath")

                //先直接尝试解码后的路径能否读取，若不能，检查是否是 file:// 或 /file:// 开头的路径，若不是，抛异常
                val file = File(decodedPath)

                if (file.canRead()) {
                    file.canonicalPath
                } else {
                    val uriPath = decodedPath

                    if (uriPath.startsWith(FsUtils.fileUriPathPrefix)) {
                        pathOrEmpty(uriPath.removePrefix(FsUtils.fileUriPathPrefix))
                    } else {
                        val slashPrefixFileUriPath = Cons.slash + FsUtils.fileUriPathPrefix  // "/file://"，质感文件的uri解码到最后再取出uri.path返回的就是这样的路径
                        if (uriPath.startsWith(slashPrefixFileUriPath)) {
                            pathOrEmpty(uriPath.removePrefix(slashPrefixFileUriPath))
                        }else {
                            emptyPath
                        }
                    }
                }
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: resolve uri err: ${e.localizedMessage}")

            emptyPath
        }
    }

    fun isEmpty():Boolean = ioPath.isEmpty()
    fun isBlank():Boolean = ioPath.isBlank()
    fun isNotEmpty():Boolean = ioPath.isNotEmpty()
    fun isNotBlank():Boolean = ioPath.isNotBlank()

    fun toFuckSafFile(context: Context):FuckSafFile {
        return FuckSafFile(context = context, path = this)
    }

    override fun toString(): String {
        return ioPath
    }

    companion object {
        //字符串有可能经过多次编码，所以需要循环一下，解析到无法再解析为止
        //不一定解码uri.path，完整的uri.toString()也行
        fun decodeTheFuckingUriPath(uriPath:String):String {
            var decodedPath = Uri.decode(uriPath)

            for (i in 1..3000) { // 解码直到无法再解
                val newPath = Uri.decode(decodedPath)
                if (newPath == decodedPath) { //再解一次和上次一样就说明无法再解了
                    break
                }

                decodedPath = newPath
            }

            return decodedPath
        }
    }
}
