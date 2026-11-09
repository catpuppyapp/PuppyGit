package com.catpuppyapp.puppygit.git

import android.content.Context
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.generateRandomString
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.catpuppyapp.puppygit.utils.replaceStringResList
import com.github.git24j.core.Branch.BranchType

class BranchNameAndTypeDto {
    var isSymbolic:Boolean=false  //是否符号引用
    var symbolicTargetFullName:String=""  //符号引用全名，例如 refs/remotes/origin/abc or refs/heads/abc, 若是符号引用，值为其名，否则为空
    var symbolicTargetShortName:String=""  //符号引用短名 例如 origin/abc or abc
    var ahead:Int=0
    var behind:Int=0
    var shortName:String=""  // 本地分支：abc, 远程分支：origin/abc
    var fullName:String=""  //本地分支：refs/heads/abc，远程分支：refs/remotes/origin/abc
    var oidStr:String=""  //分支最新提交oid
    var shortOidStr:String=""  //分支最新提交oid，短
    var type:BranchType=BranchType.INVALID
    var upstream:Upstream?=null  //分支的上游。 注意：如果是符号引用，解析出的oid会是其指向的对象的oid，而且我不确定符号引用设置的上游是否有效，即使配置文件可以修改，也不确定是否实际能用，不过一般只有HEAD一个分支是符号引用，所以不同担心这个问题
    var isCurrent:Boolean=false  //是否是当前分支

    //这个值有可能为空，因为remote名和本地分支名有可能出现歧义，若此值为空，代表remote分支名存在歧义，若想删除，需要用户手动输入一个分支名
    var remotePrefixFromShortName:String=""  //当分支是远程分支时此值可能不为空，代表的是远程分支前缀，例如 origin/main中的origin，注意 远程程名有可能包含分隔符，例如 abc/def/branchname，其中abc/def是远程名，所以可能存在歧义，有歧义时，此值为空字符串。






    private var otherCached:String? = null
    private var otherSearchableTextCached:String? = null


    fun isRemoteNameAmbiguous():Boolean {  //如果返回true，说明远程分支的remote存在歧义，无法成功解析，如果执行删除远程分支等操作，需要用户指定一个分支。另外，本地分支调用此方法无意义，会百分百返回true
        return remotePrefixFromShortName.isBlank()
    }

    fun getAheadBehind(activityContext: Context, searchable: Boolean):String {
        return if(alreadyUpToDate()) {
            if(searchable) {
                BranchSearchableText.upToDate
            }else {
                activityContext.getString(R.string.uptodate)
            }
        }else {
            if(searchable){
                replaceStringResList(BranchSearchableText.ahead_n_behind_m, listOf(""+ahead, ""+behind))
            }else{
                replaceStringResList(activityContext.getString(R.string.ahead_n_behind_m), listOf(""+ahead, ""+behind))
            }
        }
    }

    fun alreadyUpToDate() = ahead == 0 && behind == 0;

    //若remote name不存在歧义，返回移除remotename后的分支名(例如输入 origin/main ，返回 main)，否则返回空字符串
    fun getBranchNameNoRemotePrefixOrEmptyStrIfAmbiguous():String {
        if(isRemoteNameAmbiguous()) {
            return ""
        }

        return Libgit2Helper.removeRemoteBranchShortRefSpecPrefixByRemoteName(remotePrefixFromShortName+"/", shortName)?:""
    }

    /**
     * 已设置上游且已发布则为真，否则假
     */
    fun isUpstreamValid():Boolean {
        //注：`upstream?.isPublished != true` 不可写成 `xxx?.xxx == false`，因为 ?. 除了布尔值还隐含了null，所以如果写成等于false，值为null时就会返回假，那就和值为true时效果一样了
        //必须 有上游 且 上游的remote不为空 且 上游的branchRefsHeadsRefSpec不为空 且 已发布（推送到远程仓库），上游才算有效
//        return !(upstream == null || upstream?.remote.isNullOrBlank() || upstream?.branchRefsHeadsFullRefSpec.isNullOrBlank() || upstream?.isPublished != true)

        return isUpstreamAlreadySet() && isPublished()
    }

    /**
     * even no publish, if already set upstream, will return true
     */
    fun isUpstreamAlreadySet():Boolean {
        return !(upstream == null || upstream?.remote.isNullOrBlank() || upstream?.branchRefsHeadsFullRefSpec.isNullOrBlank())
    }

    fun isPublished():Boolean {
        return upstream?.isPublished == true
    }

    fun getOther(activityContext:Context, searchable:Boolean):String {
        val noCache = if(searchable) {
            otherSearchableTextCached == null
        }else {
            otherCached == null
        }

        if(noCache) {
            val suffix = ", "
            val sb= StringBuilder()

            //local独有字段
            if(type == BranchType.LOCAL) {
                sb.append(if(isCurrent) {
                    if(searchable) {
                        BranchSearchableText.isCurrent
                    }else {
                        activityContext.getString(R.string.is_current)
                    }
                } else {
                    if(searchable) {
                        BranchSearchableText.notCurrent
                    }else {
                        activityContext.getString(R.string.not_current)
                    }
                }).append(suffix)

                sb.append(if(isUpstreamAlreadySet()) {
                    if(searchable) {
                        BranchSearchableText.hasUpstream
                    }else {
                        activityContext.getString(R.string.has_upstream)
                    }

                } else {
                    if(searchable) {
                        BranchSearchableText.noUpstream
                    }else {
                        activityContext.getString(R.string.no_upstream)
                    }
                }).append(suffix)

                //检查上游是否发布，没有上游一定是未发布，有上游也不一定是已发布，只有上游fetch下载的本地分支存在才当作已发布
                sb.append(if(isPublished()){
                    if(searchable) {
                        BranchSearchableText.isPublished
                    }else {
                        activityContext.getString(R.string.is_published)
                    }
                } else {
                    if(searchable) {
                        BranchSearchableText.notPublished
                    }else {
                        activityContext.getString(R.string.not_published)
                    }
                }).append(suffix)
            }

            //remote独有字段
            if(type == BranchType.REMOTE) {

            }

            // local/remote 共有
            sb.append(if(isSymbolic){
                if(searchable) {
                    BranchSearchableText.isSymbolic
                }else {
                    activityContext.getString(R.string.is_symbolic)
                }
            } else {
                if(searchable) {
                    BranchSearchableText.notSymbolic
                }else {
                    activityContext.getString(R.string.not_symbolic)
                }
            }).append(suffix)


            //如果最后一个条目不可在写代码时指定，改成sb.removeSuffix().toString，不过上面都是写死的代码，所以直接最后一个没加后缀，自然也不需要移除
            val text = sb.removeSuffix(suffix).toString()
            if(searchable) {
                otherSearchableTextCached = text
            }else {
                otherCached = text
            }
        }

        return (if(searchable) otherSearchableTextCached else otherCached) ?: ""
    }

    fun getTypeString(activityContext: Context, searchable: Boolean):String {
        return if(type == BranchType.LOCAL) {
            if(searchable) {
                BranchSearchableText.local
            }else {
                activityContext.getString(R.string.local)
            }
        } else {
            if(searchable) {
                BranchSearchableText.remote
            }else {
                activityContext.getString(R.string.remote)
            }
        }
    }

    fun getUpstreamShortName(activityContext: Context):String {
        //非local直接返回空字符串
        if(type != BranchType.LOCAL) {
            return ""
        }

        //只有local branch才有upstream

        val shortUpstreamBranchName = upstream?.remoteBranchShortRefSpec ?:""

        return shortUpstreamBranchName.ifBlank { "[" + activityContext.getString(R.string.none) + "]" }
    }

    fun getUpstreamFullName(activityContext: Context): String {

        //非local直接返回空字符串
        if(type != BranchType.LOCAL) {
            return ""
        }

        //只有local branch才有upstream

        val upstreamBranchName = upstream?.remoteBranchRefsRemotesFullRefSpec ?:""

        return upstreamBranchName.ifBlank { "[" + activityContext.getString(R.string.none) + "]" }

    }
}


private object BranchSearchableText {
    const val isCurrent = "IsCurrent"
    const val notCurrent = "NotCurrent"
    const val hasUpstream = "HasUpstream"
    const val noUpstream = "NoUpstream"
    const val isPublished = "IsPublished"
    const val notPublished = "NotPublished"
    const val isSymbolic = "IsSymbolic"
    const val notSymbolic = "NotSymbolic"
    const val local = "Local"
    const val remote = "Remote"
    const val upToDate = "UpToDate"
    const val ahead_n_behind_m = "ahead ph_a3f241dc_1, behind ph_a3f241dc_2"

}

