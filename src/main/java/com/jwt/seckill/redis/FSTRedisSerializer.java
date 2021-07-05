package com.jwt.seckill.redis;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FSTRedisSerializer implements RedisSerializer<Object> {

    @Resource
    private FSTConfiguration configuration;

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null) {
            return null;
        }
        return configuration.asByteArray(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        return configuration.asObject(bytes);
    }
}
