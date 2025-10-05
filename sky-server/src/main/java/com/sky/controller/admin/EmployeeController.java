package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO  员工登录请求体
     * @return  包含员工信息的通用返回数据类型
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return  成功
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * Path: /admin/employee
     * Method: POST
     * @param employeeDTO  新增员工数据
     * @return  1 成功
     */
    @PostMapping
    public Result<String> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 编辑员工信息
     * Path: /admin/employee
     * Method: PUT
     * 请求体：EmployeeDTO（包含 id、username、name、phone、sex、idNumber）
     */
    @PutMapping
    public Result<String> edit(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.edit(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * Path: /admin/employee/page
     * Method: GET
     */
    @GetMapping("/page")
    public Result<PageResult> page(@ModelAttribute EmployeePageQueryDTO queryDTO) {
        log.info("员工分页查询：{}", queryDTO);
        PageResult pageResult = employeeService.page(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询员工详情
     * Path: /admin/employee/{id}
     * Method: GET
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("查询员工详情, id={}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 启用/禁用员工账号
     * Path: /admin/employee/status/{status}
     * Method: POST
     * 参数：路径参数 status（1启用，0禁用），Query 参数 id（员工ID）
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("更新员工状态, id={}, status={}", id, status);
        employeeService.updateStatus(id, status);
        return Result.success();
    }

}
