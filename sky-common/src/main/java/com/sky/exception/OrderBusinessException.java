package com.sky.exception;

/**
 * 订单业务异常
 * 用于封装订单相关业务逻辑抛出的异常信息
 */
public class OrderBusinessException extends BaseException {

    /**
     * 构造订单业务异常
     *
     * @param msg 异常提示信息
     */
    public OrderBusinessException(String msg) {
        super(msg);
    }

}
