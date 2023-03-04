package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")//登入方法
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //根据网页传过来的数据查找数据库是否有匹配的用户
        Employee one = employeeService.EmpLogin(employee);
        if (one == null)         return R.error("用户名不存在,登入失败");
        //检查密码是否正确
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        boolean equals = !one.getPassword().equals(password);
        if (equals)              return R.error("用户名或密码错误");
        //检查账号是否可登入
        if (one.getStatus() == 0)return R.error("账号已禁用");
        //保持账号密码
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }

    //员工退出
    @PostMapping("/logout")
    public R<String>  loginout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    //添加员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes())) ;
//            employee.setCreateTime(LocalDateTime.now());
//            employee.setUpdateTime(LocalDateTime.now());
//            employee.setCreateUser(empId);
//            employee.setUpdateUser(empId);
            employeeService.save(employee);
            return R.success("添加成功");
    }

    //查询
    @GetMapping("/page")
    public R<Page>  page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> qW =new LambdaQueryWrapper<>();
        qW.like(StringUtils.isNotBlank(name),Employee::getName,name);
        qW.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,qW);
        //执行查询
        return R.success(pageInfo);
    }

    //更新
    @PutMapping
    public R<String> updateEmployee(HttpServletRequest request,@RequestBody Employee employee){
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        log.info(employee.toString());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    @GetMapping("/{id}")
    public R<Employee> GetById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查到对应信息");

    }
}
