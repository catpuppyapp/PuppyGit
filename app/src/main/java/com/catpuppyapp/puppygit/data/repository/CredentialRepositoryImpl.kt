package com.catpuppyapp.puppygit.data.repository

import androidx.room.withTransaction
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.SpecialCredential
import com.catpuppyapp.puppygit.data.dao.CredentialDao
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.encrypt.PassEncryptHelper
import com.catpuppyapp.puppygit.utils.getDomainByUrl
import com.catpuppyapp.puppygit.utils.getSecFromTime
import kotlinx.coroutines.sync.withLock

private const val TAG = "CredentialRepositoryImpl"

//注： 不带decrypt和encrypt的查出的都是数据库中的原始数据
class CredentialRepositoryImpl(private val dao: CredentialDao) : CredentialRepository {
//    @Deprecated("dont use")
//    override fun getAllStream(): Flow<List<CredentialEntity?>> = dao.getAllStream()

//    @Deprecated("dont use")
//    override fun getStream(id: String): Flow<CredentialEntity?> = dao.getStream(id)


    override suspend fun getAllWithDecrypt(includeNone:Boolean, includeMatchByDomain:Boolean, masterPassword: String): List<CredentialEntity> {
        val all = dao.getAll().toMutableList()
        for(item in all) {
            decryptPassIfNeed(item, masterPassword)
        }

        prependSpecialItemIfNeed(list = all, includeNone = includeNone, includeMatchByDomain = includeMatchByDomain)

        return all
    }

    override suspend fun getAll(includeNone:Boolean, includeMatchByDomain:Boolean): List<CredentialEntity> {
        val list =  dao.getAll().toMutableList()

        prependSpecialItemIfNeed(list = list, includeNone = includeNone, includeMatchByDomain = includeMatchByDomain)

        return list
    }

    private fun prependSpecialItemIfNeed(list: MutableList<CredentialEntity>, includeNone:Boolean, includeMatchByDomain:Boolean) {
        if (includeMatchByDomain) {
            list.add(0, SpecialCredential.MatchByDomain.getEntityCopy())
        }

        if (includeNone) {
            list.add(0, SpecialCredential.NONE.getEntityCopy())
        }
    }

    override suspend fun insertWithEncrypt(item: CredentialEntity, masterPassword: String) {
        val funName = "insertWithEncrypt"
        Cons.credentialInsertLock.withLock {
            //如果名称已经存在则不保存
            if(isCredentialNameExist(item.name)) {
                MyLog.w(TAG, "#insertWithEncrypt(): Credential name exists, item will NOT insert! name is: '${item.name}'")
                throw RuntimeException("#$funName err: name already exists")

            }

            encryptPassIfNeed(item, masterPassword)

            dao.insert(item)
        }
    }

    override suspend fun insert(item: CredentialEntity) {
        val funName = "insert"

        Cons.credentialInsertLock.withLock {
            if (isCredentialNameExist(item.name)) {  //如果名称已经存在则不保存
                MyLog.w(TAG, "#insert(): Credential name exists, item will NOT insert! name is:" + item.name)
                throw RuntimeException("#$funName err: name already exists")

            }
            dao.insert(item)
        }
    }

    override suspend fun delete(item: CredentialEntity) = dao.delete(item)

    override suspend fun updateWithEncrypt(item: CredentialEntity, touchTime:Boolean, masterPassword: String) {
        if(SpecialCredential.isAllowedCredentialName(item.name).not()) {
            throw RuntimeException("credential name disallowed (#updateWithEncrypt)")
        }

        //如果密码不为空，加密密码。
        encryptPassIfNeed(item, masterPassword)

        if(touchTime) {
            item.baseFields.baseUpdateTime = getSecFromTime()
        }

        dao.update(item)
    }

    override suspend fun update(item: CredentialEntity, touchTime:Boolean) {
        if(SpecialCredential.isAllowedCredentialName(item.name).not()) {
            throw RuntimeException("credential name disallowed (#update)")
        }

        if(touchTime) {
            item.baseFields.baseUpdateTime = getSecFromTime()
        }

        dao.update(item)
    }

    override suspend fun isCredentialNameExist(name: String): Boolean {
        // disallowed name treat as existed
        if(SpecialCredential.isAllowedCredentialName(name).not()) {
            return true
        }

        val id = dao.getIdByCredentialName(name)
//        return id != null && id.isNotBlank()  // don't check blank may be better
        return id != null
    }

    override suspend fun getByIdWithDecrypt(id: String, masterPassword: String): CredentialEntity? {
        if(id.isBlank() || id==SpecialCredential.NONE.credentialId) {
            return null
        }

        val item = dao.getById(id)
        if(item == null) {
            return null
        }

        decryptPassIfNeed(item, masterPassword)

        return item
    }

    override suspend fun getByIdAndMatchByDomain(id: String, url: String): CredentialEntity? {
        // decryptPass若是false，并不会使用masterPassword，所以传空字符串即可
        return getByIdAndMatchByDomainAndDecryptOrNoDecrypt(id, url, decryptPass = false, masterPassword = "")
    }

    override suspend fun getByIdWithDecryptAndMatchByDomain(id: String, url: String, masterPassword: String): CredentialEntity? {
        return getByIdAndMatchByDomainAndDecryptOrNoDecrypt(id, url, decryptPass = true, masterPassword)
    }

    private suspend fun getByIdAndMatchByDomainAndDecryptOrNoDecrypt(id: String, url: String, decryptPass:Boolean, masterPassword: String): CredentialEntity? {
        if(id==SpecialCredential.MatchByDomain.credentialId) {
            val dcDb = AppModel.dbContainer.domainCredentialRepository
            val domain = getDomainByUrl(url)
            if(domain.isNotBlank()) {
                val domainCred = dcDb.getByDomain(domain) ?: return null
                val credId = if(Libgit2Helper.isSshUrl(url)) domainCred.sshCredentialId else domainCred.credentialId
                return if(decryptPass) getByIdWithDecrypt(credId, masterPassword) else getById(credId)
            }else {
                return null
            }
        }else {
            return if(decryptPass) getByIdWithDecrypt(id, masterPassword) else getById(id)
        }
    }

    override suspend fun getById(id: String, includeNone:Boolean, includeMatchByDomain:Boolean): CredentialEntity? {
        if(includeNone && id==SpecialCredential.NONE.credentialId) {
            return SpecialCredential.NONE.getEntityCopy()
        }else if(includeMatchByDomain && id == SpecialCredential.MatchByDomain.credentialId) {
            return SpecialCredential.MatchByDomain.getEntityCopy()
        }else if(id.isBlank()) {
            return null
        }

        return dao.getById(id)
    }

//    override suspend fun getListByType(type: Int): List<CredentialEntity> {
//        return dao.getListByType(type)
//    }
//
//    override suspend fun getSshList(): List<CredentialEntity> {
//        return dao.getListByType(Cons.dbCredentialTypeSsh)
//    }
//
//    override suspend fun getHttpList(includeNone:Boolean, includeMatchByDomain:Boolean): List<CredentialEntity> {
//        val list = dao.getListByType(Cons.dbCredentialTypeHttp).toMutableList()
//
//        prependSpecialItemIfNeed(list = list, includeNone = includeNone, includeMatchByDomain = includeMatchByDomain)
//
//        return list
//    }

    override suspend fun deleteAndUnlink(item:CredentialEntity) {
        val db = AppModel.dbContainer.db
        val repoDb = AppModel.dbContainer.repoRepository
        val remoteDb = AppModel.dbContainer.remoteRepository
        db.withTransaction {
            //删除凭据需要：删除凭据、解除关联remote、解除关联仓库（未克隆的那种，简单用credentialId匹配下仓库所有条目credentialId字段即可，因为不管克隆成功与否，反正这凭据要删除了，就该解除关联）
            remoteDb.updateFetchAndPushCredentialIdByCredentialId(item.id, item.id, "", "")  //解除关联remote
            repoDb.unlinkCredentialIdByCredentialId(item.id)  //解除关联repo克隆时使用的凭据
            delete(item)  //删除凭据
        }
    }

    override fun encryptPassIfNeed(item:CredentialEntity?, masterPassword:String) {
        //用户名不用加密，不过私钥呢？感觉也用不着加密，暂时只加密密码吧。
        if(item != null) {
            //加密的时候一律使用最新版本加密器加密并更新凭据中加密器版本号为最新
            val curEncryptorVersion = PassEncryptHelper.passEncryptCurrentVer
            if(item.pass.isNotEmpty()) {
    //            item.pass = PassEncryptHelper.encryptWithCurrentEncryptor(item.pass, masterPassword)
                item.pass = PassEncryptHelper.encryptWithSpecifyEncryptorVersion(curEncryptorVersion, item.pass, masterPassword)
            }

            item.encryptVer = curEncryptorVersion
        }
    }

    override fun decryptPassIfNeed(item:CredentialEntity?, masterPassword: String) {
        if (item != null && item.pass.isNotEmpty()) {
            //如果密码不为空，解密密码。
//            item.pass = PassEncryptHelper.decryptWithCurrentEncryptor(item.pass, masterPassword)
            item.pass = PassEncryptHelper.decryptWithSpecifyEncryptorVersion(item.encryptVer, item.pass, masterPassword)
        }
    }

    override suspend fun updateMasterPassword(oldMasterPassword:String, newMasterPassword:String): List<String> {
        if(oldMasterPassword == newMasterPassword) {
            MyLog.w(TAG, "old and new master passwords are the same, cancel update")
            return emptyList()
        }

        val decryptFailedList = mutableListOf<String>()

        val allCredentialList = getAll()

        //开事务，避免部分成功部分失误导致密码乱套，如果乱套只能把credential全删了重建了
        reEncryptCredentials(
            credentialList = allCredentialList,
            oldMasterPassword = oldMasterPassword,
            newMasterPassword = newMasterPassword,
            decryptFailedCallback = { cred, exception ->
                decryptFailedList.add(cred.name)
            }
        )

        return decryptFailedList
    }

    /**
     * 此函数用来更新主密码或者在加密器版本更新后执行迁移操作。
     *
     * 迁移密码本质上只有一个主密码，但为了兼容更新主密码的函数，所以添加了old和new两个主密码参数，但内部实际不对新旧主密码是否相同做检测，
     *   如果只是想升级加密器版本，old和new master password传一样即可
     *
     *
     * Actually migrate only has one master password, no old or new, but for compatible `updateMasterPassword()`, it has 2 params represents old and new password,
     *  if just want to upgrade encryptor by version, just passing same value for old and new master passwords
     */
    private suspend fun reEncryptCredentials(
        credentialList: List<CredentialEntity>,
        oldMasterPassword: String,
        newMasterPassword: String,
        decryptFailedCallback: (failedCredential:CredentialEntity, exception:Exception) -> Unit,
    ) {
        AppModel.dbContainer.db.withTransaction {
            //迁移密码
            for (c in credentialList) {
                //若密码为空，更新下加密器版本即可
                if (c.pass.isEmpty()) {
                    c.encryptVer = PassEncryptHelper.passEncryptCurrentVer
                    update(c, touchTime = false)
                    continue
                }

                //密码不为空，解密密码并重新加密
                try {
                    //如果解密失败会抛异常
                    decryptPassIfNeed(c, oldMasterPassword)  //解密密码

                } catch (e: Exception) {
                    MyLog.w(TAG, "decrypt password failed, credentialName=${c.name}, err=${e.localizedMessage}")
                    decryptFailedCallback(c, e)
                    continue
                }

                //一般加密不会失败，就不try...catch了
                //加密时更新加密器版本为最新版本
                updateWithEncrypt(c, touchTime = false, masterPassword = newMasterPassword)
            }

        }
    }

    override suspend fun migrateEncryptVerIfNeed(masterPassword: String) {
        val needUpdateEncryptVerCredList = getByEncryptVerNotEqualsTo(PassEncryptHelper.passEncryptCurrentVer)
        if(needUpdateEncryptVerCredList.isEmpty()) {
            return
        }

        val failedList = mutableListOf<CredentialEntity>()
        reEncryptCredentials(
            needUpdateEncryptVerCredList,
            masterPassword,
            masterPassword,
            decryptFailedCallback = { c, e ->
                failedList.add(c)
            }
        )

        if(failedList.isNotEmpty()) {
            //把失败列表记到log
            val split = ", "
            val sb = StringBuilder()
            for(i in failedList) {
                // "(name=c1, encryptVer=5), (name=c2, encryptVer=4)"
                sb.append("(").append("name=${i.name}, ").append("encryptVer=${i.encryptVer}").append(")").append(split)
            }

            MyLog.e(TAG, "#migrateEncryptVerIfNeed: these credentials migrate failed: ${sb.removeSuffix(split)}")
        }
    }

    override suspend fun getByEncryptVerNotEqualsTo(encryptVer:Int): List<CredentialEntity> {
        return dao.getByEncryptVerNotEqualsTo(encryptVer)
    }


    override suspend fun subtractTimeOffset(offsetInSec:Long) {
        dao.subtractTimeOffset(offsetInSec)
    }

}
