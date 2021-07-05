package com.jwt.seckill.kafka;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.StockMsg;
import com.jwt.seckill.service.impl.OrderServiceImpl;
import com.jwt.seckill.util.IdUtil;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMessageListener {
    private final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class);
    @Autowired
    KafkaCallbackTemplate template;
    @Autowired
    OrderServiceImpl service;

    @KafkaListener(
            topics = KafkaTopics.ORDER
            /*
            topicPartitions = @TopicPartition(
                    topic = KafkaTopics.ORDER
                    // partitions = {"0"}
                    // partitions = {"0", "1", "2", "3"}

                    partitionOffsets = {
                            @PartitionOffset(partition = "0", initialOffset = "0"),
                            @PartitionOffset(partition = "1", initialOffset = "0"),
                            @PartitionOffset(partition = "2", initialOffset = "0"),
                            @PartitionOffset(partition = "3", initialOffset = "0")
                    }
            )
             */
    )
    public void listen(@Payload List<Order> orders, Acknowledgment acknowledgment) {
        List<List<Order>> partition = ListUtils.partition(orders, 200);
        for (List<Order> batch:partition) {
            try {
                service.createBatchOrder(batch);
            }catch (DuplicateKeyException e1) {
                logger.warn("执行{}条插入时，发生重复消费", batch.size());
                createOrderNoBatch(batch);
            }catch (RuntimeException e2) {
                logger.error("执行{}条插入时，未知错误导致超卖", batch.size());
                createOrderNoBatch(batch);
            }
        }
        acknowledgment.acknowledge();
    }

    private void createOrderNoBatch(List<Order> orders) {
        for (Order order:orders) {
            service.createOrder(order);
        }
    }
}
