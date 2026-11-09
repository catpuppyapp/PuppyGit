package com.catpuppyapp.puppygit.compose

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.jni.LibgitTwo
import com.catpuppyapp.puppygit.jni.SaveBlobRet
import com.catpuppyapp.puppygit.jni.SaveBlobRetCode
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.shared.DiffFromScreen
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.cache.Cache
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.mutableCustomStateOf
import com.github.git24j.core.Blob
import com.github.git24j.core.Index
import com.github.git24j.core.Repository
import java.io.File


private const val TAG = "DiffImg"



@Composable
fun DiffImg(
    stateKeyTag:String,
    //本组件会把文件存到这个目录下，父组件在销毁时应把这个目录下的文件清空
    baseSavePath:String,

    repoFullPath:String,
    //仓库下相对路径
    relativePath:String,
    fileName:String,

    leftHash:String,
    rightHash:String,
    changeType:String,
    requireOpenAs:(filePath:String)->Unit,
    fromScreen: DiffFromScreen,
) {
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)

    val activityContext = LocalContext.current

    //可能在LazyColumn里用这个组件，而LazyColumn里用remmeberSavebaley有bug，可能会崩溃，所以这里一律用remember或自定义的状态存储器
    val needRefresh = mutableCustomStateOf(stateKeyTag, "needRefresh") {""}

    val leftSavePath = mutableCustomStateOf(stateKeyTag, "leftSavePath") {""}
    val rightSavePath = mutableCustomStateOf(stateKeyTag, "rightSavePath") {""}
    val leftLoading = mutableCustomStateOf(stateKeyTag, "leftLoading") {false}
    val rightLoading = mutableCustomStateOf(stateKeyTag, "rightLoading") {false}
    val leftErrMsg = mutableCustomStateOf(stateKeyTag, "leftErrMsg") {""}
    val rightErrMsg = mutableCustomStateOf(stateKeyTag, "rightErrMsg") {""}


    ShowImg(leftSavePath.value, activityContext, leftLoading.value, leftErrMsg.value)
    ShowImg(rightSavePath.value, activityContext, rightLoading.value, rightErrMsg.value)



    LaunchedEffect(needRefresh.value) {
        doJobThenOffLoading {
            leftSavePath.value = ""
            leftLoading.value = true
            leftErrMsg.value = ""

            val ret = loadFile(
                hash = leftHash,
                repoFullPath = repoFullPath,
                relativePath = relativePath,
                fileName = fileName,
                baseSavePath = baseSavePath
            )

            leftSavePath.value = ret.savePath
            leftErrMsg.value = ret.errMsg

            leftLoading.value = false
        }

        doJobThenOffLoading {
            leftSavePath.value = ""
            rightLoading.value = true
            leftErrMsg.value = ""


            val ret = loadFile(
                hash = rightHash,
                repoFullPath = repoFullPath,
                relativePath = relativePath,
                fileName = fileName,
                baseSavePath = baseSavePath
            )

            rightSavePath.value = ret.savePath
            rightErrMsg.value = ret.errMsg

            rightLoading.value = false
        }
    }
}

@Composable
private fun MyImageView(
    modifier: Modifier = Modifier,
    context: Context,
    filePath:String,
) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(filePath)
            .size(200, 200)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = null,
        modifier = modifier
    )
}

private fun genSavePath(entryId:String, fileName:String):String {
    return entryId.toString().substring(0,7)+fileName
}


private fun loadFile(hash:String, repoFullPath: String, relativePath: String, fileName:String, baseSavePath:String):LoadRet {
    try {
        if(hash == Cons.git_LocalWorktreeCommitHash) {
            val file = File(repoFullPath, relativePath)
            return LoadRet(savePath = file.canonicalPath, errMsg = "")
        }else {
            val ret = Repository.open(repoFullPath).use { repo->
                if(hash == Cons.git_IndexCommitHash) {
                    val entryId = repo.index().getEntryByPath(relativePath, Index.Stage.NORMAL)?.id ?: throw RuntimeException("resolve entry failed")
                    val blob = Blob.lookup(repo, entryId)?: throw RuntimeException("resolve blob failed")
                    val savePath = File(baseSavePath, genSavePath(entryId.toString(), fileName)).canonicalPath
                    SaveBlobRet(code = LibgitTwo.saveBlobToPath(blob, savePath), savePath = savePath)
                }else {
                    Libgit2Helper.saveFileOfCommitToPath(repo, hash,  relativePath, genFilePath = {entry -> genSavePath(entry.id().toString(), fileName)})
                }
            }

            if(ret.code == SaveBlobRetCode.SUCCESS) {
                return LoadRet(savePath = ret.savePath, errMsg = "")
            }else {
                throw RuntimeException(ret.code.toString())
            }
        }
    }catch (e: Exception) {
        val errMsg = "err: ${e.localizedMessage}"
        MyLog.e(TAG, "#loadFile err: ${e.stackTraceToString()}")

        return LoadRet(savePath = "", errMsg = errMsg)
    }
}

private data class LoadRet(
    val savePath:String="",
    val errMsg:String="",
)

@Composable
private fun ShowImg(savePath: String, activityContext: Context, loading: Boolean, errMsg:String) {
    if(savePath.isNotEmpty()) {
        MyImageView(
            context = activityContext,
            filePath = savePath,
        )
    }else {
        Text(if(loading) stringResource(R.string.loading) else errMsg)
    }
}

