package com.catpuppyapp.puppygit.constants

import com.catpuppyapp.puppygit.git.PuppyLine
import com.github.git24j.core.Diff

object LineNum {

    const val invalidButNotEof = -998
    const val max = Int.MAX_VALUE

    // used for restore last edited or viewed line number
    //这个值代表期望恢复上次编辑位置
    const val lastPosition = -2

    object EOF {
        const val LINE_NUM = -1
        const val TEXT = "EOF"


        /**
         * trans a puppy line EOF_NL to EOF line, because EOF_NL's content is something like "no new line of file", but I only want to show a clear new line
         */
        fun transLineToEofLine(line: PuppyLine, add:Boolean): PuppyLine {
            return line.copy(
                lineNum = LINE_NUM,
                originType = if(add) Diff.Line.OriginType.ADDITION.toString() else Diff.Line.OriginType.DELETION.toString(),

                // only line break, will show as an empty line
                content = Cons.lineBreak,
                contentLen = 1
            )
        }

    }

    /**
     * return true if this line number means should restore last edited or viewed position
     */
    fun shouldRestoreLastPosition(lineNumWillCheck:Int) :Boolean {
        return lineNumWillCheck == lastPosition || (lineNumWillCheck != EOF.LINE_NUM && lineNumWillCheck <= 0)
    }
}
