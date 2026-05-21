package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//创建了AutoFill注解，
@Target(ElementType.METHOD)//用于指定注解加到什么位置，只能加到方法上
@Retention(RetentionPolicy.RUNTIME)//指定注解在什么阶段执行
public @interface AutoFill {
    OperationType value();//枚举值，用于指定数据库操作类型
    //value()方法，用于指定数据库操作类型，返回值是OperationType枚举值，即insert、update
}
