package com.Hao.reggie.service.Impl;

import com.Hao.reggie.entity.DishFlavor;
import com.Hao.reggie.mapper.DishFlavorMapper;
import com.Hao.reggie.service.DishFlavorService;
import com.Hao.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * id为菜品id
     * @param id
     * @return
     */
    @Override
    public List<DishFlavor> findByDishId(Long id) {
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list=dishFlavorService.list(queryWrapper);
        return list;
    }
}
