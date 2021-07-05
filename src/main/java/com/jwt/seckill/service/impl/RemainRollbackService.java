package com.jwt.seckill.service.impl;

import com.jwt.seckill.redis.CachePrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class RemainRollbackService {
    @Autowired
    StringRedisTemplate template;
    @Autowired
    Random random;

    // @Transactional
    public void rollbackRemain(Long stockId, Integer amount) {
        if (template.opsForValue().getOperations().getExpire(CachePrefix.REMAIN_PREFIX+stockId) > 300) {
            template.opsForValue().increment(CachePrefix.REMAIN_PREFIX+stockId, amount);
        }
    }
}
