package com.jwt.seckill.kafka;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.entity.StockMsg;
import com.jwt.seckill.entity.UserMsg;
import org.apache.kafka.common.serialization.Deserializer;
import org.nustaq.serialization.FSTConfiguration;

public class KafkaDeserializer implements Deserializer<Object> {
    private final static FSTConfiguration configuration;

    static {
        configuration = FSTConfiguration.createDefaultConfiguration();
        configuration.registerClass(Stock.class);
        configuration.registerClass(Order.class);
        configuration.registerClass(StockMsg.class);
        configuration.registerClass(UserMsg.class);
    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        return configuration.asObject(bytes);
    }
}
