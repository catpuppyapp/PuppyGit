package com.catpuppyapp.puppygit.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.LineNum
import com.catpuppyapp.puppygit.screen.shared.CommitListFrom
import com.catpuppyapp.puppygit.screen.shared.DiffFromScreen
import com.catpuppyapp.puppygit.screen.shared.FileChooserType
import com.catpuppyapp.puppygit.screen.shared.IntentHandler
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.cache.NaviCache

@Composable
fun AppScreenNavigator() {
    //初始化compose相关变量
    AppModel.init_3()

    //上面初始化成功，这里才能获取到navController
    val navController =AppModel.navController

//    val startScreen = Cons.selectedItem_Repos
    //初始启动页面的子页面（Repos/Files之类的）
//    val currentHomeScreen = rememberSaveable{ mutableIntStateOf(Cons.selectedItem_Repos) }
    val currentHomeScreen = rememberSaveable{ mutableIntStateOf(SettingsUtil.obtainLastQuitHomeScreen()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

//    val scope = ComposeHelper.getCoroutineScope()

//    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val editorPageLastFilePath = rememberSaveable { mutableStateOf("")}

    val repoPageListState = rememberLazyListState()
//    val filePageListState = StateUtil.getRememberLazyListState()
    //网上拷贝的声称能记住滚动状态的lazyColumn listState，实际上，没有用，我记得默认的listState是能记住的，现在不能记住，可能是bug
//    val repoPageListState = rememberForeverLazyListState("repo")
//    val filePageListState = rememberForeverLazyListState("file")
    //好像lazyColumn用不了这个，只有Column能用
//    val reposPageScrollState = rememberScrollState()
//    val filesPageScrollState = rememberScrollState()

//    val haptic = LocalHapticFeedback.current


    //x 20240419 已实现) 改成记住上次退出屏幕从配置文件读取
    val navStartScreen = Cons.nav_HomeScreen;

    val gotNewIntent = rememberSaveable { IntentHandler.gotNewIntent }

    //TODO 添加一级页面的数据，编辑器打开的文件列表，都存到state里，并提供一个setter，
    // setter会更新内存里的数据和数据库里的数据，setter参考currentPage的setCurrentPage()，
    // 所有需要使用一级页面数据的页面都使用这里创建的state，并调用相同的setter更新数据，
    // 这样，数据更新后不需传递，其他compose也能拿到最新的数据，只有当依赖某信息显示页面且这
    // 个信息是一次性的时候才在路由里传数据，例如需要用户id来显示某用户的信息，这种情况，就在路由url里传id

    NavHost(navController = navController, startDestination = navStartScreen) {
        composable(Cons.nav_HomeScreen) {
            HomeScreen(drawerState, currentHomeScreen, repoPageListState, editorPageLastFilePath)
        }
        //注意：带返回箭头的二级菜单用 navController.navigateUp() api返回，这个和navipopup有点不同，如果在自己app里导航，两者一致，如果从外部app导航进本app，例如从其他app通过deeplink跳转到了本app，那么navigateUp会返回其他app，而popup则会尝试返回到操作系统为本app构建的虚拟栈中，按照这个理解，navigateUp期望返回进入本app的应用，而popup则期望返回本app内当前页面的上一页面（如果有的话）
        //如果不是通过外部app跳转进本程序，navigateUp不会退出本app，但popup会。
        //把popup理解成和返回键关联，naviup理解成和左上角返回箭头关联就行了。
        //简单来说，在左上角返回按钮那里用navigateUp()，其他情况想返回用popup就行了。
        /*
        参见：https://stackoverflow.com/questions/67245601/difference-between-navigateup-and-popbackstack
        * For starters: If you arrived at your current destination from within your app, they act exactly identical.

            ONLY if you arrived at the current destination with a deeplink from a different app, they will behave differently:

            navigateUp() will leave your app and return to the app that navigated to the deep link in your app.

            popBackStack() will attempt to go back one step in your backstack, and will not do anything if there is no backstack entry.
        * */
        composable(Cons.nav_CredentialManagerScreen+"/{remoteId}") {
            val remoteId = it.arguments?.getString("remoteId") ?: ""

            CredentialManagerScreen(
                remoteId= if(remoteId==Cons.dbInvalidNonEmptyId) "" else remoteId,
                naviUp = { navController.navigateUp() }
            )
        }
        composable(Cons.nav_DomainCredentialListScreen) {
//            val remoteId = it.arguments?.getString("remoteId") ?: ""

            DomainCredentialListScreen(
//                remoteId= if(remoteId==Cons.dbInvalidNonEmptyId) "" else remoteId,
                naviUp = { navController.navigateUp() }
            )
        }
        /*
         * composable("profile/{userId}") { backStackEntry ->
            Profile(navController, backStackEntry.arguments?.getString("userId"))
            }
         */
        composable(Cons.nav_CommitListScreen + "/{repoId}/{isHEAD}/{from}/{fullOidCacheKey}/{shortBranchNameCacheKey}") {
            val fullOidCacheKey = it.arguments?.getString("fullOidCacheKey") ?: ""
            val shortBranchNameCacheKey = it.arguments?.getString("shortBranchNameCacheKey") ?: ""

            CommitListScreen(
                repoId = it.arguments?.getString("repoId")?:"",
                isHEAD = it.arguments?.getString("isHEAD") != "0",
                fullOidCacheKey = fullOidCacheKey,
                shortBranchNameCacheKey = shortBranchNameCacheKey,
                from = CommitListFrom.fromCode(it.arguments?.getString("from")!!)!!,
                naviUp = {
                    navController.navigateUp()

                    NaviCache.del(fullOidCacheKey)
                    NaviCache.del(shortBranchNameCacheKey)
                },
            )
        }
        composable(Cons.nav_ErrorListScreen + "/{repoId}") {
            ErrorListScreen(
                it.arguments?.getString("repoId")?:"",
                naviUp = { navController.navigateUp() },
            )
        }
        composable(Cons.nav_CloneScreen + "/{repoId}") {
            CloneScreen(
                it.arguments?.getString("repoId") ?: "",
                naviUp = { navController.navigateUp() },
            )
        }
        composable(Cons.nav_DiffScreen + "/{repoId}/{fromTo}/{treeOid1Str}/{treeOid2Str}/{isDiffToLocal}/{curItemIndexAtDiffableList}/{localAtDiffRight}/{fromScreen}/{diffableListCacheKey}/{isMultiMode}") {
            val diffableListCacheKey = it.arguments?.getString("diffableListCacheKey") ?: ""

            DiffScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                fromTo = it.arguments?.getString("fromTo") ?: "",
                treeOid1Str = it.arguments?.getString("treeOid1Str") ?: "",
                treeOid2Str = it.arguments?.getString("treeOid2Str") ?: "",
                localAtDiffRight = (it.arguments?.getString("localAtDiffRight")?.toInt() ?: 0) != 0,
                isDiffToLocal = (it.arguments?.getString("isDiffToLocal")?.toInt() ?: 0) != 0,
                fromScreen = DiffFromScreen.fromCode(it.arguments?.getString("fromScreen")!!)!!,
                diffableListCacheKey = diffableListCacheKey,
                isMultiMode = it.arguments?.getString("isMultiMode") == "1",
                curItemIndexAtDiffableItemList = try {
                    (it.arguments?.getString("curItemIndexAtDiffableList") ?: "").toInt()
                }catch (_:Exception) {
                    -1
                },
                naviUp = {
                    navController.navigateUp()

                    NaviCache.del(diffableListCacheKey)
                },

            )
        }

        composable(Cons.nav_IndexScreen) {
            IndexScreen(
                naviUp = { navController.navigateUp() },
            )
        }
        composable(Cons.nav_CredentialNewOrEditScreen+"/{credentialId}") {
            CredentialNewOrEdit(
                credentialId = it.arguments?.getString("credentialId") ?: "",
                naviUp = {
                    //(用withContext委托主线程执行返回就行了和showToast一样)不知道为什么，普通的naviup不行，直接就返回到home页面了，所以需要特别处理一下
//                            navController.navigate(Cons.nav_CredentialManagerScreen) {  //这段代码的意思是导航到credential页面，但返回的时候，返回到home页面(而不是导航到credential的credential_new_or_edit页面)
//                                popUpTo(Cons.nav_HomeScreen)
//                            }
                         navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_CredentialRemoteListScreen+"/{credentialId}/{isShowLink}") {
            CredentialRemoteListScreen(
                credentialId = it.arguments?.getString("credentialId") ?: "",
                //非0都是真
                isShowLink = if(it.arguments?.getString("isShowLink")=="0") false else true,
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_BranchListScreen+"/{repoId}") {
            BranchListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_TreeToTreeChangeListScreen+"/{repoId}/{commit1OidStrCacheKey}/{commit2OidStrCacheKey}/{commitForQueryParentsCacheKey}/{titleCacheKey}") {
            val commit1OidStrCacheKey = it.arguments?.getString("commit1OidStrCacheKey") ?: ""
            val commit2OidStrCacheKey = it.arguments?.getString("commit2OidStrCacheKey") ?: ""
            val commitForQueryParentsCacheKey = it.arguments?.getString("commitForQueryParentsCacheKey") ?: ""

            val titleCacheKey = it.arguments?.getString("titleCacheKey") ?: ""

            TreeToTreeChangeListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                commit1OidStrCacheKey = commit1OidStrCacheKey,
                commit2OidStrCacheKey = commit2OidStrCacheKey,

                //若不需要查parents，则传all zero oid在url中占位即可。
                commitForQueryParentsCacheKey = commitForQueryParentsCacheKey,
                titleCacheKey = titleCacheKey,
                naviUp = {
                    navController.navigateUp()

                    NaviCache.del(commit1OidStrCacheKey)
                    NaviCache.del(commit2OidStrCacheKey)
                    NaviCache.del(commitForQueryParentsCacheKey)
                    NaviCache.del(titleCacheKey)
                },
            )
        }
        composable(Cons.nav_RemoteListScreen+"/{repoId}") {
            RemoteListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_SubPageEditor+"/{goToLine}/{initMergeMode}/{initReadOnly}/{filePathKey}") {
            val filePathKey = it.arguments?.getString("filePathKey") ?: ""

            SubPageEditor(

                goToLine = try {
                    val l = it.arguments?.getString("goToLine") ?: ""
                    l.toInt()
                }catch (e:Exception) {
                    LineNum.lastPosition
                },
                initMergeMode = it.arguments?.getString("initMergeMode") == "1",  //传1开启mergeMode，否则关闭
                initReadOnly = it.arguments?.getString("initReadOnly") == "1",  //传1开启read only，否则关闭
                editorPageLastFilePath = editorPageLastFilePath,
                filePathKey = filePathKey,
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(filePathKey)
                },
            )
        }
        composable(Cons.nav_TagListScreen+"/{repoId}") {
            TagListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_ReflogListScreen+"/{repoId}") {
            ReflogListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_StashListScreen+"/{repoId}") {
            StashListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_SubmoduleListScreen+"/{repoId}") {
            SubmoduleListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }

        composable(Cons.nav_FileHistoryScreen+"/{repoId}/{fileRelativePathKey}") {
            val fileRelativePathKey = it.arguments?.getString("fileRelativePathKey") ?: ""

            FileHistoryScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                fileRelativePathKey = fileRelativePathKey,
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(fileRelativePathKey)
                },
            )
        }

        composable(Cons.nav_FileChooserScreen+"/{type}") {
            FileChooserScreen (
                //不要用getInt()，会报错，妈的，什么狗屁api
                type = FileChooserType.fromCode(it.arguments!!.getString("type")!!)!!,
                naviUp = {
                    navController.navigateUp()
                },
            )
        }

    }

    LaunchedEffect(gotNewIntent.value) {
        IntentHandler.requireHandleNewIntent()
    }
}
