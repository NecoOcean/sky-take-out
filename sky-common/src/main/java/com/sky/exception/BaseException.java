package com.sky.exception;

/**
 * 业务异常
 * 用于封装业务逻辑中出现的异常情况，继承自RuntimeException，属于运行时异常
 */
public class BaseException extends RuntimeException {

    /**
     * 无参构造方法
     * 创建一个不包含任何详细信息的业务异常实例
     */
    public BaseException() {
    }

    /**
     * 带详细信息构造方法
     * @param msg 异常的详细信息，用于描述异常原因
     */
    public BaseException(String msg) {
        super(msg);
    }

}
