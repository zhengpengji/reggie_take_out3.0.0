package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.User;
import lombok.Value;

public interface UserService extends IService<User> {
   public void sendMsg(String to,String subject,String text);
}
