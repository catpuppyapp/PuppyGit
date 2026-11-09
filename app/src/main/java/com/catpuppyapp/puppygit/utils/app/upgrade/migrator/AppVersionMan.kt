package com.catpuppyapp.puppygit.utils.app.upgrade.migrator

import com.catpuppyapp.puppygit.utils.AppModel
import java.io.File

/**
 * 管理app版本号，可以用来执行某些迁移操作，例如之前添加pushCredential后用户原本的credential未关联push就可用版本号迁移来解决
 */
object AppVersionMan {
    val currentVersion = AppModel.getAppVersionCode()

    const val filename="AppVer"
    lateinit var verFile: File

    val err_fileNonExists = -1  //文件不存在。全新安装，或者从不支持版本号的版本升级而来
    val err_parseVersionFailed = -2  //文件存在但版本号解析int失败，例如内容为空或包含非数字

    /**
     * 执行后如果版本号文件不存在或存在但依然是旧版本号，则迁移失败；只有当迁移成功后文件才会存在且为最新版本号
     */
    //if migrator return true, will upgrade version
    suspend fun init(storeDir:File=AppModel.innerDataDir, migrator: suspend (oldVer:Int) ->Boolean) {
        verFile=File(storeDir, filename)

        val oldVer = getVersionFromFile()
        val migrateSuccess = migrator(oldVer)

        if(migrateSuccess) {
            createIfNonExists()
            upgrade(currentVersion)
        }
    }

    fun createIfNonExists() {
        if(!verFile.exists()) {
            verFile.createNewFile()
        }
    }

    fun upgrade(newVer:Int) {
        verFile.writer().use {
            // toString可避免把int当byte写入而丢失内容，不过我也不确定写int会不会丢失内容，应该是会的
            it.write(newVer.toString())
        }
    }

    fun getVersionFromFile():Int {
        try {
            if(!verFile.exists()) {
                return err_fileNonExists
            }

            return verFile.bufferedReader().readLine().toInt()
        }catch (e:Exception) {
            return err_parseVersionFailed
        }
    }

}
