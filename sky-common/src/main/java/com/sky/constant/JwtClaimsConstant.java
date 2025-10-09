package com.sky.constant;

/**
 * JWT 载荷中的自定义声明常量
 * 用于统一维护 token 中携带的键名，避免硬编码
 */
public class JwtClaimsConstant {

    /**
     * 员工ID
     */
    public static final String EMP_ID = "empId";

    /**
     * 用户ID
     */
    public static final String USER_ID = "userId";

    /**
     * 手机号
     */
    public static final String PHONE = "phone";

    /**
     * 用户名（登录账号）
     */
    public static final String USERNAME = "username";

    /**
     * 姓名（真实姓名）
     */
    public static final String NAME = "name";

}
