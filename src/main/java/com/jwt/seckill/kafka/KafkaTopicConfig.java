package com.jwt.seckill.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaTopicConfig {
    // @Value("${spring.kafka.topic-name}")
    // private String topicName;

    @Value("${spring.kafka.partitions}")
    private int partitions;

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name(KafkaTopics.ORDER).partitions(partitions).build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder.name(KafkaTopics.VERIFY).partitions(partitions).build();
    }
}
