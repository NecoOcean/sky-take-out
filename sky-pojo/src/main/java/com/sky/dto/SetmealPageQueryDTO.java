package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 套餐分页查询数据传输对象（DTO）
 * 用于在前端分页查询套餐列表时传递查询条件，支持按名称、分类ID、状态等筛选
 */
@Data
public class SetmealPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
