package com.Hao.reggie.Dto;

import com.Hao.reggie.entity.Setmeal;
import com.Hao.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
