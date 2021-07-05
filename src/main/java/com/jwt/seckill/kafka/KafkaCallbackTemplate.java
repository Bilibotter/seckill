package com.jwt.seckill.kafka;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class KafkaCallbackTemplate {
    @Autowired
    KafkaTemplate<String, Object> template;

    private Logger logger = LoggerFactory.getLogger(KafkaCallbackTemplate.class);

    public KafkaCallbackTemplate() {
    }

    public ListenableFuture<SendResult<String, Object>> send(String topic, @Nullable Object data) {
        ListenableFuture<SendResult<String, Object>> future = template.send(topic, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(@NotNull Throwable ex) {
                logger.error("发送信息"+data+"失败,"+"原因："+ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                // logger.info("发送信息"+data+"成功");
            }
        });
        return future;
    }

    public ListenableFuture<SendResult<String, Object>> send(String topic, String key, @Nullable Object data) {
        ListenableFuture<SendResult<String, Object>> future = template.send(topic, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(@NotNull Throwable ex) {
                logger.error("发送信息"+data+"失败,"+"原因："+ex.getMessage());
                // TODO: 2021/6/26 将发生失败订单插入本地数据库
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                //logger.info("发生信息"+data+"成功");
            }
        });
        return future;
    }
}
