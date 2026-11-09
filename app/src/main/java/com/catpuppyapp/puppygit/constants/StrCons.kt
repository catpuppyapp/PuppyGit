package com.catpuppyapp.puppygit.constants

/**
 * non-translatable or no need translate strings (such as language name text)
 */
object StrCons {
    const val git = "Git"

    // language name
    // order by language code a-z
    const val langName_Arabic = "العربية"
    const val langName_Bangla = "বাংলা"
    const val langName_English = "English"
    const val langName_Russian = "Русский"
    const val langName_ChineseSimplified = "中文(简体)"
    const val langName_Turkish = "Türkçe"

    // this is git push force-with-lease feature, I am not sure, but I guess it shouldn't translate
    const val withLease = "with-lease"
    // this is git refspec, shouldn't translate also
    const val refspec = "Refspec"

    const val syntaxHightDisabledDueToNoMoreMem = "Syntax highlighting disabled: No more memory!"

    // app data path name
    // why don't translate this: because if translate,
    //   then when I want to tell users about
    //   some files under the "App Data",
    //   they maybe confused which folder I talking about,
    //   if use same name, then will no this problem.
    const val appData = "App Data"
}
