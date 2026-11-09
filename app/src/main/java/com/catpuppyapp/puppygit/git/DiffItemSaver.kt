package com.catpuppyapp.puppygit.git

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.SpanStyle
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.StrCons
import com.catpuppyapp.puppygit.dto.Box
import com.catpuppyapp.puppygit.msg.OneTimeToast
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLScope
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLTheme
import com.catpuppyapp.puppygit.syntaxhighlight.hunk.HunkSyntaxHighlighter
import com.catpuppyapp.puppygit.syntaxhighlight.hunk.LineStylePart
import com.catpuppyapp.puppygit.utils.compare.CmpUtil
import com.catpuppyapp.puppygit.utils.compare.param.StringCompareParam
import com.catpuppyapp.puppygit.utils.compare.result.IndexModifyResult
import com.catpuppyapp.puppygit.utils.compare.result.IndexStringPart
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.getFileNameFromCanonicalPath
import com.catpuppyapp.puppygit.utils.getShortUUID
import com.catpuppyapp.puppygit.utils.noMoreHeapMemThenDoAct
import com.github.git24j.core.Diff
import java.util.EnumSet
import java.util.TreeMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


// used to decide which addition line compare to which deletion line in the hunk.
// 【larger value bad performance but accurate result, smaller value has good performance but in-accurate result】
// 用来在比较前决定hunk里哪个addition line和哪个deletion line关联比较。【值越大性能越差，结果更准，反之，性能好，结果更不准】
private const val targetRoughlyMatchedCount = 6


object PuppyLineOriginType{
    //不参与语法高亮分析
    const val HUNK_HDR = Diff.Line.OriginType.HUNK_HDR.toString()

    //参与语法高亮分析
    const val ADDITION = Diff.Line.OriginType.ADDITION.toString()
    const val DELETION = Diff.Line.OriginType.DELETION.toString()
    const val CONTEXT = Diff.Line.OriginType.CONTEXT.toString()

    //参与语法高亮分析，但分析前其内容会被替换为空，eof行本身可能有
    //   "/No end of new line" 之类的东西，但在ui上显示的时候
    //   改成了空字符串，如果分析前不替换为空，显示时就会越界，
    //   因为分析的有内容，而显示的时候是空字符串，最后就变成
    //   空字符串取非空range的substring，就越界了
    const val CONTEXT_EOFNL = Diff.Line.OriginType.CONTEXT_EOFNL.toString()
    const val ADD_EOFNL = Diff.Line.OriginType.ADD_EOFNL.toString()
    const val DEL_EOFNL = Diff.Line.OriginType.DEL_EOFNL.toString()

    fun isEofLine(line: PuppyLine) = line.originType.let { it == CONTEXT_EOFNL || it == ADD_EOFNL || it == DEL_EOFNL }
}


data class DiffItemSaver (
    var relativePathUnderRepo:String="",  //仓库下相对路径
    var keyForRefresh:String= getShortUUID(),

    // 这个字段好像可有可无
    var fromTo:String= Cons.gitDiffFromIndexToWorktree,

//    var fileHeader:String="";  // file好像没有header
    var oldFileOid:String="",
    var newFileOid:String="",
    var newFileSize:Long=0L,
    var oldFileSize:Long=0L,
    //diff 所有 line.content总和有无超过限制大小
    var isContentSizeOverLimit:Boolean=false,
//    var efficientFileSize=0L
    var flags: EnumSet<Diff.FlagT> = EnumSet.of(Diff.FlagT.NOT_BINARY),
    var hunks:MutableList<PuppyHunkAndLines> = mutableListOf(),  //key是hunk的指针地址

    //指示文件是否修改过，因为有时候会错误的diff没修改过的文件，所以需要判断下
    var isFileModified:Boolean=false,
    var addedLines:Int=0,  //添加了多少行。（不包含EOF，因为那个东西判断不太准，有时候明明删了却显示添加，让人困惑，而且一个空行感觉好像意义不大？）
    var deletedLines:Int=0,  //删除了多少行
    var allLines:Int=0,  //总共多少行，包含添加、删除、上下文，如果有eof，也包含eof

    //最大的行号。（用来算行号padding的）
    var maxLineNum:Int=0,
    var hasEofLine:Boolean = false,


    // 比较的左右两边的文件的类型，如果是图片，则按图片预览，若都是text，则按text预览
    var oldFileType: DiffItemSaverType = DiffItemSaverType.TEXT,
    var newFileType: DiffItemSaverType = DiffItemSaverType.TEXT,

    // 把blob文件存到本地的path，一般存到缓存目录供临时查看，预览图片时会用到
    var oldBlobSavePath:String="",
    var newBlobSavePath:String="",

    //根据delta比较出来的实际的修改类型，最终在diff页面显示的修改类型以这个为准
    var changeType:String = Cons.gitStatusUnmodified,

    // styles
    private val stylesMapLock: ReentrantReadWriteLock = ReentrantReadWriteLock(),
    // {PuppyLine.key: StylesResult}
    private val stylesMap: MutableMap<String, List<LineStylePart>> = mutableStateMapOf(),

    internal val languageScope:Box<PLScope> = if(SettingsUtil.isDiffSyntaxHighlightEnabled()) Box(PLScope.AUTO) else Box(PLScope.NONE)

) {
    fun getAndUpdateScopeIfIsAuto(fileNameForGuessLangScope: String, scope: PLScope = languageScope.value): PLScope {
        val scope = if(scope == PLScope.AUTO) {
            PLScope.guessScopeType(fileNameForGuessLangScope)
        } else {
            scope
        }

        languageScope.value = scope

        return scope
    }


    // true means changed and scope valid, false means no change, null means changed but newScope is invalid
    fun changeScope(newScope: PLScope) : Boolean? {
        // no change
        if(languageScope.value == newScope) {
            return false
        }

        // update
        languageScope.value = newScope

        // if new language scope is invalid, release then return
        return if(isLanguageScopeInvalid(newScope)) null else true
    }


    private fun isLanguageScopeInvalid(languageScope: PLScope) : Boolean {
        if(PLScope.scopeTypeInvalid(languageScope)) {
            // clean

            // remove cached styles
            operateStylesMapWithWriteLock { it.clear() }

            // release highlighter
            hunks.forEachBetter { it.hunkSyntaxHighlighter.release() }

            return true
        }

        return false
    }


    private var cachedFileName:String? = null
    fun fileName() = cachedFileName ?: getFileNameFromCanonicalPath(relativePathUnderRepo).let { cachedFileName = it; it }

//    private var cachedNoMoreMemToaster: OneTimeToast? = null
//    private var cachedSyntaxHighlightEnabledState: State<Boolean>? = null
    // `oneTimeNoMoreMemNotify` should ensure only call once
    fun startAnalyzeSyntaxHighlight(noMoreMemToaster: OneTimeToast) {
//        cachedNoMoreMemToaster = noMoreMemToaster
//        cachedSyntaxHighlightEnabledState = syntaxHighlightEnabled
        if(syntaxDisabledOrNoMoreMem(noMoreMemToaster)) {
            return
        }

        // set theme first
        PLTheme.updateThemeByAppTheme()
        // get language scope of file
        val languageScope = getAndUpdateScopeIfIsAuto(fileName())

        // check language scope valid or not
        if(isLanguageScopeInvalid(languageScope)) {
            return
        }

        for(h in hunks) {
            h.hunkSyntaxHighlighter.analyze(languageScope, noMoreMemToaster)
        }
    }

    fun syntaxDisabledOrNoMoreMem(noMoreMemToaster: OneTimeToast): Boolean {
        // only clear current diffItemSaver, but memory maybe still intense,
        //   because other diffItemSaver and their styles still in the memory
        if(
            // user can select language scope even disabled, so this condition should remove
//            !SettingsUtil.isDiffSyntaxHighlightEnabled()
//            ||
            noMoreHeapMemThenDoAct {
                noMoreMemToaster.show(StrCons.syntaxHightDisabledDueToNoMoreMem)
            }
        ) {
            operateStylesMapWithWriteLock { it.clear() }
            return true
        }

        return false
    }

    fun <T> operateStylesMapWithWriteLock(act: (MutableMap<String, List<LineStylePart>>) -> T):T {
        return stylesMapLock.write { act(stylesMap) }
    }

    fun <T> operateStylesMapWithReadLock(act: (MutableMap<String, List<LineStylePart>>) -> T):T {
        return stylesMapLock.read { act(stylesMap) }
    }


    //获取实际生效的文件大小
    //ps:如果想判断文件大小有无超过限制，用此方法返回值作为 isFileSizeOverLimit() 的入参做判断即可
    fun getEfficientFileSize():Long {
        return if(newFileSize>0) newFileSize else oldFileSize
    }


    /**
     * 为line生成假索引，有可能会用来判断一些东西，目前只用来在预览diff内容时首行加top padding
     */
    fun generateFakeIndexForGroupedLines() {
        for(h in hunks) {
            //每个hunk的索引都应从0开始数（这里-1没写错，写成-1后面直接用++index一路往下走就行，省事）
            var index = -1

            for((_, lines) in h.groupedLines) {
                //顺序是context/del/add
                lines.get(Diff.Line.OriginType.CONTEXT.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.CONTEXT_EOFNL.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.DELETION.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.DEL_EOFNL.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.ADDITION.toString())?.let { it.fakeIndexOfGroupedLine = ++index }
                lines.get(Diff.Line.OriginType.ADD_EOFNL.toString())?.let { it.fakeIndexOfGroupedLine = ++index }

            }
        }
    }

}

class PuppyHunkAndLines(
    val diffItemSaver: DiffItemSaver
) {
    var hunk:PuppyHunk=PuppyHunk();
    var lines:MutableList<PuppyLine> = mutableListOf()

    // add/deleted lines of hunk
    var addedLinesCount:Int = 0
    var deletedLinesCount:Int = 0

    // {lineKey: PuppyLine}
    val keyAndLineMap: MutableMap<String, PuppyLine> = mutableMapOf()

    //根据行号分组
    //{lineNum: {originType:line}}, 其中 originType预期有3种类型：context/del/add
    var groupedLines:TreeMap<Int, Map<String, PuppyLine>> = TreeMap()

    // {lineKey: IndexModifyResult}
    private val modifyResultMap:MutableMap<String, IndexModifyResult> = mutableMapOf()

    // {linNum: Unit}, if map.get(lineNum) != null, means already showed add or del line as context, need not show one more
    // add and del only difference at end has "/n" or not, in that case, show 1 of them as context
    // 同一行，包含添加和删除，区别只在于末尾是否有换行符，仅显示对应行号一次，且类型为context
    private val mergedAddDelLine:MutableSet<Int> = mutableSetOf()

    val hunkSyntaxHighlighter = HunkSyntaxHighlighter(this)


    class MergeAddDelLineResult (
        //是否已经显示过此行，若已显示过，不会在显示，例如第12行只有末尾是否有换行符的区别，遍历到+12时，显示12行作为context，下次遍历到-12，则直接不显示
        // if already showed this line as context, next time same line will not showed again, eg: if +12 and -12 only diff at end of line has "\n" or not, then only show context line 12 once
        // instead by field `data`
//        val alreadyShowedAsContext:Boolean,

        //是否需要显示此行为context
        // need show this line as context or not, if not, will show origin line, else set line origin type to context, then show it
        val needShowAsContext:Boolean,

        // if need show data as context, set text to this field, else set it to null
        // 如果这行已经作为上下文显示过，设其为null，否则设置上原本add或del的内容
        val line:PuppyLine?=null,
    )

    /**
     * should clear caches if page re-render
     */
    fun clearCachesForShown() {
        mergedAddDelLine.clear()
        modifyResultMap.clear()
    }


    private var cachedLinesString:String? = null
    // make sure call this after all hunk's lines ready
    fun linesToString(forceRefreshCache:Boolean = false) : String {
        if(forceRefreshCache.not() && cachedLinesString != null) {
            return cachedLinesString!!
        }

        val sb = StringBuilder()
        for (i in lines) {
            sb.append(i.getContentNoLineBreak()).append('\n')
        }
        return sb.toString().let { cachedLinesString = it; it }
    }


    /**
     * @param changeType files change type
     */
    fun addLine(puppyLine: PuppyLine, changeType:String) {
        // order is important
        lines.add(puppyLine)
        addLineToGroup(puppyLine)
        linkCompareTargetForLine(puppyLine, changeType)
    }

    fun addLineToGroup(puppyLine: PuppyLine) {
        val lineNum = puppyLine.lineNum
        val line = groupedLines.get(lineNum)
        if(line==null) {
            val map = mutableMapOf<String, PuppyLine>()
            map.put(puppyLine.originType, puppyLine)
            groupedLines.put(lineNum, map)
        }else {
            (line as MutableMap).put(puppyLine.originType, puppyLine)
        }

    }

    /**
     * must call `addLineToGroup()` before call this method, cause it depend that `groupedLine`
     *
     */
    //20250607 add: for compare line number not equals, but content similar line
    //20250607新增: 实现比较行号不同但内容实际相关的行
    @Deprecated("this method has better performance but bad matching, recommend use `linkCompareTargetForLine` to instead of")
    fun linkCompareTargetForLineByContextOffset(puppyLine: PuppyLine, changeType:String) {
        // deleted line must at added lines up side; context needn't find a compare target;
        //  so, only added line need handle, when find the compare target (related deleted line), update the deleted line as well
        // 删除行和添加行都不需要找比较目标，仅添加行需要找，找到后把对应的删除行也关联上
        if(changeType == Cons.gitStatusModified && puppyLine.originType == PuppyLineOriginType.ADDITION) {
            var foundDel = false
            var size = lines.size
            while (--size >= 0) {
                val ppLine = lines[size]
                if(ppLine.originType == PuppyLineOriginType.CONTEXT) {
                    if(foundDel) {
                        val guessedRelatedLineNum = ppLine.oldLineNum - ppLine.newLineNum + puppyLine.newLineNum
                        val guessedLine = groupedLines.get(guessedRelatedLineNum)?.get(PuppyLineOriginType.DELETION)
                        if(guessedLine != null && guessedLine.compareTargetLineKey.isBlank()) {
                            guessedLine.compareTargetLineKey = puppyLine.key
                            puppyLine.compareTargetLineKey = guessedLine.key
                        }
                    }

                    // if found context, it's finished, this line if has a matched deleted line, already handle at upside, else, none matched with it
                    break
                }else if(ppLine.originType == PuppyLineOriginType.DELETION) {
                    foundDel = true
                }
            }
        }

        keyAndLineMap.put(puppyLine.key, puppyLine)
    }

    fun linkCompareTargetForLine(puppyLine: PuppyLine, changeType:String) {
        // deleted line must at added lines up side; context needn't find a compare target;
        //  so, only added line need handle, when find the compare target (related deleted line), update the deleted line as well
        // 删除行和添加行都不需要找比较目标，仅添加行需要找，找到后把对应的删除行也关联上
        if(changeType == Cons.gitStatusModified && deletedLinesCount > 0 && puppyLine.originType == PuppyLineOriginType.ADDITION) {
            var maxMatchedLine: PuppyLine? = null
            var maxRoughMatchCnt = 0

            for(line in lines) {
                // if old line's roughly matched count less than target, try matching it with new line
                // if remove `line.roughlyMatchedCount < targetRoughlyMatchedCount` can be better for matching(both strings have more matched chars), but bad for performance, better don't remove this condition, if want to better matching, you can increase `targetRoughlyMatchedCount`
                // 如果移除 `line.roughlyMatchedCount < targetRoughlyMatchedCount` ，可能会提高匹配率，让相同字符更多的两个字符串互相关联，但会降低性能，最好不要移除此判断条件，而是通过提高 `targetRoughlyMatchedCount` 来增加匹配率
                if(line.originType == PuppyLineOriginType.DELETION && line.roughlyMatchedCount < targetRoughlyMatchedCount) {
                    val roughMatchCnt = CmpUtil.roughlyMatch(puppyLine.getContentNoLineBreak(), line.getContentNoLineBreak(), targetRoughlyMatchedCount)
                    // these two strings matched more chars than the old two lines,
                    //  so, unlink old lines and link new lines
                    if((roughMatchCnt > maxRoughMatchCnt && roughMatchCnt > line.roughlyMatchedCount)

                        // current line never linked any other lines, link it first, then will unlink if have better matched target
                        // 当前遍历到的行没关联任何行，先关联下，后面如果有更合适的再解除关联
                        || (maxMatchedLine == null && line.compareTargetLineKey.isBlank())
                    ) {
                        maxMatchedLine = line
                        maxRoughMatchCnt = roughMatchCnt

                        // if enable this break, can be improve performance, better keep this and adjust `targetRoughlyMatchedCount` to control better matching or better performance
                        // 如果在这break，可提高性能，最好启用此代码，然后通过调整 `targetRoughlyMatchedCount` 来提高匹配率
                        if(maxRoughMatchCnt >= targetRoughlyMatchedCount) {
                            break
                        }
                    }
                }
            }

            // unlink old and line new lines
            maxMatchedLine?.let { line ->
                // unlink old lines
                val oldCompareTargetLineKey = line.compareTargetLineKey
                if(oldCompareTargetLineKey.isNotBlank()) {
                    keyAndLineMap.get(oldCompareTargetLineKey)?.let {
                        it.compareTargetLineKey = ""
                        it.roughlyMatchedCount = 0
                    }
                }

                // link new lines
                line.compareTargetLineKey = puppyLine.key
                puppyLine.compareTargetLineKey = line.key
                line.roughlyMatchedCount = maxRoughMatchCnt
                puppyLine.roughlyMatchedCount = maxRoughMatchCnt
            }
        }

        keyAndLineMap.put(puppyLine.key, puppyLine)
    }


    /**
     * 仅对类型为add或del的行调用此函数，若返回true，代表两者除了末尾换行符没区别，这时将其转换为context显示，
     *
     * TODO: 目前仅发现过相同行号的两行末尾换行符不同，如果以后发现有不同行号的行，则需要改用 `keyAndLineMap` 取代 `groupedLine` 做判断，`mergedAddDelLine`也需要改成依赖line key而不是行号的，可以如果发现匹配，则把两个line key都添加上
     * TODO: only found same line num with line break not match, if found difference line number with content all matched but line break, need use `keyAndLineMap` replaced `groupedLine` to check, also need change the `mergedAddDelLine`, if matched, add two key of liens into it
     */
    fun needShowAddOrDelLineAsContext(lineNum: Int):MergeAddDelLineResult {
        val groupedLine = groupedLines.get(lineNum)
        val add = groupedLine?.get(PuppyLineOriginType.ADDITION)
        val del = groupedLine?.get(PuppyLineOriginType.DELETION)
        if(add!=null && del!=null && add.getContentNoLineBreak().equals(del.getContentNoLineBreak())) {
            val alreadyShowed = mergedAddDelLine.add(lineNum).not()

            return MergeAddDelLineResult(
                needShowAsContext = true,
                line = if(alreadyShowed) null else del.copy(originType = Diff.Line.OriginType.CONTEXT.toString()),
            )
        }else {
            return MergeAddDelLineResult(needShowAsContext = false)
        }
    }


//    fun needShowAddOrDelLineAsContext_2(lineNum: Int):Boolean {
//        val groupedLine = groupedLines.get(lineNum)
//        val add = groupedLine?.get(Diff.Line.OriginType.ADDITION.toString())
//        val del = groupedLine?.get(Diff.Line.OriginType.DELETION.toString())
//        return add!=null && del!=null && add.getContentNoLineBreak().equals(del.getContentNoLineBreak())
//    }

    fun getModifyResult(line: PuppyLine, requireBetterMatchingForCompare:Boolean, matchByWords:Boolean):IndexModifyResult? {
        // Context need not compare yet
        // Context默认并不比较，就算比较，Context也只有一种颜色，所以无需查询其比较结果
        if(line.originType == PuppyLineOriginType.CONTEXT) {
            return null
        }

        val r = modifyResultMap.get(line.key)

        if(r != null) {
            return r
        }

        // r is null, try generate
//        val line = keyAndLineMap.get(line.key) ?: return null

        // invalid key
        if(line.compareTargetLineKey.isBlank()) {
            return null
        }

        val cmpTarget = keyAndLineMap.get(line.compareTargetLineKey) ?: return null

        val add = if(line.originType == PuppyLineOriginType.ADDITION) line else cmpTarget
        val del = if(line.originType == PuppyLineOriginType.ADDITION) cmpTarget else line

        val modifyResult2 = CmpUtil.compare(
            add = StringCompareParam(add.getContentNoLineBreak(), add.getContentNoLineBreak().length),
            del = StringCompareParam(del.getContentNoLineBreak(), del.getContentNoLineBreak().length),

            //为true则对比更精细，但是，时间复杂度乘积式增加，不开 O(n)， 开了 O(nm)
            requireBetterMatching = requireBetterMatchingForCompare,
            matchByWords = matchByWords,

            //20250210之后：我发现调换后，又有很多add在前，del在后匹配率更高的情况，所以我觉得没必要调换了，可能匹配率差不太多，调换反而影响性能
            //(20250210之前)我发现del在前面add在后面匹配率更高，所以swap传true
//                swap = true
        )

        modifyResultMap.put(line.key, modifyResult2)
        modifyResultMap.put(line.compareTargetLineKey, modifyResult2)

        return modifyResult2
    }

    fun clearStyles() {
        diffItemSaver.operateStylesMapWithWriteLock { styleMap ->
            lines.forEachBetter {
                styleMap.remove(it.key)
            }
        }
    }
}

class PuppyHunk {
    /**
     * 参见 `hunk_header_format.md`
     */
    var header:String=""

    private var cachedHeader:String? = null

    //若不trimEnd()，末尾有空行，影响排版，感觉像多了padding，不好看
    fun cachedNoLineBreakHeader() = (cachedHeader ?: header.trimEnd().let { cachedHeader = it; it });

}

data class PuppyLine(
    var key:String = getShortUUID(),

    // the default compare target, it was match the compare target with line num, but sometimes line number same content totally non-related, in that case need calculate the offset the pair them
    //默认和哪行比较，过去是用相同行号作为一对比较的add/del，但有时行号相同，内容完全无关，这时需要计算偏移量，然后关联实际相关的行号，这里以行key为关联替代行号以简化处理流程
    var compareTargetLineKey:String = "",

    // means at least has how many chars same with `compareTargetLineKey`'s related line
    var roughlyMatchedCount:Int=0,

    // group line时，按行号把不同origin type的都放一组，实际上没索引，所以用这个生成一个索引替代
    var fakeIndexOfGroupedLine:Int = 0,

    var originType:String="",  //这个当初实现的时候考虑不周，既然原始类型是char我为什么要用String存呢？
    var oldLineNum:Int=-1,
    var newLineNum:Int=-1,
    var contentLen:Int=0,
    var content:String="",  //根据字节数分割后的内容

//    var rawContent:String="";  //原始内容
    var lineNum:Int=1,  // getLineNum(PuppyLine) 的返回值，实际的行号
    var howManyLines:Int=0  // content里有多少行
//    private val lines:MutableList<PuppyLine> = mutableListOf();  //针对content处理，把content的每个行都存成了一个PuppyLine（content默认只有多个连续行的第一个行有行号
//
//    /**
//     * 由于libgit2会把多个连续行合并成一个，只有第一个显示行号，所以需要处理下
//     * 注意：这个方法的正确性有待验证。
//     */
//    fun getLines():MutableList<PuppyLine> {
//        if (lines.isNotEmpty()) {
//            return lines
//        }
//        //TODO 把这个diff整清楚
//        if(debugModeOn) {
//            println("startLine=${lineNum}，多少行："+howManyLines)
//            println("content分割出多少行:"+content.lines().size)
//        }
//        lines.add(this)
////
////        //只处理 上下文（白色）和 新增（绿色）和 删除（红色）三种类型，其他的比如添加删除文件末尾行之类的不做处理
////        if(originType != Diff.Line.OriginType.CONTEXT.toString()
////            && originType != Diff.Line.OriginType.ADDITION.toString()
////            && originType!=Diff.Line.OriginType.DELETION.toString()
////        ) {
////            lines.add(this)
////        }else {
////            var cnt = lineNum
//////        println(content.lines().size-1)
////            val cls = content.lines()
////            for((idx, it) in cls.withIndex()) {
////                //多余的行。判断条件为“最后一行且为空”，可能是 lines()函数返回的空行，比如 abc\n，lines() 会返回两行，但有时候这和函数又不会返回多余的行，我凌乱了，目前先这样吧
////                if(idx==cls.size-1 && it.isEmpty()){
////                    break
////                }
////                val p = PuppyLine()
////                p.originType = originType
////                p.lineNum=cnt++
////                p.content = it
////                lines.add(p)
////            }
////        }
//
//        return lines
//    }

) {
    companion object {
        fun mergeStringAndStylePartList(stringPartList: List<IndexStringPart>, stylePartList: List<LineStylePart>, modifiedBgColorSpanStyle: SpanStyle): List<LineStylePart> {
            val retStylePartList = mutableListOf<LineStylePart>()
            val stringPartMutableList = stringPartList.toMutableList()
            stylePartList.forEachBetter { stylePart ->
                var start = stylePart.start
                val iterator = stringPartMutableList.iterator()
                while (iterator.hasNext()) {
                    val stringPart = iterator.next()
                    val reachedEnd = stringPart.end >= stylePart.end
                    val end = if(reachedEnd) stylePart.end else stringPart.end
                    retStylePartList.add(
                        LineStylePart(
                            start = start,
                            end = end,
                            style = if(stringPart.modified) stylePart.style.merge(modifiedBgColorSpanStyle) else stylePart.style
                        )
                    )

                    start = end
                    iterator.remove()

                    if(reachedEnd) {
                        if(start < stringPart.end) {
                            stringPartMutableList.add(0, IndexStringPart(start, stringPart.end, stringPart.modified))
                        }
                        break
                    }


                }
            }

            // make sure cover whole text
            val retStyleLastEndIndex = retStylePartList.last().end
            val lastStringPart = stringPartList.last()
            val stringPartLastEndIndex = lastStringPart.end
            if(retStyleLastEndIndex < stringPartLastEndIndex) {
                retStylePartList.add(
                    LineStylePart(
                        start = retStyleLastEndIndex,
                        end = stringPartLastEndIndex,
                        style = if(lastStringPart.modified) modifiedBgColorSpanStyle else MyStyleKt.emptySpanStyle
                    )
                )
            }

            return retStylePartList

        }
    }

    fun isEOF() = PuppyLineOriginType.isEofLine(this);

    private var contentNoBreak:String? = null
    fun getContentNoLineBreak():String {  // not safe for concurrency
        // replace eof to empty string to match UI behavior(display empty line instead of eof text like "\No New Line End Of File")
        return contentNoBreak ?: (if(!isEOF()) content.removeSuffix("\n").removeSuffix("\r") else "").let { contentNoBreak = it; it }
    }


    fun getAValidLineNum():Int {
        //哪个不是-1就返回哪个，和originType配合即可知道是删除还是新增行
        return if(newLineNum < 0) oldLineNum else newLineNum
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PuppyLine

        if (originType != other.originType) return false
        if (oldLineNum != other.oldLineNum) return false
        if (newLineNum != other.newLineNum) return false
        if (contentLen != other.contentLen) return false
        if (content != other.content) return false
        if (lineNum != other.lineNum) return false
        if (howManyLines != other.howManyLines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = originType.hashCode()
        result = 31 * result + oldLineNum
        result = 31 * result + newLineNum
        result = 31 * result + contentLen
        result = 31 * result + content.hashCode()
        result = 31 * result + lineNum
        result = 31 * result + howManyLines
        return result
    }


}

enum class DiffItemSaverType {
    TEXT,
    IMG,
}
