package com.sky.constant;

/**
 * 公共字段自动填充相关常量
 * 本类用于集中管理 MyBatis-Plus 自动填充时需要用到的实体字段名，
 * 避免在代码中硬编码，提高可维护性。
 */
public class AutoFillConstant {

    /**
     * 实体类中表示“创建时间”的属性名
     * 对应数据库表中的 create_time 字段，用于 MetaObjectHandler 自动填充
     */
    public static final String SET_CREATE_TIME = "createTime";

    /**
     * 实体类中表示“更新时间”的属性名
     * 对应数据库表中的 update_time 字段，用于 MetaObjectHandler 自动填充
     */
    public static final String SET_UPDATE_TIME = "updateTime";

    /**
     * 实体类中表示“创建人”的属性名
     * 对应数据库表中的 create_user 字段，用于 MetaObjectHandler 自动填充
     */
    public static final String SET_CREATE_USER = "createUser";

    /**
     * 实体类中表示“更新人”的属性名
     * 对应数据库表中的 update_user 字段，用于 MetaObjectHandler 自动填充
     */
    public static final String SET_UPDATE_USER = "updateUser";

    /**
     * 私有构造器，防止实例化
     */
    private AutoFillConstant() {
        throw new IllegalStateException("禁止实例化常量类");
    }
}
