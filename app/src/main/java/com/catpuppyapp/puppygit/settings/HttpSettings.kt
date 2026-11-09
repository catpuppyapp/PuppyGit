package com.catpuppyapp.puppygit.settings

import kotlinx.serialization.Serializable

@Serializable
data class HttpSettings(

    /**
     * note: Repository's config 'http.sslVerify' can override this settings
     */
    var sslVerify: Boolean = true,

)
