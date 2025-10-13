package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理控制器
 * 提供套餐的增删改查、起售停售等后台管理接口
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Tag(name = "套餐管理接口", description = "提供后台套餐的增删改查、起售停售等管理功能")
public class SetmealController {

    /**
     * 套餐服务接口，用于处理套餐相关操作
     */
    @Resource
    private SetmealService setmealService;

    /**
     * 新增套餐
     * 接收前端传入的套餐信息及关联菜品列表，调用服务层保存数据
     *
     * @param setmealDTO 套餐数据传输对象，包含套餐信息及关联菜品列表
     * @return Result 统一响应结果，包含操作状态与消息
     */
    @PostMapping
    @Operation(summary = "新增套餐", description = "新增套餐并同时保存套餐关联的菜品列表")
    @Parameter(name = "setmealDTO", description = "套餐数据传输对象，必填", required = true)
    public Result<String> add(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐:{}", setmealDTO);
        // 保存套餐及其关联菜品
        setmealService.savaWithDIsh(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询套餐及其包含的菜品信息
     * 根据前端传入的分页及查询条件，返回分页后的套餐列表
     *
     * @param setmealPageQueryDTO 套餐分页查询数据传输对象，包含查询条件（如套餐名称、分类、状态等）
     * @return Result<PageResult> 统一响应结果，包含分页查询结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询套餐", description = "根据条件分页查询套餐列表及其包含的菜品信息")
    @Parameter(name = "setmealPageQueryDTO", description = "套餐分页查询条件对象，非必填")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 套餐起售、停售
     * 根据路径参数status与请求参数id，修改对应套餐的销售状态
     *
     * @param status 套餐状态，1表示起售，0表示停售
     * @param id     套餐主键id
     * @return Result 统一响应结果
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "套餐起售/停售", description = "根据套餐id修改套餐销售状态，1为起售，0为停售")
    @Parameters({
            @Parameter(name = "status", description = "套餐销售状态，1为起售，0为停售", required = true, example = "1"),
            @Parameter(name = "id", description = "套餐主键id", required = true, example = "123")
    })
    public Result<String> status(@PathVariable Integer status, @RequestParam Long id) {
        log.info("套餐起售、停售: 状态:{}，id:{}", status, id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询套餐详情（含关联菜品）
     * 返回套餐基本信息及所包含的菜品列表
     *
     * @param id 套餐主键id
     * @return Result<SetmealVO> 统一响应结果，包含套餐详情视图对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询套餐详情", description = "根据套餐id查询套餐详细信息，包含关联菜品列表")
    @Parameter(name = "id", description = "套餐主键id", required = true, example = "123")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id) {
        log.info("根据套餐Id查询, id = :{}", id);
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐信息
     * 根据传入的套餐DTO更新套餐基本信息及重新绑定菜品列表
     *
     * @param setmealDTO 套餐数据传输对象，包含待更新的套餐信息及关联菜品列表
     * @return Result 统一响应结果
     */
    @PutMapping
    @Operation(summary = "修改套餐信息", description = "根据套餐id更新套餐基本信息及关联菜品列表")
    @Parameter(name = "setmealDTO", description = "套餐数据传输对象，必填", required = true)
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐:{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * 根据传入的id列表，删除对应套餐及其关联菜品
     *
     * @param ids 待删除的套餐id列表
     * @return Result 统一响应结果
     */
    @DeleteMapping
    @Operation(summary = "批量删除套餐", description = "根据套餐id列表批量删除套餐及其关联菜品")
    @Parameter(name = "ids", description = "待删除的套餐id列表，必填", required = true, example = "[1,2,3]")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除，ids:{}", ids);
        setmealService.deleteByIds(ids);
        return Result.success();
    }

}
