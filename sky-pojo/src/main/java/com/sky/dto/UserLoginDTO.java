package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录数据传输对象（DTO）
 * 用于在前端登录时传递验证码，支持用户登录操作
 */
@Data
public class UserLoginDTO implements Serializable {

    private String code;

}
