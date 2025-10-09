package com.sky.exception;

/**
 * 地址簿业务异常类
 * 用于封装与地址簿相关的业务逻辑异常信息
 */
public class AddressBookBusinessException extends BaseException {

    /**
     * 构造方法
     * @param msg 异常提示信息
     */
    public AddressBookBusinessException(String msg) {
        super(msg);
    }

}
