package com.jwt.seckill.service.impl;

import com.github.pagehelper.PageHelper;
import com.jwt.seckill.entity.Promo;
import com.jwt.seckill.dao.PromoDao;
import com.jwt.seckill.entity.PromoCO;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.service.PromoService;
import com.jwt.seckill.task.PromoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * (Promo)表服务实现类
 *
 * @author makejava
 * @since 2021-06-17 18:29:15
 */
@Service("promoService")
public class PromoServiceImpl implements PromoService {
    private final Logger logger = LoggerFactory.getLogger(PromoServiceImpl.class);
    @Resource
    private PromoDao promoDao;

    @Autowired
    private Random random;

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private RedisTemplate<Object, Object> objectTemplate;

    @Qualifier(value = "StdDateFormat")
    @Autowired
    private SimpleDateFormat ft;

    // private final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Promo queryById(Long id) {
        return this.promoDao.queryById(id);
    }

    public Promo queryByStockId(Long stockId) {
        return promoDao.queryByStockId(stockId);
    }

    // 主要为Redis的事务支持
    @Transactional
    public void publishPromo(Long end, Long promoId, Long stockId, Integer state, Double price, Integer limit) {
        long expire = end - System.currentTimeMillis();
        Stock stock = stockService.queryById(stockId);
        if (canPublish(expire, promoId, stock) != 1) {
            return;
        }
        PromoCO promoCO = new PromoCO();
        promoCO.setId(promoId);
        promoCO.setPrice(price);
        promoCO.setLimit(limit);
        // 将价格修改为活动价格
        stock.setPrice(price);
        Long remain = (Long) stock.getRemain();
        // 库存和对象分开记录
        stock.setRemain(null);
        // 重试3次，重试间隔30秒
        for (int retry=1;retry<=3;retry++) {
            try {
                expire = end - System.currentTimeMillis();
                // 活动价格的Stock与库存持续到活动结束
                objectTemplate.opsForValue().set(CachePrefix.STOCK_PREFIX+stockId, stock, Duration.ofSeconds(expire));
                template.opsForValue().setIfAbsent(CachePrefix.REMAIN_PREFIX+stockId, remain.toString());
                // 将活动标志放在最后，保证有活动标志时一定有预热缓存
                objectTemplate.opsForValue().set(CachePrefix.PROMO_PREFIX +stockId, promoCO, Duration.ofMillis(expire));
                // 活动结束后3~6分钟库存都依然有效
                template.expire(CachePrefix.REMAIN_PREFIX+stockId, Duration.ofSeconds(expire+180+random.nextInt(180)));
                // 发布状态和调度状态行为一致，没必要使用发布状态
                // promoDao.updateState(id, PromoState.PUBLISH);
                logger.info("成功发布秒杀活动"+ promoId +"，活动商品"+stockId);
                break;
            } catch (RuntimeException e) {
                e.printStackTrace();
                logger.warn("发布活动{}时发生异常{}次，原因：{}", promoId,retry, e);
                if (retry == 3) {
                    logger.error("尝试发布活动"+ promoId +"三次均失败，不再发布。");
                    promoDao.updateState(promoId, PromoState.PUBLISH_FAIL_3);
                }
                else {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException interruptedException) {
                        logger.warn(interruptedException.getMessage());
                    }
                }
            }
        }
    }

    private Integer getUpdateState(Integer state) {
        if (state == PromoState.SCHEDULED) {
            return PromoState.PUBLISH_FAIL_1;
        }
        else if (state == PromoState.PUBLISH_FAIL_1) {
            return PromoState.PUBLISH_FAIL_2;
        }
        // state == PromoState.PUBLISH_FAIL_2
        else {
            return PromoState.PUBLISH_FAIL_3;
        }
    }

    public Long canPublish(Long expire, Long id, Stock stock) {
        // 不会生效，因为只会扫描未结束活动
        if (expire < 0) {
            logger.warn("尝试发布已过期活动");
            promoDao.updateState(id, PromoState.FINISH);
            logger.info("将已过期活动"+id+"置为结束状态");
            return -1L;
        }
        if (stock == null) {
            logger.warn("尝试发布不存在商品,"+"活动Id"+id);
            promoDao.updateState(id, PromoState.UN_EXIST);
            return -2L;
        }
        if ((Long) stock.getRemain() < 1) {
            logger.warn("尝试发布已售罄商品,商品Id"+stock.getId()+",活动Id"+id);
            return 0L;
        }
        return 1L;
    }

    public List<Long> getUpdateId(Long lastId) {
        return promoDao.getUpdateId(lastId);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Promo> queryAllByLimit(int offset, int limit) {
        PageHelper.startPage(offset, limit);
        return this.promoDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param promo 实例对象
     * @return 实例对象
     */
    @Override
    public Promo insert(Promo promo) {
        Stock exist = stockService.queryById(promo.getStockId());
        if (exist == null) {
            throw new RuntimeException("秒杀活动商品不存在！");
        }
        this.promoDao.insert(promo);
        return promo;
    }

    /**
     * 修改数据
     *
     * @param promo 实例对象
     * @return 实例对象
     */
    @Override
    public Promo update(Promo promo) {
        this.promoDao.update(promo);
        return this.queryById(promo.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.promoDao.deleteById(id) > 0;
    }

    public List<Promo> queryUpdatePromo(Date date) {
        return this.promoDao.queryUpdatePromo(ft.format(date));
    }

    public int insertBatch(List<Promo> promos) {
        return this.promoDao.insertBatch(promos);
    }

    public Integer updateState(Long id, Integer state) {
        return this.promoDao.updateState(id, state);
    }

    // 获取未调度且一个小时内将开始的秒杀活动
    public List<Promo> queryPromoNotRemainOneHours() {
        return this.queryPromoNotRemainDelay(3600L);
    }

    public List<Promo> queryPromoNotRemainDelay(Long delay) {
        String gate = ft.format(new Date(System.currentTimeMillis()+delay*1000));
        return this.promoDao.queryPromoNotRemainOneHours(gate);
    }

    public Integer updateBatch(List<Promo> promos) {
        return this.promoDao.updateBatch(promos);
    }

    public List<Promo> selectScheduledFailed() {
        return promoDao.selectScheduledFailed();
    }
}
