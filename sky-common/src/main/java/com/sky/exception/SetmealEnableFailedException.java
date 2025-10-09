package com.sky.exception;

/**
 * 套餐启用失败异常
 * 当尝试启用一个套餐但操作失败时抛出，例如套餐中包含已下架的菜品
 */
public class SetmealEnableFailedException extends BaseException {

    /**
     * 无参构造方法
     */
    public SetmealEnableFailedException(){}

    /**
     * 带错误信息的构造方法
     * @param msg 异常详细信息
     */
    public SetmealEnableFailedException(String msg){
        super(msg);
    }
}
