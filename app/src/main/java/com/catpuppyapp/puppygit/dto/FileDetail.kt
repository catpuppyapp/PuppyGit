package com.catpuppyapp.puppygit.dto

import com.catpuppyapp.puppygit.screen.shared.FuckSafFile
import com.catpuppyapp.puppygit.utils.FsUtils

data class FileDetail(
    val file: FuckSafFile,
    val shortContent:String = "",
) {
    private var cached_appRelatedPath:String? = null
    fun cachedAppRelatedPath() = FsUtils.getPathWithInternalOrExternalPrefixAndRemoveFileNameAndEndSlash(file.path.ioPath, file.name).let { cached_appRelatedPath = it; it }
}
