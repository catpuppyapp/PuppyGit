package com.catpuppyapp.puppygit.utils.snapshot

import com.catpuppyapp.puppygit.etc.Ret
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextEditorState
import com.catpuppyapp.puppygit.screen.shared.FuckSafFile

/**
 * 所有创建snapshot的方法最终都应调用这个接口的方法来执行创建快照的操作，这样方便管理，例如想禁止创建快照，只需修改本接口的方法实现即可。
 */
interface SnapshotCreator{

    /**
     * 为未写入硬盘的内容创建快照，就是你在editor编辑文件但还没保存的内容，本app会在保存文件失败时触发此机制，把内存中的内容写入到快照文件
     *
     * @return Ret<Pair<fileName, fileFullPath>>
     */
    fun createSnapshotByContentAndGetResult(
        srcFileName: String,
        fileContent: String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        flag: SnapshotFileFlag
    ): Ret<Pair<String, String>?>


    /**
     * 为文件创建快照。
     * srcDir: 源文件
     * targetDir: 目标目录
     * flag: 目标文件flag。
     *
     * @return Ret<Pair<fileName, fileFullPath>>
     */
    fun createSnapshotByFileAndGetResult(srcFile: FuckSafFile, flag:SnapshotFileFlag):Ret<Pair<String,String>?>
}
