package com.catpuppyapp.puppygit.utils.markdown

import android.content.Context
import coil.request.Disposable
import coil.request.ImageRequest
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.RegexUtil
import dev.jeziellago.compose.markdowntext.plugins.image.ImagesPlugin
import dev.jeziellago.compose.markdowntext.plugins.image.ImagesPlugin.CoilStore
import io.noties.markwon.image.AsyncDrawable

object MdUtil {
    /**
     * 支持加载相对路径的图片
     */
    fun getCoilStore(context: Context, basePathNoEndSlash:String):ImagesPlugin.CoilStore {
        return object : CoilStore {
            override fun load(drawable: AsyncDrawable): ImageRequest {
                val path = drawable.destination.let {
                    FsUtils.getAbsolutePathIfIsRelative(path = it, basePathNoEndSlash = basePathNoEndSlash)
                }

                return ImageRequest.Builder(context)
                    .data(path)
                    .build()
            }

            override fun cancel(disposable: Disposable) {
                disposable.dispose()
            }
        }
    }

    fun maybeIsMarkdownFile(name: String): Boolean {
        return RegexUtil.matchWildcard(name, "*.md")
                || RegexUtil.matchWildcard(name, "*.mdown")
                || RegexUtil.matchWildcard(name, "*.markdown")
    }

}
