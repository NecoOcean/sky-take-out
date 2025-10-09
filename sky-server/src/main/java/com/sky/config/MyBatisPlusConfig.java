package com.sky.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 用于配置 MyBatis-Plus 插件，如分页插件等
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 分页插件
     * 使用 MySQL 方言进行分页查询
     *
     * @return MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建 MyBatis-Plus 拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 创建分页插件并设置 MySQL 方言
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor();
        pagination.setDialect(new MySqlDialect());
        // 将分页插件添加到拦截器中
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}