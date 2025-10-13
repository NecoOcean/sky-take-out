package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 套餐菜品Mapper接口
 * 用于数据库操作套餐菜品相关数据，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对套餐菜品的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
    /**
     * 根据菜品id查询对应的套餐id
     *
     * @param dishIds 菜品id列表，不可为null或空
     * @return 包含对应套餐id的列表，若未找到则返回空列表
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐ID查询该套餐下的菜品列表
     *
     * @param setmealId 套餐ID
     * @return 套餐菜品列表
     */
    List<SetmealDish> listBySetmealId(@Param("setmealId") Long setmealId);

}
