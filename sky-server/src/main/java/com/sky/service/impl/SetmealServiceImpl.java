package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Resource
    public CategoryMapper categoryMapper;

    /**
     * 套餐Mapper，用于数据库操作
     */
    @Resource
    public SetmealMapper setmealMapper;

    /**
     * 套餐包含菜品Mapper，用于数据库操作
     */
    @Resource
    public SetmealDishMapper setmealDishMapper;

    @Resource
    private DishMapper dishMapper;

    /**
     * 新增套餐及其包含菜品信息
     *
     * @param setmealDTO 套餐数据传输对象，包含套餐信息和包含的菜品信息
     */
    @Override
    public void savaWithDIsh(SetmealDTO setmealDTO) {
        // 保存套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        // 获取套餐id
        Long setmealId = setmeal.getId();

        // 保存套餐包含的菜品信息
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && !setmealDishList.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishList) {
                setmealDish.setSetmealId(setmealId);
                setmealDishMapper.insert(setmealDish);
            }
        }

        log.info("套餐保存成功，套餐id:{}", setmealId);
    }

    /**
     * 分页查询套餐及其包含的菜品信息
     *
     * @param setmealPageQueryDTO 套餐分页查询数据传输对象，包含查询条件
     * @return PageResult 分页查询结果，包含套餐及其包含的菜品信息
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 分页查询套餐信息
        IPage<Setmeal> page = new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Setmeal> queryWrapper = Wrappers.lambdaQuery(Setmeal.class)
                .like(StringUtils.isNotBlank(setmealPageQueryDTO.getName()), Setmeal::getName, setmealPageQueryDTO.getName())
                .eq(setmealPageQueryDTO.getCategoryId() != null, Setmeal::getCategoryId, setmealPageQueryDTO.getCategoryId())
                .eq(setmealPageQueryDTO.getStatus() != null, Setmeal::getStatus, setmealPageQueryDTO.getStatus());
        IPage<Setmeal> pageResult = setmealMapper.selectPage(page, queryWrapper);

        // 查询套餐对应的分类名称
        List<SetmealVO> setmealVOList = pageResult.getRecords().stream().map(setmeal -> {
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmeal, setmealVO);
            // 根据分类id查询分类名称
            Category category = categoryMapper.selectById(setmeal.getCategoryId());
            if (category != null) {
                setmealVO.setCategoryName(category.getName());
            }
            return setmealVO;
        }).collect(Collectors.toList());

        return new PageResult(pageResult.getTotal(), setmealVOList);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        // 起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        // 1. 只有在启售套餐时才需要检查
        // 使用 selectCount 和 inSql 进行子查询
        // SQL 逻辑: SELECT COUNT(*) FROM dish WHERE status = DISABLE AND id IN (SELECT dish_id FROM setmeal_dish WHERE setmeal_id = ?)
        // 基本参数校验
        if (id == null || status == null) {
            throw new IllegalArgumentException("套餐ID和状态不能为空");
        }

        // 1. 启售前检查
        if (StatusConstant.ENABLE.equals(status)) {
            // 查询套餐内是否存在停售的菜品
            Long disabledDishCount = dishMapper.selectCount(
                    new LambdaQueryWrapper<Dish>()
                            .eq(Dish::getStatus, StatusConstant.DISABLE)
                            .inSql(Dish::getId, "SELECT dish_id FROM setmeal_dish WHERE setmeal_id = " + id)
            );

            // 如果存在停售菜品，抛出异常
            if (disabledDishCount > 0) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        // 2. 执行状态更新
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        int affectedRows = setmealMapper.updateById(setmeal);

        // 3. 验证更新结果
        if (affectedRows == 0) {
            throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
    }

    @Override
    public SetmealVO getSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        Category category = categoryMapper.selectById(setmeal.getCategoryId());
        setmealVO.setCategoryName(category.getName());
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id", id);
        List<SetmealDish> setmealDishList = setmealDishMapper.selectList(queryWrapper);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 修改套餐表
        setmealMapper.updateById(setmeal);

        // 获取套餐ID
        Long setmealId = setmealDTO.getId();

        // 根据套餐ID删除setmeal_dish表中对应的菜品
        setmealDishMapper.deleteById(setmealId);

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && !setmealDishList.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishList) {
                setmealDish.setSetmealId(setmealId);
            }
        }

        // 重新插入setmeal_dish对应的菜品信息
        setmealDishMapper.insert(setmealDishList);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        // 起售中的套餐不能删除
        if (ids == null || ids.isEmpty()) {
            Long enabledDishCount = setmealMapper.selectCount(
                    new LambdaQueryWrapper<Setmeal>()
                            .eq(Setmeal::getStatus, StatusConstant.ENABLE)
            );
            if (enabledDishCount > 0) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        // 删除套餐表中的数据
        setmealMapper.deleteByIds(ids);

        // 删除套餐菜品关系表中的数据
        setmealDishMapper.deleteByIds(ids);
    }


}
