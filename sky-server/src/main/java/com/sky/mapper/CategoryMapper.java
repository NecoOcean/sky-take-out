package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper接口
 * 提供对Category实体的数据库操作方法，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对菜品分类和套餐分类的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
