package com.sky.controller.user;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
     * Redis模板，用于缓存操作
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Jackson JSON序列化/反序列化工具
     */
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 根据分类ID查询启售中的菜品列表（含口味信息）
     * 优先读取Redis缓存，缓存未命中则查询数据库并写入缓存
     *
     * @param categoryId 菜品分类ID，必填
     * @return Result<List<DishVO>> 封装菜品视图对象列表的响应结果
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        // 参数校验：分类ID不能为空
        if (categoryId == null) {
            return Result.error("分类ID不能为空");
        }

        // 构造Redis缓存key，格式：dish_{categoryId}
        String key = "dish_" + categoryId;
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        // 步骤1：尝试从Redis获取缓存数据
        Object cachedValue = valueOps.get(key);
        if (cachedValue != null) {
            // 使用Jackson进行安全的类型转换，避免ClassCastException
            try {
                // 构造List<DishVO>的JavaType，确保反序列化类型安全
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, DishVO.class);
                List<DishVO> list = objectMapper.readValue(objectMapper.writeValueAsString(cachedValue), javaType);
                // 缓存命中且数据非空，直接返回
                if (CollectionUtils.isNotEmpty(list)) {
                    log.info("从Redis缓存获取分类[{}]菜品数据成功，共{}条", categoryId, list.size());
                    return Result.success(list);
                }
            } catch (JsonProcessingException e) {
                // 类型转换失败记录错误日志，继续走数据库查询逻辑，保证接口可用性
                log.error("Redis缓存数据转换失败，key：{}，异常信息：", key, e);
            }
        }

        // 步骤2：缓存未命中，查询数据库
        log.info("Redis缓存未命中，查询数据库获取分类[{}]菜品数据", categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        // 仅查询启售状态的菜品
        dish.setStatus(StatusConstant.ENABLE);
        List<DishVO> list = dishService.listWithFlavor(dish);

        // 步骤3：将查询结果写入Redis缓存，设置不同过期策略
        if (CollectionUtils.isNotEmpty(list)) {
            // 数据非空：缓存2小时，提升后续查询性能
            valueOps.set(key, list, 2, TimeUnit.HOURS);
            log.info("分类[{}]菜品数据写入Redis缓存成功，共{}条，缓存2小时", categoryId, list.size());
        } else {
            // 数据为空：缓存10分钟，防止缓存穿透
            valueOps.set(key, Collections.emptyList(), 10, TimeUnit.MINUTES);
            log.info("分类[{}]无启售菜品，写入空列表到Redis缓存，缓存10分钟", categoryId);
        }

        // 返回数据库查询结果
        return Result.success(list);
    }
}