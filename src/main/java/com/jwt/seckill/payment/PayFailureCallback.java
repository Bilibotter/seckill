package com.jwt.seckill.payment;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.service.impl.RemainRollbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;

// 不要试图在Spring异步任务的任务体中加入错误回调
@Component
public class PayFailureCallback implements FailureCallback {
    @Autowired
    private RemainRollbackService rollbackService;
    private final Logger logger = LoggerFactory.getLogger(PayFailureCallback.class);

    @Override
    public void onFailure(Throwable ex) {
        Order order = (Order) ((PaymentException) ex).getData();
        logger.warn("用户{}支付{}件{}商品超时", order.getUserId(), order.getAmount(), order.getStockId());
        rollbackService.rollbackRemain(order.getStockId(), order.getAmount());
    }
}
