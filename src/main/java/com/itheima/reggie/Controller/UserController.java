package com.itheima.reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession httpSession, @RequestBody User user){
        String email= user.getPhone(); //获取邮箱号 相当于String to
        String subject= "DNSC电脑商城";
        //非空判断
        if (StringUtils.isNotEmpty(email)){
            Random Ran = new Random();
            int random =Ran.nextInt(1,10);
            for (int i = 0; i < 3; i++) {
                random=random*10+Ran.nextInt(10);
            }
            String code=String.valueOf(random);
            String test = "[DSCN电脑商城] 您好，您的登入验证码是："+code+",请尽快登入";
            userService.sendMsg(email,subject,test);
          // httpSession.setAttribute(email,code);
            //注释掉session缓存验证码，使用redis
            redisTemplate.opsForValue().set(email,code,5, TimeUnit.MINUTES);
            return R.success("验证码发送成功");
        }
        return R.error("网络异常,无法发送");
    }

    @PostMapping("/login")                        //请求体 封装输入的手机号和验证码
    public R<User> login(HttpSession session,@RequestBody Map map){
        String phone = map.get("phone").toString();
        String code  =map.get("code").toString();
     //   Object sessionCode = session.getAttribute(phone);//网页保存的密码
        Object sessionCode=redisTemplate.opsForValue().get(phone);//redis缓存验证码
        if (sessionCode != null&&sessionCode.equals(code)){//验证码一样登入成功
            LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user = userService.getOne(lqw);
            if (user == null){
                user =new User();
                user.setPhone(phone);
                user.setName(phone.substring(0,6));
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登入失败");
    }
    @PostMapping("/logout")
    public R<String>  loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
