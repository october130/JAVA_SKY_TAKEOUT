package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException  ex){//用来处理数据库操作异常，即用户名重复异常

        log.error("异常信息：{}", ex.getMessage());
      String message = ex.getMessage();//获取异常信息
      if (message.contains("Duplicate entry")) {//如果监测到异常信息里面含有Duplicate entry，说明是用户名重复异常
          String[] split = message.split(" ");
          String username = split[2];//获取用户名
          String msg = username + MessageConstant.ALREADY_EXISTS;
          return Result.error(msg);//返回错误信息,告诉用户用户名已存在
      }else{
          return Result.error(MessageConstant.UNKNOWN_ERROR);//返回未知错误,如果不是重复异常的话
      }
    }
}
