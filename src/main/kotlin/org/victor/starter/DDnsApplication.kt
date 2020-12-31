package org.victor.starter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.victor.aliyun.ddns.annotation.EnableAliyunDDns

@SpringBootApplication
@EnableAliyunDDns
class DDnsApplication

fun main(args: Array<String>) {
    runApplication<DDnsApplication>(*args)
}
