package com.sky.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 用于在插入和更新操作时自动填充审计字段（创建人、创建时间、更新人、更新时间）
 *
 * @author NecoOcean
 * @date 2025/10/10
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作自动填充
     * 填充字段：create_user、create_time、update_user、update_time
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        log.info("从 BaseContext 获取到的当前用户 ID 为: {}", currentId);

        // 审计人填充（如果上下文存在用户ID）
        if (currentId != null) {
            this.strictInsertFill(metaObject, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
            this.strictInsertFill(metaObject, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        }

        // 时间字段填充
        this.strictInsertFill(metaObject, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
    }

    /**
     * 更新操作自动填充
     * 填充字段：update_user、update_time
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        log.info("从 BaseContext 获取到的当前用户 ID 为: {}", currentId);

        // 更新人填充（如果上下文存在用户ID）
        if (currentId != null) {
            this.strictUpdateFill(metaObject, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        }

        // 更新时间填充
        this.strictUpdateFill(metaObject, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
    }
}
