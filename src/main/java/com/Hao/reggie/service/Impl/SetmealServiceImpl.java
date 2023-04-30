package com.Hao.reggie.service.Impl;

import com.Hao.reggie.Dto.SetmealDto;
import com.Hao.reggie.entity.Setmeal;
import com.Hao.reggie.entity.SetmealDish;
import com.Hao.reggie.mapper.SetmealMapper;
import com.Hao.reggie.service.SetmealDishService;
import com.Hao.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时西药保存套餐与菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表,执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的管理那关系，操作setmeal_dish表，执行insert操作
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);//更新setmeal表

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);//删除更新前与该套餐关联的setmealdish表
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();//获取要更新的setmealdish表
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
        }//为setmealdish添加套餐id
        setmealDishService.saveBatch(setmealDishes);//存储stemealdish表
    }
}
