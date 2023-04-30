package com.Hao.reggie.service;

import com.Hao.reggie.Dto.SetmealDto;
import com.Hao.reggie.entity.Setmeal;
import com.Hao.reggie.entity.SetmealDish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void updateWithDish(SetmealDto setmealDto);
}
