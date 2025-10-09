package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * JWT令牌后台管理端拦截器，用于验证请求中的JWT令牌
     */
    @Autowired
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
     * 设置静态资源映射
     * 当前项目使用springdoc-openapi，无需额外配置静态资源路径
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // springdoc 提供 /swagger-ui/index.html，无需额外静态资源映射
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
