package org.victor.aliyun.ddns

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.victor.aliyun.ddns.service.AliyunDns

@SpringBootTest
class DDnsApplicationTests(
        @Autowired
        val aliyunDns: AliyunDns
) {

    @Test
    fun contextLoads() {
        aliyunDns.sync()
    }

}
