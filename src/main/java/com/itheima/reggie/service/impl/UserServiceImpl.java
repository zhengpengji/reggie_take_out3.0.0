package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Value("${spring.mail.username}")
    private  String from ;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMsg(String to, String subject, String text) {
        SimpleMailMessage M =new SimpleMailMessage();
        M.setFrom(from);
        M.setTo(to);
        M.setSubject(subject);
        M.setText(text);
        javaMailSender.send(M);
    }
}
