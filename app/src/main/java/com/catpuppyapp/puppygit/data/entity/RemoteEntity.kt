package com.catpuppyapp.puppygit.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.common.BaseFields
import com.catpuppyapp.puppygit.utils.getShortUUID

@Entity(tableName = "remote")
data class RemoteEntity(

//    注：除了主键，repoId+remoteName也是唯一的
    @PrimaryKey
    var id: String = getShortUUID(),

//    var testMigra:String="",

    // repo push or pull etc time
    var remoteName: String = "",
    var remoteUrl: String = "",  // fetch url, if not set push url in git config, is push url too

    //这两个变量其实没有意义，pull和push都是和当前分支关联的upstream的操作，每个分支关联的remote都可能不同
    var isForPull:Int= Cons.dbCommonFalse,
    var isForPush:Int=Cons.dbCommonFalse,

    var credentialId:String="",
    var repoId:String="",

    // all/singleBranch/customBranches, default all
    @Deprecated("改成动态查询git仓库的fetch refspecs了，然后根据数量来判断是自定义分支列表还是所有分支，参见：Libgit2Helper.getRemoteFetchBranchList(remote)")
    var fetchMode:Int=Cons.dbRemote_Fetch_BranchMode_All,
    //fetchMode 为 singleBranch 时使用此值
    @Deprecated("改成动态查询git仓库信息了，而且实际上现在只判断是all还是custom了，单分支仅存在于clone时，且实际实现是只包含一个分支的custom分支列表")
    var singleBranch:String="",
    //fetchMode 为 customBranches 时使用此值
    //逗号分隔分支列表，例如：main,master,dev,feature/newFunction
    @Deprecated("改成动态查询git仓库信息了")
    var customBranches:String="",  // 这个可应用到FetchOptions的refspecs参数里，不过我还没测试还没测试还没测试！

    var pushUrl:String="",  //默认为空，git config无此字段，代表使用默认的url，例如 remote.origin.url，对应数据库的 remoteUrl 字段
    var pushCredentialId:String="",  //有值，没有为空用fetchCredentialId替代的机制，强制使用此值，若为空，则代表不使用凭据

    @Embedded
    var baseFields: BaseFields = BaseFields(),

){
    /**
     * when push url empty, will use fetch url, that case should set this value to true*
     */
    @Ignore
    var pushUrlTrackFetchUrl:Boolean = false
}
