package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺相关接口
 * 用户端接口，用于获取店铺的营业状态。
 * 主要负责处理前端发送的店铺相关请求，调用服务层进行业务逻辑处理，并返回结果。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class ShopController {

    /**
     * Redis 模板，用于操作 Redis 中的字符串数据
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取店铺营业状态
     * 从 Redis 中读取键为 "SHOP_STATUS" 的值，转换为整数后返回
     * 若 Redis 中无该键，则默认返回 0（打烊状态）
     *
     * @return Result<Integer> 统一响应结果，data 为 1 表示营业中，0 表示打烊中
     */
    @GetMapping("/status")
    @Operation(summary = "获取店铺的营业状态")
    public Result<Integer> getStatus() {
        String value = stringRedisTemplate.opsForValue().get("SHOP_STATUS");
        int status;
        try {
            status = value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Redis 中 SHOP_STATUS 值格式异常，默认为打烊状态", e);
            status = 0;
        }
        log.info("获取店铺状态为：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

}
