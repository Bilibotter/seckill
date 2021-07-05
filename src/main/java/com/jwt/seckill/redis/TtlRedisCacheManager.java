package com.jwt.seckill.redis;

import io.jsonwebtoken.lang.Strings;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

public class TtlRedisCacheManager extends RedisCacheManager {
    public TtlRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        // #之后附上过期时间，单位秒
        String[] array = Strings.delimitedListToStringArray(name, "#");
        name = array[0];
        // 不设置过期时间，则默认为5分钟
        long ttl = array.length > 1 ? Long.parseLong(array[1]) : 300;
        cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl));
        return super.createRedisCache(name, cacheConfig);
    }
}
