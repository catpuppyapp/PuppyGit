package com.catpuppyapp.puppygit.utils.storagepaths

import android.content.Context
import com.catpuppyapp.puppygit.screen.functions.getFilesScreenTitle
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.JsonUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.encodeToStream
import java.io.File


object StoragePathsMan {
    private val TAG = "StoragePathsMan"

//    private var _limit = 100000  // no limit yet, manage by user, user can delete unwanted items
    private val fileName = "storage_paths.json"

    private lateinit var _file: File
    private lateinit var _saveDir: File

    private lateinit var paths: StoragePaths
    private val lock = Mutex()

    /**
     *
     * should run this method after AppModel and Settings and MyLog init done
     *
     * @param oldSettingsStoragePaths if not null, will copy items into new place
     * @param oldSettingsLastSelectedPath if not null, will copy items into new place
     */
    fun init(saveDir:File, oldSettingsStoragePaths:List<String>?, oldSettingsLastSelectedPath:String?) {
        val saveDirPath = saveDir.canonicalPath
        _saveDir = File(saveDirPath)
        _file = File(saveDirPath, fileName)

        readFromFile()

        if(oldSettingsStoragePaths!=null || oldSettingsLastSelectedPath!=null) {
            migrateFromOldSettings(oldSettingsStoragePaths ?: listOf(), oldSettingsLastSelectedPath ?: "")
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

    private fun readFromFile() {
        val f = getFile()
        try {
            paths = JsonUtil.j.decodeFromString<StoragePaths>(f.readText())
        }catch (e:Exception) {
            // err is ok, just return a new one, when set, will overwrite old file
//            paths = StoragePaths()
//            save(paths)  // when read failed, override settings file with a empty paths list

            // when read failed, clear invalid content
            reset()
            MyLog.e(TAG, "#readFromFile: read err, file content empty or corrupted, will return a new object, err is: ${e.localizedMessage}")
        }
    }

    fun get():StoragePaths {
        return paths.copy()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun save(newPaths:StoragePaths) {
        doJobThenOffLoading {
            try {
                paths = newPaths
                lock.withLock {
                    JsonUtil.j.encodeToStream(newPaths, getFile().outputStream())
                }

            }catch (e:Exception) {
                MyLog.e(TAG, "#save: save storage paths err: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }


    private fun migrateFromOldSettings(oldSettingsStoragePaths: List<String>, oldSettingsLastSelectedPath: String) {
        try {
            val p = get()

            val needMigratePaths = oldSettingsStoragePaths.isNotEmpty()
            if(needMigratePaths) {
                p.storagePaths.addAll(oldSettingsStoragePaths)
            }

            val needMigrateSelectedPaths = oldSettingsLastSelectedPath.isNotBlank()
            if(needMigrateSelectedPaths) {
                // if new path never set, update it to old one, else don't override it
                if(p.storagePathLastSelected.isBlank()) {
                    p.storagePathLastSelected = oldSettingsLastSelectedPath
                }
            }

            if(needMigratePaths || needMigrateSelectedPaths) {
                SettingsUtil.update {
                    it.storagePaths.clear()
                    it.storagePathLastSelected=""
                }

                save(p)
            }

        }catch (e:Exception) {
            MyLog.e(TAG, "#migrateFromOldSettings err: ${e.localizedMessage}")
        }
    }

    fun update(modify:(storagePaths: StoragePaths)->Unit) {
        val tmp = get()
        modify(tmp)
        save(tmp)
    }

    fun reset() {
        paths = StoragePaths()
        save(paths)
    }

    fun allowAddPath(path:String): Boolean {
        // if is internal storage path, disallow to add
        return path != FsUtils.getInternalStorageRootPathNoEndsWithSeparator()
    }

    fun getItemName(path: String, context: Context) = getFilesScreenTitle(path, context)

}
