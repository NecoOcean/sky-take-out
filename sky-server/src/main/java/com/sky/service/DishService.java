package com.sky.service;

import java.util.List;

import com.sky.entity.Dish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.vo.DishVO;
import org.springframework.transaction.annotation.Transactional;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

/**
 * 菜品业务接口
 * <p>
 * 提供菜品的增删改查、状态变更、口味维护等核心业务能力。
 * </p>
 *
 * @author NecoOcean
 * @date 2025/10/10
 */
public interface DishService {

    /**
     * 保存菜品及其口味信息
     * <p>
     * 该方法首先将 DishDTO 中的基础菜品信息保存到数据库，<br>
     * 然后将对应的口味列表逐一与新生成的菜品 ID 关联并持久化。
     * </p>
     * <p>
     * 事务说明：当发生任何异常时，事务将回滚，确保数据一致性。
     * </p>
     *
     * @param dishDTO 封装了菜品基础信息与口味列表的数据传输对象
     */
    @Transactional(rollbackFor = Exception.class)
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * <p>
     * 1. 根据分页查询参数构建分页对象；<br>
     * 2. 执行分页查询，获取符合条件的菜品列表；<br>
     * 3. 构建分页结果对象，包含总记录数与当前页数据；<br>
     * 4. 返回分页结果。
     * </p>
     *
     * @param dishPageQueryDTO 封装了分页查询参数的数据传输对象
     * @return 分页结果对象，包含菜品列表与总记录数
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品及其关联数据
     * <p>
     * 1. 前置校验：若待删除 ID 列表为空，则直接返回，不做任何处理；<br>
     * 2. 业务校验：
     *    a) 判断待删除菜品中是否存在“起售中”状态，若存在则抛出 {@link DeletionNotAllowedException}；<br>
     *    b) 判断待删除菜品是否已被任何套餐引用，若存在引用则同样抛出 {@link DeletionNotAllowedException}；<br>
     * 3. 级联删除：
     *    a) 先删除菜品口味子表数据，避免外键约束冲突；<br>
     *    b) 再删除菜品主表数据；<br>
     * 4. 全程采用批量操作，减少数据库交互次数，提升性能。
     * </p>
     *
     * @param ids 待删除的菜品 ID 列表
     * @throws DeletionNotAllowedException 当菜品处于起售中或被套餐关联时抛出
     */
    void deleteBatch(List<Long> ids);

    /**
     * 启用或禁用菜品
     * <p>
     * 1. 根据传入的 status 和 id 更新菜品状态；<br>
     * 2. 若操作为“禁用”，则需将包含该菜品的所有套餐一并禁用，保证数据一致性。
     * </p>
     *
     * @param status 目标状态（启用/禁用）
     * @param id     菜品 ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据 ID 查询菜品及其关联口味信息
     * <p>
     * 1. 查询指定 ID 的菜品基本信息；<br>
     * 2. 若菜品不存在，则抛出 {@link DeletionNotAllowedException}；<br>
     * 3. 查询该菜品关联的所有口味信息；<br>
     * 4. 封装为 {@link DishVO} 并返回。
     * </p>
     *
     * @param id 菜品 ID
     * @return 包含菜品基本信息与关联口味列表的视图对象
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 更新菜品及其关联口味信息
     * <p>
     * 1. 根据传入的 {@link DishDTO} 更新菜品主表信息；<br>
     * 2. 删除原有的口味数据；<br>
     * 3. 若口味列表非空，批量插入新的口味数据。
     * </p>
     *
     * @param dishDTO 包含更新后的菜品信息与口味列表的数据传输对象
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类 ID 查询菜品列表
     * <p>
     * 1. 根据传入的 categoryId 查询该分类下所有状态为“启用”的菜品；<br>
     * 2. 返回符合条件的菜品列表。
     * </p>
     *
     * @param categoryId 分类 ID
     * @return 该分类下所有启用状态的菜品列表
     */
    List<Dish> list(Long categoryId);
}
