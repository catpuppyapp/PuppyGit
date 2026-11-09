package com.catpuppyapp.puppygit.constants

import com.catpuppyapp.puppygit.data.entity.StorageDirEntity
import com.catpuppyapp.puppygit.data.entity.common.BaseFields

object StorageDirCons {
    object Type {
        //粘贴和导入文件时检查，只有当处于类型为internal/external/programData的条目时，才可粘贴或导入。
        //Files页面不允许在根目录复制条目，但可点击进入任意子目录随便复制，包括orphan目录中的条目也可复制
        //Files页面不允许在根目录删除条目(换句话说：所有storageDir都不允许在Files页面删除)，但可点进任意子目录随便删除，
        //  包括orphan目录，不过如果删了目录，没删仓库本身，点击仓库时会报错，路径不存在，注意处理下这种情况，确保app不会崩溃

        val root=0  // 检测到此类型，列出所有parentId是它的id的storageDir条目，有且只有一个条目为这个类型

        //可有多个条目为此类型，但实际应该只需要一个，所以只会有一个，那就是PuppyGitData
        val programData=1  //检测到此类型，点击列出其fullPath实际路径下的条目

        //可有多个条目为此类型
        //这两种类型会列出在克隆仓库时的可选storageDir列表
        //用户可创建这两种类型的仓库，且除了默认的一个internal仓库外，其余皆可删除
        val internal=2    //检测到此类型，点击列出其fullPath实际路径下的条目
        val external=3    //检测到此类型，点击列出其fullPath实际路径下的条目

        //检测到此类型，点击列出所有repo的storageDirId为空的条目，
        // 类型为这个值的storageDir条目的fullpath只是摆设，并不指向实际目录，
        // 具体包含哪些子目录，仅取决于有哪些repo没设置storageDirId
        //有且只有一个条目为此类型
        //此类型只为兼容还没StorageDir之前的旧版创建的仓库，若是使用新版app，实际上，虽然存在此条目，但永远为空目录
        // 展示的时候可以检查下，如果类型为orphan且无关联的仓库条目，就不显示OrphanRepos条目就行了
        //注：关联此类型条目的仓库的storageDirId为空字符串或者为orphanRepos的id。正常来说不会有空字符串，迁移数据库时，
        // 默认值为orphanRepos的id，而后续创建的仓库，必然关联某个storageDir条目，所以，实际不会出现空字符串id的情况。
        //之前叫“orphan”，感觉不太合适，孤儿这词，如果真是孤儿的话，看到会不会感觉有些伤心？所以改成other了
        val other=4

    }

    object Status {
        val ok = Cons.dbCommonBaseStatusOk // 1
        val disable=2  //禁用，不算错误，用户可禁用某存储目录，被禁用的存储目录和其下的仓库将不会展示在Files和Repos和ChangeList页面，在克隆时也不可选择被禁用的存储目录作为仓库存储目录

        val fakeDel=3  //比disable更深度的禁用，在StorageDir管理目录也不会显示此类型的条目

        val err_dirNonExists=Cons.dbCommonBaseStatusErr+2  // 52，目录不存在
        val err_cantAccess=Cons.dbCommonBaseStatusErr+3  //53 无法访问，可能丢失了访问权限之类的

    }

    private const val separator = "/"

    object DefaultStorageDir {
        /**
         * 通过路径反查sd时，应排除rootDir和orphanDir，用rootDir一定会匹配成功，因为其路径为/，
         * 用orphanDir一定会匹配失败，因为它不是有效路径，而且，逻辑上来说，只有匹配失败的路径才会归类到orphanDir
         */
        val rootDir = StorageDirEntity(
            id="e526cc40419d43d59e466c",  //22位，和默认的20位有所区分，这样自动生成的就永远不会和这个冲突了
            name="",  //名字为空代码里好处理，逻辑上也通顺，因为virtualPath值应为 /name ，如果root的那么是非空，那路径就会变成 /root这样了，很奇怪，生成名字时也得特殊处理有点麻烦，留空一切问题就都不存在了
            fullPath = separator, // 这个 / 并不是系统的 /，而是虚拟根节点，所有StorageDir的根目录，只有root节点的路径值才是 /
            virtualPath = separator,
            type = Type.root,  //检测到这个类型，列出所有parentId是它的id的storageDir条目
            allowDel = Cons.dbCommonFalse,  //不允许删除
            parentId = "",  // root节点，没有parent
            baseFields = BaseFields(
                baseStatus = Status.ok,
                baseIsDel = Cons.dbCommonFalse,
                baseCreateTime = 0,  //1970-01-01 00:00:00
                baseUpdateTime = 0,
            )
        )

        //这个没法和旧版兼容，要么到时候废弃旧版目录，完全启用新版，需要考虑迁移；
        //要么将错就错，废弃此条目，以后就把 puppyGitRepos/PuppyGit-Data 作为appdata目录
        // 逻辑上迁移更合理，但实际上，将错就错更省事，反正puppyGitRepos也不允许删除，在里面弄个目录做 app data怎么了？完全没问题啊！给内置仓库目录一点小小的特殊，问题不大。
        const val puppyGitDirName = "PuppyGitData"
        @Deprecated("保留，不会删，但也不会用，因为和旧版不兼容，目前实际的puppyGitData目录在 puppyGitRepos下，不是作为StorageDir，而是作为其中的一个普通文件夹")
        val puppyGitDataDir = StorageDirEntity(
            id="d8b39e2837a84a518dc818",  //22位，和默认的20位有所区分，这样自动生成的就永远不会和这个冲突了
            name=puppyGitDirName,
            fullPath = "", // App启动时赋值
            virtualPath = "$separator$puppyGitDirName",
            type = Type.programData,  //检测到此类型，点击列出其fullPath实际路径下的条目
            allowDel = Cons.dbCommonFalse,
            parentId = rootDir.id,
            baseFields = BaseFields(
//                baseStatus = Status.ok,  //状态，是否已不存在或者被禁用啊之类的，禁用就不显示在仓库和Files页面了
                baseStatus = Status.fakeDel,  //状态，是否已不存在或者被禁用啊之类的，禁用就不显示在仓库和Files页面了
                baseIsDel = Cons.dbCommonTrue,  //这个状态，其实意义不大，和status含义冲突了，实现的时候最好写成“如果isDel为true就忽略status”，当然，保险起见，如果不放心，可同时把status设为fakeDel，那样不管用哪个判断，都会认为条目已删除
                baseCreateTime = 1, //1970-01-01 00:00:01
                baseUpdateTime = 1,
            )
        )
        //废弃，不兼容旧版
//        const val repoStorage1DirName = "RepoStorage1"
        //兼容旧版，这个Sd直接对应旧版的所有仓库的根目录
        const val puppyGitReposDirName = Cons.defaultAllRepoParentDirName
        val puppyGitRepos = StorageDirEntity(
            //20240527 交换了repoStorage1和otherRepos(原orphanRepos)的id，这样的话旧版数据库迁移的时候就直接指向repoStorage1的id，而已迁移的仓库也指向repoStorage1，最终结果就是之前已迁移的和即将迁移的所有旧版的仓库都关联上了这个条目
            id="d6c5b79ca14e42fb811fcc",  //22位，和默认的20位有所区分，这样自动生成的就永远不会和这个冲突了
            name=puppyGitReposDirName,
            fullPath = "", // App启动时赋值
            virtualPath = "$separator$puppyGitReposDirName",
            type = Type.internal,    //检测到此类型，点击列出其fullPath实际路径下的条目
            allowDel = Cons.dbCommonFalse,  //不允许删除
            parentId = rootDir.id,
            baseFields = BaseFields(
                baseStatus = Status.ok,
                baseIsDel = Cons.dbCommonFalse,
                baseCreateTime = 2,  //1970-01-01 00:00:02
                baseUpdateTime = 2,
            )
        )

        /**
         * 此sd存储3种类型的目录（实际上只有仓库目录）：
         * 1 通过fullpath反查sd时，没有任何仓库的sd与其fullpath匹配的仓库
         * 2 没有任何sd的id与其sdid匹配的仓库
         * 3 sdid为其id的仓库
         */
        //没有StorageDirId的仓库，统一放到这个目录，比如从旧版迁移而来的仓库，就没有StorageDirId，就会归类到这里
        //orphanRepos只有1个，只有没关联StorageDir的仓库才会被归类到orphan仓库，删除时，仅会移除orphanRepos关联的仓库的目录，并不会尝试删除orphanRepos目录本身，因为这个目录根本就不存在
        val otherDirName = "OtherRepos"
        val otherRepos = StorageDirEntity(
            id="3a5a4f7a3e7447fc96a395",  //22位，和默认的20位有所区分，这样自动生成的就永远不会和这个冲突了
            name=otherDirName,

            // 假fullPath，直接把其关联的所有仓库条目列出来，实际上点击条目跳转到的是仓库的fullPath，但会改成假路径，例如：“/OrphanRepos/RepoName/仓库下路径
            fullPath = "$separator$otherDirName",
            virtualPath = "$separator$otherDirName",

            type = Type.other,  //检测到这个type，不会去查询其fullPath条目，而是简单列出当前storageDir关联的所有仓库，若是internal/external，则会直接把对应目录fullPath的所有条目列出来，实际就是对目录调用listFiles()。
            allowDel = Cons.dbCommonFalse,  //不允许删除
            parentId = rootDir.id,
            baseFields = BaseFields(
                baseStatus = Status.ok,
                baseIsDel = Cons.dbCommonFalse,
                baseCreateTime = 3,  //1970-01-01 00:00:03
                baseUpdateTime = 3,
            )
        )

        val listForMatchPath = listOf(puppyGitDataDir, puppyGitRepos)
        val idListUnusedForMatchPath = listOf(rootDir.id, otherRepos.id)
        val listOfAllDefaultSds = listOf(rootDir,puppyGitDataDir, puppyGitRepos, otherRepos)
    }
}
