package com.app.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(0)
@Component
public class MeaurmentAspect {

    @Before("execution(* com.app.services.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("LoggingAspect: Method " + joinPoint.getSignature() + " is about to be executed.");
    }

    @After("execution(* com.app.services.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("LoggingAspect: Method " + joinPoint.getSignature() + " is executed.");
    }

    Logger log = LoggerFactory.getLogger(MeaurmentAspect.class);

    @Around(value = "execution(* com.app.services.*.*(..))")
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("PerformanceMonitoringAspect :");
        sb.append("for Method: ").append(joinPoint.getSignature())
                .append("\twithArgs: ").append("(").append(StringUtils.join(joinPoint.getArgs(), ",")).append(")");
        sb.append("\ttook: ");
        Object returnValue = joinPoint.proceed();
        log.info(sb.append(System.currentTimeMillis() - startTime).append(" ms.").toString());

        return returnValue;
    }

}