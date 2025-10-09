package com.sky.exception;

/**
 * 密码修改失败异常
 * 当用户尝试修改密码但操作失败时抛出，例如原密码错误、新密码不符合规则等情况
 */
public class PasswordEditFailedException extends BaseException {

    /**
     * 构造方法
     *
     * @param msg 异常提示信息，用于描述密码修改失败的具体原因
     */
    public PasswordEditFailedException(String msg) {
        super(msg);
    }

}
