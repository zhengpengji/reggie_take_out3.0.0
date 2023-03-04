package com.itheima.reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.service.impl.AddressBookServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private UserService userService;
    @PostMapping
    public R<String>  addressBook(HttpServletRequest request, @RequestBody AddressBook addressBook){
        if (addressBook!= null) {
            //用户ID 获得等同于   Long UserID=  BaseContext.getCurrentID;
            Long UserID = (Long) request.getSession().getAttribute("user");
            addressBook.setUserId(UserID);
            addressBookService.save(addressBook);
           return R.success("添加成功");
        }
      return    R.error("网络繁忙");
    }
    @GetMapping("/list")
    public R<List<AddressBook>> getListAdd (){
        Long UserId= BaseContext.getCurrentID();
        LambdaQueryWrapper<AddressBook> lqw= new LambdaQueryWrapper<>();
        lqw.eq(UserId!=null,AddressBook::getUserId,UserId);
        //！！！！！！！！！必须加条件否则查询所有人地址！！！！！！！！！！！！
        //！！！！！！！！！！！！！！！！！！！！！！！！1！！！！！！！！！1
        List<AddressBook> list = addressBookService.list(lqw);
       return R.success(list);
    }
    @Transactional
    @PutMapping("/default")
    public R<AddressBook> SetDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> lqw= new LambdaUpdateWrapper<>();
        lqw.eq(addressBook!=null,AddressBook::getUserId,BaseContext.getCurrentID());
        for (AddressBook book : addressBookService.list(lqw)) {
             if (book.getIsDefault()==1){
                 book.setIsDefault(0);
                 addressBookService.updateById(book);
                 break;
             }
        }
//       等同于 lqw.set(AddressBook::getIsDefault,0);
//        addressBookService.update(lqw);
//        addressBook = addressBookService.getById(addressBook); 不用先得到 直接设置即可
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
       return  R.success(addressBook);
    }
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }
    @GetMapping("/default")
    public R<AddressBook> GetDefault() {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId, BaseContext.getCurrentID());
        lqw.eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(lqw);
        if (one != null) {
          return R.success(one);
        }
       return R.error("没有找到该对象");
    }
    }
