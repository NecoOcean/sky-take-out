package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品Mapper接口
 * 提供对Dish实体的数据库操作
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
