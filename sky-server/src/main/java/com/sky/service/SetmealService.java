package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 套餐服务接口
 * 提供对套餐的新增、分页查询等操作，支持批量操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface SetmealService {

    /**
     * 新增套餐及其包含的菜品
     *
     * @param setmealDTO 套餐数据传输对象，包含套餐信息和包含的菜品信息
     */
    void savaWithDIsh(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐及其包含的菜品信息
     *
     * @param setmealPageQueryDTO 套餐分页查询数据传输对象，包含查询条件
     * @return PageResult 分页查询结果，包含套餐及其包含的菜品信息
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void startOrStop(Integer status, Long id);

    SetmealVO getSetmealById(Long id);

    void update(SetmealDTO setmealDTO);

    void deleteByIds(List<Long> ids);
}
