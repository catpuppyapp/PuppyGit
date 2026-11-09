package com.catpuppyapp.puppygit.utils

import kotlinx.serialization.json.Json

object JsonUtil {

    // ignoreUnknownKeys：忽略对象里没有的key，不忽略可能会报错？忘了；
    // encodeDefaults：保存默认值，不然只有当类的字段值和其默认值不一样时，才会被保存到配置文件，用于配置文件时建议不要保存默认值，这样节省文件大小，并方便开发者控制默认值。

    /**
     * 会忽略默认值（类字段的值若和代码里写的默认值一样，序列化时会将其忽略）
     */
    val j = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    /**
     * 会包含默认值
     */
//    val j2 = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    /**
     * 会包含默认值 且 格式看着顺眼
     */
//    val j2PrettyPrint = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = true }
}
