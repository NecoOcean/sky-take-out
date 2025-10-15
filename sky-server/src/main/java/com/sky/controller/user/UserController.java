package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * C端用户控制器
 * 提供用户登录、退出等核心接口
 * @author NecoOcean
 * @date 2025-10-15
 */
@RestController
@RequestMapping("/user/user")
@Slf4j
@Tag(name = "C端用户相关接口")
public class UserController {

    /**
     * 用户业务逻辑服务
     */
    @Resource
    private UserService userService;

    /**
     * JWT配置属性
     */
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 微信用户登录
     * 根据微信授权码完成登录并返回JWT令牌
     *
     * @param userLoginDTO 微信登录参数（包含授权码）
     * @return 登录成功后的用户信息及令牌
     */
    @PostMapping("/login")
    @Operation(summary = "微信用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        // 参数合法性校验
        if (userLoginDTO == null || userLoginDTO.getCode() == null || userLoginDTO.getCode().trim().isEmpty()) {
            log.warn("微信登录失败：授权码为空");
            return Result.error("授权码不能为空");
        }

        log.info("微信用户登录，授权码：{}", userLoginDTO.getCode());

        try {
            // 调用微信登录服务
            User user = userService.wxLogin(userLoginDTO);

            // 用户为空说明登录失败
            if (user == null || user.getId() == null) {
                log.warn("微信登录失败：未获取到用户信息");
                return Result.error("微信登录失败");
            }

            // 构建JWT声明
            Map<String, Object> claims = new HashMap<>(2);
            claims.put(JwtClaimsConstant.USER_ID, user.getId());

            // 生成JWT令牌
            String token = JwtUtil.createJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl(),
                    claims
            );

            // 组装返回数据
            UserLoginVO userLoginVO = UserLoginVO.builder()
                    .id(user.getId())
                    .openid(user.getOpenid())
                    .token(token)
                    .build();

            log.info("微信用户登录成功，用户ID：{}", user.getId());
            return Result.success(userLoginVO);
        } catch (Exception e) {
            log.error("微信用户登录异常，授权码：{}", userLoginDTO.getCode(), e);
            return Result.error("登录异常，请稍后重试");
        }
    }

    /**
     * 用户退出登录
     * 清除服务端会话或令牌相关信息（如有）
     *
     * @return 退出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出")
    public Result<String> logout() {
        // TODO: 如有需要，可在此清除Redis中的令牌或会话信息
        log.info("用户退出登录");
        return Result.success("退出成功");
    }
}
