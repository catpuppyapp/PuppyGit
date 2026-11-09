package com.catpuppyapp.puppygit.utils.time

enum class TimeZoneMode {
    /**
     * 跟随系统时区，每次Activity销毁重建后都会获取当前系统最新时区信息。
     * 此设置是默认值。
     */
    FOLLOW_SYSTEM,

    /**
     * 用户指定了一个有效的时区（可解析为int且在限定范围内，默认有效范围正负1080分钟）
     */
    SPECIFY,

    /**
     * 用户未指定或指定了一个无效的时区（不可解析为int或超过限定范围）
     */
    UNSET;

}
