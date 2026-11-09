package com.catpuppyapp.puppygit.utils.fileopenhistory

import com.catpuppyapp.puppygit.settings.FileEditedPos
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.JsonUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.getSecFromTime
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.encodeToStream
import java.io.File


object FileOpenHistoryMan {
    // max histories count
    const val defaultHistoryMaxCount = 50

    private const val TAG = "FileOpenHistoryMan"

    private var _limit = defaultHistoryMaxCount  // will update by settings value
    private const val fileName = "file_open_history.json"

    private lateinit var _file: File
    private lateinit var _saveDir: File

    private var curHistory: FileOpenHistory = FileOpenHistory()


    private val lock = Mutex()

    /**
     *
     * should run this method after AppModel and Settings and MyLog init done
     * @param limit how many histories will remembered
     * @param requireClearOldSettingsEditedHistory if true, will clear settings remembered file edited position, caller should check before pass this value to avoid unnecessary clear
     */
    fun init(saveDir:File, limit:Int, requireClearOldSettingsEditedHistory:Boolean) {
        _limit = limit

        val saveDirPath = saveDir.canonicalPath
        _saveDir = File(saveDirPath)
        _file = File(saveDirPath, fileName)

        readHistoryFromFile()

        if(requireClearOldSettingsEditedHistory) {
            clearOldSettingsHistory()
        }
    }

    private fun getFile():File {
        if(_saveDir.exists().not()) {
            _saveDir.mkdirs()
        }

        if(_file.exists().not()) {
            _file.createNewFile()
        }

        return _file
    }

    private fun readHistoryFromFile() {
        val f = getFile()
        try {
            curHistory = JsonUtil.j.decodeFromString<FileOpenHistory>(f.readText())
        }catch (e:Exception) {
            // err is ok, just return a new one, when set, will overwrite old file
//            curHistory = FileOpenHistory()
//            saveHistory(curHistory)  // when read from file failed, save a new history to file

            // when read failed, clear invalid content
            reset()
            MyLog.e(TAG, "#readHistoryFromFile: read err, file content empty or corrupted, will return a new history, err is: ${e.localizedMessage}")
        }
    }

    fun getHistory():FileOpenHistory {
        return curHistory.copy()
    }

    fun get(path:String):FileEditedPos {
        return getHistory().storage.get(path)?.copy() ?: FileEditedPos()
    }

    fun set(path:String, lastEditedPos: FileEditedPos) {
        updateLastUsedTime(lastEditedPos)

        val h = getHistory()
        h.storage.set(path, lastEditedPos)

        if(h.storage.size > _limit) {
            removeOldHistory(h)
        }

        update(h)
    }

    fun remove(path:String) {
        val h = getHistory()
        h.storage.remove(path)

        update(h)
    }

    /**
     * update the file last used time
     */
    fun touch(path:String) {
        set(path, updateLastUsedTime(get(path)))
    }

    /**
     * update the `lastUsedTime` and return the original object which passed in
     * 更新最后使用时间并返回传入的原始对象
     */
    private fun updateLastUsedTime(lastEditedPos: FileEditedPos):FileEditedPos {
        lastEditedPos.lastUsedTime = getSecFromTime()
        return lastEditedPos
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveHistory(newHistory:FileOpenHistory) {
        doJobThenOffLoading {
            try {
                curHistory = newHistory
                lock.withLock {
                    JsonUtil.j.encodeToStream(newHistory, getFile().outputStream())
                }

            }catch (e:Exception) {
                MyLog.e(TAG, "#saveHistory: save file opened history err: ${e.localizedMessage}")
            }
        }
    }

    private fun removeOldHistory(history: FileOpenHistory) {
        val copy:Map<String, FileEditedPos> = history.storage.toMap()
        val sortedKeys = copy.keys.toSortedSet {k1, k2 ->
            val v1 = copy[k1]
            val v2 = copy[k2]

            // make bigger number before smaller number, is a DESC order
            if(v1 == null) {
                1
            }else if(v2 == null) {
                -1
            }else {
                //desc
                v2.lastUsedTime.compareTo(v1.lastUsedTime)
            }
        }

        var count = 0
        val newStorage = mutableMapOf<String, FileEditedPos>()
        for(k in sortedKeys) {
            if(count >= _limit) {
                break
            }

            val v = copy[k]
            if(v!=null) {
                newStorage.set(k, v)
                count++
            }
        }

        history.storage = newStorage
    }

    /**
     * clear file last edited positions in settings, the filed is `Settings.Editor.filesLastEditPosition`
     */
    private fun clearOldSettingsHistory() {
        try {
            SettingsUtil.update {
                it.editor.filesLastEditPosition.clear()
            }
        }catch (e:Exception) {
            MyLog.e(TAG, "#clearOldSettingsHistory err: ${e.stackTraceToString()}")
        }
    }

    /**
     * 重置文件历史记录，实际上就是简单清空
     */
    fun reset() {
        update(FileOpenHistory())
    }

    private fun update(newHistory: FileOpenHistory) {
        curHistory = newHistory
        saveHistory(newHistory)
    }

    fun subtractTimeOffset(offsetInSec:Long) {
        val newStorage = mutableMapOf<String, FileEditedPos>()

        // update old history
        getHistory().storage.forEachBetter { k, v ->
            newStorage[k] = v.copy(lastUsedTime = v.lastUsedTime - (offsetInSec))
        }

        update(FileOpenHistory(newStorage))
    }

}
