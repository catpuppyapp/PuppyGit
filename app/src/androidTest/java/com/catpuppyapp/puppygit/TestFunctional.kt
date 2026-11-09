package com.catpuppyapp.puppygit

//import android.content.Context
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.ui.Modifier
//import com.catpuppyapp.puppygit.jni.LibgitTwo
//import com.catpuppyapp.puppygit.utils.cert.CertMan
//import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
//import com.github.git24j.core.Clone
//import com.github.git24j.core.Config
//import com.github.git24j.core.GitObject
//import com.github.git24j.core.Libgit2
//import com.github.git24j.core.Oid
//import com.github.git24j.core.Repository
//import com.github.git24j.core.Revwalk
//import java.io.File

//
//@Composable
//fun TestRepoCloneEtc(context: Context) {
//    val r = remember { mutableStateOf("Clone") }
//    // A surface container using the 'background' color from the theme
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        Column {
//
//            Text(LibgitTwo.hello(1, 2))
//            var shit = context.getExternalFilesDir(null)
//            if(shit!=null){
//                println("shit.canonicalPath:::::" + shit.canonicalPath)  //  /storage/emulated/0/Android/data/com.catpuppyapp.puppygit/files
//
//            }
//            println("path:::context.filesDir::" + context.filesDir)  //  /storage/emulated/0/Android/data/com.catpuppyapp.puppygit/files
//            Button(onClick = { cloneRepo(context, context.getExternalFilesDir(null), r, "testgithub",
//                "https://github.com/github/testrepo.git",false) }) {
//                Text(r.value+"github")
//            }
//            Button(onClick = { cloneRepo(context, context.getExternalFilesDir(null), r, "testgitlab",
//                "https://gitlab.com/pursue/test.git", true) }) {
//                Text(r.value+"gitlab")
//            }
//            Button(onClick = { cloneRepoByGit24j(context, context.getExternalFilesDir(null), r, "testgit24j",
//                "https://gitlab.com/pursue/test.git", true) }) {
//                Text(r.value+"gitlab")
//            }
//        }
//    }
//}
//
//fun cloneRepo(context: Context, externalFilesDir: File?, r: MutableState<String>, subdir:String, url:String, allowInsecure:Boolean) {
////    Main.main(arrayOf("git","--version"))
//    if (externalFilesDir != null) {
//        println("Buttonpreessed!!!")
//        val canonicalPath = externalFilesDir.canonicalPath;
//        doJobThenOffLoading {
////        Git.cloneRepository().setURI(REMOTE_URL).setDirectory(File(externalFilesDir, "testgit123"))
////            .call();
//            var lgit2 = LibgitTwo();
//            lgit2.jniLibgitTwoInit();
//            println("initpassed!")
//            val optionsPtr = lgit2.jniCreateCloneOptions(1)
//            println("optionsptr passed")
//            println("jniCreateCloneOptions DONE::::"+optionsPtr)
//            println("optionsPtr==-1::::"+(optionsPtr==-1L))
//            val dir = File(canonicalPath + subdir)
//            if(!dir.exists()){
//                dir.mkdirs();
//            }else {
//                dir.deleteRecursively()
//                dir.mkdirs()
//            }
////            val jniClone = lgit2.jniClone(REMOTE_URL,canonicalPath, optionsPtr,false);
//            val jniClone = lgit2.jniClone(url, dir.canonicalPath, optionsPtr,allowInsecure);
//            println("jniClone DONE::::"+(jniClone==-1L))
//            println("jniClone==-1::::"+(jniClone==-1L))
//
////            lgit2.jniTestClone(REMOTE_URL,canonicalPath,0)
//            r.value = subdir+"DONE"
//        }
//    }
//
//}
//
//fun cloneRepoByGit24j(context: Context, externalFilesDir: File?, r: MutableState<String>, subdir:String, url:String, allowInsecure:Boolean) {
////    Main.main(arrayOf("git","--version"))
//    if (externalFilesDir != null) {
//        println("Buttonpreessed!!!")
//        val canonicalPath = externalFilesDir.canonicalPath;
//        val dir = File(canonicalPath + subdir)
//        if(!dir.exists()){
//            dir.mkdirs();
//        }else {
//            dir.deleteRecursively()
//            dir.mkdirs()
//        }
//        doJobThenOffLoading {
////        Git.cloneRepository().setURI(REMOTE_URL).setDirectory(File(externalFilesDir, "testgit123"))
////            .call();
//            var opts = Clone.Options.defaultOpts();
//            LibgitTwo.jniSetCertFileAndOrPath(null,"/system/etc/security/cacerts/")
//            Clone.cloneRepo(url, canonicalPath + subdir, opts);
//
//            r.value = subdir+"DONE"
//        }
//    }
//
//}
//
//fun testLibgit2Opts() {
//
//    Libgit2.optsGitOptSetMwindowSize(1020L);
//    println("Libgit2.optsGitOptGetMwindowSize():::"+ Libgit2.optsGitOptGetMwindowSize())
//    assert(Libgit2.optsGitOptGetMwindowSize() ==1020L)
//
//    Libgit2.optsGitOptSetMWindowMappedLimit(1234L)
//    println("Libgit2.optsGitOptGetMWindowMappedLimit():::"+ Libgit2.optsGitOptGetMWindowMappedLimit())
//    assert(Libgit2.optsGitOptGetMWindowMappedLimit() == 1234L)
//
//    Libgit2.optsGitOptSetMWindowFileLimit(1234L)
//    assert(1234L== Libgit2.optsGitOptGetMWindowFileLimit())
//
//    Libgit2.optsGitOptSetSearchPath(Config.ConfigLevel.GLOBAL,"/abc")
//    assert(Libgit2.optsGitOptGetSearchPath(Config.ConfigLevel.GLOBAL)=="/abc")
//
//    Libgit2.optsGitOptSetCacheObjectLimit(GitObject.Type.COMMIT, 1010);
//
//    assert(Config.ConfigLevel.SYSTEM.getValue()==2);
//    assert(Config.ConfigLevel.APP.getValue()==6);
//    Libgit2.optsGitOptSetSearchPath(Config.ConfigLevel.SYSTEM,"/abc");
//    assert(Libgit2.optsGitOptGetSearchPath(Config.ConfigLevel.SYSTEM).equals("/abc"));
//
//    assert(GitObject.Type.TREE.getBit()==2);
//    assert(GitObject.Type.ANY.getBit()==-2);
//    assert(GitObject.Type.COMMIT.getBit()==1);
//    Libgit2.optsGitOptSetCacheObjectLimit(GitObject.Type.COMMIT,1010);
//
//
//    Libgit2.optsGitOptSetCacheMaxSize(11);
//
//    Libgit2.optsGitOptEnableCaching(true)
//
//    val cachedMemory = Libgit2.optsGitOptGetCachedMemory()
//    println("cachedMemory:::"+cachedMemory.maxStorage+","+cachedMemory.currentStorageValue)
//
//    val tmpPath = Libgit2.optsGitOptGetTemplatePath()
//    println("templatePath:::$tmpPath")
//    Libgit2.optsGitOptSetTemplatePath("/tmp/libgit2")
//    assert(Libgit2.optsGitOptGetTemplatePath()=="/tmp/libgit2")
//
//    Libgit2.optsGitOptSetSslCertLocations(CertMan.sysCertList[0].file, CertMan.sysCertList[0].path)
//    Libgit2.optsGitOptSetSslCertLocations(null,null)
//    //file不存在会抛异常，path不存在不会抛异常
////    Libgit2.optsGitOptSetSslCertLocations("/tmp/nonexistfile",null)  //occur GitException
//    Libgit2.optsGitOptSetSslCertLocations(null,"/tmp/nonexistpath")
//
//    Libgit2.optsGitOptSetUserAgent("chromua")
//    assert(Libgit2.optsGitOptGetUserAgent()=="chromua")
//
//    Libgit2.optsGitOptEnableStrictObjectCreation(true)
//
//    Libgit2.optsGitOptEnableStrictSymbolicRefCreation(true)
//
//    Libgit2.optsGitOptSetSslCiphers("rsa")
//    Libgit2.optsGitOptSetSslCiphers("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384")
//
//    Libgit2.optsGitOptEnableOfsDelta(false)
//    Libgit2.optsGitOptEnableFsyncGitdir(false)
//
//    //sharemode opts only for Windows, so , in android set it is nonsense, will always get 0 sharemode
//    Libgit2.optsGitOptSetWindowsSharemode(1001)
//    println("Libgit2.optsGitOptGetWindowsSharemode():::"+ Libgit2.optsGitOptGetWindowsSharemode())  //make sense in windows only, other system will get 0
//
//    Libgit2.optsGitOptEnableStrictHashVerification(true)
//    Libgit2.optsGitOptEnableUnsavedIndexSafety(true)
//
//    Libgit2.optsGitOptSetPackMaxObjects(1024)
//    assert(Libgit2.optsGitOptGetPackMaxObjects()==1024L)
//
//    Libgit2.optsGitOptDisablePackKeepFileChecks(true)
//
//    Libgit2.optsGitOptEnableHttpExpectContinue(true)
//
//    Libgit2.optsGitOptSetOdbPackedPriority(1234)
//    Libgit2.optsGitOptSetOdbLoosePriority(1234)
//
//    Libgit2.optsGitOptSetExtensions(arrayOf("abc","def"))
//    println("Libgit2.optsGitOptGetExtensions()[0]:::"+ Libgit2.optsGitOptGetExtensions()[0])
//    assert(Libgit2.optsGitOptGetExtensions()[0]=="abc")
//    assert(Libgit2.optsGitOptGetExtensions()[1]=="def")
//
//    Libgit2.optsGitOptSetOwnerValidation(false)
//    assert(Libgit2.optsGitOptGetOwnerValidation()==false)
//
//    println("Libgit2.optsGitOptGetHomedir():::"+ Libgit2.optsGitOptGetHomedir())  // default is empty
//    Libgit2.optsGitOptSetHomedir("/tmp/git2home")
//    assert(Libgit2.optsGitOptGetHomedir()=="/tmp/git2home")
//
//    println("Libgit2.optsGitOptGetServerConnectTimeout():::"+ Libgit2.optsGitOptGetServerConnectTimeout())  //default is 0
//    Libgit2.optsGitOptSetServerConnectTimeout(36000)
//    assert(Libgit2.optsGitOptGetServerConnectTimeout() == 36000L)
//
//    println("Libgit2.optsGitOptGetServerTimeout():::"+ Libgit2.optsGitOptGetServerTimeout())  // default is 0
//    Libgit2.optsGitOptSetServerTimeout(30000)
//    assert(Libgit2.optsGitOptGetServerTimeout()==30000L)
//}
//
//
//@Composable
//private fun test_addMoreFilesToList(currentPathFileList: SnapshotStateList<File>) {
//    var tmpList = ArrayList<File>()
//    for(i in 1..50) {
//        currentPathFileList.add(File(""+i))
//    }
//    currentPathFileList.addAll(tmpList)
//}
//
////@Composable
//private fun test_checkAndPrintDepth(clonedRepo: Repository) {
//    val revWalk2 = Revwalk.create(clonedRepo)
//    revWalk2.pushHead()
//    var num = 0;
//    var oid: Oid? = revWalk2.next()
//    while (oid != null) {
//        num++
//        println("oid is:::" + oid)
//        oid = revWalk2.next()
//    }
//    println("depth is :::" + num)
//}
