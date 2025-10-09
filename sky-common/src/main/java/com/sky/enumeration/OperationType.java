package com.sky.enumeration;

/**
 * 数据库操作类型枚举
 * 用于标识当前执行的数据库操作是“更新”还是“插入”
 *
 * @author sky
 * @date 2023/06/01
 */
public enum OperationType {

    /**
     * 更新操作
     * 表示对已有数据进行修改
     */
    UPDATE,

    /**
     * 插入操作
     * 表示新增一条数据
     */
    INSERT

}
