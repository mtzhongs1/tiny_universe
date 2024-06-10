package com.ailu.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * @Description:
 * @Author: your name
 * @Date: 2023/11/30 16:48
 */
@Component
@Aspect
//TODO：aop:在被扫描到的方法中执行的自己的方法,从而实现公共字段自动填充
public class AutoFillAop {
    //设置切入点的方法
    //1.切点表达式：execution(返回值 包名.类名.方法名(参数类型)
    //2.annotation(某个注解的全限定名)
    @Pointcut("execution(* com.ailu.mapper.*.*(..)) && @annotation(com.ailu.aop.AutoFill)")
    public void pointCut() {
    }

    //2.设置前置通知，即执行的方法
    @Before("pointCut()")
    public void setFill(JoinPoint jp) {
        //因为切入点是在方法上，所以强转为方法的签名
        MethodSignature signature = (MethodSignature) jp.getSignature();
        //通过反射拿到方法和注解value
        Method method = signature.getMethod();
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        InsertOrUpdate value = annotation.value();

        //拿到方法中的参数，即为要填充的对象,如UserDTO
        Object obj = jp.getArgs()[0];

        //判断是插入还是更新
        if (InsertOrUpdate.INSERT.equals(value)) {
            //插入操作
            try {
                //拿到UserDTO的方法
                Method setCreateTime = obj.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTIme = obj.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                setCreateTime.invoke(obj, LocalDateTime.now());
                setUpdateTIme.invoke(obj, LocalDateTime.now());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //更新操作
            try {
                Method setUpdateTime = obj.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                setUpdateTime.invoke(obj, LocalDateTime.now());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
