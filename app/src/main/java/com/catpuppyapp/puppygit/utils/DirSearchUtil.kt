package com.catpuppyapp.puppygit.utils

import java.io.File

object DirSearchUtil {

    /**
     * 虚假的广度优先搜索，针对顶级目录和普通过滤功能性能一样（所以凑合用了一段时间，感觉也够用），
     * 但后续针对子目录的搜索并非广度优先，
     * 实际会深入到一个子目录的最底层后才会查找
     * 其他的子目录（同时每个子目录也同样会先对目录内所有文件和文件夹进行匹配，
     * 再分别深入其子目录，这个过程是递归的），所以并非真正的广度优先搜索。
     *
     * 这个算是针对深度优先搜索做了一些优化使其针对顶层目录匹配的效率和单层级目录的过滤功能性能一样。
     *
     * 这个函数的优点是容易做并发。
     */
    fun recursiveFakeBreadthFirstSearch(
        dir: File,
        match: (srcIdx:Int, srcItem: File) -> Boolean,
        matchedCallback: (srcIdx:Int, srcItem: File) -> Unit,
        canceled: () -> Boolean
    ) {
        if(canceled()) {
            return
        }

        val files = dir.listFiles()
        if(files == null || files.isEmpty()) {
            return
        }

        val subdirs = mutableListOf<File>()
        for((idx, f) in files.withIndex()) {
            if(canceled()) {
                return
            }

            if(match(idx, f)) {
                matchedCallback(idx, f)
            }

            if(f.isDirectory) {
                subdirs.add(f)
            }
        }

        //TODO 并发优化点：这里可改成基于协程的，性能应该至少能翻1倍，但要控制协程数量，4个左右就可以，不要太多，不然物理cpu核心占满，创建更多协程也没意义
        //遍历子目录
        for(sub in subdirs) {
            recursiveFakeBreadthFirstSearch(dir = sub, match = match, matchedCallback=matchedCallback, canceled = canceled)
        }
    }

    /**
     * 真正的广度优先搜索，会一层一层查找，先第一层，再第2层，再第3层，直到最后一层目录。
     *
     * 针对顶级目录的搜索和普通单层的过滤功能性能一样。
     *
     * 这个函数不太适合做成并发的，也不是不能，就是有点麻烦，若想做可以尝试：准备弄几个并发协程就创建几个subDirs列表，然后平均往每个列表添加条目，再分发给协程去遍历，一般没必要做
     */
    fun realBreadthFirstSearch(
        dir: File,
        match: (srcIdx:Int, srcItem: File) -> Boolean,
        matchedCallback: (srcIdx:Int, srcItem: File) -> Unit,
        canceled: () -> Boolean
    ) {
        if(canceled()) {
            return
        }

        val subDirs = mutableListOf<File>()
        addAllFilesToList(dir, subDirs)

        while (subDirs.isNotEmpty()) {
            val subDirsCopy = subDirs.toList()
            subDirs.clear()

            for((idx, f) in subDirsCopy.withIndex()) {
                if(canceled()) {
                    return
                }

                if(match(idx, f)) {
                    matchedCallback(idx, f)
                }

                if(f.isDirectory) {
                    addAllFilesToList(f, subDirs)
                }
            }
        }

    }

    private fun addAllFilesToList(
        dir:File,
        subDirs:MutableList<File>,
    ) {
        dir.listFiles()?.let {
            if(it.isNotEmpty()) {
                subDirs.addAll(it)
            }
        }
    }

}
