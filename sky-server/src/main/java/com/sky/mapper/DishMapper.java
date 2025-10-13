package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品Mapper接口
 * 提供对Dish实体的数据库操作方法，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对菜品的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
