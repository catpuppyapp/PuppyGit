
---
支持多pushurl 20260618：
暂时先支持向多个pushurl推送

缺陷：
界面仍仅支持设置单个pushurl，暂时不支持多个，因为改起来有点麻烦，同时也不支持为不同pushurl关联不同凭据，也因为改起来麻烦，若配置多个url只能通过 按域名匹配凭据 来为不同的pushurl关联不同的凭据，多数情况下这样应该就够用了，以后要是再有复杂需求，考虑编译git命令行作为后端，不过git命令行怎么为不同pushurl配置不同凭据？在pc也只能通过域名匹配，以后再说吧。

---
TODO 20260524:
实现提交签名（不想写了，先记着吧）
ppgit侧栏，顶部加个banner，现在这样太丑（加了好像也不会很好看？）


x 测试一段时间再发布新版
x 暂时放弃，原因见 “暂时放弃lfs支持 20260526”）实现lfs，git-lfs用go写的，编译安卓版很简单，直接调用git-lfs吧，不要自己处理了，细节很麻烦
x asm仍不可用）更新 libgit2/openssl 等依赖库，更新openssl时，尝试启用asm，若报错则禁用，启用asm有性能提升，但兼容性差
x 已降）考虑：compose版本降级（新版滑动手感太差，关于页面，轻轻一滑就过头，我都不敢碰屏幕了）

---
暂时放弃lfs支持 20260526：
git lfs依赖git命令，所以，要么编译安卓版的git命令，然后就可以弃用libgit2了，嗯，很好，就是编译git可能有点麻烦，真的不想编译那些c库，想吐。

另一种方案是不使用git lfs，自己实现上传和下载git lfs文件的功能，然后在libgit2注册函数，函数再调用java代码，这样就可以无缝集成libgit2和lfs功能了，或者可以更进一步，自己写一个不依赖git的lfs库，并且用c实现，这样就性能又好又减少外部依赖了！

以上两个方案都很好，若采用第1个，甚至可以逐步重构这个app的所有底层功能了，都改成调用git命令；第2个方案比较简单，修改的代码也比较少，但问题是我暂时不想写。

无论是哪个方案，都需要花费比较多的时间去调试和写代码，但问题是“这个app本来就没几个人用”，而且lfs更是几乎没人用，所以，我何必要去写呢？

还有一种更简化的方案 "只支持lfs文件的下载"，这样实现会容易很多，扫描一下本地的文件指针，然后下载，替换文件指针就行了，但这只是“部分支持”，做了感觉还不如不做。

所以暂时先不支持lfs了，日后再说吧。

---
x 已经实现）force pull 功能实现备忘 20260523：
在changelist页面添加一个强制拉取，点击弹窗，明确提示用户：
会重置workdir为远程仓库最新版本。


执行操作：
1 fetch
2 hard reset local branch to upstream
x 不需要，hard reset完了，还pull啥pull，多此一举）3 pull
x 不实现这个功能了，影响性能且可能误删文件）4 如果勾选了移除untracked文件，则调用status，删除untracked文件。（由于之前reset过，所以这时status应该无条目，或者只有untracked条目）



x 废弃）弹窗上有一个选项：Delete Untracked，若勾选，将会在重置workdir为远程数据后进一步移除untracked文件。
  废弃原因：影响性能(因为git status)且可能误删文件，若用户拉取后想删除Untracked文件，下次提交时会看到对应文件，手动删下就行了(在ChangeList页面可通过Revert选项来删除)

---
git lfs功能实现备忘 20260523：
lfs指针文件内容示例：
version https://git-lfs.github.com/spec/v1
oid sha256:4d7a214614b8a2e1d8a3614614b8a2e1d8a3614614b8a2e1d8a3614614b8a2e1
size 1458273

解析：可读取文件，如果只有3行，并且三行开头匹配 version oid size，则视作lfs指针文件


pc上lfs相关配置：
[filter "lfs"]
  # -- 代表options end，表示后面的参数不是命令选项
	clean = git-lfs clean -- %f       # 旧式单独调用命令的方式，这个应该是在push前执行
	smudge = git-lfs smudge -- %f     # 旧式单独调用命令的方式，这个应该是在fetch后执行
	process = git-lfs filter-process  # 新式交互方式，过滤器启动个进程，和主程序通信来执行操作和传输数据
	required = true  # 要求该 filter 必须可用，否则 Git 将失败并中止相关操作

=======
实现方案：

方案 17251000：
参见：这个app应该是调用的git lfs可执行文件：[app/src/main/java/com/lfgit/executors/GitExec.java](https://github.com/MarekPetr/LFGit/blob/1656e936176e5cbb92601feed5b4337fe2dfc6e2/app/src/main/java/com/lfgit/executors/GitExec.java#L4)

参见 <a href="ai给的git-lfs方案-20260523.txt">libgit2注册filter</a>
参见 libgit2源码：[tests\libgit2\filter\wildcard.c](https://github.com/libgit2/libgit2/blob/main/tests/libgit2/filter/wildcard.c)


编译安卓版本的[git lfs](https://git-lfs.com/)，会得到一个可执行文件。
编译命令：去dev/scrpits找build_gitlfs.ps1，放到gitlfs源码根目录，执行即可

编译出来大概 14M ，打包进app有点大，可在关于页面加个下载链接，下载到app的内部目录，必须内部目录不然可能无法加执行权限，下载后验证hash，加执行权限，然后就能用了，若启用lfs执行，则在每次启动app时注册lg2的filter。
只注册filter似乎不够，注册过滤器后，本地的提交和检出会自动处理，但推送lfs objs可能需要手动处理（未验证），若需手动处理：
手动模拟 pre-push 钩子： 在调用 libgit2 的推送 API 之前，你必须在宿主程序中手动调用一次 git-lfs pre-push <remote> <url>，或者自己写一段网络代码把本地缓存的大文件上传到 LFS 接口。
模拟pre-push应该就够了，其余的本地操作filter应该会自动处理（未验证）。
应该还可以使用 git-lfs 命令执行一些 lfs 相关的操作，比如列出仓库的lfs文件、下载指定文件，之类的，先实现基础的lfs下载和推送后再弄这些边脚料。
先注册过滤器，然后再看能不能弄 process 模式，process模式可常驻个进程负责上传下载，文件多时比单独启动进程要节省资源。
好麻烦，要不然以后再写吧，反正用这个功能的应该不多，烦了，先这样吧。


若使用可执行文件，从apk提取lfs可执行文件时先放到临时目录再重命名，避免写入中断，文件损坏

可执行文件放到app里，解压到app内部data目录，加执行权限。

如果可以先克隆或拉取仓库，然后调用lfs命令下载转换文件指针为文件，并且在推送前先执行lfs命令将所有符合条件的文件（或者手动选择的文件）都转换为指针文件，那么就不必非得注册libgit2的过滤器了，若用户有大文件，可以在克隆仓库后和提交仓库前，手动执行实际文件和指针文件的转换。

能不能改成全手动？弄个lfs管理页面，就像submodule，然后可以手动添加指定文件为lfs指针文件，并且在Files和ChangeList针对文件条目添加转换为lfs指针的选项。这样逻辑会简化很多。但如果可以自动的话，还是优先实现自动，然后添加个管理页面用来下载未下载的lfs文件指针即可，最好在管理页面可多选条目进行指针文件和实际文件的互相转换。

注册libgit2过滤器。



git lfs似乎有长进程模式和独立进程模式（每次调用都启动一个新进程），先测试独立进程模式，因为实现简单，若可用，再使用长进程模式，因为更稳定，省内存(大概，类似tcp连接，每次都开个连接和长连接的区别)。

libgit2应该不需要执行git lfs install，但是只注册filter并不确定能否在fetch后和push前自动转换真实文件和指针文件，有待测试，很可能不会，若不会则需要手动处理。具体怎么处理呢？嗯，我不知道，可能就是在拉取和推送的前后执行些操作吧，原本应该在pc端的hook操作，改成手动调用，其实就是在fetch/push时把真实文件和指针互相转换下之类的，应该不会太复杂。

我目前还没用过lfs，所以不太确定，配置完之后应该会根据gitattribute文件来对符合条件的文件自动使用gitlfs命令上传和下载。

创建一个lfs管理页面，可使用git lfs命令列出未下载的lfs条目，针对下载，支持多选。

需要看下怎么限制git lfs最大后台进程数量，不要太多，最好先实现成同步逐个下载，简化逻辑，日后再考虑并发后台下载。


优点：完全不用自己实现上传下载管理了，直接调用命令就行，甚至list仓库内所有条目也可以调用命令，并且用现成的实现，bug应该更少。
缺点：调用库，编译库，麻烦，而且还要考虑调用git lfs命令时，凭据怎么传过去？

=======
方案 13986311：
注：这个方案的可以在java层自己实现上传和下载，也可使用外部git lfs可执行文件，看哪个容易实现吧

- 自动管理（优先实现此方案+批量下载所有本地未转换为实际文件的lfs指针文件）： 
  1. 注册libgit2 filter，即可在拉取和推送时自动转换lfs文件和对象：参见 "ai给的git-lfs方案-20260523.txt"
  2. 批量下载lfs指针为对应文件：
    - 如果使用命令可执行（未验证）：
      gitlfs可执行文件 checkout
      gitlfs可执行文件 pull
    - 如果自己实现，可扫描目录内的lfs文件指针，然后逐个下载或并行下载（需限制并行任务数，避免占用资源过多）

- 手动管理（以后再说，自动管理+全量下载一般就足够了，管理器可能多数情况下都没用，适用于拉取和推送未支持lfs时被遗漏的条目，或者期望上传指定文件为lfs）：
  1. 创建一个lfs管理页面，扫描仓库目录内所有lfs文件（参考上面的文件格式解析） ，可批量下载（先实现成同步）
  2. 在内置文件管理器，可选择任意文件，将其转换为lfs上传，链接格式：https://remote.url/info/lfs
  3. 上传和下载先实现成同步且不可取消（可杀进程强制取消），日后可实现成任务队列+后台任务+可取消


优点：直接调用java代码，上传下载完全可控，凭据传递更容易
缺点：自己实现的话，我不确定复杂度如何，解析attribute的任务可在libgit2完成，只需要自己实现上传下载和替换workdir和.git lfs obj，应该问题不大，可以试试。


其他：
替换lfs指针文件为实际文件后后，执行git status时，应该会使用index中的oid和文件的实际oid进行比对以避免指针文件和实际文件完全就是两个文件而判定文件为modified，并且应该会缓存文件的oid以避免文件没修改时重复计算hash

---
git sign commit 功能实现备忘 20260523:
优先最小化实现：
1. 仅支持gpg签名commit。
2. 提交列表条目，若提交包含签名，则添加一行显示已签名，格式"signed(类型，gpg|ssh)"，例如"signed(gpg)"。
3. 在提交列表页面，条目详情弹窗，显示签名内容（可能是armored gpg 文本）


详情：
主要功能分为签名和验证签名。

签名对象分为tag签名和commit签名。

签名类型分为ssh和gpg两种，优先支持gpg。

优先实现commit签名，其次tag签名，最后考虑实现验证签名。

签名相关的git配置项，我记得gitconfig里有一个设置gpg key的属性，在app里添加全局和仓库级设置项，全局存到app的gitconfig里，仓库级存到仓库gitconfig里。

签名选项放到提交弹窗，CheckBox，可勾选签名，优先使用仓库级key，若无，使用全局，若无，红字提示未配置签名，并且提交时不会签名。

签名实现：大概就是读取commit文本，算hash，用私钥加密，再按固定格式追加密文到commit，细节可以去网上搜索一下。

验证签名用的公钥来源：
1. 用户可在app自行管理信任的public key（增删改查）
2. 从公开gpg网站根据邮箱获取public key

优先支持本地配置可信的public key列表。

需要做个key管理页面，需要添加type字段区分ssh和gpgkey。

需要支持添加gpg公钥获取源，也就是允许用户添加自定义的公钥查询服务器（网址）。

提交历史页面，加载提交后，显示是否有公钥，但不要自动验证（避免性能问题），加个菜单项触发，或者在commit条目加个按钮触发验证。

优先使用本地公钥列表验证key，若无则联网验证，在界面上提示用户是从本地验证成功，还是联网验证成功。（可选：即使本地验证成功也可强制联网验证）


---
x TODO 20260530:
x 算了，容易让人误以为是文件夹的历史，若想恢复路径的文件，大可在对应路径创建个文件再看历史，或者去提交历史页面根据文件路径过滤）考虑：修改ppgit，对目录也添加history？因为有可能一个目录，过去曾是文件并且提交过，所以会产生历史。

---
文本编辑器键盘用户体验优化：使用键盘选择行 20250726：
只读或选择模式时启用键盘选择操作：
1. 按回车，选中某行；
2. shift 回车，选中区间；
3. 按住shift+上或下，切换行并选中；
4. esc退出选择模式。

---
TODO sha256 support 20250607:
1. waiting for libgit2 sha256 feature become stable
2. UI: add menu item to RepoCard menu to allow users switch hash method, then save settings into .gitconfig (need test if were switched to sha256, then, still can switch back to sha1?)
3. UI: can choose sha256/sha1 when init a folder to git repo, and save to .gitconfig file
4. GUIDE: honor sha256 settings in the .gitconfig file, rather than save settings to db repo table
5. CODE: change Oid class of git24j to libgit2 oid series method binding, else need implement sha256 in java side, may cause bug


中文：
1. 等 libgit2 的sha256功能稳定
2. UI: 仓库卡片添加可切换hash算法的菜单项，并保存到仓库的.gitconfig（需要测试如果切换到 sha256 还能否切换回sha1？）
3. UI: 初始化文件夹为仓库时可选hash算法，并保存到.gitconfig
4. GUIDE: 遵循 .gitconfig 的 sha256 相关选项，把相关设置都存到 .gitcofnig 里，尽量别往 db repo表 存
5. CODE: 修改 git24j 的 Oid 类为libgit2 oid系列函数的binding，不然还得自己写sha256相关的算法，可能导致新的bug

---
## TODO 20241221
- 本地化service通知信息：给RepoActUtil下相关方法添加context参数，然后逐层向上添加context参数，直到顶层Service类里，然后把this作为context传过去即可。最后在这条链路上，把硬编码的英文字符串都替换为context.getString()就行了。还有，别忘了把Service类里的相应字符串资源也替换下，例如HttpService里的"Listen on:........."字符串。（ps: 20250220已经重写了所有的Service的attachBaseContext()所以直接在Service/Activity里传this即可正常根据app设置的语言获取相应的字符串资源。btw 获取字符串资源最好不要传applicationContext，不然有可能获取到英文或者强制根据系统语言获取资源而不是获取app设置的语言对应的字符串资源）
x - 支持以 isMerged NotMerged hasUpstream noUpstream等附加条件过滤提交、分支，或者强制把这些东西改成可搜索的英语和供查看的指定语言？例如可通过IsMerged/NotMerged过滤条目，但显示的时候显示为合并而来和非合并而来
- support gif-lfs(should compitable .gitconfig settings)
- sign commit and tag(include rebase/merge/cherrypick generated commits, and can enable or disable respectively)(at least support gpg sign, better ssh as well)(should compatible .gitconfig settings)
- ChangeList/Index/TreeToTree support view by folder and support more sort method(sort by name/modified time, asc/desc just like Files screen)（submodule虽是folder但实际处理起来类似file，普通folder点击可展开/隐藏，点击file前往diff页面，选择模式下点folder自动选择其下所有文件，长按folder也可开启选择模式）
- syntax  highlighting in Editor and Diff screen
x - 启动一个http服务器，支持执行拉取推送，并返回是否出错。需要考虑如何保持进程不被杀掉，常驻通知栏或许可行。创建提交的请求必须有一个参数控制是否创允许建空提交，只有允许才能创建空提交否则不能，这样是为了避免执行命令后创建空提交并推送。细节：需要针对每个仓库分别使用各自的公平锁以避免冲突并保持操作顺序与请求顺序一致。
x 废弃，obsidian安卓版无法实现在退出app时执行操作，改由无障碍实现打开/退出app自动拉取/推送，或者通过tasker在打开/退出app时调用http pull/push api) - 实现http服务后，写一个obsidian插件，启动时发送拉取请求给puppygit，关闭时发送推送请求。

---


## TODO (early, maybe some features already implemented)
- show translation and web search and share etc context menu when text selected (may need filter the apps which supportted PROCESS_TEXT intent)
- show process detail when cloning (now only show"Cloning...")
- improve ChangeList load performance
- multi cherry-pick
- optimize Editor performance
- Editor merge mode: add colors for ours/theirs blocks
- Editor merge mode: add accept ours/theirs buttons at conflict block (expect like vscode style)
- highlighting keyword when finding in Editor
- support more encoding for Editor(now only supported utf8)
- view commit history as a interactable tree (the tree should scrollable for vertical and horizontal)
- view history of file(plan: add menu item in Files page single item menu, and Editor page top bar menu. if file is not in a git repo, show a toast)
- Files: support more order methods(order by name, order by date, desc, asc, etc), and each path can have difference order method(path and order method should save to a single config file like file opened history for avoid settings file oversized)
- Files: support override or rename for copy/move when target and src have same name
- ChangeList: support group by folder(try use different status options, if good, no need group by myself)(should expend when clicked a folder, if it is not a submodule or git repo. and can view diff content when clicked a file under a folder. and should make sure it can't stage a folder when it is a git repo under other repo but isn't a submodule)
- ChangeList: support show/hide submodules(try different status opts, if good, no need implement by myself)
- ChangeList: support show folder first(order method)
- add go to bottom for every go to top fab(column layout, upside icon, clicked go to top, bottom icon, clicked go to bottom)
- signed commits (by gpg key)
- support ssh
- git blame
- optional: can disable auto loading when go to ChangeList (for avoid this case: go to ChangeList, but the repo is not you want, and page loading, you can't switch repo...stucked... but actually, have workaround, go to Files or Repos page can direct show ChangeList of selected repo)
- support git lfs
- optional: multi worktree manage(most time users only need 1 worktree, so this feature is low priority and optional)
- support set color for:
  - Editor: conflict ours/theirs block
  - DiffScreen: add/del/context line background
- DiffScreen:
  - support adjust font size
  - support adjust line num size
  - show/hide line num
  - show/hide +/- sign for add/del line
- squash commits
- ChangeList: view difference of all changed files in one page(提供一个入口，点击进入diff页面，可预览所有修改过的文件的diff内容）
- support terminal: provide a git cli by termux, can use ssh or intent to communicate with termux, but, the only sense of this feature is can let termux use credential provided by PuppyGit, in other case, users can use ssh client instead or direct use Termux
- <del> (checked, actually is fine) check code, fix memory leak: I bet this code has many memory leak, because in git24j, many constructors create instance with weak ptr, which hold instance, but never free it </del>
