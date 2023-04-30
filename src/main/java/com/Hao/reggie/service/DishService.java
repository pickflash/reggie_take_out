package com.Hao.reggie.service;

import com.Hao.reggie.Dto.DishDto;
import com.Hao.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
    public void updateWithFlavor(DishDto dishDto);
}
