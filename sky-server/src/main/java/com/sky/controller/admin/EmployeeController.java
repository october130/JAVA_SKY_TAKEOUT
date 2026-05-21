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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")//api注解描述接口，可以改变swagger文档的栏目
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录操作")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),// jwt令牌有效期
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }
    @PostMapping
    @ApiOperation("新增员工操作")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("员工分页查询操作")
    public Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询返回的实体类：{}", employeePageQueryDTO);
 PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
 return Result.success(pageResult);
    }

@PostMapping("/status/{status}")
@ApiOperation("员工启用或者禁用操作")
    public Result startORend(@PathVariable Integer status,long id){
        log.info("启用或者禁用员工：{},{}",status, id);
        employeeService.StartOrEnd(status,id);
        return Result.success();

    }
@GetMapping("/{id}")//查询回显，如果需要修改员工信息，首先需要的是点击修改按钮时能看到该员工的信息
@ApiOperation("根据id查询员工信息")//对应的接口就是根据id查询员工信息
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据员工id来查询信息：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);//最终需要给返回给后端管理端信息
    }
@PutMapping//在进行完修改按钮点击后用户信息的反馈之后需要进行员工信息修改操作
@ApiOperation("员工信息修改操作")
  public  Result update( @RequestBody  EmployeeDTO employeeDTO){//封装成dto对象
        log.info("员工信息修改：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }



    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出操作")
    public Result<String> logout() {
        return Result.success();
    }

}
