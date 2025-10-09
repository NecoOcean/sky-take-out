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

import java.util.List;

import org.springframework.web.bind.annotation.*;

/**
 * 菜品管理控制器
 * 提供菜品新增、分页查询、批量删除、根据ID查询、修改、起售/停售、根据分类ID查询等功能
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Tag(name = "菜品相关接口")
public class DishController {

    @Resource
    private DishService dishService;

    /**
     * 新增菜品（含口味信息）
     *
     * @param dishDTO 菜品及口味数据传输对象
     * @return 成功响应
     */
    @Operation(summary = "新增菜品")
    @PostMapping
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
    @Operation(summary = "菜品分页查询")
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
    @Operation(summary = "菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        // cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据菜品id查询菜品（含口味信息）
     *
     * @param id 菜品ID
     * @return 菜品及口味视图对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
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
    @Operation(summary = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");

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
    @Operation(summary = "菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");

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
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

}
