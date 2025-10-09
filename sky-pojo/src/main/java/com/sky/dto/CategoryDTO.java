package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类数据传输对象（DTO）
 * 用于在前端与后端之间传递分类相关数据，支持新增、修改、查询等操作
 */
@Data
public class CategoryDTO implements Serializable {

    //主键
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    private Integer type;

    //分类名称
    private String name;

    //排序
    private Integer sort;

}
