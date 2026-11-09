package com.catpuppyapp.puppygit.data.entity

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.StorageDirCons
import com.catpuppyapp.puppygit.data.entity.common.BaseFields
import com.catpuppyapp.puppygit.etc.RepoPendingTask
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.getSecFromTime
import com.catpuppyapp.puppygit.utils.getShortTimeIfPossible
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.github.git24j.core.Repository

private const val TAG = "RepoEntity"

@Entity(tableName = "repo")
data class RepoEntity(
    @PrimaryKey
    var id: String = getShortUUID(),

//    var testMigra:String="",

    // repo push or pull etc time
    /**
     * usually repo name is not blank, but maybe is blank, no enforce check, e.g. if you import a empty name folder as repo, then will create
     *  a RepoEntity with blank name
     */
    var repoName: String = "",  //repo name and repo dir name，字段需唯一
    var fullSavePath:String="",  //repo full save path, include all repo save path and repo name, such as, allRepoSaveLocation/thisRepoName

    //每次修改仓库remote后都要更新下这几个值
    //这几个字段没什么意义了，之前对git理解错误，以为具体要用哪个remote和仓库关联，其实具体使用哪个remote取决于当前本地分支关联了哪个远程分支，远程分支属于哪个remote，pull和push时就会使用哪个remote，不过也可以把pull和push改成使用不同remote，就有点恶心了，暂时不考虑这种情况
    var pullRemoteName: String = "",
    var pullRemoteUrl: String = "",
    var pushRemoteName: String = "",
    var pushRemoteUrl: String = "",

    var lastUpdateTime: Long = getSecFromTime(),  //本来是最后更新仓库workStatus状态的时间，后来改成最后检查更新的时间了，只有fetchpullpush这类和服务器通信更新或检查更新的操作才会更新这个时间，所以实际上这个字段成了最后与服务器通信的时间
    var workStatus: Int = Cons.dbRepoWorkStatusNotReadyNeedClone,

    //分支短名
    var branch: String = "",  //如果仓库没detached HEAD，这个是分支名，如果detached，这个是短commit hash，可以通过git24j的相关函数判断仓库是否处于detached HEAD状态，或者在detached时直接使用 lastCommitHash 的值也行
    var lastCommitHash: String = "",  //最后提交的完整hash
    var isDetached:Int=Cons.dbCommonFalse,
    var upstreamBranch:String="",  // eg: origin/main

    // error relation
    @Deprecated("建议直接检查latestUncheckedErrMsg是否为Blank来判断是否有错误信息")
    var hasUncheckedErr: Int = Cons.dbCommonFalse,  //这个变量其实没什么意义，直接检查latestUncheckedErrMsg是否为空或空白不就知道是否有错误了？不为空就有错，为空就没错，不就行了？为什么还要加这个变量？
    var latestUncheckedErrMsg: String = "",

    //克隆时关联的credentialId，克隆完成后就没用了，到时候会根据不同的remote使用不同的credentialId，这个id在克隆结束后被会设置为 默认remote(origin) 的credentialId
    var credentialIdForClone: String="",
    var cloneUrl:String="",

    //仓库默认状态为 active，可通过仓库设置页面(不是公用仓库选项，是仓库私有的选项)设置为 inactive，如果是inactive，将不会自动检查仓库状态，在changelist也不能查看仓库状态(在changelist切换仓库，没这个仓库)
    @Deprecated("实际上没用到这个字段")
    var isActive: Int=Cons.dbCommonTrue,

    // 日后如果想区分克隆的仓库和从本地导入的仓库，会用到这字段
    var createBy:Int = Cons.dbRepoCreateByClone,

    //是否开启递归克隆
    var isRecursiveCloneOn:Int=Cons.dbCommonFalse,

    //克隆或init时发生错误，存到这个字段，本字段无历史记录，每次发生错误都会更新此字段值，只有当workStatus为cloneErr或initErr时，才会用到此字段，一旦克隆或init成功，此字段应被清空
    var createErrMsg:String="",

    var depth:Int=0, //shallow clone

    //是否是shallowClone，如果是，则可以执行unshallow操作，具体到代码就是在fetch_opts加个unshallow然后执行fetch，操作成功后把这个值设为假。
//    var shallowStatus:Int=Cons.dbRepoShallowStatusNeverShallow,

    //仓库是否处于shallow模式，本值需要在clone完仓库后查询仓库是否shallow来得到精确的值，不能仅凭depth是否为0来判断，因为有可能depth>0同时depth>仓库提交历史记录数，那样的话，即使depth>0仓库也不会处于shallow模式
    var isShallow:Int=Cons.dbCommonFalse,

    //在克隆页面只有设置了branch才会判断此值对应的勾选框是否勾选，否则一律按false处理。不设置branch也能实现singleBranch，但有点麻烦且感觉用处不大，所以我没实现。
    //这个值用于创建仓库时初始化remote的singlebranch状态，之后就没用了，因为singlebranch是针对remote的，不是针对仓库的
    var isSingleBranch:Int=Cons.dbCommonFalse,

    // parentRepoId，若是submodule或submodule的submodule...以此类推，则此值id为其父repo id
    var parentRepoId:String="",

    //当前分支是领先于上游还是落后于上游，仅在非detached HEAD状态下此值有意义
    var ahead:Int=0,
    var behind:Int=0,

    //storageDirId,若不指定，用内部默认目录，若指定，可是内部也可是外部，取决于你指定哪个
    @Deprecated("已废弃")
    var storageDirId:String=StorageDirCons.DefaultStorageDir.puppyGitRepos.id,

    @Embedded
    var baseFields: BaseFields=BaseFields(),

    //临时状态，仓库在repo list页面执行操作时会设置此状态，例如：fetching, pushing等，不往数据库存，且每次从db查数据后会更新此值，在卡片显示时需要用到此变量，另外，若此变量不为空，代表仓库正在执行某些操作，隐含仓库有效（没删除，状态正常）
//    @IgnoredOnParcel
//    @Ignore
    //这个entity和parcelable有冲突，最省事的解决方案就是不用Ignore字段注解但在写入数据库时把这个字段设为空。要不然就得手动实现parcelable或者单独弄个{repoid:tmpStatus}的StateMap，然后传参给需要的地方
    var tmpStatus: String=""

) {
    //忽略的字段应放构造器外面
    @Ignore
    var gitRepoState:Repository.StateT? = null

    @Ignore
    var parentRepoName:String=""

    /**
     * indicate parentRepoId related Repo is valid or is not
     *
     * this field only correct after you updated parent repo info for a repo entity
     * 这个字段仅在更新了父仓库信息后才有效，如果只查当前仓库信息，但没检查parentRepoId是否关联有效仓库，此值可能不准确
     */
    @Ignore
    var parentRepoValid:Boolean=false


    @Ignore
    var otherText:String?=null

    /**
     * 有些操作重量级又没必要立刻执行，先挂起，谁爱执行谁执行
     *
     * 例如：更新仓库信息的时候需要检查是否有未提交修改，但这个操作有点重量级，就可先挂起，然后在获取列表之后，开个协程单独处理，每个任务应该只消费一次，消费完就设为NONE
     */
    @Ignore
    var pendingTask:RepoPendingTask=RepoPendingTask.NONE

    @Ignore
    var lastCommitHashShort:String? = null  //最后提交hash，短
        private set
        get() {
            val tmp =  field
            if(tmp != null && lastCommitHash.startsWith(tmp)) {
                return tmp
            }

            return Libgit2Helper.getShortOidStrByFull(lastCommitHash).let { field = it; it }
        }

    @Ignore
    var lastCommitDateTime:String=""
        private set

    // this filed no need set in copy, will re-generate and cache the value at first time getting
    @Ignore
    private var lastUpdateTimeFormattedCached:String? = null


    @Ignore
    private var cached_OneLineLastestUnCheckedErrMsg:String? = null
    fun getCachedOneLineLatestUnCheckedErrMsg(): String = (cached_OneLineLastestUnCheckedErrMsg ?: Libgit2Helper.zipOneLineMsg(latestUncheckedErrMsg).let { cached_OneLineLastestUnCheckedErrMsg = it; it });

    /**
     * 拷贝所有字段，包括不在data class构造器的字段
     */
    fun copyAllFields(
        settings: AppSettings,
        newInstance: RepoEntity = copy(),
    ):RepoEntity {
        newInstance.gitRepoState = gitRepoState
        newInstance.parentRepoName = parentRepoName
        newInstance.parentRepoValid = parentRepoValid
        newInstance.otherText = otherText
        newInstance.pendingTask = pendingTask
        newInstance.lastCommitDateTime = lastCommitDateTime
        newInstance.latestCommitMsg = latestCommitMsg

        updateLastCommitHashShort()
        updateCommitDateTime(settings)

        return newInstance
    }

    fun hasOther():Boolean {
        return dbIntToBool(isShallow)
    }

    fun getOther(): String {
        if(otherText == null) {
            // for better filterable, these text should not localize
            otherText = if(dbIntToBool(isShallow)) Cons.isShallowStr else Cons.notShallowStr
        }

        return otherText ?: ""
    }

    fun equalsForSelected(other:RepoEntity):Boolean {
        return id == other.id
    }

    fun getRepoStateStr(context: Context): String {
        return Libgit2Helper.getRepoStateStr(gitRepoState, context)
    }

    fun updateLastCommitHashShort() {
        lastCommitHashShort = Libgit2Helper.getShortOidStrByFull(lastCommitHash)
    }

    // update date time of commit
    fun updateCommitDateTime(settings: AppSettings) {
        try {
            Repository.open(fullSavePath).use { repo ->
                updateCommitDateTimeWithRepo(repo, settings)
            }
        }catch (e: Exception) {
            MyLog.e(TAG, "#updateCommitDateTime: resolve commit hash failed! hash=$lastCommitHash, err=${e.localizedMessage}")
        }
    }

    fun updateCommitDateTimeWithRepo(repo: Repository, settings: AppSettings) {
        lastCommitDateTime = try {
            getShortTimeIfPossible(Libgit2Helper.getSingleCommitSimple(repo, repoId = id, commitOidStr = lastCommitHash, settings).dateTime)
        }catch (e: Exception) {
            MyLog.e(TAG, "#updateCommitDateTimeWithRepo: resolve commit hash failed! hash=$lastCommitHash, err=${e.localizedMessage}")
            ""
        }
    }

    fun cachedLastUpdateTime():String {
        return lastUpdateTimeFormattedCached ?: getShortTimeIfPossible(lastUpdateTime).let { lastUpdateTimeFormattedCached = it; it }
    }

    fun createErrMsgForView(context: Context):String {
        return if(createErrMsg.isEmpty()) {
            ""
        }else {
            context.getString(R.string.error) + ": " + createErrMsg
        }
    }

    @Ignore
    private var cached_appRelatedPath:String? = null
    fun cachedAppRelatedPath() = cached_appRelatedPath ?: FsUtils.getPathWithInternalOrExternalPrefix(fullPath = fullSavePath).let { cached_appRelatedPath = it; it }

    @Ignore
    var latestCommitMsg = ""
    @Ignore
    private var cached_oneLineLatestCommitMsg:String? = null
    fun getOrUpdateCachedOneLineLatestCommitMsg() = cached_oneLineLatestCommitMsg ?: Libgit2Helper.zipOneLineMsg(latestCommitMsg).let { cached_oneLineLatestCommitMsg = it; it }

}
