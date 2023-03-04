package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;

import java.util.List;

public interface EmployeeService extends IService<Employee> {
   public Employee EmpLogin(Employee employee);
}
