package com.catpuppyapp.puppygit.utils

import android.content.Context
import com.catpuppyapp.puppygit.constants.LangCode
import com.catpuppyapp.puppygit.constants.StrCons
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.pref.PrefMan


object LanguageUtil {
    private const val TAG="LanguageUtil"

    private const val key = PrefMan.Key.lang

    val languageCodeList = listOf(
        LangCode.auto,

        // order by a-z
        LangCode.ar,
        LangCode.bn,
        LangCode.en,
        LangCode.ru,
        LangCode.tr,
        LangCode.zh_cn,

        // other language...
    )


    fun getLangCode(context: Context):String {
        return PrefMan.get(context, key, "").let {
            if(isAuto(it)) {
                LangCode.auto
            }else {
                it
            }
        }
    }

    fun setLangCode(context: Context, langCode:String) {
        PrefMan.set(context, key, langCode)
    }

    fun isAuto(langCode: String):Boolean {
        return langCode == LangCode.auto || langCode.isBlank() || !languageCodeList.contains(langCode)
    }


    fun getLanguageTextByCode(languageCode:String, context: Context):String {
        if(isAuto(languageCode)) {
            return context.getString(R.string.auto)
        }

        // order by a-z
        if(languageCode == LangCode.ar) {
            return StrCons.langName_Arabic
        }

        if(languageCode == LangCode.bn) {
            return StrCons.langName_Bangla
        }

        if(languageCode == LangCode.en) {
            return StrCons.langName_English
        }

        if(languageCode == LangCode.ru) {
            return StrCons.langName_Russian
        }

        if(languageCode == LangCode.zh_cn) {
            return StrCons.langName_ChineseSimplified
        }

        if(languageCode == LangCode.tr) {
            return StrCons.langName_Turkish
        }

        // add other language here


        // treat unsupported languages as auto
        MyLog.d(TAG, "#getLanguageTextByCode: unknown language code '$languageCode', will use `auto`")

        return context.getString(R.string.auto)
    }

    /**
     * e.g. input "zh-rCN" return Pair("zh", "CN")
     */
    fun splitLanguageCode(languageCode:String):Pair<String,String> {
        val codes = languageCode.split("-r")
        if(codes.size>1) {
            return Pair(codes[0], codes[1])
        }else {
            return Pair(codes[0], "")
        }
    }


}
