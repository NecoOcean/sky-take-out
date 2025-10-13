package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 店铺相关接口
 * 管理员端接口，用于设置与获取店铺的营业状态。
 * 主要负责处理前端发送的店铺相关请求，调用服务层进行业务逻辑处理，并返回结果。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class ShopController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PutMapping("/{status}")
    @Operation(summary = "设置店铺的营业状态")
    public Result<String> setStatus(@PathVariable Integer status) {
        if (!Objects.equals(status, 0) && !Objects.equals(status, 1)) {
            throw new IllegalArgumentException("店铺状态仅支持0或1");
        }
        stringRedisTemplate.opsForValue().set("SHOP_STATUS", String.valueOf(status));
        log.info("已设置店铺状态为: {}", status);
        return Result.success();
    }

    @GetMapping("/status")
    @Operation(summary = "获取店铺的营业状态")
    public Result<Integer> getStatus() {
        int status = Integer.parseInt(stringRedisTemplate.opsForValue().get("SHOP_STATUS"));
        log.info("获取店铺状态为：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }


}
