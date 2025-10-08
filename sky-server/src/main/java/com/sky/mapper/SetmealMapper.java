package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐Mapper接口
 * 提供对Setmeal实体的数据库操作
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
