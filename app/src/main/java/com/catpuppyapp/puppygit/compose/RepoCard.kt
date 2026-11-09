package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.constants.PageRequest
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.dev.lfsTestPassed
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.screen.functions.goToCloneScreen
import com.catpuppyapp.puppygit.screen.functions.goToCommitListScreen
import com.catpuppyapp.puppygit.screen.functions.goToErrScreen
import com.catpuppyapp.puppygit.screen.shared.CommitListFrom
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.LfsUtil
import com.catpuppyapp.puppygit.utils.Libgit2Helper
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.UIHelper
import com.catpuppyapp.puppygit.utils.copyAndShowCopied
import com.catpuppyapp.puppygit.utils.dbIntToBool
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import com.catpuppyapp.puppygit.utils.state.CustomStateSaveable
import com.github.git24j.core.Repository
import kotlinx.coroutines.delay


/**
 * 注意：除了比较重要的关于仓库状态的那行以外，其他都用尽量 outlined 那种图标，这样一看卡片，注意力就会首先看到状态
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RepoCard(
    itemWidth:Float,
    requireFillMaxWidth:Boolean,
    showBottomSheet: MutableState<Boolean>,
    curRepo: CustomStateSaveable<RepoEntity>,
    curRepoIndex: MutableIntState,
    repoDto: RepoEntity,
    repoDtoIndex:Int,
    itemSelected:Boolean,
    titleOnClick:(RepoEntity)->Unit,
    goToFilesPage:(path:String) -> Unit,
    requireBlinkIdx:MutableIntState,
    pageRequest:MutableState<String>,
    isSelectionMode:Boolean,
    pinnedRepos: Map<String, Long>,
    onClick: (RepoEntity) -> Unit,
    onLongClick:(RepoEntity)->Unit,
    copyErrMsg: (String) -> Unit,
    requireDelRepo:(RepoEntity)->Unit,
    doCloneSingle:(RepoEntity)->Unit,
    initErrMsgDialog:(RepoEntity, errMsg: String)->Unit,
    initCommitMsgDialog:(RepoEntity)->Unit,
    refreshPage:()->Unit,
    workStatusOnclick:(clickedRepo:RepoEntity, status:Int)->Unit,
) {
    val navController = AppModel.navController
    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current

    val inDarkTheme = Theme.inDarkTheme

    val repoNotReady = Libgit2Helper.isRepoStatusNotReady(repoDto)
    val repoErr = Libgit2Helper.isRepoStatusErr(repoDto)

    //如果仓库设了临时状态，说明仓库能正常工作，否则检查仓库状态
    //其实原本没判断临时状态，但是当仓库执行操作时，例如 fetching/pushing，gitRepoState会变成null，从而误认为仓库invalid，因此增加了临时状态的判断
    val repoStatusGood = !repoNotReady && (repoDto.tmpStatus.isNotBlank() || (repoDto.gitRepoState!=null && !repoErr))


    val cardColor = UIHelper.defaultCardColor()
    val highlightColor = remember(inDarkTheme) {if(inDarkTheme) Color(0xFF9D9C9C) else Color(0xFFFFFFFF)}

    val defaultFontWeight = remember { MyStyleKt.TextItem.defaultFontWeight() }

    val clipboardManager = LocalClipboardManager.current

    val setCurRepo = {
        //设置当前仓库（如果不将repo先设置为无效值，可能会导致页面获取旧值，显示过时信息）
        curRepo.value = RepoEntity()  // change state to a new value, if delete this line, may cause page not refresh after changed repo
        curRepo.value = repoDto  // update state to target value

        curRepoIndex.intValue = repoDtoIndex
    }

//    val lineHeight = 30.dp

    Column (
//        modifier = Modifier.fillMaxWidth(),
        modifier = if(requireFillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(itemWidth.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        MyCard(
            modifier = Modifier
                .padding(MyStyleKt.defaultItemPadding)
                .fillMaxWidth()

                //使卡片按下效果圆角，但和elevation冲突，算了，感觉elevation更有立体感比这个重要，所以禁用这个吧
//                .clip(CardDefaults.shape)  //使按下卡片的半透明效果符合卡片轮廓，不然卡片圆角，按下是尖角，丑陋

                .combinedClickable(
                    //只要仓库就绪就可启用长按菜单，不检查git仓库的state是否null，因为即使仓库为null，也需要长按菜单显示删除按钮，也不检查仓库是否出错，1是因为出错不会使用此组件而是另一个errcard，2是就算使用且可长按也仅显示删除和取消
//                    enabled = !repoNotReady,  //因为改成多选了，所以这个其实无所谓了，执行操作前会先检查仓库是否能执行操作
                    onClick = {
                        onClick(repoDto)
                    },
                    onLongClick = {
                        //震动反馈
//                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        setCurRepo()

                        //显示底部菜单
                        onLongClick(repoDto)
                    },
                )
//            .defaultMinSize(minHeight = 100.dp)

            ,

            //如果是请求闪烁的索引，闪烁一下
            containerColor = if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == repoDtoIndex) {
                //高亮2s后解除
                doJobThenOffLoading {
                    delay(UIHelper.getHighlightingTimeInMills())  //解除高亮倒计时
                    requireBlinkIdx.intValue = -1  //解除高亮
                }
                highlightColor
            } else {
                cardColor
            },
//        border = BorderStroke(1.dp, Color.Black),

        ) {
            RepoTitle(
                haptic = haptic,
                repoDto = repoDto,
                isSelectionMode = isSelectionMode,
                itemSelected = itemSelected,
                pinnedRepos = pinnedRepos,
                refreshPage = refreshPage,
                titleOnClick = titleOnClick,
                titleOnLongClick = onLongClick,
            )

            MyHorizontalDivider()

            // 若测试通过就不要显示这个按钮了
            if(lfsTestPassed) {
                // 测试的按钮，以后真测试通过的时候把这个删除
                Button(
                    onClick = {
//                        LfsUtil.makeGitLfsBinExecutable()
                        LfsUtil.test()
//                        LfsUtil.runGitLfs(repoDto.fullSavePath)
                    }
                ) {
                    Text("LFS TEST")
                }
            }

            //以下开始区分 正常仓库 和 出错的仓库(一般是克隆出错)
            if (Libgit2Helper.isRepoStatusNoErr(repoDto)) {
                Column(
                    modifier = Modifier.padding(start = 10.dp, top = 4.dp, end = 10.dp, bottom = 10.dp)
                ) {

                    //不等于NONE就显示状态，若状态为null，可能仓库文件夹被删了或改名了，这时提示invalid
                    //20240822 仓库执行fetching/pushing等操作时也会变成null，因此增加了临时状态的判断，仅当临时状态为空时，gitRepoState才可信，否则若设了临时状态，就当作仓库没问题（因为正在执行某个操作，所以肯定没损坏）
                    // 仓库的gitRepoState如果为null，临时状态那行(Status)会显示错误，所以这里就不需要显示这个状态了
                    if(!repoNotReady && repoDto.gitRepoState != null && repoDto.gitRepoState != Repository.StateT.NONE && repoDto.tmpStatus.isBlank()) {  //没执行任何操作，状态又不为NONE，就代表仓库可能出问题了
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Text(text = stringResource(R.string.state) + ": ")

                            InLineIcon(
                                icon = Icons.Outlined.Info,
                                tooltipText = stringResource(R.string.state)
                            )

                            ScrollableRow {
                                Text(
                                    //如果是detached，显示分支号，否则显示“本地分支:远程分支”
                                    text = repoDto.getRepoStateStr(activityContext),  //状态为null显示错误，否则显示状态
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = defaultFontWeight,
                                    modifier = MyStyleKt.ClickableText.modifier,

                                )
                            }
                        }
                    }

                    if(repoStatusGood) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Text(text = stringResource(R.string.repo_label_branch) + ": ")

                            InLineIcon(
                                icon = ImageVector.vectorResource(R.drawable.branch),
                                tooltipText = stringResource(R.string.repo_label_branch)
                            )

                            ScrollableRow {
                                ClickableText (
                                    //如果是detached，显示分支号，否则显示“本地分支:远程分支”
                                    text = if(repoStatusGood) {if(dbIntToBool(repoDto.isDetached)) Libgit2Helper.genDetachedText(repoDto.lastCommitHashShort) else Libgit2Helper.genLocalBranchAndUpstreamText(repoDto.branch, repoDto.upstreamBranch)} else "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = defaultFontWeight,
                                    modifier = MyStyleKt.ClickableText.modifier.clickable(enabled = repoStatusGood) {
                                        navController.navigate(Cons.nav_BranchListScreen + "/" + repoDto.id)
                                    },
                                )
                            }


                        }

                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        Text(text = stringResource(R.string.repo_label_last_update_time) + ": ")
                        InLineIcon(
                            icon = Icons.Filled.AccessTime,
                            tooltipText = stringResource(R.string.repo_label_last_update_time)
                        )
                        ScrollableRow {
                            Text(
                                text = repoDto.cachedLastUpdateTime(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = MyStyleKt.ClickableText.modifier,
                                fontWeight = defaultFontWeight
                            )
                        }
                    }

                    if(repoStatusGood) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Text(text = stringResource(R.string.repo_label_last_commit) + ": ")

                            InLineIcon(
                                icon = Icons.Filled.Commit,
                                tooltipText = stringResource(R.string.repo_label_last_commit)
                            )
                            ScrollableRow {
                                ClickableText (
                                    text = (if(repoStatusGood) repoDto.lastCommitHashShort ?: "" else "") +
                                            (repoDto.lastCommitDateTime.let { if(it.isBlank()) "" else " ($it)"})
                                    ,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = MyStyleKt.ClickableText.modifier.combinedClickable(
                                        enabled = repoStatusGood,
                                        onLongClick = {
//                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            copyAndShowCopied(activityContext, clipboardManager, repoDto.lastCommitHash)
                                        }
                                    ) {
                                        //打开当前仓库的提交记录页面，话说，那个树形怎么搞？可以先不搞树形，以后再弄
                                        goToCommitListScreen(
                                            repoId = repoDto.id,
                                            fullOid = "",  //这里不需要传分支名，会通过HEAD解析当前分支
                                            shortBranchName = "",
                                            isHEAD = true,
                                            from = CommitListFrom.FOLLOW_HEAD,

                                            )
                                    },
                                    fontWeight = defaultFontWeight

                                )
                            }
                        }


                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InLineIcon(
                                icon = Icons.AutoMirrored.Outlined.Message,
                                tooltipText = stringResource(R.string.msg)
                            )

                            ClickableText (
                                text = repoDto.getOrUpdateCachedOneLineLatestCommitMsg(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = defaultFontWeight,
                                modifier = MyStyleKt.ClickableText.modifier.combinedClickable(
                                    enabled = repoStatusGood,
                                ) {
                                    initCommitMsgDialog(repoDto)
                                },

                            )
                        }
                    }


                    //所有情况都显示status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        Text(text = stringResource(R.string.repo_label_status) + ": ")

                        InLineIcon(
                            icon = Icons.Filled.Info,
                            tooltipText = stringResource(R.string.repo_label_status)
                        )

                        ScrollableRow {

                            //如果不写入数据库的临时中间状态 pushing/pulling 之类的 不为空，显示中间状态，否则显示写入数据库的持久状态
                            val tmpStatus = repoDto.tmpStatus
                            if(repoErr || repoNotReady || tmpStatus.isNotBlank() || repoDto.workStatus == Cons.dbRepoWorkStatusUpToDate) {  //不可点击的状态
                                var nullNormalTrueUpToDateFalseError:Boolean? = null
                                val text = if(repoErr || (repoDto.gitRepoState==null && tmpStatus.isBlank())) { nullNormalTrueUpToDateFalseError = false; stringResource(R.string.error) }else tmpStatus.ifBlank { nullNormalTrueUpToDateFalseError = true; stringResource(R.string.repo_status_uptodate) }
                                Text(
                                    //若tmpStatus不为空，显示；否则显示up-to-date。注：日后若添加更多状态，就不能这样简单判断了
                                    text = text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = MyStyleKt.ClickableText.modifier,
                                    fontWeight = defaultFontWeight,
                                    //出错，红色；已是最新，绿色；"加载中..."之类的非点击临时状态，默认颜色。
                                    color = if(nullNormalTrueUpToDateFalseError == null) Color.Unspecified else if(nullNormalTrueUpToDateFalseError == true) MyStyleKt.TextColor.getHighlighting() else MyStyleKt.TextColor.error(),

                                    )
                            } else {  //可点击的状态
                                ClickableText (
                                    text = (
                                            if (repoDto.workStatus == Cons.dbRepoWorkStatusMerging
                                                || repoDto.workStatus==Cons.dbRepoWorkStatusRebasing
                                                || repoDto.workStatus==Cons.dbRepoWorkStatusCherrypicking
                                            ) {
                                                stringResource(R.string.require_actions)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusHasConflicts) {
                                                stringResource(R.string.repo_status_has_conflict)
                                            } else if(repoDto.workStatus == Cons.dbRepoWorkStatusNeedCommit) {
                                                stringResource(R.string.repo_status_need_commit)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusNeedSync) {
                                                stringResource(R.string.repo_status_need_sync)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusNeedPull) {
                                                stringResource(R.string.repo_status_need_pull)
                                            } else if (repoDto.workStatus == Cons.dbRepoWorkStatusNeedPush) {
                                                stringResource(R.string.repo_status_need_push)
                                            }else if (repoDto.workStatus == Cons.dbRepoWorkStatusNoHEAD) {
                                                //便于用户理解，不提示no head，提示no commit
                                                stringResource(R.string.no_commit)
                                            } else {
                                                ""  // 未克隆仓库可能会抵达这里
                                            }
                                            ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = MyStyleKt.ClickableText.modifier.clickable(enabled = repoStatusGood) {
                                        workStatusOnclick(repoDto, repoDto.workStatus)  //让父级页面自己写callback吧，省得传参
                                    },
                                    fontWeight = defaultFontWeight

                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        Text(text = stringResource(R.string.storage) + ": ")
                        InLineIcon(
                            icon = Icons.Outlined.Folder,
                            tooltipText = stringResource(R.string.storage)
                        )

                        ScrollableRow {
                            ClickableText (
                                text = repoDto.cachedAppRelatedPath(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = MyStyleKt.ClickableText.modifier.combinedClickable(
                                    onLongClick = { // long press will copy path
    //                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        clipboardManager.setText(AnnotatedString(repoDto.fullSavePath))
                                        Msg.requireShow(activityContext.getString(R.string.copied))
                                    }
                                ) {  // on click
                                    goToFilesPage(repoDto.fullSavePath)
                                },
                                fontWeight = defaultFontWeight
    
                            )
                        }
                    }


                    //未就绪不显示错误条目，因为显示里面也没错误，repo表有个专门的字段存储未就绪仓库条目的错误信息，会在另一个ErrCard显示，与此组件无关
                    if(!repoNotReady) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //错误信息不用检测仓库状态，因为显示错误信息只需要数据库中有对应条目即可，而正常情况下，如果有有效的错误信息，必然有数据库条目
                            val hasUncheckedErr = repoDto.latestUncheckedErrMsg.isNotBlank()

//                            Text(text = stringResource(R.string.repo_label_error) + ": ")
                            InLineIcon(
                                icon = Icons.Outlined.Dangerous,
                                tooltipText = stringResource(R.string.repo_label_error)
                            )
                            ClickableText (
                                text = if (hasUncheckedErr) repoDto.getCachedOneLineLatestUnCheckedErrMsg() else stringResource(R.string.repo_err_no_err_or_all_checked),
                                maxLines = 1,
                                color = if (hasUncheckedErr) MyStyleKt.ClickableText.getErrColor() else MyStyleKt.ClickableText.getColor(),
                                fontWeight = defaultFontWeight,
                                modifier = MyStyleKt.ClickableText.modifier.clickable {
                                    //show dialog when has unchecked error, else go to error list
                                    if (hasUncheckedErr) {
                                        // "repo:xxx\n\n err:errinfo"
                                        val errMsg = StringBuilder("${activityContext.getString(R.string.repo)}: ")
                                            .append(repoDto.repoName)
                                            .append("\n\n")
                                            .append("${activityContext.getString(R.string.error)}: ")
                                            .append(repoDto.latestUncheckedErrMsg)
                                            .toString()

                                        initErrMsgDialog(repoDto, errMsg)
                                    }else {
                                        goToErrScreen(repoDto.id)
                                    }
                                } ,
                            )
                        }
                    }


                    if(repoStatusGood && repoDto.hasOther()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Text(text = stringResource(R.string.other) + ": ")
                            InLineIcon(
                                icon = Icons.AutoMirrored.Filled.Notes,
                                tooltipText = stringResource(R.string.other)
                            )

                            ScrollableRow {
                                Text(
                                    text = repoDto.getOther(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,

                                    // for now the "other text" is short, if become long in future, make clicked show full "other text" in a dialog
                                    //目前 other text短，如果以后长到无法在卡片完整显示，实现点击文字在弹窗显示完整other text
//                            style = MyStyleKt.ClickableText.style,
//                            color = MyStyleKt.ClickableText.color,
//                            modifier = MyStyleKt.ClickableText.modifier.clickable {  // on click
//                                setCurRepo()
//                                pageRequest.value = PageRequest.showOther
//                            },

                                    fontWeight = defaultFontWeight,
                                    modifier = MyStyleKt.ClickableText.modifier,


                                )
                            }
                        }
                    }


                    if(repoDto.parentRepoValid) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Text(text = stringResource(R.string.parent_repo) + ": ")
                            InLineIcon(
                                icon = Icons.Outlined.Home,
                                tooltipText = stringResource(R.string.parent_repo)
                            )

                            ScrollableRow {
                                ClickableText(
                                    text = repoDto.parentRepoName,
                                    maxLines = 1,
                                    modifier = MyStyleKt.ClickableText.modifier.clickable {  // on click
                                        setCurRepo()
                                        pageRequest.value = PageRequest.goParent
                                    },
                                    fontWeight = defaultFontWeight

                                )
                            }
                        }
                    }


                }
            }else {  // err repo card
                val iconSize = remember {28.dp}
                //按下图标，显示出的半透明阴影的size，通常是icon尺寸+8就行
                val iconPressedSize = remember {36.dp}

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
//                modifier = Modifier.height(min=lineHeight.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
//                            .combinedClickable(onLongClick = {
//                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//                            copyErrMsg(repoDto.createErrMsg)
//                        }) {  }
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MySelectionContainer {
                            Text(
                                text = repoDto.createErrMsgForView(activityContext),
                                color= MyStyleKt.TextColor.error(),
//                                textAlign = TextAlign.Left,

                                // this empty clickable intent to prevent long pressing enable selection mode
                                modifier = Modifier.combinedClickable {}
                            )
                        }
                    }

//                    Spacer(Modifier.height(10.dp))



                    ScrollableRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InLineIcon(
                            icon = Icons.Filled.Delete,
                            tooltipText = stringResource(R.string.del_repo),
                            iconModifier = Modifier.size(iconSize),
                            pressedCircleSize = iconPressedSize,
                        ) { requireDelRepo(repoDto) }

                        InLineIcon(
                            icon = Icons.Filled.Replay,
                            tooltipText = stringResource(R.string.retry),
                            iconModifier = Modifier.size(iconSize),
                            pressedCircleSize = iconPressedSize,
                        ) { doCloneSingle(repoDto) }

                        InLineIcon(
                            icon = Icons.Filled.EditNote,
                            tooltipText = stringResource(R.string.edit_repo),
                            iconModifier = Modifier.size(iconSize),
                            pressedCircleSize = iconPressedSize,
                        ) {
                            goToCloneScreen(repoDto.id)
                        }

                    }
                }
            }

        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RepoTitle(
    haptic: HapticFeedback,
    repoDto: RepoEntity,
    isSelectionMode:Boolean,
    itemSelected: Boolean,
    pinnedRepos: Map<String, Long>,
    refreshPage: () -> Unit,
    // title的onClick和卡片主体的onClick区别在于卡片的onClick仅在启用选择模式时才可切换选择；title的即使没启用选择模式也可单击选择仓库并启用选择模式。
    titleOnClick: (RepoEntity) -> Unit,
    //title的onLongClick和卡片主体的没区别，都是长按启用选择模式，若选择模式已启用则执行区域选择
    titleOnLongClick:(RepoEntity) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .combinedClickable(onLongClick = {
                //震动反馈
//                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                //显示底部菜单
                titleOnLongClick(repoDto)
            }) {
                titleOnClick(repoDto)
            }
            .then(
                if (itemSelected) {
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier
                }
            ),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
    ) {
        ScrollableRow(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 5.dp, end = MyStyleKt.defaultIconSize),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (pinnedRepos.contains(repoDto.id)) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                contentDescription = if (pinnedRepos.contains(repoDto.id)) "pinned" else "not pinned",
                modifier = Modifier.size(18.dp).clickable {
                    SettingsUtil.update {
                        if(it.pinnedRepos.contains(repoDto.id)) {
                            it.pinnedRepos.remove(repoDto.id)
                        }else {
                            it.pinnedRepos[repoDto.id] = System.currentTimeMillis()
                        }
                    }

                    refreshPage()
                }
            )

            RepoCardTitleText(repoDto.repoName)
        }

        if(isSelectionMode) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(MyStyleKt.defaultIconSize)
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SimpleCheckBox(
                    enabled = itemSelected,
                    modifier = MyStyleKt.Icon.modifier,
                )
            }
        }

    }
}



//@Preview
//@Composable
//private fun Preview() {
//    val repo = mutableCustomStateOf("test", "test", RepoEntity(
//        id="abc", repoName = "test", branch = "main", upstreamBranch = "origin/main", lastCommitHash = "a312345",
//    ).apply {
//        gitRepoState = Repository.StateT.NONE
//        fullSavePath = "test/workdir"
//        workStatus = Cons.dbRepoWorkStatusUpToDate
//    })
//    val context = LocalContext.current
//    AppModel.init_forPreview()
//    RepoCard(
//        itemWidth=392F,
//        requireFillMaxWidth=false,
//        showBottomSheet=remember {mutableStateOf(false)},
//        curRepo= repo,
//        curRepoIndex=remember { mutableIntStateOf(0) },
//        repoDto=repo.value,
//        repoDtoIndex=0,
//        itemSelected=false,
//        titleOnClick={},
//        goToFilesPage={},
//        //如果和curRepoIndex的值匹配则会高亮显示当前条目，用来跳转时实现闪烁一下目标条目的效果，但在preview时只会高亮，然后就不灭了，一直维持高亮状态。。。
//        requireBlinkIdx=remember { mutableIntStateOf(-1) },
//        pageRequest=remember { mutableStateOf("") },
//        isSelectionMode=false,
//        onClick={},
//        onLongClick={},
//        copyErrMsg={},
//        requireDelRepo={},
//        doCloneSingle={},
//        initErrMsgDialog = {_, _ ->},
//        workStatusOnclick={p1,p2->},
//    )
//}
