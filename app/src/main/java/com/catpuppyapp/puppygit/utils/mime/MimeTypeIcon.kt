/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.catpuppyapp.puppygit.utils.mime

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.TableView
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material.icons.outlined.VideoStable
import androidx.compose.ui.graphics.vector.ImageVector

val outLinedIcons = Icons.Outlined
val filledIcons = Icons.Filled
val autoMirroredOutLinedIcons = Icons.AutoMirrored.Outlined

enum class MimeTypeIcon(val icon: ImageVector) {
    APK(outLinedIcons.Android),
    ARCHIVE(outLinedIcons.Archive),
    AUDIO(outLinedIcons.AudioFile),
    CALENDAR(outLinedIcons.CalendarMonth),
    CERTIFICATE(outLinedIcons.Fingerprint),
    CODE(outLinedIcons.Code),
    CONTACT(outLinedIcons.ContactPage),
    DIRECTORY(filledIcons.Folder),
    DOCUMENT(autoMirroredOutLinedIcons.TextSnippet),
    EBOOK(outLinedIcons.Book),
    EMAIL(outLinedIcons.Email),
    FONT(outLinedIcons.FontDownload),
    GENERIC(autoMirroredOutLinedIcons.InsertDriveFile),
    IMAGE(outLinedIcons.Image),
    PDF(outLinedIcons.PictureAsPdf),
    PRESENTATION(outLinedIcons.VideoStable),
    SPREADSHEET(outLinedIcons.TableView),
    TEXT(autoMirroredOutLinedIcons.TextSnippet),
    VIDEO(outLinedIcons.VideoFile),
    WORD(autoMirroredOutLinedIcons.TextSnippet),
    EXCEL(outLinedIcons.TableView),
    POWERPOINT(outLinedIcons.VideoStable)
}

// See also https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/com/android/internal/util/MimeIconUtils.java
// See also https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
// See also http://www.iana.org/assignments/media-types/media-types.xhtml
// See also /usr/share/mime/packages/freedesktop.org.xml
val MimeType.icon: MimeTypeIcon
    get() =
        mimeTypeToIconMap[this] ?: typeToIconMap[type] ?: suffix?.let { suffixToIconMap[it] }
        ?: MimeTypeIcon.GENERIC

// See also https://mimesniff.spec.whatwg.org/#mime-type-groups
private val mimeTypeToIconMap = mapOf(
    "application/x-go" to MimeTypeIcon.CODE,
    "application/x-kotlin" to MimeTypeIcon.CODE,
    "application/x-jsx" to MimeTypeIcon.CODE,
    "application/x-tsx" to MimeTypeIcon.CODE,
    "application/x-groovy" to MimeTypeIcon.CODE,
    "application/x-batchfile" to MimeTypeIcon.CODE,
    "application/x-powershell" to MimeTypeIcon.CODE,
    "application/x-fsharp" to MimeTypeIcon.CODE,
    "application/x-julia" to MimeTypeIcon.CODE,

    "application/x-less" to MimeTypeIcon.CODE,
    "application/x-scss" to MimeTypeIcon.CODE,
    "application/x-rust" to MimeTypeIcon.CODE,
    "application/x-coffee" to MimeTypeIcon.CODE,
    "application/x-clojure" to MimeTypeIcon.CODE,
    "application/x-lua" to MimeTypeIcon.CODE,
    "application/x-php" to MimeTypeIcon.CODE,
    "application/x-r" to MimeTypeIcon.CODE,
    "application/x-sql" to MimeTypeIcon.CODE,
    "application/x-swift" to MimeTypeIcon.CODE,
    "application/x-vb" to MimeTypeIcon.CODE,
    "application/x-raku" to MimeTypeIcon.CODE,
    "application/x-toml" to MimeTypeIcon.CODE,
    "application/x-dart" to MimeTypeIcon.CODE,
    "application/x-dockerfile" to MimeTypeIcon.CODE,
    "application/x-makefile" to MimeTypeIcon.CODE,
    "application/x-zig" to MimeTypeIcon.CODE,
    "application/x-proguard" to MimeTypeIcon.CODE,
    "application/x-vue" to MimeTypeIcon.CODE,


    "application/vnd.android.package-archive" to MimeTypeIcon.APK,
    "application/gzip" to MimeTypeIcon.ARCHIVE,
    // Not in IANA list, but Mozilla and Wikipedia say so.
    "application/java-archive" to MimeTypeIcon.ARCHIVE,
    "application/mac-binhex40" to MimeTypeIcon.ARCHIVE,
    // Not in IANA list, but AOSP MimeUtils says so.
    "application/rar" to MimeTypeIcon.ARCHIVE,
    "application/zip" to MimeTypeIcon.ARCHIVE,
    "application/vnd.debian.binary-package" to MimeTypeIcon.ARCHIVE,
    "application/vnd.ms-cab-compressed" to MimeTypeIcon.ARCHIVE,
    "application/vnd.rar" to MimeTypeIcon.ARCHIVE,
    "application/x-7z-compressed" to MimeTypeIcon.ARCHIVE,
    "application/x-apple-diskimage" to MimeTypeIcon.ARCHIVE,
    "application/x-bzip" to MimeTypeIcon.ARCHIVE,
    "application/x-bzip2" to MimeTypeIcon.ARCHIVE,
    "application/x-compress" to MimeTypeIcon.ARCHIVE,
    "application/x-cpio" to MimeTypeIcon.ARCHIVE,
    "application/x-deb" to MimeTypeIcon.ARCHIVE,
    "application/x-debian-package" to MimeTypeIcon.ARCHIVE,
    "application/x-gtar" to MimeTypeIcon.ARCHIVE,
    "application/x-gtar-compressed" to MimeTypeIcon.ARCHIVE,
    "application/x-gzip" to MimeTypeIcon.ARCHIVE,
    "application/x-iso9660-image" to MimeTypeIcon.ARCHIVE,
    "application/x-java-archive" to MimeTypeIcon.ARCHIVE,
    "application/x-lha" to MimeTypeIcon.ARCHIVE,
    "application/x-lzh" to MimeTypeIcon.ARCHIVE,
    "application/x-lzma" to MimeTypeIcon.ARCHIVE,
    "application/x-lzx" to MimeTypeIcon.ARCHIVE,
    "application/x-rar-compressed" to MimeTypeIcon.ARCHIVE,
    "application/x-stuffit" to MimeTypeIcon.ARCHIVE,
    "application/x-tar" to MimeTypeIcon.ARCHIVE,
    "application/x-webarchive" to MimeTypeIcon.ARCHIVE,
    "application/x-webarchive-xml" to MimeTypeIcon.ARCHIVE,
    "application/x-xz" to MimeTypeIcon.ARCHIVE,
    "application/ogg" to MimeTypeIcon.AUDIO,
    "application/x-flac" to MimeTypeIcon.AUDIO,
    "text/calendar" to MimeTypeIcon.CALENDAR,
    "text/x-vcalendar" to MimeTypeIcon.CALENDAR,
    "application/pgp-keys" to MimeTypeIcon.CERTIFICATE,
    "application/pgp-signature" to MimeTypeIcon.CERTIFICATE,
    "application/x-pkcs12" to MimeTypeIcon.CERTIFICATE,
    "application/x-pkcs7-certificates" to MimeTypeIcon.CERTIFICATE,
    "application/x-pkcs7-certreqresp" to MimeTypeIcon.CERTIFICATE,
    "application/x-pkcs7-crl" to MimeTypeIcon.CERTIFICATE,
    "application/x-pkcs7-mime" to MimeTypeIcon.CERTIFICATE,
    "application/x-pkcs7-signature" to MimeTypeIcon.CERTIFICATE,
    "application/x-x509-ca-cert" to MimeTypeIcon.CERTIFICATE,
    "application/x-x509-server-cert" to MimeTypeIcon.CERTIFICATE,
    "application/x-x509-user-cert" to MimeTypeIcon.CERTIFICATE,
    "application/ecmascript" to MimeTypeIcon.CODE,
    "application/javascript" to MimeTypeIcon.CODE,
    "application/json" to MimeTypeIcon.CODE,
    "application/typescript" to MimeTypeIcon.CODE,
    "application/xml" to MimeTypeIcon.CODE,
    "application/x-csh" to MimeTypeIcon.CODE,
    "application/x-ecmascript" to MimeTypeIcon.CODE,
    "application/x-javascript" to MimeTypeIcon.CODE,
    "application/x-latex" to MimeTypeIcon.CODE,
    "application/x-perl" to MimeTypeIcon.CODE,
    "application/x-python" to MimeTypeIcon.CODE,
    "application/x-ruby" to MimeTypeIcon.CODE,
    "application/x-sh" to MimeTypeIcon.CODE,
    "application/x-shellscript" to MimeTypeIcon.CODE,
    "application/x-texinfo" to MimeTypeIcon.CODE,
    "application/x-yaml" to MimeTypeIcon.CODE,
    "text/css" to MimeTypeIcon.CODE,
    "text/html" to MimeTypeIcon.CODE,
    "text/ecmascript" to MimeTypeIcon.CODE,
    "text/javascript" to MimeTypeIcon.CODE,
    "text/jscript" to MimeTypeIcon.CODE,
    "text/livescript" to MimeTypeIcon.CODE,
    "text/xml" to MimeTypeIcon.CODE,
    "text/x-asm" to MimeTypeIcon.CODE,
    "text/x-c++hdr" to MimeTypeIcon.CODE,
    "text/x-c++src" to MimeTypeIcon.CODE,
    "text/x-chdr" to MimeTypeIcon.CODE,
    "text/x-csh" to MimeTypeIcon.CODE,
    "text/x-csharp" to MimeTypeIcon.CODE,
    "text/x-csrc" to MimeTypeIcon.CODE,
    "text/x-dsrc" to MimeTypeIcon.CODE,
    "text/x-ecmascript" to MimeTypeIcon.CODE,
    "text/x-haskell" to MimeTypeIcon.CODE,
    "text/x-java" to MimeTypeIcon.CODE,
    "text/x-javascript" to MimeTypeIcon.CODE,
    "text/x-literate-haskell" to MimeTypeIcon.CODE,
    "text/x-pascal" to MimeTypeIcon.CODE,
    "text/x-perl" to MimeTypeIcon.CODE,
    "text/x-python" to MimeTypeIcon.CODE,
    "text/x-ruby" to MimeTypeIcon.CODE,
    "text/x-shellscript" to MimeTypeIcon.CODE,
    "text/x-tcl" to MimeTypeIcon.CODE,
    "text/x-tex" to MimeTypeIcon.CODE,
    "text/x-yaml" to MimeTypeIcon.CODE,
    "text/vcard" to MimeTypeIcon.CONTACT,
    "text/x-vcard" to MimeTypeIcon.CONTACT,
    "inode/directory" to MimeTypeIcon.DIRECTORY,
    MimeType.DIRECTORY.value to MimeTypeIcon.DIRECTORY,
    "application/rtf" to MimeTypeIcon.DOCUMENT,
    "application/vnd.oasis.opendocument.text" to MimeTypeIcon.DOCUMENT,
    "application/vnd.oasis.opendocument.text-master" to MimeTypeIcon.DOCUMENT,
    "application/vnd.oasis.opendocument.text-template" to MimeTypeIcon.DOCUMENT,
    "application/vnd.oasis.opendocument.text-web" to MimeTypeIcon.DOCUMENT,
    "application/vnd.stardivision.writer" to MimeTypeIcon.DOCUMENT,
    "application/vnd.stardivision.writer-global" to MimeTypeIcon.DOCUMENT,
    "application/vnd.sun.xml.writer" to MimeTypeIcon.DOCUMENT,
    "application/vnd.sun.xml.writer.global" to MimeTypeIcon.DOCUMENT,
    "application/vnd.sun.xml.writer.template" to MimeTypeIcon.DOCUMENT,
    "application/x-abiword" to MimeTypeIcon.DOCUMENT,
    "application/x-kword" to MimeTypeIcon.DOCUMENT,
    "text/rtf" to MimeTypeIcon.DOCUMENT,
    "application/epub+zip" to MimeTypeIcon.EBOOK,
    "application/vnd.amazon.ebook" to MimeTypeIcon.EBOOK,
    "application/x-cbr" to MimeTypeIcon.EBOOK,
    "application/x-cbz" to MimeTypeIcon.EBOOK,
    "application/x-ibooks+zip" to MimeTypeIcon.EBOOK,
    "application/x-mobipocket-ebook" to MimeTypeIcon.EBOOK,
    "application/vnd.ms-outlook" to MimeTypeIcon.EMAIL,
    "message/rfc822" to MimeTypeIcon.EMAIL,
    "application/font-cff" to MimeTypeIcon.FONT,
    "application/font-off" to MimeTypeIcon.FONT,
    "application/font-sfnt" to MimeTypeIcon.FONT,
    "application/font-ttf" to MimeTypeIcon.FONT,
    "application/font-woff" to MimeTypeIcon.FONT,
    "application/vnd.ms-fontobject" to MimeTypeIcon.FONT,
    "application/vnd.ms-opentype" to MimeTypeIcon.FONT,
    "application/x-font" to MimeTypeIcon.FONT,
    "application/x-font-ttf" to MimeTypeIcon.FONT,
    "application/x-font-woff" to MimeTypeIcon.FONT,
    "application/vnd.oasis.opendocument.graphics" to MimeTypeIcon.IMAGE,
    "application/vnd.oasis.opendocument.graphics-template" to MimeTypeIcon.IMAGE,
    "application/vnd.oasis.opendocument.image" to MimeTypeIcon.IMAGE,
    "application/vnd.stardivision.draw" to MimeTypeIcon.IMAGE,
    "application/vnd.sun.xml.draw" to MimeTypeIcon.IMAGE,
    "application/vnd.sun.xml.draw.template" to MimeTypeIcon.IMAGE,
    "application/vnd.visio" to MimeTypeIcon.IMAGE,
    "application/pdf" to MimeTypeIcon.PDF,
    "application/vnd.oasis.opendocument.presentation" to MimeTypeIcon.PRESENTATION,
    "application/vnd.oasis.opendocument.presentation-template" to MimeTypeIcon.PRESENTATION,
    "application/vnd.stardivision.impress" to MimeTypeIcon.PRESENTATION,
    "application/vnd.sun.xml.impress" to MimeTypeIcon.PRESENTATION,
    "application/vnd.sun.xml.impress.template" to MimeTypeIcon.PRESENTATION,
    "application/x-kpresenter" to MimeTypeIcon.PRESENTATION,
    "application/vnd.oasis.opendocument.spreadsheet" to MimeTypeIcon.SPREADSHEET,
    "application/vnd.oasis.opendocument.spreadsheet-template" to MimeTypeIcon.SPREADSHEET,
    "application/vnd.stardivision.calc" to MimeTypeIcon.SPREADSHEET,
    "application/vnd.sun.xml.calc" to MimeTypeIcon.SPREADSHEET,
    "application/vnd.sun.xml.calc.template" to MimeTypeIcon.SPREADSHEET,
    "application/x-kspread" to MimeTypeIcon.SPREADSHEET,
    "application/x-quicktimeplayer" to MimeTypeIcon.VIDEO,
    "application/x-shockwave-flash" to MimeTypeIcon.VIDEO,
    "application/msword" to MimeTypeIcon.WORD,
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to MimeTypeIcon.WORD,
    "application/vnd.openxmlformats-officedocument.wordprocessingml.template" to MimeTypeIcon.WORD,
    "application/vnd.ms-excel" to MimeTypeIcon.EXCEL,
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" to MimeTypeIcon.EXCEL,
    "application/vnd.openxmlformats-officedocument.spreadsheetml.template" to MimeTypeIcon.EXCEL,
    "application/vnd.ms-powerpoint" to MimeTypeIcon.POWERPOINT,
    "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        to MimeTypeIcon.POWERPOINT,
    "application/vnd.openxmlformats-officedocument.presentationml.slideshow"
        to MimeTypeIcon.POWERPOINT,
    "application/vnd.openxmlformats-officedocument.presentationml.template"
        to MimeTypeIcon.POWERPOINT
).mapKeys { it.key.asMimeType() }

private val typeToIconMap = mapOf(
    "audio" to MimeTypeIcon.AUDIO,
    "font" to MimeTypeIcon.FONT,
    "image" to MimeTypeIcon.IMAGE,
    "text" to MimeTypeIcon.TEXT,
    "video" to MimeTypeIcon.VIDEO
)

private val suffixToIconMap = mapOf(
    "json" to MimeTypeIcon.CODE,
    "xml" to MimeTypeIcon.CODE,
    "zip" to MimeTypeIcon.ARCHIVE
)

val MimeType.iconRes: ImageVector
    get() = icon.icon
