package ru.novikov.T1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class TaskAspect {

    private static final Logger logger = LoggerFactory.getLogger(TaskAspect.class.getName());

    @Before("@annotation(LogBeforeAspect)")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Calling method before: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(LogExceptionAspect)",
            throwing = "throwable")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable throwable) {
        logger.info("Calling method afterThrowing exception: {}, message:{}", joinPoint.getSignature().getName(),
                throwable.getMessage());
    }

    @Around("@annotation(LogAroundAspect)")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        logger.info("Calling method around: {}", joinPoint.getSignature().toShortString());
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            logger.info("Method args: {}", Arrays.toString(args));
        }

        long start = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        long finish = System.currentTimeMillis();
        logger.info("Calling method around time: {} ms", finish - start);

        return result;
    }

    @AfterReturning(pointcut = "@annotation(LogAfterReturningAspect)",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Calling method afterReturning: {}. Result: {}",
                joinPoint.getSignature().toShortString(), result);
    }
}
