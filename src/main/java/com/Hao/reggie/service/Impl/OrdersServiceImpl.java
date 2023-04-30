package com.Hao.reggie.service.Impl;

import com.Hao.reggie.common.BaseContext;
import com.Hao.reggie.common.CustomException;
import com.Hao.reggie.entity.*;
import com.Hao.reggie.mapper.OrdersMapper;
import com.Hao.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;

    @Override
    public void submit(Orders orders) {
        //获取当前用户id
        Long userId=BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);//购物车数据
        if(shoppingCarts==null||shoppingCarts.size()==0){
            throw new CustomException("购物车为空,不能下单");
        }

        //查询用户信息
        User user=userService.getById(userId);

        //查询地址信息
        Long addressId=orders.getAddressBookId();
        AddressBook addressBook=addressBookService.getById(addressId);
        if(addressBook==null){
            throw new CustomException("购物车为空,不能下单");
        }

        //补充订单内容
        AtomicInteger amount=new AtomicInteger(0);//原子操作，在高并发的情况下仍能保证计算正确
        Long orderId= IdWorker.getId();
        List<OrderDetail> orderDetails=shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setAmount(item.getAmount());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setId(item.getId());
            orderDetail.setImage(item.getImage());
            orderDetail.setName(item.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setSetmealId(item.getSetmealId());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());



        orders.setNumber(String.valueOf(orderId));//订单id
        orders.setId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//计算总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null ? "":addressBook.getProvinceName())
                            +(addressBook.getCityName()==null?"":addressBook.getCityName())
                            +(addressBook.getDistrictName()==null?"":addressBook.getDistrictName())
                            +(addressBook.getDetail()==null?"":addressBook.getDetail()));

        //向订单表插入数据,一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //下单完成后清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
