package com.Hao.reggie.controller;

import com.Hao.reggie.common.Result;
import com.Hao.reggie.entity.Employee;
import com.Hao.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp==null){
            return Result.error("用户不存在,登陆失败");
        }else if(!emp.getPassword().equals(password)){
            return Result.error("密码错误，登陆失败");
        }else if(emp.getStatus()==0){
            return Result.error("账号已禁用，登陆失败");
        }else{
            request.getSession().setAttribute("employee",emp.getId());
            return Result.success(emp);
        }
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empId=(Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/
        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> getPage(int page, int pageSize, String name){
        Page<Employee> pageInfo=new Page(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isEmpty(name),Employee::getName,name);
        queryWrapper.orderByAsc(Employee::getUpdateTime);
        if(name==null){
            employeeService.page(pageInfo);
        }else{
            employeeService.page(pageInfo,queryWrapper);
        }

        return Result.success(pageInfo);
    }

    @PutMapping
    public Result<String> UpdateEmployee(@RequestBody Employee employee){
        /*Long empId=(Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/
        employeeService.updateById(employee);
        return Result.success("员工状态修改成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> FindById(@PathVariable Long id){
        /*Employee emp=employeeService.getById(id);*/
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId,id);
        Employee emp=employeeService.getOne(queryWrapper);
        if(emp!=null){
            return Result.success(emp);
        }else{
            return Result.error("用户信息不存在");
        }

    }
}
