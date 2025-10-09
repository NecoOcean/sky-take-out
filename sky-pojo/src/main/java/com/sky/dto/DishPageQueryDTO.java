package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜品分页查询数据传输对象（DTO）
 * 用于在前端分页查询菜品列表时传递查询条件，支持按名称模糊查询、按分类筛选、按状态筛选等
 */
@Data
public class DishPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
