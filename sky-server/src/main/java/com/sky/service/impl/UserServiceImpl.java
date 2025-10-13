package com.sky.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 * 提供用户相关的业务逻辑实现，包括微信登录、用户注册等。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 微信服务接口地址
     */
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信配置属性
     */
    @Resource
    private WeChatProperties weChatProperties;

    /**
     * 用户映射器，用于数据库操作
     */
    @Resource
    private UserMapper userMapper;

    /**
     * 微信登录
     * 根据微信授权码获取用户 openid，若为新用户则自动注册
     *
     * @param userLoginDTO 微信登录参数（包含授权码 code）
     * @return 登录成功后的用户实体
     * @throws LoginFailedException 当 openid 获取失败时抛出
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 参数校验
        if (userLoginDTO == null || userLoginDTO.getCode() == null || userLoginDTO.getCode().trim().isEmpty()) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 调用微信接口获取 openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 判断 openid 是否为空，如果为空表示登录失败，抛出业务异常
        if (openid == null || openid.trim().isEmpty()) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断当前用户是否为新用户
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenid, openid));

        // 如果是新用户，自动完成注册
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 返回这个用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的 openid
     *
     * @param code 微信小程序登录授权码
     * @return 微信用户的 openid，若获取失败则返回 null
     */
    private String getOpenid(String code) {
        try {
            // 组装请求参数
            Map<String, String> map = new HashMap<>(4);
            map.put("appid", weChatProperties.getAppid());
            map.put("secret", weChatProperties.getSecret());
            map.put("js_code", code);
            map.put("grant_type", "authorization_code");

            // 发起 GET 请求
            String json = HttpClientUtil.doGet(WX_LOGIN, map);

            // 解析响应结果
            JSONObject jsonObject = JSON.parseObject(json);
            if (jsonObject == null) {
                return null;
            }

            // 返回 openid
            return jsonObject.getString("openid");
        } catch (Exception e) {
            // 记录日志（可接入日志框架）并返回 null，表示获取失败
            return null;
        }
    }
}
