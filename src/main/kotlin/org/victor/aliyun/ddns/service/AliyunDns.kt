package org.victor.aliyun.ddns.service

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest
import com.aliyuncs.profile.DefaultProfile
import com.google.common.base.Function
import org.apache.logging.log4j.util.Strings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.victor.aliyun.ddns.utils.Fetch
import org.victor.aliyun.ddns.utils.createLoadingCache
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "aliyun.access")
data class AliyunConfig(val regionId: String, val key: String, val secret: String) {
    private val logger: Logger = LoggerFactory.getLogger(AliyunConfig::class.java)
    private fun getProfile(): DefaultProfile {
        logger.info("Aliyun access config {}", this)
        assert(Strings.isNotBlank(regionId))
        assert(Strings.isNotBlank(key))
        assert(Strings.isNotBlank(secret))
        return DefaultProfile.getProfile(regionId, key, secret)
    }

    fun getIAcsClient(): DefaultAcsClient {
        return DefaultAcsClient(getProfile())
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "aliyun.update")
data class AliyunUpdateConfig(val domainName: String, val keyWord: String, val type: String)

@Service
class AliyunDns(aliyunConfig: AliyunConfig, val aliyunUpdateConfig: AliyunUpdateConfig) {

    val logger: Logger = LoggerFactory.getLogger(AliyunDns::class.java)

    val client = aliyunConfig.getIAcsClient()

    val cacheLoading = Function<AliyunUpdateConfig, List<DescribeDomainRecordsResponse.Record>>
    {
        logger.info("Get data from remote {}", it!!)
        val describeRequest = DescribeDomainRecordsRequest()
            .apply {
                this.domainName = it.domainName
                this.rrKeyWord = it.keyWord
                this.type = it.type
            }

        client.getAcsResponse(describeRequest).domainRecords
    }.createLoadingCache(1, Duration.ofHours(2))

    fun sync(domainName: String, keyWord: String, type: String) {
        logger.info("domain name: {}", domainName)
        logger.info("keyword: {}", keyWord)
        logger.info("type: {}", type)
        val describeRequest = DescribeDomainRecordsRequest()
        describeRequest.domainName = domainName
        describeRequest.rrKeyWord = keyWord
        describeRequest.type = type

        val currentIdAddress = Fetch.getCurrentGlobalIpAddress()
        logger.info("Current ip: {}", currentIdAddress)

        val domainRecords = cacheLoading.get(AliyunUpdateConfig(domainName, keyWord, type))

        logger.info("Domain records: {}", domainRecords.map { it.value })

        var isUpdated = false
        domainRecords.forEach {
            if (currentIdAddress != it.value) {
                isUpdated = true
                val updateRequest = UpdateDomainRecordRequest()
                updateRequest.rr = it.rr
                updateRequest.recordId = it.recordId
                updateRequest.value = currentIdAddress
                updateRequest.type = type
                client.getAcsResponse(updateRequest)
            }
        }
        if (isUpdated) {
            logger.info("Update record and clear all cache")
            cacheLoading.invalidateAll()
        }
    }

    @Scheduled(fixedDelay = 300000)
    fun sync() {
        try {
            assert(Strings.isNotBlank(aliyunUpdateConfig.domainName))
            assert(Strings.isNotBlank(aliyunUpdateConfig.keyWord))
            assert(Strings.isNotBlank(aliyunUpdateConfig.type))
            sync(aliyunUpdateConfig.domainName, aliyunUpdateConfig.keyWord, aliyunUpdateConfig.type)
        } catch (e: Exception) {
            logger.error("sync failed", e)
        }
    }
}