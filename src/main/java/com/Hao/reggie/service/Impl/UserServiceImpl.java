package com.Hao.reggie.service.Impl;

import com.Hao.reggie.entity.User;
import com.Hao.reggie.mapper.UserMapper;
import com.Hao.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
