package com.jwt.seckill.task;

import com.jwt.seckill.dao.TaskDao;
import com.jwt.seckill.entity.Promo;
import com.jwt.seckill.service.impl.PromoServiceImpl;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class SchedulerTask {
    public static PromoServiceImpl service;
    public static TaskDao taskDao;
    private RedisTemplate<Object, Object> template;
    private final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

    // 断电重启和故障恢复时重新调度和发布已失败任务
    @PostConstruct
    public void rescheduledFailedTask() {
        List<Promo> restarts = service.selectScheduledFailed();
        for (Promo restart : restarts) {
            restart.setFinish(PromoState.UNSCHEDULED);
        }
        if (!restarts.isEmpty()) {
            service.updateBatch(restarts);
            logger.info("重新调度"+restarts.size()+"条失败任务。");
        }
    }

    @Autowired
    public SchedulerTask(PromoServiceImpl service, TaskDao taskDao) {
        SchedulerTask.service = service;
        SchedulerTask.taskDao = taskDao;
    }

    // 每隔10秒执行一次：*/5 * * * * ?
    // @Scheduled(cron = "*/10 * * * * ?")
    // @Scheduled(fixedRate = 5*1000)
    // 每20分钟执行一次
    @Scheduled(fixedRate = 15*60*1000)
    // @Scheduled(cron = "0 */15 * * * ?")
    public void schedule() throws Exception {
        List<Promo> promos = service.queryPromoNotRemainOneHours();
        if (promos.isEmpty()) {
            logger.info("无新增将发布活动");
            return;
        }
        logger.info("一小时内开始的秒杀活动增加"+promos.size()+"条.");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        for (Promo promo:promos) {
            addScheduleTask(promo, scheduler);
            addCallbackTask(promo, scheduler);
        }
        scheduler.start();
        // 避免调度失败却已更新数据库的情况
        updatePromoDB(promos);
        logger.info("成功启动调度器，任务条数为"+promos.size()+"项.");
    }

    private void addScheduleTask(Promo promo, Scheduler scheduler) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(PublishPromoTask.class)
                .withIdentity("task"+promo.getId())
                .usingJobData("end", promo.getEnd().getTime())
                .usingJobData("promoId", promo.getId())
                .usingJobData("stockId", promo.getStockId())
                .usingJobData("state", (Integer) promo.getFinish())
                .usingJobData("price", promo.getPrice())
                .usingJobData("limit", promo.getLimit())
                .build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(promo.getStart())
                .withIdentity("trigger"+promo.getId())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void addCallbackTask(Promo promo, Scheduler scheduler) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(AfterPromoTask.class)
                .withIdentity("callback"+promo.getId())
                .usingJobData("id", promo.getId())
                .build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(promo.getEnd())
                .withIdentity("ctrigger"+promo.getId())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void updatePromoDB(List<Promo> promos) {
        for (Promo promo:promos) {
            promo.setFinish(PromoState.SCHEDULED);
        }
        logger.info("Update result:"+service.updateBatch(promos));
    }
}
