package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 订单支付数据传输对象（DTO）
 * 用于在前端与后端之间传递订单支付相关数据，支持新增、修改、查询等操作
 */
@Data
public class OrdersPaymentDTO implements Serializable {
    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;

}
