package com.sky.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 * <p>
 * 用于配置 Swagger（OpenAPI 3.0）文档的基本信息、安全方案等，
 * 方便前后端开发人员在线查看和调试接口。
 * </p>
 *
 * @author sky
 * @date 2024/06/01
 */
@Configuration
public class OpenApiConfig {

    /**
     * 创建并配置 OpenAPI 实例
     *
     * @return 自定义的 OpenAPI 对象，包含文档标题、描述、版本以及全局鉴权方式
     */
    @Bean
    public OpenAPI skyOpenAPI() {
        return new OpenAPI()
                // 指定 OpenAPI 规范版本
                .openapi("3.0.1")
                // 配置 API 文档基本信息
                .info(new Info()
                        .title("Sky 外卖 API")
                        .description("管理端与用户端接口文档")
                        .version("1.0.0"))
                // 定义全局安全方案：通过请求头传递 token 进行鉴权
                .components(new Components()
                        .addSecuritySchemes("tokenHeader",
                                new SecurityScheme()
                                        .name("token")                       // 请求头名称
                                        .type(SecurityScheme.Type.APIKEY)    // 类型为 APIKey
                                        .in(SecurityScheme.In.HEADER)))      // 位于请求头
                // 对所有接口应用上述安全方案
                .addSecurityItem(new SecurityRequirement().addList("tokenHeader"));
    }
}