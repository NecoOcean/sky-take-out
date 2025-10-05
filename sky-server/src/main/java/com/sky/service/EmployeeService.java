package com.sky.service;

import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO 新增员工数据
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * Path: /admin/employee/page
     * Method: GET
     * @param queryDTO 分页查询参数
     * @return 分页结果
     */
    PageResult page(EmployeePageQueryDTO queryDTO);

}
