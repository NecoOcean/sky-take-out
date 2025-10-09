package com.sky.constant;

/**
 * 信息提示常量类
 * 用于统一管理系统中各类提示信息，便于维护与国际化扩展
 */
public class MessageConstant {

    /**
     * 密码错误提示
     */
    public static final String PASSWORD_ERROR = "密码错误";

    /**
     * 账号不存在提示
     */
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";

    /**
     * 账号被锁定提示
     */
    public static final String ACCOUNT_LOCKED = "账号被锁定";

    /**
     * 数据已存在提示
     */
    public static final String ALREADY_EXISTS = "已存在";

    /**
     * 未知错误提示
     */
    public static final String UNKNOWN_ERROR = "未知错误";

    /**
     * 用户未登录提示
     */
    public static final String USER_NOT_LOGIN = "用户未登录";

    /**
     * 分类已被套餐关联，无法删除提示
     */
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";

    /**
     * 分类已被菜品关联，无法删除提示
     */
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";

    /**
     * 购物车为空，无法下单提示
     */
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";

    /**
     * 用户地址为空，无法下单提示
     */
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";

    /**
     * 登录失败提示
     */
    public static final String LOGIN_FAILED = "登录失败";

    /**
     * 文件上传失败提示
     */
    public static final String UPLOAD_FAILED = "文件上传失败";

    /**
     * 套餐包含未启售菜品，无法启售提示
     */
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";

    /**
     * 密码修改失败提示
     */
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";

    /**
     * 起售中的菜品不能删除提示
     */
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";

    /**
     * 起售中的套餐不能删除提示
     */
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";

    /**
     * 菜品已被套餐关联，无法删除提示
     */
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";

    /**
     * 订单状态错误提示
     */
    public static final String ORDER_STATUS_ERROR = "订单状态错误";

    /**
     * 订单不存在提示
     */
    public static final String ORDER_NOT_FOUND = "订单不存在";

    /**
     * 菜品不存在提示
     */
    public static final String DISH_NOT_FOUND = "菜品不存在";

}
