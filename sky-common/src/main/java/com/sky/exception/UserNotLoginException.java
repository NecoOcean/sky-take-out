package com.sky.exception;

/**
 * 用户未登录异常
 * 当用户未登录或登录状态失效时抛出此异常
 */
public class UserNotLoginException extends BaseException {

    /**
     * 无参构造方法
     * 使用默认异常信息
     */
    public UserNotLoginException() {
    }

    /**
     * 带参构造方法
     * @param msg 自定义异常信息
     */
    public UserNotLoginException(String msg) {
        super(msg);
    }

}
