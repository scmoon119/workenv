package com.sindory.workenv

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import com.sindory.workenv.utils.SindoryProperties

@EnableConfigurationProperties(SindoryProperties::class)
@SpringBootApplication
class WorkEnvApplication

fun main(args: Array<String>) {
	runApplication<WorkEnvApplication>(*args)
}
