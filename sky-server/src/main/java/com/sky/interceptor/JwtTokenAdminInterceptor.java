package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 令牌校验拦截器
 * 负责在后台管理端请求到达 Controller 前校验 JWT 令牌的有效性，
 * 并在请求结束后清理线程上下文，防止线程复用造成数据串扰。
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    /**
     * JWT 配置属性，包含密钥、请求头名称等
     */
    @Autowired
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
        // 判断当前拦截到的是 Controller 的方法还是其他静态资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前员工id：{}", empId);
            // 写入当前员工ID到线程上下文，供后续审计字段使用
            BaseContext.setCurrentId(empId);
            // 3、通过，放行
            return true;
        } catch (Exception ex) {
            // 4、不通过，响应401状态码
            response.setStatus(401);
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
