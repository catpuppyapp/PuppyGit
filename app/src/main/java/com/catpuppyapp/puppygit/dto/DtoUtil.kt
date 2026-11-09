package com.catpuppyapp.puppygit.dto

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.SpecialCredential
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.git.BranchNameAndTypeDto
import com.catpuppyapp.puppygit.git.CommitDto
import com.catpuppyapp.puppygit.git.FileHistoryDto
import com.catpuppyapp.puppygit.git.SubmoduleDto
import com.catpuppyapp.puppygit.git.TagDto
import com.catpuppyapp.puppygit.server.bean.ApiBean
import com.catpuppyapp.puppygit.server.bean.ConfigBean
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.genHttpHostPortStr
import com.catpuppyapp.puppygit.utils.getFileNameFromCanonicalPath
import com.catpuppyapp.puppygit.utils.getParentPathEndsWithSeparator
import com.github.git24j.core.Commit
import com.github.git24j.core.Oid
import com.github.git24j.core.Repository
import com.github.git24j.core.Submodule
import java.io.File

fun createSimpleCommitDto(
    commit: Commit,
    repoId: String,
    settings:AppSettings
):CommitDto = createCommitDto(
        commitOid = commit.id(),
        allBranchList = null,
        allTagList = null,
        commit = commit,
        repoId = repoId,
        repoIsShallow = false,
        shallowOidList = null,
        settings = settings
    )


fun createCommitDto(
    //之所以单独传oid对象是因为如果有现成的oid对象，
    // 就不需要commit.id()了，commit.id()是个jni操作，
    // 性能不如直接字符串生成Oid好，
    // 但如果没现成的commitOid也可直接传commit.id()过来，都行
    commitOid: Oid,

    allBranchList: List<BranchNameAndTypeDto>?,
    allTagList:List<TagDto>?,
    commit: Commit,

    //数据库的repoId，用来判断当前是在操作哪个仓库
    //若不需要此参数，可传空字符串
    repoId: String,

    repoIsShallow:Boolean,
    shallowOidList:List<String>?,
    settings:AppSettings,
    queryParents:Boolean = true,
): CommitDto {
    val c = CommitDto()

    c.oidStr = commitOid.toString()  // next.toString() or commit.id() ，两者相同，但用 next.toString() 性能更好，因为Oid纯java实现，不需要jni
    c.shortOidStr = Libgit2Helper.getShortOidStrByFull(c.oidStr)
    val commitOidStr = commit.id().toString()

    if(allBranchList != null) {
        //添加分支列表
        for (b in allBranchList) {
            if (b.oidStr == commitOidStr) {
                c.branchShortNameList.add(b.shortName)
            }
        }
    }

    if(allTagList != null) {
        //添加tag列表
        for(t in allTagList) {
            if(t.targetFullOidStr == commitOidStr) {
                c.tagShortNameList.add(t.shortName)
            }
        }
    }

    if(queryParents) {
        //添加parent列表，合并的提交就会有多个parent，一般都是1个
        val parentCount = commit.parentCount()
        if (parentCount > 0) {
            var pc = 0
            while (pc < parentCount) {
                val parentOidStr = commit.parentId(pc).toString()
                c.parentOidStrList.add(parentOidStr)
                c.parentShortOidStrList.add(Libgit2Helper.getShortOidStrByFull(parentOidStr))
                pc++
            }
        }
    }

    c.dateTime = Libgit2Helper.getDateTimeStrOfCommit(commit, settings)
    c.originTimeOffsetInMinutes = commit.timeOffset()
    c.originTimeInSecs = commit.time().epochSecond

    val commitSignature = commit.author()  // git log 命令默认输出的author
    c.author = commitSignature.name
    c.email = commitSignature.email

    val committer = commit.committer()  //实际提交的人
    c.committerUsername = committer.name
    c.committerEmail = committer.email

    c.shortMsg = commit.summary()
    c.msg = commit.message()
    c.repoId = repoId
    c.treeOidStr = commit.treeId().toString()

    if(repoIsShallow && shallowOidList != null && shallowOidList.contains(c.oidStr)) {
        c.isGrafted=true  //当前提交是shallow root
    }

    return c
}


suspend fun updateRemoteDtoList(repo: Repository, remoteDtoList: List<RemoteDto>, onErr:(errRemote: RemoteDto, e:Exception)->Unit={r,e->}) {
    remoteDtoList.forEachBetter {
        try {
            updateRemoteDto(repo, it)

            val credDb = AppModel.dbContainer.credentialRepository
            val matchByDomainId = SpecialCredential.MatchByDomain.credentialId
            if(it.credentialId == matchByDomainId) {
                val actuallyCred = credDb.getByIdAndMatchByDomain(matchByDomainId, it.remoteUrl)
                if(actuallyCred!=null) {
                    it.actuallyCredentialIdWhenCredentialIdIsMatchByDomain = actuallyCred.id
                    it.credentialName = actuallyCred.name
                    it.credentialVal = actuallyCred.value
                    it.credentialPass = actuallyCred.pass
                    it.credentialType = actuallyCred.type
                }
            }

            if(it.pushCredentialId == matchByDomainId) {
                // when reached here, the pushUrl is set to actually using url by `updateRemoteDto()`
                val actuallyCred = credDb.getByIdAndMatchByDomain(matchByDomainId, it.pushUrl)
                if(actuallyCred!=null) {
                    it.actuallyPushCredentialIdWhenCredentialIdIsMatchByDomain = actuallyCred.id
                    it.pushCredentialName = actuallyCred.name
                    it.pushCredentialVal = actuallyCred.value
                    it.pushCredentialPass = actuallyCred.pass
                    it.pushCredentialType = actuallyCred.type
                }
            }
        }catch (e:Exception) {
            onErr(it, e)
        }
    }
}

fun updateRemoteDto(repo: Repository, remoteDto: RemoteDto) {
    val remoteName = remoteDto.remoteName
    val remote = Libgit2Helper.resolveRemote(repo, remoteName) ?: return

    //更新remoteUrl(即git config文件中的url)、pushUrl
    remoteDto.remoteUrl = remote.url()
    remoteDto.pushUrl = remote.pushurl()?:""

    // if push url not set, use same as fetch url(remoteUrl)
    // use isBlank() check the pushUrl is fine, if pushurl exists but has blank value, still will using fetch url
    if(remoteDto.pushUrl.isBlank()) {
        remoteDto.pushUrl = remoteDto.remoteUrl
        remoteDto.pushUrlTrackFetchUrl = true
    }

    //更新branchMode
    val (isAll, branchNameList) = Libgit2Helper.getRemoteFetchBranchList(remote)
    if(isAll) {
        remoteDto.branchMode = Cons.dbRemote_Fetch_BranchMode_All
        remoteDto.branchListForFetch = emptyList()  //fetch refspec为所有分支的时候用不到分支列表，设个空列表即可
    }else {
        remoteDto.branchMode = Cons.dbRemote_Fetch_BranchMode_CustomBranches
        remoteDto.branchListForFetch = branchNameList  //自定义分支列表，列表值是指定的分支的名字
    }
}


fun createSubmoduleDto(
    sm: Submodule,
    smName: String,
    parentWorkdirPathNoSlashSuffix: String,
    invalidUrlAlertText: String
): SubmoduleDto {
    val smRelativePath = sm.path()
    val smFullPath = parentWorkdirPathNoSlashSuffix + Cons.slash + smRelativePath.removePrefix(Cons.slash)

    // [fixed, the reason was pass NULL to jni StringUTF method in c codes] if call submodule.url() it will crashed when url invalid
    val smUrl = sm.url() ?: ""
    //another way to get url from .gitsubmodules, is read info by kotlin, 100% safe
    //                val smUrl = getValueFromGitConfig(parentDotGitModuleFile, "submodule.$name.url")
    val smDto = SubmoduleDto(
        name = smName,
        relativePathUnderParent = smRelativePath,
        fullPath = smFullPath,
        cloned = Libgit2Helper.isValidGitRepo(smFullPath),
        remoteUrl = smUrl,
        targetHash = Libgit2Helper.getParentRecordedTargetHashForSubmodule(sm),
        tempStatus = if (smUrl.isBlank()) invalidUrlAlertText else "",
        location = Libgit2Helper.getSubmoduleLocation(sm)
    )

    return smDto
}



fun createFileHistoryDto(
    repoWorkDirPath:String,
    commitOidStr: String,
    treeEntryOidStr:String,
    commit: Commit,
    repoId: String,
    fileRelativePathUnderRepo:String,
    settings: AppSettings,
    commitList: List<String>,
): FileHistoryDto {
    val obj = FileHistoryDto()

    obj.commitList = commitList.toList()
    obj.repoWorkDirPath = repoWorkDirPath
    obj.fileParentPathOfRelativePath = getParentPathEndsWithSeparator(fileRelativePathUnderRepo)
    obj.fileName = getFileNameFromCanonicalPath(fileRelativePathUnderRepo)
    obj.fileFullPath = File(repoWorkDirPath, fileRelativePathUnderRepo).canonicalPath
    obj.filePathUnderRepo = fileRelativePathUnderRepo
    obj.treeEntryOidStr = treeEntryOidStr
    obj.commitOidStr = commitOidStr
    obj.dateTime = Libgit2Helper.getDateTimeStrOfCommit(commit, settings)
    obj.originTimeOffsetInMinutes = commit.timeOffset()

    val commitSignature = commit.author()  // git log 命令默认输出的author
    obj.authorUsername = commitSignature.name
    obj.authorEmail = commitSignature.email

    val committer = commit.committer()  //实际提交的人
    obj.committerUsername = committer.name
    obj.committerEmail = committer.email

    obj.shortMsg = commit.summary()
    obj.msg = commit.message()
    obj.repoId = repoId

    return obj
}

fun genConfigDto(
    repoEntity: RepoEntity,
    settings: AppSettings
): ConfigBean {
    val host = settings.httpService.listenHost
    val port = settings.httpService.listenPort
    val token = settings.httpService.tokenList.let { if(it.isEmpty()) "" else it.first() }

    return ConfigBean(
        repoName = repoEntity.repoName,
        repoId = repoEntity.id,
        api = ApiBean(
            protocol = "http",
            host = host,
            port = port,
            token = token,
            pull = "/pull",
            push = "/push",
            sync = "/sync",
            //少加点参数，少写少错
            pull_example = "${genHttpHostPortStr(host, port.toString())}/pull?token=$token&repoNameOrId=${repoEntity.repoName}",
            push_example = "${genHttpHostPortStr(host, port.toString())}/push?token=$token&repoNameOrId=${repoEntity.repoName}",
            sync_example = "${genHttpHostPortStr(host, port.toString())}/sync?token=$token&repoNameOrId=${repoEntity.repoName}",
        )
    )
}

fun rawAppInfoToAppInfo(rawAppInfo: ApplicationInfo, packageManager: PackageManager, selected:(AppInfo)->Boolean) :AppInfo? {
    val appName = rawAppInfo.loadLabel(packageManager).toString()
    val appIcon = rawAppInfo.loadIcon(packageManager) ?: return null
    val isSystemApp = (rawAppInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0

    val tmp = AppInfo(
        appName = appName,
        packageName = rawAppInfo.packageName,
        appIcon = appIcon,
        isSystemApp = isSystemApp,
        isSelected = false
    )

    tmp.isSelected = selected(tmp)

    return tmp
}

fun getCredentialName(
    isFetch:Boolean,
    fetchCredentialId:String?,
    fetchCredentialName:String?,
    pushCredentialId:String?,
    pushCredentialName:String?
):String {
    val id = if(isFetch) fetchCredentialId else pushCredentialId
    val name = if(isFetch) fetchCredentialName else pushCredentialName
    val scmbd = SpecialCredential.MatchByDomain
    val scnone = SpecialCredential.NONE

    // 若id为match by domain的id则显示match by domain；否则，若name不为null则关联了有效的id，这时显示name；若name为null，则代表关联的凭据已被删除或者没关联凭据，这时实际处理时会当作无关联凭据，所以显示None
    return if(id == scmbd.credentialId) scmbd.name else (name ?: scnone.name);
}
