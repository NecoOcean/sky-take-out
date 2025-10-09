package com.sky.exception;

/**
 * 登录失败异常
 * 当用户登录验证失败时抛出，例如用户名或密码错误、账号被禁用等情况
 */
public class LoginFailedException extends BaseException {

    /**
     * 构造一个带有详细错误信息的登录失败异常
     *
     * @param msg 描述登录失败原因的详细信息
     */
    public LoginFailedException(String msg) {
        super(msg);
    }
}
