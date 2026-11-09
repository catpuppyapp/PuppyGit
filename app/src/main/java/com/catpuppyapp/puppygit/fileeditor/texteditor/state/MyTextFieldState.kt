package com.catpuppyapp.puppygit.fileeditor.texteditor.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.getRandomUUID


private val lineChangeType_NEW_dark =  Color(0xFF33691E)
private val lineChangeType_NEW =  Color(0xFF8BC34A)
private val lineChangeType_UPDATED_dark =  Color(0xFF0277BD)
private val lineChangeType_UPDATED = Color(0xFF03A9F4)


//这个stable注解，我看了下应该符合条件，若更新状态出问题，可取消注解试试
@Stable
class MyTextFieldState(


    val value: TextFieldValue = TextFieldValue(),


    //用来指示当前行是编辑过，还是新增行，就像notepad++那样，这个状态和git无关，只针对当前会话，临时的，若实现成和git有关的话，有点麻烦，算了
    var changeType:LineChangeType = LineChangeType.NONE,
    //仅当 value.text 修改才应该换新id；否则语法高亮会失效
    val syntaxHighlightId: String = getRandomUUID(),
) {
    fun copy(
        value: TextFieldValue = this.value,
        changeType: LineChangeType = this.changeType,

    ) = MyTextFieldState(
        value = value,
        changeType = changeType,
        syntaxHighlightId = if(value.text != this.value.text || this.syntaxHighlightId.isBlank()) getRandomUUID() else this.syntaxHighlightId,

    )

    override fun toString(): String {
        return "TextFieldState(syntaxHighlightId=$syntaxHighlightId, value=$value, changeType=$changeType)"
    }


    //不太确定：非常极端的情况下，如果一个文件不换行，就一行，有非常多内容（可能几M），那这样比较不如直接比较地址？
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        // or `other !is TextFieldState`
        if (javaClass != other?.javaClass) return false

        other as MyTextFieldState

        if (syntaxHighlightId != other.syntaxHighlightId) return false
        if (changeType != other.changeType) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = syntaxHighlightId.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + changeType.hashCode()
        return result
    }


    fun updateLineChangeTypeIfNone(targetChangeType: LineChangeType) {
        //若行修改类型不是NONE，则代表已经确定当前行是新增还是修改了，就无需再更新其类型
        if(changeType == LineChangeType.NONE) {
            changeType = targetChangeType
        }
    }

    fun updateLineChangeType(targetChangeType: LineChangeType) {
        changeType = targetChangeType
    }


    fun getColorOfChangeType(inDarkTheme: Boolean):Color {
        return if(changeType == LineChangeType.NEW) {
            if(inDarkTheme) lineChangeType_NEW_dark else lineChangeType_NEW
        }else if(changeType == LineChangeType.UPDATED) {
            if(inDarkTheme) lineChangeType_UPDATED_dark else lineChangeType_UPDATED
        }else if(changeType == LineChangeType.ACCEPT_OURS) {
            MyStyleKt.ConflictBlock.getAcceptOursIconColor(inDarkTheme)
        }else if(changeType == LineChangeType.ACCEPT_THEIRS) {
            MyStyleKt.ConflictBlock.getAcceptTheirsIconColor(inDarkTheme)
        }else {
            //和背景颜色一样，所以等应该看不出差别，如果能显示出差异，可改成透明
//            Color.Unspecified
            Color.Transparent
        }
    }
}

enum class LineChangeType {
    NONE,
    NEW,
    UPDATED,

    // represent create by accept ours/theirs when merge mode on
    ACCEPT_OURS,
    ACCEPT_THEIRS,

    //逻辑上来说其实还应该有个deleted，但删了就看不到了，无意义，所以实际不需要

}
