


---
1.1.4.7v123 - 20260228:
- try to fix automation service crash (pr #123, contributor @jiesou)



- 尝试修复自动服务崩溃 (pr #123, 贡献者 @jiesou)

---
1.1.4.6v122 - 20260215:
- try to fix git global username and email lost sometimes



- 尝试修复全局git用户名和邮箱有时丢失的bug

---
1.1.4.5v121 - 20251101:
- adjust UI of clone screen



- 调整克隆页面UI

---
1.1.4.4v120 - 20251018:
- editor support detect line break



- 编辑器支持检测换行符

---
1.1.4.3v119 - 20251006:
- support disable log



- 支持禁用日志

---
1.1.4.2v118 - 20250918:
- Make text of dialog copyable 
- Add changelog dialog



- 使弹窗文字可拷贝
- 添加更新日志弹窗

---
1.1.4.1v117 - 20250911:
- The file manager supports share files



- 文件管理器支持分享功能

---
1.1.4.0v116 - 20250907:
- add name for intents



- 为intents添加名称

---
1.1.3.9v115 - 20250828:
- editor support detect file encoding
- support rebase as default when pull



- 文本编辑器支持检测文件编码
- 支持pull时默认rebase

---
1.1.3.8v114 - 20250825:
- fix editor may lost data when syntax highlighting on
- sort tag list by commit time



- 修复语法高亮开启时编辑器可能丢失数据的bug
- 按提交时间排序tag列表

---
1.1.3.7v113 - 20250811:
- minor bug fixed



- 修复小bug

---
1.1.3.6v112 - 20250726:
- support filter repos and recent files by app related path
- show latest commit msg in repo card
- support vue syntax highlighting
- fix sometimes ime state lost in editor
- Editor: hide save button if current content already saved
- Editor: fix undo stack push logic incorrect in some cases
- Editor: support insert closed symbol pair after input opened
- fix many bugs of Editor
- support disable ssl verify
- upgrade target sdk to latest android version



- 支持根据app相关路径过滤仓库和最近文件
- 在仓库卡片显示最新提交信息
- 支持vue语法高亮
- 修复输入法状态在编辑器有时会丢失的bug
- 编辑器：若当前内容已保存，隐藏保存按钮
- 编辑器：修复撤销栈在某些场景更新逻辑有误
- 编辑器：支持插入开符号时，自动追加闭符号
- 修复文本编辑器的大量bug
- 支持禁用ssl验证
- 升级sdk为安卓最新版本


---
1.1.3.5v111 - 20250724:
- markdown support kotlin code block
- fix editor menu can't full expanded after ime hidden
- update dependencies



- markdown支持kotlin代码块
- 修复编辑器菜单在输入法隐藏后无法完全展开的bug
- 升级依赖

---
1.1.3.4v110 - 20250719:
- fix editor undo stack not clear after reload file
- Diff screen support syntax highlighting
- Improve commit msg dialog



- 修复编辑器在重载文件后不清空撤销栈的bug
- Diff页面支持语法高亮
- 改良提交信息弹窗


---
1.1.3.3v109 - 20250718:
- support zig file syntax highlighting
- editor support open .h/.hpp file



- 支持zig文件语法高亮
- 文本编辑器支持打开.h/.hpp文件


---
1.1.3.2v108 - 20250717:
- show cursor handle for Editor



- 文本编辑器显示光标拖柄


---
1.1.3.1v107 - 20250712:
- make storage paths displayed name better if path is external or internal path
- try go to first conflict line when open file with merge mode
- focus target line when go to Editor from DiffScreen by click line number
- Editor support auto indent when pressed enter
- Editor support tab to spaces as indent
- Editor support syntax highlighting



- 当路径是内部或外部存储时，优化存储路径条目名称
- 以冲突模式打开编辑器时尝试跳转到第一个冲突行
- 从Diff页面点击行号跳转到编辑器时，聚焦目标行
- 编辑器支持按回车自动缩进
- 编辑器支持按tab转换为空格缩进
- 编辑器支持语法高亮


---
1.1.3.0v106 - 20250626:
- Files title now can select storage paths



- 文件管理器标题现在可选择存储路径


---
1.1.2.9v105 - 20250624:
- fix: reload item err after reset and checkout in commit history screen
- fix: load resource failed when editor preview mode on
- add new language Bangla (thx @kamilhussen24)



- 修复提交历史页面重置和检出后加载条目报错
- 修复编辑器预览模式加载资源失败
- 添加孟加拉语 (thx @kamilhussen24)


---
1.1.2.8v104 - 20250618:
- fix: colors difficult to distinguish in some cases (issue: #66, thx @RokeJulianLockhart)



- 修复某些情况颜色难以区分 (issue: #66, thx @RokeJulianLockhart)


---
1.1.2.7v103 - 20250606:
- fix: sometimes commit item not update after reset
- add Thanks list in About screen
- support fetch remotes at Branches screen
- update depend libs (libgit2 to 1.9.1, openssl to 3.5.0)
- update UI libs
- Diff: optimize default match strategy
- limit file names length of commit msg template
- improve UI (issue #66)
- Automation: support set pull interval and push delay for each repo
- Editor support Ctrl+S/Ctrl+W/Ctrl+F to save/close/find the file
- Editor support auto reload when file changed by external



- 修复提交条目在重置后不更新的bug
- 关于页面添加感谢列表
- 支持在分支页面下载远程仓库
- 更新依赖库 (libgit2 to 1.9.1, openssl to 3.5.0)
- 更新UI库
- Diff: 优化默认匹配策略
- 限制模板提交信息的文件名字数
- 提升 UI (issue #66)
- 自动化：支持为每个仓库设置拉取间隔和推送延迟
- 编辑器支持 Ctrl+S/Ctrl+W/Ctrl+F 执行 保存/关闭/查找 文件
- 编辑器支持在文件被外部修改时自动重载


---
1.1.2.6v102 - 20250605:
- fix sometimes commit info not update after create tag or branch



- 修复某些情况下提交信息在创建tag或分支后不更新的bug


---
1.1.2.5v101 - 20250605:
- fix repos loading slow



- 修复仓库页面加载缓慢


---
1.1.2.4v100 - 20250605:
- adjust time format for file list item



- 调整文件列表条目的时间格式


---
1.1.2.3v99 - 20250604:
- can choose checkout left or right when compare two commits
- adjust time format for repo card



- 比较两个提交时可选检出左边或右边的文件
- 调整仓库卡片时间格式


---
1.1.2.2v98 - 20250530:
- restore ime visible when return Editor
- support Arabic language (thx https://github.com/Hussain96o)



- 返回Editor时恢复显示软键盘
- 支持阿拉伯语 (感谢 https://github.com/Hussain96o)


---
1.1.2.1v97 - 20250530:
- support compare left/right commit to local at commits changelist
- support set commit msg template
- fix: compare two difference commits mis-considered they are same


- 在提交的修改列表支持比较左侧/右侧提交和本地文件差异
- 支持设置提交信息模板
- 修复比较提交时误判两个不同的提交为相同


---
1.1.2.0v96 - 20250524:
- long press commit hash in RepoCard will copy it
- support filter Repo list by storage path and update time
- support import local Repo from top bar of Repos screen
- request manage storage permission when import repos from local



- 长按仓库卡片提交hash可拷贝
- 仓库列表支持按完整路径和更新时间过滤
- 支持从仓库页面的顶栏菜单导入本地仓库
- 从本地导入仓库时请求存储权限



---
1.1.1.9v95 - 20250521:
- optimize line change type update logic for Editor



- 优化文本编辑器行修改状态的更新逻辑


---
1.1.1.8v94 - 20250520:
- remove unused function
- fix: go to top/bottom buttons not work when showing err message



- 移除无用函数
- 修复：回到顶部和底部按钮在页面显示错误信息时无效

---
1.1.1.7v93 - 20250520:
- fix: dialog button disable color invalid in some cases
- add easter egg at About page



- 修复某些情况弹窗按钮禁用时颜色失效
- 关于页面添加彩蛋


---
1.1.1.6v92 - 20250520:
- add forget master password option in settings



- 在设置页面添加忘记主密码的选项


---
1.1.1.5v91 - 20250519:
- fix: bugs related to the master password updating



- 修复更新主密码相关的bug


---
1.1.1.4v90 - 20250519:
- support viewing commit history graphically
- support force push with lease



- 支持图形化预览提交历史
- 强制推送支持with lease


---
1.1.1.3v89 - 20250513:
- adjust Editor style
- improve UI for some screens



- 调整文本编辑器样式
- 提升一些页面的UI


---
1.1.1.2v88 - 20250510:
- popup keyboard when rename file and go to path at Files
- optimize Editor reload check logic
- Editor: fix change type indicator position wrong when RTL layout enabled



- 在文件管理器重命名文件和跳转路径时弹出键盘
- 优化文本编辑器重载检测逻辑
- 文本编辑器：修复修改类型指示器在RTL布局下位置错误


---
1.1.1.1v87 - 20250509:
- Files now display thumbnail for video



- 文件管理器现在可以为视频显示缩略图了

---
1.1.1.0v86 - 20250508:
- minor bugs fixed



- 小bug修正


---
1.1.0.9v85 - 20250508:
- show icon for apks and images in the file list
- minor bugs fixed



- 文件列表显示apk和图片文件的图标
- 小bug修正

---

1.1.0.8v84 - 20250502:
- Differences screen support multi files
- show random loading text when launching
- improve UI
- fix bugs



- 差异页面支持多文件
- 启动时显示随机加载文本
- 提升UI
- 修复bug



忘了写到更新日志的新功能：
- 下拉刷新(pull to refresh)
- 文本编辑器预览补丁模式(patch mode of Editor)

---

1.1.0.7v83 - 20250428:
- ChangeList and FileHistory screens: return from Diff screen will scroll list to make last viewed item visible
- fix: on Tablet, Repos screen layout may become weired after rotated device



- 修改列表和文件历史页面：从Diff页面返回后将滚动列表以使最后查看条目可见
- 修复仓库页面布局在旋转屏幕后可能错乱的bug

---

1.1.0.6v82 - 20250427:
- fix: Editor load file twice when open file
- fix: if compare to ref which include '/', then app will crash
- fix: bugs of screen navigate
- fix: rotation screen lost state
- show a crash Activity when compose crashed
- reduce memory use



- 修复文本编辑器打开文件会加载两次的bug
- 修复比较引用包含'/'时app会崩溃的bug
- 修复页面导航相关的bug
- 修复旋转屏幕会丢失状态的bug
- 在compose崩溃时显示一个崩溃页面
- 减少内存占用


注：app崩溃显示crash Activity的新功能忘了写到发行的changelog里，仅在此记录

---

1.1.0.5v81 - 20250426:
- fix: navigator state err cause app crashed
- AutomationService: support auto push after screen off
- update style of open as dialog



- 修复导航状态错误导致app崩溃的bug
- 自动化服务：支持熄屏自动推送
- 修改打开方式弹窗

---

1.1.0.4v80 - 20250424:
- fix: clone submodule err when require authentication
- support shallow clone submodules
- optimize code



- 修复克隆子模块时关联凭据无效的bug
- 支持浅克隆子模块
- 优化代码

---

1.1.0.3v79 - 20250422:
- adjust create stash and tag dialog
- add Stash entry at ChangeList view
- fix: navigate to some pages may got err



- 调整创建stash和tag的弹窗
- 在修改列表页面添加Stash管理器入口
- 修复导航到某些页面可能报错的bug

---

1.1.0.2v78 - 20250420:
- fix: link credential to uppercase HTTPS git url not work
- adjust CredentialManager UI



- 修复当git url为大写HTTPS url时关联凭据无效的bug
- 调整凭据管理器UI

---

1.1.0.1v77 - 20250416:
- fix: long pressing DiffScreen line menu item will crash the app



- 修复长按Diff页面行菜单项导致app崩溃的bug

---
1.1.0.0v76 - 20250415:
- change BottomBar menu icon enable condition
- fix: snapshot enable not work
- fix: cursor of Editor placed wrong in some cases
- optimize folder search



- 修改底栏菜单按钮启用条件
- 修复启用快照无效的bug
- 修复某些情况下文本编辑器光标定位出错的bug
- 优化文件夹搜索

---

1.0.9.9v75 - 20250408:
- add turkish (pr #41, thx @mikropsoft)
- fix resolve file path failed in some cases



- 新增土耳其语 (pr #41, thx @mikropsoft)
- 修复某些情况下解析文件路径失败的bug

---

1.0.9.8v74 - 20250403:
- add remove from git to ChangeList
- improve Files filter logic
- fix: when filter mode on, sometimes result no update 
- minor bugs fixed



- 修改列表添加从git移除文件（取消追踪）的选项
- 增强文件管理器过滤逻辑
- 修复过滤模式开启时列表不更新的bug
- 修复其他小bug

---
1.0.9.7v73 - 20250327:
- Automation: support set pull interval and push delay 
- optimize Editor performance
- fix: Editor redo/undo not work
- Editor selection mode: add "Clear" and "Paste" button
- style adjust
- update russian translation (thx @triksterr)



- 自动化：支持设置拉取间隔和推送延迟
- 优化编辑器性能
- 修复编辑器撤销失效的bug
- 编辑器选择模式：添加清除和粘贴按钮
- 样式调整
- 更新俄语翻译 (thx @triksterr)

---

1.0.9.6v72 - 20250322:
- change style of Files screen title



- 修改文件管理器标题样式

---

1.0.9.5v71 - 20250321:
- use internal file manager instead of system file picker


- 使用内置文件管理器替代系统文件选择器

---

1.0.9.4v70 - 20250318:
- Files support recursive search
- fix a bug may cause app run slowly when filter mode on
- support preview markdown file
- update russian translation (thx @triksterr)
- support set file associations for Editor
- optimize code


- 文件管理器支持递归搜索
- 修复一个在开启过滤模式时可能会导致app卡顿的bug
- 支持预览markdown文件
- 更新俄语翻译 (thx @triksterr)
- 支持为文本编辑器设置关联文件类型
- 优化代码

---

1.0.9.3v69 - 20250307:
- adjust set username and email dialog


- 调整设置用户名和邮箱的弹窗

---

1.0.9.2v68 - 20250306:
- fixed a bug that cause Diff screen crashed
- update dependencies


- 修复了一个会导致Diff页面崩溃的bug
- 更新依赖库版本

---

1.0.9.1v67 - 20250301:
- Files screen support Import/Export from/to external apps which implemented system documents picker (e.g. Termux)
- update russian translation (thx @triksterr)
- fix some bugs


- 文件管理器支持从实现了系统文档选择器的app（例如：Termux）导入导出文件
- 更新俄语翻译 (thx @triksterr)
- 修复一些bug

---

1.0.9.0v66 - 20250225:
- use icon instead of clickable text to copy commit hash


- 使用图标替代可点击文字拷贝提交hash

---

1.0.8.9v65 - 20250224:
- Line number of Diff and Editor screens use width-fixed font
- support create patch at Diff screen


- Diff和Editor页面的行号改用等宽字体
- 支持在Diff页面创建补丁

---

1.0.8.8v64 - 20250222:
- double-click title can switch between top and current position
- change go to top fab logic to switch between top and last position


- 双击标题可在顶部和当前位置切换
- 修改回到顶部浮动按钮逻辑为在顶部和上次位置间切换

---

1.0.8.7v63 - 20250219:
- add dev mode
- update russian translation (thx @triksterr)
- support commit/unstage/revert items from Diff screen
- optimize match by words for compare files
- icon style follow system theme (android 13+) (pr #33, thx @sebastien46)


- 添加开发模式
- 更新俄语翻译 (thx @triksterr)
- 支持从Diff页面提交、取消暂存、恢复条目
- 优化比较文件时的按单词匹配模式
- 图标风格跟随系统主题 (安卓 13+) (pr #33, thx @sebastien46)

---

1.0.8.6v62 - 20250215:
- fix: file history omitted items


- 修复文件历史页面遗漏条目的bug

---

1.0.8.5v61 - 20250214:
- update tls certs
- fix: commit oid in the file history didn't match the file modified commit oid 


- 更新tls证书
- 修复文件历史页面中的提交oid与文件修改时的提交oid不匹配的bug

---
1.0.8.4v60 - 20250213:
- remember read-only for DiffScreen
- adjust compare method


- 为比较页面记住只读状态
- 调整比较方法

---

1.0.8.3v59 - 20250206:
- update russian translation (thx @triksterr)


- 更新俄语翻译 (感谢 @triksterr)

---

1.0.8.2v58 - 20250203:
- optimize notification method


- 优化通知显示逻辑

---

1.0.8.1v57 - 20250131:
- support auto pull/push when enter/exit specified apps (issue #28)
- support run a http service, it can used for request action by tasker or other apps which can send http request (issue #23)
- will save master password on the local, no longer input required when launching app


- 支持 进入/退出 指定app时自动执行 拉取/推送 (issue #28)
- 支持运行Http服务，可通过Tasker等自动化工具发送http请求以实现某些需求 (issue #23)
- 主密码将保存在本地，启动app时不再需要输入

---

1.0.8.0v56 - 20250125:
- update Repos Screen: disable "Sync" and "Set Upstream" for not-cloned Repo
- fix: copy a file which path contains '.' may failed


- 仓库页面修改：对未克隆仓库禁用“同步”和“设置上游”选项
- 修复拷贝路径包含'.'的文件可能会出错的bug

---

1.0.7.9v55 - 20250124:
- add Status "Need Pull/Need Push" in to Repo Card


- 添加状态 "需要拉取/需要推送" 到仓库卡片

---

1.0.7.8v54 - 20250123:
- indicate repo has uncommitted changes on RepoCard (issue #26)
- support multi selection on Repos page (issue #27)


- 在仓库卡片显示仓库是否有未提交修改 (issue #26)
- 仓库页面支持多选 (issue #27)

---

1.0.7.7v53 - 20250113:
- update Editor: reload file will not clear undo stack


- 更新文本编辑器: 重载文件将不会清空撤销记录

---

1.0.7.6v52 - 20250112:
- support clear upstream for branch

---

1.0.7.5v51 - 20250107:
- fix bug: Files filter by keyword "document" will match all folders
- change BackHandler logic: press back button when filter and selection mode both are enabled, will quit selection first


- 修复文件管理器过滤关键字为"document"时将匹配所有文件夹的bug
- 修改返回键处理逻辑：当过滤模式和选择模式同时开启时，按返回键先退出选择模式

---

1.0.7.4v50 - 20250106:
- show TimeZone info at Commits/Tags/Reflogs/FileHistory if unset timezone for app
- remove "PuppyGit" from auto generated commit msg


- 如果未设置时区则在 提交/标签/引用日志/文件历史 页面显示时区信息
- 从自动生成的提交信息中移除 "PuppyGit"

---
1.0.7.3v49 - 20250105:
- ChangeList: move "Sync" button from BottomBar to commit msg dialog
- support Undo/Redo for Editor
- support stage item on DiffScreen
- update libgit2 to 1.9.0 (fix ssh shallow clone err, issue #10)


中文更新日志：
- 修改列表页面: 把 "同步按钮"从BottomBar挪到了提交信息弹窗
- 文本编辑器支持撤销和重做
- 支持在比较页面(DiffScreen)暂存条目
- 更新 libgit2 版本 1.9.0 (修复ssh浅克隆错误, issue #10)

---

1.0.7.2v48 - 20241231:
english:
- switch dark theme no more restart app required
- fix bug of Diff Screen select compare: when group by line off, select two lines no match will not update target line, but it should update to no match
- fix after rotated screen force back to Home screen (issue #19)
- add timezone settings, default is follow system (issue #20)


chinese:
- 切换主题不需要重启app了
- 修复 Diff Screen 选择比较 bug: 当按行分组关闭时，选择比较两个行（或 某行和剪贴板）后若无匹配则目标行状态不会更新
- 修复旋转屏幕后页面重置到首页的bug (issue #19)
- 添加时区设置，默认跟随系统时区，此设置会影响预览和创建提交、标签等GitObject时的时间字段以及Files页面文件的最后修改时间 (issue #20)

---

1.0.7.1v47 - 20241218:
- make app pass the Reproducible Build check, it used for verify the apk 100% built from open source (issue #13, thx @IzzySoft)
- set jvmTarget from 11 to 17
- bundled git24j src code and fix some bugs

---

1.0.7.0v46 - 20241212:
- update bundled cert

---

1.0.6.9v45 - 20241210:
- Diff Screen support match by words
- Editor changes:
  - add a recent file list
  - use icons instead texts for actions when no file opened (long press icon will got hint of it) 
  - fix bug: keyword exist but not found
- Repos Page layout adjusted for Tablet: now each row repos count will calculating by screen width (each row repos count = screen width / a repo item width) 
- support set a master password instead default password encrypt password/passphrase of your credentials
- support push at commit msg dialog
- support russian (translate by github user @triksterr)

---

1.0.6.8v44 - 20241124:
- support sort for Files (issue: #8)
- adjust Files explorer breadcrumb style
- add fallback mechanism for get external app dir: if try get external app dir failed will try get inner instead
- change android targetSdk to 35
- update UI libs version
- change app chinese name to "小狗Git"(former name "狗狗Git"）

---

1.0.6.7v43 - 20241119:
- fixed when edit repo and don't change the url, the new credential will show username/password even is ssh url(issue https://github.com/catpuppyapp/PuppyGit/issues/5)

---

1.0.6.6v42 - 20241118:
- fixed: after deleting submodule still show it in Submodules Screen
- fixed: on RepoScreen, repo status not update when do act under filter mode

---

1.0.6.5v41 - 20241117:
- DiffScreen: change deleted content color from red to gray

---

1.0.6.4v40 - 20241115:
- CloneScreen: if the git url is not http or https url, will show private key and passphrase instead username and password for the "New Credential"

---

1.0.6.3v39 - 20241115:
- fix some minor bugs
- update dependency libs
    - libgit2 from 1.7.2 to 1.8.4
    - openssl from 3.3.0 to 3.4.0
    - libssh2 from 1.11.0 to 1.11.1
    - git24j from 1.0.3 to 1.0.4

---

1.0.6.2v38 - 20241113:
- support ssh

---

1.0.6.1v37 - 20241111:
- support view file history
- support direct edit line in diff screen
- commit list support filter commits by paths
- support select lines to compare at diff screen, it's useful when similar lines has difference line number

---

1.0.6.0v36 - 20241101:
- fix Difference Screen crash

---

1.0.5.9v35 - 20241031:
- update git24j libs

---

1.0.5.8v34 - 20241030:
- support squash commits
- Improve view differences performance, now very fast

---

1.0.5.7v33 - 20241029:
- Optimize page scroll performance
- Editor, resolve conflict optimize: 
  - ours/theirs now has background color
  - add accept ours/theirs buttons
- can switch Prev/Next File in DiffScreen
- adjust go to top fab:
    - can permanent hide from settings
    - can temporary hide
    - support go to bottom
- fixed bug: commit history list omit commits
- able to copy clone err msg by long pressing the err msg on err repo card
- show last modified time in Files(selected single file) and Editor(clicked title, show file details)

---

1.0.5.6v32 - 20241017:
- fixed few minor bugs
- improved chinese translation

---

1.0.5.5v31 - 20241016:
- rename app name to PuppyGit, no more Pro suffix ever
- support chinese
- add Settings page

---

1.0.5.4v30 - 20241013:<br>
ignore file change:
- change comment start sign form "#" to "//"
- deprecated "ignores.txt", instead by "ignore_v2.txt", if users was use puppy git ignore feature, need re-ignore files

---

1.0.5.3v29 - 20241011:
- fixed Files Page loading very slow
- fixed Files Page open a unreadable dir when first launch app

---

1.0.5.2v28 - 20241011:
- Files page support go to ChangeList or Repos
- other minor bugs fixed

---

1.0.5.1v27 - 20241008:
- support submodules
- support init dir as git repo at Files page
- support ignore files at ChangeList page (git status for worktree to index)

---

1.0.5v26 - 20241005:
- settings file move to user-visible PuppyGit-Data folder
    * NOTICE: if you upgrade app from old version, below settings will lost, you can set it again, will save to new settings:
        * Editor Page: font size/show or hide line number/files last edited positions
        * Global Git username and email
        * Storage Paths
- dozen bugs fixed

---

1.0.4v25:
- important update, unlock all features

---

1.0.3.2v24:
- support clone repo to external storage (need grant manage storage permission, if don't grant, still can clone in to app internal storage like old versions)
- enable better compare method
- enable shallow clone (if make repo corrupted, please re-clone full repo)

---

1.0.3.1v23:
- open source

