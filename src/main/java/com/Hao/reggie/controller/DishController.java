package com.Hao.reggie.controller;

import com.Hao.reggie.Dto.DishDto;
import com.Hao.reggie.common.Result;
import com.Hao.reggie.entity.Category;
import com.Hao.reggie.entity.Dish;
import com.Hao.reggie.entity.DishFlavor;
import com.Hao.reggie.service.CategoryService;
import com.Hao.reggie.service.DishFlavorService;
import com.Hao.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.Timeout;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Result<Page> getPage(int page,int pageSize,String name){
        Page<Dish>  dishPage=new Page<>(page,pageSize);
        Page<DishDto>  dishDtoPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isEmpty(name),Dish::getName,name);
        lambdaQueryWrapper.orderByAsc(Dish::getSort);
        if(name==null){
            dishService.page(dishPage);
        }else{
            dishService.page(dishPage,lambdaQueryWrapper);
        }
        //对象拷贝
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records=dishPage.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId= item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        String key="dish_"+dishDto.getCategoryId()+"_1";//dish:12312312_1
        redisTemplate.delete(key);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/{id}")
    public Result<DishDto> findById(@PathVariable Long id){
        Dish dish=dishService.getById(id);
        DishDto dishDto=new DishDto();
        //获取菜品口味
        List<DishFlavor> list=dishFlavorService.findByDishId(id);
        //对象拷贝
        BeanUtils.copyProperties(dish,dishDto);
        Category category=categoryService.getById(dish.getCategoryId());
        //获取菜品分类
        dishDto.setCategoryName(category.getName());
        dishDto.setFlavors(list);
        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        //第一种情况，更新后，删除所有菜品缓存
        /*Set keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/
        //第二种情况，更新后，删除指定菜品缓存
        String key="dish_"+dishDto.getCategoryId()+"_1";//dish:12312312_1
        redisTemplate.delete(key);
        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品信息成功");
    }

    @DeleteMapping
    public Result<String> deleteById(Long[] id){
        //判断菜品状态，再决定是否删除
        for(Long dishId:id){
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            dishFlavorService.remove(queryWrapper);
            dishService.removeById(dishId);
        }
        return Result.success("菜品删除成功");
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long[] id){
        for(Long dishId:id){
            Dish dish=dishService.getById(dishId);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        if(status==0){
            return Result.success("菜品停售成功");
        }else {
            return Result.success("菜品启售成功");
        }
    }
    /*@GetMapping("/list")
    public Result<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                    .eq(Dish::getStatus,1)
                    .like(dish.getName()!=null,Dish::getName,dish.getName());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return Result.success(list);
    }*/


    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();//dish:12312312_1
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);


        if(dishDtoList!=null){
            //如果存在，直接返回，无需查询数据库
            return Result.success(dishDtoList);
        }


        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)
                .like(dish.getName()!=null,Dish::getName,dish.getName());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        dishDtoList=list.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //获取分类名称
            Category category=categoryService.getById(item.getCategoryId());
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }
            //获取菜品口味
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper=new LambdaQueryWrapper<>();
            flavorQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavorList=dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的数据缓存到Redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return Result.success(dishDtoList);
    }
}
