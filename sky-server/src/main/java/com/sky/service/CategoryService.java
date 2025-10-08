package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * 分类业务接口
 * 提供菜品及套餐分类的增删改查、启禁用等核心业务能力
 */
public interface CategoryService {

    /**
     * 新增分类
     * 用于后台管理系统新增菜品或套餐分类
     *
     * @param categoryDTO 分类信息传输对象，包含名称、类型、排序等字段
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询分类列表
     * 支持按名称模糊查询、按类型筛选及排序
     *
     * @param categoryPageQueryDTO 分页查询条件，包含页码、每页条数、名称关键字、类型等
     * @return 分页结果对象，封装总记录数及当前页数据列表
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据主键删除分类
     * 删除前需校验该分类下是否存在关联的菜品或套餐，若存在则不允许删除
     *
     * @param id 分类主键
     */
    void deleteById(Long id);

    /**
     * 修改分类信息
     * 支持修改分类名称、排序、类型等字段
     *
     * @param categoryDTO 分类信息传输对象，必须包含主键id
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用或禁用分类
     * 状态变更后，前端展示将同步更新
     *
     * @param status 目标状态：1-启用，0-禁用
     * @param id     分类主键
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类列表
     * 用于前端用户端按菜品/套餐类型筛选
     *
     * @param type 分类类型：1-菜品分类，2-套餐分类
     * @return 符合条件的分类列表，按排序号升序排列
     */
    List<Category> list(Integer type);

}
