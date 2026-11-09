package com.catpuppyapp.puppygit.utils

import org.mozilla.universalchardet.Constants
import org.mozilla.universalchardet.UniversalDetector
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * File encoding util class
 */
object EncodingUtil {
    private const val TAG = "EncodingUtil"

    internal const val UTF8_BOM = "UTF-8 BOM"

    /**
     * note: CHARSET_GB18030 include GBK, GBK include GB2312
     */
    val supportedCharsetList = listOf(
        // order by a-z
        Constants.CHARSET_BIG5,

        Constants.CHARSET_EUC_JP,
        Constants.CHARSET_EUC_KR,
        Constants.CHARSET_EUC_TW,

        Constants.CHARSET_GB18030,  // include gbk
        Constants.CHARSET_GBK,  // include gb2312

        Constants.CHARSET_IBM855,
        Constants.CHARSET_IBM866,

        Constants.CHARSET_ISO_2022_JP,
        Constants.CHARSET_ISO_2022_CN,
        Constants.CHARSET_ISO_2022_KR,
        Constants.CHARSET_ISO_8859_5,
        Constants.CHARSET_ISO_8859_7,
        Constants.CHARSET_ISO_8859_8,


        Constants.CHARSET_KOI8_R,

        Constants.CHARSET_MACCYRILLIC,

        Constants.CHARSET_SHIFT_JIS,

        Constants.CHARSET_TIS620,

        Constants.CHARSET_UTF_8,
        UTF8_BOM,

        Constants.CHARSET_UTF_16BE,
        Constants.CHARSET_UTF_16LE,
        Constants.CHARSET_UTF_32BE,
        Constants.CHARSET_UTF_32LE,

        // utf8 fully covered ASCII, so no need this encoding
//        Constants.CHARSET_US_ASCII,

        Constants.CHARSET_WINDOWS_1251,
        Constants.CHARSET_WINDOWS_1252,
        Constants.CHARSET_WINDOWS_1253,
        Constants.CHARSET_WINDOWS_1255,
    )

    val defaultCharsetName:String = Constants.CHARSET_UTF_8
    val defaultCharset: Charset = resolveCharset(defaultCharsetName)

    private fun makeSureUseASupportedCharset(originCharset: String?): String {
        if(originCharset == null) {
            return defaultCharsetName
        }

        // UTF8 full covered ASCII, can't distinguish both when a file only contains ASCII chars.
        // if a file only contains ASCII chars but expect UTF8,
        //   it will detected ASCII rather than UTF8,
        //   so just return UTF8, simple and better
        if(originCharset == Constants.CHARSET_US_ASCII) {
            return Constants.CHARSET_UTF_8
        }

        // java doesn't support this, use gbk instead, gbk included gb2312, so should be ok
        // java不支持这个，用gbk替代，gbk包含gb2312，所以应该不会乱码
        if(originCharset == Constants.CHARSET_HZ_GB_2312) {
            return Constants.CHARSET_GBK
        }

        // java doesn't support them, just use default charset
        // java不支持的编码，使用默认即可，不过可能乱码
        if(originCharset == Constants.CHARSET_X_ISO_10646_UCS_4_3412
            || originCharset == Constants.CHARSET_X_ISO_10646_UCS_4_2143
        ) {
            return defaultCharsetName
        }

        if(supportedCharsetList.contains(originCharset)) {
            return originCharset
        }

        return defaultCharsetName
    }


    fun detectEncoding(newInputStream: () -> InputStream): String {
        return try {
            detectEncodingNoCatch(newInputStream)
        }catch (e: Exception) {
            if(AppModel.devModeOn) {
                MyLog.d(TAG, "#detectEncoding() err, will use '$defaultCharsetName', err msg=${e.localizedMessage}")
                e.printStackTrace()
            }

            defaultCharsetName
        }
    }

    private fun detectEncodingNoCatch(newInputStream: () -> InputStream): String {
        // most time only need read few bytes to detect encoding, maybe thousands
        val buf = ByteArray(4096)


        // (1)
        val detector = UniversalDetector()


        // (2)
        var nread: Int = -1
        // read until detector detected the encoding
        val inputStream = newInputStream()
        while ((inputStream.read(buf).also { nread = it }) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread)
        }

        // (3)
        detector.dataEnd()


        // (4)
        val encoding = detector.getDetectedCharset()


        // (5)
        detector.reset()



        val encodingSupported = makeSureUseASupportedCharset(encoding)

        // utf8 with bom
        if(encodingSupported == Constants.CHARSET_UTF_8) {
            if(hasUtf8Bom(newInputStream())) {
                return UTF8_BOM
            }
        }

        return encodingSupported
    }

    fun resolveCharset(charsetName: String?) : Charset {
        try {
            if(charsetName == UTF8_BOM){
                return StandardCharsets.UTF_8
            }

            // `name` lowercase or uppercase all be fine
            // `name` 小写大写皆可
            return Charset.forName(charsetName)
        }catch (e: Exception) {
            MyLog.e(TAG, "#resolveCharset err, will use '$defaultCharsetName', param `charsetName`=$charsetName, err=${e.localizedMessage}")
            e.printStackTrace()
            return defaultCharset
        }
    }

    /**
     * add bom to output stream if need, "BOM" is abbreviation of "Byte order mark"
     *
     * see: https://en.wikipedia.org/wiki/Byte_order_mark#Byte-order_marks_by_encoding
     */
    fun addBomIfNeed(outputStream: OutputStream, charsetName: String?) {
        if(charsetName == UTF8_BOM) {
            outputStream.write(0xEF)
            outputStream.write(0xBB)
            outputStream.write(0xBF)
        }else if(charsetName == Constants.CHARSET_UTF_16LE) {
            outputStream.write(0xFF)
            outputStream.write(0xFE)
        }else if(charsetName == Constants.CHARSET_UTF_32LE) {
            outputStream.write(0xFF)
            outputStream.write(0xFE)
            outputStream.write(0x00)
            outputStream.write(0x00)
        }else if(charsetName == Constants.CHARSET_UTF_16BE) {
            outputStream.write(0xFE)
            outputStream.write(0xFF)
        }else if(charsetName == Constants.CHARSET_UTF_32BE) {
            outputStream.write(0x00)
            outputStream.write(0x00)
            outputStream.write(0xFE)
            outputStream.write(0xFF)
        }
    }

    fun hasUtf8Bom(inputStream: InputStream): Boolean {
        return inputStream.read() == 0xEF && inputStream.read() == 0xBB && inputStream.read() == 0xBF
    }

    /**
     * @return true is utf8 bom and consumed, else is not utf8 bom
     * @return 真代表是utf8且已消耗bom，否则代表不是utf8
     *
     * note: even isn't utf8bom, the `inputStream` still already read (即使不是utf8bom，inputStream也已经被读取过了)
     */
    fun consumeUtf8Bom(inputStream: InputStream) = hasUtf8Bom(inputStream)

    fun ignoreBomIfNeed(newInputStream: () -> InputStream, charsetName: String?): IgnoreBomResult {
        if(charsetName == UTF8_BOM) {
            val inputStream = newInputStream()

            if(consumeUtf8Bom(inputStream)) {
                return IgnoreBomResult(true, inputStream)
            }
        }

        if(charsetName == Constants.CHARSET_UTF_16LE) {
            val inputStream = newInputStream()

            if(inputStream.read() == 0xFF
                && inputStream.read() == 0xFE
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }

        if(charsetName == Constants.CHARSET_UTF_32LE) {
            val inputStream = newInputStream()

            if(inputStream.read() == 0xFF
                && inputStream.read() == 0xFE
                && inputStream.read() == 0x00
                && inputStream.read() == 0x00
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }

        if(charsetName == Constants.CHARSET_UTF_16BE) {
            val inputStream = newInputStream()

            if(inputStream.read() == 0xFE
                && inputStream.read() == 0xFF
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }

        if(charsetName == Constants.CHARSET_UTF_32BE) {
            val inputStream = newInputStream()

            if(inputStream.read() == 0x00
                && inputStream.read() == 0x00
                && inputStream.read() == 0xFE
                && inputStream.read() == 0xFF
            ) {
                return IgnoreBomResult(true, inputStream)
            }
        }

        return IgnoreBomResult(false, newInputStream())
    }



}


data class IgnoreBomResult(
    val wasHasBom: Boolean,
    val inputStream: InputStream,
)
