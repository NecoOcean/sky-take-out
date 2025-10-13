package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜品口味Mapper接口
 * 用于数据库操作菜品口味相关数据，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对菜品口味的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
    /**
     * 批量插入菜品口味
     *
     * @param flavorList 菜品口味列表
     * @return 插入成功的条数
     */
    int batchInsert(@Param("list") List<DishFlavor> flavorList);


    /**
     * 根据菜品ID列表，批量删除对应的口味数据
     *
     * @param dishIds 菜品ID列表
     */
    void deleteByDishIds(@Param("dishIds") List<Long> dishIds);
}
