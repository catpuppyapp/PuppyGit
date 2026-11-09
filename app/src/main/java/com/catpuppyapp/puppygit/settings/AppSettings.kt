package com.catpuppyapp.puppygit.settings

import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.settings.SettingsCons.startPageMode_rememberLastQuit
import com.catpuppyapp.puppygit.settings.version.SettingsVersion
import kotlinx.serialization.Serializable

/**
 * 不要直接创建本类的实例！应该由SettingsUtil的初始化方法来创建本类的实例和配置文件！
 *
 */
@Serializable
data class AppSettings(
    /**
     * 警告：
     *  1 不要删除字段
     *  2 不要修改字段名称或类型
     *  3 不要修改字段默认值
     *
     * 仅建议：添加字段
     *
     * 不然可能出现兼容问题，用户必须清数据才能启动应用，这样是绝对不行的，另外，如果有必要，可执行配置项迁移，把迁移逻辑写到 SettingsUtil#migrateIfNeed 中即可。
     */


    //不要修改默认值！！！！为了节省io开销，现在只会往配置文件里写入和默认值不一样的值，如果把这里的默认值改了，外部是无法感知的！所以，要把默认值当作写死在配置文件里的值！如果想改默认值的话，要考虑和旧值是否兼容！

    //版本号是一定会写入配置文件的，因为这个起始值不是最新的，而是会在创建当前类的实例时更新成当时最新的配置项，这样的话，以后如果升级版本号，可以写一套迁移的逻辑来处理(如果有必要的话)。
    //default版本号最好和最新的版本号不一样，不然用对应Settings类的fromJson()方法会返回带最新版本号的实例，那迁移的版本号检测就无效了，不过执行迁移时用的是map,所以其实没这个问题
    var version:Int = SettingsVersion.commonStartVer,  //默认version不是最新，创建新设置项，需要设置一下

    //下面的值只有和默认值不同时才会写入配置文件，如果修改默认值，要考虑是否和旧设置项兼容。
    //全局设置
    var startPageMode:Int=startPageMode_rememberLastQuit,
    var lastQuitHomeScreen:Int= Cons.selectedItem_Repos,

    //是否初次使用，对第一次使用的人显示个按钮可长按显示功能的提示弹窗(后来改成设置用户名和邮箱的弹窗了)
    @Deprecated("改成存到prefman了")
    var firstUse:Boolean=true,

    //快照文件保存天数，快照文件夹内最后修改时间超过此天数的文件将在app启动时被自动删除
    var snapshotKeepInDays:Int = 3,

    //全局git设置
    //充当公用git配置文件，相当于电脑上的 ~/.gitconfig
    var globalGitConfig:GlobalGitConfig = GlobalGitConfig(),

    //各个页面的设置
    val files:Files = Files(),
    val editor:Editor = Editor(),
    var changeList:ChangeList = ChangeList(),

    @Deprecated("废弃了")
    val storageDir:StorageDir = StorageDir(),  //将来会出一个StorageDir页面，用来管理存储仓库的目录，有内部，有外部，还有ProgramData(puppygitdata，这个类型不一定展示，感觉没必要展示给用户，但实际在代码里存在)
    // var settings ? Settings页面的设置？叫这个名字好像不太好，再考虑下吧

    // storagePaths for clone
    @Deprecated("instead by StoragePaths")
    val storagePaths:MutableList<String> = mutableListOf(),
    // last selected of storage paths
    @Deprecated("instead by StoragePaths")
    var storagePathLastSelected:String="",

    @Deprecated("instead by `DiffSettings` same name field")
    var groupDiffContentByLineNum:Boolean = true,

    val diff:DiffSettings = DiffSettings(),

    /**
     * commit history page, tap load more, load how many items
     */
    var commitHistoryPageSize:Int = 50,
    /**
     * when loading more at commit history page, load how many items check once terminal signal
     *
     * this settings for avoid quit commit history page, background task still loading history,
     * don't set it too big, it make non-sense, and don't set it to small, it waste cpu
     */
    var commitHistoryLoadMoreCheckAbortSignalFrequency:Int= 1000,


    @Deprecated("instead by `PrefMan.Key` same name field")
    var theme:Int = 0,
    @Deprecated("instead by `PrefMan.Key` same name field")
    var logLevel:Char = 'w',
    @Deprecated("instead by `PrefMan.Key` same name field")
    var logKeepDays:Int = 3,

    /**
     * show/hide go to top/bottom buttons
     */
    var showNaviButtons:Boolean = false,

    /**
     * file history page size
     */
    var fileHistoryPageSize:Int = 10,

    val sshSetting:SshSettings=SshSettings(),

    /**
     * 主密码的hash，用来校验用户输入的主密码是否正确，若为空，则代表禁用主密码，将使用源代码中的公开密码加密用户密码
     */
    var masterPasswordHash:String = "",

    val timeZone: TimeZone = TimeZone(),

    var httpService:HttpServiceSettings = HttpServiceSettings(),


    val automation: AutomationSettings = AutomationSettings(),

    val devSettings: DevSettings = DevSettings(),

    /**
     * 提交历史页面是否使用RTL布局，因为提交树默认是镜像的，但我又实在不想单独写了提交树页面，
     * 所以添加一个按钮，点击可切换左右布局，RTL开启时提交树就可变成非镜像的了
     */
    var commitHistoryRTL: Boolean = false,

    /**
     * 是否显示提交历史树形图
     */
    var commitHistoryGraph: Boolean = true,

    /**
     * 提交信息模板，若为空，将自动生成，否则将根据模板自动生成提交信息，但有些特殊情况，比如merge或amend时，可能不会遵循模板
     */
    var commitMsgTemplate:String = "",

    /**
     * if on, will render markdown view, else, will show raw text with syntax highlighting
     */
    var commitMsgPreviewModeOn:Boolean = true,

    /**
     * if true, will use system fonts when rendering commit msg dialog
     */
    var commitMsgUseSystemFonts: Boolean = false,

    val httpSetting: HttpSettings = HttpSettings(),

    /**
     * key is repo id, value is pinned time in utc milliseconds
     * value can be got by call `System.currentTimeMillis()`
     */
    var pinnedRepos: MutableMap<String, Long> = mutableMapOf(),
) {

    override fun equals(other: Any?): Boolean {
        //直接比较地址，避免比较内容，内容有可能过多，比较起来会耗较多时间
        return this === other
    }
}
