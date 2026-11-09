package com.catpuppyapp.puppygit.utils
import java.io.File
import kotlin.concurrent.thread

object LfsUtil {
    private const val TAG = "LfsUtil"

    fun makeGitLfsBinExecutable() {
        val file = File(FsUtils.getLfsBinPath())
        MyLog.d(TAG, "file can execute: ${file.canExecute()}")
        if(!file.canExecute()) {
            val chmodProcess = Runtime.getRuntime().exec(arrayOf("chmod", "+x", file.canonicalPath))
            chmodProcess.waitFor()
        }
    }

    fun test() {
        val process = ProcessBuilder(FsUtils.getLfsBinPath(), "--version")
            .directory(FsUtils.getInnerFilesBinDir())
            .start()
        readProcessOutput(process, "test")
    }

    fun runGitLfs(repoPath: String) {
        try {
            val process = ProcessBuilder(FsUtils.getLfsBinPath(), "fetch")
                .directory(File(repoPath))
                // 注意：这里不要加 redirectErrorStream 了
                .start()

            readProcessOutput(process, "runGitLfs")
        } catch (e: Exception) {
            throw e
        }
    }

    private fun readProcessOutput(process: Process, actTag: String) {
        thread {
            process.inputStream.bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    MyLog.d(TAG, "$actTag, [STDOUT]: $line") // 替换为你的 Log.d
                }
            }
        }

        // 2. 异步读取标准错误 (stderr)
        thread {
            process.errorStream.bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    MyLog.e(TAG, "$actTag, [STDERR]: $line") // 替换为你的 Log.e
                }
            }
        }

        // 3. 主线程同步等待子进程结束
        val exitCode = process.waitFor()
        MyLog.d(TAG, "$actTag finished, exitCode: $exitCode")

    }
}
