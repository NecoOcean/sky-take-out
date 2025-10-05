package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 员工登录数据集合体
     * @return 员工信息
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.eq(Employee::getUsername, username);
        Employee employee = employeeMapper.selectOne(query);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // --- 核心改动：使用jBcrypt进行密码校验 ---
        // BCrypt.checkpw()方法会自动从存储的哈希密码中提取盐(salt)，
        // 然后用该盐对用户输入的明文密码进行哈希，最后比较两个哈希值是否相等。
        if (!BCrypt.checkpw(password, employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        // --- 密码校验结束 ---

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、返回实体对象
        return employee;
    }

    /**
     * 对密码进行加密
     * @param plainPassword 明文密码
     * @return 加密后的哈希字符串
     */
    private static String encryptPassword(String plainPassword) {
        // 生成盐并进行哈希。12是工作因子（work factor），决定了计算的复杂度。
        // 工作因子越高，哈希越慢，安全性也越高，但消耗的CPU资源也越多。
        // 通常在 10-14 之间选择一个平衡点。
        int workFactor = 12;
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(workFactor));
    }

    /**
     * 验证密码是否匹配
     * @param plainPassword 待验证的明文密码
     * @param hashedPassword 数据库中存储的哈希密码
     * @return 如果匹配返回 true，否则返回 false
     */
    private static boolean checkPassword(String plainPassword, String hashedPassword) {
        // BCrypt.checkpw 会自动从 hashedPassword 中提取盐，并使用相同的算法进行验证。
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hash provided for comparison");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
