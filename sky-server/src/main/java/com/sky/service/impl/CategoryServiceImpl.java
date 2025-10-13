package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类业务接口实现类
 * 负责处理菜品分类和套餐分类的增删改查及状态管理。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    
    /**
     * 分类映射器，用于数据库操作
     */
    @Resource
    CategoryMapper categoryMapper;

    /**
     * 菜品映射器，用于数据库操作
     */
    @Resource
    DishMapper dishMapper;

    /**
     * 套餐映射器，用于数据库操作
     */
    @Resource
    SetmealMapper setmealMapper;

    /**
     * 新增分类
     *
     * @param categoryDTO 分类数据传输对象，包含分类名称、类型、排序等信息
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        // 将DTO转换为实体对象
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        // 执行数据库插入
        categoryMapper.insert(category);
    }

    /**
     * 分页查询分类列表
     *
     * @param categoryPageQueryDTO 分页查询条件，支持按类型、名称模糊查询
     * @return 分页结果，包含总记录数和当前页数据
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 构造分页对象
        Page<Category> page = new Page<>(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        // 构造查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryPageQueryDTO.getType() != null, Category::getType, categoryPageQueryDTO.getType())
                .like(categoryPageQueryDTO.getName() != null, Category::getName, categoryPageQueryDTO.getName())
                // 排序：按sort升序，更新时间降序作为次序
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        // 执行分页查询
        Page<Category> resultPage = categoryMapper.selectPage(page, queryWrapper);
        // 封装返回结果
        return new PageResult(resultPage.getTotal(), resultPage.getRecords());
    }

    /**
     * 根据ID删除分类
     * 删除前需确保该分类未被菜品或套餐引用
     *
     * @param id 分类主键
     * @throws DeletionNotAllowedException 当分类已被菜品或套餐引用时抛出
     */
    @Override
    public void deleteById(Long id) {
        // 检查分类是否关联了菜品
        Long dishCount = dishMapper.selectCount(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));
        if (dishCount > 0) {
            // 关联了菜品，抛出业务异常
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        // 检查分类是否关联了套餐
        Long setmealCount = setmealMapper.selectCount(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id));
        if (setmealCount > 0) {
            // 关联了套餐，抛出业务异常
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 未关联菜品或套餐，执行删除操作
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类信息
     *
     * @param categoryDTO 分类数据传输对象，包含需要更新的字段
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        // 将DTO转换为实体对象
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        // 根据主键执行更新
        categoryMapper.updateById(category);
    }

    /**
     * 启用或禁用分类
     *
     * @param status 目标状态：1启用 0禁用
     * @param id     分类主键
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 查询原分类
        Category category = categoryMapper.selectById(id);
        // 设置新状态
        category.setStatus(status);
        // 更新到数据库
        categoryMapper.updateById(category);
    }

    /**
     * 根据类型查询分类列表
     *
     * @param type 分类类型：1菜品分类 2套餐分类
     * @return 分类列表，按sort升序、更新时间降序排列
     */
    @Override
    public List<Category> list(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType, type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        return categoryMapper.selectList(queryWrapper);
    }
}
