package com.jwt.seckill.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;
import java.util.Random;


@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory, FSTRedisSerializer serializer) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 开启事务支持
        template.setEnableTransactionSupport(true);
        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key-value结构序列化数据结构
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        // hash数据结构序列化方式,必须这样否则存hash 就是基于jdk序列化的
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);
        // 启用默认序列化方式
        template.setEnableDefaultSerializer(true);
        template.setDefaultSerializer(serializer);
        return template;
    }

    @Bean(name = "SeckillRedisTemplate")
    @Primary
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setEnableTransactionSupport(true); //打开事务支持
        return template;
    }

    /*
    @Bean
    public CacheManager cacheManager(RedisTemplate<Object, Object> objectTemplate) {
        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()
                        // 设置key为String
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(objectTemplate.getStringSerializer()))
                        // 设置value 为自动转Json的Object
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(objectTemplate.getValueSerializer()))
                        // 不缓存null
                        .disableCachingNullValues();
        RedisCacheManager cacheManager = new RedisCacheManager(objectTemplate);
        // 设置缓存过期时间（秒）
        cacheManager.setDefaultExpiration(3600);
        return cacheManager;
        return new TtlRedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(objectTemplate.getConnectionFactory())),
                RedisCacheConfiguration.defaultCacheConfig());
    }
     */
}
