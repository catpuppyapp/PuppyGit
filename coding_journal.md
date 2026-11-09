

---

---

---

---
x  20260323:
x 不需要改，改了的话，app进入或离开的状态可能不对）自动化服务，查询包名若无关联仓库，直接记录一条log，然后return
x 不需要）保存包名关联的仓库后，reset自动化服务的状态

x 大概看了下，应该没问题）检查自动化服务和屏幕关闭的代码，确保逻辑正确
x 更新日志增加修复自动化服务bug (issue #130)

---
x 20250617:
x 能，但时间差有点长，可能超过3秒，最终改成如果切出app则启用ON_RESUME重载并禁用一次file listener监听了，但如果不切出app，就只能忍受这个延迟有点高的重载了）测试在外部修改content uri关联的文件能不能被app的FileListener监听到：把app内部 ON_RESUME 自动重载检测注释掉，然后在外部修改，再切换回来看是否会自动重载就行了

---
x 修改所有列表条目的label文本为图标 20250513：
x 废弃，改了怕用户分不清新旧提交，不知道操作的目标是的哪个）reflogs
x 废弃，这页面很少用，没必要改）stashes
x 废弃，改了看图标看不出含义）remotes

x 页面：tag/file history/commit history/branch/submodule

---
x fix bugs 20250510:
x 能）安卓8: 无障碍自动化能不能用？
x 能）安卓8: http服务能不能用？
x 不是，但无所谓，会报错，但app不会崩溃，就这样吧）自动化获取的仓库列表确保是readylist
x 确保readylist不要获取状态为invalid的仓库
x 是）确保inner page用的是compoenntkey
x 废弃，那个是可点击的错误颜色，有点不同，不改了) 普通仓库卡片仓库错误信息颜色改成统一的mystylekt里的
x editor键盘高度偏移量应改成以dp为单位
x 遮盖，解决了）小手机，导航栏开，横屏，fab，遮盖吗？
x 正常）试下rtl布局 fab padding是否正常
x rtl editor 修改类型指示条没换位置
x 修改文件，后台，返回，文件重载，change type丢失：解决方案：保存文件应更新其dto
x 重命名仓库也选中并弹键盘
x 自动化页面，拉取推送延迟也选中并弹键盘
x 服务页面:主机、端口也选中并弹键盘
x 提交页面和文件历史页面，页大小，也选中并弹键盘
x 没找到在哪改，算了）把list item的点击效果去掉
x 把loadmore的齿轮放到里面
x 把所有页面初始化加载时的LoadingDialog取消，只有在执行某些不希望被打断的可能比较耗时的操作时才使用这东西，太阴间了，屏幕会闪一下

