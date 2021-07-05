package com.jwt.seckill.service.impl;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.PromoCO;
import com.jwt.seckill.entity.UserMsg;
import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.redis.TokenStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    @Autowired
    StringRedisTemplate stringTemplate;
    @Autowired
    RedisTemplate<Object, Object> objectTemplate;
    @Qualifier(value = "sold")
    @Autowired
    private DefaultRedisScript<Long> soldScript;

    public Order getOrder(String token) {
        return (Order) objectTemplate.opsForValue().get(CachePrefix.ORDER_PREFIX+token);
    }

    public void addOrder(Order order, String token) {
        objectTemplate.opsForValue().set(CachePrefix.ORDER_PREFIX+token, order, 5, TimeUnit.MINUTES);
    }

    public void deleteOrder(String token) {
        objectTemplate.delete(CachePrefix.ORDER_PREFIX+token);
    }

    // 获得资格后需要在5分钟内支付
    public void addToken(String token) {
        stringTemplate.opsForValue().set(token, "1", 5, TimeUnit.MINUTES);
    }

    public Boolean deleteToken(String token) {
        return stringTemplate.delete(token);
    }

    public Long incrToken(String token) {
        Long result = stringTemplate.opsForValue().increment(token, 1);
        stringTemplate.expire(token, 5, TimeUnit.SECONDS);
        return result;
    }

    public void addToBlackList(Long userId) {
        stringTemplate.opsForValue().set(CachePrefix.BLACK_LIST+userId, "",1L, TimeUnit.DAYS);
    }

    public void setTokenState(String token, String state) {
        stringTemplate.opsForValue().set(token, state, 5, TimeUnit.MINUTES);
    }

    public Boolean inBlackList(Long userId) {
        return stringTemplate.hasKey(CachePrefix.BLACK_LIST+userId);
    }

    public Long execSoldScript(UserMsg msg) {
        return execSoldScript(msg.getStockId(), msg.getAmount());
    }

    public Long execSoldScript(Long stockId, Integer amount) {
        return stringTemplate.execute(soldScript,
                Collections.singletonList(CachePrefix.REMAIN_PREFIX+stockId),
                amount.toString());
    }

    public PromoCO getPromoCO(Long stockId) {
        return (PromoCO) objectTemplate.opsForValue().get(CachePrefix.PROMO_PREFIX+stockId);
    }
}
