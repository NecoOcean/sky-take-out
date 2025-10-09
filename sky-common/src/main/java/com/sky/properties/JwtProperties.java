package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 用于统一管理管理端与用户端 JWT 令牌的密钥、有效期及令牌名称
 *
 * @author sky
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工 JWT 密钥
     * 用于签名与验证管理端员工令牌
     */
    private String adminSecretKey;

    /**
     * 管理端员工 JWT 有效期（毫秒）
     * 令牌签发后在此时间内有效
     */
    private long adminTtl;

    /**
     * 管理端员工 JWT 在请求头中的名称
     * 前后端交互时携带该字段传递令牌
     */
    private String adminTokenName;

    /**
     * 用户端微信用户 JWT 密钥
     * 用于签名与验证用户端微信用户令牌
     */
    private String userSecretKey;

    /**
     * 用户端微信用户 JWT 有效期（毫秒）
     * 令牌签发后在此时间内有效
     */
    private long userTtl;

    /**
     * 用户端微信用户 JWT 在请求头中的名称
     * 前后端交互时携带该字段传递令牌
     */
    private String userTokenName;

}
