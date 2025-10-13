package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 配置 RedisTemplate 以支持 String 键和 Object 值的序列化。
 * 该类负责定义 RedisTemplate Bean，用于在 Spring 应用中进行 Redis 操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    /**
     * 创建 RedisTemplate 实例
     * 配置 StringRedisSerializer 作为键序列化器，使用默认的 JDK 序列化器作为值序列化器。
     * 注意：当前配置中未显式设置值序列化器，默认使用 JDK 序列化器。
     *
     * @param redisConnectionFactory Redis 连接工厂，由 Spring Boot 自动配置提供
     * @return 配置好的 RedisTemplate 实例，键为 String 类型，值为 Object 类型
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建 RedisTemplate 模板对象...");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置键的序列化方式为 StringRedisSerializer，确保键可读性好
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 可选：设置哈希键的序列化方式（如需使用 Hash 操作）
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        log.info("RedisTemplate 模板对象创建完成");
        return redisTemplate;
    }

}
