package com.catpuppyapp.puppygit.dto

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.mutableStateListOf
import com.catpuppyapp.puppygit.constants.StrCons
import com.catpuppyapp.puppygit.dev.DevFeature
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.doActWithLockIfFree
import com.catpuppyapp.puppygit.utils.getFileNameFromCanonicalPath
import com.catpuppyapp.puppygit.utils.storagepaths.StoragePathsMan
import kotlinx.coroutines.sync.Mutex
import kotlinx.parcelize.Parcelize

@Parcelize
data class NameAndPath(
    val name: String = "",
    val path: String = "",
    val type: NameAndPathType = NameAndPathType.APP_ACCESSIBLE_STORAGES,
): Parcelable {
    companion object {
        fun genByPath(path:String, type: NameAndPathType, context: Context): NameAndPath{
            val name = if(type == NameAndPathType.REPOS_STORAGE_PATH || type == NameAndPathType.FIRST_REPOS_STORAGE_PATH) {
                StoragePathsMan.getItemName(path, context)
            }else {
                getFileNameFromCanonicalPath(path)
            }

            return NameAndPath(name, path, type)
        }

        suspend fun getListForFilesManager(
            context:Context,
            list: MutableList<NameAndPath>,
            lock: Mutex
        ) {
            doActWithLockIfFree(lock, "NameAndPath#getListForFiles()") {
                val newList = mutableListOf<NameAndPath>()

                // internal path external path inner path
                newList.add(
                    NameAndPath(
                        context.getString(R.string.internal_storage),
                        FsUtils.getInternalStorageRootPathNoEndsWithSeparator(),
                        type = NameAndPathType.FIRST_APP_ACCESSIBLE_STORAGES
                    )
                )

                newList.add(
                    NameAndPath(
                        context.getString(R.string.external_storage),
                        FsUtils.getExternalStorageRootPathNoEndsWithSeparator()
                    )
                )

                newList.add(
                    NameAndPath(
                        StrCons.appData,
                        FsUtils.getAppDataRootPathNoEndsWithSeparator()
                    )
                )

                if(AppModel.devModeOn) {
                    newList.add(
                        NameAndPath(
                            DevFeature.inner_data_storage,
                            FsUtils.getInnerStorageRootPathNoEndsWithSeparator()
                        )
                    )

                    // /storage/emulated/0/Android/data/package name/
                    AppModel.externalDataDir?.canonicalPath?.let {
                        newList.add(
                            NameAndPath(
                                DevFeature.external_data_storage,
                                it
                            )
                        )
                    }
                }


                // storage path
                newList.addAll(StoragePathsMan.get().storagePaths.mapIndexed { idx, it ->
                    genByPath(
                        it,
                        if(idx == 0) NameAndPathType.FIRST_REPOS_STORAGE_PATH else NameAndPathType.REPOS_STORAGE_PATH,
                        context
                    )
                })


                // repo path
                val repoDb = AppModel.dbContainer.repoRepository
                newList.addAll(repoDb.getAll(updateRepoInfo = false).mapIndexed { idx, it ->
                    NameAndPath(
                        it.repoName,
                        it.fullSavePath,
                        if(idx == 0) NameAndPathType.FIRST_REPO_WORKDIR_PATH else NameAndPathType.REPO_WORKDIR_PATH
                    )
                })

                list.apply {
                    clear()
                    addAll(newList)
                }
            }
        }
    }
}

class NameAndPathList(
    val updateLock: Mutex = Mutex(),
    val list: MutableList<NameAndPath> = mutableStateListOf(),
)

enum class NameAndPathType {

    /**
     * internal/external/inner storage path
      */
    APP_ACCESSIBLE_STORAGES,

    /**
     * repo storage paths, which selectable in the Clone screen
     */
    REPOS_STORAGE_PATH,

    /**
     * path of repos
     */
    REPO_WORKDIR_PATH,


    FIRST_APP_ACCESSIBLE_STORAGES,
    FIRST_REPOS_STORAGE_PATH,

    FIRST_REPO_WORKDIR_PATH
    ;

}
