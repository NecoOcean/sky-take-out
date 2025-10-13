package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Employee;

/**
 * 员工Mapper接口
 * 用于数据库操作员工相关数据，继承自MyBatis-Plus的BaseMapper。
 * 主要负责对员工的增删改查操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 使用 MyBatis-Plus 通用方法，无需手写SQL
}
