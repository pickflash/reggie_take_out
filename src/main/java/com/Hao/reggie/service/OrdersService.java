package com.Hao.reggie.service;

import com.Hao.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
