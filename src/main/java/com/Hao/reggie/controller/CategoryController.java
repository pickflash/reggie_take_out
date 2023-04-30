package com.Hao.reggie.controller;

import com.Hao.reggie.common.Result;
import com.Hao.reggie.entity.Category;
import com.Hao.reggie.entity.Employee;
import com.Hao.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Result<Page> getPage(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo=new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    @PostMapping
    public Result<String> save(@RequestBody Category category){
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    @DeleteMapping
    public Result<String> delete(Long id){
        categoryService.remove(id);
        return Result.success("分类信息删除成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
            categoryService.updateById(category);
            return Result.success("员工状态修改成功");
    }
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }

}
