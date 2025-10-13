package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理控制器
 * 提供分类的增删改查、启用禁用及按类型查询等功能。
 * 主要负责处理前端发送的分类相关请求，调用服务层进行业务逻辑处理，并返回结果。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Tag(name = "分类相关接口", description = "提供分类的增删改查、启用禁用及按类型查询等功能")
@Slf4j
public class CategoryController {

    /**
     * 分类业务逻辑服务
     */
    @Resource
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO 新增分类的数据传输对象，包含分类名称、类型、排序等信息
     * @return 返回操作成功结果，无数据载体
     */
    @PostMapping
    @Operation(summary = "新增分类",
            description = "根据传入的DTO对象新增一条分类记录，要求名称唯一，排序号非负")
    public Result<String> save(@Parameter(description = "分类信息DTO", required = true)
                               @RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO 分类分页查询条件，包含页码、每页条数、名称模糊查询、类型筛选等
     * @return 返回分页结果对象，内含当前页数据列表及总记录数
     */
    @GetMapping("/page")
    @Operation(summary = "分类分页查询",
            description = "支持按名称模糊、类型、状态等条件分页查询分类列表，默认按排序号升序")
    public Result<PageResult> page(@Parameter(description = "分页查询参数", required = false)
                                   CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     *
     * @param id 待删除分类的主键ID
     * @return 返回操作成功结果，无数据载体
     */
    @DeleteMapping
    @Operation(summary = "删除分类",
            description = "根据主键ID删除分类，若分类已被菜品或套餐引用则不允许删除")
    public Result<String> deleteById(@Parameter(description = "分类主键ID", required = true)
                                     @RequestParam Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO 修改分类的数据传输对象，包含主键ID及待更新字段
     * @return 返回操作成功结果，无数据载体
     */
    @PutMapping
    @Operation(summary = "修改分类",
            description = "根据主键ID更新分类信息，允许修改名称、排序、类型、状态等字段")
    public Result<String> update(@Parameter(description = "分类更新信息DTO", required = true)
                                 @RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用、禁用分类
     *
     * @param status 目标状态，1为启用，0为禁用
     * @param id     待操作分类的主键ID
     * @return 返回操作成功结果，无数据载体
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用分类",
            description = "批量或单条切换分类状态，状态值只能为0或1")
    public Result<String> startOrStop(@Parameter(description = "目标状态，1启用 0禁用", required = true)
                                      @PathVariable("status") Integer status,
                                      @Parameter(description = "分类主键ID", required = true)
                                      @RequestParam Long id) {
        log.info("启用禁用分类：status={}, id={}", status, id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * 只查询启用的分类
     *
     * @param type 分类类型，1为菜品分类，2为套餐分类；为空时查询全部
     * @return 返回分类列表，按排序号升序排列
     */
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类",
            description = "查询启用的分类列表，可按类型筛选，结果按sort升序排列，用于下拉框或列表展示")
    public Result<List<Category>> list(@Parameter(description = "分类类型，1菜品 2套餐，留空查全部", required = false)
                                       @RequestParam(required = false) Integer type) {
        log.info("根据类型查询分类：type={}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
