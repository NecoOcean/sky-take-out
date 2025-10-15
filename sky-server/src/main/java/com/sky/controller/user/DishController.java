package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端用户浏览菜品相关接口
 * 提供菜品列表查询功能，支持Redis缓存提升性能
 *
 * @author NecoOcean
 * @date 2025-10-15
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name = "C端-菜品浏览接口")
public class DishController {

    /**
     * 菜品业务逻辑服务
     */
    @Resource
    private DishService dishService;

    /**
     * 根据分类ID查询启售中的菜品列表（含口味信息）
     * 优先读取Redis缓存，缓存未命中则查询数据库并写入缓存
     *
     * @param categoryId 菜品分类ID，必填
     * @return Result<List<DishVO>> 封装菜品视图对象列表的响应结果
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    @Cacheable(cacheNames = "dish", key = "#categoryId")
    public Result<List<DishVO>> list(Long categoryId) {
        // 参数校验：分类ID不能为空
        if (categoryId == null) {
            return Result.error("分类ID不能为空");
        }
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        // 仅查询启售状态的菜品
        dish.setStatus(StatusConstant.ENABLE);
        List<DishVO> list = dishService.listWithFlavor(dish);
        // 返回数据库查询结果
        return Result.success(list);
    }
}