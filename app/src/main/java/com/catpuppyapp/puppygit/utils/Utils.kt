package com.catpuppyapp.puppygit.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Build
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.IndentChar
import com.catpuppyapp.puppygit.data.entity.ErrorEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dto.AppInfo
import com.catpuppyapp.puppygit.dto.LineNumParseResult
import com.catpuppyapp.puppygit.dto.rawAppInfoToAppInfo
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.DirViewAndSort
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.github.git24j.core.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.exists
import kotlin.math.absoluteValue


private const val TAG = "Utils"


fun showToast(context: Context, text:String, duration:Int=Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}

fun getRepoNameFromGitUrl(gitUrl: String):String{
    val gitIdx = gitUrl.lastIndexOf(".git")
    val urlSeparatorIdx = gitUrl.lastIndexOf("/")+1

    if(urlSeparatorIdx < gitUrl.length && urlSeparatorIdx<=gitIdx){
        val folderName = gitUrl.substring(urlSeparatorIdx, gitIdx)
        return folderName
    }else {
        return ""
    }
}

//fun rmPathSuffix(path:String, suffix:String = File.separator):String {
//    return path.removeSuffix(suffix)
//}

/**
 *  baseDir可以为null，若为null，相当于 File(subDir)，若baseDir为null，不要用空字符串替代，含义不同
 */
fun isPathExists(baseDir: String?, subDir:String):Boolean {
//    val repoDirNoSeparatorSuffix = rmPathSuffix(repoDir)
//    val subDirNoSeparatorSuffix = rmPathSuffix(subdir)
//    val file = File(repoDirNoSeparatorSuffix + File.separator + subDirNoSeparatorSuffix)
    val file = if(baseDir!=null) File(baseDir, subDir) else File(subDir)  //baseDir可为null，若为null，相当于File(subDir)，注意，这里该为null就为null，不要传空字符串，空字符串和null作为basedir时，行为不同
    return file.exists()
}

fun strHasSpaceChar(str:String):Boolean {
    for(c in str) {
        if(c.isWhitespace()) {
            return true
        }
    }

    return false
}

fun strHasIllegalChars(str:String):Boolean {
    //如果包含这些字符，返回true。p.s. 这些字符来自于windows的创建文件名提示
    if(str.contains("/") || str.contains("\\") ||str.contains('?')|| str.contains(File.separator) || str.contains(File.pathSeparatorChar)
        //%2F 是 路径分隔符/ 的转义字符，如果存在这个字符，文件路径就废了，用java或c打开都可能报错
        || str.contains("*")|| str.contains("<") || str.contains(">") ||  str.contains("|") ||  str.contains("\"")
    ) {

        return true
    }

    return false
}

/**
 * 如果文件名包含“坏”字符或者文件名无法创建（比如包含非法路径字符），则返回true，否则返回false
 */
fun checkFileOrFolderNameAndTryCreateFile(nameWillCheck:String, appContext: Context):Ret<String?> {
    val funName="checkFileOrFolderNameAndTryCreateFile"

    //检测文件名，然后尝试创建文件，如果失败，说明存在非法字符
    try{
        //检测文件名是否为空
        if(nameWillCheck.isEmpty()) {
            throw RuntimeException(appContext.getString(R.string.err_name_is_empty))
        }

        //检测是否包含非法字符
        //如果包含这些字符，返回true。p.s. 这些字符来自于windows的创建文件名提示
        if(strHasIllegalChars(nameWillCheck)) {
            throw RuntimeException(appContext.getString(R.string.error_has_illegal_chars))
        }

        //获取缓存目录
        val cacheDir = AppModel.getOrCreateExternalCacheDir()
//        val fileNameNeedTest = Cons.createDirTestNamePrefix + str +"_"+ getRandomUUID()  //e.g. prefix_yourreponame_uuid，uuid是为了避免文件夹存在

        //拼接文件名
        val fileNameNeedTest = Cons.createDirTestNamePrefix + nameWillCheck;  //e.g. prefix_yourreponame
        //拼接文件完整路径
        val path = cacheDir.canonicalPath + File.separator + fileNameNeedTest

        //创建文件
        val file = File(path)
        val createSuccess = file.createNewFile()  //若文件已经存在，有可能返回false，用结果判断下，只有是我创建的情况下才删除
        //检测文件是否存在，不管上面创建成功与否，只要存在就说明是合法文件名，所以应返回假。
        if(file.exists()) {
            //如果创建成功，则删除；如果创建失败且能执行到这里，说明文件已存在但不是我创建的，那就不删，当然这样有可能导致缓存目录有无效文件，不过无所谓，顶多清下app缓存就行了，而且即使不这样做也有可能缓存目录存在无效文件，例如执行完创建后，app进程被杀，就存在无效文件了
            if(createSuccess) {  //执行到这，文件存在且是我创建的，删除
                file.delete()
            }else {  //文件存在但不是我这次创建的，可能之前创建的没成功删掉，也可能用户创建的，不删了，显示个警告。注：用户可通过app的清理缓存(20240601，还没实现)或者系统应用程序信息界面的清理缓存清掉这个在cache目录的文件
                MyLog.w(TAG, "#$funName: warn: may has invalid file '${file.name}' in cache dir, try clear app cache if you don't know that file")
            }

            return Ret.createSuccess(null)  //文件存在，说明不包含非法字符
        }

        //上面没返回，可能创建失败
        throw RuntimeException(appContext.getString(R.string.error_cant_create_file_with_this_name))
    }catch (e:Exception) {
        MyLog.e(TAG, "#$funName err: ${e.localizedMessage}")

        return Ret.createError(null, e.localizedMessage ?: appContext.getString(R.string.unknown_err_plz_try_another_name))
    }
}



// start: deprecated uuid functions
//
////返回值示例(32个字符+4个分隔符=36个字符)：171885d6-93b5-497a-8b9b-17e58ac99138
//private fun getRandomUUIDWithSeparator():String {
//    return UUID.randomUUID().toString();
//}

////返回值示例(32个字符)：171885d693b5497a8b9b17e58ac99138
//private fun getRandomUUIDNoSeparator():String {
//    return UUID.randomUUID().toString().replace("-","");
//}

//返回值示例：includeSeparator=true,36位字符:171885d6-93b5-497a-8b9b-17e58ac99138
//          includeSeparator=false，32位字符:171885d693b5497a8b9b17e58ac99138
//fun getRandomUUID(includeSeparator:Boolean=false):String {
//    return if(includeSeparator) getRandomUUIDWithSeparator()
//            else  getRandomUUIDNoSeparator()
//}

//fun getShortUUID(len:Int=20):String {
//    var actuallyLen = len
//    if(len>32) {
//        actuallyLen=32
//    }
//    return getRandomUUID().substring(0, actuallyLen)
//}
// end: deprecated uuid functions




fun getRandomUUID(len: Int = 32):String {
    return generateRandomString(len)
}

fun getShortUUID(len:Int=20):String {
    return getRandomUUID(len)
}


// start: 生成随机字符串，字符集：a..z, 0..9, A..Z
//val randomStringCharList = listOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');
val randomStringCharList = listOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9');

/**
 * 生成随机字符串比生成真UUID性能更好，好近10倍
 */
fun generateRandomString(length: Int=16): String {
    val sb = StringBuilder(length)
    for (i in 1..length) {
        sb.append(randomStringCharList.random())
    }
    return sb.toString()
}
// end: 生成随机字符串


fun dbIntToBool(v:Int):Boolean {
    return v != Cons.dbCommonFalse
}

fun boolToDbInt(b:Boolean):Int {
    return if(b) Cons.dbCommonTrue else Cons.dbCommonFalse
}


/**
 * @param originDateTimeFormatted "yyyy-MM-dd HH:mm:ss" formatted date time expected
 *
 * @return same date return "HH:mm:ss"; same year return "MM-dd HH:mm:ss"; else return fully origin date time
 */
fun getShortTimeIfPossible(originDateTimeFormatted:String) : String {
    return try {
        val nowFormatted = getNowInSecFormatted()
        val nowDateTime = nowFormatted.split(' ')
        val nowYmd = nowDateTime[0]

        val originDateTime = originDateTimeFormatted.split(' ')
        val originYmd = originDateTime[0]
        val originHms = originDateTime[1]

        if(nowYmd == originYmd) { // same date
            // HH:mm:ss
            originHms
        }else {
            val nowYmdArr = nowYmd.split('-')
            val originYmdArr = originYmd.split('-')
            if(nowYmdArr[0] == originYmdArr[0]) {  // same year
                // MM-dd HH:mm:ss
                "${originYmdArr[1]}-${originYmdArr[2]} $originHms"
            }else { // year and date totally difference, return fully origin date time
                // yyyy-MM-dd HH:mm:ss
                originDateTimeFormatted
            }
        }
    }catch (e: Exception) {
        MyLog.e(TAG, "#getShortTimeIfPossible(String) err: originDateTimeFormatted=$originDateTimeFormatted, err=${e.localizedMessage}")
        ""
    }
}

/**
 * @param sec utc+0 seconds expected, this param should not add offset, will be add when formatting
 */
fun getShortTimeIfPossible(sec: Long) : String {
    return getShortTimeIfPossible(getFormatTimeFromSec(sec))
}

/**
 * @return secs from utc
 */
fun getSecFromTime():Long {
    return getUtcTimeInSec()
}

/**
 * @param sec the sec should is unix epoch UTC+0 seconds
 */
fun getTimeFromSec(sec:Long):ZonedDateTime {

    //这个输出必须不带时区信息，否则报错，不好
//    return LocalDateTime.ofInstant(Instant.ofEpochSecond(sec), AppModel.getAppTimeZoneOffset())

    //这个输出可带时区信息也可不带，好
    return ZonedDateTime.ofInstant(Instant.ofEpochSecond(sec), AppModel.getAppTimeZoneOffsetCached())

//    return LocalDateTime.ofEpochSecond(sec, 0, AppModel.getAppTimeZoneOffset())
}


/**
 * @param sec the sec should is unix epoch UTC+0 seconds
 */
fun getFormatTimeFromSec(sec:Long, formatter:DateTimeFormatter = Cons.defaultDateTimeFormatter):String {
    try {
        val timeFromSec = getTimeFromSec(sec)
        //    val zonedDateTime = ZonedDateTime.from(localDateTimeFromSec)
        return formatter.format(timeFromSec)

    }catch (e:Exception) {
        MyLog.e(TAG, "#getFormatTimeFromSec: format datetime failed: ${e.stackTraceToString()}")
        return ""
    }
}

fun getNowInSecFormatted(formatter:DateTimeFormatter = Cons.defaultDateTimeFormatter):String {
    return getFormatTimeFromSec(getSecFromTime(), formatter)
}

fun getSystemDefaultTimeZoneOffset() :ZoneOffset{
    // tested
    return ZoneOffset.systemDefault().rules.getOffset(Instant.now())

    // tested
    // return OffsetDateTime.now().offset
}

/**
 * format minutes to UTC format (format: "UTC", + or -, hours, :minutes)
 *
 * e.g. input 480, return "UTC+8"; input 330, return "UTC+5:30"; input 303, return "UTC+5:03"
 */
fun formatMinutesToUtc(minutes:Int):String {
    try {
        // try get utc string from cache by minutes
        val cachedValue = AppModel.timezoneCacheMap.get(minutes)
        if(cachedValue != null) {
            return cachedValue
        }

        // no cache, calculate
        val hours = minutes / 60
        val resetOfMinutes = (minutes % 60).absoluteValue  // [0, 59]

        val hoursStr = if(hours >= 0) {
            //正数没符号，加个加号
            "+$hours"
        }else {
            //负数，自带符号，无需处理
            "$hours"
        }

        val resetOfMinutesStr = if(resetOfMinutes > 0) {
            val tmp = if(resetOfMinutes > 9) {
                //两位数，直接返回
                "$resetOfMinutes"
            }else {
                //一位数，补0
                "0$resetOfMinutes"
            }

            ":$tmp"
        }else {
            //取了绝对值，所以不可能小于0，如果等于0，无需显示，返回空字符串即可
            ""
        }

        val result = "UTC$hoursStr$resetOfMinutesStr"

        // update cache
        AppModel.timezoneCacheMap.put(minutes, result)

        return result
    }catch (e:Exception) {
        MyLog.e(TAG, "#formatMinutesToUtc() err: ${e.stackTraceToString()}")

        return ""
    }
}

//转换天数到秒数
fun daysToSec(days:Int) :Long{
    return (days * 24 * 60 * 60).toLong()
}

//20240425 测试了下，出错时能显示出Toast，不过不能用Msg.requireShow()，要直接用Toast.make().show()
private fun getDirIfNullThenShowToastAndThrowException(context:Context, dir:File?, errMsg:String):File {
    if(dir==null) {
        showToast(context, errMsg, Toast.LENGTH_LONG)
        throw RuntimeException(errMsg)
    }else {
        if(!dir.exists()) {
            dir.mkdirs()
        }
        return dir;
    }
}

/**
 * if get external files err, will return inner files dir, if still got err, app cant work, throw exception, done
 */
fun getExternalFilesIfErrGetInnerIfStillErrThrowException(context:Context):File {
    return try {
        getDirIfNullThenShowToastAndThrowException(context, context.getExternalFilesDir(null), Cons.errorCantGetExternalFilesDir)
    }catch (e:Exception) {
        getDirIfNullThenShowToastAndThrowException(context, context.filesDir, Cons.errorCantGetInnerFilesDir)
    }
}

/**
 * if get external cache err, will return inner cache dir, if still got err, app cant work, throw exception, done
 */
fun getExternalCacheDirIfErrGetInnerIfStillErrThrowException(context:Context):File {
    return try{
        getDirIfNullThenShowToastAndThrowException(context, context.externalCacheDir, Cons.errorCantGetExternalCacheDir)
    }catch (e:Exception) {
        getDirIfNullThenShowToastAndThrowException(context, context.cacheDir, Cons.errorCantGetInnerCacheDir)
    }
}

fun getInnerDataDirOrThrowException(context:Context):File {
    return getDirIfNullThenShowToastAndThrowException(context, context.dataDir, Cons.errorCantGetInnerDataDir)
}

fun getInnerDataFilesDirOrThrowException(context:Context):File {
    val dataDir = getInnerDataDirOrThrowException(context)
    val filesDir = File(dataDir.canonicalPath+"/files")
    if(!filesDir.exists()) {
        filesDir.mkdirs()
    }

    return filesDir
}

fun getExternalDataDirOrNull(context:Context):File? {
    return try {
        val dir = context.getExternalFilesDir(null) ?: throw RuntimeException("`context.getExternalFilesDir(null)` returned `null`")
        if(!dir.exists()) {
            dir.mkdirs()
        }

        //先转成规范路径File再获取parentFile，不然如果是 File("parent", "sub/abc")，这种路径获取到的就会是parent，而不是真正的当前目录abc的上级目录"parent/sub"
        dir.canonicalFile.parentFile
    }catch (e:Exception) {
        MyLog.e(TAG, "get app external data failed, usually this folder at '/storage/emulated/Android/data/app_package_name, err is: ${e.stackTraceToString()}")

        null
    }
}

// inner cache dir, usually at: "/data/data/app_package_name/cache"
fun getInnerCacheDirOrNull(context:Context):File? {
    return try {
        val dir = context.cacheDir ?: throw RuntimeException("`context.cacheDir` returned `null`")
        if(!dir.exists()) {
            dir.mkdirs()
        }

        dir
    }catch (e:Exception) {
        MyLog.e(TAG, "get app inner cache dir failed, err is: ${e.stackTraceToString()}")

        null
    }
}

//fun createAllRepoParentDirIfNonexists(baseDir:File, allRepoParentDir:String=Cons.defaultAllRepoParentDirName):File {
//    return createDirIfNonexists(baseDir, allRepoParentDir)
//}

//fun createFileSnapshotDirIfNonexists(baseDir:File, dirName:String=Cons.defaultFileSnapshotDirName):File {
//    return createDirIfNonexists(baseDir, dirName)
//}

//fun createLogDirIfNonexists(baseDir:File, dirName:String=Cons.logDirName):File {
//    return createDirIfNonexists(baseDir, dirName)
//}

fun createDirIfNonexists(baseDir:File, subDirName:String):File {
//    val dir = File(baseDir.canonicalPath + File.separator + subDirName)
    val dir = File(baseDir.canonicalPath, subDirName)
    if(!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

fun deleteIfFileOrDirExist(f: File):Boolean {
    if(f.exists()) {
        return f.deleteRecursively()
    }
    return true;
}

fun isFileSizeOverLimit(size:Long, limit:Long=SettingsUtil.getSettingsSnapshot().editor.maxFileSizeLimit) :Boolean {
    return isSizeOverLimit(size = size, limitMax = limit)
}

fun isDiffContentSizeOverLimit(size:Long, limit:Long=SettingsUtil.getSettingsSnapshot().diff.diffContentSizeMaxLimit) :Boolean {
    return isSizeOverLimit(size = size, limitMax = limit)
}

/**
 * @return if limitMax is 0, meant no limit, return false; else return (size > limitMax) 's result
 */
fun isSizeOverLimit(size:Long, limitMax:Long):Boolean {
    // 0 = no limit
    if(limitMax == 0L) {
        return false
    }

    return size > limitMax
}

/**
 * return file name or empty string
 * 适用于 "/"开头的绝对路径和非"/"开头的相对路径
 */
fun getFileNameFromCanonicalPath(path:String) : String {
    return runCatching { FsUtils.splitParentAndName(path).second }.getOrDefault("")
}


/**
 * @return e.g.: input "abc" or "/path/to/abc" or "/path/to/abc/" or "path/to/abc"，return "abc";
 *          input "abc//" or other bad path, return origin path;
 *          if err, return origin path
 */
@Deprecated("over complex")
fun deprecated_getFileNameFromCanonicalPath(path:String, separator:Char=Cons.slashChar) : String {
    try {
        val pathRemovedSuffix = path.trim(separator)  //为目录移除末尾的/，如果有的话

        val lastSeparatorIndex = pathRemovedSuffix.lastIndexOf(separator)  //找出最后一个/的位置

        if(lastSeparatorIndex == -1) {
            return pathRemovedSuffix
        }

        //因为先trim过separator，所以再lastIndexOf separator，结果不可能是开头或结尾，
        // 所以这里不用判断index是否在末尾，
        // trim一个字符在lastIndexOf或indexOf那个字符，结果只有两种：要么就找到了，在中间，要么就没找到，-1，而上面已经判断了-1，
        // 所以后面再调用substring(lastIndexOf+1)是绝对安全不会越界的

        //无法取出文件名则返回原字符串
        //没找到/ 或 无效路径格式(上面去了个/，末尾还有个/，说明原字符串末尾至少两个/，所以是无效路径)
//        if(lastSeparatorIndex == pathRemovedSuffix.length-1) {  // separator在末尾
//            return path
//        }

        //有效路径，返回目录或文件名
        return pathRemovedSuffix.substring(lastSeparatorIndex+1)
    }catch (e:Exception) {
        MyLog.e(TAG, "#getFileNameFromCanonicalPath err: path=$path, separator=$separator, err=${e.localizedMessage}")
        return path
    }
}

//输入 eg: /sdcard/Android/data/com.pack/files/allRepoDir/Repo/dir/etc
//return eg: allRepoDir/Repo/dir/etc
fun getFilePathStrBasedAllRepoDir(path:String):String {
    var ret = ""

    val allRepoBaseDirParentFullPath = AppModel.allRepoParentDir.parent?:""  //不知道parent是否以/结尾，如果是，后面解析出的内容会是 非/开头，直接返回即可，否则会是/开头，需要删除开头的/再返回
    val allRepoBaseIndexOf = path.indexOf(allRepoBaseDirParentFullPath)
    if(allRepoBaseIndexOf!=-1) {
        val underAllRepoBaseDirPathStartAt = allRepoBaseIndexOf + allRepoBaseDirParentFullPath.length
        if(underAllRepoBaseDirPathStartAt < path.length) {
            var pathBaseAllRepoDir = path.substring(underAllRepoBaseDirPathStartAt)  //获取 allRepoDir/Repo/dir/etc ，但不知道开头有没有/
//            if(pathBaseAllRepoDir.startsWith(File.separator)) {  //不太确定上面的api的返回值，如果allRepoBaseDirFullPath末尾没"/"，那解析出的字符串则会以"/" 开头，删除一下/。(后来测试了末尾不会包含 /)
//                if(pathBaseAllRepoDir.length>=2) {  //确保不会index out of bounds
//                    pathBaseAllRepoDir =  pathBaseAllRepoDir.substring(1)  // 返回不包含 开头 / 的内容
//                }else {  //解析完，若只有 /，则返回空字符串
//                    pathBaseAllRepoDir =  ""  //解析完只有/，返回空字符串
//                }
//            }
            ret = pathBaseAllRepoDir  //如果返回的字符串不是以/开头，直接返回
        }

    }
    return ret.removePrefix(File.separator).removeSuffix(File.separator)  //移除末尾和开头的 /
}

//输入全路径，返回仓库下路径，但不包含仓库名。
// 输入输出举例：
// 例1：输入：/sdcard/Android/data/com.pack/AllRepoBaseDir/Repo1/file1 输出：("Repo1", "file1")
// 例2：输入：/sdcard/Android/data/com.pack/AllRepoBaseDir/Repo1 输出：("Repo1",")
// 例3：输入：/sdcard/Android/data/com.pack/AllRepoBaseDir/Repo1/ 输出：("Repo1",")
// 例4：输入：/sdcard/Android/data/com.pack/files/allRepoDir/Repo/dir/etc ，返回 ("Repo","dir/etc")
fun getFilePathStrUnderRepoByFullPath(fullPath:String):Pair<String,String> {
    //假设输入：/sdcard/Android/data/com.pack/files/allRepoDir/Repo/dir/etc

    var repoFullPath = ""
    var relativePathUnderRepo = ""
    val filePathStrBasedAllRepoDir = getFilePathStrBasedAllRepoDir(fullPath)  //获取 allRepoDir/Repo/dir/etc
    if(filePathStrBasedAllRepoDir.isNotBlank()) {
        val firstSeparatorIndex = filePathStrBasedAllRepoDir.indexOf(File.separator)
        val cutAllRepoDirIndex = firstSeparatorIndex + 1  //计算 Repo/dir/etc 在 allRepoDir/Repo/dir/etc 中的起始索引
        if(cutAllRepoDirIndex!=0 && cutAllRepoDirIndex < filePathStrBasedAllRepoDir.length) {  //索引加了1，如果还是0，说明没找到对应字符串
            val repoPathUnderAllRepoBase = filePathStrBasedAllRepoDir.substring(cutAllRepoDirIndex)  //获取 Repo/dir/etc
            val finallyStrIndex = repoPathUnderAllRepoBase.indexOf(File.separator)+1  //计算 dir/etc 在 Repo/dir/etc 中的起始索引
            if(finallyStrIndex!=0 && finallyStrIndex < repoPathUnderAllRepoBase.length) {
                val repoNameEndsWithSeparator = repoPathUnderAllRepoBase.substring(0, finallyStrIndex)  //取出 Repo/
                repoFullPath = File(AppModel.allRepoParentDir.canonicalPath, repoNameEndsWithSeparator).canonicalPath  //canonicalPath 返回的结果末尾就没 / 了，结果应为 Repo
                relativePathUnderRepo = repoPathUnderAllRepoBase.substring(finallyStrIndex).removePrefix(File.separator).removeSuffix(File.separator)  //返回 dir/etc
            }
        }


    }

    return Pair(repoFullPath, relativePathUnderRepo)
}

//移除子目录的父目录前缀
//输入： /a/b/c/, /a/b/c/d/e ，返回 d/e (注意：会移除开头和末尾的 /，不管是否是目录，返回的结果一律开头和末尾都没/，调用者应该自己知道传入的路径是个目录还是文件，不需要通过这里的返回值末尾是否包含/来判断)
//输入： /a/b/c, /a/b/c/d/e/ ，返回 d/e
//输入： /a/b/c, /a/b/c ，返回 空字符串
//输入： /a/b/c, /a/b/c/ ，返回 空字符串
//输入： a/b/c, /a/b/c/d/e/ ，返回空字符串""，因为开头不匹配，本函数不对入参开头的 / 做处理
fun getFilePathUnderParent(parentFullPath:String, subFullPath:String) :String {
    if(parentFullPath.isBlank() || subFullPath.isBlank()
        ||subFullPath.length <= parentFullPath.length
        ) {
        return ""
    }

    val indexOf = subFullPath.indexOf(parentFullPath)

    //等于-1代表没找到，后面的不等于0代表在子中找到了父，但不是在开头
    if(indexOf==-1 || indexOf!=0) {  //子目录必须包含父目录且开头匹配
        return ""
    }
//    val startIndex = indexOf + parentFullPath.length  //因为上面indexOf不等于0就直接返回了，所以执行到这indexOf肯定是0，所以就不必和父路径长度相加了，直接用父长度即可
    val startIndex = parentFullPath.length
    if(startIndex >= subFullPath.length){
        return ""
    }

    return subFullPath.substring(startIndex).removePrefix(File.separator).removeSuffix(File.separator)
}

//输入 eg: /sdcard/Android/data/youpackagename/file/AllRepoBaseDir/Repo/dir/etc
//输出 eg: Repo/dir/etc
//如果输入是allRepoDir，则返回 空字符串
fun getFilePathStrBasedRepoDir(path:String, returnResultStartsWithSeparator:Boolean=false):String {
    val path2 = getFilePathStrBasedAllRepoDir(path)
    val firstIdxOfSeparator = path2.indexOf(File.separator)  //不如直接用 /
    val isAllRepoDir = firstIdxOfSeparator==-1
    var result= if(isAllRepoDir) "" else path2.substring(firstIdxOfSeparator+1)

    //检查是否期望结果以/开头，如果期望，则检查是否以/开头，不是则添加；若不期望则检查是否以/开头，是则移除
    if(returnResultStartsWithSeparator) {
        if(!result.startsWith(File.separator)) {
            result = File.separator+result
        }
    }else {
        if(result.startsWith(File.separator)) {
            result = result.removePrefix(File.separator)
        }
    }

    return result
}

//input就是git status输出的那种仓库内相对路径+文件名，例如 dir1/dir2/file.txt ，输出则返回 dir1/dir2/；如果输入是file.txt，则返回 /
//第2个参数传true适用于解析仓库相对路径的场景，因为仓库相对路径下仓库根目录下的文件没有/但其归属于/，这里的/代表仓库根目录；为false则会在查找不到路径分隔符时返回原path（入参1），这种情况暂无应用场景
/**
 * @param path src path, will try get parent path for it
 * @param trueWhenNoParentReturnSeparatorFalseReturnPath true, when parent path is empty will return separator like "/" ; else return `path`. will ignore this param if `trueWhenNoParentReturnEmpty` is true
 * @param trueWhenNoParentReturnEmpty when parent path is empty return empty, if false, return what depend by `trueWhenNoParentReturnSeparatorFalseReturnPath`
 */
fun getParentPathEndsWithSeparator(path:String, trueWhenNoParentReturnSeparatorFalseReturnPath:Boolean=true, trueWhenNoParentReturnEmpty:Boolean=false):String {
    try {
        val separator = File.separator
        val path = path.removeSuffix(separator)  // this is necessary, e.g. "def/abc/" same means with "def/abc", the parent should is "def/" for both
        val lastIndexOfSeparator = path.lastIndexOf(separator)
        if(lastIndexOfSeparator != -1) {  // found "/", has a parent path
            return path.substring(0, lastIndexOfSeparator+1)  // +1把/本身包含上
        }else {  // not found "/", no parent path yet
            //没/，可能是根目录？话说我当初为什么没找到让它返回/？
            // 啊，对了，因为是根据仓库根目录设置的，如果有个文件在仓库根目录，
            // 其仓库相对路径就是 filename，这时如果找不到/，说明是根目录
            if(trueWhenNoParentReturnEmpty) {
                return ""
            }

            return if(trueWhenNoParentReturnSeparatorFalseReturnPath) separator else path
        }

    }catch (e:Exception) {
        MyLog.e(TAG, "#getParentPathEndsWithSeparator err: path=$path, trueWhenNoParentReturnSeparatorFalseReturnPath=$trueWhenNoParentReturnSeparatorFalseReturnPath, trueWhenNoParentReturnEmpty=$trueWhenNoParentReturnEmpty, err=${e.localizedMessage}")
        //发生异常一律return path合适吗？，没什么不合适的，虽然可能会有些奇怪，但在界面能看出问题 且 用户也感觉没太大异常，嗯，就这样吧
        //发生异常return 原path
        return path
    }
}

//fun encodeStrUri(input:String):String {
//    return input.replace("/",Cons.separatorReplaceStr);  //encodeURIComponent("/"), return "%2F"
//}
//fun decodeStrUri(input:String):String {
//    return input.replace(Cons.separatorReplaceStr,"/")
//}

fun isRepoReadyAndPathExist(r: RepoEntity?): Boolean {
    if(r==null) {
        return false
    }

    //首先把仓库的gitRepoState更新一下，不然万一没更新状态，gitRepoState会是null，会导致后面误判仓库无效
    runCatching {
        Repository.open(r.fullSavePath)?.use { repo->
            r.gitRepoState = repo.state()
        }
    }

    if (Libgit2Helper.isRepoStatusReady(r)
        && r.isActive == Cons.dbCommonTrue
        && r.fullSavePath.isNotBlank()

        //过滤地址有效但仓库无效的仓库
        && r.gitRepoState != null
    ) {
        if (File(r.fullSavePath).exists()) {
            return true;
        }
    }

    return false
}

fun setErrMsgForTriggerNotify(hasErrState:MutableState<Boolean>,errMsgState:MutableState<String>,errMsg:String) {
    hasErrState.value=true;
    errMsgState.value=errMsg;
}

//如果只传job，则没loading，单纯执行job
//x 20240426修改，即使发生异常，也可解除loading)发生异常会无法解除loading，考虑到避免用户在发生错误后继续操作，所以没做处理
fun doJobThenOffLoading(
    loadingOn: (String)->Unit={},
    loadingOff: ()->Unit={},
    loadingText: String="Loading…",  //这个最好别使用appContext.getString(R.string.loading)，万一appContext都还没初始化就调用此方法，会报错，不过目前20240426为止，只有在appContext赋值给AppModel对应字段后才会调用此方法，所以实际上没我担心的这个问题，根本不会发生
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    job: suspend ()->Unit
): Job? {
    return try {
        CoroutineScope(coroutineDispatcher).launch {
            //开启loading
            try {
                loadingOn(loadingText)
            }catch (e:Exception) {
                Msg.requireShowLongDuration("loadOn err: "+e.localizedMessage)
                MyLog.e(TAG, "#doJobThenOffLoading(): #loadingOn error!\n" + e.stackTraceToString())
            }finally {
                //执行操作
                try {
                    job()
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("job err: "+e.localizedMessage)
                    MyLog.e(TAG, "#doJobThenOffLoading(): #job error!\n" + e.stackTraceToString())
                }finally {
                    try {
                        //最后解除loading
                        loadingOff()  //x 20240426job被trycatch包裹，实际上这个已经是百分百会解除了，索性放到finally里，百分百解除的意义更明确)这个要不要放到finally里？要不然一出异常，loading就无法解除了，不过解除不了也好，省得用户误操作
                    }catch (e:Exception) {
                        Msg.requireShowLongDuration("loadOff err: "+e.localizedMessage)
                        MyLog.e(TAG, "#doJobThenOffLoading(): #loadingOff error!\n" + e.stackTraceToString())
                    }
                }
            }
        }
    }catch (e:Exception) {
        Msg.requireShowLongDuration("coroutine err: "+e.localizedMessage)
        MyLog.e(TAG, "#doJobThenOffLoading(): #launch error!\n" + e.stackTraceToString())
        null
    }

}

fun doJob(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    job: suspend () -> Unit
) :Job {
    return CoroutineScope(coroutineDispatcher).launch {
        job()
    }
}

//fun doJobThenOffLoadingWith1Param(loadingOn:()->Unit={},loadingOff: ()->Unit={},job:suspend (Any)->Any, param:Any) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            //开启loading
//            loadingOn()
//
//            //执行操作
//            job(param)
//
//            //最后解除loading
//            loadingOff()
//        }catch (e:Exception){
//            e.printStackTrace()
//            MyLog.e(TAG, "#doJobThenOffLoading():" + e.stackTraceToString)
//        }
//    }
//}

//替换string resource 中的placeholder为目标字符
private fun replaceStringRes(strRes:String, placeHolderCount:Int, strWillReplaced:String):String {
    return strRes.replace(Cons.placeholderPrefixForStrRes+placeHolderCount, strWillReplaced)
}
//替换string resource 中的placeholder为目标字符
fun replaceStringResList(strRes:String, strWillReplacedList:List<String>):String {
    var ret=strRes
    for((idx, str) in strWillReplacedList.withIndex()) {
        val idxPlus1 = idx+1

        ret = replaceStringRes(ret, idxPlus1, str)
    }

    return ret;
}

fun getStrShorterThanLimitLength(src:String, limit:Int=12):String {
    return if(src.length > limit) src.substring(0, limit)+"…" else src
}

suspend fun createAndInsertError(repoId:String, errMsg: String) {
    if(repoId.isBlank() || errMsg.isBlank()) {
        return
    }

    // if repo doesn't exist, return
    val repoDb = AppModel.dbContainer.repoRepository
    if(repoDb.getById(repoId) == null) {
        MyLog.e(TAG, "$TAG#createAndInsertError: not found repo which matched the repoId '$repoId', and the errMsg is: $errMsg")
        return
    }

    //更新repo表相关字段
    repoDb.setNewErrMsg(repoId, errMsg)

    val errDb = AppModel.dbContainer.errorRepository
    errDb.insert(
        ErrorEntity(
            msg = errMsg,
            repoId = repoId,
            date = getNowInSecFormatted()
        )
    )
}

//做3件事：1记录错误信息到日志文件 2显示错误信息 3保存错误信息到数据库
suspend fun showErrAndSaveLog(logTag:String, logMsg:String, showMsg:String, showMsgMethod:(String)->Unit, repoId:String, errMsgForErrDb:String = showMsg) {
    //显示提示
    showMsgMethod(showMsg)

    //保存数据库(给用户看的，消息尽量简单些)
    createAndInsertError(repoId, errMsgForErrDb)

    //记录到日志
    MyLog.e(logTag, logMsg)
}

fun getHumanReadableSizeStr(size:Long):String {
    var s:Double=0.0;
    var unit = ""
    if(size >= Cons.sizeTB) {  //1TB
        s=size.toDouble()/Cons.sizeTB
        unit = Cons.sizeTBHumanRead
    }else if(size >= Cons.sizeGB) {
        s=size.toDouble()/Cons.sizeGB
        unit = Cons.sizeGBHumanRead
    }else if(size >= Cons.sizeMB) {
        s=size.toDouble()/Cons.sizeMB
        unit = Cons.sizeMBHumanRead
    }else if(size >= Cons.sizeKB) {
        s=size.toDouble()/Cons.sizeKB
        unit = Cons.sizeKBHumanRead
    }else {
        //整字节
        return size.toString()+Cons.sizeBHumanRead
    }

    //大于1000字节
    return "%.2f".format(s) + unit
}

fun getFileAttributes(pathToFile:String): BasicFileAttributes? {
    try {
        val filePath = Paths.get(pathToFile)
        // if file doesn't exist, unable to read file attributes, so just return null
        if(!filePath.exists()) return null  //may the filePath.exist() will not follow symbolic link? I am not sure

        val attributes: BasicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
        return attributes
    }catch (e:Exception) {
        MyLog.e(TAG, "#getFileAttributes err: pathToFile=$pathToFile, err=${e.localizedMessage}")
        return null
    }
}

fun doJobWithMainContext(job:()->Unit) {
    doJobThenOffLoading(coroutineDispatcher = Dispatchers.Main) {
        job()
    }
}

//这个和 doJobWithMainContext 的区别在于，你得自己创建个协程，然后执行job，适合已经存在协程只需要在Main上下文里执行某些操作的场景
//注：naviUp和showToast之类依赖Main线程上下文的操作应该在Dispatchers.Main里执行
suspend fun withMainContext(job:()->Unit) {
    withContext(Dispatchers.Main) {
        job()
    }
}

//对字符串添加前缀，应用场景举例：对下拉列表已选中条目进行区分(比如changelist，点击切换仓库，给目前正在使用的仓库名前面加个星号)
fun addPrefix(str: String, prefix:String="*"):String {
    return prefix+str
}

//根据索引取出list中对应元素执行操作(入参act)，如果操作成功，返回包含修改后(如果act修改了元素的话)的元素的Ret对象，否则返回包含错误信息的Ret
fun<T> doActIfIndexGood(idx:Int, list:List<T>, act:(T)-> Unit):Ret<T?> {
    try {
        if(idx>=0 && idx<list.size) {
            val item = list[idx]
            act(item)
            return Ret.createSuccess(item)
        }
        return Ret.createError(null, "err:invalid index for list", Ret.ErrCode.invalidIdxForList)
    }catch (e:Exception) {
        MyLog.e(TAG, "#doActIfIndexGood() err: "+e.stackTraceToString())
        return Ret.createError(null, "err: "+e.localizedMessage, Ret.ErrCode.doActForItemErr)
    }

}

//获取一个安全索引或-1。如果list为空，返回-1；否则返回一个不会越界的索引
fun getSafeIndexOfListOrNegativeOne(indexWillCheck:Int, listSize:Int):Int {
    //list为空，返回-1
    if(listSize<=0) {
        return -1
    }

    //确保index不小于0 且 不大于listSize-1
    return indexWillCheck.coerceAtLeast(0).coerceAtMost(listSize - 1)
}

fun <T> isGoodIndexForList(index:Int, list:List<T>) = isGoodIndex(index, list.size);

fun isGoodIndexForStr(index:Int, str:String) = isGoodIndex(index, str.length);

fun isGoodIndex(index:Int, size:Int) = index >= 0 && index < size;

// generated by chatgpt
fun getHostFromSshUrl(sshUrl: String): String? {
    // 定义正则表达式来匹配 SSH URL
    val regex = Regex("^(?:([^@]+)@)?([^:]+)(?::.*)?$")
    val matchResult = regex.matchEntire(sshUrl)

    return matchResult?.groups?.get(2)?.value // 获取主机名
}

fun getDomainByUrl(url:String):String {
    try {
        if(Libgit2Helper.isHttpUrl(url)) { // http or https, only return host, no port
            return URI.create(url).host ?: ""
        }else { // ssh url
            return getHostFromSshUrl(url) ?: ""
        }
    }catch (e:Exception) {
        MyLog.e(TAG, "#getDomainByUrl err: url=$url, err=${e.localizedMessage}")
        return ""
    }
}

fun getFormattedLastModifiedTimeOfFile(file:File):String{
    return getFormatTimeFromSec(sec = file.lastModified() / 1000)
}

fun getFormattedLastModifiedTimeOfFile(file:FuckSafFile):String{
    return getFormatTimeFromSec(sec = file.lastModified() / 1000)
}


fun<T> getFirstOrNullThenRemove(list:MutableList<T>):T? {
    try {
        return list.removeAt(0)
    }catch (e:Exception) {
        return null
    }
}

/**
 * return pair: onlyForThisFolder and viewAndSort
 */
fun getViewAndSortForPath(path:String, settings:AppSettings) :Pair<Boolean, DirViewAndSort> {
    val folderViewSort = settings.files.dirAndViewSort_Map[path]

    return if(folderViewSort == null) {
        Pair(false,  settings.files.defaultViewAndSort)
    }else {
        Pair(true, folderViewSort)
    }
}

/**
 * 获取文件扩展名或空字符串。
 * 注：若'.'在文件名开头或末尾或没有'.'，将返回空字符串；否则返回 ".txt" 之类的后缀名
 * @return input "abc.txt", return ".txt"; input ".git", return ""; input "abc.", return ""
 */
fun getFileExtOrEmpty_treatStartWithDotAsNoExt(filename:String):String {
    val extIndex = filename.lastIndexOf('.')

    // <=0 is right, cause if extIndex==0, the . is first char, meaning it is a hidden file, not represent a ext name
    // 小于等于0是对的，因为如果extIndex等于0， . 是第一个字符，代表隐藏文件而不是扩展名
    return if(extIndex <= 0 || extIndex == filename.lastIndex){
        ""
    }else{
//        filename.substring(extIndex, filename.length)  //没必要指定第2个参数
        filename.substring(extIndex)
    }
}


/**
 * @return Pair(name, ext), doesn't contains '.',
 *   e.g. input "abc.txt", return Pair("abc,", "txt");
 *   input ".git", return Pair("", "git");
 *   input "abc.", return Pair(abc, "")
 */
fun splitFileNameAndExt(filename:String) : Pair<String, String> {
    val extIndex = filename.lastIndexOf('.')

    return if(extIndex < 0) {
        Pair(filename, "")
    }else if(extIndex == 0) {
        // will not out of bounds,
        //   even filename.length is 1,
        //   the substring(length) will return an empty string ""
        Pair("", filename.substring(1))
    }else {  // extIndex > 0
        Pair(filename.substring(0, extIndex), filename.substring(extIndex + 1))
    }
}


fun getFileNameOrEmpty(filename:String) = splitFileNameAndExt(filename).first

fun getFileExtOrEmpty(filename:String) = splitFileNameAndExt(filename).second



fun readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings: AppSettings, defaultTimeOffsetInMinutes:Int):Int {
    return readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable(settings, defaultTimeOffsetInMinutes)!!
}

fun readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable(settings: AppSettings, defaultTimeOffsetInMinutes:Int?):Int? {
    return try{
        if(settings.timeZone.followSystem) {
            AppModel.getSystemTimeZoneOffsetInMinutesCached()
        }else {
            val offsetMinutes = settings.timeZone.offsetInMinutes.trim().toInt()
            if(isValidOffsetInMinutes(offsetMinutes)){
                offsetMinutes
            }else {
                val errMsg = getInvalidTimeZoneOffsetErrMsg(offsetMinutes)
                MyLog.e(TAG, "#readTimeZoneOffsetInMinutesFromSettingsOrDefaultNullable err: $errMsg")
                throw RuntimeException(errMsg)
            }
        }
    }catch (_:Exception) {
        defaultTimeOffsetInMinutes
    }
}

fun getInvalidTimeZoneOffsetErrMsg(offsetInMinutes:Int):String {
    return "invalid timezone offset: $offsetInMinutes minutes, expect in ${getValidTimeZoneOffsetRangeInMinutes()}"
}

fun getValidTimeZoneOffsetRangeInMinutes():String {
    return "[-1080, 1080] minutes"
}

/**
 * 有效的时区应该在 -18小时到+18小时之间，换算成分钟是1080，秒是64800
 */
fun isValidOffsetInMinutes(offsetInMinutes:Int):Boolean {
    return offsetInMinutes >= -1080 && offsetInMinutes <= 1080
}

fun getUtcTimeInSec():Long {
    //tested
//    return ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond()

    //tested
    return Instant.now().epochSecond
}


/**
 * @return pageChanged, if true, means page changed, selectedItemList not updated; else, page not changed and selectedItemList updated
 */
fun <T> updateSelectedList(
    selectedItemList: MutableList<T>,
    itemList: List<T>,
    match:(oldSelected:T, item:T)->Boolean,  // used for found
    quitSelectionMode: () -> Unit,
    pageChanged: () -> Boolean = {false}, //页面改变了，例如ChangeList页面换了仓库，这次的查询就不需要继续了，如果不需要此函数，可不传，用默认值即可
): Boolean {
    if (selectedItemList.isEmpty() || itemList.isEmpty()) {
        quitSelectionMode()
    } else {
        //移除选中但已经不在列表中的元素
        val stillSelectedList = mutableListOf<T>()

//                一般选中条目的列表元素会比所有条目列表少，所以选中条目在外部，这样有可能减少循环次数
        selectedItemList.forEachBetter { oldSelected ->
            val found = itemList.find { match(oldSelected, it) }
            //如果选中条目仍在条目列表存在，则视为有效选中项
            if (found != null) {
                //添加新查的列表中的“相同”元素，可能会有更新，所以不一定完全相同
                stillSelectedList.add(found)
            }
        }

        if (pageChanged()) {
            // page changed, and selected list not updated
            return true
        }

        selectedItemList.clear()
        selectedItemList.addAll(stillSelectedList)

        //如果选中条目为空，退出选择模式
        if (selectedItemList.isEmpty()) {
            quitSelectionMode()
        }
    }


    // page not changed, and selected item list updated
    return false
}


fun parseIntOrDefault(str:String, default:Int?):Int? {
    return try {
        str.trim().toInt()
    }catch (_:Exception){
        default
    }
}

fun parseLongOrDefault(str:String, default:Long?):Long? {
    return try {
        str.trim().toLong()
    }catch (_:Exception){
        default
    }
}

fun parseDoubleOrDefault(str:String, default:Double?):Double? {
    return try {
        str.trim().toDouble()
    }catch (_:Exception){
        default
    }
}

fun compareStringAsNumIfPossible(str1: String, str2: String, ignoreCase: Boolean = true):Int {
    val str1Num = parseDoubleOrDefault(str1, null)
    if(str1Num != null) {
        val str2Num = parseDoubleOrDefault(str2, null)
        if(str2Num != null) {
            return str1Num.compareTo(str2Num)
        }
    }

    return str1.compareTo(str2, ignoreCase = ignoreCase)
}

//从v2rayNG 拷的
fun receiverFlags(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ContextCompat.RECEIVER_EXPORTED
} else {
    ContextCompat.RECEIVER_NOT_EXPORTED
}

fun copyTextToClipboard(context: Context, text: String, label:String="label") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text) // 创建剪贴板数据
    clipboard.setPrimaryClip(clip) // 设置剪贴板内容
}

fun copyAndShowCopied(
    context:Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    text:String
) {
    clipboardManager.setText(AnnotatedString(text))
    Msg.requireShow(context.getString(R.string.copied))
}

fun genHttpHostPortStr(host:String, port:String, https:Boolean = false) : String {
    //如果host 是 0.0.0.0 换成 127.0.0.1，否则使用原ip
    val host = if(host == Cons.zero000Ip) Cons.localHostIp else host
    val prefix = if(https) "https://" else "http://"
    return "$prefix$host:$port"
}

fun getInstalledAppList(context:Context, selected:(AppInfo)->Boolean = {false}):List<AppInfo> {
    val packageManager = context.packageManager
    val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    val apps = mutableListOf<AppInfo>()

    for (pkg in packages) {
        val applicationInfo = pkg.applicationInfo ?: continue

        val appInfo = rawAppInfoToAppInfo(applicationInfo, packageManager, selected) ?: continue

        apps.add(appInfo)
    }

    return apps
}

fun trimLineBreak(str:String) :String {
    return str.trim { it == '\n' || it == '\r' }
}

fun isStartInclusiveEndExclusiveRangeValid(start:Int, endExclusive:Int, size:Int):Boolean {
  return  start < endExclusive && start >= 0 && start < size && endExclusive > 0 && endExclusive <= size
}

suspend fun isLocked(mutex: Mutex):Boolean {
    // delay 1 to make locked check more correct
    //先延迟一毫秒再检查，不然短时间内检查isLocked可能有误
    delay(1)
    return mutex.isLocked
}

suspend fun doActWithLockIfFree(mutex: Mutex, whoCalled:String, act: suspend ()->Unit) {
    val logPrefix = "#doActWithLockIfFree, called by '$whoCalled'";

    if(isLocked(mutex)) {
        if(AppModel.devModeOn) {
            MyLog.d(TAG, "$logPrefix: lock is busy, task will not run")
        }

        return
    }

    if(AppModel.devModeOn) {
        MyLog.d(TAG, "$logPrefix: lock is free, will run task")
    }

    // run task
    mutex.withLock { act() }

    if(AppModel.devModeOn) {
        MyLog.d(TAG, "$logPrefix: task completed")
    }

}

suspend fun apkIconOrNull(context:Context, apkPath:String, iconSizeInPx:Int): ImageBitmap? {
    return try {
        val pm = context.packageManager

        //未考证：第2个参数是个flag值，可获取附加信息，如不需要可设为0
//        val appInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)!!
        val appInfo = pm.getPackageArchiveInfo(apkPath, 0)!!

        delay(1)

        // https://stackoverflow.com/a/14313280
        appInfo.applicationInfo!!.let {
            // 这两行是关键
            it.sourceDir = apkPath
            it.publicSourceDir = apkPath

            // appName
//            val appName = it.loadLabel(pm).toString()

            // appIcon
            val icon = it.loadIcon(pm).toBitmapOrNull(width = iconSizeInPx, height = iconSizeInPx)!!.asImageBitmap()

            delay(1)

            icon
        }

    }catch (e: Exception) {
//        MyLog.d(TAG, "#apkIconOrNull() err: ${e.stackTraceToString()}")
        //这种日志没什么好记的，就算获取失败，我也没什么好改的，简单打印下错误信息就行
        e.printStackTrace()
        null
    }
}

suspend fun getVideoThumbnail(videoPath: String): ImageBitmap? {
    return try {
        val retriever = MediaMetadataRetriever()

        delay(1)

        try {
            // 设置数据源
            retriever.setDataSource(videoPath)
            // 获取缩略图，参数为时间戳（单位为微秒），可以设置为0获取视频的第一帧
            // 假设常见的每秒24帧，获取第1200帧，就是50000000微秒
            val frame = retriever.getFrameAtTime(50000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)!!.asImageBitmap()

            delay(1)

            frame
        } catch (e: Exception) {
//            MyLog.d(TAG, "#getVideoThumbnail() err: ${e.stackTraceToString()}")
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun paddingLineNumber(lineNum:String, expectLength:Int): String {
    return lineNum.padStart(expectLength, ' ')
}

fun getRangeForRenameFile(fileName:String):TextRange {
    val lastIndexOfDot = fileName.lastIndexOf('.')

    // 如果'.'在文件名开头或没有'.'，选中整个文件名；否则选中'.'之前的部分，不包括'.'本身
    return TextRange(0, if(lastIndexOfDot > 0) lastIndexOfDot else fileName.length)
}

fun appendSecondsUnit(str:String) = str+"s";


/**
 * get line number and column both are start from 1, support format 'line:column'
 *
 * @return Pair(lineNum, columnNum), both are starts from 1, you can subtract 1 to trans them to index
 */
fun parseLineAndColumn(str:String) = try {
    val lineAndColumn = str.split(":")

    //删下首尾空格，增加容错率，然后尝试转成int
    val lineNum = lineAndColumn[0].trim().toInt()

//       // deprecated, because supported to go to relative line number and it maybe less than 1, maybe same as EOF line num, so, ignore EOF check at here
//        val retLine = if(line == LineNum.EOF.LINE_NUM) {  // if is EOF, return last line number, then can go to end of file
//            actuallyLastLineNum
//        }else {
//            line
//        }

    val columnNum = lineAndColumn.getOrNull(1)?.trim()?.toInt() ?: 1
    val isRelative = str.startsWith("+") || str.startsWith("-")
    LineNumParseResult(lineNum, columnNum, isRelative)
}catch (e:Exception) {
    // parse int failed, then go first line
    LineNumParseResult()
}

fun onOffText(enabled:Boolean) = if(enabled) "ON" else "OFF";

fun tabToSpaces(spacesCount:Int) = if(spacesCount > 0) " ".repeat(spacesCount) else "\t"

fun getNextIndentByCurrentStr(current:String?, aTabToNSpaces:Int):String {
    if(current == null) {
        return ""
    }

    val sb = StringBuilder()
    for(i in current) {
        if(IndentChar.isIndent(i)) {
            sb.append(i)
        }else {
            break
        }
    }

    // x fixed: Disabled reason: it will cause pasted content have wrong intent, so disabled
    // x 已修复：改成在调用此方法前做判断了，如果可能是粘贴的内容，则不加缩进，否则加缩进，不一定准，但一般够用
    // append extras spaces for block start
    appendIndentForUnClosedSignPair(current, sb, aTabToNSpaces)

    return sb.toString()
}

private fun appendIndentForUnClosedSignPair(current: String, sb: StringBuilder, aTabToNSpaces: Int) {
    if (current.trim().let {
            it.endsWith("{")
//                    || it.endsWith("(")
//                    || it.endsWith("[")

                    // e.g. for yml
//                    || it.endsWith(":")

                    // e.g. for html xml, if only has "open" sign without "close", add indent
                    // if has pair open+close, then it should mod 2 equals to 0,
                    // else, means have not enough close sign, so we should add indent
//                    || it.pairClosed("<", "</").not()
    }) {
        sb.append(tabToSpaces(aTabToNSpaces))
    }
}

fun appAvailHeapSizeInMb():Long {
    val funName = "appAvailHeapSize"

    // src: https://stackoverflow.com/a/19267315
    val runtime = Runtime.getRuntime()
    val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
    val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
    val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB

    if(AppModel.devModeOn) {
        MyLog.i(TAG, "#$funName: ${availHeapSizeInMB}MB")
    }

    return availHeapSizeInMB
}

/**
 *
 * if no more memory over limit times, will do act then return true, else return false and do nothing
 *
 * Don't call this on main thread
 * even call, will not throw an err,
 * because the @WorkerThread is not a force limit
 *
 * @return true means no more memory, otherwise false
 */
@Deprecated("due to it use a delay for more accurately result, so if run this in concurrency way, maybe got a backlog of many mem check tasks")
@WorkerThread
fun noMoreHeapMemThenDoAct_Deprecated(
    lowestMemInMb: Int = 30,
    lowestMemLimitCount: Int = 3,
    act: () -> Unit,
) : Boolean  {
    var lowMemCount = 0

    while (true) {
        if(appAvailHeapSizeInMb() < lowestMemInMb) {
            if(++lowMemCount >= lowestMemLimitCount) {
                act()

                return true
            }

            // 如果内存不够，会增长，直到无法增长，然后throw OOM，所以需要delay一下才能确定是否还有空闲内存
            runBlocking { delay(100) }
        }else {
            return false
        }
    }
}


@WorkerThread
fun noMoreHeapMemThenDoAct(
    // 这个值不应该太大，因为内存过低时会回收，
    // 回收后可能会有更多可用内存，如果太大，在回收内存前就会认为内存不足了
    lowestMemInMb: Int = 16,

    act: () -> Unit,
) : Boolean  {
    if(appAvailHeapSizeInMb() < lowestMemInMb) {
        act()

        return true

    }else {
        return false
    }
}
