package com.sky.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skyOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.1")
                .info(new Info()
                        .title("Sky 外卖 API")
                        .description("管理端与用户端接口文档")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("tokenHeader",
                        new SecurityScheme()
                                .name("token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)))
                .addSecurityItem(new SecurityRequirement().addList("tokenHeader"));
    }
}