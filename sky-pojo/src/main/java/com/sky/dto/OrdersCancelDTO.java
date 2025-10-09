package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单取消数据传输对象（DTO）
 * 用于在前端取消订单时传递订单ID和取消原因
 */
@Data
public class OrdersCancelDTO implements Serializable {

    private Long id;
    //订单取消原因
    private String cancelReason;

}
