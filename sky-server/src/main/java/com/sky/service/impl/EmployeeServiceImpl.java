package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
      password = DigestUtils.md5DigestAsHex(password.getBytes());//md5加密,将原本的密码转化为十六进制字符串
   //在数据库中需要提前添加md5加密
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override//员工信息需要由dto转化为实体类封装，然后保存到数据库中
    public void save(EmployeeDTO employeeDTO) {
        log.info("当前员工的id是" + BaseContext.getCurrentId());
        Employee employee = new Employee();
        //创建实体类，将dto中的数据复制到实体类中
        BeanUtils.copyProperties(employeeDTO, employee);//利用BeanUtils工具类将dto中的数据复制到实体类中
        employee.setStatus(StatusConstant.ENABLE);//因为employee实体类里面属性要多几个，所以需要手动设置属性
        //这里设置的状态属性和下面的密码都有实体类，不用写死
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));//这里的密码是需要md5加密的
//        employee.setCreateTime(LocalDateTime.now());//这里设置创建时间和更新时间
//        employee.setUpdateTime(LocalDateTime.now());
//
//
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
//

        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
       Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        log.info("分页查询结果：{}", page);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
//因为pagehelper需要返回page,但是实际向需要的是pageResult
//所以这里还得获取total,records封装给pageResult
        return new PageResult(total, records);
    }

    @Override
    public void StartOrEnd(Integer status, long id) {
        Employee employee = Employee.builder().status(status).id(id).build();
        log.info("员工状态,让我看看需要更新的员工状态是{}", employee);
        employeeMapper.update(employee);
    }

    @Override
    public Employee getById(Long id) {
       Employee employee =  employeeMapper.getById(id);
       employee.setPassword("****");
       //密码设置为****，避免密码泄露
        return employee;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();//这里需要将dto转化为实体类，是因为之后mapper层可以共用update方法
        //但是在mapper.xml文件里配置的是employee实体类，所以这里需要将dto转化为实体类
        BeanUtils.copyProperties(employeeDTO, employee);//利用BeanUtils工具类将dto中的数据复制到实体类中
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        //设置的这个更新用户是当前登录用户的id，因为当前登录用户是员工，所以这里需要获取当前登录用户的id
        employeeMapper.update(employee);
    }

}
