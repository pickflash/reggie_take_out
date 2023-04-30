package com.Hao.reggie.service.Impl;

import com.Hao.reggie.entity.OrderDetail;
import com.Hao.reggie.mapper.OrderDetailMapper;
import com.Hao.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
