package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


    @Override
    public Employee EmpLogin(Employee employee) {
        //根据网页传过来的数据查找数据库是否有匹配的用户
        LambdaQueryWrapper<Employee> QWEmployee = new LambdaQueryWrapper<>();
        QWEmployee.eq(Employee::getUsername,employee.getUsername());
        return getOne(QWEmployee);
    }
}
