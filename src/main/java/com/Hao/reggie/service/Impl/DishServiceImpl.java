package com.Hao.reggie.service.Impl;

import com.Hao.reggie.Dto.DishDto;
import com.Hao.reggie.entity.Dish;
import com.Hao.reggie.entity.DishFlavor;
import com.Hao.reggie.mapper.DishMapper;
import com.Hao.reggie.service.DishFlavorService;
import com.Hao.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private  DishFlavorService dishFlavorService;
    /**
     * 新增菜品同时新增口味数据
     * @param dishDto
     */
    @Transactional//设计多表操作，加入事务控制
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //saveBatch批量保存，save保存单个
        /*for(DishFlavor flavor:dishDto.getFlavors()){
            flavor.setDishId(dishDto.getId());
        }*/
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据，---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }
}
