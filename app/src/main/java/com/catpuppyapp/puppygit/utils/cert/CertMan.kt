package com.catpuppyapp.puppygit.utils.cert

import android.content.Context
import com.catpuppyapp.puppygit.etc.CertSaver
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.readIntVersionFromFile
import com.catpuppyapp.puppygit.utils.writeIntVersionToFile
import com.github.git24j.core.Libgit2
import java.io.File

object CertMan {
    private const val TAG = "CertMan"

    //若更新证书，应更新此值
    const val currentVersion = 14

    private val certRawId = R.raw.cert_bundle_pem

//    private val inited = mutableStateOf(false)

    const val defaultCertBundleDirName = "cert-bundle"
    const val defaultCertUserDirName = "cert-user"  //存放用户证书的目录，自签证书之类的可以放这里，app会自动加载，自签证书并不是在加密上有问题，只是不被公众信任，用户如果自签，也并非一定不安全，另外：这块功能我没测试


    const val certBundleFileName = "cacertpem"
    const val certBundleVersionFileName = "cacertpem-version"
    private lateinit var certBundleFile:File;
    private lateinit var certBundleVersionFile:File;

    val sysCertList = listOf<CertSaver>(
        //android version <14 system cert path
        CertSaver(file=null, path="/system/etc/security/cacerts"),
        //android version>=14 system cert path
        CertSaver(file=null, path="/apex/com.android.conscrypt/cacerts"),
    );

    /**
     * @certBundleDir 存放app内置证书的目录，app会从内置资源文件中将证书内容读取到这个目录
     * @certUserDir 存放user certs的目录
     *
     */
    fun init(appContext: Context, certBundleDir:File, certUserDir:File) {
//        if(inited.value.not()) {
//            inited.value = true

        //加载app内置证书
        loadAppCert(appContext, certBundleDir)

        //加载用户证书
        loadUserCerts(certUserDir)

            //加载系统证书
//        loadSysCerts()
//        }
    }

    /**
     * 加载app内置证书：从app包中读取pem文件内容写入到文件，然后再加载此文件
     */
    fun loadAppCert(appContext: Context, certBundleDir:File) {
        //这个函数不用捕获异常，出错app无法正常工作(无法创建tls连接)，崩溃就行了。等下，算了，还是捕获下吧，万一无法加载app内置证书但能加载用户证书呢？对吧？虽然可能性不大

        try {
            if(!certBundleDir.exists()) {
                certBundleDir.mkdirs()
            }

            //先创建pem文件
            createCertBundlePemFileIfNeed(appContext, certBundleDir)

            //加载刚才创建的pem文件
            loadCert(CertSaver(file = certBundleFile.canonicalPath))
        }catch (e:Exception) {
            MyLog.e(TAG, "#loadAppCert err: ${e.stackTraceToString()}")
            Msg.requireShowLongDuration("err:load cert-bundle err! app may not work!")
        }
    }

    fun createCertBundlePemFileIfNeed(appContext: Context, certBundleDir:File) {
        //检查版本号，如果
        certBundleVersionFile = File(certBundleDir, certBundleVersionFileName)
        //读取已经存在的版本号
        val verInFile = readIntVersionFromFile(certBundleVersionFile)

        // out put file
        certBundleFile = File(certBundleDir, certBundleFileName)

        if(verInFile == currentVersion && certBundleFile.exists()) {
//            println("没重读证书")
            return
        }

        //保险起见，读取证书前至少应该删除版本号文件，避免“版本号为最新但证书文件不存在的情况，在证书写入一半时中断，最后版本号最新，证书文件存在但不完整”，为避免这种问题，读取前先删除证书版本号，读取成功再创建，这样可确保万无一失
        if(certBundleVersionFile.exists()) {
            certBundleVersionFile.delete()
        }

        //删除证书文件为可选，因为outputStream默认会创建并清空文件
        if(certBundleFile.exists()) {
            certBundleFile.delete()
        }

//        println("要读取证书了")

        //从app res读取证书内容到文件
        FsUtils.copy(appContext.resources.openRawResource(certRawId), certBundleFile.outputStream())

        //更新版本号（注：要确保读取成功后再更新版本号
        writeIntVersionToFile(certBundleVersionFile, currentVersion)
//        println("读取证书成功")
    }

    fun loadCert(certSaver:CertSaver) {
        loadCerts(listOf(certSaver))
    }

    fun loadCerts(certSaverList:List<CertSaver>) {
        for (certSaver in certSaverList) {
            try {
                Libgit2.optsGitOptSetSslCertLocations(certSaver.file, certSaver.path)
            } catch (e: Exception) {
                MyLog.e(TAG, "#loadCerts err: cert=$certSaver, err=${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    @Deprecated("改用app内置证书了，加载更快而且可随app更新而更新，比系统证书好，系统证书旧版安卓系统一用就是好几年，虽然证书也不用频繁更新就是了")
    fun loadSysCerts() {
        loadCerts(sysCertList)
    }

    /**
     * 加载用户证书对app来说并非必须，因此此函数应捕获异常，这样即使出错app也可运行
     */
    fun loadUserCerts(certUserDir:File) {
        val funName = "loadUserCerts"

        try {
            if(!certUserDir.exists()) {
                certUserDir.mkdirs()
                return
            }

            val userCerts = obtainUserCerts(certUserDir)

            //加载用户证书
            loadCerts(userCerts)

        }catch (e:Exception) {
            MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
        }

    }

    private fun obtainUserCerts(certUserDir: File): List<CertSaver> {
        val certs = mutableListOf<CertSaver>()
        certUserDir.listFiles { it:File ->
            if(it.isFile) {
                certs.add(CertSaver(file = it.canonicalPath))
            }

            // actually this lambda is a filter,
            //   return false to avoid create
            //   nonsense temp filtered list
            false
        }

        return certs
    }

}
