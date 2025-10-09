package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 * 用于统一返回分页数据格式，包含总记录数与当前页数据集合
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

    /**
     * 总记录数
     * 数据库中符合条件的全部记录数量
     */
    private long total;

    /**
     * 当前页数据集合
     * 当前分页查询结果的数据列表
     */
    private List records;

}
