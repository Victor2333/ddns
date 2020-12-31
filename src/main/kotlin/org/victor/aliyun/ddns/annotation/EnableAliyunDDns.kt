package org.victor.aliyun.ddns.annotation

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import org.victor.aliyun.ddns.service.AliyunConfig
import org.victor.aliyun.ddns.service.AliyunDns
import org.victor.aliyun.ddns.service.AliyunUpdateConfig

@Target(AnnotationTarget.CLASS)
@EnableConfigurationProperties(value = [AliyunConfig::class, AliyunUpdateConfig::class])
@EnableScheduling
@Import(AliyunDns::class)
annotation class EnableAliyunDDns