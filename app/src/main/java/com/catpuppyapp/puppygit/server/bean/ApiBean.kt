package com.catpuppyapp.puppygit.server.bean

import kotlinx.serialization.Serializable

@Serializable
data class ApiBean (
    val protocol:String = "http",
    val host:String = "127.0.0.1",
    val port:Int = 52520,
    val token:String="",
    val pull:String = "/pull",
    val push:String = "/push",
    val sync:String = "/sync",
    //少加点参数，少写少错
    val pull_example:String = "",
    val push_example:String = "",
    val sync_example:String = "",
)
