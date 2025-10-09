package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类分页查询数据传输对象（DTO）
 * 用于在前端分页查询分类列表时传递查询条件，支持按名称模糊查询、按类型筛选等
 */
@Data
public class CategoryPageQueryDTO implements Serializable {

    //页码
    private int page;

    //每页记录数
    private int pageSize;

    //分类名称
    private String name;

    //分类类型 1菜品分类  2套餐分类
    private Integer type;

}
