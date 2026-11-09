package com.catpuppyapp.puppygit.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.catpuppyapp.puppygit.activity.findActivity
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.IntentCons
import com.catpuppyapp.puppygit.constants.LineNum
import com.catpuppyapp.puppygit.dto.Box
import com.catpuppyapp.puppygit.dto.EditorPayload
import com.catpuppyapp.puppygit.dto.FileItemDto
import com.catpuppyapp.puppygit.dto.FileSimpleDto
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.play.pro.BuildConfig
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.utils.mime.MimeType
import com.catpuppyapp.puppygit.utils.mime.guessFromFile
import com.catpuppyapp.puppygit.utils.snapshot.SnapshotFileFlag
import com.catpuppyapp.puppygit.utils.snapshot.SnapshotUtil
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.catpuppyapp.puppygit.utils.temp.TempFileFlag
import org.mozilla.universalchardet.Constants
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "FsUtils"

object FsUtils {

    //字节流
    const val binaryMimeType = "application/octet-stream"

    const val rootName = "root"
    const val rootPath = "/"
    /**
     * internal and external storage path prefix
     */
    const val internalPathPrefix = "App://"
    const val externalPathPrefix = "Ext://"
    // /data/data/packagename/
    const val innerPathPrefix = "Inner://"

    // "App://PuppyGit-Data"
    const val appDataPathPrefix = "AppData://"

    //路径前缀
    //这个后面跟的不是路径，得用什么玩意解析一下才能拿到真名
    const val contentUriPathPrefix = "content://"
    //这个后面跟的是绝对路径，例如: "file:///storage/emulated/0/yourfile.txt"
    const val fileUriPathPrefix = "file://"
    const val absolutePathPrefix = "/"

    //换了种解析方法，这个没用了
//    const val encodedFileUriPathPrefix = "file%3A%2F%2F%2F"  // web 那个url encoder或url component编码过的 "file://"，由于安卓的uri在.net包下，可能在设计上想用来网络传输所以用web的url组件编码，呵呵，这破saf的破uri，本地用都这么恶心，还想web用，想屁呢


    //必须和 AndroidManifest.xml 里的 provider.android:authorities 的值一样
//    const val PROVIDER_AUTHORITY = "com.catpuppyapp.puppygit.play.pro.fileprovider"

    const val textMIME = "text/plain"
    const val appExportFolderName = "PuppyGitExport"
    const val appExportFolderNameUnderDocumentsDirShowToUser = "Documents/${appExportFolderName}"  //显示给用户看的路径

    enum class CopyFileConflictStrategy(val code:Int) {
        /**
         * skip exists files
         */
        SKIP(1),

        /**
         * rename exists files
         */
        RENAME(2),

        /**
         * clear folder if exists, overwrite file if exists
         */
        OVERWRITE_FOLDER_AND_FILE(3),
        ;

        companion object {
            fun fromCode(code: Int): CopyFileConflictStrategy? {
                return CopyFileConflictStrategy.entries.find { it.code == code }
            }
        }
    }

    object Patch {
        const val suffix = ".patch"

        fun getPatchDir():File{
            return AppModel.getOrCreatePatchDir()
        }

        fun newPatchFile(repoName:String, commitLeft:String, commitRight:String):File {
            val patchDir = getPatchDir()

            //在patchdir创建repo目录 (patch目录结构：patchDir/repoName/xxx..xxx.patch)
            val parentDir = File(patchDir, repoName)
            if(!parentDir.exists()) {
                parentDir.mkdirs()
            }

            val commitLeft = Libgit2Helper.getShortOidStrByFull(commitLeft)
            val commitRight = Libgit2Helper.getShortOidStrByFull(commitRight)

            var file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
            if(file.exists()) {  //如果文件已存在，重新生成一个，当然，仍然有可能存在，不过概率非常非常非常小，可忽略不计，因为文件名包含随机数和精确到分钟的时间戳
                file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
                if(file.exists()) {
                    file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
                    if(file.exists()) {
                        file = File(parentDir.canonicalPath, genFileName(commitLeft, commitRight))
                    }
                }
            }

            return file
        }

        private fun genFileName(commitLeft: String, commitRight: String):String {
            //文件名示例：abc1234..def3456-adeq12-202405031122.patch
            return "$commitLeft..$commitRight-${getShortUUID(6)}-${getNowInSecFormatted(Cons.dateTimeFormatter_yyyyMMddHHmm)}$suffix"
        }
    }

    object FileMimeTypes {
        data class MimeTypeAndDescText(
            val type:String,
            val descText:(context:Context) -> String,
        )

        // 注意： typeList和getTextList()必须对应！
        val typeList = listOf(
            MimeTypeAndDescText("text/plain") {it.getString(R.string.file_open_as_type_text)},
            MimeTypeAndDescText("image/*") {it.getString(R.string.file_open_as_type_image)},
            MimeTypeAndDescText("audio/*") {it.getString(R.string.file_open_as_type_audio)},
            MimeTypeAndDescText("video/*") {it.getString(R.string.file_open_as_type_video)},

            //暂时用zip代替归档文件(压缩文件)，因为压缩mime类型有好多个！
            // 用模糊的application/*支持的程序不多，只有zip支持的最多！
            // 而且解压程序一般会根据二进制内容判断具体类型，所以，用zip实际上效果不错
            MimeTypeAndDescText("application/zip") {it.getString(R.string.file_open_as_type_archive)},

            //不知道具体类型的，当作二进制文件（字节流）打开，在我的国产手机上反而比 */* 匹配的app多；
            // 但在pixel则相反，这个至少在任何手机上，都能多少匹配几个app，还是启用这个吧
            MimeTypeAndDescText(binaryMimeType) {it.getString(R.string.file_open_as_type_other)},

            //这个不知道会匹配到什么，期望匹配所有，但其实不一定，我测试 pixel确实几乎匹配所有，
            // 但很多都是没什么卵用的app例如日历。。。但在我的国产手机上，匹配很少，甚至无匹配
            MimeTypeAndDescText("*/*")  {it.getString(R.string.file_open_as_type_any)},
        )
    }

    /**
     * get authority for gen uri for file
     * note: the value must same as provider.android:authorities in AndroidManifest.xml
     */
    fun getAuthorityOfUri():String {
        return BuildConfig.FILE_PROVIDIER_AUTHORITY
    }

    fun getUriForFile(context: Context, file: File):Uri {
        val uri = FileProvider.getUriForFile(
            context,
            getAuthorityOfUri(),
            file
        )

        MyLog.d(TAG, "#getUriForFile: uri='$uri'")

        return uri
    }

    fun openFile(
        context: Context,
        file: File,
        mimeType: String,
        // if true, uri only add read permission
        readOnly:Boolean,

        editorPayload: EditorPayload? = null
    ):Boolean {
        try {
            val uri = getUriForFile(context, file)

            // 文件是否只读与Action匹配，更准确，但兼容性可能差点，很多app只支持ACTION_VIEW
//            val intent = Intent(if(readOnly) Intent.ACTION_VIEW else Intent.ACTION_EDIT)

            //ACTION_VIEW其实也能带写权限，具体取决于uri权限
            val intent = Intent(Intent.ACTION_VIEW)  // 这个可能兼容性更好

            MyLog.d(TAG, "#openFile(): require open: mimeType=$mimeType, uri=$uri")

            intent.setDataAndType(uri, mimeType)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            //如果非read only，追加写权限
            if(!readOnly) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            if(editorPayload != null) {
                intent.putExtra(IntentCons.ExtrasKey.editorPayload, editorPayload)
            }

            // 测试无效，废弃）for support open image by gallary app
            // 另外，之前点图片无法选择图库是因为我的手机设置了默认图库，晕。。。。
//            intent.addCategory(Intent.CATEGORY_DEFAULT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.addCategory(Intent.CATEGORY_BROWSABLE)

            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            MyLog.e(TAG, "#openFile(): try open file(path=${file.canonicalPath}) err! params is: mimeType=$mimeType, readOnly=$readOnly\n" + e.stackTraceToString())
            return false
        }
    }

    fun getExportDirUnderPublicDocument():Ret<File?> {
        return createDirUnderPublicExternalDir(dirNameWillCreate=appExportFolderName, publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
    }

    private fun createDirUnderPublicExternalDir(dirNameWillCreate: String, publicDir:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)): Ret<File?> {

        // 20240424改用saf导出文件了，saf从安卓4.0(ndk19)开始支持，兼容性更好
        //经过我的测试，api 26，安卓8.0.0 并不能访问公开目录(Documents/Pictures)之类的，所以这个判断没什么卵用，就算通过了，也不一定能获取到公开目录，貌似至少安卓9才行
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return Ret.createError(null, "Doesn't support export file to public dir on android version lower than 9", Ret.ErrCode.doesntSupportAndroidVersion)
        }

        //注：getExternalStoragePublicDirectory() 这个api在8添加，在29弃用(deprecated)了，弃用了但没删除，所以应该也能用
        val dir = File(publicDir.canonicalPath, dirNameWillCreate)

//        else {  //没测试，getExternalStorageDirectory() 应该需要权限
//            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
//        }

        // Make sure the path directory exists.
        return if(dir.exists() || dir.mkdirs()) { //文件夹存在或创建成功
            Ret.createSuccess(dir, "open folder success!", Ret.SuccessCode.default)
        }else { //文件夹不存在且创建文件夹失败
            Ret.createError(null, "open folder failed!", Ret.ErrCode.openFolderFailed)
        }

    }

    /**
     * 如果文件不存在，直接返回；如果存在，在相同路径下生成一个唯一文件名，例如输入"/abc/def.txt"，若def.txt已经存在，则可能返回"/abc/def(1).txt"
     *
     * 此函数主要用途：用来在拷贝文件名称冲突时自动重命名
     */
    fun getANonExistsFile(file:File):File {
        //若文件不存在，直接返回
        if(file.exists().not()) {
            return file
        }

        //文件存在，需要生成唯一文件名

        val (parent, fileName) = splitParentAndName(file.canonicalPath)

        return File(parent + getANonExistsName(fileName, exists = { newName -> File(parent+newName).exists() }))
    }


    /**
     * @return Pair(name, ext) e.g. input "abc.txt", return "abc" and ".txt"; 但是如果.在开头，则不视为后缀，例如 input ".git", return ".git" and ""
     */
    fun splitNameAndExt(name:String):Pair<String, String> {
        //拆分出文件名和后缀，生成类似 "file(1).ext" 的格式，而不是 "file.ext(1)" 那样
        val extIndex = name.lastIndexOf('.')
        return if(extIndex > 0) {  //注意这里是索引大于最后一个分隔符加1，即"."之前至少有一个字符，因为如果文件名开头是"."，是隐藏文件，不将其视为后缀名，这时生成的文件名类似 ".git(1)" 而不是"(1).git"
            // 例如输入：/path/to/abc.txt, 返回 "/path/to/abc" 和 ".txt"，后缀名包含"."
            Pair(name.substring(0, extIndex), name.substring(extIndex))
        }else {  //无后缀或.在第一位(例如".git")
            Pair(name, "")
        }
    }

    /**
     * 本函数适用于从"/"开头的绝对路径和非"/"开头的相对路径拆分去父路径和文件名
     * 本函数应该不会报错，但若担心可 runCatching{}
     * @return Pair(parentPath, fileName), e.g. input "/abc/def/123", will return Pair("/abc/def/", "123")，并不会移除父路径末尾的'/'，所以可以直接把parentPath和新文件名拼接获得同目录下的新文件对象
     */
    fun splitParentAndName(canonicalPath:String):Pair<String, String> {
        // `File("/").name` return empty String, so if path equals rootPath, should return empty string as file name for compatible
        if(canonicalPath.isEmpty() || canonicalPath == rootPath) {
            return Pair(canonicalPath, "")
        }

        // try split path and name
        val lastSeparatorAt = canonicalPath.lastIndexOf('/')
        val fileNameStartAt = lastSeparatorAt+1

        return if(fileNameStartAt >= canonicalPath.length) { //bad index, out of bound，执行到这，若此判断为真，"/"必然在路径末尾
            // no file name yet, canonicalPath like "path/" or "/path/"  but is not "/", cause "/" already return when enter this function
            Pair(canonicalPath, "")
        } else {
            //若是非 "/" 开头的相对路径，执行到这里，会返回 Pair("", fileName)
            Pair(canonicalPath.substring(0, fileNameStartAt), canonicalPath.substring(fileNameStartAt))
        }
    }

    /**
     * 入参期望规范路径，但不强制要求，只要是有效完整路径就行，"/.././abc" 这样的也行
     */
    fun getParentPath(canonicalPath: String):String {
        // method 1
        return File(canonicalPath).canonicalFile.parent ?: ""

        // method 2
//        return splitParentAndName(canonicalPath).first.removeSuffix(Cons.slash)
    }

    /**
     * 获取一个不存在的名字，由exists函数判定名字是否已经存在
     *
     * 主要用途：名称冲突时生成唯一文件名，不过也能用来干别的，如果有需求的话
     */
    fun getANonExistsName(name:String, exists:(String)->Boolean):String {
        var target = name
        if(exists(name)) {
            //拆分出文件名和后缀，生成类似 "file(1).ext" 的格式，而不是 "file.ext(1)" 那样
            val (fileName, fileExt) = splitNameAndExt(name)

            //生成文件名的最大编号，超过这个编号将会生成随机文件名
//            val max = Int.MAX_VALUE
            val max = 1000

            //for循环，直到生成一个不存在的名字
            for(i in 1..max) {
                target = "$fileName($i)$fileExt"
                if(!exists(target)) {
                    break
                }
            }

            //如果文件还存在，生成随机名
            if(exists(target)){
                while (true) {
                    target = "$fileName(${getShortUUID(len=8)})$fileExt"
                    if(!exists(target)) {
                        break
                    }
                }
            }
        }

        return target
    }

    data class PasteResult(val srcPath: String, val targetPath:String, val exception:Exception?)

    //考虑要不要加个suspend？加suspend是因为拷贝大量文件时，有可能长时间阻塞，但实际上不加这方法也可运行
    fun copyOrMoveOrExportFile(srcList:List<File>, destDir:File, requireDeleteSrc:Boolean):Ret<List<PasteResult>?> {
        //其实不管拷贝还是移动都要先拷贝，区别在于移动后需要删除源目录
        //如果发现同名，添加到同名列表，弹窗询问是否覆盖。

        if(srcList.isEmpty()) {  //例如，我选择了文件，然后对文件执行了重命名，导致已选中条目被移除，就会发生选中条目列表为空或缺少了条目的情况
            return Ret.createError(null, "srcList is empty!", Ret.ErrCode.srcListIsEmpty)  // 结束操作
        }


        //目标路径不能是文件
        if(!destDir.isDirectory || destDir.isFile) {  //其实这俩判断一个就行了，不过我看两个方法的实现不是简单的一个是另一个的取反，所以我索性两个都用了
            return Ret.createError(null, "target is a file but expect dir!", Ret.ErrCode.targetIsFileButExpectDir)
        }

        val errList = mutableListOf<PasteResult>()

        //开始执行 拷贝 or 移动 or 导出
        srcList.forEachBetter forEach@{
            val src = it
            var target:File? = null

            //若某个条目执行操作失败，不会中止操作，会针对其他条目继续执行操作
            try {
                //1 源不能不存在(例如，我在选择模式下对某个复制到“剪贴板”的文件执行了重命名，粘贴时就会出现源不存在的情况(这种情况实际已经解决，现在20240601选中文件后重命名会把已选中列表对应条目也更新))
                //2 源和目标不能相同(否则会无限递归复制)
                //3 源不能是目标文件夹的父目录(否则会无限递归复制)
                if((!src.exists()) || (src.isDirectory && destDir.canonicalPath.startsWith(src.canonicalPath))) {
                    return@forEach  //不会终止循环而是会进入下次迭代，相当于continue
                }

                target = getANonExistsFile(File(destDir, src.name))

                src.copyRecursively(target, false)  //false，禁用覆盖，不过，只有文件存在时才需要覆盖，而上面其实已经判断过了，所以执行到这，target肯定不存在，也用不着覆盖，但以防万一，这个值传false，避免错误覆盖文件

                if(requireDeleteSrc) {  //如果是“移动(又名“剪切”)“，则删除源
                    src.deleteRecursively()
                }

            }catch (e:Exception) {
//                MyLog.e(TAG, "#copyOrMoveOrExportFile() err: src=${src.canonicalPath}, target=${target?.canonicalPath}, err=${e.stackTraceToString()}")
                errList.add(PasteResult(src.canonicalPath, target?.canonicalPath?:"", e))
            }
        }

        //如果待覆盖的文件列表为空，则全部复制或移动完成，提示成功，否则弹窗询问是否覆盖
//                if(fileNeedOverrideList.isEmpty()) {
//                    Msg.requireShow(appContext.getString(R.string.success))
//                }else {  //显示询问是否覆盖文件的弹窗
//                    showOverrideFilesDialog.value = true
//                }

        //显示成功提示
        return if(errList.isEmpty()) {
            Ret.createSuccess(null)
        }else {
            Ret.createError(errList, "plz check the err list")
        }
    }

    fun saveFile(fileFullPath:String, text:String, charsetName: String?) {
        // 覆盖式保存文件
        FileOutputStream(fileFullPath).use { fos ->
            EncodingUtil.addBomIfNeed(fos, charsetName)

            fos.bufferedWriter(EncodingUtil.resolveCharset(charsetName)).use {
                it.write(text)
            }
        }
    }

    //操作成功返回成功，否则返回失败
    //这里我用Unit?代表此函数不会返回有意义的值，只会返回null
    fun saveFileAndGetResult(fileFullPath:String, text:String, charsetName: String?):Ret<Unit?> {
        try {
            saveFile(fileFullPath, text, charsetName)
//            val retFileName = if(fileName.isEmpty()) getFileNameFromCanonicalPath(fileFullPath) else fileName
            return Ret.createSuccess(null)
        }catch (e:Exception) {
            MyLog.e(TAG, "#saveFileAndGetResult() err: "+e.stackTraceToString())
            return Ret.createError(null, "save file failed: ${e.localizedMessage}", Ret.ErrCode.saveFileErr)
        }
    }

    fun readFile(fileFullPath: String, charset:Charset? = null):String {
        val br = FileInputStream(fileFullPath).bufferedReader(charset ?: EncodingUtil.resolveCharset(
            EncodingUtil.detectEncoding(
                newInputStream = { FileInputStream(fileFullPath) }
            )
        ))

        br.use {
            return it.readText()
        }
    }

    fun getDocumentFileFromUri(context: Context, fileUri:Uri):DocumentFile? {
        return DocumentFile.fromSingleUri(context, fileUri)
    }

    fun getFileRealNameFromUri(context: Context?, fileUri: Uri?): String? {
        if (context == null || fileUri == null) return null
        val documentFile: DocumentFile = getDocumentFileFromUri(context, fileUri) ?: return null
        val name = documentFile.name
        return if(name.isNullOrEmpty()) null else name
    }

//    fun prepareSaveToIntent() {
//        val safIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//        safIntent.addFlags(
//            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//        )
//        startActivityForResult(safIntent, 1)
//    }

    fun recursiveExportFiles_Saf(
        contentResolver: ContentResolver,
        targetDir: DocumentFile,
        srcFiles: Array<File>,
        ignorePaths:List<String> = listOf(),
        canceled:()->Boolean = {false},
        conflictStrategy:CopyFileConflictStrategy = CopyFileConflictStrategy.RENAME,
    ) {
        if(canceled()) {
            throw CancellationException()
        }

        val filesUnderExportDir = targetDir.listFiles() ?: arrayOf<DocumentFile>()

        for(f in srcFiles) {
            if(canceled()) {
                throw CancellationException()
            }

            //可以用来实现忽略.git目录之类的逻辑，这忽略的是源目录的文件或文件夹
            if(ignorePaths.contains(f.canonicalPath)) {
                continue
            }

            var targetName = f.name

            val targetFileBeforeCreate = filesUnderExportDir.find { it.name == targetName }

            if(targetFileBeforeCreate != null) {  //文件已存在
                if(conflictStrategy == CopyFileConflictStrategy.SKIP) {
                    continue
                }else if(conflictStrategy == CopyFileConflictStrategy.OVERWRITE_FOLDER_AND_FILE) {
                    if(targetFileBeforeCreate.isDirectory) {   // 递归删除目录及其子文件
                        recursiveDeleteFiles_Saf(contentResolver, targetFileBeforeCreate, targetFileBeforeCreate.listFiles() ?: arrayOf<DocumentFile>(), canceled)
                    }else {  //删除文件
                        targetFileBeforeCreate.delete()
                    }
                }else if(conflictStrategy == CopyFileConflictStrategy.RENAME) {
                    targetName = getANonExistsName(targetName, exists = {newName -> filesUnderExportDir.find { it.name == newName } != null})
                }
            }

            if(f.isDirectory) {
                val nextTargetDir = targetDir.createDirectory(targetName)?:continue
                val nextSrcFiles = f.listFiles()?:continue
                if(nextSrcFiles.isNotEmpty()) {
                    recursiveExportFiles_Saf(
                        contentResolver = contentResolver,
                        targetDir = nextTargetDir,
                        srcFiles = nextSrcFiles,
                        ignorePaths = ignorePaths,
                        canceled = canceled,
                        conflictStrategy = conflictStrategy
                    )
                }
            }else {
                val targetFile = targetDir.createFile(binaryMimeType, targetName)?:continue

                val output = contentResolver.openOutputStream(targetFile.uri)?:continue

                f.inputStream().use { ins->
                    output.use { outs ->
                        ins.copyTo(outs)
                    }
                }
            }
        }

    }

    fun recursiveImportFiles_Saf(
        contentResolver: ContentResolver,
        targetDir: File,
        srcFiles: Array<DocumentFile>,
        canceled:()->Boolean = {false},
        conflictStrategy:CopyFileConflictStrategy = CopyFileConflictStrategy.RENAME,
    ) {
        if(canceled()) {
            throw CancellationException()
        }

        val filesUnderImportDir = targetDir.listFiles() ?: arrayOf<File>()

        for(f in srcFiles) {
            if(canceled()) {
                throw CancellationException()
            }

            var targetName = f.name ?: continue

            val targetFileBeforeCreate = filesUnderImportDir.find { it.name == targetName }

            if(targetFileBeforeCreate != null) {  //文件已存在
                if(conflictStrategy == CopyFileConflictStrategy.SKIP) {
                    continue
                }else if(conflictStrategy == CopyFileConflictStrategy.OVERWRITE_FOLDER_AND_FILE) {
                    targetFileBeforeCreate.deleteRecursively()
                }else if(conflictStrategy == CopyFileConflictStrategy.RENAME) {
                    targetName = getANonExistsName(targetName, exists = {newName -> filesUnderImportDir.find { it.name == newName } != null})
                }
            }

            val nextTarget = File(targetDir.canonicalPath, targetName)

            if(f.isDirectory) {
                nextTarget.mkdirs()
                val nextSrcFiles = f.listFiles() ?: continue
                if(nextSrcFiles.isNotEmpty()) {
                    recursiveImportFiles_Saf(
                        contentResolver = contentResolver,
                        targetDir = nextTarget,
                        srcFiles = nextSrcFiles,
                        canceled = canceled,
                        conflictStrategy = conflictStrategy
                    )
                }
            }else {
                nextTarget.createNewFile()

                val inputStream = contentResolver.openInputStream(f.uri)?:continue
                val outputStream = nextTarget.outputStream()
                inputStream.use { ins->
                    outputStream.use { outs ->
                        ins.copyTo(outs)
                    }
                }
            }
        }
    }

    private fun recursiveDeleteFiles_Saf(
        contentResolver: ContentResolver,
        targetDir: DocumentFile,
        filesUnderTargetDir: Array<DocumentFile>,
        canceled:()->Boolean,
    ) {
        if(canceled()) {
            throw CancellationException()
        }

        for(f in filesUnderTargetDir) {
            if(canceled()) {
                throw CancellationException()
            }

            if(f.isDirectory) {
                val nextTargetFiles = f.listFiles() ?: arrayOf<DocumentFile>()
                if(nextTargetFiles.isEmpty()) {  //目录下无文件，直接删
                    f.delete()
                }else {  //目录下有文件，递归删除
                    recursiveDeleteFiles_Saf(contentResolver, f, nextTargetFiles, canceled)
                }
            }else {
                f.delete()
            }
        }

        targetDir.delete()
    }

    //操作成功返回content和file的快照完整路径，否则，内容快照和文件快照，谁成功谁有路径，都不成功则都没路径但不会返回null，只是返回两个空字符串。
    //注：只有所有操作都成功才会返回成功，若返回成功但内容或文件的快照路径为空字符串，说明没请求备份对应的内容
    //如果两个请求创建备份的变量都传假，则此方法等同于单纯保存内容到targetFilePath对应的文件
    //返回值：1 保存内容到目标文件是否成功， 2 内容快照路径，若创建快照成功则非空字符串值，否则为空字符串， 3 文件快照路径，创建成功则非空字符串
    fun simpleSafeFastSave(
        context: Context,
        content: String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        targetFilePath: FilePath,
        requireBackupContent: Boolean,
        requireBackupFile: Boolean,
        contentSnapshotFlag: SnapshotFileFlag,
        fileSnapshotFlag: SnapshotFileFlag
    ): Ret<Triple<Boolean, String, String>> {
        var contentAndFileSnapshotPathPair = Pair("","")

        try {
            val targetFile = FuckSafFile(context, targetFilePath)

            //为内容创建快照
            val contentRet = if(requireBackupContent) {
                SnapshotUtil.createSnapshotByContentAndGetResult(
                    srcFileName = targetFile.name,
                    fileContent = content,
                    editorState = editorState,
                    trueUseContentFalseUseEditorState = trueUseContentFalseUseEditorState,
                    flag = contentSnapshotFlag
                )
            }else {
                Ret.createSuccess(null, "no require backup content yet")
            }

            val fileRet = if(requireBackupFile) {
                SnapshotUtil.createSnapshotByFileAndGetResult(targetFile, fileSnapshotFlag)
            } else {
                Ret.createSuccess(null, "no require backup file yet")
            }



            //检查快照是否创建成功
            if(contentRet.hasError() && fileRet.hasError()) {
                throw RuntimeException("save content and file snapshots err")
            }

            if(contentRet.hasError()) {
                contentAndFileSnapshotPathPair = Pair("", fileRet.data?.second?:"")
                throw RuntimeException("save content snapshot err")
            }

            if(fileRet.hasError()) {
                contentAndFileSnapshotPathPair = Pair(contentRet.data?.second?:"", "")
                throw RuntimeException("save file snapshot err")
            }



            //执行到这，说明内容快照和文件快照皆创建成功，开始写入内容到目标文件（要保存的文件，一般来说也是内容的源文件）
            contentAndFileSnapshotPathPair = Pair(contentRet.data?.second?:"", fileRet.data?.second?:"")


            //将内容写入到目标文件
            if(trueUseContentFalseUseEditorState) {
                val charsetName = editorState.codeEditor?.editorCharset?.value

                targetFile.outputStream().use { output ->
                    EncodingUtil.addBomIfNeed(output, charsetName)

                    output.bufferedWriter(EncodingUtil.resolveCharset(charsetName)).use { writer ->
                        writer.write(content!!)
                    }
                }
            }else {
                editorState!!.dumpLines(targetFile.outputStream())
            }

            //若请求备份content或file，则返回成功时有对应的快照路径，否则为空字符串。
            //只有保存文件成功(或者说所有操作都成功)，才会返回success，否则即使快照都保存成功，也会返回error
            val writeContentToTargetFileSuccess = true
            return Ret.createSuccess(Triple(writeContentToTargetFileSuccess, contentAndFileSnapshotPathPair.first, contentAndFileSnapshotPathPair.second))

        }catch (e:Exception) {
            MyLog.e(TAG, "#simpleSafeFastSave: err: "+e.stackTraceToString())
            //若返回错误，百分百保存文件失败或未保存，但快照可能有成功创建，需要检查对应path是否为空来判断
            val writeContentToTargetFileSuccess = false
            return Ret.createError(Triple(writeContentToTargetFileSuccess, contentAndFileSnapshotPathPair.first, contentAndFileSnapshotPathPair.second), "SSFS: save err: "+e.localizedMessage)
        }

    }


    fun getDoSaveForEditor(
        editorPageShowingFilePath: MutableState<FilePath>,
        editorPageLoadingOn: (String) -> Unit,
        editorPageLoadingOff: () -> Unit,
        activityContext: Context,
        editorPageIsSaving: MutableState<Boolean>,
        needRefreshEditorPage: MutableState<String>,
        editorPageTextEditorState: CustomStateSaveable<TextEditorState>,
        pageTag: String,
        editorPageIsEdited: MutableState<Boolean>,
        requestFromParent: MutableState<String>,
        editorPageFileDto: CustomStateSaveable<FileSimpleDto>,
        isSubPageMode:Boolean,
        isContentSnapshoted: MutableState<Boolean>,
        snapshotedFileInfo: CustomStateSaveable<FileSimpleDto>,  //用来粗略判断是否已创建文件的快照，之所以说是粗略判断是因为其只能保证打开一个文件后，再不更换文件的情况下不会重复创建同一文件的快照，但一切换文件就作废了，即使已经有某个文件的快照，还是会重新创建其快照
        lastSavedFieldsId: MutableState<String>,
    ): suspend () -> Unit {
        val doSave: suspend () -> Unit = doSave@{
            val funName ="doSave"  // for log

            //让页面知道正在保存文件
            editorPageIsSaving.value = true
            editorPageLoadingOn(activityContext.getString(R.string.saving))

            // here use Box is for reduce changes the code, because `Box.value` in code is same as `State.value`, is convenience to replace
            //   here expect get current state pointed instance, if get the value then assign to a variable, must remove all .value, so use a Box to avoid many changes
            // 这里使用box是为了避免以后直接用State.value还得多改代码，因为Box.value和State.value代码一样，方便替换
            //   这里期望的是获取当前状态的实例，直接替换常量比较符合直觉，但是还得逐个把.value删除，有点麻烦，所以用box简化修改
            val editorPageTextEditorState = Box(editorPageTextEditorState.value)

            try {
                // 先把filePath和content取出来
                val filePath = editorPageShowingFilePath.value
//                val fileContent = editorPageTextEditorState.value.getAllText()

                if (filePath.isEmpty()) {
                    //path为空content不为空的可能性不大，几乎没有
                    if(editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value ) {
                        MyLog.w(pageTag, "#$funName: filePath is empty, but content is not empty, will create content snapshot with a random filename...")
                        val flag = SnapshotFileFlag.editor_content_FilePathEmptyWhenSave_Backup
                        val contentSnapRet = SnapshotUtil.createSnapshotByContentWithRandomFileName(
                            fileContent = null,
                            editorState = editorPageTextEditorState.value,
                            trueUseContentFalseUseEditorState = false,
                            flag = flag
                        )
                        if (contentSnapRet.hasError()) {
                            MyLog.e(pageTag, "#$funName: create content snapshot for empty path failed:" + contentSnapRet.msg)

                            throw RuntimeException("path is empty, and save content snapshot err")
                        }else {
                            isContentSnapshoted.value=true
                            throw RuntimeException("path is empty, but save content snapshot success")
                        }

                    }

                    throw RuntimeException("path is empty!")
                }




//                changeStateTriggerRefreshPage(needRefreshEditorPage)
//            delay(10*1000)  //测试能否显示Saving...，期望能，结果能，测试通过
//        if(debugModeOn) {
//            println("editorPageTextEditorState.getStateVal="+editorPageTextEditorState.getStateVal())
//            println("editorPageTextEditorState.value.getAllText()="+editorPageTextEditorState.value.getAllText())
//        }
                //保存文件
//            println("before getAllText:"+ getSecFromTime())

                //保存前检查文件是否修改过，如果修改过，对源文件创建快照再保存
                val targetFile = filePath.toFuckSafFile(activityContext)
                // 如果要保存的那个文件已经不存在（比如被删），就不检查其是否被外部修改过了，下面直接保存即可，保存的时候会自动创建文件
                if(targetFile.exists()) {
                    //文件存在，检查是否修改过，如果修改过，创建快照，如果创建快照失败，为当前显示的内容创建快照
                    val newDto = FileSimpleDto.genByFile(targetFile)
                    //这里没必要确保dto和newDto的路径一样，创建快照的条件要宽松一些，哪怕多创建几个也比少创建几个强。（这里后面的fullPath判断其实有点多余，这里代表当前正在显示的文件读取时的初始dto，路径应和newDto的始终一致，这个dto用来判断是否重载，作为判断是否已经创建快照的dto，要不然创建完快照一更新它，再进editor的初始化代码块时，会错误认为当前显示的文件已经是最新，而不重新加载文件）
                    //判断文件是否被外部修改过，如果修改过，则进一步判断当前已创建的快照是否和修改过的文件匹配，若不匹配则创建快照
                    if(newDto != editorPageFileDto.value) { // editor正在编辑的文件被外部修改过
                        //判断已创建快照的文件信息是否和目前硬盘上的文件信息一致，注意最后一个条件判断fullPath不相同也创建快照，在这判断的话就无需在外部更新dto信息了，直接路径不一样，创建快照，更新文件信息（包含路径）就行了，而且当路径不匹配时newDto所代表的文件是save to 的对象，其内容将被覆盖，理应创建快照
                        if(newDto != snapshotedFileInfo.value) {  //未存被外部修改过的文件快照
                            MyLog.w(pageTag, "#$funName: warn! file maybe modified by external! will create a snapshot before save...")
                            val snapRet = SnapshotUtil.createSnapshotByFileAndGetResult(targetFile, SnapshotFileFlag.editor_file_BeforeSave)
                            //连读取文件都不行，直接不保存，用户爱怎么办怎么办吧
                            //如果出错，保存content到快照，然后返回
                            //创建源文件快照出错不覆盖文件的原因：如果里面有东西，而且由于正常的原因不能创建快照，那就先不动那个文件，这样的话里面的数据不会丢，加上我在下面为content创建了快照，这样两份内容就都不会丢，比覆盖强，万一一覆盖，成功了但导致数据损失就不好了
                            if (snapRet.hasError()) {
                                //上面的调用里以及记日志了，所以这里提示用户即可
//                            Msg.requireShowLongDuration()
                                MyLog.e(pageTag, "#$funName: create file snapshot for '$filePath' failed:" + snapRet.msg)

                                //虽然为源文件创建快照失败了，但如果没有为当前内容创建快照，则为当前用户编辑的内容(content)创建个快照
                                if(editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value) {
                                    val contentSnapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                                        srcFileName = targetFile.name,
                                        fileContent = null,
                                        editorState = editorPageTextEditorState.value,
                                        trueUseContentFalseUseEditorState = false,
                                        flag = SnapshotFileFlag.editor_content_CreateSnapshotForExternalModifiedFileErrFallback
                                    )
                                    if (contentSnapRet.hasError()) {
                                        MyLog.e(pageTag, "#$funName: create content snapshot for '$filePath' failed:" + contentSnapRet.msg)

                                        throw RuntimeException("save origin file and content snapshots err")
                                    }else {
                                        isContentSnapshoted.value=true
                                        throw RuntimeException("save origin file snapshot err, but save content snapshot success")
                                    }

                                }else {
                                    //如果备份文件失败但内容快照已创建
                                    throw RuntimeException("save origin file snapshot err, and content is empty or snapshot already exists")
                                }


                            }else { //创建文件快照成功
                                // 更新已创建快照的文件信息
                                snapshotedFileInfo.value = newDto
                            }

                        }else {
                            MyLog.d(pageTag, "#$funName: file snapshot of '$filePath' already exists")
                        }

                        //创建快照成功，下面可以放心保存content到源文件了
                    }

                }

//            println("after getAllText, before save:"+ getSecFromTime())
//                val ret = FsUtils.saveFileAndGetResult(filePath, fileContent)

                // 保存文件。
                val ret = FsUtils.simpleSafeFastSave(
                    context = activityContext,
                    content = null,
                    editorState = editorPageTextEditorState.value,
                    trueUseContentFalseUseEditorState = false,
                    targetFilePath = filePath,
                    requireBackupContent = true,
                    requireBackupFile = true,
                    contentSnapshotFlag = SnapshotFileFlag.editor_content_NormalDoSave,
                    fileSnapshotFlag = SnapshotFileFlag.editor_file_NormalDoSave
                )
//            println("after save:"+ getSecFromTime())

                //判断content快照是否成功创建，如果成功创建，更新相关变量，那样即使保存出错也不会执行后面创建内容快照的步骤了
                val (_, contentSnapshotPath, _) = ret.data
                if(contentSnapshotPath.isNotEmpty()) {  //创建内容快照成功
                    isContentSnapshoted.value=true
                }

                //注：不用更新snapshotedFileInfo，因为保存时创建的快照是修改前的文件快照，新文件必然没创建快照(不过其content已创建快照)，所以，保持snapshotedFileInfo为过期状态即可，这样下次若有必要，就会为文件创建新快照了

                //如果保存失败，且内容不为空，创建文件快照
                if (ret.hasError()) {
                    //显示提示
//                    Msg.requireShowLongDuration(ret.msg)
                    MyLog.e(pageTag, "#$funName: save file '$filePath' failed:" + ret.msg)

                    //保存失败但content不为空且之前没创建过这个content的快照，则创建content快照 (ps: content就是编辑器中的未保存的内容)
                    if (editorPageTextEditorState.value.contentIsEmpty().not() && !isContentSnapshoted.value) {
                        val snapRet = SnapshotUtil.createSnapshotByContentAndGetResult(
                            srcFileName = targetFile.name,
                            fileContent = null,
                            editorState = editorPageTextEditorState.value,
                            trueUseContentFalseUseEditorState = false,
                            flag = SnapshotFileFlag.editor_content_SaveErrFallback
                        )
                        if (snapRet.hasError()) {
                            MyLog.e(pageTag, "#$funName: save content snapshot for '$filePath' failed:" + snapRet.msg)

                            throw RuntimeException("save file and content snapshots err")
                        }else {
                            isContentSnapshoted.value=true
                            throw RuntimeException("save file err, but save content snapshot success")
                        }

                    }else {
                        MyLog.w(pageTag, "#$funName: save file failed, but content is empty or already snapshoted, so will not create snapshot for it")
                        throw RuntimeException(ret.msg)
                    }

                    //如果保存失败，isEdited仍然启用，这样就可再按保存按钮
//                    editorPageIsEdited.value = true

                } else {  //保存成功
//                    保存成功，且非子页面模式，更新下dto（子页面一律强制重载请求打开的文件，无需判断文件是否修改，而dto是用来判断文件是否修改的，因此对子页面来说，更新dto无意义）
//                    if(!isSubPageMode) {
//                    FileSimpleDto.updateDto(editorPageFileDto.value, File(filePath))

                    //执行到这里，targetFile已经修改过了，但我不确定targetFile能否获取到filePath对应的文件修改后的属性，所以新建个File对象
                    //这个是重载dto和更新快照的dto无关，所以即使创建快照成功且更新了快照dto，这个dto也依然要更新（作用好像重复了？）
                    editorPageFileDto.value = FileSimpleDto.genByFile(FuckSafFile(activityContext, filePath))
//                    }

                    //如果保存成功，将isEdited设置为假
                    editorPageIsEdited.value = false

                    lastSavedFieldsId.value = editorPageTextEditorState.value.fieldsId

                    //提示保存成功
                    Msg.requireShow(activityContext.getString(R.string.file_saved))
                }

//        requireShowLoadingDialog.value= false
//                editorPageIsSaving.value = false

                //保存文件后不需要重新加载文件
//                requestFromParent.value=PageRequest.needNotReloadFile

                //关闭loading
//                editorPageLoadingOff()
//                changeStateTriggerRefreshPage(needRefreshEditorPage)  //loadingOff里有刷新，所以这就不需要了


            }catch (e:Exception){

                editorPageIsEdited.value=true  //如果出异常，把isEdited设为true，这样保存按钮会重新启用，用户可再次保存
//                Msg.requireShowLongDuration(""+e.localizedMessage)  //这里不需要显示错误，外部coroutine会显示

                throw e
            }finally {
                //无论是否出异常，都把isSaving设为假，告知外部本次保存操作已执行完毕
                editorPageIsSaving.value = false

                //即使出错也会关闭Loading，不然那个正在保存的遮罩盖着，用户什么都干不了。至于异常？直接抛给调用者即可
                editorPageLoadingOff()

            }
        }
        return doSave
    }

    //删除最后修改时间超过指定天数的快照文件，注意是最后修改时间，不要按创建时间删，要不万一用户就爱编辑快照目录的文件，我给他删了，就不好了
    //folderDesc描述folder类型，为可选参数，例如folder为快照目录，则期望的folderDesc为"snapshot folder"，记日志的时候会用到此参数，理论上，不传也行，但建议传(强制！)
    fun delFilesOverKeepInDays(keepInDays: Int, folder: File, folderDesc:String) {
        val funName = "delFilesOverKeepInDays"
        try {
            MyLog.w(TAG, "#$funName: start: del expired files for '$folderDesc'")

            //把天数转换成毫秒 (当然，也可把毫秒转换成天，但是，做乘法精度比除法高，除法还有可能有余数之类的，算起来又麻烦，所以，这里用乘法)
            val keepInDaysInMillSec = keepInDays*24*60*60*1000L
            //取出UTC时区(或GMT) 1970-01-01到现在的毫秒
            val currentTimeInMillSec = System.currentTimeMillis()

            //返回true的文件将被删除
            val predicate = predicate@{f:File ->
                if(!f.isFile) {
                    return@predicate false
                }
                //文件最后修改时间，起始时间1970-01-01, 时区GMT(UTC)。（ps：最后修改时间单位默认竟然是毫秒？我一直以为是秒，不过，应该取决于平台，例如在linux上，可能记的就是秒，我也不确定）
                val lastModTimeInMillSec = f.lastModified()
                val diffInMillSec = currentTimeInMillSec - lastModTimeInMillSec
                return@predicate diffInMillSec > keepInDaysInMillSec
            }

            //执行删除
            val successDeletedCount = delFilesByPredicate(predicate, folder, folderDesc)
            MyLog.w(TAG, "#$funName: end: del expired files for '$folderDesc' done, success deleted: $successDeletedCount")

        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: del expired files for '$folderDesc' err: ${e.stackTraceToString()}")
        }
    }

    //根据predicate 删除文件，返回值为成功删除的文件数
    fun delFilesByPredicate(predicate:(File)->Boolean, folder: File, folderDesc:String):Int {
        val funName = "delFilesByPredicate"
        var successDeleteFilesCount = 0  //成功删除的文件计数

        try {
            MyLog.w(TAG, "#$funName: checking '$folderDesc' is ready for delete files or not")

            if(!folder.exists()) {
                MyLog.w(TAG, "#$funName: '$folderDesc' doesn't exist yet, operation abort")
                return successDeleteFilesCount
            }

            val files = folder.listFiles()
            if(files==null) {
                MyLog.w(TAG, "#$funName: list files for '$folderDesc' returned null, operation abort")
                return successDeleteFilesCount
            }
            if(files.isEmpty()) {
                MyLog.w(TAG, "#$funName: '$folderDesc' is empty, operation abort")
                return successDeleteFilesCount
            }

            MyLog.w(TAG, "#$funName: '$folderDesc' passed check, will start del files for it")

            for(f in files){
                try {
                    if(predicate(f)){
                        f.delete()
                        successDeleteFilesCount++
                    }
                }catch (e:Exception) {
                    MyLog.e(TAG, "#$funName: del file '${f.name}' for $folderDesc err: "+e.stackTraceToString())
                }
            }

            if(successDeleteFilesCount==0) {
                MyLog.w(TAG, "#$funName: no file need del in '$folderDesc'")
            }else {
                MyLog.w(TAG, "#$funName: deleted $successDeleteFilesCount file(s) for '$folderDesc'")
            }

            return successDeleteFilesCount
        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName: del files for '$folderDesc' err: "+e.stackTraceToString())
            return successDeleteFilesCount
        }
    }

    /**
     * default disable edit files under these folders, because app may change it when running
     * 判断路径是否处于app内置的禁止编辑的文件夹中
     * 考虑：要不要在设置页面添加一个开关允许编辑这些路径下的文件？必要性不大，如果非编辑，可在内置editor手动关闭read-only或通过外部编辑器编辑，不过需要注意：如果用内部编辑器编辑有可能和app隐式更新这些文件发生冲突导致文件被错误覆盖。
     */
    @Deprecated("没必要检查是否只读，无所谓，用户编辑这些目录下的文件也不是不行，而且每次我打开这些目录下的文件，第一件事就是取消只读，不然光标无法显示，搜索后因为jetpackcompose限制难以同时实现关键字高亮和文字可编辑，搞得查找文字后就算找到了我也不知道关键字在哪，而且也没法用光标辅助定位上次我查看的位置，总之就是很恶心")
    fun deprecated_isReadOnlyDir(path: String): Boolean {
        return try {
            //app内置某些文件默认不允许编辑，因为app会在运行时编辑这些文件，有可能冲突或覆盖app自动生成的内容
            path.startsWith(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    || path.startsWith(AppModel.getOrCreateEditCacheDir().canonicalPath)
                    || path.startsWith(AppModel.getOrCreateLogDir().canonicalPath)
                    || path.startsWith(AppModel.certBundleDir.canonicalPath)
                    || path.startsWith(AppModel.getOrCreateSettingsDir().canonicalPath)
                    || path.startsWith(AppModel.getOrCreateSubmoduleDotGitBackupDir().canonicalPath)
                    || path.startsWith(Lg2HomeUtils.getLg2Home().canonicalPath)
        }catch (e:Exception) {
            MyLog.e(TAG, "#isReadOnlyDir err: ${e.stackTraceToString()}")
            false
        }

    }

    /**
     * 递归计算文件夹或文件的大小，结果会累加到itemsSize中，请在调用前自行重置其值为0
     */
    fun calculateFolderSize(fileOrFolder: File, itemsSize: MutableLongState) {
        if(fileOrFolder.isDirectory) {
            val list = fileOrFolder.listFiles()

            if(!list.isNullOrEmpty()) {
                list.forEachBetter {
                    calculateFolderSize(it, itemsSize)
                }
            }
        }else {  // file
            itemsSize.longValue += fileOrFolder.length()
        }
    }

    /**
     * ceiling path of app, if reached those path in Files explorer, should show 'press back again to exit' rather than go to parent dir
     *
     * note: the externalFilesDir "/storage/emulated/0/Android/data/your_package_name" should not in this list,
     *   cause it actually is subdir of external storage dir "/storage/emulated/0", 不加进列表的话，切换目录方便，可直接从App的外部data目录返回到外部存储目录
     */
    fun getAppCeilingPaths():List<String> {
        return listOf(
            // usually is "/storage/emulated/0"，这个cover了 "外部存储/Android/data/包名" 那个目录
            getExternalStorageRootPathNoEndsWithSeparator(),
            // "/data/data/app.package.name"
            AppModel.innerDataDir.canonicalPath,
            // "/"，兜底
            rootPath
        )
    }

    /**
     * @return root path of app internal storage
     */
    fun getInternalStorageRootPathNoEndsWithSeparator():String {
        return AppModel.allRepoParentDir.canonicalPath
    }

    fun getAppDataRootPathNoEndsWithSeparator():String {
        return AppModel.appDataUnderAllReposDir.canonicalPath
    }

    /**
     * @return "/storage/emulated/0" or "" if has exception
     *
     */
    fun getExternalStorageRootPathNoEndsWithSeparator():String{
        return try {
            Environment.getExternalStorageDirectory().canonicalPath
        }catch (_:Exception) {
            ""
        }
    }

    fun getInnerStorageRootPathNoEndsWithSeparator():String{
        return try {
            AppModel.innerDataDir.canonicalPath
        }catch (_:Exception) {
            ""
        }
    }

    /**
     * 返回的不一定是真实路径，例如 termux用这种方式解析路径会出现两个/ 例如 "/storage/emulated/0/tree//data/data/com.termux/files/home/Repo_export"，这是因为termux在路径中间加了两个/，懒得处理了，反正不支持使用saf克隆仓库
     * maybe returned is not real path, no promise
     *
     * @return eg. /storage/emulated/0/folder1/folder2
     */
    @Deprecated("不靠谱，尽量不要用")
    fun getRealPathFromUri(uri:Uri):String {
        return try {
            //直接用 !! 的话有可能err msg为空，所以这里手动创建个空指针异常
            // 如果是系统的文件管理器的uri，那么uri.path一般是解码过的
            val uriPathString = uri.path ?: throw NullPointerException("`uri.path` is null")

            // 例如：uri为：content://com.android.externalstorage.documents/tree/primary%3ARepos, 则uri.path为 /tree/primary:Repos，但不保证一定是canonical path，可能中间出现两个 "//" 也可能有其他东西
            //这两个trim都有意义，尤其最后的trimEnd，当uriPathString为空字符串时，可去掉中间拼接的slash
            (getExternalStorageRootPathNoEndsWithSeparator() + Cons.slash + uriPathString.let{ it.substring(it.indexOf(":") + 1).trim(Cons.slashChar) }).trimEnd(Cons.slashChar)
        }catch (_:Exception) {
            ""
        }
    }

    /**
     * 如果是 `internalPathPrefix` 或 `externalPathPrefix` 开头的路径，转换为真实路径，否则返回原路径
     */
    fun internalExternalPrefixPathToRealPath(path:String):String {
        return if(path.startsWith(appDataPathPrefix)) {
            (getAppDataRootPathNoEndsWithSeparator() + Cons.slash + removeAppDataPrefix(path)).trimEnd(Cons.slashChar)
        }else if(path.startsWith(internalPathPrefix)) {
            (getInternalStorageRootPathNoEndsWithSeparator() + Cons.slash + removeInternalStoragePrefix(path)).trimEnd(Cons.slashChar)
        }else if(path.startsWith(externalPathPrefix)) {
            (getExternalStorageRootPathNoEndsWithSeparator() + Cons.slash + removeExternalStoragePrefix(path)).trimEnd(Cons.slashChar)
        }else if(path.startsWith(innerPathPrefix)) {
            (getInnerStorageRootPathNoEndsWithSeparator() + Cons.slash + removeInnerStoragePrefix(path)).trimEnd(Cons.slashChar)
        }else {
            path
        }
    }

    fun userInputPathToCanonical(path: String):Ret<String?> {
        try{
            val path = internalExternalPrefixPathToRealPath(trimLineBreak(path))
            if(path.isBlank()) {
                throw RuntimeException("invalid path")
            }

            return Ret.createSuccess(data = File(path).canonicalPath)
        }catch (e:Exception) {
            return Ret.createError(data = null, errMsg = "err: ${e.localizedMessage}", exception = e)
        }
    }

    @Deprecated("this too complex, only trim line break then use `File(path).cononicalPath` is enough")
    fun trimPath(path:String, appendEndSlash:Boolean = false):String {
        //先把首尾的换行符移除
        val path = trimLineBreak(path)

        return if(path.length == 1) { //移除换行符后只剩一个字符了，直接返回
            path
        }else {  //移除末尾的 '/'
            val slash = '/'
            val pathStartsWithSlash = path.startsWith(slash)

            //移除末尾的'/'
            val path = path.trimEnd(slash)

            //如果原字符串以'/'开头，那trimEnd('/')之后就啥都没了，这时返回一个'/'；否则返回移除末尾'/'之后的字符串
            if(pathStartsWithSlash && path.isEmpty()) {
                slash.toString()
            }else {
                //如果字符串以'/'开头，为避免以多个连续'/'开头，移除开头的所有'/'然后替换成一个'/'，最后返回；若不以'/'开头，直接返回
                val pathWithoutEndSlash = if(pathStartsWithSlash) {
                    "$slash${path.trimStart(slash)}"
                }else {
                    path
                }

                //如果期望末尾加/就加上
                if(appendEndSlash) {
                    "$pathWithoutEndSlash$slash"
                }else {
                    pathWithoutEndSlash
                }
            }
        }
    }

    /**
     * @return eg. input parent="/abc/def", fullPath="/abc/def/123", will return "/123"; if fullPath not starts with parent, will return origin `fullPath`
     */
    fun getPathAfterParent(parent: String, fullPath: String): String {
        return fullPath.removePrefix(parent)
    }

    /**
     * eg: fullPath = /storage/emulated/0/repos/abc, return "External:/abc"
     * eg (wrong path): fullPath = /storage/emulated/0//repos/abc, return "External://repos/abc" ("External:/" + "/repos/abc")
     * eg: fullPath = /storage/emulated/0/Android/path-to-app-internal-repos-folder/abc, return "Internal:/abc"
     * eg: fullPath = /data/data/app.package.name/files/abc, return origin path
     * eg (matched internal/external storage prefix): fullPath = /storage/emulated/0, return "External:/"
     */
    fun getPathWithInternalOrExternalPrefix(fullPath:String) :String {
        val appDataRoot = getAppDataRootPathNoEndsWithSeparator()
        val internalStorageRoot = getInternalStorageRootPathNoEndsWithSeparator()
        val externalStorageRoot = getExternalStorageRootPathNoEndsWithSeparator()
        val innerStorageRoot = getInnerStorageRootPathNoEndsWithSeparator()

        return if(fullPath.startsWith(appDataRoot)) {  // internal storage must before external storage, because internal storage actually under external storage (eg: internal is "/storage/emulated/0/Android/data/packagename/xxx/xxxx/x", external is "/storage/emulated/0")
            appDataPathPrefix+((getPathAfterParent(parent= appDataRoot, fullPath=fullPath)).removePrefix("/"))
        }else if(fullPath.startsWith(internalStorageRoot)) {  // internal storage must before external storage, because internal storage actually under external storage (eg: internal is "/storage/emulated/0/Android/data/packagename/xxx/xxxx/x", external is "/storage/emulated/0")
            internalPathPrefix+((getPathAfterParent(parent= internalStorageRoot, fullPath=fullPath)).removePrefix("/"))
        }else if(fullPath.startsWith(externalStorageRoot)) {
            externalPathPrefix+((getPathAfterParent(parent= externalStorageRoot, fullPath=fullPath)).removePrefix("/"))
        }else if(fullPath.startsWith(innerStorageRoot)) {
            innerPathPrefix+((getPathAfterParent(parent= innerStorageRoot, fullPath=fullPath)).removePrefix("/"))
        }else { //原样返回, no matched, return origin path
            fullPath
        }
    }

    fun removeInternalStoragePrefix(path: String): String {
        return path.removePrefix(internalPathPrefix)
    }

    fun removeAppDataPrefix(path: String): String {
        return path.removePrefix(appDataPathPrefix)
    }

    fun removeExternalStoragePrefix(path: String): String {
        return path.removePrefix(externalPathPrefix)
    }

    fun removeInnerStoragePrefix(path: String): String {
        return path.removePrefix(innerPathPrefix)
    }

    fun stringToLines(string: String):List<String> {
        return string.lines()
    }

    /**
     * 替换多个行到指定行号，若源文件无EOFNL，执行完此函数后，会添加
     * replace lines to specified line number, if origin file no EOFNL, after this function, will add it
     *
     * note: if target line doesn't exist, will append content to the EOF, and delete/append content is not available for EOF
     *
     * @param file the file which would be replaced
     * @param startLineNum the line number start from 1 or -1 represent EOF, when you want to append content to the end of file, use -1 or a line number which bigger than the file actually lines is ok
     * @param startLineNum 行号可以是大于1的值或-1，如果想追加内容到文件末尾，可传-1或大于文件实际行号的值
     * @param newLines the lines will replace
     * @param trueInsertFalseReplaceNullDelete true insert , false replace, null delete
     */
    private suspend fun replaceOrInsertOrDeleteLinesToFile(
        file: FuckSafFile,
        startLineNum: Int,
        newLines: List<String>,
        trueInsertFalseReplaceNullDelete:Boolean?,
        settings: AppSettings,
        charsetName: String = Constants.CHARSET_UTF_8
    ) {
        if(trueInsertFalseReplaceNullDelete != null && newLines.isEmpty()) {
            return
        }

        if(startLineNum<1 && startLineNum!=LineNum.EOF.LINE_NUM) {
            throw RuntimeException("invalid line num")
        }

        if(file.exists().not()) {
            throw RuntimeException("target file doesn't exist")
        }

        if(settings.diff.createSnapShotForOriginFileBeforeSave) {
            val snapRet = SnapshotUtil.createSnapshotByFileAndGetResult(file, SnapshotFileFlag.diff_file_BeforeSave)
            if(snapRet.hasError()) {
                //其实加不加括号都行 ?: 优先级高于throw
                throw (snapRet.exception ?: RuntimeException(snapRet.msg.ifBlank { "err: create snapshot failed" }))
            }
        }


        val tempFile = FuckSafFile.fromFile(createTempFile("${TempFileFlag.FROM_DIFF_SCREEN_REPLACE_LINES_TO_FILE.flag}-${file.name}"))
        var found = false

        val lineBreak = file.detectLineBreak(charsetName).value

        file.bufferedReader(charsetName).use { reader ->
            tempFile.bufferedWriter(charsetName).use { writer ->
                var currentLine = 1

                while(true) {
                    val line = reader.readLine() ?:break
                    // note: if target line num is EOF, this if never execute, but will append content to the EOF of target file after looped
                    // 注意：如果目标行号是EOF，if里的代码永远不会被执行，然后会追加内容到目标文件的末尾
                    if (currentLine++ == startLineNum) {
                        found = true

                        // delete line
                        if(trueInsertFalseReplaceNullDelete == null) {
                            continue
                        }

                        for(i in newLines.indices) {
                            writer.write(newLines[i])
                            writer.write(lineBreak)
                        }

                        // prepend(insert) line
                        if(trueInsertFalseReplaceNullDelete == true) {
                            writer.write(line)
                            writer.write(lineBreak)
                        }
                    }else {  // not match
                        writer.write(line)
                        writer.write(lineBreak)
                    }
                }

                // not found and not delete mode, append line to the end of file
                if (found.not() && trueInsertFalseReplaceNullDelete!=null) {
                    for(i in newLines.indices) {
                        writer.write(newLines[i])
                        writer.write(lineBreak)
                    }
                }
            }
        }

        // if content updated, replace the origin file
        if(found.not() && trueInsertFalseReplaceNullDelete==null){  // no update, remove temp file
            tempFile.delete()
        }else {  // updated, move temp file to origin file, temp file will delete after moved
            tempFile.renameTo(file)
        }
    }

    suspend fun replaceLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, startLineNum, newLines, trueInsertFalseReplaceNullDelete = false, settings)
    }

    suspend fun insertLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        prependLinesToFile(file, startLineNum, newLines, settings)
    }

    suspend fun prependLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, startLineNum, newLines, trueInsertFalseReplaceNullDelete = true, settings)
    }

    suspend fun appendLinesToFile(file: FuckSafFile, startLineNum: Int, newLines: List<String>, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, startLineNum+1, newLines, trueInsertFalseReplaceNullDelete = true, settings)
    }

    suspend fun deleteLineToFile(file: FuckSafFile, lineNum: Int, settings: AppSettings) {
        replaceOrInsertOrDeleteLinesToFile(file, lineNum, newLines=listOf(), trueInsertFalseReplaceNullDelete = null, settings)
    }

    fun createTempFile(prefix:String, suffix:String=".tmp"):File{
        return File(AppModel.getOrCreateExternalCacheDir().canonicalPath, "$prefix-${generateRandomString(8)}$suffix")
    }

    /**
     * 读取文件中的行到list，
     * 支持 \r 或 \n 或 \r\n 分割的文件，
     * 正常情况下，一个文件只有一种类型的换行符，用这个函数没问题，如果是个混用各种换行符的奇葩文件，行数会乱。
     *
     * @param addNewLineIfFileEmpty if true, when file is empty, will add a empty str as element to list,
     *  else will return an empty list. set it to true, if you expect this function has same behavior with `String.lines()`
     *  (如果为true，文件为空时返回只有一个空字符串元素的list，否则返回空list。如果期望此函数和`String.lines()`行为一致（空字符串返回元素1的list），此值应传true。)
     */
    fun readLinesFromFile(
        file: FuckSafFile,
        charsetName: String?,
        addNewLineIfFileEmpty:Boolean = true,
    ): List<String> {
//        val lines = ArrayList<String>(30)  //不确定用户打开的文件到底多少行啊，算了，用默认吧
        //readLines() api 说不能用于 huge files?我看源代码好像也是用readLine一行行读的，我自己写好像差不多，不过不会创建迭代器之类的，可能稍微快一点点，但应该也不能用于huge files吧？大概
//        File(filePath).bufferedReader().readLines()

        //根据文件大小估算一下行数，没任何考证，我随便打开了几个文件，感觉file.length / 1000 * 7差不多就是行数，而直接右移7位和/1000*7结果差不多，所以就这么写了，shr是有符号右移，如果是负数，移完还是负数，不看符号的话，值如果不超Int最大值，无符号和有符号右移的区别就在于符号不同，值一般是一样的
        val estimateLineCount = (file.length() shr 7).toInt().coerceAtLeast(10).coerceAtMost(10000) // chatgpt说10000个空Object对象大概470k，不会超过1mb，可以接受，不过java创建的数组默认值不是null吗？所以可能连470k都占不了
//        val estimateLineCount = (file.length() / 1000 * 7).toInt().coerceAtLeast(10).coerceAtMost(10000)

        val lines = ArrayList<String>(estimateLineCount)  //默认是ArrayList，容量10
        val arrBuf = CharArray(4096)
        val aline = StringBuilder(100)
        var lastChar:Char? = null
        file.bufferedReader(charsetName).use { reader ->
            while (true) {
                val readSize = reader.read(arrBuf)
                if(readSize == -1) {
                    break
                }

                for (i in 0 until readSize) {
                    val char = arrBuf[i]
                    // handle the '\n' or '\r' or "\r\n"
                    if (char == '\n'){
                        if(lastChar != '\r') {
                            lines.add(aline.toString())
                            aline.clear()
                        }
                    } else if(char == '\r') {
                        lines.add(aline.toString())
                        aline.clear()
                    } else {
                        aline.append(char)
                    }

                    lastChar = char
                }
            }
        }

        if(aline.isNotEmpty()) {
            // last line no line break, means end of the file no new line(文件末尾没换行符）
            lines.add(aline.toString())

        // aline若为空有两种情况，1文件为空，压根没读；2读了，末尾是换行符，所以aline被清空。而lastChar若不等于null，说明至少读了一个字符，所以文件不为空，因此，aline为空且lastChar不为null就隐含文件末尾是换行符
        //其实这里用lastChar != null或判断lastChar==\n或\r都行，
        // 因为 这里隐含条件aline为空，而 `aline.isEmpty() && lastChar!=null` ，
        // 意味着文件非空(所以lastChar!=null)且最后一个字符为换行符(所以aline被清空)，但这样写不太直观，一看代码容易困惑
//        }else if(lastChar == '\n' || lastChar == '\r') { // or else if(lastChar != null)
        }else if(lastChar != null) {
            // last line, has line break
            lines.add("")
        }


        //因为用 空字符串.lines() 会返回size 1的集合，所以，若文件内容为空，也应如此
        if (addNewLineIfFileEmpty && lines.isEmpty()) {
            lines.add("")
        }

        return lines
    }




    fun isNotRelativePath(path: String):Boolean {
        return path.startsWith("https://") || path.startsWith("http://")
                || path.startsWith("content://") || path.startsWith("file://")
                || path.startsWith("/") || path.startsWith("ftp://") || path.startsWith("mailto:")
    }

    fun maybeIsRelativePath(path: String) :Boolean {
        return isNotRelativePath(path).not()
    }

    /**
     * if is relative path, return absolute path based on base path, else return raw path
     * @param basePathNoEndSlash 之前用的拼接，所以期望没有'/'，但现在改用files了，所以其实末尾有无slash无所谓了
     */
    fun getAbsolutePathIfIsRelative(path:String, basePathNoEndSlash: String):String {
        if(maybeIsRelativePath(path)) {
            return File(basePathNoEndSlash, path).canonicalPath
        }

        return path
    }

    fun makeThePathCanonical(path: String): String {
        if(path.isBlank()) {
            return ""
        }

        return File(path).canonicalPath
    }



    fun copy(inputStream:InputStream, outputStream:OutputStream) {
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    fun appendTextToFile(file: File, text:String, charset: Charset = StandardCharsets.UTF_8) {
        if(text.isEmpty()) {
            return
        }

        val append = true
        val filerWriter = OutputStreamWriter(FileOutputStream(file, append), charset)
        filerWriter.buffered().use { writer ->
            writer.write(text)
        }
    }

    fun readShortContent(
        file: FuckSafFile,
        charsetName: String? = null,
        contentCharsLimit:Int = 80
    ):String {
        return try {
            val sb = StringBuilder()

            file.bufferedReader(charsetName ?: file.detectEncoding()).use { br ->
                while (true) {
                    if(sb.length >= contentCharsLimit) {
                        break
                    }

                    val line = br.readLine() ?: break

                    line.trim().let {
                        if(it.isNotBlank()) {
                            sb.appendLine(it)
                        }
                    }
                }

            }

            if(sb.length <= contentCharsLimit) {
                sb.toString()
            }else {
                sb.substring(0, contentCharsLimit)
            }
        }catch (e: Exception) {
            MyLog.d(TAG, "readShortContent of file err: fileIoPath=${file.path.ioPath}, err=${e.localizedMessage}")
            ""
        }
    }


    fun getPathWithInternalOrExternalPrefixAndRemoveFileNameAndEndSlash(path:String, fileName:String):String {
        // don't handle the content uri, the file name may be encoded or even haven't a file name,
        //  try add storage prefix to it may return a wrong path
        return if(path.startsWith(contentUriPathPrefix)) {
            path
        }else {
            getPathWithInternalOrExternalPrefix(path.removeSuffix(fileName).trimEnd(Cons.slashChar)).ifBlank { Cons.slash }
        }
    }

    fun translateContentUriToRealPath(uri: Uri, appContext:Context = AppModel.realAppContext, mode: String = "rw"): String? {
        val funName = "translateContentUriToRealPath"

        try {
            val resolver = appContext.contentResolver
            MyLog.d(TAG, "#$funName: Resolving content URI: $uri")

            resolver.openFileDescriptor(uri, mode)?.use { pfd ->
                // See if we can skip the indirection and read the real file directly
                val path = findRealPath(pfd.fd)
                if (path != null) {
                    MyLog.d(TAG, "#$funName: Found real file path: $path")
                    return path
                }
            }
        } catch(e: Exception) {
            MyLog.w(TAG, "#$funName: Failed to open content fd: ${e.localizedMessage}")
            e.printStackTrace()
        }

        return null
    }

    fun findRealPath(fd: Int): String? {
        var ins: InputStream? = null
        try {
            val path = File("/proc/self/fd/${fd}").canonicalPath
            if (!path.startsWith("/proc") && File(path).canRead()) {
                // Double check that we can read it
                ins = FileInputStream(path)
                ins.read()
                return path
            }
        } catch(e: Exception) { } finally { ins?.close() }

        return null
    }

    fun shareFiles(activityContext: Context, files: List<FileItemDto>) {
        if(files.isEmpty()) {
            return
        }

        val uris = mutableListOf<Uri>()
        val mimeTypes = mutableListOf<MimeType>()
        for (f in files) {
            if(f.isFile) {
                val file = f.toFile()
                uris.add(getUriForFile(activityContext, file))
                mimeTypes.add(MimeType.guessFromFile(file))
            }
        }

        if(uris.isEmpty()) {
            return
        }

        val intent = uris.createSendStreamIntent(activityContext, mimeTypes)
            .withChooser()

        ActivityUtil.startActivitySafe(activityContext.findActivity(), intent)
    }

    fun getInnerFilesBinDir(): File {
        val filesDir = getInnerDataFilesDirOrThrowException(AppModel.realAppContext)
        val binDir = File(filesDir.canonicalPath+"/bin")
        if(!binDir.exists()) {
            binDir.mkdirs()
        }
        return binDir
    }

    fun getLfsBinPath(): String {
        return "${getNativeLibraryDir(AppModel.realAppContext)}/libgitlfs.so"
    }

    // copy from:
    // https://github.com/lhear/SimpleXray/blob/4c7890114a46d33a8beb50c6e8b7404e45f874b6/app/src/main/kotlin/com/simplexray/an/service/TProxyService.kt#L414
    fun getNativeLibraryDir(context: Context?): String? {
        if (context == null) {
            MyLog.e(TAG, "Context is null")
            return null
        }

        try {
            val applicationInfo = context.applicationInfo
            if (applicationInfo != null) {
                val nativeLibraryDir = applicationInfo.nativeLibraryDir
                MyLog.d(TAG, "Native Library Directory: $nativeLibraryDir")
                return nativeLibraryDir
            } else {
                MyLog.e(TAG, "ApplicationInfo is null")
                return null
            }
        } catch (e: Exception) {
            MyLog.e(TAG, "Error getting native library dir: ${e.stackTraceToString()}")
            return null
        }
    }
}
