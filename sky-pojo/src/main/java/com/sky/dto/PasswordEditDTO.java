package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 密码编辑数据传输对象（DTO）
 * 用于在前端编辑员工密码时传递员工ID、旧密码和新密码
 */
@Data
public class PasswordEditDTO implements Serializable {

    //员工id
    private Long empId;

    //旧密码
    private String oldPassword;

    //新密码
    private String newPassword;

}
