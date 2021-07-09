package com.jwt.seckill.service.impl;

import com.jwt.seckill.entity.PromoCO;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.dao.StockDao;
import com.jwt.seckill.redis.CachePrefix;
import com.jwt.seckill.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * (Stock)表服务实现类
 *
 * @author makejava
 * @since 2021-06-20 15:23:16
 */
@Service("stockService")
public class StockServiceImpl implements StockService {
    private static final String STOCK_PREFIX = "stk::";
    private static final String REMAIN_PREFIX = "rmn::";
    @Resource
    private StockDao stockDao;

    @Autowired
    private RedisTemplate<Object, Object> objectTemplate;

    @Autowired
    private StringRedisTemplate stringTemplate;

    @Autowired
    private Random random;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Stock queryById(Long id) {
        return stockDao.queryById(id);
    }

    public Stock getRecentStock(Long id) {
        Stock stock = (Stock) objectTemplate.opsForValue().get(CachePrefix.STOCK_PREFIX+id);
        if (stock == null) {
            stock = this.stockDao.queryById(id);
            if (stock == null) {
                return stock;
            }
            addToCacheIfPresent(stock);
        }
        // 无符号用Long
        stock.setRemain(Long.valueOf(stringTemplate.opsForValue().get(CachePrefix.REMAIN_PREFIX+stock.getId())));
        return stock;
    }

    // 数据库不存在此Id则返回false
    private boolean addToCacheIfPresent(Stock stock) {
        if (stock != null) {
            long expire = 600 + random.nextInt(600);
            // 库存与消息分开缓存
            // setIfAbsent一是防止影响到缓存预热的过期时间
            // 而是防止将旧数据插入
            stringTemplate.opsForValue().setIfAbsent(CachePrefix.REMAIN_PREFIX+stock.getId(), stock.getRemain().toString(),
                    expire, TimeUnit.SECONDS);
            stock.setRemain(null);
            // 重复设置不会有问题，因此不使用ifAbsent
            objectTemplate.opsForValue().setIfAbsent(CachePrefix.STOCK_PREFIX+stock.getId(), stock,  expire, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    public Long getRecentRemain(Long id, Long remain) {
        // null when used in pipeline / transaction.
        Boolean absent = stringTemplate.opsForValue().setIfAbsent(REMAIN_PREFIX+id, remain.toString());
        return absent ? remain:getRemainInCache(id);
    }

    private Long getRemainInCache(Long id) {
        String remain = stringTemplate.opsForValue().get(REMAIN_PREFIX+id);
        return remain == null ? null : Long.valueOf(remain);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Stock> queryAllByLimit(int offset, int limit) {
        return this.stockDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param stock 实例对象
     * @return 实例对象
     */
    @Override
    public Stock insert(Stock stock) {
        this.stockDao.insert(stock);
        return stock;
    }

    /**
     * 修改数据
     *
     * @param stock 实例对象
     * @return 实例对象
     */
    @Override
    public Stock update(Stock stock) {
        this.stockDao.update(stock);
        return this.queryById(stock.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.stockDao.deleteById(id) > 0;
    }

    public boolean soldStock(Long id, Integer amount, Integer remain) {
        return stockDao.soldStock(id, amount, remain) == 1;
    }

    public Integer selectRemain(Long id) {
        return stockDao.selectRemain(id);
    }

    public int soldStocks(List<Stock> stocks) {
        return stockDao.soldStocks(stocks);
    }
}
