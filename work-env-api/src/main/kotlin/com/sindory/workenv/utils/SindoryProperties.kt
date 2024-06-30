package com.sindory.workenv.utils

import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("sindory")
data class SindoryProperties (
    val server: ServerProperties,
){
    data class ServerProperties(
        val expireSecond: Long,
    ){

    }
}