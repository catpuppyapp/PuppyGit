package com.catpuppyapp.puppygit.utils.saf

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import com.catpuppyapp.puppygit.utils.IOUtil
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

object SafAndFileCmpUtil {

    class SafAndFileCompareResult (
        /**
         * 仅存在于saf目标的文件
         * saf to files的时候，这个应该在files被添加到files；files to saf的时候，这个应该从saf被删除
         * 注意：列表元素有可能是文件夹，也有可能是文件，处理时需要做判断
         */
        val onlyInSaf:MutableList<DocumentFile> = mutableListOf(),

        /**
         * 仅存在于files目标的文件
         * saf to files，这个应该从files里删除；files to saf，这个应该被添加到saf目录。
         * 注意：列表元素有可能是文件夹，也有可能是文件，处理时需要做判断
         */
        val onlyInFiles:MutableList<File> = mutableListOf(),

        /**
         * 两者相同目录都有同样文件，但文件内容或类型不同
         * saf to files，用saf的覆盖files的；files to saf，用files的覆盖saf的
         * 注意：列表元素有可能是文件夹，也有可能是文件，处理时需要做判断。
         * 注意：因为不同的元素会精确到文件，所以这个列表的元素不会存在两个目标都是文件夹的情况，只有可能存在 "一方文件夹另一方文件(类型不同)" 或 "两方都是文件但内容不同"
         */
        val bothAndNotSame:MutableList<SafAndFileDiffPair> = mutableListOf(),

        //缺一个 bothAndSame，不过没必要记录一样的，并不需要操作它们
    ) {
        override fun toString(): String {
            val splitLine = "\n\n------------\n\n"

            return "onlyInSaf.size=${onlyInSaf.size}, \nonlyInFiles.size=${onlyInFiles.size}, \nbothAndNotSame.size=${bothAndNotSame.size}" +
                    splitLine +
                    "onlyInSaf=${onlyInSaf.map { it.uri.toString() + "\n\n" }}" +
                    splitLine +
                    "onlyInFiles=${onlyInFiles.map { it.canonicalPath + "\n\n" }}" +
                    splitLine +
                    "bothAndNotSame=${bothAndNotSame.map { 
                        "safFile=${it.safFile.uri}, \nfile=${it.file.canonicalPath}, \ndiffType=${it.diffType}\n\n"
                    }}"
        }
    }

    /**
     * 类似git的delta，保存两个目标的pair
     */
    class SafAndFileDiffPair(
        val safFile: DocumentFile,
        val file: File,
        val diffType:SafAndFileDiffType
    )

    enum class SafAndFileDiffType(val code: Int) {
        /**
         * 没区别，完全一样
         */
        NONE(0),

        /**
         * 内容不同
         */
        CONTENT(1),

        /**
         * 类型不同，例如一个是目录，另一个是文件
         */
        TYPE(2)
        ;

        companion object {
            fun fromCode(code: Int): SafAndFileDiffType? {
                return SafAndFileDiffType.entries.find { it.code == code }
            }
        }
    }

    /**
     * 打开uri失败
     */
    class OpenInputStreamFailed(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
        // 可以在这里添加额外的属性或方法
    }

    /**
     * 比较DocumentFile和File目录的区别，保存结果盗result
     */
    fun recursiveCompareFiles_Saf(
        contentResolver: ContentResolver,
        safFiles:Array<DocumentFile>,
        files:Array<File>,
        result:SafAndFileCompareResult,
        canceled:()->Boolean,
    ) {
        if(canceled()) {
            throw CancellationException()
        }

        //两个目录都是空，没有任何需要添加的东西
        if(safFiles.isEmpty() && files.isEmpty()) {
            return
        }

        val safFiles = safFiles.toMutableList()
        val files = files.toMutableList()

        for(f in files) {

            if(canceled()) {
                throw CancellationException()
            }

            //saff = saf file
            val saffIndex = safFiles.indexOfFirst { f.name == it.name }
            if(saffIndex != -1) {
                val saff = safFiles[saffIndex]

                //注：没考虑符号链接或者documentFile的isVirtual之类的
                // 比较两个文件是否一样
                if((f.isFile.not() && saff.isFile) || (f.isFile && saff.isFile.not()) ) {
                    result.bothAndNotSame.add(
                        SafAndFileDiffPair(
                            safFile = saff,
                            file = f,
                            diffType = SafAndFileDiffType.TYPE
                        )
                    )
                }else if(f.isFile && saff.isFile) {
                    val diffType = if(f.length() != saff.length()) {
                        SafAndFileDiffType.CONTENT
                    }else { //大小一样，逐字节比较
                        val saffis = contentResolver.openInputStream(saff.uri) ?: throw OpenInputStreamFailed(message = "open InputStream for uri failed: uri = '${saff.uri}'")
                        val fiBuf = IOUtil.createByteBuffer()
                        val saffiBuf = IOUtil.createByteBuffer()
                        //初始为相同，若有不同，更新此变量
                        var type = SafAndFileDiffType.NONE
                        //开始逐字节比较
                        f.inputStream().use { fis ->
                            saffis.use { saffis ->
                                while (true) {
                                    val fb = IOUtil.readBytes(fis, fiBuf)
                                    if(fb < 1) {  //实际读了0个字节，换句话说，eof了
                                        break
                                    }

                                    //因为两个文件大小且buffer的size一样，所以按顺序读的话，fb和safb应该大小始终一样
                                    IOUtil.readBytes(saffis, saffiBuf)

                                    if(IOUtil.bytesAreNotEquals(fiBuf, saffiBuf, 0, fb)) {
                                        type = SafAndFileDiffType.CONTENT
                                        break
                                    }
                                }
                            }
                        }

                        type
                    }

                    //不一样就添加，一样就算了
                    if(diffType != SafAndFileDiffType.NONE) {
                        result.bothAndNotSame.add(
                            SafAndFileDiffPair(
                                safFile = saff,
                                file = f,
                                diffType = diffType
                            )
                        )
                    }  // else files are the same
                }else if(f.isDirectory && saff.isDirectory) {
                    recursiveCompareFiles_Saf(contentResolver, saff.listFiles() ?: arrayOf(), f.listFiles() ?: arrayOf(), result, canceled)
                }

                safFiles.removeAt(saffIndex)
            }else {  //no matched only f in files
                result.onlyInFiles.add(f)
            }
        }

        result.onlyInSaf.addAll(safFiles)
    }

}
