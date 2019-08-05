package com.gaofeng.spring.formework.aop;

import com.gaofeng.spring.formework.aop.intercept.GFMethodInvocation;
import com.gaofeng.spring.formework.aop.support.GFAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class GFJdkDynamicAopProxy implements GFAopProxy, InvocationHandler {
    private GFAdvisedSupport advised;
    public GFJdkDynamicAopProxy(GFAdvisedSupport config){
        this.advised = config;
    }
    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        GFMethodInvocation invocation = new GFMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
