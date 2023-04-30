package com.Hao.reggie.controller;

import com.Hao.reggie.common.BaseContext;
import com.Hao.reggie.common.Result;
import com.Hao.reggie.entity.AddressBook;
import com.Hao.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Result.success("地址修改成功");
    }

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return Result.success("地址修改成功");
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> queryWrapper=new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Result.success("默认地址设置成功");
    }

    @DeleteMapping
    public Result<String> delete(@RequestBody AddressBook addressBook){
        if(addressBook.getIsDefault()==1){
            return Result.error("默认地址不可删除");
        }else{
            addressBookService.removeById(addressBook);
            return Result.success("地址删除成功");
        }
    }

    /**
     * 根据id查单个地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AddressBook> findById(@PathVariable Long id){
        AddressBook addressBook=addressBookService.getById(id);
        if(addressBook!=null){
            return Result.success(addressBook);
        }else {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook=addressBookService.getOne(queryWrapper);
        if(addressBook==null){
            return Result.error("没有找到该对象");
        }
        return Result.success(addressBook);
    }


    /**
     * 根据id查询用户全部地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> findAllById(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByAsc(AddressBook::getUpdateTime);
        return Result.success(addressBookService.list(queryWrapper));
        /*LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,id);
        List<AddressBook> list=addressBookService.list(queryWrapper);*/
    }
}
