package com.jwt.seckill.service.impl;

import com.jwt.seckill.entity.Order;
import com.jwt.seckill.dao.OrderDao;
import com.jwt.seckill.entity.Stock;
import com.jwt.seckill.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * (Order)表服务实现类
 *
 * @author makejava
 * @since 2021-06-26 12:44:29
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Resource
    private OrderDao orderDao;

    @Autowired
    StockServiceImpl service;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Order queryById(Long id) {
        return this.orderDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Order> queryAllByLimit(int offset, int limit) {
        return this.orderDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param order 实例对象
     * @return 实例对象
     */
    @Override
    public Order insert(Order order) {
        this.orderDao.insert(order);
        return order;
    }

    /**
     * 修改数据
     *
     * @param order 实例对象
     * @return 实例对象
     */
    @Override
    public Order update(Order order) {
        this.orderDao.update(order);
        return this.queryById(order.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.orderDao.deleteById(id) > 0;
    }

    @Transactional
    public void createBatchOrder(List<Order> orders) {
        orderDao.insertBatch(orders);
        Map<Long, Integer> remains = new HashMap<>();
        Integer remain;
        // 库存更新合并
        for (Order order:orders) {
            remain = remains.get(order.getStockId());
            remain = remain == null ? order.getAmount() : remain + order.getAmount();
            remains.put(order.getStockId(), remain);
        }
        updateBatchStock(remains);
    }

    @Transactional
    public void updateBatchStock(Map<Long, Integer> remains) {
        List<Stock> stocks = new LinkedList<>();
        for (Map.Entry<Long, Integer> entry:remains.entrySet()) {
            Integer amount = entry.getValue();
            if(!service.soldStock(entry.getKey(), entry.getValue())) {
                throw new RuntimeException("未知错误导致商品"+entry.getKey()+"更新失败");
            }
        }
    }

    @Transactional
    public void createOrder(Order order) {
        if (order.getPromoId() == null) {
            order.setPromoId(-1L);
        }
        try {
            orderDao.insert(order);
            // logger.info("成功创建订单{}", order);
        } catch (DuplicateKeyException ignore) {
            logger.warn("尝试重复插入{}",order);
            return;
        }
        if (!service.soldStock(order.getStockId(), order.getAmount())) {
            logger.error("未知错误导致超卖，订单ID{}", order.getId());
            throw new RuntimeException("未知错误导致超卖，订单ID"+order.getId());
        }
    }
}
