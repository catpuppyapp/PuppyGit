package com.catpuppyapp.puppygit.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


// 这个类用来在将来实现一个与ppgit配套的editor时，读取这些参数，以和ppgit无缝衔接
// launched app can support it by read them from intent
@Parcelize
data class EditorPayload(
    // if possible, this should be absolute path rather than uri
    val fileIoPath: String = "",

    // line number, not index, since 1
    val firstVisibleLineNumStartFrom1: Int = 1,
    val lastEditedLineNumStartFrom1: Int = 1,

    val mergeModeOn:Boolean = false,
    val patchModeOn:Boolean = false,
    // this not about file actually
    //   has uri write permission or not,
    //   this used to let launched app to know
    //   should enable read only for this file or not
    val readOnlyOn:Boolean = false,

): Parcelable
