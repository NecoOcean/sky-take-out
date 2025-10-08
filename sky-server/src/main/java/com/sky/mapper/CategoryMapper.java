package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper接口
 * 提供对Category实体的数据库操作
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
