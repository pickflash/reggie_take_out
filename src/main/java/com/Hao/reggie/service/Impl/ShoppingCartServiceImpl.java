package com.Hao.reggie.service.Impl;

import com.Hao.reggie.entity.ShoppingCart;
import com.Hao.reggie.mapper.ShoppingCartMapper;
import com.Hao.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ServiceConfigurationError;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
