package com.basler182.spelvalidationstarter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * Aspect to discover parameter names for methods annotated with
 * {@link SpelAssert}.
 * This aspect runs with the highest precedence to ensure parameter names are
 * discovered
 * before validation occurs.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpelParameterNameAspect {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.basler182.spelvalidationstarter.SpelAssert) || @annotation(com.basler182.spelvalidationstarter.SpelAssert.List)")
    public Object captureParameterNames(ProceedingJoinPoint joinPoint) throws Throwable {
        if (joinPoint.getSignature() instanceof MethodSignature methodSignature) {
            Method method = methodSignature.getMethod();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

            if (parameterNames != null) {
                SpelContextHolder.pushParameterNames(parameterNames);
            }
        }

        try {
            return joinPoint.proceed();
        } finally {
            if (joinPoint.getSignature() instanceof MethodSignature) {
                SpelContextHolder.popParameterNames();
            }
        }
    }
}
