package com.sky.context;

/**
 * 基于 ThreadLocal 封装的上下文工具类，用于在同一线程内共享当前登录用户 ID。
 * 适用于 Web 场景下通过拦截器/过滤器将用户 ID 绑定到当前线程，
 * 在后续业务逻辑中随时获取，避免层层传参。
 *
 * @author sky
 */
public class BaseContext {

    /**
     * ThreadLocal 实例，用于保存当前线程对应的用户 ID。
     * 每个线程拥有独立的副本，线程之间互不影响。
     */
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 将当前登录用户 ID 设置到 ThreadLocal 中。
     *
     * @param id 用户主键 ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 从 ThreadLocal 中获取当前线程保存的用户 ID。
     *
     * @return 当前用户 ID；若未设置则返回 null
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 移除 ThreadLocal 中当前线程保存的用户 ID，防止内存泄漏。
     * 建议在请求处理完成后（如拦截器的 afterCompletion 阶段）调用。
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
