package com.Hao.reggie.service.Impl;

import com.Hao.reggie.entity.AddressBook;
import com.Hao.reggie.mapper.AddressBookMapper;
import com.Hao.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
