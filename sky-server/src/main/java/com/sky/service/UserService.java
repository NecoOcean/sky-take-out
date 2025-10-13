package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑操作，包括微信登录、用户注册等。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface UserService {

    /**
     * 微信登录
     * 根据用户登录DTO中的微信登录凭证进行登录验证，返回登录成功的用户信息。
     *
     * @param userLoginDTO 包含微信登录凭证的用户登录DTO
     * @return 登录成功的用户信息
     */
    User wxLogin(UserLoginDTO userLoginDTO);


}
