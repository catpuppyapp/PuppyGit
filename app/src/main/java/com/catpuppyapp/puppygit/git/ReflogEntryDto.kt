package com.catpuppyapp.puppygit.git

import com.catpuppyapp.puppygit.dto.ItemKey
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.github.git24j.core.Oid

data class ReflogEntryDto(
    var username:String="",
    var email:String="",
    var date:String="",
    var actuallyUsingTimeZoneOffsetInMinutes:Int=0,
    var originTimeZoneOffsetInMinutes:Int=0,
    var idNew: Oid?=null,
    var idOld:Oid?=null,
    var msg:String=""
) : ItemKey {

    private var shortNewIdCached:String? = null
    private var shortOldIdCached:String? = null

    private var cached_OneLineMsg:String? = null
    fun getCachedOneLineMsg(): String = (cached_OneLineMsg ?: Libgit2Helper.zipOneLineMsg(msg).let { cached_OneLineMsg = it; it });


    override fun getItemKey():String {
        return username+email+date+idNew+idOld+msg
    }

    fun getShortNewId():String {
        if(shortNewIdCached == null) {
            shortNewIdCached = Libgit2Helper.getShortOidStrByFull(idNew.toString())
        }

        return shortNewIdCached ?: ""
    }

    fun getShortOldId():String {
        if(shortOldIdCached == null) {
            shortOldIdCached = Libgit2Helper.getShortOidStrByFull(idOld.toString())
        }

        return shortOldIdCached ?: ""
    }
}
