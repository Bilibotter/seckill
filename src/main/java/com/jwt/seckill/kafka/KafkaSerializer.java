package com.jwt.seckill.kafka;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.entity.StockMsg;
import com.jwt.seckill.entity.UserMsg;
import org.apache.kafka.common.serialization.Serializer;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaSerializer implements Serializer<Object> {
    private final static FSTConfiguration configuration;

    static {
        configuration = FSTConfiguration.createDefaultConfiguration();
        configuration.registerClass(Stock.class);
        configuration.registerClass(Order.class);
        configuration.registerClass(StockMsg.class);
        configuration.registerClass(UserMsg.class);
    }

    @Override
    public byte[] serialize(String s, Object o) {
        return configuration.asByteArray(o);
    }
}
