package org.victor.aliyun.ddns.utils

import com.google.gson.Gson
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.net.Proxy
import java.time.Duration
import java.util.concurrent.TimeUnit

object Fetch {
    private val client = OkHttpClient
        .Builder()
        .connectionPool(ConnectionPool(3, 1, TimeUnit.MINUTES))
        .proxy(Proxy.NO_PROXY)
        .callTimeout(Duration.ofSeconds(15))
        .build()
    private val gson = Gson()

    fun getCurrentGlobalIpAddress(): String {
        val request = Request.Builder()
            .url("https://jsonip.com")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val resp = gson.fromJson(response.body?.string(), JsonIpResponse::class.java)
            return resp.ip
        }
    }
}

data class JsonIpResponse(val ip: String)
