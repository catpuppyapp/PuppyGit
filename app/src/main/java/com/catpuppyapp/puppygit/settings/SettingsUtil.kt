package com.catpuppyapp.puppygit.settings

import com.catpuppyapp.puppygit.settings.version.SettingsVersion
import com.catpuppyapp.puppygit.utils.JsonUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object SettingsUtil {
    private const val TAG = "SettingsUtil"
    private const val settingsFileName= "PuppyGitSettings.json"
    private const val settingsBakFileName= "PuppyGitSettings-bak.json"
//    private lateinit var settingsSaveDir:File
    private val createFileLock = Mutex()

    //保存文件相关
    private val saveLock = Mutex()
    private const val channelBufferSize = 50
    private val saveChannel = Channel<AppSettings>(capacity = channelBufferSize, onBufferOverflow = BufferOverflow.SUSPEND)
    private val saveJobStarted = AtomicBoolean(false)

//    private val appSettingsRwLock: ReadWriteLock = ReentrantReadWriteLock()
    // have an user said he was got appSettings NPE err, I am not sure, maybe is concurrency problem, so I add a RW lock for appSettings.
    private var appSettings:AppSettings = AppSettings()
//        get() = appSettingsRwLock.readLock().withLock { field }
//        set(value) = appSettingsRwLock.writeLock().withLock { field = value }

    // if only need read some fields, better use this no-lock instance
//    private var appSettingsReadOnly: AppSettings = AppSettings()

    private lateinit var settingsFile:File
    private lateinit var settingsBakFile:File
    private lateinit var saveDir:File

//    fun getSettingsFile():File {
//        return
//    }

    /**
     * this method may do over once,so need a mechanism to promise only start 1 save job
     */
    suspend fun init(settingsSaveDir:File, useBak:Boolean=false) {  //入参是保存settings的目录
        // settings files save dir
        val saveDirPath = settingsSaveDir.canonicalPath
        saveDir = File(saveDirPath)
        //settings文件
        settingsFile = File(saveDirPath, settingsFileName)
        settingsBakFile = File(saveDirPath, settingsBakFileName)

        //如果文件不存在则创建
        createFileIfNonExists()

        //启动保存任务
        if(!saveJobStarted.get()){
            saveJobStarted.compareAndSet(false, true)
            startSaveJob()
        }
        //读取配置项
        readSettingsFromFile(useBak)
    }

    fun destroyer() {
        saveChannel.close()
        saveJobStarted.compareAndSet(true, false)
    }

    private fun startSaveJob() {
        val logTag="settingsSaveJob"

        doJobThenOffLoading {
            var errCountLimit = 3
            var closed = false

            while (errCountLimit > 0 && closed.not()) {
                try {
                    saveLock.withLock {
                        //接收新的配置项
                        var newSettings = saveChannel.receive()

                        //尝试取出最多n个候选settings，这样做的目的是当同一时间有多个待写入任务时，直接取最后一个请求者，避免中间的写入，减少io，但也不能一直不写，所以设置一个限制，超过这个限制，则写入一次，然后重新接收
                        var count = 0
                        //尝试获取队列后面的条目。
                        //循环实际终止条件为：候选条目大于限制大小 或 无候选条目
                        //如果用我这种做法，在receive和tryReceive之间要么间隔长，要么队列有缓冲，否则意义不大
                        while (count++ < channelBufferSize) {
                            val result = saveChannel.tryReceive()
                            if (result.isSuccess) {
                                newSettings = result.getOrElse { newSettings }
                            } else {  //无候选设置项，终止（所谓的“无候选”，直白说就是接收到设置项后，后面没人再发新的设置项过来）
                                break
                            }
                        }

                        //更新类里的配置项
                        appSettings = newSettings
//                        appSettingsReadOnly = newSettings.copy()
                        //json保存到bak文件 和 拷贝bak到原始文件 若目标文件不存在都会自动创建文件，
                        // 所以这里不需要判断文件是否存在，直接写入就行。（就算不是其实也无所谓，顶多崩溃，重启app就解决了，所以问题不大）
                        //更新配置文件
                        saveSettings(newSettings)

                    }
                } catch (e: Exception) {
                    if(saveChannel.tryReceive().isClosed) {
                        closed = true
                    }
                    errCountLimit--

                    MyLog.e(TAG, "$logTag: write config to settings file err: ${e.stackTraceToString()}")
                }
            }

            //这里要不要关流？万一有多个writer协程，但其中一个失败其余不失败呢？不过目前就一个writer且没计划添加更多，所以，关了吧先
            if(saveChannel.tryReceive().isClosed.not()) {
                saveChannel.close()
            }

        }
    }

    private suspend fun createFileIfNonExists() {
        createFileLock.withLock {
            if(saveDir.exists().not()) {
                saveDir.mkdirs()
            }

            if (!settingsFile.exists()) {
                settingsFile.createNewFile()
            }

            if (!settingsBakFile.exists()) {
                settingsBakFile.createNewFile()
            }
        }
    }

    private suspend fun readSettingsFromFile(useBak: Boolean) {
        createFileIfNonExists()

        val settingsStr = if(useBak) settingsBakFile.readText() else settingsFile.readText();
        if(settingsStr.isBlank()) {  //文件内容为空，可能刚新建的，往里面存入内容
            //新建对象，然后写入文件
            val newSettings = getNewSettings()
            appSettings = newSettings
//            appSettingsReadOnly = newSettings.copy()
            //新创建的肯定不用迁移，直接保存即可
            doJobThenOffLoading {
                saveChannel.send(newSettings)
            }
        }else {  //文件若存在，读取即可
            val newSettings = JsonUtil.j.decodeFromString<AppSettings>(settingsStr)
            appSettings = newSettings
//            appSettingsReadOnly = newSettings.copy()
            //检查是否需要迁移，如果需要，会执行迁移，并对appSettings重新赋值
            migrateIfNeed()
        }

    }

    //对于旧版本的设置项，可能需要迁移，新创建的版本号是当下最新，则不需要
    private fun migrateIfNeed() {
        val oldVersion = appSettings.version
        val newVersion = SettingsVersion.appSettingsCurVersion
        if(oldVersion != newVersion) {
            //TODO migrate
            //把新版本添加的字段加进去就行，至于删除的字段，不用管，创建json对象时会忽略，然后再一写入，就覆盖了
            //新创建一个当前类对象appSettings的拷贝，然后设置字段，设置完字段之后，重新赋值给 appSettings 字段，然后执行 updateSettings() 即可

//            val tmpNewVersion = appSettings.copy(version = newVersion)
//            //设置新增字段
//            tmpNewVersion.newFieldsxxx = valueisxxx
//            //更新配置文件
//            updateSettings(tmpNewVersion)

            //结束！
        }

    }

    //返回一个新的初始化过的settings对象，用在第一次创建配置文件时
    private fun getNewSettings():AppSettings {
        val newSettings = AppSettings()
        newSettings.version = SettingsVersion.appSettingsCurVersion  //新文件设置为最新版本号
        //TODO 初始化其他字段的值，如果有必要的话

        return newSettings
    }

    //为settings实例创建快照，实际就是返回一份对象的拷贝，如果指定对象，则返回指定对象的拷贝，如果不指定则返回SettingsUtil类持有的settings对象的拷贝（不一定和硬盘上的一致，但一般都是最新）
//    fun getSettingsSnapshot(src:AppSettings = appSettings):AppSettings {
    fun getSettingsSnapshot():AppSettings {
        return getSettingsSnapshotFromSrc(appSettings)
    }

    private fun getSettingsSnapshotFromSrc(src:AppSettings):AppSettings {
        return src.copy()
    }

    /**
     * usage:
     * Settings.Util.update(false) { settings ->
     *  settings.abc=abc
     *  settings.def=def
     *  ... 其他修改
     * }
     * 就行了，这个调用执行完，会自动保存，如果想修改完后获取一份最新的设置项拷贝，传true即可，例如：
     * val updatedNewSettings = Settings.Util.update(true) { settings ->
     *                             settings.abc=abc
     *                             settings.def=def
     *                             ... 其他修改
     *                  }
     */
    fun update(requireReturnSnapshotOfUpdatedSettings:Boolean=false, modifySettings:(AppSettings)->Unit):AppSettings? {
        val settingsForUpdate = getSettingsSnapshot()  //获取一份设置项拷贝

        modifySettings(settingsForUpdate)  //对拷贝执行修改

        //这里不用创建拷贝，因为updateSetting仅读取变量，不会修改
        //不过这里保险起见其实也应该创建个拷贝再保存，算了，没必要，返回的时候已经创建了拷贝，这里这个变量只有更新方法使用，没必要多创建一个拷贝
        updateSettings(settingsForUpdate)  //保存修改

        //返回最新设置项的拷贝，如果需要的话，这个其实获取的不是最新的！上面写入是异步的有可能现在获取的时候还没写入呢
//        return if(requireReturnAnNewSettings) getSettingsSnapshot() else null  //返回最新的设置项，如果需要的话(否则的话，你的设置项可能已经过期，某些设置不是最新，但是，如果你需要用到的变量很少，并且就算不是最新也问题不大，就没必要获取最新设置项的拷贝了)

        //返回的时候要创建拷贝，避免和update用同一个变量，不然外部一修改返回值里的数据，即将写入的状态也会被影响，就乱套了
        //这里获取刚被修改过的实例的拷贝，相对更新一些，但可能和硬盘上的数据不一致，但正常来说，最终修改写入硬盘后，内容会一致
        return if(requireReturnSnapshotOfUpdatedSettings) getSettingsSnapshotFromSrc(settingsForUpdate) else null  //返回最新的设置项，如果需要的话(否则的话，你的设置项可能已经过期，某些设置不是最新，但是，如果你需要用到的变量很少，并且就算不是最新也问题不大，就没必要获取最新设置项的拷贝了)
    }

    fun updateSettingsIfNotEqualsWithOld(newSettings: AppSettings) {  //供外部调用的更新配置文件的方法
        doJobThenOffLoading {
            try {
                //版本如果不一样，更新；版本如果一样，检查每个字段值是否相同，如果存在不相同的字段，更新
                val tmpSettings = appSettings
                if (newSettings.version != tmpSettings.version || !newSettings.equals(tmpSettings)) {  //如果新设置和旧的不一样，更新配置文件
                    saveChannel.send(newSettings)
                }

            }catch (e:Exception) {
                MyLog.e(TAG, "#updateSettingsIfNotEqualsWithOld(): update config err!\n"+e.stackTraceToString())
            }
        }
    }

    //不检查新旧是否一样，强制更新
    fun updateSettings(newSettings: AppSettings) {  //供外部调用的更新配置文件的方法
        doJobThenOffLoading {
            try{
                saveChannel.send(newSettings)
            }catch (e:Exception) {
                MyLog.e(TAG, "#updateSettings(): update config err!\n"+e.stackTraceToString())
            }
        }
    }

    /**
     * 不要直接调用此方法，应该通过writeChannel来更新配置文件
     */
    private suspend fun saveSettings(newSettings: AppSettings) {
        // avoid dir deleted by user
        createFileIfNonExists()

        //不知道kotlin怎么以覆盖模式打开文件，索性每次写入删除一下，outputStream写入的时候会新建
        //经过我观察，不用删除，默认就是覆盖
//        if(settingsFile.exists()) {
//            settingsFile.delete()
//        }

//        if(debugModeOn) {
//            MyLog.d(TAG, "#saveSettings(): settings will save:"+JsonUtil.j.encodeToString(appSettings))
//        }
        saveSettingsBak(newSettings)
        copyBakToOrigin()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveSettingsBak(newSettings: AppSettings) {
        JsonUtil.j.encodeToStream(newSettings, settingsBakFile.outputStream())
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveSettingsOrigin(newSettings: AppSettings) {
        JsonUtil.j.encodeToStream(newSettings, settingsFile.outputStream())
    }

    fun copyBakToOrigin() {
        settingsBakFile.copyTo(settingsFile, overwrite = true)
    }

    fun delSettingsFile(settingsSaveDir:File, delOrigin:Boolean=true, delBak:Boolean=true) {
        //btw: delete non-exist file no exception will throw, so need not try catch
        if(delOrigin) {
            File(settingsSaveDir.canonicalPath, settingsFileName).delete()
        }

        if(delBak) {
            File(settingsSaveDir.canonicalPath, settingsBakFileName).delete()
        }
    }

    fun obtainLastQuitHomeScreen() = appSettings.lastQuitHomeScreen
    fun editorTabIndentCount() = appSettings.editor.tabIndentSpacesCount
    fun isEditorSyntaxHighlightEnabled() = appSettings.editor.syntaxHighlightEnabled
    fun isEditorUseSystemFonts() = appSettings.editor.useSystemFonts
    fun isDiffUseSystemFonts() = appSettings.diff.useSystemFonts
    fun isDiffSyntaxHighlightEnabled() = appSettings.diff.syntaxHighlightEnabled
    fun isCommitMsgPreviewModeOn() = appSettings.commitMsgPreviewModeOn
    fun isCommitMsgUseSystemFonts() = appSettings.commitMsgUseSystemFonts
    fun isEnabledDetailsCompareForDiff() = appSettings.diff.enableDetailsCompare
    fun isEditorAutoCloseSymbolPairEnabled() = appSettings.editor.autoCloseSymbolPair

}
