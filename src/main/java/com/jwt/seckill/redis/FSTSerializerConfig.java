package com.jwt.seckill.redis;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.entity.PromoCO;
import com.jwt.seckill.entity.Stock;
import org.aspectj.weaver.ast.Or;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;

@Configuration
public class FSTSerializerConfig {
    @Bean
    public FSTConfiguration fstConfiguration() {
        FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
        configuration.registerClass(Stock.class);
        configuration.registerClass(PromoCO.class);
        configuration.registerClass(Order.class);
        configuration.registerClass(MapSession.class);
        return configuration;
    }
}
