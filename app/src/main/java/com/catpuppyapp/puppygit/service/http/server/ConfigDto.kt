package com.catpuppyapp.puppygit.service.http.server

import kotlinx.serialization.Serializable

/**
    以后如果新增其他配置，不要删旧的，建个新的并且换新的版本号，在客户端也是一样，保留旧版
 */
@Serializable
data class ConfigDto(
    val version: String = "1",
    val repoName:String,
    val repoId:String,
    val api:ApiDto
)
