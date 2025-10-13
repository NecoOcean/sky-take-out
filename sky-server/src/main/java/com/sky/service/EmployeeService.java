package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

/**
 * 员工业务接口
 * 提供员工登录、新增、分页查询、状态更新、编辑及详情查询等功能，支持批量操作。
 *
 * @author NecoOcean
 * @date 2025/10/13
 */
public interface EmployeeService {

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 员工登录信息（包含用户名与密码）
     * @return 登录成功后的员工实体，若登录失败则返回 null
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     *
     * @param employeeDTO 新增员工数据（包含用户名、姓名、手机号、性别、身份证号等）
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * Path: /admin/employee/page
     * Method: GET
     *
     * @param queryDTO 分页查询参数（页码、每页条数、可选关键字等）
     * @return 分页结果对象，包含当前页数据及总记录数
     */
    PageResult page(EmployeePageQueryDTO queryDTO);

    /**
     * 更新员工账号状态（启用/禁用）
     * Path: /admin/employee/status/{status}
     * Method: POST
     *
     * @param id     员工ID，不能为空
     * @param status 状态值，1 表示启用，0 表示禁用
     */
    void updateStatus(Long id, Integer status);

    /**
     * 编辑员工信息
     * Path: /admin/employee
     * Method: PUT
     *
     * @param employeeDTO 员工信息（必须包含 id，可选字段：username、name、phone、sex、idNumber）
     */
    void edit(EmployeeDTO employeeDTO);

    /**
     * 根据ID查询员工详情
     * Path: /admin/employee/{id}
     * Method: GET
     *
     * @param id 员工ID，不能为空
     * @return 员工详情实体（已对敏感信息脱敏处理）
     */
    Employee getById(Long id);

}
