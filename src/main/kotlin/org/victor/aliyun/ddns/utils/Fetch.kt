package org.victor.aliyun.ddns.utils

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

object Fetch {
    private val client = OkHttpClient()
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
