package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，统一处理项目中抛出的各类异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常（BaseException）
     * 当控制器层抛出 BaseException 时，此方法会被调用
     *
     * @param ex 捕获到的业务异常对象
     * @return 封装后的统一响应结果，包含错误信息
     */
    @ExceptionHandler(BaseException.class)
    public Result exceptionHandler(BaseException ex) {
        log.error("业务异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获数据库完整性约束冲突异常（SQLIntegrityConstraintViolationException）
     * 主要用于处理如唯一索引冲突等数据库层面异常
     *
     * @param ex 捕获到的数据库异常对象
     * @return 封装后的统一响应结果，若检测到重复键则返回友好提示，否则返回未知错误
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        // 判断异常信息中是否包含“Duplicate key”关键字，识别重复键冲突
        if (message.contains("Duplicate key")) {
            // 按空格分割异常信息，提取冲突字段名
            String[] split = message.split(" ");
            String name = split[2];
            String msg = name + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        } else {
            // 其他数据库完整性异常返回统一未知错误提示
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
