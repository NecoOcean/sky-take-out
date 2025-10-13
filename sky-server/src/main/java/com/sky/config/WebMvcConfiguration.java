package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件。
 * 主要负责配置Spring MVC的拦截器、消息转换器等组件，
 * 以及自定义的ObjectMapper用于JSON序列化与反序列化。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * JWT令牌后台管理端拦截器，用于验证请求中的JWT令牌
     */
    @Resource
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     * 拦截所有/admin/**路径的请求，排除登录及Swagger相关路径
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/employee/login",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
    }

    /**
     * 拓展SpringMVC的消息转换器
     * 使用自定义的JacksonObjectMapper作为全局主ObjectMapper，
     * 统一控制JSON序列化与反序列化行为（如日期格式、空值处理等）
     *
     * @return 自定义的ObjectMapper实例
     */
    @Bean
    @Primary
    public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        // 提供全局 @Primary ObjectMapper，默认的 MappingJackson2HttpMessageConverter 将会使用它
        return new JacksonObjectMapper();
    }
}
