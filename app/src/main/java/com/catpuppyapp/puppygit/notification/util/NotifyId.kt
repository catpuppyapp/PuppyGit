package com.catpuppyapp.puppygit.notification.util

import kotlin.random.Random

// 通知id应用进程内全局唯一，同id通知会覆盖
// 保留[1, 100]，作为保留id，其余值可能会在生成随机id时生成，参见 `randomId()`
object NotifyId {
    // reversed, no use
    const val reversedId1 = 1;

    // > 80 is foreground service notify id （我规定的，不是系统规定的）
    // 常驻前台服务需确保使用独占id，不然会覆盖，例如，启动前台服务后，显示了前台通知，然后一个动态通知使用了和前台服务相同的id，那么前台通知的内容会被覆盖（我记得是这样）
    const val foregroundServiceHttp = 81
    const val foregroundServiceAutomation = 82

    // random id
    const val randomNotifyIdStartAt = 101;  // included
    fun randomId():Int {
        //I want less than `randomNotifyIdStartAt` as reserved notification Id, so don't use it
        return Random.nextInt(randomNotifyIdStartAt, Int.MAX_VALUE)
    }
}
