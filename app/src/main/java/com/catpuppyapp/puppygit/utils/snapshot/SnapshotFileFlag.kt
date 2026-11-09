package com.catpuppyapp.puppygit.utils.snapshot

private const val editorFileSnapShotPrefix = "edit_file_"
private const val editorContentSnapShotPrefix = "edit_ctnt_"
private const val diffFileSnapShotPrefix = "diff_file_"

// e.g. "edit_file_BS_maybefilename"
enum class SnapshotFileFlag(val flag: String) {
    //其中file_ 代表快照是由文件创建的，content_代表文件是由用户修改过的内容创建的（就是在app内编辑还没保存到硬盘上的内容）
    //注：file_一般代表的是要写入的目标文件

    // editor file
    editor_file_BeforeSave("${editorFileSnapShotPrefix}BS"), //file_BeforeSave, 保存之前为源文件创建的备份，一般在保存文件前发生源文件可能被外部修改过的时候会创建
    editor_file_SimpleSafeFastSave("${editorFileSnapShotPrefix}SSFS"), //调用FsUtil#SimpleSafeFastSave时创建的源文件快照
    editor_file_NormalDoSave("${editorFileSnapShotPrefix}NDS"), //NormalDoSave，正常情况下按保存按钮保存文件时创建的文件快照
    editor_file_OnPause("${editorFileSnapShotPrefix}OP"), //在Activity#OnPause事件被触发时创建的快照，一般是在app切到后台或者compose出错app崩溃时创建的快照

    // editor content
    editor_content_SaveErrFallback("${editorContentSnapShotPrefix}SEF"),  //content_SaveErrFallback, 保存文件失败，把用户在app内修改的内容保存到快照目录
    editor_content_CreateSnapshotForExternalModifiedFileErrFallback("${editorContentSnapShotPrefix}CMEF"),  //保存时发现源文件被外部修改，于是对源文件创建拷贝，结果失败，于是放弃保存，转而为当前content创建快照，就是这个flag
    editor_content_InstantSnapshot("${editorContentSnapShotPrefix}IS"),  //content_InstantSnapshot, 即时保存的用户编辑内容
    editor_content_FileNonExists_Backup("${editorContentSnapShotPrefix}FNEB"),  //content_FileNonExistsBackup, 为已打开但后来不存在（比如被外部程序删除）的文件当前在app显示的内容创建的快照
    editor_content_FilePathEmptyWhenSave_Backup("${editorContentSnapShotPrefix}PEB"),  // 保存的时候，文件路径为空，但content不为空
    editor_content_SimpleSafeFastSave("${editorContentSnapShotPrefix}SSFS"),  // 调用FsUtil#SimpleSafeFastSave时创建的未保存内容快照
    editor_content_NormalDoSave("${editorContentSnapShotPrefix}NDS"),  // NormalDoSave，正常情况下按保存按钮保存文件时创建的内容快照
    editor_content_OnPause("${editorContentSnapShotPrefix}OP"),  //在Activity#OnPause事件被触发时创建的快照，一般是在app切到后台或者compose出错app崩溃时创建的快照
    editor_content_BeforeReloadFoundSrcFileChanged("${editorContentSnapShotPrefix}BRFC"),  //Reload之前发现源文件改变了！常见的发生情形是内部Editor打开了文件，然后用外部打开，然后点重载，就会发生这种情况，一般不用保存快照，但保存也没什么损失
    editor_content_BeforeReloadFoundSrcFileChanged_ReloadByBackFromExternalDialog("${editorContentSnapShotPrefix}BRBE"),  //在弹窗“文件被外部改变了，你可能想重载文件...”那个弹窗点击reload时，创建的内容快照，和顶栏的reload有所区分

    // diff screen edit file
    diff_file_BeforeSave("${diffFileSnapShotPrefix}BS")

    ;



    fun isEditorFileSnapShot():Boolean {
        return flag.startsWith(editorFileSnapShotPrefix)
    }

    fun isEditorContentSnapShot():Boolean {
        return flag.startsWith(editorContentSnapShotPrefix)
    }

    fun isDiffFileSnapShot():Boolean {
        return flag.startsWith(diffFileSnapShotPrefix)
    }

}
