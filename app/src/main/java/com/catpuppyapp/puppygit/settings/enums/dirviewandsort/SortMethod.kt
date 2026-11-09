package com.catpuppyapp.puppygit.settings.enums.dirviewandsort

import android.content.Context
import com.catpuppyapp.puppygit.play.pro.R

enum class SortMethod(val code: Int) {
    NAME(1),
    TYPE(2),
    SIZE(3),
    LAST_MODIFIED(4);

    companion object {
        fun fromCode(code: Int): SortMethod? {
            return entries.find { it.code == code }
        }

        fun getText(sortMethod: SortMethod, context: Context):String {
            if(sortMethod == NAME) {
                return context.getString(R.string.name)
            }

            if(sortMethod == TYPE) {
                return context.getString(R.string.type)
            }

            if(sortMethod == SIZE) {
                return context.getString(R.string.size)
            }

            if(sortMethod == LAST_MODIFIED) {
                return context.getString(R.string.last_modified)
            }

            return ""
        }
    }
}
