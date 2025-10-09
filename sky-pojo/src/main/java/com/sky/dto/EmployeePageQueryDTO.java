package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工分页查询数据传输对象（DTO）
 * 用于在前端分页查询员工列表时传递查询条件，支持按姓名模糊查询等
 */
@Data
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
