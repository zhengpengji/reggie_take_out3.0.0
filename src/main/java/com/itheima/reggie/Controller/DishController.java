package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        Set keys = redisTemplate.keys("dish_*");
//        清除所有dish_开头的
//        redisTemplate.delete(keys);
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增商品成功");
    }

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String name) {
        //因为原表参数品牌是id，需要用dto扩展查询
        Page<Dish> pageDish = new Page<>(page, pageSize);
        Page<DishDto> pageDishDto = new Page<>();
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(name), Dish::getName, name);
        pageDish = dishService.page(pageDish, lqw);
        //对象深拷贝 忽略原表数据，只拿到分页page和pageSize和排序的东西的ID
        BeanUtils.copyProperties(pageDish, pageDishDto, "records");
        List<Dish> records = pageDish.getRecords();
        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String name1 = category.getName();
            dishDto.setCategoryName(name1);
            BeanUtils.copyProperties(item, dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        pageDishDto.setRecords(list);
        return R.success(pageDishDto);
        //        for (Dish record : pageDish.getRecords()) {
//            DishDto dishDto =new DishDto();
//            Long categoryId = record.getCategoryId();
//            Category category = categoryService.getById(categoryId);
//            String name1 = category.getName();
//            dishDto.setCategoryName(name1);
//        }
    }

    @GetMapping("/{dishId}")
    public R<DishDto> GetById(@PathVariable Long dishId) {
        Dish dish = dishService.getById(dishId);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        Category category = categoryService.getById(dish.getCategoryId());
        dishDto.setCategoryName(category.getName());
        //查询商品配置
        LambdaQueryWrapper<DishFlavor> lqwDishFlavor = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<DishFlavor> eq = lqwDishFlavor.eq(DishFlavor::getDishId, dishId);
        List<DishFlavor> list = dishFlavorService.list(eq);
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> saveUp(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
//        Set keys = redisTemplate.keys("dish_*");
//        清除所有dish_开头的
//        redisTemplate.delete(keys);
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改商品成功");
    }

    //起售停售
    @PostMapping("/status/{status}")
    public R<String> stopSell(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("修改成功");
    }
    @Transactional
    @DeleteMapping()
    public R<String> MyDelete(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dishService.removeById(dish);
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper =new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(ids!=null,DishFlavor::getDishId,id);
            dishFlavorService.remove(lambdaQueryWrapper);
        }
        return R.success("删除成功");
    }

//    @GetMapping("list")                   //@RequestParam Long categoryId
//    public R<List<Dish>> MyGetDishByCategory(Dish dish){
//        LambdaQueryWrapper<Dish> lqw =new LambdaQueryWrapper<>();
//        lqw.eq(Dish::getCategoryId,dish.getCategoryId());
//        //条件查询起售状态商品  lqw.eq(Dish::getStatus,1); 可以不加
//        //排序
//        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(lqw);
//        return R.success(list);
//    }
    @GetMapping("list")                   //@RequestParam Long categoryId
    public R<List<DishDto>> MyGetDishByCategory(Dish dish){
        List<DishDto> DishList=null;
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //redis 先从redis获取缓存数据
        DishList= (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (DishList!=null){
            return R.success(DishList);
        }
        //
        // 如果存在直接返回，
        //
        //
        // 不存在则查询数据库，并且缓存到redis
        LambdaQueryWrapper<Dish> lqw =new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //条件查询起售状态商品  lqw.eq(Dish::getStatus,1); 可以不加
        //排序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        List<DishDto> collect = list.stream().map(item -> {
            LambdaQueryWrapper<DishFlavor> lqwF = new LambdaQueryWrapper<>();
            lqwF.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> list1 = dishFlavorService.list(lqwF);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key,collect,60, TimeUnit.MINUTES);
        return R.success(collect);
    }
}

