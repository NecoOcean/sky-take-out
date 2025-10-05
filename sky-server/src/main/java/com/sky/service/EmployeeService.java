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

    /**
     * 更新员工账号状态（启用/禁用）
     * Path: /admin/employee/status/{status}
     * Method: POST
     * @param id 员工ID
     * @param status 状态，1为启用，0为禁用
     */
    void updateStatus(Long id, Integer status);

    /**
     * 编辑员工信息
     * Path: /admin/employee
     * Method: PUT
     * @param employeeDTO 员工信息（包含 id、username、name、phone、sex、idNumber）
     */
    void edit(EmployeeDTO employeeDTO);

    /**
     * 根据ID查询员工详情
     * Path: /admin/employee/{id}
     * Method: GET
     * @param id 员工ID
     * @return 员工详情（脱敏）
     */
    Employee getById(Long id);

}
