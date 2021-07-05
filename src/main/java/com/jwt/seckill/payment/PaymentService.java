package com.jwt.seckill.payment;

import com.jwt.seckill.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Random;

@Service
public class PaymentService {
    @Autowired
    Random random;

    // 注释掉@Async会导致无法回滚
    @Async
    public ListenableFuture<Order> pay(Order order) throws Exception{
        try {
            if (random.nextInt(100) >= 85) {
                throw new PaymentException("支付超时");
            }
        } catch (PaymentException e) {
            e.setData(order);
            throw e;
        }
        return new AsyncResult<>(order);
    }
}
