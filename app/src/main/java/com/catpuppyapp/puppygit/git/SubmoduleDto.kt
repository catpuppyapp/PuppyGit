package com.catpuppyapp.puppygit.git

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.github.git24j.core.Repository
import com.github.git24j.core.Submodule

data class SubmoduleDto (
    val name:String,
    val remoteUrl:String,
    val relativePathUnderParent:String,
    val fullPath:String,
    val cloned:Boolean,
    val targetHash:String,  //target commit hash recorded by parent repo

    val location:Set<Submodule.StatusT>,

    var tempStatus:String = "",  // cloning... etc

) {
    private var otherText:String? = null;
    private var cachedShortTargetHash:String? = null;

    fun getShortTargetHashCached(): String = cachedShortTargetHash ?: Libgit2Helper.getShortOidStrByFull(targetHash).let { cachedShortTargetHash = it; it }

    private fun getClonedText(activityContext:Context):String{
        return activityContext.getString(if(cloned) R.string.cloned else R.string.not_cloned)
    }

    fun getStatus(activityContext:Context):String {
        return tempStatus.ifBlank { getClonedText(activityContext) }
    }

    fun getStatusColor(): Color {
        return if(tempStatus.isNotBlank()) {
            MyStyleKt.TextColor.danger()
        }else if(cloned) {
            Color(0xFF4CAF50)
        }else {
            Color.Unspecified
        }
    }


    fun isRepoClonedAndShallow():Boolean {
        return try {
            Repository.open(fullPath).use { Libgit2Helper.isRepoShallow(it) }
        }catch (_:Exception) {
            false
        }
    }


    fun hasOther():Boolean {
        return isRepoClonedAndShallow()
    }

    fun getOther(): String {
        if(otherText == null) {
            // for better filterable, these text should not localize
            otherText = if(isRepoClonedAndShallow()) Cons.isShallowStr else Cons.notShallowStr
        }

        return otherText ?: ""
    }

}
