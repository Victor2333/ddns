package org.victor.aliyun.ddns.utils

import com.google.common.base.Function
import com.google.common.base.Supplier
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import java.time.Duration

object CacheUtils {
    fun <F, T> createLoadingCache(f: Function<F, T>, maxSize: Long, expired: Duration): LoadingCache<F, T> {
        return CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expired)
            .build(CacheLoader.from(f))
    }
}