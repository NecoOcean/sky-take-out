package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐Mapper接口
 * 提供对Setmeal实体的数据库操作方法，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对套餐的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
