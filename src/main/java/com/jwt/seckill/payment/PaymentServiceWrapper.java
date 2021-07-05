package com.jwt.seckill.payment;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.service.impl.RemainRollbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

// 不要试图在Spring异步任务的任务体中加入错误回调
@Service
public class PaymentServiceWrapper {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaySuccessCallback paySuccessCallback;
    @Autowired
    PayFailureCallback payFailureCallback;
    @Autowired
    private RemainRollbackService rollbackService;
    @Async
    public void pay(Order order) throws Exception {
        ListenableFuture<Order> future = paymentService.pay(order);
        future.addCallback(paySuccessCallback, payFailureCallback);
    }
}
