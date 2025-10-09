package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工Mapper接口
 * 用于数据库操作员工相关数据
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 使用 MyBatis-Plus 通用方法，无需手写SQL
}
