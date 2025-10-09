package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单拒绝数据传输对象（DTO）
 * 用于在前端拒绝订单时传递订单ID和拒绝原因
 */
@Data
public class OrdersRejectionDTO implements Serializable {

    private Long id;

    //订单拒绝原因
    private String rejectionReason;

}
