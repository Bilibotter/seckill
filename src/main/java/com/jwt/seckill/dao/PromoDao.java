package com.jwt.seckill.dao;

import com.jwt.seckill.entity.Promo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * (Promo)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-28 16:45:29
 */
@Repository
public interface PromoDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Promo queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Promo> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param promo 实例对象
     * @return 对象列表
     */
    List<Promo> queryAll(Promo promo);

    /**
     * 新增数据
     *
     * @param promo 实例对象
     * @return 影响行数
     */
    int insert(Promo promo);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Promo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Promo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Promo> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Promo> entities);

    /**
     * 修改数据
     *
     * @param promo 实例对象
     * @return 影响行数
     */
    int update(Promo promo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Promo queryByStockId(Long stockId);

    List<Long> getUpdateId(Long lastId);

    List<Promo> queryUpdatePromo(String date);

    Integer updateState(Long id, Integer status);

    List<Promo> queryPromoNotRemainOneHours(String gate);

    Integer updateBatch(List<Promo> promos);

    List<Promo> selectScheduledFailed();
}

