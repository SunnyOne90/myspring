package com.gaofeng.spring.formework.aop.intercept;

public interface GFMethodInterceptor {

    Object invoke(GFMethodInvocation mi)throws Throwable;
}
