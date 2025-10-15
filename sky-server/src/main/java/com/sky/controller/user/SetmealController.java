package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端套餐浏览控制器
 * 提供套餐列表及套餐内菜品查询接口
 * @author NecoOcean
 * @date 2025-10-15
 */
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Tag(name = "C端-套餐浏览接口")
public class SetmealController {

    /**
     * 套餐业务逻辑服务对象
     */
    @Resource
    private SetmealService setmealService;

    /**
     * 根据分类ID查询启用的套餐列表
     * 支持Spring Cache缓存，缓存名称为setmealCache，缓存键为分类ID
     *
     * @param categoryId 分类ID，用于筛选对应分类下的套餐
     * @return 返回封装后的套餐列表数据，状态为启用（ENABLE）
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId") // 缓存键示例：setmealCache::100
    public Result<List<Setmeal>> list(Long categoryId) {
        // 创建查询条件对象
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        // 仅查询启用的套餐
        setmeal.setStatus(StatusConstant.ENABLE);

        // 调用服务层查询套餐列表
        List<Setmeal> list = setmealService.list(setmeal);
        return Result.success(list);
    }

    /**
     * 根据套餐ID查询该套餐包含的所有菜品列表
     *
     * @param id 套餐ID，路径参数
     * @return 返回封装后的菜品列表数据，包含菜品名称、口味、份数等信息
     */
    @GetMapping("/dish/{id}")
    @Operation(summary = "根据套餐id查询包含的菜品列表")
    @Cacheable(cacheNames = "setmealCache",key = "#id")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        // 调用服务层根据套餐ID查询菜品列表
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);
    }
}