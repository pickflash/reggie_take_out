package com.Hao.reggie.controller;

import com.Hao.reggie.common.BaseContext;
import com.Hao.reggie.common.Result;
import com.Hao.reggie.entity.ShoppingCart;
import com.Hao.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrentId());//添加用户id
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());//获取购物车表

        ShoppingCart cart=shoppingCartService.getOne(queryWrapper);

        if(cart!=null){
            Integer num=cart.getNumber();
            cart.setNumber(num+1);//存在数量加1
            shoppingCartService.updateById(cart);
        }else {
            shoppingCart.setNumber(1);//不存在，数量设为1
            shoppingCartService.save(shoppingCart);
            cart=shoppingCart;
        }

        return Result.success(cart);
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> getAllById(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list=shoppingCartService.list(queryWrapper);
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result<String> cleanAll(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return Result.success("购物车清空成功");
    }
}
