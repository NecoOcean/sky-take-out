package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 令牌校验拦截器
 * 负责在前台用户端请求到达 Controller 前校验 JWT 令牌的有效性，
 * 并在请求结束后清理线程上下文，防止线程复用造成数据串扰。
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    /**
     * JWT 配置属性，包含密钥、请求头名称等
     */
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 在请求处理前进行 JWT 校验
     *
     * @param request  当前 HTTP 请求对象
     * @param response 当前 HTTP 响应对象
     * @param handler  当前请求对应的处理器（方法或静态资源）
     * @return true 表示放行，false 表示拦截
     * @throws Exception 校验过程中可能出现的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        // 若请求头中无令牌，直接返回401
        if (token == null || token.trim().isEmpty()) {
            log.warn("请求头缺少JWT令牌，拒绝访问");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            // 将当前用户id存入线程上下文
            BaseContext.setCurrentId(userId);
            log.info("当前用户id：{}", userId);
            // 3、通过，放行
            return true;
        } catch (Exception ex) {
            // 4、不通过，响应状态码401
            log.error("JWT令牌校验失败: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    /**
     * 请求处理完成后清理线程上下文
     *
     * @param request  当前 HTTP 请求对象
     * @param response 当前 HTTP 响应对象
     * @param handler  当前请求对应的处理器
     * @param ex       请求处理过程中抛出的异常，可能为 null
     * @throws Exception 清理过程中可能出现的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理线程上下文，避免线程复用导致数据串扰
        BaseContext.removeCurrentId();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
