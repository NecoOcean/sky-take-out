package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 提供 JWT 的生成与解析功能，采用 HS256 算法进行签名，确保令牌安全性。
 * 密钥需妥善保管，切勿泄露。
 */
public class JwtUtil {

    /**
     * 生成 JWT 令牌
     * 使用 HS256 算法进行签名，密钥为固定字符串
     *
     * @param secretKey 用于签名的密钥，需保证长度足够且安全
     * @param ttlMillis 令牌有效期（单位：毫秒）
     * @param claims    需要封装到 JWT 中的自定义声明信息
     * @return 生成的 JWT 字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secretKey must not be null or empty");
        }
        if (secretKey.length() < 32) { // HS256建议≥256bit（32字符）
            throw new IllegalArgumentException("JWT secretKey length must be >= 32 characters");
        }
        // 指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 计算过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 构建并返回 JWT
        return Jwts.builder()
                .setClaims(claims)          // 设置自定义声明
                .setExpiration(exp)         // 设置过期时间
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), signatureAlgorithm) // 设置签名密钥与算法
                .compact();                 // 生成紧凑的 JWT 字符串
    }

    /**
     * 解析 JWT 令牌，提取其中的声明信息
     * 若令牌无效、过期或签名错误，将抛出异常
     *
     * @param secretKey 用于验证签名的密钥，必须与生成时使用的一致
     * @param token     待解析的 JWT 字符串
     * @return 解析后的 Claims 对象，包含令牌中的声明信息
     */
    public static Claims parseJWT(String secretKey, String token) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secretKey must not be null or empty");
        }
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT secretKey length must be >= 32 characters");
        }
        // 使用密钥构建解析器
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // 设置验证签名的密钥
                .build()
                .parseClaimsJws(token)    // 解析并验证令牌
                .getBody();               // 获取载荷部分
    }
}
