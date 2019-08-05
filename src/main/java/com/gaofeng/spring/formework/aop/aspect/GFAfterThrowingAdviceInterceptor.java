package com.gaofeng.spring.formework.aop.aspect;

import com.gaofeng.spring.formework.aop.intercept.GFMethodInterceptor;
import com.gaofeng.spring.formework.aop.intercept.GFMethodInvocation;

import java.lang.reflect.Method;

public class GFAfterThrowingAdviceInterceptor extends GFAbstractAspectAdvice implements  GFAdvice, GFMethodInterceptor {

    private String throwingName;

    public GFAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTaget) {
        super(aspectMethod,aspectTaget);
    }
    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }

    @Override
    public Object invoke(GFMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }
}
