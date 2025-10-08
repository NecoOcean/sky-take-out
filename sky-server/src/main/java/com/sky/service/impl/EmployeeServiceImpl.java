package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import com.sky.result.PageResult;
import org.springframework.transaction.annotation.Transactional;
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

        // --- 支持BCrypt并迁移旧明文密码 ---
        String storedPassword = employee.getPassword();
        if (storedPassword == null) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        boolean passwordMatched;
        if (storedPassword.startsWith("$2a$")) {
            // 数据库已是BCrypt哈希
            passwordMatched = checkPassword(password, storedPassword);
        } else {
            // 旧数据可能为明文，进行直接比对，并在匹配时迁移为BCrypt
            passwordMatched = Objects.equals(password, storedPassword);
            if (passwordMatched) {
                String newHashed = encryptPassword(password);
                employee.setPassword(newHashed);
                // 将密码升级为BCrypt哈希
                employeeMapper.updateById(employee);
            }
        }
        if (!passwordMatched) {
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
     * 新增员工
     *
     * @param employeeDTO 新增员工数据
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        // 组装实体
        Employee employee = Employee.builder()
                .username(employeeDTO.getUsername())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .idNumber(employeeDTO.getIdNumber())
                // 默认启用状态
                .status(StatusConstant.ENABLE)
                .build();

        // 设置默认密码并加密（与登录保持一致使用 BCrypt）
        String defaultPlainPassword = "123456";
        employee.setPassword(encryptPassword(defaultPlainPassword));

        // 审计字段不再手动赋值，依赖 MyBatis-Plus 自动填充

        // 保存
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     * 支持根据姓名模糊查询
     * Path: /admin/employee/page
     * Method: GET
     * @param queryDTO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult page(EmployeePageQueryDTO queryDTO) {
        // 构造分页对象
        Page<Employee> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        // 构造查询条件
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Employee::getName, queryDTO.getName());
        }
        // 排序：按更新时间倒序，保证最新数据在前
        wrapper.orderByDesc(Employee::getUpdateTime);

        // 执行分页查询
        employeeMapper.selectPage(page, wrapper);

        // 清理敏感字段，避免密码泄露
        if (page.getRecords() != null) {
            for (Employee emp : page.getRecords()) {
                if (emp != null) {
                    emp.setPassword(null);
                }
            }
        }

        // 返回统一分页结构
        return new PageResult(page.getTotal(), page.getRecords());
    }

    /**
     * 更新员工账号状态（启用/禁用）
     * @param id 员工ID
     * @param status 状态，1为启用，0为禁用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        if (id == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
        if (!Objects.equals(status, com.sky.constant.StatusConstant.ENABLE)
                && !Objects.equals(status, com.sky.constant.StatusConstant.DISABLE)) {
            throw new IllegalArgumentException("状态值非法，仅支持0或1");
        }

        // 仅更新必要字段，避免覆盖其他字段
        Employee toUpdate = new Employee();
        toUpdate.setId(id);
        toUpdate.setStatus(status);

        // 审计字段不再手动赋值，依赖 MyBatis-Plus 自动填充

        employeeMapper.updateById(toUpdate);
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

    /**
     * 编辑员工信息
     * 仅允许编辑：username、name、phone、sex、idNumber
     * 不修改：password、status、createTime、createUser 等
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(EmployeeDTO employeeDTO) {
        if (employeeDTO == null || employeeDTO.getId() == null) {
            throw new IllegalArgumentException("编辑员工信息时，id不能为空");
        }
        Employee toUpdate = new Employee();
        toUpdate.setId(employeeDTO.getId());
        toUpdate.setUsername(employeeDTO.getUsername());
        toUpdate.setName(employeeDTO.getName());
        toUpdate.setPhone(employeeDTO.getPhone());
        toUpdate.setSex(employeeDTO.getSex());
        toUpdate.setIdNumber(employeeDTO.getIdNumber());

        // 审计字段不再手动赋值，依赖 MyBatis-Plus 自动填充
        employeeMapper.updateById(toUpdate);
    }

    /**
     * 根据ID查询员工详情（脱敏密码）
     * @param id 员工ID
     * @return 员工详情
     */
    @Override
    public Employee getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("查询员工详情时，id不能为空");
        }
        Employee employee = employeeMapper.selectById(id);
        if (employee != null) {
            employee.setPassword(null);
        }
        return employee;
    }
}
