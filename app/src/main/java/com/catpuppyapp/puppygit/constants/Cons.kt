package com.catpuppyapp.puppygit.constants

import com.github.git24j.core.Oid
import kotlinx.coroutines.sync.Mutex
import java.time.format.DateTimeFormatter

object Cons {

    // for avoid the target item at the very top of screen when scroll to it
    const val scrollToItemOffset = -2

    const val pushDelayCheckFrquencyInMillSec = 2000L

    const val repoBusyStr = "Repo busy now, plz try again later." ;
    /**
     * 详情列表，条目分割线
     */
    const val itemDetailSpliter = "\n\n--------------\n\n"

    //一个字符（但不确定是否一个字节），三个点的省略号
    const val oneChar3dots = "…"

    const val isShallowStr = "IsShallow"
    const val notShallowStr = "NotShallow"
    const val httpStr = "Http"
    const val sshStr = "Ssh"
    const val flagStr =  "Flag"




    const val init_last_clicked_item_key =  "nexist_last_clicked_item_key_1899_1253RBCZ" // this is an item key, which never exist, just used for none matched with last clicked item when init value not changed
    const val ChangeListNaviTarget_InitValue = "cl_navi_init_value"
    const val ChangeListNaviTarget_NoNeedReload = "cl_navi_no_need_reload"  //导航到这些页面(Difference/Editor)再返回不需要刷新页面(navi to these pages(Difference/Editor) then navi back, no need reload)

    // use as path separator
    const val slash = "/"
    const val slashChar = '/'
    const val lineBreak = "\n"
    const val lineBreakChar = '\n'
    const val arrowToRight = ">"

    //        @Deprecated("改用：StorageDirCons.DefaultStorageDir.allRepoParentsDir.name")  //20240527：禁用，sd相关
    const val defaultAllRepoParentDirName = "PuppyGitRepos"
    //        @Deprecated("改用：StorageDirCons.DefaultStorageDir.puppyGitDataDir.name")  //20240527：禁用，sd相关
    const val defalutPuppyGitDataUnderAllReposDirName = "PuppyGit-Data"

    const val defaultFileSnapshotDirName = "FileSnapshot"
    const val defaultEditCacheDirName = "EditCache"  //编辑缓存目录，存储编辑文件时的content缓存文件的目录，用于在app突然崩溃，或者手机突然没电，但没保存手动保存文件时恢复内容，实现机制就是某行内容一update就自动保存
    const val defaultLogDirName = "Log"
    const val defaultSubmoduleDotGitFileBakDirName = "SmGitBak"  // backup submodule's .git file, Sm=Submodule Git=git file
    const val defaultPatchDirName = "Patch"
    const val defaultSettingsDirName = "Settings"


    //废弃，直接由 /storage/emulated/0/Android/data/app包名/files目录作为内部存储，那样兼容旧版，更方便
    // 这个是在 files 目录下再建个目录作为顶级目录，确实更整洁，但问题在于不兼容旧版，所以废弃
    //        const val defaultInternalStorageDirsParentDirName = "InternalStorageDir"  //20240527：禁用，sd相关

    //DateTimeFormatter是线程安全的
    val defaultDateTimeFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTimeFormatterCompact:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val dateTimeFormatter_yyyyMMdd:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val dateTimeFormatter_yyyyMMddHHmm:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

    const val git_AllZeroOidStr = "0000000000000000000000000000000000000000"  //40个0
    val git_AllZeroOid: Oid = Oid.of(git_AllZeroOidStr)  //40个0

    // special commit hash, 'LOCAL' and 'INDEX' reserved by PuppyGit , 'HEAD' reserved by Git, don't name your branch/tag to these
    const val ppgitReservedHashPrefix = "PPGIT_RESERVED_HASH:"
    const val git_LocalWorktreeCommitHash = "${ppgitReservedHashPrefix}LOCAL"  //represent `worktree`
    const val git_IndexCommitHash = "${ppgitReservedHashPrefix}INDEX"  //represent `index`
    const val git_HeadCommitHash = "HEAD"  //represent `HEAD`, git reserved keyword

    const val git_DotGitModules = ".gitmodules"

    //        const val defaultPageCount=50  //默认每页条目数

    //const的作用是定义编译时常量，可能会内联优化之类的
    const val selectedItem_Never = -1 //这个变量永远不会是这个值，这个值只是为了在页面里配合关联的状态变量实现永真判断的，目前20240411用在切换页面更新配置文件中记住的页面值上
    const val selectedItem_Repos = 1;
    const val selectedItem_Files = 2;
    const val selectedItem_Editor = 3;
    const val selectedItem_ChangeList = 4;
    const val selectedItem_Settings = 5;
    const val selectedItem_About = 6;
    const val selectedItem_Subscription = 7;
    const val selectedItem_Exit = 8;  //仅遵循旧代码规范而加的这个变量，实际上点这个直接退出，不会切换页面，也不会记住最后退出页面
    const val selectedItem_Service = 9
    const val selectedItem_Automation = 10  //自动化


    // get app dir err
    const val errorCantGetExternalFilesDir = "Err: Can't get External files Dir"
    const val errorCantGetExternalCacheDir = "Err: Can't get External cache Dir"
    const val errorCantGetInnerDataDir = "Err: Can't get Inner data Dir"
    const val errorCantGetInnerCacheDir = "Err: Can't get Inner cache dir"
    const val errorCantGetInnerFilesDir = "Err: Can't get Inner files dir"

    //尝试创建文件夹来检测仓库名称是否合法，统一加这个前缀
    const val createDirTestNamePrefix = "test-create_"  //含义为测试创建

    const val nav_HomeScreen = "home"
    const val nav_CredentialManagerScreen = "credentialman"
    const val nav_CommitListScreen = "commitlist"
    const val nav_ErrorListScreen = "errlist"
    const val nav_CloneScreen = "clone"
    const val nav_DiffScreen = "diff"
    const val nav_IndexScreen = "index"
    const val nav_CredentialNewOrEditScreen = "credential_new_or_edit"
    const val nav_CredentialRemoteListScreen = "credential_remote_list"
    const val nav_BranchListScreen = "branchlist"
    const val nav_TreeToTreeChangeListScreen = "tree_to_tree_changelist"
    const val nav_RemoteListScreen = "remote_list"
    const val nav_SubPageEditor = "subpage_editor"
    const val nav_TagListScreen = "taglist"
    const val nav_ReflogListScreen = "refloglist"
    const val nav_StashListScreen = "stashlist"
    const val nav_SubmoduleListScreen = "submodulelist"
    const val nav_DomainCredentialListScreen = "domain_credential_list"
    const val nav_FileHistoryScreen = "file_history"
    const val nav_FileChooserScreen = "file_chooser"




    const val gitUrlTypeHttp = 1
    const val gitUrlTypeSsh = 2
    //        const val gitUrlTypeInvalid = dbCommonErrValStart+1
    const val gitUrlHttpStartStr = "http://"
    const val gitUrlHttpsStartStr = "https://"
    //        const val gitUrlSshStartStr = "git@"  //不对，git@ ， 其中git是用户名，而用户名完全可以叫别的，所以这样判断不准

    const val gitAllTagsRefspecForFetchAndPush = "refs/tags/*:refs/tags/*"
    const val gitDelAllTagsRefspecForFetchAndPush = ":refs/tags/*"



    //db相关常量开始

    // 20241230废弃，改成统一使用UTC秒数了，显示时会获取App指定时区或系统时区，无需此变量了
//    val dbUsedTimeZoneOffset: ZoneOffset = ZoneOffset.UTC
    //值大于等于50表示错误或者其他不正常的情况，例如仓库正在克隆，url类型无效，之类的，总之只要值大于等于50，就有问题
    const val dbCommonErrValStart = 50

    //一个无效的非空id。应用场景：有时候页面根据导航url中的id是否为空来决定是否启用某些功能，但url中如果传空字符串会出现"//"这样的字符串导致导航出错，可以用缓存key解决，但那样还要查缓存或者检查key是否有效，不如直接创建一个无效的导航id
    //为避免导航出错，这里不应该有 / 之类的字符
    const val dbInvalidNonEmptyId = "xyz"  //无效id的长度比默认生成的随机id短，且不包含会导致url解析出错的字符，就够了，没必要弄很长

    //代表布尔值
    const val dbCommonFalse=0;
    const val dbCommonTrue=1;

    //base开头的字段每个表都有，所以就加到Common系列变量里了
    const val dbCommonBaseStatusOk=1  //default status
    const val dbCommonBaseStatusErr=dbCommonErrValStart+1  //51

    //credential
    const val dbCredentialTypeHttp=1
    const val dbCredentialTypeSsh=2

    //repo
    //repo createType
    const val dbRepoCreateByClone=1
    // git init on local storage，
    // 若只init不导入，则不入库，亦不用记录，
    // 所以实际只需要区分clone和import两个类型
    // 这字段暂时留着
    const val dbRepoCreateByInit=2
    const val dbRepoCreateByImport=3

    //repo workStatus start
    //err
    const val dbRepoWorkStatusCloneErr=dbCommonErrValStart+3
    // init err实际上不会入库，所以不需要记录错误类型呢
    // 这字段暂时留着
    const val dbRepoWorkStatusInitErr=dbCommonErrValStart+4

    //normal
    //未就绪
    const val dbRepoWorkStatusNotReadyNeedClone=30  //正在克隆，还没结束，仓库不能用，但不算Err，所以小于错误起始值
    const val dbRepoWorkStatusNotReadyNeedInit=31
    //已就绪
    //实际只使用 needSync/hasConflict/uptodate，三个状态
    const val dbRepoWorkStatusUpToDate=1
    const val dbRepoWorkStatusNeedCommit=2
    const val dbRepoWorkStatusNeedPull=3
    const val dbRepoWorkStatusNeedPush=4
    const val dbRepoWorkStatusMerging=5  // need merge continue or abort (old name: `dbRepoWorkStatusNeedMerge`, ps: 原本是想用来在本地和远程分支不一样时提示需要merge，后来废弃此方案）
    const val dbRepoWorkStatusHasConflicts=6  //merge过后，发现有冲突，需要手动解决冲突
    const val dbRepoWorkStatusNeedSync=7  //本地和远程ahead和behind输出不都是0，需要同步
    const val dbRepoWorkStatusRebasing=8  // need rebase continue or abort
    const val dbRepoWorkStatusCherrypicking=9  // need cherrypick continue or abort
    const val dbRepoWorkStatusNoHEAD=10 //仓库有效，但没创建任何提交，这时需要创建文件并提交以使仓库正常工作，创建了仓库但没创建任何文件任何提交任何分支，就会是这个状态


    //            注意，workStatus超过50就代表错误了




    //repo workStatus end

    //fetch 分支模式 start
    const val dbRemote_Fetch_BranchMode_All=0
    @Deprecated("实际现在只有 All 和 Custom两种类型了")
    const val dbRemote_Fetch_BranchMode_SingleBranch=1  // 20240510: 这个实际上弃用了，因为CustomBranches如果仅指定1个分支，其实就是single branch了
    const val dbRemote_Fetch_BranchMode_CustomBranches=2
    //fetch 分支模式 end

    //shallow克隆状态
    //        const val dbRepoShallowStatusNeverShallow=0  //非shallow clone创建的仓库
    //        const val dbRepoShallowStatusIsShallow=1  //shallow clone的仓库且目前仍是shallow状态
    //        const val dbRepoShallowStatusUnshallowed=2  //shallow clone 然后解除了shallow状态
    //        const val dbRepoShallowStatusSetDepthButBigThanCommitCountsSoShallowOff=3  //设置了depth，但depth比实际的提交数大，所以并没有shallow clone，这种情况需要在设置了depth的仓库在克隆完后检查仓库.git目录是否存在shallow文件，如果存在则是此情况，否则是普通的shallow仓库，即dbRepoShallowStatusIsShallow状态

    //settings表相关字段
    const val dbSettingsUsedForRepo = 1
    const val dbSettingsUsedForFiles = 2
    const val dbSettingsUsedForEditor = 3
    const val dbSettingsUsedForChangeList = 4
    const val dbSettingsUsedForSettings = 5
    const val dbSettingsUsedForCommonGitConfig = 6  //公用的gitconfig文件，相当于pc上用户目录下的.gitconfig，也可以说是全局gitconfig

    //error表
    const val dbDeleteErrOverThisDay = 10  //单位天，超过这个时间error表的记录在查询error时，将被删除

    //credential
    val credentialInsertLock = Mutex()  //用来在插入数据时，避免检测是否重名会发生竞争，导致插入同名条目

    //db相关常量结束

    const val fallbackCommitMsg = "Update files"

    // git相关变量开始
    const val gitRepoStateInvalid=-1
    const val gitGlobMatchAllSign="*"
    const val gitDetachedHeadPrefix = "(Detached HEAD):"
    const val gitDetachedHead = "Detached HEAD"
    const val gitDetachHeadStr = "Detach HEAD"
    const val gitHeadStr = "HEAD"
    const val gitDefaultRemoteOrigin="origin"
    // git ref example: "refs/remotes/origin/branch"，其中 origin和branch是可变的，前面的部分基本固定不变
    const val gitDefaultRemoteStartStrPrefix = "refs/remotes/"  //和origin拆分出来是为了如果有其他remote，可以方便组合
    const val gitDefaultRemoteOriginStartStrPrefix = gitDefaultRemoteStartStrPrefix+gitDefaultRemoteOrigin+"/"  // "refs/remotes/origin/"
    const val gitRemotePlaceholder = "REMOTE_PLACEHOLDER_1935832616456155"
    const val gitBranchPlaceholder = "BRANCH_PLACEHOLDER_1618931119665177"
    //开头的加号好像可以省略，详见（页内搜"optional plus"）：https://git-scm.com/docs/git-fetch/en
    //仅可替换branch为你期望的single branch,替换后形如：+refs/heads/master:refs/remotes/origin/master
    const val gitDefaultFetchRefSpecBranchReplacer = "+refs/heads/$gitBranchPlaceholder:refs/remotes/$gitDefaultRemoteOrigin/$gitBranchPlaceholder"
    //可替换remote和branch为你期望的值
    const val gitFetchRefSpecRemoteAndBranchReplacer = "+refs/heads/$gitBranchPlaceholder:refs/remotes/$gitRemotePlaceholder/$gitBranchPlaceholder"


    //0到7，共8位，git之前默认7位，但我发现现在（20250605）有的仓库，
    // push后显示的提交号是8位，但有的仍然是7位，
    // 可能有某种检测机制决定短提交显示几位（例如：如果提交历史超过一定数量，则增加短提交位数，我没考证过）
    val gitShortCommitHashRange = IntRange(start=0, endInclusive=7)
    const val maxGitSha1HashStrLength = 40
    // [7,40] 位，16进制字符，小写字母，当然，小于7位也可能是hash，但小于7位根本没必要截断显示，所以这里不判断是否小于7位
    //主要用来在截断hash时判断一个字符串是否是hash，若是则截断，若不是（例如origin/main），则不截断
    //String.toRegex()会调用Regex()，然后调用Pattern.compile()生成Pattern实例，而Pattern实例是线程安全的，所以，toRegex()生成的对象也是线程安全的
    val gitSha1HashRegex = "^[0-9a-f]{${gitShortCommitHashRange.endInclusive},$maxGitSha1HashStrLength}$".toRegex();
    //这个也是hash regex，但限制长度为 [1,40]
    val gitSha1HashMinLen1Regex = "^[0-9a-f]{1,$maxGitSha1HashStrLength}$".toRegex();

    const val gitWellKnownSshUserName = "git"
    //git 相关变量结束

    //查询或更新git config multi var(有多个同名键的配置项) 时会用到
    const val regexMatchAll = ".*"
    const val regexMatchNothing = "\$^"  //kotlin中 $需要转义，否则会尝试匹配字符串template

    const val comma = ","

    //用来代表文件类型
    const val fileTypeFile = 0
    const val fileTypeFolder = 1

    //在这个时间内按两次返回就会退出app，单位秒
    const val pressBackDoubleTimesInThisSecWillExit = 3;

    const val disableDoublePressBackToExit = true

    // represent fetch all branches
    const val gitFetchAllBranchSign = "*"

    //git status
    const val gitStatusUnmodified = "Unmodified"  //未修改，未加载修改内容，或两个对象完全一样时，就会是这样，只能在Diff页面看到这个状态，因为cl页面不会列出未修改的文件
    const val gitStatusModified = "Modified"
    const val gitStatusNew = "New"  //most time this same as untracked
    //        const val gitStatusUntracked = "Untracked"  //untracked
    const val gitStatusRenamed = "Renamed"  //这种状态在我的app里应该不会出现，拆分成Deleted的和New的两种状态了
    const val gitStatusDeleted = "Deleted"

    //        @Deprecated("instead by 'Cons.gitStatusNew'")
    //        const val gitStatusUntracked = "Untracked"  // instead by "New"

    const val gitStatusTypechanged = "Typechanged"
    const val gitStatusConflict = "Conflict"
    // git status key，存status条目的map用的，每个key对应不同的列表
    const val gitStatusKeyIndex = "Index"
    const val gitStatusKeyWorkdir = "Workdir"
    const val gitStatusKeyConflict = "Conflict"

    //file dir or submodule, used for `StatusTypeEntrySaver`
    val gitItemTypeFile = 1;
    val gitItemTypeDir = 2;
    val gitItemTypeSubmodule = 3;
    val gitItemTypeFileStr = "File";
    val gitItemTypeDirStr = "Folder";  // use 'Folder' instead 'Dir' for distinguish with 'Dirty', else if input 'dir' as filter text, will show dirty sub and dirs
    val gitItemTypeSubmoduleStr = "Subm";

    val gitSubmoduleDirtyStr="Dirty"  // for item type submodule, when dirty, show this at ChangeList item


    // git diff from commit to commit start，实际在用的只有 indexToWorktree/headToIndex/treeToTree
    val gitDiffFromIndexToWorktree = "1";
    val gitDiffFromHeadToIndex = "2";
//    val gitDiffFromHeadToWorktree = "3";  //这个其实没有用
    val gitDiffFromTreeToTree = "4";  //例如：一个提交和另一个提交比较，就属于这个类型
    //        val gitDiffFromTreeToWorktree = "5"
    // git diff file history start
    val gitDiffFileHistoryFromTreeToLocal = "20"
    // target commit to it's previous reversion of file
    val gitDiffFileHistoryFromTreeToPrev = "21"

    //必须得用平常的文件名不会包含的那种字符
    //        val separatorReplaceStr = "\\kn16K0_ivanrof\\"


    //在stringResource中的占位符，格式："ph_随机字符串_编号"，其中ph是placeholder的缩写，编号填数字即可，需要几个占位符，就填几个字符串，一般一个就够了，所以最常用的是编号1，即 `placeholderPrefixForStrRes+1` 的值
    const val placeholderPrefixForStrRes = "ph_a3f241dc_"
    //        val placeholder1ForStringRes = placeholderPrefixForStrRes+1
    //        val placeholder2ForStringRes = placeholderPrefixForStrRes+2
    //        val placeholder3ForStringRes = placeholderPrefixForStrRes+3
    //        val placeholder4ForStringRes = placeholderPrefixForStrRes+4
    //        val placeholder5ForStringRes = placeholderPrefixForStrRes+5



    val gitConfigKeyUserName = "user.name"
    val gitConfigKeyUserEmail = "user.email"
    val gitConfigKeyHttpSslVerify = "http.sslVerify"

    val isReadyDoSyncCheckResult_NotReadyNeedSetUpstream = 1  //配置文件里没上游(upstream)需要设置一下
    val isReadyDoSyncCheckResult_ReadyDoSync = 2  //上游分支已经存在于本地
    val isReadyDoSyncCheckResult_ReadyDoPushAndRemoteRefSpecNonExists = 3  // pc git 需要带 -u 选项的情况，远程仓库不存在本地分支关联的上游分支

    //最大支持的大小需要测试下
    //        val editorFileSizeMaxLimit = 2000000L  // 2MB，在20240421之前是1mb，后来优化了保存机制，改成2mb了, instead by settings
    //        val editorFileSizeMaxLimitForHumanReadable = "2MB"  // 文件大小限制人类可读的描述方式

    // diffItem 的所有line.content总和，如果超过这个大小，就不显示了，有时候存在一个文件大小很大，但其diffcontent很少的情况，这时候其实获取diff内容并不慢，所以单独限制一个diff content大小
    //        val diffContentSizeMaxLimit = 1000000L  // 1MB  // instead by settings
    //        val diffContentSizeMaxLimitForHumanReadable = "1MB"  // 文件大小限制人类可读的描述方式


    val sizeTB = 1000000000000
    val sizeTBHumanRead = "TB"
    val sizeGB = 1000000000
    val sizeGBHumanRead = "GB"
    val sizeMB = 1000000
    val sizeMBHumanRead = "MB"
    val sizeKB = 1000
    val sizeKBHumanRead = "KB"
    val sizeBHumanRead = "B"

    /**
     * 等同于：checkout reference then update HEAD，适用本地分支
     *
     * old name: checkoutTypeLocalBranch
     */
    val checkoutType_checkoutRefThenUpdateHead = 1
    /**
     * 等同于: checkout reference then detach HEAD，适用: tag/branch等引用类型
     * old name: checkoutTypeRemoteBranch
     */
    val checkoutType_checkoutRefThenDetachHead=2

    /**
     * 等同于：checkout commit then detach HEAD，实际上适用任何类型，只要peel出commit即可，但有缺陷，不知道checkout的是哪个分支或tag之类的，只知道分支号，所以如果知道引用还是尽量用引用好
     * old name: checkoutTypeCommit
     */
    val checkoutType_checkoutCommitThenDetachHead=3

    val zero000Ip = "0.0.0.0"
    val localHostIp = "127.0.0.1"
}
