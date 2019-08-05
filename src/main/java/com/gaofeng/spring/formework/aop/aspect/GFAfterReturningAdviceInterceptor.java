package com.gaofeng.spring.formework.aop.aspect;

import com.gaofeng.spring.formework.aop.intercept.GFMethodInterceptor;
import com.gaofeng.spring.formework.aop.intercept.GFMethodInvocation;

import java.lang.reflect.Method;

public class GFAfterReturningAdviceInterceptor extends GFAbstractAspectAdvice implements GFAdvice, GFMethodInterceptor {
    private GFJoinPoint joinPoint;
    public GFAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod,aspectTarget);
    }

    @Override
    public Object invoke(GFMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }
    private void afterReturning(Object retVal,Method method,Object[] arguments,Object aThis)throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
