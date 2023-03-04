package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    //删除分类，需要判断是否关联别的套餐
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    public void MyRemove(Long id) {
        LambdaQueryWrapper<Dish> lqwDish =new LambdaQueryWrapper<>();
        lqwDish.eq(Dish::getCategoryId, id);
        int countDish=dishService.count(lqwDish);
        LambdaQueryWrapper<Setmeal> lqwSetmeal =new LambdaQueryWrapper<>();
        lqwSetmeal.eq(Setmeal::getCategoryId, id);
        int countSetmeal = setmealService.count(lqwSetmeal);
        if (countSetmeal+countDish>0){
            throw new CustomException("当前品牌还有上架商品，删除失败");
        }
        removeById(id);
    }
}
