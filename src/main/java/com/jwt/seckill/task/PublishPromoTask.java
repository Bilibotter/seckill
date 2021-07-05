package com.jwt.seckill.task;

import org.quartz.*;

public class PublishPromoTask implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        Long end = map.getLong("end");
        Long promoId = map.getLong("promoId");
        Long stockId = map.getLong("stockId");
        Integer state = map.getInt("state");
        Double price = map.getDouble("price");
        Integer limit = map.getInt("limit");
        SchedulerTask.service.publishPromo(end, promoId, stockId, state, price, limit);
    }
}
