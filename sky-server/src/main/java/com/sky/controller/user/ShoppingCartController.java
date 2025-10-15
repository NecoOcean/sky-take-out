package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端购物车控制器
 * 提供购物车相关接口，包括添加、查看、清空及删除商品等操作
 *
 * @author  NecoOcean
 * @date    2025-10-15
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name = "C端购物车相关接口")
public class ShoppingCartController {

    /**
     * 购物车业务逻辑服务
     */
    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加商品到购物车
     *
     * @param shoppingCartDTO 购物车商品信息传输对象，包含菜品/套餐ID、口味、数量等
     * @return 统一响应结果，成功时无附加数据
     */
    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车，商品信息为：{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查询当前用户购物车列表
     *
     * @return 统一响应结果，包含购物车商品列表数据
     */
    @GetMapping("/list")
    @Operation(summary = "查看购物车")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.showShoppingCart();
        return Result.success(list);
    }

    /**
     * 清空当前用户购物车
     *
     * @return 统一响应结果，成功时无附加数据
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清空购物车")
    public Result<String> clean(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 从购物车中减少或删除指定商品
     * 当商品数量为1时执行删除，否则数量减1
     *
     * @param shoppingCartDTO 购物车商品信息传输对象，需包含菜品/套餐ID、口味等
     * @return 统一响应结果，成功时无附加数据
     */
    @PostMapping("/sub")
    @Operation(summary = "删除购物车中一个商品")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中一个商品，商品：{}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
