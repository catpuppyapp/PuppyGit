package com.catpuppyapp.puppygit.utils.fileopenhistory

import com.catpuppyapp.puppygit.settings.FileEditedPos
import kotlinx.serialization.Serializable

/**
 * remember file opened history
 */
@Serializable
data class FileOpenHistory (
    var storage:MutableMap<String, FileEditedPos> = mutableMapOf(),

)
