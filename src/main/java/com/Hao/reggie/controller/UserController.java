package com.Hao.reggie.controller;

import com.Hao.reggie.common.Result;
import com.Hao.reggie.common.SMSUtils;
import com.Hao.reggie.common.ValidateCodeUtils;
import com.Hao.reggie.entity.User;
import com.Hao.reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user){
        //获取手机号
        String phone=user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机4位验证码
            String code=ValidateCodeUtils.generateValidateCode(4).toString();

            //调用阿里云提供的短信服务API完成发送短信             短信模板
            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存到Redis
            ValueOperations<String,String> ops=redisTemplate.opsForValue();
            ops.set(phone,code,5, TimeUnit.MINUTES);
            return Result.success("验证码短信发送成功");
        }
        return Result.error("验证码短信发送失败");
    }


    @PostMapping("/login")
    private Result<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone=map.get("phone").toString();
        //获取验证码
        String code=map.get("code").toString();
        //从Redis中获取验证码
        String cacheCode=redisTemplate.opsForValue().get(phone);
        //进行验证码的比对(页面中提交的验证码与Session中保存的验证码进行比对)
        if(cacheCode!=null&&cacheCode==code){
            //判断当前手机号是否存在与用户表中，如果新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user=userService.getOne(queryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果用户登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return Result.success(user);
        }
        return Result.error("登陆失败");
    }
}
