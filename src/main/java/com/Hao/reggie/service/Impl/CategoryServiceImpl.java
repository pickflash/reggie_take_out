package com.Hao.reggie.service.Impl;

import com.Hao.reggie.common.CustomException;
import com.Hao.reggie.entity.Category;
import com.Hao.reggie.entity.Dish;
import com.Hao.reggie.entity.Setmeal;
import com.Hao.reggie.mapper.CategoryMapper;
import com.Hao.reggie.service.CategoryService;
import com.Hao.reggie.service.DishService;
import com.Hao.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int countDish=dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联菜品，如果已经关联，抛出一个业务异常
        if (countDish > 0) {
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int  countSetmeal=setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类是否关联套餐，如果已经关联，抛出一个业务异常
        if(countSetmeal>0){
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
