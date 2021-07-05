package com.jwt.seckill.kafka;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.PromoCO;
import com.jwt.seckill.entity.UserMsg;
import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.redis.TokenStatus;
import com.jwt.seckill.service.impl.CacheService;
import com.jwt.seckill.util.IdUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class QualificationMessageListener {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheService cacheService;

    org.slf4j.Logger logger = LoggerFactory.getLogger(QualificationMessageListener.class);

    @PostConstruct
    public void closeLog() {
        Logger.getLogger("org").setLevel(Level.WARNING);
        Logger.getLogger("akka").setLevel(Level.WARNING);
        Logger.getLogger("kafka").setLevel(Level.WARNING);
        logger.info("关闭Kafka WARNING级别以下日志");
    }

    @KafkaListener(
            topics = KafkaTopics.VERIFY
            /*
            topicPartitions = @TopicPartition(
                    topic = KafkaTopics.VERIFY
                    // partitions = {"0"}
                    // partitions = {"0", "1", "2", "3"}
            )

             */
    )
    public void listen(@Payload List<UserMsg> userMsgs, Acknowledgment acknowledgment) {
        for (UserMsg userMsg:userMsgs) {
            // 用户进了黑名单
            if (cacheService.inBlackList(userMsg.getUserId())) {
                continue;
            }

            PromoCO promoCO = cacheService.getPromoCO(userMsg.getStockId());

            String token = buildToken(userMsg);

            if (!isValid(promoCO, userMsg, token)) {
                continue;
            }
            Order order = createOrder(userMsg, promoCO);
            cacheService.addOrder(order, token);
            // 最后加token，保证有token时一定有order
            cacheService.addToken(token);
            // logger.info("Create order{}", order.getId());
        }
        acknowledgment.acknowledge();
    }

    private String buildToken(UserMsg msg) {
        return CachePrefix.COUNT_TOKEN_HASH+msg.getUserId()+"::"+msg.getStockId();
    }

    private Order createOrder(UserMsg userMsg, PromoCO promoCO) {
        Order order = new Order();
        order.setUserId(userMsg.getUserId());
        order.setStockId(userMsg.getStockId());
        order.setAmount(userMsg.getAmount());
        order.setPromoId(promoCO.getId());
        order.setStockPrice(promoCO.getPrice());
        order.setTotalPrice(userMsg.getAmount()*promoCO.getPrice());
        order.setId(IdUtil.getId((long) order.hashCode()));
        return order;
    }

    private boolean isValid(PromoCO promoCO, UserMsg userMsg, String token) {
        // 活动已结束
        if (promoCO == null) {
            cacheService.setTokenState(token, TokenStatus.FINISHED);
            return false;
        }
        // 超过限购
        else if (userMsg.getAmount() > promoCO.getLimit()) {
            cacheService.setTokenState(token, TokenStatus.INVALID_AMOUNT);
            return false;
        }
        // 已抢购过还未付款
        else if (stringRedisTemplate.hasKey(token)) {
            Long count = cacheService.incrToken(token);
            // 进黑名单待一天
            if (count == 5) {
                cacheService.addToBlackList(userMsg.getUserId());
            }
            return false;
        }
        return true;
    }
}
