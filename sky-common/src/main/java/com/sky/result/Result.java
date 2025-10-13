package com.sky.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果封装类
 * @param <T> 返回数据的类型
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 响应编码：1表示成功，0及其他数字表示失败
     */
    private Integer code;

    /**
     * 响应提示信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 无参成功响应构造方法
     * @param <T> 数据类型
     * @return 成功结果对象，code=1
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        return result;
    }

    /**
     * 带数据的成功响应构造方法
     * @param object 返回的数据对象
     * @param <T> 数据类型
     * @return 成功结果对象，code=1，data=object
     */
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    /**
     * 失败响应构造方法
     * @param msg 错误提示信息
     * @param <T> 数据类型
     * @return 失败结果对象，code=0，msg=msg
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
