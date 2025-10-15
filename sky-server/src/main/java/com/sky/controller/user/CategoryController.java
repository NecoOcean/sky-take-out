package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端-分类控制器
 * 提供用户端分类相关接口
 * @author NecoOcean
 * @date 2025-10-15
 */
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Tag(name = "C端-分类接口")
public class CategoryController {

    /**
     * 分类服务接口
     */
    @Resource
    private CategoryService categoryService;

    /**
     * 根据类型查询分类列表
     * 
     * @param type 分类类型（1-菜品分类，2-套餐分类，null-全部）
     * @return 分类列表结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询分类")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}