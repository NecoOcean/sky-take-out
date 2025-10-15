package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理控制器
 * 提供菜品新增、分页查询、批量删除、根据ID查询、修改、起售/停售、根据分类ID查询等功能。
 * 主要负责处理前端发送的菜品相关请求，调用服务层进行业务逻辑处理，并返回结果。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Tag(name = "菜品相关接口", description = "提供菜品新增、分页查询、批量删除、根据ID查询、修改、起售/停售、根据分类ID查询等功能")
public class DishController {

    @Resource
    private DishService dishService;

    /**
     * 新增菜品（含口味信息）
     *
     * @param dishDTO 菜品及口味数据传输对象
     * @return 成功响应
     */
    @Operation(summary = "新增菜品", description = "管理员端新增菜品，支持同时录入口味信息，保存后菜品默认状态为停售")
    @PostMapping
    @Cacheable(cacheNames = "dish", key = "#dishDTO.id")
    public Result<String> saveWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 分页查询条件（页码、每页条数、分类ID、菜品名称、状态等）
     * @return 分页结果封装对象
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询", description = "支持根据分类、名称、状态等条件分页查询菜品列表，返回包含口味信息的分页数据")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * 支持一次删除多个菜品，删除前需校验菜品是否关联有效订单
     *
     * @param ids 待删除菜品ID列表
     * @return 成功响应
     */
    @DeleteMapping
    @Operation(summary = "菜品批量删除", description = "根据菜品ID列表批量删除菜品，若菜品存在关联订单则无法删除")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据菜品id查询菜品（含口味信息）
     *
     * @param id 菜品ID
     * @return 菜品及口味视图对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品", description = "根据菜品ID查询菜品基本信息及对应口味列表")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品（含口味信息）
     *
     * @param dishDTO 菜品及口味数据传输对象
     * @return 成功响应
     */
    @PutMapping
    @Operation(summary = "修改菜品", description = "管理员端修改菜品基本信息及口味信息，修改后需重新审核")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售/停售
     * 状态切换：1-起售，0-停售
     *
     * @param status 目标状态
     * @param id     菜品ID
     * @return 成功响应
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "菜品起售停售", description = "切换菜品销售状态，起售后用户端可见，停售后用户端隐藏")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品列表
     * 用于后台根据分类快速筛选菜品
     *
     * @param categoryId 分类ID
     * @return 菜品实体列表
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品", description = "根据分类ID查询该分类下所有菜品，用于后台快速筛选")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

}
