package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class SshSettings(
    /**
     * if enable, will allow when ssh connect to a host doesn't exist in the `known_hosts` file;
     * else will ask
     *
     * <del>if disable, only allow hosts in the `known_hosts`</del>
     */
    var allowUnknownHosts:Boolean = false
)
