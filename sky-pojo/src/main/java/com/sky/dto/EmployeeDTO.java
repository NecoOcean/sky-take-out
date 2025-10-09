package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工数据传输对象（DTO）
 * 用于在前端与后端之间传递员工相关数据，支持新增、修改、查询等操作
 */
@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
