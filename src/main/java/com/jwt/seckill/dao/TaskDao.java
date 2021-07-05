package com.jwt.seckill.dao;

import com.jwt.seckill.entity.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * (Task)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-21 14:17:15
 */
@Repository
public interface TaskDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Task queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Task> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param task 实例对象
     * @return 对象列表
     */
    List<Task> queryAll(Task task);

    /**
     * 新增数据
     *
     * @param task 实例对象
     * @return 影响行数
     */
    int insert(Task task);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Task> 实例对象列表
     * @return 影响行数
     */
    @Transactional
    int insertBatch(@Param("entities") List<Task> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Task> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Task> entities);

    /**
     * 修改数据
     *
     * @param task 实例对象
     * @return 影响行数
     */
    int update(Task task);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Task getLastTask();

    Integer updateByLastId(Task task);
}

