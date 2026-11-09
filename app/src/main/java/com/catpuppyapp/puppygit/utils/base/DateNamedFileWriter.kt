package com.catpuppyapp.puppygit.utils.base

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * 文件名带日期的writer，记日志用到这个类，例如根据日期创建文件，然后往里面追加内容
 */
open class DateNamedFileWriter(
    private val logTagOfSubClass:String,  //日志tag，因为这个类并不直接使用，往往作为工具类的基类，所以log tag用其子类的，这样能知道是哪个类发生了错误
    private val fileNameTag:String,  //文件名标签，可用来区分这文件是干嘛的，例如带"2025-02-02#Log.txt"，#后面就是标签，而Log表明这文件记录的是日志
    private val fileExt:String=".txt",
    private val fileNameSeparator:String="#",
    var fileKeepDays:Int = 3,
    channelBufferSize:Int = 50,  //队列设置大一些才有意义，不然跟互斥锁没差，话说kotlin里没公平锁吗？非得这么麻烦
    private val maxErrCount:Int = 5,  //若写入出错，最多重试次数，每次重试都会重新initWriter，若重试超过次数还是写入失败，将终止协程，再次执行类的init函数才会重新创建协程
) {
    private val writeLock = Mutex()
    private val writeChannel = Channel<String> (capacity = channelBufferSize, onBufferOverflow = BufferOverflow.SUSPEND) { /* onUndeliveredElement, 未交付的元素会调用此方法，一般用来执行关流之类的善后操作，不过我这用不上 */ }

    private val fileNameDateFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    protected val contentTimestampFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 日志的输出格式

    private var fileWriter: BufferedWriter? = null
    protected var file:File? = null

    private val writerJob: MutableState<Job?> = mutableStateOf(null)

    private var saveDir:File?=null
        get() {
            //若field不为null，检查目录是否存在，若不存在则创建
            field?.let {
                //若日志目录不存在则创建
                if(!it.exists()) {
                    it.mkdirs()
                }
            }

            //正常来说这里返回的应该是一定存在的目录或者null，因为上面不存在则创建了
            return field
        }

    protected fun init(saveDir:File, fileKeepDays: Int = this.fileKeepDays) {
        this.saveDir = saveDir
        this.fileKeepDays = fileKeepDays
    }

    protected fun startWriter() {
        if(writerJob.value != null) {
            return
        }

        writerJob.value = doJobThenOffLoading {
            var (file, writer) = initWriter()
            var errCountLimit = maxErrCount
            while (errCountLimit > 0) {
                //channel外面加互斥锁，这样就相当于一个公平锁了（带队列的互斥锁）,即使误开多个writer也不用担心冲突了
                writeLock.withLock {
                    val textWillWrite = writeChannel.receive()

                    while (errCountLimit > 0) {
                        try {
                            //用户可能会删除文件，删除文件其实还好，write时应该会自动创建（未测试），但用户搞不好连整个文件的保存目录都删除，所以还是需要判断下
                            if (file.exists().not()) {
                                val pair = initWriter()
                                file = pair.first
                                writer = pair.second
                            }

                            writer.write(textWillWrite + "\n\n")
                            writer.flush()

                            errCountLimit = maxErrCount
                            break
                        } catch (e: Exception) {
                            errCountLimit--
                            val pair = initWriter()
                            file = pair.first
                            writer = pair.second
                            Log.e(logTagOfSubClass, "write to file err: ${e.stackTraceToString()}")
                        }
                    }
                }

            }

            writerJob.value = null
        }
    }


    private fun getDateFromFileName(fileName:String):String {
        return try {
            fileName.split(this.fileNameSeparator)[0]
        }catch (e:Exception) {
            Log.e(logTagOfSubClass, "#getDateFromFileName err: ${e.stackTraceToString()}")
            ""
        }
    }

    private fun getFileName(): String {
        val datePrefix = fileNameDateFormatter.format(LocalDateTime.now())
        //eg: 2024-05-15#Log.txt
        return datePrefix + this.fileNameSeparator + fileNameTag + fileExt
    }


    private fun dateChanged(nowTimestamp:String, fileNameTimestamp:String):Boolean {
        // e.g. "2025-02-12 01:00:00".startsWith("2025-02-12")，true代表日期没变，否则代表变了
        return nowTimestamp.startsWith(fileNameTimestamp).not()
    }

    //如果日期改变，关流（然后就会触发重新创建当前日期的文件）
    private fun closeWriterIfTimeChanged(nowTimestamp:String){
        file?.let {
            if(dateChanged(nowTimestamp, getDateFromFileName(it.name))) {
                //关流，然后就会触发重新创建文件
                fileWriter?.close()
            }
        }
    }


    protected suspend fun sendMsgToWriter(nowTimestamp:String, msg:String){
        //若日期改变，关流触发重新创建当前日期的文件
        closeWriterIfTimeChanged(nowTimestamp)

        writeChannel.send(msg)
    }

    private fun initWriter(charset: Charset = StandardCharsets.UTF_8):Pair<File, BufferedWriter>{
        val funName = "initWriter"


        val dirsFile = saveDir!!
        if (!dirsFile.exists()) {
            dirsFile.mkdirs()
        }


        val file = File(dirsFile.getCanonicalPath(), getFileName())

        this.file = file
        if (!file.exists()) {
            //在指定的文件夹中创建文件
            file.createNewFile()
        }

        //如果writer不为null，先关流
        try {
            fileWriter?.close()
        }catch (e:Exception) {
            Log.e(logTagOfSubClass, "#$funName err: ${e.stackTraceToString()}")
        }

        //新开一个writer
        val append = true
        val filerWriter = OutputStreamWriter(FileOutputStream(file, append), charset)
        val newBufferedWriter = filerWriter.buffered()
        fileWriter = newBufferedWriter

        return Pair(file, newBufferedWriter)
    }

    /**
     * 删除过期的日志文件
     */
    fun delExpiredFiles() { // 删除日志文件
        val funName = "delExpiredFiles"

        try {
            MyLog.d(logTagOfSubClass, "#$funName, start: del expired '$fileNameTag' files")

            val dirPath = saveDir!! //获取日志路径

            //获取1970年1月1日到今天的天数
            val todayInDay = LocalDate.now().toEpochDay()
            val fileList = dirPath.listFiles()?:return
            for (f in fileList) {  //Objects.requireNonNull(param) ，param为null则抛异常
                if (f == null) {
                    continue
                }
                try {
                    val dateStrFromFileName = getDateFromFileName(f.name) //返回值类似 2024-04-08，和文件名中包含的日期格式必须匹配，否则会解析失败
                    val fileCreatedDateInDay = LocalDate.from(fileNameDateFormatter.parse(dateStrFromFileName)).toEpochDay()
                    val diffInDay = todayInDay - fileCreatedDateInDay //计算今天和文件名日期相差的天数

                    //debug
//                    System.out.println("diffInDay:::"+diffInDay+", now:"+logfile.format(new Date())+" other:"+dateStrFromFileName);
                    //debug

                    //删除超过天数的日志文件
                    if (diffInDay > fileKeepDays) {
                        f.delete()
                    }
                } catch (e: Exception) {
                    //日志类初始化完毕之后才执行此方法，所以，这里可以记录日志
                    MyLog.e(logTagOfSubClass, "#$funName, looping err: "+e.stackTraceToString())
//                    e.printStackTrace()
                    continue
                }
            }

            MyLog.d(logTagOfSubClass, "#$funName, end: del expired '$fileNameTag' files")

        } catch (e: Exception) {
            MyLog.e(logTagOfSubClass, "#$funName, err: "+e.stackTraceToString())
        }
    }

}
