package com.catpuppyapp.puppygit.dto

import android.content.Context
import com.catpuppyapp.puppygit.screen.shared.FilePath
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.utils.MyLog
import java.util.concurrent.TimeUnit

private const val TAG = "FileSimpleDto"
/**
 * 只包含能粗略判断文件是否修改过的字段，用来在切换编辑器时判断是否需要重载文件
 */
data class FileSimpleDto(
    var name:String="",
    var createTime:Long=0L,
    var lastModifiedTime:Long=0L,
    var sizeInBytes:Long =0L,
    var isFile: Boolean = true,
    var fullPath:String = "",
) {
    companion object {
        fun genByFile(file: FuckSafFile, timeUnit: TimeUnit=TimeUnit.MILLISECONDS):FileSimpleDto {
            val fdto = FileSimpleDto()
            updateDto(fdto, file, timeUnit)
            return fdto
        }

        //这个单位精确到毫秒似乎没意义，后面3位全是0，1000毫秒是一秒，所以实际上只能精确到秒
        fun updateDto(fdto:FileSimpleDto, file: FuckSafFile, timeUnit: TimeUnit=TimeUnit.MILLISECONDS) {
            try {
                fdto.name = file.name
                fdto.fullPath = file.canonicalPath

                fdto.isFile = file.isFile
                fdto.sizeInBytes = file.length()  //对于文件夹，只能读出文件夹本身占的大小(4kb or 8kb之类的)，不会计算内部文件大小总和

                fdto.lastModifiedTime = timeUnit.convert(file.lastModified(), TimeUnit.MILLISECONDS)
                fdto.createTime = timeUnit.convert(file.creationTime(), TimeUnit.MILLISECONDS)  // actually on linux, this same as lastModifiedTime ;(

            }catch (e:Exception) {
                MyLog.e(TAG, "#updateDto err: ${e.localizedMessage}")
            }
        }
    }


//  避免错误传saf uri进来，禁用这方法了
//    fun toFile():File {
//        return File(fullPath)
//    }

    fun toFuckSafFile(context:Context?): FuckSafFile {
        return FuckSafFile(context, FilePath(fullPath))
    }


}
