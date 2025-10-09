package com.sky.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 订单统计数据传输对象（VO）
 * 用于在前端展示订单的统计信息，包括待接单数量、待派送数量和派送中数量等
 */
@Data
public class OrderStatisticsVO implements Serializable {
    //待接单数量
    private Integer toBeConfirmed;

    //待派送数量
    private Integer confirmed;

    //派送中数量
    private Integer deliveryInProgress;
}
