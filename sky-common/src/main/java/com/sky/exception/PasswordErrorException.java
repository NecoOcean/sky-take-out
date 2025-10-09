package com.sky.exception;

/**
 * 密码错误异常
 * 当用户输入的密码与系统记录的密码不匹配时抛出此异常
 */
public class PasswordErrorException extends BaseException {

    /**
     * 无参构造方法
     * 使用默认异常信息
     */
    public PasswordErrorException() {
    }

    /**
     * 带参构造方法
     * @param msg 自定义异常信息
     */
    public PasswordErrorException(String msg) {
        super(msg);
    }

}
