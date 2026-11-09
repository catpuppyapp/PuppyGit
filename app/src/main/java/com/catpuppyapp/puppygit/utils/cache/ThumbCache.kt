package com.catpuppyapp.puppygit.utils.cache

import androidx.compose.ui.graphics.ImageBitmap


/**
 * 缩略图cache
 */
object ThumbCache:CacheStoreImpl() {

    fun cacheIt(filePath:String, thumb: ImageBitmap) {
        set(filePath, thumb)
    }

    fun getThumb(filePath: String): ImageBitmap? {
        return getByType<ImageBitmap>(filePath)
    }

}
