package org.victor.aliyun.ddns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan("org.victor.aliyun.ddns")
class DDnsApplication

fun main(args: Array<String>) {
	runApplication<DDnsApplication>(*args)
}
