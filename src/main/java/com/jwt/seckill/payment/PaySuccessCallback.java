package com.jwt.seckill.payment;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.kafka.KafkaCallbackTemplate;
import com.jwt.seckill.kafka.KafkaTopics;
import com.jwt.seckill.redis.CachePrefix;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.SuccessCallback;

@Component
public class PaySuccessCallback implements SuccessCallback<Order> {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private KafkaCallbackTemplate template;
    private final Logger logger = LoggerFactory.getLogger(PaySuccessCallback.class);

    @Override
    public void onSuccess(Order order) {
        // logger.info("用户{}支付{}件{}商品成功，交易金额{}", order.getUserId(), order.getAmount(), order.getStockId(), order.getTotalPrice());
        template.send(KafkaTopics.ORDER, order.getStockId().toString(), order);
        RBloomFilter<String> filter = redissonClient.getBloomFilter(CachePrefix.BLOOM_FILTER+order.getStockId());
        filter.add(order.getUserId().toString());
    }
}
