package com.jwt.seckill.task;

import com.jwt.seckill.entity.Promo;
import com.jwt.seckill.entity.Task;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterPromoTask implements Job {
    private final Logger logger = LoggerFactory.getLogger(AfterPromoTask.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long id = context.getJobDetail().getJobDataMap().getLong("id");
        SchedulerTask.service.updateState(id, PromoState.FINISH);
        logger.info("活动"+id+"结束，并成功取消");
    }
}
