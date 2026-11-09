package com.catpuppyapp.puppygit.utils

import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.utils.base.DateNamedFileWriter
import java.io.File
import java.time.LocalDateTime

private const val TAG = "EditCache"

object EditCache: DateNamedFileWriter(
    logTagOfSubClass = TAG,
    fileNameTag = "EditCache",
) {

    private var enable = true  //是否启用EditCache。（这的赋值只是初始值，实际会在init方法里更新此值，那个值才是真正有效的，而init的值与设置项对应条目关联）


    private var isInited = false

    //用来检测是否重复，若重复则只存第一个
    private var lastSavedText = mutableStateOf("")

    /**
     * 此函数可重复调用
     */
    fun init(enableCache:Boolean, cacheDir:File, keepInDays: Int=fileKeepDays) {
        try {
            //这两个变量删除过期文件需要用，所以无论是否启用cache，都初始化这两个变量
            super.init(cacheDir, keepInDays)

            //只有启用cache才有必要初始化writer，否则根本不会写入文件，自然也不需要初始化writer
            //及时禁用也不终止writer协程，不过调用write将不会执行操作
            enable = enableCache
            if(enableCache) {
                isInited=true
                startWriter()
            }else {
                //禁用cache的情况下，不会初始化writer，所以初始化flag应该为假，然后写入的时候会因为未init而不执行写入
                isInited = false
            }
        }catch (e:Exception) {
            isInited=false

            MyLog.e(TAG, "#init err: "+e.stackTraceToString())

        }
    }


    /**
     * @param text  要写入的内容
     */
    fun writeToFile(text: String) {
        //只有 启用 且 完成初始化 才 缓存输入
        if(!enable || !isInited) {
            return
        }

        doJob job@{
            try {
                //删掉尾部空白字符，开头空白字符不要删，因为开头有可能是缩进，但末尾的空白字符毫无意义，可删
                val text = text.trimEnd()

                //若内容为空 或 和上次写入内容相同 则 跳过
                if (text.isBlank() || text == lastSavedText.value) {
                    return@job
                }

                lastSavedText.value = text

                //初始化完毕并且启用cache，则写入内容到cache
                val nowTimestamp = contentTimestampFormatter.format(LocalDateTime.now())
                val needWriteMessage = "-------- $nowTimestamp :\n$text"

                sendMsgToWriter(nowTimestamp, needWriteMessage)

            } catch (e: Exception) {
                MyLog.e(TAG, "#writeToFile err: "+e.stackTraceToString())
            }
        }
    }

}
