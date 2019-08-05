package com.gaofeng.spring.formework.aop.aspect;

import com.gaofeng.spring.formework.aop.intercept.GFMethodInterceptor;
import com.gaofeng.spring.formework.aop.intercept.GFMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 */
public class GFMethodBeforeAdviceInterceptor extends GFAbstractAspectAdvice implements GFAdvice, GFMethodInterceptor {

    private GFJoinPoint joinPoint;
    public GFMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod,aspectTarget);
    }

    @Override
    public Object invoke(GFMethodInvocation mi) throws Throwable{
        //this  代理对象
        joinPoint = mi;
        before(mi.getMethod(),mi.getArguments(),mi.getThis());
        return mi.proceed();
    }
    private void before(Method method,Object[] args,Object target) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }
}

