package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单确认数据传输对象（DTO）
 * 用于在前端确认订单时传递订单ID和订单状态
 */
@Data
public class OrdersConfirmDTO implements Serializable {

    private Long id;
    //订单状态 1待付款 2待接单 3 已接单 4 派送中 5 已完成 6 已取消 7 退款
    private Integer status;

}
