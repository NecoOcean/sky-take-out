package com.sky.exception;

/**
 * 删除操作不被允许时抛出的业务异常
 * 用于在业务逻辑中标识当前删除操作因业务规则限制而无法执行
 */
public class DeletionNotAllowedException extends BaseException {

    /**
     * 构造一个带有详细错误信息的删除不允许异常
     *
     * @param msg 描述为何不允许删除的详细错误信息
     */
    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

}
