package com.sky.exception;

/**
 * 账号不存在异常
 * 当系统尝试根据账号信息查询用户时，若未找到对应账号，则抛出此异常
 */
public class AccountNotFoundException extends BaseException {

    /**
     * 无参构造方法
     * 使用默认异常信息
     */
    public AccountNotFoundException() {
    }

    /**
     * 带参构造方法
     * @param msg 自定义异常信息，用于描述具体的异常原因
     */
    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
