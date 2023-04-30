package com.Hao.reggie.service.Impl;

import com.Hao.reggie.entity.Employee;
import com.Hao.reggie.mapper.EmployeeMapper;

import com.Hao.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
