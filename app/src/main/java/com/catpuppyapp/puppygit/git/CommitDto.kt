package com.catpuppyapp.puppygit.git

import android.content.Context
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.formatMinutesToUtc
import com.catpuppyapp.puppygit.utils.readTimeZoneOffsetInMinutesFromSettingsOrDefault

class CommitDto (
    var oidStr: String="",
    var shortOidStr: String="",
    var branchShortNameList: MutableList<String> = mutableListOf(),  //分支名列表，master origin/master 之类的，能通过看这个判断出是否把分支推送到远程了
    var parentOidStrList: MutableList<String> = mutableListOf(),  //父提交id列表，需要的时候可以根据这个oid取出父提交，然后可以取出父提交的树，用来diff
    var parentShortOidStrList: MutableList<String> = mutableListOf(),  //父提交短id列表
    var dateTime: String="",
    var originTimeOffsetInMinutes:Int=0,
    var author: String="",  // username
    var email: String="",
    var committerUsername:String="",
    var committerEmail:String="",
    var shortMsg:String="", //只包含第一行
    var msg: String="",  //完整commit信息
    var repoId:String="",  //数据库的repoId，用来判断当前是在操作哪个仓库
    var treeOidStr:String="",  //提交树的oid和commit的oid不一样哦
    var isGrafted:Boolean=false,  // shallow root，此值为true，参考pcgit，在打印shallow仓库时，会对shallow root添加grafted标识，不过需要注意，一个提交列表可能有多个isGrafted，这种情况发生于多个grafted是同一个提交的父提交的情况，换句话说，那个子提交是由多个父提交合并来的
    var tagShortNameList:MutableList<String> = mutableListOf()
) {

    private var otherMsg:String?=null
    private var otherMsgSearchableText:String?=null

    fun hasOther():Boolean {
        return isGrafted || isMerged()
    }

    fun getOther(activityContext: Context, searchable:Boolean):String {
        val noCache = if(searchable) {
            otherMsgSearchableText == null
        }else {
            otherMsg == null
        }

        if(noCache) {
            val sb = StringBuilder()
            val suffix = ", "

            sb.append(if(isMerged()) {
                if(searchable) {
                    CommitDtoSearchableText.isMerged
                }else {
                    activityContext.getString(R.string.is_merged)
                }
            } else {
                if(searchable) {
                    CommitDtoSearchableText.notMerged
                }else {
                    activityContext.getString(R.string.not_merged)
                }
            }).append(suffix)
            sb.append(if(isGrafted) {
                if(searchable) {
                    CommitDtoSearchableText.isGrafted
                }else {
                    activityContext.getString(R.string.is_grafted)
                }
            } else {
                if(searchable) {
                    CommitDtoSearchableText.notGrafted
                }else {
                    activityContext.getString(R.string.not_grafted)
                }
            }).append(suffix)

            val text = sb.toString().removeSuffix(suffix)
            if(searchable) {
                otherMsgSearchableText = text
            }else {
                otherMsg = text
            }
        }

        return (if(searchable) otherMsgSearchableText else otherMsg) ?: ""
    }

    fun isMerged():Boolean {
        return parentOidStrList.size>1
    }

    fun authorAndCommitterAreSame():Boolean {
        return author==committerUsername && email==committerEmail
    }

    fun getActuallyUsingTimeZoneUtcFormat(settings: AppSettings): String {
        val minuteOffset = readTimeZoneOffsetInMinutesFromSettingsOrDefault(settings, originTimeOffsetInMinutes)

        return formatMinutesToUtc(minuteOffset)
    }

}

private object CommitDtoSearchableText {
    const val isMerged = "IsMerged"
    const val notMerged = "NotMerged"
    const val isGrafted = "IsGrafted"
    const val notGrafted = "NotGrafted"
}
