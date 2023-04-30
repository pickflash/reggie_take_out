package com.Hao.reggie.common;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split=ex.getMessage().split(" ");
            String msg=split[2]+" 已存在，新增失败";
            return Result.error(msg);
        }else{
            return Result.error("未知错误");
        }
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        return Result.error(ex.getMessage());
    }
}
