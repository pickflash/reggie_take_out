package com.Hao.reggie.service;

import com.Hao.reggie.entity.DishFlavor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DishFlavorService extends IService<DishFlavor> {
    public List<DishFlavor> findByDishId(Long id);
}
