package com.catpuppyapp.puppygit.utils

import android.content.Context
import android.util.Log
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.base.DateNamedFileWriter
import java.io.File
import java.time.LocalDateTime


//origin of this class(已改得面目全非了，看不看这链接意义不大了): https://www.cnblogs.com/changyiqiang/p/11225350.html

private const val TAG = "MyLog"

object MyLog: DateNamedFileWriter(
    logTagOfSubClass = TAG,
    fileNameTag = "Log",
) {

    private const val DISABLED_SIGN = "0"
    val logLevelList = listOf(
        DISABLED_SIGN,
        "e",
        "w",
        "i",
        "d",
        "v",
    )


    private const val MYLOG_SWITCH = true // 日志文件总开关
    private const val MYLOG_WRITE_TO_FILE = true // 日志写入文件开关
    var myLogLevel = 'w' // 日志等级，w代表只输出告警信息等，v代表输出所有信息, log level is err>warn>info>debug>verbose, low level include high level output

    //指示当前类是否完成初始化的变量，若未初始化，意味着没设置必须的参数，这时候无法记日志
    private var isInited = false


    /**
     * 此函数可重复调用
     */
    fun init(logDir:File, logKeepDays: Int= fileKeepDays, logLevel: Char=myLogLevel) {
        try {
            isInited = true
            myLogLevel = logLevel

            super.init(logDir, logKeepDays)
            startWriter()
        }catch (e:Exception) {
            isInited = false
            try {
                e.printStackTrace()
                Log.e(TAG, "#init MyLog err: "+e.stackTraceToString())
            }catch (e:Exception) {
                e.printStackTrace()
            }

        }
    }

    fun setLogLevel(level:Char) {
        myLogLevel = level
    }


    fun w(tag: String, msg: Any) { // 警告信息
        log(tag, msg.toString(), 'w')
    }

    fun e(tag: String, msg: Any) { // 错误信息
        log(tag, msg.toString(), 'e')
    }

    fun d(tag: String, msg: Any) { // 调试信息
        log(tag, msg.toString(), 'd')
    }

    fun i(tag: String, msg: Any) { //info
        log(tag, msg.toString(), 'i')
    }

    fun v(tag: String, msg: Any) {  //详细
        log(tag, msg.toString(), 'v')
    }

    fun w(tag: String, text: String) {
        log(tag, text, 'w')
    }

    fun e(tag: String, text: String) {
        log(tag, text, 'e')
    }

    fun d(tag: String, text: String) {
        log(tag, text, 'd')
    }

    fun i(tag: String, text: String) {
        log(tag, text, 'i')
    }

    fun v(tag: String, text: String) {
        log(tag, text, 'v')
    }

    /**
     * 根据tag, msg和等级，输出日志
     * @param tag
     * @param msg
     * @param level
     */
    private fun log(tag: String, msg: String, level: Char) {
        try {
            if(level.toString() == DISABLED_SIGN) {
                return
            }

            //如果未初始化MyLog，无法记日志，用安卓官方Log类打印下，然后返回
            if(isInited.not()) {
                if(level == 'e') {
                    Log.e(tag, msg)
                }else if(level == 'w') {
                    Log.w(tag, msg)
                }else if(level == 'i') {
                    Log.i(tag, msg)
                }else if(level == 'd') {
                    Log.d(tag, msg)
                }else if(level == 'v') {
                    Log.v(tag, msg)
                }else {  // should not in here if everything ok
                    Log.d(tag, msg)
                }

                return
            }

            if (MYLOG_SWITCH) { //日志文件总开关
                var isGoodLevel = true
                if ('e' == myLogLevel && 'e' == level) { // 输出错误信息
                    Log.e(tag, msg)
                } else if ('w' == myLogLevel && ('w' == level || 'e' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else {
                        Log.e(tag, msg)
                    }
                } else if ('i' == myLogLevel && ('w' == level || 'e' == level || 'i' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else if ('e' == level) {
                        Log.e(tag, msg)
                    }else {
                        Log.i(tag, msg)
                    }
                } else if ('d' == myLogLevel && ('w' == level || 'e' == level || 'd' == level || 'i' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else if ('e' == level) {
                        Log.e(tag, msg)
                    } else if ('d' == level) {
                        Log.d(tag, msg)
                    } else {
                        Log.i(tag, msg)
                    }
                } else if ('v' == myLogLevel && ('w' == level || 'e' == level || 'd' == level || 'i' == level || 'v' == level)) {
                    if ('w' == level) {
                        Log.w(tag, msg)
                    } else if ('e' == level) {
                        Log.e(tag, msg)
                    } else if ('d' == level) {
                        Log.d(tag, msg)
                    } else if ('i' == level) {
                        Log.i(tag, msg)
                    } else {
                        Log.v(tag, msg)
                    }
                } else {
                    // ignore the log msg if isn't against the log level
                    //例如：日志等级设置为 e，但请求输出的是 w 类型的日志，就会执行到这里，既不打印日志，也不保存日志到文件
                    isGoodLevel = false
                }

                //如果等级正确且写入文件开关为打开，写入日志到文件
                if (isGoodLevel && MYLOG_WRITE_TO_FILE) { //日志写入文件开关
                    doJobThenOffLoading {
                        writeLogToFile(level.toString(), tag, msg)
                    }
                }
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 打开日志文件并写入日志
     * @param mylogtype
     * @param tag
     * @param text
     */
    private suspend fun writeLogToFile(mylogtype: String, tag: String, text: String) { // 新建或打开日志文件
        try {
            val nowTimestamp = contentTimestampFormatter.format(LocalDateTime.now())
            //e.g. "2025-04-14 12:35:00 | w | ClassName | #fun: err: error msg"
            val needWriteMessage = "$nowTimestamp | $mylogtype | $tag | $text"

            sendMsgToWriter(nowTimestamp, needWriteMessage)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "#writeLogToFile err: "+e.stackTraceToString())
        }
    }

    fun getTextByLogLevel(level:String, context: Context):String {
        if(level == DISABLED_SIGN) {
            return context.getString(R.string.disable)
        } else if(level == "e") {
            return context.getString(R.string.error)
        }else if(level=="w"){
            return context.getString(R.string.warn)
        }else if(level=="i"){
            return context.getString(R.string.info)
        }else if(level=="d"){
            return context.getString(R.string.debug)
        }else if(level=="v"){
            return context.getString(R.string.verbose)
        }else {
            return context.getString(R.string.unknown)
        }
    }

    fun getCurrentLogLevel():String {
        return ""+myLogLevel
    }

}
