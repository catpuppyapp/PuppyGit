package com.catpuppyapp.puppygit.settings

object SettingsCons {
    val startPageMode_rememberLastQuit = 1  //记住上次退出的页面
    val startPageMode_userCustom = 3  //用户指定启动页面

    val defaultConflictStartStr = "<<<<<<<"  //7个
    val defaultConfilctSplitStr = "======="  // 7 个等号
    val defaultConflictEndStr = ">>>>>>>"  //7个

    val defaultFontSize = 16
    val defaultLineNumFontSize = 10


    //注： config是为了匹配 .git/config 那个git配置文件
    val editor_defaultFileAssociationList = listOf(
        "*.md",
        "*.txt",
        "*.log",
        "*.markdown",
        "*.ini",
        "config",
        ".gitignore",
        ".gitconfig",
        ".gitmodules",
        ".gitattributes",
        "dockerfile",
        "makefile",
        "*.xsl",
        "vagrantfile"
    )
}
