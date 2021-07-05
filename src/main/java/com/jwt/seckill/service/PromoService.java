package com.jwt.seckill.service;

import com.jwt.seckill.entity.Promo;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * (Promo)表服务接口
 *
 * @author makejava
 * @since 2021-06-17 18:29:15
 */
@Service
public interface PromoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Promo queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Promo> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param promo 实例对象
     * @return 实例对象
     */
    Promo insert(Promo promo);

    /**
     * 修改数据
     *
     * @param promo 实例对象
     * @return 实例对象
     */
    Promo update(Promo promo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
