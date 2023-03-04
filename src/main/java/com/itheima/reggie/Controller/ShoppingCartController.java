package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */





    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户ID 指定添加购物车的是哪个用户
        Long UserID = BaseContext.getCurrentID();
        shoppingCart.setUserId(UserID);

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserID!=null,ShoppingCart::getUserId,UserID);
        Long DishId=shoppingCart.getDishId();
        if (DishId!=null){
            //说明添加的是菜品
            lqw.eq(ShoppingCart::getDishId,DishId);
        }else {
            //说明添加的是套餐
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(lqw);
        if (one == null){
            shoppingCartService.save(shoppingCart);
            one =shoppingCart;
        }else {
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        }
        return R.success(one);
    }

    @PostMapping("/sub")
    public R<ShoppingCart>  deleteShoppingCart(@RequestBody ShoppingCart shoppingCart){
        Long UserID = BaseContext.getCurrentID();
        LambdaQueryWrapper<ShoppingCart> lqw =new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,UserID);
       if (shoppingCart.getDishId()!=null) {
           lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
       }else {
           lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
       }
        ShoppingCart one = shoppingCartService.getOne(lqw);
        if(one == null) return R.success(one);
        if (one.getNumber()!=1){
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
            return R.success(one);
        }else {
            shoppingCartService.removeById(one);
            one.setNumber(one.getNumber()-1);
        }
             return R.success(one);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseContext.getCurrentID()!=null,ShoppingCart::getUserId,BaseContext.getCurrentID());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentID());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}