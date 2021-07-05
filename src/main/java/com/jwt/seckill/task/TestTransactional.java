package com.jwt.seckill.task;

import com.jwt.seckill.entity.Promo;
import com.jwt.seckill.service.impl.PromoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class TestTransactional {
    @Autowired
    private PromoServiceImpl service;
    @Transactional
    public void add() {
        reallyAdd();
    }

    @Transactional
    public void addDelay() {
        Promo promo = new Promo();
        promo.setName("zy");
        promo.setStockId(1L);
        promo.setStart(new Date());
        promo.setEnd(new Date(System.currentTimeMillis()+3600*1000));
        promo.setFinish(-2);
        promo.setPrice(1.0);
        System.out.println("insert:"+service.insert(promo));
        try {
            Thread.sleep(20000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // @Transactional
    public void reallyAdd() {
        Promo promo = new Promo();
        promo.setName("zy");
        promo.setStockId(1L);
        promo.setStart(new Date());
        promo.setEnd(new Date(System.currentTimeMillis()+3600*1000));
        promo.setFinish(-2);
        promo.setPrice(1.0);
        System.out.println("insert:"+service.insert(promo));
        throw new RuntimeException("回滚");
    }
}
