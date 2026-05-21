package com.sky.aspect;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;

@Aspect//代表切面
@Component//代表组件
@Slf4j
//自定义切面，实现公共字段填充，如果mapper层标注autofill注解，则切面会主动拦截，通过反射获取参数，然后进行赋值
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && " +
            "@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}//切入点方法，用来匹配com.sky.mapper包下的所有方法，并且该方法有@AutoFill注解
    //这个pointcut注解就是代表切入点，指定在mapper包下的所有方法,并且方法上面还有autofill注解的



    @Before("autoFillPointCut()")//前置通知,当匹配上切入点方法时，会先执行这个方法
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //joinPoint可以获取方法参数,这个参数是aop本来的参数，这个参数是aop自己封装的，里面有方法参数，方法名，方法所在的类等信息
        log.info("开始进行数据填充");
       MethodSignature signature =(MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的自动填充注解对象
        OperationType operationType = autoFill.value();//获取数据库操作类型



        Object[] args = joinPoint.getArgs();//获取被拦截方法参数



        if (args == null || args.length == 0){ //如果参数不存在，则直接返回
            return;
        }

        Object entity = args[0];//获取第一个参数，即实体对象，因为实体对象类型不清楚，所以使用Object


        LocalDateTime now = LocalDateTime.now();//获取当前时间
        Long currentId = BaseContext.getCurrentId();//获取当前登录用户的id


        if (operationType == OperationType.INSERT){//如果是插入操作,则需要设置创建时间和更新时间，创建人，更新人
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            //获得四个set 方法，便于直接赋值

            setCreateTime.invoke(entity, now);//最后，通过反射调用set方法，将当前时间赋值给实体对象
            setUpdateTime.invoke(entity, now);
            setCreateUser.invoke(entity, currentId);
            setUpdateUser.invoke(entity, currentId);//最后，通过反射调用set方法，将当前用户id赋值给实体对象
        } else if (operationType == OperationType.UPDATE){
            //如果是更新操作,则只需要设置更新时间，更新人
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        }
    }
    //表示在方法进行之前进行执行,给控制台输出数据填充的信息通知


}
