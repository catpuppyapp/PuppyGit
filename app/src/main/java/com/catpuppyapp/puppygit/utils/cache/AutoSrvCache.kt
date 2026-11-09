package com.catpuppyapp.puppygit.utils.cache


object AutoSrvCache:CacheStoreImpl(){
    object Key {
        const val curPackageName = "cur_package_name"

    }

    /**
     * 用来更新当前显示的包名。
     *
     * 用途1：在push前会检查待push的仓库是否后当前显示的包名关联的仓库重叠，若重叠，仅push其他仓库，重叠的仓库待当前app退出后再push
     * 其他用途暂时没有，以后有也不一定更新此注释
     */
    fun setCurPackageName(packageName:String) {
        set(Key.curPackageName, packageName)
    }

    fun getCurPackageName():String {
        return getOrDefaultByType<String>(Key.curPackageName, default = {""})
    }
}
