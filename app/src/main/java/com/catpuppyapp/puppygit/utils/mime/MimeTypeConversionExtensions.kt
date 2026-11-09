/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.catpuppyapp.puppygit.utils.mime

import android.webkit.MimeTypeMap
import com.catpuppyapp.puppygit.utils.asFileName
import com.catpuppyapp.puppygit.utils.asPathName
import java.io.File


fun MimeType.Companion.guessFromPath(path: String): MimeType {
    val fileName = path.asPathName().fileName ?: return DIRECTORY
    return guessFromExtension(fileName.asFileName().singleExtension)
}

//这个省事但是有点重量级，除非调用者手里正好有个File，可以用这个方法，否则如果用起来不麻烦的话，还是用guessFromPath比较好
fun MimeType.Companion.guessFromFile(file: File): MimeType {
    if(file.isDirectory) return DIRECTORY

    val fileName = file.name
    return guessFromExtension(fileName.asFileName().singleExtension)
}

//需要调用者自己确定入参不是目录
fun MimeType.Companion.guessFromFileName(fileName: String): MimeType {
    return guessFromExtension(fileName.asFileName().singleExtension)
}

fun MimeType.Companion.guessFromExtension(extension: String): MimeType {
    val extension = extension.lowercase()
    return extensionToMimeTypeOverrideMap[extension]
        ?: MimeTypeMap.getSingleton().getMimeTypeFromExtensionCompat(extension)?.asMimeTypeOrNull()
//        ?: TEXT_PLAIN  //如果文件类型未知，当作文本文件处理
        ?: GENERIC  //如果文件类型未知，当作常规文件处理，GENERIC 调用 intentType 会返回 */*（好像因为做了map映射？），有点反直觉，我还以为会返回字节流octet那个类型。另外，由于我有open as弹窗，所以未知类型用户可选用内部编辑器打开或外部程序打开，操作我感觉还算方便，所以废弃“无匹配mime就当作text类型”的方案
//        ?: ANY  //如果文件类型未知，当作 */* 处理，这个好像匹配不如generic多，废弃
}

// @see https://android.googlesource.com/platform/external/mime-support/+/master/mime.types
// @see https://android.googlesource.com/platform/frameworks/base/+/master/mime/java-res/android.mime.types
// @see http://www.iana.org/assignments/media-types/media-types.xhtml
// @see https://salsa.debian.org/debian/media-types/-/blob/master/mime.types
// @see /usr/share/mime/packages/freedesktop.org.xml
private val extensionToMimeTypeOverrideMap = mapOf(
    // Fixes
    "cab" to "application/vnd.ms-cab-compressed", // Was "application/x-cab"
    "csv" to "text/csv", // Was "text/comma-separated-values"
    "sh" to "application/x-sh", // Was "text/x-sh"
    "otf" to "font/otf", // Was "font/ttf"
    // Addition
    "bz" to "application/x-bzip",
    "bz2" to "application/x-bzip2",
    "z" to "application/x-compress",
    "lzma" to "application/x-lzma",
    "p7b" to "application/x-pkcs7-certificates",
    "spc" to "application/x-pkcs7-certificates", // Clashes with "chemical/x-galactic-spc"
    "p7c" to "application/pkcs7-mime",
    "p7s" to "application/pkcs7-signature",
    "ts" to "application/typescript", // Clashes with "video/mp2ts"
    "py3" to "text/x-python",
    "py3x" to "text/x-python",
    "pyx" to "text/x-python",
    "wsgi" to "text/x-python",
    "yaml" to "text/x-yaml",
    "yml" to "text/x-yaml",
    "asm" to "text/x-asm",
    "s" to "text/x-asm",
    "cs" to "text/x-csharp",
    "azw" to "application/vnd.amazon.ebook",
    "ibooks" to "application/x-ibooks+zip",
    "msg" to "application/vnd.ms-outlook",
    "mkd" to "text/markdown",
    "conf" to "text/plain",
    "ini" to "text/plain",
    "list" to "text/plain",
    "log" to "text/plain",
    "prop" to "text/plain",
    "properties" to "text/plain",
    "rc" to "text/plain"
).mapValues { it.value.asMimeType() }

val MimeType.intentType: String
    get() = intentMimeType.value

private val MimeType.intentMimeType: MimeType
    get() = mimeTypeToIntentMimeTypeMap[this] ?: this

private val mimeTypeToIntentMimeTypeMap = listOf(
    // see: `memo.md`: "为内置Editor关联文件 20250718："

    // Allows matching "text/*"
    "application/ecmascript" to "text/ecmascript",
    "application/javascript" to "text/javascript",
    "application/json" to "text/json",
    "application/x-go" to "text/x-go",
    "application/x-kotlin" to "text/x-kotlin",
    "application/x-groovy" to "text/x-groovy",
    "application/x-jsx" to "text/x-jsx",
    "application/x-batchfile" to "text/x-batchfile",
    "application/x-powershell" to "text/x-powershell",

    "application/x-less" to "text/x-less",
    "application/x-scss" to "text/x-scss",
    "application/x-rust" to "text/x-rust",
    "application/x-coffee" to "text/x-coffee",
    "application/x-clojure" to "text/x-clojure",
    "application/x-lua" to "text/x-lua",
    "application/x-php" to "text/x-php",
    "application/x-r" to "text/x-r",
    "application/x-sql" to "text/x-sql",
    "application/x-swift" to "text/x-swift",
    "application/x-vb" to "text/x-vb",
    "application/x-dart" to "text/x-dart",
    "application/x-dockerfile" to "text/x-dockerfile",
    "application/x-makefile" to "text/x-makefile",
    "application/x-ruby" to "text/x-ruby",

    "application/x-tsx" to "text/x-tsx",
    "application/typescript" to "text/typescript",
    "application/x-sh" to "text/x-shellscript",
    "application/x-shellscript" to "text/x-shellscript",

    "application/x-raku" to "text/x-raku",
    "application/x-toml" to "text/x-toml",
    "application/x-proguard" to "text/x-proguard",
    "application/x-zig" to "text/x-zig",
    "application/x-vue" to "text/x-vue",
    "application/x-fsharp" to "text/x-fsharp",
    "application/x-julia" to "text/x-julia",

    // Allows matching generic
    MimeType.GENERIC.value to MimeType.ANY.value
).associate { it.first.asMimeType() to it.second.asMimeType() }

val Collection<MimeType>.intentType: String
    get() {
        if (isEmpty()) {
            return MimeType.ANY.value
        }
        val intentMimeTypes = map { it.intentMimeType }
        val firstIntentMimeType = intentMimeTypes.first()
        if (intentMimeTypes.all { firstIntentMimeType.match(it) }) {
            return firstIntentMimeType.value
        }
        val wildcardIntentMimeType = MimeType.of(firstIntentMimeType.type, "*", null)
        if (intentMimeTypes.all { wildcardIntentMimeType.match(it) }) {
            return wildcardIntentMimeType.value
        }
        return MimeType.ANY.value
    }
