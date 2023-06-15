package com.tag.prietag.core.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class MyLogAdvice {
    @Pointcut("@annotation(com.tag.prietag.core.annotation.MyErrorLog)")
    public void myErrorLog(){}

    @Before("myErrorLog()")
    public void errorLogAdvice(JoinPoint jp) throws Exception {
        Object[] args = jp.getArgs();

        for (Object arg : args) {
            if(arg instanceof Exception){
                Exception e = (Exception) arg;
                log.error("에러 : "+e.getMessage());
            }
        }
    }
}
