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
 * 
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class ShopController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/status")
    @Operation(summary = "获取店铺的营业状态")
    public Result<Integer> getStatus() {
        int status = Integer.parseInt(stringRedisTemplate.opsForValue().get("SHOP_STATUS"));
        log.info("获取店铺状态为：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

}
