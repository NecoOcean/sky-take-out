package com.sky.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 审计人填充（如果上下文存在用户ID）
        if (currentId != null) {
            this.strictInsertFill(metaObject, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
            this.strictInsertFill(metaObject, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        }

        // 时间字段填充
        this.strictInsertFill(metaObject, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 更新人填充（如果上下文存在用户ID）
        if (currentId != null) {
            this.strictUpdateFill(metaObject, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        }

        // 更新时间填充
        this.strictUpdateFill(metaObject, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
    }
}
