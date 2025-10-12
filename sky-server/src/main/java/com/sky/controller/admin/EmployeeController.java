package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理控制器
 * 提供员工登录、退出、新增、编辑、分页查询、详情查询、状态更新等RESTful接口
 *
 * @author NecoOcean
 * @date 2025/10/10
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工管理", description = "员工相关接口，包括登录、退出、增删改查、状态切换等")
public class EmployeeController {

    /**
     * 员工业务逻辑接口
     */
    @Resource
    private EmployeeService employeeService;

    /**
     * JWT配置属性
     */
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 员工登录
     * 验证用户名与密码，登录成功后生成JWT令牌并返回员工基本信息
     *
     * @param employeeLoginDTO 员工登录请求体，包含用户名与密码
     * @return Result<EmployeeLoginVO> 统一响应结果，包含员工ID、用户名、姓名及JWT令牌
     */
    @PostMapping("/login")
    @Operation(
            summary = "员工登录",
            description = "根据用户名+密码完成登录校验，成功则返回JWT令牌及员工基本信息",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "登录请求体",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmployeeLoginDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "登录成功",
                            content = @Content(schema = @Schema(implementation = EmployeeLoginVO.class))),
                    @ApiResponse(responseCode = "401", description = "用户名或密码错误")
            }
    )
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        // 调用service完成登录校验
        Employee employee = employeeService.login(employeeLoginDTO);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        // 封装返回数据
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 员工退出
     * 前端清除本地令牌即可，后端无状态，无需处理
     *
     * @return Result<String> 统一响应结果，仅提示成功
     */
    @PostMapping("/logout")
    @Operation(
            summary = "员工退出",
            description = "退出登录，前端自行清除本地令牌即可，后端无状态",
            responses = @ApiResponse(responseCode = "200", description = "退出成功")
    )
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * 接收前端提交的EmployeeDTO，完成员工数据持久化
     *
     * @param employeeDTO 新增员工数据，包含用户名、姓名、手机号、性别、身份证号
     * @return Result<String> 统一响应结果，仅提示成功
     */
    @PostMapping
    @Operation(
            summary = "新增员工",
            description = "后台管理员新增员工账号，用户名全局唯一",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "新增员工请求体",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "新增成功"),
                    @ApiResponse(responseCode = "400", description = "参数非法或用户名已存在")
            }
    )
    public Result<String> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 编辑员工信息
     * 根据员工ID更新除用户名外的其他字段
     *
     * @param employeeDTO 编辑员工信息请求体，必须包含主键ID
     * @return Result<String> 统一响应结果，仅提示成功
     */
    @PutMapping
    @Operation(
            summary = "编辑员工信息",
            description = "根据主键ID更新员工信息（用户名不可修改）",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "编辑员工请求体",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "编辑成功"),
                    @ApiResponse(responseCode = "400", description = "参数非法"),
                    @ApiResponse(responseCode = "404", description = "员工不存在")
            }
    )
    public Result<String> edit(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.edit(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * 支持按姓名模糊查询、性别筛选，并分页返回结果
     *
     * @param queryDTO 分页查询参数，封装页码、页大小、姓名关键词、性别等
     * @return Result<PageResult> 统一响应结果，包含分页数据与总记录数
     */
    @GetMapping("/page")
    @Operation(
            summary = "员工分页查询",
            description = "根据姓名（模糊）、性别等条件分页查询员工列表",
            parameters = {
                    @Parameter(name = "page", description = "页码，从1开始", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "1")),
                    @Parameter(name = "pageSize", description = "每页条数", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "name", description = "姓名关键词，模糊匹配", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                    @Parameter(name = "gender", description = "性别，1男 0女", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
            },
            responses = @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = PageResult.class)))
    )
    public Result<PageResult> page(@ModelAttribute EmployeePageQueryDTO queryDTO) {
        log.info("员工分页查询：{}", queryDTO);
        PageResult pageResult = employeeService.page(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询员工详情
     * 用于回显编辑表单
     *
     * @param id 员工主键ID
     * @return Result<Employee> 统一响应结果，返回员工实体
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "查询员工详情",
            description = "根据主键ID获取员工详细信息，用于编辑回显",
            parameters = @Parameter(name = "id", description = "员工主键ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功",
                            content = @Content(schema = @Schema(implementation = Employee.class))),
                    @ApiResponse(responseCode = "404", description = "员工不存在")
            }
    )
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("查询员工详情, id={}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 启用/禁用员工账号
     * 通过status路径参数与id查询参数完成状态切换
     *
     * @param status 目标状态，1启用，0禁用
     * @param id     员工主键ID
     * @return Result<String> 统一响应结果，仅提示成功
     */
    @PostMapping("/status/{status}")
    @Operation(
            summary = "启用/禁用员工账号",
            description = "切换员工账号状态，1启用 0禁用",
            parameters = {
                    @Parameter(name = "status", description = "目标状态，1启用 0禁用", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer")),
                    @Parameter(name = "id", description = "员工主键ID", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "状态更新成功"),
                    @ApiResponse(responseCode = "400", description = "参数非法"),
                    @ApiResponse(responseCode = "404", description = "员工不存在")
            }
    )
    public Result<String> updateStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("更新员工状态, id={}, status={}", id, status);
        employeeService.updateStatus(id, status);
        return Result.success();
    }

}
