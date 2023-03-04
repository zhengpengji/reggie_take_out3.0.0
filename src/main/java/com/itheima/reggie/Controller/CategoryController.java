package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> Save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("保存成功");
    }
    @GetMapping("/page")
    public R<Page>  page(int page, int pageSize){
        //构造分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> qW =new LambdaQueryWrapper<>();
        qW.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,qW);
        //执行查询
        return R.success(pageInfo);
    }
    @DeleteMapping
    public R<String> deleteCategory(Long ids){
        categoryService.MyRemove(ids);
        return R.success("删除成功");
    }
    @PutMapping
    public  R<String> UpdateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return  R.success("修改成功");
    }
    @GetMapping("/list")
    public R<List<Category>> getCategoryList( Category category){
        LambdaQueryWrapper<Category> lqwCategory = new LambdaQueryWrapper<>();
        //需要不等于空，商品管理里面的Type
        lqwCategory.eq(category.getType()!=null,Category::getType,category.getType());
        lqwCategory.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return R.success(categoryService.list(lqwCategory));
    }
}
