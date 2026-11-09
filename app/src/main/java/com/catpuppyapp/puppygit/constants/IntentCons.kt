package com.catpuppyapp.puppygit.constants

import com.catpuppyapp.puppygit.utils.AppModel

object IntentCons {
    object ExtrasKey {
        const val startPage = "startPage"
        const val startRepoId = "startRepoId"
        const val newState = "newState"
        const val errMsg = "errMsg"

        const val filePath = "filePath"
        const val fileName = "fileName"
        const val lineNum = "lineNum"

        const val editorPayload = "editorPayload"
    }

    object Action {
        val UPDATE_TILE = genActName("UPDATE_TILE")
        val SHOW_ERR_MSG = genActName("SHOW_ERR_MSG")
        val OPEN_FILE = genActName("OPEN_FILE")
    }
}

private fun genActName(act:String):String {
    //格式："包名.action"
    return "${AppModel.appPackageName}.$act"
}
