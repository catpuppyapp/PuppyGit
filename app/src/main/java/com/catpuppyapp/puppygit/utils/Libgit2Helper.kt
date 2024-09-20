package com.catpuppyapp.puppygit.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.data.repository.CredentialRepository
import com.catpuppyapp.puppygit.data.repository.RemoteRepository
import com.catpuppyapp.puppygit.data.repository.RepoRepository
import com.catpuppyapp.puppygit.dto.RemoteDto
import com.catpuppyapp.puppygit.dto.createCommitDto
import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.git.BranchNameAndTypeDto
import com.catpuppyapp.puppygit.git.CommitDto
import com.catpuppyapp.puppygit.git.DiffItemSaver
import com.catpuppyapp.puppygit.git.PuppyHunkAndLines
import com.catpuppyapp.puppygit.git.PuppyLine
import com.catpuppyapp.puppygit.git.ReflogEntryDto
import com.catpuppyapp.puppygit.git.RemoteAndCredentials
import com.catpuppyapp.puppygit.git.StashDto
import com.catpuppyapp.puppygit.git.StatusTypeEntrySaver
import com.catpuppyapp.puppygit.git.TagDto
import com.catpuppyapp.puppygit.git.Upstream
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.github.git24j.core.AnnotatedCommit
import com.github.git24j.core.Apply
import com.github.git24j.core.Branch
import com.github.git24j.core.Checkout
import com.github.git24j.core.Cherrypick
import com.github.git24j.core.Commit
import com.github.git24j.core.Config
import com.github.git24j.core.Credential
import com.github.git24j.core.Diff
import com.github.git24j.core.FetchOptions
import com.github.git24j.core.GitObject
import com.github.git24j.core.Graph
import com.github.git24j.core.Index
import com.github.git24j.core.Merge
import com.github.git24j.core.Oid
import com.github.git24j.core.Patch
import com.github.git24j.core.PushOptions
import com.github.git24j.core.Rebase
import com.github.git24j.core.Reference
import com.github.git24j.core.Reflog
import com.github.git24j.core.Remote
import com.github.git24j.core.Repository
import com.github.git24j.core.Repository.MergeheadForeachCb
import com.github.git24j.core.Reset
import com.github.git24j.core.Revparse
import com.github.git24j.core.Revwalk
import com.github.git24j.core.Signature
import com.github.git24j.core.SortT
import com.github.git24j.core.Stash
import com.github.git24j.core.Status
import com.github.git24j.core.Status.StatusList
import com.github.git24j.core.Tag
import com.github.git24j.core.Tree
import java.io.File
import java.net.URI
import java.nio.charset.Charset
import java.time.ZoneOffset
import java.util.EnumSet
import kotlin.io.path.pathString


private val TAG = "Libgit2Helper"

class Libgit2Helper {
    companion object {
        private val getCredentialCb = { credentialType:Int, usernameOrPrivateKey:String, passOrPassphrase:String ->

            //凭据认证回调函数的文档：https://libgit2.org/libgit2/#HEAD/group/callback/git_credential_acquire_cb
            { url: String?, usernameFromUrl: String?, allowedTypes: Int? ->
                //TODO 这个不知道会不会报错，如果报错的话，改用allowedType判断决定返回什么类型的Credential ，可参考：libgit2 src/examples/common.c 函数 int cred_acquire_cb()
                if (credentialType == Cons.dbCredentialTypeHttp) {  //type http
                    Credential.userpassPlaintextNew(
                        usernameOrPrivateKey,
                        passOrPassphrase
                    )
                } else {  //type SSH
                    //用户名先设置成广泛使用的默认名 git
                    var usernameForSsh  = Cons.gitWellKnownSshUserName
                    //如果url包含用户名，设置成url里的
                    if(usernameFromUrl != null && usernameFromUrl.isNotBlank()) {
                        usernameForSsh = usernameFromUrl
                    }
                    val passphraseOrNull = passOrPassphrase.ifBlank { null }  // pass?pass:null

                    //params: username, publickey, privatekey, passphrase。其中username和privatekey必须有，passphrase如果设置了就有，否则没有，publickey在客户端不需要。
                    Credential.sshKeyMemoryNew(
                        usernameForSsh, null,
                        usernameOrPrivateKey, passphraseOrNull
                    )
                }
            }
        }

        private fun getDefaultStatusOptTypeSet():EnumSet<Status.OptT> {
            return EnumSet.of(
                Status.OptT.OPT_INCLUDE_UNTRACKED,
                Status.OptT.OPT_RECURSE_UNTRACKED_DIRS,  // 包含untracked目录下的每个文件而不是只列出目录。（不设这个的话，如果目录里全是untracked文件，则不会列出其中的文件，只会列出目录名，还得我自己遍历目录文件，麻烦）
//            Status.OptT.OPT_RENAMES_HEAD_TO_INDEX,  //把index条目的rename检测关了，这个会把相似度高的文件的删除和新增合并成一个renamed条目，还得检查oldfile和newfile，麻烦，不如直接把新增和删除列出来，反正用户自己知道是否重命名
                Status.OptT.OPT_SORT_CASE_INSENSITIVELY,
                Status.OptT.OPT_EXCLUDE_SUBMODULES,  //暂不支持submodule，先排除
            )
        }

        private fun getDefaultDiffOptionsFlags():EnumSet<Diff.Options.FlagT> {
            return EnumSet.of(
                Diff.Options.FlagT.INCLUDE_UNTRACKED,
                Diff.Options.FlagT.SHOW_UNTRACKED_CONTENT,
                Diff.Options.FlagT.RECURSE_UNTRACKED_DIRS,  //包含untracked目录下的文件内容
                //                Diff.Options.FlagT.INCLUDE_CASECHANGE,  //包含文件名大小写改变，不建议设置，由配置文件控制即可
                //                Diff.Options.FlagT.IGNORE_CASE,  //忽略文件名大小写改变，不建议设置，由配置文件控制即可
                Diff.Options.FlagT.IGNORE_SUBMODULES,  //忽略submodules
//            Diff.Options.FlagT.SHOW_UNMODIFIED
//            Diff.Options.FlagT.PATIENCE,
//            Diff.Options.FlagT.MINIMAL,
//            Diff.Options.FlagT.INDENT_HEURISTIC,
//            Diff.Options.FlagT.IGNORE_BLANK_LINES,
//            Diff.Options.FlagT.IGNORE_WHITESPACE_EOL,

                //                ,Diff.Options.FlagT.REVERSE  // 反转比较的两者，例如：index to worktree 变成 worktree to index
            )
        }

        private fun getDefaultRevwalkSortMode():EnumSet<SortT>{
            return EnumSet.of(
                SortT.NONE, //git默认的git log输出顺序？
            )
        }

        private val defaultBranchTypeForList = Branch.BranchType.ALL



        private fun getRepoStatusList(repo:Repository,
                              showType:Status.ShowT = Status.ShowT.INDEX_AND_WORKDIR,
                              flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
                              )
        :StatusList {
                val statusOpts: Status.Options = Status.Options.newDefault()
                statusOpts.show = showType.bit  //e.g. Status.ShowT.INDEX_AND_WORKDIR
                statusOpts.flags = flags;  // e.g. EnumSet.of(Status.OptT.OPT_INCLUDE_UNTRACKED, Status.OptT.OPT_RENAMES_HEAD_TO_INDEX, Status.OptT.OPT_SORT_CASE_INSENSITIVELY)

                if (repo.isBare) {
                    throw RuntimeException("Cannot report status on bare repository: " + repo.workdir())
                }

                return StatusList.listNew(repo, statusOpts)
        }

        fun getIndexStatusList(repo:Repository,
                                  flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
        )
                :StatusList {

            val repoStatusList = getRepoStatusList(repo, Status.ShowT.INDEX_ONLY, flags)
            //debug
//            打印下这个list，看下是不是冲突条目
//            val entryCount = repoStatusList.entryCount()
//            for(i in 0 until entryCount){
//                MyLog.d(TAG,"#getIndexStatusList():"+repoStatusList.byIndex(i).headToIndex!!.oldFile.path)
//            }
            //debug
            return repoStatusList
        }

        //如果只想检测index是否为空，repoId可随便传，如果想在页面使用条目，必须传正确的repoId，不然操作对应条目的时候不知道是哪个仓库
        //如果onlyCheckEmpty为true，则只在必要时查询statusTypeEntryList，否则会在能够确定是否为空的情况下直接返回，并把列表返回值设为null
        //返回值1代表index是否为空，为空则true，不为空false；返回值2代表index条目的列表
        fun checkIndexIsEmptyAndGetIndexList(repo:Repository, repoId:String, onlyCheckEmpty:Boolean):Pair<Boolean, List<StatusTypeEntrySaver>?> {
            val repoStatusList = getIndexStatusList(repo)
            val index = repo.index()
            val indexMustEmpty = repoStatusList.entryCount() < 1
            if(indexMustEmpty) {
                return Pair(true, null)
            }

            //执行到这，index条目一定大于0，肯定有条目，但如果不存在冲突，那现在就已经可以确定index有条目了，如果存在冲突，则还需要进一步检测，现在的index列表的条目是不是全是未stage的冲突条目
            if(!index.hasConflicts() && onlyCheckEmpty) {  //如果没冲突，列表肯定不为空，但如果没设置仅检查是否为空的参数，则继续执行，取出不包含冲突条目的index条目列表
                return Pair(false, null)
            }

            //执行到这有两种可能：
            //1 存在冲突，上面返回的列表有可能包含 conflict但是又没stage的文件，所以需要进一步检测
            //2 不存在冲突，但onlyCheckEmpty为假，代表用户想获得不包含冲突条目的index列表
            val (_, statusMap) = statusListToStatusMap(
                repo,
                repoStatusList,
                repoId,
                Cons.gitDiffFromHeadToIndex
            )
            val indexListFromStatusMap = statusMap[Cons.gitStatusKeyIndex]

            //这次的值就准确了
            return Pair(indexListFromStatusMap.isNullOrEmpty(), indexListFromStatusMap)
        }

        fun indexIsEmpty(repo:Repository):Boolean {
            //这的repoId没用，我只想检查是否为空
            val (isEmpty,_) = checkIndexIsEmptyAndGetIndexList(repo = repo, repoId = "", onlyCheckEmpty = true)
            return isEmpty
        }

        fun getWorkdirStatusList(repo:Repository,
                                  flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
                                 )
        :StatusList {
            return getRepoStatusList(repo,Status.ShowT.WORKDIR_ONLY,flags)
        }

        @Deprecated("建议分别获取index和workdir的list，不要混在一起")
        fun getIndexAndWorkdirStatusList(repo:Repository,
                                         flags:EnumSet<Status.OptT> = getDefaultStatusOptTypeSet()
        )
        :StatusList {
            return getRepoStatusList(repo,Status.ShowT.INDEX_AND_WORKDIR,flags)
        }

        //改用 repo.index().hasConflicts() 了
        private fun hasConflictItemInStatusList(statusList:StatusList):Boolean {
            val entryCnt: Int = statusList.entryCount()
            //until， 左闭右开，左包含，右不包含
            for (i in 0 until entryCnt)  {
                val entry = statusList.byIndex(i)
                if(entry.status.contains(Status.StatusT.CONFLICTED)){
                    return true
                }
            }
            return false;
        }

        fun hasConflictItemInRepo(repo:Repository):Boolean {
            /*
            //遍历 statusList 的方式
            //查询index或workdir都会包含冲突条目，由于本app倾向stage和commit绑定，因此多数情况下stage为空，所以这里优先查询stage(即：index区)是否有冲突文件
            val indexStatusList = getIndexStatusList(repo)
            return hasConflictItemInStatusList(indexStatusList)
            */

            //用git24j的api
            return repo.index().hasConflicts()

        }

        fun getRepoCanonicalPath(repo: Repository, itemUnderRepoRelativePath: String): String {
            return repo.workdir().pathString + File.separator + itemUnderRepoRelativePath
        }

        fun getRepoPathSpecType(path: String): Int {
            val endsWithSeparator = path.endsWith("/")
            if(endsWithSeparator){
                return Cons.gitItemTypeDir
            }else {
                return Cons.gitItemTypeFile
            }
        }

        //repoPathNoFileSeparatorAtEnd，结尾没有路径分隔符的repo path
        fun getRelativePathUnderRepo(repoPathNoFileSeparatorAtEnd: String, fileFullPath: String): String {
            return fileFullPath.substring(fileFullPath.indexOf(repoPathNoFileSeparatorAtEnd)+repoPathNoFileSeparatorAtEnd.length+1)
        }

        //废弃，改用 DiffItemSaver 内部的 getLines()
        //diff item会把行拼在一起只显示一个行号，不太好，处理一下
        //参数说明：lineStartNum就是diffitem的行号，实际上是连续行的起始行号；originType就是 + - 这类代表添加删除行的符号；content就是diffItem的content
        //举例：输入 12、+、 ["line1content","line2content","line3content"]，返回["+12:line1content", "+13:line2content","+14:line3content"]
//        fun handleDiffItemContentByLine(lineStartNumber:Int, originType:String, lines:List<String>):List<String> {
//            val ret = mutableListOf<String>()
////            val lines = content.lines()
//            var cnt = lineStartNumber
//            lines.forEach{
//                ret.add(originType+(cnt++)+":"+it)
//            }
//            return ret
//        }

        /**return:
         * {
         *      index:{path:status, ...},
         *      workdir:{path:status, ...},
         *      conflict:{path:status}
         * }，
         * 注：untracked属于workdir
         *
         * 根据fromTo判断是index还是worktree的status list，然后往对应集合填条目(历史遗留问题，所以有index和work两个列表，其实有一个就行)。
         * index时worktree的列表为空，反之，statuslist是worktree的列表时，index列表为空
         * 特殊情况：conflict条目在index和workdir两个列表都有，且条目一样
         * */
        fun statusListToStatusMap(
            repo: Repository,
            statusList:StatusList,
            repoIdFromDb:String,
            fromTo: String,
            removeNonExistsConflictItems:Boolean=true

            //Pair第1个参数代表本函数是否更新了index，第2个代表返回的数据。
        ):Pair<Boolean, Map<String,List<StatusTypeEntrySaver>>> {
            //按路径名排序
            val index:MutableList<StatusTypeEntrySaver> = ArrayList()
            val workdir:MutableList<StatusTypeEntrySaver> =ArrayList()
            val conflict:MutableList<StatusTypeEntrySaver> = ArrayList()

            val entryCnt: Int = statusList.entryCount()
            val repoIndex = repo.index()
            var isIndexChanged = false

            //until， 左闭右开，左包含，右不包含
            for (i in 0 until entryCnt)  {
                val entry = statusList.byIndex(i)

                var delta = entry.indexToWorkdir  //changelist page
                if(fromTo == Cons.gitDiffFromHeadToIndex){  //indexpage
                    delta = entry.headToIndex
                }

                val oldFile = delta?.oldFile
                val newFile = delta?.newFile

//                var file = delta?.oldFile  //?的作用是如果delta为null，返回null；否则返回oldFile
//                if(file == null) {
//                    file = delta?.newFile
//                }
//
//                // 若path为null，则continue
//                val path = file?.path ?: continue

                //把file和path初始化为newFile的，如果是删除，会把这个替换成oldfile的path
                //后面会判断，如果发现修改类型是删除，则把file换成oldFile，path改成oldFile的path
                var file=newFile
                var path=newFile?.path?:""


                val status = entry.status  //status有可能有多个状态，例如：同时包含INDEX_NEW和WT_MODIFIED两种状态

                //忽略的文件一般不需要包含，查询的时候就没查，应该没有状态为忽略的文件
//                if(status.contains(Status.StatusT.IGNORED)) {
//
//                }
                val statusTypeSaver = StatusTypeEntrySaver()
//                statusTypeSaver.entry = entry  //这个会随着列表的释放而被释放，持有引用可能会变成空指针
                statusTypeSaver.repoIdFromDb = repoIdFromDb

                if(status.contains(Status.StatusT.CONFLICTED)) {  //index或worktree都会包含冲突条目
                    val mustPath = newFile?.path?:oldFile?.path?:""
                    if(mustPath.isNotEmpty()) {
                        val f = File(repo.workdir().pathString, mustPath)
                        if(!f.exists() && removeNonExistsConflictItems){
                            MyLog.w(TAG, "#statusListToStatusMap: removed a Non-exists conflict item from git, file '$mustPath' may delete after it become conflict item")

                            repoIndex.conflictRemove(mustPath)
                            isIndexChanged = true
                        }else {
                            statusTypeSaver.changeType=Cons.gitStatusConflict
                            conflict.add(statusTypeSaver)
                        }

                    }else{
                        MyLog.w(TAG, "#statusListToStatusMap: conflict item with empty path!")
                    }
                }else{
                    //判断index还是worktree，用不同的变量判断，然后把条目添加到不同的列表
                    if(fromTo == Cons.gitDiffFromHeadToIndex) {  //index
                        if(status.contains(Status.StatusT.INDEX_NEW)){
                            statusTypeSaver.changeType=Cons.gitStatusNew
                            index.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.INDEX_DELETED)){
//                            println("newFIle:::"+newFile?.size?:0) // 0，无法获取已删除文件的大小，但提交后再diff，就能获取到大小了，可能是bug
//                            println("oldFile:::"+oldFile?.size?:0)  // 0
                            file=oldFile
                            path=oldFile?.path?:""
                            statusTypeSaver.changeType=Cons.gitStatusDeleted
                            index.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.INDEX_MODIFIED)){
                            statusTypeSaver.changeType=Cons.gitStatusModified
                            index.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.INDEX_RENAMED)){
                            statusTypeSaver.changeType=Cons.gitStatusRenamed
                            index.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.INDEX_TYPECHANGE)){
                            statusTypeSaver.changeType=Cons.gitStatusTypechanged
                            index.add(statusTypeSaver)
                        }

                    }else {  // worktree
                        if(status.contains(Status.StatusT.WT_NEW)){ //untracked
                            statusTypeSaver.changeType=Cons.gitStatusNew
                            workdir.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.WT_DELETED)){
                            //如果类型是删除，把file和path替换成旧文件的，不然文件大小会是0
                            file=oldFile
                            path=oldFile?.path?:""
                            statusTypeSaver.changeType=Cons.gitStatusDeleted
                            workdir.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.WT_MODIFIED)){
                            statusTypeSaver.changeType=Cons.gitStatusModified
                            workdir.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.WT_RENAMED)){
                            statusTypeSaver.changeType=Cons.gitStatusRenamed
                            workdir.add(statusTypeSaver)
                        }else if(status.contains(Status.StatusT.WT_TYPECHANGE)){
                            statusTypeSaver.changeType=Cons.gitStatusTypechanged
                            workdir.add(statusTypeSaver)
                        }

                    }
                }

                //为目录递归添加文件
                //xTODO 先改成检测status的时候把submodule排除
                //xTODO(未来实现submodule的时候再做这个): 需要区分submodule和文件夹，如果是submodule，不遍历StatusEntry.itemType为submodule，当作文件夹条目列出来且不可diff
                //xTODO: 如果path是个路径: 递归遍历，把里面所有文件都作为一个条目添加到map，每个条目的type都和路径的type一样，例如文件夹是untracked，则里面 所有的条目都是untracked
                val canonicalPath = getRepoCanonicalPath(repo,path)
                val fileType = getRepoPathSpecType(path)
                statusTypeSaver.itemType = fileType
                statusTypeSaver.canonicalPath = canonicalPath
                statusTypeSaver.fileName = getFileNameFromCanonicalPath(canonicalPath)  // or File(canonicalPath).name
                statusTypeSaver.relativePathUnderRepo = path
                statusTypeSaver.fileSizeInBytes = file?.size?.toLong()?:0L

                //目前：libgit2 1.7.1有bug，status获取已删除文件有可能大小为0(并非百分百，若index为空，有可能获取到已删除文件的真实大小)，但实际不为0，所以这里检查下，如果大小等于0且类型是删除，用diffTree查询一下
                //这两个判断条件没一个多余，大小为0的检测的作用是日后如果libgit2修复了获取删除文件大小错误的bug，就不会在执行此方法了，代码不用改动，又或者偶然能获取到正常文件大小，则不需要再多余查询，所以第1个条件必须
                //第2个条件是因为我暂时只发现类型为删除的文件获取大小异常，所以其他类型不用检测，若日后发现其他类型也有问题，再改判断条件即可
                if(statusTypeSaver.fileSizeInBytes==0L && statusTypeSaver.changeType == Cons.gitStatusDeleted) {
                    val diffItem = getSingleDiffItem(
                        repo,
                        statusTypeSaver.relativePathUnderRepo,
                        fromTo,
                        onlyCheckFileSize = true
                    )
                    statusTypeSaver.fileSizeInBytes = diffItem.getEfficientFileSize()
                }

//                20240407 之后添加了status flag，实际上只有file类型了，所以不用做下面的判断了
//                if(fileType == Cons.gitItemTypeFile) {  //文件类型，直接添加
//                    statusTypeSaver.canonicalPath = canonicalPath
//                    statusTypeSaver.fileName = File(canonicalPath).name
//                    statusTypeSaver.relativePathUnderRepo = path
//                    statusTypeSaver.fileSizeInBytes = file?.size?.toLong()?:0L
//                }
//                else {  //目录类型，遍历添加，不过 20240407 之后 添加了 Status.OptT.OPT_RECURSE_UNTRACKED_DIRS 后，应该不会执行到这个分支了，不过也没删的必要，就这样不动即可
//                    // actually dead code at 20240407 start
//                    val parent = statusTypeSaver
//                    //递归遍历目录并为每个文件设置相同的修改类型(modified/untracked之类的)
//                    val dir = File(canonicalPath);
//                    val subs = parent.subs
//                    dir.walkTopDown().forEach { f:File->
//                        if(f.isFile) {
//                            val subEntry = StatusTypeEntrySaver()
//                            subEntry.canonicalPath = f.canonicalPath
//                            subEntry.changeType = parent.changeType
//                            subEntry.itemType = Cons.gitItemTypeFile
//                            subEntry.fileName = f.name
//                            subEntry.repoIdFromDb = repoIdFromDb
//                            subEntry.relativePathUnderRepo = getRelativePathUnderRepo(repo.workdir().toString(), f.canonicalPath)
////                            statusTypeSaver.fileSizeInBytes = file?.size?.toLong()?:0L  //对于目录来说，这个参数不准确，不过，不会进入到这个代码块了，因为开启了递归遍历目录的flag
//                            subs.add(subEntry)
//                        }
//                    }
//                    // actually dead code at 20240407 end
//                }


            }

            if(isIndexChanged) {
                repoIndex.write()
            }

            val resultMap:MutableMap<String,MutableList<StatusTypeEntrySaver>> = HashMap()
            resultMap[Cons.gitStatusKeyIndex] = index
            resultMap[Cons.gitStatusKeyWorkdir] = workdir
            resultMap[Cons.gitStatusKeyConflict] = conflict

            return Pair(isIndexChanged, resultMap);
        }

        fun getGitUrlType(gitUrl: String): Int {
            //这个条件一定要“只要不是http/https就是ssh”这种逻辑，因为http和https就两种，能穷举完，但ssh我不太了解，而且有的sshurl是用户名写最前面，匹配起来恶心得很！
            return if(gitUrl.startsWith(Cons.gitUrlHttpStartStr) || gitUrl.startsWith(Cons.gitUrlHttpsStartStr)){
                Cons.gitUrlTypeHttp
            }else {
                Cons.gitUrlTypeSsh
            }
        }

        fun getCredentialTypeByGitUrlType(gitUrlType: Int): Int {
            //不是http就是ssh
            return if(gitUrlType == Cons.gitUrlTypeHttp){
                Cons.dbCredentialTypeHttp
            }else {  //  gitUrlType == Cons.gitUrlTypeSsh
                Cons.dbCredentialTypeSsh
            }
        }

        fun getTreeToTreeChangeList(
            repo: Repository,
            repoId: String,
            tree1: Tree,
            tree2: Tree?,  // when diff to worktree, pass `null`
            diffOptionsFlags: EnumSet<Diff.Options.FlagT> = getDefaultDiffOptionsFlags(),
            reverse: Boolean = false, // when compare worktreeToTree , pass true, then can use treeToWorktree api to diff worktree to tree
            treeToWorkTree: Boolean = false, //这样设计其实不好，最好是添加一个fromTo type，但是，直接添加这个变量需要改的代码最少，暂时先这样吧
        ):List<StatusTypeEntrySaver> {
            //目前20240407不考虑submodule，在diffOptionsFlags里排除了，日后如果实现的话，也不包含submodule，若想diff submodule，应该去submodule的commit history里点击它的commit来diff，父模块顶多就是提示一下submodule关联的hash不一样了之类的，不提供文件diff。

            val ret = mutableListOf<StatusTypeEntrySaver>()  //如果担心有重复条目，可用Set存一下，然后addAll到list，不要把返回值改成Set，因为页面还要兼容其他类型的diff，而其他类型的diff用的都是List存条目
            val options = Diff.Options.create()

            val opFlags = diffOptionsFlags.toMutableSet()
            if(reverse) {
                opFlags.add(Diff.Options.FlagT.REVERSE)
            }

            options.flags = EnumSet.copyOf(opFlags);
            MyLog.d(TAG, "#getTreeToTreeChangeList: options.flags = $opFlags")

            //注意比较的顺序是旧版本号 to 新版本号（父提交 to 新提交）
            val diff = if(treeToWorkTree) Diff.treeToWorkdir(repo, tree1, options) else Diff.treeToTree(repo, tree1, tree2, options)

            diff.foreach(
                { delta: Diff.Delta, progress: Float ->
                    val oldFile = delta.oldFile
                    val newFile = delta.newFile
                    val oldFileOid = oldFile.id
                    val newFileOid = newFile.id
                    /*
                        需要设置的字段（注：这里没有canonicalPath，因为树上的文件没检出就不在硬盘上，自然也没硬盘上的文件路径）
                        var repoIdFromDb:String="";
                        var relativePathUnderRepo:String="";
                        var fileName:String="";

                        //file or dir or submodule
                        var itemType:Int = Cons.gitItemTypeFile;

                        var changeType:String?=null;

                     */
                    val stes = StatusTypeEntrySaver()
                    stes.repoIdFromDb = repoId
                    stes.relativePathUnderRepo = newFile.path  //不管新增还是删除还是重命名文件，新旧文件都有path而且都一样，所以用哪个都行
                    stes.canonicalPath = getRepoCanonicalPath(repo, stes.relativePathUnderRepo)
                    stes.fileName = getFileNameFromCanonicalPath(stes.relativePathUnderRepo)  //用相对路径或完整路径都能取出文件名
                    stes.itemType=Cons.gitItemTypeFile

                    if(oldFileOid.isNullOrEmptyOrZero && !newFileOid.isNullOrEmptyOrZero) {  //新增
                        stes.changeType = Cons.gitStatusNew
                        stes.fileSizeInBytes = newFile.size.toLong()  //新增时，取新文件的size
                    }else if(!oldFileOid.isNullOrEmptyOrZero && newFileOid.isNullOrEmptyOrZero) {  //删除
                        stes.changeType = Cons.gitStatusDeleted
                        stes.fileSizeInBytes = oldFile.size.toLong()  //删除时，取旧文件的size
                    }else if(!oldFileOid.isNullOrEmptyOrZero && !newFileOid.isNullOrEmptyOrZero){ //修改
                        stes.changeType = Cons.gitStatusModified
                        stes.fileSizeInBytes = newFile.size.toLong()  //修改时，也是取新文件的size，和新增时一样
                    }else{ //新旧文件oid都是全0
                        // 这种情况不会发生，新旧oid都全0，那文件根本就不存在，所以也不可能遍历到这个文件
                    }
                    ret.add(stes)
                    0
                },
                { delta, binary ->

                    0
                },
                { delta: Diff.Delta?, hunk: Diff.Hunk ->

                    0
                },
                { delta: Diff.Delta?, hunk: Diff.Hunk?, line: Diff.Line ->

                    0
                })

            return ret
        }

        fun revparseSingle(repo: Repository, revspec: String):GitObject? {
            try {
                val gitObject = Revparse.single(repo, revspec)
                return gitObject
            }catch (e:Exception) {
                MyLog.e(TAG, "#revparseSingle() error, params are (revspec=$revspec),\nerr is:"+e.stackTraceToString())
                return null
            }
        }

        //merge abort 是用这个命令实现的
        fun resetHardToHead(repo: Repository, checkoutOptions: Checkout.Options?=null):Ret<String?> {
            return resetHardToRevspec(repo, "HEAD",checkoutOptions)
        }

        fun resetHardToRevspec(repo: Repository, revspec: String,checkoutOptions: Checkout.Options?=null):Ret<String?> {
            return resetToRevspec(repo, revspec, Reset.ResetT.HARD,checkoutOptions)
        }

        fun resetToRevspec(repo: Repository, revspec: String, resetType:Reset.ResetT,checkoutOptions: Checkout.Options?=null):Ret<String?> {
            try {
                val resetTarget = revparseSingle(repo, revspec)
                if(resetTarget==null){
                    return Ret.createError(null, "resolve revspec failed!", Ret.ErrCode.resolveRevspecFailed)
                }
                Reset.reset(repo, resetTarget, resetType, checkoutOptions);

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#resetToRevspec() error:"+e.stackTraceToString())
                return Ret.createError(null, "reset err: ${e.localizedMessage}", Ret.ErrCode.resetErr)
            }
        }

        fun forEachMergeHeads(repo: Repository, act:(Oid)->Unit) {
            repo.mergeHeadForeach(  //遍历 merge heads
                object : MergeheadForeachCb() {
                    override fun call(oid: Oid): Int {
                        act(oid)
                        return 0
                    }
            })
        }

        fun getMergeHeads(repo: Repository):MutableList<Oid> {
            val mergeHeadList = mutableListOf<Oid>()
            forEachMergeHeads(repo) {
                mergeHeadList.add(it)
            }
            return mergeHeadList
        }

        //返回 success 就是就绪了，否则就是没就绪
        fun readyForContinueMerge(repo: Repository):Ret<String?> {
            val appContext = AppModel.singleInstanceHolder.appContext

            try {
                if(repo.state() != Repository.StateT.MERGE) {
                    return Ret.createError(null, appContext.getString(R.string.repo_not_in_merging))
                }
                if(repo.index().hasConflicts()) {
                    return Ret.createError(null, appContext.getString(R.string.plz_resolve_conflicts_first))
                }
                if(getMergeHeads(repo).isEmpty()) {
                    return Ret.createError(null, appContext.getString(R.string.no_merge_head_found))
                }

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#readyForContinueMerge err: "+e.stackTraceToString())
                return Ret.createError(null, "err:"+e.localizedMessage)
            }
        }

        //不会返回oid，只是和continue rebase的返回值一致，方便返回
        fun readyForContinueRebase(repo: Repository):Ret<Oid?> {
            val funName = "readyForContinueRebase"
            val appContext = AppModel.singleInstanceHolder.appContext

            try {
                //20240814:目前libgit2只支持REBASE_MERGE，不支持 git默认的 REBASE_INTERACTIVE
                if(repo.state() != Repository.StateT.REBASE_MERGE) {
                    return Ret.createError(null, appContext.getString(R.string.repo_not_in_rebasing))
                }
                if(repo.index().hasConflicts()) {
                    return Ret.createError(null, appContext.getString(R.string.plz_resolve_conflicts_first))
                }
//                if(getMergeHeads(repo).isEmpty()) {
//                    return Ret.createError(null, appContext.getString(R.string.no_merge_head_found))
//                }

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName err: "+e.stackTraceToString())
                return Ret.createError(null, "err:"+e.localizedMessage)
            }
        }

        fun rebaseGetCurCommit(repo:Repository):Commit? {
            val rebaseOptions = Rebase.Options.createDefault()
            initRebaseOptions(rebaseOptions)

            val rebase = Rebase.open(repo, rebaseOptions)

            val curidx = rebase.operationCurrent()
            val cur = rebase.operationByIndex(curidx)
            val curOid = cur?.id ?: return null

            return resolveCommitByHash(repo, curOid.toString())
        }

        fun rebaseGetCurCommitRet(repo:Repository):Ret<Commit?> {
            try {
                val c = rebaseGetCurCommit(repo)
                if(c==null) {
                    return Ret.createError(null, "resolve cur commit of rebase err")
                }

                return Ret.createSuccess(c)
            }catch (e:Exception) {
                MyLog.e(TAG, "#rebaseGetCurCommitRet err:${e.stackTraceToString()}")
                return Ret.createError(null, e.localizedMessage ?:"err when get cur commit of rebase", exception = e)
            }
        }

        // for rebase continue dialog
        fun rebaseGetCurCommitMsg(repo:Repository):String {
            val curCommit = rebaseGetCurCommit(repo)
            if(curCommit!=null) {
                return curCommit.message()
            }else{
                return ""
            }
        }

        /**
         * @param overwriteAuthorForFirstCommit 执行continue时覆盖提交的作者用户名和邮箱，只针对continue的对象也就是rebase的当前commit有效，仅当skipFirst为假时有效
         * @param skipFirst 为true时将强制不提交当前rebase的目标commit，直接执行next
         */
        fun rebaseContinue(repo:Repository, username: String, email: String, overwriteAuthorForFirstCommit:Boolean=false, commitMsgForFirstCommit: String="", skipFirst:Boolean= false):Ret<Oid?>{
            val readyCheck = readyForContinueRebase(repo)
            if (readyCheck.hasError()) {
                return readyCheck
            }

            val rebaseOptions = Rebase.Options.createDefault()
            initRebaseOptions(rebaseOptions)

            val rebase = Rebase.open(repo, rebaseOptions)
            val rebaseCommitter = Signature.create(username, email)

            //注意：若继续rebase但index为空，会跳过当前pick的提交而不是创建空提交
            //提交上次中断的 pick
            if(!skipFirst && !indexIsEmpty(repo)) {  //有可能提交完了，没执行next，所以仍卡在当前commit；也有可能next了，但没commit，所以卡在当前提交，无法判断，所以这里检查下index是否为空，若为空就不创建提交了。注意：如果rebase前的分支存在空提交，这样做有可能会忽略空提交哦！
                //为null用原提交信息，否则用传入的参数的值
                rebase.commit(if(overwriteAuthorForFirstCommit) rebaseCommitter else null, rebaseCommitter, null, commitMsgForFirstCommit.ifBlank { null })
            }

            val curRebaseOpIndex = rebase.operationCurrent()
            val allOpCount = rebase.operationEntrycount()
            var nextIdx = curRebaseOpIndex+1
            while(nextIdx < allOpCount) {  //如果还有其余的commit，则继续rebase
                //继续后面的pick
                rebase.next()
                if(!repo.index().hasConflicts()) {  //无冲突，提交
                    rebase.commit(null, rebaseCommitter, null, null)
                }else {  //有冲突，返回，之后可 continue或abort，不过不支持 skip
                    return Ret.createError(null, "rebase:has conflicts when continue")
                }

                nextIdx++
            }

            //全部rebase完成
//            val rebaseOriginName = rebase.origHeadName()  //用户恢复之前的分支

            //全部执行完了
            rebase.finish(rebaseCommitter)

            val headId = repo.head()?.id() ?: return Ret.createError(null, "rebase:get new oid err after finish rebase")

            //我发现只要正确处理Ac.fromRef和lookup，就不需要手动恢复分支
            //如果执行rebase之前非detached Head，恢复一下之前的分支
//            if(rebaseOriginName!=null) {  //不为null，rebase前处于某个分支，需要恢复分支；若为null，rebase前为detached HEAD，无需恢复分支
//                //切换到之前分支
//                checkoutLocalBranchThenUpdateHead(repo, rebaseOriginName, force=false, updateHead=true)
//
//                //重置之前的分支Head指向rebase最新创建的提交
//                resetHardToRevspec(repo, headId.toString())
//            }

            return Ret.createSuccess(headId)
        }

        fun rebaseSkip(repo: Repository, username: String, email: String):Ret<Oid?> {
            //reset HEAD，直接丢弃上次 rebase.next() 造成的修改
            //不能用reset，这个会把状态清掉！就不能继续rebase了！
//            val resetRet = resetHardToHead(repo)
//            if(resetRet.hasError()) {
//                return Ret.createError(null, resetRet.msg, resetRet.code)
//            }

            //用force checkout重置 worktree和index 为HEAD中的数据
            val checkoutOptions = Checkout.Options.defaultOptions()
            checkoutOptions.strategy = EnumSet.of(Checkout.StrategyT.FORCE)  //使用force reset，不指定路径则代表全部文件
            Checkout.head(repo, checkoutOptions)

            //然后执行 rebase.next() 就跳过当前提交了，这里直接执行rebaseContinue，skipFirst传true会跳过当前提交，并直接执行rebase.next()
            return rebaseContinue(repo, username, email, skipFirst = true)
        }

        fun rebaseAbort(repo:Repository):Ret<Unit?> {
            val funName = "rebaseAbort"
            try{
                val rebaseOptions = Rebase.Options.createDefault()
                initRebaseOptions(rebaseOptions)

                val rebase = Rebase.open(repo, rebaseOptions)
                rebase.abort()

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
                return Ret.createError(null, e.localizedMessage?:"rebase abort err", exception = e)
            }
        }

        fun rebaseAccept(repo: Repository, pathSpecList: List<String>, acceptTheirs:Boolean):Ret<String?> {
            try {
                if(pathSpecList.isEmpty()) {
                    return Ret.createError(null, "pathspec list is Empty!")
                }

                if(repo.state() != Repository.StateT.REBASE_MERGE) {
                    return Ret.createError(null, "repo not in REBASE")
                }
                if(!repo.index().hasConflicts()) {
                    return Ret.createError(null, "repo has no conflicts")
                }

                val checkoutOpts = Checkout.Options.defaultOptions()

                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.DISABLE_PATHSPEC_MATCH, Checkout.StrategyT.FORCE)  //禁用路径模糊匹配避免文件名有*而匹配错

                //设置要恢复的文件列表
                checkoutOpts.setPaths(pathSpecList.toTypedArray())

                return if(acceptTheirs) {
                    rebaseAcceptTheirs(repo, checkoutOpts)
                }else {  //用merge的实现即可，都是从HEAD恢复文件，一样
                    mergeAcceptOurs(repo, checkoutOpts)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#rebaseAccept err: params are: pathSpecList=$pathSpecList, acceptTheirs=$acceptTheirs\n"+e.stackTraceToString())
                return Ret.createError(null, "rebase accept ${if(acceptTheirs) "theirs" else "ours"} err:"+e.localizedMessage)
            }
        }

        fun cherrypickAccept(repo: Repository, pathSpecList: List<String>, acceptTheirs:Boolean):Ret<String?> {
            try {
                if(pathSpecList.isEmpty()) {
                    return Ret.createError(null, "pathspec list is Empty!")
                }

                if(repo.state() != Repository.StateT.CHERRYPICK) {
                    return Ret.createError(null, "repo not in CHERRYPICK")
                }
                if(!repo.index().hasConflicts()) {
                    return Ret.createError(null, "repo has no conflicts")
                }

                val checkoutOpts = Checkout.Options.defaultOptions()

                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.DISABLE_PATHSPEC_MATCH, Checkout.StrategyT.FORCE)  //禁用路径模糊匹配避免文件名有*而匹配错

                //设置要恢复的文件列表
                checkoutOpts.setPaths(pathSpecList.toTypedArray())

                return if(acceptTheirs) {
                    cherrypickAcceptTheirs(repo, checkoutOpts)
                }else {  //用merge的实现即可，都是从HEAD恢复文件，一样
                    mergeAcceptOurs(repo, checkoutOpts)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#cherrypickAccept err: params are: pathSpecList=$pathSpecList, acceptTheirs=$acceptTheirs\n"+e.stackTraceToString())
                return Ret.createError(null, "cherrypick accept ${if(acceptTheirs) "theirs" else "ours"} err:"+e.localizedMessage)
            }
        }

        private fun cherrypickAcceptTheirs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
            val cpHeadCommitId = getCherryPickHeadCommit(repo).data?.id() ?: return Ret.createError(null, "resolve cur cherrypick commit err!")

            val targetTree = resolveTree(repo, cpHeadCommitId.toString()) ?: return Ret.createError(null, "resolve cur cherrypick commit to tree err!")

            Checkout.tree(repo, targetTree, checkoutOptions)

            return Ret.createSuccess(null)
        }

        /**
         * 注：要恢复的文件列表包含在checkoptions
         * @param checkoutOptions 调用者应自行设置checkout策略为force 和 要checkout的文件路径
         */
        private fun rebaseAcceptTheirs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
            //这里只是用rebase来取下提交号而已，checkout是否force无所谓，最终用来执行accpet的那个checkoutOptions(就是函数参数中的那个）需要被调用者设置checkout 策略为 force和要checkout的文件路径
            val rebaseOptions = Rebase.Options.createDefault()
            initRebaseOptions(rebaseOptions)

            val rebase = Rebase.open(repo, rebaseOptions)
            val curRebase = rebase.operationByIndex(rebase.operationCurrent())
            val curRebaseCommitId = curRebase?.id ?: return Ret.createError(null, "resolve cur rebase commit err!")

            val targetTree = resolveTree(repo, curRebaseCommitId.toString()) ?: return Ret.createError(null, "resolve cur rebase commit to tree err!")
            Checkout.tree(repo, targetTree, checkoutOptions)

            return Ret.createSuccess(null)
        }

        /**
            仅适用于合并两个分支的场景
         @param pathSpecList : 要恢复的文件路径
         */
        fun mergeAccept(repo:Repository, pathSpecList: List<String>, acceptTheirs:Boolean):Ret<String?> {
            try {
                if(pathSpecList.isEmpty()) {
                    return Ret.createError(null, "pathspec list is Empty!")
                }

                if(repo.state() != Repository.StateT.MERGE) {
                    return Ret.createError(null, "repo not in MERGE")
                }
                if(!repo.index().hasConflicts()) {
                    return Ret.createError(null, "repo has no conflicts")
                }

                val checkoutOpts = Checkout.Options.defaultOptions()

                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.DISABLE_PATHSPEC_MATCH, Checkout.StrategyT.FORCE)  //禁用路径模糊匹配避免文件名有*而匹配错

                checkoutOpts.setPaths(pathSpecList.toTypedArray())

                return if(acceptTheirs) {
                    mergeAcceptTheirs(repo, checkoutOpts)
                }else {
                    mergeAcceptOurs(repo, checkoutOpts)
                }
            }catch (e:Exception) {
                MyLog.e(TAG, "#mergeAccept err: params are: pathSpecList=$pathSpecList, acceptTheirs=$acceptTheirs\n"+e.stackTraceToString())
                return Ret.createError(null, "merge accept ${if(acceptTheirs) "theirs" else "ours"} err:"+e.localizedMessage)
            }
        }

        private fun mergeAcceptTheirs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
            val mergeHeadList = getMergeHeads(repo)

            if(mergeHeadList.size!=1) {
                return Ret.createError(null, "too less or many merge head, expect 1, but found ${mergeHeadList.size}")
            }

            val mergeHead = mergeHeadList[0]

            //废弃，reset太强悍，直接把merge状态都整没了，改用checkout了
//            resetToRevspec(repo, parent.toString(), ResetT.HARD, checkoutOptions)

            val mergeHeadTree = resolveTree(repo, mergeHead.toString()) ?: return Ret.createError(null, "resolve merge head to tree err!")
            Checkout.tree(repo, mergeHeadTree, checkoutOptions)

            return Ret.createSuccess(null)
        }

        private fun mergeAcceptOurs(repo:Repository, checkoutOptions: Checkout.Options):Ret<String?> {
            Checkout.head(repo, checkoutOptions)
            return Ret.createSuccess(null)
        }

        //x 加了个获取diff条目列表的函数，和这个配合即可，不用改这个函数了) 废案)写个获取一个列表的diffitem的方法，返回一个List<DiffItem>，然后把这个single方法内部调用列表方法，只不过列表里只设置一个条目，写单个和列表逻辑都一样，但列表更通用
        //获取某个文件新增多少行，删除多少行，以及新增和删除行的内容和行号
        fun getSingleDiffItem(repo:Repository, relativePathUnderRepo:String, fromTo:String,
                              // changeType:String, //changeType 用来判断要diff还是patch，如果是修改类型，用patch；若是新增和删除，用diff？好像不用，都用patch就行？patch貌似只要是改变的文件，都能处理，无论新增删除修改，但如果是没修改的就返回null
                              tree1:Tree?=null, tree2:Tree?=null,
                              diffOptionsFlags:EnumSet<Diff.Options.FlagT> = getDefaultDiffOptionsFlags(),
                              onlyCheckFileSize:Boolean = false,
                              reverse: Boolean=false,
                              treeToWorkTree: Boolean = false
                             )
        :DiffItemSaver{
            val funName = "getSingleDiffItem"
            MyLog.d(TAG, "#$funName(): relativePathUnderRepo=${relativePathUnderRepo}, fromTo=${fromTo}")

            val diffItem = DiffItemSaver()
            val options = Diff.Options.create()

            val opFlags = diffOptionsFlags.toMutableSet()

            // because no workdirToTree, so use treeToWorkdir+reverse instead, so must set reverse if want to diff workTree to tree
            if(reverse) {
                opFlags.add(Diff.Options.FlagT.REVERSE)
            }

            options.flags = EnumSet.copyOf(opFlags);
            MyLog.d(TAG, "#$funName: options.flags = $opFlags")
            options.pathSpec = arrayOf(relativePathUnderRepo) //set only diff a single file

            lateinit var diff:Diff;
            if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                diff = Diff.indexToWorkdir(repo, null, options)

            }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
                val headTree:Tree? = resolveHeadTree(repo)
                if(headTree==null) {
                    MyLog.w(TAG, "#$funName(): require diff from head to index, but resolve HEAD tree failed!")
                    return DiffItemSaver()
                }

                diff = Diff.treeToIndex(repo, headTree, repo.index(), options)
            }else if(fromTo == Cons.gitDiffFromHeadToWorktree){
                val headTree:Tree? = resolveHeadTree(repo)
                if(headTree==null) {
                    MyLog.w(TAG, "#$funName(): require diff from head to worktree, but resolve HEAD tree failed!")
                    return DiffItemSaver()
                }

                diff = Diff.treeToWorkdir(repo, headTree, options);
            }else {  // fromTo == Cons.gitDiffFromTreeToTree
                  // tree to tree
                MyLog.d(TAG, "#$funName(): require diff from tree to tree, tree1Oid=${tree1?.id().toString()}, tree2Oid=${tree2?.id().toString()}, reverse=$reverse")
//                println("treeToWorkTree:${treeToWorkTree},  tree1Oid=${tree1?.id().toString()}, tree2Oid=${tree2?.id().toString()}, reverse=$reverse")
                diff = if(treeToWorkTree) Diff.treeToWorkdir(repo, tree1, options) else Diff.treeToTree(repo, tree1, tree2, options)
            }

            //上面的代码没bug，下面的不一定！（flag，后来下面的代码果然报错了）


            val deltaNum = diff.numDeltas()
            //deltaNum小于1则意味着文件没改变，直接返回即可
            if(deltaNum<1) {
                diffItem.isFileModified = false
                return diffItem
            }

            //执行到这说明文件改变了（因为是diff的单条目，所以其实最多只会有1个delta，文件没改变则0个，如果是diff的多个文件，则可能的最大delta数取决于有多少个文件）
            diffItem.isFileModified = true
//            if(debugModeOn) {
//                println("deltaNum:"+deltaNum)
//            }

            //fromDiff(diff, idx)其中idx应该是delta的索引，其范围应该是 [0, numDeltas)，numDeltas可通过调用 diff.numDeltas()获得
            //这里是diff的单文件，所以最多应该只有一个delta，因此索引固定取0即可，若文件完全一样，则一个delta都没有，尝试Patch.fromDiff()就会越界，这时应直接返回不要尝试获取patch
            val patch = Patch.fromDiff(diff, 0)?:return diffItem

            //delta hunk line 区别：delta包含新旧文件路径（仓库下相对路径）和类型（主要是是否二进制）之类的信息；hunk包含一个内容片段；line组成hunk中的内容（但一个line实际可能包含多个行）。
            //delta hunk line 关系：delta和hunk各自独立，line属于hunk

            //应该先获取delta，后获取hunk，不然hunk为空一返回，就拿不到delta的flags了

            //获取delta
            val delta=patch.delta
            diffItem.flags = delta.flags
            val oldFile = delta.oldFile
            val newFile = delta.newFile
            diffItem.oldFileOid = oldFile.id.toString()
            diffItem.newFileOid = newFile.id.toString()

            //这个判断可能没有意义，因为如果文件未修改，就不会有delta，在上面Patch.fromDiff()的时候就索引越界了，
            // 或者在更前面获取numDeltas的时候就获取到0，然后进入文件未修改的判断，然后就返回了，根本不会执行到这，
            // 执行到文件就肯定修改过，oid也肯定不一样
            //文件没有改变
            if(diffItem.oldFileOid == diffItem.newFileOid) {
                diffItem.isFileModified = false
                return diffItem
            }


            diffItem.oldFileSize = oldFile.size.toLong()
            diffItem.newFileSize = newFile.size.toLong()

            //检查文件大小，如果超过限制或者调用者请求仅检查文件大小(status列表可能会这么干，仅用此方法检查某个文件大小但不获取内容，正常来说是不需要的，但libgit2 1.7.1有bug，有些情况无法获取已删除文件的大小，所以需要这么干)，则不查询内容
//            diffItem.isFileSizeOverLimit = isFileSizeOverLimit(diffItem.getEfficientFileSize())
            if(onlyCheckFileSize) {
                return diffItem
            }

            //获取hunk
            val numHunks = patch.numHunks()
            if(numHunks==0) {
                return diffItem
            }
            var contentLenSum =0L
            //用来存储puppyLine和rawLine对，这样可以实现先统计大小，若没超，则取line content，否则不取的逻辑
            val puppyAndRawLineList = mutableListOf<Pair<PuppyLine, Diff.Line>>()
            for(i in 0 until numHunks) {
                val hunkInfo = patch.getHunk(i) ?:continue
                val hunk = hunkInfo.hunk
                val lineCnt = hunkInfo.lines
                val hunkAndLines = PuppyHunkAndLines()
                hunkAndLines.hunk.header = hunk.header

//                    libgit2 1.7.1 ，经过测试，我发现其计算的header长度不对，该在 @@结束，
//                    但实际上却是在@@之后加上了下面一行的内容，不过我后来发现pc git
//                    （在看cmake openssl脚本的patch文件时发现的，那个patch应该就是diff输出，
//                    但@@后面有东西）好像有时候也会在@@后面有东西？所以可能不是bug？
                MyLog.d(TAG, "#$funName(): hunk header:"+hunkAndLines.hunk.header)

                diffItem.hunks.add(hunkAndLines)
                val lines = hunkAndLines.lines
                for(j in 0 until lineCnt) {
                    val line = patch.getLineInHunk(i, j)?:continue

                    val pLine = PuppyLine()
                    //先检查文件大小是否超了
                    pLine.contentLen = line.contentLen
                    contentLenSum+=pLine.contentLen

                    //如果content累积的大小超过限制，直接返回，不要再获取了
                    if(isDiffContentSizeOverLimit(contentLenSum)) {
                        diffItem.isContentSizeOverLimit = true
                        return diffItem
                    }

                    pLine.originType = ""+line.origin
                    pLine.oldLineNum= line.oldLineno /** Line number in old file or -1 for added line */
                    pLine.newLineNum=line.newLineno  /** Line number in new file or -1 for deleted line */

//                    pLine.rawContent = line.content
                    //添加pLine和rawLine到集合，如果大小没超限制，之后会取出rawLine的内容到pLine
                    puppyAndRawLineList.add(Pair(pLine, line))
//                    pLine.content = line.content
//                    pLine.content = LibgitTwo.getContent(line.contentLen, LibgitTwo.jniLineGetContent(line.rawPointer))
                    pLine.lineNum = getLineNum(pLine)
                    //不知道为什么全是1
                    pLine.howManyLines = line.numLines
//
//                    //不知道为什么，感觉还是有bug
//                    val sb = StringBuilder()
//                    //如果行数是1，最多会取2行
//                    var cnt = 0
//                    val splitLines = pLine.rawContent.lines()
//                    for(tmpLine in splitLines) {
//                        //假如有howManyLines是1，那最多会取两行，因为howManyLines是行号标识符，但有可能存在一行数据或多行数据最后一行不以行号结尾的情况，如果用 < 来判断，可能漏一行，用 <= 则不会，只是不知道多出的一行会不会是错误的信息？
//                        if(cnt < pLine.howManyLines) {
//                            sb.appendLine(tmpLine)
//                        }else if(cnt == pLine.howManyLines) {
//                            sb.append(tmpLine)
//                        }else {  // cnt > pLine.howManyLines
//                            break
//                        }
//                        cnt++
//                    }
//
//                    //根据换行符分割后的内容，但是，如果原数据没换行符，岂不就会被遗漏？谁知道呢。整不明白这东西了
//                    pLine.content = sb.toString()

//                    if(debugModeOn) {
//                        println("line.numLines:"+line.numLines)
//                    }

                    lines.add(pLine)


                    //20240618新增：实现增量diff相关代码
                    hunkAndLines.addLineToGroup(pLine)
                }
            }

            //执行到这，说明contentLen总和没超过限制，否则早在循环里就返回了
            diffItem.isContentSizeOverLimit = false
            //既然总大小没超，就取下内容吧
            //rawLine的生命周期好像和diff一样，而diff的生命周期在这个方法代码块内，所以在这取rawLine应该不会出现内存错误吧？大概吧，有待时间考验。
            for((puppyLine, rawLine) in puppyAndRawLineList) {
                puppyLine.content = rawLine.content
            }

//            println("getSingleDiffItem返回了")
//            if(debugModeOn) {
//                println(patch.toBuf())  //err
//            }
//
//            patch.print  { delta: Diff.Delta?, hunk: Diff.Hunk?, line: Diff.Line ->
////                val hunk = hunkInfo.hunk
////                val lineCnt = hunkInfo.lines
//                val hunkAndLines = PuppyHunkAndLines()
////                hunkAndLines.hunk.header = hunk?.header?
//                diffItem.hunks.add(hunkAndLines)
//                val lines = hunkAndLines.lines
//
//                val pLine = PuppyLine()
//                pLine.originType = line.origin.toString()
//                pLine.oldLineNum= line.oldLineno /** Line number in old file or -1 for added line */
//                pLine.newLineNum=line.newLineno  /** Line number in new file or -1 for deleted line */
//                pLine.rawContent = line.content
//                pLine.lineNum = getLineNum(pLine)
//                val sb = StringBuilder()
//                var l = 0
//                pLine.rawContent.lines().forEach {
//                    if(l++ < pLine.lineNum) {
//                        sb.append(it)
//                    }
//                }
//                pLine.content = sb.toString()
//
//                //不知道为什么全是1
//                pLine.howManyLines = line.numLines
//                if(debugModeOn) {
//                    println("line.numLines:"+line.numLines)
//                }
//
//                lines.add(pLine)
////                for(j in 0 until lineCnt) {
////                    val line = patch.getLineInHunk(i, j)?:continue
//
//                0
//            }

////           (不对，用patch输出也不对劲，patch和diff.foreach()还有patch.print()，基本上，都一样) 这个东西不能用来输出，乱七八糟，和git diff输出也不一样，实际上，git diff输出是patch（不知道算不算格式，类似一个事实上的diff规范），git diff的输出可以当作patch的输入用来更新文件
//            diff.foreach(
//                { delta: Diff.Delta, progress: Float ->
//    //                var f = delta.oldFile
//    //                if (f == null) {
//    //                    f = delta.newFile
//    //                }
//    //                println("Diff.FileCb found file:" + f!!.path)
//                    diffItem.flags = delta.flags
//                    diffItem.oldFileOid = delta.oldFile.id.toString()
//                    diffItem.newFileOid = delta.newFile.id.toString()
//                    0
//                },
//                { delta, binary ->
//
//                    0
//                },
//                Diff.HunkCb { delta: Diff.Delta?, hunk: Diff.Hunk ->
//                    val hunkAndLines = PuppyHunkAndLines()
//                    hunkAndLines.hunk.header = hunk.header
//                    diffItem.hunks.add(hunkAndLines)
//                    0
//                },
//                Diff.LineCb { delta: Diff.Delta?, hunk: Diff.Hunk?, line: Diff.Line ->
////                    TODO 其实不需要检测hunk，只需要行，然后统计新增删除多少行显示给用户，就行了
////                    println("==============")
////                    println("abc\n".lines().size)  //这api有点坑，这明明就一行，它说有两行！
////                    println(line.newLineno.toString()+":"+line.content+":"+line.content.split("\\n")[0])
////                    println("==================")
//                    val hunkAndLines =
//                        diffItem.hunks.get(diffItem.hunks.size - 1)  //一个hunk调用n次linecb，所以每次被调用的linecd都和最后一个存的hunk有关
//                    hunkAndLines ?: return@LineCb 0 //if null, return
//
//                    val lines = hunkAndLines.lines
//                    val pLine = PuppyLine()
//                    pLine.originType = line.origin.toString()
//                    pLine.oldLineNum = line.oldLineno
//                    /** Line number in old file or -1 for added line */
//                    pLine.newLineNum = line.newLineno
//                    /** Line number in new file or -1 for deleted line */
//                    pLine.content = line.content
//                    pLine.lineNum = getLineNum(pLine)
//
//                    //不知道为什么全是1
//                    pLine.howManyLines = line.numLines
//
//                    lines.add(pLine)
//                    // conflict item will not show at here, try diff workdir to HEAD for it
////                    println(
////                        getLineStatus(
////                            line.oldLineno,
////                            line.newLineno
////                        ) + "," + line.oldLineno + "to" + line.newLineno + ":::" + line.origin + line.content
////                    )
//                    0
//                })


            return diffItem
        }

        fun getDiffLineBgColor(line:PuppyLine, inDarkTheme: Boolean):Color{
            if(line.originType == Diff.Line.OriginType.ADDITION.toString()) {  //添加行
                return if(inDarkTheme) MyStyleKt.ChangeListItemColor.added_darkTheme else MyStyleKt.ChangeListItemColor.added
            }else if(line.originType == Diff.Line.OriginType.DELETION.toString()) {  //删除行
                return if(inDarkTheme) MyStyleKt.ChangeListItemColor.deleted_darkTheme else MyStyleKt.ChangeListItemColor.deleted
            }else if(line.originType == Diff.Line.OriginType.HUNK_HDR.toString()) {  //hunk header
                return Color.Gray
            }else if(line.originType == Diff.Line.OriginType.CONTEXT.toString()) {  //上下文
                return Color.Unspecified
            }else if(line.originType == Diff.Line.OriginType.CONTEXT_EOFNL.toString()) {  //新旧文件都没末尾行
                return Color.Unspecified
            }else if(line.originType == Diff.Line.OriginType.ADD_EOFNL.toString()) {  //添加了末尾行
                return Color.Unspecified
            }else if(line.originType == Diff.Line.OriginType.DEL_EOFNL.toString()) {  //删除了末尾行
                return Color.Unspecified
            }else {  // unknown
                return Color.Unspecified
            }
        }
        fun getDiffLineTextColor(line:PuppyLine, inDarkTheme:Boolean):Color{
            if(line.originType == Diff.Line.OriginType.ADDITION.toString()) {  //添加行
                return UIHelper.getFontColor(inDarkTheme)
            }else if(line.originType == Diff.Line.OriginType.DELETION.toString()) {  //删除行
                return UIHelper.getFontColor(inDarkTheme)
            }else if(line.originType == Diff.Line.OriginType.CONTEXT.toString()) {  //上下文
                return UIHelper.getFontColor(inDarkTheme)


                //20240501:下面的其实都没用
            }else if(line.originType == Diff.Line.OriginType.HUNK_HDR.toString()) {  //hunk header
                return UIHelper.getFontColor(inDarkTheme)
            }else if(line.originType == Diff.Line.OriginType.CONTEXT_EOFNL.toString()) {  //新旧文件都没末尾行
                return UIHelper.getSecondaryFontColor(inDarkTheme)
            }else if(line.originType == Diff.Line.OriginType.ADD_EOFNL.toString()) {  //添加了末尾行
                return UIHelper.getSecondaryFontColor(inDarkTheme)
            }else if(line.originType == Diff.Line.OriginType.DEL_EOFNL.toString()) {  //删除了末尾行
                return UIHelper.getSecondaryFontColor(inDarkTheme)
            }else {  // unknown
                return Color.Unspecified
            }
        }

        @Composable
        fun getDiffLineTypeStr(line:PuppyLine): String {
            if(line.originType == Diff.Line.OriginType.ADDITION.toString()) {
                return stringResource(R.string.diff_line_type_add)
            }else if(line.originType == Diff.Line.OriginType.DELETION.toString()) {
                return stringResource(R.string.diff_line_type_del)
            }else if(line.originType == Diff.Line.OriginType.HUNK_HDR.toString()) {
                return stringResource(R.string.diff_line_type_hunk_header)
            }else if(line.originType == Diff.Line.OriginType.CONTEXT.toString()) {
                return stringResource(R.string.diff_line_type_context)
            }else if(line.originType == Diff.Line.OriginType.CONTEXT_EOFNL.toString()) {
                return ""
            }else if(line.originType == Diff.Line.OriginType.ADD_EOFNL.toString()) {
                return stringResource(R.string.diff_line_type_add)
            }else if(line.originType == Diff.Line.OriginType.DEL_EOFNL.toString()) {
                return stringResource(R.string.diff_line_type_del)
            }else {
                return ""
            }
        }

        fun getLineNum(line:PuppyLine):Int {  //哪个不是-1就返回哪个，和originType配合即可知道是删除还是新增行
            return if(line.newLineNum<0) line.oldLineNum else line.newLineNum
        }

        fun getRepoGitDirEndsWithSlash(repo:Repository):String {
            return repo.itemPath(Repository.Item.GITDIR)?:""
        }

        //检查仓库是否是shallowed，实现机制就是：检查 .git 目录是否有 shallow 文件
        fun isRepoShallow(repo:Repository):Boolean {
            //自己实现
//            val f = File(getRepoGitDir(repo),"shallow")
//            return f.exists()
            //用libgit2内部实现
            return repo.isShallow
        }


        //不传参数，默认情况下返回的是remote origin匹配所有分支的refspec，形如：+refs/heads/*:refs/remotes/origin/*
//传参数则会把remote和branch替换为传来的参数，若想获得某个branch的singlebranch refspec，把期望的branch传进来即可，
// 以默认remote origin举例，返回值形如：+refs/heads/yourbranch:refs/remotes/origin/yourbranch
        fun getGitRemoteFetchRefSpec(remote:String=Cons.gitDefaultRemoteOrigin, branch:String=Cons.gitGlobMatchAllSign):String {
            val replacedRemoteStr = Cons.gitFetchRefSpecRemoteAndBranchReplacer.replace(Cons.gitRemotePlaceholder, remote)
            return replacedRemoteStr.replace(Cons.gitBranchPlaceholder, branch)
        }

        //向配置文件追加条目，key一样，但value不一样，同一个key可以有多个value，形成一个列表，例如：
        // fetch = url1
        // fetch = url2
        // fetch = url3
        //key是fetch，value是 url1/url2/url3
        //追加的含义就是不修改已有条目，因为需要匹配一个特殊的pattern，所以建了个方法
        private fun appendMultiVarToGitConfig(config: Config, key:String, value:String) {
            //注：这个方法默认是修改value匹配到第2个参数的条目，但 Cons.regexMatchNothing 啥value也匹配不到，所以就等于追加了
            config.setMultivar(key, Cons.regexMatchNothing, value)
        }

        fun getRepoConfigForRead(repo:Repository):Config {
            return repo.configSnapshot()  //只读
        }
        fun getRepoConfigForWrite(repo:Repository):Config {
            return repo.config()  //只写
        }

        //如果要删除的key不存在，会报异常
        fun deletePushUrl(writableConfig: Config, remoteName: String) {
            val key ="remote.$remoteName.pushurl"  // eg: remote.origin.pushurl
            try {
                writableConfig.deleteEntry(key)
            }catch (e:Exception) {
                val keyDoesntExistFlag = "could not find key"  // eg: com.github.git24j.core.GitException: could not find key 'remote.origin.pushurl' to delete
                if(e.localizedMessage?.contains(keyDoesntExistFlag) != true) {  //如果不是“key不存在”的错误，则抛出，否则就不用抛了，因为本来就是要删除，"不存在"和"删除成功"没有区别
                    throw e
                }
            }
        }

        //为仓库设置fetch url，可以有多个
        //注：config可通过本类的方法 getRepoConfig() 获得
        fun setRemoteFetchRefSpecToGitConfig(config: Config,
                                             fetch_BranchMode:Int,
                                             remote:String,
                                             branchOrBranches:String,
                                             branchListSeparator:String=Cons.stringListSeparator,
                                             appContext: Context
        ):Ret<String?> {
            try {
                //e.g. remote.origin.fetch
                val key = "remote.$remote.fetch"

                //如果fetchall或单分支，删除条目并设置一个fetch条目即可；如果是fetch自定义分支列表，删除所有条目，然后逐个设置上，这样fetch的时候就只会fetch显式设置的分支了，但这里只是针对分支的，不是针对remote的，与pc "git fetch --all" 含义有所不同，pc git fetch --all会fetch所有remote
                if(fetch_BranchMode==Cons.dbRemote_Fetch_BranchMode_All || fetch_BranchMode==Cons.dbRemote_Fetch_BranchMode_SingleBranch) {  //fetch all或者fetch 单分支(singlebranch)
                    val refSpec = getGitRemoteFetchRefSpec(remote, branchOrBranches.trim());
                    //删除remote下所有fetch条目
                    config.deleteMultivar(key, Cons.regexMatchAll);
                    appendMultiVarToGitConfig(config,key,refSpec)  // 因为前面已经删除了所有的fetch条目，所以这里不会出现多个fetch，因此用设置单个条目的方法 `config.setString(key, refSpec)` 也可以。

                }else if(fetch_BranchMode==Cons.dbRemote_Fetch_BranchMode_CustomBranches) {  //自定义fetch的分支列表
                    //如果分支列表为空，直接返回
                    if(branchOrBranches.isBlank()) {
                        return Ret.createError(null, appContext.getString(R.string.err_branches_str_is_invalid));
                    }

//                    val branches = branchOrBranches.trim().split(branchListSeparator)
                    val branches = getBranchListFromUserInputCsvStr(branchOrBranches, branchListSeparator)

                    if(branches.isEmpty()) {
                        return Ret.createError(null, appContext.getString(R.string.err_branch_list_is_empty));
                    }

                    //删除所有条目
                    config.deleteMultivar(key, Cons.regexMatchAll);

                    //为所有分支创建条目
                    for (b in branches) {
                        //避免类似下面的字符串： branch1, , ,branch2
                        if(b.isBlank()) {
                            continue
                        }
                        val refSpec = getGitRemoteFetchRefSpec(remote, b.trim());
                        appendMultiVarToGitConfig(config, key, refSpec)
                    }
                }

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#setRemoteFetchRefSpecToGitConfig err:"+e.stackTraceToString())
                return Ret.createError(null, "update branch err:"+e.localizedMessage)
            }

        }

        //return Pair(username,password)
        fun getGitUserNameAndEmailFromRepo(repo: Repository):Pair<String,String> {
            val config = getRepoConfigForRead(repo)

            return Pair<String,String>(config.getString(Cons.gitConfigKeyUserName).orElse(""), config.getString(Cons.gitConfigKeyUserEmail).orElse(""))
        }

        fun getChangeTypeColor(type: String):Color {
            if(type == Cons.gitStatusNew) {
                return MyStyleKt.ChangeListItemColor.added
            }
            if(type == Cons.gitStatusDeleted) {
                return MyStyleKt.ChangeListItemColor.deleted
            }
            if(type == Cons.gitStatusModified) {
                return MyStyleKt.ChangeListItemColor.modified
            }
            if(type == Cons.gitStatusConflict) {
                return MyStyleKt.ChangeListItemColor.conflict
            }
            return Color.Unspecified
        }

        //把worktree的文件恢复为index中的版本。
        fun revertFilesToIndexVersion(repo:Repository, pathSpecList:List<String>) {
            val checkoutOptions = Checkout.Options.defaultOptions()
            //FORCE 强制恢复worktree文件to index，DISABLE_PATHSPEC_MATCH 禁用通配符匹配，强制把文件路径理解成精确的文件名
            checkoutOptions.strategy = EnumSet.of(
                Checkout.StrategyT.FORCE,
                Checkout.StrategyT.DISABLE_PATHSPEC_MATCH,  //禁用glob模糊匹配，就是通配符，星号之类的
            )

            checkoutOptions.setPaths(pathSpecList.toTypedArray())
            Checkout.index(repo, repo.index(), checkoutOptions)
        }

        fun rmUntrackedFiles(fullPathList:List<String>) {
            for (item in fullPathList){
                try {
                    File(item).delete()
                }catch (e:Exception) {
                    MyLog.e(TAG, "#rmStatusUntrackedFiles(): "+e.stackTraceToString())
                }
            }
        }


        //save failed return false, else return true
        fun saveGitUsernameAndEmailForGlobal(
            requireShowErr: (String) -> Unit,
            errText: String,
            errCode1:String,  // for noticed where caused error
            errCode2:String,
            username: String,
            email: String
        ): Boolean {
            try {
//                val commonGitConfigSettings = MoshiUtil.commonGitConfigSettingsJsonAdapter.fromJson(settingsEntity.jsonVal)
                val commonGitConfigSettings = SettingsUtil.getSettingsSnapshot().globalGitConfig
                if (commonGitConfigSettings == null) {
                    requireShowErr(errText + " :$errCode2")
                    return false
                }
                commonGitConfigSettings.username = username
                commonGitConfigSettings.email = email

//                settingsEntity.jsonVal = MoshiUtil.commonGitConfigSettingsJsonAdapter.toJson(commonGitConfigSettings)
//                settingsDb.update(settingsEntity)
                //这里重新读取是用效率换正确性，如果在之前读取一个设置项，然后这直接写入，其他线程再读取配置再写入，有可能设置项会保存为不期望的值，不过就算在这重读，还是有可能保存错，但概率会降低，用内存和效率换正确性
                val settingsWillSave = SettingsUtil.getSettingsSnapshot()
                settingsWillSave.globalGitConfig = commonGitConfigSettings
                SettingsUtil.updateSettings(settingsWillSave)
                return true
            }catch (e:Exception) {
                requireShowErr(e.localizedMessage?:"error save username and email: 12490068")
                MyLog.e(TAG, "#saveGitUsernameAndEmailForGlobal: "+e.stackTraceToString())
                return false
            }
        }

        fun saveGitUsernameAndEmailForRepo(repo: Repository, requireShowErr:(String)->Unit, username:String, email:String):Boolean {
            try{
                val config = getRepoConfigForWrite(repo)

                //删除的条目若不存在会抛异常，所以捕获一下
                try {
                    //字段不为空则存储，为空则删除
                    if(username.isNotBlank()) {
                        config.setString(Cons.gitConfigKeyUserName, username)
                    }else {
                        config.deleteEntry(Cons.gitConfigKeyUserName)
                    }
                }catch (_:Exception) {
                }

                try {
                    //字段不为空则存储，为空则删除
                    if(email.isNotBlank()) {
                        config.setString(Cons.gitConfigKeyUserEmail,email)
                    }else {
                        config.deleteEntry(Cons.gitConfigKeyUserEmail)
                    }
                }catch (_:Exception) {
                }

                return true
            }catch (e:Exception) {
                MyLog.e(TAG, "#saveGitUsernameAndEmailForRepo: "+e.stackTraceToString())
                requireShowErr(e.localizedMessage?:"error save username and email: 18145855")

                return false
            }

        }

        fun getGitUsernameAndEmailFromGlobalConfig():Pair<String,String> {
            //查询公用的git配置文件
            var username = ""
            var email = ""

            val commonGitConfigSettings = SettingsUtil.getSettingsSnapshot().globalGitConfig
            if(commonGitConfigSettings != null) {
                username = commonGitConfigSettings.username
                email = commonGitConfigSettings.email
            }

            return Pair(username, email)
        }

        /**
         * @return Pair(username, email)
         */
        fun getGitUsernameAndEmail(repo: Repository):Pair<String, String> {
            //先检查仓库配置文件是否有email或username，若没有，检查全局git配置项中是否有email，若没有，询问是否现在设置，三个选项：1设置全局email，2设置仓库email，3不设置(中止提交)
            var (username, email) = getGitUserNameAndEmailFromRepo(repo)

            //如果仓库配置文件的username和email为空，则尝试获取全局的
            if(username.isBlank() || email.isBlank()) {
                //获取全局用户名和邮箱
                val (usernameFromGlobal, emailFromGlobal) = getGitUsernameAndEmailFromGlobalConfig()
                //如果配置文件获取的用户名为空，则使用全局的
                if(username.isBlank()) {
                    username = usernameFromGlobal
                }

                //如果配置文件获取的邮箱为空，则使用全局的
                if(email.isBlank()) {
                    email = emailFromGlobal
                }

            }

            //username和email依然有可能是空，调用者仍需自己检测
            return Pair(username, email)
        }

        //注：这的itemList只是用来生成commit msg，实际提交的条目列表还是从index取，所以不用担心这列表数据陈旧导致提交错文件
        fun genCommitMsg(repo:Repository, itemList: List<StatusTypeEntrySaver>?=null): Ret<String?> {
            val repoState = repo.state()
            var actuallyItemList = itemList
            if(actuallyItemList.isNullOrEmpty()) { //如果itemList为null或一个元素都没有，查询一下实际的index列表
                val (isIndexEmpty, indexItemList) = checkIndexIsEmptyAndGetIndexList(repo, "", onlyCheckEmpty = false)  //这里期望获得列表，所以仅检查空传假
                if(repoState!=Repository.StateT.MERGE && (isIndexEmpty || indexItemList.isNullOrEmpty())) {  //如果实际的index没条目，直接返回错误
                    MyLog.w(TAG, "#genCommitMsg() error: repoState=$repoState, isIndexEmpty = "+isIndexEmpty+", indexItemList.isNullOrEmpty() = "+indexItemList.isNullOrEmpty())
                    return Ret.createError(null, "index is Empty!",Ret.ErrCode.indexIsEmpty)
                }

                //执行到这index一定是有东西的，或者是解决冲突的提交，index空也无所谓
                actuallyItemList = indexItemList?: emptyList()
            }

            val limitCharsLen = 200;  //提交信息字符数长度限制
            var count = 0;  //文件记数，用来计算超字符数长度限制后还有几个文件名没追加上


            //生成提交信息
            val filesNum = actuallyItemList.size
            val summary = (if(repoState==Repository.StateT.MERGE) "Conclude Merge, " else "") + ("Updated $filesNum file(s) by PuppyGit:\n")
            val descriptions=StringBuilder(summary)
            val split = ", "
            for(item in actuallyItemList) {  //终止条件为：列表遍历完毕 或者 达到包含文件名的限制数目(上面的limit变量控制)
                descriptions.append(item.fileName+split)
                ++count;
                if(descriptions.length>limitCharsLen) {
                    descriptions.append("...omitted ${filesNum - count} file(s)")
                    break
                }
//                if(++start > limit) {  //达到包含文件名的限制数目则break(上面的limit变量控制)
//                    break
//                }
            }
            val commitMsg = descriptions.removeSuffix(split).toString()  //移除最后的 “, ”，如果有的话
            return Ret.createSuccess(commitMsg)
        }

        @Deprecated("限制文件名数量的版本，在20240425弃用了")
        fun genCommitMsg_deprecated_at_20240425(repo:Repository, itemList: List<StatusTypeEntrySaver>?=null): Ret<String?> {
            var actuallyItemList = itemList
            if(actuallyItemList == null) { //如果itemList为null，查询一下实际的index列表
                val (isIndexEmpty, indexItemList) = checkIndexIsEmptyAndGetIndexList(repo, "", onlyCheckEmpty = false)  //这里期望获得列表，所以仅检查空传假
                if(isIndexEmpty || indexItemList.isNullOrEmpty()) {
                    MyLog.d(TAG, "#genCommitMsg() error! isIndexEmpty = "+isIndexEmpty+", indexItemList.isNullOrEmpty() = "+indexItemList.isNullOrEmpty())
                    return Ret.createError(null, "index is Empty!",Ret.ErrCode.indexIsEmpty)
                }

                actuallyItemList = indexItemList
            }

            val limit = 5;  //提交信息包含几个文件名
            var start = 0;

            //生成提交信息
            val sb=StringBuilder()
            val suffix = ", "
            for(item in actuallyItemList) {  //终止条件为：列表遍历完毕 或者 达到包含文件名的限制数目(上面的limit变量控制)
                sb.append(item.fileName+suffix)
                if(++start > limit) {  //达到包含文件名的限制数目则break(上面的limit变量控制)
                    break
                }
            }
            val fileListStr = sb.removeSuffix(suffix).toString()  //移除最后的 “, ”
            val commitMsg = "Updated " + actuallyItemList.size +" files:" +fileListStr+"...and more. -- by PuppyGit"
            return Ret.createSuccess(commitMsg)
        }

        private fun doCreateCommit(repo: Repository, msg: String, username: String, email: String, curBranchFullRefSpec:String, parentList:List<Commit>, amend: Boolean, overwriteAuthorWhenAmend:Boolean, cleanRepoStateIfSuccess:Boolean):Ret<Oid?> {
            if(username.isBlank()) {
                return Ret.createError(null, "username is blank", Ret.ErrCode.usernameIsBlank)
            }
            if(email.isBlank()) {
                return Ret.createError(null, "email is blank",Ret.ErrCode.emailIsBlank)
            }

            val sign: Signature = Signature.create(username, email)

            //write repo's index as a tree
            val tree = Tree.lookup(repo, repo.index().writeTree())

            val messageEncoding = null // if null will use utf8
            //commit
            val resultCommitOid =if(amend){
                val headCommitRet = getHeadCommit(repo)
                if(headCommitRet.hasError()) {
                    return Ret.createError(null, "resolve HEAD failed!")
                }

                val author = if(overwriteAuthorWhenAmend) sign else null  // if null will keep origin commit author info
                val committer = sign  // committer will always is actually committer, even is amend

                Commit.amend(headCommitRet.data!!, curBranchFullRefSpec, author, committer, messageEncoding, msg, tree)
            }else{
                //如果cherrypick且没勾选覆盖作者信息，就采用被pick的提交的原始作者信息，否则使用执行提交的人的用户信息（用户名和邮箱）
                val author = if(!overwriteAuthorWhenAmend && (repo.state()==Repository.StateT.CHERRYPICK || repo.state()==Repository.StateT.REBASE_MERGE)) {  //use origin commit's username and email
                    val commitRet = if(repo.state() == Repository.StateT.CHERRYPICK) getCherryPickHeadCommit(repo) else /*Rebase*/ rebaseGetCurCommitRet(repo)
                    if(commitRet.hasError() || commitRet.data==null) {
                        return Ret.createError(null, "query origin commit author info err!")
                    }

                    commitRet.data!!.author()
                }else {  //使用创建提交的人的username和email
                    sign
                }

                Commit.create(
                    repo, curBranchFullRefSpec, author, sign, messageEncoding,
                    msg,
                    tree, parentList
                )
            }

            //执行到这创建commit就成功了

            //检查是否请求在创建提交成功后清除仓库状态
            if(cleanRepoStateIfSuccess) {
                cleanRepoState(repo)
            }

            return Ret.createSuccess(resultCommitOid)
        }

        fun cleanRepoState(repo: Repository) {
            repo.stateCleanup()
        }

        fun isReadyCreateCommit(repo: Repository):Ret<Oid?> {
            val appContext = AppModel.singleInstanceHolder.appContext

            //检查是否存在未stage的冲突，若有则不能创建提交
            if(hasConflictItemInRepo(repo)) {
                return Ret.createError(null, appContext.getString(R.string.plz_resolve_conflicts_first), Ret.ErrCode.hasConflictsNotStaged)
            }

            //检查仓库的index是否为空，为空就不用提交了，注意，如果仓库状态是merging，其实可创建空提交，例如：你合并分支，冲突，然后接受我方文件，这时就没冲突条目，worktree和index也为空了，因为worktree和index都是根据HEAD比较的，所以无法判断是否合并分支，因此，如果在合并状态下，允许创建空提交，实际上不是空提交，因为还会和另一个提交树合并，只是对当前分支来说是空提交罢了
            val repoState = repo.state()
            val indexIsEmpty1 = indexIsEmpty(repo)
            if(repoState==Repository.StateT.MERGE && getMergeHeads(repo).isEmpty()) {
                return Ret.createError(null, appContext.getString(R.string.repo_state_is_merge_but_no_merge_head_found_plz_use_abort_merge_to_clean_state))
            }else if(repoState==Repository.StateT.REBASE_MERGE) {
                if(rebaseGetCurCommit(repo) == null) {
                    return Ret.createError(null, appContext.getString(R.string.repo_state_is_rebase_but_no_rebase_head_found_plz_use_abort_rebase_to_clean_state))
                }
            }else if(repoState==Repository.StateT.CHERRYPICK) {
                if(getCherryPickHeadCommit(repo).data == null) {
                    return Ret.createError(null, appContext.getString(R.string.repo_state_is_cherrypick_but_no_cherrypick_head_found_plz_use_abort_cherrypick_to_clean_state))
                }
            }

            //这几种状态不用检查index是否为空，merge可能当前分支为空，另一分支不为空；rebase和cherrypick可能为空，但可继续执行操作然后清理状态
            if(repoState!=Repository.StateT.MERGE && repoState!=Repository.StateT.REBASE_MERGE && repoState!=Repository.StateT.CHERRYPICK
                && indexIsEmpty1
            ) {
                return Ret.createError(null, appContext.getString(R.string.index_is_empty), Ret.ErrCode.indexIsEmpty)
            }

            // is ready for commit
            return Ret.createSuccess(null)
        }

        //通过检查才创建提交
        fun createCommitIfPassedCheck(repo: Repository, msg: String, username: String, email: String):Ret<Oid?> {
            val isReady = isReadyCreateCommit(repo)
            if(isReady.hasError()) {  //如果有错，说明没准备就绪，直接返回
                return isReady
            }

            //没错则说明准备就绪，创建提交
            //create commit
            return createCommit(repo, msg,username,email)
        }

        /**
         * 直接创建提交，需用调用者自行检查index是否为空，是否存在冲突条目等
         * 若想commit后清除仓库state，需自行操作，本函数不负责
         * @param indexItemList 用来生成提交信息，可不传，将自动查询index，建议如果在index页面执行commit就顺便传一下
         */
        fun createCommit(repo: Repository, msg: String, username: String, email: String, branchFullRefName:String="", indexItemList:List<StatusTypeEntrySaver> ?= null, parents:List<Commit>? = null, amend:Boolean = false, overwriteAuthorWhenAmend:Boolean = false, cleanRepoStateIfSuccess:Boolean=false):Ret<Oid?> {
            val funName = "createCommit"

//            println("repo.state=${repo.state()}") //test, debug

            //不处理的情况，正常来说应在界面逻辑上做检测，执行对应操作，不应该丢给用户一个错误信息
            if(repo.state() == Repository.StateT.REBASE_MERGE) {
                return Ret.createError(null, "plz use Rebase Continue instead")
            }
            //cherryPick continue最终就是调用本函数收尾的，这里再不允许cherrypick状态，就矛盾了
//            else if(repo.state() == StateT.CHERRYPICK) {
//                return Ret.createError(null, "plz use Cherrypick Continue instead")
//            }


            val needQueryRefNameByFunc = branchFullRefName.isEmpty()
            val needAddParentsByFunc = (parents == null)  //parent若为null将自动添加parents (ps:会处理merge时多head的情况)
            val parents = if(needAddParentsByFunc) mutableListOf<Commit>() else parents!!
            var branchFullRefName = branchFullRefName  //非detached HEAD时为分支名，否则为"HEAD"

            if(needAddParentsByFunc || needQueryRefNameByFunc) {  //只有这两种情况需要查询head，所以判断一下
                //取出head 引用名，创建提交后会更新引用
                // 这是个普通的创建提交的案例，如果是merge成功后创建的提交应该有两个父提交： HEAD 和 targetBranch.
                val headRef = resolveRefByName(repo, "HEAD")
                if(headRef==null) {
                    return Ret.createError(null, "get HEAD failed!", Ret.ErrCode.headIsNull)
                }

                //参数如果为空使用参数中的值，否则自动查询head
                if(needQueryRefNameByFunc) {
                    branchFullRefName = headRef.name()
                }


                // 打印下repo state，如果是merging状态，需要取出 merge heads
                MyLog.d(TAG, "#$funName(), repo.state() = "+repo.state())


                //添加parents如果需要的话

                //若parent不为null将强制使用参数中的值
                if(needAddParentsByFunc) {
                    // add HEAD
                    val curBranchLatestCommit = headRef.id()?.let { Commit.lookup(repo, it) }
                    if (curBranchLatestCommit != null) {
                        (parents as MutableList<Commit>).add(curBranchLatestCommit)
                    }else {
                        return Ret.createError(null,"get current HEAD latest commit failed",Ret.ErrCode.createCommitFailedByGetRepoHeadCommitFaild)
                    }

                    //add merge heads if need
                    if(repo.state() == Repository.StateT.MERGE) {
                        //遍历 merge heads
                        forEachMergeHeads(repo) { oid ->
                            (parents as MutableList<Commit>).add(Commit.lookup(repo, oid))  //添加到parents列表
                        }
                    }

                }

            }


            //如果msg为空，自动生成提交信息
            val msg = if(msg.isBlank()) {  //maybe empty is better? no, blank better!
                val genCommitMsgRet = genCommitMsg(repo, indexItemList)

                var cmtmsg = genCommitMsgRet.data
                if(genCommitMsgRet.hasError() || cmtmsg.isNullOrBlank()) {
                    MyLog.e(TAG, "#$funName, genCommitMsg Err!")
                    cmtmsg = "Update files"
                }

                cmtmsg
            }else {
                msg
            }

            return doCreateCommit(repo,msg,username,email,branchFullRefName,parents, amend, overwriteAuthorWhenAmend, cleanRepoStateIfSuccess)
        }

        //param eg: main; ret eg: refs/heads/main
        fun getRefsHeadsBranchFullRefSpecFromShortRefSpec(shortRefSpec:String):String {
            return "refs/heads/"+shortRefSpec
        }

        //ret, eg: refs/heads/abc/def
        fun getRepoCurBranchFullRefSpec(repo: Repository):String {
//            val headRef = Reference.dwim(repo, "HEAD")  // == repo.head()
            val headRef = repo.head()
            return headRef?.name() ?: ""
        }
        //ret, eg: abc/def
        fun getRepoCurBranchShortRefSpec(repo: Repository):String {
//            val headRef = Reference.dwim(repo, "HEAD")
            val headRef = repo.head()
            return headRef?.shorthand() ?: ""
        }

        fun getUpstreamOfBranch(repo: Repository, shortBranchName: String): Upstream {
            try {
                val u = Upstream()
                //HEAD 需要特殊处理下，因为：HEAD要么detached直接指向提交，没上游；
                // 要么作为符号引用指向分支，上游应该属于其指向的分支，所以HEAD也没上游。
                // HEAD不管怎样都不可能有上游。
                // 而且libgit2禁止创建refs/heads/HEAD分支，
                // 所以实际也不会有和HEAD（此处HEAD为长名，指常见的真HEAD）重名的本地分支存在，
                // 因此也不需要处理 refs/heads/HEAD分支，所以，在此判断如果短名为HEAD，只有一种可能，那就是真的HEAD分支，无上游。
                if(shortBranchName.isBlank() || shortBranchName == "HEAD") {
                    return u
                }

                val c = getRepoConfigForRead(repo)

                //拼接完整分支名
                // 注意：非HEAD必须加refs/heads前缀，不然如果tag和分支同名，会解析错，HEAD不能加
                val refsHeadsBranchName = "refs/heads/$shortBranchName"

                //这个得用lookup解析，要不然分支和tag重名，可能会解析错，虽然上面加了refs/heads前缀以明确匹配分支，但对于HEAD，不能加此前缀，如果同时有个refs/tags/HEAD，可能就会解析成tag而不是分支
                val localBranchRef = resolveRefByName(repo, refsHeadsBranchName, trueUseDwimFalseUseLookup = false)

                if(localBranchRef==null) {
                    //抛异常，然后会记日志，然后返回给调用者一个空的Upstream
                    throw RuntimeException("resolve shortBranchName to reference failed! shortBranchName=${shortBranchName}, refsHeadsBranchName=$refsHeadsBranchName")
                }

                val remoteFromConfig = c.getString("branch."+shortBranchName+".remote").orElse("")
                val fullRefSpecFromConfig = c.getString("branch."+shortBranchName+".merge").orElse("")

                //只有remote和fullrefspec都有值时，上游才有意义，否则返回一个空的上游，就当没上游，让用户去设置
                if(remoteFromConfig.isBlank() || fullRefSpecFromConfig.isBlank()) {  //没remote 或 没远程分支，等于没有有效上游，直接返回空上游，请求用户设置一个
                    return u  //返回空上游
                }

                //开始设置上游字段
                //执行到这，说明配置文件里有上游(虽然不一定正确，但只要不是我搞乱的，就不用我管)，更新下相关字段
                u.remote = remoteFromConfig
                u.branchRefsHeadsFullRefSpec = fullRefSpecFromConfig
                // 设置本地分支相关字段，反正都取出来了设置一下也无妨
                u.downstreamLocalBranchShortRefSpec = localBranchRef?.shorthand()?:""
                u.downstreamLocalBranchRefsHeadsFullRefSpec = localBranchRef?.name()?:""
                u.localOid = localBranchRef?.peel(GitObject.Type.COMMIT)?.id()?.toString() ?:""

                //设置pushRefSpec，“本地分支:远程分支”，形如：refs/heads/main:refs/heads/main
                u.pushRefSpec = u.downstreamLocalBranchRefsHeadsFullRefSpec+":"+u.branchRefsHeadsFullRefSpec

                //用配置文件里的值拼接成远程分支名，输出例如：origin/main 这样的远程分支名，如果传参为空字符串，则返回空字符串
                u.remoteBranchShortRefSpec = getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(u.remote, u.branchRefsHeadsFullRefSpec)
                u.remoteBranchRefsRemotesFullRefSpec = "refs/remotes/"+u.remoteBranchShortRefSpec
                //获取远程分支去除refs/heads/后的名字，这个值等于远程分支短名去除远程前缀(例如origin/)之后的值。
                u.remoteBranchShortRefSpecNoPrefix = getShortRefSpecByRefsHeadsRefSpec(u.branchRefsHeadsFullRefSpec)?:""  // 或调用 removeRemoteShortRefSpecPrefix(u.remote, u.remoteBranchShortRefSpec)
                u.isPublished = false  //先设置成远程分支没发布，后续会检查是否存在远程分支，若存在，再更新这个变量


                val remoteOid = resolveCommitOidByRef(repo, u.remoteBranchRefsRemotesFullRefSpec)
                if(remoteOid!=null && !remoteOid.isNullOrEmptyOrZero) {
                    u.remoteOid = remoteOid.toString()
                }

                //查询是否实际存在上游远程分支
                //这判断好像多余，上面其实已判断，如果remote和branchRefHeadsFullRefSpec值为空就直接返回了
                if(u.remote.isNotBlank() && u.branchRefsHeadsFullRefSpec.isNotBlank()) {  //如果配置文件里不为空，则查询下有无实际远程分支
                    //方法1：检测能否取得上游分支引用并获得短名称，若能，分支肯定存在，也就是已发布
//                val remoteBranchShortName = getUpstreamRemoteBranchShortRefSpecByLocalBranchShortName(repo, shortBranchName)
//                if(remoteBranchShortName!=null && remoteBranchShortName.isNotBlank()){
//                    u.isPublished = true
////                    u.remoteBranchShortRefSpec = remoteBranchShortName  //这个值应该和上面设置的值一样，所以更新不更新无所谓
//                }

                    //方法2：直接检测是否存在配置文件中设置的当前分支关联的上游remote和分支引用，若存在，上游则存在，否则不存在
                    u.isPublished = isUpstreamActuallyExistOnLocal(repo, u.remote, u.branchRefsHeadsFullRefSpec)
                }

                return u
            }catch (e:Exception) {
                MyLog.e(TAG, "#getUpstreamOfBranch() error:"+e.stackTraceToString())
                //发生异常，返回一个空upstream
                return Upstream()  //确保出异常返回空上游，所以这里新创建一个
            }
        }

        fun setUpstreamForBranch(repo: Repository, upstream: Upstream, setForBranchName:String="") {
            setUpstreamForBranchByRemoteAndRefspec(repo, upstream.remote, upstream.branchRefsHeadsFullRefSpec, setForBranchName)
        }

        //remote eg: origin, fullBranchRefSpec eg: refs/heads/main
        fun setUpstreamForBranchByRemoteAndRefspec(repo: Repository, remote: String?, fullBranchRefSpec:String?, targetBranchShortName:String=""):Boolean {
            if(remote.isNullOrBlank() || fullBranchRefSpec.isNullOrBlank()) {
                MyLog.d(TAG,"#setUpstreamForBranchByRemoteAndRefspec, bad upstream, remote="+remote+", fullBranchRefSpec="+fullBranchRefSpec)
                return false
            }
            val c = getRepoConfigForWrite(repo)

            //如果调用者提供的分支名是空，查询仓库当前分支名，否则使用提供的分支名
            val branchWhichWillUpdate = if(targetBranchShortName.isBlank()) {
                getRepoCurBranchShortRefSpec(repo)
            }else {
                targetBranchShortName
            }

            c.setString("branch."+branchWhichWillUpdate+".remote", remote)
            c.setString("branch."+branchWhichWillUpdate+".merge",fullBranchRefSpec)
            return true
        }

        //注意：如果我设置了上游，写入了配置文件但还没推送，这个方法就会返回null。（这时候仓库卡片依然显示远程分支，分支列表显示本地分支的上游但后面会跟个未发布的提示）
        //例如：输入 main，输出 origin/main
        fun getUpstreamRemoteBranchShortRefSpecByLocalBranchShortName(repo:Repository, localBranchShortBranchName:String):String? {
            val localBranchRef = resolveBranch(repo, localBranchShortBranchName, Branch.BranchType.LOCAL)  //取出本地分支的Reference
            if(localBranchRef == null) {
                return null
            }
            val upstreamRef = Branch.upstream(localBranchRef)  //取出本地分支的上游引用
            if(upstreamRef==null) {
                return null
            }

            return upstreamRef.shorthand()
        }

        //无效返回真，有效返回假
        fun isUpstreamInvalid(upstream: Upstream?):Boolean {
            if(upstream == null) {
                return true
            }

            return isUpstreamFieldsInvalid(upstream.remote, upstream.branchRefsHeadsFullRefSpec)
        }
        fun isUpstreamFieldsInvalid(remote: String?, branchFullRefSpec: String?):Boolean {
            return isStringListHasInvalidItem(listOf(remote,branchFullRefSpec))
        }

        fun isUsernameAndEmailInvalid(username: String?, email: String?):Boolean {
            return isStringListHasInvalidItem(listOf(username,email))
        }

        fun isStringListHasInvalidItem(strList:List<String?>): Boolean{
            for(s in strList) {
                if(s==null || s.isBlank()) {
                    return true
                }
            }

            return false
        }

        //检查上游remote和branch是否在本地存在，如果不存在，则可能是配置好了没推送过，需要先推送创建远程分支(git push -u)，才能fetch
        //remote例如：origin；branchFullRefSpec例如：refs/heads/main
        fun isUpstreamActuallyExistOnLocal(repo: Repository, remote:String, branchFullRefSpec:String):Boolean {
            //把 remote和全分支名（例如origin和refs/heads/main），拼接成需要检查的远程分支名（例如：origin/main）
            val needCheckRemoteBranchName = getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(remote, branchFullRefSpec)
//            MyLog.d(TAG,"#isUpstreamActuallyExistOnLocal(), needCheckRemoteBranchName="+needCheckRemoteBranchName)
            try {
                val ref = resolveRefByName(repo, needCheckRemoteBranchName)  //x (改用resolveRefByName()了，不会抛异常了) 这个必须捕获异常，要不然设置一个烂upstream，连branch列表都显示不出来，用户想取消设置都没门，绝对不行！这里捕获的话，如果用户设置一个烂upstream，还能去分支列表取消一下
                val exist = ref!=null
//                MyLog.d(TAG,"#isUpstreamActuallyExistOnLocal(): exist=$exist")
                return exist
            }catch (e:Exception) {
                MyLog.e(TAG, "#isUpstreamActuallyExistOnLocal() error:"+e.stackTraceToString())
                return false
            }
        }

        fun getRemoteList(repo: Repository): List<String> {
            try {
                return Remote.list(repo)
            }catch (e:Exception) {
                MyLog.e(TAG, "#getRemoteList() err, will return an empty list!\nException is:${e.stackTraceToString()}")
                return listOf()
            }
        }

        //如果需要获取最新的信息，doFetchIfNeed传true，如果你在外部已经fetch过了，或者你确定当前本地仓库就是最新的信息（包含最新的远程分支是否存在之类的信息），那就传false
        fun isBranchHasUpstream(repo: Repository):Boolean {
            val shortBranchName = getRepoCurBranchShortRefSpec(repo)
            val upstream: Upstream = getUpstreamOfBranch(repo, shortBranchName)
            if (upstream.remote.isBlank() || upstream.branchRefsHeadsFullRefSpec.isBlank()) {
                return false
            }
            return true
        }

        //pathSpecList，仓库路径下的相对路径列表
        fun addToIndexThenWriteToDisk(repo: Repository, pathSpecList: List<String>) {
            val index = repo.index()
            pathSpecList.forEach {
                index.add(it)
            }
            //写入硬盘
            index.write()
        }

        //pathSpecList，仓库路径下的相对路径列表
        //列表pair数据结构：(isFile, path)
        fun removePathSpecListFromIndexThenWriteToDisk(repo: Repository, pathSpecList: List<Pair<Boolean, String>>) {
            val repoIndex = repo.index()
            pathSpecList.forEach {
                removeFromIndexThenWriteToDisk(repoIndex, it, requireWriteToDisk=false)  //最后一个值表示不希望调用的函数执行 index.write()，我删完列表后自己会执行，不需要每个条目都执行，所以传false请求调用的函数别执行index.write()
            }
            //写入硬盘，保存修改
            repoIndex.write()
        }

        //pair数据结构：(isFile, path)
        // 参数 requireWriteToDisk 应用场景： 例如外部有个列表，想删完所有条目后写入一次，那调用这个方法时就可以传false，然后自己写入即可；如果是单次调用本方法，且希望本方法保存修改，则传true
        //如果移除的文件path是仓库.git目录下的，则不会从git移除文件，也不会报错，就跟没移过一样，所以调用此方法前不用手动排除.git目录下的文件
        fun removeFromIndexThenWriteToDisk(index:Index, isFileAndPathSpec: Pair<Boolean, String>, requireWriteToDisk:Boolean=false) {
            val (isFile, pathspec) = isFileAndPathSpec  //pathspec是仓库下相对路径
            if(isFile) {  // 移除文件
                index.removeByPath(pathspec)
            }else {  // 移除目录
                index.removeDirectory(pathspec, 0)  //第2个参数没看懂什么意思，不过传1好像删除不了
            }

            //如果调用者请求将修改写入硬盘则写入。
            if(requireWriteToDisk) {
                //写入硬盘
                index.write()
            }
        }

        //添加worktree新和修改的文件到index，从index移除已删除的worktree文件
        fun stageStatusEntryAndWriteToDisk(repo: Repository, list: List<StatusTypeEntrySaver>) {
            val index = repo.index()
            list.forEach {
                if(it.changeType == Cons.gitStatusDeleted) {  //changeType is "Deleted"
                    index.removeByPath(it.relativePathUnderRepo)
                }else{  // changeType is "Modified" or "New" or "Conflict"
                    index.add(it.relativePathUnderRepo)
                }
            }
            //写入硬盘
            index.write()
        }

        //操作成功返回true，发生异常返回false
        //remoteList元素为 Pair(remoteName, credentialObj)
        //入参requireUnshallow如果为真，会执行unshallowfetch，如果为假，会判断仓库是否是shallow，如果是则取出其depth值，否则会fetch all(depth=0，fetch的默认depth)
        fun fetchRemoteListForRepo(
            repo: Repository,
            remoteList:List<RemoteAndCredentials>,
            repoFromDb:RepoEntity,
            requireUnshallow:Boolean=false,
            refspecs:Array<String>? = null,

            //默认fetch分支时,PruneT.PRUNE + Remote.AutotagOptionT.UNSPECIFIED，不会删除本地有远程没有的tags，也不会覆盖本地和远程同名的tags，与期望一致
            pruneType:FetchOptions.PruneT =FetchOptions.PruneT.PRUNE,  // prune 删除本地有远程没有的，如果fetch branch，对tag无效，但如果refspec是refs/tags/xxx，则对tag有效；UNSPECIFIED，默认值，遵循配置文件； NO_prune，强制不删
            downloadTags: Remote.AutotagOptionT = Remote.AutotagOptionT.UNSPECIFIED,  // auto，更新已有；all 下载所有；None严格遵循refspec list不自动附加tag相关refspec；unspecified，默认值，遵循配置文件
        ) {
            var repoIsShallow = isRepoShallow(repo)
            val repoGitDirPath = repo.workdir().pathString + File.separator + ".git"
            val shallowFile = File(repoGitDirPath, "shallow")

            for(remoteAndCredentials in remoteList) {
//                if(dev_ProModeOn || (isShallowAndSingleBranchPassed && UserInfo.isPro())) {
//                    //目前20240509，libgit2有bug，如果fetch，有可能会删除本地shallow仓库的shallow文件
//                    //恢复shallow文件(这里不用恢复啊！如果repoIsShallow为true，则必然有shallow文件，即使如果fetch多个remote，且fetch当前就会因bug删除shallow文件，那我在末尾也又重新创建了shallow文件，
                //所以到下次循环，shallow文件还是必然存在，所以，其实只要在末尾恢复shallow文件就够了
//                    if(repoIsShallow && !shallowFile.exists()) {
//                        ShallowManage.restoreShallowByBak1(repoGitDirPath)
//                    }
//
//                }

                val fetchOpts = FetchOptions.createDefault()
                //设置depth值，实现将一个shallowed仓库改成unshallow的时候，和这部分代码有关
                //TODO searchKey(多remote相关待测试) :目前的机制是多remote的情况下只有第一次unshallow成功会删除 shallow和shallow.1.bak文件，后续不会再删除。但是，对每个remote执行fetch都会用DepthT.UNSHALLOW，【我不太确定unshallow的时候如果shallow文件不存在，是否会报错，等以后实现了多remote，测试一下】
                if(requireUnshallow) {//请求unshallow
                    fetchOpts.depth = FetchOptions.DepthT.UNSHALLOW
                }else {
                    fetchOpts.depth = FetchOptions.DepthT.FULL  //默认值

                      //不需要判断是否is shallow，克隆成功后再fetch直接用默认值full即可，如果再设shallow，又会找不到object id，而且unshallow都救不回来！但如果不设，报找不到object id，unshallow还能抢救一下
//                    if(dbIntToBool(repoFromDb.isShallow)) {  //仓库状态是shallow，取出depth
//                        fetchOpts.depth = repoFromDb.depth
//                    }else {  //仓库状态不是shallow，用默认值
//                        fetchOpts.depth = FetchOptions.DepthT.FULL  //默认值
//                    }
                }

                fetchOpts.downloadTags = downloadTags
                fetchOpts.prune = pruneType  //prune会删除本地存在但远程已经不存在的分支以及tag，fetch tag时最好关了这个，不然会把本地有远程没有的tag删掉

//                val (remoteName, credential) = pair
                val remoteName = remoteAndCredentials.remoteName
                val credential = remoteAndCredentials.fetchCredential
                //TEST
//                LibgitTwo.jniSetCredentialCbTest(fetchOpts.callbacks.rawPointer)
                //TEST
                if(credential!=null) {  //如果不是null，设置下验证凭据的回调
                    setCredentialCbForRemoteCallbacks(fetchOpts.callbacks, credential.type, credential.value, credential.pass)
                }
                val remote = Remote.lookup(repo, remoteName)
                //这里不要用?，有错直接报错
                remote!!.fetch(refspecs, fetchOpts, "fetch: $remoteName")

                //判断是否需要恢复shallow文件
//                if(dev_ProModeOn || (isShallowAndSingleBranchPassed && UserInfo.isPro())) {
                //恢复shallow文件
                //如果请求unshallow 且 shallow文件不再存在，则说明unshallow成功，这时删除备份的shallow文件
                //只有shallow file不存在才有可能需要恢复，若shallow file存在，则百分百不需要恢复
                if(repoIsShallow && !shallowFile.exists()) {
                    if (requireUnshallow) {  //执行到这，说明仓库原本是shallow且请求unshallow，且fetch完后shallow文件不存在了，说明unshallow成功，所以可以删除bak1了
                        ShallowManage.deleteBak1(repoGitDirPath)  //unshallow会删除shallow文件，我这再删除下 shallow.1.bak， 之后，就不会再恢复shallow文件了
                        repoIsShallow=false  //更新shallow值，避免fetch多个remote时发生不必要的重入
                        MyLog.d(TAG, "deleted '${ShallowManage.bak1}' for repo '${repoFromDb.repoName}'")
                    //如果仓库最初是shallow，且没请求unshallow或请求了unshallow但操作失败，则恢复shallow文件
                    } else { //  实际条件为：仓库原本是shallow，且现在shallow文件没了，且没请求unshallow，说明fetch错误删除了shallow文件，恢复下
                        ShallowManage.restoreShallowFile(repoGitDirPath)
                        //注：这里不需要把repoIsShallow设为true，因为如果是false，根本不可能执行这个代码块
                    }
                }
//                }
            }
        }

        //将一个仓库状态取消shallowed，注：我对shallow有点困惑
        //注：shallow是针对remote的（应该吧），应该放到remote管理里，加个unshallow fetch的选项
        fun unshallowRepoByRemoteList(repo: Repository, remoteList:List<RemoteAndCredentials>,repoFromDb: RepoEntity){
            fetchRemoteListForRepo(repo, remoteList, repoFromDb, requireUnshallow = true)
        }

        //这个方法有两部分组成：1对git仓库所有remotes执行unshallow fetch。 2更新数据库中的仓库的isShallow字段为假
        suspend fun unshallowRepo(repo: Repository, repoFromDb: RepoEntity, repoDb:RepoRepository, remoteDb:RemoteRepository, credentialDb: CredentialRepository):Ret<String?> {
            try {
                val remoteDtoListFromDb = remoteDb.getRemoteDtoListByRepoId(repoFromDb.id)
                val remoteAndCredentialPairList = genRemoteCredentialPairList(remoteDtoListFromDb, credentialDb, requireFetchCredential = true, requirePushCredential = false)

                //仅对origin执行unshallow（同时在添加remote时不设置depth，fetch完整remote），其实就够了，参见：“对已经unshallow的仓库再执行unshallow会不会报错 测试 20240523”
                //TODO searchKey(多remote相关待测试) : 需要测试，支持添加多个remote后，新添加的remote是否直接fetch所有提交，如果是，这的代码就不用改，否则，取消注释下面的代码，改成先对origin执行unshallow，然后对所有分支执行 DepthT=UNSHALLOW的fetch
                val singleOriginList = listOf(remoteAndCredentialPairList.first { it.remoteName == Cons.gitDefaultRemoteOrigin })
                unshallowRepoByRemoteList(repo, singleOriginList, repoFromDb)
                //对仓库所有remote执行unshallow fetch
//                unshallowRepoByRemoteList(repo, remoteListWithCredential, repoFromDb)

                //更新数据库中的repo isShallow为假
                repoDb.updateIsShallow(repoFromDb.id, Cons.dbCommonFalse)
                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#unshallowRepo(): err:"+e.stackTraceToString())

                val errMsg = "unshallow err:"+e.localizedMessage
                createAndInsertError(repoFromDb.id, errMsg)
                return Ret.createError(null, errMsg, Ret.ErrCode.unshallowRepoErr)
            }
        }

        //输入remoteDto列表，返回包含remote名和凭据的Pair列表
        fun genRemoteCredentialPairList(list:List<RemoteDto>, credentialDb: CredentialRepository, requireFetchCredential:Boolean, requirePushCredential:Boolean):List<RemoteAndCredentials> {
            //remote名和凭据组合的列表
            val remoteCredentialList = mutableListOf<RemoteAndCredentials>()
//            val credentialDb = AppModel.singleInstanceHolder.dbContainer.credentialRepository
            list.forEach {  //添加remote名和凭据进列表
                val rac = RemoteAndCredentials()
                rac.remoteName = it.remoteName

                if(requireFetchCredential) {
                    var credential: CredentialEntity? = null
                    //用户名和密码至少一个不为空才创建凭据
                    if (it.credentialVal?.isNotBlank() == true || it.credentialPass?.isNotBlank() == true) {
                        credential = CredentialEntity()
                        credential.value = it.credentialVal ?: ""
                        credential.pass = it.credentialPass ?: ""
                        credential.type = it.credentialType
                        //解密下密码
                        credentialDb.decryptPassIfNeed(credential)
                        rac.fetchCredential = credential
                    }
                }
                if(requirePushCredential) {
                    var credential: CredentialEntity? = null
                    //用户名和密码至少一个不为空才创建凭据
                    if (it.pushCredentialVal?.isNotBlank() == true || it.pushCredentialPass?.isNotBlank() == true) {
                        credential = CredentialEntity()
                        credential.value = it.pushCredentialVal ?: ""
                        credential.pass = it.pushCredentialPass ?: ""
                        credential.type = it.pushCredentialType
                        //解密下密码
                        credentialDb.decryptPassIfNeed(credential)
                        rac.pushCredential = credential
                    }
                }
                //添加进待fetch/push列表
                remoteCredentialList.add(rac)
            }

            return remoteCredentialList
        }

        //本方法有可能失败，最好trycatch一下，在失败时显示提示
        //成功返回true；失败返回false
        fun fetchRemoteForRepo(repo:Repository, remoteName: String, credential:CredentialEntity?, repoFromDb: RepoEntity, refspecs:Array<String>? = null) {
            val remote = listOf(RemoteAndCredentials(remoteName, fetchCredential = credential))
            fetchRemoteListForRepo(repo, remote, repoFromDb, refspecs=refspecs)
        }

        fun setCredentialCbForRemoteCallbacks(remoteCallbacks: Remote.Callbacks, credentialType: Int, usernameOrPrivateKey:String, passOrPassphrase:String) {
//            用户名和密码如果都为空就不用设置了，如果一个为空，还是要设置，有的可只设token，例如gitlab就是(电脑是，手机不知道行不行)；有的可只设private key，就是只有私钥没passphrase的情况，所以这两个可只有一个为空，但不能两个都为空，两个都为空就没有任何意义了。
//            MyLog.d(TAG,"#setCredentialForFetchOptions(): username.isBlank():"+usernameOrPrivateKey.isBlank()+", passOrPassphrase.isBlank():"+passOrPassphrase.isBlank())
            if(usernameOrPrivateKey.isBlank() && passOrPassphrase.isBlank()) {
                MyLog.w(TAG, "#setCredentialCbForRemoteCallbacks(): call method with empty username/privatekey and password/passphrase")
                return;
            }
            remoteCallbacks.setCredAcquireCb(getCredentialCb(credentialType, usernameOrPrivateKey, passOrPassphrase))
        }

        suspend fun getRemoteCredential(remoteDb:RemoteRepository, credentialDb:CredentialRepository, repoId:String, remoteName:String, trueFetchFalsePush:Boolean):CredentialEntity? {
            val remote = remoteDb.getByRepoIdAndRemoteName(repoId, remoteName)

            val credentialId = if(trueFetchFalsePush) {remote?.credentialId ?:""} else {remote?.pushCredentialId ?:""}

            if(remote == null || credentialId.isBlank()) {
                return null
            }

            val credential = credentialDb.getByIdWithDecrypt(credentialId)

            return credential
        }

        fun mergeOneHead(repo: Repository, targetRefName: String, username: String, email: String, requireMergeByRevspec:Boolean=false, revspec: String=""):Ret<Oid?> {
            return mergeOrRebase(repo, targetRefName, username, email, requireMergeByRevspec, revspec, trueMergeFalseRebase = true)
        }

        private fun startRebase(
            repo: Repository,
            theirHeads: List<AnnotatedCommit>,
            username: String,
            email: String
        ):Ret<Oid?> {
            val funName = "startRebase"

            //分析仓库状态
            val state = repo.state()

            //这里不用像merge一样检查是否有冲突，因为仓库有可能状态为rebase但不存在任何冲突，例如你执行为rebase.next没提交或提交完成，然后手机突然关机、app进程突然被杀掉，等，都有可能导致那种情况发生。
            if(state == null || (state != Repository.StateT.NONE)) {
                return Ret.createError(null, "err:repo state is not 'NONE'", Ret.ErrCode.rebaseFailedByRepoStateIsNotNone)
            }

//            val ourHeadRef = repo.head()
//            MyLog.d(TAG, "ourHeadRef == null:::"+(ourHeadRef==null))
//            val analysisResult = Merge.analysisForRef(repo, ourHeadRef, theirHeads)  //需要我们自己取出head传给它，这样会增加jni中c和java的通信，也更容易出bug，所以不要用这个方法，能直接全在c做的就全在c做，全在java做的就全在java做，非必要，不通信(jni)

            //分析我们的head和要合并的分支的状态，是否已经是最新，能否fast-forward之类的，Merge.analysis() 这个方法不需要你传headRef，它会自己查，然后内部也是调用 Merge.analysisRef()
            val analysisResult = Merge.analysis(repo, theirHeads)  //内部自己取head
//            MyLog.d(TAG, "analysisResult.analysis == null:::"+(analysisResult.analysis==null))
            //出现过空指针异常，原因不明，再出再查
            val analySet = analysisResult.analysisSet
            val preferenceSet = analysisResult.preferenceSet

            //最新，不需合并
            if (analySet.contains(Merge.AnalysisT.UP_TO_DATE)) {
                return Ret.createSuccess(null, "Already up-to-date", Ret.SuccessCode.upToDate)
            }

            //能fast-forward
            if (analySet.contains(Merge.AnalysisT.UNBORN)  // unborn是指HEAD还没创建，比如刚克隆的时候？不太清楚这种情况具体的发生场景
                //分析结果为可fast-forward，且配置文件里没设置不想fast-forward
                || (analySet.contains(Merge.AnalysisT.FASTFORWARD)
                        && !preferenceSet.contains(Merge.PreferenceT.NO_FASTFORWARD)
                        )
            ) {  // do fast-forward
                // 若能fast-forward，说明theirHeads列表只有一个分支
                if (theirHeads.size != 1) {
                    return Ret.createError(null, "theirHeads.size!=1 when do fast-forward", Ret.ErrCode.fastforwardTooManyHeads)
                }

                val targetId = theirHeads[0].id()
                return doFastForward(repo, targetId, analySet.contains(Merge.AnalysisT.UNBORN), caller = "rebase")
            }

            //合并
            if(analySet.contains(Merge.AnalysisT.NORMAL)){
                if(preferenceSet.contains(Merge.PreferenceT.FASTFORWARD_ONLY)) {
                    return Ret.createError(null, "config is fast-forward only, but need merge", Ret.ErrCode.mergeFailedByConfigIsFfOnlyButCantFfMustMerge)
                }

                val head = resolveHEAD(repo)
                if(head == null) {
                    return Ret.createError(null, "HEAD is null")
                }
                val headDetachedBeforeRebase = repo.headDetached()
                if(headDetachedBeforeRebase && head.id()==null) {
                    return Ret.createError(null, "HEAD's oid is null")
                }

                //如果detachedHead且head.id为null会在上面返回，所以如果执行到这里，要么head非detach要么detach但head.id不为null
                val srcAnnotatedCommit = if(headDetachedBeforeRebase) AnnotatedCommit.lookup(repo, head.id()!!) else AnnotatedCommit.fromRef(repo, head)

                //记录分支名 （不需要了，直接用 fromRef创建AnnotatedCommit将可使用 rebase.origHeadName 和 rebase.onto
//                RebaseHelper.saveRepoCurBranchNameOrDetached(repo)

                val rebaseOptions: Rebase.Options = Rebase.Options.createDefault()
                initRebaseOptions(rebaseOptions)


                //start rebase
                val onto = null // use upstream，2nd param to Rebase.init
                val rebase = Rebase.init(repo, srcAnnotatedCommit, theirHeads[0], onto, rebaseOptions)
                val allOpCount = rebase.operationEntrycount()
//                var didOpCount = 0
                val rebaseCommiter = Signature.create(username, email)
                val originCommitAuthor:Signature? = null // null to use originCommit author
                val originCommitMsgEncoding:Charset? = null // null to use origin commit msg encoding
                val originCommitMsg:String? = null // null to use origin commit msg

                for(i in 0 ..< allOpCount) {
                    rebase.next()
                    if(!repo.index().hasConflicts()) {  //无冲突，提交，这里不检查index是否为空，强制创建提交 // 需要测试能否创建空提交？应该能
                        rebase.commit(originCommitAuthor, rebaseCommiter, originCommitMsgEncoding, originCommitMsg)
                    }else {  //有冲突，返回，之后可 continue或abort，不过不支持 skip
                        return Ret.createError(null, "rebase:has conflicts", Ret.ErrCode.mergeFailedByAfterMergeHasConfilts)  //和merge用同一个冲突错误代码
                    }
                }

                //全部rebase完成
                //注：rebase.origHeadName 如果通过 AnnotatedCommit.lookup 创建，此值为null，否则为分支名。但是rebase.ontoName则无论通过AC.lookup创建还是fromRef创建都不会返回null，前者返回提交号，后者返回分支名
//                val rebaseOriginName = rebase.origHeadName()  //用户恢复之前的分支

                //全部执行完了
                rebase.finish(rebaseCommiter)

                val headId = repo.head()?.id() ?: return Ret.createError(null, "rebase:get new oid err after finish rebase")

                //只要在非detachedHead时通过fromRef创建AnnotatedCommit就不需要手动恢复分支，finish会自动恢复
                //如果执行rebase之前非detached Head，恢复一下之前的分支
//                if(!headDetachedBeforeRebase) {
//                    if(rebaseOriginName==null) {  //用非fromRef 方式创建的AnnotatedCommit就会为null，这时就没法恢复之前的分支了，除非你操作前记过
//                        return Ret.createError(null, "rebase:origin name is null, can't restore branch to before rebase")
//                    }
//
//                    //切换到之前分支
//                    checkoutLocalBranchThenUpdateHead(repo, rebaseOriginName, force=false, updateHead=true)
//
//                    //重置之前的分支Head指向rebase最新创建的提交
//                    resetHardToRevspec(repo, headId.toString())
//                }

                return Ret.createSuccess(headId)

            }

            MyLog.e(TAG, "#$funName:rebase failed, unknown analysis set of repo: repo state before rebase=`$state` analysis set=`$analySet`, preference set=`$preferenceSet`, codepos=12614088")
            //分析完不知道什么状态，返回错误，最后的codepose是用来在代码中定位此错误出现的地方的
            return Ret.createError(null, "rebase err: unknown analysis set: $analySet")
        }

        /**
         * 之所以不直接在这个函数里创建并返回RebaseOptions对象，是因为我不确定在这个函数里创建然后返回后对象是否会被释放
         */
        private fun initRebaseOptions(rebaseOptions: Rebase.Options) {
            val mergeOpts = rebaseOptions.mergeOptions
            mergeOpts.flags = 0  // 0代表一个flag都不设。另外：flag可设多个，重命名检测和有冲突不合并立即退出等，参见: https://libgit2.org/libgit2/#HEAD/type/git_merge_flag_t
            mergeOpts.fileFlags = Merge.FileFlagT.STYLE_DIFF3.bit //GIT_MERGE_FILE_STYLE_DIFF3, 1<<1

            val checkoutOpts = rebaseOptions.checkoutOptions
            //强制target(upstream)覆盖本地文件，即使文件修改没提交
            //                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.FORCE, Checkout.StrategyT.ALLOW_CONFLICTS)
            //不会覆盖本地未提交的文件（推荐）
            checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.ALLOW_CONFLICTS)

        }

        //即使targetRefName传长引用名在冲突文件里显示的依然是短引用名，所以，若想提高dwim找到引用的机率，建议这个值传长引用名（例如 refs/heads/abc），不过，其实合并冲突的文件里显示的是长引用名还是短引用名无所谓，都比hash好，所以就算不为了在冲突文件里显示得更友好，也建议这个传长引用名。
        fun mergeOrRebase(repo: Repository, targetRefName: String, username: String, email: String, requireMergeByRevspec:Boolean=false, revspec: String="", trueMergeFalseRebase:Boolean=true):Ret<Oid?> {
            val funName = "mergeOrRebase"

            //创建父提交列表，然后执行分析
            MyLog.d(TAG, "#mergeOneHead(): targetRefName=$targetRefName, revspec=$revspec, requireMergeByRevspec=$requireMergeByRevspec")

            if(username.isBlank() || email.isBlank()) {
                MyLog.w(TAG, "#$funName(): can't start merge, because `username` or `email` is blank!, username.isBlank()=${username.isBlank()}, email.isBlank()=${email.isBlank()}")
                return Ret.createError(null, "username or email is blank!",  Ret.ErrCode.usernameOrEmailIsBlank)
            }

            //checkoutopts.strategy为safe此检查应该是可选的，若index有东西，应该会中止操作(merge/rebase/cherrypick)
            //检查下index是否为空，不为空禁止merge。（另外：checkout默认设为Safe则不用检查worktree，如果worktree changes不为空且冲突，merge会中止）
//            if(!indexIsEmpty(repo)) {
//                return Ret.createError(null, "index has uncommitted changes! ${if(trueMergeFalseRebase) "merge" else "rebase"} abort")
//            }

            val targetAnnotatedCommit = if(requireMergeByRevspec) {
                AnnotatedCommit.fromRevspec(repo, revspec) //revspec一般是commit hash

            }else{
                val targetRef = resolveRefByName(repo, targetRefName)  //targetRefName一般是分支名
                MyLog.d(TAG, "#$funName(): targetRef==null: "+(targetRef==null)+", targetRefName: "+targetRefName)
                if(targetRef==null) {
                    return Ret.createError(null, "resolve targetRefName to Reference failed!",Ret.ErrCode.resolveReferenceError)
                }
                AnnotatedCommit.fromRef(repo, targetRef)  //targetRef一般是分支名查询出的引用对象
            }

            val parents = mutableListOf<AnnotatedCommit>()
            parents.add(targetAnnotatedCommit)

            return if(trueMergeFalseRebase) {
                //调用这个方法执行merge
                mergeManyHeads(repo, parents, username, email)
            }else{
                startRebase(repo, parents, username, email)
            }

        }

        //返回值是新创建的commit的oid
        // 类似 git merge branch1 branch2，可以合并多个分支，不过不知道和octpus那个合并(八爪合并，章鱼合并)有什么区别
        private fun mergeManyHeads(
            repo: Repository,
            theirHeads: List<AnnotatedCommit>,
            username: String,
            email: String
        ):Ret<Oid?> {
            //分析仓库状态
            val state = repo.state()
            if(state == null ||(state != Repository.StateT.NONE)) {
                //如果 "state是merge 且 一个mergeHead都没有 且 不存在冲突 且 index为空" 且没有merge head，则清下状态，app有时候崩溃就会残留在这个状态
                if(state == Repository.StateT.MERGE && getMergeHeads(repo).isEmpty() && !hasConflictItemInRepo(repo) && getIndexStatusList(repo).entryCount()==0) {
                    MyLog.w(TAG, "#mergeManyHeads(): repo state is \"MERGE\" , but no conflict items and Index is empty! maybe is wrong state, will clean repo state, old state is: \""+state.name+"\", new state expect to be \"NONE\"")
                    cleanRepoState(repo)

                }else {
                    MyLog.w(TAG, "#mergeManyHeads(): merge failed, repo state is: \""+state?.name+"\", expect \"NONE\"")
                    return Ret.createError(null, "merge failed! repo state is: \""+state?.name+"\", expect \"NONE\"", Ret.ErrCode.mergeFailedByRepoStateIsNotNone)
                }
            }
//            val ourHeadRef = repo.head()
//            MyLog.d(TAG, "ourHeadRef == null:::"+(ourHeadRef==null))
//            val analysisResult = Merge.analysisForRef(repo, ourHeadRef, theirHeads)  //需要我们自己取出head传给它，这样会增加jni中c和java的通信，也更容易出bug，所以不要用这个方法，能直接全在c做的就全在c做，全在java做的就全在java做，非必要，不通信(jni)

            //分析我们的head和要合并的分支的状态，是否已经是最新，能否fast-forward之类的，Merge.analysis() 这个方法不需要你传headRef，它会自己查，然后内部也是调用 Merge.analysisRef()
            val analysisResult = Merge.analysis(repo, theirHeads)  //内部自己取head
//            MyLog.d(TAG, "analysisResult.analysis == null:::"+(analysisResult.analysis==null))
            //出现过空指针异常，原因不明，再出再查
            val analySet = analysisResult.analysisSet
            val preferenceSet = analysisResult.preferenceSet

            //最新，不需合并
            if (analySet.contains(Merge.AnalysisT.UP_TO_DATE)) {
                return Ret.createSuccess(null, "Already up-to-date", Ret.SuccessCode.upToDate)
            }

            //能fast-forward
            if (analySet.contains(Merge.AnalysisT.UNBORN)  // unborn是指HEAD还没创建，比如刚克隆的时候？不太清楚这种情况具体的发生场景
                //分析结果为可fast-forward，且配置文件里没设置不想fast-forward
                || (analySet.contains(Merge.AnalysisT.FASTFORWARD)
                        && !preferenceSet.contains(Merge.PreferenceT.NO_FASTFORWARD)
                        )
            ) {  // do fast-forward
                // 若能fast-forward，说明theirHeads列表只有一个分支
                if (theirHeads.size != 1) {
                    return Ret.createError(null, "theirHeads.size!=1 when do fast-forward", Ret.ErrCode.fastforwardTooManyHeads)
                }

                val targetId = theirHeads[0].id()
                return doFastForward(repo, targetId, analySet.contains(Merge.AnalysisT.UNBORN), caller = "merge")
            }

            //合并
            if(analySet.contains(Merge.AnalysisT.NORMAL)){
                val mergeOpts = Merge.Options.create()
                mergeOpts.flags = 0  // 0代表一个flag都不设。另外：flag可设多个，重命名检测和有冲突不合并立即退出等，参见: https://libgit2.org/libgit2/#HEAD/type/git_merge_flag_t
                mergeOpts.fileFlags = Merge.FileFlagT.STYLE_DIFF3.bit //GIT_MERGE_FILE_STYLE_DIFF3, 1<<1

                val checkoutOpts = Checkout.Options.defaultOptions()
                //强制target(upstream)覆盖本地文件，即使文件修改没提交
//                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.FORCE, Checkout.StrategyT.ALLOW_CONFLICTS)
                //不会覆盖本地未提交的文件（推荐）
                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.ALLOW_CONFLICTS)

                if(preferenceSet.contains(Merge.PreferenceT.FASTFORWARD_ONLY)) {
                    return Ret.createError(null, "config is fast-forward only, but need merge", Ret.ErrCode.mergeFailedByConfigIsFfOnlyButCantFfMustMerge)
                }

                //do merge
                Merge.merge(repo,theirHeads, mergeOpts, checkoutOpts)
            }

            /* If we get here, we actually performed the merge above */
            //如果有冲突，提示解决冲突，否则创建提交
            if(repo.index().hasConflicts()) {
                //调用者可在返回error后检查repo.index.hasConflicts()来确认是否因为有冲突所以合并失败，不过一般不用检查，返回error直接显示提示并终止后续操作就行了
                return Ret.createError(null, "merge failed:has conflicts", Ret.ErrCode.mergeFailedByAfterMergeHasConfilts)  //merge完了，存在冲突
            }else {  //创建提交
                // merge成功后创建的提交应该有两个父提交： HEAD 和 targetBranch.
                val parent = mutableListOf<Commit>()
                val headRef = resolveRefByName(repo, "HEAD")
                if(headRef==null) {
                    return Ret.createError(null, "resolve HEAD error!", Ret.ErrCode.headIsNull)
                }
                val headCommit = headRef?.id()?.let { Commit.lookup(repo, it) }
                //第一个父提交必须得是直系的HEAD
                //添加直接父commit:HEAD
                if (headCommit != null) {
                    parent.add(headCommit)
                }else {
                    return Ret.createError(null, "get current HEAD latest commit failed", Ret.ErrCode.mergeFailedByGetRepoHeadCommitFaild)
                }
                //添加其他父提交
                val sb = StringBuilder(headRef.shorthand()+" merged with:")
                val suffix = ", "
                for(ac in theirHeads) {
                    val c = Commit.lookup(repo, ac.id())
                    //生成merge commit msg，首先尝试取出ac.ref()中分支名部分。
//                    MyLog.d(TAG, "#mergeManyHeads() , ac.ref():"+Reference.dwim(repo, ac.ref())?.shorthand())  //能取出本地(例如main)或远程分支名(例如origin/main)，但这个还得jni，没必要

                    //首先尝试从 refs/heads/本地分支名 这样的refspec查找本地分支名，如果找不到，尝试从 refs/remotes/远程仓库名/远程分支名 这样的refspec查找远程分支名
                    //ref是有可能为null的，如果这个AnnotatedCommit没被任何分支指向的话
                    val ref = ac.ref()?:""  // ac.ref()可能的值：refs/heads/本地分支名(见过) or refs/remotes/远程仓库名/远程分支名(见过) 。不会是commit hash，若用commit hash创建AnnotatedCommit，ref()方法会返回null，亲测，即使有分支指向的那种commit也一样，只要创建AnnotateCommit时没传引用名(分支名)，ac.ref()一律返回null，所以我在这里加了 ?:"" 用来使其和commit关联时返回空字符串，后面调用的方法处理了空字符串的情况，确保后面的一系列操作不会出错
                    var branchNameOrRefShortHash = getShortRefSpecByRefsHeadsRefSpec(ref)  //ac.ref()返回的是完整refspec，例如 refs/heads/main，取出来的应该是短refspec，例如 main
                    if(branchNameOrRefShortHash == null || branchNameOrRefShortHash.isBlank()) {  //没找到本地分支名，可能是远程分支
                        branchNameOrRefShortHash = getShortRefSpecByRefsRemotesRefSpec(ref)  //尝试取出远程分支名
                        if(branchNameOrRefShortHash == null || branchNameOrRefShortHash.isBlank()) {  //本地分支名和远程分支名都没找到，用 短commit_hash 代替
                            branchNameOrRefShortHash = c.shortId().toString()  //默认是commit hash的前7个字符
                        }
                    }
                    sb.append(branchNameOrRefShortHash+suffix)  //拼接字符串，形如："分支名, "
                    //添加提交节点到父节点列表
                    parent.add(c)
                }

                val msg = sb.removeSuffix(suffix).toString()  //移除最后一个 ", "
                val branchFullRefName: String = headRef.name()

                //创建提交
//                val commitResult = doCreateCommit(repo, msg, username, email, branchFullRefName, parent)
                val commitResult = createCommit(repo, msg, username, email, branchFullRefName, indexItemList = null, parent)
                if(commitResult.hasError()) {
                    return Ret.createError(null, "merge failed:"+commitResult.msg, Ret.ErrCode.mergeFailedByCreateCommitFaild)
                }

                //合并并且成功创建了提交，返回成功
                return Ret.createSuccess(commitResult.data, "merge success, new commit oid:"+commitResult.data.toString())
            }

        }

        private fun doFastForward(repo:Repository, targetOid: Oid, isUnborn:Boolean, caller:String):Ret<Oid?> {
            val targetRef = if(isUnborn) { // HEAD non-exist，需要查找一下
                val headRef = Reference.lookup(repo, "HEAD")
                val symbolicRef = headRef?.symbolicTarget()?:throw RuntimeException("doFastForward() failed by headRef.symbolicTarget() return null")
                Reference.create(repo, symbolicRef, targetOid, false, "born HEAD when fast-forward")
            } else {
                repo.head()
            }

            if(targetRef == null) {
                return Ret.createError(null,  "targetRef is null", Ret.ErrCode.targetRefNotFound)
            }

            val targetCommit = resolveGitObject(repo, targetOid, GitObject.Type.COMMIT)
            if(targetCommit == null) {
                return Ret.createError(null, "targetCommit is null", Ret.ErrCode.targetCommitNotFound)
            }
            val fastForwardCheckoutOpts = Checkout.Options.defaultOptions()
            fastForwardCheckoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE)

            Checkout.tree(repo, targetCommit, fastForwardCheckoutOpts)

            val newTargetRef = targetRef.setTarget(targetOid, "$caller: ${targetRef.name()} fast-forward to $targetOid")

            //这判断好像没什么意义，其实如果没抛异常，这里直接返回true就行
            return if(newTargetRef != null) {
                Ret.createSuccess(newTargetRef.id(), "success")
            }else {
                Ret.createError(null, "newTargetRef is null", Ret.ErrCode.newTargetRefIsNull)
            }
        }

        suspend fun updateDbAfterMergeSuccess(mergeResult:Ret<Oid?>, appContext:Context, repoId:String, msgNotifier:(String)->Unit, trueMergeFalseRebase:Boolean) {
            if(mergeResult.code == Ret.SuccessCode.upToDate) {  //合并成功，但什么都没改，因为into的那个分支已经领先或者和mergeTarget拥有相同的最新commit了(换句话说：接收合并的那个分支要么比请求合并的分支新，要么和它一样)
                // up to date 时 hash没变，所以不用更新db，只显示下提示即可
                msgNotifier(appContext.getString(R.string.already_up_to_date))
            }else {  //合并成功且创建了新提交
                //合并完了，创建了新提交，需要更新db
                val repoDB = AppModel.singleInstanceHolder.dbContainer.repoRepository
                val shortNewCommitHash = mergeResult.data.toString().substring(Cons.gitShortCommitHashRange)
                //更新db
                repoDB.updateCommitHash(
                    repoId=repoId,
                    lastCommitHash = shortNewCommitHash,
                )

                //显示成功通知
                msgNotifier(appContext.getString(if(trueMergeFalseRebase) R.string.merge_success else R.string.rebase_success))

            }
        }

        //例如：输入：refs/heads/main，返回：main
        fun getShortRefSpecByRefsHeadsRefSpec(refspec:String):String? {
            return removeGitRefSpecPrefix("refs/heads/", refspec)
        }

        //例如：输入：origin/main，返回：main
        fun removeRemoteBranchShortRefSpecPrefixByRemoteName(remoteName: String, refspec:String):String? {
            return removeGitRefSpecPrefix(remoteName, refspec)
        }

        //输入类似：origin/abc 返回：abc
        //警告这个方法只适用于远程名不包含分隔符的情况！
        @Deprecated("只适用于远程名不包含分隔符的情况，建议改用 `removeRemoteBranchShortRefSpecPrefixByRemoteName()`")
        fun removeRemoteBranchShortRefSpecPrefixBySeparator(refspec:String):String? {
            return removeGitRefSpecPrefix("/",refspec)  //我想了下，这个函数就算用 / 作为前缀，也能正常工作，例如传 origin/abc，返回 abc，但如果传 refs/heads/abc ，则会返回 heads/abc
        }

        //输入：refs/remotes/origin/abc 返回 origin/abc
        fun getShortRefSpecByRefsRemotesRefSpec(refspec:String):String? {
            return removeGitRefSpecPrefix("refs/remotes/", refspec)
        }


        //例如：移除refspec前缀，例如 输入 refs/heads/ 和 refs/heads/main，会返回main；
        //特殊用法(不推荐这么用，例如用来移除远程前缀，但remote包含分隔符，就会返回错误的结果)：可输入/来移除refspec的第一个/和其左边的内容，主要应用场景是移除远程分支名的remote前缀，例如：输入 / 和 origin/main，返回main。
        fun removeGitRefSpecPrefix(prefix: String, refspec:String):String? {
            if(refspec.isBlank()) {
                return null
            }

            val indexOf = refspec.indexOf(prefix)
            if(indexOf == -1) {  //找不到就返回null
                return null
            }
            //返回 `prefix` 后面的部分
            return refspec.substring(indexOf+prefix.length)
        }

        /**
         * 注意：pushRefSpec不要带加号+，若想强制推送，传force=true。不过这里没强制要求，若你非带加号同时force传false，也能强制push
         */
        fun push(repo: Repository, remoteName: String, pushRefSpec: String, credential: CredentialEntity?, force: Boolean=false):Ret<String?> {
            //head detached没有上游，不能push
            if(repo.headDetached()) {
                return Ret.createError(null, "push failed: head detached!", Ret.ErrCode.headDetached)
            }

            //+的作用是force push，如果带+，当本地和远程不一致时（无法fast-forward)，会强制用本地覆盖远程提交列表，但一般远程仓库主分支都有覆盖保护，所以可能会push失败
            val pushRefSpec = if(force && !pushRefSpec.startsWith("+")) "+$pushRefSpec" else pushRefSpec

            val pushOptions = PushOptions.createDefault()

            //如果凭据不为空，设置一下
            if(credential!=null) {
                setCredentialCbForRemoteCallbacks(pushOptions.callbacks!!, credential.type, credential.value, credential.pass)
            }

            //找remote
            val remote = resolveRemote(repo, remoteName)
            if(remote==null) {
                return Ret.createError(null, "resolve remote failed!", Ret.ErrCode.resolveRemoteFailed)
            }
            //要push的refspec (要push哪些分支)
            // push RefSpec 形如：refs/heads/main:refs/heads/main
            val refspecList = mutableListOf(pushRefSpec)
//            val refspecList = mutableListOf("refs/heads/bb6:refs/heads/bb6up")
//            val refspecList = mutableListOf<String>()  //传空列表，将使用配置文件中的设置，但实际上有点问题，测试了下没把我想推送的分支推上去，所以还是不要传空列表了

            MyLog.d(TAG, "#push(): remoteName=$remoteName, refspecList=$refspecList")

            //推送
            remote.push(refspecList, pushOptions)

            return Ret.createSuccess(null, "push success")
        }


        /**
         * 针对每个remotes推送一遍refspecs
         * 注意：这个函数没有force参数！如果要force push，需要调用前自己在refspecs的对应元素里加加号
         */
        fun pushMulti(repo: Repository, remotes: List<RemoteAndCredentials>, refspecs: List<String>, returnIfAnyRemotePushFailed:Boolean=true):Ret<String?> {
            val funName = "pushMulti"

            for (rc in remotes) {
                val credential = rc.pushCredential
                val pushOptions = PushOptions.createDefault()

                //如果凭据不为空，设置一下
                if(credential!=null) {
                    setCredentialCbForRemoteCallbacks(pushOptions.callbacks!!, credential.type, credential.value, credential.pass)
                }

                //找remote
                val remote = resolveRemote(repo, rc.remoteName)
                if(remote==null) {
                    val errMsg = "resolve remote failed! remoteName=${rc.remoteName}"

                    if(returnIfAnyRemotePushFailed) {
                        return Ret.createError(null, errMsg, Ret.ErrCode.resolveRemoteFailed)
                    }else {
                        MyLog.e(TAG, "#$funName: $errMsg")
                        continue
                    }
                }
                //要push的refspec (要push哪些分支)
                // push RefSpec 形如：refs/heads/main:refs/heads/main
//                val refspecList = mutableListOf(pushRefSpec)
//            val refspecList = mutableListOf("refs/heads/bb6:refs/heads/bb6up")
//            val refspecList = mutableListOf<String>()  //传空列表，将使用配置文件中的设置，但实际上有点问题，测试了下没把我想推送的分支推上去，所以还是不要传空列表了

                MyLog.d(TAG, "#$funName: will push: remoteName=${rc.remoteName}, refspecs=$refspecs")

                //推送
                remote.push(refspecs, pushOptions)

            }

            return Ret.createSuccess(null, "push success")
        }

        //remote,例如：origin；branchFullRefSpec，例如：refs/heads/master
        //返回结果，例如：origin/master
        fun getUpstreamRemoteBranchShortNameByRemoteAndBranchRefsHeadsRefSpec(remote:String, branchFullRefSpec:String) :String {
            if(remote.isBlank() || branchFullRefSpec.isBlank()) {
                return ""
            }
            return remote + "/" + getShortRefSpecByRefsHeadsRefSpec(branchFullRefSpec)
        }

        //unstage并不是从index删除，而是reset index中的数据到head的状态，
        // 参见：https://github.com/libgit2/libgit2/issues/3632
        fun unStageItems(repo: Repository, pathSpecList: List<String>) {
            val head = repo.head()
            val headCommit = head?.peel(GitObject.Type.COMMIT)
            headCommit?.let { Reset.resetDefault(repo, it, pathSpecList.toTypedArray()) }
        }

        //(?好像和这个无关)如果手机用了vpn，不设这个不能用，在中国，因为有傻逼gfw，所以这个必须设置
        //我感觉应该不用设置这个，之所以提示followRedirect之类的错误可能是gfw把github.com重定向了，不跟是对的，或者不配置
//        fun setFollowRedirectsForFetchOpts(fetchOptions: FetchOptions) {
//            fetchOptions.followRedirects = Remote.RedirectT.NONE  // or ALL
//        }

        fun getBranchList(repo:Repository, branchType:Branch.BranchType=defaultBranchTypeForList, excludeRemoteHead:Boolean=false):List<BranchNameAndTypeDto> {
            val isDetached = repo.headDetached()
            var head:Reference?=null
            if(!isDetached) {
               head = repo.head()
            }
            val it = Branch.Iterator.create(repo, branchType)
            val list = mutableListOf<BranchNameAndTypeDto>()

            //默认是local在前，remote在后，如果发现乱序，处理一下，弄成local在前，remote在后
            var b = it.next()
            while(b!=null) {

                //后来发现问题不出在这里
//                //需要检查一下，不然refs/tags会乱入
//                if(!(b.key.isBranch || b.key.isRemote)) {  // if not refs/heads/xxx or refs/remotes/xxx, continue
//                    // throw RuntimeException("shou not be here 3333344444")  //test ，测试了下，问题不出在这里，没抛异常
//                    continue
//                }

                //把结尾是/HEAD的远程分支忽略掉（本地HEAD不会出现在列表，所以不用处理），
                // 没必要列出来，其实就指向远程分支的主分支（常见的是main or master）而已，
                // 而主分支本身就已经在列表中了，所以再列HEAD没意义
                if(excludeRemoteHead) {  //如果设置了不包含remote head，则排除，否则不排除。在分支列表一般排除remote head，但在提交列表一般不排除。，不过其实在分支列表似乎也没必要排除HEAD啊？要不列出来？
                    if(b.value == Branch.BranchType.REMOTE
                        && b.key.name().endsWith("/HEAD")  //用末尾 /HEAD 的方式有可能判断误判，因为分支名也有可能叫这个，而remote名有可能歧义，所以，不能用名称来判断
                        && b.key.id()==null  //我发现远程head id 是null，暂时用这种方式排除了，之所以是null，是因为head是指向指针的指针：b.key.symbolicTarget() 即可取出其指向的真实引用，一般是远程main分支，例如 origin/main，若想取出origin/main的id，可b.key.peel(GitObject.Type.COMMIT).id()
                        && b.key.symbolicTarget()!=null
                    ) {
                        b = it.next()  //记得更新下迭代器，不然就死循环了
                        continue
                    }
                }



                //创建对象存上分支信息
                val bnat = BranchNameAndTypeDto()
                bnat.fullName = b.key.name()
                bnat.shortName = b.key.shorthand()
                bnat.type = b.value

                //判断是否是指向指针的指针，例如 origin/HEAD 实际指向 origin/main
                bnat.isSymbolic = b.key.symbolicTarget()!=null
                if(bnat.isSymbolic) {  //若是符号引用取出其名
                    bnat.symbolicTargetFullName = b.key.symbolicTarget() ?: ""
                    bnat.symbolicTargetShortName = getBranchShortNameByFull(bnat.symbolicTargetFullName)
                }

                try {  //解析oid，若是符号引用会解析出实际指向的对象指向的commit的oid，一般符号引用顶多两层即可抵达commit，例如 HEAD->main->commit
//                        bnat.oidStr  = b.key.peel(GitObject.Type.COMMIT).id().toString()  //20240427:不要用resolve！经过测试，我发现有时候peel能解析出对象，但resolve会返回null。20240825：更新，这个问题已解决，是git24j里用>0当作有效指针判断导致的，实际c指针在java里可能为负数，因为java没unsigned类型，但还是建议用peel解析commit，经我测试，peel比resolve更强大，能解析annotated tags为commit，但resolve不能) or b.key.resolve().id().toString()，
                    bnat.oidStr  = b.key.peel(GitObject.Type.COMMIT)?.id()?.toString()?:throw RuntimeException("resolve branch to direct ref(commit) failed, branch is: ${bnat.fullName}") // or b.key.resolve().id().toString()
                }catch (e:Exception) {
                    MyLog.e(TAG, "#getBranchList() err: ${e.stackTraceToString()}")
                    b=it.next()  //更新迭代器，不然continue就死循环了
                    continue;  //忽略这个分支
                    //狗屁，符号类型id会是null，返回这个没意义）备用方案
//                        bnat.oidStr = b.key.id().toString()
                }


//                if(debugModeOn) {
//                    if(b.key.name().endsWith("origin/HEAD")) {
//                        MyLog.d(TAG, "#getBranchList: origin/HEAD, id=${bnat.oidStr}")
//                        MyLog.d(TAG, "#getBranchList: origin/HEAD, point to=${b.key.symbolicTarget()}")
//                    }
//                }

                bnat.shortOidStr = getShortOidStrByFull(bnat.oidStr)

                //如果是本地分支，查询下是否有其关联的远程分支
                if(b.value == Branch.BranchType.LOCAL) {
                    val upstream = getUpstreamOfBranch(repo, bnat.shortName)
                    bnat.upstream = upstream
                    if(upstream.remoteOid.isNotBlank() && upstream.localOid.isNotBlank()) {
                        // test code block start
//                        if(upstream.localOid != bnat.oidStr) {  //如果这俩值不相等，说明解析分支的引用解析错了，测试时可启用此代码，正常来说，永远不会进入此代码块
//                            throw RuntimeException("upstream.localOid != bnat.oidStr is true, something wents wrong!")  //test
//                        }
                        // test code block end

                        val (ahead, behind) = getAheadBehind(repo, Oid.of(upstream.localOid), Oid.of(upstream.remoteOid))
                        bnat.ahead = ahead
                        bnat.behind = behind
                    }
                }else if(b.value == Branch.BranchType.REMOTE) {  //如果是远程分支，查询下它的远程名
                    //这个值有可能是空，代表远程分支的remote名存在歧义（例如和本地分支名冲突，只有彻底不允许remote包含/或者设置一个明确的remote和分支名分隔符才能解决这个问题）
                    bnat.remotePrefixFromShortName = resolveBranchRemotePrefix(repo, bnat.fullName)
                }

                //如果不是detached并且有head（一般都有），查询下正在遍历的这个分支是否是仓库当前分支
                //正常来说只会有一个分支得到这个真值，其他都是假
                bnat.isCurrent = !isDetached && head!=null && head.name() == bnat.fullName

                //添加到集合
                list.add(bnat)

                //更新迭代器
                b = it.next()
            }

            return list
        }

        /**
         * @param fullName refs/remotes/xxx or refs/heads/xxx
         *
         * @return branch name without prefix refs/remotes/ or refs/heads/
         */
        fun getBranchShortNameByFull(fullName: String): String {
            if(fullName.isBlank()) {
                return ""
            }

            val refsHeadsPrefix = "refs/heads/"
            val refHeadsIdx = fullName.indexOf(refsHeadsPrefix)
            if(refHeadsIdx != -1) {  // local
                return fullName.substring(refHeadsIdx+refsHeadsPrefix.length)
            }else {  // remote, eg: refs/remotes/origin/abc
                val refsRemotesPrefix = "refs/remotes/"
                val refRemoteIdx = fullName.indexOf(refsRemotesPrefix)
                if(refRemoteIdx==-1) {  //不是本地分支也不是远程分支，返回空字符串
                    return ""
                }

                //是远程分支
                return fullName.substring(refRemoteIdx+refsRemotesPrefix.length)
            }
        }

        fun getRepoNameOnBranch(repoName:String, branchName: String):String {
            return "$repoName on $branchName"
        }

        fun getBranchNameOfRepoName(repoName:String, branchName: String):String {
            return "$branchName of $repoName"
        }

        fun getShortOidStrByFull(oidStr:String):String{
            if(oidStr.length > Cons.gitShortCommitHashRangeEndInclusive) {
                return oidStr.substring(Cons.gitShortCommitHashRange)
            }
            return oidStr
        }
        //参数值类似：origin/abc/def，返回值，1：origin，2 abc/def
        fun splitRemoteAndBranchFromRemoteShortRefName(shortRefName:String):Pair<String,String> {
            val indexOf = shortRefName.indexOf("/")
            val remote = shortRefName.substring(0, indexOf)
            val branch = shortRefName.substring(indexOf+1, shortRefName.length)
            return Pair(remote, branch)
        }

        fun isBranchNameAlreadyExists(repo:Repository ,branchName:String):Boolean {  //主要用来在创建分支时如果分支已存在，则提示用户改名
            return resolveRefByName(repo, branchName) != null
        }

        fun createLocalBranchBasedHead(repo: Repository, branchName:String, overwriteIfExisted: Boolean):Ret<Triple<String, String, String>?> {
            val head = repo.head()
            if(head==null) {
                return Ret.createError(null, "HEAD is null",Ret.ErrCode.headIsNull)
            }

            val currentHeadLatestCommitHash = head.id().toString()

            return createLocalBranchBasedOidStr(repo, branchName, currentHeadLatestCommitHash, overwriteIfExisted)
        }

        //创建成功返回: 分支长名，分支短名，完整hash 。 例如：refs/heads/main , main , abc11111111111...省略
        fun createLocalBranchBasedOidStr(repo: Repository, branchName:String, oidStr:String, overwriteIfExisted: Boolean):Ret<Triple<String, String, String>?> {
            //这个本意是判断hash是否有效，但出现了意料之外的bug：如果使用Oid.of(短hash)，就会报错，那样就会触发即使短hash有效，也无法checkout的bug
//            val oid = Oid.of(oidStr)
//            if(oid.isNullOrEmptyOrZero) {
//                return Ret.error(null, "invalid Oid!", Ret.ErrCode.invalidOid)
//            }

            val commit = resolveCommitByHash(repo, oidStr)
            if(commit == null) {
                return Ret.createError(null, "resolve commit error!",Ret.ErrCode.resolveCommitErr)
            }

//            println("branchName:$branchName")  //debug
//            println("overwriteIfExisted:$overwriteIfExisted")  //debug

            //最后一个布尔值是强制创建与否，默认否，若传true，会覆盖本地同名分支
            val newBranch = Branch.create(
                repo,
                branchName,
                commit,
                overwriteIfExisted
            )

            //创建成功返回: 分支长名，分支短名，完整hash。
            return Ret.createSuccess(Triple(newBranch.name(), newBranch.shorthand(), commit.id().toString()))
        }

        //checkout分支并使HEAD指向分支
        fun checkoutLocalBranchThenUpdateHead(repo: Repository, branchName: String, force:Boolean=false, updateHead:Boolean=true):Ret<Oid?> {
            return checkoutBranchThenUpdateHead(repo, branchName,force,detachHead = false, updateHead=updateHead)
        }


        //checkout远程分支然后把HEAD变成detached head。(这种实现方式和pc git行为一致，不过libgit2的example里checkout remote分支不会使仓库变成detached head)
        fun checkoutRemoteBranchThenDetachHead(repo: Repository, branchName: String, force:Boolean=false, updateHead:Boolean=true):Ret<Oid?> {
            return checkoutBranchThenUpdateHead(repo, branchName,force,detachHead = true, updateHead=updateHead)
        }


        //checkoutCommit并使分支变成detached HEAD
        fun checkoutCommitThenDetachHead(repo: Repository, commitHash:String, force:Boolean=false, updateHead:Boolean=true):Ret<Oid?> {
            //checkout
            val checkRet = checkoutByHash(repo, commitHash, force)
            if(checkRet.hasError()) {
                return checkRet
            }

            //detach head
            if(updateHead) {
                //oid为null这种情况应该不会发生，空值检测只是以防万一
                val targetCommitOid = checkRet.data ?: return Ret.createError(null,"checkout success but detach head failed, because new commit id is invalid",  Ret.ErrCode.checkoutSuccessButDetacheHeadFailedByNewCommitInvalid)
                repo.setHeadDetached(targetCommitOid)
            }

            return checkRet
        }

        private fun checkoutBranchThenUpdateHead(repo: Repository, branchName: String, force:Boolean=false, detachHead:Boolean, updateHead:Boolean=true):Ret<Oid?> {
            //查找ref
            val ref = resolveRefByName(repo, branchName)
//            println("branchName:$branchName")  //debug
            if(ref==null) {
                return Ret.createError(null,"ref is null",Ret.ErrCode.refIsNull)
            }
            //checkout
            //需要peel一下，不然，复杂tag会解析出tagid而不是commit id，然后出错
            val checkoutRet = checkoutByHash(repo, ref.peel(GitObject.Type.COMMIT).id().toString(), force)
            if(checkoutRet.hasError()) {
                return checkoutRet
            }

            //如果请求更新head则更新，否则不更新，不更新就相当于只改变worktree的文件，但不改变head
            if(updateHead) {
                // checkout 成功了，更新HEAD
                if(detachHead) { //detach head，checkout 远程分支或commit 应该走这里的逻辑。（不过其实也可以强制为远程分支创建具体分支名，但与pc git行为不符，pc git checkout 远程分支直接就变detached head，后续可通过创建本地分支来解除detached状态）
                    val ac = AnnotatedCommit.fromRef(repo, ref)  //包含分支名之类的，创建出的reflog更详细，会包含引用名，而普通hash只包含hash。 ps: 我是因为看到libgit2的example checkout.c里用的这个函数，所以我也用的这个函数。
                    repo.setHeadDetachedFromAnnotated(ac)  //这个方法如果不灵，可替换成： repo.setHeadDetached(targetCommit.id())，targetCommit可通过checkoutRet.data取得
                }else {  //设置HEAD指向分支，checkout 本地分支应该执行这里
                    repo.setHead(ref.name())
                }
            }

            return checkoutRet
        }

        //只checkout不更新HEAD
        private fun checkoutByHash(repo: Repository, commitHash:String, force:Boolean=false):Ret<Oid?> {
            val state = repo.state()
            //先检查仓库状态
            if(state != Repository.StateT.NONE) {
                MyLog.d(TAG, "#checkoutByHash:"+"repo state is not NONE, it is:"+state.toString())
                return Ret.createError(null, "repo state is not NONE",Ret.ErrCode.repoStateIsNotNone)
            }

            //仓库状态正常，准备执行checkout
            val ckOpts = Checkout.Options.defaultOptions()
            //建变量是为了避免后面打印日志时再调用jni取ckOpts里的strategy
            val strategy = if (force) EnumSet.of(Checkout.StrategyT.FORCE) else EnumSet.of(Checkout.StrategyT.SAFE)
            ckOpts.strategy = strategy
            MyLog.w(TAG, "#checkoutByHash: will checkout commit '$commitHash' with strategy '$strategy'")

            val targetCommit = resolveCommitByHash(repo, commitHash)
            if(targetCommit==null) {
                MyLog.d(TAG, "#checkoutByHash:"+"target commit not found, hash is:"+commitHash)
                return Ret.createError(null,"target commit not found!",Ret.ErrCode.targetCommitNotFound)
            }

            val errno = Checkout.tree(repo, targetCommit, ckOpts)
            if(errno < 0) {
                MyLog.d(TAG, "#checkoutByHash:"+"Checkout.tree() err, errno="+errno)
                return Ret.createError(null, "Checkout Tree err(errno=$errno)!", Ret.ErrCode.checkoutTreeError)
            }

            //checkout 成功了，返回commit oid
            return Ret.createSuccess(targetCommit.id())
        }

        fun resolveHEAD(repo:Repository):Reference? {
            return resolveRefByName(repo, "HEAD")
        }

        fun resolveRefByName(repo:Repository, refNameShortOrFull:String, trueUseDwimFalseUseLookup:Boolean=true):Reference? {
            try {
                MyLog.d(TAG, "#resolveRefName(refNameShortOrFull=$refNameShortOrFull, trueUseDwimFalseUseLookup=$trueUseDwimFalseUseLookup)")

                //注：如果使用lookup必须全名查找，例如refs/heads/main 或 refs/remotes/origin/main，而且lookup不会解引用对象，如果ref是个符号引用(symbolic reference)，就会返回一个符号引用
                val ref = if(trueUseDwimFalseUseLookup) Reference.dwim(repo, refNameShortOrFull) else Reference.lookup(repo, refNameShortOrFull)
                return ref?.resolve()  //resolve reference to direct ref, direct ref is point to commit, no symbolicTarget
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveRefName(): resolve refname err! refname="+refNameShortOrFull+", trueUseDwimFalseUseLookup=$trueUseDwimFalseUseLookup, err is:"+e.stackTraceToString())
                return null
            }
        }

        fun resolveGitObject(repo:Repository, targetOid:Oid, type:GitObject.Type):GitObject? {
            try {
                val gitObj = GitObject.lookup(repo, targetOid, type)
                return gitObj
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveGitObject(): resolve GitObject err! targetOid=$targetOid, type=${type.name} \n Exception is:"+e.stackTraceToString())
                return null
            }
        }

        fun resolveCommitByHash(repo: Repository, shortOrLongHash:String):Commit? {
            try {
                //lookupPrefix既能找短hash，也能找完整hash
                val resolved =  Commit.lookupPrefix(repo, shortOrLongHash)
                return resolved
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveCommitByHash() error, param is (shortOrLongHash=$shortOrLongHash):\nerr is:"+e.stackTraceToString())
                return null
            }
        }

        fun resolveBranchRemotePrefix(repo: Repository, fullRemoteBranchRefSpec:String):String {
            try {
                //注：好像，有时候，符号引用解析出来会是空指针，但有时候就不会，原因不明，可能跟single branch有关，但我没验证过，参见，未解决的问题文档，标题：“Libgit2Helper#resolveBranchRemotePrefix解析出空指针问题 20240510”
                val remoteName = Branch.remoteName(repo, fullRemoteBranchRefSpec) ?: ""  //后面必须做空值判断：因为返回值为String而不是String?，所以，如果remoteName为null，会报异常，然后进入异常代码块，最后返回空字符串
                MyLog.d(TAG, "#resolveBranchRemotePrefix: in: fullRemoteBranchRefSpec=$fullRemoteBranchRefSpec; out: remoteName=$remoteName")
                return remoteName
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveBranchRemotePrefix() error, param is (fullRemoteBranchRefSpec=$fullRemoteBranchRefSpec) :\nerr is:"+e.stackTraceToString())
                return ""
            }
        }

        fun resolveRemote(repo: Repository, remoteName:String):Remote? {
            try {
                val remoteObj = Remote.lookup(repo, remoteName)
                return remoteObj
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveRemote() error, param is (remoteName=$remoteName):\nerr is:"+e.stackTraceToString())
                return null
            }
        }

        fun getRemoteFetchBranchList(remote:Remote): Pair<Boolean, List<String>> {
            val isNotAll = false
            try {
                val list = remote.fetchRefspecs
                if(list==null || list.isEmpty()) {
                    return Pair(isNotAll, emptyList())
                }
                val branchNameList = mutableListOf<String>()
                list.forEach {
                    val prefixStr = "refs/heads/"  //这里故意没写前面的 +，因为有的可能没+，例如："refs/heads/*:refs/remotes/origin/*"，不过我见过最多的还是带+的例如：“+refs/heads/*:refs/remotes/origin/*”
                    val prefixIndex = it.indexOf(prefixStr)
                    if(prefixIndex < 0){
                        return@forEach
                    }

                    val end = it.indexOf(":")
                    if(end<0) {
                        return@forEach
                    }

                    val start = prefixIndex + prefixStr.length
                    branchNameList.add(it.substring(start, end))
                }
                val isAllRealValue = branchNameList.size == 1 && branchNameList[0] == "*"
                return Pair(isAllRealValue, branchNameList)
            }catch (e:Exception) {
                MyLog.e(TAG, "#getRemoteFetchBranchList() error: "+e.stackTraceToString())
                return Pair(isNotAll, emptyList())
            }
        }

        fun getBranchListFromUserInputCsvStr(branchListCsv:String, branchListSeparator:String=Cons.stringListSeparator):List<String> {
            val list = mutableListOf<String>()
            branchListCsv.replace("\n", "").split(branchListSeparator).forEach {
                val s = it.trim()
                if(s.isNotBlank()) {
                    list.add(s)
                }
            }
            return list
        }

        //只支持传本地分支 "abc" 或 远程分支 "origin/main" 这两种格式，也就是 Reference.shorthand() 输出的名字，传完整名会解析不出来，返回空指针
        //注意：解析本地的远程分支，必须指定remote前缀，例如传origin/main是可以的，但如果传main，即使type是REMOTE，也会返回空指针
        //还有，不要传完整引用名例如：refs/remotes/开头的和refs/heads开头的，传这种一律会返回null，这个api就是这么傻逼
        fun resolveBranch(repo: Repository, branchShortName:String, type:Branch.BranchType):Reference? {
            try {
                val ref = Branch.lookup(repo, branchShortName, type)
                return ref
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveBranch() error, params are (branchShortName=$branchShortName, type=${type.name}),\nerr is:"+e.stackTraceToString())
                return null
            }
        }

        //revspec可以是长或短分支名或长或短hash
        //输入revspec，返回 Tree ，可以用来diff，或者从树上找某个问题之类的
        fun resolveTree(repo: Repository, revspec:String):Tree? {
            try {
                val tree = Tree.lookup(repo, Revparse.lookup(repo, "$revspec^{tree}").getFrom().id(), GitObject.Type.TREE) as? Tree
                return tree
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveTree() error, params are (revspec=$revspec),\nerr is:"+e.stackTraceToString())
                return null
            }

        }

        fun resolveHeadTree(repo: Repository):Tree? {
            return resolveTree(repo,"HEAD")
        }

        //删除本地的本地分支或本地的远程分支
        fun deleteBranch(repo: Repository, branchNameShortOrFull: String):Ret<String?> {
            val ref = resolveRefByName(repo, branchNameShortOrFull)
            if(ref==null) {
                return Ret.createError(null, "resolve branch name to reference failed!", Ret.ErrCode.resolveReferenceError)
            }

            try {
                Branch.delete(ref)
                return Ret.createSuccess(null, "delete branch success")
            }catch (e:Exception) {
                MyLog.e(TAG, "#deleteBranch(): delete branch error:\n"+e.stackTraceToString())
                return Ret.createError(null, "delete branch error"+e.localizedMessage, Ret.ErrCode.deleteBranchErr)
            }
        }

        //如果解析remote前缀不存在歧义，可调用这个方法，否则需让用户输入具体remote名，然后调用deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec()
        //参数remoteBranchFullRefSpec，预期，类似：refs/remotes/origin/branchname，不要带冒号，本函数执行push的时候会自己加，ps “:远程分支名” 的意思是本地没你的下游了，自我了断吧，所以作用就是删除冒号后面的远程分支名
        fun deleteRemoteBranch(repo: Repository, remoteBranchFullRefSpec: String, credential: CredentialEntity?):Ret<String?> {
            val remote = resolveBranchRemotePrefix(repo, remoteBranchFullRefSpec)
            if(remote.isNullOrBlank()) {
                MyLog.d(TAG, "#deleteRemoteBranch(): resolve remote prefix failed, param: remoteBranchFullRefSpec=$remoteBranchFullRefSpec")
                return Ret.createError(null,"resolve remote name from remote branch full refspec failed!", Ret.ErrCode.resolveRemotePrefixFromRemoteBranchFullRefSpecFailed)
            }
            //取出分支名
            val remoteBranchShortName = removeGitRefSpecPrefix("refs/remotes/$remote/", remoteBranchFullRefSpec)
            val remoteBranchFullName = "refs/heads/$remoteBranchShortName"

            return deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec(repo, remote, remoteBranchFullName, credential)
        }

        //使用调用者提供的remote名删除远程分支
        fun deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec(repo: Repository, remote: String, refsHeadsRefspec:String, credential: CredentialEntity?):Ret<String?> {
            try {
                if(remote.isNullOrBlank()) {
                    return Ret.createError(null, "remote is blank", Ret.ErrCode.remoteIsBlank)
                }
                if(refsHeadsRefspec.isNullOrBlank()) {
                    return Ret.createError(null, "refspec is blank", Ret.ErrCode.refspecIsBlank)
                }
                //push里会resolve，这里就不resolve remote了
//                val remoteObj = resolveRemote(repo, remote)
//                if(remoteObj==null) {
//                    return Ret.error(null, "resolve remote failed!", Ret.ErrCode.resolveRemoteFailed)
//                }
                // :refspec，等于删除远程分支
                return push(repo, remote, ":$refsHeadsRefspec", credential)
            }catch (e:Exception) {
                MyLog.e(TAG, "#deleteRemoteBranchByRemoteAndRefsHeadsBranchRefSpec() error:params are(remote=$remote, refsHeadsRefspec=$refsHeadsRefspec), err="+e.stackTraceToString())
                return Ret.createError(null, e.localizedMessage ?: "del remote branch err")
            }
        }

        fun getSingleCommit(repo: Repository, repoId: String, commitOidStr: String) :CommitDto{
            if(commitOidStr.isBlank()) {
                return CommitDto()
            }
            val commitOid = Oid.of(commitOidStr)
            if(commitOid.isNullOrEmptyOrZero) {
                return CommitDto()
            }

            val allBranchList = getBranchList(repo)
            val commit = resolveCommitByHash(repo, commitOidStr)?:return CommitDto()

            val repoIsShallow = isRepoShallow(repo)
            val shallowOidList = ShallowManage.getShallowOidList(repo.workdir().toString()+File.separator+".git")

            val allTagList = getAllTags(repo)
            return createCommitDto(commitOid, allBranchList, allTagList, commit, repoId, repoIsShallow, shallowOidList)
        }

        fun getRepoDotGitDirPathNoEndSlash(repo:Repository) :String {
            return repo.workdir().toString()+File.separator+".git"
        }

        //返回值 (nextOid, CommitDtoList)，nextOid就是CommitDtoList列表里最后一个元素之后的Oid，用来实现加载更多，如果不存在下一个元素，则是null，意味着已经遍历到提交树的最初提交了
        fun getCommitList(repo: Repository, repoId: String, startOid:Oid, stopCount:Int=Cons.defaultPageCount, sortMode:EnumSet<SortT> = getDefaultRevwalkSortMode(), retList: MutableList<CommitDto>):Pair<Oid?, List<CommitDto>> {
//            if(debugModeOn) {
//                MyLog.d(TAG, "#getCommitList: startOid="+startOid.toString())
//            }

            if(startOid==null || startOid.isNullOrEmptyOrZero) {
                return Pair(null, mutableListOf())
            }

            val revwalk = Revwalk.create(repo)
            revwalk.sorting(sortMode)
            revwalk.push(startOid)
            var count = 0
            val allBranchList = getBranchList(repo)

            val repoIsShallow = isRepoShallow(repo)
            val shallowOidList = ShallowManage.getShallowOidList(repo.workdir().toString()+File.separator+".git")

            val allTagList = getAllTags(repo)

            var next = revwalk.next()
            while (next!=null) {
                try {
                    if(count++ >= stopCount) {
                        break
                    }
                    val commit = resolveCommitByHash(repo, next.toString())?:continue
                    val c = createCommitDto(next, allBranchList, allTagList, commit, repoId, repoIsShallow, shallowOidList)

                    //添加元素
                    retList.add(c)
                    //更新迭代器
                    next = revwalk.next()
                }catch (e:Exception) {
                    MyLog.e(TAG, "#getCommitList():err:"+e.stackTraceToString())
                    return Pair(null, retList)
                }

            }

            return Pair(next, retList)
        }

        //获取一个提交的所有父提交的oid字符串列表
        fun getCommitParentsOidStrList(repo: Repository, commitOidStr:String):List<String> {
            val parentList = mutableListOf<String>()
            val commit = resolveCommitByHash(repo, commitOidStr)
            if(commit == null) {
                return parentList
            }

            val parentCount = commit.parentCount()
            if(parentCount>0) {
                var pc = 0
                while (pc < parentCount) {
                    val parent = commit.parentId(pc).toString()
                    parentList.add(parent)
                    pc++
                }
            }
            return parentList
        }

        fun getDateTimeStrOfCommit(commit: Commit):String {
            val time = commit.time()
            val secOffset = commit.timeOffset() * 60  // commit.timeOffset() 返回的是分钟偏移量，需要转换成秒给java的对象使用
            val formattedTimeStr = time.atOffset(ZoneOffset.ofTotalSeconds(secOffset))
                .format(Cons.defaultDateTimeFormatter)
            return formattedTimeStr
        }

        //如果仓库detached HEAD，显示 [仓库名 on hash(Detached)] ； 否则显示 [仓库名 on 分支名]
        fun getRepoOnBranchOrOnDetachedHash(repo:RepoEntity):String {
            return if(dbIntToBool(repo.isDetached)) "[${repo.repoName} on ${repo.lastCommitHash}(Detached)]" else "[${repo.repoName} on ${repo.branch}]"
        }

        //ps: 长hash用来checkout；短hash用来记到数据库(不过现在每次查数据库的仓库信息后都会从git仓库更新hash，所以这个字段实际已废弃)
        suspend fun doCheckoutBranchThenUpdateDb(repo:Repository, repoId:String, shortBranchNameOrShortHash:String,
                                                 fullBranchNameOrFullHash:String, upstreamBranchShortNameParam:String, checkoutType:Int,
                                                 force:Boolean=false, updateHead:Boolean=true
//                                                 errCallback: (String)->Unit = Msg.requireShow,
//                                                 successCallback:()->Unit
        ):Ret<Oid?> {

            val checkoutRet = if(checkoutType==Cons.checkoutType_checkoutRefThenUpdateHead) { //checkout本地分支
                // checkout分支并更新HEAD
                checkoutLocalBranchThenUpdateHead(repo, fullBranchNameOrFullHash, force, updateHead)
            }else if(checkoutType==Cons.checkoutType_checkoutRefThenDetachHead){  //checkout 远程分支/Tag
                checkoutRemoteBranchThenDetachHead(repo, fullBranchNameOrFullHash, force, updateHead)
            }else {  //checkout commit
                checkoutCommitThenDetachHead(repo, fullBranchNameOrFullHash, force, updateHead)
            }

            if(checkoutRet.hasError()) {
                return checkoutRet
            }


            //更新db
            if(updateHead) {
                //因为切换了分支，所以需要更新db
                //先根据id查出最新的db数据
                val repoDb = AppModel.singleInstanceHolder.dbContainer.repoRepository
                val appContext = AppModel.singleInstanceHolder.appContext
//            val repoFromDb = repoDb.getById(repoId)
//            if (repoFromDb == null) {
//                requireShowToast(appContext.getString(R.string.err_when_querying_repo_info))
//                requireShowToast(appContext.getString(R.string.checkout_failed))
//                return@doCheckoutLocalBranch false
//            }

                //把分支改成现在切换的分支
//            repoFromDb.branch = branchName.value

                //取出最新的提交hash

                val lastCommitHash = getShortOidStrByFull(checkoutRet.data.toString())  //执行到这，checkout肯定成功，data为commit Oid，应该不会为null

                //另一种取出最新提交hash的方法，有点舍近求远，主要思路是如果入参直接是hash，使用，如果不是，解析ref取出其hash(解析分支取出其commitid)
//                var lastCommitHash = shortBranchNameOrShortHash
//                if(checkoutType != Cons.checkoutTypeCommit) {  //如果checkoutType不是commit(是 本地分支 或 远程分支)，解析ref（一般是分支名），取出其commitId
//                    //查最新的commit hash
//                    // resolveRefByName() 用dwim()实现，应该会找到一个直接引用
//                    val resolveRefByName = resolveRefByName(repo, shortBranchNameOrShortHash);
//                    if (resolveRefByName == null) {
////                    errCallback(appContext.getString(R.string.err_when_querying_branch_commit_id))
////                    errCallback(appContext.getString(R.string.checkout_failed))
//                        return Ret.createError(null, appContext.getString(R.string.err_when_querying_branch_commit_id) , Ret.ErrCode.refIsNull)
//                    }
//
////                if(resolveRefByName.symbolicTarget()!=null) { // 如果id是null的话，需要peel出commit，不过应该不需要，因为resolveRefByName()用dwim()实现的，应该会取出直接引用
////
////                }
//                    //把commithash改成现在的分支最新新commithash
//                    lastCommitHash = resolveRefByName.id().toString().substring(Cons.gitShortCommitHashRange)
//                }

                if(checkoutType == Cons.checkoutType_checkoutRefThenUpdateHead) { //本地分支
                    //第4个参数是
                    repoDb.updateBranchAndCommitHash(
                        repoId = repoId,
                        branch = shortBranchNameOrShortHash,
                        lastCommitHash=lastCommitHash,
                        isDetached = Cons.dbCommonFalse,  //更新isDetached值为假（切换本地分支，肯定不是detached）
                        upstreamBranch = upstreamBranchShortNameParam  //这个参数是当前分支的上游，如果是新建分支，应该传空，因为新创建的分支，肯定没上游，因为我没写创建时设置上游的功能
                    )
                }else {  // 远程分支或hash。detached head
                    repoDb.updateBranchAndCommitHash(
                        repoId = repoId,
                        branch = lastCommitHash,  //checkout 远程分支，这个值应该传commit hash，或者不传也无所谓，因为发现仓库是detached状态时，会不显示分支，改显示commithash
                        lastCommitHash=lastCommitHash,
                        isDetached = Cons.dbCommonTrue,
                        upstreamBranch = upstreamBranchShortNameParam
                    )
                }

            }

//            successCallback()
            return checkoutRet

        }

        //errCallBack 可用来显示错误通知，和记录错误信息到日志之类的，errCallBack第1个参数是错误信息，第2个参数是仓库id
        fun doCreateBranch(
            repo: Repository,
            repoId: String,
            branchNameParam: String,  //要创建的分支名
            basedHead: Boolean,  //是否基于head，若是则无视baseRefSpec和createByRef
            baseRefSpec: String,  //若createByRef为假，此值应为提交号；否则此值应为分支名（最好是完整名）
            createByRef: Boolean,  //true 根据引用创建分支，否则直接用提交号创建分支，只有baseHead为假时，此值才有效，否则一律使用当前HEAD指向的提交创建分支
//                           errCallback:(String)->Unit = Msg.requireShow,
//                           successCallback:()->Unit
            overwriteIfExisted:Boolean
        ):Ret<Triple<String, String, String>?>  //返回: 分支长名，短名，完整hash
        {
            val appContext = AppModel.singleInstanceHolder.appContext

            //这个如果检查，不会发生本地和远程分支同名的情况，例如同时存在 refs/remotes/origin/abc 和 refs/heads/origin/abc，好处是不容易混淆，坏处是增加了限制；
            //如果不检查，则可创建和远程分支同名分支，虽然全名处于不同命名空间（refs/remotes和refs/heads），但很多地方是通过短名解析的，所以实际上会混淆解析失败或者原本想
            // 解析远程结果解析出的是本地的情况，不加限制了，出错算用户的，又不是我的锅，加了的话万一需要用到，我还得改代码，麻烦。
            //如果没开已存在则覆盖，检查分支是否存在，存在则直接返回
//            if(!overwriteIfExisted) {
//                //检查分支是否存在
//                val isBranchExists = isBranchNameAlreadyExists(repo, branchNameParam)
//
//                //分支若存在，显示提示，结束操作，无需刷新页面
//                if(isBranchExists) {
////                errCallback(appContext.getString(R.string.create_failed_branch_exists))
//                    return  Ret.createError(null,appContext.getString(R.string.branch_already_exists), Ret.ErrCode.branchAlreadyExists)
//                }
//            }


            //创建分支
            val result = if(basedHead) {
                createLocalBranchBasedHead(repo, branchNameParam, overwriteIfExisted)  //内部也调用的 #createLocalBranchBasedOidStr，所以返回值和 createLocalBranchBasedOidStr 匹配
            }else {  //基于remote创建分支！
                var refOidStr = baseRefSpec  //适用于用提交创建分支
                if(createByRef) {  //如果是用引用创建分支，会查一下其commitOid，并给refOidStr重新赋值
                    val baseRef = resolveRefByName(repo, baseRefSpec)
                    if(baseRef == null) {
//                        errCallback(appContext.getString(R.string.resolve_reference_failed))
                        return Ret.createError(null,appContext.getString(R.string.resolve_reference_failed), Ret.ErrCode.refIsNull)
                    }
                    refOidStr = baseRef.peel(GitObject.Type.COMMIT).id().toString()  // peel出commit id
                }
                createLocalBranchBasedOidStr(repo, branchNameParam, refOidStr, overwriteIfExisted)
            }

            if(result.hasError()) {  //应该不会出错，但有可能出异常，呵呵
                if(result.code == Ret.ErrCode.headIsNull) {
                    result.msg = appContext.getString(R.string.resolve_repo_head_failed)
                }
                //失败result
                return result
            }

//            successCallback()
            //成功result
            return result
        }


        fun isRepoStatusErr(repoFromDb: RepoEntity):Boolean {
            return repoFromDb.workStatus >= Cons.dbCommonErrValStart
        }

        fun isRepoStatusNotReady(repoFromDb: RepoEntity):Boolean {
            return repoFromDb.workStatus==Cons.dbRepoWorkStatusNotReadyNeedClone || repoFromDb.workStatus==Cons.dbRepoWorkStatusNotReadyNeedInit
        }
        fun isRepoStatusNotReadyOrErr(repoFromDb: RepoEntity):Boolean {
            return isRepoStatusErr(repoFromDb) || isRepoStatusNotReady(repoFromDb)

        }

        fun isRepoStatusReady(repoFromDb: RepoEntity):Boolean {
            return !isRepoStatusNotReady(repoFromDb)
        }

        fun isRepoStatusNoErr(repoFromDb: RepoEntity):Boolean {
            return !isRepoStatusErr(repoFromDb)
        }

        //更新从数据库中查出的仓库信息，每次从数据库get仓库时，都应调用此方法
        fun updateRepoInfo(repoFromDb: RepoEntity) {
            val funName = "updateRepoInfo"

            try {
                //未就绪或出错的仓库没啥好更新的，直接返回即可
                if(isRepoStatusNotReadyOrErr(repoFromDb)) {
                    return;
                }

                Repository.open(repoFromDb.fullSavePath).use { repo ->
                    //算了，不捕获了，查状态出错后面应该也没法正常进行，直接走正常流程抛异常记log即可
//                    repoFromDb.gitRepoState = try {
//                        repo.state()
//                    }catch (e:Exception) {
//                        MyLog.e(TAG, "#$funName: query repo state err: ${e.stackTraceToString()}")
//                        null
//                    }

                    //查询仓库状态
                    repoFromDb.gitRepoState = repo.state()

                    //查询仓库最新信息
                    val head = resolveHEAD(repo)
                    repoFromDb.branch = head?.shorthand()?:""
                    repoFromDb.lastCommitHash = getShortOidStrByFull(head?.id().toString())
                    repoFromDb.isDetached = boolToDbInt(repo.headDetached())
                    if(!dbIntToBool(repoFromDb.isDetached)) {  //只有非detached才有upstream
                        val upstream = getUpstreamOfBranch(repo, repoFromDb.branch)
                        repoFromDb.upstreamBranch = upstream.remoteBranchShortRefSpec

                        //如果存在有效上游且上游【已发布】(已发布代表远程分支在本地存在)，检查本地和上游是否一致（是否需要更新）
                        if(upstream.isPublished && upstream.remote.isNotBlank() && upstream.branchRefsHeadsFullRefSpec.isNotBlank()) {
                            //非detached时，检查 ahead behind commit，来决定是否提示用户需要同步
                            val localOid = head?.id()
                            val upstreamOid = resolveRefByName(repo, upstream.remoteBranchRefsRemotesFullRefSpec)?.id()
                            if(localOid!=null && upstreamOid!=null) {
                                val (ahead, behind) = getAheadBehind(repo, localOid, upstreamOid)
                                repoFromDb.ahead = ahead
                                repoFromDb.behind = behind
                                if(repoFromDb.ahead==0 && repoFromDb.behind ==0) {  //本地不领先也不落后上游，最新
                                    repoFromDb.workStatus = Cons.dbRepoWorkStatusUpToDate
                                }else {
                                    //这里并不是最终的workStatus值，后面还会检查是否有冲突，如果有会再更新
                                    repoFromDb.workStatus = Cons.dbRepoWorkStatusNeedSync
                                }

                            }

                        }
                    }
                    repoFromDb.isShallow = boolToDbInt(isRepoShallow(repo))

                    //更新workstatus
                    val repoState = repo.state()
                    //仓库NONE无需处理，就显示up-to-date之类的就行
                    if(repoState!=Repository.StateT.NONE) {
                        if(hasConflictItemInRepo(repo)) {  //有冲突条目
                            repoFromDb.workStatus = Cons.dbRepoWorkStatusHasConflicts
                        }else if(repoState == Repository.StateT.MERGE) {  //不一定有冲突条目，但仓库处于merge状态，这个必须处理，不然changelist能看出来在merge状态，仓库页面卡片还显示错误的up-to-date，之前遇到过几次，容易让人迷惑
                            repoFromDb.workStatus = Cons.dbRepoWorkStatusMerging
                        }else if(repoState==Repository.StateT.REBASE_MERGE) {
                            repoFromDb.workStatus = Cons.dbRepoWorkStatusRebasing
                        }else if(repoState==Repository.StateT.CHERRYPICK) {
                            repoFromDb.workStatus = Cons.dbRepoWorkStatusCherrypicking
                        }

//                     else if(state == StateT.BISECT){} //暂时不用处理其他状态
//                        else{ //上面一系列else if后，进入else，代表仓库状态正常
//                            //仓库正常时没什么要执行的操作
////                            repoFromDb.workStatus = repoState.name
//                        }

                    }

                    //检查是否有临时状态，syncing之类的，如果有存上，临时状态的设置和清除都由操作执行者承担，比如syncing状态，谁doSync谁设这个状态和清这个状态
                    repoFromDb.tmpStatus = RepoStatusUtil.getRepoStatus(repoFromDb.id)

                }
            }catch (e:Exception) {
                //TODO 这里可设个特殊的仓库状态，卡片显示查询仓库信息出错，可选操作为 刷新、删除，和克隆错误的区别在于无法编辑仓库信息，或者也可支持重新编辑克隆信息并克隆（需要提醒用户会删除之前克隆时创建的本地仓库目录）
                //这里不插入了，因为这个函数经常调用的关系，如果仓库一更新错且没及时处理，会生成很多调错误记录
//                createAndInsertError(repoFromDb.id, "update repo info err!")
                //不过log还是要记下的
                MyLog.e(TAG, "#$funName() failed!, repoId=${repoFromDb.id}, repo.toString()=${repoFromDb.toString()}\n Exception is:${e.stackTraceToString()}")
            }
        }

        /**
         * 返回一个pair，左边ahead，右边behind
         */
        fun getAheadBehind(repo:Repository, localOid:Oid, upstreamOid:Oid) : Pair<Int,Int>{
            try{
                val count = Graph.aheadBehind(repo, localOid, upstreamOid)
                return Pair(count.ahead, count.behind)
            }catch (e:Exception) {
                MyLog.e(TAG, "#getAheadBehind() failed!, localOid=${localOid}, upstreamOid=${upstreamOid}\n Exception is:${e.stackTraceToString()}")
                //查不出来就当up-to-date吧
                return Pair(0,0)
            }
        }

        fun setRemoteUrlForRepo(repo: Repository, remote:String, url:String) {
            Remote.setUrl(repo,remote, URI.create(url));

        }

        //参数是git仓库下任意路径，可以用这个方法来判断指定目录是否是一个git仓库，是就返回仓库引用，否则返回null
        fun findRepoByPath(underGitRepoPath:String) :Repository? {
            //最后一个参数是用系统分隔符(linux 是 :, windows可能是 ;)分隔的目录列表，搜索如果遇到上限目录就会停止
            try {  // 如果找到，返回仓库引用，否则返回null

                //可正常处理submodule（子模块），如果入参是一个子模块下的路径，会返回子模块的仓库引用而不是父的。（因此用在remove from git功能时，不会用父仓库移除子模块下的文件。）
                return Repository.openExt(underGitRepoPath, null,AppModel.singleInstanceHolder.allRepoParentDir.canonicalPath)

            }catch (e:Exception) {
                MyLog.e(TAG, "#findRepoByPath(): err: ${e.stackTraceToString()}")
                return null
            }
        }

        fun setRepoStateText(repoStateIntValue:Int, needShowRepoState:MutableState<Boolean>, repoStateText:MutableState<String>, appContext: Context) {
            //判断仓库状态
            if(repoStateIntValue == Cons.gitRepoStateInvalid) {  //没匹配到任何枚举状态，就会是null，应该不会发生这种状况
                repoStateText.value = appContext.getString(R.string.unknown)
                needShowRepoState.value = true
            }else if(repoStateIntValue == Repository.StateT.MERGE.bit) {
                repoStateText.value = appContext.getString(R.string.merge_state)
                needShowRepoState.value = true
            }else if(repoStateIntValue == Repository.StateT.REBASE_MERGE.bit) {
                repoStateText.value = appContext.getString(R.string.rebase_state)
                needShowRepoState.value = true

            //如果以后添加其他状态，在这加else if 即可
            }else if(repoStateIntValue == Repository.StateT.CHERRYPICK.bit) {
                repoStateText.value = appContext.getString(R.string.cherrypick_state)
                needShowRepoState.value = true

            //如果以后添加其他状态，在这加else if 即可
            }else { // NONE
                repoStateText.value = ""
                needShowRepoState.value = false
            }

        }

        fun createTagLight(repo: Repository, tagName:String, commit: Commit, force: Boolean):Oid {
            return Tag.createLightWeight(
                repo,
                tagName,
                commit,
                force);
        }

        fun createTagAnnotated(repo: Repository, tagName:String, commit: Commit, tagMsg: String, gitUserName:String, gitEmail:String, force: Boolean):Oid {
            return Tag.create(
                repo,
                tagName,
                commit,
                Signature.create(gitUserName, gitEmail),
                tagMsg,
                force
            )
        }

        fun getAllTags(repo: Repository): List<TagDto> {
            val tags = mutableListOf<TagDto>()
            Tag.foreach(repo) { name, oidStr ->
                try {
                    // only annotated tag lookup-able, light tag will throw exception
                    val tag = Tag.lookup(repo, Oid.of(oidStr))
                    val tagger = tag.tagger()
                    tags.add(
                        TagDto(
                            name = name,
                            shortName = getShortTagNameByFull(name),
                            fullOidStr = oidStr,
                            targetFullOidStr = tag.targetId().toString(),
                            isAnnotated = true,
                            taggerName = tagger?.name ?:"",
                            taggerEmail = tagger?.email ?:"",
                            date = tagger?.getWhen(),
                            msg = tag.message(),
                        )
                    )

                }catch (e:Exception) {
                    //light weight tag
                    tags.add(
                        TagDto(
                            name = name,
                            shortName = getShortTagNameByFull(name),
                            fullOidStr = oidStr,
                            targetFullOidStr = oidStr,
                            isAnnotated = false
                        )
                    )
                }

                0
            }

            return tags
        }

        fun getShortTagNameByFull(fullTagName:String) :String {
            return try {
                val keyword = "refs/tags/"
                fullTagName.substring(fullTagName.indexOf(keyword)+keyword.length)

            }catch (e:Exception) {
                ""
            }
        }

        fun getFormattedUsernameAndEmail(username: String, email: String):String {
            return username+" <${email}>"
        }

        fun fetchAllTags(repo:Repository, repoFromDb: RepoEntity, remoteAndCredentials:List<RemoteAndCredentials>, force:Boolean = false) {
            val refspecs = Array(1) {if(force) "+${Cons.gitAllTagsRefspecForFetchAndPush}" else Cons.gitAllTagsRefspecForFetchAndPush}  //这+号好像还是有点用的
//            println("refspecs:${refspecs.contentToString()}")  // refspec没问题，就是单纯不支持 + 作为force，他妈的，还得配合autodownloadtag选项才能实现强制覆盖tags
//            val refspecs = Array(1) {Cons.gitAllTagsRefspecForFetchAndPush}
            val downloadTags = if(force) Remote.AutotagOptionT.NONE else Remote.AutotagOptionT.UNSPECIFIED
            fetchRemoteListForRepo(
                repo, remoteAndCredentials, repoFromDb,
                requireUnshallow = false, refspecs,
                pruneType = FetchOptions.PruneT.UNSPECIFIED,  // FetchOptions.PruneT.UNSPECIFIED作用是遵循配置文件prune，默认情况下可避免删除远程有本地没有的tags，除非用户改配置文件强制启用prune
                downloadTags = downloadTags,
            )
        }

        /**
         * 若需要force push 自己在对应的refspec里加
         */
        fun pushTags(repo:Repository, remoteAndCredentials:List<RemoteAndCredentials>, refspecs: List<String>) {
            pushMulti(repo, remoteAndCredentials, refspecs)
        }

//        fun pushAllTags(repo:Repository, remoteAndCredentials:List<RemoteAndCredentials>, force: Boolean=false, delMode:Boolean=false) {
//            val refspecs = if(delMode) listOf(Cons.gitDelAllTagsRefspecForFetchAndPush) else listOf(if(force) "+${Cons.gitAllTagsRefspecForFetchAndPush}" else Cons.gitAllTagsRefspecForFetchAndPush)
//            pushTags(repo, remoteAndCredentials, refspecs)
//        }

        /**
         * tag name list必须用不包含 refs/tags/ 前缀的shortName，不然libgit2库会自动加前缀，导致删错
         */
        fun delTags(repo:Repository, tagShortNames:List<String>) {
            val funName = "delTags"
            tagShortNames.forEach {
                try {
                    Tag.delete(repo, it)
                }catch (e:Exception) {
                    MyLog.e(TAG, "#$funName err: del tag '$it' err: ${e.stackTraceToString()}")
                }
            }
        }

        fun createRemote(repo:Repository, remoteName:String, url:String):Ret<Remote?> {
            return try{
                val remote = Remote.create(
                    repo,
                    remoteName,
                    URI.create(url)
                )
                Ret.createSuccess(remote)
            }catch (e:Exception) {
                MyLog.e(TAG, "#createRemote err: remoteName=$remoteName, url=$url, err="+e.stackTraceToString())
                Ret.createError(null, e.localizedMessage ?: "unknown err", exception = e)
            }
        }

        fun delRemote(repo:Repository, remoteName:String):Ret<String?> {
            return try {
                //删除fetch字段
                //fetch字段可能multi，这个库有bug，如果fetch字段多个，会删除remote失败，所以处理一下
                val key = "remote.$remoteName.fetch"
                val writableConfig = getRepoConfigForWrite(repo)
                writableConfig.deleteMultivar(key, Cons.regexMatchAll)

                //删除remote
                Remote.delete(repo, remoteName)

                //返回成功
                Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#delRemote err: remoteName=$remoteName, err="+e.stackTraceToString())
                Ret.createError(null, e.localizedMessage?:"unknown err", exception = e)
            }
        }

        fun stashList(repo:Repository, out:MutableList<StashDto>):List<StashDto> {
            Stash.foreach(
                repo
            ) { index: Int, message: String?, stashId: Oid ->
                out.add(StashDto(index, message?:"", stashId))
                0
            }
            return out
        }

        fun stashSave(
            repo:Repository,
            stasher: Signature,
            msg:String,
            flags:EnumSet<Stash.Flags> = EnumSet.of(Stash.Flags.DEFAULT)
        ):Oid? {

            return Stash.save(repo, stasher, msg, flags)

//            return try {
//                Ret.createSuccess(Stash.save(repo, stasher, msg, flags))
//            }catch (e:Exception) {
//                Ret.createError(null, e.localizedMessage ?: "unknown err", exception = e)
//            }
        }

        fun stashApply(repo:Repository, indexOfStash:Int, applyOptions: Stash.ApplyOptions? = null) {
            Stash.apply(repo, indexOfStash, applyOptions);
        }

        fun stashPop(repo:Repository, indexOfStash:Int, applyOptions: Stash.ApplyOptions? = null) {
            Stash.pop(repo, indexOfStash, applyOptions);
        }

        fun stashDrop(repo:Repository, indexOfStash:Int) {
            Stash.drop(repo, indexOfStash);
        }

        /**
         * @return "stash@datetime#random"
         */
        fun stashGenMsg():String {
            return "stash@"+ getNowInSecFormatted(Cons.dateTimeFormatterCompact)+"#"+getShortUUID()
        }

        fun getReflogList(repo:Repository, name:String="HEAD", out: MutableList<ReflogEntryDto>):List<ReflogEntryDto> {
            val reflog = Reflog.read(repo, name)
            val count = reflog.entryCount()
            if(count>0) {
                for(i in 0 ..< count) {
                    val e = reflog.entryByIndex(i)
                    val commiter = e.committer()

                    out.add(ReflogEntryDto(
                        username = commiter.name,
                        email = commiter.email,
                        date = commiter.`when`.format(Cons.defaultDateTimeFormatter),
                        idNew = e.idNew(),
                        idOld = e.idOld(),
                        msg = e.message()?:""
                    ))
                }
            }

            return out
        }

        private fun initCherrypickOptions(opts:Cherrypick.Options) {
            val mergeOpts = opts.mergeOpts
            mergeOpts.flags = 0  // 0代表一个flag都不设。另外：flag可设多个，重命名检测和有冲突不合并立即退出等，参见: https://libgit2.org/libgit2/#HEAD/type/git_merge_flag_t
            mergeOpts.fileFlags = Merge.FileFlagT.STYLE_DIFF3.bit //GIT_MERGE_FILE_STYLE_DIFF3, 1<<1

            val checkoutOpts = opts.checkoutOpts
            //强制target(upstream)覆盖本地文件，即使文件修改没提交
            //                checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.FORCE, Checkout.StrategyT.ALLOW_CONFLICTS)
            //不会覆盖本地未提交的文件（推荐）
            checkoutOpts.strategy = EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.ALLOW_CONFLICTS)

        }

        /**
         * @param parentCommit for match mainline params，仅当存在多个parent时需要设置
         * @param pathSpecList 指定要checkout的文件列表，若不为空则会设置给checkout选项
         *
         * @return 注意：操作成功也有可能oid为null，因为只有当autoCommit为true才会创建提交才会有oid，否则只会执行cherrypick不提交也没oid
         */
        fun cherrypick(repo:Repository, targetCommitFullHash:String, parentCommitFullHash:String="", pathSpecList: List<String>?=null, cherrypickOpts:Cherrypick.Options? = null, autoCommit:Boolean = true):Ret<Oid?> {
            val state = repo.state()
            if(state == null || (state != Repository.StateT.NONE)) {
                return Ret.createError(null, "err:repo state is not 'NONE'")
            }

            //这个检查是可选的，checkout 设为safe时 index/worktree如果有东西就会中止操作
//            if(!indexIsEmpty(repo)) {
//                return Ret.createError(null, "plz commit your changes before cherrypick")
//            }

            val cherrypickOpts = if(cherrypickOpts==null) {
                val tmp = Cherrypick.Options.createDefault()
                initCherrypickOptions(tmp)
                tmp
            }else {
                cherrypickOpts
            }

            val target = resolveCommitByHash(repo, targetCommitFullHash)
            if(target == null){
                return Ret.createError(null, "resolve target commit failed!")
            }

            //设置mainline
            //如果父提交大于1必须设置mainline否则会报错 （只有1个父提交就不用设置了，默认值0即可）
            val pc = target.parentCount()
            if(pc>1) {
                if(parentCommitFullHash.isBlank()) {
                    return Ret.createError(null, "parent commit not specified")
                }

                for(i in 0..<pc) {
                    val pid = target.parentId(i)
                    if(pid==null) {  //shouldn't be null
                        continue
                    }

                    if(parentCommitFullHash.equals(pid.toString())) {
                        cherrypickOpts.mainline = i+1  //mainline值为索引加1
                        break
                    }
                }
            }

            //如果文件列表不为空，设置一下。（这个参数的应用场景为cherrypick特定文件，例如：在TreeToTree diff页面，选择文件，然后仅pick那些文件）
            if(pathSpecList!=null && pathSpecList.isNotEmpty()) {
                cherrypickOpts.checkoutOpts.setPaths(pathSpecList.toTypedArray())
            }

            //执行cherrypick
            Cherrypick.cherrypick(repo, target, cherrypickOpts)

            //有冲突直接返回，不创建提交，后续执行cherrypick continue即可
            if(repo.index().hasConflicts()) {
                return Ret.createError(null, "cherrypick: has conflicts!", Ret.ErrCode.mergeFailedByAfterMergeHasConfilts)
            }

            //pick完了，发现，没区别...这时没必要创建提交，清下状态即可
            if(indexIsEmpty(repo)) {
                cleanRepoState(repo)
                //返回错误是为了显示错误信息，如果返回success就不显示up to date了
                return Ret.createError(null, "Already up-to-date", errCode = Ret.ErrCode.alreadyUpToDate)
            }

            //pick完了，无冲突，index非空，创建提交
            if(autoCommit) {
                //无冲突则创建提交
                val author = target.author()
                //创建commit并返回oid
                return createCommit(repo, target.message(), author.name, author.email, cleanRepoStateIfSuccess = true)
            }

            return Ret.createSuccess(null)
        }

        /**
         * 获取cherry pick target hash，用来查询提交信息之类的
         */
        fun getCherryPickHeadHash(repo: Repository):String {
            val file = File(getRepoDotGitDirPathNoEndSlash(repo), "CHERRY_PICK_HEAD")
            //文件不存在返回空
            if(!file.exists()) {
                return ""
            }

            //文件存在返回第一行，正常来说应该是cherrypick的target commit hash
            file.bufferedReader().use {
                return it.readLine().trim()
            }

        }


        //不会返回oid，只是和continue rebase的返回值一致，方便返回
        fun readyForContinueCherrypick(repo: Repository):Ret<Oid?> {
            val funName = "readyForContinueCherrypick"
            val appContext = AppModel.singleInstanceHolder.appContext

            try {
                //20240814:目前libgit2只支持REBASE_MERGE，不支持 git默认的 REBASE_INTERACTIVE
                if(repo.state() != Repository.StateT.CHERRYPICK) {
                    return Ret.createError(null, appContext.getString(R.string.repo_not_in_cherrypick))
                }
                if(repo.index().hasConflicts()) {
                    return Ret.createError(null, appContext.getString(R.string.plz_resolve_conflicts_first))
                }
                if(getCherryPickHeadHash(repo).isBlank()) {
                    return Ret.createError(null, appContext.getString(R.string.no_cherrypick_head_found))
                }

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName err: "+e.stackTraceToString())
                return Ret.createError(null, "err:"+e.localizedMessage)
            }
        }

        fun getCherryPickHeadCommit(repo:Repository):Ret<Commit?> {
            val cherrypickTargetCommit = resolveCommitByHash(repo, getCherryPickHeadHash(repo))
            if(cherrypickTargetCommit==null) {
                return Ret.createError(null, "resolve cherry pick HEAD failed!")
            }

            return Ret.createSuccess(cherrypickTargetCommit)
        }

        fun getCherryPickHeadCommitMsg(repo: Repository):String {
            val ret = getCherryPickHeadCommit(repo)
            if(ret.hasError()) {
                return ""
            }else {
                return ret.data!!.message()
            }
        }

        /**
         * 解决完冲突后调用这个
         * @param overwriteAuthor overwriteAuthor为真则新提交的author会变成执行cherrypick的那个人的信息而不是原提交的信息；若为假则使用原始提交作者信息
         *
         * @param msg 如果为空，将会使用原提交的msg
         */
        fun cherrypickContinue(repo: Repository, msg: String="", username: String, email: String, autoClearState:Boolean=true, overwriteAuthor: Boolean):Ret<Oid?> {
            val readyCheckRet = readyForContinueCherrypick(repo)
            if(readyCheckRet.hasError()) {
                return readyCheckRet
            }

            //获取cherrypick提交号
//            .git/CHERRY_PICK_HEAD 第一行

            //如果msg为空，尝试获取原提交的信息
            val msg = if(msg.isBlank()) {
                val r = getCherryPickHeadCommit(repo)
                if(r.hasError()) {
                    return Ret.createError(null, r.msg, exception = r.exception)
                }

                r.data!!.message()
            }else {  //若提交不为空则使用参数中的信息
                msg
            }

            //执行到这，冲突已解决

            // 检查下index是否为空，若为空，可能用户在冲突时接受了他自己的代码，这时没有需要提交的内容，清下仓库状态退出cherrypick即可，因为目前只cherrypick最多一个提交，所以如果index为空，直接退出cherrypick即可，如果cherrypick多个提交则可检查有无后续，若有就跳过当前，继续执行后面的，参考rebaseContinue
            if(indexIsEmpty(repo)) {  // 这里隐含的完整条件为：index为空且无冲突条目，冲突条目在上面ready里判断了，所以这里无需再判断
//                cherrypickAbort(repo) //用abort不太合适，太重量级，hardReset还可能覆盖用户内容
                cleanRepoState(repo)  //简单清下状态就行
                return Ret.createError(null, "index is empty, cherrypick canceled")
            }

            //创建提交
            val ret = createCommit(repo, msg, username, email, overwriteAuthorWhenAmend = overwriteAuthor, cleanRepoStateIfSuccess = autoClearState)

            //改成在创建commit的函数里清了
            //如果创建提交成功，清除仓库状态
//            if(ret.success() && autoClearState) {
//                cleanRepoState(repo)
//            }

            return ret
        }

        /**
         * 和abort merge实现一样
         */
        fun cherrypickAbort(repo:Repository):Ret<String?> {
            return resetHardToHead(repo)
        }

        fun savePatchToFileAndGetContent(
            outFile:File?=null,  // 为null不写入文件，否则写入
            pathSpecList: List<String>?=null,   //为null或空代表diff所有文件

            repo: Repository,
            tree1: Tree,
            tree2: Tree?,  // when diff to worktree, pass `null`
            diffOptionsFlags: EnumSet<Diff.Options.FlagT> = getDefaultDiffOptionsFlags(),
            fromTo: String,
            reverse: Boolean = false, // when compare worktreeToTree , pass true, then can use treeToWorktree api to diff worktree to tree
            treeToWorkTree: Boolean = false,  // only used when fromTo=TreeToTree, if true, will use treeToWorkdir instead treeToTree

            returnDiffContent:Boolean = false  //为true返回patch内容，否则不返回，因为有可能内容很大，所以若没必要不建议返回
        ):Ret<String?>{
            val funName = "savePatchToFile"
            try{
                val options = Diff.Options.create()
                val opFlags = diffOptionsFlags.toMutableSet()

                if(reverse) {
                    opFlags.add(Diff.Options.FlagT.REVERSE)
                }

                options.flags = EnumSet.copyOf(opFlags);
                MyLog.d(TAG, "#$funName: options.flags = $opFlags")

                if(pathSpecList!=null && pathSpecList.isNotEmpty()) {
                    options.pathSpec = pathSpecList.toTypedArray()
                }

                val diff = if(fromTo == Cons.gitDiffFromIndexToWorktree) {
                    Diff.indexToWorkdir(repo, null, options)
                }else if(fromTo == Cons.gitDiffFromHeadToIndex) {
                    val headTree:Tree? = resolveHeadTree(repo)
                    if(headTree==null) {
                        MyLog.w(TAG, "#$funName(): require diff from head to index, but resolve HEAD tree failed!")
                        return Ret.createError(null, "require diff from head to index, but resolve HEAD tree failed!")
                    }

                    Diff.treeToIndex(repo, headTree, repo.index(), options)
                }else if(fromTo == Cons.gitDiffFromHeadToWorktree){
                    val headTree:Tree? = resolveHeadTree(repo)
                    if(headTree==null) {
                        MyLog.w(TAG, "#$funName(): require diff from head to worktree, but resolve HEAD tree failed!")
                        return Ret.createError(null, "require diff from head to worktree, but resolve HEAD tree failed!")
                    }

                    Diff.treeToWorkdir(repo, headTree, options);
                }else {  // fromTo == Cons.gitDiffFromTreeToTree
                    // tree to tree
                    MyLog.d(TAG, "#$funName(): require diff from tree to tree, tree1Oid=${tree1?.id().toString()}, tree2Oid=${tree2?.id().toString()}, reverse=$reverse")
//                println("treeToWorkTree:${treeToWorkTree},  tree1Oid=${tree1?.id().toString()}, tree2Oid=${tree2?.id().toString()}, reverse=$reverse")
                    if(treeToWorkTree) Diff.treeToWorkdir(repo, tree1, options) else Diff.treeToTree(repo, tree1, tree2, options)
                }

                //遍历diff条目，存储patch到字符串
                val sb = StringBuilder()
                val allDeltas = diff.numDeltas()
                for(i in 0..<allDeltas) {
                    val d = diff.getDelta(i)
                    //如果是binary file，忽略
                    if(d?.flags?.contains(Diff.FlagT.BINARY) == true) {
                        continue
                    }

                    val patch = Patch.fromDiff(diff, i)
                    val patchOutStr = patch?.toBuf() ?:""

                    //忽略空字符串
                    if(patchOutStr.isEmpty()) {
                        continue
                    }

                    sb.append(patchOutStr).appendLine()
                }

                var diffContent = ""

                val writeToFile = outFile != null
                if(writeToFile) {
                    //写入patch字符串到文件
                    outFile!!.bufferedWriter().use {
                        diffContent = sb.toString()
                        it.write(diffContent)
                    }
                }

                val ret = if(returnDiffContent) {
                    if(!writeToFile) {  //如果writeToFile为假，需要获取一下diff内容
                        sb.toString()
                    }else {  //如果writeToFile为真，写入文件之前已经获取了diff内容，这里直接返回即可
                        diffContent
                    }
                }else {  //不请求返回diff内容
                    null
                }

                //返回操作成功
                return Ret.createSuccess(ret)

            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName err:${e.stackTraceToString()}")
                return Ret.createError(null, e.localizedMessage ?: "save patch err", exception = e)
            }
        }

        fun isWorktreeClean(repo: Repository):Boolean {
            return getWorkdirStatusList(repo).entryCount() == 0
        }

        fun applyPatchFromFile(
            inputFile:File,
            repo:Repository,
            applyOptions: Apply.Options?=null,
            location:Apply.LocationT = Apply.LocationT.WORKDIR,  // default same as `git apply`
            checkWorkdirCleanBeforeApply: Boolean = true,
            checkIndexCleanBeforeApply: Boolean = false
        ):Ret<Unit?> {
            try {
                if(checkWorkdirCleanBeforeApply && !isWorktreeClean(repo)) {
                    return Ret.createError(null, "err: workdir has uncommitted changes!")
                }

                if(checkIndexCleanBeforeApply && !indexIsEmpty(repo)) {
                    return Ret.createError(null, "err: index has uncommitted changes!")
                }

                //强制检查是否有冲突
                if(repo.index().hasConflicts()) {
                    return Ret.createError(null, "err: plz resolve conflicts before apply patch!")
                }

                //取出patch内容
                var content = ""
                inputFile.bufferedReader().use {
                    content = it.readText()
                }
                //检查内容是否为空
                if(content.isBlank()) {
                    return Ret.createError(null, "err: patch is empty!")
                }

                //根据patch内容创建diff对象
                val diff = Diff.fromBuffer(content)
                //应用patch
                Apply.apply(repo, diff, location, applyOptions)

                return Ret.createSuccess(null)
            }catch (e:Exception) {
                return Ret.createError(null, e.localizedMessage ?:"apply patch err", exception = e)
            }

        }


        /**
         * 仅在HEAD不为null时返回成功
         */
        fun getHeadCommit(repo: Repository):Ret<Commit?> {
            try {
                val head = resolveHEAD(repo) ?: return Ret.createError(null, "HEAD is null")
                val commit = resolveCommitByHash(repo, head.id().toString()) ?: return Ret.createError(null, "HEAD commit is null")

                return Ret.createSuccess(commit)
            }catch (e:Exception) {
                return Ret.createError(null, e.localizedMessage ?:"get commit of head err", exception = e)
            }
        }

        /**
         * most time used this for amend commit, when user checked amend, then query msg of last commit
         */
        fun getHeadCommitMsg(repo:Repository):String {
            val commitRet = getHeadCommit(repo)

            return if(commitRet.hasError()) {
                ""
            }else {
                commitRet.data?.message() ?: ""
            }
        }

        fun resolveTag(repo:Repository, oidFullOrShortStr: String):Tag? {
            try {
                val tag = Tag.lookupPrefix(repo, oidFullOrShortStr)
                return tag
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveTag() error, params are (oidFullOrShortStr=$oidFullOrShortStr}),\nerr is:"+e.stackTraceToString())
                return null
            }
        }


        /**
         * 解析出ref的commit oid，若出错，返回空字符串
         */
        fun resolveCommitOidByRef(repo:Repository, shortOrFullRefSpec:String):Oid? {
            try {
                val ref = resolveRefByName(repo, shortOrFullRefSpec)
                val cid = ref?.peel(GitObject.Type.COMMIT)?.id()

                return cid
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveCommitOidByRef() error, params are (shortOrFullRefSpec=$shortOrFullRefSpec}),\nerr is:"+e.stackTraceToString())

                return null
            }
        }

        fun resolveCommitByRef(repo:Repository, shortOrFullRefSpec:String):Commit? {
            try {
                val cid = resolveCommitOidByRef(repo, shortOrFullRefSpec)

                if(cid==null || cid.isNullOrEmptyOrZero) {
                    return null
                }

                return resolveCommitByHash(repo, cid.toString())
            }catch (e:Exception) {
                MyLog.e(TAG, "#resolveCommitByRef() error, params are (shortOrFullRefSpec=$shortOrFullRefSpec}),\nerr is:"+e.stackTraceToString())

                return null
            }
        }

        /**
         * 通过 hash 或 引用（branch/tag）解析 Commit对象
         * @return 只有成功解析出commit才会返回成功
         */
        fun resolveCommitByHashOrRef(repo: Repository, hashOrBranchOrTag: String): Ret<Commit?> {
            val funName ="resolveCommitByHashOrRef"
            try {
                //先直接解析hash
                var c =  resolveCommitByHash(repo, hashOrBranchOrTag)
                if(c==null) {  //解析引用（分支或tag）
                    c = resolveCommitByRef(repo, hashOrBranchOrTag)

                    //解析失败，目标 不是有效hash也不是有效分支名或tag名
                    if(c==null) {
                        return Ret.createError(null, "resolve commit failed!")
                    }
                }

                //解析出commit且不为null，返回成功
                return Ret.createSuccess(c)

            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName() error, params are (hashOrBranchOrTag=$hashOrBranchOrTag}),\nerr is:"+e.stackTraceToString())
                return Ret.createError(null, e.localizedMessage ?:"resolve commit err: param=$hashOrBranchOrTag", exception = e)
            }
        }

        fun checkoutFiles(repo: Repository, targetCommitHash:String, pathSpecs: List<String>, force: Boolean, checkoutOptions: Checkout.Options?=null):Ret<Unit?> {
            val funName = "checkoutFiles"
            try {
                val targetTree = resolveTree(repo, targetCommitHash) ?: return Ret.createError(null, "resolve target tree failed")

                //设置checkout选项
                val checkoutOptions = if(checkoutOptions==null) {
                    val opts = Checkout.Options.defaultOptions()
                    opts.strategy = if(force) {
                        EnumSet.of(Checkout.StrategyT.FORCE, Checkout.StrategyT.DISABLE_PATHSPEC_MATCH)
                    }else {
                        EnumSet.of(Checkout.StrategyT.SAFE, Checkout.StrategyT.DISABLE_PATHSPEC_MATCH)
                    }

                    if(pathSpecs.isNotEmpty()) {
                        opts.setPaths(pathSpecs.toTypedArray())
                    }

                    opts
                }else {
                    checkoutOptions
                }

                Checkout.tree(repo, targetTree, checkoutOptions)

                return Ret.createSuccess(null)

            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
                return Ret.createError(null, e.localizedMessage ?: "checkout files err", exception = e)
            }

        }

        /**
         * @return "(2/6)", if err, return empty string
         */
        fun rebaseCurOfAllFormatted(repo:Repository, prefix: String="(", split:String="/", suffix:String=")"):String {
            try {
                if(repo.state() != Repository.StateT.REBASE_MERGE) {
                    throw RuntimeException("repo state is not REBASE")
                }
                val rebase = Rebase.open(repo, null)  //这里不执行next或其他具体操作，仅查计数，所以rebaseOption传null即可

                //eg: "(2/6)"
                return prefix+(rebase.operationCurrent()+1)+split+rebase.operationEntrycount()+suffix
            }catch (e:Exception) {
                return ""
            }
        }

    }

    object CommitUtil{
        //输入两个提交号后用此方法检测，若一样则提示用户且不进行diff
        fun isSameCommitHash(h1:String, h2:String):Boolean {
            return h1.startsWith(h2) || h2.startsWith(h1)
        }

        fun isLocalCommitHash(c:String):Boolean {
            return c == Cons.gitLocalWorktreeCommitHash
        }

        /**
         * 未检查是否全16进制字符，所以即使返回真也不一定是有效commit，但如果返回假，一定不是有效commit
         */
        fun mayGoodCommitHash(c:String):Boolean {
            return c.isNotBlank() && c != Cons.allZeroOidStr
        }
    }

    object ShallowManage{
        val originShallow = "shallow"  //原始shallow文件名
        val bak1 = "shallow.1.bak"  //用在代码里恢复的shallow文件
        val bak2 = "shallow.2.bak"  //可手动用于恢复的shallow文件，主要是我用来测试的

        fun createShallowBak(repoDotGitDir:String) {
            val originShallowFile = File(repoDotGitDir+"${File.separator}$originShallow")  // repo/.git/shallow

            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(repoDotGitDir).canonicalPath
            val bak1File = File(puppyGitDirUnderGitCanonicalPath+"${File.separator}$bak1")  // repo/.git/puppygit目录/shallow.1.bak
            val bak2File = File(puppyGitDirUnderGitCanonicalPath+"${File.separator}$bak2")  // repo/.git/puppygit目录/shallow.2.bak
            originShallowFile.copyTo(bak1File, overwrite = true)
            originShallowFile.copyTo(bak2File, overwrite = true)
        }

        fun restoreShallowFile(repoDotGitDir:String) {
            val originShallowFile = File(repoDotGitDir+"${File.separator}$originShallow")

            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(repoDotGitDir).canonicalPath
            val bak1File = File(puppyGitDirUnderGitCanonicalPath+"${File.separator}$bak1")

            //如果bak1存在，用它恢复shallow文件；如果不存在，用bak2恢复bak1和shallow文件
            if(bak1File.exists()) {
                bak1File.copyTo(originShallowFile, overwrite = true)
            }else {  //bak1不存在，用bak2恢复bak1和原始shallow文件
                val bak2File = File(puppyGitDirUnderGitCanonicalPath+"${File.separator}$bak2")
                bak2File.copyTo(bak1File, overwrite = true)
                bak2File.copyTo(originShallowFile, overwrite = true)
            }
        }

        fun deleteBak1(repoDotGitDir:String) {
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(repoDotGitDir).canonicalPath

            val bak1File = File(puppyGitDirUnderGitCanonicalPath+"${File.separator}$bak1")
            if(bak1File.exists()) {
                bak1File.delete()
            }
            //另一种写法
//            bak1File.apply {  //绑定作用域为bak1File，作用域内this即为bak1File
//                if(exists()) {
//                    delete()
//                }
//            }
        }

        //在提交页面显示仓库是否grafted的时候用到这个方法
        fun getShallowOidList(repoDotGitDir: String):List<String> {
            val originShallowFile = File(repoDotGitDir+"${File.separator}$originShallow")  // repo/.git/shallow
            if(!originShallowFile.exists()) {
                return emptyList()
            }
            //返回原始文件中的非空行
            return originShallowFile.readLines().filter { it.isNotBlank() }
        }
    }

    object RebaseHelper {
        //rebase init完毕但还没执行 next，这时过去current operation index 会获得-1
        private const val rebaseInitedButNeverNextYetValue = -1

        private const val detachedHead = "detached HEAD"
        private val fileName_beforeBranch = "rebase_before_branch"

        /**
         * 如果创建rebase时传的Annotated是fromRef创建的则不需要用此函数获取分支名
         */
        fun saveRepoCurBranchNameOrDetached(repo:Repository) {
            val name = if(repo.headDetached()) detachedHead else repo.head()?.name()
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(Libgit2Helper.getRepoGitDirEndsWithSlash(repo)).canonicalPath
            val file = File(puppyGitDirUnderGitCanonicalPath, fileName_beforeBranch)
            if(!file.exists()) {
                file.createNewFile()
            }
            file.bufferedWriter().use {
                it.write(name)
            }
        }

        /**
         * 获取启动rebase之前的分支名
         */
        fun getRebaseBeforeName(repo:Repository):String? {
            val puppyGitDirUnderGitCanonicalPath = AppModel.PuppyGitUnderGitDirManager.getDir(Libgit2Helper.getRepoGitDirEndsWithSlash(repo)).canonicalPath
            val file = File(puppyGitDirUnderGitCanonicalPath, fileName_beforeBranch)
            if(!file.exists()) {
                return null
            }

            file.bufferedReader().use {
                val ret = try {
                    it.readLine()  //取出之前的分支名
                }catch (_:Exception){
                    null
                }
                return ret
            }
        }
    }
}