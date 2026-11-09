package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class DevSettings (

    /**
     * diff页面每次只显示一个文件，性能更好
     */
    var singleDiffOn: Boolean = false,

    /**
     * 在按单词匹配失败时降级为按字符匹配。
     * 对非空格分隔的语言友好，反之不好
     */
    var degradeMatchByWordsToMatchByCharsIfNonMatched: Boolean = false,

    /**
     * Diff页面，行选项（select compare那个菜单），显示`匹配所有/不匹配所有`选项
     */
    var showMatchedAllAtDiff: Boolean = true,

    /**
     * 若为true，使用之前加载changelist的方法，多jni，少数据拷贝，循环一次；
     * 若为false，使用少jni调用，多数据拷贝，多循环一次的方法。
     *
     * 还是之前的方法比较快，白写了，写的时候忽略了很重要的一点“检测是否有文件修改是最耗时的，而不是加载修改列表本身”，
     * 然而检测修改这块完全是c写的，java这边根本没有优化空间，而c那边恐怕也接近极限了，所以其实没什么好优化的，还是用legacy方法比较好。
     */
    var legacyChangeListLoadMethod: Boolean = true,

    /**
     * if true, will treat no words matched as non-matched when diff contents and enabled match by words
     */
    var treatNoWordMatchAsNoMatchedForDiff: Boolean = false,
)
