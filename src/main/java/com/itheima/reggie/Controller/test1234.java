//package com.itheima.reggie.Controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.itheima.reggie.common.R;
//import com.itheima.reggie.entity.Employee;
//import com.itheima.reggie.mapper.EmployeeMapper;
//import com.itheima.reggie.service.EmployeeService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.DigestUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
////@Slf4j
////@RestController
////@RequestMapping("/employee")
//public class test1234 {
//    @Autowired
//    private EmployeeService employeeService;
//
//    @Autowired
//    private EmployeeMapper employeeMapper;
//    @PostMapping("/login")
//    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
////        String password = employee.getPassword();
////        password = DigestUtils.md5DigestAsHex(password.getBytes());
////        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
////        employeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
////       Employee one = employeeService.getOne(employeeLambdaQueryWrapper);
////        if (one == null){
////            return R.error("登入失败");
////        }
////        if (!one.getPassword().equals(password)){
////            return R.error("密码错误");
////        }
////        if (one.getStatus() == 0){
////            return R.error("账号禁用");
////        }
////        request.getSession().setAttribute("employee",one.getId());
////        return R.success(one);
////    }
//////    @GetMapping ("/1")
//////    public List<Employee> get(@PathVariable  Integer id){
//////        return employeeService.list();
//////
//////    }
////    @GetMapping("/tset")
////    public R<List<Employee>> test1(){
////        return R.success(employeeService.list());
////    }
////
////    @GetMapping("/tset2")
////    public R<List<Employee>> test12(){
////        return R.success(employeeMapper.selectList(null));
////    }
////
////    @GetMapping("/tset3")
////    public R<Employee> test3(){
////        System.out.println("test 3 执行");
////        return R.success(employeeMapper.selectById(1));
////    }
//
//
//}
