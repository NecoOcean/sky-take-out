package com.sky.exception;

/**
 * 账号被锁定异常
 * 当用户因多次登录失败等原因导致账号被锁定时抛出此异常
 */
public class AccountLockedException extends BaseException {

    /**
     * 无参构造方法
     * 使用默认异常信息
     */
    public AccountLockedException() {
    }

    /**
     * 带参构造方法
     * @param msg 自定义异常信息
     */
    public AccountLockedException(String msg) {
        super(msg);
    }

}
