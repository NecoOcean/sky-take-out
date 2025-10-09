package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 购物车数据传输对象（DTO）
 * 用于在前端与后端之间传递购物车相关数据，支持新增、修改、查询等操作
 */
@Data
public class ShoppingCartDTO implements Serializable {

    private Long dishId;
    private Long setmealId;
    private String dishFlavor;

}
