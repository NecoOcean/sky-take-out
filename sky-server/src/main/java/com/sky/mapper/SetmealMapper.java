package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 套餐Mapper接口
 * 提供对Setmeal实体的数据库操作方法，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对套餐的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface SetmealMapper extends BaseMapper<Setmeal> {

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
