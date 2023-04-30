package com.Hao.reggie.controller;

import com.Hao.reggie.Dto.DishDto;
import com.Hao.reggie.Dto.SetmealDto;
import com.Hao.reggie.common.CustomException;
import com.Hao.reggie.common.Result;
import com.Hao.reggie.entity.*;
import com.Hao.reggie.service.CategoryService;
import com.Hao.reggie.service.SetmealDishService;
import com.Hao.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    public Result<Page> getPage(int page,int pageSize,String name){
        Page<Setmeal> setmealPage=new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isEmpty(name), Setmeal::getName,name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        if(name==null){
            setmealService.page(setmealPage);
        }else {
            setmealService.page(setmealPage,queryWrapper);
        }
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records=setmealPage.getRecords();
        //for循环
        /*for(SetmealDto setmealDto:list){
            Category category=new Category();
            category.setId(setmealDto.getCategoryId());
            setmealDto.setCategoryName(category.getName());
        }*/
        //数据流操作
        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId=item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return Result.success(setmealDtoPage);
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return Result.success("保存套餐成功");
    }

    @DeleteMapping
    public Result<String> deleteById(Long[] id){
        //查询套餐状态，确定是否可以进行删除
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper=new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId,id);
        setmealQueryWrapper.eq(Setmeal::getStatus,1);
        int count=setmealService.count(setmealQueryWrapper);
        if(count>0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐中的数据
        //删除关系表中的数据
        for(Long setmealId:id){
            LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
            setmealDishService.remove(queryWrapper);
            setmealService.removeById(setmealId);
        }
        return Result.success("套餐删除成功");
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long[] id){
        for(Long setmealId:id){
            Setmeal setmeal=setmealService.getById(setmealId);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        if(status==0){
            return Result.success("套餐停售成功");
        }else {
            return Result.success("套餐启售成功");
        }
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id){
        Setmeal setmeal=setmealService.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes=setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return Result.success(setmealDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return Result.success("更新套餐成功");
    }

    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals=setmealService.list(queryWrapper);
        return Result.success(setmeals);
    }
}
