package com.sky.exception;

/**
 * 购物车业务异常
 * 当购物车相关业务出现非法操作时抛出，如商品已下架、库存不足、重复添加等场景
 */
public class ShoppingCartBusinessException extends BaseException {

    /**
     * 构造购物车业务异常
     *
     * @param msg 异常提示信息
     */
    public ShoppingCartBusinessException(String msg) {
        super(msg);
    }

}
