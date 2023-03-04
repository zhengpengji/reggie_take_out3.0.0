package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.dto.dto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.SaveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }
    @GetMapping("/page")
    public  R<Page<SetmealDto>> getPage(int page , int pageSize,String name){
        Page<Setmeal> pageSetmeal =new Page<>(page,pageSize);
        Page<SetmealDto> Page = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> lqwSet  = new LambdaQueryWrapper<>();
        lqwSet.like(name!=null,Setmeal::getName,name);
        lqwSet.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageSetmeal,lqwSet);
        BeanUtils.copyProperties(pageSetmeal,page,"records");
        List<SetmealDto> listSetmealDish = new ArrayList<>();
        for (Setmeal setmeal : pageSetmeal.getRecords()) {
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SetmealDish::getSetmealId, setmeal.getId());
            List<SetmealDish> list = setmealDishService.list(lqw);
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            setmealDto.setSetmealDishes(list);
            if (setmeal.getCategoryId()!=null){
            setmealDto.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
            }
            listSetmealDish.add(setmealDto);
        }
        Page.setRecords(listSetmealDish);
        return R.success(Page);
    }

    @DeleteMapping
    public  R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return  R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> stopSell(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if (setmeal != null) {
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("修改成功");
    }
    @GetMapping("/list")
    //@RequestParam Long categoryId,@RequestParam Long status
    public R<List<Setmeal>> getSetmealDish(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        return R.success(setmealService.list(lqw));
    }
    @GetMapping("/dish/{id}")
    public R<List<dto>> getDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> lqw= new LambdaQueryWrapper<>();
        lqw.eq(id!=null,SetmealDish::getSetmealId,id);
        lqw.orderByDesc(SetmealDish::getCreateTime);
        List<SetmealDish> list = setmealDishService.list(lqw);
        List<dto> collect = list.stream().map(item -> {
            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            String name = dish.getName();
            dish.setName(" ");
            String description = dish.getDescription();
            dish.setDescription(name + "\r\n" +description);
            dto dto = new dto();
            BeanUtils.copyProperties(dish,dto);
            dto.setCopies(1);
            return dto;
        }).collect(Collectors.toList());

        return R.success(collect);
    }
}
