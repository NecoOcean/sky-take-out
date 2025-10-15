package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜品业务层实现类
 * <p>
 * 负责处理与菜品相关的业务逻辑，包括菜品及其口味信息的保存、更新、删除、启用/禁用、分页查询、
 * 根据分类查询等操作。所有写操作均通过事务保证数据一致性。
 * </p>
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    /**
     * 菜品数据访问层对象
     * <p>负责与数据库中 dish 表进行 CRUD 操作</p>
     */
    @Resource
    private DishMapper dishMapper;

    /**
     * 分类数据访问层对象
     * <p>用于查询菜品所属分类信息，避免 N+1 问题</p>
     */
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 菜品口味数据访问层对象
     * <p>负责 dish_flavor 表的批量插入、删除及查询</p>
     */
    @Resource
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 套餐-菜品关联数据访问层对象
     * <p>用于校验菜品是否被套餐引用，以及查询套餐 ID 列表</p>
     */
    @Resource
    private SetmealDishMapper setmealDishMapper;

    /**
     * 套餐数据访问层对象
     * <p>当禁用菜品时，级联禁用相关套餐</p>
     */
    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 保存菜品及其口味信息
     * <p>
     * 事务粒度：整个方法为一个事务，任何一步失败均回滚，保证菜品与口味数据强一致。
     * </p>
     *
     * @param dishDTO 封装了菜品基础信息与口味列表的数据传输对象，不能为 {@code null}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDTO dishDTO) {
        // 1. 保存菜品主表信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        // 2. 获取自增主键，用于后续口味数据关联
        Long dishId = dish.getId();

        // 3. 批量保存口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (CollectionUtils.isNotEmpty(flavors)) {
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.batchInsert(flavors);
        }

        log.info("保存菜品成功，菜品ID：{}", dishId);
    }

    /**
     * 菜品分页查询
     * <p>
     * 支持按名称模糊、分类、状态筛选，并按更新时间倒序排列；<br>
     * 通过一次性批量查询分类名称，彻底避免 N+1 性能问题。
     * </p>
     *
     * @param dishPageQueryDTO 分页查询参数，不能为 {@code null}
     * @return 分页结果对象，记录总数与当前页 VO 列表
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 1. 构造分页对象
        Page<Dish> page = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        // 2. 动态组装查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if (dishPageQueryDTO.getName() != null && !dishPageQueryDTO.getName().trim().isEmpty()) {
            queryWrapper.like(Dish::getName, dishPageQueryDTO.getName().trim());
        }
        if (dishPageQueryDTO.getCategoryId() != null) {
            queryWrapper.eq(Dish::getCategoryId, dishPageQueryDTO.getCategoryId());
        }
        if (dishPageQueryDTO.getStatus() != null) {
            queryWrapper.eq(Dish::getStatus, dishPageQueryDTO.getStatus());
        }
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 3. 执行分页查询
        Page<Dish> dishPage = dishMapper.selectPage(page, queryWrapper);
        List<Dish> records = dishPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new PageResult(dishPage.getTotal(), List.of());
        }

        // 4. 一次性查询分类名称，构建映射
        Set<Long> categoryIds = records.stream()
                .map(Dish::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = categoryIds.isEmpty() ? Map.of() :
                categoryMapper.selectByIds(categoryIds).stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));

        // 5. 组装 VO 列表
        List<DishVO> voList = records.stream()
                .map(dish -> DishVO.builder()
                        .id(dish.getId())
                        .name(dish.getName())
                        .categoryId(dish.getCategoryId())
                        .categoryName(categoryNameMap.get(dish.getCategoryId()))
                        .price(dish.getPrice())
                        .image(dish.getImage())
                        .description(dish.getDescription())
                        .status(dish.getStatus())
                        .updateTime(dish.getUpdateTime())
                        .build())
                .collect(Collectors.toList());

        return new PageResult(dishPage.getTotal(), voList);
    }

    /**
     * 批量删除菜品及其关联数据
     * <p>
     * 前置校验：<br>
     * 1. 若传入 ID 列表为空，直接返回；<br>
     * 2. 若存在“起售中”菜品，则抛出 {@link DeletionNotAllowedException}；<br>
     * 3. 若菜品被套餐引用，同样抛出异常；<br>
     * 删除顺序：先删口味子表，再删主表，保证外键完整性。
     * </p>
     *
     * @param ids 待删除的菜品 ID 列表，允许为空
     * @throws DeletionNotAllowedException 当菜品处于起售中或被套餐引用时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 1. 查询待删除菜品
        List<Dish> dishList = dishMapper.selectByIds(ids);

        // 2. 校验状态
        boolean hasOnSale = dishList.stream()
                .anyMatch(d -> StatusConstant.ENABLE.equals(d.getStatus()));
        if (hasOnSale) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        // 3. 校验是否被套餐引用
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (CollectionUtils.isNotEmpty(setmealIds)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 4. 级联删除
        dishFlavorMapper.deleteByDishIds(ids);
        dishMapper.deleteByIds(ids);

        log.info("批量删除菜品成功，IDs：{}", ids);
    }

    /**
     * 启用或禁用菜品
     * <p>
     * 1. 更新菜品状态；<br>
     * 2. 若操作为“禁用”，则级联禁用所有包含该菜品的套餐，保证业务一致性。
     * </p>
     *
     * @param status 目标状态，{@link StatusConstant#ENABLE} 或 {@link StatusConstant#DISABLE}
     * @param id     菜品 ID，必须已存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Integer status, Long id) {
        // 1. 更新菜品状态（依赖 MyBatis-Plus 自动填充 updateTime/updateUser）
        Dish toUpdate = new Dish();
        toUpdate.setId(id);
        toUpdate.setStatus(status);
        dishMapper.updateById(toUpdate);

        // 2. 级联禁用套餐
        if (StatusConstant.DISABLE.equals(status)) {
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(List.of(id));
            if (CollectionUtils.isNotEmpty(setmealIds)) {
                Setmeal setmeal = new Setmeal();
                setmeal.setStatus(StatusConstant.DISABLE);
                setmealMapper.update(setmeal, Wrappers.<Setmeal>lambdaUpdate()
                        .in(Setmeal::getId, setmealIds));
            }
        }

        log.info("菜品状态更新成功，ID：{}，状态：{}", id, status);
    }

    /**
     * 根据 ID 查询菜品及其口味信息
     * <p>
     * 若菜品不存在则抛出 {@link DeletionNotAllowedException}。
     * </p>
     *
     * @param id 菜品 ID
     * @return 包含菜品基本信息与口味列表的 VO 对象
     * @throws DeletionNotAllowedException 当菜品不存在时抛出
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 1. 查询菜品
        Dish dish = dishMapper.selectById(id);
        if (Objects.isNull(dish)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_NOT_FOUND);
        }

        // 2. 查询口味
        List<DishFlavor> flavors = dishFlavorMapper.selectList(
                Wrappers.<DishFlavor>lambdaQuery().eq(DishFlavor::getDishId, id));

        // 3. 组装 VO
        return DishVO.builder()
                .id(dish.getId())
                .categoryId(dish.getCategoryId())
                .name(dish.getName())
                .price(dish.getPrice())
                .image(dish.getImage())
                .description(dish.getDescription())
                .status(dish.getStatus())
                .flavors(flavors)
                .build();
    }

    /**
     * 根据 ID 修改菜品及其口味信息
     * <p>
     * 事务粒度：整个方法为一个事务；<br>
     * 执行步骤：先更新菜品主表，再删除原口味，最后批量插入新口味。
     * </p>
     *
     * @param dishDTO 包含菜品基本信息与口味列表的数据传输对象，ID 不能为空
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(DishDTO dishDTO) {
        // 1. 更新菜品主表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dishDTO.getId());
        dishMapper.update(dish, updateWrapper);

        // 2. 删除原有口味
        dishFlavorMapper.deleteByDishIds(List.of(dishDTO.getId()));

        // 3. 批量插入新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (CollectionUtils.isNotEmpty(flavors)) {
            flavors.forEach(f -> f.setDishId(dishDTO.getId()));
            dishFlavorMapper.batchInsert(flavors);
        }

        log.info("更新菜品成功，ID：{}", dishDTO.getId());
    }

    /**
     * 根据分类 ID 查询“启用”状态的菜品列表
     * <p>
     * 常用于移动端展示某一分类下的可点菜品。
     * </p>
     *
     * @param categoryId 分类 ID
     * @return 该分类下所有启用的菜品列表，不会返回 {@code null}
     */
    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.selectList(
                Wrappers.<Dish>lambdaQuery()
                        .eq(Dish::getCategoryId, categoryId)
                        .eq(Dish::getStatus, StatusConstant.ENABLE));
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = Wrappers.<Dish>lambdaQuery()
                .eq(Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, StatusConstant.ENABLE);
        List<Dish> dishList = dishMapper.selectList(queryWrapper);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            // 使用条件构造器根据菜品id查询对应的口味
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = Wrappers.<DishFlavor>lambdaQuery()
                    .eq(DishFlavor::getDishId, d.getId());
            List<DishFlavor> flavors = dishFlavorMapper.selectList(flavorQueryWrapper);

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
