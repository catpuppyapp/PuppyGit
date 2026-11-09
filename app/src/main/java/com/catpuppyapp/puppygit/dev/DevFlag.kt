package com.catpuppyapp.puppygit.dev

import com.catpuppyapp.puppygit.user.UserUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog
import java.io.File

//TODO before release:发布应用之前把这几个变量改成false
@Deprecated("用 `UserUtil.isPro()` 替代")
private val dev_ProModeOn = false

//是否启用未通过测试的feature，默认为假，但以后可设置个类似“实验性功能”的选项给用户，让用户可选择开启未充分测试的特性，不过好像没必要
// 目前20240530这个变量仅用来隐藏不想在发行版中显示的功能，同时每个功能对应一个`xxxxxTestPassed`变量，判断是否启用功能时用
// `启用未测试特性 or 对应功能是否测试通过` 来判断是否启用某些特性，这样我开发时只要把这个变量设为真，就能访问未测试功能，而发行时则把这个
// 变量设为假，但把对应测试通过的功能设为真，这样就可在发行版中启用已测试通过的特性，完美
var dev_EnableUnTestedFeature = false  //release版此变量应设为假，具体是否启用未测试特性应由对应功能的开关变量控制；开发模式下一般此变量为真以方便访问所有功能

//开发时设置的是否开启debug模式的变量，和用户态的debugModeOn分别独立，只要有一个为true，就当作debug模式开启，具体参见 `DevFlag#isDebugModeOn()`
private val dev_debugModeOn = false  //请用 `DevFlag#isDebugModeOn()` 来判断是否开启debug模式

//TODO 发布之前把这个变量设为true
fun isReleaseMode():Boolean {  //检查当前是否处于生产环境，因为以后可能根据各种flag变量判断，所以这里用函数而不是常量
//    return false  // test mode
    return true  // release mode
}

fun isDebugModeOn():Boolean {
    //开发者设置了debugModeOn 或 用户设置了debugModeOn，则debugMode is on
//    return dev_debugModeOn || AppModel.debugModeOn

//    return settings.logLevel == 'd'

    return MyLog.getCurrentLogLevel() == "d"
}

fun proFeatureEnabled(featureFlag:Boolean):Boolean {
    return UserUtil.isPro() && (dev_EnableUnTestedFeature || featureFlag)
}

fun featureEnabled(featureFlag: Boolean):Boolean {
    return dev_EnableUnTestedFeature || featureFlag
}

// for debug, don't enable when release, else, app running very slow and will got huge log, maybe
fun printIncrementalSyntaxHighlightText():Boolean {
    return false
}

/**
 * untested features or tested but not enough tested
 */
//TODO 测试功能完毕后设为true
val lfsTestPassed = false
val submoduleTestPassed = true
val ignoreWorktreeFilesTestPassed = true
val initRepoFromFilesPageTestPassed = true


val shallowAndSingleBranchTestPassed = true  //代表shallow clone和single branch功能是否测试通过
val tagsTestPassed = true  // tags管理功能是否测试通过
val detailsDiffTestPassed = true  //增量diff是否测试通过
val reflogTestPassed = true  //reflog
val stashTestPassed = true  //stash
val editorMergeModeTestPassed = true  // editor两处：1 从cl页面进入subeditor时是否以merge mode启动；2 editor页面菜单项merge mode功能
val editorSearchTestPassed = true  // editor search 功能
val commitsDiffCommitsTestPassed = true  //commit页面，弹窗输入两个提交号然后跳转到diff页面那个功能
val commitsDiffToLocalTestPassed = true  //commit页面，长按提交菜单项 "Diff To Local"
val commitsTreeToTreeDiffReverseTestPassed = true  //TreeToTreeChangelist页面顶栏 swap commits 按钮
val rebaseTestPassed = true
val resetByHashTestPassed = true
val diffToHeadTestPassed = true
val pushForceTestPassed = true

val cherrypickTestPassed = true
val createPatchTestPassed = true
val checkoutFilesTestPassed = true
fun treeToTreeBottomBarActAtLeastOneTestPassed() = cherrypickTestPassed || createPatchTestPassed || checkoutFilesTestPassed  // tree to tree页面的checkout/cherrypick/patch，至少一个测试通过才在tree to tree页面显示底栏

val applyPatchTestPassed = true
val overwriteExistWhenCreateBranchTestPassed = true
val dontCheckoutWhenCreateBranchAtCheckoutDialogTestPassed = true
val forceCheckoutTestPassed = true
val dontUpdateHeadWhenCheckoutTestPassed = true
val createRemoteTestPassed = true

val branchListPagePublishBranchTestPassed = true
val branchRenameTestPassed = true

val repoRenameTestPassed = true

val importReposFromFilesTestPassed = true

val editorFontSizeTestPassed = true  //editor字体大小调整（会保存，有设置项）
val editorLineNumFontSizeTestPassed = true  //editor行号字体大小调整（会保存，有设置项）
val editorHideOrShowLineNumTestPassed = true  //editor显示或隐藏行号（会保存，有设置项）
val editorEnableLineSelecteModeFromMenuTestPassed = true  //editor 从菜单开启行选择模式，若隐藏行号，只能由此开启选择模式（不保存）

val importRepoTestPassed = true

const val soraEditorComposeTestPassed = false
const val soraEditorActivityTestPassed = false

/**
 * below is bug
 */

//严格来说并没修复这个bug，但是选中区域，配合高亮关键字，work as expected，非常好，能高亮关键字，又有选中效果，两者结合，意外完美
val bug_Editor_SelectColumnRangeOfLine_Fixed = true  // editor，搜索，定位到某行，选中某个范围，有bug，跳转到对应行，会自动取消选中，应该是Editor实现有问题，那代码我暂时不想看，先禁用选中关键字功能
val bug_Editor_GoToColumnCantHideKeyboard_Fixed = true // editor，启动时或非启动时，定位到某列，强制弹出键盘，无法实现“打开文件定位光标到上次编辑行的上次编辑列但不弹出键盘”

// 影响1：editor，搜索，定位到某行，再按搜索，卡在原位，因为上次编辑列的变量被错误更新了导致搜索起始位置被重置到错误的列；（测试搜索时遇到很多次此问题，当时测试搜索的关键字为某行最后一个字符，长度为1，可尝试复现）
// 影响2：editor，定位到某行，列index被更新为其他行的index （推测有此问题，但测试很少，所以没注意到）
val bug_Editor_WrongUpdateEditColumnIdx_Fixed = false

//旋转屏幕，editor的undo stack会被清空，如果不清，会导致undo功能失效并且undo后编辑器的内容无法编辑，原因不明
// 无所谓了，这个redoStack怎么弄好像都有bug，不需要此变量了，不过保留此变量以提醒我这个bug还存在
// 20250428: 已经解决，改 AndroidManifest.xml 加属性让Activity在旋转屏幕后不重建就行了，妈的原来这么简单
//val bug_Editor_undoStackLostAfterRotateScreen_Fixed = true


/**
 * 调试用的flag文件
 *
 * 注：文件位置：仓库根目录下的PuppyGit-Data目录。
 * 注：如无特别说明，创建或修改文件后均需重启app生效。
 *
 *
 * flag files for debug
 *
 * note: all flag files under PuppyGit-Data
 * note: create or edit flag files need restart app for effect
 *
 */
object FlagFileName {
    /**
     * 开启调试模式，日志更详细，会记录debug级别的信息
     */
    const val enableDebugMode = "debugModeOn"

    /**
     * 启用所有未测试特性，即使标记为未测试通过
     */
    const val enableUnTestedFeature = "enableUnTestedFeatureBoom"

    /**
     * 启用编辑缓存，存在此文件或设置项启用editcache开关开启则启用editcache。
     * if this file exists or settings editcache switch to on, then will enable editcache
     */
    @Deprecated("use `settings.editor.editCacheEnable` instead")
    const val enableEditCache = "enableEditCache"

    /**
     * 若此文件存在则启用内容快照，否则是否启用内容快照取决于settings.editor.enableContentSnapshot
     * if this file exists then enable content snapshot, else demand on settings.editor.enableContentSnapshot
     */
    const val enableContentSnapshot = "enableContentSnapshot"

    /**
     * 若此文件存在则启用文件快照，否则是否启用文件快照取决于settings.editor.enableFileSnapshot
     * if this file exists then enable file snapshot, else demand on settings.editor.enableFileSnapshot
     */
    const val enableFileSnapshot = "enableFileSnapshot"

    const val disableGroupDiffContentByLineNum = "disableGroupDiffContentByLineNum"

    fun flagFileExist(flagFileName:String):Boolean {
        return File(AppModel.getOrCreatePuppyGitDataUnderAllReposDir().canonicalPath, flagFileName).exists()
    }
}
